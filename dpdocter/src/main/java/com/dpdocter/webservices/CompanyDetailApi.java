package com.dpdocter.webservices;

import javax.ws.rs.QueryParam;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CompanyDetail;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.RegisteredUser;
import com.dpdocter.beans.SafePlaceMobileNumber;
import com.dpdocter.beans.Yoga;
import com.dpdocter.collections.CompanyDetailCollection;
import com.dpdocter.collections.SafePlaceMobileNumberCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.CompanyDetailService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//for covid project

@RestController
@Api(value = PathProxy.COMPANY_DETAILS, description = "Endpoint for Country")
@RequestMapping(value = PathProxy.COMPANY_DETAILS, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class CompanyDetailApi {

	private static Logger logger = LogManager.getLogger(CountryApi.class.getName());

	@Autowired
	private CompanyDetailService companyDetailService;

	@PostMapping(value = PathProxy.CompanyDetailsUrl.ADD_EDIT_COMPANY_DETAILS)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.ADD_EDIT_COMPANY_DETAILS, notes = PathProxy.CompanyDetailsUrl.ADD_EDIT_COMPANY_DETAILS)
	public Response<CompanyDetail> addEditCompanyDetail(@RequestBody CompanyDetail request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CompanyDetail> response = new Response<CompanyDetail>();
		response.setData(companyDetailService.addEditCompanyDetail(request));
		return response;
	}

	@GetMapping(value = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAILS)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAILS, notes = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAILS)
	public Response<CompanyDetail> getCompanyDetail(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
		Integer count = companyDetailService.countCompanyDetail(isDiscarded, searchTerm);
		Response<CompanyDetail> response = new Response<CompanyDetail>();
		if (count > 0)
			response.setDataList(companyDetailService.getCompanyDetail(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.CompanyDetailsUrl.DELETE_COMPANY_DETAILS)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.DELETE_COMPANY_DETAILS, notes = PathProxy.CompanyDetailsUrl.DELETE_COMPANY_DETAILS)
	public Response<Boolean> deleteCompanyDetail(@PathVariable("companyId") String companyId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(companyId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(companyDetailService.deleteCompanyDetail(companyId, isDiscarded));
		return response;
	}
	
//	@PostMapping(value = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE)
//	@ApiOperation(value = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE, notes = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE)
//	@ResponseBody
//	public Response<ImageURLResponse> uploadImages(@RequestBody FileDetails request,
//			@RequestParam(value = "module", defaultValue = "logo") String module) {
//		if (request == null) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		if (DPDoctorUtils.allStringsEmpty(request.getFileEncoded(), request.getFileName(),
//				request.getFileExtension())) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput,
//					"FileEncoded,FileName and File Extension should not null or Empty");
//		}
//
//		ImageURLResponse image = companyDetailService.uploadImages(request, module);
//		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
//		response.setData(image);
//		return response;
//	}
//	
	@PostMapping(value = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE , consumes = { MediaType.MULTIPART_FORM_DATA})
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE, notes = PathProxy.CompanyDetailsUrl.UPLOAD_IMAGE)
	public Response<ImageURLResponse> saveImage(@RequestParam(value = "file") MultipartFile file) {
		
		ImageURLResponse image = companyDetailService.uploadImageMultipart(file);
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(image);
		return response;
	}
	
	
	
	@GetMapping(value = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAIL_BY_ID)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAIL_BY_ID, notes = PathProxy.CompanyDetailsUrl.GET_COMPANY_DETAIL_BY_ID)
	public Response<CompanyDetail> getCountryById(@PathVariable("companyId") String companyId) {
		if (companyId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		CompanyDetail companyDetail = companyDetailService.getCompanyDetailById(companyId);
		Response<CompanyDetail> response = new Response<CompanyDetail>();
		response.setData(companyDetail);
		return response;

	}
	@GetMapping(value = PathProxy.CompanyDetailsUrl.ACTIVATE_COMPANY_USER)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.ACTIVATE_COMPANY_USER, notes = PathProxy.CompanyDetailsUrl.ACTIVATE_COMPANY_USER)
	public Response<Boolean> activateConferenceAdmin(@PathVariable("companyId") String companyId,
			@RequestParam(required = true, value = "isActive", defaultValue = "false")Boolean isActive) {
		if (DPDoctorUtils.anyStringEmpty(companyId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean res = companyDetailService.activeAdmin(companyId,isActive);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(res);
		return response;
	}
	
	@GetMapping(value = PathProxy.CompanyDetailsUrl.GET_MOBILE_NUMBER)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.GET_MOBILE_NUMBER, notes = PathProxy.CompanyDetailsUrl.GET_MOBILE_NUMBER)
	public Response<SafePlaceMobileNumber> getMobileNumbers(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "searchTerm", defaultValue = "") String searchTerm) {
	
		Integer count = companyDetailService.getMobileNumbersCount(isDiscarded, searchTerm);
		Response<SafePlaceMobileNumber> response = new Response<SafePlaceMobileNumber>();
		if (count > 0)
			response.setDataList(companyDetailService.getMobileNumbers(size, page, isDiscarded, searchTerm));
		response.setCount(count);
		return response;
	}
	
	@PostMapping(value = PathProxy.CompanyDetailsUrl.ADD_EDIT_MOBILE_NUMBER)
	@ApiOperation(value = PathProxy.CompanyDetailsUrl.ADD_EDIT_MOBILE_NUMBER, notes = PathProxy.CompanyDetailsUrl.ADD_EDIT_MOBILE_NUMBER)
	public Response<SafePlaceMobileNumber> addEditMobileNumberReason(@RequestBody SafePlaceMobileNumber request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<SafePlaceMobileNumber> response = new Response<SafePlaceMobileNumber>();
		response.setData(companyDetailService.addEditMobileNumberReason(request));
		return response;
	}
}
