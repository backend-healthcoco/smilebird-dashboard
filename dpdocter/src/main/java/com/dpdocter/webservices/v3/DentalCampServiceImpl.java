package com.dpdocter.webservices.v3;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.CampUser;
import com.dpdocter.beans.CampaignObjectCollection;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.CampNameCollection;
import com.dpdocter.collections.CampaignNameCollection;
import com.dpdocter.collections.DentalCampCollection;
import com.dpdocter.collections.DentalCampTreatmentNameCollection;
import com.dpdocter.collections.DentalChainCityCollection;
import com.dpdocter.collections.DentalReasonsCollection;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.FollowupCommunicationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.BroadcastType;
import com.dpdocter.enums.DateFilterType;
import com.dpdocter.enums.FollowupType;
import com.dpdocter.enums.LeadStage;
import com.dpdocter.enums.LeadType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CampMsgTemplateRepository;
import com.dpdocter.repository.CampNameRepository;
import com.dpdocter.repository.CampaignNameRepository;
import com.dpdocter.repository.CampaignObjectRepository;
import com.dpdocter.repository.DentalCampRepository;
import com.dpdocter.repository.DentalCampTreatmentNameRepository;
import com.dpdocter.repository.FollowupCommunicationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
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
import com.dpdocter.response.InteraktResponse;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class DentalCampServiceImpl implements DentalCampService {

	private static Logger logger = LogManager.getLogger(DentalCampServiceImpl.class.getName());

	@Autowired
	private CampNameRepository campNameRepository;

	@Autowired
	private CampaignNameRepository campaignNameRepository;

	@Autowired
	private CampaignObjectRepository campaignObjectRepository;

	@Autowired
	private DentalCampRepository dentalCampRepository;

	@Autowired
	private CampMsgTemplateRepository msgTemplateRepository;

	@Autowired
	private FollowupCommunicationRepository followupCommunicationRepository;

	@Autowired
	private DentalCampTreatmentNameRepository dentalCampTreatmentNameRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${smilebird.support.number}")
	private String smilebirdSupportNumber;

	@Value(value = "${interakt.secret.key}")
	private String secretKey;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Override
	public CampNameResponse addEditCampName(CampNameRequest request) {
		CampNameResponse response = null;
		CampNameCollection campNameCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				campNameCollection = campNameRepository.findById(new ObjectId(request.getId())).orElse(null);
				campNameCollection.setUpdatedTime(new Date());
				campNameCollection.setAssociateDoctorIds(null);
			} else {
				campNameCollection = new CampNameCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, campNameCollection);
			if (request.getAssociateDoctorIds() == null) {
				campNameCollection.setAssociateDoctorIds(null);
			} else {
				List<ObjectId> reasonIds = new ArrayList<ObjectId>();
				for (String reasonId : request.getAssociateDoctorIds()) {
					reasonIds.add(new ObjectId(reasonId));
				}
				campNameCollection.setAssociateDoctorIds(reasonIds);
			}
			campNameCollection = campNameRepository.save(campNameCollection);
			response = new CampNameResponse();
			BeanUtil.map(campNameCollection, response);
			if (campNameCollection.getAssociateDoctorIds() != null) {
				List<UserCollection> reasonsCollections = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("id").in(campNameCollection.getAssociateDoctorIds()))),
						UserCollection.class, UserCollection.class).getMappedResults();
				List<String> doctorsName = new ArrayList<String>();
				for (UserCollection reasonsCollection : reasonsCollections) {
					doctorsName.add(reasonsCollection.getFirstName());
				}
				response.setAssociateDoctors(doctorsName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getCampNames(int size, int page, String searchTerm, Boolean isDiscarded, Boolean isCamp) {
		List<CampNameResponse> responseList = new ArrayList<CampNameResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (isCamp != null)
				criteria.and("isCamp").is(isCamp);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("campName").regex("^" + searchTerm, "i"),
						new Criteria("campName").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					CampNameCollection.class, CampNameResponse.class).getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						CampNameCollection.class, CampNameResponse.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampNameCollection.class, CampNameResponse.class).getMappedResults();

			}

			response.setDataList(responseList);
			for (CampNameResponse campNameResponse : responseList) {
				if (campNameResponse.getAssociateDoctorIds() != null) {
					List<UserCollection> reasonsCollections = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("id").in(campNameResponse.getAssociateDoctorIds()))),
							UserCollection.class, UserCollection.class).getMappedResults();
					List<String> doctorsName = new ArrayList<String>();
					for (UserCollection reasonsCollection : reasonsCollections) {
						doctorsName.add(reasonsCollection.getFirstName());
					}
					campNameResponse.setAssociateDoctors(doctorsName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteCampName(String campNameId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CampNameCollection campNameCollection = campNameRepository.findById(new ObjectId(campNameId)).orElse(null);
			if (campNameCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			campNameCollection.setUpdatedTime(new Date());
			campNameCollection.setIsDiscarded(isDiscarded);
			campNameCollection = campNameRepository.save(campNameCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public CampNameResponse getCampNameById(String campNameId) {
		CampNameResponse response = new CampNameResponse();
		try {
			CampNameCollection campNameCollection = campNameRepository.findById(new ObjectId(campNameId)).orElse(null);
			if (campNameCollection != null) {
				BeanUtil.map(campNameCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DentalCampResponse addEditDentalCamp(DentalCampRequest request) {
		DentalCampResponse response = null;
		DentalCampCollection dentalCampCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				dentalCampCollection = dentalCampRepository.findById(new ObjectId(request.getId())).orElse(null);
				dentalCampCollection.setUpdatedTime(new Date());
				dentalCampCollection.setImageUrls(null);
				dentalCampCollection.setTreatmentId(null);
				dentalCampCollection.setAddiction(null);
				dentalCampCollection.setDentalImages(null);
				request.setCreatedTime(dentalCampCollection.getCreatedTime());
			} else {
				dentalCampCollection = new DentalCampCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());

				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							sendAppointmentwhastSmsNotification(request);
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			BeanUtil.map(request, dentalCampCollection);

			if (request.getAssociateDoctorIds() == null) {
				dentalCampCollection.setAssociateDoctorIds(null);
			} else {
				List<ObjectId> treatmentIds = new ArrayList<ObjectId>();
				for (String treatmentId : request.getAssociateDoctorIds()) {
					treatmentIds.add(new ObjectId(treatmentId));
				}
				dentalCampCollection.setAssociateDoctorIds(treatmentIds);
			}

			if (request.getTreatmentId() == null) {
				dentalCampCollection.setTreatmentId(null);
			} else {
				List<ObjectId> treatmentIds = new ArrayList<ObjectId>();
				for (String treatmentId : request.getTreatmentId()) {
					treatmentIds.add(new ObjectId(treatmentId));
				}
				dentalCampCollection.setTreatmentId(treatmentIds);
			}

			if (request.getReasonIds() == null) {
				dentalCampCollection.setReasonIds(null);
			} else {
				List<ObjectId> reasonIds = new ArrayList<ObjectId>();
				for (String reasonId : request.getReasonIds()) {
					reasonIds.add(new ObjectId(reasonId));
				}
				dentalCampCollection.setReasonIds(reasonIds);
			}

			if (request.getConvertedDate() == null && request.getLeadStage() != null) {
				if (request.getLeadStage().getType().equalsIgnoreCase(LeadStage.CONVERTED.getType()))
					dentalCampCollection.setConvertedDate(new Date());
			}

			dentalCampCollection = dentalCampRepository.save(dentalCampCollection);
			response = new DentalCampResponse();
			BeanUtil.map(dentalCampCollection, response);
			if (dentalCampCollection.getTreatmentId() != null) {
				List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation
										.match(new Criteria("id").in(dentalCampCollection.getTreatmentId()))),
								DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
						.getMappedResults();
				List<String> dentalTreatment = new ArrayList<String>();
				for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
					dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
				}
				response.setTreatmentNames(dentalTreatment);
			}
			if (request.getSmileBuddyId() != null) {
				UserCollection smileBuddyUserCollection = userRepository
						.findById(new ObjectId(request.getSmileBuddyId())).orElse(null);
				if (smileBuddyUserCollection != null)
					response.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
			}

			if (request.getReferredBy() != null) {
				DentalCampCollection referredByUserCollection = dentalCampRepository
						.findById(new ObjectId(request.getReferredBy())).orElse(null);
				if (referredByUserCollection != null)
					response.setReferredByName(referredByUserCollection.getUserName());
			}

			if (request.getDentalStudioId() != null) {
				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getDentalStudioId())).orElse(null);
				if (locationCollection != null)
					response.setLocationName(locationCollection.getLocationName());
			}

			if (request.getAssociateDoctorIds() != null) {
				List<UserCollection> reasonsCollections = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("id").in(request.getAssociateDoctorIds()))),
						UserCollection.class, UserCollection.class).getMappedResults();
				List<String> doctorsName = new ArrayList<String>();
				for (UserCollection reasonsCollection : reasonsCollections) {
					doctorsName.add(reasonsCollection.getFirstName());
				}
				response.setAssociateDoctors(doctorsName);
			}
			if (dentalCampCollection.getReasonIds() != null) {
				List<DentalReasonsCollection> reasonsCollections = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("id").in(dentalCampCollection.getReasonIds()))),
						DentalReasonsCollection.class, DentalReasonsCollection.class).getMappedResults();
				List<String> reasonString = new ArrayList<String>();
				for (DentalReasonsCollection reasonsCollection : reasonsCollections) {
					reasonString.add(reasonsCollection.getReason());
				}
				response.setReasons(reasonString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private void sendAppointmentwhastSmsNotification(DentalCampRequest request) throws MessagingException {
		if (request.getIsSendWelcomeSms()) {
			sendMsg(request.getUserName(), request.getMobileNumber(), request.getCampName(), request.getLanguage(),
					request.getIsCamp());
		}
		if (request.getIsSendWelcomeWhatsapp()) {
			sendWhatsapp(request.getUserName(), request.getWhatsAppNumber(), request.getCampName(),
					request.getLanguage(), request.getIsCamp());
		}
	}

	private void sendWhatsapp(String userName, String whatsAppNumber, String campName, String language,
			Boolean isCamp) {
		try {

			URL url = new URL("https://api.interakt.ai/v1/public/message/");

			JSONObject requestObject1 = new JSONObject();
			JSONObject requestObject2 = new JSONObject();
			JSONArray requestObject3 = new JSONArray();
			requestObject1.put("phoneNumber", whatsAppNumber);
			requestObject1.put("countryCode", "+91");
			requestObject1.put("type", "Template");
			requestObject3.put(userName);
			if (isCamp) {
				requestObject3.put("attending our camp");
				if (language.equals("English")) {
					requestObject2.put("name", "patient_appointment_sms");
					requestObject2.put("languageCode", "en");
				} else if (language.equals("Hindi")) {
					requestObject2.put("name", "patient_appointment_sms");
					requestObject2.put("languageCode", "hi");
				}
			} else {
				requestObject3.put("contacting us");
				if (language.equals("English")) {
					requestObject2.put("name", "patient_appointment_sms");
					requestObject2.put("languageCode", "en");
				} else if (language.equals("Hindi")) {
					requestObject2.put("name", "patient_appointment_sms");
					requestObject2.put("languageCode", "hi");
				}
			}
			requestObject2.put("bodyValues", requestObject3);
			requestObject1.put("template", requestObject2);

			ObjectMapper mapper = new ObjectMapper();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InteraktResponse list = null;

			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.post().setUri(url.toURI())
					.setHeader("Authorization", "Basic " + secretKey)
					.setEntity(new StringEntity(requestObject1.toString(), ContentType.APPLICATION_JSON)).build();
			org.apache.http.HttpResponse responses = client.execute(httprequest);
			responses.getEntity().writeTo(out);
			list = mapper.readValue(out.toString(), InteraktResponse.class);
			String responseString = out.toString();
			System.out.println("responseString" + responseString);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendMsg(String userName, String mobileNumber, String campName, String language, Boolean isCamp) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setType("TXN");
		SMSDetail smsDetail = new SMSDetail();
		String text = "";
		SMS sms = new SMS();
		if (isCamp) {
			if (language.equals("English")) {
				text = "Welcome " + userName + " to Smilebird. Thanks for" + " attending our camp,"
						+ " our team can be reached at 09834377100 for booking appointment." + "\n"
						+ "- Team Smilebird";
			} else if (language.equals("Hindi")) {
				text = "Welcome " + userName + " to Smilebird. Thanks for" + " attending our camp,"
						+ " our team can be reached at 09834377100 for booking appointment." + "\n"
						+ "- Team Smilebird";
			}
		} else {
			if (language.equals("English")) {
				text = "Welcome " + userName + " to Smilebird. Thanks for" + " contacting us,"
						+ " our team can be reached at 09834377100 for booking appointment." + "\n"
						+ "- Team Smilebird";
			} else if (language.equals("Hindi")) {
				text = "Welcome " + userName + " to Smilebird. Thanks for" + " contacting us,"
						+ " our team can be reached at 09834377100 for booking appointment." + "\n"
						+ "- Team Smilebird";
			}
		}
		smsDetail.setUserName(userName);
		smsTrackDetail.setTemplateId("1307165900801967389");
//		if (language.equals("English")) {
//			text = "Dear " + userName + "," + " Thanks for " + "attending " + "Smilebird's Dental " + "Camp."
//					+ " Please" + " call/Whatsapp" + " us at " + smilebirdSupportNumber
//					+ " for booking appointment with us" + "." + "\n" + "- Smilebird Team";
//			smsDetail.setUserName(userName);
//			smsTrackDetail.setTemplateId("1307165418154676875");
//		} else if (language.equals("Hindi")) {
//			text = "प्रिय " + userName + "," + " स्माइलबर्ड्स डेंटल " + " शिविर " + "में" + " भाग "
//					+ "लेने के लिए धन्यवाद। हमारे साथ अपॉइंटमेंट बुक करने के लिए कृपया हमें " + smilebirdSupportNumber
//					+ " पर" + " कॉल/व्हाट्सएप" + " करें। " + "\n" + "- Smilebird Team";
//			smsDetail.setUserName(userName);
//			smsTrackDetail.setTemplateId("1307165418156325150");
//		}

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

	@Override
	public Response<Object> getDentalCamps(List<String> treatmentId, List<String> associateDoctorId, String salaryRange,
			String campName, int size, int page, String locality, String language, String city, String leadType,
			String leadStage, String profession, Boolean isPatientCreated, String isPhotoUpload, String gender,
			String followupType, String age, String complaint, String dateFilterType, String fromDate, String toDate,
			String searchTerm, Boolean isDiscarded, Boolean isMobileNumberPresent, String smileBuddyId,
			String campaignId) {
		List<DentalCampResponse> responseList = new ArrayList<DentalCampResponse>();
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

			if (associateDoctorId != null && !associateDoctorId.isEmpty()) {
				List<ObjectId> associateDoctorIds = new ArrayList<ObjectId>();
				for (String id : associateDoctorId) {
					associateDoctorIds.add(new ObjectId(id));
				}
				criteria.and("associateDoctorIds").in(associateDoctorIds);
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
			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.DESC, "registrationDate"));

			if (from != null && to != null) {
				if (dateFilterType.equals(DateFilterType.FOLLOWUP.getFilter())) {
					criteria.and("followUp").gte(from).lte(to);
					sortOperation = Aggregation.sort(new Sort(Direction.DESC, "followUp"));
				} else if (dateFilterType.equals(DateFilterType.CREATEDTIME.getFilter())) {
					criteria.and("createdTime").gte(from).lte(to);
					sortOperation = Aggregation.sort(new Sort(Direction.DESC, "createdTime"));
				} else if (dateFilterType.equals(DateFilterType.UPDATEDTIME.getFilter())) {
					criteria.and("updatedTime").gte(from).lte(to);
					sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
				} else if (dateFilterType.equals(DateFilterType.REGISTRATIONDATE.getFilter())) {
					criteria.and("registrationDate").gte(from).lte(to);
					sortOperation = Aggregation.sort(new Sort(Direction.DESC, "registrationDate"));
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("smileBuddyId").is(new ObjectId(smileBuddyId));
			if (!DPDoctorUtils.anyStringEmpty(campaignId))
				criteria.and("campaignId").is(new ObjectId(campaignId));
			if (!DPDoctorUtils.anyStringEmpty(campName))
				criteria.and("campName").is(campName);
			if (!DPDoctorUtils.anyStringEmpty(salaryRange))
				criteria.and("salaryRange").is(salaryRange);
			if (!DPDoctorUtils.anyStringEmpty(locality))
				criteria.and("locality").is(locality);
			if (!DPDoctorUtils.anyStringEmpty(language))
				criteria.and("language").is(language);
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria.and("city").is(city);
			if (!DPDoctorUtils.anyStringEmpty(leadType))
				criteria.and("leadType").is(LeadType.valueOf(leadType));
			if (!DPDoctorUtils.anyStringEmpty(leadStage))
				criteria.and("leadStage").is(LeadStage.valueOf(leadStage));
			if (!DPDoctorUtils.anyStringEmpty(profession))
				criteria.and("profession").is(profession);
			if (isPatientCreated != null)
				criteria.and("isPatientCreated").is(isPatientCreated);
			if (!DPDoctorUtils.anyStringEmpty(isPhotoUpload))
				criteria.and("isPhotoUpload").is(isPhotoUpload);
			if (!DPDoctorUtils.anyStringEmpty(gender))
				criteria.and("gender").is(gender);
			if (!DPDoctorUtils.anyStringEmpty(followupType))
				criteria.and("followupType").is(FollowupType.valueOf(followupType));
			if (!DPDoctorUtils.anyStringEmpty(age))
				criteria.and("age").is(age);
			if (!DPDoctorUtils.anyStringEmpty(complaint))
				criteria.and("complaint").is(complaint);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("campName").regex("^" + searchTerm, "i"),
						new Criteria("campName").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm),
						new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("complaint").regex("^" + searchTerm, "i"),
						new Criteria("complaint").regex("^" + searchTerm));
			}
//			Criteria criteriaForFollowup = new Criteria();
//			if (dateFilterType.equals(DateFilterType.FOLLOWUP.getFilter())) {
//				criteriaForFollowup.and("followUpCommunication.communication.get(0).dateTime").gte(from).lte(to);
//			}
			response.setCount(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//							Aggregation.sort(Sort.Direction.DESC, "registrationDate")
					sortOperation), DentalCampCollection.class, DentalCampResponse.class).getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//								Aggregation.lookup("followUp_communication_details_cl", "userId", "_id",
//										"followUpCommunication"),
//								Aggregation.unwind("followUpCommunication"), Aggregation.match(criteriaForFollowup),
//						Aggregation.sort(Sort.Direction.DESC, "registrationDate"),
						sortOperation, Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						DentalCampCollection.class, DentalCampResponse.class).getMappedResults();
			} else {
				responseList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//								Aggregation.sort(Sort.Direction.DESC, "registrationDate")
						sortOperation), DentalCampCollection.class, DentalCampResponse.class).getMappedResults();

			}
			for (DentalCampResponse dentalCampResponse : responseList) {
				if (dentalCampResponse.getTreatmentId() != null) {
					List<DentalTreatmentDetailCollection> detailCollections = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation
											.match(new Criteria("id").in(dentalCampResponse.getTreatmentId()))),
									DentalTreatmentDetailCollection.class, DentalTreatmentDetailCollection.class)
							.getMappedResults();
					List<String> dentalTreatment = new ArrayList<String>();
					for (DentalTreatmentDetailCollection dentalTreatmentDetailCollection : detailCollections) {
						dentalTreatment.add(dentalTreatmentDetailCollection.getTreatmentName());
					}
					dentalCampResponse.setTreatmentNames(dentalTreatment);
				}
				if (dentalCampResponse.getSmileBuddyId() != null) {
					UserCollection smileBuddyUserCollection = userRepository
							.findById(new ObjectId(dentalCampResponse.getSmileBuddyId())).orElse(null);
					if (smileBuddyUserCollection != null)
						dentalCampResponse.setSmileBuddyName(smileBuddyUserCollection.getFirstName());
				}
				if (dentalCampResponse.getReferredBy() != null) {
					DentalCampCollection referredByUserCollection = dentalCampRepository
							.findById(new ObjectId(dentalCampResponse.getReferredBy())).orElse(null);
					if (referredByUserCollection != null)
						dentalCampResponse.setReferredByName(referredByUserCollection.getUserName());
				}
				if (dentalCampResponse.getDentalStudioId() != null) {
					LocationCollection locationCollection = locationRepository
							.findById(new ObjectId(dentalCampResponse.getDentalStudioId())).orElse(null);
					if (locationCollection != null)
						dentalCampResponse.setLocationName(locationCollection.getLocationName());
				}
				if (dentalCampResponse.getCampaignId() != null) {
					CampaignNameCollection campaignNameCollection = campaignNameRepository
							.findById(new ObjectId(dentalCampResponse.getCampaignId())).orElse(null);
					if (campaignNameCollection != null)
						dentalCampResponse.setCampaignName(campaignNameCollection.getCampaignName());
				}

				if (dentalCampResponse.getAssociateDoctorIds() != null) {
					List<UserCollection> reasonsCollections = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation
									.match(new Criteria("id").in(dentalCampResponse.getAssociateDoctorIds()))),
							UserCollection.class, UserCollection.class).getMappedResults();
					List<String> doctorsName = new ArrayList<String>();
					for (UserCollection reasonsCollection : reasonsCollections) {
						doctorsName.add(reasonsCollection.getFirstName());
					}
					dentalCampResponse.setAssociateDoctors(doctorsName);
				}

				if (dentalCampResponse.getReasonIds() != null) {
					List<DentalReasonsCollection> reasonsCollections = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("id").in(dentalCampResponse.getReasonIds()))),
							DentalReasonsCollection.class, DentalReasonsCollection.class).getMappedResults();
					System.out.println("reasonsCollections:" + reasonsCollections.size());
					List<String> reasonString = new ArrayList<String>();
					for (DentalReasonsCollection reasonsCollection : reasonsCollections) {
						reasonString.add(reasonsCollection.getReason());
					}
					dentalCampResponse.setReasons(reasonString);
				}
			}
			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteDentalCampsById(String campId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			DentalCampCollection dentalCampCollection = dentalCampRepository.findById(new ObjectId(campId))
					.orElse(null);
			if (dentalCampCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			dentalCampCollection.setUpdatedTime(new Date());
			dentalCampCollection.setIsDiscarded(isDiscarded);
			dentalCampCollection = dentalCampRepository.save(dentalCampCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DentalCampResponse getDentalCampsById(String campId) {
		DentalCampResponse response = new DentalCampResponse();
		try {
			DentalCampCollection dentalCampCollection = dentalCampRepository.findById(new ObjectId(campId))
					.orElse(null);
			if (dentalCampCollection != null) {
				BeanUtil.map(dentalCampCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DentalCampTreatmentNameRequest addEditDentalCampTreatmentName(DentalCampTreatmentNameRequest request) {
		DentalCampTreatmentNameRequest response = new DentalCampTreatmentNameRequest();
		DentalCampTreatmentNameCollection dentalCampTreatmentNameCollection = new DentalCampTreatmentNameCollection();
		BeanUtil.map(request, dentalCampTreatmentNameCollection);
		dentalCampTreatmentNameCollection.setCreatedTime(new Date());
		dentalCampTreatmentNameRepository.save(dentalCampTreatmentNameCollection);
		BeanUtil.map(dentalCampTreatmentNameCollection, response);
		return response;
	}

	@Override
	public Response<Object> getDentalCampsTreatmentList(int size, int page, String type, String searchTerm,
			Boolean isDiscarded) {
		List<DentalCampTreatmentNameRequest> responseList = new ArrayList<DentalCampTreatmentNameRequest>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("discarded").ne(true);
			else
				criteria.and("discarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("dentalTreatment").regex("^" + searchTerm, "i"),
						new Criteria("dentalTreatment").regex("^" + searchTerm));
			}

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.orOperator(new Criteria("type").regex("^" + type, "i"),
						new Criteria("type").regex("^" + type));
			}

			response.setCount(mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "createdTime")),
							DentalCampTreatmentNameCollection.class, DentalCampTreatmentNameRequest.class)
					.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.sort(Sort.Direction.DESC, "createdTime"),
										Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
								DentalCampTreatmentNameCollection.class, DentalCampTreatmentNameRequest.class)
						.getMappedResults();
			} else {
				responseList = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								DentalCampTreatmentNameCollection.class, DentalCampTreatmentNameRequest.class)
						.getMappedResults();
			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteCampMsgTemplate(String templateId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CampMsgTemplateCollection msgTemplateCollection = msgTemplateRepository.findById(new ObjectId(templateId))
					.orElse(null);
			if (msgTemplateCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			msgTemplateCollection.setUpdatedTime(new Date());
			msgTemplateCollection.setDiscarded(isDiscarded);
			msgTemplateCollection = msgTemplateRepository.save(msgTemplateCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getCampMsgTemplate(int size, int page, String searchTerm, Boolean isDiscarded,
			String broadcastType) {
		List<CampMsgTemplateResponse> responseList = new ArrayList<CampMsgTemplateResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("discarded").ne(true);
			else
				criteria.and("discarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(broadcastType)) {
				criteria.and("broadcastType").is(BroadcastType.valueOf(broadcastType.toUpperCase()));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									CampMsgTemplateCollection.class, CampMsgTemplateResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						CampMsgTemplateCollection.class, CampMsgTemplateResponse.class).getMappedResults();
			} else {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampMsgTemplateCollection.class, CampMsgTemplateResponse.class).getMappedResults();
			}
			response.setDataList(responseList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public CampMsgTemplateResponse addEditCampMsgTemplate(CampMsgTemplateRequest request) {
		CampMsgTemplateResponse response = null;
		CampMsgTemplateCollection msgTemplateCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				msgTemplateCollection = msgTemplateRepository.findById(new ObjectId(request.getId())).orElse(null);
				msgTemplateCollection.setUpdatedTime(msgTemplateCollection.getCreatedTime());
				request.setCreatedTime(new Date());
			} else {
				msgTemplateCollection = new CampMsgTemplateCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}

			BeanUtil.map(request, msgTemplateCollection);
			msgTemplateCollection = msgTemplateRepository.save(msgTemplateCollection);
			response = new CampMsgTemplateResponse();
			BeanUtil.map(msgTemplateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DentalChainFile uploadImage(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {
				String path = "smilebirdImage" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = saveImage(file, recordPath, true);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	@Override
	public DentalChainFile uploadVideo(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {

				String path = "smilebirdVideo" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = saveVideo(file, recordPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	public DentalChainFile saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
		Double fileSizeInMB = 10.0;
		try {
			InputStream fis = file.getInputStream();
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentType());
			metadata.setContentType(file.getContentType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, file.getInputStream(), metadata));
			response.setImageUrl(imagePath + recordPath);
			if (createThumbnail) {
				response.setThumbnailUrl(imagePath + saveThumbnailUrl(file, recordPath));
			}
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;

	}

	public DentalChainFile saveVideo(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
		Double fileSizeInMB = 10.0;
		try {
			InputStream fis = file.getInputStream();
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentType());
			metadata.setContentType(file.getContentType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, file.getInputStream(), metadata));
			response.setVideoUrl(imagePath + recordPath);

		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;

	}

	public String saveThumbnailUrl(MultipartFile file, String path) {
		String thumbnailUrl = "";

		try {
			String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
			AmazonS3 s3client = new AmazonS3Client(credentials);
			S3Object object = s3client.getObject(new GetObjectRequest(bucketName, path));
			InputStream objectData = object.getObjectContent();

			BufferedImage originalImage = ImageIO.read(objectData);
			double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
			int height = originalImage.getHeight();

			int width = originalImage.getWidth();
			int max = 120;
			if (width == height) {
				width = max;
				height = max;
			} else if (width > height) {
				height = max;
				width = (int) (ratio * max);
			} else {
				width = max;
				height = (int) (max / ratio);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
					null);
			// String fileName = fileDetails.getFileName() + "_thumb." +
			// fileDetails.getFileExtension();
			thumbnailUrl = "thumb_" + path;

			originalImage.flush();
			originalImage = null;

			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ImageIO.write(img, fileExtension, outstream);
			byte[] buffer = outstream.toByteArray();
			objectData = new ByteArrayInputStream(buffer);

			String contentType = URLConnection.guessContentTypeFromStream(objectData);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(buffer.length);
			metadata.setContentEncoding(fileExtension);
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			s3client.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, metadata));
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return thumbnailUrl;
	}

	@Override
	public Boolean broadcastTemplateToLeads(DentalCampBroadcastTemplateRequest request) {
		Boolean response = false;
		try {

			Criteria criteria = new Criteria();
			Aggregation aggregation = null;
			if (request.getIsDiscarded() != null) {
				if (request.getIsDiscarded() == false)
					criteria.and("isDiscarded").ne(true);
				else
					criteria.and("isDiscarded").is(request.getIsDiscarded());
			}
			if (request.getIsMobileNumberPresent() != null) {
				if (request.getIsMobileNumberPresent() == true)
					criteria.and("mobileNumber").ne("");
				else
					criteria.and("mobileNumber").is("");
			}

			if (request.getTreatmentId() != null && !request.getTreatmentId().isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : request.getTreatmentId()) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}

			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getFromDate(), request.getToDate())) {
				from = new Date(Long.parseLong(request.getFromDate()));
				to = new Date(Long.parseLong(request.getToDate()));
			} else if (!DPDoctorUtils.anyStringEmpty(request.getFromDate())) {
				from = new Date(Long.parseLong(request.getFromDate()));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(request.getToDate())) {
				from = new Date(0);
				to = new Date(Long.parseLong(request.getToDate()));
			}

			if (from != null && to != null) {
				if (request.getDateFilterType().equals(DateFilterType.FOLLOWUP.getFilter())) {
					criteria.and("followUp").gte(from).lte(to);
				} else if (request.getDateFilterType().equals(DateFilterType.CREATEDTIME.getFilter())) {
					criteria.and("createdTime").gte(from).lte(to);
				} else if (request.getDateFilterType().equals(DateFilterType.UPDATEDTIME.getFilter())) {
					criteria.and("updatedTime").gte(from).lte(to);
				} else if (request.getDateFilterType().equals(DateFilterType.REGISTRATIONDATE.getFilter())) {
					criteria.and("registrationDate").gte(from).lte(to);
				}
			}
			List<ObjectId> campaignIds = new ArrayList<ObjectId>();
			if (request.getCampaignId() != null) {
				for (String campaignId : request.getCampaignId()) {
					campaignIds.add(new ObjectId(campaignId));
				}
				criteria.and("campaignId").in(campaignIds);
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getCampName()))
				criteria.and("campName").is(request.getCampName());
			if (!DPDoctorUtils.anyStringEmpty(request.getSmileBuddyId()))
				criteria.and("smileBuddyId").is(new ObjectId(request.getSmileBuddyId()));
			if (!DPDoctorUtils.anyStringEmpty(request.getSalaryRange()))
				criteria.and("salaryRange").is(request.getSalaryRange());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocality()))
				criteria.and("locality").is(request.getLocality());
			if (!DPDoctorUtils.anyStringEmpty(request.getLanguage()))
				criteria.and("language").is(request.getLanguage());
			if (!DPDoctorUtils.anyStringEmpty(request.getCity()))
				criteria.and("city").is(request.getCity());
			if (!DPDoctorUtils.anyStringEmpty(request.getLeadType()))
				criteria.and("leadType").is(LeadType.valueOf(request.getLeadType()));
			if (!DPDoctorUtils.anyStringEmpty(request.getLeadStage()))
				criteria.and("leadStage").is(LeadStage.valueOf(request.getLeadStage()));
			if (!DPDoctorUtils.anyStringEmpty(request.getProfession()))
				criteria.and("profession").is(request.getProfession());
			if (request.getIsPatientCreated() != null)
				criteria.and("isPatientCreated").is(request.getIsPatientCreated());
			if (!DPDoctorUtils.anyStringEmpty(request.getIsPhotoUpload()))
				criteria.and("isPhotoUpload").is(request.getIsPhotoUpload());
			if (!DPDoctorUtils.anyStringEmpty(request.getGender()))
				criteria.and("gender").is(request.getGender());
			if (!DPDoctorUtils.anyStringEmpty(request.getFollowupType()))
				criteria.and("followupType").is(FollowupType.valueOf(request.getFollowupType()));
			if (!DPDoctorUtils.anyStringEmpty(request.getAge()))
				criteria.and("age").is(request.getAge());
			if (!DPDoctorUtils.anyStringEmpty(request.getComplaint()))
				criteria.and("complaint").is(request.getComplaint());

			if (!DPDoctorUtils.anyStringEmpty(request.getSearchTerm())) {
				String searchTerm = request.getSearchTerm();
				criteria.orOperator(new Criteria("campName").regex("^" + searchTerm, "i"),
						new Criteria("campName").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm),
						new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("complaint").regex("^" + searchTerm, "i"),
						new Criteria("complaint").regex("^" + searchTerm));
			}

			if (request.getCampUsers() != null && !request.getCampUsers().isEmpty()) {
				List<ObjectId> campUserIds = new ArrayList<ObjectId>();
				List<ObjectId> ids = new ArrayList<ObjectId>();
				for (CampUser campUser : request.getCampUsers()) {
					if (campUser.getIsCampUser()) {
						campUserIds.add(new ObjectId(campUser.getId()));
					} else {
						ids.add(new ObjectId(campUser.getId()));
						List<UserCollection> users = mongoTemplate
								.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(ids))),
										UserCollection.class, UserCollection.class)
								.getMappedResults();
						System.out.println("users" + users.size());
//							isCampUser = false;
					}
				}
				criteria.and("id").in(campUserIds);
			}

			if (request.getSize() > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "registrationDate"),
						Aggregation.skip((long) (request.getPage()) * request.getSize()));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "registrationDate"));
			}

			System.out.println("aggregation" + aggregation);
			List<String> mobileNumbers = new ArrayList<String>();
			List<String> whatsappNumbers = new ArrayList<String>();
			List<DentalCampCollection> campCollections = mongoTemplate
					.aggregate(aggregation, DentalCampCollection.class, DentalCampCollection.class).getMappedResults();
			for (DentalCampCollection dentalCampCollection : campCollections) {
				if (!mobileNumbers.contains(dentalCampCollection.getMobileNumber())) {
					mobileNumbers.add(dentalCampCollection.getMobileNumber());
				}
				if (!whatsappNumbers.contains(dentalCampCollection.getMobileNumber())) {
					whatsappNumbers.add(dentalCampCollection.getMobileNumber());
				}
			}
			if (request.getBroadcastType().equals(BroadcastType.SMS)) {
				for (String mobileNumber : mobileNumbers) {
					sendMsg(request, mobileNumber);
				}
				response = true;
			}

			if (request.getBroadcastType().equals(BroadcastType.WHATSAPP)) {
				for (String whatsappNumber : whatsappNumbers) {
					sendWhatsapp(request, whatsappNumber);
				}
				response = true;
			}
		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}
		return response;
	}

	@Override
	public Boolean broadcastTemplateToDoctors(DentalCampBroadcastTemplateRequest request) {
		Boolean response = false;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria("location.isClinic").is(true).and("doctor").exists(true).and("user")
					.exists(true).and("location.isDentalChain").is(true).and("user.mobileNumber").exists(true);

			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
				criteria.and("doctor.gender").regex(request.getGender());
			}

			if (request.getDoctorId() != null && !request.getDoctorId().isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : request.getDoctorId()) {
					doctorObjectIds.add(new ObjectId(id));
				}
				criteria.and("doctorId").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getCity())) {
				criteria.and("location.city").regex(request.getCity());
			}

			criteria.and("user.isActive").is(true);

			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("id", "$doctorId"), Fields.field("mobileNumber", "$user.mobileNumber")));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					Aggregation.unwind("user"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList);

			AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation,
					DoctorClinicProfileCollection.class, UserCollection.class);
			List<UserCollection> userCollections = results.getMappedResults();

			List<String> mobileNumbers = new ArrayList<String>();
			for (UserCollection userCollection : userCollections) {
				if (!mobileNumbers.contains(userCollection.getMobileNumber())) {
					mobileNumbers.add(userCollection.getMobileNumber());
				}
			}
			if (request.getBroadcastType().equals(BroadcastType.SMS)) {
				for (String mobileNumber : mobileNumbers) {
					sendMsg(request, mobileNumber);
				}
			}

			if (request.getBroadcastType().equals(BroadcastType.WHATSAPP)) {
				for (String whatsappNumber : mobileNumbers) {
					sendWhatsapp(request, whatsappNumber);
				}
			}
			response = true;
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean broadcastTemplateToPatients(DentalCampBroadcastTemplateRequest request) {
		Boolean response = false;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria("userState").is(UserState.USERSTATECOMPLETE).and("isDentalChainPatient")
					.is(true);

			if (request.getIsDiscarded() == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(request.getIsDiscarded());

			if (request.getIsMobileNumberPresent() != null) {
				if (request.getIsMobileNumberPresent() == true)
					criteria.and("mobileNumber").ne("");
				else
					criteria.and("mobileNumber").is("");
			}

			if (request.getTreatmentId() != null && !request.getTreatmentId().isEmpty()) {
				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
				for (String id : request.getTreatmentId()) {
					treatmentObjectIds.add(new ObjectId(id));
				}
				criteria.and("treatmentId").in(treatmentObjectIds);
			}

			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getFromDate(), request.getToDate())) {
				from = new Date(Long.parseLong(request.getFromDate()));
				to = new Date(Long.parseLong(request.getToDate()));
			} else if (!DPDoctorUtils.anyStringEmpty(request.getFromDate())) {
				from = new Date(Long.parseLong(request.getFromDate()));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(request.getToDate())) {
				from = new Date(0);
				to = new Date(Long.parseLong(request.getToDate()));
			}

			if (from != null && to != null) {
				if (request.getDateFilterType().equals(DateFilterType.FOLLOWUP.getFilter())) {
					criteria.and("followUp").gte(from).lte(to);
				} else if (request.getDateFilterType().equals(DateFilterType.CREATEDTIME.getFilter())) {
					criteria.and("createdTime").gte(from).lte(to);
				} else if (request.getDateFilterType().equals(DateFilterType.UPDATEDTIME.getFilter())) {
					criteria.and("updatedTime").gte(from).lte(to);
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getSmileBuddyId()))
				criteria.and("smileBuddyId").is(new ObjectId(request.getSmileBuddyId()));
			if (!DPDoctorUtils.anyStringEmpty(request.getLocality()))
				criteria.and("locality").is(request.getLocality());
			if (!DPDoctorUtils.anyStringEmpty(request.getLanguage()))
				criteria.and("language").is(request.getLanguage());
			if (!DPDoctorUtils.anyStringEmpty(request.getCity()))
				criteria.and("city").is(request.getCity());
			if (!DPDoctorUtils.anyStringEmpty(request.getGender()))
				criteria.and("gender").is(request.getGender());
			if (!DPDoctorUtils.anyStringEmpty(request.getFollowupType()))
				criteria.and("followupType").is(FollowupType.valueOf(request.getFollowupType()));
			if (!DPDoctorUtils.anyStringEmpty(request.getAge()))
				criteria.and("age").is(request.getAge());
			if (!DPDoctorUtils.anyStringEmpty(request.getComplaint()))
				criteria.and("complaint").is(request.getComplaint());

			if (!DPDoctorUtils.anyStringEmpty(request.getSearchTerm())) {
				String searchTerm = request.getSearchTerm();
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (request.getSize() > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),
						Aggregation.skip((long) (request.getPage()) * request.getSize()));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation" + aggregation);

			AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation, UserCollection.class,
					UserCollection.class);

			List<UserCollection> userCollections = results.getMappedResults();
			System.out.println("results" + userCollections.size());
			List<String> mobileNumbers = new ArrayList<String>();
			for (UserCollection userCollection : userCollections) {
				if (!mobileNumbers.contains(userCollection.getMobileNumber())) {
					mobileNumbers.add(userCollection.getMobileNumber());
				}
			}
			if (request.getBroadcastType().equals(BroadcastType.SMS)) {
				for (String mobileNumber : mobileNumbers) {
					sendMsg(request, mobileNumber);
				}
			}

			if (request.getBroadcastType().equals(BroadcastType.WHATSAPP)) {
				for (String whatsappNumber : mobileNumbers) {
					sendWhatsapp(request, whatsappNumber);
				}
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteTreatmentName(String id, Boolean isDiscarded) {
		Boolean response = false;
		try {
			DentalCampTreatmentNameCollection dentalCampCollection = dentalCampTreatmentNameRepository
					.findById(new ObjectId(id)).orElse(null);
			if (dentalCampCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			dentalCampCollection.setUpdatedTime(new Date());
			dentalCampCollection.setDiscarded(isDiscarded);
			dentalCampCollection = dentalCampTreatmentNameRepository.save(dentalCampCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private void sendWhatsapp(DentalCampBroadcastTemplateRequest request, String mobileNumber) {
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

			requestObject2.put("name", request.getTemplateName());
			if (request.getBodyValues() != null && !request.getBodyValues().isEmpty()) {
				for (String string : request.getBodyValues()) {
					requestObject3.put(string);
				}
			}
			if (request.getLanguageCode() != null) {
				if (request.getLanguageCode().equals("en")) {
					requestObject2.put("languageCode", "en");
				} else if (request.getLanguageCode().equals("hi")) {
					requestObject2.put("languageCode", "hi");
				}
			} else
				requestObject2.put("languageCode", "en");
			if (request.getHeaderValues() != null && !request.getHeaderValues().isEmpty()) {
				for (String string : request.getHeaderValues()) {
					headerArray.put(string);
				}
				requestObject2.put("headerValues", headerArray);
			}

			if (request.getImageUrl() != null) {
				headerArray.put(request.getImageUrl());
				requestObject2.put("headerValues", headerArray);
			}
			if (request.getHeaderFileName() != null) {
				requestObject2.put("fileName", request.getHeaderFileName());
			}
			if (request.getButtonValues() != null) {
				buttonValueArray.put(request.getButtonValues());
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

	private void sendMsg(DentalCampBroadcastTemplateRequest request, String mobileNumber) {
		try {
			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			smsTrackDetail.setType(request.getRoute().getType());
			SMSDetail smsDetail = new SMSDetail();
			SMS sms = new SMS();

			smsTrackDetail.setTemplateId(request.getTemplateId());

			sms.setSmsText(request.getMessage());

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

	@Override
	public FollowupCommunicationRequest getFollowupByUserId(String userId) {
		FollowupCommunicationRequest response = null;
		try {
			FollowupCommunicationCollection followupCommunicationCollection = followupCommunicationRepository
					.findByUserId(new ObjectId(userId));
			if (followupCommunicationCollection == null) {
				return response;
			}
			response = new FollowupCommunicationRequest();
			BeanUtil.map(followupCommunicationCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public List<FollowupCommunicationRequest> getFollowupDetailList(int size, int page, String searchTerm) {
		List<FollowupCommunicationRequest> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, FollowupCommunicationCollection.class, FollowupCommunicationRequest.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting details " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countDetailsList(String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			response = (int) mongoTemplate.count(new Query(criteria), FollowupCommunicationCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while details " + e.getMessage());

		}
		return response;
	}

	@Override
	public FollowupCommunicationRequest addEditFollowupCommunication(FollowupCommunicationRequest request) {
		FollowupCommunicationRequest response = null;
		try {
			FollowupCommunicationCollection followupCommunicationCollection = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
//			if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))
//				request.setMobileNumber(userCollection.getMobileNumber());
//			
//			if (DPDoctorUtils.anyStringEmpty(request.getUserName()))
//				request.setMobileNumber(userCollection.getUserName());
//
//			if (DPDoctorUtils.anyStringEmpty(request.getCity()))
//				request.setMobileNumber(userCollection.getCity());
//			
//			if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress()))
//				request.setMobileNumber(userCollection.getEmailAddress());
//
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				followupCommunicationCollection = followupCommunicationRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				if (followupCommunicationCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Communication Not found with Id");
				}
				request.setUpdatedTime(new Date());
				followupCommunicationCollection.setCommunication(null);
				request.setCreatedBy(followupCommunicationCollection.getCreatedBy());
				request.setCreatedTime(followupCommunicationCollection.getCreatedTime());
				BeanUtil.map(request, followupCommunicationCollection);

			} else {
//				if (request.getUserId() != null) {
//					FollowupCommunicationCollection FollowupCommunicationCollectionCk = followupCommunicationRepository
//							.findByUserId(new ObjectId(request.getUserId()));
//					if (FollowupCommunicationCollectionCk != null) {
//						throw new BusinessException(ServiceError.NotFound,
//								"Communication for this doctor already present  with Id"
//										+ FollowupCommunicationCollectionCk.getId());
//					}
//				}
				followupCommunicationCollection = new FollowupCommunicationCollection();
				BeanUtil.map(request, followupCommunicationCollection);
				followupCommunicationCollection.setCreatedBy("ADMIN");
				followupCommunicationCollection.setUpdatedTime(new Date());
				followupCommunicationCollection.setCreatedTime(new Date());
			}
			if (request.getCommunication() != null && !request.getCommunication().isEmpty()) {
				DentalCampCollection dentalCampCollection = dentalCampRepository
						.findById(new ObjectId(request.getUserId())).orElse(null);
				if (dentalCampCollection != null) {
					if (request.getCommunication().get(0) != null) {
						dentalCampCollection.setFollowUp(request.getCommunication().get(0).getDateTime());
						dentalCampCollection.setFollowupType(request.getCommunication().get(0).getFollowupType());
					}
					dentalCampCollection = dentalCampRepository.save(dentalCampCollection);
				} else {
					if (userCollection != null) {
						if (request.getCommunication().get(0) != null) {
							userCollection.setFollowUp(request.getCommunication().get(0).getDateTime());
							userCollection.setFollowupType(request.getCommunication().get(0).getFollowupType());
						}
						userCollection = userRepository.save(userCollection);
					}
				}
			}

			followupCommunicationCollection = followupCommunicationRepository.save(followupCommunicationCollection);
			response = new FollowupCommunicationRequest();
			BeanUtil.map(followupCommunicationCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Followup Communication " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while add/edit Followup Communication " + e.getMessage());

		}
		return response;
	}

	@Override
	public Response<Object> getDentalCampsCount(List<String> treatmentId, List<String> associateDoctorId,
			String salaryRange, String campName, int size, int page, String locality, String language, String city,
			String leadType, String leadStage, String profession, Boolean isPatientCreated, String isPhotoUpload,
			String gender, String followupType, String age, String complaint, String dateFilterType, String fromDate,
			String toDate, String searchTerm, Boolean isDiscarded, Boolean isMobileNumberPresent) {
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
			if (associateDoctorId != null && !associateDoctorId.isEmpty()) {
				List<ObjectId> associateDoctorIds = new ArrayList<ObjectId>();
				for (String id : treatmentId) {
					associateDoctorIds.add(new ObjectId(id));
				}
				criteria.and("associateDoctorIds").in(associateDoctorIds);
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
				} else if (dateFilterType.equals(DateFilterType.REGISTRATIONDATE.getFilter())) {
					criteria.and("registrationDate").gte(from).lte(to);
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(campName))
				criteria.and("campName").is(campName);
			if (!DPDoctorUtils.anyStringEmpty(salaryRange))
				criteria.and("salaryRange").is(salaryRange);
			if (!DPDoctorUtils.anyStringEmpty(locality))
				criteria.and("locality").is(locality);
			if (!DPDoctorUtils.anyStringEmpty(language))
				criteria.and("language").is(language);
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria.and("city").is(city);
			if (!DPDoctorUtils.anyStringEmpty(leadType))
				criteria.and("leadType").is(LeadType.valueOf(leadType));
			if (!DPDoctorUtils.anyStringEmpty(leadStage))
				criteria.and("leadStage").is(LeadStage.valueOf(leadStage));
			if (!DPDoctorUtils.anyStringEmpty(profession))
				criteria.and("profession").is(profession);
			if (isPatientCreated != null)
				criteria.and("isPatientCreated").is(isPatientCreated);
			if (!DPDoctorUtils.anyStringEmpty(isPhotoUpload))
				criteria.and("isPhotoUpload").is(isPhotoUpload);
			if (!DPDoctorUtils.anyStringEmpty(gender))
				criteria.and("gender").is(gender);
			if (!DPDoctorUtils.anyStringEmpty(followupType))
				criteria.and("followupType").is(FollowupType.valueOf(followupType));
			if (!DPDoctorUtils.anyStringEmpty(age))
				criteria.and("age").is(age);
			if (!DPDoctorUtils.anyStringEmpty(complaint))
				criteria.and("complaint").is(complaint);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("campName").regex("^" + searchTerm, "i"),
						new Criteria("campName").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm),
						new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("complaint").regex("^" + searchTerm, "i"),
						new Criteria("complaint").regex("^" + searchTerm));
			}
			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "registrationDate")),
					DentalCampCollection.class, DentalCampResponse.class).getMappedResults().size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public CampaignNameResponse addEditCampaignName(CampaignNameRequest request) {
		CampaignNameResponse response = null;
		CampaignNameCollection campNameCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				campNameCollection = campaignNameRepository.findById(new ObjectId(request.getId())).orElse(null);
				BeanUtil.map(request, campNameCollection);
				campNameCollection.setUpdatedTime(new Date());
			} else {
				campNameCollection = new CampaignNameCollection();
				BeanUtil.map(request, campNameCollection);
				campNameCollection.setUpdatedTime(new Date());
				campNameCollection.setCreatedTime(new Date());
			}
			List<ObjectId> cityIds = new ArrayList<ObjectId>();
			if (request.getCity() == null) {
				campNameCollection.setCity(null);
			} else {
				for (String cityId : request.getCity()) {
					cityIds.add(new ObjectId(cityId));
				}
				campNameCollection.setCity(cityIds);
			}
			campNameCollection = campaignNameRepository.save(campNameCollection);
			response = new CampaignNameResponse();
			BeanUtil.map(campNameCollection, response);
			List<DentalChainCityCollection> cityCollections = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(cityIds))),
							DentalChainCityCollection.class, DentalChainCityCollection.class)
					.getMappedResults();
			List<String> citys = new ArrayList<String>();
			for (DentalChainCityCollection cityCollection : cityCollections) {
				citys.add(cityCollection.getCity());
			}
			response.setCityNames(citys);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getCampaignNames(int size, int page, String searchTerm, Boolean isDiscarded) {
		List<CampaignNameResponse> responseList = new ArrayList<CampaignNameResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("campaignName").regex("^" + searchTerm, "i"),
						new Criteria("campaignName").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									CampaignNameCollection.class, CampaignNameResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						CampaignNameCollection.class, CampaignNameResponse.class).getMappedResults();
			} else {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampaignNameCollection.class, CampaignNameResponse.class).getMappedResults();
			}

			for (CampaignNameResponse campaignNameResponse : responseList) {
				List<CampaignObjectResponse> objectResponses = null;

				Criteria criteria1 = new Criteria();
				criteria1.and("isDiscarded").is(false);

				if (!DPDoctorUtils.anyStringEmpty(campaignNameResponse.getId())) {
					criteria1.and("campaignId").is(new ObjectId(campaignNameResponse.getId()));
				}
				objectResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria1),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampaignObjectCollection.class, CampaignObjectResponse.class).getMappedResults();

				campaignNameResponse.setObjectResponses(objectResponses);
				List<ObjectId> cityIds = new ArrayList<ObjectId>();
				if (campaignNameResponse.getCity() != null) {
					for (String cityId : campaignNameResponse.getCity()) {
						cityIds.add(new ObjectId(cityId));
					}
				}
				List<DentalChainCityCollection> cityCollections = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(cityIds))),
								DentalChainCityCollection.class, DentalChainCityCollection.class)
						.getMappedResults();
				List<String> citys = new ArrayList<String>();
				for (DentalChainCityCollection cityCollection : cityCollections) {
					citys.add(cityCollection.getCity());
				}
				campaignNameResponse.setCityNames(citys);
			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteCampaignName(String campaignId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CampaignNameCollection campaignNameCollection = campaignNameRepository.findById(new ObjectId(campaignId))
					.orElse(null);
			if (campaignNameCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			campaignNameCollection.setUpdatedTime(new Date());
			campaignNameCollection.setIsDiscarded(isDiscarded);
			campaignNameCollection = campaignNameRepository.save(campaignNameCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public CampaignObjectResponse addEditCampaignObject(CampaignObjectRequest request) {
		CampaignObjectResponse response = null;
		CampaignObjectCollection objectCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				objectCollection = campaignObjectRepository.findById(new ObjectId(request.getId())).orElse(null);
				objectCollection.setUpdatedTime(new Date());
			} else {
				objectCollection = new CampaignObjectCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, objectCollection);
			objectCollection = campaignObjectRepository.save(objectCollection);
			response = new CampaignObjectResponse();
			BeanUtil.map(objectCollection, response);
			CampaignNameCollection campaignNameCollection = campaignNameRepository
					.findById(new ObjectId(request.getCampaignId())).orElse(null);
			List<ObjectId> campaignObjectIds = null;
			if (campaignNameCollection != null) {
				if (campaignNameCollection.getCampaignObjectIds() != null
						&& campaignNameCollection.getCampaignObjectIds().isEmpty()) {
					campaignObjectIds = campaignNameCollection.getCampaignObjectIds();
					if (!campaignObjectIds.contains(objectCollection.getId())) {
						campaignObjectIds.add(objectCollection.getId());
						campaignNameCollection.setCampaignObjectIds(campaignObjectIds);
						campaignNameCollection = campaignNameRepository.save(campaignNameCollection);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getCampaignObjects(String campaignId, int size, int page, Boolean isDiscarded) {
		List<CampaignObjectResponse> responseList = new ArrayList<CampaignObjectResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(campaignId)) {
				criteria.and("campaignId").is(campaignId);
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									CampaignObjectCollection.class, CampaignObjectResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						CampaignObjectCollection.class, CampaignObjectResponse.class).getMappedResults();
			} else {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampaignObjectCollection.class, CampaignObjectResponse.class).getMappedResults();
			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteCampaignObject(String campaignObjectId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			CampaignObjectCollection objectCollection = campaignObjectRepository
					.findById(new ObjectId(campaignObjectId)).orElse(null);
			if (objectCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			objectCollection.setUpdatedTime(new Date());
			objectCollection.setIsDiscarded(isDiscarded);
			objectCollection = campaignObjectRepository.save(objectCollection);
			response = true;

			CampaignNameCollection campaignNameCollection = campaignNameRepository
					.findById(objectCollection.getCampaignId()).orElse(null);
			List<ObjectId> campaignObjectIds = null;
			if (campaignNameCollection != null) {
				if (campaignNameCollection.getCampaignObjectIds() != null
						&& campaignNameCollection.getCampaignObjectIds().isEmpty()) {
					campaignObjectIds = campaignNameCollection.getCampaignObjectIds();
					if (campaignObjectIds.contains(objectCollection.getId())) {
						campaignObjectIds.remove(objectCollection.getId());
						campaignNameCollection.setCampaignObjectIds(campaignObjectIds);
						campaignNameCollection = campaignNameRepository.save(campaignNameCollection);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DentalChainFile uploadAudio(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {
				String path = "smilebirdAudio" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = saveAudio(file, recordPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	private DentalChainFile saveAudio(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
		Double fileSizeInMB = 10.0;
		try {
			InputStream fis = file.getInputStream();
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentType());
			metadata.setContentType(file.getContentType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, file.getInputStream(), metadata));
			response.setAudioUrl(imagePath + recordPath);

		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<FollowupCommunicationResponse> getFollowupList(String smileBuddyId, List<String> treatmentId,
			String fromDate, String toDate, int size, int page, String searchTerm) {
		List<FollowupCommunicationResponse> response = null;
		try {
			Criteria criteria = new Criteria();
//
//			if (treatmentId != null && !treatmentId.isEmpty()) {
//				List<ObjectId> treatmentObjectIds = new ArrayList<ObjectId>();
//				for (String id : treatmentId) {
//					treatmentObjectIds.add(new ObjectId(id));
//				}
//				criteria.and("treatmentId").in(treatmentObjectIds);
//			}

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

			if (from != null && to != null)
				criteria.and("dateTime").gte(from).lte(to);
			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("memberId").is(new ObjectId(smileBuddyId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("userName").regex("^" + searchTerm, "i"),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}
			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("userName", new BasicDBObject("$first", "$userName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("memberName", new BasicDBObject("$first", "$communication.memberName"))
							.append("memberId", new BasicDBObject("$first", "$communication.memberId"))
							.append("dateTime", new BasicDBObject("$first", "$communication.dateTime"))
							.append("comment", new BasicDBObject("$first", "$communication.comment"))
							.append("followupType", new BasicDBObject("$first", "$communication.followupType"))
							.append("treatmentDone", new BasicDBObject("$first", "$communication.treatmentDone"))
							.append("priceIssue", new BasicDBObject("$first", "$communication.priceIssue"))
							.append("distanceIssue", new BasicDBObject("$first", "$communication.distanceIssue"))
							.append("isDropLead", new BasicDBObject("$first", "$communication.isDropLead"))
							.append("followupStatus", new BasicDBObject("$first", "$communication.followupStatus"))
							.append("quotedAmount", new BasicDBObject("$first", "$communication.quotedAmount"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("userName", "$userName").append("mobileNumber", "$mobileNumber")
								.append("city", "$city")
								.append("memberName",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.memberName", 0)))
								.append("memberId",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.memberId", 0)))
								.append("dateTime",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.dateTime", 0)))
								.append("comment",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.comment", 0)))
								.append("followupType",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.followupType", 0)))
								.append("treatmentDone",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.treatmentDone", 0)))
								.append("priceIssue",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.priceIssue", 0)))
								.append("distanceIssue",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.distanceIssue", 0)))
								.append("isDropLead",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.isDropLead", 0)))
								.append("followupStatus",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.followupStatus", 0)))
								.append("quotedAmount",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.quotedAmount", 0))))),
						Aggregation.sort(new Sort(Direction.DESC, "dateTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("userName", "$userName").append("mobileNumber", "$mobileNumber")
								.append("city", "$city")
								.append("comment",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.comment", 0)))
								.append("memberName",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.memberName", 0)))
								.append("memberId",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.memberId", 0)))
								.append("dateTime",
										new BasicDBObject("$arrayElemAt", Arrays.asList("$communication.dateTime", 0)))
								.append("followupType",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.followupType", 0)))
								.append("treatmentDone",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.treatmentDone", 0)))
								.append("priceIssue",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.priceIssue", 0)))
								.append("distanceIssue",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.distanceIssue", 0)))
								.append("isDropLead",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.isDropLead", 0)))
								.append("followupStatus",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.followupStatus", 0)))
								.append("quotedAmount",
										new BasicDBObject("$arrayElemAt",
												Arrays.asList("$communication.quotedAmount", 0))))),
						Aggregation.sort(new Sort(Direction.DESC, "dateTime")));
			}
			System.out.println("aggregation" + aggregation);
			response = mongoTemplate
					.aggregate(aggregation, "followUp_communication_details_cl", FollowupCommunicationResponse.class)
					.getMappedResults();
//			List<FollowupCommunicationResponse> modifiableList = new ArrayList<FollowupCommunicationResponse>(response);
//			Collections.sort(modifiableList, Comparator.comparing(FollowupCommunicationResponse::getDateTime));

		} catch (BusinessException e) {
			logger.error("Error while getting details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting details " + e.getMessage());

		}
		return response;
	}

}
