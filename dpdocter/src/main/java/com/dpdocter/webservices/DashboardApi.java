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
	 * 13. Get Clinic API (Dummy)
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.CLINICS)
	@ApiOperation(value = PathProxy.DashboardApiUrls.CLINICS, notes = "Returns dummy list of clinics with financials and doctors")
	public Response<List<Map<String, Object>>> getClinics(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,@RequestParam(required = false) String zone,
			@RequestParam(required = false) String city, @RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) String page, @RequestParam(required = false) String size) {

		List<Map<String, Object>> clinics = new ArrayList<>();

		Map<String, Object> clinic1 = new HashMap<>();
		clinic1.put("clinicId", "65e91c3f84e543d234abc123");
		clinic1.put("clinicName", "Smilebird Worli");
		clinic1.put("city", "Mumbai");
		clinic1.put("revenue", 1200000);
		clinic1.put("expenses", 800000);
		clinic1.put("profit", 400000);
		clinic1.put("profitPercentage", 33.33);
		clinic1.put("specialty", "Dental");
		clinic1.put("doctotName", "Dr.Tushita Singh");
		clinic1.put("doctorId", "65e91c3f84e543d234abc124");

		clinics.add(clinic1);

		Response<List<Map<String, Object>>> response = new Response<>();
		response.setData(clinics);
		return response;
	}

	/**
	 * View Clinic by clinicId (Dummy)
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.VIEW_CLINIC_BY_ID)
	@ApiOperation(value = "View clinic by clinicId", notes = "Returns clinic details and KPIs by clinicId (dummy response)")
	public Response<ViewClinicResponse> viewClinicById(@PathVariable("clinicId") String clinicId) {
		ViewClinicResponse clinicDetails = new ViewClinicResponse();
		clinicDetails.setClinicId(clinicId);
		clinicDetails.setRevenue(1200000);
		clinicDetails.setNetIncome(400000);
		clinicDetails.setTotalPatient(1500);
		clinicDetails.setFootfall(1800);
		clinicDetails.setNewPatientConversion(65.5);
		clinicDetails.setNewPatients(500);
		clinicDetails.setReturning(1000);
		clinicDetails.setDoctorInCharge("Dr. Tushita Singh");
		clinicDetails.setOperatories(5);
		clinicDetails.setBreakevenStatus("Achieved");
		clinicDetails.setAverageRating(4.7);
		clinicDetails.setTreatmentCompletion(92.3);

		Response<ViewClinicResponse> response = new Response<>();
		response.setData(clinicDetails);
		return response;
	}

	/**
	 * Monthly Summary API (Dummy)
	 */
	@GetMapping(value = PathProxy.DashboardApiUrls.MONTHLY_SUMMARY)
	@ApiOperation(value = "Monthly Summary", notes = "Returns monthly summary for a clinic")
	public Response<DashboardSummaryDto> getMonthlySummary(
			@RequestParam String clinicId,
			@RequestParam int month,
			@RequestParam int year) {

		DashboardSummaryDto summary = dashboardKPIService.getMonthlySummary(clinicId, month, year);
		Response<DashboardSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.PERFORMANCE_METRICS)
	@ApiOperation(value = "Performance Metrics", notes = "Returns performance metrics for a clinic by year")
	public Response<List<PerformanceMetricsDto>> getPerformanceMetrics(
			@RequestParam String clinicId,
			@RequestParam int year) {
		List<PerformanceMetricsDto> metrics = dashboardKPIService.getPerformanceMetrics(clinicId, year);
		Response<List<PerformanceMetricsDto>> response = new Response<>();
		response.setData(metrics);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.COMPANY_FINANCIALS)
	@ApiOperation(value = "Company Financials", notes = "Returns company-level financial summary for a given period")
	public Response<FinancialSummaryDto> getCompanyFinancials(@RequestParam String period) {
		FinancialSummaryDto summary = dashboardKPIService.getCompanyFinancials(period);
		Response<FinancialSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.CLINIC_FINANCIALS)
	@ApiOperation(value = "Clinic Financials", notes = "Returns clinic-level financial summary for a given period")
	public Response<FinancialSummaryDto> getClinicFinancials(@RequestParam String clinicId, @RequestParam String period) {
		FinancialSummaryDto summary = dashboardKPIService.getClinicFinancials(clinicId, period);
		Response<FinancialSummaryDto> response = new Response<>();
		response.setData(summary);
		return response;
	}

	@GetMapping(value = PathProxy.DashboardApiUrls.CLINIC_PERFORMANCE_COMPARISON)
	@ApiOperation(value = "Clinic Performance Comparison", notes = "Returns performance comparison for clinics for a given period")
	public Response<List<ClinicPerformanceDto>> getClinicPerformanceComparison(@RequestParam String period) {
		List<ClinicPerformanceDto> list = dashboardKPIService.getClinicPerformanceComparison(period);
		Response<List<ClinicPerformanceDto>> response = new Response<>();
		response.setData(list);
		return response;
	}
}