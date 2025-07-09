package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.Address;
import com.dpdocter.beans.AdminSignupRequest;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocaleContactUsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.collections.PCUserCollection;
import com.dpdocter.collections.PharmaCompanyCollection;
import com.dpdocter.collections.PharmaLicenseCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.SubscriptionDetailCollection;
import com.dpdocter.collections.SubscriptionHistoryCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AdminType;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LocaleContactStateType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PeriodEnums;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocaleContactUsRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;
import com.dpdocter.repository.PCUserRepository;
import com.dpdocter.repository.PharmaCompanyRepository;
import com.dpdocter.repository.PharmaLicenseRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.EditAdminRequest;
import com.dpdocter.request.PharmaCompanySignupRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.PharmaCompanyResponse;
import com.dpdocter.response.PharmaLicenseResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PharmaService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.SubscriptionDetailServices;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;

import common.util.web.DPDoctorUtils;

@Service
public class SignUpServiceImpl implements SignUpService {

	private static Logger logger = LogManager.getLogger(SignUpServiceImpl.class.getName());

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CustomPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private PharmaCompanyRepository pharmaCompanyRepository;

	@Autowired
	private PCUserRepository pcUserRepository;

	@Autowired
	private SubscriptionDetailRepository subscriptionDetailRepository;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.account.activate.subject}")
	private String accountActivateSubject;

	@Value(value = "${web.link}")
	private String LOGIN_WEB_LINK;

	@Value(value = "${patient.count}")
	private String patientCount;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private ServicesRepository servicesRepository;

	@Autowired
	private LocaleContactUsRepository localeContactUsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private DoctorContactUsRepository doctorContactUsRepository;

	@Autowired
	private SubscriptionDetailServices subscriptionDetailServices;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private CollectionBoyRepository collectionBoyRepository;

	@Autowired
	private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

	@Autowired
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${Signup.verifyPatientBasedOn80PercentMatchOfName}")
	private String verifyPatientBasedOn80PercentMatchOfName;

	@Value(value = "${Signup.unlockPatientBasedOn80PercentMatch}")
	private String unlockPatientBasedOn80PercentMatch;

	@Value(value = "${doctor.app.bit.link}")
	private String doctorAppLink;

	@Value(value = "${verify.pharmacy.link}")
	private String pharmacyVerificationLink;

	@Value(value = "${set.pharmacy.password.link}")
	private String setPharmacyPasswordLink;

	@Autowired
	private PharmaService pharmaService;

	@Autowired
	private PharmaLicenseRepository pharmaLicenseRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	SubscriptionHistoryRepository subscriptionHistoryRepository;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Boolean activateUser(String userId, Boolean activate) {
		UserCollection userCollection = null;
		PCUserCollection pcUserCollection = null;
		DoctorCollection doctorCollection = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		List<DoctorClinicProfileCollection> clinicProfileCollections = null;
		PharmaLicenseResponse licenseResponse = null;
		//
		List<String> speciality = null;
		boolean isPediatrician = false;

		// Subscription new for Doctor
		SubscriptionCollection subscriptionCollection = new SubscriptionCollection();
		Boolean response = false;
		try {
			userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {

				if (userCollection.getUserState().getState()
						.equalsIgnoreCase(UserState.USERSTATEINCOMPLETE.getState())) {
					logger.error("User State is incomplete so user cannot be activated");
					throw new BusinessException(ServiceError.Unknown,
							"User State is incomplete so user cannot be activated");
				} else if (userCollection.getUserState().getState()
						.equalsIgnoreCase(UserState.NOTVERIFIED.getState())) {
					logger.error("User has not verified his mail so user cannot be activated");
					throw new BusinessException(ServiceError.Unknown,
							"User has not verified his mail so user cannot be activated");
				}
				userCollection.setIsActive(activate);
				userCollection.setActivationDate(new Date());
				userCollection.setUpdatedTime(new Date());
				if (activate) {

					userCollection.setUserState(UserState.USERSTATECOMPLETE);
				} else
					userCollection.setUserState(UserState.NOTACTIVATED);
				userRepository.save(userCollection);
				clinicProfileCollections = doctorClinicProfileRepository.findByDoctorId(new ObjectId(userId));
				if (clinicProfileCollections != null) {
					clinicProfileCollection = clinicProfileCollections.get(0);
					if (clinicProfileCollection.getMrCode() != null) {
						pcUserCollection = pcUserRepository.findByMrCode(clinicProfileCollection.getMrCode());
						if (pcUserCollection != null) {
							List<PharmaLicenseResponse> pharmaLicenseResponses = pharmaService
									.getLicenses(pcUserCollection.getCompanyId().toString(), 0, 0);
							for (PharmaLicenseResponse pharmaLicenseResponse : pharmaLicenseResponses) {
								if (pharmaLicenseResponse.getAvailable() > 0) {
									licenseResponse = pharmaLicenseResponse;
									break;
								}
							}
						}
					}

					doctorCollection = doctorRepository.findByUserId(new ObjectId(userId));

					// add services according to specialty id
					if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {

						// change vaccine module true for Pediatrician specility
						speciality = (List<String>) CollectionUtils.collect(
								(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
								new BeanToPropertyValueTransformer("superSpeciality"));

						System.out.println(speciality);

						isPediatrician = speciality.contains("Pediatrician");
						if (isPediatrician) {
							clinicProfileCollection.setIsVaccinationModuleOn(true);
						}

						List<ServicesCollection> servicesCollections = servicesRepository
								.findBySpecialityIdsIn(doctorCollection.getSpecialities());
						List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
								new BeanToPropertyValueTransformer("id"));
						Set<ObjectId> services = new HashSet<>(servicesIds);

						if (doctorCollection.getServices() != null)
							doctorCollection.getServices().addAll(services);
						else
							doctorCollection.setServices(services);

						doctorCollection = doctorRepository.save(doctorCollection);

					}

					// get subscription trial period by country code from country collection
					if (activate) {
						SubscriptionCollection subscriptionCkD = subscriptionRepository
								.findByDoctorId(userCollection.getId());
						if (subscriptionCkD != null) {
							subscriptionCollection.setId(subscriptionCkD.getId());
						}
						CountryCollection countryCollection = countryRepository
								.findByCountryCode(userCollection.getCountryCode());

						subscriptionCollection.setCreatedBy("ADMIN");

						subscriptionCollection.setCreatedTime(new Date());
						subscriptionCollection.setDoctorId(userCollection.getId());
						subscriptionCollection.setPackageName(PackageType.BASIC);
						subscriptionCollection.setCountryCode(userCollection.getCountryCode());
						subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
						subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());
						subscriptionCollection.setFromDate(userCollection.getActivationDate());
						// set todate according to trail peried enums
						Calendar calendar = Calendar.getInstance();
						Date today = calendar.getTime();
						String PlanDays = "";
						if (countryCollection != null) {
							if (countryCollection.getPeriod() == PeriodEnums.THIRTY_DAYS) {
								// get to date according to trail peried
								calendar.add(Calendar.MONTH, +1);
								Date nextMonth = calendar.getTime();
								PlanDays = "30 Days.";
								subscriptionCollection.setToDate(nextMonth);
							} else if (countryCollection.getPeriod() == PeriodEnums.FIFTEEN_DAYS) {
								// get to date according to trail peried
								calendar.add(Calendar.DATE, +15);
								Date nextMonth = calendar.getTime();
								subscriptionCollection.setToDate(nextMonth);
								PlanDays = "15 Days.";
							} else if (countryCollection.getPeriod() == PeriodEnums.ONE_YEAR) {
								// get to date according to trail peried
								calendar.add(Calendar.YEAR, +1);
								Date nextMonth = calendar.getTime();
								subscriptionCollection.setToDate(nextMonth);
								PlanDays = "1 Year.";

							} else if (countryCollection.getPeriod() == PeriodEnums.SEVEN_DAYS) {
								// get to date according to trail peried
								calendar.add(Calendar.DATE, +7);
								Date nextMonth = calendar.getTime();
								subscriptionCollection.setToDate(nextMonth);
								PlanDays = "7 Days.";

							} else if (countryCollection.getPeriod() == PeriodEnums.THREE_MONTHS) {
								// get to date according to trail peried
								calendar.add(Calendar.MONTH, +3);
								Date nextMonth = calendar.getTime();
								subscriptionCollection.setToDate(nextMonth);
								PlanDays = "3 Months.";

							} else if (countryCollection.getPeriod() == PeriodEnums.SIX_MONTHS) {
								// get to date according to trail peried
								calendar.add(Calendar.MONTH, +6);
								Date nextMonth = calendar.getTime();
								subscriptionCollection.setToDate(nextMonth);
								PlanDays = "6 Months.";

							}
						}

						subscriptionRepository.save(subscriptionCollection);
						// clinic package change

						// Save to History
						SubscriptionHistoryCollection subscriptionHistoryCollection = new SubscriptionHistoryCollection();
						BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
						subscriptionHistoryCollection.setSubscriptionId(subscriptionCollection.getId());
						subscriptionHistoryCollection.setCreatedBy("ADMIN");
						subscriptionHistoryCollection.setCreatedTime(new Date());
						subscriptionHistoryCollection.setDoctorId(subscriptionCollection.getDoctorId());
						subscriptionHistoryCollection = subscriptionHistoryRepository
								.save(subscriptionHistoryCollection);

						// send sms
						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

						smsTrackDetail.setType(ComponentType.PACKAGE_DETAIL.getType());
						SMSDetail smsDetail = new SMSDetail();

						smsDetail.setUserName(userCollection.getFirstName());
						SMS sms = new SMS();

						sms.setSmsText("Hello " + userCollection.getFirstName() + ", We have activated your "
								+ subscriptionCollection.getPackageName()
								+ " trial plan, the duration of the plan is for " + PlanDays
								+ " Download the Healthcoco+ app now: http://bit.ly/2aaH4w1, For queries contact 8459802223");

						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(userCollection.getMobileNumber());
						sms.setSmsAddress(smsAddress);
						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setTemplateId("1307162160371234834");
						smsTrackDetail.setSmsDetails(smsDetails);
						Boolean cksms = sMSServices.sendSMS(smsTrackDetail, false);
						System.out.println("sms send" + cksms);

						String body = null;
						if (!DPDoctorUtils.anyStringEmpty(userCollection.getEmailAddress())) {
							body = mailBodyGenerator.subscriptionEmailBody(userCollection.getTitle(),
									userCollection.getFirstName(), "", PlanDays,
									subscriptionCollection.getPackageName().toString(), "subscriptionActivation.vm");
							mailService.sendEmail(userCollection.getEmailAddress(),
									"Activation successful. Subscription Plan Set to BASIC", body, null);
						}
					}

					// Subscribe Doctor with Clinic
					SubscriptionDetail detail = new SubscriptionDetail();
					detail.setCreatedBy("Admin");
					detail.setDoctorId(userCollection.getId().toString());
					detail.setIsDemo(true);
					detail.setMonthsforSms(1);
					detail.setMonthsforSuscrption(1);
					detail.setNoOfsms(500);
					Set<String> locationSet = new HashSet<String>();
					locationSet.add(clinicProfileCollection.getLocationId().toString());
					detail.setLocationIds(locationSet);
					if (licenseResponse != null) {

						List<SubscriptionDetailCollection> subscriptionDetailCollections = subscriptionDetailRepository
								.findByDoctorIdAndLicenseId(new ObjectId(userId),
										new ObjectId(licenseResponse.getId()));

						if (subscriptionDetailCollections.isEmpty()) {
							detail.setIsDemo(false);
							detail.setFromDate(new Date());
							detail.setToDate(DateUtils.addYears(new Date(), licenseResponse.getDuration()));
							detail.setLicenseId(licenseResponse.getId());
							detail.setMrCode(clinicProfileCollection.getMrCode());
							licenseResponse.setAvailable(licenseResponse.getAvailable() - 1);
							licenseResponse.setConsumed(licenseResponse.getConsumed() + 1);
							PharmaLicenseCollection pharmaLicenseCollection = new PharmaLicenseCollection();
							BeanUtil.map(licenseResponse, pharmaLicenseCollection);
							pharmaLicenseRepository.save(pharmaLicenseCollection);
						}

					}
					subscriptionDetailServices.activate(detail);
				}
				response = true;
				if (activate) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					String body = mailBodyGenerator.generateActivationEmailBody(
							(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName(),
							tokenCollection.getId(), "accountActivateTemplate.vm", null, null);
					mailService.sendEmail(userCollection.getEmailAddress(), accountActivateSubject, body, null);
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					smsTrackDetail.setType("AFTER_VERIFICATION_TO_DOCTOR");
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(userCollection.getId());
					smsDetail.setUserName(userCollection.getFirstName());
					SMS sms = new SMS();
					sms.setSmsText((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName()
							+ ", Your Healthcoco+ account has been activated, Download the Healthcoco+ app now: "
							+ doctorAppLink
							+ ". For queries, please feel free to contact us at support@healthcoco.com.-Healthcoco");

					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(userCollection.getMobileNumber());
					sms.setSmsAddress(smsAddress);

					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setTemplateId("1307161641574429894");
					smsTrackDetail.setSmsDetails(smsDetails);
					sMSServices.sendSMS(smsTrackDetail, true);

					pushNotificationServices.notifyUser(userCollection.getId().toString(),
							" Your Healthcoco+ account has been activated", ComponentType.SIGNED_UP.getType(), null,
							null);

				} else {
					pushNotificationServices.notifyUser(userCollection.getId().toString(),
							" Your Healthcoco+ account has been deactivated", ComponentType.DEACTIVATED.getType(), null,
							null);

				}
				Criteria criteria = new Criteria("role.role").is(RoleEnum.LOCATION_ADMIN.getRole())
						.and("role.locationId").is(null).and("userId").is(userCollection.getId());

				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.lookup("role_cl", "roleId", "_id", "role"), Aggregation.unwind("role"),
						Aggregation.match(criteria));

				List<UserRoleCollection> userRoleCollections = mongoTemplate
						.aggregate(aggregation, UserRoleCollection.class, UserRoleCollection.class).getMappedResults();

				if (userRoleCollections != null && !userRoleCollections.isEmpty()) {
					for (UserRoleCollection userRoleCollection : userRoleCollections) {
						if (!DPDoctorUtils.anyStringEmpty(userRoleCollection.getLocationId())) {
							List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
									.findByLocationId(userRoleCollection.getLocationId());
							if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
								for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
									doctorClinicProfileCollection.setIsActivate(activate);
									doctorClinicProfileCollection.setUpdatedTime(new Date());
									doctorClinicProfileCollection.setActivationDate(new Date());
									if (activate) {
										doctorClinicProfileCollection
												.setPackageType(subscriptionCollection.getPackageName().toString());
									}
									doctorClinicProfileCollection.setIsVaccinationModuleOn(isPediatrician);
									doctorClinicProfileRepository.save(doctorClinicProfileCollection);
								}
							}
							LocationCollection locationCollection = locationRepository
									.findById(userRoleCollection.getLocationId()).orElse(null);
							if (locationCollection != null) {
								locationCollection.setUpdatedTime(new Date());
								locationCollection.setIsActivate(activate);
								locationRepository.save(locationCollection);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage() + " Error While Verifying User");
			throw new BusinessException(ServiceError.Unknown, "Error While Verifying User");
		}
		return response;
	}

	@Override
	public Boolean activateLocation(String locationId, Boolean activate) {
		LocationCollection locationCollection = null;
		Boolean response = false;
		try {
			locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection != null) {
				locationCollection.setUpdatedTime(new Date());
				locationCollection.setIsActivate(activate);
				locationRepository.save(locationCollection);
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(locationCollection.getId());
				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						doctorClinicProfileCollection.setUpdatedTime(new Date());
						doctorClinicProfileCollection.setIsActivate(activate);
						doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					}
				}
			} else {
				logger.error("Location Not Found For The Given Id");
				throw new BusinessException(ServiceError.Unknown, "Location Not Found For The Given Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Verifying User");
			throw new BusinessException(ServiceError.Unknown, "Error While Verifying User");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorSignUp doctorSignUp(DoctorSignupRequest request) {
		DoctorSignUp response = null;
		List<RoleCollection> roleCollections = null;
		HospitalCollection hospitalCollection = null;
		LocationCollection locationCollection = null;
		PCUserCollection pcUserCollection = null;
		PharmaLicenseResponse licenseResponse = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
				logger.warn("Email Address cannot be null");
				throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
			}

			if (DPDoctorUtils.allStringsEmpty(request.getHospitalId(), request.getLocationId())) {

				roleCollections = roleRepository.findByRoleInAndLocationIdAndHospitalId(
						Arrays.asList(RoleEnum.HOSPITAL_ADMIN.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
								RoleEnum.DOCTOR.getRole(), RoleEnum.SUPER_ADMIN.getRole()),
						null, null);
			} else if (request.getRoles() != null && !request.getRoles().isEmpty()) {
				roleCollections = roleRepository.findByRoleInAndLocationIdAndHospitalId(request.getRoles(), null, null);
			} else {
				roleCollections = roleRepository
						.findByRoleInAndLocationIdAndHospitalId(Arrays.asList(RoleEnum.DOCTOR.getRole()), null, null);
			}
			if (roleCollections == null || roleCollections.isEmpty()) {
				logger.warn("Role Collection in database is either empty or not defind properly");
				throw new BusinessException(ServiceError.NoRecord,
						"Role Collection in database is either empty or not defind properly");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId())) {
				hospitalCollection = hospitalRepository.findById(new ObjectId(request.getHospitalId())).orElse(null);
				if (hospitalCollection == null) {
					logger.warn("Hospital not found by hospitalId");
					throw new BusinessException(ServiceError.NoRecord, "Hospital not found by hospitalId");
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
				locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
				if (locationCollection == null) {
					logger.warn("Location not found by locationId");
					throw new BusinessException(ServiceError.NoRecord, "Location not found by locationId");
				}
			}

			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			if (request.getDob() != null
					&& request.getDob().getAge() != null & request.getDob().getAge().getYears() < 0) {
				logger.warn("Incorrect Date of Birth");
				throw new BusinessException(ServiceError.InvalidInput, "Incorrect Date of Birth");
			}
			userCollection.setUserName(request.getEmailAddress());
			if (DPDoctorUtils.allStringsEmpty(request.getTitle()))
				userCollection.setTitle("Dr.");
			userCollection.setCreatedTime(new Date());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setUserState(UserState.USERSTATECOMPLETE);
			userCollection = userRepository.save(userCollection);
			// save doctor specific details
			DoctorCollection doctorCollection = new DoctorCollection();
			List<String> specialities = request.getSpecialities();
			request.setSpecialities(null);

			List<String> services = request.getServices();
			request.setServices(null);

			BeanUtil.map(request, doctorCollection);
			if (specialities != null && !specialities.isEmpty()) {
				List<SpecialityCollection> specialityCollections = specialityRepository
						.findBySuperSpecialityIn(specialities);
				Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
						new BeanToPropertyValueTransformer("id"));
				if (specialityIds != null && !specialityIds.isEmpty())
					doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
				else
					doctorCollection.setSpecialities(null);
			} else {
				doctorCollection.setSpecialities(null);
			}

			if (services != null && !services.isEmpty()) {
				List<ServicesCollection> servicesCollections = servicesRepository.findByServiceIn(services);
				List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
						new BeanToPropertyValueTransformer("id"));
				Set<ObjectId> servicesSet = new HashSet<>(servicesIds);
				if (servicesSet != null && !servicesSet.isEmpty())
					doctorCollection.setServices(servicesSet);
				else
					doctorCollection.setServices(null);
			} else {
				doctorCollection.setServices(null);
			}

			doctorCollection.setRegisterNumber(request.getRegisterNumber());
			doctorCollection.setUserId(userCollection.getId());
			doctorCollection.setCreatedTime(new Date());
			doctorCollection = doctorRepository.save(doctorCollection);

			userCollection = userRepository.save(userCollection);

			if (DPDoctorUtils.allStringsEmpty(request.getHospitalId(), request.getLocationId())) {
				hospitalCollection = new HospitalCollection();
				BeanUtil.map(request, hospitalCollection);
				hospitalCollection.setCreatedTime(new Date());
				hospitalCollection
						.setHospitalUId(UniqueIdInitial.HOSPITAL.getInitial() + DPDoctorUtils.generateRandomId());
				hospitalCollection = hospitalRepository.save(hospitalCollection);

				// save location for hospital
				locationCollection = new LocationCollection();
				if (request.getLocationName().contains("&")) {
					request.setLocationName(request.getLocationName().replace("&", "and"));
				}
				BeanUtil.map(request, locationCollection);
				if (locationCollection.getId() == null) {
					locationCollection.setCreatedTime(new Date());
				}
				locationCollection
						.setLocationUId(UniqueIdInitial.LOCATION.getInitial() + DPDoctorUtils.generateRandomId());
				locationCollection.setHospitalId(hospitalCollection.getId());
				locationCollection.setIsActivate(true);
//				List<GeocodedLocation> geocodedLocations = locationServices
//						.geocodeLocation((!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
//								? locationCollection.getStreetAddress() + ", "
//								: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
//										? locationCollection.getLandmarkDetails() + ", "
//										: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
//										? locationCollection.getLocality() + ", "
//										: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
//										? locationCollection.getCity() + ", "
//										: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
//										? locationCollection.getState() + ", "
//										: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
//										? locationCollection.getCountry() + ", "
//										: "")
//								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
//										? locationCollection.getPostalCode()
//										: ""));
//
//				if (geocodedLocations != null && !geocodedLocations.isEmpty())
//					BeanUtil.map(geocodedLocations.get(0), locationCollection);

				locationCollection = locationRepository.save(locationCollection);
			}
			// save user location.
			DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
			doctorClinicProfileCollection.setDoctorId(userCollection.getId());
			doctorClinicProfileCollection.setLocationId(locationCollection.getId());
			doctorClinicProfileCollection.setCreatedTime(new Date());
			doctorClinicProfileCollection.setIsNutritionist(request.getIsNutritionist());
			doctorClinicProfileCollection.setIsSuperAdmin(true);
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);

			Collection<ObjectId> roleIds = CollectionUtils.collect(roleCollections,
					new BeanToPropertyValueTransformer("id"));
			List<UserRoleCollection> userRoleCollections = new ArrayList<>();
			for (ObjectId roleId : roleIds) {
				UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleId,
						locationCollection.getId(), locationCollection.getHospitalId());
				userRoleCollection.setCreatedTime(new Date());
				userRoleCollections.add(userRoleCollection);
			}
			userRoleRepository.saveAll(userRoleCollections);

			if (request.getMrCode() != null) {
				pcUserCollection = pcUserRepository.findByMrCode(request.getMrCode());
				List<PharmaLicenseResponse> pharmaLicenseResponses = pharmaService
						.getLicenses(pcUserCollection.getCompanyId().toString(), 0, 0);
				for (PharmaLicenseResponse pharmaLicenseResponse : pharmaLicenseResponses) {
					if (pharmaLicenseResponse.getAvailable() > 0) {
						licenseResponse = pharmaLicenseResponse;
						break;
					}
				}
			}

			// Subscribe Doctor with Clinic
			SubscriptionDetail detail = new SubscriptionDetail();
			detail.setCreatedBy("Admin");
			detail.setDoctorId(userCollection.getId().toString());
			detail.setIsDemo(true);
			detail.setMonthsforSms(1);
			detail.setMonthsforSuscrption(1);
			detail.setNoOfsms(500);
			Set<String> locationSet = new HashSet<String>();
			locationSet.add(locationCollection.getId().toString());
			detail.setLocationIds(locationSet);
			if (licenseResponse != null) {
				detail.setIsDemo(false);
				detail.setFromDate(new Date());
				detail.setToDate(DateUtils.addMonths(new Date(), licenseResponse.getDuration()));
				detail.setLicenseId(licenseResponse.getId());
				licenseResponse.setAvailable(licenseResponse.getAvailable() - 1);
				licenseResponse.setConsumed(licenseResponse.getConsumed() + 1);
				PharmaLicenseCollection pharmaLicenseCollection = new PharmaLicenseCollection();
				BeanUtil.map(licenseResponse, pharmaLicenseCollection);
				pharmaLicenseRepository.save(pharmaLicenseCollection);

			}
			subscriptionDetailServices.activate(detail);

			// save token
			TokenCollection tokenCollection = new TokenCollection();
			tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
			tokenCollection.setCreatedTime(new Date());
			tokenCollection = tokenRepository.save(tokenCollection);

			// commented for error checking
			/*
			 * String body = mailBodyGenerator .generateActivationEmailBody(
			 * (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") +
			 * userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm",
			 * null, null); mailService.sendEmail(userCollection.getEmailAddress(),
			 * signupSubject, body, null);
			 * 
			 * System.out.println(body); // user.setPassword(null);
			 * 
			 * if (userCollection.getMobileNumber() != null) { SMSTrackDetail smsTrackDetail
			 * = new SMSTrackDetail();
			 * 
			 * smsTrackDetail.setType("BEFORE_VERIFICATION_TO_DOCTOR"); SMSDetail smsDetail
			 * = new SMSDetail(); smsDetail.setUserId(userCollection.getId());
			 * smsDetail.setUserName(userCollection.getFirstName()); SMS sms = new SMS();
			 * sms.setSmsText("Welcome " + (userCollection.getTitle() != null ?
			 * userCollection.getTitle() + " " : "") + userCollection.getFirstName() +
			 * " to Healthcoco. We will contact you shortly to get you started. Download the Healthcoco+ app now: "
			 * + doctorAppLink +
			 * ". For queries, please feel free to contact us at support@healthcoco.com");
			 * 
			 * SMSAddress smsAddress = new SMSAddress();
			 * smsAddress.setRecipient(userCollection.getMobileNumber());
			 * sms.setSmsAddress(smsAddress);
			 * 
			 * smsDetail.setSms(sms); smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			 * List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			 * smsDetails.add(smsDetail); smsTrackDetail.setSmsDetails(smsDetails);
			 * sMSServices.sendSMS(smsTrackDetail, true); }
			 */
			response = new DoctorSignUp();
			User user = new User();
			// user.setId(userCollection.getId().toString());
			userCollection.setPassword(null);
			BeanUtil.map(userCollection, user);
			// BeanUtil.map(locationCollection,user);
			user.setEmailAddress(userCollection.getEmailAddress());
			user.setSpecialities(specialities);
			user.setServices(services);
			response.setUser(user);

			List<AccessControl> accessControls = accessControlServices.getAllAccessControls(roleIds, null, null);
			List<Role> roles = new ArrayList<Role>();
			if (accessControls != null && !accessControls.isEmpty())
				for (AccessControl accessControl : accessControls) {
					Role role = new Role();
					for (RoleCollection roleCollection : roleCollections) {
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.DOCTOR.getRole());
					}
					BeanUtil.map(accessControl.getAccessModules(), role);
					roles.add(role);
				}

			/*
			 * DoctorContactUsCollection doctorContactUsCollection =
			 * doctorContactUsRepository
			 * .findByUserNameRegexAndEmailAddressRegex(request.getEmailAddress()); if
			 * (doctorContactUsCollection != null) {
			 * doctorContactUsCollection.setContactState(DoctorContactStateType.SIGNED_UP);
			 * doctorContactUsRepository.save(doctorContactUsCollection); }
			 */

			Hospital hospital = new Hospital();
			BeanUtil.map(hospitalCollection, hospital);
			List<LocationAndAccessControl> locations = new ArrayList<LocationAndAccessControl>();

			LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
			BeanUtil.map(locationCollection, locationAndAccessControl);
			locationAndAccessControl.setRoles(roles);

			locations.add(locationAndAccessControl);
			hospital.setLocationsAndAccessControl(locations);
			response.setHospital(hospital);

		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Email address already registerd. Please login");
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating doctor");
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean checkUserNameExist(String username) {
		try {
			UserCollection userCollection = userRepository.findByUserName(username);
			if (userCollection == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean checkMobileNumExist(String mobileNum) {
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNum);
			if (userCollections != null) {
				if (!userCollections.isEmpty()) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public Boolean checkEmailAddressExist(String email) {
		try {
			List<UserCollection> userCollections = userRepository.findByEmailAddressStartsWithIgnoreCase(email);
			if (userCollections != null) {
				if (!userCollections.isEmpty()) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public User adminSignUp(AdminSignupRequest request) {
		User user = null;
		try {
			UserCollection userCollection = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(),
					request.getAdminType().getType());
			if (userCollection == null) {
				String password;
				userCollection = new UserCollection();
				BeanUtil.map(request, userCollection);
				userCollection.setUserName(UniqueIdInitial.ADMIN.getInitial() + request.getMobileNumber());
				userCollection.setFirstName(request.getFirstName());
				userCollection.setEmailAddress(request.getEmailAddress());
				userCollection.setMobileNumber(request.getMobileNumber());
				userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
				userCollection.setIsVerified(true);
				userCollection.setIsActive(true);
				userCollection.setCreatedTime(new Date());
				userCollection.setUserState(UserState.ADMIN);

//				userCollection.setSalt(DPDoctorUtils.generateSalt());
//				String salt = new String(userCollection.getSalt());

				if (DPDoctorUtils.anyStringEmpty(request.getPwd())) {
					password = new String("admin@123");
				} else {
					password = request.getPwd();
				}
				password = passwordEncoder.encode(password);
				userCollection.setPassword(password.toCharArray());
				userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
				userCollection = userRepository.save(userCollection);
				user = new User();
				BeanUtil.map(userCollection, user);

			} else {
				throw new BusinessException(ServiceError.NotAcceptable,
						"Mobile Number already registerd. Please login");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating ADMIN");
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating ADMIN");
		}
		return user;
	}

	@Override
	@Transactional
	public Boolean resendVerificationEmail(String emailaddress) {
		UserCollection userCollection = null;
		Boolean response = false;
		try {
			Criteria criteria = new Criteria("userName").regex(emailaddress, "i");
			Query query = new Query();
			query.addCriteria(criteria);
			List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
			if (userCollections != null && !userCollections.isEmpty())
				userCollection = userCollections.get(0);

			if (userCollection != null) {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(userCollection.getId());
				DoctorClinicProfileCollection doctorClinicProfileCollection = null;
				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);
					// save token
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					// send activation email
					String body = mailBodyGenerator.generateActivationEmailBody(
							(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName(),
							tokenCollection.getId(), "mailTemplate.vm", null, null);
					mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);

					response = true;
				}

			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While sending verification email");
			throw new BusinessException(ServiceError.Unknown, "Error While sending verification email");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean resendVerificationSMS(String mobileNumber) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findByMobileNumberAndUserState(mobileNumber,
					UserState.PHARMACY.toString());
			if (userCollection != null) {
				if (userCollection.getUserState() == UserState.PHARMACY) {
					if (userCollection.getMobileNumber().trim().equals(mobileNumber.trim())) {
						TokenCollection tokenCollection = new TokenCollection();
						tokenCollection.setResourceId(userCollection.getId());
						tokenCollection.setCreatedTime(new Date());
						tokenCollection = tokenRepository.save(tokenCollection);
						SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(
								null, null, null, null, null, "Hi, please verify your account "
										+ pharmacyVerificationLink + tokenCollection.getId() + "\n Thanks you",
								userCollection.getMobileNumber(), "resetPassword");
						sMSServices.sendSMS(smsTrackDetail, false);
					} else {
						logger.warn("Mobile Number is empty.");
						throw new BusinessException(ServiceError.InvalidInput, "Mobile Number is empty.");
					}

					response = true;
				}

			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While sending verification SMS");
			throw new BusinessException(ServiceError.Unknown, "Error While sending verification SMS");
		}
		return response;
	}

	@Override
	@Transactional
	public Locale signupLocale(Locale locale) {
		Locale response = null;
		ESUserLocaleDocument esUserLocaleDocument = null;
		try {

			// System.out.println(localePassword);
			UserCollection userCollection = userRepository.findByMobileNumberAndUserState(locale.getContactNumber(),
					UserState.PHARMACY.toString());

			if (userCollection == null) {
				LocaleContactUsCollection contactUsCollection = localeContactUsRepository
						.findByContactNumber(locale.getContactNumber());
				contactUsCollection.setContactStateType(LocaleContactStateType.INTERESTED);

				userCollection = new UserCollection();
				userCollection.setUserName(UniqueIdInitial.PHARMACY.getInitial() + locale.getContactNumber());
				userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
				userCollection.setIsVerified(false);
				userCollection.setIsActive(false);
				userCollection.setFirstName(locale.getRegisteredOwnerName());
				userCollection.setCreatedTime(new Date());
				userCollection.setMobileNumber(locale.getContactNumber());
				userCollection.setUserState(UserState.PHARMACY);
				userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
				userCollection = userRepository.save(userCollection);
				LocaleCollection localeCollection = new LocaleCollection();
				BeanUtil.map(locale, localeCollection);
				localeCollection.setLocaleUId(UniqueIdInitial.PHARMACY.getInitial() + DPDoctorUtils.generateRandomId());
				localeContactUsRepository.save(contactUsCollection);
				if (localeCollection.getAddress() != null) {
					Address address = localeCollection.getAddress();
					List<GeocodedLocation> geocodedLocations = locationServices
							.geocodeLocation((!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
									? address.getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getLocality())
											? address.getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getState())
											? address.getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getCountry())
											? address.getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode()
											: ""));

					if (geocodedLocations != null && !geocodedLocations.isEmpty())
						BeanUtil.map(geocodedLocations.get(0), localeCollection);
				}
				localeCollection.setCreatedTime(new Date());
				localeCollection = localeRepository.save(localeCollection);
				if (localeCollection != null) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);
					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(
							null, null, null, null, null, "Hi, please verify your account " + pharmacyVerificationLink
									+ tokenCollection.getId() + "\n Thank you",
							userCollection.getMobileNumber(), "resetPassword");
					response = new Locale();
					sMSServices.sendSMS(smsTrackDetail, false);
					BeanUtil.map(localeCollection, response);
				}
				esUserLocaleDocument = new ESUserLocaleDocument();
				BeanUtil.map(userCollection, esUserLocaleDocument);
				BeanUtil.map(localeCollection, esUserLocaleDocument);
				esUserLocaleDocument.setUserId(userCollection.getId().toString());
				esUserLocaleDocument.setLocaleId(localeCollection.getId().toString());
				transnationalService.addResource(localeCollection.getId(), Resource.PHARMACY, false);
				esRegistrationService.addLocale(esUserLocaleDocument);
			} else {
				throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
			}
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	/*
	 * public CollectionBoy signupCollectionBoys(CollectionBoy collectionBoy) {
	 * CollectionBoy response = null; ESCollectionBoyDocument
	 * esCollectionBoyDocument = null; try { // String localePassword =
	 * DPDoctorUtils.randomString(8); // System.out.println(localePassword);
	 * UserCollection userCollection = new UserCollection();
	 * userCollection.setUserName(UniqueIdInitial.COLLECTION_BOYS.getInitial() +
	 * collectionBoy.getMobileNumber());
	 * userCollection.setUserUId(UniqueIdInitial.USER.getInitial() +
	 * DPDoctorUtils.generateRandomId()); userCollection.setIsVerified(true);
	 * userCollection.setIsActive(true);
	 * userCollection.setFirstName(collectionBoy.getName());
	 * userCollection.setCreatedTime(new Date());
	 * userCollection.setMobileNumber(collectionBoy.getMobileNumber());
	 * userCollection.setUserState(UserState.COLLECTION_BOY);
	 * userCollection.setSalt(DPDoctorUtils.generateSalt()); String salt = new
	 * String(userCollection.getSalt()); // char[] sha3Password = //
	 * DPDoctorUtils.getSHA3SecurePassword(localePassword.toCharArray()); char[]
	 * sha3Password =
	 * DPDoctorUtils.getSHA3SecurePassword(collectionBoy.getMobileNumber().
	 * toCharArray()); String password = new String(sha3Password); password =
	 * passwordEncoder.encodePassword(password, salt);
	 * userCollection.setPassword(password.toCharArray());
	 * userCollection.setColorCode(new
	 * RandomEnum<ColorCode>(ColorCode.class).random().getColor()); userCollection =
	 * userRepository.save(userCollection);
	 * 
	 * CollectionBoyCollection collectionBoyCollection = new
	 * CollectionBoyCollection(); BeanUtil.map(collectionBoy,
	 * collectionBoyCollection); //
	 * localeCollection.setLocaleUId(UniqueIdInitial.PHARMACY.getInitial() // +
	 * DPDoctorUtils.generateRandomId());
	 * 
	 * if (collectionBoyCollection.getAddress() != null) { Address address =
	 * collectionBoyCollection.getAddress(); List<GeocodedLocation>
	 * geocodedLocations = locationServices
	 * .geocodeLocation((!DPDoctorUtils.anyStringEmpty(address.getStreetAddress()) ?
	 * address.getStreetAddress() + ", " : "") +
	 * (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality()
	 * + ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCity()) ?
	 * address.getCity() + ", " : "") +
	 * (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() +
	 * ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ?
	 * address.getCountry() + ", " : "") +
	 * (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ?
	 * address.getPostalCode() : ""));
	 * 
	 * if (geocodedLocations != null && !geocodedLocations.isEmpty())
	 * BeanUtil.map(geocodedLocations.get(0), collectionBoyCollection); }
	 * 
	 * collectionBoyCollection =
	 * collectionBoyRepository.save(collectionBoyCollection); if
	 * (collectionBoyCollection != null) { SMSTrackDetail smsTrackDetail = new
	 * SMSTrackDetail(); smsTrackDetail.setType("LOCALE_SIGNUP"); SMSDetail
	 * smsDetail = new SMSDetail(); smsDetail.setUserId(userCollection.getId()); if
	 * (userCollection != null)
	 * smsDetail.setUserName(collectionBoyCollection.getName()); SMS sms = new
	 * SMS(); sms.setSmsText("Hi ," + collectionBoyCollection.getName() +
	 * " your registration with Healthcoco is completed. Please use provided contact number for login. Your password is "
	 * + collectionBoy.getMobileNumber());
	 * 
	 * SMSAddress smsAddress = new SMSAddress();
	 * smsAddress.setRecipient(collectionBoyCollection.getMobileNumber());
	 * sms.setSmsAddress(smsAddress);
	 * 
	 * smsDetail.setSms(sms); smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
	 * List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	 * smsDetails.add(smsDetail); smsTrackDetail.setSmsDetails(smsDetails);
	 * sMSServices.sendSMS(smsTrackDetail, true); response = new CollectionBoy();
	 * BeanUtil.map(collectionBoyCollection, response); } esCollectionBoyDocument =
	 * new ESCollectionBoyDocument(); BeanUtil.map(collectionBoyCollection,
	 * esCollectionBoyDocument);
	 * transnationalService.addResource(collectionBoyCollection.getId(),
	 * Resource.COLLECTION_BOY, false);
	 * esRegistrationService.addCollectionBoy(esCollectionBoyDocument); } catch
	 * (DuplicateKeyException de) { de.printStackTrace(); logger.error(de); throw
	 * new BusinessException(ServiceError.Unknown,
	 * "Mobile number already registerd. Please login"); } catch (BusinessException
	 * be) { be.printStackTrace(); logger.warn(be); throw be; } catch (Exception e)
	 * { e.printStackTrace(); logger.error(e +
	 * " Error occured while contacting Healthcoco"); throw new
	 * BusinessException(ServiceError.Unknown,
	 * "Error occured while contacting Healthcoco"); } return response; }
	 */
	@Override
	@Transactional
	public Boolean activateAdmin(String id, boolean isActive) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(id)).orElse(null);
			if (userCollection != null) {
				userCollection.setIsActive(isActive);
				userCollection = userRepository.save(userCollection);
				response = true;

				if (!isActive) {
					List<OAuth2AuthenticationRefreshTokenCollection> refreshTokenCollections = oAuth2RefreshTokenRepository
							.findByAuthenticationUserAuthenticationDetailsClient_IdAndAuthenticationUserAuthenticationDetailsUsername(
									"healthco2admin@16", userCollection.getMobileNumber());
					if (!refreshTokenCollections.isEmpty() && refreshTokenCollections != null) {
						oAuth2RefreshTokenRepository.deleteAll(refreshTokenCollections);
					}

					List<OAuth2AuthenticationAccessTokenCollection> accessTokenCollections = oAuth2AccessTokenRepository
							.findByClientIdAndUserName("healthco2admin@16", userCollection.getMobileNumber());
					if (!accessTokenCollections.isEmpty() && accessTokenCollections != null) {
						oAuth2AccessTokenRepository.deleteAll(accessTokenCollections);
					}

				}
			} else {
				throw new BusinessException(ServiceError.NotFound, "Admin not found for id");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error occured while getting ADMIN");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting ADMIN");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean editAdminProfile(EditAdminRequest request) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (userCollection != null) {
				if (!DPDoctorUtils.allStringsEmpty(request.getAdminType())) {
					userCollection.setAdminType(AdminType.valueOf(request.getAdminType()));

				}
				if (!DPDoctorUtils.allStringsEmpty(request.getCity())) {
					userCollection.setCity(request.getCity());

				}
				if (!DPDoctorUtils.allStringsEmpty(request.getEmailAddress())) {
					userCollection.setEmailAddress(request.getEmailAddress());

				}
				if (!DPDoctorUtils.allStringsEmpty(request.getFirstName())) {
					userCollection.setFirstName(request.getFirstName());

				}
				if (!DPDoctorUtils.allStringsEmpty(request.getNotes())) {
					userCollection.setNotes(request.getNotes());
				}

				if (request.getIsAnonymousAppointment() != null) {
					userCollection.setIsAnonymousAppointment(request.getIsAnonymousAppointment());
				}
				
				if (request.getIsBuddy() != null) {
					userCollection.setIsBuddy(request.getIsBuddy());
				}

				if (request.getIsAgent() != null) {
					userCollection.setIsAgent(request.getIsAgent());
				}
				if (request.getIsCampDoctor() != null) {
					userCollection.setIsCampDoctor(request.getIsCampDoctor());
				}


//				if(!DPDoctorUtils.allStringsEmpty(request.getPassword()))
//				{
//					request.setPassword(request.getPassword());
//				}
				userRepository.save(userCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NotFound, "Admin not found for id");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error occured while edit ADMIN");
			throw new BusinessException(ServiceError.Unknown, "Error occured while edit ADMIN");
		}
		return response;
	}

	@Override
	@Transactional
	public PharmaCompanyResponse signupPharmaCompany(PharmaCompanySignupRequest request) {
		PharmaCompanyResponse response = null;

		try {
			PharmaCompanyCollection pharmaCompanyCollection = new PharmaCompanyCollection();
			BeanUtil.map(request, pharmaCompanyCollection);
			pharmaCompanyCollection.setUserName(request.getName());
			pharmaCompanyCollection.setCompanyCode(request.getCompanyInitial() + 0);
			pharmaCompanyCollection = pharmaCompanyRepository.save(pharmaCompanyCollection);
			response = new PharmaCompanyResponse();
			BeanUtil.map(pharmaCompanyCollection, response);

			PCUserCollection userCollection = new PCUserCollection();
			userCollection.setCompanyId(pharmaCompanyCollection.getId());
			userCollection.setUserName(request.getEmailAddress());
			userCollection.setUserUId(pharmaCompanyCollection.getCompanyInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setIsVerified(true);
			userCollection.setIsActive(false);
			userCollection.setName(request.getName());
			userCollection.setCreatedTime(new Date());
			userCollection.setEmailAddress(request.getEmailAddress());
			userCollection.setMobileNumber(request.getMobileNumber());
			userCollection.setMrCode(pharmaCompanyCollection.getCompanyCode());
			userCollection.setRole(RoleEnum.SUPER_ADMIN.getRole());
			userCollection = pcUserRepository.save(userCollection);

			if (userCollection.getEmailAddress() != null) {
				TokenCollection tokenCollection = new TokenCollection();
				tokenCollection.setResourceId(userCollection.getId());
				tokenCollection.setCreatedTime(new Date());
				tokenCollection = tokenRepository.save(tokenCollection);

				String body = mailBodyGenerator.generateActivationEmailBodyForPharmaCompany(userCollection.getName(),
						tokenCollection.getId(), "pharmaAccountActivateTemplate.vm", null, null);
				mailService.sendEmail(userCollection.getEmailAddress(), "Password setup for Healthcoco account.", body,
						null);
			}

		} catch (DuplicateKeyException de) {
			de.printStackTrace();
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			be.printStackTrace();
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown, "Error occured while contacting Healthcoco");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean sendSetPasswordEmail(String id) {
		Boolean status = false;
		PharmaCompanyCollection pharmaCompanyCollection = null;
		try {
			pharmaCompanyCollection = pharmaCompanyRepository.findById(new ObjectId(id)).orElse(null);
			if (pharmaCompanyCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Pharma company not found");
			}

			// Criteria criteria = new
			// Criteria("userName").is(pharmaCompanyCollection.getEmailAddress());
			// Query query = new Query();
			// query.addCriteria(criteria);
			PCUserCollection userCollection = pcUserRepository
					.findByUserName(pharmaCompanyCollection.getEmailAddress());
			if (userCollection != null) {
				if (userCollection.getEmailAddress() != null) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					String body = mailBodyGenerator.generateActivationEmailBodyForPharmaCompany(
							userCollection.getName(), tokenCollection.getId(), "pharmaAccountActivateTemplate.vm", null,
							null);
					mailService.sendEmail(userCollection.getEmailAddress(), "Password setup for Healthcoco account.",
							body, null);
					status = true;
				}
			} else {
				throw new BusinessException(ServiceError.NoRecord, "User not found.");
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	@Override
	@Transactional
	public Boolean sendDoctorWelcomeMessage(String id) {
		DoctorContactUsCollection doctorContactUs = null;
		Boolean status = false;
		try {
			doctorContactUs = doctorContactUsRepository.findById(new ObjectId(id)).orElse(null);
			TokenCollection tokenCollection = new TokenCollection();
			tokenCollection.setResourceId(doctorContactUs.getId());
			tokenCollection.setCreatedTime(new Date());
			tokenCollection = tokenRepository.save(tokenCollection);
			String body = mailBodyGenerator.doctorWelcomeEmailBody(
					doctorContactUs.getTitle() + " " + doctorContactUs.getFirstName(), tokenCollection.getId(),
					"doctorWelcomeTemplate.vm", null, null);
			mailService.sendEmail(doctorContactUs.getEmailAddress(), doctorWelcomeSubject, body, null);

			System.out.println(tokenCollection.getId());
			System.out.println(body);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return status;
	}

	@Override
	@Transactional
	public CollectionBoyResponse signupCollectionBoys(CollectionBoy collectionBoy) {
		CollectionBoyResponse response = null;
		ESCollectionBoyDocument esCollectionBoyDocument = null;
		try {

			if (DPDoctorUtils.anyStringEmpty(collectionBoy.getPassword())) {
				collectionBoy.setPassword(DPDoctorUtils.randomString(8));
			}

			UserCollection userCollection = new UserCollection();
			userCollection.setUserName(UniqueIdInitial.PHARMACY.getInitial() + collectionBoy.getMobileNumber());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setIsVerified(true);
			userCollection.setIsActive(true);
			userCollection.setFirstName(collectionBoy.getName());
			userCollection.setCreatedTime(new Date());
			userCollection.setMobileNumber(collectionBoy.getMobileNumber());
			userCollection.setUserState(UserState.DELIVERY_BOY);
//			userCollection.setSalt(DPDoctorUtils.generateSalt());
//			String salt = new String(userCollection.getSalt());
//			char[] sha3Password = DPDoctorUtils.getSHA3SecurePassword(collectionBoy.getPassword().toCharArray());
//			String password = new String(sha3Password);

			String password = passwordEncoder.encode(collectionBoy.getPassword());
			userCollection.setPassword(password.toCharArray());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection = userRepository.save(userCollection);

			CollectionBoyCollection collectionBoyCollection = new CollectionBoyCollection();
			BeanUtil.map(collectionBoy, collectionBoyCollection);
			collectionBoyCollection.setLabType(collectionBoy.getLabType().getType());
			collectionBoyCollection.setUserId(userCollection.getId());
			// localeCollection.setLocaleUId(UniqueIdInitial.PHARMACY.getInitial()
			// + DPDoctorUtils.generateRandomId());

			/*
			 * if (collectionBoyCollection.getAddress() != null) { Address address =
			 * collectionBoyCollection.getAddress(); List<GeocodedLocation>
			 * geocodedLocations = locationServices
			 * .geocodeLocation((!DPDoctorUtils.anyStringEmpty(address.getStreetAddress()) ?
			 * address.getStreetAddress() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality()
			 * + ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCity()) ?
			 * address.getCity() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() +
			 * ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ?
			 * address.getCountry() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ?
			 * address.getPostalCode() : ""));
			 * 
			 * if (geocodedLocations != null && !geocodedLocations.isEmpty())
			 * BeanUtil.map(geocodedLocations.get(0), collectionBoyCollection); }
			 */

			collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
			if (collectionBoyCollection != null) {
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("CB_SIGNUP");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				if (userCollection != null)
					smsDetail.setUserName(collectionBoyCollection.getName());
				SMS sms = new SMS();
				sms.setSmsText("Hi ," + collectionBoyCollection.getName()
						+ " your registration with Healthcoco is completed. Please use provided contact number for login. Your password is "
						+ collectionBoy.getPassword());

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(collectionBoyCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
				response = new CollectionBoyResponse();
				BeanUtil.map(collectionBoyCollection, response);
				// response.setPassword(null);
			}
			esCollectionBoyDocument = new ESCollectionBoyDocument();
			BeanUtil.map(collectionBoyCollection, esCollectionBoyDocument);
			transnationalService.addResource(collectionBoyCollection.getId(), Resource.COLLECTION_BOY, false);
			esRegistrationService.addCollectionBoy(esCollectionBoyDocument);
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown,
					" Error occured while contacting Healthcoco. Please Check mobile number or contact administration");
		}
		return response;
	}

}
