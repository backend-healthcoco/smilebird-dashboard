package com.dpdocter.webservices.v3;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.AlignerIPRAndAttachmetDetail;
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

import common.util.web.Response;

public interface AlignersV3Services {

	DentalChainFile uploadImage(MultipartFile file);

	DentalChainFile uploadVideo(MultipartFile file);

	DentalChainFile uploadPly(MultipartFile file);

	Boolean deleteAlignerPlan(String planId, Boolean isDiscarded);

	Response<Object> getAlignerPlans(String packageId, int size, int page, String searchTerm, Boolean isDiscarded);

	AlignerPlanResponse addEditAlignerPlan(AlignerPlanRequest request);

	Boolean deleteAlignerPackage(String packageId, Boolean isDiscarded);

	Response<Object> getAlignerPackages(int size, int page, String searchTerm, Boolean isDiscarded);

	AlignerPackageResponse addEditAlignerPackage(AlignerPackageRequest request);

	AlignerUserPlanResponse addEditAssignPlanToUser(AlignerUserPlanRequest request);

	Response<Object> getAlignerPlansNames(String packageId, String searchTerm);

	Response<Object> getAlignerPackagesNames(String searchTerm);

	Boolean addVideosToPlan(String videoUrl, String userId, String planId);

	Boolean addMakeoverVisuals(AlignerMakeoverVisualsRequest request);

	Boolean addMakeoverImages(AlignerMakeoverImagesRequest request);

	List<AlignerUserPlanResponse> getUserAlignerPlan(String userId);

	Boolean changeStatusOfPlan(String planId,String status);

	AlignerDeliveryDetailResponse addEditDeliveryDetailOfPlan(AlignerDeliveryDetailRequest request);

	AlignerIPRAndAttachmetDetail addAlignerIPRAndAttachmetDetail(AlignerIPRAndAttachmetDetail request);

}
