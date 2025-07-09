package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.dpdocter.Scheduler.BroadCastAsyncService;
import com.dpdocter.beans.BroadcastRequest;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Message;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.UserMobileNumbers;

import com.dpdocter.collections.BroadCastCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.MessageCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BroadCastRepository;
import com.dpdocter.repository.MessageRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BroadCastMailRequest;
import com.dpdocter.request.BroadCastRequest;
import com.dpdocter.request.MessageRequest;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.response.PatientAppUsersResponse;
import com.dpdocter.response.RazorPayAccountResponse;
import com.dpdocter.services.BroadCastService;
import com.dpdocter.services.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class BroadCastServiceImpl implements BroadCastService {

	private static Logger logger = LogManager.getLogger(BroadCastServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private BroadCastAsyncService broadCastAsyncService;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BroadCastRepository broadCastRepository;
	
	@Autowired
	private SMSTrackRepository smsTrackRepository;
	
	@Value(value = "${AUTH_KEY}")
	private String AUTH_KEY;

	@Value(value = "${SENDER_ID}")
	private String SENDER_ID;

	@Value(value = "${UNICODE}")
	private String UNICODE;

	@Value(value = "${DEFAULT_ROUTE}")
	private String ROUTE;

	@Value(value = "${PROMOTIONAL_ROUTE}")
	private String PROMOTIONAL_ROUTE;

	@Value(value = "${DEFAULT_COUNTRY}")
	private String COUNTRY_CODE;

	@Value(value = "${SMS_POST_URL}")
	private String SMS_POST_URL;

	@Value(value = "${SERVICE_ID}")
	private String SID;
	
	@Value(value = "${API_KEY}")
	private String KEY;
	
	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Value(value = "${mobile.numbers.resource}")
	private String MOBILE_NUMBERS_RESOURCE;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private BroadCastService broadcastService;
	
	@Autowired
	private MailService mailService;



	public Boolean broadcastMail(BroadCastMailRequest request) {
		try {
			List<ObjectId> subscriberIds = null;
			List<ObjectId> specialityId = null;
			if (request.getSubscriberIds() != null && !request.getSubscriberIds().isEmpty()) {
				subscriberIds = new ArrayList<ObjectId>();
				for (String subscriberId : request.getSubscriberIds()) {
					if (!DPDoctorUtils.anyStringEmpty(subscriberId)) {
						subscriberIds.add(new ObjectId(subscriberId));
					}
				}
			}
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getAdminId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "invalid ADMIN");
			}

			if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {

				List<SpecialityCollection> specialityCollections = specialityRepository
						.findBySpecialityIn(request.getSpeciality());
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}
			
			else if(request.getSpecialityType()!=null && request.getSpecialityType().equalsIgnoreCase("ALL")) {
				List<SpecialityCollection> specialityCollections = specialityRepository.findAll();
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}
			
			if (request.getUserTypes() != null && !request.getUserTypes().isEmpty())
				for (String usertype : request.getUserTypes()) {
					if (usertype.equalsIgnoreCase("DOCTOR")) {
						Aggregation aggregation = null;
						Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true)
								.and("user.emailAddress").exists(true);
						if (specialityId != null && !specialityId.isEmpty()) {
							criteria.and("doctor.specialities").in(specialityId);
						}
						if (subscriberIds != null && !subscriberIds.isEmpty()) {
							criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
						}
						
						if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
							criteria.and("doctor.gender").regex(request.getGender());
						}
						
						if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
							criteria.and("location.city").regex(request.getCity());
						}
						
						if (request.getActivate()!=null) {
							criteria.and("user.isActive").is(request.getActivate());
						}
						
						
						ProjectionOperation projectList = new ProjectionOperation(Fields.from(
								Fields.field("id", "$doctorId"), Fields.field("emailAddress", "$user.emailAddress")));

						aggregation = Aggregation
								.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
										Aggregation.unwind("user"),
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
										Aggregation.unwind("doctor"), Aggregation.match(criteria),
										new CustomAggregationOperation(new Document("$group",
												new BasicDBObject("_id", "$doctorId").append("emailAddress",
														new BasicDBObject("$first", "$user.emailAddress")))));

						AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
								DoctorClinicProfileCollection.class, UserCollection.class);
						List<UserCollection> userCollections = results.getMappedResults();
						if (userCollections != null && !userCollections.isEmpty()) {
							broadCastAsyncService.broadcastMail(userCollections, request);
						}
					}
					if (usertype.equalsIgnoreCase("LAB")) {
						Aggregation aggregation = null;
						Criteria criteria = new Criteria("doctor").exists(true).and("user").exists(true)
								.and("user.emailAddress").exists(true);

						if (request.getIsParent() != null) {
							criteria.and("location.isParent").is(request.getIsParent());
						}

						if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
							criteria.and("doctor.gender").regex(request.getGender());
						}

						if (request.getIsDentalWorksLab() != null) {
							criteria.and("location.isDentalWorksLab").is(request.getIsDentalWorksLab());
						}

						if (request.getIsDentalImagingLab() != null) {
							criteria.and("location.isDentalImagingLab").is(request.getIsDentalImagingLab());
						}

						if (request.getIsDiagnosisLab() != null) {
							new Criteria("location.isLab").is(request.getIsDiagnosisLab());
						}

						ProjectionOperation projectList = new ProjectionOperation(Fields.from(
								Fields.field("id", "$doctorId"), Fields.field("emailAddress", "$user.emailAddress")));
						if (specialityId != null && !specialityId.isEmpty()) {
							criteria.and("doctor.specialities").in(specialityId);
						}
						if (subscriberIds != null && !subscriberIds.isEmpty()) {
							criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
						}
						aggregation = Aggregation.newAggregation(
								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
								Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

						AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
								DoctorClinicProfileCollection.class, UserCollection.class);
						List<UserCollection> userCollections = results.getMappedResults();
						if (userCollections != null && !userCollections.isEmpty()) {
							broadCastAsyncService.broadcastMail(userCollections, request);
						}
					}
					if (usertype.equalsIgnoreCase("PHARMACY")) {
						Criteria criteria = new Criteria("user.userState").is(UserState.PHARMACY)
								.and("localeEmailAddress").exists(true);
						if (subscriberIds != null && !subscriberIds.isEmpty()) {
							criteria.and("_id").in(subscriberIds);
						}
						ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
								Fields.field("emailAddress", "$user.emailAddress")));

						Aggregation aggregation = Aggregation.newAggregation(
								Aggregation.lookup("user_cl", "contactNumber", "mobileNumber", "user"),
								Aggregation.unwind("user"), Aggregation.match(criteria), projectList);

						AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
								LocaleCollection.class, UserCollection.class);
						List<UserCollection> userCollections = results.getMappedResults();
						if (userCollections != null && !userCollections.isEmpty()) {
							broadCastAsyncService.broadcastMail(userCollections, request);
						}
					}
					if (usertype.equalsIgnoreCase("PATIENT")) {
						Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE)
								.and("role.role").is("PATIENT").and("user.signedUp").is(true);
						if (subscriberIds != null && !subscriberIds.isEmpty()) {
							criteria.and("userId").in(subscriberIds);
						}
						ProjectionOperation projectList = new ProjectionOperation(Fields.from(
								Fields.field("id", "$userId"), Fields.field("emailAddress", "$user.emailAddress")));
						Aggregation aggregation = Aggregation.newAggregation(
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
								Aggregation.match(criteria), projectList,
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", new BasicDBObject("emailAddress", "$emailAddress"))
												.append("emailAddress",
														new BasicDBObject("$first", "$emailAddress")))));

						AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
								UserRoleCollection.class, UserCollection.class);
						List<UserCollection> userCollections = results.getMappedResults();
						
//				Aggregation		aggregation = Aggregation.newAggregation(
//								Aggregation.lookup("user_cl","userId","_id","patient"),
//								Aggregation.unwind("patient"),
//								Aggregation.match(new Criteria("user.signedUp").is(true)),
//								new CustomAggregationOperation(new Document("$group",
//										new BasicDBObject("_id", "$_id")
//										.append("title", new BasicDBObject("$first", "$title"))
//										.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//
//										.append("lastName", new BasicDBObject("$first", "$lastName"))
//										.append("middleName", new BasicDBObject("$first", "$middleName"))
//										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//										.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//
//										.append("mobileNumber", new BasicDBObject("$first", "$patient.mobileNumber"))
//
//										.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//										.append("createdTime",  new BasicDBObject("$first", "$createdTime"))
//										.append("updatedTime",  new BasicDBObject("$first", "$updatedTime"))
//										)),
//								Aggregation.sort(new Sort(Direction.DESC, "createdTime")))
//								.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
//					
//					List<UserCollection> userCollections = mongoTemplate.aggregate(aggregation, PatientCollection.class, UserCollection.class).getMappedResults();

						
						if (userCollections != null && !userCollections.isEmpty()) {
							broadCastAsyncService.broadcastMail(userCollections, request);
						}
					}
				}
			BroadCastCollection broadCastCollection = new BroadCastCollection();
			if (request.getRecivers() != null && !request.getRecivers().isEmpty()) {
				broadCastAsyncService.broadcastMail(null, request);
				broadCastCollection.setUserTypes(new ArrayList<String>());
				broadCastCollection.getUserTypes().add("UNKNOWN");
			} else {
				broadCastCollection.setUserTypes(request.getUserTypes());
			}

			if (!DPDoctorUtils.anyStringEmpty(userCollection.getFirstName())) {
				broadCastCollection.setCreatedBy(userCollection.getFirstName());
			}

			broadCastCollection.setCreatedTime(new Date());
			broadCastCollection.setMessageType("EMAIL");
			broadCastCollection.setMessage(request.getBody());
			broadCastRepository.save(broadCastCollection);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Broadcast mail");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Broadcast mail");

		}

		return true;
	}

	public Boolean broadcastPushNotification(BroadCastRequest request) {
		try {
			List<ObjectId> subscriberIds = null;
			List<ObjectId> specialityId = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getAdminId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "invalid ADMIN");
			}
			if (request.getSubscriberIds() != null && !request.getSubscriberIds().isEmpty()) {
				subscriberIds = new ArrayList<ObjectId>();
				for (String subscriberId : request.getSubscriberIds()) {
					if (!DPDoctorUtils.anyStringEmpty(subscriberId)) {
						subscriberIds.add(new ObjectId(subscriberId));
					}
				}
			}
			if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {

				List<SpecialityCollection> specialityCollections = specialityRepository
						.findBySpecialityIn(request.getSpeciality());
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}
			else if(request.getSpecialityType()!=null && request.getSpecialityType().equalsIgnoreCase("ALL")) {
				List<SpecialityCollection> specialityCollections = specialityRepository.findAll();
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}
			for (String usertype : request.getUserTypes()) {
				if (usertype.equalsIgnoreCase("DOCTOR")) {
					Aggregation aggregation = null;
					Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true)
							.and("user").exists(true);
					if (specialityId != null && !specialityId.isEmpty()) {
						criteria.and("doctor.specialities").in(specialityId);
					}
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
						criteria.and("location.city").regex(request.getCity());
					}
					
					if (request.getActivate()!=null) {
						criteria.and("user.isActive").is(request.getActivate());
					}
					ProjectionOperation projectList = new ProjectionOperation(
							Fields.from(Fields.field("_id", "$doctorId")));

					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							DoctorClinicProfileCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
		//			System.out.println("aggregation"+aggregation);
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastNotication(userCollections, request,
								RoleEnum.valueOf(usertype.toUpperCase()));
					}
				}
				if (usertype.equalsIgnoreCase("LAB")) {
					Aggregation aggregation = null;
					Criteria criteria = new Criteria("doctor").exists(true).and("user").exists(true);
					ProjectionOperation projectList = new ProjectionOperation(
							Fields.from(Fields.field("id", "$doctorId")));

					if (request.getIsParent() != null) {
						criteria.and("location.isParent").is(request.getIsParent());
					}

					if (request.getIsDentalWorksLab() != null) {
						criteria.and("location.isDentalWorksLab").is(request.getIsDentalWorksLab());
					}
					if (request.getIsDiagnosisLab() != null) {
						new Criteria("location.isLab").is(request.getIsDiagnosisLab());
					}

					if (request.getIsDentalImagingLab() != null) {
						criteria.and("location.isDentalImagingLab").is(request.getIsDentalImagingLab());
					}
					if (specialityId != null && !specialityId.isEmpty()) {
						criteria.and("doctor.specialities").in(specialityId);
					}
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
					}
					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							DoctorClinicProfileCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastNotication(userCollections, request,
								RoleEnum.valueOf(usertype.toUpperCase()));
					}
				}
				if (usertype.equalsIgnoreCase("PHARMACY")) {
					Criteria criteria = new Criteria("user.userState").is(UserState.PHARMACY);
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("id").in(subscriberIds);
					}
					ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id")));
					Aggregation aggregation = Aggregation.newAggregation(
							Aggregation.lookup("user_cl", "contactNumber", "mobileNumber", "user"),
							Aggregation.unwind("user"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							LocaleCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastNotication(userCollections, request,
								RoleEnum.valueOf(usertype.toUpperCase()));
					}
				}
//				if (usertype.equalsIgnoreCase("PATIENT")) {
//					Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE).and("role.role")
//							.is("PATIENT").and("user.signedUp").is(true);
//					if (subscriberIds != null && !subscriberIds.isEmpty()) {
//						criteria.and("userId").in(subscriberIds);
//					}
//					ProjectionOperation projectList = new ProjectionOperation(
//							Fields.from(Fields.field("id", "$userId")));
//					Aggregation aggregation = Aggregation.newAggregation(
//							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
//							Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
//							Aggregation.match(criteria), projectList,
//							new CustomAggregationOperation(new Document("$group",
//									new BasicDBObject("_id", new BasicDBObject("mobileNumber", "$mobileNumber"))
//											.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))));
//
//					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
//							UserRoleCollection.class, UserCollection.class);
//					List<UserCollection> userCollections = results.getMappedResults();
//					System.out.println("userCollections"+userCollections);
//					System.out.println("aggregation"+aggregation);
//					if (userCollections != null && !userCollections.isEmpty()) {
//						broadCastAsyncService.broadcastNotication(userCollections, request,
//								RoleEnum.valueOf(usertype.toUpperCase()));
//					}
//				}
				
				if (usertype.equalsIgnoreCase("PATIENT")) {
					Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE)
						//	.and("role.role").is("PATIENT")
							.and("user").exists(true).and("user.signedUp").is(true);
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("userId").in(subscriberIds);
					}
				
					ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("_id", "$userId"),
							Fields.field("mobileNumber", "$user.mobileNumber")));
					Aggregation aggregation = Aggregation.newAggregation(
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
					//		Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
							Aggregation.match(criteria), projectList
//							new CustomAggregationOperation(new Document("$group",
//									new BasicDBObject("_id", new BasicDBObject("_id", "$_id"))
//											.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))
									);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							PatientCollection.class, UserCollection.class);
					
					List<UserCollection> userCollections = results.getMappedResults();
			//		System.out.println("userCollections"+userCollections);
			//		System.out.println("aggregation"+aggregation);
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastNotication(userCollections, request,
								RoleEnum.valueOf(usertype.toUpperCase()));
					}
					
					
					
				}

			}
			BroadCastCollection broadCastCollection = new BroadCastCollection();
			if (!DPDoctorUtils.anyStringEmpty(userCollection.getFirstName())) {
				broadCastCollection.setCreatedBy(userCollection.getFirstName());
			}
			broadCastCollection.setCreatedTime(new Date());
			broadCastCollection.setUserTypes(request.getUserTypes());
			broadCastCollection.setMessageType("NOTIFICATION");
			broadCastCollection.setMessage(request.getMessage());
			broadCastRepository.save(broadCastCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Broadcast notification");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Broadcast notification");
		}

		return true;
	}

	public Boolean broadcastSMS(BroadCastRequest request) {
		try {
			List<ObjectId> subscriberIds = null;
			List<ObjectId> specialityId = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getAdminId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "invalid ADMIN");
			}
			if (request.getSubscriberIds() != null && !request.getSubscriberIds().isEmpty()) {
				subscriberIds = new ArrayList<ObjectId>();
				for (String subscriberId : request.getSubscriberIds()) {
					if (!DPDoctorUtils.anyStringEmpty(subscriberId)) {
						subscriberIds.add(new ObjectId(subscriberId));
					}
				}
			}
			if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {

				List<SpecialityCollection> specialityCollections = specialityRepository
						.findBySpecialityIn(request.getSpeciality());
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}
			else if(request.getSpecialityType() != null && request.getSpecialityType().equalsIgnoreCase("ALL")) {
				List<SpecialityCollection> specialityCollections = specialityRepository.findAll();
				specialityId = new ArrayList<ObjectId>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialityId.add(specialityCollection.getId());
				}
			}

			for (String usertype : request.getUserTypes()) {
				if (usertype.equalsIgnoreCase("DOCTOR")) {
					Aggregation aggregation = null;
					Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true)
							.and("user").exists(true).and("user.mobileNumber").exists(true);
					if (specialityId != null && !specialityId.isEmpty()) {
						criteria.and("doctor.specialities").in(specialityId);
					}
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
						criteria.and("doctor.gender").regex(request.getGender());
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
						criteria.and("location.city").regex(request.getCity());
					}
					
					if (request.getActivate()!=null) {
						criteria.and("user.isActive").is(request.getActivate());
					}
					
					ProjectionOperation projectList = new ProjectionOperation(Fields
							.from(Fields.field("id", "$doctorId"), Fields.field("mobileNumber", "$user.mobileNumber")));

					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							DoctorClinicProfileCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastSMS(userCollections, request);
					}
				}
				if (usertype.equalsIgnoreCase("LAB")) {
					Aggregation aggregation = null;
					Criteria criteria = new Criteria("doctor").exists(true).and("user").exists(true)
							.and("user.mobileNumber").exists(true);
					ProjectionOperation projectList = new ProjectionOperation(Fields
							.from(Fields.field("id", "$doctorId"), Fields.field("mobileNumber", "$user.mobileNumber")));
					if (specialityId != null && !specialityId.isEmpty()) {
						criteria.and("doctor.specialities").in(specialityId);
					}
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("doctor.userId").in(subscriberIds).and("doctorId").in(subscriberIds);
					}
					
					if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
						criteria.and("doctor.gender").regex(request.getGender());
					}
					if (request.getIsParent() != null) {
						criteria.and("location.isParent").is(request.getIsParent());
					}

					if (request.getIsDentalWorksLab() != null) {
						criteria.and("location.isDentalWorksLab").is(request.getIsDentalWorksLab());
					}

					if (request.getIsDentalImagingLab() != null) {
						criteria.and("location.isDentalImagingLab").is(request.getIsDentalImagingLab());
					}
					if (request.getIsDiagnosisLab() != null) {
						new Criteria("location.isLab").is(request.getIsDiagnosisLab());
					}
					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							DoctorClinicProfileCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastSMS(userCollections, request);
					}
				}
				if (usertype.equalsIgnoreCase("PHARMACY")) {
					Criteria criteria = new Criteria("user.userState").is(UserState.PHARMACY);
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("id").in(subscriberIds);
					}
					ProjectionOperation projectList = new ProjectionOperation(
							Fields.from(Fields.field("id", "$id"), Fields.field("mobileNumber", "$user.mobileNumber")));
					Aggregation aggregation = Aggregation.newAggregation(
							Aggregation.lookup("user_cl", "contactNumber", "mobileNumber", "user"),
							Aggregation.unwind("user"), Aggregation.match(criteria), projectList);

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							LocaleCollection.class, UserCollection.class);
					List<UserCollection> userCollections = results.getMappedResults();
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastSMS(userCollections, request);
					}
				}
				if (usertype.equalsIgnoreCase("PATIENT")) {
					Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE).and("role.role")
							.is("PATIENT").and("user").exists(true).and("user.signedUp").is(true);
					if (subscriberIds != null && !subscriberIds.isEmpty()) {
						criteria.and("userId").in(subscriberIds);
					}
					ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$userId"),
							Fields.field("mobileNumber", "$user.mobileNumber")));
					Aggregation aggregation = Aggregation.newAggregation(
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
							Aggregation.match(criteria), projectList,
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", new BasicDBObject("mobileNumber", "$mobileNumber"))
											.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))));

					AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
							UserRoleCollection.class, UserCollection.class);
					
					List<UserCollection> userCollections = results.getMappedResults();
				//	System.out.println("userCollections"+userCollections);
					if (userCollections != null && !userCollections.isEmpty()) {
						broadCastAsyncService.broadcastSMS(userCollections, request);
					}
				}
			}

			BroadCastCollection broadCastCollection = new BroadCastCollection();
			if (!DPDoctorUtils.anyStringEmpty(userCollection.getFirstName())) {
				broadCastCollection.setCreatedBy(userCollection.getFirstName());
			}
			broadCastCollection.setCreatedTime(new Date());
			broadCastCollection.setUserTypes(request.getUserTypes());
			broadCastCollection.setMessageType("SMS");
			broadCastCollection.setMessage(request.getMessage());
			broadCastRepository.save(broadCastCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Broadcast sms");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Broadcast SMS");
		}

		return true;
	}
	
	
@Override
	public String sendSms(List<String>mobileNumbers,String message,String type) {
		String response = null;
		try {

			 
			 message = StringEscapeUtils.unescapeJava(message);
     	
		
			String strUrl = "https://api.kaleyra.io/v1/"+SID+"/messages";
			
			List<String> numberlist = new ArrayList<String>(mobileNumbers);
			String numberString = StringUtils.join(numberlist, ',');
			//working
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.post().addParameter("to",numberString)
					.addParameter("type", type)
					.addParameter("body", message)
					.addParameter("sender",SENDER_ID)
			  .setUri(strUrl)
			  .setHeader( "api-key", KEY)
			  .build();
		
			 org.apache.http.HttpResponse responses = client.execute(httprequest);

			 
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
			   
			   
			 responses.getEntity().writeTo(out);
			 String responseString = out.toString();
			System.out.println("responseString"+responseString);
			  ObjectMapper mapper = new ObjectMapper();

			MessageResponse list = mapper.readValue(out.toString(),MessageResponse.class);
	       
			MessageCollection collection=new MessageCollection();
			list.setMessageId(list.getId());
			list.setId(null);
			BeanUtil.map(list, collection);
			
			collection.setCreatedTime(new Date());
			collection.setUpdatedTime(new Date());
			messageRepository.save(collection);			
			response=list.getMessageId();
			
	
		} catch (Exception e) {

			e.printStackTrace();
			return "Failed";
		}

		return response.toString();

	}

	@Override
	public String sendSMSToUser(MessageRequest request) {
		String response=null;
		try {
			String sms=null;
			Integer size=100;
			if(request.getType().equals("SMS")) {
			if(request.getMobileNumber() !=null && request.getMessage() !=null && request.getSmsType()!=null)
			{
				
				List<String> sublist=null;
				for(int start=0;start<request.getMobileNumber().size();start+=size)
					{
						int end=Math.min(start+size,request.getMobileNumber().size());
						 sublist=request.getMobileNumber().subList(start, end);
				//		 System.out.println("sublist"+sublist);
			
				sms=broadcastService.sendSms(sublist, request.getMessage(), request.getSmsType().getType());
					}
				if(sms !=null)
				 response="Sms Sent Succesfully";
				}
			}
			else if(request.getType().equals("EMAIL"))
			{
				
				for(String email:request.getMobileNumber())
					{
						
				Boolean status=mailService.sendEmail(email,request.getSubject(),request.getMessage(),null);
					if(status==true)
						response="Email Sent Successfully";
					}
			}
			
		}catch (BusinessException e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Integer countUsers(BroadcastRequest request) {
		Integer response=0;
	try {
		List<ObjectId> specialityId = null;
		
		if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {

			List<SpecialityCollection> specialityCollections = specialityRepository
					.findBySpecialityIn(request.getSpeciality());
			specialityId = new ArrayList<ObjectId>();
			for (SpecialityCollection specialityCollection : specialityCollections) {
				specialityId.add(specialityCollection.getId());
			}
		}
		if (request.getRoleType().equalsIgnoreCase("PATIENT")) {
			Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE).and("role.role")
					.is("PATIENT").and("user").exists(true).and("user.signedUp").is(true);
			
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$userId"),
					Fields.field("mobileNumber", "$user.mobileNumber")));
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
					Aggregation.match(criteria), projectList,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("mobileNumber", "$mobileNumber"))
									.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))));

			List<UserCollection> results = mongoTemplate.aggregate(aggregation,
					UserRoleCollection.class, UserCollection.class).getMappedResults();
		response=results.size();
		}
		else if (request.getRoleType().equalsIgnoreCase("DOCTOR")) {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true)
					.and("user").exists(true).and("user.mobileNumber").exists(true);
			if (specialityId != null && !specialityId.isEmpty()) {
				criteria.and("doctor.specialities").in(specialityId);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(request.getCity())) {
				criteria.and("location.city").regex(request.getCity());
			}
//			if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {
//
//				List<SpecialityCollection> specialityCollections = specialityRepository
//						.findBySpecialityIn(request.getSpeciality());
//				specialityId = new ArrayList<ObjectId>();
//				for (SpecialityCollection specialityCollection : specialityCollections) {
//					specialityId.add(specialityCollection.getId());
//				}
//			}
			
			
			ProjectionOperation projectList = new ProjectionOperation(Fields
					.from(Fields.field("id", "$doctorId"), Fields.field("mobileNumber", "$user.mobileNumber")));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					Aggregation.unwind("user"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"),
					Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

			List<UserCollection> results = mongoTemplate.aggregate(aggregation,
					DoctorClinicProfileCollection.class, UserCollection.class).getMappedResults();
			
			response=results.size();
		}
		
	}
	catch (BusinessException e) {
		logger.error("Error : " + e.getMessage());
		throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
	}

}
