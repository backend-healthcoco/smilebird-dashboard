package com.dpdocter.webservices.v3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.AppointmentLookupResponse;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Slot;
import com.dpdocter.beans.User;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.AppointmentWorkFlowCollection;
import com.dpdocter.collections.DentalCampCollection;
import com.dpdocter.collections.DentalChainCityCollection;
import com.dpdocter.collections.DentalChainLandmarkLocalityCollection;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.BroadcastType;
import com.dpdocter.enums.DateFilterType;
import com.dpdocter.enums.DentalAppointmentType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSContent;
import com.dpdocter.enums.SMSFormatType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.AppointmentWorkFlowRepository;
import com.dpdocter.repository.CampMsgTemplateRepository;
import com.dpdocter.repository.DentalChainCityRepository;
import com.dpdocter.repository.DentalChainLandmarkLocalityRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.BroadcastByTreatmentRequest;
import com.dpdocter.request.DentalChainAppointmentRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.CampMsgTemplateResponse;
import com.dpdocter.response.InteraktResponse;
import com.dpdocter.response.PatientAppUsersResponse;
import com.dpdocter.response.PatientTimelineHistoryResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.SmilebirdAppoinmentLookupResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;
import common.util.web.Response;

@Service
public class AppointmentV3ServiceImpl implements AppointmentV3Service {

	private static Logger logger = LogManager.getLogger(AppointmentV3ServiceImpl.class.getName());

	@Autowired
	private DentalChainCityRepository cityRepository;

	@Autowired
	private DentalChainLandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Appointment.timeSlotIsBooked}")
	private String timeSlotIsBooked;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	UserRoleRepository UserRoleRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ESPatientRepository esPatientRepository;

	@Autowired
	private AppointmentWorkFlowRepository appointmentWorkFlowRepository;

	@Value(value = "${Appointment.incorrectAppointmentId}")
	private String incorrectAppointmentId;

	@Value(value = "${mail.appointment.cancel.subject}")
	private String appointmentCancelMailSubject;

	@Value(value = "${mail.appointment.confirm.to.doctor.subject}")
	private String appointmentConfirmToDoctorMailSubject;

	@Value(value = "${mail.appointment.request.to.doctor.subject}")
	private String appointmentRequestToDoctorMailSubject;

	@Value(value = "${mail.appointment.confirm.to.patient.subject}")
	private String appointmentConfirmToPatientMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.doctor.subject}")
	private String appointmentRescheduleToDoctorMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.patient.subject}")
	private String appointmentRescheduleToPatientMailSubject;

	@Value(value = "${smilebird.support.number}")
	private String smilebirdSupportNumber;

	@Value(value = "${interakt.secret.key}")
	private String secretKey;

	@Autowired
	private CampMsgTemplateRepository msgTemplateRepository;

	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Override
	public Appointment updateAppointment(DentalChainAppointmentRequest request) {
		Appointment response = null;
		List<String> dentalTreatment = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getType().getType())) {

				request.setType(AppointmentType.APPOINTMENT);
			}
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(request.getAppointmentId())),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"));
			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, AppointmentLookupResponse.class)
					.getUniqueMappedResult();
			if (appointmentLookupResponse != null) {
				AppointmentCollection appointmentCollection = new AppointmentCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentCollection);
				PatientCard patientCard = null;
				List<PatientCard> patientCards = null;
				if (!DPDoctorUtils.allStringsEmpty(request.getPatientId())) {
					patientCards = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
											.and("locationId").is(new ObjectId(request.getLocationId()))
											.and("hospitalId").is(new ObjectId(request.getHospitalId()))),
									Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
							PatientCollection.class, PatientCard.class).getMappedResults();
					if (patientCards != null && !patientCards.isEmpty())
						patientCard = patientCards.get(0);
					appointmentCollection.setLocalPatientName(patientCard.getLocalPatientName());
				} else {
					appointmentCollection.setLocalPatientName(request.getLocalPatientName());
				}
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getLocationId())).orElse(null);

				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();

				AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentWorkFlowCollection);
				appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

				appointmentCollection.setState(request.getState());

				if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
					if (request.getCancelledBy() != null) {
						if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.ADMIN.getType())) {
							appointmentCollection.setCancelledBy(AppointmentCreatedBy.ADMIN.getType());
							appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.ADMIN.getType());
						} else if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType())) {
							appointmentCollection.setCancelledBy(appointmentLookupResponse.getDoctor().getTitle() + " "
									+ appointmentLookupResponse.getDoctor().getFirstName());
							appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.DOCTOR.getType());
						} else {
							appointmentCollection.setCancelledBy(patientCard.getLocalPatientName());
							appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.PATIENT.getType());
						}
					}
					AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
							.findByAppointmentId(request.getAppointmentId());
					if (bookedSlotCollection != null)
						appointmentBookedSlotRepository.delete(bookedSlotCollection);
				} else if (request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())) {
					if (request.getRescheduleBy() != null) {
						if (request.getRescheduleBy().equalsIgnoreCase(AppointmentCreatedBy.ADMIN.getType())) {
							appointmentCollection.setRescheduleBy(AppointmentCreatedBy.ADMIN.getType());
							appointmentCollection.setRescheduleByProfile(AppointmentCreatedBy.ADMIN.getType());
						} else if (request.getRescheduleBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType())) {
							appointmentCollection.setRescheduleBy(appointmentLookupResponse.getDoctor().getTitle() + " "
									+ appointmentLookupResponse.getDoctor().getFirstName());
							appointmentCollection.setRescheduleByProfile(AppointmentCreatedBy.DOCTOR.getType());
						} else {
							appointmentCollection.setRescheduleBy(patientCard.getLocalPatientName());
							appointmentCollection.setRescheduleByProfile(AppointmentCreatedBy.PATIENT.getType());
						}
					}

					appointmentCollection.setFromDate(request.getFromDate());
					appointmentCollection.setToDate(request.getToDate());
					appointmentCollection.setTime(request.getTime());
					appointmentCollection.setIsRescheduled(true);
					appointmentCollection.setState(AppointmentState.CONFIRM);
					AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
							.findByAppointmentId(request.getAppointmentId());
					if (bookedSlotCollection != null) {
						bookedSlotCollection.setDoctorId(new ObjectId(request.getDoctorId()));
						bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
						bookedSlotCollection.setToDate(appointmentCollection.getToDate());
						bookedSlotCollection.setTime(request.getTime());
						bookedSlotCollection.setUpdatedTime(new Date());
						appointmentBookedSlotRepository.save(bookedSlotCollection);
					}

					if (!request.getDoctorId().equalsIgnoreCase(appointmentLookupResponse.getDoctorId())) {
						appointmentCollection.setDoctorId(new ObjectId(request.getDoctorId()));
						User drCollection = mongoTemplate.aggregate(
								Aggregation.newAggregation(
										Aggregation.match(new Criteria("id").is(appointmentCollection.getDoctorId()))),
								UserCollection.class, User.class).getUniqueMappedResult();
						appointmentLookupResponse.setDoctor(drCollection);
					}
				}

				DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(appointmentCollection.getDoctorId(),
								appointmentCollection.getLocationId());
				appointmentCollection.setIsDentalChainAppointment(true);

				appointmentCollection.setNotifyDoctorByEmail(request.getNotifyDoctorByEmail());
				appointmentCollection.setNotifyDoctorBySms(request.getNotifyDoctorBySms());
				appointmentCollection.setNotifyPatientByEmail(request.getNotifyPatientByEmail());
				appointmentCollection.setNotifyPatientBySms(request.getNotifyPatientByEmail());
				appointmentCollection.setUpdatedTime(new Date());
				List<ObjectId> treatmentIds = null;
				treatmentIds = appointmentCollection.getTreatmentId();
				if (treatmentIds != null) {
					if (request.getTreatmentId() == null) {
						appointmentCollection.setTreatmentId(treatmentIds);
					} else {
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						appointmentCollection.setTreatmentId(treatmentIds);
					}
				} else {
					if (request.getTreatmentId() == null) {
						appointmentCollection.setTreatmentId(null);
					} else {
						treatmentIds = new ArrayList<ObjectId>();
						for (String treatmentId : request.getTreatmentId()) {
							treatmentIds.add(new ObjectId(treatmentId));
						}
						appointmentCollection.setTreatmentId(treatmentIds);
					}
				}
				if (request.getType() != null) {
					appointmentCollection.setType(request.getType());
				} else {
					appointmentCollection.setType(appointmentCollection.getType());
				}
				if (DPDoctorUtils.anyStringEmpty(appointmentCollection.getCreatedBy()))
					appointmentCollection.setCreatedBy(request.getCreatedBy());
				appointmentCollection = appointmentRepository.save(appointmentCollection);

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
				String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
						appointmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				if (clinicProfileCollection != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				} else {
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}

				Date _24HourDt = _24HourSDF.parse(_24HourTime);
				final String locationMapUrl = locationCollection.getGoogleMapShortUrl();

				final String patientName = (patientCard != null ? patientCard.getFirstName() : "");
				final String appointmentId = appointmentCollection.getAppointmentId();
				final String time = _12HourSDF.format(_24HourDt);
				final String date = sdf.format(appointmentCollection.getFromDate());
				final String branch = appointmentLookupResponse.getBranch();
				final String clinicName = appointmentLookupResponse.getLocation().getLocationName();
				final String clinicContactNum = appointmentLookupResponse.getLocation().getClinicNumber() != null
						? appointmentLookupResponse.getLocation().getClinicNumber()
						: "";
				UserCollection user = userRepository.findById(new ObjectId(patientCard.getUserId())).orElse(null);

				// sendSMS after appointment is saved
				final String id = appointmentCollection.getId().toString(),
						patientEmailAddress = user != null ? user.getEmailAddress() : null,
						patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
						patientWhatsAppNumber = patientCard != null ? patientCard.getUser().getWhatsAppNumber() : null,
						patientLanguage = user != null ? user.getLanguage() : null,
						doctorEmailAddress = appointmentLookupResponse.getDoctor().getEmailAddress(),
						doctorMobileNumber = appointmentLookupResponse.getDoctor().getMobileNumber();
				final DoctorFacility facility = (clinicProfileCollection != null)
						? clinicProfileCollection.getFacility()
						: null;
				System.out.println("patientEmailAddress" + patientEmailAddress);
				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							sendAppointmentEmailSmsNotification(false, request, id, appointmentId, doctorName,
									patientName, time, date, clinicName, clinicContactNum, patientEmailAddress,
									patientMobileNumber, patientWhatsAppNumber, doctorEmailAddress, doctorMobileNumber,
									facility, branch, locationMapUrl, patientLanguage);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
				if (patientCard != null) {
					patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
					patientCard.getUser().setLocationId(patientCard.getLocationId());
					patientCard.getUser().setHospitalId(patientCard.getHospitalId());
					BeanUtil.map(patientCard.getUser(), patientCard);
					patientCard.setUserId(patientCard.getUserId());
					patientCard.setId(patientCard.getUserId());
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
					patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
				} else {
					patientCard = new PatientCard();
					patientCard.setLocalPatientName(request.getLocalPatientName());
				}
				response.setPatient(patientCard);

				response.setDoctorName(doctorName);
				if (appointmentLookupResponse.getLocation() != null) {
					response.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
					response.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

					String address = (!DPDoctorUtils
							.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
									? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
											? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
											: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
									? appointmentLookupResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
									? appointmentLookupResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
									? appointmentLookupResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
									? appointmentLookupResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
									? appointmentLookupResponse.getLocation().getPostalCode()
									: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					response.setClinicAddress(address);
					response.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
					response.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
				}

				if (request.getSmileBuddyId() != null) {
					UserCollection smileBuddyUserCollection = userRepository
							.findById(new ObjectId(request.getSmileBuddyId())).orElse(null);
					if (smileBuddyUserCollection != null)
						response.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
				}

				if (treatmentIds != null) {
					List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(treatmentIds))),
									DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
							.getMappedResults();
					dentalTreatment = new ArrayList<String>();
					for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
						dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
					}
					response.setTreatmentNames(dentalTreatment);
				}
			} else {
				logger.error(incorrectAppointmentId);
				throw new BusinessException(ServiceError.InvalidInput, incorrectAppointmentId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public Clinic getDentalClinicById(String locationId, Boolean active) {
		Clinic response = new Clinic();
		LocationCollection localtionCollection = null;
		Location location = new Location();
		HospitalCollection hospitalCollection = null;
		Hospital hospital = new Hospital();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			localtionCollection = locationRepository.findByIdAndIsActivate(new ObjectId(locationId), active);
			if (localtionCollection == null) {
				return null;
			} else {
				BeanUtil.map(localtionCollection, location);
				response.setLocation(location);

				hospitalCollection = hospitalRepository.findById(localtionCollection.getHospitalId()).orElse(null);
				if (hospitalCollection != null) {
					BeanUtil.map(hospitalCollection, hospital);
					response.setHospital(hospital);
				}

				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationIdAndIsActivate(localtionCollection.getId(), active);
				for (Iterator<DoctorClinicProfileCollection> iterator = doctorClinicProfileCollections
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorRepository
							.findByUserId(doctorClinicProfileCollection.getDoctorId());
					UserCollection userCollection = userRepository.findById(doctorClinicProfileCollection.getDoctorId())
							.orElse(null);

					if (doctorCollection != null && userCollection != null && userCollection.getUserState().getState()
							.equals(UserState.USERSTATECOMPLETE.getState())) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

							if (doctorClinicProfile.getClinicOwnershipImageUrl() != null) {
								doctorClinicProfile.setClinicOwnershipImageUrl(
										getFinalImageURL(doctorClinicProfile.getClinicOwnershipImageUrl()));
							}

							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("superSpeciality"));
							doctor.setSpecialities(specialities);
						}
						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	/*
	 * slots are divided in such a way that we slice available time from 'fromTime
	 * of working hours' to 'start time of booked slots' & not available 'start Time
	 * of booked slotss' to 'end time of booked slots' & assign startTime = end time
	 * of booked slots. After booked slots completed again slicing available time
	 * from 'endTime of last booked slots i.e. now start time' to 'to Time of
	 * working hours
	 */
	@Override
	@Transactional
	public SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date, Boolean isPatient) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		SlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				if (!isPatient) {
					UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdAndLocationId(doctorObjectId,
							locationObjectId);
					if (userRoleCollection != null) {
						RoleCollection roleCollection = roleRepository.findById(userRoleCollection.getId())
								.orElse(null);
						if (roleCollection != null)
							if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.RECEPTIONIST_NURSE.getRole())
									|| roleCollection.getRole().equalsIgnoreCase("RECEPTIONIST")) {
								throw new BusinessException(ServiceError.NotAuthorized,
										"You are not authorized to have slots.");
							}
					}
				}
				Integer startTime = 0, endTime = 0;
				float slotTime = 15;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				String day = sdf.format(date);
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
					if (slotTime == 0.0)
						slotTime = 15;

					response = new SlotDataResponse();
					response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
					slotResponse = new ArrayList<Slot>();
					List<WorkingHours> workingHours = null;
					for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
						if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
							workingHours = workingSchedule.getWorkingHours();
						}
					}
					if (workingHours != null && !workingHours.isEmpty()) {

						Calendar localCalendar = Calendar
								.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
						localCalendar.setTime(date);
						int dayOfDate = localCalendar.get(Calendar.DATE);
						int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
						int yearOfDate = localCalendar.get(Calendar.YEAR);

						DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						DateTime end = new DateTime(yearOfDate, monthOfDate, dayOfDate, 23, 59, 59, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository
								.findByDoctorIdAndLocationId(doctorObjectId, locationObjectId, start, end,
										new Sort(Direction.ASC, "time.fromTime"));
						int i = 0;
						for (WorkingHours hours : workingHours) {
							startTime = hours.getFromTime();
							endTime = hours.getToTime();

							if (bookedSlots != null && !bookedSlots.isEmpty()) {
								while (i < bookedSlots.size()) {
									AppointmentBookedSlotCollection bookedSlot = bookedSlots.get(i);
									if (endTime > startTime) {
										if (bookedSlot.getTime().getFromTime() >= startTime
												|| bookedSlot.getTime().getToTime() >= endTime) {
											if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
												if (bookedSlot.getIsAllDayEvent()) {
													if (bookedSlot.getFromDate().equals(date))
														bookedSlot.getTime().setToTime(719);
													if (bookedSlot.getToDate().equals(date))
														bookedSlot.getTime().setFromTime(0);
												}
											}
											List<Slot> slots = DateAndTimeUtility.sliceTime(startTime,
													bookedSlot.getTime().getFromTime(), Math.round(slotTime), true);
											if (slots != null)
												slotResponse.addAll(slots);

											slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(),
													bookedSlot.getTime().getToTime(), Math.round(slotTime), false);
											if (slots != null)
												slotResponse.addAll(slots);
											startTime = bookedSlot.getTime().getToTime();
											i++;
										} else {
											i++;
											break;
										}
									} else {
										i++;
										break;
									}
								}
							}

							if (endTime > startTime) {
								List<Slot> slots = DateAndTimeUtility.sliceTime(startTime, endTime,
										Math.round(slotTime), true);
								if (slots != null)
									slotResponse.addAll(slots);
							}
						}

						if (checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
								doctorClinicProfileCollection.getTimeZone()))
							for (Slot slot : slotResponse) {
								if (slot.getMinutesOfDay() < getMinutesOfDay(date)) {
									slot.setIsAvailable(false);
									slotResponse.set(slotResponse.indexOf(slot), slot);
								}
							}
					}
					response.setSlots(slotResponse);
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

	@Transactional
	public Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone) {
		Boolean status = false;
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		if (yearOfDate == localCalendar.get(Calendar.YEAR) && dayOfDate == localCalendar.get(Calendar.DAY_OF_YEAR)) {
			status = true;
		}
		return status;
	}

	@Transactional
	public Integer getMinutesOfDay(Date date) {
		DateTime dateTime = new DateTime(new Date(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		Integer currentMinute = dateTime.getMinuteOfDay();
		return currentMinute;
	}

	@Override
	@Transactional
	public City addCity(City city) {
		try {
			DentalChainCityCollection cityCollection = new DentalChainCityCollection();
			city.setCreatedTime(new Date());
			city.setUpdatedTime(new Date());
			BeanUtil.map(city, cityCollection);

			cityCollection = cityRepository.save(cityCollection);
			BeanUtil.map(cityCollection, city);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return city;
	}

	@Override
	@Transactional
	public Boolean activateDeactivateCity(String cityId, boolean activate) {
		try {
			DentalChainCityCollection cityCollection = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (cityCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid city Id");
			}
			cityCollection.setIsActivated(activate);
			cityRepository.save(cityCollection);
		} catch (BusinessException be) {
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	@Transactional
	public Response<Object> getCities(int size, int page, String state, String searchTerm, Boolean isDiscarded,
			Boolean isActivated) {
		List<City> responseList = new ArrayList<City>();
		Response<Object> response = new Response<Object>();

		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (isActivated != null)
				criteria.and("isActivated").is(isActivated);

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("city").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//							Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
					Aggregation.sort(Sort.Direction.DESC, "createdTime")), DentalChainCityCollection.class, City.class)
					.getMappedResults().size());

			if (size > 0) {

				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),
//								Aggregation.sort(new Sort(Sort.Direction.ASC, "city")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						DentalChainCityCollection.class, City.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//								Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime")), DentalChainCityCollection.class,
						City.class).getMappedResults();

			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public City getCity(String cityId) {
		City response = new City();
		try {
			DentalChainCityCollection city = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (city != null) {
				BeanUtil.map(city, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LandmarkLocality addLandmaklLocality(LandmarkLocality landmarkLocality) {
		try {
			DentalChainLandmarkLocalityCollection landmarkLocalityCollection = new DentalChainLandmarkLocalityCollection();
			landmarkLocality.setCreatedTime(new Date());
			landmarkLocality.setUpdatedTime(new Date());
			BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
			if (landmarkLocality.getCityId() != null) {
			}

			landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
			BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return landmarkLocality;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Clinic getClinic(String locationId) {
		Clinic response = new Clinic();
		LocationCollection localtionCollection = null;
		Location location = new Location();
		HospitalCollection hospitalCollection = null;
		Hospital hospital = new Hospital();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
			localtionCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (localtionCollection == null) {
				return null;
			} else {
				BeanUtil.map(localtionCollection, location);
				response.setLocation(location);

				hospitalCollection = hospitalRepository.findById(localtionCollection.getHospitalId()).orElse(null);
				if (hospitalCollection != null) {
					BeanUtil.map(hospitalCollection, hospital);
					response.setHospital(hospital);
				}

				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(localtionCollection.getId());
				for (Iterator<DoctorClinicProfileCollection> iterator = doctorClinicProfileCollections
						.iterator(); iterator.hasNext();) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = iterator.next();
					DoctorCollection doctorCollection = doctorRepository
							.findByUserId(doctorClinicProfileCollection.getDoctorId());
					UserCollection userCollection = userRepository.findById(doctorClinicProfileCollection.getDoctorId())
							.orElse(null);

					if (doctorCollection != null) {
						Doctor doctor = new Doctor();
						BeanUtil.map(doctorCollection, doctor);
						if (userCollection != null) {
							BeanUtil.map(userCollection, doctor);
						}

						if (doctorClinicProfileCollection != null) {
							DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
							BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
							doctorClinicProfile.setLocationId(doctorClinicProfileCollection.getLocationId().toString());
							doctorClinicProfile.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

							if (doctorClinicProfile.getClinicOwnershipImageUrl() != null) {
								doctorClinicProfile.setClinicOwnershipImageUrl(
										getFinalImageURL(doctorClinicProfile.getClinicOwnershipImageUrl()));
							}

							doctor.setDoctorClinicProfile(doctorClinicProfile);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<String> specialities = (List<String>) CollectionUtils.collect(
									(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
									new BeanToPropertyValueTransformer("superSpeciality"));
							doctor.setSpecialities(specialities);
						}
						doctors.add(doctor);
					}
				}
				response.setDoctors(doctors);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getCountries() {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation,
					DentalChainCityCollection.class, City.class);
			response = groupResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getCountriesCount() {
		Integer response = 0;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation,
					DentalChainCityCollection.class, City.class);
			response = groupResults.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getLandmarkLocality(int size, int page, String cityId, String searchTerm,
			Boolean isDiscarded) {
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			Aggregation aggregation = null;
			if (!DPDoctorUtils.anyStringEmpty(cityId))
				criteria.and("cityId").is(new ObjectId(cityId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("locality").regex("^" + searchTerm, "i"),
						new Criteria("locality").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//							Aggregation.sort(new Sort(Sort.Direction.ASC, "city"))
					Aggregation.sort(Sort.Direction.DESC, "createdTime")), DentalChainLandmarkLocalityCollection.class,
					LandmarkLocality.class).getMappedResults().size());

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "locality"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "locality"))
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<LandmarkLocality> groupResults = mongoTemplate.aggregate(aggregation,
					DentalChainLandmarkLocalityCollection.class, LandmarkLocality.class);
			response.setDataList(groupResults.getMappedResults());

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getStates(String country) {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state", "country").andExclude("_id"),
					Aggregation.sort(Sort.Direction.ASC, "state"));
			if (!DPDoctorUtils.anyStringEmpty(country))
				aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation,
					DentalChainCityCollection.class, City.class);
			response = groupResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getStatesCount(String country) {
		Integer response = 0;
		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state", "country").andExclude("_id"),
					Aggregation.sort(Sort.Direction.ASC, "state"));
			if (!DPDoctorUtils.anyStringEmpty(country))
				aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation,
					DentalChainCityCollection.class, City.class);
			response = groupResults.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteCityById(String cityId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			DentalChainCityCollection cityCollection = cityRepository.findById(new ObjectId(cityId)).orElse(null);
			if (cityCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			cityCollection.setUpdatedTime(new Date());
			cityCollection.setIsDiscarded(isDiscarded);
			cityCollection = cityRepository.save(cityCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteLocalityById(String localityId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			DentalChainLandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository
					.findById(new ObjectId(localityId)).orElse(null);
			if (landmarkLocalityCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			landmarkLocalityCollection.setUpdatedTime(new Date());
			landmarkLocalityCollection.setIsDiscarded(isDiscarded);
			landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Appointment addAppointmentForDentalChain(DentalChainAppointmentRequest request) {
		Appointment response = null;
		List<String> dentalTreatment = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getType().getType())) {

				request.setType(AppointmentType.APPOINTMENT);
			}
			ObjectId doctorId = null, locationId = null, hospitalId = null, patientId = null;
			if (request.getDoctorId() != null)
				doctorId = new ObjectId(request.getDoctorId());

			if (request.getLocationId() != null)
				locationId = new ObjectId(request.getLocationId());

			if (request.getHospitalId() != null)
				hospitalId = new ObjectId(request.getHospitalId());

			patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);

			logger.info("patientId:" + patientId);

			UserCollection userCollection = userRepository.findById(doctorId).orElse(null);
			LocationCollection locationCollection = locationRepository.findById(locationId).orElse(null);
			PatientCard patientCard = null;
			List<PatientCard> patientCards = null;
			List<ObjectId> treatmentIds = null;
			if (patientId != null) {
				patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(patientId).and("locationId").is(locationId)
										.and("hospitalId").is(hospitalId)),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty())
					patientCard = patientCards.get(0);
				if (patientCard != null)
					request.setLocalPatientName(patientCard.getLocalPatientName());
			}
			AppointmentCollection appointmentCollection = null;

			List<AppointmentCollection> appointmentCollections = mongoTemplate
					.aggregate(
							Aggregation
									.newAggregation(
											Aggregation.match(new Criteria("locationId")
													.is(new ObjectId(request.getLocationId())).andOperator(
															new Criteria()
																	.orOperator(
																			new Criteria("doctorId").is(new ObjectId(
																					request.getDoctorId())),
																			new Criteria("doctorIds").is(
																					new ObjectId(request.getDoctorId()))
																					.and("isCalenderBlocked").is(true)),
															new Criteria()
																	.orOperator(
																			new Criteria("time.fromTime")
																					.lte(request.getTime()
																							.getFromTime())
																					.and("time.toTime")
																					.gt(request.getTime().getToTime()),
																			new Criteria("time.fromTime")
																					.lt(request.getTime().getFromTime())
																					.and("time.toTime")
																					.gte(request.getTime()
																							.getToTime())))
													.and("fromDate").is(request.getFromDate()).and("toDate")
													.is(request.getToDate()).and("state")
													.ne(AppointmentState.CANCEL.getState()))),

							AppointmentCollection.class, AppointmentCollection.class)
					.getMappedResults();
//			if (appointmentCollections != null && !appointmentCollections.isEmpty()) {
//				logger.error(timeSlotIsBooked);
//				throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
//			}

			if (userCollection != null && locationCollection != null) {

				clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorId,
						locationId);

				appointmentCollection = new AppointmentCollection();
				BeanUtil.map(request, appointmentCollection);
				appointmentCollection.setType(request.getType());
				appointmentCollection.setIsDentalChainAppointment(true);
				appointmentCollection.setCreatedTime(new Date());
				appointmentCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());
				if (request.getTreatmentId() == null) {
					appointmentCollection.setTreatmentId(null);
				} else {
					treatmentIds = new ArrayList<ObjectId>();
					for (String treatmentId : request.getTreatmentId()) {
						treatmentIds.add(new ObjectId(treatmentId));
					}
					appointmentCollection.setTreatmentId(treatmentIds);
				}

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

				String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
						appointmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				if (clinicProfileCollection != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				} else {
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}

				Date _24HourDt = _24HourSDF.parse(_24HourTime);
				final String branch = "";
				final String locationMapUrl = locationCollection.getGoogleMapShortUrl();

				final String patientName = (patientCard != null ? patientCard.getFirstName() : "");
				final String appointmentId = appointmentCollection.getAppointmentId();
				final String time = _12HourSDF.format(_24HourDt);
				final String date = sdf.format(appointmentCollection.getFromDate());
				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();
				final String clinicName = locationCollection.getLocationName(),
						clinicContactNum = locationCollection.getClinicNumber() != null
								? locationCollection.getClinicNumber()
								: "";

				appointmentCollection.setState(AppointmentState.PENDING);

				if (request.getType() == null) {
					appointmentCollection.setType(AppointmentType.APPOINTMENT);
				} else {
					appointmentCollection.setType(request.getType());
				}

				appointmentCollection = appointmentRepository.save(appointmentCollection);

				AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
				BeanUtil.map(appointmentCollection, bookedSlotCollection);
				bookedSlotCollection.setDoctorId(appointmentCollection.getDoctorId());
				bookedSlotCollection.setLocationId(appointmentCollection.getLocationId());
				bookedSlotCollection.setHospitalId(appointmentCollection.getHospitalId());
				bookedSlotCollection.setId(null);
				bookedSlotCollection.setType(appointmentCollection.getType());
				appointmentBookedSlotRepository.save(bookedSlotCollection);

				// sendSMS after appointment is saved
				UserCollection user = userRepository.findById(new ObjectId(patientCard.getUserId())).orElse(null);

				final String id = appointmentCollection.getId().toString(),
						patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
						patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
						patientWhatsAppNumber = patientCard != null ? patientCard.getUser().getWhatsAppNumber() : null,
						patientLanguage = user != null ? user.getLanguage() : null,
						doctorEmailAddress = userCollection.getEmailAddress(),
						doctorMobileNumber = userCollection.getMobileNumber();
				final DoctorFacility facility = (clinicProfileCollection != null)
						? clinicProfileCollection.getFacility()
						: null;

				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							if (request.getDentalAppointmentType().equals(DentalAppointmentType.OFFLINE)) {

							} else if (request.getDentalAppointmentType().equals(DentalAppointmentType.OFFLINE)) {

							}
							sendAppointmentEmailSmsNotification(true, request, id, appointmentId, doctorName,
									patientName, time, date, clinicName, clinicContactNum, patientEmailAddress,
									patientMobileNumber, patientWhatsAppNumber, doctorEmailAddress, doctorMobileNumber,
									facility, branch, locationMapUrl, patientLanguage);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});

				if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);

					if (patientCard != null) {
						patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
						patientCard.getUser().setLocationId(patientCard.getLocationId());
						patientCard.getUser().setHospitalId(patientCard.getHospitalId());
						BeanUtil.map(patientCard.getUser(), patientCard);
						patientCard.setUserId(patientCard.getUserId());
						patientCard.setId(patientCard.getUserId());
						patientCard.setColorCode(patientCard.getUser().getColorCode());
						patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
						patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
					} else {
						patientCard = new PatientCard();
						patientCard.setLocalPatientName(request.getLocalPatientName());
					}
					response.setPatient(patientCard);
					if (userCollection != null)
						response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
					if (locationCollection != null) {
						response.setLocationName(locationCollection.getLocationName());
						response.setClinicNumber(locationCollection.getClinicNumber());

						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						logger.info("clinicAddress:" + address);
						response.setClinicAddress(address);
						response.setLatitude(locationCollection.getLatitude());
						response.setLongitude(locationCollection.getLongitude());
					}

					if (request.getSmileBuddyId() != null) {
						UserCollection smileBuddyUserCollection = userRepository
								.findById(new ObjectId(request.getSmileBuddyId())).orElse(null);
						if (smileBuddyUserCollection != null)
							response.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
					}

					if (treatmentIds != null) {
						List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
								.aggregate(
										Aggregation
												.newAggregation(Aggregation.match(new Criteria("id").in(treatmentIds))),
										DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
								.getMappedResults();
						dentalTreatment = new ArrayList<String>();
						for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
							dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
						}
						response.setTreatmentNames(dentalTreatment);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private void sendAppointmentEmailSmsNotification(Boolean isAddAppointment, DentalChainAppointmentRequest request,
			String appointmentCollectionId, String appointmentId, String doctorName, String patientName, String time,
			String date, String clinicName, String clinicContactNum, String patientEmailAddress,
			String patientMobileNumber, String patientWhatsAppNumber, String doctorEmailAddress,
			String doctorMobileNumber, DoctorFacility doctorFacility, String branch, String locationMapUrl,
			String patientLanguage) throws MessagingException {

		if (isAddAppointment) {
			if (request.getDentalAppointmentType().equals(DentalAppointmentType.OFFLINE)) {

//				sendMsg(null, "APPOINTMENT_REQUEST_TO_PATIENT", request.getDoctorId(), request.getLocationId(),
//						request.getHospitalId(), request.getPatientId(), patientMobileNumber, patientName,
//						appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, null, null,
//						locationMapUrl);
//
//				sendWhatsapp("APPOINTMENT_REQUEST_TO_PATIENT", request.getDoctorId(), request.getLocationId(),
//						request.getHospitalId(), request.getPatientId(), patientMobileNumber, patientName,
//						appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl);
			} else if (request.getDentalAppointmentType().equals(DentalAppointmentType.ONLINE)) {

			}
		} else if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
			if (request.getCancelledBy().equals(AppointmentCreatedBy.ADMIN.getType())) {

			} else if (request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {

				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR",
							doctorEmailAddress, locationMapUrl, appointmentId);

				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, null, null, locationMapUrl, patientLanguage);

					sendWhatsapp("CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& patientEmailAddress != null)
					sendEmail(doctorName, patientName, time, date, clinicName,
							"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", patientEmailAddress, locationMapUrl,
							appointmentId);

				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
						&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
					sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, time, date,
							doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
							patientLanguage);
					sendWhatsapp("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							patientWhatsAppNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}
			} else if (request.getCancelledBy().equals(AppointmentCreatedBy.PATIENT.getType())) {
				sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName, appointmentId,
						time, date, doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
						patientLanguage);
				sendWhatsapp("CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getPatientId(), doctorMobileNumber, patientName, appointmentId,
						time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl, patientLanguage);

				if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, time, date,
							doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
							patientLanguage);
				sendWhatsapp("CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getPatientId(), patientWhatsAppNumber, patientName,
						appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl,
						patientLanguage);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& patientEmailAddress != null)
					sendEmail(doctorName, patientName, time, date, clinicName,
							"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", patientEmailAddress, locationMapUrl,
							appointmentId);
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName,
							"CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, locationMapUrl,
							appointmentId);
			}

		} else if (request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())) {

			if (request.getRescheduleBy().equals(AppointmentCreatedBy.ADMIN.getType())) {

			} else if (request.getRescheduleBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName,
							"RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR", doctorEmailAddress, locationMapUrl,
							appointmentId);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName,
							"RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT", patientEmailAddress, locationMapUrl,
							appointmentId);
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, time, date,
							doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
							patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							patientWhatsAppNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}
				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, null, null, locationMapUrl, patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}
			} else if (request.getRescheduleBy().equals(AppointmentCreatedBy.PATIENT.getType())) {
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName,
							"RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress, locationMapUrl,
							appointmentId);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName, "RESCHEDULE_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, locationMapUrl, appointmentId);
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(),
							"RESCHEDULE_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							patientMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, null, null, locationMapUrl, patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							patientWhatsAppNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}
				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, null, null, locationMapUrl, patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getPatientId(),
							doctorMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
							clinicContactNum, branch, locationMapUrl, patientLanguage);

				}
			} else {
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName, "RESCHEDULE_APPOINTMENT_TO_DOCTOR",
							doctorEmailAddress, locationMapUrl, appointmentId);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail())
					sendEmail(doctorName, patientName, time, date, clinicName, "RESCHEDULE_APPOINTMENT_TO_PATIENT",
							patientEmailAddress, locationMapUrl, appointmentId);
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, time, date,
							doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
							patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getPatientId(), patientWhatsAppNumber, patientName,
							appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl,
							patientLanguage);

				}
				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getPatientId(), doctorMobileNumber, patientName,
							appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, null, null,
							locationMapUrl, patientLanguage);
					sendWhatsapp("RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getPatientId(), doctorMobileNumber, patientName,
							appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl,
							patientLanguage);

				}
			}
		} else if (request.getState().getState().equals(AppointmentState.CONFIRM.getState())) {

			if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail()) {
				sendEmail(doctorName, patientName, time, date, clinicName, "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT",
						doctorEmailAddress, locationMapUrl, appointmentId);
			}

			if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {

				sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getPatientId(), doctorMobileNumber, patientName, appointmentId,
						time, date, doctorName, clinicName, clinicContactNum, branch, null, null, locationMapUrl,
						patientLanguage);
				sendWhatsapp("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getPatientId(), doctorMobileNumber, patientName, appointmentId,
						time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl, patientLanguage);
			}

			if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()) {

				sendEmail(doctorName, patientName, time, date, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
						patientEmailAddress, locationMapUrl, appointmentId);
			}

			if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
				sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
						request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId(),
						patientMobileNumber, patientName, appointmentId, time, date, doctorName, clinicName,
						clinicContactNum, branch, null, null, locationMapUrl, patientLanguage);
				sendWhatsapp("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(), request.getLocationId(),
						request.getHospitalId(), request.getPatientId(), patientWhatsAppNumber, patientName,
						appointmentId, time, date, doctorName, clinicName, clinicContactNum, branch, locationMapUrl,
						patientLanguage);
			}
		}

	}

	private void sendWhatsapp(String type, String doctorId, String locationId, String hospitalId, String userId,
			String mobileNumber, String patientName, String appointmentId, String time, String date, String doctorName,
			String clinicName, String clinicContactNum, String branch, String locationMapUrl, String patientLanguage) {
		try {
//			registrationService.addUserToInretackt();

			JSONObject requestObject1 = new JSONObject();
			JSONObject requestObject2 = new JSONObject();
			JSONArray requestObject3 = new JSONArray();
			requestObject1.put("phoneNumber", mobileNumber);
			requestObject1.put("countryCode", "+91");
			requestObject1.put("type", "Template");

			String dateTime = time + ", " + date;
			if (DPDoctorUtils.anyStringEmpty(patientName))
				patientName = "";
			if (DPDoctorUtils.anyStringEmpty(appointmentId))
				appointmentId = "";
			else
				appointmentId = "(ID: " + appointmentId + ")";
			if (DPDoctorUtils.anyStringEmpty(date))
				date = "";
			if (DPDoctorUtils.anyStringEmpty(time))
				time = "";
			if (DPDoctorUtils.anyStringEmpty(doctorName))
				doctorName = "";
			if (DPDoctorUtils.anyStringEmpty(clinicName))
				clinicName = "";
			if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
				clinicContactNum = "";
			if (DPDoctorUtils.anyStringEmpty(locationMapUrl))
				locationMapUrl = "";

			String supportNo = smilebirdSupportNumber;

			switch (type) {
			case "APPOINTMENT_REQUEST_TO_PATIENT": {
				requestObject2.put("languageCode", "en");
				requestObject2.put("name", "welcome_message_new");
				requestObject3.put(patientName);
				requestObject3.put(clinicName);
			}
				break;
			case "CONFIRMED_APPOINTMENT_TO_PATIENT":
				switch (patientLanguage) {
				case "English":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_confirmation_to_patient_ua");
					break;
				case "Marathi":
					requestObject2.put("languageCode", "mr");
					requestObject2.put("name", "appointment_confirmation_to_patient_marathi");
					break;
				case "Hindi":
					requestObject2.put("languageCode", "hi");
					requestObject2.put("name", "appointment_confirmation_to_patient_hindi_k0");
					break;
				case "Hinglish":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_confirmation_to_patient_hinglish");
					break;
				default:
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_confirmation_to_patient_ua");
					break;
				}

				requestObject3.put(patientName);
				requestObject3.put(appointmentId);
				requestObject3.put(clinicName);
				requestObject3.put(dateTime);
				requestObject3.put(supportNo);
				requestObject3.put(locationMapUrl);
				break;
			case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
				requestObject2.put("languageCode", "en");
				requestObject2.put("name", "appointment_confirmation_to_dentist");
				requestObject3.put(doctorName);
				requestObject3.put(patientName);
				requestObject3.put(dateTime);
				requestObject3.put(clinicName);
				requestObject3.put(supportNo);
			}
				break;
			case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
				requestObject2.put("languageCode", "en");
				requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_dentist");
				requestObject3.put(doctorName);
				requestObject3.put(patientName);
				requestObject3.put(dateTime);
				requestObject3.put(clinicName);
			}
				break;
			case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
				switch (patientLanguage) {
				case "English":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_patient");
					break;
				case "Marathi":
					requestObject2.put("languageCode", "mr");
					requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_patient_marathi");
					break;
				case "Hindi":
					requestObject2.put("languageCode", "hi");
					requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_patient_hindi");
					break;
				case "Hinglish":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_patient_hinglish_r3");
					break;
				default:
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_dentist_sms_to_patient");
					break;
				}
				requestObject3.put(patientName);
				requestObject3.put(appointmentId);
				requestObject3.put(dateTime);
				requestObject3.put(clinicName);
				requestObject3.put(supportNo);
			}
				break;
			case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
				requestObject2.put("languageCode", "en");
				requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_dentist");
				requestObject3.put(doctorName);
				requestObject3.put(patientName);
				requestObject3.put(clinicName);
				requestObject3.put(dateTime);
			}
				break;
			case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
				switch (patientLanguage) {
				case "English":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_patient");
					break;
				case "Marathi":
					requestObject2.put("languageCode", "mr");
					requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_patient_marathi");
					break;
				case "Hindi":
					requestObject2.put("languageCode", "hi");
					requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_patient_hindi");
					break;
				case "Hinglish":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_patient_hinglish");
					break;
				default:
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_cancellation_by_patient_sms_to_patient");
					break;
				}
				requestObject3.put(patientName);
				requestObject3.put(appointmentId);
				requestObject3.put(clinicName);
				requestObject3.put(dateTime);
				requestObject3.put(supportNo);
			}
				break;
			case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT":
				switch (patientLanguage) {
				case "English":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_dentist_sms_to_patient");
					break;
				case "Marathi":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_dentist_sms_to_patient_marathi");
					break;
				case "Hindi":
					requestObject2.put("languageCode", "hi");
					requestObject2.put("name", "appointment_reschedule_by_dentist_sms_to_patient_hindi");
					break;
				case "Hinglish":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_dentist_sms_to_patient_hinglish");
					break;
				default:
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_dentist_sms_to_patient");
					break;
				}
				requestObject3.put(patientName);
				requestObject3.put(appointmentId);
				requestObject3.put(clinicName);
				requestObject3.put(dateTime);
				requestObject3.put(locationMapUrl);
				requestObject3.put(supportNo);
				break;
			case "RESCHEDULE_APPOINTMENT_TO_PATIENT_BY_PATIENT":
			case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
				switch (patientLanguage) {
				case "English":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_patient");
					break;
				case "Marathi":
					requestObject2.put("languageCode", "mr");
					requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_patient_marathi");
					break;
				case "Hindi":
					requestObject2.put("languageCode", "hi");
					requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_patient_hindi");
					break;
				case "Hinglish":
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_patient_hinglish");
					break;
				default:
					requestObject2.put("languageCode", "en");
					requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_patient");
					break;
				}
				requestObject3.put(patientName);
				requestObject3.put(appointmentId);
				requestObject3.put(clinicName);
				requestObject3.put(dateTime);
				requestObject3.put(locationMapUrl);
				requestObject3.put(supportNo);
			}
				break;
			case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR":
			case "RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT":
			case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
				requestObject2.put("languageCode", "en");
				requestObject2.put("name", "appointment_reschedule_by_patient_sms_to_dentist");
				requestObject3.put(doctorName);
				requestObject3.put(patientName);
				requestObject3.put(dateTime);
				requestObject3.put(clinicName);
			}
				break;
			default:
				break;
			}

			requestObject2.put("bodyValues", requestObject3);
			requestObject1.put("template", requestObject2);

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

	private void sendEmail(String doctorName, String patientName, String dateTime, String date, String clinicName,
			String type, String emailAddress, String locationMapUrl, String appointmentId) throws MessagingException {
		String emailBody = null;
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			emailBody = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at "
					+ clinicName + " has been confirmed @ " + dateTime + "." + " If you need help, reach out to us at "
					+ smilebirdSupportNumber + "." + "\n" + "Dental Studio address link -" + locationMapUrl + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "confirmappointment.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;
		case "CONFIRMED_APPOINTMENT_TO_ADMIN_BY_PATIENT": {
			emailBody = "Hello Admin " + "\n" + "An appointment for " + doctorName + " from " + patientName
					+ " has been requested to scheduled @ " + dateTime + " at " + clinicName + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "userappointment.vm");
			mailService.sendEmail(emailAddress, appointmentRequestToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;
		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			emailBody = "Hello " + doctorName + "," + " your appointment with " + patientName + " has been scheduled @ "
					+ dateTime + " at " + clinicName + "." + " For any query, reach out to us at "
					+ smilebirdSupportNumber + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "confirmappointment.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			emailBody = "Hello " + doctorName + "," + " your appointment with " + patientName + " scheduled @ "
					+ dateTime + " at " + clinicName + " has been cancelled as per your request.";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "cancelappointment.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			emailBody = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " @ " + dateTime
					+ " at " + clinicName
					+ " has been cancelled. Our team will reach out to you to book a new appointment, we apologize for the inconvenience caused. If you need any help, reach out to us at "
					+ smilebirdSupportNumber + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "cancelappointment.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			emailBody = "Hello " + doctorName + ", your appointment with " + patientName + " @ " + clinicName + " at "
					+ dateTime + ", has been cancelled by patient.";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "cancelappointment.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject + " " + dateTime, body, null);
			break;
		}
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			emailBody = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " @ "
					+ clinicName + " at " + dateTime
					+ " has been cancelled as per your request. If you need to schedule a new appointment, reach out to us at "
					+ smilebirdSupportNumber + " .";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody, "cancelappointment.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject + " " + dateTime, body, null);
		}
			break;
		case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT":
		case "RESCHEDULE_APPOINTMENT_TO_PATIENT_BY_PATIENT":
		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			emailBody = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at "
					+ clinicName + " has been rescheduled" + " @ " + dateTime
					+ ", we apologize for the inconvenience caused. If you need any help, reach out to us at "
					+ smilebirdSupportNumber + " ." + "\nOur Studio address link- " + locationMapUrl + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody,
					"rescheduleappointment.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToPatientMailSubject + " " + dateTime, body, null);
		}
			break;
		case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR":
		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT":
		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			emailBody = "Hello " + doctorName + ", your appointment with " + patientName + " has been rescheduled"
					+ " @ " + dateTime + " at " + clinicName + ".";
			String body = mailBodyGenerator.generateSmilebirdAppointmentEmailBody(emailBody,
					"rescheduleappointment.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		default:
			break;
		}
	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,
			String userId, String mobileNumber, String patientName, String appointmentId, String time, String date,
			String doctorName, String clinicName, String clinicContactNum, String branch, String appointmentType,
			String consultationType, String locationMapUrl, String patientLanguage) {
		SMSFormatCollection smsFormatCollection = null;
		if (formatType != null) {
			smsFormatCollection = sMSFormatRepository.findByDoctorIdAndLocationIdAndHospitalIdAndType(
					new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId), formatType);
		}

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(doctorId));
		smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
		smsTrackDetail.setLocationId(new ObjectId(locationId));
		smsTrackDetail.setType("TXN");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(userId));
		SMS sms = new SMS();
		String dateTime = time + ", " + date;
		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		else
			appointmentId = "ID: " + appointmentId;
		if (DPDoctorUtils.anyStringEmpty(date))
			date = "";
		if (DPDoctorUtils.anyStringEmpty(time))
			time = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";
		if (DPDoctorUtils.anyStringEmpty(locationMapUrl))
			locationMapUrl = "";
		if (DPDoctorUtils.anyStringEmpty(patientLanguage))
			patientLanguage = "English";
		if (smsFormatCollection != null) {
			if (type.equalsIgnoreCase("CONFIRMED_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR")
					|| type.equalsIgnoreCase("APPOINTMENT_REMINDER_TO_PATIENT")
					|| type.equalsIgnoreCase("RESCHEDULE_APPOINTMENT_TO_PATIENT")) {
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent())
						|| clinicName == null)
					clinicName = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_CONTACT_NUMBER.getContent())
						|| clinicContactNum == null)
					clinicContactNum = "";
				if (!smsFormatCollection.getContent().contains(SMSContent.BRANCH.getContent()) || branch == null)
					branch = "";
			}
		}
		String text = "";
		switch (type) {
		case "APPOINTMENT_REQUEST_TO_PATIENT": {
			text = "Welcome " + patientName + " to " + "Smilebird Dental Studio."
					+ " Thanks for choosing us, our team will contact you soon to confirm your appointment" + "." + "\n"
					+ "Team Smilebird";
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307165054366944223");
		}
			break;
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at " + clinicName
					+ " has been confirmed @ " + dateTime + "." + " If you need help, reach out to us at "
					+ smilebirdSupportNumber + "." + "\n" + "Dental Studio address link -" + locationMapUrl + "." + "\n"
					+ "Team Smilebird";
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307165157019874563");
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Hello " + doctorName + "," + " your appointment with " + patientName + " has been scheduled @ "
					+ dateTime + " at " + clinicName + "." + " For any query, reach out to us at "
					+ smilebirdSupportNumber + "." + "\n" + "Team Smilebird";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307165106672849771");
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR":
			text = "Hello " + doctorName + "," + " your appointment with " + patientName + " scheduled @ " + dateTime
					+ " at " + clinicName + " has been cancelled as per your request." + "\n" + "Team Smilebird";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307165106745413049");
			break;
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			smsDetail.setUserName(patientName);
			switch (patientLanguage) {
			case "English":
				text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " @ " + dateTime
						+ " at " + clinicName
						+ " has been cancelled. Our team will reach out to you to book a new appointment, we apologize for the inconvenience caused. If you need any help, reach out to us at "
						+ smilebirdSupportNumber + "." + "\n" + "Team Smilebird";
				smsTrackDetail.setTemplateId("1307165104579879558");
				break;
			case "Marathi":
				text = "नमस्कार  " + patientName + "," + " तुमची  " + clinicName + " वरील  " + " @ " + dateTime
						+ " वाजता ची अपॉइंटमेंट " + "(" + appointmentId + ")"
						+ " रद्द करण्यात आली आहे. आमची टीम नवीन अपॉइंटमेंट बुक करण्यासाठी तुमच्यापर्यंत पोहोचेल, झालेल्या गैरसोयीबद्दल आम्हाला खेद आहोत. तुम्हाला काही मदत हवी असल्यास, आमच्याशी  "
						+ smilebirdSupportNumber + " वर संपर्क साधा." + "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585289408805");
				break;
			case "Hinglish":
				text = "Namaste " + patientName + "," + " aapka appointment " + "(" + appointmentId + ")" + " @ "
						+ dateTime + clinicName
						+ " par cancel kar diya gya hai. Nayi appointment book karne ke liye hamari team aapse contact karegi, aapko hui asuvidha ke liye hame khed hai. Yadi aapko kisi sahayata chahiye, to "
						+ smilebirdSupportNumber + " par hamse contact kar. Dental Studio address link -"
						+ locationMapUrl + "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585425138335");
				break;
			}
		}
			break;
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Hello " + doctorName + ", your appointment with " + patientName + " @ " + clinicName + " at "
					+ dateTime + ", has been cancelled by patient." + "\n" + "Team Smilebird";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307165104612578845");
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			smsDetail.setUserName(patientName);
			switch (patientLanguage) {
			case "English":
				text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " @ " + clinicName
						+ " at " + dateTime
						+ " has been cancelled as per your request. If you need to schedule a new appointment, reach out to us at "
						+ smilebirdSupportNumber + " ." + "\n" + "Team Smilebird";
				smsTrackDetail.setTemplateId("1307165104536059658");
				break;
			case "Hindi":
				text = "नमस्ते " + patientName + "," + " आपके अनुरोध के अनुसार " + clinicName + " पर आपका अपॉइंटमेंट  "
						+ "(" + appointmentId + ")" + " @ " + dateTime
						+ " रद्द कर दिया गया है। अगर आपको कोई नया अपॉइंटमेंट शेड्यूल करना है, तो  "
						+ smilebirdSupportNumber + " पर हमसे संपर्क करें।" + "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585214921374");
				break;
			case "Hinglish":
				text = "Namaste " + patientName + "," + " aapke request ke anusar " + clinicName
						+ " par aapka appointment " + "(" + appointmentId + ")" + " @ " + dateTime
						+ " cancel kar diya gya hai. Agar aapko koi naya appointment schedule karna hai, to "
						+ smilebirdSupportNumber + " par hamse contact kare." + "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585277173276");
				break;
			default:
				text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " @ " + clinicName
						+ " at " + dateTime
						+ " has been cancelled as per your request. If you need to schedule a new appointment, reach out to us at "
						+ smilebirdSupportNumber + " ." + "\n" + "Team Smilebird";
				smsTrackDetail.setTemplateId("1307165104536059658");
				break;
			}
		}
			break;
		case "RESCHEDULE_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			smsDetail.setUserName(patientName);
			switch (patientLanguage) {
			case "English":
				text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at "
						+ clinicName + " has been rescheduled @" + dateTime + "."
						+ " If you need any help, reach out to us at " + smilebirdSupportNumber + "." + "\n"
						+ "Our Dental Studio address link - " + locationMapUrl + "." + "\n" + "Team Smilebird";
				smsTrackDetail.setTemplateId("1307165104690573267");
				break;
			case "Hindi":
				text = "नमस्ते " + patientName + ", " + clinicName + " पर आपका अपॉइंटमेंट  " + "(" + appointmentId + ")"
						+ "  @ " + dateTime + " पर रीशेड्यूल किया गया है। अगर आपको कोई मदद चाहिए, तो हमें  "
						+ smilebirdSupportNumber + " पर संपर्क करें। हमारा डेंटल स्टूडियो पता लिंक -  " + locationMapUrl
						+ "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585534213559");
				break;
			case "Marathi":
				text = "नमस्कार " + patientName + "," + " तुमची " + clinicName + " येथे अपॉइंटमेंट " + "("
						+ appointmentId + ")" + " @" + dateTime
						+ "वाजता पुन्हा शेड्यूल केली गेली आहे. तुम्हाला काही मदत हवी असल्यास, संपर्क साधा आम्हाला "
						+ smilebirdSupportNumber + " येथे." + "\n" + "आमचा डेंटल स्टुडिओ पत्ता लिंक - " + locationMapUrl
						+ "\n" + "- Smilebird";
				smsTrackDetail.setTemplateId("1307167585538127203");
				break;
			default:
				text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at "
						+ clinicName + " has been rescheduled @" + dateTime + "."
						+ " If you need any help, reach out to us at " + smilebirdSupportNumber + "." + "\n"
						+ "Our Dental Studio address link - " + locationMapUrl + "." + "\n" + "Team Smilebird";
				smsTrackDetail.setTemplateId("1307165104690573267");
				break;
			}

		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Hello " + doctorName + ", your appointment with " + patientName + " has been rescheduled by patient"
					+ " @ " + dateTime + " at " + clinicName + "." + "\n" + "Team Smilebird";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307165104728746460");
		}
			break;

		case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_PATIENT": {
			text = "Hi " + patientName + "," + " your appointment " + "(" + appointmentId + ")" + " at " + clinicName
					+ " has been rescheduled" + " @ " + dateTime
					+ ", we apologize for the inconvenience caused. If you need any help, reach out to us at "
					+ smilebirdSupportNumber + " ." + "\nOur Studio address link- " + locationMapUrl + "." + "\n"
					+ "Team Smilebird";
			smsDetail.setUserName(patientName);
			smsTrackDetail.setTemplateId("1307165105183730337");
		}
			break;

		case "RESCHEDULE_APPOINTMENT_BY_DOCTOR_TO_DOCTOR": {
			text = "Hello " + doctorName + ", your appointment with " + patientName + " has been rescheduled" + " @ "
					+ dateTime + " at " + clinicName + "." + "\n" + "Team Smilebird";
			smsDetail.setUserName(doctorName);
			smsTrackDetail.setTemplateId("1307165105164326179");
		}
			break;

		default:
			break;
		}

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

	private ObjectId registerPatientIfNotRegistered(DentalChainAppointmentRequest request, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {
			if (request.getAppointmentFor().equals(AppointmentForType.SELF)) {
				Criteria criteria = new Criteria("localPatientName").is(request.getLocalPatientName());
				criteria.and("mobileNumber").is(request.getMobileNumber());

				UserCollection patient = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)), UserCollection.class,
								UserCollection.class)
						.getUniqueMappedResult();
				if (patient == null) {
					if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
						throw new BusinessException(ServiceError.InvalidInput,
								"Patient not selected in user app" + request);
					}
					PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
					patientRegistrationRequest.setFirstName(request.getLocalPatientName());
					patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
					patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
					patientRegistrationRequest.setDoctorId(request.getDoctorId());
					patientRegistrationRequest.setLocationId(request.getLocationId());
					patientRegistrationRequest.setHospitalId(request.getHospitalId());
					RegisteredPatientDetails patientDetails = null;
					patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
					if (patientDetails != null) {
						patientId = new ObjectId(patientDetails.getUserId());
					}
					transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
					esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
				} else
					patientId = patient.getId();
			} else if (request.getAppointmentFor().equals(AppointmentForType.OTHER)) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setFirstName(request.getAppointmentForOtherName());
				patientRegistrationRequest.setLocalPatientName(request.getAppointmentForOtherName());
				patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = null;
				patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
				if (patientDetails != null) {
					patientId = new ObjectId(patientDetails.getUserId());
				}
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			}
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
			if (request.getAppointmentFor().equals(AppointmentForType.SELF)) {
				patientId = new ObjectId(request.getPatientId());
				List<PatientCollection> patients = patientRepository.findByUserId(patientId);
				PatientCollection patient = null;
				if (patients != null && !patients.isEmpty())
					patient = patients.get(0);
				if (patient == null) {
					PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
					patientRegistrationRequest.setDoctorId(request.getDoctorId());
					patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
					patientRegistrationRequest.setFirstName(request.getLocalPatientName());
					patientRegistrationRequest.setUserId(request.getPatientId());
					patientRegistrationRequest.setLocationId(request.getLocationId());
					patientRegistrationRequest.setHospitalId(request.getHospitalId());
					RegisteredPatientDetails patientDetails = registrationService
							.registerExistingPatient(patientRegistrationRequest);
					transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
					esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
				} else {
					List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
					if (consultantDoctorIds == null)
						consultantDoctorIds = new ArrayList<ObjectId>();
					if (!consultantDoctorIds.contains(doctorId))
						consultantDoctorIds.add(doctorId);
					patient.setConsultantDoctorIds(consultantDoctorIds);
					patient.setDoctorId(new ObjectId(request.getDoctorId()));
					patient.setLocationId(new ObjectId(request.getLocationId()));
					patient.setHospitalId(new ObjectId(request.getHospitalId()));
					patient.setUpdatedTime(new Date());
					patient.setCreatedTime(patient.getCreatedTime());
					patientRepository.save(patient);

					PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
					patientRegistrationRequest.setUserId(request.getPatientId());
					patientRegistrationRequest.setFirstName(request.getLocalPatientName());
					patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
					patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
					patientRegistrationRequest.setDoctorId(request.getDoctorId());
					patientRegistrationRequest.setLocationId(request.getLocationId());
					patientRegistrationRequest.setHospitalId(request.getHospitalId());
					patientRegistrationRequest.setGender(patient.getGender());
					patientRegistrationRequest.setDob(patient.getDob());
					if (patient.getDob() != null
							&& patient.getDob().getAge() != null & patient.getDob().getAge().getYears() > 0)
						patientRegistrationRequest.setAge(patient.getDob().getAge().getYears());
//					
//					ESPatientDocument esPatientDocument = esPatientRepository.findById(patient.getId().toString())
//							.orElse(null);
//					esPatientDocument.setDoctorId(request.getDoctorId());
//					esPatientDocument.setLocationId(request.getLocationId());
//					esPatientDocument.setHospitalId(request.getHospitalId());
//					esPatientDocument = esPatientRepository.save(esPatientDocument);

					RegisteredPatientDetails patientDetails = registrationService
							.registerExistingPatient(patientRegistrationRequest);
					transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
					esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));

				}
			} else if (request.getAppointmentFor().equals(AppointmentForType.OTHER)) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setFirstName(request.getAppointmentForOtherName());
				patientRegistrationRequest.setLocalPatientName(request.getAppointmentForOtherName());
				patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = null;
				patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
				if (patientDetails != null) {
					patientId = new ObjectId(patientDetails.getUserId());
				}
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			}
		}
		return patientId;
	}

	@Override
	public WebAppointmentSlotDataResponse getTimeSlotsWeb(String doctorId, String locationId, String hospitalId,
			String date) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		List<SlotDataResponse> slotDataResponses = null;
		WebAppointmentSlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			Date dateObj = new Date(Long.parseLong(date));

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(doctorObjectId,
					locationObjectId);

			if (doctorClinicProfileCollection != null) {

				Integer startTime = 0, endTime = 0;
				float slotTime = 15;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));

				Calendar localCalendar = Calendar
						.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				localCalendar.setTime(dateObj);

				response = new WebAppointmentSlotDataResponse();
				response.setDoctorId(doctorId);
				response.setLocationId(locationId);
				response.setDoctorSlugURL(doctorClinicProfileCollection.getDoctorSlugURL());
				response.setHospitalId(hospitalId);

				slotDataResponses = new ArrayList<SlotDataResponse>();
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					for (int j = 0; j < 6; j++) {
						String day = sdf.format(localCalendar.getTime());
						slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
						if (slotTime == 0.0)
							slotTime = 15;

						SlotDataResponse slotDataResponse = new SlotDataResponse();
						slotDataResponse.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
						slotDataResponse.setDate(localCalendar.getTime().getTime());

						slotResponse = new ArrayList<Slot>();
						List<WorkingHours> workingHours = null;
						for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
							if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
								workingHours = workingSchedule.getWorkingHours();
							}
						}

						if (workingHours != null && !workingHours.isEmpty()) {
							int dayOfDate = localCalendar.get(Calendar.DATE);
							int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
							int yearOfDate = localCalendar.get(Calendar.YEAR);

							DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
									.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));

							DateTime end = new DateTime(localCalendar.get(Calendar.YEAR),
									localCalendar.get(Calendar.MONTH) + 1, localCalendar.get(Calendar.DATE), 23, 59, 59,
									DateTimeZone.forTimeZone(
											TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));

							List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository
									.findByDoctorLocationId(doctorObjectId, locationObjectId, start, end,
											new Sort(Direction.ASC, "time.fromTime"));
							int i = 0;
							for (WorkingHours hours : workingHours) {
								startTime = hours.getFromTime();
								endTime = hours.getToTime();

								if (startTime != null && endTime != null) {
									if (bookedSlots != null && !bookedSlots.isEmpty()) {
										while (i < bookedSlots.size()) {
											AppointmentBookedSlotCollection bookedSlot = bookedSlots.get(i);
											if (endTime > startTime) {
												if (bookedSlot.getTime().getFromTime() >= startTime
														|| bookedSlot.getTime().getToTime() >= endTime) {
													if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
														if (bookedSlot.getIsAllDayEvent()) {
															if (bookedSlot.getFromDate().equals(date))
																bookedSlot.getTime().setToTime(719);
															if (bookedSlot.getToDate().equals(date))
																bookedSlot.getTime().setFromTime(0);
														}
													}
													List<Slot> slots = DateAndTimeUtility.sliceTime(startTime,
															bookedSlot.getTime().getFromTime(), Math.round(slotTime),
															true);
													if (slots != null)
														slotResponse.addAll(slots);

													slots = DateAndTimeUtility.sliceTime(
															bookedSlot.getTime().getFromTime(),
															bookedSlot.getTime().getToTime(), Math.round(slotTime),
															false);
													if (slots != null)
														slotResponse.addAll(slots);
													startTime = bookedSlot.getTime().getToTime();
													i++;
												} else {
													i++;
													break;
												}
											} else {
												i++;
												break;
											}
										}
									}
									if (endTime > startTime) {
										List<Slot> slots = DateAndTimeUtility.sliceTime(startTime, endTime,
												Math.round(slotTime), true);
										if (slots != null)
											slotResponse.addAll(slots);
									}
								}
							}
							if (checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
									doctorClinicProfileCollection.getTimeZone()))
								for (Slot slot : slotResponse) {
									if (slot.getMinutesOfDay() < getMinutesOfDay(dateObj)) {
										slot.setIsAvailable(false);
										slotResponse.set(slotResponse.indexOf(slot), slot);
									}
								}
							slotDataResponse.setSlots(slotResponse);
						}
						slotDataResponses.add(slotDataResponse);
						localCalendar.add(Calendar.DATE, 1);
					}
					response.setSlots(slotDataResponses);
				} else {
					throw new BusinessException(ServiceError.Unknown, "Working slot null");

				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "doctorClinicProfileCollection null");

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

	@Override
	public Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId,
			List<String> treatmentId, String from, String to, int page, int size, String updatedTime, String status,
			String state, String sortBy, String fromTime, String toTime, Boolean isRegisteredPatientRequired,
			String type, String searchTerm, String smileBuddyId) {
		Response<Appointment> response = new Response<Appointment>();

		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria();
			Criteria criteriaSecond = new Criteria();

			criteria.and("isDentalChainAppointment").is(true);
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			} else {
				criteria.and("type").is(AppointmentType.APPOINTMENT.getType());
			}

//			criteria.and("updatedTime").gte(new Date(updatedTimeStamp));
			criteria.and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId) {
					doctorObjectIds.add(new ObjectId(id));
				}
				criteria.and("doctorId").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (treatmentId != null && !treatmentId.isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : treatmentId) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(state))
				criteria.and("state").is(state);

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase()).and("state").ne(AppointmentState.CANCEL.getState());

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));

				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));

				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteriaSecond = criteriaSecond.orOperator(
						new Criteria("patientCard.localPatientName").regex("^" + searchTerm, "i"),
						new Criteria("patientCard.localPatientName").regex("^" + searchTerm));
			}

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));
			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				if (status.equalsIgnoreCase(QueueStatus.WAITING.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedInAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "engagedAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedOutAt"));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("startTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "time.fromTime"));
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("createdTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "createdTime"));
			}

			response = getAppointmentsForWeb(criteria, criteriaSecond, sortOperation, page, size,
					appointmentLookupResponses);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private Response<Appointment> getAppointmentsForWeb(Criteria criteria, Criteria criteriaSecond,
			SortOperation sortOperation, int page, int size,
			List<AppointmentLookupResponse> appointmentLookupResponses) {
		Response<Appointment> response = null;
		List<Appointment> appointments = null;
		List<SmilebirdAppoinmentLookupResponse> smilebirdAppoinmentLookupResponses = null;
//		Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
//		if (count > 0) {
		response = new Response<Appointment>();
		CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
				new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("patientId", "$patientId").append("time", "$time")
						.append("state", "$state").append("isRescheduled", "$isRescheduled")
						.append("fromDate", "$fromDate").append("toDate", "$toDate")
						.append("appointmentId", "$appointmentId").append("subject", "$subject")
						.append("explanation", "$explanation").append("type", "$type")
						.append("isCalenderBlocked", "$isCalenderBlocked")
						.append("doctorName",
								new BasicDBObject("$concat", Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
						.append("cancelledBy", "$cancelledBy").append("status", "$status")
						.append("waitedFor", "$waitedFor").append("appointmentBy", "$appointmentBy")
						.append("appointmentFor", "$appointmentFor")
						.append("appointmentForOtherName", "$appointmentForOtherName").append("count", "$count")
						.append("cancelledByProfile", "$cancelledByProfile")
						.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("patient._id", "$patientCard.userId").append("patient.userId", "$patientCard.userId")
						.append("patient.localPatientName", "$patientCard.localPatientName")
						.append("patient.followUp", "$patientUser.followUp")
						.append("patient.platform", "$patientUser.platform").append("patient.PID", "$patientCard.PID")
						.append("patient.PNUM", "$patientCard.PNUM").append("dentalTreatment", "$dentalTreatment")
						.append("treatmentId", "$treatmentId").append("locationName", "$location.locationName")
						.append("patient.imageUrl",
								new BasicDBObject("$cond",
										new BasicDBObject("if",
												new BasicDBObject("eq", Arrays.asList("$patientCard.imageUrl", null)))
												.append("then",
														new BasicDBObject("$concat",
																Arrays.asList(imagePath, "$patientCard.imageUrl")))
												.append("else", null)))
						.append("patient.thumbnailUrl", new BasicDBObject("$cond",
								new BasicDBObject("if",
										new BasicDBObject("eq", Arrays.asList("$patientCard.thumbnailUrl", null)))
										.append("then",
												new BasicDBObject("$concat",
														Arrays.asList(imagePath, "$patientCard.thumbnailUrl")))
										.append("else", null)))
						.append("patient.mobileNumber", "$patientUser.mobileNumber")
						.append("smileBuddyId", "$smileBuddyId")
						.append("patient.colorCode", "$patientUser.colorCode")));

		CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
				new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("time", new BasicDBObject("$first", "$time"))
						.append("state", new BasicDBObject("$first", "$state"))
						.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("toDate", new BasicDBObject("$first", "$toDate"))
						.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
						.append("subject", new BasicDBObject("$first", "$subject"))
						.append("explanation", new BasicDBObject("$first", "$explanation"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
						.append("status", new BasicDBObject("$first", "$status"))
						.append("appointmentBy", new BasicDBObject("$first", "$appointmentBy"))
						.append("appointmentFor", new BasicDBObject("$first", "$appointmentFor"))
						.append("appointmentForOtherName", new BasicDBObject("$first", "$appointmentForOtherName"))
						.append("count", new BasicDBObject("$first", "$count"))
						.append("cancelledByProfile", new BasicDBObject("$first", "$cancelledByProfile"))
						.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))
						.append("locationName", new BasicDBObject("$first", "$locationName"))
						.append("dentalTreatment", new BasicDBObject("$first", "$dentalTreatment"))
						.append("treatmentId", new BasicDBObject("$first", "$treatmentId"))
						.append("smileBuddyId", new BasicDBObject("$first", "$smileBuddyId"))
						.append("patient", new BasicDBObject("$first", "$patient"))));
//						.append("dentalTreatments", new BasicDBObject("$addToSet", "$dentalTreatments"))));
		Aggregation aggregation = null;

		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
					Aggregation.match(criteriaSecond),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
//							Aggregation.lookup("dental_treatment_detail_cl", "treatmentId", "_id", "dentalTreatments"),
//							Aggregation.unwind("dentalTreatments"),
//							new CustomAggregationOperation(new Document("$unwind",
//									new BasicDBObject("path", "$dentalTreatments").append("preserveNullAndEmptyArrays",
//											true))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
									.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
					projectOperation, groupOperation, sortOperation, Aggregation.skip((long) (page) * size),
					Aggregation.limit(size))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
					Aggregation.match(criteriaSecond),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
//							new CustomAggregationOperation(new Document("$unwind",
//									new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays",
//											true))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$dentalTreatments").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
									.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
					projectOperation, groupOperation)
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

		}

		appointments = mongoTemplate.aggregate(aggregation, AppointmentCollection.class, Appointment.class)
				.getMappedResults();
		System.out.println("aggregation" + aggregation);
		for (Appointment appointment : appointments) {

			if (appointment.getSmileBuddyId() != null) {
				UserCollection smileBuddyUserCollection = userRepository
						.findById(new ObjectId(appointment.getSmileBuddyId())).orElse(null);
				if (smileBuddyUserCollection != null)
					appointment.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
			}
			if (appointment.getTreatmentId() != null) {
				List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(
										Aggregation.match(new Criteria("id").in(appointment.getTreatmentId()))),
								DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
						.getMappedResults();
				List<String> dentalTreatment = new ArrayList<String>();
				for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
					dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
				}
				appointment.setTreatmentNames(dentalTreatment);
			}
		}
//		appointments = new ArrayList<Appointment>();
//		for (SmilebirdAppoinmentLookupResponse smilebirdAppoinmentLookupResponse : smilebirdAppoinmentLookupResponses) {
//			Appointment appointment = new Appointment();
//			BeanUtil.map(smilebirdAppoinmentLookupResponse, appointment);
//			appointments.add(appointment);
//		}
		response.setDataList(appointments);
		response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
				Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
//												new CustomAggregationOperation(new Document("$unwind",
//														new BasicDBObject("path", "$dentalTreatments")
//																.append("preserveNullAndEmptyArrays", true))),
				new CustomAggregationOperation(new Document("$unwind",
						new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
				new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
						new BasicDBObject("if",
								new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
								.append("then", "$$KEEP").append("else", "$$PRUNE")))),

				Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
				projectOperation, groupOperation)
				.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
				AppointmentCollection.class, Appointment.class).getMappedResults().size());

		return response;
	}

	@Override
	public Boolean updateAppointmentState(String id, String state, String userId) {
		Boolean response = false;
		AppointmentCollection appointmentCollection = appointmentRepository.findById(new ObjectId(id)).orElse(null);
		if (appointmentCollection == null)
			throw new BusinessException(ServiceError.InvalidInput, "Appointment Not Found");

		if (appointmentCollection != null) {
			appointmentCollection.setState(AppointmentState.valueOf(state));
			appointmentRepository.save(appointmentCollection);
			response = true;
			// For email sms
			ObjectId doctorId = appointmentCollection.getDoctorId(), locationId = appointmentCollection.getLocationId();
			LocationCollection locationCollection = locationRepository.findById(locationId).orElse(null);
			DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(appointmentCollection.getDoctorId(),
							appointmentCollection.getLocationId());

			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
					appointmentCollection.getTime().getFromTime() % 60);
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			if (clinicProfileCollection != null) {
				sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
			}
			Date _24HourDt = null;
			try {
				_24HourDt = _24HourSDF.parse(_24HourTime);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			UserCollection userCollection = userRepository.findById(doctorId).orElse(null);
			final String locationMapUrl = locationCollection.getGoogleMapShortUrl();
			final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();

			UserCollection patientCard = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (patientCard != null && DPDoctorUtils.anyStringEmpty(patientCard.getWhatsAppNumber()))
				patientCard.setWhatsAppNumber(patientCard.getMobileNumber());
			final String patientName = (patientCard != null ? patientCard.getFirstName() : "");
			final String appointmentId = appointmentCollection.getAppointmentId();
			final String time = _12HourSDF.format(_24HourDt);
			final String date = sdf.format(appointmentCollection.getFromDate());
			final String clinicName = locationCollection.getLocationName();
			final String clinicContactNum = locationCollection.getClinicNumber() != null
					? locationCollection.getClinicNumber()
					: "";
			// sendSMS after appointment is saved

			final DoctorFacility facility = (clinicProfileCollection != null) ? clinicProfileCollection.getFacility()
					: null;
			final String branch = "";
			final String id1 = appointmentCollection.getId().toString(),
					patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
					patientMobileNumber = patientCard != null ? patientCard.getMobileNumber() : null,
					patientWhatsAppNumber = patientCard != null ? patientCard.getWhatsAppNumber() : null,
					patientLanguage = patientCard != null ? patientCard.getLanguage() : null,
					doctorEmailAddress = userCollection.getEmailAddress(),
					doctorMobileNumber = userCollection.getMobileNumber();
			DentalChainAppointmentRequest request = new DentalChainAppointmentRequest();
			request.setDoctorId(appointmentCollection.getDoctorId().toString());
			request.setPatientId(appointmentCollection.getPatientId().toString());
			request.setLocationId(appointmentCollection.getLocationId().toString());
			request.setHospitalId(appointmentCollection.getHospitalId().toString());
			request.setState(AppointmentState.CONFIRM);
			request.setNotifyDoctorByEmail(appointmentCollection.getNotifyDoctorByEmail());
			request.setNotifyDoctorBySms(appointmentCollection.getNotifyDoctorBySms());
			request.setNotifyPatientByEmail(appointmentCollection.getNotifyPatientByEmail());
			request.setNotifyPatientBySms(appointmentCollection.getNotifyPatientBySms());
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					try {
						sendAppointmentEmailSmsNotification(false, request, id, appointmentId, doctorName, patientName,
								time, date, clinicName, clinicContactNum, patientEmailAddress, patientMobileNumber,
								patientWhatsAppNumber, doctorEmailAddress, doctorMobileNumber, facility, branch,
								locationMapUrl, patientLanguage);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		return response;
	}

	@Override
	public Response<Object> getUsersFromUserCl(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient, String fromDate, String toDate, String dateFilterType, String city,
			List<String> treatmentId, String locality, String language, String gender, String followupType, Integer age,
			String complaint, Boolean isDiscarded, Boolean isMobileNumberPresent, String smileBuddyId) {

		Response<Object> response = new Response<Object>();
		List<PatientAppUsersResponse> patientResponse = null;
		try {
			Criteria criteria = new Criteria();

			if (isMobileNumberPresent != null) {
				if (isMobileNumberPresent == true)
					criteria.and("mobileNumber").ne("");
				else
					criteria.and("mobileNumber").is("");
			}

			if (treatmentId != null && !treatmentId.isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : treatmentId) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}

			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			}

			if (from != null && to != null) {
				if (dateFilterType.equals(DateFilterType.FOLLOWUP.getFilter())) {
					criteria.and("followUp").gte(from).lte(to);
				} else if (dateFilterType.equals(DateFilterType.CREATEDTIME.getFilter())) {
					criteria.and("createdTime").gte(from).lte(to);
				} else if (dateFilterType.equals(DateFilterType.UPDATEDTIME.getFilter())) {
					criteria.and("updatedTime").gte(from).lte(to);
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
			}

			if (isDentalChainPatient)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);
			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("smileBuddyId").is(new ObjectId(smileBuddyId));
			if (!DPDoctorUtils.anyStringEmpty(locality))
				criteria.and("locality").is(locality);
			if (!DPDoctorUtils.anyStringEmpty(language))
				criteria.and("language").is(language);
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria.and("city").is(city);
			if (!DPDoctorUtils.anyStringEmpty(gender))
				criteria.and("gender").is(gender);
			if (!DPDoctorUtils.anyStringEmpty(followupType))
				criteria.and("followupType").is(FollowupType.valueOf(followupType));
			if (age > 0)
				criteria.and("age").is(age);
			if (!DPDoctorUtils.anyStringEmpty(complaint))
				criteria.and("complaint").is(complaint);

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			System.out.println(aggregation + "aggregation");

			patientResponse = mongoTemplate.aggregate(aggregation, UserCollection.class, PatientAppUsersResponse.class)
					.getMappedResults();
			List<String> dentalTreatment = null;

			for (PatientAppUsersResponse patientAppUsersResponse : patientResponse) {
				if (patientAppUsersResponse.getTreatmentId() != null) {
					List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation
											.match(new Criteria("id").in(patientAppUsersResponse.getTreatmentId()))),
									DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
							.getMappedResults();
					dentalTreatment = new ArrayList<String>();
					for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
						dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
					}
					patientAppUsersResponse.setTreatmentNames(dentalTreatment);
				}

				if (patientAppUsersResponse.getSmileBuddyId() != null) {
					UserCollection smileBuddyUserCollection = userRepository
							.findById(new ObjectId(patientAppUsersResponse.getSmileBuddyId())).orElse(null);
					if (smileBuddyUserCollection != null)
						patientAppUsersResponse.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
				}
			}

			response.setDataList(patientResponse);
			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					UserCollection.class, PatientAppUsersResponse.class).getMappedResults().size());

			return response;
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patients " + e.getMessage());
		}
	}

	@Override
	public Appointment getPatientLastAppointment(String patientId) {
		Appointment response = null;
		try {
			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("patientId")
					.is(new ObjectId(patientId));

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime dateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

			criteria.and("fromDate").lte(dateTime);

			List<AppointmentLookupResponse> appointmentLookupResponses = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
							Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime")),
							Aggregation.limit(1)), AppointmentCollection.class, AppointmentLookupResponse.class)
					.getMappedResults();

			if (appointmentLookupResponses == null || appointmentLookupResponses.isEmpty()) {
				criteria = new Criteria("patientId").is(new ObjectId(patientId)).and("fromDate").gte(dateTime);

				appointmentLookupResponses = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")), Aggregation.limit(1)),
						AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
			}

			if (appointmentLookupResponses != null) {
				response = new Appointment();
				for (AppointmentLookupResponse collection : appointmentLookupResponses) {
					BeanUtil.map(collection, response);
					if (collection.getDoctor() != null) {
						response.setDoctorName(
								collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while getting patient last appointment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient last appointment");
		}
		return response;
	}

	// to set remainder email to admin for followUp patient
	@Scheduled(cron = "0 0 8 * * ?", zone = "IST") //
	public void scheduleFollowupReminderToUsers() {

		// Getting the default zone id
		Date todayDate = new Date(); // today's time

		ZoneId defaultZoneId = ZoneId.systemDefault();
		// Converting the date to Instant
		Instant instant = todayDate.toInstant();
		System.out.println("todayDate" + todayDate);
		// Converting the Date to LocalDate
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);// set time 00:00
		LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);// set time 23:59

		try {
			Criteria criteria = new Criteria("followUp").gte(startOfDay).lte(endOfDay);

			List<UserCollection> response = null;
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			response = mongoTemplate.aggregate(aggregation, UserCollection.class, UserCollection.class)
					.getMappedResults();

			String emailBody = "";
			if (response != null && !response.isEmpty()) {

				StringJoiner joiner = new StringJoiner(",");
				for (UserCollection userCollection : response) {
					if (userCollection != null) {
						joiner.add(userCollection.getFirstName());
					}
				}
				emailBody = "Your Patient to Follow Up Today: " + joiner.toString();
				String body = mailBodyGenerator.followUpPatientEmailBody(emailBody, "confirmappointment.vm");
				mailService.sendEmail("shreshtha.solanki@healthcoco.com", "Your Patient to Follow Up Today", body,
						null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending Email ");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Email");

		}
	}

	// to set remainder email to admin for followUp patient
	@Scheduled(cron = "0 0 8 * * ?", zone = "IST") //
	public void scheduleFollowupReminderToCampUsers() {

		// Getting the default zone id
		Date todayDate = new Date(); // today's time

		ZoneId defaultZoneId = ZoneId.systemDefault();
		// Converting the date to Instant
		Instant instant = todayDate.toInstant();
		System.out.println("todayDate" + todayDate);
		// Converting the Date to LocalDate
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);// set time 00:00
		LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);// set time 23:59

		try {
			Criteria criteria = new Criteria("followUp").gte(startOfDay).lte(endOfDay);

			List<DentalCampCollection> response = null;
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			response = mongoTemplate.aggregate(aggregation, DentalCampCollection.class, DentalCampCollection.class)
					.getMappedResults();

			String emailBody = "";
			if (response != null && !response.isEmpty()) {
				StringJoiner joiner = new StringJoiner(",");
				for (DentalCampCollection userCollection : response) {
					if (userCollection != null) {
						joiner.add(userCollection.getUserName());
					}
				}
				emailBody = "Your Camp User to Follow Up Today: " + joiner.toString();
				String body = mailBodyGenerator.followUpPatientEmailBody(emailBody, "confirmappointment.vm");
				if (isEnvProduction)
					mailService.sendEmail("rushikesh.deshmukh@healthcoco.com", "Your Camp User to Follow Up Today",
							body, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending Email ");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending Email");
		}
	}

	@Override
	public List<PatientTimelineHistoryResponse> getPatientTimelineHistory(String locationId, String doctorId,
			String patientId) {
		List<PatientTimelineHistoryResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			criteria.and("isDentalChainAppointment").is(true);
			criteria.and("type").is(AppointmentType.APPOINTMENT.getType());

			criteria.and("isPatientDiscarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "createdTime"));

			response = new ArrayList<PatientTimelineHistoryResponse>();

			CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("patientId", "$patientId")
							.append("locationId", "$locationId").append("firstName", "$patientUser.firstName")
							.append("emailAddress", "$patientUser.emailAddress").append("gender", "$patientCard.gender")
							.append("dob", "$patientCard.dob").append("registrationDate", "$patientUser.createdTime")
							.append("patientCreateBy", "$patientUser.createdBy")
							.append("appointmentCreateBy", "$createdBy").append("hospitalId", "$hospitalId")
							.append("time", "$time").append("fromDate", "$fromDate").append("toDate", "$toDate")
							.append("appointmentId", "$appointmentId")
							.append("doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
							.append("appointmentBy", "$appointmentBy").append("appointmentFor", "$appointmentFor")
							.append("appointmentForOtherName", "$appointmentForOtherName")
							.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
							.append("followUp", "$patientUser.followUp")
							.append("followUpReason", "$patientUser.followUpReason")
							.append("platform", "$patientUser.platform").append("dentalTreatment", "$dentalTreatment")
							.append("locationName", "$location.locationName")
							.append("mobileNumber", "$patientUser.mobileNumber")));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("firstName", new BasicDBObject("$first", "$patientUser.firstName"))
							.append("emailAddress", new BasicDBObject("$first", "$patientUser.emailAddress"))
							.append("gender", new BasicDBObject("$first", "$patientCard.gender"))
							.append("mobileNumber", new BasicDBObject("$first", "$patientUser.locationId"))
							.append("dob", new BasicDBObject("$first", "$patientCard.dob"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("followUp", new BasicDBObject("$first", "$patientUser.followUp"))
							.append("platform", new BasicDBObject("$first", "$patientUser.platform"))
							.append("followUpReason", new BasicDBObject("$first", "$atientUser.followUpReason"))
							.append("registrationDate", new BasicDBObject("$first", "$patientUser.createdTime"))
							.append("patientCreateBy", new BasicDBObject("$first", "$patientUser.createdBy"))
							.append("appointmentCreateBy", new BasicDBObject("$first", "$createdBy"))

							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("time", new BasicDBObject("$first", "$time"))
							.append("state", new BasicDBObject("$first", "$state"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
							.append("doctorName", new BasicDBObject("$first", "$doctorName"))
							.append("appointmentBy", new BasicDBObject("$first", "$appointmentBy"))
							.append("appointmentFor", new BasicDBObject("$first", "$appointmentFor"))
							.append("appointmentForOtherName", new BasicDBObject("$first", "$appointmentForOtherName"))
							.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("locationName", new BasicDBObject("$first", "$locationName"))
							.append("dentalTreatment", new BasicDBObject("$first", "$dentalTreatment"))));
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$redact", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq", Arrays.asList("$patientCard.locationId", "$locationId")))
									.append("then", "$$KEEP").append("else", "$$PRUNE")))),

					Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"), Aggregation.unwind("patientUser"),
					projectOperation);

			response = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, PatientTimelineHistoryResponse.class)
					.getMappedResults();
			System.out.println("aggregation" + aggregation);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getCampMsgTemplateByTreatmentName(Boolean preTreatment, Boolean postTreatment,
			Boolean isDiscarded, List<String> treatmentId) {
		List<CampMsgTemplateResponse> responseList = new ArrayList<CampMsgTemplateResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("discarded").ne(true);
			else
				criteria.and("discarded").is(isDiscarded);

			if (treatmentId != null && !treatmentId.isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : treatmentId) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}
			if (preTreatment != null) {
				criteria.and("preTreatment").is(preTreatment);
			}
			if (postTreatment != null) {
				criteria.and("postTreatment").is(postTreatment);
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									CampMsgTemplateCollection.class, CampMsgTemplateResponse.class)
							.getMappedResults().size());

			responseList = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					CampMsgTemplateCollection.class, CampMsgTemplateResponse.class).getMappedResults();

			response.setDataList(responseList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean broadcastByTreatmentToUser(BroadcastByTreatmentRequest request) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (userCollection != null) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				if (request.getTemplateId() != null && !request.getTemplateId().isEmpty()) {
					for (String id : request.getTemplateId()) {
						treatmentObjectIds.add(new ObjectId(id));
					}
				}
				List<CampMsgTemplateCollection> msgTemplateCollections = msgTemplateRepository
						.findByIdIn(treatmentObjectIds);

				if (msgTemplateCollections != null) {
					for (CampMsgTemplateCollection campMsgTemplateCollection : msgTemplateCollections) {
						if (request.getBroadcastType().equals(BroadcastType.SMS)) {
							sendMsg(request, userCollection.getMobileNumber(), campMsgTemplateCollection);
						}

						if (request.getBroadcastType().equals(BroadcastType.WHATSAPP)) {
							sendWhatsapp(request, userCollection.getMobileNumber(), campMsgTemplateCollection);
						}
					}
					response = true;
				}
			}
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}

		return response;
	}

	private void sendMsg(BroadcastByTreatmentRequest request, String mobileNumber,
			CampMsgTemplateCollection campMsgTemplateCollection) {
		try {
			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			if (campMsgTemplateCollection.getRoute() != null)
				smsTrackDetail.setType(campMsgTemplateCollection.getRoute().getType());
			SMSDetail smsDetail = new SMSDetail();
			SMS sms = new SMS();
			if (!DPDoctorUtils.anyStringEmpty(campMsgTemplateCollection.getTemplateId()))
				smsTrackDetail.setTemplateId(campMsgTemplateCollection.getTemplateId());

			if (!DPDoctorUtils.anyStringEmpty(campMsgTemplateCollection.getMessage()))
				sms.setSmsText(campMsgTemplateCollection.getMessage());

			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);

			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			sMSServices.sendDentalChainSMS(smsTrackDetail, true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendWhatsapp(BroadcastByTreatmentRequest request, String mobileNumber,
			CampMsgTemplateCollection campMsgTemplateCollection) {
		try {
			JSONObject requestObject1 = new JSONObject();
			JSONObject requestObject2 = new JSONObject();
			JSONObject buttonValuesObject = new JSONObject();
			JSONArray requestObject3 = new JSONArray();
			JSONArray headerArray = new JSONArray();
			JSONArray buttonValueArray = new JSONArray();

			requestObject1.put("phoneNumber", mobileNumber);
			requestObject1.put("countryCode", "+91");
			requestObject1.put("type", "Template");

			String supportNo = smilebirdSupportNumber;
			requestObject2.put("name", campMsgTemplateCollection.getTemplateName());
			if (campMsgTemplateCollection.getBodyValues() != null
					&& !campMsgTemplateCollection.getBodyValues().isEmpty()) {
				for (String string : campMsgTemplateCollection.getBodyValues()) {
					requestObject3.put(string);
				}
			}
			if (request.getLanguageCode() != null) {
				if (campMsgTemplateCollection.getLanguageCode().equals("en")) {
					requestObject2.put("languageCode", "en");
				} else if (request.getLanguageCode().equals("hi")) {
					requestObject2.put("languageCode", "hi");
				}
			} else
				requestObject2.put("languageCode", "en");
			if (campMsgTemplateCollection.getHeaderValues() != null
					&& !campMsgTemplateCollection.getHeaderValues().isEmpty()) {
				for (String string : campMsgTemplateCollection.getHeaderValues()) {
					headerArray.put(string);
				}
				requestObject2.put("headerValues", headerArray);
			}

			if (campMsgTemplateCollection.getHeaderMediaUrl() != null) {
				headerArray.put(campMsgTemplateCollection.getHeaderMediaUrl());
				requestObject2.put("headerValues", headerArray);
			}
			if (campMsgTemplateCollection.getHeaderFileName() != null) {
				requestObject2.put("fileName", campMsgTemplateCollection.getHeaderFileName());
			}
			if (campMsgTemplateCollection.getButtonValues() != null) {
				buttonValueArray.put(campMsgTemplateCollection.getButtonValues());
				buttonValuesObject.put("1", buttonValueArray);
				requestObject2.put("buttonValues", buttonValuesObject);
			}

			requestObject2.put("bodyValues", requestObject3);
			requestObject1.put("template", requestObject2);

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
	public Response<Object> getUsersFromUserClCount(int size, int page, String searchTerm, String mobileNumber,
			Boolean isDentalChainPatient, String fromDate, String toDate, String dateFilterType, String city,
			List<String> treatmentId, String locality, String language, String gender, String followupType, Integer age,
			String complaint, Boolean isDiscarded, Boolean isMobileNumberPresent, String smileBuddyId) {
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (isMobileNumberPresent != null) {
				if (isMobileNumberPresent == true)
					criteria.and("mobileNumber").ne("");
				else
					criteria.and("mobileNumber").is("");
			}

			if (treatmentId != null && !treatmentId.isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : treatmentId) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}

			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			}

			if (from != null && to != null) {
				if (dateFilterType.equals(DateFilterType.FOLLOWUP.getFilter())) {
					criteria.and("followUp").gte(from).lte(to);
				} else if (dateFilterType.equals(DateFilterType.CREATEDTIME.getFilter())) {
					criteria.and("createdTime").gte(from).lte(to);
				} else if (dateFilterType.equals(DateFilterType.UPDATEDTIME.getFilter())) {
					criteria.and("updatedTime").gte(from).lte(to);
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("mobileNumber").is(mobileNumber);
			}

			if (isDentalChainPatient)
				criteria.and("isDentalChainPatient").is(isDentalChainPatient);
			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("smileBuddyId").is(new ObjectId(smileBuddyId));
			if (!DPDoctorUtils.anyStringEmpty(locality))
				criteria.and("locality").is(locality);
			if (!DPDoctorUtils.anyStringEmpty(language))
				criteria.and("language").is(language);
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria.and("city").is(city);
			if (!DPDoctorUtils.anyStringEmpty(gender))
				criteria.and("gender").is(gender);
			if (!DPDoctorUtils.anyStringEmpty(followupType))
				criteria.and("followupType").is(FollowupType.valueOf(followupType));
			if (age > 0)
				criteria.and("age").is(age);
			if (!DPDoctorUtils.anyStringEmpty(complaint))
				criteria.and("complaint").is(complaint);

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					UserCollection.class, PatientAppUsersResponse.class).getMappedResults().size());

			return response;
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting count " + e.getMessage());
		}
	}

	@Override
	public Response<Object> getDoctorsCount(String city, String locationId, List<String> doctorId, String gender) {
		Response<Object> response = new Response<Object>();
		try {

			Aggregation aggregation = null;
			Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true).and("user")
					.exists(true).and("location.isDentalChain").is(true).and("user.mobileNumber").exists(true);

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(gender)) {
				criteria.and("doctor.gender").regex(gender);
			}

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId) {
					doctorObjectIds.add(new ObjectId(id));
				}
				criteria.and("doctorId").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(city)) {
				criteria.and("location.city").regex(city);
			}

			criteria.and("user.isActive").is(true);

			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("id", "$doctorId"), Fields.field("mobileNumber", "$user.mobileNumber")));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);
			System.out.println("aggregation" + aggregation);
			response.setCount(
					mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class, UserCollection.class)
							.getMappedResults().size());

			return response;
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting count " + e.getMessage());
		}
	}

	@Override
	public Boolean updateAppointmentStatus(String appointmentId, String status, String userId) {
		Boolean response = false;
		AppointmentCollection appointmentCollection = appointmentRepository.findById(new ObjectId(appointmentId))
				.orElse(null);
		if (appointmentCollection == null)
			throw new BusinessException(ServiceError.InvalidInput, "Appointment Not Found");

		if (appointmentCollection != null) {
			appointmentCollection.setStatus(QueueStatus.valueOf(status));
			appointmentRepository.save(appointmentCollection);
			response = true;
		}
		return response;
	}

}