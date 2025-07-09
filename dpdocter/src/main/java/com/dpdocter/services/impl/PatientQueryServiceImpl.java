package com.dpdocter.services.impl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientQuery;
import com.dpdocter.beans.PatientQueryRequest;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;

import com.dpdocter.collections.PatientQueryCollection;
import com.dpdocter.collections.SMSTrackDetail;

import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientQueryRepository;
import com.dpdocter.services.PatientQueryService;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PatientQueryServiceImpl implements PatientQueryService {

	private static Logger logger = LogManager.getLogger(PatientQueryServiceImpl.class.getName());
	
	@Autowired
	private PatientQueryRepository patientQueryRepository;
	
	@Autowired
	private SMSServices sMSServices;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	
	@Override
	public PatientQuery addEditPatientQuery(PatientQueryRequest request) {
		PatientQuery response = null;
		try {
			PatientQueryCollection patientQueryCollection = null;
			
				patientQueryCollection = new PatientQueryCollection();
				BeanUtil.map(request, patientQueryCollection);
			
				patientQueryCollection.setUpdatedTime(new Date());
				patientQueryCollection.setCreatedTime(new Date());
			
			patientQueryCollection = patientQueryRepository.save(patientQueryCollection);
			response = new PatientQuery();
			
			
			Criteria criteria = new Criteria();
			
			if(request.getPatientId() !=null)
			{
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));
			}
			
			if(request.getLocationId() !=null)
			{
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));
			}
			
			if(request.getDoctorId() !=null)
			{
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));
			}
			
			Aggregation	aggregation = Aggregation.newAggregation(
			Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
			Aggregation.unwind("doctorProfile"), 
		
			Aggregation.lookup("user_cl", "patientId", "_id", "userProfile"),
			Aggregation.unwind("userProfile"), 
			Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
			Aggregation.unwind("doctorClinic"),
			Aggregation.lookup("location_cl", "locationId", "_id", "location"),
			Aggregation.unwind("location"),
			Aggregation.match(criteria),
			new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
					.append("doctorName", "$doctorProfile.firstName")
							.append("patientName", "$userProfile.firstName")
							.append("patientMobileNumber", "$userProfile.mobileNumber")
							.append("doctorMobileNumber", "$doctorProfile.mobileNumber")
							.append("doctorId", "$doctorId")
							.append("createdTime", "$createdTime")
							.append("locationId", "$locationId")
							.append("locationName", "$location.locationName")
							.append("patient_problem", "$patient_problem")
							)));
			
			System.out.println("aggregation:"+aggregation);
		List<PatientQuery>patientQuery	=mongoTemplate.aggregate(aggregation,PatientQueryCollection.class,PatientQuery.class).getMappedResults();
			response=patientQuery.get(0);
			
			
	
			 SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				
			//	smsTrackDetail.setType(ComponentType.ONLINE_PAYMENT.getType());
				SMSDetail smsDetail = new SMSDetail();
				
			
				SMS sms = new SMS();
			
				String pattern = "dd/MM/yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				sms.setSmsText("Greetings from Healthcoco,"+" Dr. " + response.getDoctorName() + ", a patient "+ response.getPatientName() +" and his mobileno."+response.getPatientMobileNumber()+" is contacted you for the problem "+request.getPatient_problem()+" at clinic "+response.getLocationName()+" on Date:"+simpleDateFormat.format(patientQueryCollection.getCreatedTime()) + ".");

					SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(response.getDoctorMobileNumber());
				sms.setSmsAddress(smsAddress);
				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
				
			System.out.println("sms sent");


		} catch (BusinessException e) {
			logger.error("Error while add/edit patient query " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit patient query " + e.getMessage());
		}
	
		return response;
	}
	
	
	@Override
	public List<PatientQuery> getPatientQuery(int size, int page,String searchTerm,String speciality,String city) {
		List<PatientQuery> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("doctorProfile.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctorProfile.firstName").regex("^" + searchTerm),
						new Criteria("doctorProfile.mobileNumber").regex("^" + searchTerm),
						new Criteria("userProfile.mobileNumber").regex("^" + searchTerm),
						new Criteria("userProfile.firstName").regex("^" + searchTerm),

						new Criteria("userProfile.firstName").regex("^" + searchTerm,"i"));

			
			if (!DPDoctorUtils.anyStringEmpty(city))
			{
				criteria.and("doctorProfile.city").is(city);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(speciality))
			{
				criteria.and("speciality.speciality").is(speciality);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation =  Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
				//		Aggregation.unwind("doctorProfile"), 
						Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
						Aggregation.lookup("speciality_cl","doctor.specialities","_id","speciality"),
						Aggregation.lookup("user_cl", "patientId", "_id", "userProfile"),
				//		Aggregation.unwind("userProfile"), 
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
				//		Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "doctorClinic.locationId", "_id", "location"),
				//		Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber")
										.append("doctorMobileNumber", "$doctorProfile.mobileNumber")
										.append("doctorId", "$doctorId")
										.append("patientId", "$patientId")
										.append("createdTime", "$createdTime")
										.append("locationId", "$locationId")
										.append("locationName", "$location.locationName")
										.append("patient_problem", "$patient_problem")
										.append("city", "$doctorProfile.city")
										.append("specialities", "$speciality.speciality")
										)),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctorProfile"),
				//		Aggregation.unwind("doctorProfile"), 
						Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
						Aggregation.lookup("speciality_cl","doctor.specialities","_id","speciality"),
						Aggregation.lookup("user_cl", "patientId", "_id", "userProfile"),
				//		Aggregation.unwind("userProfile"), 
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
				//		Aggregation.unwind("doctorClinic"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
			//			Aggregation.unwind("location"),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("_id", "$_id")
								.append("doctorName", "$doctorProfile.firstName")
										.append("patientName", "$userProfile.firstName")
										.append("patientMobileNumber", "$userProfile.mobileNumber")
										.append("doctorMobileNumber", "$doctorProfile.mobileNumber")
										.append("doctorId", "$doctorId")
										.append("createdTime", "$createdTime")
										.append("locationId", "$locationId")
										.append("patientId", "$patientId")
										.append("locationName", "$location.locationName")
										.append("patient_problem", "$patient_problem")
										.append("city", "$doctorProfile.city")
										.append("specialities", "$speciality.speciality")
										)),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			System.out.println("aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation,PatientQueryCollection.class, PatientQuery.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting patient query " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient query " + e.getMessage());

		}
		return response;

	}

}
