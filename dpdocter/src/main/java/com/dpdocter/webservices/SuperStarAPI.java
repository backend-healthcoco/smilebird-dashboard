package com.dpdocter.webservices;

import java.util.List;

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

import com.dpdocter.beans.DoctorSchoolAssociation;
import com.dpdocter.beans.NutritionSchoolAssociation;
import com.dpdocter.beans.School;
import com.dpdocter.beans.SchoolBranch;
import com.dpdocter.beans.SuperStarHealthCamp;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SuperstarService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.SUPERSTAR_BASE_URL, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SUPERSTAR_BASE_URL, description = "Endpoint for superstar")
public class SuperStarAPI {
	private static Logger logger = LogManager.getLogger(SuperStarAPI.class.getName());

	@Autowired
	SuperstarService superstarService;

	@PostMapping(value = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL)
	@ApiOperation(value = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL, notes = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL)
	public Response<School> addEditSchool(@RequestBody School request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		School school = superstarService.addEditSchool(request);
		Response<School> response = new Response<School>();

		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_SCHOOL_BY_ID)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_SCHOOL_BY_ID, notes = PathProxy.SuperStarUrls.GET_SCHOOL_BY_ID)
	public Response<School> getSchoolById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<School> response = new Response<School>();

		School school = superstarService.getSchoolById(id);
		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_SCHOOLS)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_SCHOOLS, notes = PathProxy.SuperStarUrls.GET_SCHOOLS)
	public Response<School> getSchools(
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated) {

		Response<School> response = new Response<School>();

		List<School> schools = superstarService.getSchoolList(updatedTime, searchTerm, page, size, isActivated);
		response.setCount(superstarService.getSchoolListCount(updatedTime, searchTerm, isActivated));

		response.setDataList(schools);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL)
	@ApiOperation(value = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL, notes = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL)
	public Response<School> ActivateSchool(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<School> response = new Response<School>();

		School school = superstarService.activateSchool(id, isActivated);
		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.VERIFY_SCHOOL)
	@ApiOperation(value = PathProxy.SuperStarUrls.VERIFY_SCHOOL, notes = PathProxy.SuperStarUrls.VERIFY_SCHOOL)
	public Response<School> VerifySchool(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isVerify") Boolean isVerify) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<School> response = new Response<School>();

		School school = superstarService.verifySchool(id, isVerify);
		response.setData(school);
		return response;
	}

	@PostMapping(value = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL_BRANCH)
	@ApiOperation(value = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL, notes = PathProxy.SuperStarUrls.ADD_EDIT_SCHOOL)
	public Response<SchoolBranch> addEditSchoolBranch(@RequestBody SchoolBranch request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		SchoolBranch school = superstarService.addEditSchoolBranch(request);
		Response<SchoolBranch> response = new Response<SchoolBranch>();

		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_SCHOOL_BRANCH_BY_ID)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_SCHOOL_BY_ID, notes = PathProxy.SuperStarUrls.GET_SCHOOL_BY_ID)
	public Response<SchoolBranch> getSchoolBranchById(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SchoolBranch> response = new Response<SchoolBranch>();

		SchoolBranch school = superstarService.getSchoolBranchById(id);
		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_SCHOOL_BRANCHES)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_SCHOOL_BRANCHES, notes = PathProxy.SuperStarUrls.GET_SCHOOL_BRANCHES)
	public Response<SchoolBranch> getSchoolBranches(
			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated,
			@RequestParam(required = false, value = "isDiscarded") Boolean isDiscarded) {

		Response<SchoolBranch> response = new Response<SchoolBranch>();

		List<SchoolBranch> schools = superstarService.getSchoolBranches(updatedTime, searchTerm, page, size,
				isActivated, isDiscarded);
		response.setDataList(schools);
		response.setCount(superstarService.getSchoolBranchesCount(updatedTime, searchTerm, isActivated, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_BRANCHES_FOR_SCHOOL)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_BRANCHES_FOR_SCHOOL, notes = PathProxy.SuperStarUrls.GET_BRANCHES_FOR_SCHOOL)
	public Response<SchoolBranch> getBranchesForSchool(@PathVariable("schoolId") String schoolId,

			@RequestParam(required = false, value = "updatedTime", defaultValue = "0") String updatedTime,

			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated) {

		Response<SchoolBranch> response = new Response<SchoolBranch>();

		List<SchoolBranch> schools = superstarService.getSchoolBranchesForSchool(schoolId, updatedTime, searchTerm,
				page, size, isActivated);
		response.setDataList(schools);
		response.setCount(
				superstarService.getSchoolBranchesForSchoolCount(schoolId, updatedTime, searchTerm, isActivated));

		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL_BRANCH)
	@ApiOperation(value = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL_BRANCH, notes = PathProxy.SuperStarUrls.ACTIVATE_SCHOOL_BRANCH)
	public Response<SchoolBranch> ActivateSchoolBranch(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isActivated") Boolean isActivated) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SchoolBranch> response = new Response<SchoolBranch>();

		SchoolBranch school = superstarService.activateSchoolBranch(id, isActivated);
		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.VERIFY_SCHOOL_BRANCH)
	@ApiOperation(value = PathProxy.SuperStarUrls.VERIFY_SCHOOL_BRANCH, notes = PathProxy.SuperStarUrls.VERIFY_SCHOOL_BRANCH)
	public Response<SchoolBranch> VerifySchoolBranch(@PathVariable("id") String id,
			@RequestParam(required = false, value = "isVerify") Boolean isVerify) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SchoolBranch> response = new Response<SchoolBranch>();

		SchoolBranch school = superstarService.verifySchoolBranch(id, isVerify);
		response.setData(school);
		return response;
	}

	@DeleteMapping(value = PathProxy.SuperStarUrls.DISCARD_SCHOOL_BRANCH)
	@ApiOperation(value = PathProxy.SuperStarUrls.DISCARD_SCHOOL_BRANCH, notes = PathProxy.SuperStarUrls.DISCARD_SCHOOL_BRANCH)

	public Response<SchoolBranch> DiscardSchoolBranch(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SchoolBranch> response = new Response<SchoolBranch>();

		SchoolBranch school = superstarService.discardSchoolBranch(id, discarded);
		response.setData(school);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.CHECK_USERNAME_EXISTS)
	@ApiOperation(value = PathProxy.SuperStarUrls.CHECK_USERNAME_EXISTS, notes = PathProxy.SuperStarUrls.CHECK_USERNAME_EXISTS)
	public Response<Boolean> DiscardSchoolBranch(@PathVariable("userName") String userName) {
		if (DPDoctorUtils.anyStringEmpty(userName)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();

		Boolean status = superstarService.checkUserNameExists(userName);
		response.setData(status);
		return response;
	}

	
	@ResponseBody
	@PostMapping(value = PathProxy.SuperStarUrls.ADD_EDIT_HEALTHCAMP)
	@ApiOperation(value = PathProxy.SuperStarUrls.ADD_EDIT_HEALTHCAMP, notes = PathProxy.SuperStarUrls.ADD_EDIT_HEALTHCAMP)
	public Response<SuperStarHealthCamp> addEditSchool(@RequestBody SuperStarHealthCamp request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		SuperStarHealthCamp healthcamp = superstarService.addEditHealthcamp(request);
		Response<SuperStarHealthCamp> response = new Response<SuperStarHealthCamp>();

		response.setData(healthcamp);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_HEALTHCAMP_BY_ID)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_HEALTHCAMP_BY_ID, notes = PathProxy.SuperStarUrls.GET_HEALTHCAMP_BY_ID)
	public Response<SuperStarHealthCamp> getHealthcampById(@PathVariable("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SuperStarHealthCamp> response = new Response<SuperStarHealthCamp>();

		SuperStarHealthCamp healthcamp = superstarService.getHealthcampById(id);
		response.setData(healthcamp);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_HEALTHCAMPS_BY_DOCTOR_ID)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_HEALTHCAMPS_BY_DOCTOR_ID, notes = PathProxy.SuperStarUrls.GET_HEALTHCAMPS_BY_DOCTOR_ID)
	public Response<SuperStarHealthCamp> getHealthcampByDoctorId(@PathVariable("doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<SuperStarHealthCamp> response = new Response<SuperStarHealthCamp>();

		List<SuperStarHealthCamp> healthcamp = superstarService.getHealthcampByDoctorId(doctorId);
		response.setDataList(healthcamp);
		return response;
	}

	@GetMapping(value = PathProxy.SuperStarUrls.GET_HEALTHCAMPS)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_HEALTHCAMPS, notes = PathProxy.SuperStarUrls.GET_HEALTHCAMPS)
	public Response<SuperStarHealthCamp> getHealthcamps(
			@RequestParam(value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(value = "searchTerm", required = false) String searchTerm,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "size", required = false) int size) {

		Response<SuperStarHealthCamp> response = new Response<SuperStarHealthCamp>();

		List<SuperStarHealthCamp> healthcamp = superstarService.getHealthcamps(updatedTime, searchTerm, page, size);
		response.setDataList(healthcamp);
		response.setCount(superstarService.getHealthcampCount(updatedTime, searchTerm, page, size));
		return response;
	}

	@DeleteMapping(value = PathProxy.SuperStarUrls.DISCARD_HEALTHCAMP)
	@ApiOperation(value = PathProxy.SuperStarUrls.DISCARD_HEALTHCAMP, notes = PathProxy.SuperStarUrls.DISCARD_HEALTHCAMP)
	public Response<Boolean> discardHealthcamp(@PathVariable("id") String id,
			@RequestParam(value = "discarded", required = false) Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();

		Boolean healthcamp = superstarService.discardHealthCamp(id, discarded);
		response.setData(healthcamp);
		return response;
	}
	
	@ResponseBody
	@PostMapping(value = PathProxy.SuperStarUrls.ADD_EDIT_NUTRITION_SCHOOL_ASSOCIATION)
	@ApiOperation(value = PathProxy.SuperStarUrls.ADD_EDIT_NUTRITION_SCHOOL_ASSOCIATION, notes = PathProxy.SuperStarUrls.ADD_EDIT_NUTRITION_SCHOOL_ASSOCIATION)
	public Response<NutritionSchoolAssociation> addEditNutritionSchoolAssociation(@RequestBody NutritionSchoolAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		NutritionSchoolAssociation healthcamp = superstarService.addEditNutritionSchoolAssociation(request);
		Response<NutritionSchoolAssociation> response = new Response<NutritionSchoolAssociation>();

		response.setData(healthcamp);
		return response;
	}
	
	@GetMapping(value = PathProxy.SuperStarUrls.GET_NUTRITION_SCHOOL_ASSOCIATIONS)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_NUTRITION_SCHOOL_ASSOCIATIONS, notes = PathProxy.SuperStarUrls.GET_NUTRITION_SCHOOL_ASSOCIATIONS)
	public Response<NutritionSchoolAssociation> getAssociations(
			@RequestParam(value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(value = "searchTerm", required = false) String searchTerm,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "size", required = false) int size,
			@RequestParam(value = "branchId", required = true) String branchId) {

		Response<NutritionSchoolAssociation> response = new Response<NutritionSchoolAssociation>();

		List<NutritionSchoolAssociation> healthcamp = superstarService.getNutritionSchoolAssociation(branchId,updatedTime, searchTerm, page, size);
		response.setDataList(healthcamp);
		response.setCount(superstarService.getNutritionSchoolAssociationCount(branchId, updatedTime, searchTerm, page, size));
		return response;
	}

	@ResponseBody
	@PostMapping(value = PathProxy.SuperStarUrls.ADD_EDIT_DOCTOR_SCHOOL_ASSOCIATION)
	@ApiOperation(value = PathProxy.SuperStarUrls.ADD_EDIT_DOCTOR_SCHOOL_ASSOCIATION, notes = PathProxy.SuperStarUrls.ADD_EDIT_DOCTOR_SCHOOL_ASSOCIATION)
	public Response<DoctorSchoolAssociation> addEditDoctorSchoolAssociation(@RequestBody DoctorSchoolAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		DoctorSchoolAssociation healthcamp = superstarService.addEditDoctorSchoolAssociation(request);
		Response<DoctorSchoolAssociation> response = new Response<DoctorSchoolAssociation>();

		response.setData(healthcamp);
		return response;
	}
	
	@GetMapping(value = PathProxy.SuperStarUrls.GET_DOCTOR_SCHOOL_ASSOCIATIONS)
	@ApiOperation(value = PathProxy.SuperStarUrls.GET_DOCTOR_SCHOOL_ASSOCIATIONS, notes = PathProxy.SuperStarUrls.GET_DOCTOR_SCHOOL_ASSOCIATIONS)
	public Response<DoctorSchoolAssociation> getDoctorSchoolAssociation(
			@RequestParam(value = "updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(value = "searchTerm", required = false) String searchTerm,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "size", required = false) int size,
			@RequestParam(value = "branchId", required = true) String branchId) {

		Response<DoctorSchoolAssociation> response = new Response<DoctorSchoolAssociation>();

		List<DoctorSchoolAssociation> healthcamp = superstarService.getDoctorSchoolAssociation(branchId,updatedTime, searchTerm, page, size);
		response.setDataList(healthcamp);
		response.setCount(superstarService.getDoctorSchoolAssociationCount(branchId, updatedTime, searchTerm));
		return response;
	}
}
