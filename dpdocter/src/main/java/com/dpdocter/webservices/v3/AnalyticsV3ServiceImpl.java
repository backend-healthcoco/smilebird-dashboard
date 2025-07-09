package com.dpdocter.webservices.v3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dpdocter.beans.CampaignObjectCollection;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.MonthlyData;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.CampNameCollection;
import com.dpdocter.collections.CampaignNameCollection;
import com.dpdocter.collections.DentalCampCollection;
import com.dpdocter.collections.DentalChainCityCollection;
import com.dpdocter.collections.DentalReasonsCollection;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.AnalyticType;
import com.dpdocter.enums.AppointmentByType;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentForType;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.CampaignRunningAt;
import com.dpdocter.enums.ContactState;
import com.dpdocter.enums.DentalAppointmentType;
import com.dpdocter.enums.LeadStage;
import com.dpdocter.enums.LeadType;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CampNameRepository;
import com.dpdocter.repository.DentalChainCityRepository;
import com.dpdocter.repository.DentalTreatmentDetailRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.AnalyticsDetailDataResponse;
import com.dpdocter.response.AppointmentByCountResponse;
import com.dpdocter.response.AppointmentForCountResponse;
import com.dpdocter.response.AppointmentStateCountResponse;
import com.dpdocter.response.AppointmentStatusCountResponse;
import com.dpdocter.response.AppointmentTypeCountResponse;
import com.dpdocter.response.AvgTimeCountResponse;
import com.dpdocter.response.BugdetCostCountResponse;
import com.dpdocter.response.CancelledByCountResponse;
import com.dpdocter.response.ClinicAppointmentCountResponse;
import com.dpdocter.response.ContactStateCountResponse;
import com.dpdocter.response.CreatedByCountResponse;
import com.dpdocter.response.CreatedFromCountResponse;
import com.dpdocter.response.FollowupCountResponse;
import com.dpdocter.response.FunnelLeadCountResponse;
import com.dpdocter.response.GenderCountResponse;
import com.dpdocter.response.LanguageCountResponse;
import com.dpdocter.response.LeadSourceCountResponse;
import com.dpdocter.response.LeadStageCountResponse;
import com.dpdocter.response.LeadTypeCountResponse;
import com.dpdocter.response.MonthlyAnalyticsResponse;
import com.dpdocter.response.NotInterestedLeadResponse;
import com.dpdocter.response.RescheduledByCountResponse;
import com.dpdocter.response.TreatmentCountResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class AnalyticsV3ServiceImpl implements AnalyticsV3Service {

	private static Logger logger = LogManager.getLogger(AnalyticsV3ServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DentalTreatmentDetailRepository dentalTreatmentDetailRepository;

	@Autowired
	private CampNameRepository campNameRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DentalChainCityRepository cityRepository;

	@Override
	public Response<Object> getLeadsAnalyticData(String fromDate, String toDate, String city, List<String> campaignId,
			String analyticType, String dentalStudioId) {
		Response<Object> response = new Response<Object>();
		DentalTreatmentDetailCollection dentalTreatmentDetailCollection = null;
		CampNameCollection campNameCollection = null;

		try {
			Criteria criteria = new Criteria();

			criteria.and("isDiscarded").is(false);
			List<String> citys = new ArrayList<String>();
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				DentalChainCityCollection cityCollection = cityRepository.findById(new ObjectId(city)).orElse(null);
				citys.add(cityCollection.getCity());
				criteria.and("city").in(citys);
			}

			List<ObjectId> campaignObjectIds = new ArrayList<ObjectId>();
			if (campaignId != null && !campaignId.isEmpty()) {
				for (String id : campaignId) {
					campaignObjectIds.add(new ObjectId(id));
				}
				criteria.and("campaignId").in(campaignObjectIds);
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalStudioId)) {
				criteria.and("dentalStudioId").is(new ObjectId(dentalStudioId));
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

			if (to != null && from != null) {
				criteria.and("registrationDate").gte(from).lte(to);
			}
			switch (AnalyticType.valueOf(analyticType)) {

			case LEADS_TYPE:
				LeadTypeCountResponse leadTypeCountResponse = new LeadTypeCountResponse();

				// Total HOT_LEAD
				Criteria criteria1 = new Criteria("leadType").is(LeadType.HOT_LEAD.getType());
				leadTypeCountResponse.setHot(getAnalyticCount(criteria, criteria1));

				// Total GENUINE leads
				Criteria criteria2 = new Criteria("leadType").is(LeadType.GENUINE.getType());
				leadTypeCountResponse.setGenuine(getAnalyticCount(criteria, criteria2));

				// Total INVALID leads
				Criteria criteria3 = new Criteria("leadType").is(LeadType.INVALID.getType());
				leadTypeCountResponse.setInvalid(getAnalyticCount(criteria, criteria3));

				// Total CONVERTED leads
				Criteria criteria4 = new Criteria("leadType").is(LeadType.CONVERTED.getType());
				leadTypeCountResponse.setConverted(getAnalyticCount(criteria, criteria4));
				response.setData(leadTypeCountResponse);

				// Total NOT_INTERESTED leads
				Criteria criteriaz = new Criteria("leadType").is(LeadType.NOT_INTERESTED.getType());
				leadTypeCountResponse.setNotInterested(getAnalyticCount(criteria, criteriaz));
				response.setData(leadTypeCountResponse);

				// Total nullvalues
				Criteria criteriay = new Criteria("leadType").is(null);
				leadTypeCountResponse.setNoValues(getAnalyticCount(criteria, criteriay));
				response.setData(leadTypeCountResponse);

				break;

			case LEAD_STAGE:
				LeadStageCountResponse leadStageCountResponse = new LeadStageCountResponse();

				// Total INITIAL_CALL leads
				Criteria criteria5 = new Criteria("leadStage").is(LeadStage.INITIAL_CALL.getType());
				leadStageCountResponse.setInitialCall(getAnalyticCount(criteria, criteria5));

				// Total CLINIC_VISIT leads
				Criteria criteria6 = new Criteria("leadStage").is(LeadStage.CLINIC_VISIT.getType());
				leadStageCountResponse.setClinicVisit(getAnalyticCount(criteria, criteria6));

				// Total CONVERTED leads
				Criteria criteria7 = new Criteria("leadStage").is(LeadStage.CONVERTED.getType());
				leadStageCountResponse.setConvertedStage(getAnalyticCount(criteria, criteria7));

				// Total nullvalues
				Criteria criteriax = new Criteria("leadStage").is(null);
				leadStageCountResponse.setNoValues(getAnalyticCount(criteria, criteriax));
				response.setData(leadStageCountResponse);

				break;

			case FOLLOWUP:
				FollowupCountResponse followupCountResponse = new FollowupCountResponse();

				// Total followUp leads
				Criteria criteria8 = new Criteria("followUp").gte(from).lte(to);
				followupCountResponse.setFollowup(getAnalyticCount(criteria, criteria8));
				response.setData(followupCountResponse);

				break;

			case GENDER:
				GenderCountResponse genderCountResponse = new GenderCountResponse();

				// Total Female leads
				Criteria criteria9 = new Criteria("gender").is("Female");
				genderCountResponse.setFemale(getAnalyticCount(criteria, criteria9));

				// Total Male leads
				Criteria criteriaa = new Criteria("gender").is("Male");
				genderCountResponse.setMale(getAnalyticCount(criteria, criteriaa));

				// Total Other leads
				Criteria criteriab = new Criteria("gender").is("Other");
				genderCountResponse.setOther(getAnalyticCount(criteria, criteriab));

				// Total nullvalues
				Criteria criteriaw = new Criteria("gender").is(null);
				genderCountResponse.setNoValues(getAnalyticCount(criteria, criteriaw));
				response.setData(genderCountResponse);

				break;
			case TREATMENT:
				TreatmentCountResponse treatmentCountResponse = new TreatmentCountResponse();

				// Total Braces leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Braces");
				Criteria criteriac = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setBraces(getAnalyticCount(criteria, criteriac));

				// Total Dental Implants leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Dental Implants");
				Criteria criteriad = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setImplants(getAnalyticCount(criteria, criteriad));

				// Total Root Canal leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Root Canal");
				Criteria criteriae = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setRootCanals(getAnalyticCount(criteria, criteriae));

				// Total Teeth Whitening leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Teeth Whitening");
				Criteria criteriaf = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setToothWhitening(getAnalyticCount(criteria, criteriaf));

				// Total crowns and bridges leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Crowns and Bridges");
				Criteria criteriag = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setCrowsAndBridges(getAnalyticCount(criteria, criteriag));

				// Total Preventive Dentistry leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Preventive Dentistry");
				Criteria criteriah = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setPreventiveDentistry(getAnalyticCount(criteria, criteriah));

				// Total Dental Filling leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Filling");
				Criteria criteriai = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalFilling(getAnalyticCount(criteria, criteriai));

				// Total Dental Veneers leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Veneers");
				Criteria criteriaj = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalVeneers(getAnalyticCount(criteria, criteriaj));

				// Total Wisdom Teeth leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Wisdom Teeth");
				Criteria criteriak = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setWisdomToothRemoval(getAnalyticCount(criteria, criteriak));

				// Total Kids Dentist leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Kids Dentist");
				Criteria criterial = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setKidsDentistry(getAnalyticCount(criteria, criterial));

				// Total Dentures leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dentures");
				Criteria criteriam = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentures(getAnalyticCount(criteria, criteriam));

				// Total SmileMakeover leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Smile makeovers");
				Criteria criterian = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setSmileMakeover(getAnalyticCount(criteria, criterian));

				// Total nullvalues
				Criteria criteriav = new Criteria("treatmentId").is(null);
				treatmentCountResponse.setNoValues(getAnalyticCount(criteria, criteriav));

				response.setData(treatmentCountResponse);

				break;
			case LEADS_SOURCE:
				LeadSourceCountResponse leadSourceCountResponse = new LeadSourceCountResponse();
				// Total Direct Call leads
				campNameCollection = campNameRepository.findByCampName("Direct Call");
				Criteria criteriao = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setDirectCall(getAnalyticCount(criteria, criteriao));

				// Total Google Ads leads
				campNameCollection = campNameRepository.findByCampName("Google Ads");
				Criteria criteriap = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setGoogleAds(getAnalyticCount(criteria, criteriap));

				// Total Website leads
				campNameCollection = campNameRepository.findByCampName("Website");
				Criteria criteriaq = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setWebsite(getAnalyticCount(criteria, criteriaq));

				// Total Instagram lead
				campNameCollection = campNameRepository.findByCampName("Instagram");
				System.out.println("campNameCollection" + campNameCollection.toString());
				Criteria criteriar = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setInstagram(getAnalyticCount(criteria, criteriar));

				// Total Facebook lead
				campNameCollection = campNameRepository.findByCampName("Facebook");
				Criteria criterias = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setFacebook(getAnalyticCount(criteria, criterias));

				// Total Whatsapp lead
				campNameCollection = campNameRepository.findByCampName("Whatsapp");
				Criteria criteriaT = new Criteria("campNameId").in(campNameCollection.getId());
				leadSourceCountResponse.setWhatsapp(getAnalyticCount(criteria, criteriaT));

				// Total nullvalues
				Criteria criteriau = new Criteria("campNameId").is(null);
				leadSourceCountResponse.setNoValues(getAnalyticCount(criteria, criteriau));

				response.setData(leadSourceCountResponse);

				break;

			case AVG_TIME:
				AvgTimeCountResponse avgTimeCountResponse = new AvgTimeCountResponse();
				// For Avaerage time

				List<DentalCampCollection> dentalCampResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "registrationDate")),
						DentalCampCollection.class, DentalCampCollection.class).getMappedResults();
				long sum = 0;
				for (DentalCampCollection dentalCampResponse : dentalCampResponses) {
					if (dentalCampResponse.getConvertedDate() != null
							&& dentalCampResponse.getRegistrationDate() != null) {
						long diff = dentalCampResponse.getConvertedDate().getTime()
								- dentalCampResponse.getRegistrationDate().getTime();
						long diffDays = (diff / (24 * 60 * 60 * 1000));
						sum += diffDays;
					}
				}
				long avg = 0;
				if (sum > 0)
					avg = (sum / dentalCampResponses.size());
				avgTimeCountResponse.setAvaerageTimeOfConversion(avg);
				response.setData(avgTimeCountResponse);

				break;
			case NOT_INTERESTED:
				List<NotInterestedLeadResponse> list = new ArrayList<NotInterestedLeadResponse>();
				LinkedHashMap<String, Integer> data = new LinkedHashMap<String, Integer>();
				// Total NOT_INTERESTED leads
				Criteria criteriaaa = new Criteria("leadType").is(LeadType.NOT_INTERESTED.getType());
				if (to != null && from != null) {
					criteriaaa.and("registrationDate").gte(from).lte(to);
				}
				criteriaaa.and("isDiscarded").is(false);
				List<DentalCampCollection> responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteriaaa),
								Aggregation.sort(Sort.Direction.DESC, "registrationDate")),
						DentalCampCollection.class, DentalCampCollection.class).getMappedResults();
				for (DentalCampCollection dentalCampCollection : responseList) {
					if (dentalCampCollection.getReasonIds() != null) {
						List<DentalReasonsCollection> reasonsCollections = mongoTemplate
								.aggregate(
										Aggregation.newAggregation(Aggregation
												.match(new Criteria("id").in(dentalCampCollection.getReasonIds()))),
										DentalReasonsCollection.class, DentalReasonsCollection.class)
								.getMappedResults();
						for (DentalReasonsCollection reasonsCollection : reasonsCollections) {

							if (data.containsKey(reasonsCollection.getReason())) {
								data.put(reasonsCollection.getReason(), data.get(reasonsCollection.getReason()) + 1);
							} else {
								data.put(reasonsCollection.getReason(), 1);
							}
						}
					}
				}
				if (data != null && !data.isEmpty()) {
					for (String string : data.keySet()) {
						Integer count = data.get(string);
						NotInterestedLeadResponse notInterestedLeadResponse = new NotInterestedLeadResponse();
						notInterestedLeadResponse.setCount(count);
						notInterestedLeadResponse.setType(string);

						list.add(notInterestedLeadResponse);
					}
				}
				response.setData(data);
				break;
			case DENTAL_CLINICS:
				LinkedHashMap<String, Integer> data1 = new LinkedHashMap<String, Integer>();
				Criteria criteriaab = new Criteria();
				if (to != null && from != null) {
					criteriaab.and("registrationDate").gte(from).lte(to);
				}
				criteriaab.and("isDiscarded").is(false);
				List<DentalCampCollection> responseList1 = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteriaab),
								Aggregation.sort(Sort.Direction.DESC, "registrationDate")),
						DentalCampCollection.class, DentalCampCollection.class).getMappedResults();
				for (DentalCampCollection dentalCampCollection : responseList1) {
					if (dentalCampCollection.getDentalStudioId() != null) {
						LocationCollection locationCollection = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation
										.match(new Criteria("id").is(dentalCampCollection.getDentalStudioId()))),
								LocationCollection.class, LocationCollection.class).getUniqueMappedResult();
						if (locationCollection != null) {
							if (data1.containsKey(locationCollection.getLocationName())) {
								data1.put(locationCollection.getLocationName(),
										data1.get(locationCollection.getLocationName()) + 1);
							} else {
								data1.put(locationCollection.getLocationName(), 1);
							}
						}
					}
				}
				response.setData(data1);
				break;
			case FUNNEL:
				FunnelLeadCountResponse funnelLeadCountResponse = new FunnelLeadCountResponse();
				// Total HOT_LEAD
				Criteria criteriaaac = new Criteria("leadType").is(LeadType.HOT_LEAD.getType());
				funnelLeadCountResponse.setHot(getAnalyticCount(criteria, criteriaaac));

				// Total GENUINE leads
				Criteria criteriaaad = new Criteria("leadType").is(LeadType.GENUINE.getType());
				funnelLeadCountResponse.setGenuine(getAnalyticCount(criteria, criteriaaad));

				// Total Total leads
				funnelLeadCountResponse.setTotalLeads(getAnalyticCount(criteria, null));

				// Total CONVERTED leads
				Criteria criteriaaaf = new Criteria("leadType").is(LeadType.CONVERTED.getType());
				funnelLeadCountResponse.setConverted(getAnalyticCount(criteria, criteriaaaf));

				// Total CLINIC_VISIT leads
				Criteria criteriaaag = new Criteria("leadStage").is(LeadStage.CLINIC_VISIT.getType());
				funnelLeadCountResponse.setClinicVisit(getAnalyticCount(criteria, criteriaaag));

				response.setData(funnelLeadCountResponse);

				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting leads analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting leads analytic");
		}
		return response;
	}

	private Integer getAnalyticCount(Criteria criteria, Criteria criteria1) {
		if (criteria1 != null)
			return (int) mongoTemplate.count(new Query(criteria1).addCriteria(criteria), DentalCampCollection.class);
		else
			return (int) mongoTemplate.count(new Query(criteria), DentalCampCollection.class);
	}

	@Override
	public Response<Object> getCampaignAnalyticData(String fromDate, String toDate, String city,
			List<String> campaignId, String analyticType) {
		Response<Object> response = new Response<Object>();

		try {
			Criteria criteria = new Criteria();

			criteria.and("isDiscarded").is(false);
			List<ObjectId> cityIds = new ArrayList<ObjectId>();
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				cityIds.add(new ObjectId(city));
				criteria.and("city").in(cityIds);
			}

			List<ObjectId> campaignObjectIds = new ArrayList<ObjectId>();
			if (campaignId != null && !campaignId.isEmpty()) {
				for (String id : campaignId) {
					campaignObjectIds.add(new ObjectId(id));
				}
				criteria.and("campaignId").in(campaignObjectIds);
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

			if (to != null && from != null) {
				criteria.and("registrationDate").gte(from).lte(to);
			}
			switch (AnalyticType.valueOf(analyticType)) {
			case COST:
				BugdetCostCountResponse bugdetCostCountResponse = new BugdetCostCountResponse();

				// Total Total Campaign Cost
				Criteria criteriau = new Criteria();
				criteriau.and("isDiscarded").is(false);

				if (!DPDoctorUtils.anyStringEmpty(city)) {
					criteriau.and("city").in(cityIds);
				}

				if (from != null && to != null) {

					criteriau.and("fromDate").gte(from).lte(to);
				}

				if (campaignId != null && !campaignId.isEmpty()) {
					criteriau.and("id").in(campaignObjectIds);
				}

				List<CampaignNameCollection> campaignNameCollections = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteriau),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampaignNameCollection.class, CampaignNameCollection.class).getMappedResults();
				System.out.println("Result" + Aggregation.newAggregation(Aggregation.match(criteriau),
						Aggregation.sort(Sort.Direction.DESC, "createdTime")));
				System.out.println("count" + campaignNameCollections.size());

				long googleSum = 0;
				long metaSum = 0;
				long youtubeSum = 0;

				for (CampaignNameCollection campaignNameCollection : campaignNameCollections) {
					switch (CampaignRunningAt.valueOf(campaignNameCollection.getCampaignRunningAt().getType())) {
					case GOOGLE_ADS:
						Criteria criteriav = new Criteria();
						if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
							criteriav.and("campaignId").is(campaignNameCollection.getId());
						}
						List<CampaignObjectCollection> googleResponses = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriav), Aggregation.match(criteriav),
										Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
						System.out.println("Result" + Aggregation.newAggregation(Aggregation.match(criteriau),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")));
						for (CampaignObjectCollection objectCollection : googleResponses) {
							if (objectCollection.getAmountSpend() != null)
								googleSum += objectCollection.getAmountSpend();
						}
						bugdetCostCountResponse.setGoogle(googleSum);
						break;
					case YOUTUBE:
						Criteria criteriaw = new Criteria();
						if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
							criteriaw.and("campaignId").is(campaignNameCollection.getId());
						}
						List<CampaignObjectCollection> youtubetResponses = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaw), Aggregation.match(criteriaw),
										Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
						for (CampaignObjectCollection objectCollection : youtubetResponses) {
							if (objectCollection.getAmountSpend() != null)
								youtubeSum += objectCollection.getAmountSpend();
						}
						bugdetCostCountResponse.setYoutube(youtubeSum);
						break;
					case META:
						Criteria criteriax = new Criteria();
						if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
							criteriax.and("campaignId").is(campaignNameCollection.getId());
						}
						List<CampaignObjectCollection> metaResponses = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriax), Aggregation.match(criteriax),
										Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
						for (CampaignObjectCollection objectCollection : metaResponses) {
							if (objectCollection.getAmountSpend() != null)
								metaSum += objectCollection.getAmountSpend();
						}
						bugdetCostCountResponse.setMeta(metaSum);
						break;
					default:
						break;
					}
				}
//				bugdetCostCountResponse.setTotal(googleSum + youtubeSum + metaSum);
				response.setData(bugdetCostCountResponse);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting leads analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting leads analytic");
		}
		return response;
	}

	@Override
	public Response<Object> getPatientAnalyticData(String fromDate, String toDate, String city, String smileBuddyId,
			String analyticType) {
		Response<Object> response = new Response<Object>();
		DentalTreatmentDetailCollection dentalTreatmentDetailCollection = null;

		try {
			Criteria criteria = new Criteria();

			criteria.and("isDentalChainPatient").is(true);

			List<ObjectId> cityIds = new ArrayList<ObjectId>();
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				cityIds.add(new ObjectId(city));
				criteria.and("city").in(cityIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("smileBuddyId").is(new ObjectId(smileBuddyId));

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

			if (to != null && from != null) {
				criteria.and("createdTime").gte(from).lte(to);
			}

			switch (AnalyticType.valueOf(analyticType)) {

			case LANGUAGE:
				LanguageCountResponse languageCountResponse = new LanguageCountResponse();

				// Total English
				Criteria criteria1 = new Criteria("language").is("English");
				languageCountResponse.setEnglish(getPatientAnalyticCount(criteria, criteria1));

				// Total Hindi
				Criteria criteria2 = new Criteria();
				criteria2.and("language").is("Hindi");
				languageCountResponse.setHindi(getPatientAnalyticCount(criteria, criteria2));

				// Total Marathi
				Criteria criteria3 = new Criteria("language").is("Marathi");
				languageCountResponse.setMarathi(getPatientAnalyticCount(criteria, criteria3));

				// Total Hinglish
				Criteria criteria4 = new Criteria("language").is("Hinglish");
				languageCountResponse.setHinglish(getPatientAnalyticCount(criteria, criteria4));

				// Total nullvalues
				Criteria criteriao = new Criteria("language").is(null);
				languageCountResponse.setNoValues(getPatientAnalyticCount(criteria, criteriao));

				response.setData(languageCountResponse);

				break;
			case CREATED_FROM:
				CreatedFromCountResponse createdFromCountResponse = new CreatedFromCountResponse();

				// Total Marathi
				Criteria criteria5 = new Criteria("createdFrom").is("ADMIN");
				createdFromCountResponse.setCreatedFromAdmin(getPatientAnalyticCount(criteria, criteria5));

				// Total Hinglish
				Criteria criteria6 = new Criteria("createdFrom").is("WEBSITE");
				createdFromCountResponse.setCreatedFromWebsite(getPatientAnalyticCount(criteria, criteria6));

				// Total nullvalues
				Criteria criteriap = new Criteria("createdFrom").is(null);
				createdFromCountResponse.setNoValues(getPatientAnalyticCount(criteria, criteriap));

				response.setData(createdFromCountResponse);

				break;
			case CONTACT_STATE:
				ContactStateCountResponse contactStateCountResponse = new ContactStateCountResponse();

				// Total FIRSTCALL
				Criteria criteria7 = new Criteria("contactState").is(ContactState.FIRSTCALL.getState());
				contactStateCountResponse.setFirstCall(getPatientAnalyticCount(criteria, criteria7));

				// Total SECONDCALL
				Criteria criteriaq = new Criteria("contactState").is(ContactState.SECONDCALL.getState());
				contactStateCountResponse.setSecondCall(getPatientAnalyticCount(criteria, criteriaq));

				// Total THIRDCALL
				Criteria criterir = new Criteria("contactState").is(ContactState.THIRDCALL.getState());
				contactStateCountResponse.setThirdCall(getPatientAnalyticCount(criteria, criterir));

				// Total FIRSTAPPOINTMENT
				Criteria criterias = new Criteria("contactState").is(ContactState.FIRSTAPPOINTMENT.getState());
				contactStateCountResponse.setFirstAppointment(getPatientAnalyticCount(criteria, criterias));

				// Total nullvalues
				Criteria criteriat = new Criteria("contactState").is(null);
				contactStateCountResponse.setNoValues(getPatientAnalyticCount(criteria, criteriat));

				response.setData(contactStateCountResponse);

				break;
			case FOLLOWUP:
				FollowupCountResponse followupCountResponse = new FollowupCountResponse();

				// Total followUp leads
				Criteria criteria8 = new Criteria("followUp").gte(from).lte(to);
				followupCountResponse.setFollowup(getPatientAnalyticCount(criteria, criteria8));
				response.setData(followupCountResponse);
				break;
			case GENDER:
				GenderCountResponse genderCountResponse = new GenderCountResponse();

				// Total Female leads
				Criteria criteria9 = new Criteria("gender").is("Female");
				genderCountResponse.setFemale(getPatientAnalyticCount(criteria, criteria9));

				// Total Male leads
				Criteria criteriaa = new Criteria("gender").is("Male");
				genderCountResponse.setMale(getPatientAnalyticCount(criteria, criteriaa));

				// Total Other leads
				Criteria criteriab = new Criteria("gender").is("Other");
				genderCountResponse.setOther(getPatientAnalyticCount(criteria, criteriab));

				// Total nullvalues
				Criteria criteriau = new Criteria("gender").is(null);
				genderCountResponse.setNoValues(getPatientAnalyticCount(criteria, criteriau));

				response.setData(genderCountResponse);

				break;

			case TREATMENT:

				TreatmentCountResponse treatmentCountResponse = new TreatmentCountResponse();

				// Total Braces leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Braces");
				Criteria criteriac = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setBraces(getPatientAnalyticCount(criteria, criteriac));

				// Total Dental Implants leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Dental Implants");
				Criteria criteriad = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setImplants(getPatientAnalyticCount(criteria, criteriad));

				// Total Root Canal leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Root Canal");
				Criteria criteriae = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setRootCanals(getPatientAnalyticCount(criteria, criteriae));

				// Total Teeth Whitening leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Teeth Whitening");
				Criteria criteriaf = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setToothWhitening(getPatientAnalyticCount(criteria, criteriaf));

				// Total crowns and bridges leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Crowns and Bridges");
				Criteria criteriag = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setCrowsAndBridges(getPatientAnalyticCount(criteria, criteriag));

				// Total Preventive Dentistry leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Preventive Dentistry");
				Criteria criteriah = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setPreventiveDentistry(getPatientAnalyticCount(criteria, criteriah));

				// Total Dental Filling leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Filling");
				Criteria criteriai = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalFilling(getPatientAnalyticCount(criteria, criteriai));

				// Total Dental Veneers leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Veneers");
				Criteria criteriaj = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalVeneers(getPatientAnalyticCount(criteria, criteriaj));

				// Total Wisdom Teeth leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Wisdom Teeth");
				Criteria criteriak = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setWisdomToothRemoval(getPatientAnalyticCount(criteria, criteriak));

				// Total Kids Dentist leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Kids Dentist");
				Criteria criterial = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setKidsDentistry(getPatientAnalyticCount(criteria, criterial));

				// Total Dentures leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dentures");
				Criteria criteriam = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentures(getPatientAnalyticCount(criteria, criteriam));

				// Total SmileMakeover leads
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Smile makeovers");
				Criteria criterian = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setSmileMakeover(getPatientAnalyticCount(criteria, criterian));

				// Total nullvalues
				Criteria criteriav = new Criteria("treatmentId").is(null);
				treatmentCountResponse.setNoValues(getPatientAnalyticCount(criteria, criteriav));

				response.setData(treatmentCountResponse);

				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting leads analytic");
		}
		return response;
	}

	private Integer getPatientAnalyticCount(Criteria criteria1, Criteria criteria) {
		return (int) mongoTemplate.count(new Query(criteria1).addCriteria(criteria), UserCollection.class);
	}

	@Override
	public Response<Object> getAppointmentAnalyticData(String fromDate, String toDate, String locationId,
			String doctorId, String smileBuddyId, String analyticType) {
		Response<Object> response = new Response<Object>();
		DentalTreatmentDetailCollection dentalTreatmentDetailCollection = null;

		try {
			Criteria criteria = new Criteria();

			criteria.and("isDentalChainAppointment").is(true);

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(smileBuddyId))
				criteria.and("smileBuddyId").is(new ObjectId(smileBuddyId));

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

			if (to != null && from != null) {
				criteria.and("createdTime").gte(from).lte(to);
			}
			switch (AnalyticType.valueOf(analyticType)) {
			case APPOINTMENT_TYPE:
				AppointmentTypeCountResponse appointmentTypeCountResponse = new AppointmentTypeCountResponse();

				// Total ONLINE
				Criteria criteria1 = new Criteria("dentalAppointmentType").is(DentalAppointmentType.ONLINE.getType());
				appointmentTypeCountResponse.setOnline(getAppointmentAnalyticCount(criteria, criteria1));

				// Total OFFLINE
				Criteria criteria2 = new Criteria("dentalAppointmentType").is(DentalAppointmentType.OFFLINE.getType());
				appointmentTypeCountResponse.setOffline(getAppointmentAnalyticCount(criteria, criteria2));

				// Total nullvalues
				Criteria criteriau = new Criteria("dentalAppointmentType").is(null);
				appointmentTypeCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriau));

				response.setData(appointmentTypeCountResponse);

				break;
			case FOLLOWUP:
				FollowupCountResponse followupCountResponse = new FollowupCountResponse();

				// Total followUp
				Criteria criteria3 = new Criteria("followUp").gte(from).lte(to);
				followupCountResponse.setFollowup(getAppointmentAnalyticCount(criteria, criteria3));
				response.setData(followupCountResponse);

				break;
			case TREATMENT:
				TreatmentCountResponse treatmentCountResponse = new TreatmentCountResponse();

				// Total Braces
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Braces");
				Criteria criteria4 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setBraces(getAppointmentAnalyticCount(criteria, criteria4));

				// Total crowns and bridges
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Crowns and Bridges");
				Criteria criteria5 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setCrowsAndBridges(getAppointmentAnalyticCount(criteria, criteria5));

				// Total Dental Implants
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Dental Implants");
				Criteria criteria6 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setImplants(getAppointmentAnalyticCount(criteria, criteria6));

				// Total Kids Dentist
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Kids Dentist");
				Criteria criteria7 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setKidsDentistry(getAppointmentAnalyticCount(criteria, criteria7));

				// Total Dentures
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dentures");
				Criteria criteria8 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentures(getAppointmentAnalyticCount(criteria, criteria8));

				// Total Root Canal
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Root Canal");
				Criteria criteria9 = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setRootCanals(getAppointmentAnalyticCount(criteria, criteria9));

				// Total Teeth Whitening
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Teeth Whitening");
				Criteria criteriaa = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setToothWhitening(getAppointmentAnalyticCount(criteria, criteriaa));

				// Total Dental Filling
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Filling");
				Criteria criteriab = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalFilling(getAppointmentAnalyticCount(criteria, criteriab));

				// Total Preventive Dentistry
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Preventive Dentistry");
				Criteria criteriac = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setPreventiveDentistry(getAppointmentAnalyticCount(criteria, criteriac));

				// Total Dental Veneers
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Dental Veneers");
				Criteria criteriad = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setDentalVeneers(getAppointmentAnalyticCount(criteria, criteriad));

				// Total Wisdom Teeth
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findByTreatmentName("Wisdom Teeth");
				Criteria criteriae = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setWisdomToothRemoval(getAppointmentAnalyticCount(criteria, criteriae));

				// Total setSmileMakeover
				dentalTreatmentDetailCollection = dentalTreatmentDetailRepository
						.findByTreatmentName("Smile makeovers");
				Criteria criteriaf = new Criteria("treatmentId").in(dentalTreatmentDetailCollection.getId());
				treatmentCountResponse.setSmileMakeover(getAppointmentAnalyticCount(criteria, criteriaf));

				// Total nullvalues
				Criteria criteriav = new Criteria("treatmentId").is(null);
				treatmentCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriav));

				response.setData(treatmentCountResponse);

				break;

			case APPOINTMENT_FOR:
				AppointmentForCountResponse appointmentForCountResponse = new AppointmentForCountResponse();

				// Total SELF
				Criteria criteriag = new Criteria("appointmentFor").is(AppointmentForType.SELF.getType());
				appointmentForCountResponse.setSelf(getAppointmentAnalyticCount(criteria, criteriag));

				// Total OTHER
				Criteria criteriah = new Criteria("appointmentFor").is(AppointmentForType.OTHER.getType());
				appointmentForCountResponse.setOther(getAppointmentAnalyticCount(criteria, criteriah));

				// Total nullvalues
				Criteria criteriaw = new Criteria("appointmentFor").is(null);
				appointmentForCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriaw));

				response.setData(appointmentForCountResponse);

				break;
			case APPOINTMENT_BY:
				AppointmentByCountResponse appointmentByCountResponse = new AppointmentByCountResponse();

				// Total CALL
				Criteria criteriai = new Criteria("appointmentBy").is(AppointmentByType.CALL.getType());
				appointmentByCountResponse.setCall(getAppointmentAnalyticCount(criteria, criteriai));

				// Total WEBSITE
				Criteria criteriaj = new Criteria("appointmentBy").is(AppointmentByType.WEBSITE.getType());
				appointmentByCountResponse.setWebsite(getAppointmentAnalyticCount(criteria, criteriaj));

				// Total nullvalues
				Criteria criteriax = new Criteria("appointmentBy").is(null);
				appointmentByCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriax));

				response.setData(appointmentByCountResponse);

				break;
			case APPOINTMENT_STATE:
				AppointmentStateCountResponse appointmentStateCountResponse = new AppointmentStateCountResponse();

				// Total CONFIRM
				Criteria criteriak = new Criteria("state").is(AppointmentState.CONFIRM.getState());
				appointmentStateCountResponse.setConfirm(getAppointmentAnalyticCount(criteria, criteriak));

				// Total PENDING
				Criteria criterial = new Criteria("state").is(AppointmentState.PENDING.getState());
				appointmentStateCountResponse.setPending(getAppointmentAnalyticCount(criteria, criterial));

				// Total CANCEL
				Criteria criteriam = new Criteria("state").is(AppointmentState.CANCEL.getState());
				appointmentStateCountResponse.setCancel(getAppointmentAnalyticCount(criteria, criteriam));

				// Total RESCHEDULE
				Criteria criterian = new Criteria("state").is(AppointmentState.RESCHEDULE.getState());
				appointmentStateCountResponse.setReschedule(getAppointmentAnalyticCount(criteria, criterian));

				// Total nullvalues
				Criteria criteriay = new Criteria("state").is(null);
				appointmentStateCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriay));

				response.setData(appointmentStateCountResponse);

				break;
			case CANCELLED_BY:

				CancelledByCountResponse cancelledByCountResponse = new CancelledByCountResponse();

				// Total cancelledBy PATIENT
				Criteria criteriao = new Criteria("cancelledByProfile").is(AppointmentCreatedBy.PATIENT.getType());
				cancelledByCountResponse.setCancelledByPatient(getAppointmentAnalyticCount(criteria, criteriao));

				// Total cancelledBy DOCTOR
				Criteria criteriap = new Criteria("cancelledByProfile").is(AppointmentCreatedBy.DOCTOR.getType());
				cancelledByCountResponse.setCancelledByDoctor(getAppointmentAnalyticCount(criteria, criteriap));

				// Total nullvalues
				Criteria criteriaz = new Criteria("cancelledByProfile").is(null);
				cancelledByCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriaz));

				response.setData(cancelledByCountResponse);

				break;

			case RESCHEDULED_BY:
				RescheduledByCountResponse rescheduledByCountResponse = new RescheduledByCountResponse();

				// Total rescheduleBy PATIENT
				Criteria criteriaq = new Criteria("rescheduleByProfile").is(AppointmentCreatedBy.PATIENT.getType());
				rescheduledByCountResponse.setRescheduleByPatient(getAppointmentAnalyticCount(criteria, criteriaq));

				// Total rescheduleBy DOCTOR
				Criteria criteriar = new Criteria("rescheduleByProfile").is(AppointmentCreatedBy.DOCTOR.getType());
				rescheduledByCountResponse.setRescheduleByDoctor(getAppointmentAnalyticCount(criteria, criteriar));

				// Total nullvalues
				Criteria criteriaaa = new Criteria("rescheduleByProfile").is(null);
				rescheduledByCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriaaa));

				response.setData(rescheduledByCountResponse);

				break;
			case CREATED_BY:
				CreatedByCountResponse createdByCountResponse = new CreatedByCountResponse();

				// Total isCreatedByPatient
				Criteria criterias = new Criteria("isCreatedByPatient").is(true);
				createdByCountResponse.setCreatedByPatient(getAppointmentAnalyticCount(criteria, criterias));

				// Total nullvalues
				Criteria criteriaab = new Criteria("isCreatedByPatient").is(null);
				createdByCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriaab));

				response.setData(createdByCountResponse);

				break;
			case APPOINTMENT_STATUS:
				AppointmentStatusCountResponse appointmentStatusCountResponse = new AppointmentStatusCountResponse();

				// Total CONFIRM
				Criteria criteriat = new Criteria("status").is(QueueStatus.SCHEDULED);
				appointmentStatusCountResponse.setScheduled(getAppointmentAnalyticCount(criteria, criteriat));

				// Total PENDING
				Criteria criteriaac = new Criteria("status").is(QueueStatus.CHECKED_OUT);
				appointmentStatusCountResponse.setCheckOut(getAppointmentAnalyticCount(criteria, criteriaac));

				// Total CANCEL
				Criteria criteriaad = new Criteria("status").is(QueueStatus.NO_SHOW);
				appointmentStatusCountResponse.setNoShow(getAppointmentAnalyticCount(criteria, criteriaad));

				// Total nullvalues
				Criteria criteriaae = new Criteria("status").is(null);
				appointmentStatusCountResponse.setNoValues(getAppointmentAnalyticCount(criteria, criteriaae));
				response.setData(appointmentStatusCountResponse);
				break;
			case CLINIC:
				LinkedHashMap<String, Integer> data = new LinkedHashMap<String, Integer>();

				List<ClinicAppointmentCountResponse> clinicAppointmentCountResponse = new ArrayList<ClinicAppointmentCountResponse>();

				Aggregation aggregation = null;

				Criteria criteriaaf = new Criteria();

				criteriaaf.and("isDentalChain").is(true);

				criteriaaf.and("isClinic").is(true);

				aggregation = Aggregation.newAggregation(Aggregation.match(criteriaaf),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

				List<LocationCollection> results = mongoTemplate
						.aggregate(aggregation, LocationCollection.class, LocationCollection.class).getMappedResults();
				System.out.println("aggregation" + aggregation);
				for (LocationCollection locationCollection : results) {

					Criteria criteriaag = new Criteria();
					criteriaag.and("isDentalChainAppointment").is(true);

					criteriaag.and("locationId").is(locationCollection.getId());

					if (to != null && from != null) {
						criteriaag.and("createdTime").gte(from).lte(to);
					}
					int appointment = (int) mongoTemplate.count(new Query(criteriaag), AppointmentCollection.class);
					System.out.println("appointment " + appointment);

					if (data.containsKey(locationCollection.getLocationName())) {
						data.put(locationCollection.getLocationName(), appointment + 1);
					} else
						data.put(locationCollection.getLocationName(), appointment);
					System.out.println("data " + data.size());

				}

				for (String string : data.keySet()) {
					Integer count = data.get(string);
					ClinicAppointmentCountResponse notInterestedLeadResponse = new ClinicAppointmentCountResponse();
					notInterestedLeadResponse.setCount(count);
					notInterestedLeadResponse.setName(string);
					System.out.println("string " + string);

					clinicAppointmentCountResponse.add(notInterestedLeadResponse);
				}

				response.setDataList(clinicAppointmentCountResponse);
				break;
			case DOCTOR:
				LinkedHashMap<String, Integer> data1 = new LinkedHashMap<String, Integer>();

				List<ClinicAppointmentCountResponse> doctorAppointmentCountResponse = new ArrayList<ClinicAppointmentCountResponse>();
				List<Doctor> doctors = new ArrayList<Doctor>();

				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationIdAndIsActivate(new ObjectId(locationId), true);
				System.out.println("doctorClinicProfileCollections" + doctorClinicProfileCollections.size());

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
						doctors.add(doctor);
					}
				}

				for (Doctor doctor : doctors) {

					Criteria criteriaah = new Criteria();
					criteriaah.and("isDentalChainAppointment").is(true);

					criteriaah.and("doctorId").is(new ObjectId(doctor.getId()));

					if (to != null && from != null) {
						criteriaah.and("createdTime").gte(from).lte(to);
					}
					int appointment = (int) mongoTemplate.count(new Query(criteriaah), AppointmentCollection.class);

					if (data1.containsKey(doctor.getFirstName())) {
						data1.put(doctor.getFirstName(), appointment + 1);
					} else
						data1.put(doctor.getFirstName(), appointment);
					System.out.println("data1 " + data1.size());

				}

				for (String string : data1.keySet()) {
					Integer count = data1.get(string);
					ClinicAppointmentCountResponse doctorAppointmentResponse = new ClinicAppointmentCountResponse();
					doctorAppointmentResponse.setCount(count);
					doctorAppointmentResponse.setName(string);

					doctorAppointmentCountResponse.add(doctorAppointmentResponse);
				}

				response.setDataList(doctorAppointmentCountResponse);
				break;
			default:
				break;

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting leads analytic");
		}
		return response;
	}

	private Integer getAppointmentAnalyticCount(Criteria criteria, Criteria criteria1) {
		if (criteria1 != null)
			return (int) mongoTemplate.count(new Query(criteria1).addCriteria(criteria), AppointmentCollection.class);
		else
			return (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
	}

	@Override
	public Response<Object> getAnalyticDetaileData(String fromDate, String toDate, String city,
			List<String> campaignId) {
		Response<Object> response = new Response<Object>();
		AnalyticsDetailDataResponse analyticsDetailDataResponse = new AnalyticsDetailDataResponse();

		try {
			Criteria criteriau = new Criteria();
			criteriau.and("isDiscarded").is(false);

			List<ObjectId> campaignObjectIds = new ArrayList<ObjectId>();
			if (campaignId != null && !campaignId.isEmpty()) {
				for (String id : campaignId) {
					campaignObjectIds.add(new ObjectId(id));
				}
				criteriau.and("campaignId").in(campaignObjectIds);
			}

			List<ObjectId> cityIds = new ArrayList<ObjectId>();
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				cityIds.add(new ObjectId(city));
				criteriau.and("city").in(cityIds);
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

			if (from != null) {
				criteriau.and("fromDate").gte(from).lte(to);
			}
//
//			if (to != null) {
//				criteriau.and("toDate").lte(to);
//			}

			// Total Total Campaign Cost
			List<CampaignNameCollection> campaignNameCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteriau),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					CampaignNameCollection.class, CampaignNameCollection.class).getMappedResults();
			System.out.println("criteriau" + Aggregation.newAggregation(Aggregation.match(criteriau),
					Aggregation.sort(Sort.Direction.DESC, "createdTime")));
			long googleSum = 0;
			long metaSum = 0;
			long youtubeSum = 0;
			long frequency = 0;
			long reach = 0;
			long clickThroughRation = 0;
			long avgCostPerClicks = 0;

			for (CampaignNameCollection campaignNameCollection : campaignNameCollections) {
				Criteria criteriat = new Criteria();
				if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
					criteriat.and("campaignId").is(campaignNameCollection.getId());
				}
				List<CampaignObjectCollection> objectCollections = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteriat),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
				for (CampaignObjectCollection objectCollection : objectCollections) {
					if (objectCollection.getFrequency() != null)
						frequency += objectCollection.getFrequency();
					if (objectCollection.getReach() != null)
						reach += objectCollection.getReach();
					if (objectCollection.getCostPerLinkClick() != null)
						clickThroughRation += objectCollection.getCostPerLinkClick();
					if (objectCollection.getAvgCostPerClicks() != null)
						avgCostPerClicks += objectCollection.getAvgCostPerClicks();
				}

				switch (CampaignRunningAt.valueOf(campaignNameCollection.getCampaignRunningAt().getType())) {
				case GOOGLE_ADS:
					Criteria criteriav = new Criteria();
					if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
						criteriav.and("campaignId").is(campaignNameCollection.getId());
					}
					List<CampaignObjectCollection> googleResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteriav), Aggregation.match(criteriav),
									Aggregation.sort(Sort.Direction.DESC, "createdTime")),
							CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
					System.out.println("Result" + Aggregation.newAggregation(Aggregation.match(criteriau),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")));
					for (CampaignObjectCollection objectCollection : googleResponses) {
						if (objectCollection.getAmountSpend() != null)
							googleSum += objectCollection.getAmountSpend();
					}
					break;
				case YOUTUBE:
					Criteria criteriaw = new Criteria();
					if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
						criteriaw.and("campaignId").is(campaignNameCollection.getId());
					}
					List<CampaignObjectCollection> youtubetResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteriaw), Aggregation.match(criteriaw),
									Aggregation.sort(Sort.Direction.DESC, "createdTime")),
							CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
					for (CampaignObjectCollection objectCollection : youtubetResponses) {
						if (objectCollection.getAmountSpend() != null)
							youtubeSum += objectCollection.getAmountSpend();
					}
					break;
				case META:
					Criteria criteriax = new Criteria();
					if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
						criteriax.and("campaignId").is(campaignNameCollection.getId());
					}
					List<CampaignObjectCollection> metaResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteriax), Aggregation.match(criteriax),
									Aggregation.sort(Sort.Direction.DESC, "createdTime")),
							CampaignObjectCollection.class, CampaignObjectCollection.class).getMappedResults();
					for (CampaignObjectCollection objectCollection : metaResponses) {
						if (objectCollection.getAmountSpend() != null)
							metaSum += objectCollection.getAmountSpend();
					}
					break;
				default:
					break;
				}
			}
			analyticsDetailDataResponse.setAmaountSpend(googleSum + youtubeSum + metaSum);

			// Total Lead
			Criteria criteria = new Criteria();
			criteria.and("isDiscarded").is(false);

			if (campaignId != null && !campaignId.isEmpty()) {
				criteria.and("campaignId").in(campaignObjectIds);
			}
			if (to != null && from != null) {
				criteria.and("registrationDate").gte(from).lte(to);
			}
			List<String> citys = new ArrayList<String>();
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				DentalChainCityCollection cityCollection = cityRepository.findById(new ObjectId(city)).orElse(null);
				citys.add(cityCollection.getCity());
				criteria.and("city").in(citys);
			}

			analyticsDetailDataResponse
					.setTotalLead(mongoTemplate.count(new Query(criteria), DentalCampCollection.class));

			// Gnuiene lead

			Criteria criteria2 = new Criteria("leadType").is(LeadType.GENUINE.getType());
			analyticsDetailDataResponse.setGenuineLead(getAnalyticCount(criteria, criteria2));

			// total appointment
			Criteria criteria3 = new Criteria();

			criteria3.and("isDentalChainAppointment").is(true);

			if (to != null && from != null) {
				criteria3.and("createdTime").gte(from).lte(to);
			}

			analyticsDetailDataResponse
					.setAppointment(mongoTemplate.count(new Query(criteria3), AppointmentCollection.class));

			// total AvgConversionOfTime
			Criteria criteri4 = new Criteria();

			criteri4.and("isDiscarded").is(false);

			if (to != null && from != null) {
				criteri4.and("createdTime").gte(from).lte(to);
			}

			List<DentalCampCollection> dentalCampResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteri4),
							Aggregation.sort(Sort.Direction.DESC, "registrationDate")),
					DentalCampCollection.class, DentalCampCollection.class).getMappedResults();
			long sum = 0;
			for (DentalCampCollection dentalCampResponse : dentalCampResponses) {
				if (dentalCampResponse.getConvertedDate() != null && dentalCampResponse.getRegistrationDate() != null) {
					long diff = dentalCampResponse.getConvertedDate().getTime()
							- dentalCampResponse.getRegistrationDate().getTime();
					long diffDays = (diff / (24 * 60 * 60 * 1000));
					sum += diffDays;
				}
			}
			long avg = 0;
			if (sum > 0)
				avg = (sum / dentalCampResponses.size());

			analyticsDetailDataResponse.setAvgConversionOfTime(avg);

			// total frequency

			analyticsDetailDataResponse.setFrequency(frequency);

			// total reach

			analyticsDetailDataResponse.setReach(reach);

			// total clickThroughRation

			analyticsDetailDataResponse.setClickThroughRation(clickThroughRation);

			// total avgCostPerClicks

			analyticsDetailDataResponse.setCostPerClick(avgCostPerClicks);

			// total costOfConversation
			long totalSpend = googleSum + youtubeSum + metaSum;

			// Total CONVERTED leads

			Criteria criteria4 = new Criteria();

			criteria4.and("isDiscarded").is(false);

			if (to != null && from != null) {
				criteria4.and("registrationDate").gte(from).lte(to);
			}
			criteria4.and("leadType").is(LeadType.CONVERTED.getType());
			long totalConverted = mongoTemplate.count(new Query(criteria4), DentalCampCollection.class);
			long costOfConversation = 0;
			if (totalConverted > 0 && totalSpend > 0)
				costOfConversation = totalSpend / totalConverted;

			analyticsDetailDataResponse.setCostOfConversation(costOfConversation);

			response.setData(analyticsDetailDataResponse);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting leads analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting leads analytic");
		}
		return response;
	}

	@Override
	public Response<Object> getMonthlyAnalyticsData(String fromDate, String toDate, List<String> analyticTypes) {
		Response<Object> response = new Response<Object>();
		List<MonthlyAnalyticsResponse> monthlyAnalyticsResponses = new ArrayList<>();
		try {

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
			LocalDate startDate = convertToLocalDateViaInstant(from);
			LocalDate endDate = convertToLocalDateViaInstant(to);

			// number of weeks
			long weekNumber = ChronoUnit.WEEKS.between(startDate, endDate);
			Map<LocalDate, LocalDate> weeks = new LinkedHashMap<>();
			for (int i = 0; i < weekNumber; i++) {
				LocalDate endOfWeek = startDate.plusDays(6);
				weeks.put(startDate, endOfWeek);
				startDate = endOfWeek.plusDays(1);
			}
			List<MonthlyData> monthlyDatas = new ArrayList<MonthlyData>();
			for (String analyticType : analyticTypes) {
				switch (AnalyticType.valueOf(analyticType)) {
				case APPOINTMENT:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse1 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDentalChainAppointment").is(true);
						if (to != null && from != null) {
							criteria.and("createdTime").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						monthlyData.setValue(getAppointmentAnalyticCount(criteria, null));
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse1.setType(analyticType);
					monthlyAnalyticsResponse1.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse1);
					break;
				case LEADS:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse2 = new MonthlyAnalyticsResponse();

					for (LocalDate week : weeks.keySet()) {
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (to != null && from != null) {
							criteria.and("registrationDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						monthlyData.setValue(getAnalyticCount(criteria, null));
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse2.setType(analyticType);
					monthlyAnalyticsResponse2.setMonthlydata(monthlyDatas);
					break;
				case GENUINE_LEAD:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse3 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (to != null && from != null) {
							criteria.and("registrationDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						criteria.and("leadType").is(LeadType.GENUINE.getType());
						monthlyData.setValue(getAnalyticCount(criteria, null));
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse3.setType(analyticType);
					monthlyAnalyticsResponse3.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse3);
					break;
				case HOT_LEAD:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse4 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (to != null && from != null) {
							criteria.and("registrationDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						criteria.and("leadType").is(LeadType.HOT_LEAD.getType());
						monthlyData.setValue(getAnalyticCount(criteria, null));
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse4.setType(analyticType);
					monthlyAnalyticsResponse4.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse4);
					break;
				case CONVERTED:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse5 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (to != null && from != null) {
							criteria.and("registrationDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						criteria.and("leadType").is(LeadType.CONVERTED.getType());
						monthlyData.setValue(getAnalyticCount(criteria, null));
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse5.setType(analyticType);
					monthlyAnalyticsResponse5.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse5);
					break;
				case REACH:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse6 = new MonthlyAnalyticsResponse();

					for (LocalDate week : weeks.keySet()) {
						long reach = 0;

						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (from != null) {
							criteria.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
//
//					if (to != null) {
//						criteria.and("toDate").lte(convertToDateViaSqlDate(endOfWeek));
//					}
						List<CampaignNameCollection> campaignNameCollections = mongoTemplate
								.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
										CampaignNameCollection.class, CampaignNameCollection.class)
								.getMappedResults();
						for (CampaignNameCollection campaignNameCollection : campaignNameCollections) {
							Criteria criteriat = new Criteria();
							criteriat.and("isDiscarded").is(false);
							criteriat.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
							if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
								criteriat.and("campaignId").is(campaignNameCollection.getId());
							}

							List<CampaignObjectCollection> objectCollections = mongoTemplate
									.aggregate(
											Aggregation.newAggregation(Aggregation.match(criteriat),
													Aggregation.sort(Sort.Direction.DESC, "createdTime")),
											CampaignObjectCollection.class, CampaignObjectCollection.class)
									.getMappedResults();
							for (CampaignObjectCollection objectCollection : objectCollections) {
								if (objectCollection.getReach() != null)
									reach += objectCollection.getReach();
							}
						}
						monthlyData.setValue(reach);
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse6.setType(analyticType);
					monthlyAnalyticsResponse6.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse6);
					break;
				case AMOUNT_SPEND:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse7 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						long amountSpend = 0;
						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (from != null) {
							criteria.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}

//					if (to != null) {
//						criteria.and("toDate").lte(convertToDateViaSqlDate(endOfWeek));
//					}
						List<CampaignNameCollection> campaignNameCollections = mongoTemplate
								.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
										CampaignNameCollection.class, CampaignNameCollection.class)
								.getMappedResults();
						for (CampaignNameCollection campaignNameCollection : campaignNameCollections) {
							Criteria criteriat = new Criteria();
							criteriat.and("isDiscarded").is(false);
							criteriat.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));

							if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
								criteriat.and("campaignId").is(campaignNameCollection.getId());
							}

							List<CampaignObjectCollection> objectCollections = mongoTemplate
									.aggregate(
											Aggregation.newAggregation(Aggregation.match(criteriat),
													Aggregation.sort(Sort.Direction.DESC, "createdTime")),
											CampaignObjectCollection.class, CampaignObjectCollection.class)
									.getMappedResults();

							for (CampaignObjectCollection objectCollection : objectCollections) {
								if (objectCollection.getAmountSpend() != null) {
									amountSpend += objectCollection.getAmountSpend();
									System.out.println(
											"objectCollection.getAmountSpend() " + objectCollection.getAmountSpend());
									System.out.println("amountSpend " + amountSpend);
								}
							}
						}
						monthlyData.setValue(amountSpend);
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse7.setType(analyticType);
					monthlyAnalyticsResponse7.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse7);
					break;
				case COST_PER_CLICK:
					MonthlyAnalyticsResponse monthlyAnalyticsResponse8 = new MonthlyAnalyticsResponse();
					for (LocalDate week : weeks.keySet()) {
						long avgCostPerClick = 0;

						LocalDate endOfWeek = weeks.get(week);
						MonthlyData monthlyData = new MonthlyData();
						monthlyData.setFromDate(convertToDateViaSqlDate(week));
						monthlyData.setToDate(convertToDateViaSqlDate(endOfWeek));
						Criteria criteria = new Criteria();
						criteria.and("isDiscarded").is(false);
						if (from != null) {
							criteria.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
						}
						List<CampaignNameCollection> campaignNameCollections = mongoTemplate
								.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
										CampaignNameCollection.class, CampaignNameCollection.class)
								.getMappedResults();
						for (CampaignNameCollection campaignNameCollection : campaignNameCollections) {
							Criteria criteriat = new Criteria();
							criteriat.and("isDiscarded").is(false);
							criteriat.and("fromDate").gte(convertToDateViaSqlDate(week))
									.lte(convertToDateViaSqlDate(endOfWeek));
							if (!DPDoctorUtils.anyStringEmpty(campaignNameCollection.getId())) {
								criteriat.and("campaignId").is(campaignNameCollection.getId());
							}

							List<CampaignObjectCollection> objectCollections = mongoTemplate
									.aggregate(
											Aggregation.newAggregation(Aggregation.match(criteriat),
													Aggregation.sort(Sort.Direction.DESC, "createdTime")),
											CampaignObjectCollection.class, CampaignObjectCollection.class)
									.getMappedResults();
							for (CampaignObjectCollection objectCollection : objectCollections) {
								if (objectCollection.getAvgCostPerClicks() != null)
									avgCostPerClick += objectCollection.getAvgCostPerClicks();
							}
						}
						monthlyData.setValue(avgCostPerClick);
						monthlyDatas.add(monthlyData);
					}
					monthlyAnalyticsResponse8.setType(analyticType);
					monthlyAnalyticsResponse8.setMonthlydata(monthlyDatas);
					monthlyAnalyticsResponses.add(monthlyAnalyticsResponse8);
					break;
				default:
					break;
				}
			}
			response.setDataList(monthlyAnalyticsResponses);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting monthlyDatas analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting monthlyDatas analytic");
		}
		return response;
	}

	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		ZoneId z = ZoneId.of("Asia/Kolkata");
		return dateToConvert.toInstant().atZone(z).toLocalDate().atStartOfDay().toLocalDate();
	}

	public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
		return java.sql.Date.valueOf(dateToConvert);
	}

	@Override
	public String getChatGPTData(String input) {
		StringBuilder response1 = new StringBuilder(); // or StringBuffer if Java version 5+
		String response = "";
//		try {
//			JSONObject body = new JSONObject();
//			JSONObject arrayBodyobject = new JSONObject();
//			body.put("model", "gpt-3.5-turbo");
//			JSONArray jsonArray = new JSONArray();
//			arrayBodyobject.put("role", "user");
//			arrayBodyobject.put("content", input);
//			jsonArray.put(arrayBodyobject);
//			body.put("messages", jsonArray);
//			InputStream is = null;
//
//			URL url = new URL("https://api.openai.com/v1/chat/completions");
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("Authorization",
//			connection.setUseCaches(false);
//			connection.setDoOutput(true);
//
//			// Send request
//			System.out.println(body);
//			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//			wr.writeBytes(body.toString());
//			wr.close();
//
//			// Get Response
//
//			try {
//				is = connection.getInputStream();
//			} catch (IOException ioe) {
//				if (connection instanceof HttpURLConnection) {
//					HttpURLConnection httpConn = (HttpURLConnection) connection;
//					int statusCode = httpConn.getResponseCode();
//					if (statusCode != 200) {
//						is = httpConn.getErrorStream();
//					}
//				}
//			}
//
//			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//
//			String line;
//			while ((line = rd.readLine()) != null) {
//				response1.append(line);
//				response1.append('\r');
//			}
//			rd.close();
//
//			System.out.println("http response" + response1.toString());
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//		response = response1.toString();
		return response;
	}
}
