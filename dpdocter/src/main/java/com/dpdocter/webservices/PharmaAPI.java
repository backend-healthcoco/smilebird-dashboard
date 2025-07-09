package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.PharmaLicense;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.EditPharmaCompanyRequest;
import com.dpdocter.response.PharmaCompanyResponse;
import com.dpdocter.response.PharmaLicenseResponse;
import com.dpdocter.services.PharmaService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PHARMA_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PHARMA_URL, description = "Endpoint for Pharma")
public class PharmaAPI {

    private static Logger logger = LogManager.getLogger(PharmaAPI.class.getName());
    
    @Autowired
    private PharmaService pharmaService;
	
	@PostMapping(value = PathProxy.PharmaUrls.ADD_EDIT_LICENSE)
	@ApiOperation(value = PathProxy.PharmaUrls.ADD_EDIT_LICENSE, notes = PathProxy.PharmaUrls.ADD_EDIT_LICENSE)
	public Response<PharmaLicenseResponse> addEditLicense(@RequestBody PharmaLicense request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<PharmaLicenseResponse> response = new Response<PharmaLicenseResponse>();
		response.setData(pharmaService.addeditPharmaLicense(request));
		return response;
	}
	

	@GetMapping(value = PathProxy.PharmaUrls.GET_LICENSES)
	@ApiOperation(value = PathProxy.PharmaUrls.GET_LICENSES, notes = PathProxy.PharmaUrls.GET_LICENSES)
	public Response<PharmaLicenseResponse> getPharmaLicenses(@RequestParam(required = false, value ="companyId") String companyId , @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<PharmaLicenseResponse> response = new Response<PharmaLicenseResponse>();
		response.setDataList(pharmaService.getLicenses(companyId, page, size));
		return response;
	}
	

	@DeleteMapping(value = PathProxy.PharmaUrls.DISCARD_LICENSE)
	@ApiOperation(value = PathProxy.PharmaUrls.DISCARD_LICENSE, notes = PathProxy.PharmaUrls.DISCARD_LICENSE)
	public Response<PharmaLicenseResponse> discardService(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded") boolean discarded) {
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<PharmaLicenseResponse> response = new Response<PharmaLicenseResponse>();
		response.setData(pharmaService.discardPharmaLicense(id, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.PharmaUrls.GET_COMPANY_LIST)
	@ApiOperation(value = PathProxy.PharmaUrls.GET_COMPANY_LIST, notes = PathProxy.PharmaUrls.GET_COMPANY_LIST)
	public Response<PharmaCompanyResponse> getPharmaCompanies(@RequestParam(required = false, value ="searchTerm") String searchTerm, @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setDataList(pharmaService.getPharmaCompanyList(searchTerm,page, size));
		return response;
	}
	
	@GetMapping(value = PathProxy.PharmaUrls.ACTIVATE_COMPANY)
	@ApiOperation(value = PathProxy.PharmaUrls.ACTIVATE_COMPANY, notes = PathProxy.PharmaUrls.ACTIVATE_COMPANY)
	public Response<PharmaCompanyResponse> activateCompany(@PathVariable("id") String id ,
			@RequestParam(required = false, value ="activated", defaultValue = "true") Boolean activated  ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setData(pharmaService.activatePharmaCompany(id, activated));
		return response;
	
	}
	
	
	@GetMapping(value = PathProxy.PharmaUrls.VERIFY_COMPANY)
	@ApiOperation(value = PathProxy.PharmaUrls.VERIFY_COMPANY, notes = PathProxy.PharmaUrls.VERIFY_COMPANY)
	public Response<PharmaCompanyResponse> verifyCompany(@PathVariable("id") String id ,
			@RequestParam(required = false, value ="verified", defaultValue = "true") Boolean verified  ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setData(pharmaService.verifyPharmaCompany(id, verified));
		return response;
	}
	
	@PostMapping(value = PathProxy.PharmaUrls.EDIT_COMPANY_DETAILS)
	@ApiOperation(value = PathProxy.PharmaUrls.EDIT_COMPANY_DETAILS, notes = PathProxy.PharmaUrls.EDIT_COMPANY_DETAILS)
	public Response<PharmaCompanyResponse> editCompanyDetails(@RequestBody EditPharmaCompanyRequest companyRequest ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setData(pharmaService.editPharmaCompanyDetails(companyRequest));
		return response;
	}
	
	@GetMapping(value = PathProxy.PharmaUrls.GET_COMPANY_DETAILS)
	@ApiOperation(value = PathProxy.PharmaUrls.GET_COMPANY_DETAILS, notes = PathProxy.PharmaUrls.GET_COMPANY_DETAILS)
	public Response<PharmaCompanyResponse> verifyCompany(@PathVariable("id") String id  ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setData(pharmaService.getCompanyDetails(id));
		return response;
	}
	
	@GetMapping(value = PathProxy.PharmaUrls.GET_DRUG_COMPANY_LIST)
	@ApiOperation(value = PathProxy.PharmaUrls.GET_DRUG_COMPANY_LIST, notes = PathProxy.PharmaUrls.GET_DRUG_COMPANY_LIST)
	public Response<PharmaCompanyResponse> getDrugCompanies(@RequestParam(required = false, value ="searchTerm") String searchTerm, @RequestParam(required = false, value ="page", defaultValue = "0") int page , @RequestParam(required = false, value ="size", defaultValue = "0") int size  ) {
		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setDataList(pharmaService.getDrugCompanyList(searchTerm, page, size));
		return response;
	}
}
