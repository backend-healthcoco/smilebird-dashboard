package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTestPackage;
import com.dpdocter.beans.DiagnosticTestSamplePickUpSlot;
import com.dpdocter.beans.OrderDiagnosticTest;
import com.dpdocter.beans.PickUpSlot;
import com.dpdocter.collections.DiagnosticTestPackageCollection;
import com.dpdocter.collections.DiagnosticTestPickUpSlotCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OrderDiagnosticTestCollection;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiagnosticTestPackageRepository;
import com.dpdocter.repository.DiagnosticTestPickUpSlotRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OrderDiagnosticTestRepository;
import com.dpdocter.services.DiagnosticTestOrderService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DiagnosticTestOrderServicesimpl implements DiagnosticTestOrderService {

	private static Logger logger = LogManager.getLogger(DiagnosticTestOrderServicesimpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private DiagnosticTestPickUpSlotRepository diagnosticTestPickUpSlotRepository;
	
	@Autowired
	private OrderDiagnosticTestRepository orderDiagnosticTestRepository;

	@Autowired
	private DiagnosticTestPackageRepository diagnosticTestPackageRepository;
	
	@Autowired
	private LocationRepository locationRepository;

	@Override
	public List<DiagnosticTestSamplePickUpSlot> getDiagnosticTestSamplePickUpTimeSlots() {
		List<DiagnosticTestSamplePickUpSlot> response = new ArrayList<DiagnosticTestSamplePickUpSlot>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			
			Date date = new Date();
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(date);
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime slotStartDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			
			DateTime slotEndDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			
			DiagnosticTestPickUpSlotCollection pickUpSlotCollection = diagnosticTestPickUpSlotRepository.findAll().get(0);
			
			for(int i=0 ; i<7; i++) {
				
				DiagnosticTestSamplePickUpSlot slot = new DiagnosticTestSamplePickUpSlot();
				slot.setDay(Day.valueOf(slotStartDateTime.dayOfWeek().getAsText().toUpperCase()));
				slot.setSlotDate(slotStartDateTime.getMillis());
				
				List<PickUpSlot> pickUpSlots = pickUpSlotCollection.getSlots().get(slot.getDay());
				
				if(pickUpSlots != null && !pickUpSlots.isEmpty()) {
					for(PickUpSlot pickUpSlot : pickUpSlots) {
						Integer noOfBookedSlots = orderDiagnosticTestRepository.countByDateAndTime(slotStartDateTime.getMillis(), slotEndDateTime.getMillis(), pickUpSlot.getFromTime());
						if(noOfBookedSlots != null && noOfBookedSlots >= pickUpSlot.getNoOfAppointmentsAllowed())pickUpSlot.setIsAvailable(false);
					}
					
					slot.setSlot(pickUpSlots);
					response.add(slot);
				}
				slotStartDateTime = slotStartDateTime.plusDays(1);
				slotEndDateTime = slotEndDateTime.plusDays(1);
			}
		}catch(Exception e){
			logger.error("Error while getting Diagnostic Test Sample Pick Up Slots "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting Diagnostic Test Sample Pick Up Slots.");
		}
		return response;
	}

	@Override
	public DiagnosticTestPickUpSlotCollection addEditPickUpTimeSlots(DiagnosticTestPickUpSlotCollection request) {
		DiagnosticTestPickUpSlotCollection response = null;
		try {
			List<DiagnosticTestPickUpSlotCollection> slotCollections = diagnosticTestPickUpSlotRepository.findAll();
			if(slotCollections == null || slotCollections.isEmpty()) {
				response = new DiagnosticTestPickUpSlotCollection();
			}
			else response = slotCollections.get(0);
			
			response.setSlots(request.getSlots());
			response = diagnosticTestPickUpSlotRepository.save(response);
			response.setId(null);
		}catch(Exception e){
			logger.error("Error while adding Diagnostic Test Sample Pick Up Slots "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while adding Diagnostic Test Sample Pick Up Slots.");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest updateDiagnosticTestOrderStatus(String orderId, String status) {
		OrderDiagnosticTest response = null;
		try {
			OrderDiagnosticTestCollection orderDiagnosticTestCollection = orderDiagnosticTestRepository.findById(new ObjectId(orderId)).orElse(null);
			if(orderDiagnosticTestCollection != null) {
				orderDiagnosticTestCollection.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
				orderDiagnosticTestCollection.setUpdatedTime(new Date());
				orderDiagnosticTestCollection = orderDiagnosticTestRepository.save(orderDiagnosticTestCollection);
				
				response = new OrderDiagnosticTest();
				BeanUtil.map(orderDiagnosticTestCollection, response);
			}
		}catch(Exception e){
			logger.error("Error while changing the status of order Diagnostic Test Sample"+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while changing the status of order Diagnostic Test Sample");
		}
		return response;
	}

	@Override
	public List<OrderDiagnosticTest> getOrders(String locationId, String userId, int page, int size) {
		List<OrderDiagnosticTest> response = null;
		try {
			
			Criteria criteria = new Criteria();
			if(!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
			if(!DPDoctorUtils.anyStringEmpty(userId))criteria.and("userId").is(new ObjectId(userId));
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("patient_cl", "userId", "userId", "patient"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
							.append("locationId","$locationId")
							.append("userId","$userId")
							.append("uniqueOrderId","$uniqueOrderId")
							.append("pickUpTime","$pickUpTime")
							.append("pickUpDate","$pickUpDate")
							.append("testsPackageId","$testsPackageId")
							.append("diagnosticTests","$diagnosticTests")
							.append("pickUpAddress","$pickUpAddress")
							.append("orderStatus","$orderStatus")
							.append("totalCost","$totalCost")
							.append("totalCostForPatient","$totalCostForPatient")
							.append("totalSavingInPercentage","$totalSavingInPercentage")
							.append("isCancelled","$isCancelled")
							.append("createdTime","$createdTime")
							.append("updatedTime","$updatedTime")
							.append("patientName","$patient.localPatientName")
							.append("locationName","$location.locationName")
							.append("isNABLAccredited","$location.isNABLAccredited"))),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("locationId", new BasicDBObject("$first","$locationId"))
							.append("userId", new BasicDBObject("$first","$userId"))
							.append("uniqueOrderId", new BasicDBObject("$first","$uniqueOrderId"))
							.append("pickUpTime", new BasicDBObject("$first","$pickUpTime"))
							.append("pickUpDate", new BasicDBObject("$first","$pickUpDate"))
							.append("testsPackageId", new BasicDBObject("$first","$testsPackageId"))
							.append("diagnosticTests", new BasicDBObject("$first","$diagnosticTests"))
							.append("pickUpAddress", new BasicDBObject("$first","$pickUpAddress"))
							.append("orderStatus", new BasicDBObject("$first","$orderStatus"))
							.append("totalCost", new BasicDBObject("$first","$totalCost"))
							.append("totalCostForPatient", new BasicDBObject("$first","$totalCostForPatient"))
							.append("totalSavingInPercentage", new BasicDBObject("$first","$totalSavingInPercentage"))
							.append("isCancelled", new BasicDBObject("$first","$isCancelled"))
							.append("patientName", new BasicDBObject("$first","$patientName"))
							.append("updatedTime", new BasicDBObject("$first","$updatedTime"))
							.append("createdTime", new BasicDBObject("$first","$createdTime"))
							.append("locationName", new BasicDBObject("$first","$locationName"))
							.append("isNABLAccredited", new BasicDBObject("$first","$isNABLAccredited")))),
					(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
					(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()),	
					new CustomAggregationOperation(new Document("$sort", new BasicDBObject("updatedTime", -1)))
					);
			
			response = mongoTemplate.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class).getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting lab orders"+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting lab orders");
		}
		return response;
	}

	@Override
	public OrderDiagnosticTest getDiagnosticTestOrderById(String orderId) {
		OrderDiagnosticTest response = null;
		try {
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(orderId))),
					Aggregation.lookup("patient_cl", "userId", "userId", "patient"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$patient").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					new CustomAggregationOperation(new Document("$unwind", new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
							.append("locationId","$locationId")
							.append("userId","$userId")
							.append("uniqueOrderId","$uniqueOrderId")
							.append("pickUpTime","$pickUpTime")
							.append("pickUpDate","$pickUpDate")
							.append("testsPackageId","$testsPackageId")
							.append("diagnosticTests","$diagnosticTests")
							.append("pickUpAddress","$pickUpAddress")
							.append("orderStatus","$orderStatus")
							.append("totalCost","$totalCost")
							.append("totalCostForPatient","$totalCostForPatient")
							.append("totalSavingInPercentage","$totalSavingInPercentage")
							.append("isCancelled","$isCancelled")
							.append("createdTime","$createdTime")
							.append("updatedTime","$updatedTime")
							.append("patientName","$patient.localPatientName")
							.append("locationName","$location.locationName")
							.append("isNABLAccredited","$location.isNABLAccredited"))),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("locationId", new BasicDBObject("$first","$locationId"))
							.append("userId", new BasicDBObject("$first","$userId"))
							.append("uniqueOrderId", new BasicDBObject("$first","$uniqueOrderId"))
							.append("pickUpTime", new BasicDBObject("$first","$pickUpTime"))
							.append("pickUpDate", new BasicDBObject("$first","$pickUpDate"))
							.append("testsPackageId", new BasicDBObject("$first","$testsPackageId"))
							.append("diagnosticTests", new BasicDBObject("$first","$diagnosticTests"))
							.append("pickUpAddress", new BasicDBObject("$first","$pickUpAddress"))
							.append("orderStatus", new BasicDBObject("$first","$orderStatus"))
							.append("totalCost", new BasicDBObject("$first","$totalCost"))
							.append("totalCostForPatient", new BasicDBObject("$first","$totalCostForPatient"))
							.append("totalSavingInPercentage", new BasicDBObject("$first","$totalSavingInPercentage"))
							.append("isCancelled", new BasicDBObject("$first","$isCancelled"))
							.append("patientName", new BasicDBObject("$first","$patientName"))
							.append("updatedTime", new BasicDBObject("$first","$updatedTime"))
							.append("createdTime", new BasicDBObject("$first","$createdTime"))
							.append("locationName", new BasicDBObject("$first","$locationName"))
							.append("isNABLAccredited", new BasicDBObject("$first","$isNABLAccredited")))));
			
			List<OrderDiagnosticTest> orderDiagnosticTests = mongoTemplate.aggregate(aggregation, OrderDiagnosticTestCollection.class, OrderDiagnosticTest.class).getMappedResults();
			if(orderDiagnosticTests != null && !orderDiagnosticTests.isEmpty())response = orderDiagnosticTests.get(0);
			else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid orderId");
			}
		}catch(Exception e){
			logger.error("Error while getting lab orders"+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting order");
		}
		return response;
	}

	@Override
	public DiagnosticTestPackage addEditDiagnosticTestPackage(DiagnosticTestPackage request) {
		DiagnosticTestPackage response = null;
		try {
			DiagnosticTestPackageCollection diagnosticTestPackageCollection = null;
			if(!DPDoctorUtils.allStringsEmpty(request.getId())) {
				diagnosticTestPackageCollection = diagnosticTestPackageRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setCreatedBy(diagnosticTestPackageCollection.getCreatedBy());
				request.setCreatedTime(diagnosticTestPackageCollection.getCreatedTime());
				request.setDiscarded(diagnosticTestPackageCollection.getDiscarded());
			}
			
			if(diagnosticTestPackageCollection == null) {
				diagnosticTestPackageCollection = new DiagnosticTestPackageCollection();
				LocationCollection location = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
				request.setCreatedBy(location.getLocationName());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, diagnosticTestPackageCollection);
			diagnosticTestPackageCollection.setUpdatedTime(new Date());
			diagnosticTestPackageCollection = diagnosticTestPackageRepository.save(diagnosticTestPackageCollection);
			response = new DiagnosticTestPackage();
			BeanUtil.map(diagnosticTestPackageCollection, response);
		} catch (Exception e) {
			logger.error("Error while adding diagnostic test package" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding diagnostic test package");
		}
		return response;
	}

	@Override
	public List<DiagnosticTestPackage> getDiagnosticTestPackages(String locationId, String hospitalId, Boolean discarded, int page, int size) {
		List<DiagnosticTestPackage> response = null; 
		try {
			Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if(!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if(!discarded)criteria.and("discarded").is(false);
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("testIds"),
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),Aggregation.unwind("location"),
										Aggregation.lookup("diagnostic_test_cl", "testIds", "_id", "tests"),Aggregation.unwind("tests"),
										new CustomAggregationOperation(new Document("$project", 
												new BasicDBObject("id", "$_id")
												.append("locationId", "$locationId")
												.append("locationName", "$location.locationName")
												.append("isNABLAccredited", "$location.isNABLAccredited")
												.append("hospitalId", "$hospitalId").append("packageName", "$packageName")
												.append("explanation", "$explanation")
												.append("discarded", "$discarded")
												.append("diagnosticTestPackageCost", "$diagnosticTestPackageCost")
												.append("diagnosticTestCostPackageForPatient", "$diagnosticTestCostPackageForPatient")
												.append("totalSavingInPercentage", new BasicDBObject("$multiply", 
														Arrays.asList(new BasicDBObject("$divide", Arrays.asList(new BasicDBObject("$subtract", Arrays.asList("$diagnosticTestPackageCost","$diagnosticTestCostPackageForPatient")),"$diagnosticTestPackageCost")), 100)))
												.append("testId", "$tests._id")
												.append("testName", "$tests.testName")
												.append("createdTime", "$createdTime")
												.append("updatedTime", "$updatedTime")
												.append("createdBy", "$createdBy"))),
										new CustomAggregationOperation(new Document("$group", 
												new BasicDBObject("id", "$_id")
												.append("locationId", new BasicDBObject("$first","$locationId"))
												.append("hospitalId", new BasicDBObject("$first","$hospitalId"))
												.append("packageName", new BasicDBObject("$first","$packageName"))
												.append("locationName", new BasicDBObject("$first","$locationName"))
												.append("isNABLAccredited", new BasicDBObject("$first","$isNABLAccredited"))
												.append("explanation", new BasicDBObject("$first","$explanation"))
												.append("discarded", new BasicDBObject("$first","$discarded"))
												.append("diagnosticTestPackageCost", new BasicDBObject("$first","$diagnosticTestPackageCost"))
												.append("diagnosticTestCostPackageForPatient", new BasicDBObject("$first","$diagnosticTestCostPackageForPatient"))
												.append("totalSavingInPercentage", new BasicDBObject("$first","$totalSavingInPercentage"))
												.append("testIds", new BasicDBObject("$push","$testId"))
												.append("testNames", new BasicDBObject("$push","$testName"))
												.append("createdTime", new BasicDBObject("$first","$createdTime"))
												.append("updatedTime", new BasicDBObject("$first","$updatedTime"))
												.append("createdBy", new BasicDBObject("$first","$createdBy")))),
												(size > 0) ? Aggregation.skip(page * size) : Aggregation.match(new Criteria()),
												(size > 0) ? Aggregation.limit(size) : Aggregation.match(new Criteria()));
			
			response = mongoTemplate.aggregate(aggregation, DiagnosticTestPackageCollection.class, DiagnosticTestPackage.class).getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting diagnostic test package" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting diagnostic test package");
		}
		return response;
	}

}
