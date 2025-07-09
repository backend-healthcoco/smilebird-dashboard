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

	public static final String CLINICAL_NOTES_BASE_URL = BASE_URL + "/clinicalNotes";

	public interface ClinicalNotesUrls {

		public static final String ADD_COMPLAINT = "/complaint/add";

		public static final String ADD_OBSERVATION = "/observation/add";

		public static final String ADD_INVESTIGATION = "/investigation/add";

		public static final String ADD_DIAGNOSIS = "/diagnosis/add";

		public static final String ADD_NOTES = "/notes/add";

		public static final String ADD_DIAGRAM = "/diagram/add";

		public static final String ADD_PRESENT_COMPLAINT = "/presentComplaint/add";

		public static final String ADD_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/add";

		public static final String ADD_GENERAL_EXAM = "/generalExam/add";

		public static final String ADD_SYSTEM_EXAM = "/systemExam/add";

		public static final String ADD_MENSTRUAL_HISTORY = "/menstrualHistory/add";

		public static final String ADD_OBSTETRICS_HISTORY = "/obstetricHistory/add";

		public static final String ADD_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/add";

		public static final String ADD_INDICATION_OF_USG = "/indicationOfUSG/add";

		public static final String ADD_PA = "/pa/add";

		public static final String ADD_PV = "/pv/add";

		public static final String ADD_PS = "/ps/add";

		public static final String ADD_PC_NOSE = "/pcnose/add";

		public static final String ADD_PC_EARS = "/pcears/add";

		public static final String ADD_PC_THROAT = "/pcthroat/add";

		public static final String ADD_PC_ORAL_CAVITY = "/pcoralCavity/add";

		public static final String ADD_NECK_EXAM = "/neckExam/add";

		public static final String ADD_NOSE_EXAM = "/noseExam/add";

		public static final String ADD_EARS_EXAM = "/earsExam/add";

		public static final String ADD_INDIRECT_LARYGOSCOPY_EXAM = "/indirectLarygoscopyExam/add";

		public static final String ADD_ORAL_CAVITY_THROAT_EXAM = "/oralCavityThroatExam/add";

		public static final String DELETE_COMPLAINT = "/complaint/{id}/delete";

		public static final String DELETE_OBSERVATION = "/observation/{id}//delete";

		public static final String DELETE_INVESTIGATION = "/investigation/{id}/delete";

		public static final String DELETE_DIAGNOSIS = "/diagnosis/{id}/delete";

		public static final String DELETE_NOTE = "/notes/{id}/delete";

		public static final String DELETE_DIAGRAM = "/diagram/{id}/delete";

		public static final String DELETE_PRESENT_COMPLAINT = "/presentComplaint/{id}/delete";

		public static final String DELETE_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/delete";

		public static final String DELETE_GENERAL_EXAM = "/generalExam/{id}/delete";

		public static final String DELETE_SYSTEM_EXAM = "/systemExam/{id}/delete";

		public static final String DELETE_MENSTRUAL_HISTORY = "/menstrualHistory/{id}/delete";

		public static final String DELETE_OBSTETRIC_HISTORY = "/obstetricHistory/{id}/delete";

		public static final String DELETE_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/{id}/delete";

		public static final String DELETE_PA = "/pa/{id}/delete";

		public static final String DELETE_PV = "/pv/{id}/delete";

		public static final String DELETE_PS = "/ps/{id}/delete";

		public static final String DELETE_PC_NOSE = "/pcNose/{id}/delete";
		public static final String DELETE_PC_EARS = "/pcEars/{id}/delete";
		public static final String DELETE_PC_ORAL_CAVITY = "/pcOralCavity/{id}/delete";
		public static final String DELETE_PC_THROAT = "/pcThroat/{id}/delete";
		public static final String DELETE_NECK_EXAM = "/neckExam/{id}/delete";
		public static final String DELETE_EARS_EXAM = "/earsExam/{id}/delete";
		public static final String DELETE_NOSE_EXAM = "/noseExam/{id}/delete";
		public static final String DELETE_ORAL_CAVITY_THROAT_EXAM = "/oralCavityThroatExam/{id}/delete";
		public static final String DELETE_INDIRECT_LARYGOSCOPY_EXAM = "/indirectLarygoscopyExam/{id}/delete";

		public static final String DELETE_INDICATION_OF_USG = "/indicationOfUSG/{id}/delete";

		public static final String GET_CINICAL_ITEMS = "/{type}/{range}";

		public static final String EMAIL_CLINICAL_NOTES = "/{clinicalNotesId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DOWNLOAD_CLINICAL_NOTES = "/download/{clinicalNotesId}";
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

	public static final String RECORDS_BASE_URL = BASE_URL + "/records";

	public interface RecordsUrls {
		public static final String ADD_RECORDS = "/add";

		public static final String GET_RECORD_BY_ID = "/{recordId}/view";

		public static final String TAG_RECORD = "/tagrecord";

		public static final String SEARCH_RECORD = "/search";

		public static final String GET_RECORDS_PATIENT_ID = "/{patientId}";

		public static final String GET_RECORD_COUNT = "/getRecordCount/{doctorId}/{patientId}/{locationId}/{hospitalId}";

		public static final String CREATE_TAG = "/createtag";

		public static final String GET_ALL_TAGS = "/getalltags/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_PATIENT_EMAIL_ADD = "/getpatientemailaddr/{patientId}";

		public static final String EMAIL_RECORD = "/{recordId}/{doctorId}/{locationId}/{hospitalId}/{emailAddress}/mail";

		public static final String DELETE_RECORD = "/{recordId}/delete";

		public static final String DOWNLOAD_RECORD = "/download/{recordId}";

		public static final String DELETE_TAG = "/tagrecord/{tagid}/delete";

		public static final String GET_FLEXIBLE_COUNTS = "/getFlexibleCounts";

		public static final String EDIT_RECORD = "/{recordId}/update";

		public static final String CHANGE_LABEL_AND_DESCRIPTION_RECORD = "/changeLabelAndDescription";

		public static final String ADD_RECORDS_MULTIPART = "/add";

		public static final String SAVE_RECORDS_IMAGE = "/saveImage";
	}

	public static final String PRESCRIPTION_BASE_URL = BASE_URL + "/prescription";

	public interface PrescriptionUrls {

		public static final String ADD_LAB_TEST = "/labTest/add";

		public static final String EDIT_LAB_TEST = "/labTest/{labTestId}/update";

		public static final String DELETE_LAB_TEST = "/labTest/{labTestId}/{locationId}/{hospitalId}/delete";

		public static final String UPLOAD_DIAGNOSTIC_TEST = "diagnosticTest/upload";

		public static final String UPLOAD_DRUGS = "drugs/upload";
		public static final String UPLOAD_DRUGSDETAILS = "drugdetails/upload";
		public static final String GET_DRUGSDETAILS = "drugdetails/get";

	}

	public static final String HISTORY_BASE_URL = BASE_URL + "/history";

	public interface HistoryUrls {
		public static final String ADD_DISEASE = "/disease/add";

		public static final String EDIT_DISEASE = "/disease/{diseaseId}/update";

		public static final String DELETE_DISEASE = "/disease/{diseaseId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_DISEASES = "/diseases/{range}";

		public static final String ADD_REPORT_TO_HISTORY = "/report/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_CLINICAL_NOTES_TO_HISTORY = "/clinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_PRESCRIPTION_TO_HISTORY = "/prescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_PATIENT_TREATMENT_TO_HISTORY = "/patientTreament/{treatmentId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";

		public static final String ADD_SPECIAL_NOTES = "/addSpecialNotes";

		public static final String ASSIGN_MEDICAL_HISTORY = "/assignMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String ASSIGN_FAMILY_HISTORY = "/assignFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String HANDLE_MEDICAL_HISTORY = "/medicalHistory";

		public static final String GET_MEDICAL_AND_FAMILY_HISTORY = "/getMedicalAndFamilyHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String HANDLE_FAMILY_HISTORY = "/familyHistory";

		public static final String REMOVE_REPORTS = "/removeReports/{reportId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_CLINICAL_NOTES = "/removeClinicalNotes/{clinicalNotesId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_PRESCRIPTION = "/removePrescription/{prescriptionId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_PATIENT_TREATMENT = "/removePatientTreatment/{treatmentId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_MEDICAL_HISTORY = "/removeMedicalHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String REMOVE_FAMILY_HISTORY = "/removeFamilyHistory/{diseaseId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_PATIENT_HISTORY_OTP_VERIFIED = "/getPatientHistory/{patientId}/{doctorId}/{locationId}/{hospitalId}/{otpVerified}";

		public static final String GET_PATIENT_HISTORY = "/{patientId}";

		public static final String MAIL_MEDICAL_DATA = "mailMedicalData";

		// public static final String ADD_VISITS_TO_HISTORY =
		// "/visits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}/add";
		//
		// public static final String REMOVE_VISITS =
		// "/removeVisits/{visitId}/{patientId}/{doctorId}/{locationId}/{hospitalId}";

		public static final String GET_MULTIPLE_DATA = "getMultipleData";
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

	public static final String PATIENT_VISIT_BASE_URL = BASE_URL + "/patientVisit";

	public interface PatientVisitUrls {

		public static final String ADD_MULTIPLE_DATA = "/add";

		public static final String EMAIL = "/email/{visitId}/{emailAddress}";

		public static final String GET_VISIT = "/{visitId}";

		public static final String GET_VISITS = "/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String GET_VISITS_HANDHELD = "/handheld/{doctorId}/{locationId}/{hospitalId}/{patientId}";

		public static final String DELETE_VISITS = "/{visitId}/delete";

		public static final String SMS_VISITS = "/{visitId}/{doctorId}/{locationId}/{hospitalId}/{mobileNumber}/sms";

		public static final String DOWNLOAD_PATIENT_VISIT = "/download/{visitId}";

	}

	public static final String ACCESS_CONTROL_BASE_URL = BASE_URL + "/accessControl";

	public interface AccessControlUrls {
		public static final String GET_ACCESS_CONTROLS = "getAccessControls/{roleOrUserId}/{locationId}/{hospitalId}";

		public static final String SET_ACCESS_CONTROLS = "setAccessControls";
	}

	public static final String ISSUE_TRACK_BASE_URL = BASE_URL + "/issueTrack";

	public interface IssueTrackUrls {

		public static final String RAISE_ISSUE = "add";

		public static final String DELETE_ISSUE = "/{issueId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String UPDATE_STATUS_DOCTOR_SPECIFIC = "/{issueId}/{status}/{doctorId}/{locationId}/{hospitalId}/update";

		public static final String UPDATE_STATUS_ADMIN = "/{issueId}/{status}/update";

		public static final String GET_ISSUE = "getissue";
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

	public static final String GENERAL_TESTS_URL = BASE_URL + "/tests";

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

	public static final String NUTRITION_URL = BASE_URL + "/nutrition";

	public interface NutritionUrl {
		public static final String GET_ALL_PLAN_CATEGORY = "/getAllCategory";
		public static final String ADD_EDIT_NUTRITION_PLAN = "/addEditPlan";
		public static final String GET_NUTRITION_PLAN = "/getPlan";
		public static final String GET_NUTRITION_PLAN_BY_ID = "/getPlanById/{id}";
		public static final String DELETE_NUTRITION_PLAN = "/plan/{id}/delete";
		public static final String ADD_EDIT_SUBSCRIPTION_PLAN = "/addEditSubscriptionPlan";
		public static final String GET_SUBSCRIPTION_PLANS = "/getSubscriptionPlan";
		public static final String GET_SUBSCRIPTION_PLAN_BY_ID = "/getSubscriptionPlanById/{id}";
		public static final String DELETE_SUBSCRIPTION_PLAN = "/subscriptionPlan/{id}/delete";
		public static final String GET_NUTRITION_PLAN_CATEGORY = "/getPlanByCategory";
		public static final String GET_USER_PLAN_SUBSCRIPTION = "/getUserPlanSubscription/{id}";
		public static final String GET_USER_PLAN_SUBSCRIPTIONS = "/getUserPlanSubscriptions";
		public static final String GET_NUTRITION_APPOINTMENTS = "/appointment/get";
		public static final String UPDATE_NUTRITION_APPOINTMENT_STATUS = "update/{appointmentId}/status";
		public static final String ADD_EDIT_TESTIMONIALS = "/tesitmonials/addEdit";
		public static final String DELETE_TESTIMONIALS = "/testimonials/delete/{id}";
		public static final String GET_TESTIMONIALS_BY_PLAN_ID = "/testimonials/getByPlanId/{id}";
		public static final String GET_NUTRITION_PLAN_NAME_CATEGORY = "/getPlanNameByCategory";
		public static final String PUT_REMOVE_PLAN_PRICES = "/putRemovePlanPrices";
		public static final String GET_USER_NUTRITION_PLAN_BY_ID = "/getNutritionPlanById/{id}";
		public static final String ADD_EDIT_NUTRITION_RDA = "/rda/addEdit";
		public static final String GET_NUTRITION_RDA_BY_ID = "/rda/{id}";
		public static final String GET_NUTRITION_RDA = "/rda";
		public static final String DELETE_NUTRITION_RDA = "/rda/{id}/delete";
		public static final String UPLOAD_NUTRITION_RDA = "/rda/upload";

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

		public static final String MAIL_CUSTOM_DRUGS = "/mailDrugs";

		// public static final String GET_USERS =
		// "/users/{locationId}/{hospitalId}"; this API is in RegsiterAPI

		// public static final String GET_FEEDBACKS = "/feedbacks"; API is
		// already there

		// public static final String EDIT_FEEDBACK =
		// "/feedback/{feedbackId}/edit"; visible API is already there

		// public static final String GET_HELPUS_DATA = "/helpUsData";API is
		// already there

		public static final String GET_SMS_COUNT = "/smsCount/{doctorId}/{locationId}/{hospitalId}";

		public static final String EDIT_SMS_COUNT = "/smsCount/{doctorId}/{locationId}/{hospitalId}/edit";

		public static final String ADD_RESUMES = "/resumes/add";

		public static final String GET_RESUMES = "/resumes";

		public static final String ADD_CONTACT_US = "/contactUs/add";

		public static final String GET_CONTACT_US = "/contactUs";

		public static final String GET_PATIENT = "/patients";

		public static final String SEARCH_CLINIC_BY_NAME = "/clinic";

		public static final String SEARCH_DOCTOR_BY_NAME = "/doctor";

		public static final String GET_UNIQUE_SPECIALITY = "/speciality";

		public static final String IMPORT_DRUG = "/importDrug";

		public static final String IMPORT_CITY = "/importCity";

		public static final String IMPORT_DIAGNOSTIC_TEST = "/importDiagnosticTest";

		public static final String IMPORT_EDUCATION_QUALIFICATION = "/importEducationQualification";

		public static final String IMPORT_EDUCATION_INSTITUTE = "/importEducationInstitute";

		public static final String GET_CINICAL_ITEMS = "/clinicalNotes/{type}/{range}";

		public static final String ADD_COMPLAINT = "/complaint/add";

		public static final String ADD_OBSERVATION = "/observation/add";

		public static final String ADD_INVESTIGATION = "/investigation/add";

		public static final String ADD_DIAGNOSIS = "/diagnosis/add";

		public static final String ADD_NOTES = "/notes/add";

		public static final String ADD_DIAGRAM = "/diagram/add";

		public static final String ADD_PRESENT_COMPLAINT = "/presentComplaint/add";

		public static final String ADD_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/add";

		public static final String ADD_GENERAL_EXAM = "/generalExam/add";

		public static final String ADD_SYSTEM_EXAM = "/systemExam/add";

		public static final String ADD_MENSTRUAL_HISTORY = "/menstrualHistory/add";

		public static final String ADD_OBSTETRICS_HISTORY = "/obstetricHistory/add";

		public static final String ADD_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/add";

		public static final String ADD_INDICATION_OF_USG = "/indicationOfUSG/add";

		public static final String ADD_PA = "/pa/add";

		public static final String ADD_PV = "/pv/add";

		public static final String ADD_PS = "/ps/add";

		public static final String ADD_X_RAY_DETAILS = "/xRayDetails/add";

		public static final String ADD_ECG_DETAILS = "/ecgDetails/add";

		public static final String ADD_ECHO = "/echo/add";

		public static final String ADD_HOLTER = "/holter/add";

		public static final String ADD_PROCEDURE_NOTE = "/procedureNote/add";

		public static final String DELETE_COMPLAINT = "/complaint/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_OBSERVATION = "/observation/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_INVESTIGATION = "/investigation/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_DIAGNOSIS = "/diagnosis/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_NOTE = "/notes/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_DIAGRAM = "/diagram/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PRESENT_COMPLAINT = "/presentComplaint/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PROVISIONAL_DIAGNOSIS = "/provisionalDiagnosis/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_GENERAL_EXAM = "/generalExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_SYSTEM_EXAM = "/systemExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_MENSTRUAL_HISTORY = "/menstrualHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_OBSTETRIC_HISTORY = "/obstetricHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PRESENT_COMPLAINT_HISTORY = "/presentComplaintHistory/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PA = "/pa/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PV = "/pv/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PS = "/ps/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_INDICATION_OF_USG = "/indicationOfUSG/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_X_RAY_DETAILS = "/xRayDetails/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_ECG_DETAILS = "/ecgDetails/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_ECHO = "/echo/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_HOLTER = "/holter/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PROCEDURE_NOTE = "/procedureNote/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PC_NOSE = "/pcNose/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PC_EARS = "/pcEars/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PC_ORAL_CAVITY = "/pcOralCavity/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_PC_THROAT = "/pcThroat/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_NECK_EXAM = "/neckExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_EARS_EXAM = "/earsExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_NOSE_EXAM = "/noseExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_ORAL_CAVITY_THROAT_EXAM = "/oralCavityThroatExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String DELETE_INDIRECT_LARYGOSCOPY_EXAM = "/indirectLarygoscopyExam/{id}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String ADD_DISEASE = "/disease/add";

		public static final String EDIT_DISEASE = "/disease/{diseaseId}/update";

		public static final String DELETE_DISEASE = "/disease/{diseaseId}/{doctorId}/{locationId}/{hospitalId}/delete";

		public static final String GET_DISEASES = "/diseases/{range}";

		public static final String ADD_DRUG = "/drug/add";

		public static final String MAKE_CUSTOM_DRUG_GLOBAL = "/drug/makeGobal";

		public static final String EDIT_DRUG = "/drug/{drugId}/update";

		public static final String DELETE_GLOBAL_DRUG = "/drug/{drugId}/delete";

		public static final String ADD_LAB_TEST = "/labTest/add";

		public static final String EDIT_LAB_TEST = "/labTest/{labTestId}/update";

		public static final String DELETE_GLOBAL_LAB_TEST = "/labTest/{labTestId}/delete";

		public static final String GET_DIAGNOSTIC_TEST = "/diagnosticTest";

		public static final String ADD_EDIT_DIAGNOSTIC_TEST = "/diagnosticTest/addEdit";

		public static final String DELETE_GLOBAL_DIAGNOSTIC_TEST = "/diagnosticTest/{diagnosticTestId}/delete";

		public static final String GET_PRESCRIPTION_ITEMS = "/prescription/{type}/{range}";

		public static final String ADD_DRUG_TYPE = "/drugType/add";

		public static final String EDIT_DRUG_TYPE = "/drugType/{drugTypeId}/update";

		public static final String DELETE_DRUG_TYPE = "/drugType/{drugTypeId}/delete";

		public static final String ADD_DRUG_STRENGTH = "/drugStrength/add";

		public static final String EDIT_DRUG_STRENGTH = "/drugStrength/{drugStrengthId}/update";

		public static final String DELETE_DRUG_STRENGTH = "/drugStrength/{drugStrengthId}/delete";

		public static final String ADD_DRUG_DOSAGE = "/drugDosage/add";

		public static final String EDIT_DRUG_DOSAGE = "/drugDosage/{drugDosageId}/update";

		public static final String DELETE_DRUG_DOSAGE = "/drugDosage/{drugDosageId}/delete";

		public static final String ADD_DRUG_DIRECTION = "/drugDirection/add";

		public static final String EDIT_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/update";

		public static final String DELETE_DRUG_DIRECTION = "/drugDirection/{drugDirectionId}/delete";

		public static final String ADD_DRUG_DURATION_UNIT = "/drugDurationUnit/add";

		public static final String EDIT_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/update";

		public static final String DELETE_DRUG_DURATION_UNIT = "/drugDurationUnit/{drugDurationUnitId}/delete";

		public static final String IMPORT_PROFESSIONAL_MEMBERSHIP = "/importProfessionalMembership";

		public static final String IMPORT_MEDICAL_COUNCIL = "/importMedicalCouncil";

		public static final String GET_DOCTOR_PROFILE = "/doctorProfile/{doctorId}/view";

		public static final String ADD_REMOVE_GENERIC_CODE_TO_DRUG = "/genericCode/{action}/{genericId}/{drugCode}";

		public static final String ADD_EDIT_GENERIC_CODE = "/genericCode/addEdit";

		public static final String ADD_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/";

		public static final String UPLOAD_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/upload";

		public static final String DELETE_GENERIC_CODE_WITH_REACTION = "/genericCodeWithReaction/delete";

		public static final String SEND_APP_LINK = "/sendLink";

		public static final String GET_COUNT_PATIENT_RECORD = "/countpatientrecord";

		public static final String ADD_BLOG = "/addBlog";

		public static final String GET_BLOG = "/getBlogs";

		public static final String GET_BLOG_BY_ID = "/getBlog/{blogId}";

		public static final String GET_BLOG_BY_SLUG_URL = "/getBlogbySlugURL/{slugURL}";

		public static final String DELETE_BLOG = "/blog/{blogId}/{userId}/delete";

		public static final String UPLOAD_BLOG_IMAGES = "/uploadBlogImages";

		public static final String GET_BLOG_IMAGES = "/getBlogImages";

		public static final String GET_BLOGS_CATEGORY = "/getBlogCategory";

		public static final String ADD_TAG = "/blog/addTag";

		public static final String GET_TAGS = "/blog/getTags";

		public static final String DELETE_TAG = "/blog/tag/{tagId}/delete";

		public static final String UPDATE_DOCTOR_CLINIC_ISLISTED = "/updateDoctorClinicIsListed/{locationId}";

		public static final String UPDATE_CLINIC_SLUG_URL = "/updateClinicSlugURL/{locationId}/{slugURL}";

		public static final String UPDATE_DOCTOR_CLINIC_RANKING_COUNT = "/updateDoctorClinicRankingCount";

		public static final String ADD_ADVICE = "/advice";

		public static final String DELETE_ADVICE = "/advice/{adviceId}/delete";

		public static final String GET_SUGGESTION = "/getSuggestion";

		public static final String UPDATE_SUGGESTION_STATE = "/updateSuggestionState/{suggestionId}";

		public static final String UPDATE_CLINIC_CONTACT_STATE = "/updateClinicContactState/{contactId}/{contactState}";

		public static final String UPLOAD_DOCUMENTS_FOR_VERIFICATIONS = "/documents";

		public static final String VERIFY_DOCUMENTS = "/verify/{documentId}";

		public static final String Add_SUBCRIPTION_DETAIL = "/addSubscriptionDetails";

		public static final String ADD_CSV_DATA = "/drug/csv/add";

		public static final String GET_PHARMACY_REQUEST_DETAILS = "/pharmacy/getRequestDetails";

		public static final String GET_PHARMACY_RESPONSE = "/pharmacy/{uniqueRequestId}/getResponse";

		public static final String BLOCK_USER = "/block/{userId}/user";

		public static final String UPDATE_LOCATION = "/location/update";

		public static final String UPDATE_PHARMACY = "/locale/update";

		public static final String UPDATE_USER = "/user/update";

		public static final String ADD_CERTIFICATE_TEMPLATES = "/certificate/template/add";

		public static final String GET_CERTIFICATE_TEMPLATES = "/certificate/template";

		public static final String DELETE_CERTIFICATE_TEMPLATES = "/certificate/template/{templateId}/delete";

		public static final String GET_CERTIFICATE_TEMPLATE_BY_ID = "/certificate/template/{templateId}/view";

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
//		public static final String GET_DRUG_TYPE_BY_ID = "/prescription/{drugTypeId}/getById";
//		public static final String GET_DRUG_DIRECTION_BY_ID = "/prescription/{drugDirectionId}/getById";
//		public static final String GET_DRUG_DOSAGE_BY_ID = "/prescription/{drugDosageId}/getById";
//		public static final String GET_DRUG_DURATION_BY_ID = "/prescription/{drugDurationId}/getById";
		public static final String ADD_EAMIL_LIST_FOR_DOCTOR_CONTACT = "/emailList/add";
		public static final String SEND_DAILY_REPORT_ANALYTIC_REPORT_TO_DOCTOR = "/send/getDailyReportAnalyticstoDoctor";
		public static final String SEND_WEEKLY_REPORT_ANALYTIC_REPORT_TO_DOCTOR = "/send/getWeeklyReportAnalyticstoDoctor";

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

	public static final String FEEDBACK_BASE_URL = BASE_URL + "/feedback";

	public interface FeedbackUrls {
		public static final String SAVE_DOCTOR_APP_FEEDBACK = "/doctorApp/save";
		public static final String GET_DOCTOR_APP_FEEDBACKS = "/doctorApp/get";
		public static final String GET_PATIENT_FEEDBACKS = "/patient/get";
		public static final String APPROVE_PATIENT_FEEDBACKS = "/patient/{id}/approve";
	}

	public static final String LAB_BASE_URL = BASE_URL + "/lab";

	public interface LabUrls {
		public static final String GET_ASSOCIATED_LABS = "/getAssociatedLabs/{locationId}";
		public static final String ADD_ASSOCIATED_LABS = "/addAssociatedLabs";
		public static final String EDIT_PARENT_STATUS = "/editParentStatus/{locationId}";
		public static final String GET_SPECIMEN_LIST = "/getSpecimens";
		public static final String ADD_SPECIMEN_LIST = "/addSpecimen";
	}

	public static final String DIAGNOSTIC_TEST_ORDER_BASE_URL = BASE_URL + "/admin/test";

	public interface DiagnosticTestOrderUrls {

		public static final String GET_SAMPLE_PICKUP_TIME_SLOTS = "/pickUpTimeSlots";

		public static final String ADD_EDIT_PICKUP_TIME_SLOTS = "/pickUpTimeSlots/addEdit";

		public static final String UPDATE_ORDER_STATUS = "/order/changeStatus/{orderId}/{status}";

		public static final String GET_ORDERS = "orders";

		public static final String GET_ORDER_BY_ID = "/order/{orderId}/view";

		public static final String ADD_EDIT_DIAGNOSTIC_TEST_PACKAGE = "packages/addEdit";

		public static final String GET_DIAGNOSTIC_TEST_PACKAGES = "packages";

	}

	public static final String BROAD_CAST_BASE_URL = BASE_URL + "/broadcast";

	public interface BroadCastUrls {
		public static final String BROAD_CAST_MAIL = "/mail";
		public static final String BROAD_CAST_NOTIFICATION = "/notification";
		public static final String BROAD_CAST_SMS = "/sms";
		public static final String SEND_SMS = "/sendSmsEmail";
		public static final String GET_USERS_COUNT = "/users/count";
	}

	public static final String DENTAL_LAB_BASE_URL = BASE_URL + "/dentalLab";

	public interface DentalLabUrls {
		public static final String ADD_EDIT_DENTAL_WORKS = "/addEditDentalWorks";
		public static final String GET_DENTAL_WORKS = "/getDentalWorks";
		public static final String DELETE_DENTAL_WORKS = "/deleteDentalWorks";
		public static final String CHANGE_LAB_TYPE = "/changeLabType";
		public static final String ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION = "/addEditDentalLabDoctorAssociation";
		public static final String GET_DENTAL_LAB_DOCTOR_ASSOCIATION = "/getDentalLabDoctorAssociation";
	}

	public static final String ADMIN_UI_PERMISSION_BASE_URL = BASE_URL + "/uiPermission";

	public interface AdminUIPermissionUrls {
		public static final String ADD_EDIT_ADMIN_UI_PERMISSION = "/addEditAdminUIPermission";
		public static final String GET_ADMIN_UI_PERMISSIONS = "/getAdminUIPermissions";
		public static final String GET_ADMIN_UI_PERMISSION = "/getAdminUIPermission/{id}";
		public static final String GET_ADMIN_UI_PERMISSION_BY_ADMINID = "/getAdminUIPermissionsByAdminId/{adminId}";

	}

	public static final String REFERENCE_BASE_URL = BASE_URL + "/reference";

	public interface ReferenceUrls {
		public static final String UPDATE_DOCTOR_LAB_REFERNCE = "/doctorLab/update";
		public static final String GET_DOCTOR_LAB_REFERNCES = "/doctorLab/get";
		public static final String GET_DOCTOR_LAB_REFERNCE = "/doctorLab/{id}/view";
	}

	public static final String DENTAL_IMAGING_URL = BASE_URL + "/dentalImaging";

	public interface DentalImagingUrl {

		public static final String ADD_EDIT_SERVICES = "/addEditService";
		public static final String GET_SERVICES = "/getServices";
		public static final String GET_SERVICES_BY_ID = "/getServicesById/{id}";
		public static final String DISCARD_SERVICE = "/discardService/{id}";
		public static final String ADD_DOCTOR_HOSPITAL_ASSOCIATION = "/addDoctorHospitalAssociation";
		public static final String GET_DOCTOR_HOSPITAL_ASSOCIATIONS = "/getDoctorHospitalAssociations";
		public static final String GET_HOSPITAL_LIST = "/getHospitalList";
		public static final String ADD_EDIT_CBDT_QUADRANT = "/addEditCBDTQuadrant";
		public static final String GET_CBDT_QUADRANTS = "/getCBDTQuadrants";
		public static final String GET_CBDT_QUADRANT_BY_ID = "/getCBDTQuadrantsById/{id}";
		public static final String ADD_EDIT_CBDT_ARCH = "/addEditCBDTArch";
		public static final String GET_CBDT_ARCHS = "/getCBDTArchs";
		public static final String GET_CBDT_ARCH_BY_ID = "/getCBDTArchsById/{id}";
		public static final String ADD_EDIT_FOV = "/addEditFOV";
		public static final String GET_FOVS = "/getFOVs";
		public static final String GET_FOV_BY_ID = "/getFOVsById/{id}";

	}

	public static final String PHARMA_URL = BASE_URL + "/pharma";

	public interface PharmaUrls {
		public static final String ADD_EDIT_LICENSE = "/addEditLicense";
		public static final String GET_LICENSES = "/getLicenses";
		public static final String DISCARD_LICENSE = "/discardLicense/{id}";
		public static final String GET_COMPANY_LIST = "/getCompanies";
		public static final String GET_COMPANY_DETAILS = "/getCompanyDetail/{id}";
		public static final String EDIT_COMPANY_DETAILS = "/editCompanyDetails";
		public static final String ACTIVATE_COMPANY = "/activateCompany/{id}";
		public static final String VERIFY_COMPANY = "/verifyCompany/{id}";
		public static final String GET_DRUG_COMPANY_LIST = "/getDrugCompanies";

	}

	public static final String RECIPE_BASE_URL = BASE_URL + "/recipe";

	public interface RecipeUrls {
		public static final String ADD_EDIT_NUTRIENT = "nutrient/addEdit";
		public static final String GET_NUTRIENTS = "nutrient/get";
		public static final String DELETE_NUTRIENT = "nutrient/{nutrientId}/delete";
		public static final String GET_NUTRIENT = "nutrient/{nutrientId}/get";

		public static final String ADD_EDIT_INGREDIENT = "ingredient/addEdit";
		public static final String GET_INGREDIENTS = "ingredient/get";
		public static final String DELETE_INGREDIENT = "ingredient/{ingredientId}/delete";
		public static final String GET_INGREDIENT = "ingredient/{ingredientId}/get";
		public static final String UPDATE_INGREDIENT = "ingredient/{ingredientId}/update";

		public static final String ADD_EDIT_RECIPE = "addEdit";
		public static final String GET_RECIPES = "get";
		public static final String DELETE_RECIPE = "{recipeId}/delete";
		public static final String GET_RECIPE = "{recipeId}/get";
		public static final String UPDATE_NUTRIENT_VALUE_AT_RECIPE_LEVEL = "/update/nutrientValueAtRecipeLevel";
		public static final String VERIFY_RECIPE = "{recipeId}/verify";
		public static final String UPDATE_RECIPE = "{recipeId}/update";

		public static final String ADD_EDIT_EXERCISE = "exercise/addEdit";
		public static final String GET_EXERCISES = "exercise/get";
		public static final String UPLOAD_EXERCISE_IMAGE = "exerciseImage/upload";
		public static final String UPLOAD_EXERCISE_VIDEO = "exerciseVideo/upload";

		public static final String DELETE_EXERCISE = "exercise/{exerciseId}/delete";
		public static final String GET_EXERCISE = "exercise/{exerciseId}/get";
		public static final String UPDATE_RECIPE_DATA = "recipe/data/update";

		public static final String ADD_EDIT_RECIPE_STEPS = "steps/addEdit";
		public static final String GET_RECIPE_STEPS = "steps/get/{recipeId}";
		public static final String ADD_EDIT_RECIPE_PLANS = "plans/addEdit";

		public static final String ADD_EDIT_FOOD_COMMUNITY = "/foodCommunity/addEdit";
		public static final String GET_FOOD_COMMUNITY_BY_ID = "/foodCommunity/{id}";
		public static final String GET_FOOD_COMMUNITIES = "/foodCommunity";
		public static final String DELETE_FOOD_COMMUNITY = "/foodCommunity/{id}/delete";

		public static final String ADD_EDIT_FOOD_GROUP = "/foodGroup/addEdit";
		public static final String GET_FOOD_GROUP_BY_ID = "/foodGroup/{id}";
		public static final String GET_FOOD_GROUPS = "/foodGroup";
		public static final String DELETE_FOOD_GROUP = "/foodGroup/{id}/delete";

		public static final String ADD_EDIT_NUTRIENT_GOAL = "/nutrientGoal/addEdit";
		public static final String GET_NUTRIENT_GOAL_BY_ID = "/nutrientGoal/{id}";
		public static final String GET_NUTRIENT_GOALS = "/nutrientGoal";
		public static final String DELETE_NUTRIENT_GOAL = "/nutrientGoal/{id}/delete";

		public static final String ADD_EDIT_RECIPE_NUTRIENT_TYPE = "/recipeNutrientType/addEdit";
		public static final String GET_RECIPE_NUTRIENT_TYPE_BY_ID = "/recipeNutrientType/{id}";
		public static final String GET_RECIPE_NUTRIENT_TYPES = "/recipeNutrientType";
		public static final String DELETE_RECIPE_NUTRIENT_TYPE = "/recipeNutrientType/{id}/delete";

		public static final String ADD_EDIT_NUTRITION_DISEASE = "/nutritionDisease/addEdit";
		public static final String GET_NUTRITION_DISEASE_BY_ID = "/nutritionDisease/{id}";
		public static final String GET_NUTRITION_DISEASES = "/nutritionDisease/get";
		public static final String DELETE_NUTRITION_DISEASE = "/nutritionDisease/{id}/delete";

	}

	public static final String OFFER_URL = BASE_URL + "/offer";

	public interface OfferUrls {

		public static final String ADD_EDIT_OFFER = "/addEdit";
		public static final String GET_OFFERS = "/get";
		public static final String GET_OFFER = "{id}/get";
		public static final String DELETE_OFFER = "{id}/delete";
		public static final String UPLOAD_IMAGE = "image/upload";

	}

	public static final String NEW_FEATURE_URL = BASE_URL + "/newFeature";

	public interface NewFeatureUrls {

		public static final String ADD_EDIT_FEATURE = "/addEdit";
		public static final String GET_FEATURES = "/get";
		public static final String GET_FEATURE = "{id}/get";
		public static final String DELETE_FEATURE = "{id}/delete";

	}

	public static final String TRENDING_URL = BASE_URL + "/trending";

	public interface TrendingUrls {

		public static final String ADD_EDIT_TRENDING = "/addEdit";
		public static final String GET_TRENDINGS = "/get";
		public static final String GET_TRENDING = "{id}/get";
		public static final String DELETE_TRENDING = "{id}/delete";
		public static final String UPDATE_RANK = "rank/{id}/update";
		public static final String COUNT_TRENDING = "/count";

	}

	public static final String CONFERENCE_URL = BASE_URL + "/conference";

	public interface ConferenceUrls {
		public static final String GET_SESSION_TOPICS = "/sessionTopic/get";
		public static final String GET_SESSION_TOPIC = "/sessionTopic/{id}/get";
		public static final String ADD_EDIT_SESSION_TOPIC = "/sessionTopic/addEdit";
		public static final String DELETE_SESSION_TOPIC = "/sessionTopic/{id}/delete";
		public static final String GET_SPEAKER_PROFILES = "/speakerProfile/get";
		public static final String GET_SPEAKER_PROFILE = "/speakerProfile/{id}/get";
		public static final String ADD_EDIT_SPEAKER_PROFILE = "/speakerProfile/addEdit";
		public static final String DELETE_SPEAKER_PROFILE = "/speakerProfile/{id}/delete";
		public static final String GET_DOCTOR_CONFERENCES = "/doctor/get";
		public static final String GET_DOCTOR_CONFERENCE = "/doctor/{id}/get";
		public static final String ADD_EDIT_DOCTOR_CONFERENCE = "/doctor/addEdit";
		public static final String DELETE_DOCTOR_CONFERENCE = "/doctor/{id}/delete";
		public static final String GET_CONFERENCE_SESSIONS = "/session/{conferenceId}/list";
		public static final String GET_CONFERENCE_SESSION = "/session/{id}/get";
		public static final String ADD_EDIT_CONFERENCE_SESSION = "/session/addEdit";
		public static final String DELETE_CONFERENCE_SESSION = "/session/{id}/delete";
		public static final String GET_CONFERENCE_AGENDAS = "/agenda/{conferenceId}/list";
		public static final String GET_CONFERENCE_AGENDA = "/agenda/{id}/get";
		public static final String ADD_EDIT_CONFERENCE_AGENDA = "/agenda/addEdit";
		public static final String DELETE_CONFERENCE_AGENDA = "/agenda/{id}/delete";
		public static final String UPLOAD_CONFERENCE_IMAGE = "/image/upload";
		public static final String GET_CONFERENCE_SESSION_DATES = "/session/{conferenceId}/date";
		public static final String ADD_CONFERENCE_ADMIN = "/admin/addEdit";
		public static final String ACTIVATE_CONFERENCE_USER = "/admin/{userId}/activate";
		public static final String RESET_PASSWORD = "/admin/password/reset";
		public static final String EMAIL_VERIFY_USER = "/verify/{userId}/email";
		public static final String SEND_SMS_VERIFY = "/verify/{userId}/{mobileNumber}/sms";
		public static final String SEND_RESET_PASSWORD_SMS = "/reset/{userId}/{mobileNumber}/password/sms";
		public static final String VERIFY_CONFERENCE = "/verify/{userId}";
		public static final String EMAIL_RESET_PASSWORD = "/reset/password/{userId}/email";
		public static final String UPDATE_STATUS_CONFERENCE = "/status/{id}/{status}/update";

		public static final String ADD_EDIT_CATEGORY = "/category/addEdit";
		public static final String GET_CATEGORIES = "/category/{conferenceId}/list";
		public static final String GET_CATEGORY = "/category/{id}/get";
		public static final String DELETE_CATEGORY = "/category/{id}/delete";

		public static final String ADD_EDIT_SUB_CATEGORY = "/subcategory/addEdit";
		public static final String GET_SUB_CATEGORIES = "/subcategory/{conferenceId}/list";
		public static final String GET_SUB_CATEGORY = "/subcategory/{id}/get";
		public static final String DELETE_SUB_CATEGORY = "/subcategory/{id}/delete";

		public static final String GET_CONFERENCE_USER = "/register/user/{conferenceId}/get";
		public static final String SEND_CONFERENCE_USER_BULK_SMS = "/bulk/sms/send";

	}

	public static final String ORDER_MEDICINE_BASE_URL = BASE_URL + "/order/medicine";

	public interface OrderMedicineUrls {

		public static final String UPLOAD_IMAGE = "/upload/image";
		public static final String MEDICINE_ORDER_ADD_EDIT_RX = "/addEditRx";
		public static final String MEDICINE_ORDER_ADD_EDIT_ADDRESS = "/addEditAddress";
		public static final String MEDICINE_ORDER_ADD_EDIT_PAYMENT = "/addEditPayment";
		public static final String MEDICINE_ORDER_ADD_EDIT_PREFERENCE = "/addEditPreference";
		public static final String DISCARD_MEDICINE_ORDER = "/discard/{id}";
		public static final String UPDATE_STATUS = "/updateStatus/{id}";
		public static final String PATIENT_ORDER_GET_LIST = "/patient/getList/{patientId}";
		public static final String VENDOR_ORDER_GET_LIST = "/vendor/getList/{vendorId}";
		public static final String ORDER_GET_LIST = "/getList";
		public static final String GET_BY_ID = "/get/{id}";
		public static final String ADD_EDIT_VENDOR = "/addEditVendor";
		public static final String GET_VENDOR_BY_ID = "/getVendor/{id}";
		public static final String DISCARD_VENDOR = "/discardVendor/{id}";
		public static final String GET_VENDORS = "/getVendors";
		public static final String ASSIGN_VENDOR = "/assignVendor";
		public static final String GET_DRUG_LIST = "/getDrugList";
		public static final String GET_DELIVERY_BOY_LIST = "/getDeliveryBoyList";
		public static final String GET_DELIVERY_BOY_BY_ID = "/getDeliveryBoyById/{id}";
		public static final String DISCARD_DELIVERY_BOY = "/discardDeliveryBoy/{id}";
		public static final String ADD_EDIT_TRACKING_DETAILS = "/addEditTrackingDetails";
		public static final String GET_TRACKING_DETAILS = "/getTrackingDetails/{orderId}";
		public static final String ASSIGN_DELIVERY_BOY = "/assignDeliveryBoy";
		public static final String EDIT_DELIVERY_BOY = "/editDeliveryBoy";
		public static final String MEDICINE_ORDER_ADD_EDIT_RX_IMAGE = "/addEditRxImage";
		public static final String GET_DRUG_INFO_LIST = "/getDrugInfoList";

	}

	public static final String NUTRITION_REFERENCE_BASE_URL = BASE_URL + "/nutritionReference";

	public interface NutritionReferenceUrl {
		public static final String GET_NUTRITION_REFERENCES = "/get";
		public static final String GET_NUTRITION_ANALYTICS = "/getNutritionAnalytics";
		public static final String GRT_NUTRITION_REFERNCE = "/{id}/get";
		public static final String CHANGE_REFERENCE_STATUS = "/change/{id}/status";
	}

	public static final String RAZORPAY_BASE_URL = BASE_URL + "/razorpay";

	public interface RazorPayUrls {

		public static final String GET_PAYMENTS = "/payments";
		public static final String GET_PAYMENT_BY_ID = "/getPaymentById/{id}";
		public static final String REFUND_PAYMENT = "/refundPayment/{id}";

	}

	public static final String SUPERSTAR_BASE_URL = BASE_URL + "/superstar";

	public interface SuperStarUrls {

		public static final String ADD_EDIT_SCHOOL = "/addEditSchool";
		public static final String GET_SCHOOL_BY_ID = "/getSchoolById/{id}";
		public static final String GET_SCHOOLS = "/getSchools";
		public static final String GET_BRANCHES_FOR_SCHOOL = "/getBranchesForSchool/{schoolId}";
		public static final String VERIFY_SCHOOL = "/verifySchool/{id}";
		public static final String ACTIVATE_SCHOOL = "/activateSchool/{id}";
		public static final String ADD_EDIT_SCHOOL_BRANCH = "/addEditSchoolBranch";
		public static final String GET_SCHOOL_BRANCH_BY_ID = "/getSchoolBranchById/{id}";
		public static final String GET_SCHOOL_BRANCHES = "/getSchoolBranches";
		public static final String DISCARD_SCHOOL_BRANCH = "/discardSchoolBranch/{id}";
		public static final String VERIFY_SCHOOL_BRANCH = "/verifySchoolBranch/{id}";
		public static final String ACTIVATE_SCHOOL_BRANCH = "/activateSchoolBranch/{id}";
		public static final String CHECK_USERNAME_EXISTS = "/checkUserNameExists/{userName}";
		public static final String ADD_EDIT_HEALTHCAMP = "/addEditHealthcamp/";
		public static final String GET_HEALTHCAMP_BY_ID = "/getHealthcampById/{id}";
		public static final String GET_HEALTHCAMPS = "/getHealthcamps";
		public static final String GET_HEALTHCAMPS_BY_DOCTOR_ID = "/getHealthcampByDoctorId/{doctorId}";
		public static final String DISCARD_HEALTHCAMP = "/discardHealthcamp/{id}";
		public static final String ADD_EDIT_NUTRITION_SCHOOL_ASSOCIATION = "/addEditNutritionSchoolAssociation/";
		public static final String GET_NUTRITION_SCHOOL_ASSOCIATIONS = "/getNutritionSchoolAssociations";
		public static final String ADD_EDIT_DOCTOR_SCHOOL_ASSOCIATION = "addEditDoctorSchoolAssociation";
		public static final String GET_DOCTOR_SCHOOL_ASSOCIATIONS = "/getDoctorSchoolAssociations";

	}

	public static final String HAPPINESS_BASE_URL = BASE_URL + "/happiness";

	public interface HappinessUrls {

		public static final String ADD_EDIT_LANGUAGE = "/language/addEdit";
		public static final String GET_LANGUAGE_BY_ID = "/language/{id}";
		public static final String GET_LANGUAGES = "/language/get";
		public static final String DELETE_LANGUAGE = "/language/{id}/delete";

		public static final String ADD_EDIT_ACTIVITY = "/activity/addEdit";
		public static final String GET_ACTIVITY = "/activity/get";
		public static final String DELETE_ACTIVITY = "/activity/{id}/delete";

		public static final String ADD_EDIT_STORIES = "/stories/addEdit";
		public static final String GET_STORIES = "/stories/get";
		public static final String DELETE_STORIES = "/stories/{id}/delete";

		public static final String ADD_EDIT_MINDFULNESS = "/mindfulness/addEdit";
		public static final String GET_MINDFULNESS = "/mindfulness/get";
		public static final String DELETE_MINDFULNESS = "/mindfulness/{id}/delete";

		public static final String UPLOAD_IMAGE = "/uploadImage";
		public static final String UPLOAD_VIDEO = "/uploadVideo";

		public static final String ADD_EDIT_TODAY_SESSION = "/todaysession/addEdit";
		public static final String GET_TODAY_SESSION = "/todaysession/get";
		public static final String DELETE_TODAY_SESSION = "/todaysession/{id}/delete";

		public static final String GET_ACTIVITY_ASSIGN = "/activityAssign/get";
		public static final String ADD_EDIT_ACTIVITY_ASSIGN = "/activityAssign/addEdit";
		public static final String DELETE_ACTIVITY_ASSIGN = "/activityAssign/{id}/delete";

		public static final String GET_STORIES_ASSIGN = "/storiesAssign/get";
		public static final String ADD_EDIT_STORIES_ASSIGN = "/storiesAssign/addEdit";
		public static final String DELETE_STORIES_ASSIGN = "/storiesAssign/{id}/delete";

		public static final String GET_MINDFULNESS_ASSIGN = "/mindfulnessAssign/get";
		public static final String ADD_EDIT_MINDFULNESS_ASSIGN = "/mindfulnessAssign/addEdit";
		public static final String DELETE_MINDFULNESS_ASSIGN = "/mindfulnessAssign/{id}/delete";

	}

	public static final String YOGA_BASE_URL = BASE_URL + "/yoga";

	public interface YogaUrls {

		public static final String ADD_EDIT_YOGA_TEACHER = "/yogaTeacher/addEdit";
		public static final String GET_YOGA_TEACHER_BY_ID = "/yogaTeacher/{id}";
		public static final String GET_YOGA_TEACHER = "/yogaTeacher/get";
		public static final String DELETE_YOGA_TEACHER = "/yogaTeacher/{id}/delete";

		public static final String ADD_EDIT_INJURY = "/injury/addEdit";
		public static final String GET_INJURY_BY_ID = "/injury/{id}";
		public static final String GET_INJURY = "/injury/get";
		public static final String DELETE_INJURY = "/injury/{id}/delete";

		public static final String ADD_EDIT_YOGA_DISEASE = "/yogaDisease/addEdit";
		public static final String GET_YOGA_DISEASE_BY_ID = "/yogaDisease/{id}";
		public static final String GET_YOGA_DISEASE = "/yogaDisease/get";
		public static final String DELETE_YOGA_DISEASE = "/yogaDisease/{id}/delete";

		public static final String ADD_EDIT_YOGA = "/addEdit";
		public static final String GET_YOGA_BY_ID = "/{id}";
		public static final String GET_YOGA = "/get";
		public static final String DELETE_YOGA = "/{id}/delete";

		public static final String ADD_EDIT_YOGA_CLASSES = "/yogaClass/addEdit";
		public static final String GET_YOGA_CLASSES_BY_ID = "/yogaClasses/{id}";
		public static final String GET_YOGA_CLASSES = "/yogaClasses/get";
		public static final String DELETE_YOGA_CLASSES = "/yogaClasses/{id}/delete";

		public static final String ADD_EDIT_YOGA_SESSION = "/yogaSession/addEdit";
		public static final String GET_YOGA_SESSION_BY_ID = "/yogaSession/{id}";
		public static final String GET_YOGA_SESSION = "/yogaSession/get";
		public static final String DELETE_YOGA_SESSION = "/yogaSession/{id}/delete";

		public static final String ADD_EDIT_CURATED_YOGA_SESSION = "/curatedYogaSession/addEdit";
		public static final String GET_CURATED_YOGA_SESSION_BY_ID = "/curatedYogaSession/{id}";
		public static final String GET_CURATED_YOGA_SESSION = "/curatedYogaSession/get";
		public static final String DELETE_CURATED_YOGA_SESSION = "/curatedYogaSession/{id}/delete";

		public static final String ADD_EDIT_ESSENTIAL_YOGA_SESSION = "/essentialsYogaSession/addEdit";
		public static final String GET_ESSENTIAL_YOGA_SESSION_BY_ID = "/essentialsYogaSession/{id}";
		public static final String GET_ESSENTIAL_YOGA_SESSION = "/essentialsYogaSession/get";
		public static final String DELETE_ESSENTIAL_YOGA_SESSION = "/essentialsYogaSession/{id}/delete";

		public static final String UPLOAD_IMAGE = "/uploadImage";
		public static final String UPLOAD_VIDEO = "/uploadVideo";

	}

//public static final String SUBSCRIPTION_BASE_URL = BASE_URL + "/subscription";

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

	public static final String ONLINE_CONSULTATION_PAYMENT_BASE_URL = BASE_URL + "/onlineConsultationPayment";

	public interface OnlinConsultationPaymentUrls {

		public static final String EDIT_ONLINE_CONSULTATION_PAYMENT = "/edit";
		public static final String GET_BANK_DETAILS = "/bankDetails/get";
		public static final String CREATE_ACCOUNT = "/createAccount";
		public static final String GET_DOCTOR_CONSULTATIONS = "/doctorConsultation/get";

		public static final String ADD_EDIT_ONLINE_CONSULTATION_SPECIALITY_PRICE = "/onlineConsultationSpecialityPrice";
		public static final String GET_ONLINE_CONSULTATION_SPECIALITY_PRICE = "/onlineConsultationSpecialityPrice/get";

		public static final String UPDATE_TRANSACTION_STATUS = "/transactionStatus/update";

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

	// covid dashboard
	public static final String COMPANY_DETAILS = BASE_URL + "/company";

	public interface CompanyDetailsUrl {

		public static final String ADD_EDIT_COMPANY_DETAILS = "/addEdit";

		public static final String GET_COMPANY_DETAILS = "/get";

		public static final String GET_COMPANY_DETAIL_BY_ID = "/{companyId}/get";

		public static final String DELETE_COMPANY_DETAILS = "/{companyId}/delete";

		public static final String UPLOAD_IMAGE = "/logo/upload";

		public static final String ACTIVATE_COMPANY_USER = "/admin/{companyId}/activate";

		public static final String GET_MOBILE_NUMBER = "/mobileNumber/get";

		public static final String ADD_EDIT_MOBILE_NUMBER = "/mobileNumber/addEdit";

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

	public static final String COMPANY_LOCATION_BASE_URL = BASE_URL + "/companyLocation";

	public interface CompanyLocationUrls {
		public static final String ADD_EDIT_COMPANY_LOCATION = "/addEdit";
		public static final String GET_COMPANY_LOCATIONS = "/getList/{companyId}";
		public static final String DELETE_COMPANY_LOCATION = "/delete/{id}";

		public static final String GET_EMPLOYEE_COUNT_BY_LOCATION = "/employee/count/get";
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

	public static final String CONSULTATION_PROBLEM_DETAILS_BASE_URL = BASE_URL + "/consultationProblemDetails";

	public interface ConsultationproblemDetailsUrls {

		public static final String ADD_EDIT_CONSULTATION_PROBLEM_DETAILS = "/addEdit";
		public static final String GET_CONSULTATION_PROBLEM_DETAILS = "/get";

	}

	public static final String COMMUNICATION_MODULE_BASE_URL = BASE_URL + "/communication";

	public interface CommunicationModuleUrls {

		public static final String ADD_EDIT_CONSULTATION_COMMUNICATION = "/addEdit";
		public static final String GET_COMMUNICATION_DETAILS_LIST = "/get";
		public static final String GET_COMMUNICATION_DETAILS_GET_BY_DOCTORID = "/{doctorId}/getByDoctorId";

	}

	public static final String HEALTHTHERAPY_URL = BASE_URL + "/healthTherapyPlans";

	public interface HealthTherapyModuleUrls {

		public static final String ADD_EDIT_HEALTHPLAN = "/addEdit";
		public static final String GET_HEALTHPLAN_LIST = "/getList";
		public static final String GET_HEALTHPLAN_GET_BY_ID = "/{planId}/get";
		public static final String DELETE_HEALTHPLAN_GET_BY_ID = "/{planId}/delete";
		public static final String GET_HEALTHPLAN_BY_SLUGURL = "/{slugURL}/{planUId}/get";
		public static final String GET_HEALTHPLANS_TITLE = "/allpackages/get";

		public static final String GET_HEALTHIENS_PLANS = "/{partnerName}/planlist/get";

	}

	public static final String PATIENT_QUERY_BASE_URL = BASE_URL + "/patientQuery";

	public interface PatientQueryUrls {

		public static final String ADD_EDIT_PATIENT_QUERY = "/addEdit";

		public static final String GET_PATIENT_QUERY = "/get";

	}

	public static final String APPOINTMENT_PAYMENT_BASE_URL = BASE_URL + "/appointmentPayment";

	public interface AppointmentPaymentUrls {

		public static final String GET_ANONYMOUS_APPOINTMENT_LIST = "/get";
		public static final String CREATE_TRANSFER = "/transfer";
		public static final String GET_PATIENT_APPOINTMENT_REQUEST = "/appointmentList/get";

	}

	public static final String LABTEST_URL = BASE_URL + "/LabTestPlans";

	public interface LabTestModuleUrls {

		public static final String ADD_EDIT_LAB_TEST = "/addEdit";
		public static final String GET_LAB_TEST_LIST = "/getList";

		public static final String GET_LAB_TEST_PAYMENTLIST = "/payment/getList";

		public static final String GET_LAB_TEST_GET_BY_ID = "/{planId}/get";
		public static final String DELETE_LAB_TEST_GET_BY_ID = "/{planId}/delete";
		public static final String GET_HEALTHIANS_PLANS_RESPONSE = "/allpackages/get";
		public static final String GET_HEALTHIANS_PROD_PLANS_RESPONSE = "/prod/allpackages/get";
		public static final String GET_HEALTHIANS_PROD_PLANS_UPLOAD = "/prod/upload";

		public static final String GET_THYROCARE_PLANS_RESPONSE = "/thyrocareplans/get";

		public static final String GET_LAB_TEST_CUSTOMER_LIST = "/users/getList";
		public static final String GET_LAB_TEST_USER_GET_BY_ID = "/{labTestAppointmentId}/getUser";

		public static final String LAB_TEST_GET_BOOKING_STATUS = "/getBookingStatus";
		public static final String LAB_TEST_GET_BOOKING_STATUS_THYROCARE = "/thyrocare/getBookingStatus";

	}

	public static final String NDHM_BASE_URL = BASE_URL + "/ndhm";

	public interface NdhmUrls {

		public static final String ADD_NDHM_FACILITY = "/facility/add";
		public static final String GET_LIST_STATES = "/states/get";

		public static final String GET_LIST_DISTRICTS = "/districts/get";
		public static final String GET_SESSION = "/session";

		public static final String NOTIFY = "/api/v3/consent/request/hip/notify";

	}

	public static final String RTPCR_BASE_URL = BASE_URL + "/rtpcr";

	public interface RTPCRUrls {

		public static final String ADD_EDIT_RTPCR_TEST = "/addEdit";
		public static final String GET_RTPCR_TEST = "/get";
		public static final String UPLOAD_RTPCR_IMAGE = "/upload";

		public static final String DISCARD_RTPCR_TEST = "/{id}/discard";

	}

	public static final String COMMUNITY_BUILDING_BASE_URL = BASE_URL + "/communityBuilding";

	public interface CommunityBuildingUrls {
		public static final String ADD_EDIT_FORUM = "forum/addEdit";
		public static final String GET_FORUM_RESPONSES = "forum/get";
		public static final String ADD_EDIT_FEEDS = "feeds/addEdit";
		public static final String GET_FEEDS = "feeds/get";
		public static final String GET_ARTICLES = "articles/get";
		public static final String GET_FEEDS_BY_ID = "feeds/{id}/get";
		public static final String GET_FORUM_RESPONSE_BY_ID = "forum/{id}/get";
		public static final String DELETE_FEEDS_BY_ID = "feeds/delete";
		public static final String DELETE_FORUM_BY_ID = "forum/delete";
		public static final String DELETE_COMMENT_BY_ID = "comment/delete";
		public static final String GET_COMMENTS = "comments/get";
		public static final String ADD_EDIT_COMMENTS = "/addEditComments";
	}

	public static final String LANGUAGE_BASE_URL = BASE_URL + "/language";

	public interface LanguageUrls {

		public static final String GET_LANGUAGE_BY_ID = "/{id}/get";
		public static final String GET_LANGUAGES = "/get";
		public static final String ADD_EDIT_LANGUAGE = "/language/addEdit";
		public static final String DELETE_LANGUAGE = "/{id}/delete";
	}

	public static final String SHOPIFY_BASE_URL = "/shopify";

	public interface ShopifyUrls {

		public static final String ADD_EDIT_CUSTOMER = "/customer/addEdit";
		public static final String DELETE_CUSTOMER = "/customer/delete";
		public static final String UPDATE_ORDER_TRANSACTION = "/orderTransaction/update";
	}

	public static final String CUMIN_BASE_URL = "/cumin";

	public interface CuminUrls {
		public static final String JOIN_GROUP = "/group";
		public static final String ADD_GROUP = "/group/add";
		public static final String USER_REGISTRATION = "/registration";
		public static final String UPDATE_ISCUMIN_DOCTOR = "/expert/{locationId}";
		public static final String GET_REGISTER_USER = "/userlist/get";

	}

}