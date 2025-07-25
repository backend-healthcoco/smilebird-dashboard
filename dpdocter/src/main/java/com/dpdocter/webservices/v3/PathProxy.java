package com.dpdocter.webservices.v3;

public interface PathProxy {

	public static final String HOME_URL = "/api";

	public static final String BASE_URL = HOME_URL + "/v3";

	public static final String APPOINTMENT_BASE_URL = BASE_URL + "/appointment";

	public interface AppointmentUrls {

		public static final String ADD_CITY = "/city/add";

		public static final String ACTIVATE_DEACTIVATE_CITY = "/activateCity/{cityId}";

		public static final String GET_CITY = "/cities";

		public static final String GET_LANDMARK_LOCALITY = "/landmarkLocality/{cityId}";

		public static final String GET_COUNTRIES = "/countries";

		public static final String GET_STATES = "/states";

		public static final String GET_CITY_ID = "/getCity/{cityId}";

		public static final String ADD_LANDMARK_LOCALITY = "/landmarkLocality/add";

		public static final String ADD_ZONE = "/zone/add";

		public static final String DELETE_CITY_BY_ID = "/delete/city/{cityId}";

		public static final String DELETE_LOCALITY_BY_ID = "/delete/locality/{localityId}";

		public static final String GET_CLINIC = "/clinic/{locationId}";

		public static final String GET_TIME_SLOTS = "getTimeSlots/{doctorId}/{locationId}/{date}";

		public static final String GET_TIME_SLOTS_WEB = "web/getTimeSlots/{doctorId}/{locationId}/{date}";

		public static final String DENTAL_CHAIN_GET_APPOINTMENT_DETAIL = "/dentalChain/patientDetail/add";

		public static final String DENTAL_CHAIN_BOOK_APPOINTMENT = "/dentalChain/book";

		public static final String GET_DENTAL_CHAIN_APPOINTMENT = "/dentalChain/getList";

		public static final String UPDATE_DENTAL_CHAIN_APPOINTMENT_STATE = "/dentalChain/updateState/{appointmentId}";

		public static final String UPDATE_DENTAL_CHAIN_APPOINTMENT_STATUS = "/dentalChain/updateStatus/{appointmentId}";

		public static final String GET_USERS = "/admin/getUsers";

		public static final String GET_USERS_COUNT = "/admin/getUsersCount";

		public static final String GET_DOCTORS_COUNT = "/admin/getDoctorsCount";

		public static final String GET_PATIENT_LAST_APPOINTMENT = "/patient/last/{patientId}";

		public static final String GET_PATIENT_TIMELINE_HISTORY = "/patient/timelineHistory/{patientId}";

		public static final String GET_DENTAL_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID = "/campMsgTemplate/getList";

		public static final String BROADCAST_CAMP_MSG_TEMPLATE_BY_TREATMENT_ID = "/campMsgTemplate/broadcast";

	}

	public static final String OTP_BASE_URL = BASE_URL + "/otp";

	public interface OTPUrls {

		public static final String DENTAL_CHAIN_OTP_GENERATOR = "/dentalChain/generate";

	}

	public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";

	public interface RegistrationUrls {
		public static final String PATIENTS_BY_PHONE_NUM = "/patients/{mobileNumber}";

		public static final String ADD_PATIENTS = "/patients/add";

		public static final String UPDATE_DENTAL_CHAIN_PATIENT_CONTACT_STATE = "/dentalChain/updatePatientContactState/{patientId}";

		public static final String UPDATE_PATIENT_FOLLOW_UP = "/dentalChain/updatePatientFollowUp/{patientId}";

	}

	public static final String ADMIN_BASE_URL = BASE_URL + "/admin";

	public interface AdminUrls {

		public static final String GET_CLINICS_AND_LABS = "/clinics";

		public static final String UPDATE_DENTALCHAIN_STATE_OF_CLINIC = "/updateDentalChainStateOfClinic/{locationId}";

		public static final String UPDATE_PATIENT_VERIFY = "/patient/{patientId}";

		public static final String ADD_EDIT_DENTAL_TREAMENT_DETAIL = "/dentalChainTreatment/addEdit";

		public static final String DELETE_DENTAL_TREAMENT_DETAIL = "/dentalChainTreatment/delete/{id}";

		public static final String GET_DENTAL_TREAMENT_DETAIL = "/dentalChainTreatment/getList";

		public static final String GET_DENTAL_TREAMENT_NAMES = "/dentalChainTreatment/names/getList";

		public static final String GET_DENTAL_ADMIN_NAME = "/getDentalAdminNames";

		public static final String EDIT_LOCATION_SLUG_URL = "/editLocationSlugUrl";

		public static final String ADD_EDIT_DENTAL_REASONS = "/dentalReasons/addEdit";

		public static final String GET_DENTAL_REASONS = "/dentalReasons/getList";

		public static final String DELETE_DENTAL_REASONS = "/dentalReasons/delete/{reasonId}";
		
		public static final String GET_USER_PROFILE = "/getUserProfile";
		
		public static final String ADD_EDIT_CLINIC_CHARGE_CODES = "/clinicChargeCode/addEdit";

	}

	public static final String DENTAL_CAMP_BASE_URL = BASE_URL + "/dentalCamp";

	public interface DentalCampUrls {

		public static final String UPLOAD_IMAGE = "/uploadImage";

		public static final String UPLOAD_VIDEO = "/uploadVideo";

		public static final String UPLOAD_AUDIO = "/uploadAudio";

		public static final String ADD_EDIT_DENTAL_CAMP_NAME = "/campName/addEdit";

		public static final String GET_DENTAL_CAMP_NAME = "/campName/getList";

		public static final String DELETE_DENTAL_CAMP_NAME = "/campName/delete/{campNameId}";

		public static final String GET_DENTAL_CAMP_NAME_BY_ID = "/campName/getbyId/{campNameId}";

		public static final String ADD_EDIT_DENTAL_CAMP = "/addEdit";

		public static final String GET_DENTAL_CAMP = "/getList";

		public static final String GET_DENTAL_LEADS_COUNT = "/getCount";

		public static final String DELETE_DENTAL_CAMP = "/delete/{campId}";

		public static final String GET_DENTAL_CAMP_BY_ID = "/getbyId/{campId}";

		public static final String GET_DENTAL_CAMP_TREATMENT_LIST = "/treatmentname/getList";
		public static final String ADD_EDIT_DENTAL_CAMP_TREATMENT_NAME = "/treatmentname/addEdit";
		public static final String DELETE_DENTAL_CAMP_TREATMENT_NAME = "/treatmentname/delete/{id}";

		public static final String ADD_EDIT_CAMP_MSG_TEMPLATE = "/campMsgTemplate/addEdit";

		public static final String GET_DENTAL_CAMP_MSG_TEMPLATE = "/campMsgTemplate/getList";

		public static final String DELETE_CAMP_MSG_TEMPLATE = "/campMsgTemplate/delete/{templateId}";

		public static final String BROADCAST_CAMP_MSG_TEMPLATE = "/broadcast";

		public static final String ADD_EDIT_FOLLOWUP_COMMUNICATION = "/followup/addEdit";
		public static final String GET_FOLLOWUP_COMMUNICATION_DETAILS_LIST = "/followup/get";
		public static final String GET_FOLLOWUP_COMMUNICATION_DETAILS_GET_BY_USERID = "/followup/{userId}/getByUserId";

		public static final String ADD_EDIT_DENTAL_CAMPAIGN_NAME = "/campaign/addEdit";

		public static final String ADD_EDIT_CAMPAIGN_OBJECT = "/campaignObject/addEdit";

		public static final String GET_CAMPAIGN_OBJECT = "/campaignObject/getList/{campaignId}";

		public static final String DELETE_CAMPAIGN_OBJECT = "/campaignObject/delete/{campaignObjectId}";

		public static final String GET_DENTAL_CAMPAIGN_NAME = "/campaign/getList";

		public static final String DELETE_DENTAL_CAMPAIGN_NAME = "/campaign/delete/{campaignId}";

		public static final String UPDATE_LEAD_TYPE_REASON = "/leadType/reason/{leadId}";

		public static final String GET_FOLLOWUP_DETAILS_LIST = "/followup/getList";

	}

	public static final String ANALYTICS_BASE_URL = BASE_URL + "/analytics";

	public interface AnalyticsUrls {
		public static final String GET_PATIENT_ANALYTICS_DATA = "/patient";
		public static final String GET_LEADS_ANALYTICS_DATA = "/leads";
		public static final String GET_APPOINTMENT_ANALYTICS_DATA = "/appointment";
		public static final String GET_CAMPAIGN_ANALYTICS_DATA = "/campaign";
		public static final String GET_ANALYTICS_DATA = "/detail";
		public static final String GET_MONTHLY_ANALYTICS_DATA = "/monthlyAnalytics";
		
		public static final String GET_CHAT_GPT_RESPONSE = "/getChatGPT";

	}

	public static final String ALIGNERS_BASE_URL = BASE_URL + "/aligners";

	public interface AlignersUrls {
		public static final String UPLOAD_IMAGE = "/uploadImage";

		public static final String UPLOAD_VIDEO = "/uploadVideo";

		public static final String UPLOAD_PLY = "/uploadPly";

		public static final String ADD_EDIT_ALIGNER_PACKAGE = "/package/addEdit";

		public static final String GET_ALIGNER_PACKAGE = "/package/getList";

		public static final String GET_ALIGNER_PACKAGE_NAME = "/package/getNames";

		public static final String DELETE_ALIGNER_PACKAGE = "/package/delete/{packageId}";

		public static final String ADD_EDIT_ALIGNER_PLAN = "/plan/addEdit";

		public static final String GET_ALIGNER_PLAN = "/plan/getList/{packageId}";

		public static final String GET_ALIGNER_PLAN_NAME = "/plan/getNames/{packageId}";

		public static final String DELETE_ALIGNER_PLAN = "/plan/delete/{planId}";

		public static final String ADD_EDIT_ASSIGN_PLAN_TO_USER = "/plan/assignToUser";

		public static final String DELETE_ASSIGN_PLAN_TO_USER = "/plan/deleteAssignPlan";

		public static final String GET_CHANGE_STATUS_OF_PLAN = "/plan/changeStatusOfPlan/{planId}";

		public static final String ADD_VIDEOS_TO_PLAN = "/plan/addVideosToPlan";

		public static final String ADD_MAKEOVER_VISUALS = "/plan/addMakeoverVisuals";

		public static final String ADD_MAKEOVER_IMAGES = "/plan/addMakeoverImages";

		public static final String GET_USER_ALIGNER_PLAN = "/plan/getUserAlignerPlan/{userId}";

		public static final String ADD_EDIT_DELIVERY_DETAIL_OF_PLAN = "/plan/addEditDeliveryDetailOfPlan";

		public static final String ADD_IPR_ATTACHMENT_DETAIL = "/addAlignerIPRAndAttachmetDetail";

	}

	public static final String FAQS_BASE_URL = BASE_URL + "/faqs";

	public interface FaqsUrls {
		public static final String ADD_EDIT_FAQS = "/addEdit";
		public static final String GET_FAQS = "/getList";
		public static final String DELETE_FAQS = "/delete/{id}";

	}

}
