package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

import com.dpdocter.beans.BankDetails;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorOnlineConsultation;
import com.dpdocter.beans.OnlineConsultationPayment;

import com.dpdocter.beans.OnlineConsultationSpecialityPrice;

import com.dpdocter.beans.TransactionStatusRequest;

import com.dpdocter.beans.User;
import com.dpdocter.collections.BankDetailsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.OnlineConsultationSpecialityPriceCollection;
import com.dpdocter.collections.OnlineConsultionPaymentCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BankDetailsRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.OnlineConsultationPaymentRepository;
import com.dpdocter.repository.OnlineConsultationSpecialityPriceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.OnlineConsultationAccountRequest;
import com.dpdocter.request.RazorPayAccountRequest;
import com.dpdocter.response.RazorPayAccountResponse;
import com.dpdocter.security.AES;
import com.dpdocter.services.OnlineConsultationPaymentServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.VirtualAccount;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class OnlineConsultationPaymentServiceImpl implements OnlineConsultationPaymentServices {

	private static Logger logger = LogManager.getLogger(OnlineConsultationPaymentServiceImpl.class.getName());
	
	@Autowired
	private OnlineConsultationPaymentRepository onlineConsultationPaymentRepository;
	
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BankDetailsRepository bankDetailsRepository;
	
	@Value(value = "${secret.key.account.details}")
	private String secretKeyAccountDetails;
	
	@Value(value = "${razorpay.api.secret}")
	private String secret;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${razorpay.api.key}")
	private String keyId;
	
	@Autowired
	private OnlineConsultationSpecialityPriceRepository doctorOnlineConsultationPriceRepository;
	
	@Override
	public Boolean editPaymentDetail(OnlineConsultationAccountRequest request) {
		Boolean response=false;
		
		List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
		
		try {
			BankDetailsCollection  onlinePaymentCollection=null;
			onlinePaymentCollection=bankDetailsRepository.findByDoctorId(new ObjectId(request.getDoctorId()));
			if(onlinePaymentCollection!=null)
			{
				onlinePaymentCollection.setRazorPayAccountId(request.getRazorPayAccountId());
				bankDetailsRepository.save(onlinePaymentCollection);
			}
			
			doctorClinicProfileCollections = doctorClinicProfileRepository.findByDoctorId(new ObjectId(request.getDoctorId()));
			
			if(onlinePaymentCollection !=null && onlinePaymentCollection.getRazorPayAccountId() !=null) {
				for(DoctorClinicProfileCollection clinicProfileCollection : doctorClinicProfileCollections) {
				
				clinicProfileCollection.setIsOnlineConsultationAvailable(true);
				doctorClinicProfileRepository.save(clinicProfileCollection);
				
				}
			}
			
			response=true;
		
	} catch (Exception e) {
		e.printStackTrace();
		logger.error(e + " Error Editing Doctor Payment details");
		throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Editing Doctor Payment Details");
	}
		return response;
	}

	@Override
	public Response<Object> getBankDetailsList(int page, int size, String searchTerm,String city,String specialities,Boolean isRegistrationDetailsVerified,Boolean isPhotoIdVerified,
			String fromDate,String toDate) {
		Response<Object> response = new Response<Object>();
		List<BankDetails> bankDetails=new ArrayList<BankDetails>();
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("doctorProfile.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctorProfile.firstName").regex("^" + searchTerm),
						new Criteria("doctorProfile.emailAddress").regex("^" + searchTerm));

			
			
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (fromDateTime != null && toDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
			} else if (fromDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime);
			} else if (toDateTime != null) {
				criteria.and("createdTime").lte(toDateTime);
			}


			

			
			
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				criteria.and("location.city").is(city);
			}

			if (!DPDoctorUtils.anyStringEmpty(specialities)) {
				criteria.and("speciality.speciality").is(specialities);
			}
			if (isRegistrationDetailsVerified!=null) {
				criteria.and("doctorSpeciality.isRegistrationDetailsVerified").is(isRegistrationDetailsVerified);
			}
			
			if (isPhotoIdVerified!=null) {
				criteria.and("doctorSpeciality.isPhotoIdVerified").is(isPhotoIdVerified);
			}
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
						Aggregation.unwind("doctorProfile"), 
						Aggregation.lookup("docter_cl", "doctorId","userId","doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality.specialities"),
						Aggregation.lookup("speciality_cl", "doctorSpeciality.specialities","_id","speciality"),
						Aggregation.unwind("speciality"),
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "doctorClinic.locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("accountholderName", "$accountholderName")
										.append("accountNumber", "$accountNumber")
										.append("ifscNumber", "$ifscNumber")
										.append("panCardNumber", "$panCardNumber")
										.append("createdTime", "$createdTime")
										.append("accountType", "$accountType")
										.append("bankName", "$bankName")
										.append("branchCity", "$branchCity")
										.append("mobileNumber", "$doctorProfile.mobileNumber")
										.append("emailAddress", "$doctorProfile.emailAddress")
										.append("razorPayAccountId", "$razorPayAccountId")
										.append("doctorId", "$doctorId")
										.append("city", "$location.city")
										.append("speciality", "$speciality.speciality")
										.append("isRegistrationDetailsVerified", "$doctorSpeciality.isRegistrationDetailsVerified")
										.append("isPhotoIdVerified", "$doctorSpeciality.isPhotoIdVerified"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										.append("doctorName", new BasicDBObject("$first", "$doctorName"))
										.append("accountholderName", new BasicDBObject("$first", "$accountholderName"))
										.append("accountNumber", new BasicDBObject("$first", "$accountNumber"))
										.append("ifscNumber", new BasicDBObject("$first", "$ifscNumber"))
										.append("panCardNumber", new BasicDBObject("$first", "$panCardNumber"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
										.append("accountType", new BasicDBObject("$first", "$accountType"))
										.append("bankName", new BasicDBObject("$first", "$bankName"))
										.append("branchCity", new BasicDBObject("$first", "$branchCity"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("doctorId", new BasicDBObject("$first", "$doctorId"))
										.append("city", new BasicDBObject("$first", "$city"))
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("isRegistrationDetailsVerified",new BasicDBObject("$first", "$isRegistrationDetailsVerified"))
										.append("isPhotoIdVerified",new BasicDBObject("$first", "$isPhotoIdVerified"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId"))
										)),
		
										
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
						Aggregation.unwind("doctorProfile"), 
						Aggregation.lookup("docter_cl", "doctorId","userId","doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality.specialities"),
						Aggregation.lookup("speciality_cl", "doctorSpeciality.specialities","_id","speciality"),
						Aggregation.unwind("speciality"),
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "doctorClinic.locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("accountholderName", "$accountholderName")
										.append("accountNumber", "$accountNumber")
										.append("ifscNumber", "$ifscNumber")
										.append("panCardNumber", "$panCardNumber")
										.append("createdTime", "$createdTime")
										.append("accountType", "$accountType")
										.append("bankName", "$bankName")
										.append("branchCity", "$branchCity")
										.append("mobileNumber", "$doctorProfile.mobileNumber")
										.append("emailAddress", "$doctorProfile.emailAddress")
										.append("razorPayAccountId", "$razorPayAccountId")
										.append("doctorId", "$doctorId")
										.append("city", "$location.city")
										.append("speciality", "$speciality.speciality")
										.append("isRegistrationDetailsVerified", "$doctorSpeciality.isRegistrationDetailsVerified")
										.append("isPhotoIdVerified", "$doctorSpeciality.isPhotoIdVerified"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										.append("doctorName", new BasicDBObject("$first", "$doctorName"))
										.append("accountholderName", new BasicDBObject("$first", "$accountholderName"))
										.append("accountNumber", new BasicDBObject("$first", "$accountNumber"))
										.append("ifscNumber", new BasicDBObject("$first", "$ifscNumber"))
										.append("panCardNumber", new BasicDBObject("$first", "$panCardNumber"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
										.append("accountType", new BasicDBObject("$first", "$accountType"))
										.append("bankName", new BasicDBObject("$first", "$bankName"))
										.append("branchCity", new BasicDBObject("$first", "$branchCity"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("doctorId", new BasicDBObject("$first", "$doctorId"))
										.append("city", new BasicDBObject("$first", "$city"))
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("isRegistrationDetailsVerified",new BasicDBObject("$first", "$isRegistrationDetailsVerified"))
										.append("isPhotoIdVerified",new BasicDBObject("$first", "$isPhotoIdVerified"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId")))),
		
																
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation"+aggregation);
			bankDetails = mongoTemplate.aggregate(aggregation,BankDetailsCollection.class, BankDetails.class)
					.getMappedResults();
			
			response.setCount(bankDetails.size());
			
			if(bankDetails!=null)
			for(BankDetails bankDetailsCollection:bankDetails) {
				 bankDetailsCollection.setAccountholderName(AES.decrypt(bankDetailsCollection.getAccountholderName(), secretKeyAccountDetails));
			bankDetailsCollection.setAccountNumber(AES.decrypt(bankDetailsCollection.getAccountNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setIfscNumber(AES.decrypt(bankDetailsCollection.getIfscNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setPanCardNumber(AES.decrypt(bankDetailsCollection.getPanCardNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setBankName(AES.decrypt(bankDetailsCollection.getBankName(), secretKeyAccountDetails));
			bankDetailsCollection.setBranchCity(AES.decrypt(bankDetailsCollection.getBranchCity(), secretKeyAccountDetails));
		//	bankDetailsCollection.setDoctorId(bankDetailsCollection.getDoctorId());
		//	bankDetailsCollection.setId(bankDetailsCollection.getId());
			}
//			response=new ArrayList<BankDetails>();
			response.setDataList(bankDetails);

			
//			BeanUtil.map(bankDetails, response);
			
		} catch (BusinessException e) {
			logger.error("Error while getting bank Details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting bank Details " + e.getMessage());

		}
		return response;
	}
	
//	 @Override
//		public Integer countConsultationPayment(Boolean discarded, String searchTerm) {
//			Integer response=null;
//			try {
//				Criteria criteria = new Criteria("discarded").is(discarded);
//			    criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
//					new Criteria("name").regex("^" + searchTerm));
//		
//		response = (int) mongoTemplate.count(new Query(criteria), OnlineConsultionPaymentCollection.class);
//	} catch (BusinessException e) {
//		logger.error("Error while counting Online Payment Details " + e.getMessage());
//		e.printStackTrace();
//		throw new BusinessException(ServiceError.Unknown, "Error while counting Online Payment Details " + e.getMessage());
//
//	}
//			return response;
//		}
	
	@Override
	public RazorPayAccountResponse createAccount(RazorPayAccountRequest request) {
		RazorPayAccountResponse response=null;
		try {
	//	RazorpayClient rayzorpayClient = new RazorpayClient(keyId, secret);
		BankDetailsCollection bankDetails=null;
		bankDetails=bankDetailsRepository.findByDoctorId(new ObjectId(request.getDoctorId()));
		if (bankDetails == null) {
			throw new BusinessException(ServiceError.InvalidInput, "bank details not found");
		}
		JSONObject orderRequest = new JSONObject();
		UserCollection doctor = userRepository
				.findById(new ObjectId(request.getDoctorId())).orElse(null);
		if (doctor == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor not found");
		}
		JSONObject requestObject1 = new JSONObject();
		JSONObject requestObject2 = new JSONObject();
		orderRequest.put("name",request.getName());
		orderRequest.put("email", request.getEmail());
		orderRequest.put("tnc_accepted", true);
		
		requestObject1.put("business_name", request.getBusiness_name());
		requestObject1.put("business_type", "individual");
		orderRequest.put("account_details", requestObject1);
		requestObject2.put("ifsc_code", request.getIfsc_code());
		requestObject2.put("beneficiary_name", request.getBeneficiary_name());
		requestObject2.put("account_number", request.getAccount_number());
		requestObject2.put("account_type", request.getAccount_type());
		orderRequest.put("bank_account", requestObject2);
		
		System.out.println(orderRequest);

	//	account = rayzorpayClient.VirtualAccounts.create(orderRequest);
		
		String url="https://api.razorpay.com/v1/beta/accounts";
		 String authStr=keyId+":"+secret;
		 String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		
		con.setDoOutput(true);
		
		System.out.println(con.getErrorStream());
		con.setDoInput(true);
		// optional default is POST
		con.setRequestMethod("POST");
//		con.setRequestProperty("User-Agent",
//				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	//	con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Content-Type","application/json");
		con.setRequestProperty("Authorization", "Basic " +  authStringEnc);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(orderRequest.toString());
		System.out.println("Orderrequest:"+orderRequest.toString());
		  wr.flush();
            wr.close();
            con.disconnect();
            InputStream in=con.getInputStream();
        //    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c=in.read()) !=-1) {

				output.append((char) c);
				
			}
			System.out.println("response:"+output.toString());
			  ObjectMapper mapper = new ObjectMapper();

			  RazorPayAccountResponse list = mapper.readValue(output.toString(),RazorPayAccountResponse.class);
		
		if(list!=null)
		{
			bankDetails.setRazorPayAccountId(list.getId());
			bankDetails.setIsAccountCreated(true);
			bankDetails.setUpdatedTime(new Date());
			bankDetailsRepository.save(bankDetails);
			
		}
		response=new RazorPayAccountResponse();
		BeanUtil.map(bankDetails, response);

		
	} 
		catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				
			}
		return response;

	}
	
	@Override
	public List<DoctorOnlineConsultation> getDoctorOnlineConsultationInfo(int page, int size, String searchTerm,String city,
			String specialities,String transactionStatus,
			String fromDate,String toDate) {
		List<DoctorOnlineConsultation> response=null;
		try {
			
			
			
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("doctorProfile.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctorProfile.firstName").regex("^" + searchTerm),
						new Criteria("doctorProfile.emailAddress").regex("^" + searchTerm));

			
			
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (fromDateTime != null && toDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
			} else if (fromDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime);
			} else if (toDateTime != null) {
				criteria.and("createdTime").lte(toDateTime);
			}


			

			
			
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				criteria.and("location.city").is(city);
			}

			if (!DPDoctorUtils.anyStringEmpty(specialities)) {
				criteria.and("speciality.speciality").is(specialities);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(transactionStatus)) {
			criteria.and("transactionStatus").is(transactionStatus);
			}
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
						Aggregation.unwind("doctorProfile"), 
						
						Aggregation.lookup("docter_cl", "doctorId","userId","doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality.specialities"),
						Aggregation.lookup("speciality_cl", "doctorSpeciality.specialities","_id","speciality"),
						Aggregation.unwind("speciality"),
						Aggregation.lookup("user_cl", "userId", "_id", "userProfile"),
						Aggregation.unwind("userProfile"), 
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "doctorClinic.locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("createdTime", "$createdTime")
										.append("mobileNumber", "$doctorProfile.mobileNumber")
										.append("emailAddress", "$doctorProfile.emailAddress")
										.append("razorPayAccountId", "$razorPayAccountId")
										.append("doctorId", "$doctorId")
										.append("locationId", "$doctorClinic.locationId")
										.append("city", "$location.city")
										.append("speciality", "$speciality.speciality")
										.append("transactionId", "$transactionId")
										.append("transactionStatus", "$transactionStatus")
										.append("patientId", "$userId")
										.append("reciept", "$reciept")
										.append("mode", "$mode")
										.append("consultationType", "$consultationType")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										.append("doctorName", new BasicDBObject("$first", "$doctorName"))
										
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
										
									
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("doctorId", new BasicDBObject("$first", "$doctorId"))
										
										.append("locationId", new BasicDBObject("$first", "$locationId"))
										
										.append("city", new BasicDBObject("$first", "$city"))
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId"))
										.append("mode", new BasicDBObject("$first", "$mode"))
										.append("transactionId", new BasicDBObject("$first", "$transactionId"))
										.append("transactionStatus", new BasicDBObject("$first", "$transactionStatus"))
										.append("patientId", new BasicDBObject("$first", "$patientId"))
										.append("reciept", new BasicDBObject("$first", "$reciept"))
										.append("patientName", new BasicDBObject("$first", "$patientName"))
										.append("patientMobileNumber", new BasicDBObject("$first", "$patientMobileNumber"))
										)),
		
										
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
						Aggregation.unwind("doctorProfile"), 
						Aggregation.lookup("docter_cl", "doctorId","userId","doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality"),
						Aggregation.unwind("doctorSpeciality.specialities"),
						Aggregation.lookup("speciality_cl", "doctorSpeciality.specialities","_id","speciality"),
						Aggregation.unwind("speciality"),
						Aggregation.lookup("user_cl", "userId", "_id", "userProfile"),
						Aggregation.unwind("userProfile"), 
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "doctorClinic.locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("createdTime", "$createdTime")
										.append("mobileNumber", "$doctorProfile.mobileNumber")
										.append("emailAddress", "$doctorProfile.emailAddress")
										.append("razorPayAccountId", "$razorPayAccountId")
										.append("doctorId", "$doctorId")
										.append("locationId", "$doctorClinic.locationId")
										.append("city", "$location.city")
										.append("speciality", "$speciality.speciality")
										.append("transactionId", "$transactionId")
										.append("transactionStatus", "$transactionStatus")
										.append("patientId", "$userId")
										.append("consultationType", "$consultationType")
										.append("mode", "$mode")
										.append("reciept", "$reciept")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										.append("doctorName", new BasicDBObject("$first", "$doctorName"))
										
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
										
									
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("doctorId", new BasicDBObject("$first", "$doctorId"))
										.append("locationId", new BasicDBObject("$first", "$locationId"))
										.append("city", new BasicDBObject("$first", "$city"))
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId"))
										.append("mode", new BasicDBObject("$first", "$mode"))
										.append("reciept", new BasicDBObject("$first", "$reciept"))
										.append("transactionId", new BasicDBObject("$first", "$transactionId"))
										.append("transactionStatus", new BasicDBObject("$first", "$transactionStatus"))
										.append("patientId", new BasicDBObject("$first", "$patientId"))
										.append("consultationType", new BasicDBObject("$first", "$consultationType"))
										.append("patientName", new BasicDBObject("$first", "$patientName"))
										.append("patientMobileNumber", new BasicDBObject("$first", "$patientMobileNumber"))
										)),
		
																
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation,OnlineConsultionPaymentCollection.class, DoctorOnlineConsultation.class)
					.getMappedResults();
			

			
			
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		}
		
				return response;
		
	}

	@Override
	public OnlineConsultationSpecialityPrice addEditPrice(OnlineConsultationSpecialityPrice request) {
		OnlineConsultationSpecialityPrice response=null;
		try {
			
			OnlineConsultationSpecialityPriceCollection doctorOnlineConsultationPriceCollection=null;
			
			if(!DPDoctorUtils.anyStringEmpty(request.getId()))
			{
				doctorOnlineConsultationPriceCollection = doctorOnlineConsultationPriceRepository.findById(new ObjectId(request.getId())).orElse(null);
				 
				if(doctorOnlineConsultationPriceCollection==null)
				{
					throw new BusinessException(ServiceError.NotFound,"Id not present");
				}
				doctorOnlineConsultationPriceCollection.setCommonSymptoms(null);
				BeanUtil.map(request, doctorOnlineConsultationPriceCollection);
				
				doctorOnlineConsultationPriceCollection.setCreatedTime(new Date());
				doctorOnlineConsultationPriceCollection.setUpdatedTime(new Date());
				}
			else {
				
				doctorOnlineConsultationPriceCollection=new OnlineConsultationSpecialityPriceCollection();
				BeanUtil.map(request, doctorOnlineConsultationPriceCollection);
				doctorOnlineConsultationPriceCollection.setCreatedTime(new Date());
				doctorOnlineConsultationPriceCollection.setUpdatedTime(new Date());
				
			}
			
			doctorOnlineConsultationPriceRepository.save(doctorOnlineConsultationPriceCollection);

			response=new OnlineConsultationSpecialityPrice();
			BeanUtil.map(doctorOnlineConsultationPriceCollection,response);
			
			
			
		}
		catch (BusinessException e) {
			logger.error("Error while addEdit doctor OnlineConsultation Price details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit doctor OnlineConsultation Price details  " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<OnlineConsultationSpecialityPrice> getDoctorOnlineConsultationPriceList(int page, int size,
			String searchTerm) {
		
		List<OnlineConsultationSpecialityPrice> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("speciality.speciality").regex("^" + searchTerm, "i"),
						new Criteria("speciality.speciality").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
						
			}
			else {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
	
			}
			
			response = mongoTemplate.aggregate(aggregation,OnlineConsultationSpecialityPriceCollection.class,OnlineConsultationSpecialityPrice.class).getMappedResults();
			
		}
		catch (BusinessException e) {
			logger.error("Error while getting doctor OnlineConsultation Speciality Price details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctor OnlineConsultation Speciality Price details  " + e.getMessage());
		}
		return response;
	}
	
	
	
	@Override
	public Boolean updateTransactionStatus(TransactionStatusRequest request)
	{
		Boolean response=false;
		try {
			OnlineConsultionPaymentCollection payment=onlineConsultationPaymentRepository.findByReciept(request.getReceipt());
			if(payment !=null)
			{
				payment.setTransactionStatus(request.getTransactionStatus());
				payment.setUpdatedTime(new Date());
				onlineConsultationPaymentRepository.save(payment);
				response=true;
			}
			
		}catch (BusinessException e) {
			e.printStackTrace();
			logger.error(e + " Error Updating transaction Status");
			throw new BusinessException(ServiceError.Unknown, "Error Updating transaction Status");
		}
			return response;
		
	}


}
