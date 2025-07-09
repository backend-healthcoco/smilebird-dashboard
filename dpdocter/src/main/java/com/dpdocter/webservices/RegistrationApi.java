package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.ClinicTreatmentRatelist;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.Role;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author veeraj
 */
@RestController
@RequestMapping(value=PathProxy.REGISTRATION_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.REGISTRATION_BASE_URL, description = "Endpoint for register")
public class RegistrationApi {

	private static Logger logger = LogManager.getLogger(RegistrationApi.class.getName());

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Context
	private UriInfo uriInfo;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.first.name.validation}")
	private String firstNameValidaton;

	@Value(value = "${register.mobile.number.validation}")
	private String mobileNumberValidaton;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@PostMapping(value = PathProxy.RegistrationUrls.ADD_REFERRENCE)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_REFERRENCE, notes = PathProxy.RegistrationUrls.ADD_REFERRENCE, response = Response.class)
	public Response<Reference> addReference(@RequestBody Reference request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getReference(), request.getDoctorId(),
				request.getLocationId(), request.getHospitalId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Reference reference = registrationService.addEditReference(request);
		transnationalService.addResource(new ObjectId(reference.getId()), Resource.REFERENCE, false);
		ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
		BeanUtil.map(reference, esReferenceDocument);
		esRegistrationService.addEditReference(esReferenceDocument);
		Response<Reference> response = new Response<Reference>();
		response.setData(reference);
		return response;
	}

	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_REFERRENCE, notes = PathProxy.RegistrationUrls.DELETE_REFERRENCE, response = Response.class)
	public Response<Reference> deleteReferrence(@PathVariable("referrenceId") String referrenceId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (referrenceId == null) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Reference reference = registrationService.deleteReferrence(referrenceId, discarded);
		transnationalService.addResource(new ObjectId(reference.getId()), Resource.REFERENCE, false);
		ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
		BeanUtil.map(reference, esReferenceDocument);
		esRegistrationService.addEditReference(esReferenceDocument);
		Response<Reference> response = new Response<Reference>();
		response.setData(reference);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.GET_REFERRENCES)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_REFERRENCES, notes = PathProxy.RegistrationUrls.GET_REFERRENCES, response = Response.class)
	public Response<ReferenceDetail> getReferences(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {

		List<ReferenceDetail> references = registrationService.getReferences(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded);
		Response<ReferenceDetail> response = new Response<ReferenceDetail>();
		response.setDataList(references);
		return response;
	}

	// @Path(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR)
	// @GET
	// @ApiOperation(value = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR,
	// notes = PathProxy.RegistrationUrls.PATIENT_ID_GENERATOR, response =
	// Response.class)
	// public Response<String> patientIDGenerator(@PathVariable("doctorId") String
	// doctorId, @PathVariable("locationId") String locationId,
	// @PathVariable("hospitalId") String hospitalId) {
	//
	// if (doctorId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.doctorId is null");
	// }
	// if (locationId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.locationId is null");
	// }
	// if (hospitalId == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid
	// Input.hospitalId is null");
	// }
	//
	// Response<String> response = new Response<String>();
	// String generatedId = registrationService.patientIdGenerator(doctorId,
	// locationId, hospitalId);
	// response.setData(generatedId);
	// return response;
	// }

	@GetMapping(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, notes = PathProxy.RegistrationUrls.GET_CLINIC_DETAILS, response = Response.class)
	public Response<Location> getClinicDetails(@PathVariable("clinicId") String clinicId) {
		if (DPDoctorUtils.anyStringEmpty(clinicId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Location clinicDetails = registrationService.getClinicDetails(clinicId);
		if (clinicDetails != null) {
			if (clinicDetails.getImages() != null && !clinicDetails.getImages().isEmpty()) {
				for (ClinicImage clinicImage : clinicDetails.getImages()) {
					if (clinicImage.getImageUrl() != null) {
						clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
					}
					if (clinicImage.getThumbnailUrl() != null) {
						clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
					}
				}
			}
			if (clinicDetails.getLogoUrl() != null)
				clinicDetails.setLogoUrl(getFinalImageURL(clinicDetails.getLogoUrl()));
			if (clinicDetails.getLogoThumbnailUrl() != null)
				clinicDetails.setLogoThumbnailUrl(getFinalImageURL(clinicDetails.getLogoThumbnailUrl()));
		}
		Response<Location> response = new Response<Location>();
		response.setData(clinicDetails);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE)
	public Response<ClinicProfile> updateClinicProfile(@RequestBody ClinicProfile request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicProfile clinicProfileUpdateResponse = registrationService.updateClinicProfile(request);
		transnationalService.addResource(new ObjectId(clinicProfileUpdateResponse.getId()), Resource.LOCATION, false);

		if (clinicProfileUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicProfile> response = new Response<ClinicProfile>();
		response.setData(clinicProfileUpdateResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_PROFILE_HANDHELD)
	public Response<ClinicProfile> updateClinicProfile(@RequestBody ClinicProfileHandheld request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicProfile clinicProfileUpdateResponse = registrationService.updateClinicProfileHandheld(request);
		transnationalService.addResource(new ObjectId(clinicProfileUpdateResponse.getId()), Resource.LOCATION, false);
		if (clinicProfileUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicProfile> response = new Response<ClinicProfile>();
		response.setData(clinicProfileUpdateResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_ADDRESS)
	public Response<ClinicAddress> updateClinicAddress(@RequestBody ClinicAddress request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicAddress clinicAddressUpdateResponse = registrationService.updateClinicAddress(request);
		transnationalService.addResource(new ObjectId(clinicAddressUpdateResponse.getId()), Resource.LOCATION, false);
		if (clinicAddressUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicAddress> response = new Response<ClinicAddress>();
		response.setData(clinicAddressUpdateResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_TIMING)
	public Response<ClinicTiming> updateClinicTiming(@RequestBody ClinicTiming request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicTiming clinicTimingUpdateResponse = registrationService.updateClinicTiming(request);
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
		if (clinicTimingUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicTiming> response = new Response<ClinicTiming>();
		response.setData(clinicTimingUpdateResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TREATMENT_RATELIST)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_TREATMENT_RATELIST, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_TREATMENT_RATELIST)
	public Response<ClinicTreatmentRatelist> updateClinicTreatmentRatelist(@RequestBody ClinicTreatmentRatelist request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicTreatmentRatelist clinicTimingUpdateResponse = registrationService.updateClinicTreatmentRatelist(request);
		Response<ClinicTreatmentRatelist> response = new Response<ClinicTreatmentRatelist>();
		response.setData(clinicTimingUpdateResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_SPECIALIZATION)
	public Response<ClinicSpecialization> updateClinicSpecialization(@RequestBody ClinicSpecialization request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicSpecialization clinicSpecializationUpdateResponse = registrationService
				.updateClinicSpecialization(request);
		transnationalService.addResource(new ObjectId(clinicSpecializationUpdateResponse.getId()), Resource.LOCATION,
				false);
		if (clinicSpecializationUpdateResponse != null)
			transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicSpecialization> response = new Response<ClinicSpecialization>();
		response.setData(clinicSpecializationUpdateResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES)
	@ApiOperation(value = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES, notes = PathProxy.RegistrationUrls.UPDATE_CLINIC_LAB_PROPERTIES)
	public Response<ClinicLabProperties> updateLabProperties(@RequestBody ClinicLabProperties request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicLabProperties clinicLabProperties = registrationService.updateLabProperties(request);
		if (clinicLabProperties != null) {
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
			transnationalService.checkLocation(new ObjectId(request.getId()));
		}
		Response<ClinicLabProperties> response = new Response<ClinicLabProperties>();
		response.setData(clinicLabProperties);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.CHANGE_PHARMA_LOGO)
	@ApiOperation(value = PathProxy.RegistrationUrls.CHANGE_PHARMA_LOGO, notes = PathProxy.RegistrationUrls.CHANGE_PHARMA_LOGO)
	public Response<ImageURLResponse> changePharmaLogo(@RequestBody FileDetails request) {
		if (request == null ) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ImageURLResponse clinicLogoResponse = registrationService.changePharmaLogo(request);
		if (clinicLogoResponse != null) {
			if (clinicLogoResponse.getImageUrl() != null) {
				clinicLogoResponse.setImageUrl(getFinalImageURL(clinicLogoResponse.getImageUrl()));
			}
			if (clinicLogoResponse.getThumbnailUrl() != null) {
				clinicLogoResponse.setThumbnailUrl(getFinalImageURL(clinicLogoResponse.getThumbnailUrl()));
			}
		}
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(clinicLogoResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
	@ApiOperation(value = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO, notes = PathProxy.RegistrationUrls.CHANGE_CLINIC_LOGO)
	public Response<ClinicLogo> changeClinicLogo(@RequestBody ClinicLogoAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		ClinicLogo clinicLogoResponse = registrationService.changeClinicLogo(request);
		if (clinicLogoResponse != null) {
			if (clinicLogoResponse.getLogoURL() != null) {
				clinicLogoResponse.setLogoURL(getFinalImageURL(clinicLogoResponse.getLogoURL()));
			}
			if (clinicLogoResponse.getLogoThumbnailURL() != null) {
				clinicLogoResponse.setLogoThumbnailURL(getFinalImageURL(clinicLogoResponse.getLogoThumbnailURL()));
			}
		}
		transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
		transnationalService.checkLocation(new ObjectId(request.getId()));
		Response<ClinicLogo> response = new Response<ClinicLogo>();
		response.setData(clinicLogoResponse);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE, notes = PathProxy.RegistrationUrls.ADD_CLINIC_IMAGE)
	public Response<ClinicImage> addClinicImage(@RequestBody ClinicImageAddRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getId())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (request.getImages() == null) {
			logger.warn("Invalid Input. Request Image Is Null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Request Image Is Null");
		} else if (request.getImages().size() > 5) {
			logger.warn("More than 5 images cannot be uploaded at a time");
			throw new BusinessException(ServiceError.NotAcceptable, "More than 5 images cannot be uploaded at a time");
		}
		List<ClinicImage> clinicImageResponse = registrationService.addClinicImage(request);
		if (clinicImageResponse != null && !clinicImageResponse.isEmpty()) {
			for (ClinicImage clinicalImage : clinicImageResponse) {
				if (clinicalImage.getImageUrl() != null) {
					clinicalImage.setImageUrl(getFinalImageURL(clinicalImage.getImageUrl()));
				}
				if (clinicalImage.getThumbnailUrl() != null) {
					clinicalImage.setThumbnailUrl(getFinalImageURL(clinicalImage.getThumbnailUrl()));
				}
			}
			transnationalService.addResource(new ObjectId(request.getId()), Resource.LOCATION, false);
			transnationalService.checkLocation(new ObjectId(request.getId()));
		}
		Response<ClinicImage> response = new Response<ClinicImage>();
		response.setDataList(clinicImageResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE, notes = PathProxy.RegistrationUrls.DELETE_CLINIC_IMAGE)
	public Response<Boolean> deleteClinicImage(@PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "counter") int counter) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			logger.warn("Invalid Input. Location Id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Location Id is null");
		} else if (counter == 0) {
			logger.warn("Invalid Input. Counter cannot be 0");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Counter cannot be 0");
		}

		Boolean deleteImage = registrationService.deleteClinicImage(locationId, counter);
		transnationalService.addResource(new ObjectId(locationId), Resource.LOCATION, false);
		transnationalService.checkLocation(new ObjectId(locationId));
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteImage);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_BLOOD_GROUP, notes = PathProxy.RegistrationUrls.GET_BLOOD_GROUP)
	public Response<BloodGroup> getBloodGroup() {
		List<BloodGroup> bloodGroupResponse = registrationService.getBloodGroup();
		Response<BloodGroup> response = new Response<BloodGroup>();
		response.setDataList(bloodGroupResponse);
		return response;
	}

	// @Path(value = PathProxy.RegistrationUrls.ADD_PROFESSION)
	// @POST
	// @ApiOperation(value = PathProxy.RegistrationUrls.ADD_PROFESSION, notes =
	// PathProxy.RegistrationUrls.ADD_PROFESSION)
	// public Response<Profession> addProfession(Profession request) {
	// if (request == null) {
	// throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.
	// Request Sent Is Empty");
	// }
	// Profession professionResponse =
	// registrationService.addProfession(request);
	// Response<Profession> response = new Response<Profession>();
	// response.setData(professionResponse);
	// return response;
	// }

	@GetMapping(value = PathProxy.RegistrationUrls.GET_PROFESSION)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PROFESSION, notes = PathProxy.RegistrationUrls.GET_PROFESSION)
	public Response<Profession> getProfession(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime) {

		List<Profession> professionResponse = registrationService.getProfession(page, size, updatedTime);
		Response<Profession> response = new Response<Profession>();
		response.setDataList(professionResponse);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.ADD_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_ROLE, notes = PathProxy.RegistrationUrls.ADD_ROLE)
	public Response<Role> addRole(@RequestBody Role request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getRole())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role professionResponse = registrationService.addRole(request);
		Response<Role> response = new Response<Role>();
		response.setData(professionResponse);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.GET_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_ROLE, notes = PathProxy.RegistrationUrls.GET_ROLE)
	public Response<Role> getRole(@PathVariable(value = "range") String range, @RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size, @RequestParam(required = false, value = "locationId") String locationId,
			@RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime) {
		if (DPDoctorUtils.anyStringEmpty(range)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		List<Role> professionResponse = registrationService.getRole(range, page, size, locationId, hospitalId,
				updatedTime);
		Response<Role> response = new Response<Role>();
		response.setDataList(professionResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.RegistrationUrls.DELETE_ROLE)
	@ApiOperation(value = PathProxy.RegistrationUrls.DELETE_ROLE, notes = PathProxy.RegistrationUrls.DELETE_ROLE)
	public Response<Role> deleteRole(@PathVariable(value = "roleId") String roleId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(roleId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Role role = registrationService.deleteRole(roleId, discarded);
		Response<Role> response = new Response<Role>();
		response.setData(role);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.ADD_FEEDBACK, notes = PathProxy.RegistrationUrls.ADD_FEEDBACK)
	public Response<Feedback> addFeedback(@RequestBody Feedback request) {
		if (request == null) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Feedback feedback = registrationService.addFeedback(request);
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), new ObjectId(feedback.getLocationId()));
		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK, notes = PathProxy.RegistrationUrls.VISIBLE_FEEDBACK)
	public Response<Feedback> visibleFeedback(@PathVariable("feedbackId") String feedbackId,
			@RequestParam(required = false, value ="isVisible", defaultValue = "true") Boolean isVisible) {

		Feedback feedback = registrationService.visibleFeedback(feedbackId, isVisible);
		if (!DPDoctorUtils.anyStringEmpty(feedback.getDoctorId()))
			transnationalService.checkDoctor(new ObjectId(feedback.getDoctorId()),
					new ObjectId(feedback.getLocationId()));
		else
			transnationalService.checkLocation(new ObjectId(feedback.getDoctorId()));
		Response<Feedback> response = new Response<Feedback>();
		response.setData(feedback);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_PATIENT_STATUS, notes = PathProxy.RegistrationUrls.GET_PATIENT_STATUS)
	public Response<PatientStatusResponse> getPatientStatus(@PathVariable("patientId") String patientId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId) {

		if (DPDoctorUtils.anyStringEmpty(patientId, doctorId, locationId, hospitalId)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		Response<PatientStatusResponse> response = new Response<PatientStatusResponse>();

		PatientStatusResponse patientStatusResponse = registrationService.getPatientStatus(patientId, doctorId,
				locationId, hospitalId);
		response.setData(patientStatusResponse);
		return response;
	}

	@GetMapping(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	@ApiOperation(value = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK, notes = PathProxy.RegistrationUrls.GET_DOCTOR_FEEDBACK)
	public Response<Feedback> getFeedback(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="doctorId") String doctorId, @RequestParam(required = false, value ="locationId") String locationId,
			@RequestParam(required = false, value ="hospitalId") String hospitalId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value = "type") String type) {

		List<Feedback> feedbacks = registrationService.getFeedback(page, size, doctorId, locationId, hospitalId,
				updatedTime, type);
		Response<Feedback> response = new Response<Feedback>();
		response.setDataList(feedbacks);
		return response;
	}

	@PostMapping(value = PathProxy.RegistrationUrls.CLINIC_REGISTER)
	@ApiOperation(value = PathProxy.RegistrationUrls.CLINIC_REGISTER, notes = PathProxy.RegistrationUrls.CLINIC_REGISTER, response = Response.class)
	public Response<Location> clinicRegister(@RequestBody ClinicContactUs request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		} else if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())
				&& DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn(mobileNumberValidaton);
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId and  emailAddress  cannot be Null or empty");
		} else if (request.getLocationName().length() < 2) {
			logger.warn("LcationNameValidaton");
			throw new BusinessException(ServiceError.InvalidInput, "LcationNameValidaton");
		}
		Location locationresponse = registrationService.registerClinic(request);
		Response<Location> response = new Response<Location>();
		response.setData(locationresponse);
		return response;
	}
}
