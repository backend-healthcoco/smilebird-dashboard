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
@RequestMapping(value=PathProxy.SIGNUP_BASE_URL, produces = {MediaType.APPLICATION_JSON} ,  consumes = MediaType.APPLICATION_JSON)
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
		/*
		 * F if (user != null) { if (user.getImageUrl() != null) {
		 * user.setImageUrl(getFinalImageURL(user.getImageUrl())); } if
		 * (user.getThumbnailUrl() != null) {
		 * user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl())); } }
		 */

		Response<User> response = new Response<User>();
		response.setData(user);
		return response;
	}

	@PostMapping(value = PathProxy.SignUpUrls.LOCALE_SIGNUP)
	@ApiOperation(value = PathProxy.SignUpUrls.LOCALE_SIGNUP, notes = PathProxy.SignUpUrls.LOCALE_SIGNUP)
	public Response<Locale> signupLocale(@RequestBody Locale request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}

		Locale locale = signUpService.signupLocale(request);

		Response<Locale> response = new Response<Locale>();
		response.setData(locale);
		return response;
	}

	@PostMapping(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	@ApiOperation(value = PathProxy.SignUpUrls.DOCTOR_SIGNUP, notes = PathProxy.SignUpUrls.DOCTOR_SIGNUP)
	public Response<DoctorSignUp> doctorSignup(@RequestBody DoctorSignupRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getFirstName(), request.getEmailAddress(),
				request.getMobileNumber(), request.getCity())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (request.getFirstName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		DoctorSignUp doctorSignUp = signUpService.doctorSignUp(request);
		if (doctorSignUp != null) {
			if (doctorSignUp.getUser() != null) {
				if (doctorSignUp.getUser().getImageUrl() != null) {
					doctorSignUp.getUser().setImageUrl(getFinalImageURL(doctorSignUp.getUser().getImageUrl()));
				}
				if (doctorSignUp.getUser().getThumbnailUrl() != null) {
					doctorSignUp.getUser().setThumbnailUrl(getFinalImageURL(doctorSignUp.getUser().getThumbnailUrl()));
				}
			}
			if (doctorSignUp.getHospital() != null) {
				if (doctorSignUp.getHospital().getHospitalImageUrl() != null) {
					doctorSignUp.getHospital()
							.setHospitalImageUrl(getFinalImageURL(doctorSignUp.getHospital().getHospitalImageUrl()));
				}
			}
			transnationalService.checkDoctor(new ObjectId(doctorSignUp.getUser().getId()), null);

		}

		Response<DoctorSignUp> response = new Response<DoctorSignUp>();
		response.setData(doctorSignUp);
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.ACTIVATE_USER)
	@ApiOperation(value = PathProxy.SignUpUrls.ACTIVATE_USER, notes = PathProxy.SignUpUrls.ACTIVATE_USER)
	public Response<Boolean> activateUser(@PathVariable("userId") String userId,
			@RequestParam(required = false, value ="activate", defaultValue = "true") Boolean activate) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input. User Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. User Id Cannot Be Empty");
		}
		Boolean verifyUserResponse = signUpService.activateUser(userId, activate);
		esRegistrationService.activateUser(userId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyUserResponse);
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.ACTIVATE_LOCATION)
	@ApiOperation(value = PathProxy.SignUpUrls.ACTIVATE_LOCATION, notes = PathProxy.SignUpUrls.ACTIVATE_LOCATION)
	public Response<Boolean> activateLocation(@PathVariable("locationId") String locationId,
			@RequestParam(required = false, value ="activate", defaultValue = "true") Boolean activate) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input. Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Location Id Cannot Be Empty");
		}
		Boolean verifyUserResponse = signUpService.activateLocation(locationId, activate);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(verifyUserResponse);
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_USERNAME_EXIST)
	public Response<Boolean> checkUsernameExist(@PathVariable(value = "username") String username) {
		if (username == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkUserNameExist(username));
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST)
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_MOBNUM_EXIST)
	public Response<Boolean> checkMobileNumExist(@PathVariable(value = "mobileNumber") String mobileNumber) {
		if (mobileNumber == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkMobileNumExist(mobileNumber));
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
	@ApiOperation(value = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST, notes = PathProxy.SignUpUrls.CHECK_IF_EMAIL_ADDR_EXIST)
	public Response<Boolean> checkEmailExist(@PathVariable(value = "emailaddress") String emailaddress) {
		if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.checkEmailAddressExist(emailaddress));
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		return imagePath + imageURL;
	}

	@GetMapping(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
	@ApiOperation(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR, notes = PathProxy.SignUpUrls.RESEND_VERIFICATION_EMAIL_TO_DOCTOR)
	public Response<Boolean> resendVerificationEmail(@PathVariable(value = "emailaddress") String emailaddress) {
		if (DPDoctorUtils.anyStringEmpty(emailaddress)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.resendVerificationEmail(emailaddress));
		return response;
	}

	@GetMapping(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_SMS)
	@ApiOperation(value = PathProxy.SignUpUrls.RESEND_VERIFICATION_SMS, notes = PathProxy.SignUpUrls.RESEND_VERIFICATION_SMS)
	public Response<Boolean> resendVerificationSMS(@PathVariable(value = "mobileNumber") String mobileNumber) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(signUpService.resendVerificationSMS(mobileNumber));
		return response;
	}

	@PostMapping(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
	@ApiOperation(value = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT, notes = PathProxy.SignUpUrls.SUBMIT_DOCTOR_CONTACT)
	public Response<String> submitDoctorContactUsInfo(@RequestBody DoctorContactUs doctorContactUs) {
		if (doctorContactUs == null) {
			logger.warn("Doctor contact data is null");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact data is null");
		} else if (DPDoctorUtils.anyStringEmpty(doctorContactUs.getFirstName(), doctorContactUs.getEmailAddress(),
				doctorContactUs.getTitle(), doctorContactUs.getCity(), doctorContactUs.getMobileNumber())
				|| doctorContactUs.getGender() == null || doctorContactUs.getSpecialities() == null
				|| doctorContactUs.getSpecialities().isEmpty()) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if (doctorContactUs.getFirstName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		Response<String> response = new Response<String>();
		response.setData(doctorContactUsService.submitDoctorContactUSInfo(doctorContactUs));
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

	@GetMapping(value = PathProxy.SignUpUrls.GET_LOCALE_CONTACT_LIST)
	@ApiOperation(value = PathProxy.SignUpUrls.GET_LOCALE_CONTACT_LIST, notes = PathProxy.SignUpUrls.GET_LOCALE_CONTACT_LIST)
	public Response<Object> getLocaleContactList(@RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size, @RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="contactState") String contactState) {
		List<LocaleContactUs> doctorContactUsList = localeContactUsService.getLocaleContactList(page, size, searchTerm,
				contactState);
		Response<Object> response = new Response<Object>();
		response.setData(localeContactUsService.countLocaleContactList(searchTerm, contactState));
		response.setDataList(doctorContactUsList);
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

	@PostMapping(value = PathProxy.SignUpUrls.SUBMIT_LOCALE_CANTACT_US)
	@ApiOperation(value = PathProxy.SignUpUrls.SUBMIT_LOCALE_CANTACT_US, notes = "This API is used for submitting contact details of Locale")
	public Response<Boolean> submitContact(@RequestBody LocaleContactUs contactUs) {
		if (contactUs == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request cannot be null");
		} else if (DPDoctorUtils.anyStringEmpty(contactUs.getContactNumber())) {
			throw new BusinessException(ServiceError.InvalidInput, "contactNumber is null or empty");

		}

		Boolean responseMessage = localeContactUsService.submitContact(contactUs);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(responseMessage);

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
	
	@PostMapping(value = PathProxy.SignUpUrls.PHARMA_COMPANY_SIGNUP)
	@ApiOperation(value = PathProxy.SignUpUrls.PHARMA_COMPANY_SIGNUP, notes = PathProxy.SignUpUrls.PHARMA_COMPANY_SIGNUP)
	public Response<PharmaCompanyResponse> signupPharmaCompany(@RequestBody PharmaCompanySignupRequest request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}

		PharmaCompanyResponse pharmaCompanyResponse = signUpService.signupPharmaCompany(request);

		Response<PharmaCompanyResponse> response = new Response<PharmaCompanyResponse>();
		response.setData(pharmaCompanyResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.SignUpUrls.DELIVERY_BOY_SIGNUP)
	@ApiOperation(value = PathProxy.SignUpUrls.DELIVERY_BOY_SIGNUP, notes = PathProxy.SignUpUrls.DELIVERY_BOY_SIGNUP)
	public Response<CollectionBoyResponse> deliveryBoySignup(@RequestBody CollectionBoy request) {
		if (request == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}

		CollectionBoyResponse collectionBoyResponse = signUpService.signupCollectionBoys(request);

		Response<CollectionBoyResponse> response = new Response<CollectionBoyResponse>();
		response.setData(collectionBoyResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.SignUpUrls.SEND_PHARMA_PASSWORD_LINK)
	@ApiOperation(value = PathProxy.SignUpUrls.SEND_PHARMA_PASSWORD_LINK, notes = PathProxy.SignUpUrls.SEND_PHARMA_PASSWORD_LINK)
	public Response<Boolean> sendSetPasswordLink(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}

		Boolean status = signUpService.sendSetPasswordEmail(id);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
	
	@GetMapping(value = PathProxy.SignUpUrls.SEND_DOCTOR_WELCOME_LINK)
	@ApiOperation(value = PathProxy.SignUpUrls.SEND_DOCTOR_WELCOME_LINK, notes = PathProxy.SignUpUrls.SEND_DOCTOR_WELCOME_LINK)
	public Response<Boolean> sendDoctorWelcomeLink(@PathVariable("id") String id) {
		if (id == null) {
			logger.warn("Request send  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
		}

		Boolean status = signUpService.sendDoctorWelcomeMessage(id);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

}
