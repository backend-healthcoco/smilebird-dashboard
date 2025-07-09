package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.LabTestType;
import com.dpdocter.beans.MedicineOrder;

import com.dpdocter.beans.RtpcrFileRequest;

import com.dpdocter.beans.RtpcrFileResponse;

import com.dpdocter.beans.RtpcrTest;
import com.dpdocter.beans.RtpcrTestResponse;
import com.dpdocter.collections.MedicineOrderCollection;
import com.dpdocter.collections.PaymentTransferCollection;
import com.dpdocter.collections.RtpcrTestCollection;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.RtpcrTestRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.RtpcrTestService;
import com.dpdocter.webservices.RtpcrApi;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class RtpcrTestServiceImpl implements RtpcrTestService{
	
	private static Logger logger = LogManager.getLogger(RtpcrTestServiceImpl.class.getName());
	
	@Autowired
	private RtpcrTestRepository rtpcrTestRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private FileManager fileManager;

	
	@Value(value = "${image.path}")
	private String imagePath;


	@Override
	public RtpcrTest addEditTest(RtpcrTest request) {
		RtpcrTest response=null;
		try {
		
			RtpcrTestCollection collection=null;
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
		    collection=rtpcrTestRepository.findById(new ObjectId(request.getId())).orElse(null);
			
		    if(collection==null)
		    	throw new BusinessException(ServiceError.NoRecord, "Record not found");
			
		    	collection.setReports(null);

		    	collection.setLabTestType(null);

				BeanUtil.map(request, collection);
				collection.setUpdatedTime(new Date());
			}
			else {
				collection=new RtpcrTestCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
			}
			
			rtpcrTestRepository.save(collection);
			response=new RtpcrTest();
			BeanUtil.map(collection, response);
			
		}
		catch (BusinessException e) {
			logger.error("Error while addEdit rtpcr test " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit rtpcr test"+e.getMessage());
		}
		return response;
	}

	@Override
	public RtpcrTestResponse getTest(int page, int size, String searchTerm,String fromDate, String toDate,String CollectionBoy,Boolean discarded) {

		RtpcrTestResponse response=null;
		try {
			List<RtpcrTest>	res=new ArrayList<RtpcrTest>();
			Criteria criteria = new Criteria();
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))
			{
				criteria.orOperator(new Criteria("patientName").regex("^" + searchTerm, "i"),

				new Criteria("hospitalName").regex("^" + searchTerm),
				new Criteria("hospitalName").regex("^" + searchTerm,"i"),

				new Criteria("patientName").regex("^" + searchTerm),
				new Criteria("patientMobileNumber").regex("^" + searchTerm),
				new Criteria("collectionBoy.name").regex("^" + searchTerm),
				new Criteria("collectionBoy.name").regex("^" + searchTerm,"i"),
				new Criteria("collectionBoy.mobileNumber").regex("^" + searchTerm));
			}
			
			if(!DPDoctorUtils.anyStringEmpty(CollectionBoy))
					{
				criteria.and("collectionBoy.name").regex("^" + CollectionBoy,"i");
					}
			if(discarded!=null)
				criteria.and("discarded").is(discarded);

			
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


				
				
			Aggregation aggregation=null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

				res = mongoTemplate.aggregate(aggregation, RtpcrTestCollection.class, RtpcrTest.class)
					.getMappedResults();
			
			RtpcrTestResponse total=totalAmountRtpcr(fromDate,toDate,CollectionBoy);
			
//				Double total=0.0;
//			for(RtpcrTest test:res)
//			{
//				for(LabTestType lab:test.getLabTestType())
//				{
//					total=lab.getAmount()+total;
//				}
//			}
//			
//			response.setTotalAmountCollected(total);
//			

			
			if(res!=null && !res.isEmpty())
			{
			response=new RtpcrTestResponse();
			response.setRtpcr(res);
			
			response.setTotalAmountCollected(total.getTotalAmountCollected());
			}

		}
		catch (Exception e) {
			logger.error("Error while getting rtpcr test " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting rtpcr test"+e.getMessage());
		}
		return response;
		
	}
	
	RtpcrTestResponse totalAmountRtpcr(String fromDate, String toDate,String collectionBoy)
	{

		RtpcrTestResponse resp=null;
		try {

		Criteria criteria1 = new Criteria();
		Aggregation aggregation=null;
		
		if(!DPDoctorUtils.anyStringEmpty(collectionBoy))
		{
			criteria1.and("collectionBoy.name").regex("^" + collectionBoy,"i");
		}
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
				criteria1.and("createdTime").gte(fromDateTime).lte(toDateTime);

			} else if (fromDateTime != null) {
				criteria1.and("createdTime").gte(fromDateTime);

			} else if (toDateTime != null) {
				criteria1.and("createdTime").lte(toDateTime);

			}

			

			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
							.append("totalAmountReceivedByChat", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$payment.consultationType.consultationType",
															ConsultationType.CHAT.getType()))).append("then", "$amount")
																	.append("else", 0)))
							.append("totalAmountReceivedByVideo", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$payment.consultationType.consultationType",
															ConsultationType.VIDEO.getType())))
																	.append("then", "$amount").append("else", 0)))

							// .append("consultationType.consultationType",
							// "$consultationType.consultationType")
							// .append("consultationType.cost", "$consultationType.cost")
							// .append("consultationType.healthcocoCharges",
							// "$consultationType.healthcocoCharges")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")));

			CustomAggregationOperation project2 = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
							// .append("totalAmountReceivedByChat", "$totalAmountReceivedByChat")
							
							.append("totalAmountCollected", "$totalAmountCollected")

							// .append("consultationType.consultationType",
							// "$consultationType.consultationType")
							// .append("consultationType.cost", "$consultationType.cost")
							// .append("consultationType.healthcocoCharges",
							// "$consultationType.healthcocoCharges")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",

					new BasicDBObject("_id", "_id")
							.append("totalAmountCollected",
									new BasicDBObject("$sum", "$labTestType.amount"))
							// .append("consultationType", new BasicDBObject("$addToSet",
							// "$consultationType"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria1),
					Aggregation.unwind("labTestType"),
					  group,

					 project2,
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			// }

			System.out.println("Aggregation"+aggregation);

			 resp = mongoTemplate.aggregate(aggregation, RtpcrTestCollection.class, RtpcrTestResponse.class)
				.getUniqueMappedResult();
			
		}catch (Exception e) {
				logger.error("Error while getting rtpcr calculation part " + e.getMessage());
				throw new BusinessException(ServiceError.Unknown, "Error while getting rtpcr calculation part"+e.getMessage());
			}

		
		return resp;
		
	}

	@Override
	@Transactional
	public RtpcrFileResponse uploadRtpcrImage(RtpcrFileRequest request) {
		RtpcrFileResponse response=new RtpcrFileResponse();
		try {
			
			if (request.getFile() != null) {
				request.getFile().setFileName(request.getFile().getFileName() + (new Date()).getTime());
				String path = "rtpcr" ;
				ImageURLResponse file = fileManager.saveImageAndReturnImageUrl(request.getFile(), path,
						true);
				
				String shortUrl = DPDoctorUtils.urlShortner(getFinalImageURL(file.getImageUrl()));
				
				file.setImageUrl(getFinalImageURL(file.getImageUrl()));
				response.setImageBitLink(shortUrl);
				file.setThumbnailUrl(getFinalImageURL(file.getThumbnailUrl()));
				if(file!=null)
				{
					response.setFile(file);
					response.setFilename(request.getFile().getFileName());
					response.setFileType(request.getFile().getFileExtension());
				}

			}
			
		}catch (Exception e) {
			// TODO: handle exception
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
	public Boolean discardOrder(String id, Boolean discarded) {
		Boolean status = false;
		RtpcrTestCollection rtpcrTestCollection = null;

		try {

			rtpcrTestCollection = rtpcrTestRepository.findById(new ObjectId(id)).orElse(null);

			if (rtpcrTestCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			rtpcrTestCollection.setDiscarded(discarded);
			rtpcrTestCollection = rtpcrTestRepository.save(rtpcrTestCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return status;
	}

}
