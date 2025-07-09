package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DoctorClinicImage;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorDetails;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorOnlineConsultationAddEditRequest;
import com.dpdocter.beans.DoctorOnlineGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetails;
import com.dpdocter.beans.DoctorSlugUrlRequest;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Services;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.TransactionalSmsReport;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;
import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.RegistrationType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditMRCodeRequest;
import com.dpdocter.request.AddEditNutritionistRequest;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditCityRequest;
import com.dpdocter.request.DoctorAddEditFacilityRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorDOBAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorGenderAddEditRequest;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorOnlineWorkingTimeRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;
import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.TransactionSmsServices;
import com.dpdocter.services.TransactionalManagementService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.DOCTOR_PROFILE_URL, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DOCTOR_PROFILE_URL, description = "Endpoint for doctor profile")
public class DoctorProfileApi {

	private static Logger logger = LogManager.getLogger(DoctorProfileApi.class.getName());

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESMasterService esMasterService;
	
	@Autowired
	private TransactionSmsServices transactionSmsServices;
	

	
	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_NAME)
	public Response<Boolean> addEditName(@RequestBody DoctorNameAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getFirstName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditNameResponse = doctorProfileService.addEditName(request);
		if (addEditNameResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditNameResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE)
	public Response<Boolean> addEditExperience(@RequestBody DoctorExperienceAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorExperience experienceResponse = doctorProfileService.addEditExperience(request);
		if (experienceResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONTACT)
	public Response<Boolean> addEditContact(@RequestBody DoctorContactAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditContactResponse = doctorProfileService.addEditContact(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditContactResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EDUCATION)
	public Response<Boolean> addEditEducation(@RequestBody DoctorEducationAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditEducationResponse = doctorProfileService.addEditEducation(request);
		if(addEditEducationResponse)
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditEducationResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
	@GET
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS, notes = PathProxy.DoctorProfileUrls.GET_MEDICAL_COUNCILS)
	public Response<MedicalCouncil> getMedicalCouncils(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value ="searchTerm")  String searchTerm,@RequestParam(required = false, value ="discarded") Boolean discarded) {
		List<MedicalCouncil> medicalCouncils = doctorProfileService.getMedicalCouncils(page, size, updatedTime,searchTerm,discarded);
		Response<MedicalCouncil> response = new Response<MedicalCouncil>();
		response.setDataList(medicalCouncils);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SPECIALITY)
	public Response<Boolean> addEditSpeciality(@RequestBody DoctorSpecialityAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<String> specialityResponse = doctorProfileService.addEditSpeciality(request);
		request.setSpeciality(specialityResponse);
		if (specialityResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ACHIEVEMENT)
	public Response<Boolean> addEditAchievement(@RequestBody DoctorAchievementAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditSpecialityResponse = doctorProfileService.addEditAchievement(request);
		if(addEditSpecialityResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditSpecialityResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_STATEMENT)
	public Response<Boolean> addEditProfessionalStatement(@RequestBody DoctorProfessionalStatementAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditProfessionalStatementResponse = doctorProfileService.addEditProfessionalStatement(request);
		if(addEditProfessionalStatementResponse)
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditProfessionalStatementResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_REGISTRATION_DETAIL)
	public Response<Boolean> addEditRegistrationDetail(@RequestBody DoctorRegistrationAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditRegistrationDetailResponse = doctorProfileService.addEditRegistrationDetail(request);
		if(addEditRegistrationDetailResponse)
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_EXPERIENCE_DETAIL)
	public Response<Boolean> addEditExperienceDetail(@RequestBody DoctorExperienceDetailAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditExperienceDetailResponse = doctorProfileService.addEditExperienceDetail(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditExperienceDetailResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFILE_PICTURE)
	public Response<String> addEditProfilePicture(@RequestBody DoctorProfilePictureAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditProfilePictureResponse = doctorProfileService.addEditProfilePicture(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditProfilePictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditProfilePictureResponse = getFinalImageURL(addEditProfilePictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditProfilePictureResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_COVER_PICTURE)
	public Response<String> addEditCoverPicture(@RequestBody DoctorProfilePictureAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditCoverPictureResponse = doctorProfileService.addEditCoverPicture(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditCoverPictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditCoverPictureResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_PROFESSIONAL_MEMBERSHIP)
	public Response<Boolean> addEditProfessionalMembership(@RequestBody DoctorProfessionalAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditProfessionalMembershipResponse = doctorProfileService.addEditProfessionalMembership(request);
		if(addEditProfessionalMembershipResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditProfessionalMembershipResponse);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	public Response<DoctorProfile> getDoctorProfile(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId, false);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			if (doctorProfile.getCoverImageUrl() != null) {
				doctorProfile.setCoverImageUrl(getFinalImageURL(doctorProfile.getCoverImageUrl()));
			}
			if (doctorProfile.getCoverThumbnailImageUrl() != null) {
				doctorProfile.setCoverThumbnailImageUrl(getFinalImageURL(doctorProfile.getCoverThumbnailImageUrl()));
			}
			

			
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfile clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (ClinicImage clinicImage : clinicProfile.getImages()) {
							if (clinicImage.getImageUrl() != null)
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							if (clinicImage.getThumbnailUrl() != null)
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
						}
					}
					
					if (clinicProfile.getClinicOwnershipImageUrl() != null)
						clinicProfile.setClinicOwnershipImageUrl(getFinalImageURL(clinicProfile.getClinicOwnershipImageUrl()));

				}
			}
		}
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(doctorProfile);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS, notes = PathProxy.DoctorProfileUrls.GET_PROFESSIONAL_MEMBERSHIPS)
	public Response<ProfessionalMembership> getProfessionalMemberships(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime) {
		List<ProfessionalMembership> professionalMemberships = doctorProfileService.getProfessionalMemberships(page,
				size, updatedTime);
		Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
		response.setDataList(professionalMemberships);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_NUMBERS)
	public Response<Boolean> addEditAppointmentNumbers(@RequestBody DoctorAppointmentNumbersAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditAppointmentNumbersResponse = doctorProfileService.addEditAppointmentNumbers(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditAppointmentNumbersResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditAppointmentNumbersResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_VISITING_TIME)
	public Response<Boolean> addEditVisitingTime(@RequestBody DoctorVisitingTimeAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditVisitingTimeResponse = doctorProfileService.addEditVisitingTime(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditVisitingTimeResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditVisitingTimeResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_CONSULTATION_FEE)
	public Response<Boolean> addEditConsultationFee(@RequestBody DoctorConsultationFeeAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditConsultationFeeResponse = doctorProfileService.addEditConsultationFee(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditConsultationFeeResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditConsultationFeeResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_APPOINTMENT_SLOT)
	public Response<Boolean> addEditAppointmentSlot(@RequestBody DoctorAppointmentSlotAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditAppointmentSlotResponse = doctorProfileService.addEditAppointmentSlot(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditAppointmentSlotResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditAppointmentSlotResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENERAL_INFO)
	public Response<Boolean> addEditGeneralInfo(@RequestBody DoctorGeneralInfo request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		Boolean addEditGeneralInfoResponse = doctorProfileService.addEditGeneralInfo(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditGeneralInfoResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditGeneralInfoResponse);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_SPECIALITIES, notes = PathProxy.DoctorProfileUrls.GET_SPECIALITIES)
	public Response<Speciality> getSpeciality(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime) {
		List<Speciality> specialities = doctorProfileService.getSpecialities(page, size, updatedTime);
		Response<Speciality> response = new Response<Speciality>();
		response.setDataList(specialities);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_SERVICES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_SERVICES, notes = PathProxy.DoctorProfileUrls.GET_SERVICES)
	public Response<Services> getServices(@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value = "searchTerm") String searchTerm) {
		List<Services> specialities = doctorProfileService.getServices(page, size, updatedTime, searchTerm);
		Response<Services> response = new Response<Services>();
		response.setDataList(specialities);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_INSTITUTES)
	public Response<EducationInstitute> getEducationInstitutes(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value ="searchTerm")  String searchTerm,
			@RequestParam(required = false, value ="discarded")  Boolean discarded) {
		List<EducationInstitute> educationInstitutes = doctorProfileService.getEducationInstitutes(page, size,
				updatedTime,searchTerm,discarded);
		Response<EducationInstitute> response = new Response<EducationInstitute>();
		response.setDataList(educationInstitutes);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS, notes = PathProxy.DoctorProfileUrls.GET_EDUCATION_QUALIFICATIONS)
	public Response<EducationQualification> getEducationQualifications(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime) {
		List<EducationQualification> qualifications = doctorProfileService.getEducationQualifications(page, size,
				updatedTime);
		Response<EducationQualification> response = new Response<EducationQualification>();
		response.setDataList(qualifications);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_MULTIPLE_DATA)
	public Response<DoctorMultipleDataAddEditResponse> addEditMultipleData(@RequestBody DoctorMultipleDataAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Request Cannot Be null");
			throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be null");
		}
		DoctorMultipleDataAddEditResponse addEditNameResponse = doctorProfileService.addEditMultipleData(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditNameResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditNameResponse.setCoverImageUrl(getFinalImageURL(addEditNameResponse.getCoverImageUrl()));
		addEditNameResponse.setProfileImageUrl(getFinalImageURL(addEditNameResponse.getProfileImageUrl()));
		addEditNameResponse
				.setThumbnailCoverImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailCoverImageUrl()));
		addEditNameResponse
				.setThumbnailProfileImageUrl(getFinalImageURL(addEditNameResponse.getThumbnailProfileImageUrl()));

		Response<DoctorMultipleDataAddEditResponse> response = new Response<DoctorMultipleDataAddEditResponse>();
		response.setData(addEditNameResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_FACILITY)
	public Response<Boolean> addEditFacility(@RequestBody DoctorAddEditFacilityRequest request) {
		if (request == null) {
			logger.warn("Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		Boolean addEditIBSResponse = doctorProfileService.addEditFacility(request);
		if (addEditIBSResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditIBSResponse);
		return response;

	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_GENDER)
	public Response<Boolean> addEditGender(@RequestBody DoctorGenderAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditNameResponse = doctorProfileService.addEditGender(request);
		if (addEditNameResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditNameResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_DOB)
	public Response<Boolean> addEditDOB(@RequestBody DoctorDOBAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditNameResponse = doctorProfileService.addEditDOB(request);
		if (addEditNameResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditNameResponse);
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VERIFICATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_VERIFICATION, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_VERIFICATION)
	public Response<Boolean> addEditVerification(@PathVariable("doctorId") String doctorId,
			@RequestParam(required = false, value ="isVerified") Boolean isVerified) {
		Response<Boolean> response = null;
		try {
			if (isVerified == null || DPDoctorUtils.anyStringEmpty(doctorId)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			Boolean addEditVerificationResponse = doctorProfileService.addEditVerification(doctorId, isVerified);
			if (addEditVerificationResponse)
				transnationalService.checkDoctor(new ObjectId(doctorId), null);
			response = new Response<Boolean>();
			response.setData(addEditVerificationResponse);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_MEDICAL_COUNCIL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_MEDICAL_COUNCIL, notes = PathProxy.DoctorProfileUrls.ADD_MEDICAL_COUNCIL)
	public Response<MedicalCouncil> addEditMedicalCouncil(@RequestBody MedicalCouncil request) {
		Response<MedicalCouncil> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}

			response = new Response<MedicalCouncil>();
			MedicalCouncil medicalCouncil = doctorProfileService.addMedicalCouncils(request);
			if (medicalCouncil != null) {
				ESMedicalCouncilDocument medicalCouncilDocument = new ESMedicalCouncilDocument();
				BeanUtil.map(medicalCouncil, medicalCouncilDocument);
				esMasterService.addEditMedicalCouncil(medicalCouncilDocument);
			}
			response.setData(medicalCouncil);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDUCATION_QUALIFICATION)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDUCATION_QUALIFICATION, notes = PathProxy.DoctorProfileUrls.ADD_EDUCATION_QUALIFICATION)
	public Response<EducationQualification> addEditEducationQualification(@RequestBody EducationQualification request) {
		Response<EducationQualification> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}

			response = new Response<EducationQualification>();
			EducationQualification educationQualification = doctorProfileService.addEditEducationQualification(request);
			if (educationQualification != null) {
				ESEducationQualificationDocument esEducationQualificationDocument = new ESEducationQualificationDocument();
				BeanUtil.map(educationQualification, esEducationQualificationDocument);
				esMasterService.addEditQualification(esEducationQualificationDocument);
			}
			response.setData(educationQualification);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDUCATION_INSTITUDE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDUCATION_INSTITUDE, notes = PathProxy.DoctorProfileUrls.ADD_EDUCATION_INSTITUDE)
	public Response<EducationInstitute> addEditEducationInstitude(@RequestBody EducationInstitute request) {
		Response<EducationInstitute> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}
			response = new Response<EducationInstitute>();
			EducationInstitute educationInstitute = doctorProfileService.addEditEducationInstitutes(request);
			if (educationInstitute != null) {
				ESEducationInstituteDocument esEducationInstituteDocument = new ESEducationInstituteDocument();
				BeanUtil.map(educationInstitute, esEducationInstituteDocument);
				esMasterService.addEditInstitude(esEducationInstituteDocument);
			}
			response.setData(educationInstitute);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_SPECIALITIES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_SPECIALITIES, notes = PathProxy.DoctorProfileUrls.ADD_SPECIALITIES)
	public Response<Speciality> addEditSpecialities(@RequestBody Speciality request) {
		Response<Speciality> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}
			response = new Response<Speciality>();
			Speciality speciality = doctorProfileService.addeditSpecialities(request);
			if (speciality != null) {
				ESSpecialityDocument esSpecialityDocument = new ESSpecialityDocument();
				BeanUtil.map(speciality, esSpecialityDocument);
				esMasterService.addEditSpeciality(esSpecialityDocument);
			}
			response.setData(speciality);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_SERVICES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_SERVICES, notes = PathProxy.DoctorProfileUrls.ADD_SERVICES)
	public Response<Services> addEditServices(@RequestBody Services request) {
		Response<Services> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}
			response = new Response<Services>();
			Services services = doctorProfileService.addEditServices(request);
			if (services != null) {
				transnationalService.addResource(new ObjectId(services.getId()), Resource.SERVICE,
						true);
				ESServicesDocument esServicesDocument = new ESServicesDocument();
				BeanUtil.map(services, esServicesDocument);
				esMasterService.addEditServices(esServicesDocument);
			}
			response.setData(services);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_PROFESSIONAL_MEMBERSHIP)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.DoctorProfileUrls.ADD_PROFESSIONAL_MEMBERSHIP)
	public Response<ProfessionalMembership> addEditSpecialities(@RequestBody ProfessionalMembership request) {
		Response<ProfessionalMembership> response = null;
		try {
			if (request == null) {
				throw new BusinessException(ServiceError.InvalidInput, "request is null");
			}
			response = new Response<ProfessionalMembership>();

			ProfessionalMembership professionalMembership = doctorProfileService.addeditProfessionalMembership(request);
			if (professionalMembership != null) {
				ESProfessionalMembershipDocument esProfessionalMembershipDocument = new ESProfessionalMembershipDocument();
				BeanUtil.map(professionalMembership, esProfessionalMembershipDocument);
				esMasterService.addEditProfessionalMembership(esProfessionalMembershipDocument);
			}
			response.setData(professionalMembership);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = PathProxy.DoctorProfileUrls.UPLOAD_VERIFICATION_DOCUMENTS, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPLOAD_VERIFICATION_DOCUMENTS, notes = PathProxy.DoctorProfileUrls.UPLOAD_VERIFICATION_DOCUMENTS)
	public Response<Boolean> uploadVerificationDocument(@PathVariable("doctorId") String doctorId,
			@FormDataParam("file") FormDataBodyPart file) {
		// if
		// (uploadVerificationDocumentResponse)transnationalService.checkDoctor(new
		// ObjectId(doctorId), null);
		Response<Boolean> response = null;
		try {

			if (file == null || DPDoctorUtils.anyStringEmpty(doctorId)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
			Boolean uploadVerificationDocumentResponse = doctorProfileService.uploadVerificationDocuments(doctorId,
					file);
			response = new Response<Boolean>();
			response.setData(uploadVerificationDocumentResponse);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@DeleteMapping(value = PathProxy.DoctorProfileUrls.DELETE_PROFILE_IMAGE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.DELETE_PROFILE_IMAGE, notes = PathProxy.DoctorProfileUrls.DELETE_PROFILE_IMAGE)
	public Response<Boolean> deleteDoctorProfilePicture(@PathVariable(value = "doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input. doctor Id is null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. doctor Id is null");
		}

		Boolean deleteImage = doctorProfileService.deleteDoctorProfilePicture(doctorId);
		transnationalService.addResource(new ObjectId(doctorId), Resource.DOCTOR, false);
		transnationalService.checkDoctor(new ObjectId(doctorId), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(deleteImage);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_PACKAGE_TYPE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_PACKAGE_TYPE, notes = PathProxy.DoctorProfileUrls.UPDATE_PACKAGE_TYPE)
	public Response<Boolean> updatePackageType(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="packageType") String packageType) {
		Boolean boolResposne = doctorProfileService.updatePackageType(doctorId, locationId, packageType);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(boolResposne);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_VACCINATION_MODULE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_VACCINATION_MODULE, notes = PathProxy.DoctorProfileUrls.UPDATE_VACCINATION_MODULE)
	public Response<Boolean> updatePackageType(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="vaccination") Boolean vaccination) {
		Boolean boolResposne = doctorProfileService.updateVaccinationModule(doctorId, locationId, vaccination);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(boolResposne);
		return response;
	}

	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_KOISK)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_KOISK, notes = PathProxy.DoctorProfileUrls.UPDATE_KOISK)
	public Response<Boolean> updateKoisk(@RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {
		Boolean boolResposne = doctorProfileService.updateKiosk(doctorId, locationId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(boolResposne);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_SERVICES)
	public Response<DoctorServicesAddEditRequest> addEditServices(@RequestBody DoctorServicesAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DoctorServicesAddEditRequest specialityResponse = doctorProfileService.addEditServices(request);

		if (specialityResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorServicesAddEditRequest> response = new Response<DoctorServicesAddEditRequest>();
		response.setData(specialityResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_CITY)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_CITY, notes = PathProxy.DoctorProfileUrls.ADD_CITY)
	public Response<Boolean> addEditCity(@RequestBody DoctorAddEditCityRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditContactResponse = doctorProfileService.addEditCity(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditContactResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_MR_CODE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_MR_CODE, notes = PathProxy.DoctorProfileUrls.ADD_MR_CODE)
	public Response<Boolean> addEditMrCode(@RequestBody AddEditMRCodeRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditContactResponse = doctorProfileService.addEditMRCode(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditContactResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FEEDBACK_URL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_FEEDBACK_URL, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_FEEDBACK_URL)
	public Response<Boolean> addEditFeedbackURL(@RequestBody DoctorMultipleDataAddEditRequest request) {
		if (request == null) {
			logger.warn("Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		Boolean addEditResponse = doctorProfileService.addEditFeedbackURL(request);
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditResponse);
		return response;

	}
	

	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NUTRITION_DETAILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_NUTRITION_DETAILS, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_NUTRITION_DETAILS)
	public Response<Boolean> addEditNutritionistRequest(@RequestBody AddEditNutritionistRequest request) {
		if (request == null) {
			logger.warn("Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId())) {
			logger.warn("Doctor Id, LocationId Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}

		Boolean addEditResponse = doctorProfileService.addEditNutritionistRequest(request);		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditResponse);
		return response;

	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_EXPERIENCE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_EXPERIENCE, notes = PathProxy.DoctorProfileUrls.UPDATE_EXPERIENCE)
		public Response<Boolean> updatePatientExperience() {
			
		 
		Response<Boolean> response = new Response<Boolean>();
		response.setData(doctorProfileService.updateExperience());
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_TIME)
	public Response<Boolean> addEditOnlineConsultationTiming(@RequestBody DoctorOnlineWorkingTimeRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditVisitingTimeResponse = doctorProfileService.addEditOnlineWorkingTime(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (addEditVisitingTimeResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	
	}

	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_INFO)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_INFO, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_INFO)
	public Response<DoctorOnlineGeneralInfo> addEditOnlineConsultationInfo(@RequestBody DoctorOnlineGeneralInfo request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		DoctorOnlineGeneralInfo addEditVisitingTimeResponse = doctorProfileService.addEditDoctorOnlineInfo(request);
		transnationalService.addResource(new ObjectId(request.getDoctorId()), Resource.DOCTOR, false);
		if (request.getDoctorId()!=null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<DoctorOnlineGeneralInfo> response = new Response<DoctorOnlineGeneralInfo>();
		response.setData(addEditVisitingTimeResponse);
		return response;
	
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.CHECK_MEDICAL_DOCUMENTS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.CHECK_MEDICAL_DOCUMENTS, notes = PathProxy.DoctorProfileUrls.CHECK_MEDICAL_DOCUMENTS)
	public Response<DoctorProfile> checkMedicalDocument(@RequestParam (required = true,value = "isRegistrationDetailsVerified")Boolean isRegistrationDetailsVerified,@RequestParam (required = true,value = "isPhotoIdVerified")Boolean isPhotoIdVerified
			,@RequestParam(required = true,value = "doctorId")String doctorId) {
		
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Is Empty");
		}
		DoctorProfile addEditVisitingTimeResponse = doctorProfileService.checkMedicalDocument(doctorId, isRegistrationDetailsVerified,isPhotoIdVerified);
		transnationalService.addResource(new ObjectId(doctorId), Resource.DOCTOR, false);
		if (doctorId !=null)
			transnationalService.checkDoctor(new ObjectId(doctorId),null);
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(addEditVisitingTimeResponse);
		return response;
	
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS, notes = PathProxy.DoctorProfileUrls.UPLOAD_REGISTRATION_DETAILS)
	public Response<String> uploadRegistrationDetails( @RequestBody DoctorRegistrationDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		String addEditCoverPictureResponse = doctorProfileService.uploadRegistrationDetails(request);
		if (addEditCoverPictureResponse != null)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		addEditCoverPictureResponse = getFinalImageURL(addEditCoverPictureResponse);
		Response<String> response = new Response<String>();
		response.setData(addEditCoverPictureResponse);
		return response;
	}
	
	
//	@PutMapping(value = PathProxy.DoctorProfileUrls.VERIFY_CLINIC_DOCUMENT)
//	@ApiOperation(value = PathProxy.DoctorProfileUrls.VERIFY_CLINIC_DOCUMENT, notes = PathProxy.DoctorProfileUrls.VERIFY_CLINIC_DOCUMENT)
//	public Response<Boolean> verifyClinicDocument(@RequestParam (required = true,value = "isClinicOwnershipVerified")Boolean isClinicOwnershipVerified
//			,@RequestParam(required = true,value = "doctorId")String doctorId,@RequestParam(required = true,value = "locationId")String locationId) {
//		
//		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
//			logger.warn("Doctor Id Is Empty");
//			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Is Empty");
//		}
//		Boolean addEditVisitingTimeResponse = doctorProfileService.verifyClinicDocument(doctorId, isClinicOwnershipVerified,locationId);
//		transnationalService.addResource(new ObjectId(doctorId), Resource.DOCTOR, false);
//		if (doctorId !=null)
//			transnationalService.checkDoctor(new ObjectId(doctorId),null);
//		Response<Boolean> response = new Response<Boolean>();
//		response.setData(addEditVisitingTimeResponse);
//		return response;
//	
//	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.EDIT_DOCTOR_SLUG_URL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.EDIT_DOCTOR_SLUG_URL, notes = PathProxy.DoctorProfileUrls.EDIT_DOCTOR_SLUG_URL)
	public Response<Boolean> addEditDoctorSlugUrl(@RequestBody DoctorSlugUrlRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditRegistrationDetailResponse = doctorProfileService.editDoctorSlugUrl(request);
		if(addEditRegistrationDetailResponse)
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.DoctorProfileUrls.DELETE_MEDICAL_COUNCILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.DELETE_MEDICAL_COUNCILS, notes = PathProxy.DoctorProfileUrls.DELETE_MEDICAL_COUNCILS)
	public Response<MedicalCouncil> deleteMedicalCouncils(
			@RequestParam(required = false, value ="discarded")Boolean discarded,
			@RequestParam(required = false, value ="medicalCouncilId")  String medicalCouncilId) {
		MedicalCouncil medicalCouncils = doctorProfileService.deleteMedicalCouncil(medicalCouncilId,discarded);
		Response<MedicalCouncil> response = new Response<MedicalCouncil>();
		response.setData(medicalCouncils);
		return response;
	}
	
	@DeleteMapping(value = PathProxy.DoctorProfileUrls.DELETE_EDUCATION_INSTITUTE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.DELETE_EDUCATION_INSTITUTE, notes = PathProxy.DoctorProfileUrls.DELETE_EDUCATION_INSTITUTE)
	public Response<EducationInstitute> deleteEducationInstitutes(
			@RequestParam(required = false, value ="discarded")Boolean discarded,
			@RequestParam(required = false, value ="educationInstituteId")  String educationInstituteId) {
		EducationInstitute medicalCouncils = doctorProfileService.deleteEducationInstitute(educationInstituteId, discarded);
		Response<EducationInstitute> response = new Response<EducationInstitute>();
		response.setData(medicalCouncils);
		return response;
	}
	

	@GetMapping(value = PathProxy.DoctorProfileUrls.CHECK_BIRTHDAY_SMS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.CHECK_BIRTHDAY_SMS, notes = PathProxy.DoctorProfileUrls.CHECK_BIRTHDAY_SMS)
	public Response<Boolean> checkBirthdaySms(@RequestParam(required = false, value ="doctorId")String doctorId,
			@RequestParam(required = false, value ="locationId")  String locationId,
			@RequestParam(required = false, value ="isSendBirthdaySMS")  Boolean isSendBirthdaySMS) {
//		if ( DPDoctorUtils.anyStringEmpty(doctorId,locationId)) {
//			logger.warn("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
		Boolean addEditRegistrationDetailResponse = doctorProfileService.checkBirthdaySms(doctorId,locationId,isSendBirthdaySMS);
//		if(addEditRegistrationDetailResponse)
//		transnationalService.checkDoctor(new ObjectId(doctorId), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_BIRTHDAY_SMS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_BIRTHDAY_SMS, notes = PathProxy.DoctorProfileUrls.UPDATE_BIRTHDAY_SMS)
	public Response<Boolean> updateBirthdaySmsStatus(
			@RequestParam(required = false, value ="isSendBirthdaySMS")  Boolean isSendBirthdaySMS,
			@RequestParam(required = false, value ="doctorId")  String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {
		
		Boolean addEditRegistrationDetailResponse = doctorProfileService.updateBirthdaySms(isSendBirthdaySMS, doctorId, locationId);
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_WELCOME_PATIENT_SMS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_WELCOME_PATIENT_SMS, notes = PathProxy.DoctorProfileUrls.UPDATE_WELCOME_PATIENT_SMS)
	public Response<Boolean> updateWelcomePatientSMS(
			@RequestParam(required = false, value ="isWelcomePatientSMS")  Boolean isWelcomePatientSMS,
			@RequestParam(required = false, value ="doctorId")  String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {
		
		Boolean boolean1 = doctorProfileService.updateWelcomePatientSMS(isWelcomePatientSMS, doctorId, locationId);
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(boolean1);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_TRANSACTION_SMS_REPORT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_TRANSACTION_SMS_REPORT, notes = PathProxy.DoctorProfileUrls.GET_TRANSACTION_SMS_REPORT)
	public Response<TransactionalSmsReport> getTransactionSmsReport(@DefaultValue("0")@RequestParam(required = false,value ="size") int size, 
			@DefaultValue("0")	@RequestParam(required = false,value ="page") int page,
			@RequestParam(required = false,value="fromDate") String fromDate, @RequestParam(required = false,value="toDate") String toDate,
			@RequestParam(required = false,value ="doctorId") String doctorId,
			@RequestParam(required = false,value ="locationId") String locationId) {

		Response<TransactionalSmsReport> response = new Response<TransactionalSmsReport>();
		if (doctorId == null) {
			logger.warn("doctorId or locationid  is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "doctorId send  is NULL");
		}
			response.setDataList(transactionSmsServices.getSmsReport(page, size, doctorId, locationId,fromDate,toDate));
	
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_SLOT)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_SLOT, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_ONLINE_CONSULTATION_SLOT)
	public Response<Boolean> addEditOnlineConsultationSlot(@RequestBody DoctorOnlineConsultationAddEditRequest request) {
		if (request == null) {
			logger.warn("Doctor Contact Request Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Contact Request Is Empty");
		} else if (DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Doctor Id Is Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id, LocationId Is Empty");
		}
		Boolean addEditOnlineConsultationSlotResponse = doctorProfileService.addEditOnlineConsultationSlot(request);
			if (addEditOnlineConsultationSlotResponse)
			transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditOnlineConsultationSlotResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOCTOR_DETAILS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.ADD_EDIT_DOCTOR_DETAILS, notes = PathProxy.DoctorProfileUrls.ADD_EDIT_DOCTOR_DETAILS)
	public Response<Boolean> addEditDoctorDetails(@RequestBody DoctorDetails request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addEditDoctorDetailResponse = doctorProfileService.addEditDoctorDetails(request);
		if(addEditDoctorDetailResponse)
		transnationalService.checkDoctor(new ObjectId(request.getDoctorId()), null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditDoctorDetailResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_SUPER_ADMIN)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_SUPER_ADMIN, notes = PathProxy.DoctorProfileUrls.UPDATE_SUPER_ADMIN)
	public Response<Boolean> updateSuperAdmin(
			@RequestParam(required = false, value ="isSuperAdmin")  Boolean isSuperAdmin,
			@RequestParam(required = false, value ="doctorId")  String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {
		
		Boolean addEditRegistrationDetailResponse = doctorProfileService.updateSuperAdmin(isSuperAdmin, doctorId, locationId);
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditRegistrationDetailResponse);
		return response;
	}
	
	@GetMapping(value = PathProxy.DoctorProfileUrls.UPDATE_TRANSACTIONAL_SMS)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_TRANSACTIONAL_SMS, notes = PathProxy.DoctorProfileUrls.UPDATE_TRANSACTIONAL_SMS)
	public Response<Boolean> updateTransactionalSms(
			@RequestParam(required = false, value ="isTransactionalSms")  Boolean isTransactionalSms,
			@RequestParam(required = false, value ="doctorId")  String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId) {
		
		Boolean transactionalSmsResponse = doctorProfileService.updateDoctorTransactionSms(isTransactionalSms, doctorId, locationId);
		
		Response<Boolean> response = new Response<Boolean>();
		
		response.setData(transactionalSmsResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.DoctorProfileUrls.UPDATE_CLINICAL_IMAGE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.UPDATE_CLINICAL_IMAGE, notes = PathProxy.DoctorProfileUrls.UPDATE_CLINICAL_IMAGE)
	public Response<Boolean> updateClinicalImage(@RequestBody DoctorClinicImage request) {
		
		Boolean transactionalSmsResponse = doctorProfileService.updateClinicImage(request);
		
		Response<Boolean> response = new Response<Boolean>();
		
		response.setData(transactionalSmsResponse);
		return response;
	}
	
}
