package com.dpdocter.webservices.v3;

import java.util.Date;
import java.util.List;

import javax.ws.rs.MatrixParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.PatientRegistrationV3Request;
import com.dpdocter.response.UserAddressResponse;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author parag
 */
@RestController
@RequestMapping(value = PathProxy.REGISTRATION_BASE_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationV3Api {

	private static Logger logger = LogManager.getLogger(RegistrationV3Api.class.getName());

	@Autowired
	private RegistrationV3Service registrationService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	@Value(value = "${register.mobile.number.validation}")
	private String mobileNumberValidaton;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@GetMapping(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM)
	@ApiOperation(value = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, notes = PathProxy.RegistrationUrls.PATIENTS_BY_PHONE_NUM, response = Response.class)
	public Response<Object> getExistingPatients(@PathVariable("mobileNumber") String mobileNumber,
			@RequestParam(required = false, value = "isDentalChainPatient", defaultValue = "false") Boolean isDentalChainPatient,
			@RequestParam(required = false, value = "getAddress", defaultValue = "false") Boolean getAddress,
			@RequestParam(required = false, value = "discardedAddress", defaultValue = "true") Boolean discardedAddress) {
		if (DPDoctorUtils.anyStringEmpty(mobileNumber)) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput, mobileNumberValidaton);
		}
		Response<Object> response = new Response<Object>();

		List<RegisteredPatientDetails> users = registrationService.getPatientsByPhoneNumber(isDentalChainPatient,
				mobileNumber);
		if (users != null && !users.isEmpty()) {
			for (RegisteredPatientDetails user : users) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
			if (getAddress) {
				List<UserAddress> userAddress = registrationService.getUserAddress(null, mobileNumber,
						discardedAddress);
				if (userAddress != null && !userAddress.isEmpty()) {
					UserAddressResponse userAddressResponse = new UserAddressResponse();
					userAddressResponse.setUserAddress(userAddress);
					response.setData(userAddressResponse);
				}
			}
		}
		response.setDataList(users);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.ADD_PATIENTS)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_PATIENTS, notes = PathProxy.RegistrationUrls.ADD_PATIENTS, response = Response.class)
	public Response<RegisteredPatientDetails> patientRegister(@RequestBody PatientRegistrationV3Request request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (request.getLocalPatientName().length() < 2) {
			logger.warn(firstNameValidaton);
			throw new BusinessException(ServiceError.InvalidInput, firstNameValidaton);
		}

		Response<RegisteredPatientDetails> response = new Response<RegisteredPatientDetails>();
		RegisteredPatientDetails registeredPatientDetails = null;

		if (request.getId() == null) {
			if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))
				registrationService.checkPatientCount(request.getMobileNumber());
			registeredPatientDetails = registrationService.registerNewPatient(request);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));

		} else {
			registeredPatientDetails = registrationService.registerExistingPatient(request);
			transnationalService.addResource(new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT,
					false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));
			System.out.println("Result" + esRegistrationService
					.addPatient(registrationService.getESPatientDocument(registeredPatientDetails)));
		}
		// Whatsapp interakt add user
//		registrationService.addUserToInretackt(request);
		registeredPatientDetails.setImageUrl(getFinalImageURL(registeredPatientDetails.getImageUrl()));
		registeredPatientDetails.setThumbnailUrl(getFinalImageURL(registeredPatientDetails.getThumbnailUrl()));
		response.setData(registeredPatientDetails);

		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_DENTAL_CHAIN_PATIENT_CONTACT_STATE)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_DENTAL_CHAIN_PATIENT_CONTACT_STATE, notes = PathProxy.RegistrationUrls.UPDATE_DENTAL_CHAIN_PATIENT_CONTACT_STATE)
	public Response<Boolean> updatePatientContactState(@PathVariable(value = "patientId") String patientId,
			@RequestParam(required = true, value = "contactState") String contactState) {
		Boolean doctorContactUs = registrationService.updatePatientContactState(patientId, contactState);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorContactUs);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_FOLLOW_UP)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_PATIENT_FOLLOW_UP, notes = PathProxy.RegistrationUrls.UPDATE_PATIENT_FOLLOW_UP)
	public Response<Boolean> updatePatientFollowUp(@PathVariable(value = "patientId") String patientId,
			@RequestParam(required = true, value = "followUp") String followUp,
			@RequestParam(required = false, value = "followUpReason") String followUpReason) {
		Boolean doctorContactUs = registrationService.updatePatientFollowUp(patientId, followUp, followUpReason);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorContactUs);
		return response;
	}

}
