package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyCautionMoney;
import com.dpdocter.beans.CollectionBoyCautionMoneyHistory;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderItems;
import com.dpdocter.beans.MedicineOrderRxImageRequest;
import com.dpdocter.beans.RxNotes;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.Vendor;
import com.dpdocter.collections.CollectionBoyCautionMoneyCollection;
import com.dpdocter.collections.CollectionBoyCautionMoneyHistoryCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.DrugInfoCollection;
import com.dpdocter.collections.MedicineOrderCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TrackingOrderCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.VendorCollection;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.TransactionEnum;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CollectionBoyCautionMoneyHistoryRepository;
import com.dpdocter.repository.CollectionBoyCautionMoneyRepository;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.MedicineOrderRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.TrackingOrderRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.VendorRepository;
import com.dpdocter.request.AddEditVendorRequest;
import com.dpdocter.request.EditDeliveryBoyRequest;
import com.dpdocter.request.MedicineOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderAssignDeliveryBoyRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;

import com.dpdocter.request.MedicineOrderVendorAssignRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MedicineOrderService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.tokenstore.CustomPasswordEncoder;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class MedicineOrderServiceImpl implements MedicineOrderService {

	private static Logger logger = LogManager.getLogger(RecordsServiceImpl.class.getName());

	@Autowired
	FileManager fileManager;

	@Autowired
	private MedicineOrderRepository medicineOrderRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private VendorRepository vendorRepository;

	@Value(value = "${image.path}")
	private String imagePath;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SMSServices smsServices;
	
	@Autowired
	private CollectionBoyRepository collectionBoyRepository;
	
	@Autowired
	private TrackingOrderRepository trackingOrderRepository;
	
	@Autowired
	private CustomPasswordEncoder passwordEncoder;
	
	@Autowired
	private CollectionBoyCautionMoneyRepository collectionBoyCautionMoneyRepository;
	
	@Autowired
	private CollectionBoyCautionMoneyHistoryRepository collectionBoyCautionMoneyHistoryRepository;
	
	@Autowired
	private PushNotificationServices pushNotificationServices;
	
	@Value(value = "${medicine.order.placed.message}")
	private String ORDER_PLACED_MESSAGE;
	@Value(value = "${medicine.order.confirmed.message}")
	private String ORDER_CONFIRMED_MESSAGE;
	@Value(value = "${medicine.order.dispatched.message}")
	private String ORDER_DISPATCHED_MESSAGE;
	@Value(value = "${medicine.order.picked.message}")
	private String ORDER_PICKED_MESSAGE;
	@Value(value = "${medicine.order.out.of.delivery.message}")
	private String ORDER_OUT_OF_DELIVERY_MESSAGE;
	@Value(value = "${medicine.order.delivered.message}")
	private String ORDER_DELIVERED_MESSAGE;
	@Value(value = "${medicine.order.packed.message}")
	private String ORDER_PACKED_MESSAGE;
	
	
	
	
	@Override
	@Transactional
	public ImageURLResponse uploadImage(MultipartFile file) {
		String recordPath = null;
		ImageURLResponse imageURLResponse = null;
		
		try {

			Date createdTime = new Date();
			if (file != null) {
				
				String path = "medorderRX" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path  + fileName + createdTime.getTime() +"." +fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
				
//				if(imageURLResponse != null)
//				{
//					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
//					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl()); 
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return imageURLResponse;
	}

	@Override
	@Transactional
	public MedicineOrder addeditRx(MedicineOrderRXAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if (request.getId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
				medicineOrderCollection.setUpdatedTime(new Date());
			}

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setCreatedTime(new Date());
				medicineOrderCollection.setUniqueOrderId(
						UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			//medicineOrderCollection.setPatientId(new ObjectId(request.getPatientId()));
			medicineOrderCollection.setDiscarded(request.getDiscarded());
			if(request.getTotalPrice()!=null) {
				medicineOrderCollection.setTotalPrice(request.getTotalPrice());
	
				medicineOrderCollection.setDiscountedPrice(request.getTotalPrice());
				}
			//if(request.getDiscountedPrice()!=null)
			//	medicineOrderCollection.setDiscountedPrice(request.getTotalPrice());
			
			if(request.getRxImage()!=null) {
				medicineOrderCollection.setRxImage(null);
			medicineOrderCollection.setRxImage(request.getRxImage());
			}
			medicineOrderCollection.setPatientId(new ObjectId(request.getPatientId()));
			if (request.getItems() != null) {
				List<MedicineOrderItems> items = new ArrayList<>();
				for (MedicineOrderAddEditItems addEditItems : request.getItems()) {
					MedicineOrderItems orderItems = new MedicineOrderItems();
					BeanUtil.map(addEditItems, orderItems);
					items.add(orderItems);
				}
				medicineOrderCollection.setItems(items);
			}
			
			if(request.getRxNotes() !=null)
			{
				medicineOrderCollection.setRxNotes(null);
				medicineOrderCollection.setRxNotes(request.getRxNotes());
			}

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);

			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	
	@Override
	@Transactional
	public MedicineOrder addeditRxImage(MedicineOrderRxImageRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
				medicineOrderCollection.setUpdatedTime(new Date());
			}

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
				medicineOrderCollection.setCreatedTime(new Date());
			}

			medicineOrderCollection.setDiscarded(request.getDiscarded());
			if(request.getRxImage()!=null)
			{
				medicineOrderCollection.setRxImage(null);
			medicineOrderCollection.setRxImage(request.getRxImage());
			}
			if(request.getRxNotes()!=null)
			{
				medicineOrderCollection.setRxNotes(null);
				medicineOrderCollection.setRxNotes(request.getRxNotes());
			}
				if(request.getTotalPrice()!=null) {
				medicineOrderCollection.setTotalPrice(request.getTotalPrice());
	
				medicineOrderCollection.setDiscountedPrice(request.getTotalPrice());
				}
			//medicineOrderCollection.setPatientId(new ObjectId(request.getPatientId()));
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public MedicineOrder addeditAddress(MedicineOrderAddEditAddressRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if (request.getId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(
						UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setShippingAddress(request.getShippingAddress());
			medicineOrderCollection.setBillingAddress(request.getBillingAddress());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public MedicineOrder addeditPayment(MedicineOrderPaymentAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if (request.getId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(
						UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setIsOrderPaid(request.getIsPaid());
			medicineOrderCollection.setTotalAmount(request.getTotalAmount());
			medicineOrderCollection.setDiscountedAmount(request.getDiscountedAmount());
			medicineOrderCollection.setDiscountedPercentage(request.getDiscountedPercentage());
			medicineOrderCollection.setFinalAmount(request.getFinalAmount());
			medicineOrderCollection.setDeliveryCharges(request.getDeliveryCharges());
			medicineOrderCollection.setNotes(request.getNotes());
			medicineOrderCollection.setPaymentMode(request.getPaymentMode());
			medicineOrderCollection.setCallingPreference(request.getCallingPreference());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public MedicineOrder addeditPreferences(MedicineOrderPreferenceAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if (request.getId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(
						UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setDeliveryPreference(request.getDeliveryPreference());
			medicineOrderCollection.setNextDeliveryDate(request.getNextDeliveryDate());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public MedicineOrder updateStatus(String id, OrderStatus status) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;
		String message = "";
		String templateId=null;
		TrackingOrder trackingOrder = null;
		try {

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setOrderStatus(status);
			
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}
			switch (status) {
			case PLACED:
				System.out.println("Inside Placed");

				String prescriptionDetails = "";
				int i = 0;
				if (medicineOrderCollection.getRxNotes() != null && !medicineOrderCollection.getRxNotes().isEmpty())
					for (RxNotes rxnotes : medicineOrderCollection.getRxNotes()) {
						if (rxnotes != null) {

							i++;

							String drugType = rxnotes.getOrderType() != null
									? (!DPDoctorUtils.anyStringEmpty(rxnotes.getOrderType()) ? rxnotes.getOrderType()
											: "")
									: "";
							String drugName = !DPDoctorUtils.anyStringEmpty(rxnotes.getOrderName())
									? rxnotes.getOrderName()
									: "";

							int durationValue = rxnotes.getOrderQty().getNumber() != 0
									? rxnotes.getOrderQty().getNumber()
									: 0;

							String durationUnit = "";
							if (rxnotes.getOrderQty() != null && rxnotes.getOrderQty().getType()!=null)
								durationUnit = rxnotes.getOrderQty().getType().getType() != null
										? rxnotes.getOrderQty().getType().getType()

										: "";

							Double price = rxnotes.getPrice() != null ? rxnotes.getPrice()

									: 0;

							prescriptionDetails = prescriptionDetails + "\n" + " " + i + ")" + drugType + " "

									+ drugName+" " + durationValue+" " + durationUnit;
						}
					}

				 message = "Thank you for ordering with Healthcoco,Your OrderId is " + medicineOrderCollection.getUniqueOrderId()
						+ " for the drugs:" + prescriptionDetails
						+ ". Pharmacist is verifying your order.-Healthcoco";
				System.out.println(message);
				 templateId="1307161562134585802";

				System.out.println(message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message,
						ComponentType.ORDER_PLACED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()),
						medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				
				break;
				
			case CONFIRMED:
				message = ORDER_CONFIRMED_MESSAGE;
				System.out.println(message);
				 templateId="1307161675877658397";
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_CONFIRMED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				//pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_CONFIRMED.getType(), id, null);
				//sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				
				break;

				
			case PACKED:
				message = ORDER_PACKED_MESSAGE;
				System.out.println(message);
				templateId="1307161761593165557";
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_PACKED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				
				break;

				
			case DISPATCHED:
				message = ORDER_DISPATCHED_MESSAGE;
				System.out.println(message);
				templateId="1307161675875956067";
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_DISPATCHED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				
				break;

				
			case OUT_FOR_DELIVERY:
				message = ORDER_OUT_OF_DELIVERY_MESSAGE;
				System.out.println(message);
				templateId="1307161675874247949";
				if(medicineOrderCollection.getCollectionBoy() != null)
				{
					message = message.replace("{deliveryBoy}", medicineOrderCollection.getCollectionBoy().getName());
				}
				else{
					message = message.replace("{deliveryBoy}", "our representative");
				}
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_OUT_FOR_DELIVERY.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				
				break;
				
			case DELIVERED:
				message = ORDER_DELIVERED_MESSAGE;
				System.out.println(message);
				templateId="1307161562820354029";
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_OUT_FOR_DELIVERY.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message,templateId);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;


			default:
				break;
			}

			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public MedicineOrder getOrderById(String id) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			UserCollection patient=userRepository.findById(medicineOrderCollection.getPatientId()).orElse(null);
			
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
				medicineOrder.setPatientName(patient.getFirstName());
				medicineOrder.setMobileNumber(patient.getMobileNumber());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}

	@Override
	@Transactional
	public Boolean discardOrder(String id, Boolean discarded) {
		Boolean status = false;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setDiscarded(discarded);
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return status;
	}

	@Override
	@Transactional
	public List<MedicineOrder> getOrderListByPatient(String patientId, String updatedTime, String searchTerm, int page,
			int size, List<String> status, Boolean isImageAvailable, Boolean isRXAvailable , Boolean discarded) {
		List<MedicineOrder> orders = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("uniqueOrderId").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}
			if (status != null) {
				criteria.and("orderStatus").in(status);
			}
			if (isImageAvailable) {
				criteria.and("rxImage").exists(true);
			}
			if (isRXAvailable) {
				criteria.and("items").exists(true);
			}
			
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} 

			orders = mongoTemplate.aggregate(aggregation, MedicineOrderCollection.class, MedicineOrder.class)
					.getMappedResults();
			UserCollection patient=userRepository.findById(new ObjectId(patientId)).orElse(null);
			for(MedicineOrder order:orders)
			{
				order.setPatientName(patient.getFirstName());
				order.setMobileNumber(patient.getMobileNumber());
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}

	@Override
	@Transactional
	public List<MedicineOrder> getOrderListByVendor(String vendorId, String updatedTime, String searchTerm, int page,
			int size) {
		List<MedicineOrder> orders = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(vendorId)) {
				criteria.and("vendorId").is(new ObjectId(vendorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("uniqueOrderId").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}

			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			orders = mongoTemplate.aggregate(aggregation, MedicineOrderCollection.class, MedicineOrder.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}

	@Override
	@Transactional
	public Vendor addEditVendor(AddEditVendorRequest request) {
		Vendor response = null;
		VendorCollection vendorCollection = null;
		UserCollection userCollection = null;
		try {
			
			 userCollection = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(),
					UserState.VENDOR.toString());

			if (userCollection == null) {
				
				//String pass = DPDoctorUtils.randomString(8);
				//System.out.println("Password :: "+pass);
				userCollection = new UserCollection();
				userCollection.setUserName(UniqueIdInitial.PHARMACY.getInitial() + request.getMobileNumber());
				userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
				userCollection.setIsVerified(true);
				userCollection.setIsActive(true);
				userCollection.setFirstName(request.getPharmacyName());
				userCollection.setCreatedTime(new Date());
				userCollection.setMobileNumber(request.getMobileNumber());
				userCollection.setUserState(UserState.VENDOR);
				
//				userCollection.setSalt(DPDoctorUtils.generateSalt());
//				String salt = new String(userCollection.getSalt());
//				char[] sha3Password = DPDoctorUtils.getSHA3SecurePassword(pass.toCharArray());
//				String password = new String(sha3Password);
				
				String password = passwordEncoder.encode(BCrypt.hashpw(request.getMobileNumber(), "$2a$12$HHBRV5pOMt9wQ9Ve.2mnhu"));
				userCollection.setPassword(password.toCharArray());
				userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
				userCollection = userRepository.save(userCollection);

				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("CB_SIGNUP");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				if (userCollection != null)
					smsDetail.setUserName(userCollection.getFirstName());
				SMS sms = new SMS();
				sms.setSmsText("Hi ," + userCollection.getFirstName()
						+ " your registration with Healthcoco is completed.");

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
				// response.setPassword(null);
				
				
			}
			
			if (request.getId() != null) {
				vendorCollection = vendorRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (vendorCollection == null) {
				vendorCollection = new VendorCollection();
			}
			if(request.getDocuments()!=null)
			{
				vendorCollection.setDocuments(null);
			}
			
			BeanUtil.map(request, vendorCollection);
			if(userCollection != null)
			{
				vendorCollection.setUserId(userCollection.getId());
			}
			vendorCollection = vendorRepository.save(vendorCollection);
			if (vendorCollection != null) {
				response = new Vendor();
				BeanUtil.map(vendorCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}

	@Override
	@Transactional
	public Vendor getVendorById(String id) {
		Vendor response = null;
		VendorCollection vendorCollection = null;
		try {
			if (id != null) {
				vendorCollection = vendorRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (vendorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not Found");
			}
			if (vendorCollection != null) {
				response = new Vendor();
				BeanUtil.map(vendorCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}

	@Override
	@Transactional
	public Vendor discardVendor(String id, Boolean discarded) {
		Vendor response = null;
		VendorCollection vendorCollection = null;
		try {
			if (id != null) {
				vendorCollection = vendorRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (vendorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not Found");
			}
			vendorCollection.setDiscarded(discarded);
			vendorCollection = vendorRepository.save(vendorCollection);
			if (vendorCollection != null) {
				response = new Vendor();
				BeanUtil.map(vendorCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}

	@Override
	@Transactional
	public List<Vendor> getVendorList(String updatedTime, String searchTerm, int page, int size, Boolean discarded) {
		List<Vendor> vendors = null;
		Aggregation aggregation = null;
		try {

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if(discarded != null){
				criteria.and("discarded").is(discarded);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm, "i"));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}

			vendors = mongoTemplate.aggregate(aggregation, VendorCollection.class, Vendor.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return vendors;
	}
	
	@Override
	@Transactional
	public Integer getVendorListCount(String updatedTime, String searchTerm, Boolean discarded) {
		Integer response = null;
		Long count = null;
		try {

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if(discarded != null){
				criteria.and("discarded").is(discarded);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm, "i"));
			}

			count = mongoTemplate.count(new Query(criteria), VendorCollection.class);
			if(count != null)
			{
				response = count.intValue();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public List<MedicineOrder> getOrderList(String updatedTime, String searchTerm, int page, int size, Boolean discarded , Long from , Long to ,  Boolean isImageAvailable, Boolean isRXAvailable,String orderStatus) {
		List<MedicineOrder> orders = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			//long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("uniqueOrderId").regex("^" + searchTerm, "i"),
						new Criteria("vendor.name").regex("^" + searchTerm, "i"),
						new Criteria("user.firstName").regex("^" + searchTerm, "i"),
						new Criteria("user.firstName").regex("^" + searchTerm));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(orderStatus)) {
				criteria.and("orderStatus").is(orderStatus);
			}
			
			if(discarded != null)
			{
				criteria.and("discarded").is(discarded);
			}
			
			if (isImageAvailable != null && isImageAvailable == true) {
				criteria.and("rxImage").exists(true);
			}
			if (isRXAvailable != null && isRXAvailable == true) {
				criteria.and("items").exists(true);
			}

			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime( new Date(from))).lte(DPDoctorUtils.getToTime( new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime( new Date(from)));
			} else if (to != 0) {
				criteria.and("updatedTime").lte(DPDoctorUtils.getToTime( new Date(to)));
			}
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
//								Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
//								new CustomAggregationOperation(new Document("$unwind",
//										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
//												true))),
								Aggregation.lookup("user_cl", "patientId","_id","user"),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(
//								Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
//								new CustomAggregationOperation(new Document("$unwind",
//										new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
//												true))),
								Aggregation.lookup("user_cl", "patientId","_id","user"),
								Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			System.out.println("Aggregation"+aggregation);
			orders = mongoTemplate.aggregate(aggregation, MedicineOrderCollection.class, MedicineOrder.class)
					.getMappedResults();
			
			
			for(MedicineOrder order:orders)
			{
				if(order.getPatientId()!=null) {
				UserCollection patient=userRepository.findById(new ObjectId(order.getPatientId())).orElse(null);
				if(patient!=null) {
				if(!DPDoctorUtils.anyStringEmpty(patient.getFirstName()))
				order.setPatientName(patient.getFirstName());
				if(!DPDoctorUtils.anyStringEmpty(patient.getMobileNumber()))
				order.setMobileNumber(patient.getMobileNumber());
				}
				 }
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}

	@Override
	@Transactional
	public MedicineOrder assignVendorToOrder(MedicineOrderVendorAssignRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;
		VendorCollection vendorCollection = null;
		try {

			if (request.getOrderId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getOrderId())).orElse(null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}

			if (request.getVendorId() != null) {
				vendorCollection = vendorRepository.findById(new ObjectId(request.getVendorId())).orElse(null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}

			if (vendorCollection != null && medicineOrderCollection != null) {
				medicineOrderCollection.setVendorId(vendorCollection.getId());
				medicineOrderCollection.setVendor(request.getVendor());
				medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, " Record not found");
			}

			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder assignDeliveryBoyToOrder(MedicineOrderAssignDeliveryBoyRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;
		CollectionBoyCollection collectionBoyCollection = null;
		try {

			if (request.getOrderId() != null) {
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getOrderId())).orElse(null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}

			if (request.getCollectionBoyId() != null) {
				collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(request.getCollectionBoyId())).orElse(null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}

			if (collectionBoyCollection != null && medicineOrderCollection != null) {
				//medicineOrderCollection.setVendorId(collectionBoyCollection.getId());
				medicineOrderCollection.setCollectionBoyId(collectionBoyCollection.getId());
				medicineOrderCollection.setCollectionBoy(request.getCollectionBoy());
				medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, " Record not found");
			}

			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	
	@Override
	@Transactional
	public List<CollectionBoyResponse> getCollectionBoyList(int size, int page, String locationId, String searchTerm , String labType , Boolean discarded) {
		List<CollectionBoyResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("name").regex(searchTerm, "i"));
			}
			if (!DPDoctorUtils.anyStringEmpty(labType))
			{
				criteria.and("labType").is(labType);
			}
			
			if(discarded != null)
			{
				criteria.and("discarded").is(discarded);
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTimes")));
			AggregationResults<CollectionBoyResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyCollection.class, CollectionBoyResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys");
		}
		return response;
	}
	
	@Override
	@Transactional
	public Integer getCollectionBoyListCount(String locationId, String searchTerm, String labType, Boolean discarded) {
		Integer response = null;
		Long count = null;
		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("name").regex(searchTerm, "i"));
			}

			if (!DPDoctorUtils.anyStringEmpty(labType)) {
				criteria.and("labType").is(labType);
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			count = mongoTemplate.count(new Query(criteria), CollectionBoyCollection.class);
			if(count != null)
			{
				response = count.intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys count");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys count");
		}
		return response;
	}


	@Override
	@Transactional
	public Integer getCBCount(String locationId, String searchTerm , String labType) {
		Integer count = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("name").regex(searchTerm, "i"));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(labType)) {
				criteria.and("labType").is(labType);
			}
			//criteria.and("locationId").is(new ObjectId(locationId));

			
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<CollectionBoy> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyCollection.class, CollectionBoy.class);
			count = aggregationResults.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys");
		}
		return count;
	}
	
	@Override
	@Transactional
	public CollectionBoyResponse getCollectionBoyById(String id) {
		CollectionBoyResponse response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		try {
			if (id != null) {
				collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (collectionBoyCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not Found");
			}
			if (collectionBoyCollection != null) {
				response = new CollectionBoyResponse();
				BeanUtil.map(collectionBoyCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}

	@Override
	@Transactional
	public CollectionBoy discardCollectionBoy(String id, Boolean discarded) {
		
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		try {
			if (id != null) {
				collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (collectionBoyCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not Found");
			}
			collectionBoyCollection.setDiscarded(discarded);
			collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
			if (collectionBoyCollection != null) {
				response = new CollectionBoy();
				BeanUtil.map(collectionBoyCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}
	
	@Override
	@Transactional
	public TrackingOrder addeditTrackingDetails(TrackingOrder request) {
		TrackingOrder trackingOrder = null;
		TrackingOrderCollection trackingOrderCollection = null;

		try {

			if(request.getId() != null)
			{
				trackingOrderCollection = trackingOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (trackingOrderCollection == null) {
				trackingOrderCollection = new TrackingOrderCollection();
				trackingOrderCollection.setCreatedTime(new Date());
				//medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			BeanUtil.map(request, trackingOrderCollection);

			trackingOrderCollection = trackingOrderRepository.save(trackingOrderCollection);
			
			if (trackingOrderCollection != null) {
				trackingOrder = new TrackingOrder();
				BeanUtil.map(trackingOrderCollection, trackingOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return trackingOrder;
	}
	
	@Override
	@Transactional
	public List<TrackingOrder> getTrackingList(String orderId , String updatedTime , String searchTerm, int page , int size) {
		List<TrackingOrder> orders = null;
		Aggregation aggregation =null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if (!DPDoctorUtils.anyStringEmpty(orderId)) {
				criteria.and("orderId").is(new ObjectId(orderId));
			}
			
			/*if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}*/
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
							
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						 Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
							
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			
			
			orders = mongoTemplate.aggregate(
					aggregation,
					TrackingOrderCollection.class, TrackingOrder.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}
	
	@Override
	public Integer getOrderListCount(String updatedTime, String searchTerm,  Boolean discarded , Long from , Long to) {
		Integer resposne = null;
		Long count = null;
		try {

			//long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("uniqueOrderId").regex("^" + searchTerm, "i"),
						new Criteria("vendor.name").regex("^" + searchTerm, "i"));
			}
			
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime( new Date(from))).lte(DPDoctorUtils.getToTime( new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime( new Date(from)));
			} else if (to != 0) {
				criteria.and("updatedTime").lte(DPDoctorUtils.getToTime( new Date(to)));
			}
			
			if(discarded != null)
			{
				criteria.and("discarded").is(discarded);
			}
			

			count = mongoTemplate.count(new Query(criteria), MedicineOrderCollection.class);

			if(count != null)
			{
				resposne = count.intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return resposne;
	}

	
	@Override
	@Transactional
	public CollectionBoyResponse editCollectionBoy(EditDeliveryBoyRequest request) {
		CollectionBoyResponse response = null;
		CollectionBoyCollection collectionBoyCollection = null;

		try {

			if (request.getId() != null) {
				collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (collectionBoyCollection == null) {
				throw new BusinessException(ServiceError.NoRecord , "Collecton Boy not found");
			}
			if(request.getDocuments()!=null)
			{
				collectionBoyCollection.setDocuments(null);
			}
			
			BeanUtil.map(request, collectionBoyCollection);
			
			collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
			if (collectionBoyCollection != null) {
				response = new CollectionBoyResponse();
				BeanUtil.map(collectionBoyCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<CollectionBoyCautionMoney> getCollectionBoyCautionMoneyHistory(String collectionBoyId, Long from, Long to, int page, int size)
	{
		List<CollectionBoyCautionMoney> cautionMoneys = null;
		Aggregation aggregation =null;
		try {
			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyId)) {
				criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			}
			
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime(new Date(from))).lte(DPDoctorUtils.getToTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(DPDoctorUtils.getFormTime(new Date(from)));
			} else if (to != 0) {
				criteria.and("updatedTime").lte(DPDoctorUtils.getToTime(new Date(to)));
			}
			
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
						 Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			
			cautionMoneys = mongoTemplate.aggregate(
					aggregation,
					CollectionBoyCautionMoneyCollection.class, CollectionBoyCautionMoney.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return cautionMoneys;
	}
	
	
	@Override
	@Transactional
	public Boolean discardCollectionBoyCautionMoney(String id, Boolean discarded) {
		
		Boolean response = false;
		CollectionBoyCautionMoneyHistoryCollection collectionBoyCautionMoneyHistoryCollection = null;
		try {
			if (id != null) {
				collectionBoyCautionMoneyHistoryCollection = collectionBoyCautionMoneyHistoryRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (collectionBoyCautionMoneyHistoryCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not Found");
			}
			collectionBoyCautionMoneyHistoryCollection.setDiscarded(discarded);
			collectionBoyCautionMoneyHistoryCollection = collectionBoyCautionMoneyHistoryRepository.save(collectionBoyCautionMoneyHistoryCollection);
			if (collectionBoyCautionMoneyHistoryCollection != null) {
				response = true;
				CollectionBoyCautionMoneyCollection collectionBoyCautionMoneyCollection = collectionBoyCautionMoneyRepository.findByCollectionBoyId(collectionBoyCautionMoneyHistoryCollection.getCollectionBoyId());
				if(collectionBoyCautionMoneyHistoryCollection.getTransactionType().equals(TransactionEnum.CREDIT))
				{
					collectionBoyCautionMoneyCollection.setRemainingMoney(collectionBoyCautionMoneyCollection.getRemainingMoney() - collectionBoyCautionMoneyHistoryCollection.getAmount());
				}
				else if(collectionBoyCautionMoneyHistoryCollection.getTransactionType().equals(TransactionEnum.DEBIT))
				{
					collectionBoyCautionMoneyCollection.setRemainingMoney(collectionBoyCautionMoneyCollection.getRemainingMoney() + collectionBoyCautionMoneyHistoryCollection.getAmount());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;

	}
	
	
	@Override
	@Transactional
	public CollectionBoyCautionMoney addeditCollectionBoyCautionMoney(CollectionBoyCautionMoney request) {
		CollectionBoyCautionMoney collectionBoyCautionMoney = null;
		CollectionBoyCautionMoneyCollection collectionBoyCautionMoneyCollection = null;

		try {

			if(request.getId() != null)
			{
				collectionBoyCautionMoneyCollection = collectionBoyCautionMoneyRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (collectionBoyCautionMoneyCollection == null) {
				collectionBoyCautionMoneyCollection = new CollectionBoyCautionMoneyCollection();
				collectionBoyCautionMoneyCollection.setCreatedTime(new Date());
				//medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			BeanUtil.map(request, collectionBoyCautionMoney);

			collectionBoyCautionMoneyCollection = collectionBoyCautionMoneyRepository.save(collectionBoyCautionMoneyCollection);
			
			if (collectionBoyCautionMoneyCollection != null) {
				collectionBoyCautionMoney = new CollectionBoyCautionMoney();
				BeanUtil.map(collectionBoyCautionMoneyCollection, collectionBoyCautionMoney);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return collectionBoyCautionMoney;
	}
	
	@Override
	@Transactional
	public CollectionBoyCautionMoneyHistory addeditCollectionBoyCautionMoneyHistory(CollectionBoyCautionMoneyHistory request) {
		CollectionBoyCautionMoneyHistory collectionBoyCautionMoneyHistory = null;
		CollectionBoyCautionMoneyHistoryCollection collectionBoyCautionMoneyHistoryCollection = null;

		try {

			if(request.getId() != null)
			{
				collectionBoyCautionMoneyHistoryCollection = collectionBoyCautionMoneyHistoryRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (collectionBoyCautionMoneyHistoryCollection == null) {
				collectionBoyCautionMoneyHistoryCollection = new CollectionBoyCautionMoneyHistoryCollection();
				collectionBoyCautionMoneyHistoryCollection.setCreatedTime(new Date());
			}

			BeanUtil.map(request, collectionBoyCautionMoneyHistoryCollection);

			collectionBoyCautionMoneyHistoryCollection = collectionBoyCautionMoneyHistoryRepository.save(collectionBoyCautionMoneyHistoryCollection);
			
			if (collectionBoyCautionMoneyHistoryCollection != null) {
				collectionBoyCautionMoneyHistory = new CollectionBoyCautionMoneyHistory();
				BeanUtil.map(collectionBoyCautionMoneyHistoryCollection, collectionBoyCautionMoneyHistory);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return collectionBoyCautionMoneyHistory;
	}
	
	@Override
	@Transactional
	public List<DrugInfo> getDrugInfo(int page, int size, String updatedTime,
			String searchTerm, Boolean discarded) {
		List<DrugInfo> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("brandName").regex("^" + searchTerm, "i"),
						new Criteria("drugType").regex("^" + searchTerm, "i"));
			}
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
				Aggregation.match(criteria),Aggregation.sort(new Sort(Direction.ASC, "brandName")),
				Aggregation.skip((long)(page) * size),Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),Aggregation.sort(new Sort(Direction.ASC, "brandName")));
			}
			
			//System.out.println(aggregation);
			
			response = mongoTemplate.aggregate(
					aggregation,
					DrugInfoCollection.class, DrugInfo.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}
	
	private void sendStatusChangeMessage(String patientId , String patientName, String mobileNumber , String message, String templateId) {
		try {
			
			if (mobileNumber != null) {
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				
				smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(new ObjectId(patientId));
				SMS sms = new SMS();
				smsDetail.setUserName(patientName);

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);
				sms.setSmsText(message);
				smsDetail.setSms(sms);
				
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				if(templateId!=null)
				smsTrackDetail.setTemplateId(templateId);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

}
