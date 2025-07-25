package com.dpdocter.webservices.v3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.collections.DentalCampCollection;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.collections.DoctorLabReportCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserAddressCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.SmsRoute;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DentalCampRepository;
import com.dpdocter.repository.DoctorLabReportRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientV3Repository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRepositoryForAppointment;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.PatientRegistrationV3Request;
import com.dpdocter.response.CheckPatientSignUpResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.InteraktResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.impl.RegistrationServiceImpl;

import common.util.web.DPDoctorUtils;

@Service
public class RegistrationV3Serviceimpl implements RegistrationV3Service {

	private static Logger logger = LogManager.getLogger(RegistrationServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRepositoryForAppointment userRepositoryForAppoint;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PatientV3Repository patientRepository;

	@Autowired
	private PatientGroupRepository patientGroupRepository;

	@Autowired
	private ReferenceRepository referrenceRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private OTPService otpService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	private ESRegistrationService esRegRistrationService;

	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;

	@Autowired
	private DoctorLabReportRepository doctorLabReportRepository;

	@Autowired
	private DentalCampRepository dentalCampRepository;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Value(value = "${mail.staffmember.account.verify.subject}")
	private String staffmemberAccountVerifySub;

	@Value(value = "${mail.add.existing.doctor.to.clinic.subject}")
	private String addExistingDoctorToClinicSub;

	@Value(value = "${mail.add.doctor.to.clinic..verify.subject}")
	private String addDoctorToClinicVerifySub;

	@Value(value = "${mail.add.feedback.subject}")
	private String addFeedbackSubject;

	@Value(value = "${mail.add.feedback.for.doctor.subject}")
	private String addFeedbackForDoctorSubject;

	@Value(value = "${patient.count}")
	private String patientCount;

	@Value(value = "${Register.checkPatientCount}")
	private String checkPatientCount;

	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${interakt.secret.key}")
	private String secretKey;

	@Autowired
	private SMSServices sMSServices;

	@Override
	public ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient) {
		ESPatientDocument esPatientDocument = null;
		try {
			esPatientDocument = new ESPatientDocument();
			if (patient.getAddress() != null) {
				BeanUtil.map(patient.getAddress(), esPatientDocument);
			}
			if (patient.getPatient() != null) {
				BeanUtil.map(patient.getPatient(), esPatientDocument);
			}
			BeanUtil.map(patient, esPatientDocument);
			if (patient.getBackendPatientId() != null)
				esPatientDocument.setId(patient.getBackendPatientId());
			if (patient.getReferredBy() != null)
				esPatientDocument.setReferredBy(patient.getReferredBy().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esPatientDocument;
	}

	@Override
	public RegisteredPatientDetails registerExistingPatient(PatientRegistrationV3Request request) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		PatientCollection patientCollection = null;
		List<Group> groups = null;
		try {

			if (!DPDoctorUtils.anyStringEmpty(request.getPID())) {
				Integer count = patientRepository.findPatientByPID(new ObjectId(request.getDoctorId()),
						new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()), request.getPID(),
						new ObjectId(request.getId()));
				if (count != null && count > 0) {
					logger.warn("Patient with this PID is already present. Please add patient different PID");
					throw new BusinessException(ServiceError.InvalidInput,
							"Patient with this PID is already present. Please add patient different PID");
				}
			}
			if (request.getDob() != null && request.getDob().getAge() != null
					&& request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.InvalidInput, DOB);
			} else if (request.getDob() == null && request.getAge() != null) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
				request.setDob(new DOB(currentDay, currentMonth, currentYear));
			}
			ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId()))
				userObjectId = new ObjectId(request.getId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			// save Patient Info
			if (DPDoctorUtils.anyStringEmpty(doctorObjectId, hospitalObjectId, locationObjectId)) {
				UserCollection userCollection = userRepository.findById(userObjectId).orElse(null);
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}
				BeanUtil.map(request, userCollection);
				if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName()))
					userCollection.setFirstName(request.getLocalPatientName());

				userCollection.setIsActive(true);
				userCollection.setUpdatedTime(new Date());
				userCollection.setIsDentalChainPatient(true);
				userCollection.setCreatedFrom("WEBSITE");

				BeanUtil.map(userCollection, registeredPatientDetails);
				Criteria criteria = new Criteria();
				if (!DPDoctorUtils.anyStringEmpty(userObjectId))
					criteria.and("userId").is(userObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId))
					criteria.and("locationId").is(locationObjectId);
				if (!DPDoctorUtils.anyStringEmpty(hospitalObjectId))
					criteria.and("hospitalId").is(hospitalObjectId);
				patientCollection = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
						PatientCollection.class, PatientCollection.class).getUniqueMappedResult();
//				patientCollection = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userObjectId,
//						doctorObjectId, locationObjectId, hospitalObjectId);
				System.out.println("patientCollection" + patientCollection);

				if (patientCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
						patientCollection.setLocalPatientName(request.getLocalPatientName());
						patientCollection.setFirstName(request.getLocalPatientName());
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getBloodGroup()))
						patientCollection.setBloodGroup(request.getBloodGroup());
					if (!DPDoctorUtils.anyStringEmpty(request.getGender()))
						patientCollection.setGender(request.getGender());
					if (!DPDoctorUtils.anyStringEmpty(request.getEmailAddress()))
						patientCollection.setEmailAddress(request.getEmailAddress());
					if (request.getDob() != null)
						patientCollection.setDob(request.getDob());
					if (request.getAddress() != null)
						patientCollection.setAddress(request.getAddress());

					if (request.getAddress() != null)
						patientCollection.setAddress(request.getAddress());

					if (!DPDoctorUtils.anyStringEmpty(request.getPID())) {
						patientCollection.setPID(request.getPID());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getLanguage())) {
						patientCollection.setLanguage(request.getLanguage());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getFatherName())) {
						patientCollection.setFatherName(request.getFatherName());
					}

					if (!DPDoctorUtils.anyStringEmpty(request.getMotherName())) {
						patientCollection.setMotherName(request.getMotherName());
					}

					patientCollection.setIsChild(request.getIsChild());
					// patientCollection.setRegistrationDate(request.getRegistrationDate());
				} else {
					logger.error("Incorrect User Id, DoctorId, LocationId, HospitalId");
					throw new BusinessException(ServiceError.InvalidInput,
							"Incorrect User Id, DoctorId, LocationId, HospitalId");
				}

				List<ObjectId> treatmentIds = null;
				treatmentIds = userCollection.getTreatmentId();
				if (treatmentIds != null) {
					if (request.getTreatmentId() == null) {
						userCollection.setTreatmentId(treatmentIds);
					} else {
						treatmentIds.clear();
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						userCollection.setTreatmentId(treatmentIds);
					}
				} else {
					if (request.getTreatmentId() == null) {
						userCollection.setTreatmentId(null);
					} else {
						treatmentIds = new ArrayList<ObjectId>();
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						userCollection.setTreatmentId(treatmentIds);
					}
				}
				registeredPatientDetails.getTreatmentId().clear();
				BeanUtil.map(userCollection, registeredPatientDetails);

				if (request.getImage() != null) {
					String path = "profile-images";
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
				}
				// patientCollection.setRegistrationDate(request.getRegistrationDate());
				patientCollection.setUpdatedTime(new Date());
				patientCollection.setCreatedTime(patientCollection.getCreatedTime());
				userCollection.setCreatedBy(request.getCreatedBy());
				userCollection.setCreatedTime(userCollection.getCreatedTime());
				userCollection = userRepository.save(userCollection);
				patientCollection = patientRepository.save(patientCollection);

				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				patient.setPatientId(userCollection.getId().toString());
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				registeredPatientDetails.setPNUM(patientCollection.getPNUM());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
				registeredPatientDetails.setIsChild(patientCollection.getIsChild());
			} else {
				System.out.println("false");

				List<PatientCollection> patientCollections = patientRepository
						.findByUserIdAndLocationIdAndHospitalId(userObjectId, locationObjectId, hospitalObjectId);
				if (patientCollections != null && !patientCollections.isEmpty())
					patientCollection = patientCollections.get(0);
				String PID = request.getPID(), PNUM = request.getPNUM();
				if (patientCollection != null) {
					ObjectId patientId = patientCollection.getId();
					ObjectId patientDoctorId = patientCollection.getDoctorId();
					if (DPDoctorUtils.anyStringEmpty(request.getPID()))
						PID = patientCollection.getPID();
					if (DPDoctorUtils.anyStringEmpty(request.getPNUM()))
						PNUM = patientCollection.getPNUM();

					// request.setRegistrationDate(patientCollection.getRegistrationDate());
					BeanUtil.map(request, patientCollection);
					patientCollection.setId(patientId);
					patientCollection.setUpdatedTime(new Date());
					patientCollection.setDoctorId(patientDoctorId);
					if (request.getRegistrationDate() != null)
						patientCollection.setRegistrationDate(request.getRegistrationDate());
					else
						patientCollection.setRegistrationDate(new Date().getTime());

				} else {
					patientCollection = new PatientCollection();
					patientCollection.setCreatedTime(new Date());
					BeanUtil.map(request, patientCollection);
					if (request.getRegistrationDate() != null)
						patientCollection.setRegistrationDate(request.getRegistrationDate());
					else
						patientCollection.setRegistrationDate(new Date().getTime());
				}

				patientCollection.setRelations(request.getRelations());
				patientCollection.setNotes(request.getNotes());

				Map<String, String> generatedId = patientIdGenerator(request.getLocationId(), request.getHospitalId(),
						patientCollection.getRegistrationDate(), PID, PNUM);

				patientCollection.setPID(generatedId.get("PID"));
				patientCollection.setPNUM(generatedId.get("PNUM"));

				if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
					patientCollection.setProfession(request.getProfession());
				}

				ReferencesCollection referencesCollection = null;
				if (request.getReferredBy() != null) {
					if (request.getReferredBy().getId() != null) {
						referencesCollection = referrenceRepository
								.findById(new ObjectId(request.getReferredBy().getId())).orElse(null);
					}
					if (referencesCollection == null) {
						referencesCollection = new ReferencesCollection();
						BeanUtil.map(request.getReferredBy(), referencesCollection);
						BeanUtil.map(request, referencesCollection);
						referencesCollection.setId(null);
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
							referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
							referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
							referencesCollection.setLocationId(new ObjectId(request.getLocationId()));
						referencesCollection = referrenceRepository.save(referencesCollection);
						transnationalService.addResource(referencesCollection.getId(), Resource.REFERENCE, false);
						ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
						BeanUtil.map(referencesCollection, esReferenceDocument);
						esRegRistrationService.addEditReference(esReferenceDocument);
					}
					patientCollection.setReferredBy(referencesCollection.getId());
				}

				// assign groups
				if (request.getGroups() != null) {

					List<String> groupIds = new ArrayList<String>();
					List<PatientGroupCollection> patientGroupCollections = patientGroupRepository
							.findByPatientIdAndDiscardedIsFalse(patientCollection.getUserId());
					if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
						for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
							if (request.getGroups() != null && !request.getGroups().isEmpty()) {
								groupIds.add(patientGroupCollection.getGroupId().toString());
								if (!request.getGroups().contains(patientGroupCollection.getGroupId().toString())) {
									patientGroupRepository.delete(patientGroupCollection);
								}
							} else {
								patientGroupRepository.delete(patientGroupCollection);
							}
						}
					}

					if (request.getGroups() != null && !request.getGroups().isEmpty()) {
						for (String group : request.getGroups()) {
							if (!groupIds.contains(group)) {
								PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
								patientGroupCollection.setGroupId(new ObjectId(group));
								patientGroupCollection.setPatientId(patientCollection.getUserId());
								patientGroupRepository.save(patientGroupCollection);
							}
						}
					}
				}
				UserCollection userCollection = userRepository.findById(userObjectId).orElse(null);
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}

				List<ObjectId> treatmentIds = null;
				treatmentIds = userCollection.getTreatmentId();
				if (treatmentIds != null) {
					if (request.getTreatmentId() == null) {
						userCollection.setTreatmentId(treatmentIds);
					} else {
						treatmentIds.clear();
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						userCollection.setTreatmentId(treatmentIds);
					}
				} else {
					if (request.getTreatmentId() == null) {
						userCollection.setTreatmentId(null);
					} else {
						treatmentIds = new ArrayList<ObjectId>();
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						userCollection.setTreatmentId(treatmentIds);
					}
				}
				if (registeredPatientDetails.getTreatmentId() != null)
					registeredPatientDetails.getTreatmentId().clear();
				/* registeredPatientDetails = new RegisteredPatientDetails(); */
				BeanUtil.map(userCollection, registeredPatientDetails);
				if (request.getImage() != null) {
					String path = "profile-images";
					// save image
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					registeredPatientDetails.setImageUrl(imageURLResponse.getImageUrl());
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
					registeredPatientDetails.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
				}
				BeanUtil.map(request, userCollection);

				userCollection.setFirstName(request.getLocalPatientName());
				userCollection.setUpdatedTime(new Date());
				userCollection.setCreatedTime(userCollection.getCreatedTime());

				userCollection = userRepository.save(userCollection);
				registeredPatientDetails.setCreatedBy(request.getCreatedBy());
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				patientCollection.setFirstName(userCollection.getFirstName());

				// if(RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())){
				List<ObjectId> consultantDoctorIds = patientCollection.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(new ObjectId(request.getDoctorId())))
					consultantDoctorIds.add(new ObjectId(request.getDoctorId()));
				patientCollection.setConsultantDoctorIds(consultantDoctorIds);
				// }

				patientCollection = patientRepository.save(patientCollection);
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				patient.setPatientId(userCollection.getId().toString());

				Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
				} else {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherLocations(userCollection.getId(),
							patientCollection.getHospitalId(), patientCollection.getLocationId());
				}

				if ((prescriptionCount != null && prescriptionCount > 0)
						|| (clinicalNotesCount != null && clinicalNotesCount > 0)
						|| (recordsCount != null && recordsCount > 0))
					patient.setIsDataAvailableWithOtherDoctor(true);

				patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(),
						patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(),
						userCollection.getId().toString()));

				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				registeredPatientDetails.setPNUM(patientCollection.getPNUM());
				registeredPatientDetails.setConsultantDoctorIds(patient.getConsultantDoctorIds());
				registeredPatientDetails.setIsChild(patientCollection.getIsChild());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				if (referencesCollection != null) {
					Reference reference = new Reference();
					BeanUtil.map(referencesCollection, reference);
					registeredPatientDetails.setReferredBy(reference);
				}
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				if (request.getGroups() != null) {
					List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
					for (String groupId : request.getGroups())
						groupObjectIds.add(new ObjectId(groupId));
					groups = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))),
							GroupCollection.class, Group.class).getMappedResults();
					registeredPatientDetails.setGroups(groups);
				}

			}

			List<String> dentalTreatment = null;
			if (registeredPatientDetails.getTreatmentId() != null) {
				List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation
										.match(new Criteria("id").in(registeredPatientDetails.getTreatmentId()))),
								DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
						.getMappedResults();
				dentalTreatment = new ArrayList<String>();
				for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
					dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
				}
				registeredPatientDetails.setTreatmentNames(dentalTreatment);
			}

			if (request.getSmileBuddyId() != null) {
				UserCollection userCollection = userRepository
						.findById(new ObjectId(registeredPatientDetails.getSmileBuddyId())).orElse(null);
				if (userCollection != null)
					registeredPatientDetails.setSmileBuddyName(userCollection.getFirstName());
			}

			// set flag for camp user
			if (!DPDoctorUtils.anyStringEmpty(request.getCampUserId())) {
				DentalCampCollection dentalCampCollection = dentalCampRepository
						.findById(new ObjectId(request.getCampUserId())).orElse(null);
				if (dentalCampCollection != null) {
					dentalCampCollection.setUpdatedTime(new Date());
					dentalCampCollection.setIsPatientCreated(true);
					dentalCampCollection = dentalCampRepository.save(dentalCampCollection);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	private void sendWelcomeMessageToPatient(PatientCollection patientCollection, String mobileNumber) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(patientCollection.getDoctorId());
		smsTrackDetail.setHospitalId(patientCollection.getHospitalId());
		smsTrackDetail.setLocationId(patientCollection.getLocationId());
		smsTrackDetail.setType(SmsRoute.TXN.getType());
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(patientCollection.getUserId());
		SMS sms = new SMS();
		String patientName = "";
		if (DPDoctorUtils.anyStringEmpty(patientCollection.getLocalPatientName()))
			patientName = patientCollection.getLocalPatientName();

		String text = "";

		text = "Welcome " + patientName + " to " + "Smilebird" + "."
				+ " Thanks for choosing us, our team will contact you soon to confirm your appointment." + "\n"
				+ "Team Smilebird";
		smsDetail.setUserName(patientName);
		smsTrackDetail.setTemplateId("1307165054366944223");

		sms.setSmsText(text);

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		sMSServices.sendDentalChainSMS(smsTrackDetail, true);

	}

	private Map<String, String> patientIdGenerator(String locationId, String hospitalId, Long registrationDate,
			String PID, String PNUM) {
		Map<String, String> generatedId = new HashMap<String, String>();
		try {

			if (DPDoctorUtils.anyStringEmpty(PID, PNUM)) {
				ObjectId locationObjectId = null, hospitalObjectId = null;
				if (!DPDoctorUtils.anyStringEmpty(locationId))
					locationObjectId = new ObjectId(locationId);
				if (!DPDoctorUtils.anyStringEmpty(hospitalId))
					hospitalObjectId = new ObjectId(hospitalId);

				LocationCollection location = locationRepository.findById(locationObjectId).orElse(null);
				if (location == null) {
					logger.warn("Invalid Location Id");
					throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
				}

				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				if (registrationDate != null) {
					localCalendar.setTime(new Date(registrationDate));
				} else {
					localCalendar.setTime(new Date());
				}

				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				Long startTimeinMillis = start.getMillis();
				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				Long endTimeinMillis = end.getMillis();
				Integer patientSize = patientRepository.findTodaysRegisteredPatient(locationObjectId, hospitalObjectId,
						startTimeinMillis, endTimeinMillis);

				if (patientSize == null)
					patientSize = 0;

				String patientInitial = location.getPatientInitial();

				if (DPDoctorUtils.anyStringEmpty(PID)) {
					PID = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay)
							+ DPDoctorUtils.getPrefixedNumber(currentMonth)
							+ DPDoctorUtils.getPrefixedNumber(currentYear % 100)
							+ DPDoctorUtils.getPrefixedNumber(patientSize + 1);
				}

				if (DPDoctorUtils.anyStringEmpty(PNUM)) {
					int patientCounter = location.getPatientCounter();
					List<PatientCollection> patientCollections = patientRepository
							.findByLocationIdAndHospitalIdAndPNUMNotNull(locationObjectId, hospitalObjectId,
									PageRequest.of(0, 1, Direction.DESC, "createdTime"));
					if (patientCollections != null && !patientCollections.isEmpty()) {
						PatientCollection patientCollection = patientCollections.get(0);
						String lastRegisterdPatientPNUM = patientCollection.getPNUM().replaceAll("[a-zA-Z\\s\\W_]", "");
						Integer lastRegisterdPatientPNUMCount = 0;
						if (!DPDoctorUtils.anyStringEmpty(lastRegisterdPatientPNUM))
							lastRegisterdPatientPNUMCount = Integer.parseInt(lastRegisterdPatientPNUM);
						if (lastRegisterdPatientPNUMCount < patientCounter) {
							patientSize = patientCounter;
						} else {
							patientSize = lastRegisterdPatientPNUMCount + 1;
						}
					}
					PNUM = patientInitial + patientSize;
				}
			}
			generatedId.put("PID", PID);
			generatedId.put("PNUM", PNUM);
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generatedId;
	}

	public RegisteredPatientDetails registerNewPatient(PatientRegistrationV3Request request) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		
		return registeredPatientDetails;
	}

	private CheckPatientSignUpResponse checkIfPatientIsSignedUp(String mobileNumber) {
		CheckPatientSignUpResponse response = null;
		try {
			List<UserCollection> userCollections = userRepositoryForAppoint.findByMobileNumberAndUserState(mobileNumber,
					UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (userCollection.getEmailAddress() != null) {
						if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
							if (userCollection.isSignedUp()) {
								response = new CheckPatientSignUpResponse(userCollection.getPassword(),
										userCollection.getSalt(), userCollection.isSignedUp());
							}
							break;
						}
					} else {
						if (userCollection.isSignedUp()) {
							response = new CheckPatientSignUpResponse(userCollection.getPassword(),
									userCollection.getSalt(), userCollection.isSignedUp());
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public void checkPatientCount(String mobileNumber) {

		int count = 0;

		List<UserCollection> userCollections = null;
		Criteria criteria = new Criteria();
		if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
			criteria.and("mobileNumber").is(mobileNumber);

		criteria.and("userState").is(UserState.USERSTATECOMPLETE.getState());
		criteria.and("isDentalChainPatient").is(true);
		userCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
				UserCollection.class, UserCollection.class).getMappedResults();

		if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
				if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
					count++;
			}
			if (count >= Integer.parseInt(patientCount)) {
				logger.warn(checkPatientCount);
				throw new BusinessException(ServiceError.Unknown, checkPatientCount);
			}
		}
	}

	@Override
	public List<RegisteredPatientDetails> getPatientsByPhoneNumber(Boolean isDentalChainPatient, String mobileNumber) {
		List<RegisteredPatientDetails> users = null;
		try {
			List<UserCollection> userCollections = null;
			Criteria criteria = new Criteria();
			if (isDentalChainPatient != null)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				criteria.and("mobileNumber").is(mobileNumber);

			criteria.and("userState").is(UserState.USERSTATECOMPLETE.getState());

			userCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					UserCollection.class, UserCollection.class).getMappedResults();

			if (userCollections != null) {
				users = new ArrayList<RegisteredPatientDetails>();
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						Patient patient = new Patient();
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null,
										null);

						if (patientCollection != null) {
							BeanUtil.map(patientCollection, patient);
							BeanUtil.map(patientCollection, user);
							BeanUtil.map(userCollection, user);
							if (patientCollection.getLocationId() != null) {
								user.setLocationId(patientCollection.getLocationId().toString());
							}
							if (patientCollection.getHospitalId() != null) {
								user.setHospitalId(patientCollection.getHospitalId().toString());
							}
							user.setImageUrl(patientCollection.getImageUrl());
							user.setThumbnailUrl(patientCollection.getThumbnailUrl());
						} else {
							BeanUtil.map(userCollection, user);
						}
						patient.setPatientId(userCollection.getId().toString());
						user.setPatient(patient);
						users.add(user);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}

	@Override
	public List<UserAddress> getUserAddress(String userId, String mobileNumber, Boolean discarded) {
		List<UserAddress> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria.and("userIds").is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				criteria.and("mobileNumber").is(mobileNumber);

			if (!discarded)
				criteria.and("discarded").is(discarded);

			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					UserAddressCollection.class, UserAddress.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (UserAddress userAddress : response) {
					Address address = userAddress.getAddress();
					String formattedAddress = (!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
							? address.getStreetAddress() + ", "
							: "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
									? address.getLandmarkDetails() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ? address.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode() : "");

					if (formattedAddress.charAt(formattedAddress.length() - 2) == ',') {
						formattedAddress = formattedAddress.substring(0, formattedAddress.length() - 2);
					}
					userAddress.setFormattedAddress(formattedAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting user address");
		}
		return response;
	}

	@Override
	public Boolean updatePatientContactState(String patientId, String contactState) {
		Boolean response = false;
		UserCollection userCollection = null;
		try {
			userCollection = userRepository.findById(new ObjectId(patientId)).orElse(null);
			if (userCollection != null) {
				userCollection.setContactState(ContactState.valueOf(contactState));
				userCollection.setUpdatedTime(new Date());
				userRepository.save(userCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public void addUserToInretackt(PatientRegistrationV3Request request) {
		try {
			JSONObject requestObject1 = new JSONObject();

			JSONObject requestObject2 = new JSONObject();
			requestObject1.put("phoneNumber", request.getMobileNumber());
			requestObject1.put("countryCode", "+91");

			requestObject2.put("name", request.getFirstName());
			requestObject2.put("email", request.getEmailAddress());
			requestObject1.put("traits", requestObject2);

			InputStream is = null;
			URL url = new URL("https://api.interakt.ai/v1/public/message/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Basic " + secretKey);
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			System.out.println(requestObject1);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(requestObject1.toString());
			wr.close();

			// Get Response

			try {
				is = connection.getInputStream();
			} catch (IOException ioe) {
				if (connection instanceof HttpURLConnection) {
					HttpURLConnection httpConn = (HttpURLConnection) connection;
					int statusCode = httpConn.getResponseCode();
					if (statusCode != 200) {
						is = httpConn.getErrorStream();
					}
				}
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			System.out.println("http response" + response.toString());

			ObjectMapper mapper = new ObjectMapper();

			InteraktResponse interaktResponse = mapper.readValue(response.toString(), InteraktResponse.class);
			if (!interaktResponse.getResult()) {
				logger.warn("Error while sending message :" + interaktResponse.getMessage());
				throw new BusinessException(ServiceError.Unknown,
						"Error while sending message:" + interaktResponse.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();

		}

	}

	@Override
	public Boolean updatePatientFollowUp(String patientId, String followUp, String followUpReason) {
		Boolean response = false;
		UserCollection userCollection = null;
		try {
			userCollection = userRepository.findById(new ObjectId(patientId)).orElse(null);
			if (userCollection != null) {
				Date date = new Date(Long.parseLong(followUp));
				userCollection.setFollowUp(date);
				userCollection.setFollowUpReason(followUpReason);
				userCollection.setUpdatedTime(new Date());
				userRepository.save(userCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

}
