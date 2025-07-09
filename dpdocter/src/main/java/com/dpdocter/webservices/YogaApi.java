package com.dpdocter.webservices;

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
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CuratedYogaSession;
import com.dpdocter.beans.Essentials;
import com.dpdocter.beans.Injury;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.Yoga;
import com.dpdocter.beans.YogaClasses;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaSession;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.YogaService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.YOGA_BASE_URL, description = "Endpoint for yoga ")
@RequestMapping(value = PathProxy.YOGA_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class YogaApi {

	private static Logger logger = LogManager.getLogger(YogaApi.class.getName());

	@Autowired
	private YogaService yogaService;

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_TEACHER)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_TEACHER, notes = PathProxy.YogaUrls.ADD_EDIT_YOGA_TEACHER)
	public Response<YogaTeacher> addEditYogaTeacher(@RequestBody YogaTeacher request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaTeacher> response = new Response<YogaTeacher>();
		response.setData(yogaService.addEditTeacher(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_TEACHER_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_TEACHER_BY_ID, notes = PathProxy.YogaUrls.GET_YOGA_TEACHER_BY_ID)
	public Response<YogaTeacher> getTeacher(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaTeacher> response = new Response<YogaTeacher>();
		response.setData(yogaService.getTeacherById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_TEACHER)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_TEACHER, notes = PathProxy.YogaUrls.GET_YOGA_TEACHER)
	public Response<YogaTeacher> getYogaTeacher(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countYogaTeacher(discarded, searchTerm);
		Response<YogaTeacher> response = new Response<YogaTeacher>();

		response.setDataList(yogaService.getTeachers(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_YOGA_TEACHER)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_YOGA_TEACHER, notes = PathProxy.YogaUrls.DELETE_YOGA_TEACHER)
	public Response<YogaTeacher> deleteYogaTeacher(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<YogaTeacher> response = new Response<YogaTeacher>();
		response.setData(yogaService.deleteTeacher(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_INJURY)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_INJURY, notes = PathProxy.YogaUrls.ADD_EDIT_INJURY)
	public Response<Injury> addEditInjury(@RequestBody Injury request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Injury> response = new Response<Injury>();
		response.setData(yogaService.addEditInjury(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_INJURY_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_INJURY_BY_ID, notes = PathProxy.YogaUrls.GET_INJURY_BY_ID)
	public Response<Injury> getInjuryById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Injury> response = new Response<Injury>();
		response.setData(yogaService.getInjuryById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_INJURY)
	@ApiOperation(value = PathProxy.YogaUrls.GET_INJURY, notes = PathProxy.YogaUrls.GET_INJURY)
	public Response<Injury> getInjury(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countInjury(discarded, searchTerm);
		Response<Injury> response = new Response<Injury>();
		if (count > 0)
			response.setDataList(yogaService.getInjury(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_INJURY)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_INJURY, notes = PathProxy.YogaUrls.DELETE_INJURY)
	public Response<Injury> deleteInjury(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Injury> response = new Response<Injury>();
		response.setData(yogaService.deleteInjury(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_YOGA)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_YOGA, notes = PathProxy.YogaUrls.ADD_EDIT_YOGA)
	public Response<Yoga> addEditYoga(@RequestBody Yoga request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Yoga> response = new Response<Yoga>();
		response.setData(yogaService.addEditYoga(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_BY_ID, notes = PathProxy.YogaUrls.GET_YOGA_BY_ID)
	public Response<Yoga> getYogaById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Yoga> response = new Response<Yoga>();
		response.setData(yogaService.getYogaById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA, notes = PathProxy.YogaUrls.GET_YOGA)
	public Response<Yoga> getYoga(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countYoga(discarded, searchTerm);
		Response<Yoga> response = new Response<Yoga>();
		// if (count > 0)
		response.setDataList(yogaService.getYoga(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_YOGA)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_YOGA, notes = PathProxy.YogaUrls.DELETE_YOGA)
	public Response<Yoga> deleteYoga(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Yoga> response = new Response<Yoga>();
		response.setData(yogaService.deleteYoga(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_CLASSES)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_CLASSES, notes = PathProxy.YogaUrls.ADD_EDIT_YOGA_CLASSES)
	public Response<YogaClasses> addEditYogaClasses(@RequestBody YogaClasses request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaClasses> response = new Response<YogaClasses>();
		response.setData(yogaService.addEditYogaClasses(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_CLASSES_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_CLASSES_BY_ID, notes = PathProxy.YogaUrls.GET_YOGA_CLASSES_BY_ID)
	public Response<YogaClasses> getYogaClassesById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaClasses> response = new Response<YogaClasses>();
		response.setData(yogaService.getYogaClassesById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_CLASSES)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_CLASSES, notes = PathProxy.YogaUrls.GET_YOGA_CLASSES)
	public Response<YogaClasses> getYogaClasses(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countYogaClasses(discarded, searchTerm);
		Response<YogaClasses> response = new Response<YogaClasses>();

		response.setDataList(yogaService.getYogaClasses(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_YOGA_CLASSES)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_YOGA_CLASSES, notes = PathProxy.YogaUrls.DELETE_YOGA_CLASSES)
	public Response<YogaClasses> deleteYogaClasses(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<YogaClasses> response = new Response<YogaClasses>();
		response.setData(yogaService.deleteYogaClasses(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.UPLOAD_IMAGE, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.YogaUrls.UPLOAD_IMAGE, notes = PathProxy.YogaUrls.UPLOAD_IMAGE)
	public Response<Yoga> saveImage(@RequestParam(value = "file") MultipartFile file) {

		Yoga yoga = yogaService.uploadImage(file);
		Response<Yoga> response = new Response<Yoga>();
		response.setData(yoga);
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.UPLOAD_VIDEO, consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = PathProxy.YogaUrls.UPLOAD_VIDEO, notes = PathProxy.YogaUrls.UPLOAD_VIDEO)
	public Response<Yoga> saveVideo(@RequestParam(value = "file") MultipartFile file) {

		Yoga mindfulness = yogaService.uploadVideo(file);
		Response<Yoga> response = new Response<Yoga>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_SESSION, notes = PathProxy.YogaUrls.ADD_EDIT_YOGA_SESSION)
	public Response<YogaSession> addEditYogaSession(@RequestBody YogaSession request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaSession> response = new Response<YogaSession>();
		response.setData(yogaService.addEditYogaSession(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_SESSION_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_SESSION_BY_ID, notes = PathProxy.YogaUrls.GET_YOGA_SESSION_BY_ID)
	public Response<YogaSession> getYogaSessionById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaSession> response = new Response<YogaSession>();
		response.setData(yogaService.getYogaSessionById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_SESSION, notes = PathProxy.YogaUrls.GET_YOGA_SESSION)
	public Response<YogaSession> getYogaSession(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countYogaSession(discarded, searchTerm);
		Response<YogaSession> response = new Response<YogaSession>();
		// if (count > 0)
		response.setDataList(yogaService.getYogaSession(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_YOGA_SESSION, notes = PathProxy.YogaUrls.DELETE_YOGA_SESSION)
	public Response<YogaSession> deleteYogaSession(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<YogaSession> response = new Response<YogaSession>();
		response.setData(yogaService.deleteYogaSession(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_DISEASE)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_YOGA_DISEASE, notes = PathProxy.YogaUrls.ADD_EDIT_YOGA_DISEASE)
	public Response<YogaDisease> addEditYogaDisease(@RequestBody YogaDisease request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaDisease> response = new Response<YogaDisease>();
		response.setData(yogaService.addEditYogaDisease(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_DISEASE_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_DISEASE_BY_ID, notes = PathProxy.YogaUrls.GET_YOGA_DISEASE_BY_ID)
	public Response<YogaDisease> getYogaDiseaseById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<YogaDisease> response = new Response<YogaDisease>();
		response.setData(yogaService.getYogaDiseaseById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_YOGA_DISEASE)
	@ApiOperation(value = PathProxy.YogaUrls.GET_YOGA_DISEASE, notes = PathProxy.YogaUrls.GET_YOGA_DISEASE)
	public Response<YogaDisease> getYogaDisease(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countYogaDisease(discarded, searchTerm);
		Response<YogaDisease> response = new Response<YogaDisease>();
		// if (count > 0)
		response.setDataList(yogaService.getYogaDisease(page, size, discarded, searchTerm, null));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_YOGA_DISEASE)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_YOGA_DISEASE, notes = PathProxy.YogaUrls.DELETE_YOGA_DISEASE)
	public Response<YogaDisease> deleteYogaDisease(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<YogaDisease> response = new Response<YogaDisease>();
		response.setData(yogaService.deleteYogaDisease(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_CURATED_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_CURATED_YOGA_SESSION, notes = PathProxy.YogaUrls.ADD_EDIT_CURATED_YOGA_SESSION)
	public Response<CuratedYogaSession> addEditCuratedYogaSession(@RequestBody CuratedYogaSession request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CuratedYogaSession> response = new Response<CuratedYogaSession>();
		response.setData(yogaService.addEditCuratedYogaSession(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION_BY_ID, notes = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION_BY_ID)
	public Response<CuratedYogaSession> getCuratedYogaSessionById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CuratedYogaSession> response = new Response<CuratedYogaSession>();
		response.setData(yogaService.getCuratedYogaSessionById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION, notes = PathProxy.YogaUrls.GET_CURATED_YOGA_SESSION)
	public Response<CuratedYogaSession> getCuratedYogaSession(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countCuratedYogaSession(discarded, searchTerm);
		Response<CuratedYogaSession> response = new Response<CuratedYogaSession>();
		// if (count > 0)
		response.setDataList(yogaService.getCuratedYogaSession(page, size, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_CURATED_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_CURATED_YOGA_SESSION, notes = PathProxy.YogaUrls.DELETE_CURATED_YOGA_SESSION)
	public Response<CuratedYogaSession> deleteCuratedYogaSession(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<CuratedYogaSession> response = new Response<CuratedYogaSession>();
		response.setData(yogaService.deleteCuratedYogaSession(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.YogaUrls.ADD_EDIT_ESSENTIAL_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.ADD_EDIT_ESSENTIAL_YOGA_SESSION, notes = PathProxy.YogaUrls.ADD_EDIT_ESSENTIAL_YOGA_SESSION)
	public Response<Essentials> addEditEssentialsYogaSession(@RequestBody Essentials request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Essentials> response = new Response<Essentials>();
		response.setData(yogaService.addEditEssentials(request));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION_BY_ID)
	@ApiOperation(value = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION_BY_ID, notes = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION_BY_ID)
	public Response<Essentials> getEssentialYogaSessionById(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Essentials> response = new Response<Essentials>();
		response.setData(yogaService.getEssentialById(id));
		return response;
	}

	@GetMapping(value = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION, notes = PathProxy.YogaUrls.GET_ESSENTIAL_YOGA_SESSION)
	public Response<Essentials> getEssentialYogaSession(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = yogaService.countEssentials(discarded, searchTerm);
		Response<Essentials> response = new Response<Essentials>();
		// if (count > 0)
		response.setDataList(yogaService.getEssentials(page, size, discarded, searchTerm));
		response.setCount(count);
		return response;

	}

	@DeleteMapping(value = PathProxy.YogaUrls.DELETE_ESSENTIAL_YOGA_SESSION)
	@ApiOperation(value = PathProxy.YogaUrls.DELETE_ESSENTIAL_YOGA_SESSION, notes = PathProxy.YogaUrls.DELETE_ESSENTIAL_YOGA_SESSION)
	public Response<Essentials> deleteEssentialYogaSession(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Essentials> response = new Response<Essentials>();
		response.setData(yogaService.deleteEssentials(id, discarded));
		return response;
	}

}
