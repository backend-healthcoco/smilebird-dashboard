package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.AdminSignupRequest;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.collections.PCUserCollection;
import com.dpdocter.collections.PharmaLicenseCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AdminType;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.ColorCode.RandomEnum;
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
	private ServicesRepository servicesRepository;

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
						Arrays.asList(RoleEnum.ADMIN.getRole(), RoleEnum.SUPER_ADMIN.getRole()), null, null);
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
							role.setRole(RoleEnum.ADMIN.getRole());
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.SUPER_ADMIN.getRole());
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

}
