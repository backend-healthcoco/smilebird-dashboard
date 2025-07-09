package com.dpdocter.webservices.v3;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.AlignerIPRAndAttachmetDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.AlignerDeliveryDetailRequest;
import com.dpdocter.request.AlignerMakeoverImagesRequest;
import com.dpdocter.request.AlignerMakeoverVisualsRequest;
import com.dpdocter.request.AlignerPackageRequest;
import com.dpdocter.request.AlignerPlanRequest;
import com.dpdocter.request.AlignerUserPlanRequest;
import com.dpdocter.response.AlignerDeliveryDetailResponse;
import com.dpdocter.response.AlignerPackageResponse;
import com.dpdocter.response.AlignerPlanResponse;
import com.dpdocter.response.AlignerUserPlanResponse;
import com.dpdocter.response.DentalChainFile;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.ALIGNERS_BASE_URL, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ALIGNERS_BASE_URL, description = "Endpoint for Aligner")
public class AlignersV3API {
	private static Logger logger = LogManager.getLogger(AlignersV3API.class.getName());

	@Autowired
	AlignersV3Services alignersServices;

	@PostMapping(value = PathProxy.AlignersUrls.UPLOAD_IMAGE, consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.AlignersUrls.UPLOAD_IMAGE, notes = PathProxy.AlignersUrls.UPLOAD_IMAGE)
	public Response<DentalChainFile> saveImage(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = alignersServices.uploadImage(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.UPLOAD_VIDEO, consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = PathProxy.AlignersUrls.UPLOAD_VIDEO, notes = PathProxy.AlignersUrls.UPLOAD_VIDEO)
	public Response<DentalChainFile> saveVideo(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = alignersServices.uploadVideo(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.UPLOAD_PLY, consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = PathProxy.AlignersUrls.UPLOAD_PLY, notes = PathProxy.AlignersUrls.UPLOAD_PLY)
	public Response<DentalChainFile> uploadPly(@RequestParam(value = "file") MultipartFile file) {

		DentalChainFile mindfulness = alignersServices.uploadPly(file);
		Response<DentalChainFile> response = new Response<DentalChainFile>();
		response.setData(mindfulness);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PACKAGE)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PACKAGE, notes = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PACKAGE)
	public Response<AlignerPackageResponse> addEditAlignerPackage(@RequestBody AlignerPackageRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AlignerPackageResponse alignerPackageResponse = alignersServices.addEditAlignerPackage(request);

		Response<AlignerPackageResponse> response = new Response<AlignerPackageResponse>();
		response.setData(alignerPackageResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE, notes = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE)
	public Response<Object> getAlignerPackages(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = alignersServices.getAlignerPackages(size, page, searchTerm, isDiscarded);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE_NAME)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE_NAME, notes = PathProxy.AlignersUrls.GET_ALIGNER_PACKAGE_NAME)
	public Response<Object> getAlignerPackagesNames(
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Response<Object> response = alignersServices.getAlignerPackagesNames(searchTerm);
		return response;
	}

	@DeleteMapping(value = PathProxy.AlignersUrls.DELETE_ALIGNER_PACKAGE)
	@ApiOperation(value = PathProxy.AlignersUrls.DELETE_ALIGNER_PACKAGE, notes = PathProxy.AlignersUrls.DELETE_ALIGNER_PACKAGE)
	public Response<Boolean> deleteAlignerPackage(@PathVariable(value = "packageId") String packageId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(packageId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignersServices.deleteAlignerPackage(packageId, isDiscarded));
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PLAN, notes = PathProxy.AlignersUrls.ADD_EDIT_ALIGNER_PLAN)
	public Response<AlignerPlanResponse> addEditAlignerPlan(@RequestBody AlignerPlanRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AlignerPlanResponse alignerPlanResponse = alignersServices.addEditAlignerPlan(request);

		Response<AlignerPlanResponse> response = new Response<AlignerPlanResponse>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_ALIGNER_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_ALIGNER_PLAN, notes = PathProxy.AlignersUrls.GET_ALIGNER_PLAN)
	public Response<Object> getAlignerPlans(@PathVariable(value = "packageId") String packageId,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "false") Boolean isDiscarded,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size) {
		Response<Object> response = alignersServices.getAlignerPlans(packageId, size, page, searchTerm, isDiscarded);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_ALIGNER_PLAN_NAME)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_ALIGNER_PLAN_NAME, notes = PathProxy.AlignersUrls.GET_ALIGNER_PLAN_NAME)
	public Response<Object> getAlignerPlansNames(@PathVariable(value = "packageId") String packageId,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Response<Object> response = alignersServices.getAlignerPlansNames(packageId, searchTerm);
		return response;
	}

	@DeleteMapping(value = PathProxy.AlignersUrls.DELETE_ALIGNER_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.DELETE_ALIGNER_PLAN, notes = PathProxy.AlignersUrls.DELETE_ALIGNER_PLAN)
	public Response<Boolean> deleteAlignerPlan(@PathVariable(value = "planId") String planId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignersServices.deleteAlignerPlan(planId, isDiscarded));
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_EDIT_ASSIGN_PLAN_TO_USER)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_EDIT_ASSIGN_PLAN_TO_USER, notes = PathProxy.AlignersUrls.ADD_EDIT_ASSIGN_PLAN_TO_USER)
	public Response<AlignerUserPlanResponse> addEditAssignPlanToUser(@RequestBody AlignerUserPlanRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AlignerUserPlanResponse alignerPlanResponse = alignersServices.addEditAssignPlanToUser(request);

		Response<AlignerUserPlanResponse> response = new Response<AlignerUserPlanResponse>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_VIDEOS_TO_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_VIDEOS_TO_PLAN, notes = PathProxy.AlignersUrls.ADD_VIDEOS_TO_PLAN)
	public Response<Boolean> addVideosToPlan(@RequestParam(value = "videoUrl") String videoUrl,
			@RequestParam(value = "userId") String userId, @RequestParam(value = "planId") String planId) {
		Boolean alignerPlanResponse = alignersServices.addVideosToPlan(videoUrl, userId, planId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_MAKEOVER_VISUALS)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_MAKEOVER_VISUALS, notes = PathProxy.AlignersUrls.ADD_MAKEOVER_VISUALS)
	public Response<Boolean> addMakeoverVisuals(@RequestBody AlignerMakeoverVisualsRequest request) {
		Boolean alignerPlanResponse = alignersServices.addMakeoverVisuals(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_MAKEOVER_IMAGES)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_MAKEOVER_IMAGES, notes = PathProxy.AlignersUrls.ADD_MAKEOVER_IMAGES)
	public Response<Boolean> addMakeoverImages(@RequestBody AlignerMakeoverImagesRequest request) {
		Boolean alignerPlanResponse = alignersServices.addMakeoverImages(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_USER_ALIGNER_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_USER_ALIGNER_PLAN, notes = PathProxy.AlignersUrls.GET_USER_ALIGNER_PLAN)
	public Response<AlignerUserPlanResponse> getUserAlignerPlan(@PathVariable(value = "userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<AlignerUserPlanResponse> alignerPlanResponse = alignersServices.getUserAlignerPlan(userId);
		Response<AlignerUserPlanResponse> response = new Response<AlignerUserPlanResponse>();
		response.setDataList(alignerPlanResponse);
		return response;
	}

	@GetMapping(value = PathProxy.AlignersUrls.GET_CHANGE_STATUS_OF_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.GET_CHANGE_STATUS_OF_PLAN, notes = PathProxy.AlignersUrls.GET_CHANGE_STATUS_OF_PLAN)
	public Response<Boolean> changeStatusOfPlan(@PathVariable(value = "planId") String planId,
			@RequestParam(value = "status") String status) {
		Boolean alignerPlanResponse = alignersServices.changeStatusOfPlan(planId, status);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(alignerPlanResponse);
		return response;
	}

	@PostMapping(value = PathProxy.AlignersUrls.ADD_EDIT_DELIVERY_DETAIL_OF_PLAN)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_EDIT_DELIVERY_DETAIL_OF_PLAN, notes = PathProxy.AlignersUrls.ADD_EDIT_DELIVERY_DETAIL_OF_PLAN)
	public Response<AlignerDeliveryDetailResponse> addEditDeliveryDetailOfPlan(
			@RequestBody AlignerDeliveryDetailRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AlignerDeliveryDetailResponse alignerPlanResponse = alignersServices.addEditDeliveryDetailOfPlan(request);
		Response<AlignerDeliveryDetailResponse> response = new Response<AlignerDeliveryDetailResponse>();
		response.setData(alignerPlanResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.AlignersUrls.ADD_IPR_ATTACHMENT_DETAIL)
	@ApiOperation(value = PathProxy.AlignersUrls.ADD_IPR_ATTACHMENT_DETAIL, notes = PathProxy.AlignersUrls.ADD_IPR_ATTACHMENT_DETAIL)
	public Response<AlignerIPRAndAttachmetDetail> addAlignerIPRAndAttachmetDetail(
			@RequestBody AlignerIPRAndAttachmetDetail request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		AlignerIPRAndAttachmetDetail alignerPlanResponse = alignersServices.addAlignerIPRAndAttachmetDetail(request);
		Response<AlignerIPRAndAttachmetDetail> response = new Response<AlignerIPRAndAttachmetDetail>();
		response.setData(alignerPlanResponse);
		return response;
	}
}
