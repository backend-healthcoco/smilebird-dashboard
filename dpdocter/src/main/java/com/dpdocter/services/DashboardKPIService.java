package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DashboardSummaryDto;
import com.dpdocter.beans.PerformanceMetricsDto;
import com.dpdocter.beans.FinancialSummaryDto;
import com.dpdocter.beans.ClinicPerformanceDto;
import com.dpdocter.response.DashboardKPIDataResponse;

public interface DashboardKPIService {
	DashboardKPIDataResponse getKPIs(String startDate, String endDate, String clinicId, String role, String doctorId, String locationId, String speciality);

	DashboardSummaryDto getMonthlySummary(String clinicId, int month, int year);

	List<PerformanceMetricsDto> getPerformanceMetrics(String clinicId, int year);

	FinancialSummaryDto getCompanyFinancials(String period);
	FinancialSummaryDto getClinicFinancials(String clinicId, String period);
	List<ClinicPerformanceDto> getClinicPerformanceComparison(String period);
} 