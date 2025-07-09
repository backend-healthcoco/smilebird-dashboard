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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.ActivityAssign;
import com.dpdocter.beans.Language;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.MindfulnessAssign;
import com.dpdocter.beans.Stories;
import com.dpdocter.beans.StoriesAssign;
import com.dpdocter.beans.TodaySession;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.HappinessServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = PathProxy.HAPPINESS_BASE_URL, description = "Endpoint for happiness")
@RequestMapping(value=PathProxy.HAPPINESS_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
public class HappinessAPI {

	 private static Logger logger = LogManager.getLogger(HappinessAPI.class.getName());
	 
	 @Autowired
	 private HappinessServices happinessService;
	 
	 
	 @PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_LANGUAGE)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_LANGUAGE, notes = PathProxy.HappinessUrls.ADD_EDIT_LANGUAGE)
		public Response<Language> addEditLanguage(@RequestBody Language request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(happinessService.addEditLanguage(request));
		return response;
		}
		
		@GetMapping(value=PathProxy.HappinessUrls.GET_LANGUAGE_BY_ID)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_LANGUAGE_BY_ID, notes = PathProxy.HappinessUrls.GET_LANGUAGE_BY_ID)
		public Response<Language> getLanguage(@PathVariable("id")String id)
		{
			
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Language> response = new Response<Language>();
		response.setData(happinessService.getLanguage(id));
		return response;
		}
		
		@GetMapping(value = PathProxy.HappinessUrls.GET_LANGUAGES)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_LANGUAGES, notes = PathProxy.HappinessUrls.GET_LANGUAGES)
		public Response<Language> getLanguages(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm) {
			Integer count = happinessService.countLanguage(discarded, searchTerm);
			Response<Language> response = new Response<Language>();
			if (count > 0)
				response.setDataList(happinessService.getLanguages(size, page, discarded, searchTerm));
			response.setCount(count);
			return response;
		}
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_LANGUAGE)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_LANGUAGE, notes = PathProxy.HappinessUrls.DELETE_LANGUAGE)
		public Response<Language> deleteLanguage(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<Language> response = new Response<Language>();
			response.setData(happinessService.deleteLanguage(id, discarded));
			return response;
		}
		
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY, notes = PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY)
		public Response<Activity> addEditActivity(@RequestBody Activity request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Activity> response = new Response<Activity>();
		response.setData(happinessService.addEditActivity(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_ACTIVITY)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_ACTIVITY, notes = PathProxy.HappinessUrls.DELETE_ACTIVITY)
		public Response<Activity> discardActivity(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<Activity> response = new Response<Activity>();
			response.setData(happinessService.deleteActivity(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_ACTIVITY)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_ACTIVITY, notes = PathProxy.HappinessUrls.GET_ACTIVITY)
		public Response<Activity> getActivity(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm,
				@QueryParam(value = "languageId") String languageId) {
			Integer count = happinessService.countActivity(discarded, searchTerm);
			Response<Activity> response = new Response<Activity>();
		
				response.setDataList(happinessService.getActivity(size, page, discarded, searchTerm,languageId));
			response.setCount(count);
			return response;
		}
		
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_STORIES)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_STORIES, notes = PathProxy.HappinessUrls.ADD_EDIT_STORIES)
		public Response<Stories> addEditStories(@RequestBody Stories request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Stories> response = new Response<Stories>();
		response.setData(happinessService.addEditStories(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_STORIES)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_STORIES, notes = PathProxy.HappinessUrls.DELETE_STORIES)
		public Response<Stories> discardStories(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<Stories> response = new Response<Stories>();
			response.setData(happinessService.deleteStories(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_STORIES)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_STORIES, notes = PathProxy.HappinessUrls.GET_STORIES)
		public Response<Stories> getStories(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm,
				@QueryParam(value = "languageId") String languageId
				) {
			Integer count = happinessService.countStories(discarded, searchTerm);
			Response<Stories> response = new Response<Stories>();
			
				response.setDataList(happinessService.getStories(size, page, searchTerm, discarded,languageId));
			response.setCount(count);
			return response;
		}
	 
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS, notes = PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS)
		public Response<Mindfulness> addEditMindfulness(@RequestBody Mindfulness request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Mindfulness> response = new Response<Mindfulness>();
		response.setData(happinessService.addEditMindfulness(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_MINDFULNESS)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_MINDFULNESS, notes = PathProxy.HappinessUrls.DELETE_MINDFULNESS)
		public Response<Mindfulness> discardMindfulness(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<Mindfulness> response = new Response<Mindfulness>();
			response.setData(happinessService.deleteMindfulness(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_MINDFULNESS)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_MINDFULNESS, notes = PathProxy.HappinessUrls.GET_MINDFULNESS)
		public Response<Mindfulness> getMindfulness(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm) {
			Integer count = happinessService.countMindfulness(discarded, searchTerm);
			Response<Mindfulness> response = new Response<Mindfulness>();
		
				response.setDataList(happinessService.getMindfulness(size, page, searchTerm, discarded));
			response.setCount(count);
			return response;
		}
	 
	 
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_TODAY_SESSION)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_TODAY_SESSION, notes = PathProxy.HappinessUrls.ADD_EDIT_TODAY_SESSION)
		public Response<TodaySession> addEditTodaySession(@RequestBody TodaySession request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<TodaySession> response = new Response<TodaySession>();
		response.setData(happinessService.addEditSession(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_TODAY_SESSION)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_TODAY_SESSION, notes = PathProxy.HappinessUrls.DELETE_TODAY_SESSION)
		public Response<TodaySession> discardTodaySession(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<TodaySession> response = new Response<TodaySession>();
			response.setData(happinessService.deleteSession(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_TODAY_SESSION)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_TODAY_SESSION, notes = PathProxy.HappinessUrls.GET_TODAY_SESSION)
		public Response<TodaySession> getTodaySession(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm) {
			Integer count = happinessService.countSession(discarded, searchTerm);
			Response<TodaySession> response = new Response<TodaySession>();

				response.setDataList(happinessService.getSession(size, page, searchTerm, discarded));
			response.setCount(count);
			return response;
		}
		
		@PostMapping(value = PathProxy.HappinessUrls.UPLOAD_IMAGE , consumes = { MediaType.MULTIPART_FORM_DATA})
		@ApiOperation(value = PathProxy.HappinessUrls.UPLOAD_IMAGE , notes = PathProxy.HappinessUrls.UPLOAD_IMAGE)
		public Response<Mindfulness> saveImage(@RequestParam(value = "file") MultipartFile file) {
			
			Mindfulness mindfulness = happinessService.uploadImage(file);
			//imageURL = getFinalImageURL(imageURL);
//			if (mindfulness != null) {
//			
//				ESExerciseDocument document = new ESExerciseDocument();
//				BeanUtil.map(exerciseURL, document);
//				esRecipeService.uploadImage(document);
//			}
			Response<Mindfulness> response = new Response<Mindfulness>();
			response.setData(mindfulness);
			return response;
		}

		@PostMapping(value = PathProxy.HappinessUrls.UPLOAD_VIDEO, consumes = MediaType.MULTIPART_FORM_DATA)
		@ApiOperation(value = PathProxy.HappinessUrls.UPLOAD_VIDEO, notes = PathProxy.HappinessUrls.UPLOAD_VIDEO)
		public Response<Mindfulness> saveVideo(@RequestParam(value = "file") MultipartFile file) {
			
			Mindfulness mindfulness = happinessService.uploadVideo(file);
			//imageURL = getFinalImageURL(imageURL);
//			if (exerciseURL != null) {
//				
//				ESExerciseDocument document = new ESExerciseDocument();
//				BeanUtil.map(exerciseURL, document);
//				esRecipeService.uploadVideo(document);
//			}
			Response<Mindfulness> response = new Response<Mindfulness>();
			response.setData(mindfulness);
			return response;
		}
		
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY_ASSIGN, notes = PathProxy.HappinessUrls.ADD_EDIT_ACTIVITY_ASSIGN)
		public Response<ActivityAssign> addEditActivityAssign(@RequestBody ActivityAssign request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<ActivityAssign> response = new Response<ActivityAssign>();
		response.setData(happinessService.addEditActivityAssign(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_ACTIVITY_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_ACTIVITY_ASSIGN, notes = PathProxy.HappinessUrls.DELETE_ACTIVITY_ASSIGN)
		public Response<ActivityAssign> discardActivityAssign(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<ActivityAssign> response = new Response<ActivityAssign>();
			response.setData(happinessService.deleteActivityAssign(id,discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_ACTIVITY_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_ACTIVITY_ASSIGN, notes = PathProxy.HappinessUrls.GET_ACTIVITY_ASSIGN)
		public Response<ActivityAssign> getAssignActivities(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm
			
				) {
			
			Response<ActivityAssign> response = new Response<ActivityAssign>();
			
				response.setDataList(happinessService.getActivityAssign(size, page, searchTerm, discarded));
			
			return response;
		}
		
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_STORIES_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_STORIES_ASSIGN, notes = PathProxy.HappinessUrls.ADD_EDIT_STORIES_ASSIGN)
		public Response<StoriesAssign> addEditStoriesAssign(@RequestBody StoriesAssign request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<StoriesAssign> response = new Response<StoriesAssign>();
		response.setData(happinessService.addEditStoriesAssign(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_STORIES_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_STORIES_ASSIGN, notes = PathProxy.HappinessUrls.DELETE_STORIES_ASSIGN)
		public Response<StoriesAssign> discardStoriesAssign(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<StoriesAssign> response = new Response<StoriesAssign>();
			response.setData(happinessService.deleteStoriesAssign(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_STORIES_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_STORIES_ASSIGN, notes = PathProxy.HappinessUrls.GET_STORIES_ASSIGN)
		public Response<StoriesAssign> getStoriesAssign(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm
			
				) {
			
			Response<StoriesAssign> response = new Response<StoriesAssign>();
			
				response.setDataList(happinessService.getStoriesAssign(size, page, searchTerm, discarded));
			
			return response;
		}
	 
		@PostMapping(value=PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS_ASSIGN, notes = PathProxy.HappinessUrls.ADD_EDIT_MINDFULNESS_ASSIGN)
		public Response<MindfulnessAssign> addEditMindfulnessAssign(@RequestBody MindfulnessAssign request)
		{
			
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<MindfulnessAssign> response = new Response<MindfulnessAssign>();
		response.setData(happinessService.addEditMindfulnessAssign(request));
		return response;
		}
		
		
		@DeleteMapping(value = PathProxy.HappinessUrls.DELETE_MINDFULNESS_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.DELETE_MINDFULNESS_ASSIGN, notes = PathProxy.HappinessUrls.DELETE_MINDFULNESS_ASSIGN)
		public Response<MindfulnessAssign> discardMindfulnessAssign(@PathVariable("id") String id,
				@RequestParam(required = false, value ="discarded", defaultValue = "true") Boolean discarded) {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

			}
			Response<MindfulnessAssign> response = new Response<MindfulnessAssign>();
			response.setData(happinessService.deleteMindfulnessAssign(id, discarded));
			return response;
		}
	 
		@GetMapping(value = PathProxy.HappinessUrls.GET_MINDFULNESS_ASSIGN)
		@ApiOperation(value = PathProxy.HappinessUrls.GET_MINDFULNESS_ASSIGN, notes = PathProxy.HappinessUrls.GET_MINDFULNESS_ASSIGN)
		public Response<MindfulnessAssign> getMindfulnessAssign(@RequestParam(required = false, value ="size", defaultValue = "0") int size, 
				@RequestParam(required = false, value ="page", defaultValue = "0") int page,
				@RequestParam(required = false, value ="discarded") Boolean discarded, 
				@RequestParam(required = false, value ="searchTerm") String searchTerm
			
				) {
			
			Response<MindfulnessAssign> response = new Response<MindfulnessAssign>();
			
				response.setDataList(happinessService.getMindfulnessAssign(size, page, searchTerm, discarded));
			
			return response;
		}
	 
}
