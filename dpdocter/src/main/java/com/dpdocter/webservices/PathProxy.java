package com.dpdocter.webservices;

/**
 * @author veeraj
 */
public interface PathProxy {

	public static final String HOME_URL = "/api";

	public static final String BASE_URL = HOME_URL + "/v1";

	public static final String SIGNUP_BASE_URL = BASE_URL + "/signup";

	public interface SignUpUrls {

		public static final String ADMIN_SIGNUP = "/admin";

		public static final String DOCTOR_SIGNUP = "/doctor";

		public static final String LOCALE_SIGNUP = "/locale";

		public static final String DOCTOR_SIGNUP_HANDHELD = "/doctorHandheld";

		public static final String DOCTOR_SIGNUP_HANDHELD_CONTINUE = "/doctorHandheldContinue";

		public static final String ACTIVATE_USER = "/activate/{userId}";

		public static final String ACTIVATE_LOCATION = "/activate/location/{locationId}";

		public static final String VERIFY_USER = "/verify/{tokenId}";

		public static final String CHECK_IF_USERNAME_EXIST = "/check-username-exists/{username}";

		public static final String CHECK_IF_MOBNUM_EXIST = "/check-mobnum-exists/{mobileNumber}";

		public static final String CHECK_MOBNUM_SIGNEDUP = "/{mobileNumber}/signedUp";

		public static final String CHECK_IF_EMAIL_ADDR_EXIST = "/check-email-exists/{emailaddress}";

		public static final String VERIFY_UNLOCK_PATIENT = "/patient/verifyorunlock";

		public static final String RESEND_VERIFICATION_EMAIL_TO_DOCTOR = "/resendVerificationEmail/{emailaddress}";

		public static final String RESEND_VERIFICATION_SMS = "/resendVerificationSMS/{mobileNumber}";

		public static final String SUBMIT_DOCTOR_CONTACT = "/submitDoctorContact";

		public static final String GET_DOCTOR_CONTACT_LIST = "/getDoctorContactList";

		public static final String GET_CLINIC_CONTACT_LIST = "/getClinicContactList";

		public static final String GET_LOCALE_CONTACT_LIST = "/getLocaleContactList";

		public static final String ACTIVATE_ADMIN = "/activate/admin/{userId}";

		public static final String SUBMIT_LOCALE_CANTACT_US = "/submitLocaleContactUs";

		public static final String EDIT_ADMIN_PROFILE = "/admin/edit";

		public static final String PHARMA_COMPANY_SIGNUP = "/pharmaCompany";

		public static final String SEND_PHARMA_PASSWORD_LINK = "/sendPharmaPasswordLink/{id}";

		public static final String SEND_DOCTOR_WELCOME_LINK = "/sendDoctorWelcomeLink/{id}";

		public static final String DELIVERY_BOY_SIGNUP = "/deliveryBoy";

	}

	public static final String LOGIN_BASE_URL = BASE_URL + "/login";

	public interface LoginUrls {
//		public static final String LOGIN_ADMIN = "/dashboard/{mobileNumber}";
		public static final String LOGIN_ADMIN = "/admin/{mobileNumber}";
	}

	public static final String CONTACTS_BASE_URL = BASE_URL + "/contacts";

	public interface ContactsUrls {

		public static final String DOCTOR_CONTACTS_DOCTOR_SPECIFIC = "/{type}";

		public static final String DOCTOR_CONTACTS_HANDHELD = "/handheld";

		public static final String BLOCK_CONTACT = "/{doctorId}/{patientId}/block";

		public static final String ADD_GROUP = "/group/add";

		public static final String EDIT_GROUP = "/group/{groupId}/update";

		public static final String GET_ALL_GROUPS = "/groups";

		public static final String DELETE_GROUP = "/group/{groupId}/delete";

		public static final String TOTAL_COUNT = "/totalcount";

		public static final String IMPORT_CONTACTS = "/importContacts";

		public static final String EXPORT_CONTACTS = "/exportContacts";

		public static final String ADD_GROUP_TO_PATIENT = "/patient/addgroup";

		public static final String SEND_APP_LINK = "/sendLink";

	}

	public static final String REGISTRATION_BASE_URL = BASE_URL + "/register";

	public interface RegistrationUrls {

		public static final String USER_REGISTER = "/user";

		public static final String ADD_REFERRENCE = "/referrence/add";

		public static final String DELETE_REFERRENCE = "/referrence/{referrenceId}/delete";

		public static final String GET_REFERRENCES = "/reference/{range}";

		public static final String GET_CLINIC_DETAILS = "/settings/getClinicDetails/{clinicId}";

		public static final String UPDATE_CLINIC_PROFILE = "/settings/updateClinicProfile";

		public static final String EXISTING_PATIENTS_BY_PHONE_NUM = "/existing_patients/{mobileNumber}/{doctorId}/{locationId}/{hospitalId}";

		public static final String EXISTING_PATIENTS_BY_PHONE_NUM_COUNT = "/existing_patients_count/{mobileNumber}";

		public static final String GET_PATIENT_PROFILE = "/getpatientprofile/{userId}";

		public static final String UPDATE_CLINIC_PROFILE_HANDHELD = "/settings/updateClinicProfileHandheld";

		public static final String UPDATE_CLINIC_SPECIALIZATION = "/settings/updateClinicSpecialization";

		public static final String UPDATE_CLINIC_ADDRESS = "/settings/updateClinicAddress";

		public static final String UPDATE_CLINIC_TIMING = "/settings/updateClinicTiming";

		public static final String UPDATE_CLINIC_TREATMENT_RATELIST = "/settings/updateClinicTreatmentRatelist";

		public static final String UPDATE_CLINIC_LAB_PROPERTIES = "/settings/updateLabProperties";

		public static final String CHANGE_CLINIC_LOGO = "/settings/changeClinicLogo";

		public static final String CHANGE_PHARMA_LOGO = "/settings/changePharmaLogo";

		public static final String ADD_CLINIC_IMAGE = "/settings/clinicImage/add";

		public static final String DELETE_CLINIC_IMAGE = "/settings/clinicImage/{locationId}/{counter}/delete";

		public static final String GET_BLOOD_GROUP = "/settings/bloodGroup";

		public static final String ADD_PROFESSION = "/settings/profession/add";

		public static final String GET_PROFESSION = "/settings/profession";

		public static final String ADD_ROLE = "/role/add";

		public static final String GET_ROLE = "/role/{range}";

		public static final String DELETE_ROLE = "/role/{roleId}/delete";

		public static final String GET_USERS = "/users";

		public static final String DELETE_USER = "/user/{userId}/{locationId}/delete";

		public static final String ADD_FEEDBACK = "/feedback/add";

		public static final String VISIBLE_FEEDBACK = "/feedback/visible/{feedbackId}";

		public static final String GET_DOCTOR_FEEDBACK = "/feedback";

		public static final String GET_PATIENT_STATUS = "/patientStatus/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String CLINIC_REGISTER = "/clinic";
	}

	public static final String FORGOT_PASSWORD_BASE_URL = BASE_URL + "/forgotPassword";

	public interface ForgotPasswordUrls {
		public static final String CHECK_LINK_IS_ALREADY_USED = "/checkLink/{userId}";

		public static final String FORGOT_PASSWORD_DOCTOR = "/forgotPasswordDoctor";

		public static final String FORGOT_PASSWORD_PATIENT = "/forgotPasswordPatient";

		public static final String RESET_PASSWORD_PATIENT = "/resetPasswordPatient";

		public static final String RESET_PASSWORD = "/resetPassword";

		public static final String FORGOT_USERNAME = "/forgot-username";

	}

	public static final String RESET_PASSWORD_BASE_URL = BASE_URL + "/resetPassword";

	public interface ResetPasswordUrls {
		public static final String RESET_PASSWORD_USER = "/resetPasswordUser";
	}

	public static final String DOCTOR_PROFILE_URL = BASE_URL + "/doctorProfile";

	public interface DoctorProfileUrls {

		public static final String ADD_EDIT_NAME = "/addEditName";

		public static final String ADD_EDIT_EXPERIENCE = "/addEditExperience";

		public static final String ADD_EDIT_CONTACT = "/addEditContact";

		public static final String ADD_EDIT_EDUCATION = "/addEditEducation";

		public static final String ADD_EDIT_SPECIALITY = "/addEditSpeciality";

		public static final String ADD_EDIT_ACHIEVEMENT = "/addEditAchievement";

		public static final String ADD_EDIT_PROFESSIONAL_STATEMENT = "/addEditProfessionalStatement";

		public static final String ADD_EDIT_REGISTRATION_DETAIL = "/addEditRegistrationDetail";

		public static final String ADD_EDIT_EXPERIENCE_DETAIL = "/addEditExperienceDetail";

		public static final String ADD_EDIT_PROFILE_PICTURE = "/addEditProfilePicture";

		public static final String ADD_EDIT_COVER_PICTURE = "/addEditCoverPicture";

		public static final String ADD_EDIT_PROFESSIONAL_MEMBERSHIP = "/addEditProfessionalMembership";

		public static final String ADD_EDIT_MEDICAL_COUNCILS = "/addEditMedicalCouncils";

		public static final String GET_MEDICAL_COUNCILS = "/getMedicalCouncils";

		public static final String DELETE_MEDICAL_COUNCILS = "/deleteMedicalCouncils";

		public static final String INSERT_PROFESSIONAL_MEMBERSHIPS = "/insertProfessionalMemberships";

		public static final String GET_PROFESSIONAL_MEMBERSHIPS = "/getProfessionalMemberships";

		public static final String ADD_EDIT_CLINIC_PROFILE = "/clinicProfile/addEdit";

		public static final String ADD_EDIT_APPOINTMENT_NUMBERS = "/clinicProfile/addEditAppointmentNumbers";

		public static final String ADD_EDIT_VISITING_TIME = "/clinicProfile/addEditVisitingTime";

		public static final String ADD_EDIT_CONSULTATION_FEE = "/clinicProfile/addEditConsultationFee";

		public static final String ADD_EDIT_APPOINTMENT_SLOT = "/clinicProfile/addEditAppointmentSlot";

		public static final String ADD_EDIT_GENERAL_INFO = "/clinicProfile/addEditGeneralInfo";

		public static final String GET_SPECIALITIES = "/getSpecialities";

		public static final String GET_EDUCATION_INSTITUTES = "/getEducationInstitutes";

		public static final String GET_DOCTOR_PROFILE = "/{doctorId}/view";

		public static final String GET_EDUCATION_QUALIFICATIONS = "/getEducationQualifications";

		public static final String ADD_EDIT_MULTIPLE_DATA = "/addEditMultipleData";

		public static final String ADD_EDIT_FACILITY = "clinicProfile/editFacility";

		public static final String ADD_EDIT_GENDER = "addEditGender";

		public static final String ADD_EDIT_DOB = "addEditDOB";

		public static final String ADD_EDIT_VERIFICATION = "addEditVerification/{doctorId}";

		public static final String ADD_MEDICAL_COUNCIL = "/medicalCouncil/add";

		public static final String ADD_EDUCATION_QUALIFICATION = "/educationQualification/add";

		public static final String ADD_PROFESSIONAL_MEMBERSHIP = "/professionalMembership/add";

		public static final String ADD_SPECIALITIES = "/specialities/add";

		public static final String ADD_EDUCATION_INSTITUDE = "/educationInstitude/add";

		public static final String DELETE_EDUCATION_INSTITUTE = "/educationInstitute/delete";

		public static final String UPLOAD_VERIFICATION_DOCUMENTS = "uploadVerificationDocument/{doctorId}";

		public static final String DELETE_PROFILE_IMAGE = "profilePicture/{doctorId}/delete";

		public static final String UPDATE_PACKAGE_TYPE = "updatePackageType";

		public static final String UPDATE_VACCINATION_MODULE = "updateVaccinationModule";

		public static final String UPDATE_KOISK = "updateKoisk";

		public static final String ADD_EDIT_SERVICES = "/addEditServices";

		public static final String ADD_CITY = "/city/add";

		public static final String ADD_MR_CODE = "/mrCode/add";

		public static final String GET_SERVICES = "/services";

		public static final String ADD_SERVICES = "/services/add";

		public static final String ADD_EDIT_FEEDBACK_URL = "/clinicProfile/addEditFeedbackURL";

		public static final String ADD_EDIT_NUTRITION_DETAILS = "/clinicProfile/addEditNutritionDetails";

		public static final String UPDATE_EXPERIENCE = "/updateExperience";

		public static final String ASSOCIATE_DEPARTMENTS_TO_DOCTOR = "/associateDepartments";

		public static final String ADD_EDIT_ONLINE_CONSULTATION_TIME = "/addEditOnlineConsultationTime";

		public static final String ADD_EDIT_ONLINE_CONSULTATION_INFO = "/addEditOnlineConsultationInfo";

		public static final String CHECK_MEDICAL_DOCUMENTS = "/checkMedicalDocuments";

		public static final String UPLOAD_REGISTRATION_DETAILS = "/uploadRegistrationDetails";

		public static final String VERIFY_CLINIC_DOCUMENT = "/verifyClinic";

		public static final String EDIT_DOCTOR_SLUG_URL = "/editDoctorSlugUrl";

		public static final String CHECK_BIRTHDAY_SMS = "/checkBirthdaySms";

		public static final String UPDATE_BIRTHDAY_SMS = "/updateBirthdaySms";

		public static final String UPDATE_SUPER_ADMIN = "/updateSuperAdmin";

		public static final String UPDATE_WELCOME_PATIENT_SMS = "/updateWelcomePatientSms";

		public static final String GET_TRANSACTION_SMS_REPORT = "/transaction/report/get";

		public static final String ADD_EDIT_ONLINE_CONSULTATION_SLOT = "/clinicProfile/addEditOnlineConsultationSlot";

		public static final String ADD_EDIT_DOCTOR_DETAILS = "/addeditDoctorDetails";

		public static final String UPDATE_TRANSACTIONAL_SMS = "/updateTransactionalSms";

		public static final String UPDATE_CLINICAL_IMAGE = "/updateClinicalImage";

	}

	public static final String ACCESS_CONTROL_BASE_URL = BASE_URL + "/accessControl";

	public interface AccessControlUrls {
		public static final String GET_ACCESS_CONTROLS = "getAccessControls/{roleOrUserId}/{locationId}/{hospitalId}";

		public static final String SET_ACCESS_CONTROLS = "setAccessControls";
	}

	public static final String PRINT_SETTINGS_BASE_URL = BASE_URL + "/printSettings";

	public interface PrintSettingsUrls {

		public static final String SAVE_SETTINGS_DEFAULT_DATA = "saveDefault";

		public static final String SAVE_PRINT_SETTINGS = "add";

		public static final String DELETE_PRINT_SETTINGS = "/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_PRINT_SETTINGS = "/{printFilter}/{doctorId}/{locationId}/{hospitalId}";

		public static final String ENBLE_SHOW_POWERED_BY = "/enable/{doctorId}/{locationId}/{hospitalId}/poweredby";
	}

	public static final String SOLR_CLINICAL_NOTES_BASEURL = BASE_URL + "/solr/clinicalNotes";

	public interface SolrClinicalNotesUrls {
		public static final String SEARCH_COMPLAINTS = "searchComplaints/{range}";

		public static final String SEARCH_DIAGNOSES = "searchDiagnoses/{range}";

		public static final String SEARCH_NOTES = "searchNotes/{range}";

		public static final String SEARCH_DIAGRAMS = "searchDiagrams/{range}";

		public static final String SEARCH_DIAGRAMS_BY_SPECIALITY = "searchDiagramsBySpeciality/{searchTerm}";

		public static final String SEARCH_INVESTIGATIONS = "searchInvestigations/{range}";

		public static final String SEARCH_OBSERVATIONS = "searchObservations/{range}";

	}

	public static final String SOLR_PRESCRIPTION_BASEURL = BASE_URL + "/solr/prescription";

	public interface SolrPrescriptionUrls {

		public static final String SEARCH_DRUG = "searchDrug/{range}";

		public static final String SEARCH_LAB_TEST = "searchLabTest/{range}";

		public static final String SEARCH_DIAGNOSTIC_TEST = "searchDiagnosticTest/{range}";
	}

	public static final String SOLR_REGISTRATION_BASEURL = BASE_URL + "/solr/registration";

	public interface SolrRegistrationUrls {
		public static final String SEARCH_PATIENT = "searchPatient/{doctorId}/{locationId}/{hospitalId}/{searchTerm}";

		public static final String SEARCH_PATIENT_ADV = "searchPatient";
	}

	public static final String APPOINTMENT_BASE_URL = BASE_URL + "/appointment";

	public interface AppointmentUrls {

		public static final String ADD_COUNTRY = "/country/add";

		public static final String ADD_STATE = "/state/add";

		public static final String ADD_CITY = "/city/add";

		public static final String ACTIVATE_DEACTIVATE_CITY = "/activateCity/{cityId}";

		public static final String GET_CITY = "/cities";

		public static final String GET_LANDMARK_LOCALITY = "/landmarkLocality/{cityId}";

		public static final String GET_COUNTRIES = "/countries";

		public static final String GET_STATES = "/states";

		public static final String GET_CITY_ID = "/getCity/{cityId}";

		public static final String ADD_LANDMARK_LOCALITY = "/landmarkLocality/add";

		public static final String ADD_ZONE = "/zone/add";
		public static final String GET_ZONE_BY_CITY_ID = "/zone/{cityId}";
		public static final String DELETE_ZONE_BY_ID = "/zone/{zoneId}/delete";
		public static final String GET_CLINIC = "/clinic/{locationId}";

		public static final String GET_LAB = "/lab/{locationId}";

		public static final String ADD_EDIT_EVENT = "/event";

		public static final String CANCEL_EVENT = "/event/{eventId}/{doctorId}/{locationId}/cancel";

		public static final String GET_PATIENT_APPOINTMENTS = "/patient";

		public static final String GET_TIME_SLOTS = "getTimeSlots/{doctorId}/{locationId}/{date}";

		public static final String SEND_REMINDER_TO_PATIENT = "/sendReminder/patient/{appointmentId}";

		public static final String ADD_PATIENT_IN_QUEUE = "/queue/add";

		public static final String REARRANGE_PATIENT_IN_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}/{patientId}/{appointmentId}/{sequenceNo}/rearrange";

		public static final String GET_PATIENT_QUEUE = "/queue/{doctorId}/{locationId}/{hospitalId}";

		public static final String CANCEL_APPOINTMENT = "/cancel/{appointmentId}";

		public static final String ADD_EDIT_CITY = "/city/addEdit";

		public static final String UPLOAD_LANDMARK_LOCALITY = "/landmarkLocality/upload";

		public static final String VERIFY_CLINIC_DOCUMENT = "/verifyClinic";

		public static final String DELETE_CITY_BY_ID = "/delete/city/{cityId}";

		public static final String DELETE_LOCALITY_BY_ID = "/delete/locality/{localityId}";

		// new appointment apis
		public static final String ADD_APPOINTMENT = "/admin/add";

		public static final String UPDATE_APPOINTMENT = "/admin/update";

		public static final String GET_APPOINTMENT = "/admin/get";

		public static final String GET_APPOINTMENT_WITH_SPECIALITY = "/admin/speciality/get";

		public static final String GET_PATIENTS = "/admin/getPatients";
		public static final String GET_USERS = "/admin/getUsers";

		public static final String GET_ONLINE_CONSULTATION_TIME_SLOTS = "getOnlineConsulationTimeSlots/{doctorId}/{date}";

		public static final String GET_APPOINTMENT_ID = "/{appointmentId}/view";

	}

	public static final String PATIENT_TREATMENT_BASE_URL = BASE_URL + "/treatment";

	public interface PatientTreatmentURLs {
		public static final String ADD_EDIT_SERVICE = "/service/add";

		public static final String DELETE_SERVICE = "/service/{treatmentServiceId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_SERVICES = "/{type}/{range}";

		public static final String ADD_EDIT_PRODUCT_SERVICE = "addEditProductService";

		public static final String ADD_EDIT_PRODUCT_SERVICE_COST = "addEditProductServiceCost";

		public static final String GET_PRODUCTS_AND_SERVICES = "getProductsAndServices";

		public static final String ADD_EDIT_PATIENT_TREATMENT = "addEditPatientTreatment";

		public static final String DELETE_PATIENT_TREATMENT = "deletePatientTreatment";

		public static final String GET_PATIENT_TREATMENT_BY_ID = "getPatientTreatment/{treatmentId}";

		public static final String GET_PATIENT_TREATMENTS = "getPatientTreatments";

		public static final String ADD_EDIT_TREATMENT_RATELIST = "/ratelist/addEdit";

		public static final String GET_TREATMENT_RATELIST = "/ratelist/getList";

		public static final String DELETE_TREATMENT_RATELIST = "/ratelist/delete/{rateListId}";

	}

	public static final String SOLR_CITY_BASE_URL = BASE_URL + "/solr/city";

	public interface SolrCityUrls {

		public static final String SEARCH_LOCATION = "searchLocation";
	}

	public static final String SMS_BASE_URL = BASE_URL + "/sms";

	public interface SMSUrls {

		public static final String SEND_SMS = "/send";

		public static final String GET_SMS_DETAILS = "/getDetails";

		public static final String GET_SMS_STATUS = "/getStatus";

		public static final String UPDATE_DELIVERY_REPORTS = "/updateDeliveryReports";

		public static final String ADD_NUMBER = "/addNumber/{mobileNumber}";

		public static final String DELETE_NUMBER = "/deleteNumber/{mobileNumber}";

		public static final String ADD_EDIT_SMS_FORMAT = "/format/add";

		public static final String GET_SMS_FORMAT = "/format/{doctorId}/{locationId}/{hospitalId}";

		public static final String SEND_WHATSAPP_MESSAGE = "/whatsapp/send";

	}

	public static final String EMAIL_TRACK_BASE_URL = BASE_URL + "/email";

	public interface EmailTrackUrls {

	}

	public static final String OTP_BASE_URL = BASE_URL + "/otp";

	public interface OTPUrls {

		public static final String OTP_GENERATOR = "/{doctorId}/{locationId}/{hospitalId}/{patientId}/generate";

		public static final String OTP_GENERATOR_MOBILE = "/{mobileNumber}";

		public static final String VERIFY_OTP = "/{doctorId}/{locationId}/{hospitalId}/{patientId}/{otpNumber}/verify";

		public static final String VERIFY_OTP_MOBILE = "/{mobileNumber}/{otpNumber}/verify";

		public static final String VERIFY_ADMIN_OTP = "/admin/{mobileNumber}/{otpNumber}/verify";

	}

	public static final String SOLR_APPOINTMENT_BASE_URL = BASE_URL + "/solr/appointment";

	public interface SolrAppointmentUrls {

		public static final String SEARCH = "/search";

		public static final String GET_DOCTORS = "/doctors";

		public static final String GET_LABS = "/labs";

		public static final String ADD_SPECIALITY = "/addSpecialization";

		public static final String SEND_SMS_TO_DOCTOR = "/smsToDoctor";

		public static final String SEND_SMS_TO_PHARMACY = "/smsToPharmacy";
	}

	public static final String SOLR_MASTER_BASE_URL = BASE_URL + "/solr/master";

	public interface SolrMasterUrls {

		public static final String SEARCH_REFERENCE = "/reference/{range}";

		public static final String SEARCH_DISEASE = "/disease/{range}";

		public static final String SEARCH_BLOOD_GROUP = "/bloodGroup";

		public static final String SEARCH_PROFESSION = "/profession";

		public static final String SEARCH_MEDICAL_COUNCIL = "/medicalCouncil";

		public static final String SEARCH_EDUCATION_INSTITUTE = "/educationInstitute";

		public static final String SEARCH_EDUCATION_QUALIFICATION = "/educationQualification";

		public static final String SEARCH_PROFESSIONAL_MEMBERSHIP = "/professionalMembership";

		public static final String SEARCH_SPECIALITY = "/speciality";
	}

	public static final String ADMIN_BASE_URL = BASE_URL + "/admin";

	public interface AdminUrls {

		public static final String UPDATE_DOCTOR_CONTACT_STATE = "/updateDoctorContactState/{contactId}/{contactState}";

		public static final String UPDATE_LOCALE_CONTACT_STATE = "/updateLocaleContactState/{contactId}/{contactState}";

		public static final String GET_INACTIVE_USERS = "/inactiveUsers";

		public static final String GET_HOSPITALS = "/hospitals";

		public static final String GET_CLINICS_AND_LABS = "/clinics";

		public static final String GET_DOCTORS = "/doctors";

		public static final String GET_USER_APP_USERS = "/userApp/users";

		public static final String GET_DOCTORS_LIST = "/doctorsList";

		public static final String GET_ADMIN = "/admin";

		public static final String GET_DOCTOR_PROFILE = "/doctorProfile/{doctorId}/view";

		public static final String SEND_APP_LINK = "/sendLink";

		public static final String UPDATE_CLINIC_CONTACT_STATE = "/updateClinicContactState/{contactId}/{contactState}";

		public static final String UPLOAD_DOCUMENTS_FOR_VERIFICATIONS = "/documents";

		public static final String VERIFY_DOCUMENTS = "/verify/{documentId}";

		public static final String Add_SUBCRIPTION_DETAIL = "/addSubscriptionDetails";

		public static final String ADD_CSV_DATA = "/drug/csv/add";

		public static final String BLOCK_USER = "/block/{userId}/user";

		public static final String UPDATE_LOCATION = "/location/update";

		public static final String SEARCH_DOCTOR = "/search/doctor";

		public static final String COUNT_DOCTOR = "/count/doctor";

		public static final String COUNT_LAB = "/count/lab";

		public static final String COUNT_CLINIC = "/count/clinic";

		public static final String UPDATE_BLOG_SUPER_CATEGORY = "/blogs/updateSuperCategory/{blogId}/{category}";

		public static final String ADD_EXPENSE_TYPE = "/expenseType/add";

		public static final String GET_EXPENSE_TYPE = "/expenseType/get";

		public static final String DELETE_EXPENSE_TYPE = "/expenseType/{expenseTypeId}/delete";

		public static final String GET_EXPENSE_TYPE_BY_ID = "/expenseType/{expenseTypeId}/view";

		public static final String UPDATE_ES_DATA = "/update/es/data";

		public static final String GET_DRUG_NAME_BY_ID = "/prescription/{drugNameId}/getById";
		public static final String ADD_EAMIL_LIST_FOR_DOCTOR_CONTACT = "/emailList/add";
		public static final String SEND_DAILY_REPORT_ANALYTIC_REPORT_TO_DOCTOR = "/send/getDailyReportAnalyticstoDoctor";
		public static final String SEND_WEEKLY_REPORT_ANALYTIC_REPORT_TO_DOCTOR = "/send/getWeeklyReportAnalyticstoDoctor";
		public static final String UPDATE_DOCTOR_CLINIC_ISLISTED = "/updateDoctorClinicIsListed/{locationId}";

		public static final String UPDATE_CLINIC_SLUG_URL = "/updateClinicSlugURL/{locationId}/{slugURL}";

		public static final String UPDATE_DOCTOR_CLINIC_RANKING_COUNT = "/updateDoctorClinicRankingCount";
		public static final String UPDATE_CLINIC_NPS_SCORE = "/updateClinicNpsScore";

	}

	public static final String PUSH_NOTIFICATION_BASE_URL = BASE_URL + "/notification";

	public interface PushNotificationUrls {

		public static final String ADD_DEVICE = "/device/add";

		public static final String BROADCAST_NOTIFICATION = "/broadcast";

		public static final String READ_NOTIFICATION = "/read/{deviceId}";

	}

	public static final String LOCALE_BASE_URL = BASE_URL + "/locale";

	public interface LocaleUrls {

		public static final String UPLOAD = "/upload/image/{localeId}";
		public static final String DELETE_LOCALE_IMAGE = "image/{localeId}/delete";
		public static final String EDIT_LOCALE_ADDRESS_DETAILS = "/editAddressDetails";
		public static final String EDIT_LOCALE_DETAILS = "/editDetails";
		public static final String GET_LOCALE = "/{id}/get";
		public static final String GET_LOCALE_LIST = "/list";
		public static final String ACTIVATE_DEACTIVATE_LOCALE = "/activate/{id}/";
		public static final String COUNT_LOCALE = "/count/pharmacy/";

	}

	public static final String VERSION_CONTROL_BASE_URL = BASE_URL + "/version";

	public interface VersionControlUrls {
		public static final String CHECK_VERSION = "/check";
		public static final String CHANGE_VERSION = "/change";
		public static final String GET_VERSION = "/get";
	}

	public static final String SUBSCRIPTION_BASE_URL = BASE_URL + "/subscription";

	public interface SescriptionUrls {
		public static final String ACTIVATE_SUBSCRIPTION = "/activate";
		public static final String DEACTIVATE_SUBSCRIPTION = "/deactivate/{subscriptionDetailId}";
		public static final String GET_SUBSCRIPTION_DETAILS = "/getdetails";

	}

	public static final String ADMIN_UI_PERMISSION_BASE_URL = BASE_URL + "/uiPermission";

	public interface AdminUIPermissionUrls {
		public static final String ADD_EDIT_ADMIN_UI_PERMISSION = "/addEditAdminUIPermission";
		public static final String GET_ADMIN_UI_PERMISSIONS = "/getAdminUIPermissions";
		public static final String GET_ADMIN_UI_PERMISSION = "/getAdminUIPermission/{id}";
		public static final String GET_ADMIN_UI_PERMISSION_BY_ADMINID = "/getAdminUIPermissionsByAdminId/{adminId}";

	}

	public static final String SMILEBIRD_ADMIN_UI_PERMISSION_BASE_URL = BASE_URL + "/smilebirduUiPermission";

	public interface SmilebirdAdminUIPermissionUrls {
		public static final String ADD_EDIT_ADMIN_UI_PERMISSION = "/addEditAdminUIPermission";
		public static final String GET_ADMIN_UI_PERMISSIONS = "/getAdminUIPermissions";
		public static final String GET_ADMIN_UI_PERMISSION = "/getAdminUIPermission/{id}";
		public static final String GET_ADMIN_UI_PERMISSION_BY_ADMINID = "/getAdminUIPermissionsByAdminId/{adminId}";

	}
	public static final String RAZORPAY_BASE_URL = BASE_URL + "/razorpay";

	public interface RazorPayUrls {

		public static final String GET_PAYMENTS = "/payments";
		public static final String GET_PAYMENT_BY_ID = "/getPaymentById/{id}";
		public static final String REFUND_PAYMENT = "/refundPayment/{id}";

	}

	public interface CountryUrls {

		public static final String ADD_EDIT_COUNTRY = "/country/addEdit";

		public static final String GET_COUNTRY = "/country/get";

		public static final String GET_COUNTRY_BY_ID = "/country/{id}/get";

		public static final String DELETE_COUNTRY = "/country/{id}/delete";

		public static final String GET_COLLECTION = "/collection/get";

		public static final String GET_DRUGCOLLECTION = "/collection/drugget";

	}

	public interface PackageUrls {

		public static final String ADD_EDIT_PACKAGE = "/packages/addEdit";

		public static final String GET_PACKAGE = "/packages/get";

		public static final String GET_PACKAGE_BY_ID = "/packages/{id}/get";

		public static final String DELETE_PACKAGE = "/packages/{id}/delete";

	}

	public interface SubscriptionUrls {

		public static final String ADD_EDIT_SUBSCRIPTION = "/edit";

		public static final String ADD_EDIT_SUBSCRIPTION_HISTORY = "/history/addEdit";

		public static final String DELETE_SUBSCRIPTION_HISTORY = "/history/{id}/delete";

		public static final String GET_SUBSCRIPTION = "/get";

		public static final String GET_SUBSCRIPTION_HISTORY = "/history/get";

		public static final String GET_SUBSCRIPTION_BY_ID = "/{id}/get";

		public static final String GET_SUBSCRIPTION_BY_DOCTORID = "/doctor/{doctorId}/get";

		public static final String GET_SUBSCRIPTION_BY_ID_HISTORY = "/history/{id}/get";

		public static final String GET_SUBSCRIPTION_PAYMENT = "/payment/{doctorId}/get";

	}

	public interface PackageDetaiUrls {

		public static final String ADD_EDIT_PACKAGES = "/package/addEdit";

		public static final String GET_PACKAGES = "/package/getList";

		public static final String GET_PACKAGES_BY_NAME = "/package/get";

		public static final String DELETE_PACKAGE = "/package/{id}/delete";

	}

	public static final String BULK_SMS_PACKAGE_BASE_URL = BASE_URL + "/bulkSms";

	public interface BulkSmsPackageUrls {

		public static final String ADD_EDIT_PACKAGE = "/package/addEdit";
		public static final String GET_SMS_PACKAGE = "/package/get";
		public static final String DELETE_SMS_PACKAGE = "/package/delete";
		public static final String GET_BULK_SMS_CREDITS = "/credits/get";
		public static final String ADD_BULK_SMS_CREDITS = "/credits/add";
		public static final String GET_SMS_HISTORY = "/history/get";
		public static final String GET_BULK_SMS_REPORTS = "/bulkSmsReport/get";

	}

	public static final String FILE_BASE_URL = BASE_URL + "/file";

	public interface FileBaseUrls {

		public static final String UPLOAD_FILE = "/upload";

		public static final String GET_FILES = "/get";

		public static final String GET_PATH = "/path";

		public static final String UPLOAD_DATA = "/data/upload";

		public static final String UPLOAD_NEW_DATA = "/data/uploadNew";

		static final String DOWNLOAD_DATA = "/data/download";

		public static final String UPLOAD_CLINIC_DATA = "/clinic/upload";

	}

	public static final String LANGUAGE_BASE_URL = BASE_URL + "/language";

	public interface LanguageUrls {

		public static final String GET_LANGUAGE_BY_ID = "/{id}/get";
		public static final String GET_LANGUAGES = "/get";
		public static final String ADD_EDIT_LANGUAGE = "/language/addEdit";
		public static final String DELETE_LANGUAGE = "/{id}/delete";
	}

	public static final String DASHBOARD_BASE_URL = BASE_URL + "/dashboard";

	public interface DashboardApiUrls { 
		public static final String DASHBOARD_KPIS	= "/kpis";
		public static final String CLINICS = "/clinics";
		public static final String VIEW_CLINIC_BY_ID = "/clinics/{clinicId}";
		public static final String MONTHLY_SUMMARY = "/monthly-summary";
		public static final String PERFORMANCE_METRICS = "/performance-metrics";
		public static final String COMPANY_FINANCIALS = "/company-financials";
		public static final String CLINIC_FINANCIALS = "/clinic-financials";
		public static final String CLINIC_PERFORMANCE_COMPARISON = "/clinic-performance-comparison";
		
		// New Financial APIs
		public static final String FINANCIAL_OVERVIEW = "/financial/overview";
		public static final String REVENUE_EXPENSES_TREND = "/financial/revenue-expenses-trend";
		public static final String CLINIC_WISE_FINANCIALS = "/financial/clinic-wise";
		public static final String EXPENSES_BREAKDOWN = "/financial/expenses-breakdown";
	}
}