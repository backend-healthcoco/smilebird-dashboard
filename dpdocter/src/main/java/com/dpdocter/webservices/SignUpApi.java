package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AdminSignupRequest;
import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocaleContactUs;
import com.dpdocter.beans.User;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.EditAdminRequest;
import com.dpdocter.request.PharmaCompanySignupRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.PharmaCompanyResponse;
import com.dpdocter.services.ClinicContactUsService;
import com.dpdocter.services.DoctorContactUsService;
import com.dpdocter.services.LocaleContactUsService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.SIGNUP_BASE_URL, produces = {
		MediaType.APPLICATION_JSON }, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for signup")
public class SignUpApi {

	private static Logger logger = LogManager.getLogger(SignUpApi.class.getName());

	@Autowired
	private SignUpService signUpService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private DoctorContactUsService doctorContactUsService;

	@Autowired
	private ClinicContactUsService clinicContactUsService;

	@Autowired
	private LocaleContactUsService localeContactUsService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	@PostMapping(value = PathProxy.SignUpUrls.ADMIN_SIGNUP)
	@ApiOperation(value = PathProxy.SignUpUrls.ADMIN_SIGNUP, notes = PathProxy.SignUpUrls.ADMIN_SIGNUP)
	public Response<User> adminSignup(@RequestBody AdminSignupRequest request) {
		if (request == null || (DPDoctorUtils.allStringsEmpty(request.getFirstName(), request.getMobileNumber()))) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Request");
		}
		User user = signUpService.adminSignUp(request);

		Response<User> response = new Response<User>();
		response.setData(user);
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.ACTIVATE_ADMIN)
	@ApiOperation(value = PathProxy.SignUpUrls.ACTIVATE_ADMIN, notes = PathProxy.SignUpUrls.ACTIVATE_ADMIN)
	public Response<Boolean> activateAdmin(@PathVariable("userId") String userId,
			@RequestParam(required = false, value = "isActive") boolean isActive) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Admin Id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Please enter userId");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.activateAdmin(userId, isActive));
		return response;
	}

	@PostMapping(value = PathProxy.SignUpUrls.EDIT_ADMIN_PROFILE)
	@ApiOperation(value = PathProxy.SignUpUrls.EDIT_ADMIN_PROFILE, notes = "This API is used for submitting Edit Admin Profile")
	public Response<Boolean> submitContact(@RequestBody EditAdminRequest request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
		} else if (DPDoctorUtils.anyStringEmpty(request.getId())) {
			throw new BusinessException(ServiceError.InvalidInput, "id is null");

		}

		Boolean responseMessage = signUpService.editAdminProfile(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(responseMessage);

		return response;
	}
	@GetMapping(value = PathProxy.SignUpUrls.GET_DOCTOR_CONTACT_LIST)
	@ApiOperation(value = PathProxy.SignUpUrls.GET_DOCTOR_CONTACT_LIST, notes = PathProxy.SignUpUrls.GET_DOCTOR_CONTACT_LIST)
	public Response<Object> getDoctorContactList(@RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size, @RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="contactState") String contactState) {

		Response<Object> response = new Response<Object>();
		response.setData(doctorContactUsService.countDoctorContactList(searchTerm, contactState));
		response.setDataList(doctorContactUsService.getDoctorContactList(page, size, searchTerm, contactState));
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.GET_CLINIC_CONTACT_LIST)
	@ApiOperation(value = PathProxy.SignUpUrls.GET_CLINIC_CONTACT_LIST, notes = PathProxy.SignUpUrls.GET_CLINIC_CONTACT_LIST)
	public Response<Object> getClinicContactList(@RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size, @RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="contactState") String contactState) {
		List<ClinicContactUs> doctorContactUsList = clinicContactUsService.getClinicContactList(page, size, searchTerm,
				contactState);
		Response<Object> response = new Response<Object>();
		response.setData(clinicContactUsService.countClinicContactList(searchTerm, contactState));
		response.setDataList(doctorContactUsList);
		return response;
	}
}
