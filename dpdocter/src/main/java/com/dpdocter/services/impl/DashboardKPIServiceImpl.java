package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicPerformanceDto;
import com.dpdocter.beans.ClinicWiseFinancialsDto;
import com.dpdocter.beans.DashboardSummaryDto;
import com.dpdocter.beans.ExpensesBreakdownDto;
import com.dpdocter.beans.FinancialOverviewDto;
import com.dpdocter.beans.FinancialSummaryDto;
import com.dpdocter.beans.PerformanceMetricsDto;
import com.dpdocter.beans.RevenueExpensesTrendDto;
import com.dpdocter.beans.ViewClinicResponse;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.PatientTreatmentStatus;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DashboardKPIDataResponse;
import com.dpdocter.services.DashboardKPIService;
import com.dpdocter.webservices.DashboardClinicResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.DateUtil;

@Service
public class DashboardKPIServiceImpl implements DashboardKPIService {

	private static final Logger logger = LogManager.getLogger(DashboardKPIServiceImpl.class);

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorPatientReceiptRepository doctorPatientReceiptRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PatientTreamentRepository patientTreatmentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public DashboardKPIDataResponse getKPIs(String startDate, String endDate, String cityId, String zoneId, String doctorId,
			String locationId, String speciality) {
		DashboardKPIDataResponse kpi = new DashboardKPIDataResponse();

		// Step 1: Fetch locationIds based on city and zone
		List<ObjectId> locationIds = new ArrayList<>();
		Criteria locationCriteria = new Criteria();
		locationCriteria.and("isDentalChain").is(true);

		if (!DPDoctorUtils.anyStringEmpty(cityId)) {
			locationCriteria.and("city").is(cityId);
		}

		if (!DPDoctorUtils.anyStringEmpty(zoneId)) {
			locationCriteria.and("zone").is(zoneId);
		}

		List<LocationCollection> matchedLocations = mongoTemplate.find(new Query(locationCriteria),
				LocationCollection.class);
		locationIds = matchedLocations.stream().map(LocationCollection::getId).collect(Collectors.toList());

		// Step 2: Build common filter criteria for receipts and visits
		Criteria baseCriteria = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			baseCriteria.and("doctorId").is(new ObjectId(doctorId));
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			baseCriteria.and("locationId").is(new ObjectId(locationId));
		} else if (!locationIds.isEmpty()) {
			baseCriteria.and("locationId").in(locationIds);
		}

		baseCriteria.and("discarded").is(false);

		// Step 3: Total Revenue Calculation from DoctorPatientReceiptCollection
		Criteria receiptCriteria = new Criteria().andOperator(baseCriteria);
		applyDateCriteria(receiptCriteria, "receivedDate", startDate, endDate);

		List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(receiptCriteria),
				DoctorPatientReceiptCollection.class);

		double totalRevenue = receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0)
				.sum();
		kpi.setTotalRevenue((long) totalRevenue);

		// Step 4: NPS Calculation
		double totalNps = matchedLocations.stream().filter(loc -> loc.getNpsScore() != null)
				.mapToDouble(LocationCollection::getNpsScore).sum();
		long countNps = matchedLocations.stream().filter(loc -> loc.getNpsScore() != null).count();
		double averageNps = countNps > 0 ? totalNps / countNps : 0.0;
		kpi.setNps(averageNps);
		Aggregation aggregation = null;
		Criteria visitCriteria = Criteria.where("discarded").is(false);
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			visitCriteria = visitCriteria.and("doctorId").is(new ObjectId(doctorId));
		}

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			visitCriteria = visitCriteria.and("locationId").is(new ObjectId(locationId));
		}

		applyDateCriteria(visitCriteria, "createdTime", startDate, endDate);

		aggregation = Aggregation.newAggregation(Aggregation.match(visitCriteria));
		Integer totalPatientCount = mongoTemplate.aggregate(aggregation, "patient_cl", PatientCollection.class)
				.getMappedResults().size();

		//
		Integer totalPatientVisitCount = mongoTemplate
				.aggregate(aggregation, "patient_visit_cl", PatientVisitCollection.class).getMappedResults().size();

		kpi.setNewPatients(totalPatientCount);
		kpi.setVisitedPatients(totalPatientVisitCount);

		int repeatPatientCount = totalPatientVisitCount - totalPatientCount;

		// 2. Get Total Expenses from DoctorExpenseCollection
		Criteria expenseCriteria = Criteria.where("discarded").is(false);
		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			expenseCriteria = expenseCriteria.and("locationId").is(new ObjectId(locationId));
		}

		applyDateCriteria(expenseCriteria, "toDate", startDate, endDate);

		long expenses = (long) mongoTemplate.find(Query.query(expenseCriteria), DoctorExpenseCollection.class).stream()
				.mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		kpi.setTotalEbita((long) totalRevenue - expenses);

		return kpi;
	}

	public static void applyDateCriteria(Criteria criteria, String field, String fromDate, String toDate) {
		DateTime fromTime = null;
		DateTime toTime = null;
		Date from = null;
		Date to = null;
		long date = 0;
		if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
			from = new Date(Long.parseLong(fromDate));
			to = new Date(Long.parseLong(toDate));
		} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
			from = new Date(Long.parseLong(fromDate));
			to = new Date();
		} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
			from = new Date(date);
			to = new Date(Long.parseLong(toDate));
		} else {
			from = new Date(date);
			to = new Date();
		}

		fromTime = new DateTime(DateUtil.getStartOfDay(from));
		toTime = new DateTime(DateUtil.getEndOfDay(to));
//		fromTime = new DateTime(from);
//		toTime = new DateTime(to);

		criteria.and(field).gte(fromTime).lte(toTime);
	}

	@Override
	public DashboardSummaryDto getMonthlySummary(String clinicId, String startDate, String endDate) {
		DashboardSummaryDto dto = new DashboardSummaryDto();
		dto.setClinicId(clinicId);
		
		// Parse dates to extract month and year for display purposes
		int month = 1;
		int year = new DateTime().getYear();
		if (!DPDoctorUtils.anyStringEmpty(startDate)) {
			DateTime startDateTime = parseTimestampToDateTime(startDate);
			if (startDateTime != null) {
				month = startDateTime.getMonthOfYear();
				year = startDateTime.getYear();
			}
		}
		dto.setMonth(month);
		dto.setYear(year);

		try {
			// Parse date strings to DateTime objects
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current year if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999);
			}
			
			// Get revenue vs expenses data for the date range
			dto.setRevenueVsExpenses(getRevenueVsExpensesForPeriod(clinicId, startDateTime, endDateTime));
			
			// Get patient trends data for the date range
			dto.setPatientTrends(getPatientTrendsForPeriod(clinicId, startDateTime, endDateTime));

			// Calculate revenue analytics for the period
			DashboardSummaryDto.RevenueAnalytics revenueAnalytics = new DashboardSummaryDto.RevenueAnalytics();
			double periodRevenue = calculateRevenueForPeriod(clinicId, startDateTime, endDateTime);
			revenueAnalytics.setTotalRevenue((int) periodRevenue);
			revenueAnalytics.setDetailsLink("/financials/revenue-breakdown");
			dto.setRevenueAnalytics(revenueAnalytics);

			// Calculate expense analytics for the period
			DashboardSummaryDto.ExpenseAnalytics expenseAnalytics = new DashboardSummaryDto.ExpenseAnalytics();
			double periodExpenses = calculateExpensesForPeriod(clinicId, startDateTime, endDateTime);
			expenseAnalytics.setTotalExpenses((int) periodExpenses);
			expenseAnalytics.setDetailsLink("/financials/expense-breakdown");
			dto.setExpenseAnalytics(expenseAnalytics);

		} catch (Exception e) {
			logger.error("Error getting monthly summary for clinic: " + clinicId + ", period: " + startDate + " to " + endDate, e);
		}

		return dto;
	}

	private List<DashboardSummaryDto.RevenueExpenseData> getRevenueVsExpenses(String clinicId, int year) {
		List<DashboardSummaryDto.RevenueExpenseData> list = new ArrayList<>();
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			for (int i = 0; i < 12; i++) {
				int monthNumber = i + 1; // Convert to 1-based month
				
				// Calculate revenue for this month
				double revenue = calculateMonthlyRevenue(clinicId, monthNumber, year);
				
				// Calculate expenses for this month
				double expenses = calculateMonthlyExpenses(clinicId, monthNumber, year);
				
				DashboardSummaryDto.RevenueExpenseData data = new DashboardSummaryDto.RevenueExpenseData();
				data.setMonth(months[i]);
				data.setRevenue((int) revenue);
				data.setExpenses((int) expenses);
				list.add(data);
			}
		} catch (Exception e) {
			logger.error("Error getting revenue vs expenses data for clinic: " + clinicId + ", year: " + year, e);
		}
		
		return list;
	}

	private List<DashboardSummaryDto.PatientTrendData> getPatientTrends(String clinicId, int year) {
		List<DashboardSummaryDto.PatientTrendData> list = new ArrayList<>();
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			for (int i = 0; i < 12; i++) {
				int monthNumber = i + 1; // Convert to 1-based month
				
				// Calculate new patients for this month
				int newPatients = calculateMonthlyNewPatients(clinicId, monthNumber, year);
				
				// Calculate returning patients for this month
				int returningPatients = calculateMonthlyReturningPatients(clinicId, monthNumber, year);
				
				// Calculate total footfall (visits) for this month
				int totalFootfall = calculateMonthlyFootfall(clinicId, monthNumber, year);
				
				DashboardSummaryDto.PatientTrendData data = new DashboardSummaryDto.PatientTrendData();
				data.setMonth(months[i]);
				data.setNewPatients(newPatients);
				data.setReturningPatients(returningPatients);
				data.setTotalFootfall(totalFootfall);
				list.add(data);
			}
		} catch (Exception e) {
			logger.error("Error getting patient trends data for clinic: " + clinicId + ", year: " + year, e);
		}
		
		return list;
	}

	/**
	 * Calculate monthly revenue for a specific clinic, month, and year
	 */
	private double calculateMonthlyRevenue(String clinicId, int month, int year) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Create date range for the specific month
			DateTime startOfMonth = new DateTime(year, month, 1, 0, 0, 0);
			DateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
			
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicObjectId);
			criteria.and("discarded").is(false);
			criteria.and("receivedDate").gte(startOfMonth.toDate()).lte(endOfMonth.toDate());

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating monthly revenue for clinic: " + clinicId + ", month: " + month + ", year: " + year, e);
			return 0.0;
		}
	}

	/**
	 * Calculate monthly expenses for a specific clinic, month, and year
	 */
	private double calculateMonthlyExpenses(String clinicId, int month, int year) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Create date range for the specific month
			DateTime startOfMonth = new DateTime(year, month, 1, 0, 0, 0);
			DateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
			
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicObjectId);
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startOfMonth.toDate()).lte(endOfMonth.toDate());

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating monthly expenses for clinic: " + clinicId + ", month: " + month + ", year: " + year, e);
			return 0.0;
		}
	}

	/**
	 * Calculate new patients for a specific clinic, month, and year
	 */
	private int calculateMonthlyNewPatients(String clinicId, int month, int year) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Create date range for the specific month
			DateTime startOfMonth = new DateTime(year, month, 1, 0, 0, 0);
			DateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
			
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicObjectId);
			criteria.and("discarded").is(false);
			criteria.and("createdTime").gte(startOfMonth.toDate()).lte(endOfMonth.toDate());

			List<PatientCollection> newPatients = mongoTemplate.find(new Query(criteria), PatientCollection.class);
			return newPatients.size();
		} catch (Exception e) {
			logger.error("Error calculating monthly new patients for clinic: " + clinicId + ", month: " + month + ", year: " + year, e);
			return 0;
		}
	}

	/**
	 * Calculate returning patients for a specific clinic, month, and year
	 */
	private int calculateMonthlyReturningPatients(String clinicId, int month, int year) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Create date range for the specific month
			DateTime startOfMonth = new DateTime(year, month, 1, 0, 0, 0);
			DateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
			
			// Get all patients who visited in this month
			Criteria visitCriteria = new Criteria();
			visitCriteria.and("locationId").is(clinicObjectId);
			visitCriteria.and("discarded").is(false);
			visitCriteria.and("createdTime").gte(startOfMonth.toDate()).lte(endOfMonth.toDate());

			List<PatientVisitCollection> visits = mongoTemplate.find(new Query(visitCriteria), PatientVisitCollection.class);
			
			// Get unique patients who visited
			List<ObjectId> visitingPatientIds = visits.stream()
					.map(PatientVisitCollection::getPatientId)
					.distinct()
					.collect(Collectors.toList());
			
			if (visitingPatientIds.isEmpty()) {
				return 0;
			}
			
			// Get patients who were created before this month (returning patients)
			Criteria returningCriteria = new Criteria();
			returningCriteria.and("_id").in(visitingPatientIds);
			returningCriteria.and("locationId").is(clinicObjectId);
			returningCriteria.and("discarded").is(false);
			returningCriteria.and("createdTime").lt(startOfMonth.toDate());

			List<PatientCollection> returningPatients = mongoTemplate.find(new Query(returningCriteria), PatientCollection.class);
			return returningPatients.size();
		} catch (Exception e) {
			logger.error("Error calculating monthly returning patients for clinic: " + clinicId + ", month: " + month + ", year: " + year, e);
			return 0;
		}
	}

	/**
	 * Calculate total footfall (visits) for a specific clinic, month, and year
	 */
	private int calculateMonthlyFootfall(String clinicId, int month, int year) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Create date range for the specific month
			DateTime startOfMonth = new DateTime(year, month, 1, 0, 0, 0);
			DateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
			
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicObjectId);
			criteria.and("discarded").is(false);
			criteria.and("createdTime").gte(startOfMonth.toDate()).lte(endOfMonth.toDate());

			List<PatientVisitCollection> visits = mongoTemplate.find(new Query(criteria), PatientVisitCollection.class);
			return visits.size();
		} catch (Exception e) {
			logger.error("Error calculating monthly footfall for clinic: " + clinicId + ", month: " + month + ", year: " + year, e);
			return 0;
		}
	}

	@Override
	public List<PerformanceMetricsDto> getPerformanceMetrics(String clinicId, String startDate, String endDate) {
		List<PerformanceMetricsDto> list = new ArrayList<>();

		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Parse date strings to DateTime objects
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current year if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999);
			}
			
			// Generate monthly breakdown for the date range
			DateTime current = startDateTime.withDayOfMonth(1).withTime(0, 0, 0, 0);
			String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			
			// Calculate the end month (first day of the month after endDateTime)
			DateTime endMonth = endDateTime.withDayOfMonth(1).plusMonths(1);
			
			logger.info("Performance metrics date range - Start: " + startDateTime + ", End: " + endDateTime + ", EndMonth: " + endMonth);
			logger.info("Starting month iteration from: " + current);
			
			while (current.isBefore(endMonth)) {
				DateTime monthStart = current;
				DateTime monthEnd = current.plusMonths(1).minusSeconds(1);
				
				// Calculate revenue for this month
				double revenue = calculateRevenueForPeriod(clinicId, monthStart, monthEnd);
				
				// Calculate expenses for this month
				double expenses = calculateExpensesForPeriod(clinicId, monthStart, monthEnd);
				
				// Calculate net profit
				double netProfit = revenue - expenses;
				
				// Calculate new patients for this month
				int newPatients = calculateNewPatientsForPeriod(clinicId, monthStart, monthEnd);
				
				// Calculate returning patients for this month
				int returningPatients = calculateReturningPatientsForPeriod(clinicId, monthStart, monthEnd);
				
				// Calculate total footfall (visits) for this month
				int totalFootfall = calculateFootfallForPeriod(clinicId, monthStart, monthEnd);
				
				PerformanceMetricsDto dto = new PerformanceMetricsDto();
				String monthLabel = months[current.getMonthOfYear() - 1] + " " + current.getYear();
				dto.setMonth(monthLabel);
				dto.setRevenue((int) revenue);
				dto.setExpenses((int) expenses);
				dto.setNetProfit((int) netProfit);
				dto.setNewPatients(newPatients);
				dto.setReturningPatients(returningPatients);
				dto.setTotalFootfall(totalFootfall);
				list.add(dto);
				
				logger.info("Added month: " + monthLabel + " (Revenue: " + revenue + ", Expenses: " + expenses + ")");
				
				// Move to next month
				current = current.plusMonths(1);
			}
			
			logger.info("Total months generated: " + list.size());

		} catch (Exception e) {
			logger.error("Error getting performance metrics for clinic: " + clinicId + ", period: " + startDate + " to " + endDate, e);
		}
		
		return list;
	}

	@Override
	public FinancialSummaryDto getCompanyFinancials(String startDate, String endDate) {
		FinancialSummaryDto dto = new FinancialSummaryDto();
		dto.setPeriod(startDate + " to " + endDate);
		
		try {
			// Parse date strings to DateTime objects
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current year if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999);
			}
			
			// Calculate total revenue across all clinics
			double totalRevenue = calculateCompanyRevenue(startDateTime, endDateTime);
			dto.setTotalRevenue((int) totalRevenue);
			
			// Calculate total costs across all clinics
			double totalCosts = calculateCompanyExpenses(startDateTime, endDateTime);
			dto.setTotalCosts((int) totalCosts);
			
			// Calculate EBIT (Earnings Before Interest and Taxes)
			double ebit = totalRevenue - totalCosts;
			dto.setEbit((int) ebit);
			
			// For company-level financials, we'll use estimated values for interest and taxes
			// In a real implementation, these would come from accounting systems
			double interest = ebit * 0.15; // Assume 15% of EBIT as interest
			dto.setInterest((int) interest);
			
			double taxes = (ebit - interest) * 0.25; // Assume 25% tax rate
			dto.setTaxes((int) taxes);
			
			// Calculate net income
			double netIncome = ebit - interest - taxes;
			dto.setNetIncome((int) netIncome);
			
		} catch (Exception e) {
			logger.error("Error getting company financials for period: " + startDate + " to " + endDate, e);
			// Set default values in case of error
			dto.setTotalRevenue(0);
			dto.setTotalCosts(0);
			dto.setEbit(0);
			dto.setInterest(0);
			dto.setTaxes(0);
			dto.setNetIncome(0);
		}
		
		return dto;
	}

	@Override
	public FinancialSummaryDto getClinicFinancials(String clinicId, String startDate, String endDate) {
		FinancialSummaryDto dto = new FinancialSummaryDto();
		dto.setPeriod(startDate + " to " + endDate);
		
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Parse date strings to DateTime objects
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current year if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999);
			}
			
			// Calculate total revenue for the clinic
			double totalRevenue = calculateClinicRevenueForPeriod(clinicObjectId, startDateTime, endDateTime);
			dto.setTotalRevenue((int) totalRevenue);
			
			// Calculate total costs for the clinic
			double totalCosts = calculateClinicExpensesForPeriod(clinicObjectId, startDateTime, endDateTime);
			dto.setTotalCosts((int) totalCosts);
			
			// Calculate EBIT (Earnings Before Interest and Taxes)
			double ebit = totalRevenue - totalCosts;
			dto.setEbit((int) ebit);
			
			// For clinic-level financials, we'll use estimated values for interest and taxes
			// In a real implementation, these would come from accounting systems
			double interest = ebit * 0.12; // Assume 12% of EBIT as interest for clinics
			dto.setInterest((int) interest);
			
			double taxes = (ebit - interest) * 0.20; // Assume 20% tax rate for clinics
			dto.setTaxes((int) taxes);
			
			// Calculate net income
			double netIncome = ebit - interest - taxes;
			dto.setNetIncome((int) netIncome);
			
		} catch (Exception e) {
			logger.error("Error getting clinic financials for clinic: " + clinicId + ", period: " + startDate + " to " + endDate, e);
			// Set default values in case of error
			dto.setTotalRevenue(0);
			dto.setTotalCosts(0);
			dto.setEbit(0);
			dto.setInterest(0);
			dto.setTaxes(0);
			dto.setNetIncome(0);
		}
		
		return dto;
	}

	@Override
	public List<ClinicPerformanceDto> getClinicPerformanceComparison(String startDate, String endDate) {
		List<ClinicPerformanceDto> list = new ArrayList<>();
		
		try {
			// Parse date strings to DateTime objects
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current year if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withMonthOfYear(12).withDayOfMonth(31).withTime(23, 59, 59, 999);
			}
			
			// Get all dental chain clinics
			Criteria clinicCriteria = new Criteria();
			clinicCriteria.and("isDentalChain").is(true);
			clinicCriteria.and("isClinic").is(true);
			clinicCriteria.and("discarded").is(false);
			
			List<LocationCollection> clinics = mongoTemplate.find(new Query(clinicCriteria), LocationCollection.class);
			
			for (LocationCollection clinic : clinics) {
				ClinicPerformanceDto dto = new ClinicPerformanceDto();
				
				// Set basic clinic information
				dto.setClinicName(clinic.getLocationName());
				dto.setZone(clinic.getZone());
				dto.setSpecialty(clinic.getSpecialization() != null && !clinic.getSpecialization().isEmpty() 
					? clinic.getSpecialization().get(0) : "Dental");
				
				// Calculate financial metrics for the clinic
				double revenue = calculateRevenueForPeriod(clinic.getId(), startDateTime, endDateTime);
				double ebitda = calculateEBITDA(clinic.getId(), startDateTime, endDateTime);
				double netProfit = calculateNetProfit(clinic.getId(), startDateTime, endDateTime);
				double capex = calculateCAPEX(clinic.getId(), startDateTime, endDateTime);
				
				// Calculate profit share percentage (EBITDA to revenue ratio)
				double profitShare = revenue > 0 ? (ebitda / revenue) * 100 : 0;
				
				// Determine status based on performance
				String status = determineClinicStatus(revenue, ebitda, profitShare);
				
				// Set calculated values
				dto.setRevenue((int) revenue);
				dto.setProfitShare((int) profitShare);
				dto.setCapex((int) capex);
				dto.setEbitda((int) ebitda);
				dto.setNetProfit((int) netProfit);
				dto.setStatus(status);
				
				list.add(dto);
			}
			
			// Sort by revenue in descending order
			list.sort((a, b) -> Integer.compare(b.getRevenue(), a.getRevenue()));
			
		} catch (Exception e) {
			logger.error("Error getting clinic performance comparison for period: " + startDate + " to " + endDate, e);
		}
		
		return list;
	}

	@Override
	public List<DashboardClinicResponse> getclinics(String startDate, String endDate, String zone, String city,
			String searchTerm, String page, String size) {
		List<DashboardClinicResponse> response = new ArrayList<>();
		try {
			// Build criteria for filtering clinics
			Criteria criteria = new Criteria();

			// Filter for dental chain clinics only
			criteria.and("isDentalChain").is(true);
			criteria.and("isClinic").is(true);

			// Apply search term filter
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("locationName").regex("^" + searchTerm));
			}

			// Apply zone filter
			if (!DPDoctorUtils.anyStringEmpty(zone)) {
				criteria.and("zone").is(zone);
			}

			// Apply city filter
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				criteria.and("city").is(city);
			}

			// Apply date range filter
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				applyDateCriteria(criteria, "createdTime", startDate, endDate);
			}

			// Build aggregation pipeline
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

			// Apply pagination if provided
			if (!DPDoctorUtils.anyStringEmpty(page, size)) {
				int pageNum = Integer.parseInt(page);
				int sizeNum = Integer.parseInt(size);
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip((long) pageNum * sizeNum), Aggregation.limit(sizeNum));
			}

			// Execute aggregation to get clinics
			List<LocationCollection> clinics = mongoTemplate
					.aggregate(aggregation, "location_cl", LocationCollection.class).getMappedResults();
			System.out.println("aggregation" + aggregation);
			System.out.println(clinics.size());
			// Convert to DashboardClinicResponse and calculate financial metrics
			for (LocationCollection clinic : clinics) {
				DashboardClinicResponse clinicResponse = new DashboardClinicResponse();
				clinicResponse.setId(clinic.getId().toString());
				clinicResponse.setClinicName(clinic.getLocationName());
				clinicResponse.setCity(clinic.getCity());
				clinicResponse.setSpecialty(clinic.getSpecialization() != null && !clinic.getSpecialization().isEmpty()
						? clinic.getSpecialization().get(0)
						: "Dental");

				// Calculate revenue and expenses for the clinic
				double revenue = calculateClinicRevenue(clinic.getId(), startDate, endDate);
				double expenses = calculateClinicExpenses(clinic.getId(), startDate, endDate);
				double profit = revenue - expenses;
				double profitPercentage = revenue > 0 ? (profit / revenue) * 100 : 0;

				clinicResponse.setRevenue(revenue);
				clinicResponse.setExpenses(expenses);
				clinicResponse.setProfit(profit);
				clinicResponse.setProfitPercentage(profitPercentage);

				// Get doctor information (primary doctor for the clinic)
				String doctorInfo = getPrimaryDoctorForClinic(clinic.getId());
				if (doctorInfo != null) {
					String[] parts = doctorInfo.split("\\|");
					if (parts.length >= 2) {
						clinicResponse.setDoctorId(parts[0]);
						clinicResponse.setDoctorName(parts[1]);
					}
				}

				response.add(clinicResponse);
			}

		} catch (Exception e) {
			logger.error("Error while getting clinics: " + e.getMessage());
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * Calculate revenue for a specific clinic within date range
	 */
	private double calculateClinicRevenue(ObjectId clinicId, String startDate, String endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				applyDateCriteria(criteria, "receivedDate", startDate, endDate);
			}

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating clinic revenue: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Calculate expenses for a specific clinic within date range
	 */
	private double calculateClinicExpenses(ObjectId clinicId, String startDate, String endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				applyDateCriteria(criteria, "toDate", startDate, endDate);
			}

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating clinic expenses: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Get primary doctor information for a clinic
	 */
	private String getPrimaryDoctorForClinic(ObjectId clinicId) {
		try {
			// Get the first active doctor profile for this clinic
			List<DoctorClinicProfileCollection> doctorProfiles = doctorClinicProfileRepository
					.findByLocationIdAndIsActivate(clinicId, true);

			if (doctorProfiles != null && !doctorProfiles.isEmpty()) {
				DoctorClinicProfileCollection doctorProfile = doctorProfiles.get(0);

				// Get doctor details
				DoctorCollection doctorCollection = doctorRepository.findByUserId(doctorProfile.getDoctorId());
				UserCollection userCollection = userRepository.findById(doctorProfile.getDoctorId()).orElse(null);

				if (doctorCollection != null && userCollection != null) {
					String firstName = userCollection.getFirstName() != null ? userCollection.getFirstName() : "";
					String lastName = userCollection.getLastName() != null ? userCollection.getLastName() : "";

					String doctorName = (lastName.isEmpty()) ? firstName : firstName + " " + lastName;
					return doctorProfile.getDoctorId().toString() + "|" + doctorName;
				}
			}
			return null;
		} catch (Exception e) {
			logger.error("Error getting primary doctor: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ViewClinicResponse getClinicById(String clinicId) {
		try {
			// Validate input
			if (DPDoctorUtils.anyStringEmpty(clinicId)) {
				logger.warn("Clinic ID is empty or null");
				return null;
			}

			// Find clinic by ID
			LocationCollection clinic = locationRepository.findById(new ObjectId(clinicId)).orElse(null);
			if (clinic == null) {
				logger.warn("Clinic not found with ID: " + clinicId);
				return null;
			}

			// Create ViewClinicResponse object
			ViewClinicResponse clinicResponse = new ViewClinicResponse();
			clinicResponse.setClinicId(clinicId);

			// Calculate revenue for the clinic (last 12 months)
			double revenue = calculateClinicRevenue(clinic.getId(), null, null);
			clinicResponse.setRevenue((int) revenue);

			// Calculate net income (revenue - expenses)
			double expenses = calculateClinicExpenses(clinic.getId(), null, null);
			double netIncome = revenue - expenses;
			clinicResponse.setNetIncome((int) netIncome);

			// Calculate patient metrics
			int totalPatients = calculateTotalPatients(clinic.getId());
			clinicResponse.setTotalPatient(totalPatients);

			// Calculate footfall (total visits)
			int footfall = calculateClinicFootfall(clinic.getId());
			clinicResponse.setFootfall(footfall);

			// Calculate new patient conversion rate
			double newPatientConversion = calculateNewPatientConversion(clinic.getId());
			clinicResponse.setNewPatientConversion(newPatientConversion);

			// Calculate new vs returning patients
			int newPatients = calculateNewPatients(clinic.getId());
			int returningPatients = totalPatients - newPatients;
			clinicResponse.setNewPatients(newPatients);
			clinicResponse.setReturning(returningPatients);

			// Get doctor in charge
			String doctorInCharge = getPrimaryDoctorForClinic(clinic.getId());
			if (doctorInCharge != null) {
				String[] parts = doctorInCharge.split("\\|");
				if (parts.length >= 2) {
					clinicResponse.setDoctorInCharge(parts[1]);
				}
			} else {
				clinicResponse.setDoctorInCharge("Not Assigned");
			}

			// Set operatories (default to 3 if not available)
			clinicResponse.setOperatories(3);

			// Calculate breakeven status
			String breakevenStatus = netIncome > 0 ? "Achieved" : "Not Achieved";
			// clinicResponse.setBreakevenStatus(breakevenStatus);

			// Calculate average rating (from clinic reviews if available)
			double averageRating = calculateAverageRating(clinic.getId());
			// clinicResponse.setAverageRating(averageRating);

			// Calculate treatment completion rate
			double treatmentCompletion = calculateTreatmentCompletion(clinic.getId());
			// clinicResponse.setTreatmentCompletion(treatmentCompletion);

			return clinicResponse;

		} catch (Exception e) {
			logger.error("Error while getting clinic by ID: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculate total patients for a clinic
	 */
	private int calculateTotalPatients(ObjectId clinicId) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			List<PatientCollection> patients = mongoTemplate.find(new Query(criteria), PatientCollection.class);
			return patients.size();
		} catch (Exception e) {
			logger.error("Error calculating total patients: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Calculate clinic footfall (total visits)
	 */
	private int calculateClinicFootfall(ObjectId clinicId) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			List<PatientVisitCollection> visits = mongoTemplate.find(new Query(criteria), PatientVisitCollection.class);
			return visits.size();
		} catch (Exception e) {
			logger.error("Error calculating clinic footfall: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Calculate new patient conversion rate
	 */
	private double calculateNewPatientConversion(ObjectId clinicId) {
		try {
			int totalPatients = calculateTotalPatients(clinicId);
			int newPatients = calculateNewPatients(clinicId);

			if (totalPatients > 0) {
				return (double) newPatients / totalPatients * 100;
			}
			return 0.0;
		} catch (Exception e) {
			logger.error("Error calculating new patient conversion: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Calculate new patients for a clinic
	 */
	private int calculateNewPatients(ObjectId clinicId) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			// Get patients created in the last 12 months
			DateTime twelveMonthsAgo = DateTime.now().minusMonths(12);
			criteria.and("createdTime").gte(twelveMonthsAgo.toDate());

			List<PatientCollection> newPatients = mongoTemplate.find(new Query(criteria), PatientCollection.class);
			return newPatients.size();
		} catch (Exception e) {
			logger.error("Error calculating new patients: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Calculate average rating for a clinic
	 */
	private double calculateAverageRating(ObjectId clinicId) {
		try {
			// This would typically come from a reviews/ratings collection
			// For now, return a default value
			return 4.5;
		} catch (Exception e) {
			logger.error("Error calculating average rating: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Calculate treatment completion rate
	 */
	private double calculateTreatmentCompletion(ObjectId clinicId) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);

			List<PatientTreatmentCollection> treatments = mongoTemplate.find(new Query(criteria),
					PatientTreatmentCollection.class);

			int totalTreatments = 0;
			int completedTreatments = 0;

			for (PatientTreatmentCollection treatment : treatments) {
				if (treatment.getPatientTreatments() != null) {
					for (com.dpdocter.beans.PatientTreatment patientTreatment : treatment.getPatientTreatments()) {
						totalTreatments++;
						if (patientTreatment.getStatus() == PatientTreatmentStatus.COMPLETED) {
							completedTreatments++;
						}
					}
				}
			}

			if (totalTreatments > 0) {
				return (double) completedTreatments / totalTreatments * 100;
			}
			return 0.0;
		} catch (Exception e) {
			logger.error("Error calculating treatment completion: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Calculate company revenue across all clinics for a given period
	 */
	private double calculateCompanyRevenue(DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("discarded").is(false);
			criteria.and("receivedDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating company revenue for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate company expenses across all clinics for a given period
	 */
	private double calculateCompanyExpenses(DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating company expenses for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate clinic revenue for a specific period
	 */
	private double calculateClinicRevenueForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("receivedDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating clinic revenue for period: " + startDate + " to " + endDate + " for clinic: " + clinicId, e);
			return 0.0;
		}
	}

	/**
	 * Calculate clinic expenses for a specific period
	 */
	private double calculateClinicExpensesForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating clinic expenses for period: " + startDate + " to " + endDate + " for clinic: " + clinicId, e);
			return 0.0;
		}
	}

	// New Financial APIs Implementation

	@Override
	public FinancialOverviewDto getFinancialOverview(String startDate, String endDate, String zone, String clinicId) {
		try {
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			// Parse date strings to DateTime objects
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current month if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withDayOfMonth(1).plusMonths(1).minusDays(1).withTime(23, 59, 59, 999);
			}
			
			double totalRevenue = 0.0;
			double totalExpenses = 0.0;
			
			if (!DPDoctorUtils.anyStringEmpty(clinicId)) {
				// Clinic-specific overview
				ObjectId clinicObjectId = new ObjectId(clinicId);
				totalRevenue = calculateClinicRevenueForPeriod(clinicObjectId, startDateTime, endDateTime);
				totalExpenses = calculateClinicExpensesForPeriod(clinicObjectId, startDateTime, endDateTime);
			} else {
				// Company-wide overview with optional zone filter
				List<ObjectId> locationIds = new ArrayList<>();
				
				if (!DPDoctorUtils.anyStringEmpty(zone)) {
					Criteria locationCriteria = new Criteria();
					locationCriteria.and("isDentalChain").is(true);
					locationCriteria.and("zone").is(zone);
					
					List<LocationCollection> matchedLocations = mongoTemplate.find(new Query(locationCriteria),
							LocationCollection.class);
					locationIds = matchedLocations.stream().map(LocationCollection::getId).collect(Collectors.toList());
				}
				
				if (locationIds.isEmpty()) {
					// No zone filter or no locations found, get all
					totalRevenue = calculateCompanyRevenue(startDateTime, endDateTime);
					totalExpenses = calculateCompanyExpenses(startDateTime, endDateTime);
				} else {
					// Zone-specific calculation
					totalRevenue = calculateRevenueForLocations(locationIds, startDateTime, endDateTime);
					totalExpenses = calculateExpensesForLocations(locationIds, startDateTime, endDateTime);
				}
			}
			
			double totalProfit = totalRevenue - totalExpenses;
			double profitPercentage = totalRevenue > 0 ? (totalProfit / totalRevenue) * 100 : 0.0;
			
			return new FinancialOverviewDto(totalRevenue, totalExpenses, totalProfit, profitPercentage);
			
		} catch (Exception e) {
			logger.error("Error getting financial overview for startDate: " + startDate + ", endDate: " + endDate + ", zone: " + zone + ", clinicId: " + clinicId, e);
			return new FinancialOverviewDto(0.0, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public RevenueExpensesTrendDto getRevenueExpensesTrend(String range, String startDate, String endDate, String clinicId) {
		try {
			List<String> labels = new ArrayList<>();
			List<Double> revenue = new ArrayList<>();
			List<Double> expenses = new ArrayList<>();
			
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			// Parse date strings to DateTime objects
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current month if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withDayOfMonth(1).plusMonths(1).minusDays(1).withTime(23, 59, 59, 999);
			}
			
			if ("weekly".equalsIgnoreCase(range)) {
				// Generate weekly data
				DateTime current = startDateTime;
				int weekCounter = 1;
				
				while (current.isBefore(endDateTime) || current.isEqual(endDateTime)) {
					DateTime weekEnd = current.plusWeeks(1).minusSeconds(1);
					if (weekEnd.isAfter(endDateTime)) {
						weekEnd = endDateTime;
					}
					
					labels.add("Week " + weekCounter);
					if (!DPDoctorUtils.anyStringEmpty(clinicId)) {
						revenue.add(calculateRevenueForPeriod(clinicId, current, weekEnd));
						expenses.add(calculateExpensesForPeriod(clinicId, current, weekEnd));
					} else {
						revenue.add(calculateCompanyRevenue(current, weekEnd));
						expenses.add(calculateCompanyExpenses(current, weekEnd));
					}
					
					current = current.plusWeeks(1);
					weekCounter++;
				}
			} else if ("monthly".equalsIgnoreCase(range)) {
				// Generate monthly data
				DateTime current = startDateTime;
				int monthCounter = 1;
				
				while (current.isBefore(endDateTime) || current.isEqual(endDateTime)) {
					DateTime monthEnd = current.plusMonths(1).minusSeconds(1);
					if (monthEnd.isAfter(endDateTime)) {
						monthEnd = endDateTime;
					}
					
					labels.add("Month " + monthCounter);
					if (!DPDoctorUtils.anyStringEmpty(clinicId)) {
						revenue.add(calculateRevenueForPeriod(clinicId, current, monthEnd));
						expenses.add(calculateExpensesForPeriod(clinicId, current, monthEnd));
					} else {
						revenue.add(calculateCompanyRevenue(current, monthEnd));
						expenses.add(calculateCompanyExpenses(current, monthEnd));
					}
					
					current = current.plusMonths(1);
					monthCounter++;
				}
			} else {
				// Default to yearly data
				DateTime current = startDateTime;
				int yearCounter = 1;
				
				while (current.isBefore(endDateTime) || current.isEqual(endDateTime)) {
					DateTime yearEnd = current.plusYears(1).minusSeconds(1);
					if (yearEnd.isAfter(endDateTime)) {
						yearEnd = endDateTime;
					}
					
					labels.add("Year " + yearCounter);
					if (!DPDoctorUtils.anyStringEmpty(clinicId)) {
						revenue.add(calculateRevenueForPeriod(clinicId, current, yearEnd));
						expenses.add(calculateExpensesForPeriod(clinicId, current, yearEnd));
					} else {
						revenue.add(calculateCompanyRevenue(current, yearEnd));
						expenses.add(calculateCompanyExpenses(current, yearEnd));
					}
					
					current = current.plusYears(1);
					yearCounter++;
				}
			}
			
			return new RevenueExpensesTrendDto(labels, revenue, expenses);
			
		} catch (Exception e) {
			logger.error("Error getting revenue expenses trend for range: " + range + ", startDate: " + startDate + ", endDate: " + endDate + ", clinicId: " + clinicId, e);
			return new RevenueExpensesTrendDto(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
	}

	@Override
	public ClinicWiseFinancialsDto getClinicWiseFinancials(String startDate, String endDate) {
		try {
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			// Parse date strings to DateTime objects
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current month if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withDayOfMonth(1).plusMonths(1).minusDays(1).withTime(23, 59, 59, 999);
			}
			
			// Get all dental chain clinics
			Criteria criteria = new Criteria();
			criteria.and("isDentalChain").is(true);
			criteria.and("isClinic").is(true);
			
			List<LocationCollection> clinics = mongoTemplate.find(new Query(criteria), LocationCollection.class);
			List<ClinicWiseFinancialsDto.ClinicFinancialData> clinicDataList = new ArrayList<>();
			
			for (LocationCollection clinic : clinics) {
				double clinicRevenue = calculateClinicRevenueForPeriod(clinic.getId(), startDateTime, endDateTime);
				double clinicExpenses = calculateClinicExpensesForPeriod(clinic.getId(), startDateTime, endDateTime);
				double clinicProfit = clinicRevenue - clinicExpenses;
				double profitPercentage = clinicRevenue > 0 ? (clinicProfit / clinicRevenue) * 100 : 0.0;
				
				ClinicWiseFinancialsDto.ClinicFinancialData clinicData = new ClinicWiseFinancialsDto.ClinicFinancialData(
					clinic.getId().toString(),
					clinic.getLocationName(),
					clinicRevenue,
					clinicExpenses,
					clinicProfit,
					profitPercentage
				);
				
				clinicDataList.add(clinicData);
			}
			
			return new ClinicWiseFinancialsDto(clinicDataList);
			
		} catch (Exception e) {
			logger.error("Error getting clinic-wise financials for startDate: " + startDate + ", endDate: " + endDate, e);
			return new ClinicWiseFinancialsDto(new ArrayList<>());
		}
	}

	@Override
	public ExpensesBreakdownDto getExpensesBreakdown(String startDate, String endDate, String clinicId) {
		try {
			DateTime startDateTime = null;
			DateTime endDateTime = null;
			
			// Parse date strings to DateTime objects
			if (!DPDoctorUtils.anyStringEmpty(startDate, endDate)) {
				startDateTime = parseTimestampToDateTime(startDate);
				endDateTime = parseTimestampToDateTime(endDate);
				if (endDateTime != null) {
					endDateTime = endDateTime.withTime(23, 59, 59, 999);
				}
			}
			
			// Default to current month if dates not provided or parsing failed
			if (startDateTime == null) {
				startDateTime = new DateTime().withDayOfMonth(1).withTime(0, 0, 0, 0);
			}
			if (endDateTime == null) {
				endDateTime = new DateTime().withDayOfMonth(1).plusMonths(1).minusDays(1).withTime(23, 59, 59, 999);
			}
			
			Criteria criteria = new Criteria();
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDateTime.toDate()).lte(endDateTime.toDate());
			
			if (!DPDoctorUtils.anyStringEmpty(clinicId)) {
				criteria.and("locationId").is(new ObjectId(clinicId));
			}
			
			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria), DoctorExpenseCollection.class);
			
			// Group expenses by category (assuming there's an expenseType field)
			Map<String, Double> categoryTotals = new HashMap<>();
			
			for (DoctorExpenseCollection expense : expenses) {
				String category = expense.getExpenseType() != null ? expense.getExpenseType() : "Other";
				double amount = expense.getCost() != null ? expense.getCost() : 0.0;
				
				categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
			}
			
			List<ExpensesBreakdownDto.ExpenseCategory> categories = new ArrayList<>();
			for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
				categories.add(new ExpensesBreakdownDto.ExpenseCategory(entry.getKey(), entry.getValue()));
			}
			
			// Sort by amount in descending order
			categories.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
			
			return new ExpensesBreakdownDto(categories);
			
		} catch (Exception e) {
			logger.error("Error getting expenses breakdown for startDate: " + startDate + ", endDate: " + endDate + ", clinicId: " + clinicId, e);
			return new ExpensesBreakdownDto(new ArrayList<>());
		}
	}

	/**
	 * Calculate revenue for specific locations
	 */
	private double calculateRevenueForLocations(List<ObjectId> locationIds, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").in(locationIds);
			criteria.and("discarded").is(false);
			criteria.and("receivedDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating revenue for locations: " + locationIds + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate expenses for specific locations
	 */
	private double calculateExpensesForLocations(List<ObjectId> locationIds, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").in(locationIds);
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating expenses for locations: " + locationIds + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	// New helper methods for period-based calculations
	
	/**
	 * Get revenue vs expenses data for a specific period
	 */
	private List<DashboardSummaryDto.RevenueExpenseData> getRevenueVsExpensesForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		List<DashboardSummaryDto.RevenueExpenseData> list = new ArrayList<>();
		
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Generate monthly breakdown for the date range
			DateTime current = startDate.withDayOfMonth(1).withTime(0, 0, 0, 0);
			String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			
			while (current.isBefore(endDate) || current.isEqual(endDate)) {
				DateTime monthStart = current;
				DateTime monthEnd = current.plusMonths(1).minusSeconds(1);
				
				// Ensure we don't exceed the end date
				if (monthEnd.isAfter(endDate)) {
					monthEnd = endDate;
				}
				
				// Calculate revenue for this month
				double revenue = calculateRevenueForPeriod(clinicObjectId, monthStart, monthEnd);
				
				// Calculate expenses for this month
				double expenses = calculateExpensesForPeriod(clinicObjectId, monthStart, monthEnd);
				
				DashboardSummaryDto.RevenueExpenseData data = new DashboardSummaryDto.RevenueExpenseData();
				data.setMonth(months[current.getMonthOfYear() - 1] + " " + current.getYear());
				data.setRevenue((int) revenue);
				data.setExpenses((int) expenses);
				list.add(data);
				
				// Move to next month
				current = current.plusMonths(1);
			}
		} catch (Exception e) {
			logger.error("Error getting revenue vs expenses data for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
		}
		
		return list;
	}

	/**
	 * Get patient trends data for a specific period
	 */
	private List<DashboardSummaryDto.PatientTrendData> getPatientTrendsForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		List<DashboardSummaryDto.PatientTrendData> list = new ArrayList<>();
		
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			
			// Generate monthly breakdown for the date range
			DateTime current = startDate.withDayOfMonth(1).withTime(0, 0, 0, 0);
			String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
			
			while (current.isBefore(endDate) || current.isEqual(endDate)) {
				DateTime monthStart = current;
				DateTime monthEnd = current.plusMonths(1).minusSeconds(1);
				
				// Ensure we don't exceed the end date
				if (monthEnd.isAfter(endDate)) {
					monthEnd = endDate;
				}
				
				// Calculate new patients for this month
				int newPatients = calculateNewPatientsForPeriod(clinicObjectId, monthStart, monthEnd);
				
				// Calculate returning patients for this month
				int returningPatients = calculateReturningPatientsForPeriod(clinicObjectId, monthStart, monthEnd);
				
				// Calculate total footfall (visits) for this month
				int totalFootfall = calculateFootfallForPeriod(clinicObjectId, monthStart, monthEnd);
				
				DashboardSummaryDto.PatientTrendData data = new DashboardSummaryDto.PatientTrendData();
				data.setMonth(months[current.getMonthOfYear() - 1] + " " + current.getYear());
				data.setNewPatients(newPatients);
				data.setReturningPatients(returningPatients);
				data.setTotalFootfall(totalFootfall);
				list.add(data);
				
				// Move to next month
				current = current.plusMonths(1);
			}
		} catch (Exception e) {
			logger.error("Error getting patient trends data for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
		}
		
		return list;
	}

	/**
	 * Calculate revenue for a specific period
	 */
	private double calculateRevenueForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			return calculateRevenueForPeriod(clinicObjectId, startDate, endDate);
		} catch (Exception e) {
			logger.error("Error calculating revenue for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate revenue for a specific period (ObjectId version)
	 */
	private double calculateRevenueForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("receivedDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorPatientReceiptCollection> receipts = mongoTemplate.find(new Query(criteria),
					DoctorPatientReceiptCollection.class);

			return receipts.stream().mapToDouble(r -> r.getAmountPaid() != null ? r.getAmountPaid() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating revenue for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate expenses for a specific period
	 */
	private double calculateExpensesForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			return calculateExpensesForPeriod(clinicObjectId, startDate, endDate);
		} catch (Exception e) {
			logger.error("Error calculating expenses for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate expenses for a specific period (ObjectId version)
	 */
	private double calculateExpensesForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDate.toDate()).lte(endDate.toDate());

			List<DoctorExpenseCollection> expenses = mongoTemplate.find(new Query(criteria),
					DoctorExpenseCollection.class);

			return expenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
		} catch (Exception e) {
			logger.error("Error calculating expenses for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate new patients for a specific period
	 */
	private int calculateNewPatientsForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			return calculateNewPatientsForPeriod(clinicObjectId, startDate, endDate);
		} catch (Exception e) {
			logger.error("Error calculating new patients for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Calculate new patients for a specific period (ObjectId version)
	 */
	private int calculateNewPatientsForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("createdTime").gte(startDate.toDate()).lte(endDate.toDate());

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			List<PatientCollection> patients = mongoTemplate.aggregate(aggregation, "patient_cl", PatientCollection.class)
					.getMappedResults();

			return patients.size();
		} catch (Exception e) {
			logger.error("Error calculating new patients for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Calculate returning patients for a specific period
	 */
	private int calculateReturningPatientsForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			return calculateReturningPatientsForPeriod(clinicObjectId, startDate, endDate);
		} catch (Exception e) {
			logger.error("Error calculating returning patients for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Calculate returning patients for a specific period (ObjectId version)
	 */
	private int calculateReturningPatientsForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("createdTime").gte(startDate.toDate()).lte(endDate.toDate());

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			List<PatientVisitCollection> visits = mongoTemplate.aggregate(aggregation, "patient_visit_cl", PatientVisitCollection.class)
					.getMappedResults();

			return visits.size();
		} catch (Exception e) {
			logger.error("Error calculating returning patients for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Calculate total footfall for a specific period
	 */
	private int calculateFootfallForPeriod(String clinicId, DateTime startDate, DateTime endDate) {
		try {
			ObjectId clinicObjectId = new ObjectId(clinicId);
			return calculateFootfallForPeriod(clinicObjectId, startDate, endDate);
		} catch (Exception e) {
			logger.error("Error calculating footfall for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Calculate total footfall for a specific period (ObjectId version)
	 */
	private int calculateFootfallForPeriod(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("createdTime").gte(startDate.toDate()).lte(endDate.toDate());

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			List<PatientVisitCollection> visits = mongoTemplate.aggregate(aggregation, "patient_visit_cl", PatientVisitCollection.class)
					.getMappedResults();

			return visits.size();
		} catch (Exception e) {
			logger.error("Error calculating footfall for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0;
		}
	}

	/**
	 * Parse timestamp string to DateTime object
	 * Handles both timestamp in milliseconds and date strings
	 */
	private DateTime parseTimestampToDateTime(String timestamp) {
		try {
			if (timestamp == null || timestamp.trim().isEmpty()) {
				return null;
			}
			
			// Check if it's a numeric timestamp (milliseconds)
			if (timestamp.matches("\\d+")) {
				long timestampLong = Long.parseLong(timestamp);
				return new DateTime(timestampLong);
			} else {
				// Try to parse as date string
				return new DateTime(timestamp);
			}
		} catch (Exception e) {
			logger.error("Error parsing timestamp: " + timestamp, e);
			return null;
		}
	}

	/**
	 * Determine clinic status based on performance metrics
	 */
	private String determineClinicStatus(double revenue, double ebitda, double profitShare) {
		// Define performance thresholds
		double highRevenueThreshold = 5000000; // 5M revenue
		double highProfitShareThreshold = 30; // 30% profit share
		double highEbitdaThreshold = 1500000; // 1.5M EBITDA
		
		double mediumRevenueThreshold = 2000000; // 2M revenue
		double mediumProfitShareThreshold = 20; // 20% profit share
		double mediumEbitdaThreshold = 500000; // 500K EBITDA
		
		// Check if clinic is performing exceptionally well
		if (revenue >= highRevenueThreshold && profitShare >= highProfitShareThreshold && ebitda >= highEbitdaThreshold) {
			return "Outstanding";
		}
		// Check if clinic is performing well
		else if (revenue >= mediumRevenueThreshold && profitShare >= mediumProfitShareThreshold && ebitda >= mediumEbitdaThreshold) {
			return "Achieved";
		}
		// Check if clinic is performing moderately
		else if (revenue >= 1000000 && profitShare >= 10 && ebitda >= 200000) {
			return "On Track";
		}
		// Check if clinic needs improvement
		else if (revenue >= 500000 && profitShare >= 5 && ebitda >= 100000) {
			return "Needs Improvement";
		}
		// Clinic is underperforming
		else {
			return "Underperforming";
		}
	}

	/**
	 * Calculate more accurate EBITDA for a clinic
	 */
	private double calculateEBITDA(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			double revenue = calculateRevenueForPeriod(clinicId, startDate, endDate);
			double expenses = calculateExpensesForPeriod(clinicId, startDate, endDate);
			
			// EBITDA = Revenue - Operating Expenses
			// For simplicity, we're using all expenses as operating expenses
			return revenue - expenses;
		} catch (Exception e) {
			logger.error("Error calculating EBITDA for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate more accurate CAPEX for a clinic
	 */
	private double calculateCAPEX(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			// Look for capital expenditure type expenses
			Criteria criteria = new Criteria();
			criteria.and("locationId").is(clinicId);
			criteria.and("discarded").is(false);
			criteria.and("toDate").gte(startDate.toDate()).lte(endDate.toDate());
			
			// Filter for capital expenditure types (equipment, infrastructure, etc.)
			criteria.and("expenseType").in("Equipment", "Infrastructure", "Renovation", "Capital");
			
			List<DoctorExpenseCollection> capexExpenses = mongoTemplate.find(new Query(criteria), DoctorExpenseCollection.class);
			
			double capex = capexExpenses.stream().mapToDouble(e -> e.getCost() != null ? e.getCost() : 0.0).sum();
			
			// If no specific CAPEX expenses found, estimate as 10% of revenue
			if (capex == 0) {
				double revenue = calculateRevenueForPeriod(clinicId, startDate, endDate);
				capex = revenue * 0.10;
			}
			
			return capex;
		} catch (Exception e) {
			logger.error("Error calculating CAPEX for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

	/**
	 * Calculate more accurate net profit for a clinic
	 */
	private double calculateNetProfit(ObjectId clinicId, DateTime startDate, DateTime endDate) {
		try {
			double ebitda = calculateEBITDA(clinicId, startDate, endDate);
			
			// Estimate interest and taxes
			double interest = ebitda * 0.15; // Assume 15% of EBITDA as interest
			double taxes = (ebitda - interest) * 0.25; // Assume 25% tax rate
			
			// Net Profit = EBITDA - Interest - Taxes
			return ebitda - interest - taxes;
		} catch (Exception e) {
			logger.error("Error calculating net profit for clinic: " + clinicId + " for period: " + startDate + " to " + endDate, e);
			return 0.0;
		}
	}

}