package com.dpdocter.services.impl;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
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
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AppointmentPaymentInfo;
import com.dpdocter.beans.AppointmentPaymentTransferResponse;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.AppointmentPaymentCollection;
import com.dpdocter.collections.AppointmentPaymentTransferCollection;
import com.dpdocter.collections.BankDetailsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentPaymentTransferRepository;
import com.dpdocter.repository.BankDetailsRepository;
import com.dpdocter.request.AppointmentPaymentTransferRequest;
import com.dpdocter.response.AppointmentResponse;
import com.dpdocter.services.AppointmentPaymentService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.razorpay.RazorpayClient;
import com.razorpay.Transfer;

import common.util.web.DPDoctorUtils;

@Service
public class AppointmentPaymentServiceImpl implements AppointmentPaymentService {

	private static Logger logger = LogManager.getLogger(AppointmentPaymentServiceImpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${razorpay.api.secret}")
	private String secret;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${razorpay.api.key}")
	private String keyId;
	
	
	@Autowired
	private MailService mailService;
	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	
	@Autowired
	private AppointmentPaymentTransferRepository  appointmentPaymentTranferRepository;


	
	@Override
	public List<AppointmentPaymentInfo> getAppointmentPaymentInfo(int page, int size, String searchTerm, String city,
			String specialities, String transactionStatus, String fromDate, String toDate) {
		
		
		List<AppointmentPaymentInfo> response=null;
		try {
			
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("speciality.speciality").regex("^" + searchTerm, "i"),
						new Criteria("speciality.speciality").regex("^" + searchTerm));

			
			
			
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

			
			if (!DPDoctorUtils.anyStringEmpty(transactionStatus)) {
			criteria.and("transactionStatus").is(transactionStatus);
			}
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.lookup("user_cl", "userId", "_id", "userProfile"),
						Aggregation.unwind("userProfile"), 
						Aggregation.lookup("speciality_cl", "specialityId","_id","speciality"),
						Aggregation.unwind("speciality"),
						
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								
										.append("createdTime", "$createdTime")
										
										.append("razorPayAccountId", "$razorPayAccountId")
										.append("appointmentId", "$appointmentId")
										.append("speciality", "$speciality.speciality")
										.append("specialityId", "$speciality._id")
								
										.append("transactionStatus", "$transactionStatus")
										.append("patientId", "$userId")
										.append("reciept", "$reciept")
										.append("mode", "$mode")
										.append("consultationType", "$consultationType")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("specialityId", new BasicDBObject("$first", "$specialityId"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId"))
										.append("mode", new BasicDBObject("$first", "$mode"))
										.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
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
						
						Aggregation.lookup("user_cl", "userId", "_id", "userProfile"),
						Aggregation.unwind("userProfile"), 
						Aggregation.lookup("speciality_cl", "specialityId","_id","speciality"),
						Aggregation.unwind("speciality"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								
										.append("createdTime", "$createdTime")
										
										.append("razorPayAccountId", "$razorPayAccountId")
										
										.append("speciality", "$speciality.speciality")
										.append("specialityId", "$speciality._id")
										.append("appointmentId", "$appointmentId")
										.append("transactionStatus", "$transactionStatus")
										.append("patientId", "$userId")
										.append("consultationType", "$consultationType")
										.append("mode", "$mode")
										.append("reciept", "$reciept")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id")
										.append("speciality", new BasicDBObject("$first", "$speciality"))
										.append("specialityId", new BasicDBObject("$first", "$specialityId"))
										.append("createdTime",new BasicDBObject("$first", "$createdTime"))
										.append("razorPayAccountId", new BasicDBObject("$first", "$razorPayAccountId"))
										.append("mode", new BasicDBObject("$first", "$mode"))
										.append("reciept", new BasicDBObject("$first", "$reciept"))
										.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
										.append("transactionStatus", new BasicDBObject("$first", "$transactionStatus"))
										.append("patientId", new BasicDBObject("$first", "$patientId"))
										.append("consultationType", new BasicDBObject("$first", "$consultationType"))
										.append("patientName", new BasicDBObject("$first", "$patientName"))
										.append("patientMobileNumber", new BasicDBObject("$first", "$patientMobileNumber"))
										)),
		
																
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation,AppointmentPaymentCollection.class,AppointmentPaymentInfo.class)
					.getMappedResults();

			
			
		}catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			
		}
		
				return response;
		
		
	}

	@Override
	public Boolean createPaymentTransfer(AppointmentPaymentTransferRequest request) {
		Boolean response = false;
		List<Transfer> transferResponse = null;
		try {
			RazorpayClient rayzorpayClient = new RazorpayClient(keyId, secret);
			JSONObject orderRequest = new JSONObject();
			BankDetailsCollection doctor = bankDetailsRepository.findByDoctorId(new ObjectId(request.getDoctorId()));
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Doctor not found");
			}
			request.setAccount(doctor.getRazorPayAccountId());
			
			
			
			
			orderRequest.put("account",request.getAccount());
			orderRequest.put("amount", request.getAmount());
			orderRequest.put("currency", request.getCurrency());
			
			
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(orderRequest);

			//	account = rayzorpayClient.VirtualAccounts.create(orderRequest);
				
				String url="https://api.razorpay.com/v1/transfers";
				 String authStr=keyId+":"+secret;
				 String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				
				con.setDoOutput(true);
				
				System.out.println(con.getErrorStream());
				con.setDoInput(true);
				// optional default is POST
				con.setRequestMethod("POST");
//				con.setRequestProperty("User-Agent",
//						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
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
					  AppointmentPaymentTransferResponse list = mapper.readValue(output.toString(),AppointmentPaymentTransferResponse.class);
						
					  AppointmentPaymentTransferCollection collection=null;
						if(list!=null)
						{
							collection =new AppointmentPaymentTransferCollection();
							
							BeanUtil.map(list,collection);
							collection.setDoctorId(new ObjectId(request.getDoctorId()));
							collection.setUserId(new ObjectId(request.getUserId()));
							collection.setCreatedTime(new Date());
							collection.setUpdatedTime(new Date());
							collection.setIsPaymentTransfer(true);
							appointmentPaymentTranferRepository.save(collection);
							response=true;
						}
						
			

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();

		}
		return response;

	}
	
	@Override
	public List<AppointmentResponse> getAppointmentRequest(int size, int page,String searchTerm,String transactionStatus) {
		List<AppointmentResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("specialities.speciality").regex("^" + searchTerm, "i"),
						new Criteria("specialities.speciality").regex("^" + searchTerm),
						new Criteria("localPatientName").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm));
			
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
					
					.append("appointmentId", "$appointmentId")
					.append("problemDetailsId", "$problemDetailsId")		
					.append("transactionId", "$transactionId")
					.append("transactionStatus", "$transactionStatus")
					.append("speciality", "$specialities.speciality")
					.append("specialityId", "$specialityId")
					.append("consultationType.consultationType", "$consultationType.consultationType")
					.append("consultationType.cost", "$consultationType.cost")
					.append("consultationType.healthcocoCharges", "$consultationType.healthcocoCharges")					
					.append("amount", "$amount")
					.append("discountAmount", "$discountAmount")
					.append("mode", "$mode")
					.append("orderId", "$orderId")
					.append("reciept", "$reciept")
					.append("isAnonymousAppointment", "$isAnonymousAppointment")
					.append("localPatientName", "$localPatientName")
					.append("patientId", "$patientId")
					.append("state", "$state")
					.append("type", "$type")
					.append("time", "$time")
					.append("fromDate", "$fromDate")
					.append("createdTime", "$createdTime")
					.append("updatedTime", "$updatedTime")
					.append("toDate", "$toDate")));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("speciality_cl", "specialityId","_id", "specialities"),
						project,
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("speciality_cl", "specialityId","_id", "specialities"),
						project,
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,AppointmentPaymentCollection.class, AppointmentResponse.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting user appointment request " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting user appointment request " + e.getMessage());

		}
		return response;

	}


	
}
