package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.LabTestRequest;
import com.dpdocter.collections.LabTestCustomerdetailCollection;
import com.dpdocter.collections.LabTestPlansCollection;
import com.dpdocter.collections.LabTestUsersPaymentCollection;
import com.dpdocter.collections.ProdDealIdsForLabPlanTestCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DisplayTypeThyrocare;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LabTestCustomerRepository;
import com.dpdocter.repository.LabTestPlansRepository;
import com.dpdocter.repository.ProdDealIdsForLabPlanTestRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.beans.HealthiansPlanCreateObject;
import com.dpdocter.beans.HealthiensCustomer;
import com.dpdocter.response.HealthiansPlanObject;
import com.dpdocter.response.LabTestCustomerResponse;
import com.dpdocter.response.LabTestResponse;
import com.dpdocter.response.ProdDealIdsForLabPlanResponse;
import com.dpdocter.services.LabTestPlansService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
@Transactional
public class LabTestPlansServiceImpl implements LabTestPlansService {

	private static Logger logger = LogManager.getLogger(LabTestPlansServiceImpl.class.getName());

	@Autowired
	LabTestPlansRepository labTestPlansRepository;

	@Autowired
	LabTestCustomerRepository labTestCustomerRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${healthians_secret_key}")
	private String secret;

	
	@Value(value = "${healthians_end_point}")
	private String healthians_endpoint;
		
	@Value(value = "${healthians_partner_name}")
	private String partner_name;
	
	
	@Value(value = "${thyrocare_username}")
	private String thyrocareUsername;

	@Value(value = "${thyrocare_password}")
	private String thyrocarePassword;
	
	@Value(value = "${thyrocare_endpoint}")
	private String thyrocareEndpoint;
			

	// for prod upload data healthiens
	@Value(value = "${upload.plans.detail.of.prod}")
	private String PLAN_DETAIL_INFORMATION_UPLOADED_FILE;
	
	@Autowired
	ProdDealIdsForLabPlanTestRepository prodDealIdsForLabPlanTestRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public LabTestResponse addEditLabTestPlan(LabTestRequest request) {
		LabTestResponse response = null;
		try {

			LabTestPlansCollection collection = null;
			collection = new LabTestPlansCollection();
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = labTestPlansRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (collection == null) {
					throw new BusinessException(ServiceError.NoRecord, " Plan Not found with Id");
				}
				request.setCreatedTime(collection.getCreatedTime());
				collection.setFaqs(null);
				collection.setTestIncluded(null);
				collection.setPlanDescription(null);
				request.setAdminCreatedTime(collection.getAdminCreatedTime());
				request.setCreatedBy(collection.getCreatedBy());
				request.getAmount().setSession("ONCE");
				BeanUtil.map(request, collection);

			} else {
				BeanUtil.map(request, collection);
				collection.getAmount().setSession("ONCE");
				collection.setCreatedTime(new Date());
				collection.setAdminCreatedTime(new Date());
				collection.setCreatedBy("Admin");
				collection.setPlanUId(UniqueIdInitial.LABTEST_PLANS.getInitial() + DPDoctorUtils.generateRandomId());
			}

			collection = labTestPlansRepository.save(collection);
			response = new LabTestResponse();
			BeanUtil.map(collection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit Plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit  Plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public LabTestResponse getLabTestPlanById(String planId) {
		LabTestResponse response = null;
		try {
			LabTestPlansCollection labTestPlansCollection = labTestPlansRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (labTestPlansCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new LabTestResponse();
			BeanUtil.map(labTestPlansCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Integer countPlans(String labPartner, boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);

			criteria = criteria.orOperator(new Criteria("testName").regex("^" + searchTerm, "i"),
					new Criteria("testName").regex("^" + searchTerm));

			if (labPartner != null) {
				criteria.and("labPartner").is(labPartner);
			}

			response = (int) mongoTemplate.count(new Query(criteria), LabTestPlansCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<LabTestResponse> getLabTestPlans(int page, int size, String labPartner, boolean isDiscarded,
			String searchTerm) {
		List<LabTestResponse> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("testName").regex("^" + searchTerm, "i"),
						new Criteria("testName").regex("^" + searchTerm));

//			if (labPartner == "HEALTHIANS") {
//				criteria.and("healthiansPlanObject.labPartner").is(labPartner);
//			}else {
//				
//			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, LabTestPlansCollection.class, LabTestResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean deleteLabTestPlan(String planId, boolean isDiscarded) {
		Boolean response = false;
		try {
			LabTestPlansCollection labTestPlansCollection = labTestPlansRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (labTestPlansCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			labTestPlansCollection.setIsDiscarded(isDiscarded);
			labTestPlansCollection.setUpdatedTime(new Date());
			labTestPlansCollection = labTestPlansRepository.save(labTestPlansCollection);
			response = true;
//			BeanUtil.map(countryCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while deleting the plan  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the plan");
		}

		return response;
	}

	@Override
	public LabTestResponse getLabTestPlanBySlugUrl(String slugURL, String planUId) {
		LabTestResponse response = null;
		try {
			LabTestPlansCollection labTestPlansCollection = labTestPlansRepository.findByPlanUId(planUId);
			if (labTestPlansCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new LabTestResponse();
			BeanUtil.map(labTestPlansCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Response<Object> getHealthiensPlans() {
		Response<Object> response = new Response<Object>();
		// can get access token api for authentication token
		Unirest.setTimeouts(0, 0);
		HttpResponse<JsonNode> auth = null;
//		try {
//			auth = Unirest.get(healthians_endpoint+"/api/"+partner_name+"/getAccessToken").basicAuth(keyId, secret)
//					.asJson();
//
//			if (auth.getStatus() != 200) {
//				throw new RuntimeException("Failed : token generate api : " + auth.getStatus());
//			}
//			;
//		} catch (UnirestException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// call get packages api by passing access token
		Unirest.setTimeouts(0, 0);
		try {
			HttpResponse<JsonNode> serverResponse = Unirest
					.get(healthians_endpoint+"/api/"+partner_name+"/getPopularPackages")
					.header("accept", "application/json")
					.header("Authorization", "Bearer " + auth.getBody().getObject().get("access_token")).asJson();

			if (auth.getStatus() != 200) {
				throw new RuntimeException("Failed : get packages api : " + auth.getStatus());
			}
			;
			ObjectMapper mapper = new ObjectMapper();
			List<HealthiansPlanObject> list = null;
			try {
				list = Arrays.asList(mapper.readValue(serverResponse.getBody().getObject().get("packages").toString(),
						HealthiansPlanObject[].class));
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}

			System.out.println(serverResponse.getStatus());
			response.setDataList(list);
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public List<LabTestCustomerResponse> getLabTestUsersList(int page, int size, String labPartner, String searchTerm) {
		List<LabTestCustomerResponse> response = null;

//		LabTestCustomerdetailCollection labTestCustomerdetailCollection = null;
//		LabTestPlansCollection labTestPlansCollection = null;
		UserCollection userCollection = null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("mobile").regex("^" + searchTerm, "i"),
						new Criteria("mobile").regex("^" + searchTerm));

			if (labPartner != null) {
				criteria.and("labPartner").is(labPartner);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("lab_test_plan_cl", "planId", "_id", "labtestplans"),
						Aggregation.unwind("labtestplans", true),
						Aggregation.lookup("lab_test_customer_cl", "_id", "_id", "customers"),
						Aggregation.unwind("customers", true),
						Aggregation.lookup("lab_test_payment_cl", "_id", "labTestAppointmentId", "payment"),
						Aggregation.unwind("payment", true),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("userName", "$user.firstName")
										.append("testName", "$labtestplans.testName")
										.append("createdTime", "$customers.createdTime")
										.append("mobile", "$customers.mobile")
										.append("payment_option", "$customers.payment_option")
										.append("booking_id", "$customers.booking_id")
										.append("appointment_date", "$customers.appointment_date")
										.append("appointment_to_Time", "$customers.appointment_to_Time")
										.append("appointment_from_Time", "$customers.appointment_from_Time")
										.append("transactionStatus", "$customers.transactionStatus")
										.append("isCancel", "$customers.isCancel").append("paymentId", "$payment._id")
										.append("payOrderId", "$payment.orderId")
										.append("labPartner", "$customers.labPartner")
										.append("address", "$customers.address")
										.append("ref_orderId", "$customers.ref_orderId")
										.append("cancelReason", "$customers.cancelReason").append("isReshedule",
												"$customers.isReshedule"))),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("userName", new BasicDBObject("$first", "$userName"))
								.append("testName", new BasicDBObject("$first", "$testName"))
								.append("userId", new BasicDBObject("$first", "$userId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("mobile", new BasicDBObject("$first", "$mobile"))
								.append("booking_id", new BasicDBObject("$first", "$booking_id"))
								.append("payment_option", new BasicDBObject("$first", "$payment_option"))
								.append("address", new BasicDBObject("$first", "$address"))
								.append("appointment_date", new BasicDBObject("$first", "$appointment_date"))
								.append("appointment_to_Time", new BasicDBObject("$first", "$appointment_to_Time"))
								.append("appointment_from_Time", new BasicDBObject("$first", "$appointment_from_Time"))
								.append("transactionStatus", new BasicDBObject("$first", "$transactionStatus"))
								.append("isCancel", new BasicDBObject("$first", "$isCancel"))
								.append("paymentId", new BasicDBObject("$first", "_id"))
								.append("labPartner", new BasicDBObject("$first", "$labPartner"))
								.append("ref_orderId", new BasicDBObject("$first", "$ref_orderId"))
								.append("payOrderId", new BasicDBObject("$first", "$orderId"))
								.append("custPrderId", new BasicDBObject("$first", "$orderId"))								
								.append("isReshedule", new BasicDBObject("$first", "$isReshedule")))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(

						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("lab_test_plan_cl", "planId", "_id", "labtestplans"),
						Aggregation.unwind("labtestplans", true),
						Aggregation.lookup("lab_test_payment_cl", "_id", "labTestAppointmentId", "payment"),
						Aggregation.unwind("payment", true),
						Aggregation.lookup("lab_test_customer_cl", "_id", "_id", "customers"),
						Aggregation.unwind("customers", true), Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("userName", "$user.firstName")
										.append("testName", "$labtestplans.testName")
										.append("createdTime", "$customers.createdTime")
										.append("mobile", "$customers.mobile")
										.append("payment_option", "$customers.payment_option")
										.append("booking_id", "$customers.booking_id")
										.append("address", "$customers.address")
										.append("appointment_date", "$customers.appointment_date")
										.append("appointment_to_Time", "$customers.appointment_to_Time")
										.append("appointment_from_Time", "$customers.appointment_from_Time")
										.append("transactionStatus", "$customers.transactionStatus")
										.append("isCancel", "$customers.isCancel")
										.append("cancelReason", "$customers.cancelReason")
										.append("paymentId", "$payment._id")
										.append("payOrderId", "$payment.orderId")
										.append("custPrderId", "$customers.orderId")
										.append("labPartner", "$customers.labPartner")
										.append("ref_orderId", "$customers.ref_orderId")
										.append("isReshedule", "$customers.isReshedule"))),

						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("userName", new BasicDBObject("$first", "$userName"))
								.append("testName", new BasicDBObject("$first", "$testName"))
								.append("userId", new BasicDBObject("$first", "$userId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("mobile", new BasicDBObject("$first", "$mobile"))
								.append("payment_option", new BasicDBObject("$first", "$payment_option"))
								.append("labPartner", new BasicDBObject("$first", "$labPartner"))
								.append("ref_orderId", new BasicDBObject("$first", "$ref_orderId"))
								.append("booking_id", new BasicDBObject("$first", "$booking_id"))
								.append("appointment_date", new BasicDBObject("$first", "$appointment_date"))
								.append("appointment_to_Time", new BasicDBObject("$first", "$appointment_to_Time"))
								.append("appointment_from_Time", new BasicDBObject("$first", "$appointment_from_Time"))
								.append("transactionStatus", new BasicDBObject("$first", "$transactionStatus"))
								.append("isCancel", new BasicDBObject("$first", "$isCancel"))
								.append("paymentId", new BasicDBObject("$first", "$_id"))
								.append("address", new BasicDBObject("$first", "$address"))
								.append("payOrderId", new BasicDBObject("$first", "$orderId"))
								.append("isReshedule", new BasicDBObject("$first", "$isReshedule")))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation" + aggregation);
			response = mongoTemplate
					.aggregate(aggregation, LabTestCustomerdetailCollection.class, LabTestCustomerResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public LabTestCustomerResponse getLabTestPlanCustomerById(String labTestAppointmentId) {
		LabTestCustomerResponse response = null;
		try {
			LabTestCustomerdetailCollection labTestPlansCollection = labTestCustomerRepository
					.findById(new ObjectId(labTestAppointmentId)).orElse(null);
			if (labTestPlansCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new LabTestCustomerResponse();
			BeanUtil.map(labTestPlansCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Integer countLabTestPlans(String labPartner, boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("mobile").regex("^" + searchTerm, "i"),
						new Criteria("mobile").regex("^" + searchTerm));

			if (labPartner != null) {
				criteria.and("labPartner").is(labPartner);
			}

			response = (int) mongoTemplate.count(new Query(criteria), LabTestCustomerdetailCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting  " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<?> getLabTestPayment(int page, int size, String labPartner, boolean isDiscarded, String searchTerm) {
		List<LabTestUsersPaymentCollection> response = null;
		try {
			Criteria criteria = new Criteria();
//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("testName").regex("^" + searchTerm, "i"),
//						new Criteria("testName").regex("^" + searchTerm));
//
//			if (labPartner != null) {
//				criteria.and("labPartner").is(labPartner);
//			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, LabTestUsersPaymentCollection.class, LabTestUsersPaymentCollection.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	
	// login api for thyrocare for getting api key
	public String getLoginThyrocarePlans() {
		
		String response = null;
		
		String Url = thyrocareEndpoint+"/common.svc/"+thyrocareUsername+"/"+thyrocarePassword+"/portalorders/DSA/login";
				
		Unirest.setTimeouts(0, 0);
		HttpResponse<JsonNode> auth = null;
		try {
			auth = Unirest.get(Url).asJson();

			if (auth.getStatus() != 200) {
				throw new RuntimeException("Failed : token generate api : " + auth.getStatus());
			}
			System.out.println(auth.getBody().getObject().get("API_KEY").toString());
			response = auth.getBody().getObject().get("API_KEY").toString();
		} catch (UnirestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return response;
		
		
	}
	
	@Override
	public Response<Object> getThyrocarePlans(DisplayTypeThyrocare display_type) {

		Response<Object> response = new Response<Object>();
		try {
		String api_key = getLoginThyrocarePlans();
		
		System.out.println("from Product api"+api_key);
//		"https://www.thyrocare.com/API_BETA/master.svc/sNhdlQjqvoD7zCbzf56sxppBJX3MmdWSAomi@RBhXRrVcGyko7hIzQ==/ALL/products";
		
		String Url = thyrocareEndpoint+"/master.svc/"+api_key+"/"+display_type+"/products";
		
		Unirest.setTimeouts(0, 0);
		HttpResponse<JsonNode> serverResponse = null;
		try {
			serverResponse = Unirest.get(Url).asJson();

			if (serverResponse.getStatus() != 200) {
				throw new RuntimeException("Failed : token generate api : " + serverResponse.getStatus());
			}
		} catch (UnirestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectMapper mapper = new ObjectMapper();
//		List<HealthiansPlanObject> list = null;
		Object list = null;
		try {
			response.setDataList(Arrays.asList(mapper.readValue(serverResponse.getBody().toString().toLowerCase(),Object.class)));
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		System.out.println(serverResponse.getStatus());
//		response.setData(list);
		}
		catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Response<Object> getHealthiensPlansProdData() {
		Response<Object> response = new Response<Object>();
		try {
//			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("testName").regex("^" + searchTerm, "i"),
//						new Criteria("testName").regex("^" + searchTerm));

//			if (labPartner == "HEALTHIANS") {
//				criteria.and("healthiansPlanObject.labPartner").is(labPartner);
//			}else {
//				
//			}

			System.out.println("step 1");
			Aggregation aggregation = null;
//			if (size > 0) {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
//						Aggregation.limit(size));
//			} else {
				aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
//			}
			response.setDataList(mongoTemplate.aggregate(aggregation, ProdDealIdsForLabPlanTestCollection.class, ProdDealIdsForLabPlanResponse.class)
					.getMappedResults());
			
			response.setCount(mongoTemplate.aggregate(aggregation, ProdDealIdsForLabPlanTestCollection.class, ProdDealIdsForLabPlanResponse.class)
					.getMappedResults().size());
			
			
			

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean uploadProdDealDetail() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int lineCount = 0;
		String csvLine = null;
//		int dataCountNotUploaded =0;
		FileWriter fileWriter = null;

		try {

			System.out.println("step 1");

			br = new BufferedReader(new FileReader(PLAN_DETAIL_INFORMATION_UPLOADED_FILE));


			while ((line = br.readLine()) != null) {
				System.out.println("step 2");
				System.out.println(line);
				if (lineCount > 0) {
					ProdDealIdsForLabPlanTestCollection prodDealIdsForLabPlanTest = new ProdDealIdsForLabPlanTestCollection();

					System.out.println("step 3");

					String[] fields = line.split(",");
					if (!DPDoctorUtils.anyStringEmpty(fields[0])&& !fields[0].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setHealthiens_dealId(fields[0]);
					}
					if (!DPDoctorUtils.anyStringEmpty(fields[1])&& !fields[1].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setThyrocare_dealId(fields[1]);
					}
					if (!DPDoctorUtils.anyStringEmpty(fields[2])&& !fields[2].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setPackageName(fields[2]);
					}
					if (!DPDoctorUtils.anyStringEmpty(fields[3])&& !fields[3].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setHealthiens_sellingPrice(fields[3]);
					}
					if (!DPDoctorUtils.anyStringEmpty(fields[4])&& !fields[4].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setHealthiens_pickupCharge(fields[4]);
					}
					if (!DPDoctorUtils.anyStringEmpty(fields[5])&& !fields[5].equalsIgnoreCase("NA")) {
						prodDealIdsForLabPlanTest.setHealthcoco_sellingPrice(fields[5]);
					}
//					if (!DPDoctorUtils.anyStringEmpty(fields[6])&& !fields[6].equalsIgnoreCase("NA")) {
//						prodDealIdsForLabPlanTest.setHalfLifeOfDrug(fields[6]);
//					}
//					if (!DPDoctorUtils.anyStringEmpty(fields[15])&& !fields[15].equalsIgnoreCase("NA")) {
//						prodDealIdsForLabPlanTest.setOnsetOfAction(fields[15]);
//					}
//					if (!DPDoctorUtils.anyStringEmpty(fields[16])&& !fields[16].equalsIgnoreCase("NA")) {
//						prodDealIdsForLabPlanTest.setDurationOfEffect(fields[16]);
//					}
			
					prodDealIdsForLabPlanTest = prodDealIdsForLabPlanTestRepository.save(prodDealIdsForLabPlanTest);
					System.out.println(prodDealIdsForLabPlanTest.getId());
					System.out.println("Data Saved for "+prodDealIdsForLabPlanTest.getPackageName());
				}
				System.out.println("step 4"+lineCount);
				lineCount = lineCount + 1;
				response = true;
			}
			
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
				
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return response;

	}

	@Override
	public Response<Object> getBookingStatus(String booking_id) {
		Response<Object> response = new Response<Object>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject healthiensRequest = new JSONObject();

		// can get access token api for authentication token
		Unirest.setTimeouts(0, 0);
		HttpResponse<JsonNode> auth = null;
//		try {
//			auth = Unirest.get(healthians_endpoint + "/api/" + partner_name + "/getAccessToken")
//					.basicAuth(keyId, secret).asJson();
//
//			System.out.println("Token" + auth.getBody().getObject().get("access_token"));
//			if (auth.getStatus() != 200) {
//				throw new RuntimeException("Failed : token generate api : " + auth.getStatus());
//			}
//			;
//		} catch (UnirestException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// call get packages api by passing access token
		Unirest.setTimeouts(0, 0);
		try {
			System.out.println("Call 2 api");
			healthiensRequest.put("booking_id", booking_id);
			System.out.println(healthiensRequest);

			HttpResponse<String> serverResponse = Unirest
					.post(healthians_endpoint + "/api/" + partner_name + "/getBookingStatus")
					.header("Authorization", "Bearer " + auth.getBody().getObject().get("access_token"))
					.header("Content-Type", "application/json").header("accept", "application/json")
					.body(healthiensRequest).asString();
			System.out.println(serverResponse.getStatus());
			System.out.println(serverResponse.getBody());
		
			HealthiansPlanCreateObject responseData = null;
			try {
				responseData = mapper.readValue(serverResponse.getBody().toString(), HealthiansPlanCreateObject.class);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(responseData.getStatus()) {
			for (HealthiensCustomer o: responseData.getData().getCustomer()) {
				  System.out.println(o.getVendor_customer_id());
				  if(!o.getVendor_customer_id().equalsIgnoreCase("temp")) {
				  UserCollection userCollection = userRepository.findById(new ObjectId(o.getVendor_customer_id()))
							.orElse(null);
				  o.setName(userCollection.getFirstName());
				  o.setMobileNumber(userCollection.getMobileNumber());
				  }				  
				}			
			}
			response.setData(responseData);

		} catch (UnirestException | JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	@Override
	public Response<Object> getBookingStatusForThyrocare(String loginMobile, String ref_orderId) {
		Response<Object> response = new Response<Object>();
		ObjectMapper mapper = new ObjectMapper();

		// call login
		String api_key = getLoginThyrocarePlans();
		System.out.println("from loginapi " + api_key);

		// call get packages api by passing access token
		Unirest.setTimeouts(0, 0);
		try {

			System.out.println("Call 2 api");
			String Url = thyrocareEndpoint + "/order.svc/" + api_key + "/" + ref_orderId + "/" + loginMobile
					+ "/all/OrderSummary";
			System.out.println(Url);
			Unirest.setTimeouts(0, 0);
			HttpResponse<String> serverResponse = null;
			try {

				serverResponse = Unirest.get(Url).asString();
				System.out.println(serverResponse.getStatus());
				System.out.println("isSUCCESS " + serverResponse.getBody());

				if (serverResponse.getStatus() != 200) {
					throw new RuntimeException("Failed : token generate api : " + serverResponse.getStatus());
				}
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Object list = null;

			try {
				list = mapper.readValue(serverResponse.getBody().toString().toLowerCase(), Object.class);

			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}

			response.setData(list);
		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}

		return response;
	}
}
