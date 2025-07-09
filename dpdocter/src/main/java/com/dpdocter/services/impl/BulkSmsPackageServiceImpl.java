package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsCreditsRequest;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BulkSmsCreditsRepository;
import com.dpdocter.repository.BulkSmsHistoryRepository;
import com.dpdocter.repository.BulkSmsPackageRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.services.BulkSmsPackageServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class BulkSmsPackageServiceImpl implements BulkSmsPackageServices{

	@Autowired
	private BulkSmsPackageRepository bulkSmsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private BulkSmsCreditsRepository bulkSmsCreditRepository;

	@Autowired
	private DoctorRepository  doctorRepository;
	
	@Autowired
	private BulkSmsHistoryRepository bulkSmsHistoryRepository;

	@Override
	public BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
				bulkSms=bulkSmsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				BeanUtil.map(request, bulkSms);
				bulkSms.setUpdatedTime(new Date());
			}else {
				bulkSms=new BulkSmsPackageCollection();
				BeanUtil.map(request, bulkSms);
				bulkSms.setCreatedTime(new Date());
				bulkSms.setUpdatedTime(new Date());
				
			}
			bulkSmsRepository.save(bulkSms);
			response=new BulkSmsPackage();
			BeanUtil.map(bulkSms, response);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while addEdit Bulksms package"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsPackage> getBulkSmsPackage(int page, int size, String searchTerm, Boolean discarded) {
		List<BulkSmsPackage> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			if(discarded !=null)
			criteria.and("discarded").is(discarded);
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsPackageCollection.class, BulkSmsPackage.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
		return response;
	}

	@Override
	public BulkSmsPackage getBulkSmsPackageById(String bulkSmsId) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(bulkSmsId)) {
				bulkSms=bulkSmsRepository.findById(new ObjectId(bulkSmsId)).orElse(null);
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				response=new BulkSmsPackage();
				BeanUtil.map(bulkSms, response);
			}
	}
		catch (BusinessException e) {
		e.printStackTrace();
		throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
	}
	return response;
}

	@Override
	public Integer CountBulkSmsPackage(String searchTerm, Boolean discarded) {
		Integer count=null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			
			count=(int) mongoTemplate.count(new Query(criteria), BulkSmsPackageCollection.class);
		
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return count;
	}

	@Override
	public BulkSmsCredits getCreditsByDoctorId(String doctorId) {
		BulkSmsCredits response=null;
		try {
			BulkSmsCreditsCollection bulk=new BulkSmsCreditsCollection();
			bulk=bulkSmsCreditRepository.findByDoctorId(new ObjectId(doctorId));
			
			if(bulk==null)
			{
				throw new BusinessException(ServiceError.Unknown,"DoctorId not found for bulk sms");
			}
			response=new BulkSmsCredits();
			BeanUtil.map(bulk,response);
			
			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms credits count"+ e.getMessage());
		}
		return response;
	}
	
	@Override
	public Boolean addBulkSmsCredits(BulkSmsCreditsRequest request) {
		Boolean response=false;
		try {
			DoctorCollection doctorCollections = null;
			doctorCollections = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			BulkSmsCredits credit=new BulkSmsCredits();
			BulkSmsHistoryCollection history=new BulkSmsHistoryCollection();
			if (doctorCollections != null)
			{
				BeanUtil.map(request,credit);
				doctorCollections.setBulkSmsCredit(credit);
				
				Long creditBalance=doctorCollections.getBulkSmsCredit().getCreditBalance();
				System.out.println("creditBalance:"+creditBalance);
				creditBalance=creditBalance+request.getSmsPackage().getSmsCredit();
				System.out.println("creditBalance:"+creditBalance);
				doctorCollections.getBulkSmsCredit().setCreditBalance(creditBalance);
				
				BeanUtil.map(credit, history);
				history.setCreatedTime(new Date());
				history.setUpdatedTime(new Date());
				bulkSmsHistoryRepository.save(history);
//				doctorClinicProfileCollections.getBulkSmsCredit().setCreditBalance(request.getCreditBalance());
//				doctorClinicProfileCollections.getBulkSmsCredit().setDateOfTransaction(request.getDateOfTransaction());
//				doctorClinicProfileCollections.getBulkSmsCredit().setPaymentMode(request.getPaymentMode());
//				doctorClinicProfileCollections.getBulkSmsCredit().setSmsPackage(request.getSmsPackage());
				doctorCollections.setUpdatedTime(new Date());
				doctorRepository.save(doctorCollections);
				
				response=true;
			}
			
			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while addEdit Bulksms package"+ e.getMessage());
		}
		return response;
	}
	
	@Override
	public List<BulkSmsCredits> getBulkSmsHistory(int page, int size, String searchTerm, String doctorId,String locationId) {
		List<BulkSmsCredits> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if(!DPDoctorUtils.anyStringEmpty(locationId))
			{
				ObjectId locationObjectId=new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsHistoryCollection.class, BulkSmsCredits.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
			return response;
		}

	@Override
	public BulkSmsPackage deleteSmsPackage(String packageId, Boolean discarded) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			bulkSms=bulkSmsRepository.findById(new ObjectId(packageId)).orElse(null);
			if(bulkSms==null)
			{
				throw new BusinessException(ServiceError.Unknown,"Error Id not found");
				
			}
			bulkSms.setDiscarded(discarded);
			bulkSmsRepository.save(bulkSms);
			response=new BulkSmsPackage();
			BeanUtil.map(bulkSms, response);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while deleting Bulksms package"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsReport> getSmsReport(int page, int size, String doctorId, String locationId) {
		List<BulkSmsReport> response=null;
		try {
	Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if(!DPDoctorUtils.anyStringEmpty(locationId))
			{
				ObjectId locationObjectId=new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			
			
				criteria.and("type").is("BULK_SMS");
			
			
			
	
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
										
					.append("doctorId", "$doctorId")
					.append("locationId", "$locationId")					
					.append("smsDetails.sms.smsText", "$smsDetail.sms.smsText")
					.append("smsDetails.deliveryStatus", "$smsDetail.deliveryStatus")
					.append("smsDetails.sentTime", "$smsDetail.sentTime")
					.append("type", "$type")					
					.append("responseId", "$responseId")
					.append("delivered", "$delivered")
					.append("undelivered", "$undelivered")	
					.append("totalCreditsSpent", "$totalCreditSpent")));
//			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("doctorId", new BasicDBObject("$first", "$doctorId"))
					.append("locationId", new BasicDBObject("$first", "$locationId"))
					.append("smsDetails", new BasicDBObject("$first", "$smsDetails"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("responseId", new BasicDBObject("$first", "$responseId"))
						.append("delivered", new BasicDBObject("$first", "$delivered"))
						.append("undelivered", new BasicDBObject("$first", "$undelivered"))
						.append("totalCreditsSpent", new BasicDBObject("$first", "$totalCreditsSpent"))));

//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
//						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				}
			
				System.out.println("Aggregation:"+aggregation);
				response = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, BulkSmsReport.class).getMappedResults();

//				CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//						new BasicDBObject("_id", "$_id")
//						.append("smsDetails", new BasicDBObject("$addToSet", "$smsDetails"))
//						));

				
				for(BulkSmsReport credit:response)
				{
					Long total=(long) credit.getSmsDetails().size();
					Integer totalLength=160; 
					String message=credit.getSmsDetails().get(0).getSms().getSmsText();
					  Integer messageLength=message.length();
					  System.out.println("messageLength:"+messageLength);
					  long credits=(messageLength/totalLength);
					  
					  long temp=messageLength%totalLength;
					  if(credits==0 || temp!=0) 
					  credits=credits+1;
					
					  long subCredits=credits*(total);
					
					Long count= mongoTemplate.count(new Query(new Criteria("smsDetails.deliveryStatus").is("DELIVERED").andOperator(criteria)),SMSTrackDetail.class);
					credit.setDelivered(count);
					credit.setUndelivered(total-count);
					credit.setTotalCreditsSpent(subCredits);
				}
//			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return response;

	}

}
