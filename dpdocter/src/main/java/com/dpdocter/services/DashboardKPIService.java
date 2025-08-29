package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DashboardSummaryDto;
import com.dpdocter.beans.PerformanceMetricsDto;
import com.dpdocter.beans.FinancialSummaryDto;
import com.dpdocter.beans.ClinicPerformanceDto;
import com.dpdocter.beans.ViewClinicResponse;
import com.dpdocter.beans.FinancialOverviewDto;
import com.dpdocter.beans.RevenueExpensesTrendDto;
import com.dpdocter.beans.ClinicWiseFinancialsDto;
import com.dpdocter.beans.ExpensesBreakdownDto;
import com.dpdocter.response.DashboardKPIDataResponse;
import com.dpdocter.webservices.DashboardClinicResponse;

public interface DashboardKPIService {
	DashboardKPIDataResponse getKPIs(String startDate, String endDate, String cityId, String zoneId, String doctorId, String locationId, String speciality);

	DashboardSummaryDto getMonthlySummary(String clinicId, String startDate, String endDate);

	List<PerformanceMetricsDto> getPerformanceMetrics(String clinicId, String startDate, String endDate);

	FinancialSummaryDto getCompanyFinancials(String startDate, String endDate);
	FinancialSummaryDto getClinicFinancials(String clinicId, String startDate, String endDate);
	List<ClinicPerformanceDto> getClinicPerformanceComparison(String startDate, String endDate);

	List<DashboardClinicResponse> getclinics(String startDate, String endDate, String zone, String city,
			String searchTerm, String page, String size);
	
	ViewClinicResponse getClinicById(String clinicId);
	
	// New Financial APIs
	FinancialOverviewDto getFinancialOverview(String startDate, String endDate, String zone, String clinicId);
	RevenueExpensesTrendDto getRevenueExpensesTrend(String range, String startDate, String endDate, String clinicId);
	ClinicWiseFinancialsDto getClinicWiseFinancials(String startDate, String endDate);
	ExpensesBreakdownDto getExpensesBreakdown(String startDate, String endDate, String clinicId);
} 