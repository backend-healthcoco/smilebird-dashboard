package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.dpdocter.response.DashboardKPIDataResponse;
import com.dpdocter.services.DashboardKPIService;
import common.util.web.Response;
import com.dpdocter.beans.ViewClinicResponse;
import com.dpdocter.beans.DashboardSummaryDto;
import com.dpdocter.beans.PerformanceMetricsDto;
import com.dpdocter.beans.FinancialSummaryDto;
import com.dpdocter.beans.ClinicPerformanceDto;
import com.dpdocter.beans.FinancialOverviewDto;
import com.dpdocter.beans.RevenueExpensesTrendDto;
import com.dpdocter.beans.ClinicWiseFinancialsDto;
import com.dpdocter.beans.ExpensesBreakdownDto;

// Use PathProxy for the base URL

@RestController
@RequestMapping(value = PathProxy.DASHBOARD_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DASHBOARD_BASE_URL, description = "Dashboard KPIs Endpoint")
public class DashboardApi {
	private static Logger logger = LogManager.getLogger(DashboardApi.class.getName());

	@Autowired
	private DashboardKPIService dashboardKPIService;

	@GetMapping(value = PathProxy.DashboardApiUrls.DASHBOARD_KPIS)
	@ApiOperation(value = PathProxy.DashboardApiUrls.DASHBOARD_KPIS, notes = PathProxy.DashboardApiUrls.DASHBOARD_KPIS)
	public Response<DashboardKPIDataResponse> getKPIs(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate, @RequestParam(required = false) String cityId,
			@RequestParam(required = false) String zoneId, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam(required = false) String speciality) {
		DashboardKPIDataResponse kpis = dashboardKPIService.getKPIs(startDate, endDate, cityId, zoneId, doctorId,
				locationId, speciality);
		Response<DashboardKPIDataResponse> response = new Response<DashboardKPIDataResponse>();
		response.setData(kpis);
		return response;
	}

	/**
	 * 13. Get Clinic API 
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.CLINICS)
	@ApiOperation(value = PathProxy.DashboardApiUrls.CLINICS, notes = PathProxy.DashboardApiUrls.CLINICS)
	public Response<DashboardClinicResponse> getClinics(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,@RequestParam(required = false) String zone,
			@RequestParam(required = false) String city, @RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) String page, @RequestParam(required = false) String size) {

		List<DashboardClinicResponse> clinics = dashboardKPIService.getclinics(startDate,endDate,zone,city,searchTerm,page,size);

		Response<DashboardClinicResponse> response = new Response<>();
		response.setDataList(clinics);
		return response;
	}

	/**
	 * View Clinic by clinicId
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.VIEW_CLINIC_BY_ID)
	@ApiOperation(value = "View clinic by clinicId", notes = "Returns clinic details and KPIs by clinicId")
	public Response<ViewClinicResponse> viewClinicById(@PathVariable("clinicId") String clinicId) {
		ViewClinicResponse clinicDetails = dashboardKPIService.getClinicById(clinicId);
		
		Response<ViewClinicResponse> response = new Response<>();
		response.setData(clinicDetails);
		return response;
	}

	/**
	 * Monthly Summary API
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.MONTHLY_SUMMARY)
	@ApiOperation(value = "Monthly Summary", notes = "Returns monthly summary for a clinic with actual revenue, expenses, and patient data")
	public Response<DashboardSummaryDto> getMonthlySummary(
			@RequestParam String clinicId,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {

		DashboardSummaryDto summary = dashboardKPIService.getMonthlySummary(clinicId, startDate, endDate);
		Response<DashboardSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.PERFORMANCE_METRICS)
	@ApiOperation(value = "Performance Metrics", notes = "Returns actual performance metrics for a clinic with real revenue, expenses, and patient data")
	public Response<List<PerformanceMetricsDto>> getPerformanceMetrics(
			@RequestParam String clinicId,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		List<PerformanceMetricsDto> metrics = dashboardKPIService.getPerformanceMetrics(clinicId, startDate, endDate);
		Response<List<PerformanceMetricsDto>> response = new Response<>();
		response.setData(metrics);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.COMPANY_FINANCIALS)
	@ApiOperation(value = "Company Financials", notes = "Returns actual company-level financial summary for a given period with real revenue and expense data")
	public Response<FinancialSummaryDto> getCompanyFinancials(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		FinancialSummaryDto summary = dashboardKPIService.getCompanyFinancials(startDate, endDate);
		Response<FinancialSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.CLINIC_FINANCIALS)
	@ApiOperation(value = "Clinic Financials", notes = "Returns actual clinic-level financial summary for a given period with real revenue and expense data")
	public Response<FinancialSummaryDto> getClinicFinancials(
			@RequestParam String clinicId, 
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		FinancialSummaryDto summary = dashboardKPIService.getClinicFinancials(clinicId, startDate, endDate);
		Response<FinancialSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.CLINIC_PERFORMANCE_COMPARISON)
	@ApiOperation(value = "Clinic Performance Comparison", notes = "Returns performance comparison for clinics for a given period")
	public Response<List<ClinicPerformanceDto>> getClinicPerformanceComparison(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		List<ClinicPerformanceDto> list = dashboardKPIService.getClinicPerformanceComparison(startDate, endDate);
		Response<List<ClinicPerformanceDto>> response = new Response<>();
		response.setData(list);
		return response;
	}

	// New Financial APIs

	/**
	 * Get Financial Overview
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.FINANCIAL_OVERVIEW)
	@ApiOperation(value = "Financial Overview", notes = "Returns financial overview with total revenue, expenses, profit and profit percentage")
	public Response<FinancialOverviewDto> getFinancialOverview(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String zone,
			@RequestParam(required = false) String clinicId) {
		
		FinancialOverviewDto overview = dashboardKPIService.getFinancialOverview(startDate, endDate, zone, clinicId);
		Response<FinancialOverviewDto> response = new Response<>();
		response.setData(overview);
		return response;
	}

	/**
	 * Get Revenue vs Expenses Trend Chart
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.REVENUE_EXPENSES_TREND)
	@ApiOperation(value = "Revenue vs Expenses Trend", notes = "Returns revenue and expenses trend data for charts")
	public Response<RevenueExpensesTrendDto> getRevenueExpensesTrend(
			@RequestParam(required = false) String range,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String clinicId) {
		
		RevenueExpensesTrendDto trend = dashboardKPIService.getRevenueExpensesTrend(range, startDate, endDate, clinicId);
		Response<RevenueExpensesTrendDto> response = new Response<>();
		response.setData(trend);
		return response;
	}

	/**
	 * Get Clinic-wise Financials
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.CLINIC_WISE_FINANCIALS)
	@ApiOperation(value = "Clinic-wise Financials", notes = "Returns financial data for all clinics")
	public Response<ClinicWiseFinancialsDto> getClinicWiseFinancials(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate) {
		
		ClinicWiseFinancialsDto clinicFinancials = dashboardKPIService.getClinicWiseFinancials(startDate, endDate);
		Response<ClinicWiseFinancialsDto> response = new Response<>();
		response.setData(clinicFinancials);
		return response;
	}

	/**
	 * Get Expenses Breakdown by Category
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.EXPENSES_BREAKDOWN)
	@ApiOperation(value = "Expenses Breakdown", notes = "Returns expenses breakdown by category")
	public Response<ExpensesBreakdownDto> getExpensesBreakdown(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String clinicId) {
		
		ExpensesBreakdownDto breakdown = dashboardKPIService.getExpensesBreakdown(startDate, endDate, clinicId);
		Response<ExpensesBreakdownDto> response = new Response<>();
		response.setData(breakdown);
		return response;
	}
}