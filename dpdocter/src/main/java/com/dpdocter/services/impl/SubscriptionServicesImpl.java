package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CompanyDetail;
import com.dpdocter.beans.DoctorSubscriptionPayment;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.PackageAmountObject;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Subscription;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;
import com.dpdocter.collections.MindfulnessCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.PackageDetailObjectCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.SubscriptionHistoryCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PeriodEnums;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorSubscriptionPaymentRepository;
import com.dpdocter.repository.PackageDetailObjectRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SubscriptionServices;

import common.util.web.DPDoctorUtils;

@Service
public class SubscriptionServicesImpl implements SubscriptionServices {

	private static Logger logger = LogManager.getLogger(SubscriptionServicesImpl.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	PackageDetailObjectRepository packageDetailObjectRepository;

	@Autowired
	DoctorSubscriptionPaymentRepository doctorSubscriptionPaymentRepository;

	@Override
	public Subscription addEditSubscription(Subscription request) {
		Subscription response = null;
		try {
			SubscriptionCollection subscriptionCollection = null;
			SubscriptionHistoryCollection subscriptionHistoryCollection = null;
			subscriptionHistoryCollection = new SubscriptionHistoryCollection();

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				subscriptionCollection = subscriptionRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (subscriptionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Subscription Not found with Id");
				}
				// get doctor from doctor id;
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
				}
				// set old package value
				PackageType oldPackageName = subscriptionCollection.getPackageName();

				request.setCreatedTime(subscriptionCollection.getCreatedTime());
				BeanUtil.map(request, subscriptionCollection);
				subscriptionCollection.setUpdatedTime(new Date());
				subscriptionCollection.setCreatedBy("ADMIN");
				subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
				subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());
				subscriptionCollection.setTransactionStatus("SUCCESS");
				subscriptionCollection = subscriptionRepository.save(subscriptionCollection);

				// add payment in collection
//				if (request.getIsExtended() == false && request.getPaymentStatus() == true) {

				DoctorSubscriptionPaymentCollection payment = new DoctorSubscriptionPaymentCollection();
				payment.setSubscriptionId(subscriptionCollection.getId());
				payment.setDoctorId(subscriptionCollection.getDoctorId());
				payment.setAmount(request.getAmount());
				payment.setAccountNo(request.getAccountNo());
				payment.setBankName(request.getBankName());
				payment.setBranch(request.getBranch());
				payment.setChequeDate(request.getChequeDate());
				payment.setChequeNo(request.getChequeNo());
				payment.setMode(request.getMode());
				payment.setTransactionStatus("SUCCESS");
				payment.setDiscountAmount(request.getDiscountAmount());
				payment.setCreatedBy("ADMIN");
				payment.setCreatedTime(new Date());
				doctorSubscriptionPaymentRepository.save(payment);
				BeanUtil.map(payment, subscriptionHistoryCollection);
//				} 
//				else {
//					request.setPaymentStatus(subscriptionCollection.getPaymentStatus());
//				}
				BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
				subscriptionHistoryCollection.setSubscriptionId(subscriptionCollection.getId());
				subscriptionHistoryCollection.setCreatedTime(new Date());
				subscriptionHistoryCollection.setCreatedBy("ADMIN");
				subscriptionHistoryCollection.setDoctorId(subscriptionCollection.getDoctorId());
				subscriptionHistoryCollection = subscriptionHistoryRepository.save(subscriptionHistoryCollection);

				// set new package value
				PackageType newPackageName = request.getPackageName();

				// clinic package change
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(new ObjectId(request.getDoctorId()));

				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						doctorClinicProfileCollection.setUpdatedTime(new Date());
						doctorClinicProfileCollection.setPackageType(newPackageName.toString());

						doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					}
				}

				// call sms function

				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

				smsTrackDetail.setType(ComponentType.PACKAGE_DETAIL.getType());
				SMSDetail smsDetail = new SMSDetail();

				smsDetail.setUserName(userCollection.getFirstName());
				SMS sms = new SMS();

				// from date toDate difference
				Calendar fromDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				fromDateConvert.setTime(subscriptionCollection.getFromDate());
				int fromDateConvertDay = fromDateConvert.get(Calendar.DATE);
				int fromDateConvertMonth = fromDateConvert.get(Calendar.MONTH) + 1;
				int fromDateConvertYear = fromDateConvert.get(Calendar.YEAR);
				System.out.println("frm days" + fromDateConvertDay + fromDateConvertMonth + fromDateConvertYear);
				// to date
				Calendar toDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				toDateConvert.setTime(subscriptionCollection.getToDate());
				int toDateConvertDay = toDateConvert.get(Calendar.DATE);
				int toDateConvertMonth = toDateConvert.get(Calendar.MONTH) + 1;
				int toDateConvertYear = toDateConvert.get(Calendar.YEAR);
				LocalDate fromDate = LocalDate.of(fromDateConvertYear, fromDateConvertMonth, fromDateConvertDay);
				LocalDate toDate = LocalDate.of(toDateConvertYear, toDateConvertMonth, toDateConvertDay);

				Period diiff = Period.between(fromDate, toDate);// get difference bet today & fromdate
				String pattern = "dd/MM/yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

//				if (oldPackageName == newPackageName) {
				sms.setSmsText("Hello " + userCollection.getFirstName()
						+ ", your Payment has been done successfully on Date: "
						+ simpleDateFormat.format(subscriptionCollection.getCreatedTime()) + ", Mode of Payment:  "
						+ subscriptionCollection.getMode() + ", Total cost Rs.: " + subscriptionCollection.getAmount()
						+ ", Plan: " + subscriptionCollection.getPackageName() + ", Duration: " + diiff.getYears()
						+ " year.");
				smsTrackDetail.setTemplateId("1307161561866511824");
//
//				} 
//				else {
//					sms.setSmsText("Hello " + userCollection.getFirstName() + ", your Payment has been done successfully on Date: "
//							+simpleDateFormat.format(subscriptionCollection.getCreatedTime())
//							+ ", Mode of Payment:  "+subscriptionCollection.getMode()
//							+ ", Total cost Rs.: "+ subscriptionCollection.getAmount() 
//							+ ", Plan changed from " +oldPackageName +" to "+  newPackageName
//							+ ", Duration: "+ diiff.getYears() +" year.");
//
//				}
				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);
				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsTrackDetail.setTemplateId("1307161561866511824");
				Boolean ck = smsServices.sendSMS(smsTrackDetail, false);
				System.out.println("sms send" + smsDetails);

//				String body = doctorName + " Your Subscription Plan Changed from to "
//						+ newPackageName;
				String body = mailBodyGenerator.subscriptionPaymentEmailBody(userCollection.getTitle(),
						userCollection.getFirstName(),
						"from " + simpleDateFormat.format(subscriptionCollection.getFromDate()) + " to "
								+ simpleDateFormat.format(subscriptionCollection.getToDate()),
						subscriptionCollection.getPackageName().toString(), subscriptionCollection.getMode().toString(),
						Integer.toString(subscriptionCollection.getAmount()), "subscriptionPayment.vm");
				try {
					mailService.sendEmail(userCollection.getEmailAddress(), "Subscription Plan Updated on Healthcoco+",
							body, null);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				SubscriptionCollection subscriptionCkD = subscriptionRepository
						.findByDoctorId(new ObjectId(request.getDoctorId()));
				if (subscriptionCkD != null) {
					throw new BusinessException(ServiceError.NotFound,
							"Subscription already present for this doctor with this id " + subscriptionCkD.getId());
				}
				// get doctor from doctor id;
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
				}

				subscriptionCollection = new SubscriptionCollection();
				BeanUtil.map(request, subscriptionCollection);
				subscriptionCollection.setCreatedBy("ADMIN");
				subscriptionCollection.setTransactionStatus("SUCCESS");
				subscriptionCollection.setUpdatedTime(new Date());
				subscriptionCollection.setCreatedTime(new Date());
				subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
				subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());
				subscriptionCollection = subscriptionRepository.save(subscriptionCollection);

				// add payment in collection
				if (request.getPaymentStatus() == true) {
					DoctorSubscriptionPaymentCollection payment = new DoctorSubscriptionPaymentCollection();
					payment.setSubscriptionId(subscriptionCollection.getId());
					payment.setDoctorId(subscriptionCollection.getDoctorId());
					payment.setAmount(subscriptionCollection.getAmount());
					payment.setAccountNo(subscriptionCollection.getAccountNo());
					payment.setBankName(subscriptionCollection.getBankName());
					payment.setPackageName(subscriptionCollection.getPackageName());
					payment.setBranch(subscriptionCollection.getBranch());
					payment.setChequeDate(subscriptionCollection.getChequeDate());
					payment.setChequeNo(subscriptionCollection.getChequeNo());
					payment.setMode(subscriptionCollection.getMode());
					payment.setTransactionStatus("SUCCESS");
					payment.setDiscountAmount(subscriptionCollection.getDiscountAmount());
					payment.setCreatedBy("ADMIN");
					payment.setCreatedTime(new Date());
					doctorSubscriptionPaymentRepository.save(payment);
					BeanUtil.map(payment, subscriptionHistoryCollection);
				}
				BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
				subscriptionHistoryCollection.setDoctorId(subscriptionCollection.getDoctorId());
				subscriptionHistoryCollection.setCreatedTime(new Date());
				subscriptionHistoryCollection.setCreatedBy("ADMIN");
				subscriptionHistoryCollection.setSubscriptionId(subscriptionCollection.getId());
				subscriptionHistoryCollection = subscriptionHistoryRepository.save(subscriptionHistoryCollection);

				// clinic package change
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(new ObjectId(request.getDoctorId()));

				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						doctorClinicProfileCollection.setUpdatedTime(new Date());
						doctorClinicProfileCollection
								.setPackageType(subscriptionCollection.getPackageName().toString());

						doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					}
				}

				// call sms function
				String doctorName = userCollection.getTitle() + userCollection.getFirstName();
				PackageType newPackageName = request.getPackageName();

				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

				smsTrackDetail.setType(ComponentType.PACKAGE_DETAIL.getType());
				SMSDetail smsDetail = new SMSDetail();
				String pattern = "dd/MM/yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				smsDetail.setUserName(doctorName);
				SMS sms = new SMS();

				// from date toDate difference
				Calendar fromDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				fromDateConvert.setTime(subscriptionCollection.getFromDate());
				int fromDateConvertDay = fromDateConvert.get(Calendar.DATE);
				int fromDateConvertMonth = fromDateConvert.get(Calendar.MONTH) + 1;
				int fromDateConvertYear = fromDateConvert.get(Calendar.YEAR);
				System.out.println("frm days" + fromDateConvertDay + fromDateConvertMonth + fromDateConvertYear);
				// to date
				Calendar toDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				toDateConvert.setTime(subscriptionCollection.getToDate());
				int toDateConvertDay = toDateConvert.get(Calendar.DATE);
				int toDateConvertMonth = toDateConvert.get(Calendar.MONTH) + 1;
				int toDateConvertYear = toDateConvert.get(Calendar.YEAR);
				LocalDate fromDate = LocalDate.of(fromDateConvertYear, fromDateConvertMonth, fromDateConvertDay);
				LocalDate toDate = LocalDate.of(toDateConvertYear, toDateConvertMonth, toDateConvertDay);

				Period diiff = Period.between(fromDate, toDate);// get difference bet today & fromdate

				sms.setSmsText("Hello " + userCollection.getFirstName()
						+ ", your Payment has been done successfully on Date: "
						+ simpleDateFormat.format(subscriptionCollection.getCreatedTime()) + ", Mode of Payment:  "
						+ subscriptionCollection.getMode() + ", Total cost Rs.: " + subscriptionCollection.getAmount()
						+ ", Plan: " + subscriptionCollection.getPackageName() + ", Duration: " + diiff.getYears()
						+ " year.");

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);
				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsTrackDetail.setTemplateId("1307161561866511824");
				Boolean ck = smsServices.sendSMS(smsTrackDetail, false);
				System.out.println("sms send" + ck);

				String body = mailBodyGenerator.subscriptionPaymentEmailBody(userCollection.getTitle(),
						userCollection.getFirstName(),
						"from " + simpleDateFormat.format(subscriptionCollection.getFromDate()) + " to "
								+ simpleDateFormat.format(subscriptionCollection.getToDate()),
						subscriptionCollection.getPackageName().toString(), subscriptionCollection.getMode().toString(),
						Integer.toString(subscriptionCollection.getAmount()), "subscriptionPayment.vm");
				try {
					mailService.sendEmail(userCollection.getEmailAddress(), "Subscription Plan Updated on Healthcoco+",
							body, null);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			response = new Subscription();
			BeanUtil.map(subscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Subscription  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Subscription " + e.getMessage());

		}
		return response;

	}

	public Boolean sendSMS(String doctorName, String mobileNumber, String countryCode, PackageType oldPackageName,
			PackageType newPackageName) {
		Boolean response = false;
		try {
			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

			smsTrackDetail.setType(ComponentType.PACKAGE_DETAIL.getType());
			SMSDetail smsDetail = new SMSDetail();

			smsDetail.setUserName(doctorName);
			SMS sms = new SMS();

			sms.setSmsText(" Your Subscription Plan has been Changed from " + oldPackageName + " to " + newPackageName
					+ ". Stay Healthy and Happy!");

			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setTemplateId("1307162160395400042");
			smsTrackDetail.setSmsDetails(smsDetails);
			Boolean ck = smsServices.sendSMS(smsTrackDetail, false);
			System.out.println("sms send" + smsDetails);

			// save sms in repository write code

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Sending SMS");
			throw new BusinessException(ServiceError.Unknown, "Error While Sending SMS " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<Subscription> getSubscription(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<Subscription> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, SubscriptionCollection.class, Subscription.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Subscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Subscription " + e.getMessage());

		}
		return response;

	}

	@Override
	public Integer countSubscription(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), SubscriptionCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Subscription " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while Subscription " + e.getMessage());

		}
		return response;
	}

	@Override
	public Subscription getSubscriptionById(String id) {
		Subscription response = null;
		try {
			SubscriptionCollection subscriptionCollection = subscriptionRepository.findById(new ObjectId(id))
					.orElse(null);
			if (subscriptionCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new Subscription();
			BeanUtil.map(subscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}

	@Override
	public Subscription getSubscriptionByDoctorId(String doctorId, PackageType packageName, int duration,
			int newAmount) {
		Subscription response = null;
		try {
			SubscriptionCollection subscriptionCollection = subscriptionRepository
					.findByDoctorId(new ObjectId(doctorId));
			if (subscriptionCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			if (packageName != null) {
				PackageDetailObjectCollection packageBasic = packageDetailObjectRepository
						.findByPackageName(PackageType.BASIC);

				PackageDetailObjectCollection packagePro = packageDetailObjectRepository
						.findByPackageName(PackageType.PRO);

				PackageDetailObjectCollection packageAdvance = packageDetailObjectRepository
						.findByPackageName(PackageType.ADVANCE);
				// package price
				List<PackageAmountObject> BASIC = packageBasic.getPackageAmount();
				List<PackageAmountObject> PRO = packagePro.getPackageAmount();
				List<PackageAmountObject> ADVANCE = packageAdvance.getPackageAmount();

				if (subscriptionCollection.getPackageName() != PackageType.FREE) {
					// for trial period condition
					if (subscriptionCollection.getPackageName() == PackageType.BASIC
							&& subscriptionCollection.getAmount() == 0) {
						subscriptionCollection.setAmount(newAmount);
					} else {
						// from date toDate difference
						Calendar fromDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						fromDateConvert.setTime(subscriptionCollection.getFromDate());
						int fromDateConvertDay = fromDateConvert.get(Calendar.DATE);
						int fromDateConvertMonth = fromDateConvert.get(Calendar.MONTH) + 1;
						int fromDateConvertYear = fromDateConvert.get(Calendar.YEAR);
						System.out
								.println("frm days" + fromDateConvertDay + fromDateConvertMonth + fromDateConvertYear);
						// to date
						Calendar toDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						toDateConvert.setTime(subscriptionCollection.getToDate());
						int toDateConvertDay = toDateConvert.get(Calendar.DATE);
						int toDateConvertMonth = toDateConvert.get(Calendar.MONTH) + 1;
						int toDateConvertYear = toDateConvert.get(Calendar.YEAR);
						LocalDate fromDate = LocalDate.of(fromDateConvertYear, fromDateConvertMonth,
								fromDateConvertDay);
						LocalDate toDate = LocalDate.of(toDateConvertYear, toDateConvertMonth, toDateConvertDay);

						Period diiff = Period.between(fromDate, toDate);// get difference bet today & fromdate

						// for 10th point
						Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						LocalDate currentDate = LocalDate.now();
						localCalendar.setTime(subscriptionCollection.getFromDate());
						int currentDay = localCalendar.get(Calendar.DATE);
						int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
						int currentYear = localCalendar.get(Calendar.YEAR);

						LocalDate newDate = LocalDate.of(currentYear, currentMonth, currentDay);
						Period period = Period.between(currentDate, newDate);// get difference bet today & fromdate
						int usedMonths = -(period.getMonths());
						System.out.println(usedMonths);
						// pro to adv
//					Cost = new amount - old amount + (per month cost of old amount * months used)
						// find per month cost of packages
						int getMonthsFromYear = diiff.getYears() * 12;// calculate number of months from old duration
						int amountPerMonth = subscriptionCollection.getAmount() / getMonthsFromYear;// to get per month
																									// cost
																									// of old package
						System.out.println("amountPerMonth" + amountPerMonth);
						int discountedAmount = newAmount - subscriptionCollection.getAmount()
								+ amountPerMonth * usedMonths;
						System.out.println(discountedAmount);

						subscriptionCollection.setAmount(discountedAmount);
					}
				} // if close of amt
				else {
					// send request amount directly
					subscriptionCollection.setAmount(newAmount);

					// pro to adv
//					if (packageName == PackageType.ADVANCE) {
//						ADVANCE.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					} // basic to adv
//					else if (packageName == PackageType.BASIC) {
//						BASIC.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					} // basic to pro
//					if (packageName == PackageType.PRO) {
//						PRO.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					}
				}
			} // if close of package name
			response = new Subscription();
			BeanUtil.map(subscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}

	/*
	 * schedule function to send sms to doctors about trial peried end from 5 days
	 * before end date
	 */
	@Scheduled(cron = "0 00 11 * * ?", zone = "IST") //
//	@Scheduled(fixedRate = 30000)
	public void scheduleReminder() {

		int i;// start count for 5 days
		for (i = 5; i >= 0; i--) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +i); // get date after i days from today
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date dateAfter7Days = cal.getTime();
			// convert this date to local date
			// Getting the default zone id
			ZoneId defaultZoneId = ZoneId.systemDefault();
			// Converting the date to Instant
			Instant instant = dateAfter7Days.toInstant();
			System.out.println("dateAfter7Days" + i + dateAfter7Days);
			// Converting the Date to LocalDate
			LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
			LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);// set time 00:00
			LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);// set time 23:59

			try {

				Criteria criteria = new Criteria("toDate").gte(startOfDay).lte(endOfDay);

				List<SubscriptionCollection> response = null;
				Aggregation aggregation1 = Aggregation.newAggregation(Aggregation.match(criteria));
				response = mongoTemplate
						.aggregate(aggregation1, SubscriptionCollection.class, SubscriptionCollection.class)
						.getMappedResults();
				System.out.println("SubscriptionCollection data" + response);

//				Aggregation aggregation32 = Aggregation.newAggregation(
//						Aggregation.lookup("user_cl", "doctorId","id", "subs_doc"),
//						Aggregation.match(criteria),
//						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
//				
//				System.out.println("aggregation32 "+aggregation32);
//				List<UserCollection> userCollections32 = mongoTemplate.aggregate(aggregation32, SubscriptionCollection.class,
//						UserCollection.class).getMappedResults();
//				
//				System.out.println("userCollections 32" + userCollections32);

				if (response.size() > 0)
					for (SubscriptionCollection subscription : response) {

						// get doctor from doctor id;
						UserCollection userCollection = userRepository.findById(subscription.getDoctorId())
								.orElse(null);
						if (userCollection == null) {
							throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
						}
						if (userCollection.getIsActive()) {
							String message;
							String body;
							String PlanDays = Integer.toString(i);
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							if (i == 0) {
								message = "Hello " + userCollection.getTitle() + userCollection.getFirstName()
										+ " Your subscription is going to expire today"
										+ ". Please renew your plan. For queries, contact 8459802223" + ".-Healthcoco";
								body = mailBodyGenerator.subscriptionEmailBody(userCollection.getTitle(),
										userCollection.getFirstName(), "on", "today", "", "subscriptionReminder.vm");
								smsTrackDetail.setTemplateId("1407173165553634881");

							} else {
								message = "Hello " + userCollection.getTitle() + userCollection.getFirstName()
										+ " your subscription is going to expire in " + i + " days"
										+ ". Please renew your plan. For queries contact 8459802223" + ".-Healthcoco";

								body = mailBodyGenerator.subscriptionEmailBody(userCollection.getTitle(),
										userCollection.getFirstName(), "in", PlanDays + "days", "",
										"subscriptionReminder.vm");
								smsTrackDetail.setTemplateId("1407173148776834955");

							}

							smsTrackDetail.setType("Remainder");
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(subscription.getDoctorId());
							SMS sms = new SMS();
//					smsDetail.setUserName(userCollection.getFirstName());
							sms.setSmsText(message);

							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(subscription.getMobileNumber());//
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							System.out.println(smsTrackDetail);
							smsServices.sendSMS(smsTrackDetail, false);

							if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
								mailService.sendEmail(userCollection.getEmailAddress(), "Subscription Reminder", body,
										null);
							}
						}
					}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e + " Error Occurred While Sending SMS ");
				throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending SMS");

			}
		} // for loop close
	}

	@Scheduled(cron = "0 59 23 * * ?", zone = "IST")
	public void packegeChangeToFree() {
		// convert this date to local date
		// Getting the default zone id
		Date todayDate = new Date(); // today's time

		ZoneId defaultZoneId = ZoneId.systemDefault();
		// Converting the date to Instant
		Instant instant = todayDate.toInstant();
		System.out.println("todayDate" + todayDate);
		// Converting the Date to LocalDate
		LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
		LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);// set time 00:00
		LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);// set time 23:59
		try {

			Criteria criteria = new Criteria("toDate").gte(startOfDay).lte(endOfDay);

			List<SubscriptionCollection> response = null;
			Aggregation aggregation1 = Aggregation.newAggregation(Aggregation.match(criteria));
			response = mongoTemplate.aggregate(aggregation1, SubscriptionCollection.class, SubscriptionCollection.class)
					.getMappedResults();
			System.out.println("SubscriptionCollection data" + response);

//			Aggregation aggregation32 = Aggregation.newAggregation(
//					Aggregation.lookup("user_cl", "doctorId","id", "subs_doc"),
//					Aggregation.match(criteria),
//					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
//			
//			System.out.println("aggregation32 "+aggregation32);
//			List<UserCollection> userCollections32 = mongoTemplate.aggregate(aggregation32, SubscriptionCollection.class,
//					UserCollection.class).getMappedResults();
//			
//			System.out.println("userCollections 32" + userCollections32);

			if (response.size() > 0)
				for (SubscriptionCollection subscription : response) {

					// get doctor from doctor id;
					UserCollection userCollection = userRepository.findById(subscription.getDoctorId()).orElse(null);
					if (userCollection == null) {
						throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
					}
					if (userCollection.getIsActive()) {

						subscription.setPackageName(PackageType.FREE);
						subscription.setPaymentStatus(false);
						subscription.setAmount((int) 0);
						subscription.setMode(null);
						subscription.setTransactionStatus(null);
						subscription.setFromDate(new Date());
						subscription.setToDate(null);
						subscription = subscriptionRepository.save(subscription);

						SubscriptionHistoryCollection subscriptionHistoryCollection = new SubscriptionHistoryCollection();
						BeanUtil.map(subscription, subscriptionHistoryCollection);
						subscriptionHistoryCollection.setDoctorId(subscription.getDoctorId());
						subscriptionHistoryCollection.setCreatedTime(new Date());
						subscriptionHistoryCollection.setCreatedBy("ADMIN");
						subscriptionHistoryCollection.setSubscriptionId(subscription.getId());
						subscriptionHistoryCollection = subscriptionHistoryRepository
								.save(subscriptionHistoryCollection);

						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByDoctorId((subscription.getDoctorId()));

						if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
							for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
								doctorClinicProfileCollection.setUpdatedTime(new Date());
								doctorClinicProfileCollection.setPackageType(subscription.getPackageName().toString());
								;
								doctorClinicProfileRepository.save(doctorClinicProfileCollection);
							}
						}

						String message;
						message = "Hello " + userCollection.getTitle() + userCollection.getFirstName()
								+ " your subscription plan has been changed to Free. \n"
								+ " Download the Healthcoco+ app now: http://bit.ly/2aaH4w1, For queries contact 8459802223";

						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
						smsTrackDetail.setType("Remainder");
						SMSDetail smsDetail = new SMSDetail();
						smsDetail.setUserId(subscription.getDoctorId());
						SMS sms = new SMS();
//				smsDetail.setUserName(userCollection.getFirstName());
						sms.setSmsText(message);

						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(subscription.getMobileNumber());
						sms.setSmsAddress(smsAddress);

						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						System.out.println(smsTrackDetail);
						smsServices.sendSMS(smsTrackDetail, true);

						String body = mailBodyGenerator.subscriptionEmailBody(userCollection.getTitle(),
								userCollection.getFirstName(), "", "", "FREE", "subscriptionPackegeFree.vm");
						mailService.sendEmail(userCollection.getEmailAddress(),
								"Subscription Plan Updated on Healthcoco+", body, null);

					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Sending SMS ");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Sending SMS");

		}
	}

	@Override
	public List<DoctorSubscriptionPayment> getSubscriptionPaymentByDoctorId(String doctorId, int size, int page) {
		List<DoctorSubscriptionPayment> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));

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
					.aggregate(aggregation, DoctorSubscriptionPaymentCollection.class, DoctorSubscriptionPayment.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Subscription Payment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Subscription Payment" + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countSubscriptionPayment(String doctorId) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));

			response = (int) mongoTemplate.count(new Query(criteria), DoctorSubscriptionPaymentCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting  Subscription Payment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while Subscription Payment " + e.getMessage());

		}
		return response;
	}
}
