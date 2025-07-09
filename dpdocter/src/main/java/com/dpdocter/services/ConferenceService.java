package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.RegisteredUser;
import com.dpdocter.beans.ResearchPaperCategory;
import com.dpdocter.beans.ResearchPaperSubCategory;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.request.ConferenceBulkSMSRequest;
import com.dpdocter.request.ConfexAdminRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.SessionDateResponse;

public interface ConferenceService {
	public SessionTopic addEditTopic(SessionTopic request);

	public List<SessionTopic> getTopics(int size, int page, boolean discarded, String searchTerm);

	public SessionTopic deleteTopic(String id, boolean discarded);

	public SessionTopic getTopic(String id);

	public SpeakerProfile addEditSpeakerProfile(SpeakerProfile request);

	public List<SpeakerProfile> getSpeakerProfiles(int size, int page, boolean discarded, String searchTerm);

	public SpeakerProfile deleteSpeakerProfile(String id, boolean discarded);

	public SpeakerProfile getSpeakerProfile(String id);

	public DoctorConferenceSession addEditConferenceSession(DoctorConferenceSession request);

	public List<DoctorConferenceSession> getConferenceSessions(int size, int page, boolean discarded, String city,
			Integer fromtime, Integer toTime, String fromDate, String toDate, String searchTerm, String conferenceId,
			List<String> topics);

	public DoctorConferenceSession deleteConferenceSession(String id, boolean discarded);

	public DoctorConferenceSession getConferenceSession(String id);

	public DoctorConference addEditDoctorConference(DoctorConference request);

	public List<DoctorConference> getDoctorConference(int size, int page, boolean discarded, String city,
			String speciality, String fromDate, String toDate, String searchTerm);

	public DoctorConference deleteDoctorConference(String id, boolean discarded);

	public DoctorConference getDoctorConference(String id);

	public DoctorConferenceAgenda addEditConferenceAgenda(DoctorConferenceAgenda request);

	public List<DoctorConferenceAgenda> getConferenceAgenda(int size, int page, boolean discarded, String fromDate,
			String toDate, String searchTerm, String conferenceId);

	public DoctorConferenceAgenda deleteConferenceAgenda(String id, boolean discarded);

	public DoctorConferenceAgenda getConferenceAgenda(String id);

	public String uploadImages(FileDetails request, String module);

	public List<SessionDateResponse> getConferenceSessionDate(String conferenceId);

	public DoctorConference addConferenceAdmin(ConfexAdminRequest request);

	public Boolean activeConferenceAdmin(String userId);

	public Boolean sendEmailSetPassword(String userId);

	public Boolean resendVerificationSMS(String userId, String mobileNumber);

	public Boolean resetPassword(ResetPasswordRequest request);

	public Boolean verifyAdmin(String userId);

	public Boolean resetPasswordSMS(String userId, String mobileNumber);

	public DoctorConference updateStatus(String id, String status);
	
	public ResearchPaperCategory addCategory(ResearchPaperCategory request);

	public List<ResearchPaperCategory> getCategoryList(int page, int size, String conferenceId, String searchTerm,
			Boolean discarded);

	public ResearchPaperCategory getCategory(String id);

	public Boolean discardCategory(String id, Boolean discarded);

	public ResearchPaperSubCategory addSubCategory(ResearchPaperSubCategory request);

	public List<ResearchPaperSubCategory> getSubCategoryList(int page, int size, String conferenceId,
			String searchTerm, Boolean discarded);

	public ResearchPaperSubCategory getSubCategory(String id);

	public Boolean discardSubCategory(String id, Boolean discarded);
	
	public List<RegisteredUser> getRegisterUser(int size, int page, String fromDate, String toDate,
			Boolean paymentStatus, String conferenceId, String searchTerm);
	
	public Integer countRegisterUser(String fromDate, String toDate, Boolean paymentStatus, String conferenceId,
			String searchTerm);
	public Boolean sendBulkSMS(ConferenceBulkSMSRequest request);
	
}
