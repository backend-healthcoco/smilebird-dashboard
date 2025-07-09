package com.dpdocter.services.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.ConferenceBulkSMS;
import com.dpdocter.beans.ConfexUser;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.OrganizingCommittee;
import com.dpdocter.beans.RegisteredUser;
import com.dpdocter.beans.ResearchPaperCategory;
import com.dpdocter.beans.ResearchPaperSubCategory;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.collections.ConfexUserCollection;
import com.dpdocter.collections.DoctorConferenceAgendaCollection;
import com.dpdocter.collections.DoctorConferenceCollection;
import com.dpdocter.collections.DoctorConferenceSessionCollection;
import com.dpdocter.collections.ResearchPaperCategoryCollection;
import com.dpdocter.collections.ResearchPaperSubCategoryCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SessionTopicCollection;
import com.dpdocter.collections.SpeakerProfileCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ConfexUserRepository;
import com.dpdocter.repository.DoctorConferenceRepository;
import com.dpdocter.repository.DoctorConferenceSessionRepository;
import com.dpdocter.repository.DoctorconferenceAgendaRepository;
import com.dpdocter.repository.ResearchPaperCategoryRepository;
import com.dpdocter.repository.ResearchPaperSubCategoryRepository;
import com.dpdocter.repository.SessionTopicRepository;
import com.dpdocter.repository.SpeakerProfileRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.request.ConferenceBulkSMSRequest;
import com.dpdocter.request.ConfexAdminRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.OrganizingCommitteeResponse;
import com.dpdocter.response.SessionDateResponse;
import com.dpdocter.services.ConferenceService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class ConferenceServiceImpl implements ConferenceService {

	private static Logger logger = LogManager.getLogger(ConferenceServiceImpl.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private SpeakerProfileRepository speakerProfileRepository;

	@Autowired
	private SessionTopicRepository sessionTopicRepository;

	@Autowired
	private DoctorConferenceRepository doctorConferenceRepository;

	@Autowired
	private DoctorconferenceAgendaRepository doctorConferenceAgendaRepository;

	@Autowired
	private DoctorConferenceSessionRepository doctorConferenceSessionRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private ConfexUserRepository confexUserRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private ResearchPaperCategoryRepository researchPaperCategoryRepository;

	@Autowired
	private ResearchPaperSubCategoryRepository researchPaperSubCategoryRepository;

	@Value(value = "${verify.pharmacy.link}")
	private String pharmacyVerificationLink;

	@Value(value = "${confex.welcome.link}")
	private String welcomeLink;

	@Value(value = "${confex.setpassword.link}")
	private String setPasswordLink;

	@Override
	public String uploadImages(FileDetails request, String module) {
		String response = null;
		try {
			request.setFileName(request.getFileName() + new Date().getTime());
			String path = module + File.separator + "images";
			ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request, path, false);
			response = imagePath + imageURLResponse.getImageUrl();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;

	}

	@Override
	public SessionTopic addEditTopic(SessionTopic request) {
		SessionTopic response = null;
		try {
			SessionTopicCollection sessionTopicCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				sessionTopicCollection = sessionTopicRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (sessionTopicCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Session Topic Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(sessionTopicCollection.getCreatedBy());
				request.setCreatedTime(sessionTopicCollection.getCreatedTime());
				BeanUtil.map(request, sessionTopicCollection);

			} else {
				sessionTopicCollection = new SessionTopicCollection();
				BeanUtil.map(request, sessionTopicCollection);
				sessionTopicCollection.setCreatedBy("ADMIN");
				sessionTopicCollection.setUpdatedTime(new Date());
				sessionTopicCollection.setCreatedTime(new Date());
			}
			sessionTopicCollection = sessionTopicRepository.save(sessionTopicCollection);
			response = new SessionTopic();
			BeanUtil.map(sessionTopicCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Session Topic " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SessionTopic> getTopics(int size, int page, boolean discarded, String searchTerm) {
		List<SessionTopic> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("topic").regex("^" + searchTerm, "i"),
						new Criteria("topic").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "topic")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "topic")));
			}
			response = mongoTemplate.aggregate(aggregation, SessionTopicCollection.class, SessionTopic.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Session Topic " + e.getMessage());

		}
		return response;
	}

	@Override
	public SessionTopic deleteTopic(String id, boolean discarded) {
		SessionTopic response = null;
		try {
			SessionTopicCollection sessionTopicCollection = sessionTopicRepository.findById(new ObjectId(id))
					.orElse(null);
			if (sessionTopicCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Session Topic Not found with Id");
			}
			sessionTopicCollection.setDiscarded(discarded);
			sessionTopicCollection.setUpdatedTime(new Date());
			sessionTopicCollection = sessionTopicRepository.save(sessionTopicCollection);

			response = new SessionTopic();
			BeanUtil.map(sessionTopicCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Session Topic " + e.getMessage());

		}
		return response;

	}

	@Override
	public SessionTopic getTopic(String id) {
		SessionTopic response = null;
		try {
			SessionTopicCollection sessionTopicCollection = sessionTopicRepository.findById(new ObjectId(id))
					.orElse(null);
			response = new SessionTopic();
			BeanUtil.map(sessionTopicCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Session Topic " + e.getMessage());

		}
		return response;

	}

	@Override
	public SpeakerProfile addEditSpeakerProfile(SpeakerProfile request) {
		SpeakerProfile response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getProfileImage())) {
				request.setProfileImage(request.getProfileImage().replace(imagePath, ""));
			}
			SpeakerProfileCollection speakerProfileCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				speakerProfileCollection = speakerProfileRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (speakerProfileCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Speaker Profile Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(speakerProfileCollection.getCreatedBy());
				request.setCreatedTime(speakerProfileCollection.getCreatedTime());
				BeanUtil.map(request, speakerProfileCollection);

			} else {
				speakerProfileCollection = new SpeakerProfileCollection();
				BeanUtil.map(request, speakerProfileCollection);
				speakerProfileCollection.setCreatedBy("ADMIN");
				speakerProfileCollection.setUpdatedTime(new Date());
				speakerProfileCollection.setCreatedTime(new Date());
			}
			speakerProfileCollection = speakerProfileRepository.save(speakerProfileCollection);
			response = new SpeakerProfile();
			BeanUtil.map(speakerProfileCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getProfileImage())) {
				response.setProfileImage(getFinalImageURL(response.getProfileImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while addedit Speaker Profile " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Speaker Profile " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<SpeakerProfile> getSpeakerProfiles(int size, int page, boolean discarded, String searchTerm) {
		List<SpeakerProfile> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "firstName")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, SpeakerProfileCollection.class, SpeakerProfile.class)
					.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (SpeakerProfile speaker : response) {
					speaker.setProfileImage(getFinalImageURL(speaker.getProfileImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Speaker Profile " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Speaker Profile " + e.getMessage());

		}
		return response;
	}

	@Override
	public SpeakerProfile deleteSpeakerProfile(String id, boolean discarded) {
		SpeakerProfile response = null;
		try {
			SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository.findById(new ObjectId(id))
					.orElse(null);
			if (speakerProfileCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Speaker Profile Not found with Id");
			}
			speakerProfileCollection.setDiscarded(discarded);
			speakerProfileCollection.setUpdatedTime(new Date());
			speakerProfileCollection = speakerProfileRepository.save(speakerProfileCollection);
			response = new SpeakerProfile();
			BeanUtil.map(speakerProfileCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getProfileImage())) {
				response.setProfileImage(getFinalImageURL(response.getProfileImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while delete Speaker Profile" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Speaker Profile" + e.getMessage());

		}
		return response;

	}

	@Override
	public SpeakerProfile getSpeakerProfile(String id) {
		SpeakerProfile response = null;
		try {
			SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository.findById(new ObjectId(id))
					.orElse(null);
			response = new SpeakerProfile();
			BeanUtil.map(speakerProfileCollection, response);

			if (!DPDoctorUtils.anyStringEmpty(response.getProfileImage())) {
				response.setProfileImage(getFinalImageURL(response.getProfileImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Speaker Profile" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Speaker Profile " + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConferenceSession addEditConferenceSession(DoctorConferenceSession request) {
		DoctorConferenceSession response = null;
		try {
			if (request.getSpeakers() != null && !request.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : request.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(committeeResponse.getProfileImage().replace(imagePath, ""));
					}
				}
			}
			if (request.getAddress() != null) {
				if ((request.getAddress().getLatitude() != null && request.getAddress().getLatitude() == 0)
						&& (request.getAddress().getLongitude() != null && request.getAddress().getLongitude() == 0)) {
					Address address = request.getAddress();
					List<GeocodedLocation> geocodedLocations = locationServices
							.geocodeLocation((!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
									? address.getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getLocality())
											? address.getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getState())
											? address.getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCountry())
											? address.getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode()
											: ""));

					if (geocodedLocations != null && !geocodedLocations.isEmpty())
						BeanUtil.map(geocodedLocations.get(0), request.getAddress());
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getTitleImage())) {
				request.setTitleImage(request.getTitleImage().replace(imagePath, ""));
			}
			DoctorConferenceSessionCollection sessionCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				sessionCollection = doctorConferenceSessionRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (sessionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Conference Session Not found with Id");
				}
				sessionCollection.setTopicIds(new ArrayList<ObjectId>());
				sessionCollection.setSpeakers(new ArrayList<OrganizingCommittee>());
				request.setUpdatedTime(new Date());
				request.setCreatedBy(sessionCollection.getCreatedBy());
				request.setCreatedTime(sessionCollection.getCreatedTime());
				BeanUtil.map(request, sessionCollection);

			} else {
				sessionCollection = new DoctorConferenceSessionCollection();
				BeanUtil.map(request, sessionCollection);
				sessionCollection.setCreatedBy("ADMIN");
				sessionCollection.setUpdatedTime(new Date());
				sessionCollection.setCreatedTime(new Date());
			}
			List<SessionTopicCollection> collections = null;
			if (request.getTopics() != null && !request.getTopics().isEmpty()) {
				collections = sessionTopicRepository.findByTopicIn(request.getTopics());
				@SuppressWarnings("unchecked")
				Collection<ObjectId> specialityIds = CollectionUtils.collect(collections,
						new BeanToPropertyValueTransformer("id"));
				if (specialityIds != null && !specialityIds.isEmpty())
					sessionCollection.setTopicIds(new ArrayList<>(specialityIds));
				else
					sessionCollection.setTopicIds(null);
			} else {
				sessionCollection.setTopicIds(null);
			}
			sessionCollection = doctorConferenceSessionRepository.save(sessionCollection);

			List<String> topics = null;
			if (sessionCollection.getTopicIds() != null && !sessionCollection.getTopicIds().isEmpty()) {
				topics = new ArrayList<String>();
				for (SessionTopicCollection collection : collections) {
					topics.add(collection.getTopic());
				}
			}
			response = new DoctorConferenceSession();
			BeanUtil.map(sessionCollection, response);
			if (response.getTopics() != null && !response.getTopics().isEmpty()) {
				response.setTopics(new ArrayList<String>());
				response.setTopics(topics);
			}

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}

		} catch (

		BusinessException e) {
			logger.error("Error while addedit Conference Session" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while addedit Conference Session " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<DoctorConferenceSession> getConferenceSessions(int size, int page, boolean discarded, String city,
			Integer fromtime, Integer toTime, String fromDate, String toDate, String searchTerm, String conferenceId,
			List<String> topics) {
		List<DoctorConferenceSession> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria = criteria.orOperator(new Criteria("address.city").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm));

			if (topics != null && !topics.isEmpty())
				criteria = criteria.and("topics").in(topics);

			if (!DPDoctorUtils.anyStringEmpty(conferenceId))
				criteria.and("conferenceId").is(new ObjectId(conferenceId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate))).lt(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("onDate").lt(new Date(Long.parseLong(toDate)));

			}

			if (fromtime > 0 && toTime > 0) {
				criteria.and("schedule.fromTime").gte(fromtime);
				criteria.and("schedule.toTime").lte(toTime);
			} else if (fromtime > 0) {
				criteria.and("schedule.fromTime").gte(fromtime);

			} else if (toTime > 0) {
				criteria.and("schedule.toTime").lte(toTime);

			}
			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("topics", new BasicDBObject("$push", "$topics.topic"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays",
												true))),
								groupFirst, Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")),
								Aggregation.skip((long) page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays",
												true))),
								groupFirst, Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, DoctorConferenceSessionCollection.class, DoctorConferenceSession.class)
					.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (DoctorConferenceSession session : response) {
					session.setTitleImage(getFinalImageURL(session.getTitleImage()));
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConferenceSession getConferenceSession(String id) {
		DoctorConferenceSession response = null;
		try {
			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("topics", new BasicDBObject("$push", "$topics.topic"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListThird = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("title", "$title"), Fields.field("titleImage", "$titleImage"),
					Fields.field("description", "$description"), Fields.field("address", "$address"),
					Fields.field("noOfQuestion", "$noOfQuestion"), Fields.field("onDate", "$onDate"),
					Fields.field("schedule", "$schedule"), Fields.field("topics", "$topics"),
					Fields.field("discarded", "$discarded"), Fields.field("conferenceId", "$conferenceId"),
					Fields.field("speakers.speakerId", "$speakers.speakerId"),
					Fields.field("speakers.firstName", "$speaker.firstName"),
					Fields.field("speakers.profileImage", "$speaker.profileImage"),
					Fields.field("speakers.role", "$speakers.role"), Fields.field("createdTime", "$createdTime"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy")));
			CustomAggregationOperation groupThird = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("topics", new BasicDBObject("$first", "$topics"))
							.append("speakers", new BasicDBObject("$push", "$speakers"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			response = mongoTemplate.aggregate(Aggregation.newAggregation(

					Aggregation.match(new Criteria("_id").is(new ObjectId(id)).and("discarded").is(false)),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays", true))),
					groupFirst,
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speakers").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("speaker_profile_cl", "$speakers.speakerId", "_id", "speaker"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speaker").append("preserveNullAndEmptyArrays", true))),
					projectListThird, groupThird),

					"doctor_conference_session_cl", DoctorConferenceSession.class).getUniqueMappedResult();

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session" + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConferenceSession deleteConferenceSession(String id, boolean discarded) {
		DoctorConferenceSession response = null;
		try {
			DoctorConferenceSessionCollection doctorConferenceSessionCollection = doctorConferenceSessionRepository
					.findById(new ObjectId(id)).orElse(null);
			if (doctorConferenceSessionCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Conference Session Not found with Id");
			}
			doctorConferenceSessionCollection.setDiscarded(discarded);
			doctorConferenceSessionCollection.setUpdatedTime(new Date());
			doctorConferenceSessionCollection = doctorConferenceSessionRepository
					.save(doctorConferenceSessionCollection);
			response = new DoctorConferenceSession();
			BeanUtil.map(doctorConferenceSessionCollection, response);

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while delete Conference Session" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while discard Conference Session" + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConference addEditDoctorConference(DoctorConference request) {
		DoctorConference response = null;
		try {
			List<SpecialityCollection> specialityCollections = null;
			if (request.getSpeakers() != null && !request.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : request.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(committeeResponse.getProfileImage().replace(imagePath, ""));
					}
				}
			}
			if (request.getCommiteeMember() != null && !request.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : request.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(committeeResponse.getProfileImage().replace(imagePath, ""));
					}
				}
			}
			if (request.getAddress() != null) {
				if ((request.getAddress().getLatitude() != null && request.getAddress().getLatitude() == 0)
						&& (request.getAddress().getLongitude() != null && request.getAddress().getLongitude() == 0)) {
					Address address = request.getAddress();
					List<GeocodedLocation> geocodedLocations = locationServices
							.geocodeLocation((!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
									? address.getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getLocality())
											? address.getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getState())
											? address.getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCountry())
											? address.getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode()
											: ""));

					if (geocodedLocations != null && !geocodedLocations.isEmpty())
						BeanUtil.map(geocodedLocations.get(0), request.getAddress());
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getTitleImage())) {
				request.setTitleImage(request.getTitleImage().replace(imagePath, ""));
			}

			if (request.getSpecialities() != null && !request.getSpecialities().isEmpty()) {
				specialityCollections = specialityRepository.findBySuperSpecialityIn(request.getSpecialities());

				if (specialityCollections != null && !specialityCollections.isEmpty()) {
					request.setSpecialities(new ArrayList<String>());
					for (SpecialityCollection specialityCollection : specialityCollections) {
						request.getSpecialities().add(specialityCollection.getId().toString());
					}
				} else
					request.setSpecialities(null);
			} else {
				request.setSpecialities(null);
			}
			DoctorConferenceCollection conferenceCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				conferenceCollection = doctorConferenceRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (conferenceCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Conference Not found with Id");
				}
				conferenceCollection.setCommiteeMember(new ArrayList<OrganizingCommittee>());
				conferenceCollection.setSpeakers(new ArrayList<OrganizingCommittee>());
				conferenceCollection.setSpecialities(new ArrayList<ObjectId>());
				request.setUpdatedTime(new Date());
				request.setCreatedBy(conferenceCollection.getCreatedBy());
				request.setCreatedTime(conferenceCollection.getCreatedTime());
				BeanUtil.map(request, conferenceCollection);
				conferenceCollection.setMealType(new ArrayList<>());
				conferenceCollection.setMealType(request.getMealType());

			} else {
				conferenceCollection = new DoctorConferenceCollection();
				BeanUtil.map(request, conferenceCollection);
				conferenceCollection.setCreatedBy("ADMIN");
				conferenceCollection.setUpdatedTime(new Date());
				conferenceCollection.setCreatedTime(new Date());
			}

			conferenceCollection = doctorConferenceRepository.save(conferenceCollection);
			List<String> specialities = null;
			if (conferenceCollection.getSpecialities() != null && !conferenceCollection.getSpecialities().isEmpty()) {
				specialities = new ArrayList<String>();
				for (SpecialityCollection specialityCollection : specialityCollections) {
					specialities.add(specialityCollection.getSuperSpeciality());
				}
			}
			response = new DoctorConference();
			BeanUtil.map(conferenceCollection, response);
			if (conferenceCollection.getSpecialities() != null && !conferenceCollection.getSpecialities().isEmpty()) {
				response.setSpecialities(new ArrayList<String>());
				response.setSpecialities(specialities);
			}

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			if (response.getCommiteeMember() != null && !response.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			List<ConfexUserCollection> usercollections = confexUserRepository
					.findByConferenceIdAndUserType(new ObjectId(response.getId()), "ADMIN");
			List<ConfexUser> users = null;
			if (usercollections != null && !usercollections.isEmpty()) {
				ConfexUser user = null;
				users = new ArrayList<ConfexUser>();
				for (ConfexUserCollection usercollection : usercollections) {
					user = new ConfexUser();
					BeanUtil.map(usercollection, user);
					user.setImageUrl(getFinalImageURL(user.getImageUrl()));
					user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
					users.add(user);
				}
				response.setAdmin(users);
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}

		} catch (

		BusinessException e) {
			logger.error("Error while addedit Conference" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Conference" + e.getMessage());

		}
		return response;
	}

	@Override
	public List<DoctorConference> getDoctorConference(int size, int page, boolean discarded, String city,
			String speciality, String fromDate, String toDate, String searchTerm) {
		List<DoctorConference> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(speciality))
				criteria = criteria.orOperator(new Criteria("specialities.speciality").regex("^" + searchTerm, "i"),
						new Criteria("specialities.superSpeciality").regex("^" + searchTerm),
						new Criteria("specialities.superSpeciality").regex("^" + searchTerm, "i"),
						new Criteria("specialities.speciality").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("fromDate").gte(new Date(Long.parseLong(fromDate))).and("toDate")
						.lt(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("fromDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("toDate").lt(new Date(Long.parseLong(toDate)));

			}
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria = criteria.orOperator(new Criteria("address.city").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("uIdPrefix", new BasicDBObject("$first", "$uIdPrefix"))
							.append("mealType", new BasicDBObject("$first", "$mealType"))
							.append("smsHeader", new BasicDBObject("$first", "$smsHeader"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("website", new BasicDBObject("$first", "$website"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), groupOperation,
								Aggregation.sort(new Sort(Direction.ASC, "fromDate")),
								Aggregation.skip((long) page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), groupOperation,
								Aggregation.sort(new Sort(Direction.ASC, "fromDate")));
			}
			response = mongoTemplate.aggregate(aggregation, DoctorConferenceCollection.class, DoctorConference.class)
					.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (DoctorConference conference : response) {
					conference.setTitleImage(getFinalImageURL(conference.getTitleImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Conference  " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConference getDoctorConference(String id) {
		DoctorConference response = null;
		try {

			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("specialities", new BasicDBObject("$push", "$specialities.superSpeciality"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("uIdPrefix", new BasicDBObject("$first", "$uIdPrefix"))
							.append("mealType", new BasicDBObject("$first", "$mealType"))
							.append("smsHeader", new BasicDBObject("$first", "$smsHeader"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("website", new BasicDBObject("$first", "$website"))
							.append("allowRegistration", new BasicDBObject("$first", "$allowRegistration"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListsecond = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("title", "$title"), Fields.field("titleImage", "$titleImage"),
					Fields.field("description", "$description"), Fields.field("fromDate", "$fromDate"),
					Fields.field("toDate", "$toDate"), Fields.field("address", "$address"),
					Fields.field("discarded", "$discarded"), Fields.field("specialities", "$specialities"),
					Fields.field("speakers.speakerId", "$speakers.speakerId"),
					Fields.field("speakers.firstName", "$speaker.firstName"),
					Fields.field("speakers.role", "$speakers.role"), Fields.field("mealType", "$mealType"),
					Fields.field("smsHeader", "$smsHeader"),
					Fields.field("speakers.profileImage", "$speaker.profileImage"), Fields.field("status", "$status"),
					Fields.field("uIdPrefix", "$uIdPrefix"), Fields.field("website", "$website"),
					Fields.field("allowRegistration", "$allowRegistration"),
					Fields.field("commiteeMember", "$commiteeMember"), Fields.field("createdTime", "$createdTime"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation groupsecond = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("speakers", new BasicDBObject("$push", "$speakers"))
							.append("specialities", new BasicDBObject("$first", "$specialities"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("uIdPrefix", new BasicDBObject("$first", "$uIdPrefix"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("mealType", new BasicDBObject("$first", "$mealType"))
							.append("smsHeader", new BasicDBObject("$first", "$smsHeader"))
							.append("website", new BasicDBObject("$first", "$website"))
							.append("allowRegistration", new BasicDBObject("$first", "$allowRegistration"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),

					Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
					groupFirst,
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speakers").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("speaker_profile_cl", "$speakers.speakerId", "_id", "speaker"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speaker").append("preserveNullAndEmptyArrays", true))),
					projectListsecond, groupsecond);
			response = mongoTemplate.aggregate(aggregation, "doctor_conference_cl", DoctorConference.class)
					.getUniqueMappedResult();

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			if (response.getCommiteeMember() != null && !response.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getCommiteeMember()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getSpeakerId())) {
						SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository
								.findById(new ObjectId(committeeResponse.getSpeakerId())).orElse(null);
						committeeResponse.setFirstName(speakerProfileCollection.getFirstName());
						if (!DPDoctorUtils.anyStringEmpty(speakerProfileCollection.getProfileImage())) {

							committeeResponse
									.setProfileImage(getFinalImageURL(speakerProfileCollection.getProfileImage()));
						}
					}
				}
			}

			List<ConfexUserCollection> usercollections = confexUserRepository
					.findByConferenceIdAndUserType(new ObjectId(id), "ADMIN");
			List<ConfexUser> users = null;
			if (usercollections != null && !usercollections.isEmpty()) {
				ConfexUser user = null;
				users = new ArrayList<ConfexUser>();
				for (ConfexUserCollection usercollection : usercollections) {
					user = new ConfexUser();
					BeanUtil.map(usercollection, user);
					user.setImageUrl(getFinalImageURL(user.getImageUrl()));
					user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
					users.add(user);
				}
				response.setAdmin(users);
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Conference   " + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConference deleteDoctorConference(String id, boolean discarded) {
		DoctorConference response = null;
		try {
			DoctorConferenceCollection doctorConferenceCollection = doctorConferenceRepository
					.findById(new ObjectId(id)).orElse(null);
			if (doctorConferenceCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Conference  Not found with Id");
			}
			doctorConferenceCollection.setDiscarded(discarded);
			doctorConferenceCollection.setUpdatedTime(new Date());
			doctorConferenceCollection = doctorConferenceRepository.save(doctorConferenceCollection);
			response = new DoctorConference();
			BeanUtil.map(doctorConferenceCollection, response);
			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			if (response.getCommiteeMember() != null && !response.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (

		BusinessException e) {
			logger.error("Error while delete Conference " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Conference " + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConferenceAgenda addEditConferenceAgenda(DoctorConferenceAgenda request) {
		DoctorConferenceAgenda response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getTitleImage())) {
				request.setTitleImage(request.getTitleImage().replace(imagePath, ""));
			}
			DoctorConferenceAgendaCollection conferenceAgendaCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				conferenceAgendaCollection = doctorConferenceAgendaRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (conferenceAgendaCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Conference Agenda Not found with Id");
				}

				BeanUtil.map(request, conferenceAgendaCollection);

			} else {
				conferenceAgendaCollection = new DoctorConferenceAgendaCollection();
				BeanUtil.map(request, conferenceAgendaCollection);

			}
			conferenceAgendaCollection = doctorConferenceAgendaRepository.save(conferenceAgendaCollection);
			response = new DoctorConferenceAgenda();
			BeanUtil.map(conferenceAgendaCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while addedit Conference Agenda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Conference Agenda" + e.getMessage());

		}
		return response;
	}

	@Override
	public List<DoctorConferenceAgenda> getConferenceAgenda(int size, int page, boolean discarded, String fromDate,
			String toDate, String searchTerm, String conferenceId) {
		List<DoctorConferenceAgenda> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(conferenceId))
				criteria.and("conferenceId").is(new ObjectId(conferenceId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate))).lte(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("onDate").lte(new Date(Long.parseLong(toDate)));

			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")),
						Aggregation.sort(new Sort(Direction.ASC, "onDate")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")),
						Aggregation.sort(new Sort(Direction.ASC, "onDate")));
			}
			response = mongoTemplate
					.aggregate(aggregation, DoctorConferenceAgendaCollection.class, DoctorConferenceAgenda.class)
					.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (DoctorConferenceAgenda agenda : response) {
					agenda.setTitleImage(getFinalImageURL(agenda.getTitleImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Agenda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Agenda " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConferenceAgenda deleteConferenceAgenda(String id, boolean discarded) {
		DoctorConferenceAgenda response = null;
		try {
			DoctorConferenceAgendaCollection doctorConferenceAgendaCollection = doctorConferenceAgendaRepository
					.findById(new ObjectId(id)).orElse(null);
			if (doctorConferenceAgendaCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Conference Agenda Not found with Id");
			}
			doctorConferenceAgendaCollection.setDiscarded(discarded);
			doctorConferenceAgendaCollection = doctorConferenceAgendaRepository.save(doctorConferenceAgendaCollection);
			response = new DoctorConferenceAgenda();
			BeanUtil.map(doctorConferenceAgendaCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while delete Conference Agenda" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Conference Agenda" + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConferenceAgenda getConferenceAgenda(String id) {
		DoctorConferenceAgenda response = null;
		try {
			DoctorConferenceAgendaCollection conferenceAgendaCollection = doctorConferenceAgendaRepository
					.findById(new ObjectId(id)).orElse(null);
			response = new DoctorConferenceAgenda();
			BeanUtil.map(conferenceAgendaCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Agenda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Agenda  " + e.getMessage());

		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	public List<SessionDateResponse> getConferenceSessionDate(String conferenceId) {
		List<SessionDateResponse> response = null;
		try {
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("onDate", new BasicDBObject("$first", "$onDate"))
									.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))));

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("onDate", "$onDate"),
					Fields.field("schedule", "$schedule"), Fields.field("conferenceId", "$conferenceId")));

			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("conferenceId").is(new ObjectId(conferenceId))
									.and("discarded").is(false)),
							projectList.and("onDate").extractDayOfMonth().as("day").and("onDate").extractMonth()
									.as("month").and("onDate").extractYear().as("year").and("onDate").extractWeek()
									.as("week"),
							group, Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime", "onDate"))),
					DoctorConferenceSessionCollection.class, SessionDateResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session" + e.getMessage());

		}
		return response;

	}

	@Override
	public DoctorConference addConferenceAdmin(ConfexAdminRequest request) {
		DoctorConference response = null;
		try {
			ConfexUserCollection admin = null;
			DoctorConferenceCollection doctorConferenceCollection = null;
			if (request != null) {
				if (!DPDoctorUtils.anyStringEmpty(request.getConferenceId())) {
					doctorConferenceCollection = doctorConferenceRepository
							.findById(new ObjectId(request.getConferenceId())).orElse(null);
					if (doctorConferenceCollection == null) {
						throw new BusinessException(ServiceError.NotFound, "Conference  Not found with Id");
					}
				}

				request.setImageUrl(!DPDoctorUtils.anyStringEmpty(request.getImageUrl())
						? request.getImageUrl().replace(imagePath, "")
						: null);
				request.setThumbnailUrl(!DPDoctorUtils.anyStringEmpty(request.getThumbnailUrl())
						? request.getThumbnailUrl().replace(imagePath, "")
						: null);
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					admin = new ConfexUserCollection();
					BeanUtil.map(request, admin);
					admin.setCreatedTime(new Date());
					admin.setUpdatedTime(new Date());
					admin.setIsVerified(true);
					admin.setCreatedBy("ADMIN");
					admin.setUserType(ConfexUserState.ADMIN);
					admin.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
					admin.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
					SimpleDateFormat df = new SimpleDateFormat("yyyy");
					String year = df.format(new Date());
					long count = mongoTemplate.count(
							new Query(new Criteria("userName")
									.regex(request.getFirstName().toLowerCase().replace(" ", "."))),
							ConfexUserCollection.class);
					if (count > 0)
						admin.setUserName(request.getFirstName().toLowerCase().replace(" ", ".") + "0" + count
								+ "@confex-" + year);
					else
						admin.setUserName(request.getFirstName().toLowerCase().replace(" ", ".") + "@confex-" + year);
				} else {
					admin = confexUserRepository.findById(new ObjectId(request.getId())).orElse(null);
					admin.setFirstName(request.getFirstName());
					admin.setEmailAddress(request.getEmailAddress());
					admin.setImageUrl(request.getImageUrl());
					admin.setThumbnailUrl(request.getThumbnailUrl());
					admin.setTitle(request.getTitle());
					admin.setConferenceId(new ObjectId(request.getConferenceId()));
					admin.setMobileNumber(request.getMobileNumber());
					admin.setUpdatedTime(new Date());
				}
				admin = confexUserRepository.save(admin);
				response = new DoctorConference();
				BeanUtil.map(doctorConferenceCollection, response);
				List<ConfexUserCollection> usercollections = confexUserRepository
						.findByConferenceIdAndUserType(admin.getConferenceId(), "ADMIN");
				List<ConfexUser> users = null;
				if (usercollections != null && !usercollections.isEmpty()) {
					ConfexUser user = null;
					users = new ArrayList<ConfexUser>();
					for (ConfexUserCollection usercollection : usercollections) {
						user = new ConfexUser();
						BeanUtil.map(usercollection, user);
						user.setImageUrl(getFinalImageURL(user.getImageUrl()));
						user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
						users.add(user);
					}
					response.setAdmin(users);
				}
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {

					sendEmailSetPassword(admin.getId().toString());
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while signup Conference admin " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting signup Conference admin" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean activeConferenceAdmin(String userId) {
		Boolean response = false;
		try {

			ConfexUserCollection userCollection = confexUserRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				if (!userCollection.getSetPassword()) {
					throw new BusinessException(ServiceError.InvalidInput, "Confex Admin hasn't set password");
				}

				userCollection.setIsActive(!userCollection.getIsActive());
				userCollection = confexUserRepository.save(userCollection);
				if (userCollection.getIsActive()) {
					String body = mailBodyGenerator.conferenceEmailBody(userCollection.getFirstName(), welcomeLink,
							"confexAccountActivate.vm");
					mailService.sendEmail(userCollection.getEmailAddress(), "Welcome to Healthcoco Confex.", body,
							null);
				}
				response = true;
			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Activating confex Admin");
			throw new BusinessException(ServiceError.Unknown, "Error While Activating confex Admin");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean resendVerificationSMS(String userId, String mobileNumber) {
		Boolean response = false;
		try {

			ConfexUserCollection userCollection = confexUserRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(mobileNumber.trim())) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);
					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(
							null, null, null, null, null, "Hi, please verify your account " + pharmacyVerificationLink
									+ tokenCollection.getId() + "\n Thanks you",
							userCollection.getMobileNumber(), "VerifyConference");
					sMSServices.sendSMS(smsTrackDetail, false);
				} else {
					logger.warn("Mobile Number is empty.");
					throw new BusinessException(ServiceError.InvalidInput, "Mobile Number is empty.");
				}

				response = true;
			}

			else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While sending verification SMS");
			throw new BusinessException(ServiceError.Unknown, "Error While sending verification SMS");
		}
		return response;
	}

	@Override
	public Boolean sendEmailSetPassword(String userId) {
		Boolean status = false;
		try {
			ConfexUserCollection userCollection = confexUserRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "user not found");
			}
			if (!userCollection.getIsVerified()) {
				throw new BusinessException(ServiceError.InvalidInput, "User is not verified ");
			}

			if (userCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);
					System.out.println(setPasswordLink + tokenCollection.getId() + "/setPassword");
					String body = mailBodyGenerator.conferenceEmailBody(userCollection.getFirstName(),
							setPasswordLink + tokenCollection.getId() + "/setPassword", "confexSetPassword.vm");
					mailService.sendEmail(userCollection.getEmailAddress(), "Password setup for Confex account.", body,
							null);

					status = true;
				}
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	@Override
	@Transactional
	public Boolean resetPassword(ResetPasswordRequest request) {
		Boolean response = false;
		try {
			ConfexUserCollection userCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getUserId())) {
				userCollection = confexUserRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			} else {
				userCollection = confexUserRepository.findByUserName(request.getUserName());
			}
			if (userCollection != null) {
				char[] salt = DPDoctorUtils.generateSalt();

				if (salt != null && salt.length > 0) {
					char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
					for (int i = 0; i < request.getPassword().length; i++) {
						passwordWithSalt[i] = request.getPassword()[i];
					}
					for (int i = 0; i < salt.length; i++) {
						passwordWithSalt[i + request.getPassword().length] = salt[i];
					}
					passwordWithSalt = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);
					userCollection.setPassword(passwordWithSalt);
					userCollection.setSalt(salt);
					userCollection.setSetPassword(true);
				}
				confexUserRepository.save(userCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "conference user not found");
			}

			response = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean verifyAdmin(String userId) {
		Boolean response = false;
		try {
			ConfexUserCollection userCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				userCollection = confexUserRepository.findById(new ObjectId(userId)).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "conference user not found");
				}
				userCollection.setIsVerified(!userCollection.getIsVerified());
				confexUserRepository.save(userCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean resetPasswordSMS(String userId, String mobileNumber) {
		Boolean response = false;
		try {

			ConfexUserCollection userCollection = confexUserRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				if (!userCollection.getIsVerified()) {
					throw new BusinessException(ServiceError.InvalidInput, "User is not verified ");
				}
				if (!DPDoctorUtils.anyStringEmpty(mobileNumber)) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);
					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(
							null, null, null, null, null, "Hi, please set your password  " + pharmacyVerificationLink
									+ tokenCollection.getId() + "\n Thanks you",
							userCollection.getMobileNumber(), "resetPassword");
					sMSServices.sendSMS(smsTrackDetail, false);
				} else {
					logger.warn("Mobile Number is empty.");
					throw new BusinessException(ServiceError.InvalidInput, "Mobile Number is empty.");
				}

				response = true;
			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While sending verification SMS");
			throw new BusinessException(ServiceError.Unknown, "Error While sending verification SMS");
		}
		return response;
	}

	@Override
	public DoctorConference updateStatus(String id, String status) {
		DoctorConference response = null;
		try {
			DoctorConferenceCollection conferenceCollection = doctorConferenceRepository.findById(new ObjectId(id))
					.orElse(null);
			if (conferenceCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Conference Not found with Id");
			} else {
				conferenceCollection.setStatus(status);
				conferenceCollection = doctorConferenceRepository.save(conferenceCollection);
			}

			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("specialities", new BasicDBObject("$push", "$specialities.superSpeciality"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("mealType", new BasicDBObject("$first", "$mealType"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListsecond = new ProjectionOperation(
					Fields.from(Fields.field("id", "$id"), Fields.field("title", "$title"),
							Fields.field("titleImage", "$titleImage"), Fields.field("description", "$description"),
							Fields.field("fromDate", "$fromDate"), Fields.field("mealType", "$mealType"),
							Fields.field("toDate", "$toDate"), Fields.field("address", "$address"),
							Fields.field("discarded", "$discarded"), Fields.field("specialities", "$specialities"),
							Fields.field("speakers.speakerId", "$speakers.speakerId"),
							Fields.field("speakers.firstName", "$speaker.firstName"),
							Fields.field("speakers.role", "$speakers.role"),
							Fields.field("speakers.profileImage", "$speaker.profileImage"),
							Fields.field("commiteeMember", "$commiteeMember"), Fields.field("status", "$status"),
							Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation groupsecond = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("speakers", new BasicDBObject("$push", "$speakers"))
							.append("specialities", new BasicDBObject("$first", "$specialities"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("mealType", new BasicDBObject("$first", "$mealType"))

							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),

					Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
					groupFirst,
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speakers").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("speaker_profile_cl", "$speakers.speakerId", "_id", "speaker"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speaker").append("preserveNullAndEmptyArrays", true))),
					projectListsecond, groupsecond);
			response = mongoTemplate.aggregate(aggregation, "doctor_conference_cl", DoctorConference.class)
					.getUniqueMappedResult();

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			if (response.getCommiteeMember() != null && !response.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getCommiteeMember()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getSpeakerId())) {
						SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository
								.findById(new ObjectId(committeeResponse.getSpeakerId())).orElse(null);
						committeeResponse.setFirstName(speakerProfileCollection.getFirstName());
						if (!DPDoctorUtils.anyStringEmpty(speakerProfileCollection.getProfileImage())) {

							committeeResponse
									.setProfileImage(getFinalImageURL(speakerProfileCollection.getProfileImage()));
						}
					}
				}
			}

			List<ConfexUserCollection> usercollections = confexUserRepository
					.findByConferenceIdAndUserType(new ObjectId(id), "ADMIN");
			List<ConfexUser> users = null;
			if (usercollections != null && !usercollections.isEmpty()) {
				ConfexUser user = null;
				users = new ArrayList<ConfexUser>();
				for (ConfexUserCollection usercollection : usercollections) {
					user = new ConfexUser();
					BeanUtil.map(usercollection, user);
					user.setImageUrl(getFinalImageURL(user.getImageUrl()));
					user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
					users.add(user);
				}
				response.setAdmin(users);
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Conference   " + e.getMessage());

		}
		return response;

	}

	@Override
	public ResearchPaperSubCategory addSubCategory(ResearchPaperSubCategory request) {
		ResearchPaperSubCategory response = null;
		try {
			DoctorConferenceCollection conferenceCollection = doctorConferenceRepository
					.findById(new ObjectId(request.getConferenceId())).orElse(null);
			if (conferenceCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Doctor Conference not found with conferenceId");
			}

			ResearchPaperSubCategoryCollection categoryCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				categoryCollection = researchPaperSubCategoryRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (categoryCollection == null) {

					throw new BusinessException(ServiceError.NotFound, "SubCatogory not found with Id");

				}
				categoryCollection.setSubCategory(request.getSubCategory());
				categoryCollection.setUpdatedTime(new Date());
			}
			if (categoryCollection == null) {
				categoryCollection = new ResearchPaperSubCategoryCollection();
				BeanUtil.map(request, categoryCollection);
				categoryCollection.setCreatedTime(new Date());
				categoryCollection.setCreatedBy("ADMIN");
			}
			categoryCollection = researchPaperSubCategoryRepository.save(categoryCollection);
			response = new ResearchPaperSubCategory();
			BeanUtil.map(categoryCollection, response);

		} catch (Exception e) {
			logger.error("Error while adding SubCategory" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding SubCategory" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<ResearchPaperSubCategory> getSubCategoryList(int page, int size, String conferenceId, String searchTerm,
			Boolean discarded) {
		List<ResearchPaperSubCategory> response = null;
		try {
			Criteria criteria = new Criteria("conferenceId").is(new ObjectId(conferenceId));

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("subCategory").regex("^" + searchTerm, "i"),
						new Criteria("subCategory").regex("^" + searchTerm));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "subCategory")));
			}
			response = mongoTemplate
					.aggregate(aggregation, ResearchPaperSubCategoryCollection.class, ResearchPaperSubCategory.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting SubCategory" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting SubCategory" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResearchPaperSubCategory getSubCategory(String id) {
		ResearchPaperSubCategory response;
		try {
			ResearchPaperSubCategoryCollection categoryCollection = researchPaperSubCategoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (categoryCollection == null) {

				throw new BusinessException(ServiceError.NotFound, "SubCatogory not found with Id");

			}
			response = new ResearchPaperSubCategory();
			BeanUtil.map(categoryCollection, response);
		} catch (Exception e) {
			logger.error("Error while getting Sub Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Sub Category" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardSubCategory(String id, Boolean discarded) {
		Boolean response = false;
		try {
			ResearchPaperSubCategoryCollection categoryCollection = researchPaperSubCategoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (categoryCollection == null) {

				throw new BusinessException(ServiceError.NotFound, "Sub Catogory not found with Id");

			}
			categoryCollection.setDiscarded(discarded);
			researchPaperSubCategoryRepository.save(categoryCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while getting SubCategory" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting SubCategory" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResearchPaperCategory addCategory(ResearchPaperCategory request) {
		ResearchPaperCategory response = null;
		try {
			DoctorConferenceCollection conferenceCollection = doctorConferenceRepository
					.findById(new ObjectId(request.getConferenceId())).orElse(null);
			if (conferenceCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Doctor Conference not found with conferenceId");
			}

			ResearchPaperCategoryCollection categoryCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				categoryCollection = researchPaperCategoryRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (categoryCollection == null) {

					throw new BusinessException(ServiceError.NotFound, "Catogory not found with Id");

				}
				categoryCollection.setCategory(request.getCategory());
				categoryCollection.setUpdatedTime(new Date());
			}
			if (categoryCollection == null) {
				categoryCollection = new ResearchPaperCategoryCollection();
				BeanUtil.map(request, categoryCollection);
				categoryCollection.setCreatedTime(new Date());
				categoryCollection.setCreatedBy("ADMIN");
			}
			categoryCollection = researchPaperCategoryRepository.save(categoryCollection);
			response = new ResearchPaperCategory();
			BeanUtil.map(categoryCollection, response);

		} catch (Exception e) {
			logger.error("Error while adding Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding Category" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<ResearchPaperCategory> getCategoryList(int page, int size, String conferenceId, String searchTerm,
			Boolean discarded) {
		List<ResearchPaperCategory> response = null;
		try {
			Criteria criteria = new Criteria("conferenceId").is(new ObjectId(conferenceId));

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("category").regex("^" + searchTerm, "i"),
						new Criteria("category").regex("^" + searchTerm));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "category")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "category")));
			}
			response = mongoTemplate
					.aggregate(aggregation, ResearchPaperCategoryCollection.class, ResearchPaperCategory.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Category" + e.getMessage());
		}
		return response;
	}

	@Override
	public ResearchPaperCategory getCategory(String id) {
		ResearchPaperCategory response;
		try {
			ResearchPaperCategoryCollection categoryCollection = researchPaperCategoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (categoryCollection == null) {

				throw new BusinessException(ServiceError.NotFound, "Catogory not found with Id");

			}
			response = new ResearchPaperCategory();
			BeanUtil.map(categoryCollection, response);
		} catch (Exception e) {
			logger.error("Error while getting Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Category" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardCategory(String id, Boolean discarded) {
		Boolean response = false;
		try {
			ResearchPaperCategoryCollection categoryCollection = researchPaperCategoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (categoryCollection == null) {

				throw new BusinessException(ServiceError.NotFound, "Catogory not found with Id");

			}
			categoryCollection.setDiscarded(discarded);
			researchPaperCategoryRepository.save(categoryCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while getting Category" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Category" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<RegisteredUser> getRegisterUser(int size, int page, String fromDate, String toDate,
			Boolean paymentStatus, String conferenceId, String searchTerm) {
		List<RegisteredUser> response = null;
		try {
			Date from = null, to = null;
			Criteria criteria = new Criteria("conferenceId").is(new ObjectId(conferenceId)).and("userType").is("USER");
			Criteria criteriaSecond = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0);
				to = new Date();
			}
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("userName", new BasicDBObject("$first", "$userName"))
							.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("imageUrl", new BasicDBObject("$first", "$imageUrl"))
							.append("thumbnailUrl", new BasicDBObject("$first", "$thumbnailUrl"))
							.append("isActive", new BasicDBObject("$push", "$isActive"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("city", new BasicDBObject("$first", "$address.city"))
							.append("state", new BasicDBObject("$first", "$address.state"))
							.append("isVerified", new BasicDBObject("$first", "$isVerified"))
							.append("userUId", new BasicDBObject("$first", "$userUId"))
							.append("dob", new BasicDBObject("$first", "$dob"))
							.append("colorCode", new BasicDBObject("$first", "$colorCode"))
							.append("userUId", new BasicDBObject("$first", "$userUId"))
							.append("registrationType", new BasicDBObject("$first", "$registrationType"))
							.append("organization", new BasicDBObject("$first", "$organization"))
							.append("designation", new BasicDBObject("$first", "$designation"))
							.append("regNo", new BasicDBObject("$first", "$regNo"))
							.append("isoiNo", new BasicDBObject("$first", "$isoiNo"))
							.append("specialities", new BasicDBObject("$push", "$specialities.superSpeciality"))
							.append("specialityIds", new BasicDBObject("$push", "$specialities._id"))
							.append("isCancellationRequest", new BasicDBObject("$first", "$isCancellationRequest"))
							.append("paymentStatus", new BasicDBObject("$first", "$paymentStatus"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			criteria.and("createdTime").gte(from).lte(to);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteriaSecond = criteriaSecond.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm, "i"),
						new Criteria("state").regex("^" + searchTerm, "i"),
						new Criteria("designation").regex("^" + searchTerm, "i"),
						new Criteria("regNo").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}

			if (paymentStatus != null) {
				criteria.and("paymentStatus").is(paymentStatus);
			}
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialityIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("speciality_cl", "specialityIds", "_id", "specialities"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
						group, Aggregation.match(criteriaSecond),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialityIds").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("speciality_cl", "specialityIds", "_id", "specialities"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
						group, Aggregation.match(criteriaSecond),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, ConfexUserCollection.class, RegisteredUser.class)
					.getMappedResults();

			for (RegisteredUser user : response) {
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference user " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting  Conference user" + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countRegisterUser(String fromDate, String toDate, Boolean paymentStatus, String conferenceId,
			String searchTerm) {
		Integer response = null;
		try {
			Date from = null, to = null;
			Criteria criteria = new Criteria("conferenceId").is(new ObjectId(conferenceId)).and("userType").is("USER");
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0);
				to = new Date();
			}

			criteria.and("createdTime").gte(from).lte(to);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("city").regex("^" + searchTerm, "i"),
						new Criteria("state").regex("^" + searchTerm, "i"),
						new Criteria("designation").regex("^" + searchTerm, "i"),
						new Criteria("regNo").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}

			if (paymentStatus != null) {
				criteria.and("paymentStatus").is(paymentStatus);
			}
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

			response = mongoTemplate.aggregate(aggregation, ConfexUserCollection.class, RegisteredUser.class)
					.getMappedResults().size();

		} catch (BusinessException e) {
			logger.error("Error while getting Conference user " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting  Conference user" + e.getMessage());
		}
		return response;
	}

//	@Override
//	public Boolean sendBulkSMS(ConferenceBulkSMSRequest request) {
//		Boolean response = false;
//		try {
//			Criteria criteria = new Criteria("conferenceId").is(new ObjectId(request.getConferenceId()));
//			Criteria criteria2 = new Criteria();
//			if (request.getRegisterUserIds() != null && !request.getRegisterUserIds().isEmpty()) {
//				List<ObjectId> listIds = request.getRegisterUserIds().parallelStream().map(p -> new ObjectId(p))
//						.collect(Collectors.toList());
//				criteria2.and("confxUser.id").in(listIds);
//			}
//
//			if (request.getPaymentStatus() != null) {
//				criteria2.and("confxUser.paymentStatus").is(request.getPaymentStatus());
//			}
//			if (!DPDoctorUtils.anyStringEmpty(request.getState().getState())) {
//				criteria2.and("confxUser.userType").is(request.getState().getState());
//			}
//			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//					new BasicDBObject("_id", "$_id")
//							.append("smsHeader", new BasicDBObject("$first", "$confxUser.smsHeader"))
//							.append("mobileNumbers", new BasicDBObject("$push", "$confxUser.mobileNumber"))));
//			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//					Aggregation.lookup("confex_user_cl", "conferenceId", "_id", "confxUser"),
//					Aggregation.unwind("confxUser"), Aggregation.match(criteria2), group);
//
//			ConferenceBulkSMS smsResponse = mongoTemplate
//					.aggregate(aggregation, DoctorConferenceCollection.class, ConferenceBulkSMS.class)
//					.getUniqueMappedResult();
//			if (smsResponse != null) {
//				sMSServices.sendBulkSMSResponse(smsResponse.getMobileNumbers(), request.getMessage(),
//						smsResponse.getSmsHeader());
//				response = true;
//
//			}
//
//		} catch (BusinessException e) {
//			logger.error("Error while sending Bulk sms to Conference user " + e.getMessage());
//			e.printStackTrace();
//			throw new BusinessException(ServiceError.Unknown,
//					"Error while sending Bulk sms to Conference user " + e.getMessage());
//		}
//		return response;
//	}
	
	@Override
	public Boolean sendBulkSMS(ConferenceBulkSMSRequest request) {
        Boolean response = false;
        try {
            Criteria criteria = new Criteria("conferenceId").is(new ObjectId(request.getConferenceId()));
            
            if (request.getRegisterUserIds() != null && !request.getRegisterUserIds().isEmpty()) {
                List<ObjectId> listIds = new ArrayList<ObjectId>();
                for (String id : request.getRegisterUserIds()) {
                    listIds.add(new ObjectId(id));
                }
                if (listIds != null && !listIds.isEmpty()) {
                    criteria.and("_id").in(listIds);
                }
            }

            if (request.getPaymentStatus() != null) {
                criteria.and("paymentStatus").is(request.getPaymentStatus());
            }
            if (request.getState() != null) {
                criteria.and("userType").is(request.getState());
            }
            
            
            
            
            CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
                    new BasicDBObject("_id", "$conferenceId")
                            .append("smsHeader", new BasicDBObject("$first", "$conference.smsHeader"))
                            .append("mobileNumbers", new BasicDBObject("$addToSet", "$mobileNumber"))));

            
       	CustomAggregationOperation projectOperation = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("smsHeader", "$conference.smsHeader")
					.append("mobileNumbers", "$mobileNumbers")));
        	
        	
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                    Aggregation.lookup("doctor_conference_cl","conferenceId", "_id",  "conference"),
                    Aggregation.unwind("conference"), group);

            List<ConferenceBulkSMS> smsResponse = mongoTemplate
                    .aggregate(aggregation, ConfexUserCollection.class, ConferenceBulkSMS.class)
                    .getMappedResults();
            if (smsResponse != null && !smsResponse.isEmpty()) {
                sMSServices.sendBulkSMSResponse(smsResponse.get(0).getMobileNumbers(), request.getMessage(),
                        smsResponse.get(0).getSmsHeader());
                response = true;

            }

        } catch (BusinessException e) {
            logger.error("Error while sending Bulk sms to Conference user " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(ServiceError.Unknown,
                    "Error while sending Bulk sms to Conference user " + e.getMessage());
        }
        return response;
    }

}
