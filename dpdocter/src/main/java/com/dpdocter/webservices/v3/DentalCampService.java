package com.dpdocter.webservices.v3;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CommunicationDoctorTeamRequest;
import com.dpdocter.request.CampMsgTemplateRequest;
import com.dpdocter.request.CampNameRequest;
import com.dpdocter.request.CampaignNameRequest;
import com.dpdocter.request.CampaignObjectRequest;
import com.dpdocter.request.DentalCampBroadcastTemplateRequest;
import com.dpdocter.request.DentalCampRequest;
import com.dpdocter.request.DentalCampTreatmentNameRequest;
import com.dpdocter.request.FollowupCommunicationRequest;
import com.dpdocter.response.CampMsgTemplateResponse;
import com.dpdocter.response.CampNameResponse;
import com.dpdocter.response.CampaignNameResponse;
import com.dpdocter.response.CampaignObjectResponse;
import com.dpdocter.response.DentalCampResponse;
import com.dpdocter.response.DentalChainFile;
import com.dpdocter.response.FollowupCommunicationResponse;

import common.util.web.Response;

public interface DentalCampService {

	CampNameResponse addEditCampName(CampNameRequest request);

	Response<Object> getCampNames(int size, int page, String searchTerm, Boolean isDiscarded, Boolean isCamp);

	Boolean deleteCampName(String campNameId, Boolean isDiscarded);

	CampNameResponse getCampNameById(String campNameId);

	DentalCampResponse addEditDentalCamp(DentalCampRequest request);

	Response<Object> getDentalCamps(List<String> treatmentId, List<String> associateDoctorId,String salaryRange, String campName, int size, int page,
			String locality, String language, String city, String leadType, String leadStage, String profession,
			Boolean isPatientCreated, String isPhotoUpload, String gender, String followupType, String age,
			String complaint, String dateFilterType, String fromDate, String toDate, String searchTerm,
			Boolean isDiscarded, Boolean isMobileNumberPresent,String smileBuddyId, String campaignId);

	Boolean deleteDentalCampsById(String campId, Boolean isDiscarded);

	DentalCampResponse getDentalCampsById(String campId);

	DentalCampTreatmentNameRequest addEditDentalCampTreatmentName(DentalCampTreatmentNameRequest request);

	Response<Object> getDentalCampsTreatmentList(int size, int page, String type, String searchTerm,
			Boolean isDiscarded);

	Boolean deleteCampMsgTemplate(String campNameId, Boolean isDiscarded);

	Response<Object> getCampMsgTemplate(int size, int page, String searchTerm, Boolean isDiscarded,
			String broadcastType);

	CampMsgTemplateResponse addEditCampMsgTemplate(CampMsgTemplateRequest request);

	DentalChainFile uploadImage(MultipartFile file);

	DentalChainFile uploadVideo(MultipartFile file);

	Boolean deleteTreatmentName(String id, Boolean isDiscarded);

	Boolean broadcastTemplateToLeads(DentalCampBroadcastTemplateRequest request);

	FollowupCommunicationRequest getFollowupByUserId(String userId);

	List<FollowupCommunicationRequest> getFollowupDetailList(int size, int page, String searchTerm);

	Integer countDetailsList(String searchTerm);

	FollowupCommunicationRequest addEditFollowupCommunication(FollowupCommunicationRequest request);

	Boolean broadcastTemplateToDoctors(DentalCampBroadcastTemplateRequest request);

	Boolean broadcastTemplateToPatients(DentalCampBroadcastTemplateRequest request);

	Response<Object> getDentalCampsCount(List<String> treatmentId, List<String> associateDoctorId,String salaryRange, String campName, int size,
			int page, String locality, String language, String city, String leadType, String leadStage,
			String profession, Boolean isPatientCreated, String isPhotoUpload, String gender, String followupType,
			String age, String complaint, String dateFilterType, String fromDate, String toDate, String searchTerm,
			Boolean isDiscarded, Boolean isMobileNumberPresent);

	CampaignNameResponse addEditCampaignName(CampaignNameRequest request);

	Response<Object> getCampaignNames(int size, int page, String searchTerm, Boolean isDiscarded);

	Boolean deleteCampaignName(String campNameId, Boolean isDiscarded);

	CampaignObjectResponse addEditCampaignObject(CampaignObjectRequest request);

	Response<Object> getCampaignObjects(String campaignId, int size, int page, Boolean isDiscarded);

	Boolean deleteCampaignObject(String campNameId, Boolean isDiscarded);

	DentalChainFile uploadAudio(MultipartFile file);

	List<FollowupCommunicationResponse> getFollowupList(String smileBuddyId, List<String> treatmentId, String fromDate, String toDate, int size, int page, String searchTerm);

}
