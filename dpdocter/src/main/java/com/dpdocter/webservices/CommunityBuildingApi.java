package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.Feeds;
import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.Forum;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CommunityBuildingServices;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.COMMUNITY_BUILDING_BASE_URL, consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.COMMUNITY_BUILDING_BASE_URL, description = "Endpoint for Community Building")
public class CommunityBuildingApi {

	private static Logger logger = LogManager.getLogger(CommunityBuildingApi.class.getName());
	
	@Autowired
	private CommunityBuildingServices communityBuildingServices;
	
	@PostMapping(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FEEDS)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FEEDS, notes = PathProxy.CommunityBuildingUrls.ADD_EDIT_FEEDS)
	public Response<FeedsResponse> addEditFeeds(@RequestBody FeedsRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		

		Response<FeedsResponse> response = new Response<FeedsResponse>();
		response.setData(communityBuildingServices.addEditPost(request));
		return response;
	}

	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_FEEDS)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FEEDS, notes = PathProxy.CommunityBuildingUrls.GET_FEEDS)
	public Response<Feeds> getFeeds(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value = "languageId") String languageId,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<Feeds> response = new Response<Feeds>();
		response.setDataList(communityBuildingServices.getFeeds(size, page, discarded, type, languageId,searchTerm));
		return response;
	}
	
	@PostMapping(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM, notes = PathProxy.CommunityBuildingUrls.ADD_EDIT_FORUM)
	public Response<ForumResponse> addEditForum(@RequestBody ForumRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		

		Response<ForumResponse> response = new Response<ForumResponse>();
		response.setData(communityBuildingServices.addEditForumResponse(request));
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES, notes = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSES)
	public Response<Forum> getForumResponse(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<Forum> response = new Response<Forum>();
		response.setDataList(communityBuildingServices.getForumResponse(page, size, searchTerm, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_ARTICLES)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_ARTICLES, notes = PathProxy.CommunityBuildingUrls.GET_ARTICLES)
	public Response<FeedsResponse> getLearningSession(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "languageId") String languageId,
			@RequestParam(required = false, value = "type") String type,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<FeedsResponse> response = new Response<FeedsResponse>();
		response.setDataList(communityBuildingServices.getLearningSession(page, size, discarded, searchTerm, languageId,type));
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID, notes = PathProxy.CommunityBuildingUrls.GET_FORUM_RESPONSE_BY_ID)
	public Response<ForumResponse> getForumResponseById(
			@PathVariable(value = "id") String id,
			@RequestParam(required = false, value = "languageId") String languageId,
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<ForumResponse> response = new Response<ForumResponse>();
		response.setData(communityBuildingServices.getForumResponseById(id));
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_FEEDS_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_FEEDS_BY_ID, notes = PathProxy.CommunityBuildingUrls.GET_FEEDS_BY_ID)
	public Response<FeedsResponse> getArticlesById(@PathVariable(value = "id") String id,
			@RequestParam(required = false, value = "languageId") String languageId) {
		Response<FeedsResponse> response = new Response<FeedsResponse>();
		response.setData(communityBuildingServices.getArticleById(id, languageId));
		return response;
	}
	
	@DeleteMapping(value = PathProxy.CommunityBuildingUrls.DELETE_FORUM_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.DELETE_FORUM_BY_ID, notes = PathProxy.CommunityBuildingUrls.DELETE_FORUM_BY_ID)
	public Response<Boolean> deleteForumById(@RequestParam(required = false, value = "discarded",defaultValue = "true")Boolean discarded,
			@RequestParam(required = false, value = "id") String id) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(communityBuildingServices.deleteForumResponseById(id,discarded));
		return response;
	}
	
	@DeleteMapping(value = PathProxy.CommunityBuildingUrls.DELETE_FEEDS_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.DELETE_FEEDS_BY_ID, notes = PathProxy.CommunityBuildingUrls.DELETE_FEEDS_BY_ID)
	public Response<FeedsResponse> deleteFeeds(@RequestParam(required = false, value = "discarded")Boolean discarded,
			@RequestParam(required = false, value = "id") String id) {
		Response<FeedsResponse> response = new Response<FeedsResponse>();
		response.setData(communityBuildingServices.deleteFeedsById(id,discarded));
		return response;
	}
	
	@DeleteMapping(value = PathProxy.CommunityBuildingUrls.DELETE_COMMENT_BY_ID)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.DELETE_COMMENT_BY_ID, notes = PathProxy.CommunityBuildingUrls.DELETE_COMMENT_BY_ID)
	public Response<Comment> deleteComment(@RequestParam(required = false,value = "id") String id,
			@RequestParam(required = false, value = "discarded") Boolean discarded) {
		Response<Comment> response = new Response<Comment>();
		response.setData(communityBuildingServices.deleteCommentsById(id, discarded));
		return response;
	}
	
	@GetMapping(value = PathProxy.CommunityBuildingUrls.GET_COMMENTS)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.GET_COMMENTS, notes = PathProxy.CommunityBuildingUrls.GET_COMMENTS)
	public Response<Comment> getComment(@RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "feedId") String feedId,
			
			@RequestParam(required = false, value ="discarded", defaultValue = "false") boolean discarded) {
		Response<Comment> response = new Response<Comment>();
		response.setDataList(communityBuildingServices.getComments(size, page, discarded,searchTerm, feedId));
		return response;
	}
	
	@PostMapping(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS)
	@ApiOperation(value = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS, notes = PathProxy.CommunityBuildingUrls.ADD_EDIT_COMMENTS)
	public Response<Comment> addeditComments(@RequestBody CommentRequest request) {
		Response<Comment> response = new Response<Comment>();
		Comment feeds=communityBuildingServices.addEditComment(request);
		response.setData(feeds);
		return response;
	}
	
	
}
