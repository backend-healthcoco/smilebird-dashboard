package com.dpdocter.services.impl;

import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.response.DashboardKPIDataResponse;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.services.DashboardKPIService;

import common.util.web.DPDoctorUtils;
import common.util.web.DateUtil;

import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.beans.DashboardSummaryDto;
import com.dpdocter.beans.PerformanceMetricsDto;
import com.dpdocter.beans.FinancialSummaryDto;
import com.dpdocter.beans.ClinicPerformanceDto;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardKPIServiceImpl implements DashboardKPIService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorPatientReceiptRepository doctorPatientReceiptRepository;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public DashboardKPIDataResponse getKPIs(String fromDate, String toDate, String city, String zone, String doctorId,
			String locationId, String speciality) {
		DashboardKPIDataResponse kpi = new DashboardKPIDataResponse();

		// Step 1: Fetch locationIds based on city and zone
		List<ObjectId> locationIds = new ArrayList<>();
		Criteria locationCriteria = new Criteria();
		locationCriteria.and("isDentalChain").is(true);

		if (!DPDoctorUtils.anyStringEmpty(city)) {
			locationCriteria.and("city").is(city);
		}

		if (!DPDoctorUtils.anyStringEmpty(zone)) {
			locationCriteria.and("zone").is(zone);
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
		applyDateCriteria(receiptCriteria, "receivedDate", fromDate, toDate);

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

		applyDateCriteria(visitCriteria, "createdTime", fromDate, toDate);

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

		applyDateCriteria(expenseCriteria, "toDate", fromDate, toDate);

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
    public DashboardSummaryDto getMonthlySummary(String clinicId, int month, int year) {
        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setClinicId(clinicId);
        dto.setMonth(month);
        dto.setYear(year);

        dto.setRevenueVsExpenses(getRevenueVsExpenses(clinicId, year));
        dto.setPatientTrends(getPatientTrends(clinicId, year));

        DashboardSummaryDto.RevenueAnalytics revenueAnalytics = new DashboardSummaryDto.RevenueAnalytics();
        revenueAnalytics.setTotalRevenue(1200000);
        revenueAnalytics.setDetailsLink("/financials/revenue-breakdown");
        dto.setRevenueAnalytics(revenueAnalytics);

        DashboardSummaryDto.ExpenseAnalytics expenseAnalytics = new DashboardSummaryDto.ExpenseAnalytics();
        expenseAnalytics.setTotalExpenses(800000);
        expenseAnalytics.setDetailsLink("/financials/expense-breakdown");
        dto.setExpenseAnalytics(expenseAnalytics);

        return dto;
    }

    private List<DashboardSummaryDto.RevenueExpenseData> getRevenueVsExpenses(String clinicId, int year) {
        List<DashboardSummaryDto.RevenueExpenseData> list = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] revenues = {100000, 120000, 115000, 130000, 140000, 125000,
                          135000, 128000, 145000, 138000, 132000, 142000};
        int[] expenses = {60000, 75000, 72000, 80000, 85000, 77000,
                          80000, 79000, 88000, 86000, 83000, 89000};

        for (int i = 0; i < 12; i++) {
            DashboardSummaryDto.RevenueExpenseData data = new DashboardSummaryDto.RevenueExpenseData();
            data.setMonth(months[i]);
            data.setRevenue(revenues[i]);
            data.setExpenses(expenses[i]);
            list.add(data);
        }
        return list;
    }

    private List<DashboardSummaryDto.PatientTrendData> getPatientTrends(String clinicId, int year) {
        List<DashboardSummaryDto.PatientTrendData> list = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] newPatients = {30, 35, 40, 38, 42, 37, 45, 44, 50, 48, 47, 52};
        int[] returningPatients = {50, 55, 53, 60, 65, 58, 62, 60, 68, 66, 63, 70};

        for (int i = 0; i < 12; i++) {
            DashboardSummaryDto.PatientTrendData data = new DashboardSummaryDto.PatientTrendData();
            data.setMonth(months[i]);
            data.setNewPatients(newPatients[i]);
            data.setReturningPatients(returningPatients[i]);
            data.setTotalFootfall(newPatients[i] + returningPatients[i]);
            list.add(data);
        }
        return list;
    }

    @Override
    public List<PerformanceMetricsDto> getPerformanceMetrics(String clinicId, int year) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] revenues = {470000, 430000, 500000, 510000, 520000, 480000, 530000, 500000, 540000, 510000, 490000, 520000};
        int[] expenses = {360000, 340000, 370000, 370000, 380000, 350000, 390000, 370000, 380000, 360000, 350000, 380000};
        int[] netProfits = {110000, 80000, 130000, 140000, 140000, 130000, 140000, 130000, 160000, 150000, 140000, 140000};
        int[] newPatients = {82, 70, 89, 92, 96, 85, 98, 90, 105, 100, 97, 110};
        int[] returningPatients = {85, 104, 95, 100, 106, 99, 110, 102, 115, 108, 105, 120};
        int[] totalFootfall = {147, 176, 172, 183, 202, 184, 208, 192, 220, 208, 202, 230};
        List<PerformanceMetricsDto> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            PerformanceMetricsDto dto = new PerformanceMetricsDto();
            dto.setMonth(months[i]);
            dto.setRevenue(revenues[i]);
            dto.setExpenses(expenses[i]);
            dto.setNetProfit(netProfits[i]);
            dto.setNewPatients(newPatients[i]);
            dto.setReturningPatients(returningPatients[i]);
            dto.setTotalFootfall(totalFootfall[i]);
            list.add(dto);
        }
        return list;
    }

    @Override
    public FinancialSummaryDto getCompanyFinancials(String period) {
        FinancialSummaryDto dto = new FinancialSummaryDto();
        dto.setPeriod(period);
        dto.setTotalRevenue(4520000);
        dto.setTotalCosts(3080000);
        dto.setEbit(1440000);
        dto.setInterest(210000);
        dto.setTaxes(360000);
        dto.setNetIncome(870000);
        return dto;
    }

    @Override
    public FinancialSummaryDto getClinicFinancials(String clinicId, String period) {
        FinancialSummaryDto dto = new FinancialSummaryDto();
        dto.setPeriod(period);
        dto.setTotalRevenue(1200000);
        dto.setTotalCosts(800000);
        dto.setEbit(400000);
        dto.setInterest(50000);
        dto.setTaxes(70000);
        dto.setNetIncome(280000);
        return dto;
    }

    @Override
    public List<ClinicPerformanceDto> getClinicPerformanceComparison(String period) {
        List<ClinicPerformanceDto> list = new ArrayList<>();
        ClinicPerformanceDto c1 = new ClinicPerformanceDto();
        c1.setClinicName("Smilebird Andheri");
        c1.setZone("West");
        c1.setSpecialty("Dental");
        c1.setRevenue(4500000);
        c1.setProfitShare(40);
        c1.setCapex(250000);
        c1.setEbitda(1350000);
        c1.setNetProfit(850000);
        c1.setStatus("Achieved");
        list.add(c1);
        ClinicPerformanceDto c2 = new ClinicPerformanceDto();
        c2.setClinicName("Smilebird Bandra");
        c2.setZone("West");
        c2.setSpecialty("Ortho");
        c2.setRevenue(3800000);
        c2.setProfitShare(40);
        c2.setCapex(180000);
        c2.setEbitda(1140000);
        c2.setNetProfit(760000);
        c2.setStatus("Achieved");
        list.add(c2);
        return list;
    }
}