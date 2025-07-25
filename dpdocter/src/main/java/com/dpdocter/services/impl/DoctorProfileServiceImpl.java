package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicImage;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorDetails;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.beans.DoctorOnlineConsultationAddEditRequest;
import com.dpdocter.beans.DoctorOnlineGeneralInfo;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.beans.DoctorRegistrationDetails;
import com.dpdocter.beans.DoctorSlugUrlRequest;
import com.dpdocter.beans.DoctorTrainingAddEditRequest;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Services;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.CardioPermissionEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.RegistrationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MedicalCouncilRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditMRCodeRequest;
import com.dpdocter.request.AddEditNutritionistRequest;
import com.dpdocter.request.AddEditSEORequest;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorAddEditCityRequest;
import com.dpdocter.request.DoctorAddEditFacilityRequest;
import com.dpdocter.request.DoctorAppointmentNumbersAddEditRequest;
import com.dpdocter.request.DoctorAppointmentSlotAddEditRequest;
import com.dpdocter.request.DoctorConsultationFeeAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorDOBAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorExperienceDetailAddEditRequest;
import com.dpdocter.request.DoctorGenderAddEditRequest;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;
import com.dpdocter.request.DoctorNameAddEditRequest;
import com.dpdocter.request.DoctorOnlineWorkingTimeRequest;
import com.dpdocter.request.DoctorProfessionalAddEditRequest;
import com.dpdocter.request.DoctorProfessionalStatementAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorServicesAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.request.DoctorVisitingTimeAddEditRequest;

import com.dpdocter.response.DoctorMultipleDataAddEditResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.DynamicUIService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.PushNotificationServices;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

	private static Logger logger = LogManager.getLogger(DoctorProfileServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private ServicesRepository servicesRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MedicalCouncilRepository medicalCouncilRepository;

	@Autowired
	private EducationInstituteRepository educationInstituteRepository;

	@Autowired
	private EducationQualificationRepository educationQualificationRepository;

	@Autowired
	DynamicUIRepository dynamicUIRepository;

	@Autowired
	DynamicUIService dynamicUIService;

	@Autowired
	ESDoctorRepository esdoctorRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Override
	@Transactional
	public Boolean addEditName(DoctorNameAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			BeanUtil.map(request, userCollection);
			BeanUtil.map(request, doctorCollection);
			userRepository.save(userCollection);
			doctorRepository.save(doctorCollection);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorExperience addEditExperience(DoctorExperienceAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		DoctorExperience response = new DoctorExperience();
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (request.getExperience() > 0) {
				DoctorExperience doctorExperience = new DoctorExperience();
				doctorExperience.setExperience(request.getExperience());
				doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
				doctorCollection.setExperience(doctorExperience);
				response = new DoctorExperience();
				BeanUtil.map(doctorExperience, response);
			} else {
				doctorCollection.setExperience(null);
			}
			doctorRepository.save(doctorCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	@Override

	@Scheduled(cron = "0 30 13 11 3 ?", zone = "IST")
	public Boolean updateExperience() {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		System.out.println("Experience ");
		try {

//			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//	
//				localCalendar.setTime(new Date(Long.parseLong("1514764800000")));
//				int currentDay1 = localCalendar.get(Calendar.DATE);
//				int currentMonth1 = localCalendar.get(Calendar.MONTH) + 1;
//				int currentYear1 = localCalendar.get(Calendar.YEAR);
//
//			//	DateTime
//			DateTime	fromDateTime = new DateTime(currentYear1, currentMonth1, currentDay1, 0, 0, 0,
//						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
//
//			Criteria criteria=new Criteria();
//				criteria.and("createdTime").gte(fromDateTime);
//				System.out.println(fromDateTime);
//			Aggregation aggregation=Aggregation.newAggregation(Aggregation.match(criteria));
//			System.out.println("Aggregation"+aggregation);
//			List<DoctorCollection> doctorExperiences = mongoTemplate.aggregate(aggregation,DoctorCollection.class,DoctorCollection.class).getMappedResults();
			List<DoctorCollection> doctorExperiences = doctorRepository.findAll();
			System.out.println(doctorExperiences.size());
			for (DoctorCollection doctorEperience : doctorExperiences) {
				String id = doctorEperience.getId().toString();
				doctorCollection = doctorRepository.findById(new ObjectId(id)).orElse(null);

				if (doctorCollection.getExperience() != null) {
					System.out.println("enter");
					long created = doctorCollection.getCreatedTime().getTime();
					Date current = new Date();
					Integer currentMonth = current.getMonth();
					Integer currentYear = current.getYear();
					long current1 = current.getTime();
					Integer createdYear = doctorCollection.getCreatedTime().getYear();
					Integer createdMonth = doctorCollection.getCreatedTime().getMonth();

					Integer diffMonth = 0;

					if (currentYear > createdYear) {

						Integer diff = (currentYear - createdYear);
						System.out.println("diffYear" + diff);
						if (currentMonth > createdMonth) {
							diffMonth = currentMonth - createdMonth;
						} else {
							currentYear = currentYear - 1;
							currentMonth = currentMonth + 12;
							diffMonth = currentMonth - createdMonth;

						}

						System.out.println("currentMonth" + diffMonth);

						// Date created=doctorCollection.getCreatedTime();
//				long dif=current1-created;
//				Date d2=new Date(dif* 1000);
//				int month=d2.getMonth();
//				int year=d2.getYear();
//				//Date d3=dif
//				System.out.println(d2); 
//				//Date dif=current;
//					Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

						// Integer currentYear = new Date().getYear();
//

						System.out.println(doctorCollection.getId());
						System.out.println(doctorCollection.getExperience());

						Integer total = 0;
						if (diff > 1) {
							int dif = 0;
							if (currentMonth > createdMonth)
								dif = diff - 1;

							if (doctorCollection.getIsExpUpdated() == true)
								total = doctorCollection.getExperience().getExperience() + 1;
							else {
								total = doctorCollection.getExperience().getExperience() + dif;
								doctorCollection.setIsExpUpdated(true);
							}
						} else if (diff == 1 && currentMonth >= createdMonth) {
							if (doctorCollection.getIsExpUpdated() == true)
								total = doctorCollection.getExperience().getExperience() + 1;
							else {
								total = doctorCollection.getExperience().getExperience() + diff;
								doctorCollection.setIsExpUpdated(true);
							}

						}
						if (total > 0) {
							doctorCollection.getExperience().setExperience(total);
							System.out.println(doctorCollection.getExperience());

							doctorCollection.setUpdatedTime(new Date());
							// System.out.println(total);
							doctorRepository.save(doctorCollection);

							List<ESDoctorDocument> doctorDocuments = esdoctorRepository
									.findByUserId(doctorEperience.getUserId().toString());

							if (doctorDocuments != null)
								for (ESDoctorDocument doctorDocument : doctorDocuments) {

									if (doctorDocument.getExperience() != null) {
										doctorDocument.getExperience().setExperience(total);
										esdoctorRepository.save(doctorDocument);
									} else {
										DoctorExperience experience = new DoctorExperience();
										experience.setExperience(total);
										experience.setPeriod(DoctorExperienceUnit.YEAR);
										doctorDocument.setExperience(experience);
										esdoctorRepository.save(doctorDocument);
									}
								}
						}
						System.out.println("Scheduler running for doctor experience");
					}
					response = true;
				}

			}

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;

	}

	@Override
	@Transactional
	public Boolean addEditContact(DoctorContactAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			userCollection.setMobileNumber(request.getMobileNumber());
			userCollection.setCountryCode(request.getCountryCode());
			doctorCollection.setAdditionalNumbers(request.getAdditionalNumbers());
			doctorCollection.setOtherEmailAddresses(request.getOtherEmailAddresses());
			userRepository.save(userCollection);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditEducation(DoctorEducationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setEducation(request.getEducation());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Speciality addeditSpecialities(Speciality request) {
		Speciality response;
		try {

			SpecialityCollection specialityCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				specialityCollection = specialityRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (specialityCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " Speciality not present with Id");
				}
				BeanUtil.map(request, specialityCollection);
				specialityCollection.setUpdatedTime(new Date());
				specialityCollection.setCreatedBy("Admin");
			} else {
				specialityCollection = new SpecialityCollection();
				BeanUtil.map(request, specialityCollection);
				specialityCollection.setUpdatedTime(new Date());
				specialityCollection.setCreatedTime(new Date());
				specialityCollection.setCreatedBy("Admin");
			}
			specialityCollection = specialityRepository.save(specialityCollection);
			response = new Speciality();
			BeanUtil.map(specialityCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Speciality");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Speciality");
		}
		return response;
	}

	@Override
	@Transactional
	public ProfessionalMembership addeditProfessionalMembership(ProfessionalMembership request) {
		ProfessionalMembership response;
		try {

			ProfessionalMembershipCollection professionalMembershipCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				professionalMembershipCollection = professionalMembershipRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				if (professionalMembershipCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " professional Membership not present with Id");
				}
				BeanUtil.map(request, professionalMembershipCollection);
				professionalMembershipCollection.setUpdatedTime(new Date());
				professionalMembershipCollection.setCreatedTime(new Date());
				professionalMembershipCollection.setCreatedBy("Admin");
			} else {
				professionalMembershipCollection = new ProfessionalMembershipCollection();
				BeanUtil.map(request, professionalMembershipCollection);
				professionalMembershipCollection.setCreatedTime(new Date());
				professionalMembershipCollection.setCreatedBy("Admin");
			}
			professionalMembershipCollection = professionalMembershipRepository.save(professionalMembershipCollection);
			response = new ProfessionalMembership();
			BeanUtil.map(professionalMembershipCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing  professional Membership");
			throw new BusinessException(ServiceError.Unknown, "Error Editing  professional Membership");
		}
		return response;
	}

	@Override
	@Transactional
	public List<MedicalCouncil> getMedicalCouncils(int page, int size, String updatedTime, String searchTerm,
			Boolean discarded) {
		List<MedicalCouncil> medicalCouncils = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (searchTerm != null)
				criteria = criteria.orOperator(new Criteria("medicalCouncil").regex("^" + searchTerm, "i"),
						new Criteria("medicalCouncil").regex("^" + searchTerm));

			if (discarded != null)
				criteria.and("discarded").is(discarded);

//			if (size > 0)
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")),
//						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
//			else
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
//						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "medicalCouncil")));

			}
			System.out.println("aggregation" + aggregation);
			medicalCouncils = mongoTemplate.aggregate(aggregation, MedicalCouncilCollection.class, MedicalCouncil.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Medical Councils");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Medical Councils");
		}
		return medicalCouncils;
	}

	@Override
	@Transactional
	public MedicalCouncil addMedicalCouncils(MedicalCouncil request) {
		MedicalCouncil medicalCouncil = null;
		try {
			MedicalCouncilCollection medicalCouncilCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				medicalCouncilCollection = medicalCouncilRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (medicalCouncilCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "No Medical Council not present with Id");
				}
				BeanUtil.map(request, medicalCouncilCollection);
				medicalCouncilCollection.setUpdatedTime(new Date());
				medicalCouncilCollection.setCreatedBy("Admin");
			} else {
				medicalCouncilCollection = new MedicalCouncilCollection();
				BeanUtil.map(request, medicalCouncilCollection);
				medicalCouncilCollection.setCreatedTime(new Date());

			}
			medicalCouncilCollection = medicalCouncilRepository.save(medicalCouncilCollection);
			medicalCouncil = new MedicalCouncil();
			BeanUtil.map(medicalCouncilCollection, medicalCouncil);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Adding Medical Councils");
			throw new BusinessException(ServiceError.Unknown, "Error Adding Medical Councils");
		}
		return medicalCouncil;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<String> addEditSpeciality(DoctorSpecialityAddEditRequest request) {
		List<String> response = null;
		DoctorCollection doctorCollection = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			List<ObjectId> oldSpecialities = doctorCollection.getSpecialities();
			if (doctorCollection != null) {
				response = request.getSpeciality();
				if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {
					List<SpecialityCollection> specialityCollections = specialityRepository
							.findBySuperSpecialityIn(request.getSpeciality());
					Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("id"));
					if (specialityIds != null && !specialityIds.isEmpty()) {
						doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
						if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
							removeOldSpecialityPermissions(specialityIds, oldSpecialities, request.getDoctorId());
							List<ServicesCollection> servicesCollections = servicesRepository
									.findBySpecialityIdsIn(oldSpecialities);
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
									new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);
							if (doctorCollection.getServices() != null)
								doctorCollection.getServices().removeAll(services);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<ServicesCollection> servicesCollections = servicesRepository
									.findBySpecialityIdsIn(doctorCollection.getSpecialities());
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
									new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);
							if (doctorCollection.getServices() != null)
								doctorCollection.getServices().addAll(services);
							else
								doctorCollection.setServices(services);
						}
					} else {
						doctorCollection.setSpecialities(null);
						assignDefaultUIPermissions(request.getDoctorId());
					}
				} else {
					doctorCollection.setSpecialities(null);
					assignDefaultUIPermissions(request.getDoctorId());
				}
				doctorRepository.save(doctorCollection);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	private void assignDefaultUIPermissions(String doctorId) {
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		UIPermissions uiPermissions = dynamicUIService.getDefaultPermissions();
		dynamicUICollection.setUiPermissions(uiPermissions);
		dynamicUIRepository.save(dynamicUICollection);
	}

	private void removeOldSpecialityPermissions(Collection<ObjectId> specialityIds,
			Collection<ObjectId> oldSpecialities, String doctorId) {
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		if (dynamicUICollection != null) {
			for (ObjectId objectId : specialityIds) {
				if (oldSpecialities.contains(objectId))
					oldSpecialities.remove(objectId);
			}
			if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
				List<SpecialityCollection> oldSpecialityCollections = (List<SpecialityCollection>) specialityRepository
						.findByIdIn(oldSpecialities);
				@SuppressWarnings("unchecked")
				Collection<String> specialities = CollectionUtils.collect(oldSpecialityCollections,
						new BeanToPropertyValueTransformer("speciality"));
				UIPermissions uiPermissions = dynamicUICollection.getUiPermissions();
				for (String speciality : specialities) {
					if (speciality.equalsIgnoreCase("OPHTHALMOLOGIST")) {
						if (uiPermissions.getClinicalNotesPermissions() != null)
							uiPermissions.getClinicalNotesPermissions()
									.remove(OpthoPermissionEnums.OPTHO_CLINICAL_NOTES.getPermissions());
						if (uiPermissions.getPrescriptionPermissions() != null)
							uiPermissions.getPrescriptionPermissions()
									.remove(OpthoPermissionEnums.OPTHO_RX.getPermissions());
					}
					if (speciality.equalsIgnoreCase("PEDIATRICIAN")) {
						if (uiPermissions.getProfilePermissions() != null)
							uiPermissions.getProfilePermissions()
									.remove(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
					}
					if (speciality.equalsIgnoreCase("GYNECOLOGIST/OBSTETRICIAN")) {
						if (uiPermissions.getClinicalNotesPermissions() != null) {
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PA.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PV.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.PS.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.INDICATION_OF_USG.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.EDD.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.LMP.getPermissions());
							uiPermissions.getClinicalNotesPermissions()
									.remove(GynacPermissionsEnum.USG_GENDER_COUNT.getPermissions());
							uiPermissions.getProfilePermissions()
									.remove(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
						}
					}
					if (speciality.equalsIgnoreCase("CARDIOLOGIST")) {
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.ECG.getPermissions());
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.ECHO.getPermissions());
						uiPermissions.getClinicalNotesPermissions().remove(CardioPermissionEnum.XRAY.getPermissions());
						uiPermissions.getClinicalNotesPermissions()
								.remove(CardioPermissionEnum.HOLTER.getPermissions());
					}
				}
				dynamicUIRepository.save(dynamicUICollection);
			}
		}
	}

	@Override
	@Transactional
	public Boolean addEditAchievement(DoctorAchievementAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setAchievements(request.getAchievements());
			doctorRepository.save(doctorCollection);

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());
			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setAchievements(request.getAchievements());
					esdoctorRepository.save(doctorDocument);
				}

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditProfessionalStatement(DoctorProfessionalStatementAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setProfessionalStatement(request.getProfessionalStatement());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			System.out.println(request.toString());
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));

			doctorCollection.setRegistrationDetails(request.getRegistrationDetails());
			doctorRepository.save(doctorCollection);

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());
			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setRegistrationDetails(request.getRegistrationDetails());
					esdoctorRepository.save(doctorDocument);
				}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditExperienceDetail(DoctorExperienceDetailAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setExperienceDetails(request.getExperienceDetails());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public String addEditProfilePicture(DoctorProfilePictureAddEditRequest request) {
		UserCollection userCollection = null;
		String response = "";
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (request.getImage() != null) {
				String path = "profile-image";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				userCollection.setImageUrl(imageURLResponse.getImageUrl());
				userCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());

				userCollection = userRepository.save(userCollection);
				response = userCollection.getImageUrl();
				List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());
				if (doctorDocuments != null)
					for (ESDoctorDocument doctorDocument : doctorDocuments) {
						doctorDocument.setImageUrl(userCollection.getImageUrl());
						esdoctorRepository.save(doctorDocument);
					}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public String addEditCoverPicture(DoctorProfilePictureAddEditRequest request) {
		UserCollection userCollection = null;
		String response = "";
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (request.getImage() != null) {
				String path = "cover-image";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				userCollection.setCoverImageUrl(imageURLResponse.getImageUrl());
				userCollection.setCoverThumbnailImageUrl(imageURLResponse.getThumbnailUrl());
				userCollection = userRepository.save(userCollection);
				response = userCollection.getCoverImageUrl();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorProfile getDoctorProfile(String doctorId, String locationId, String hospitalId, Boolean isAdmin) {
		DoctorProfile doctorProfile = null;
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		List<String> specialities = null;
		List<String> services = null;
		List<DoctorRegistrationDetail> registrationDetails = null;
		List<String> professionalMemberships = null;
		List<DoctorClinicProfile> clinicProfile = new ArrayList<DoctorClinicProfile>();
		Subscription subscription = new Subscription();

		try {
			userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (userCollection == null || doctorCollection == null) {
				logger.error("No user found");
				throw new BusinessException(ServiceError.NoRecord, "No user found");
			}
			if (locationId == null) {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorIdAndIsActivateIsTrue(userCollection.getId());

				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						clinicProfile.add(
								getDoctorClinic(doctorClinicProfileCollection, doctorClinicProfileCollections.size()));
					}
				}
			} else {
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(userCollection.getId(), new ObjectId(locationId));
				if (doctorClinicProfileCollection != null) {
					clinicProfile.add(getDoctorClinic(doctorClinicProfileCollection, 1));

				}
			}

			doctorProfile = new DoctorProfile();
			BeanUtil.map(userCollection, doctorProfile);
			BeanUtil.map(doctorCollection, doctorProfile);

			doctorProfile.setIsTransactionalSms(doctorCollection.getIsTransactionalSms());
			doctorProfile.setDoctorId(doctorCollection.getUserId().toString());
			doctorProfile.setClinicProfile(clinicProfile);
			// set specialities using speciality ids
			if (doctorCollection.getSpecialities() != null) {
				// System.out.println(doctorCollection.getSpecialities());

				specialities = (List<String>) CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("superSpeciality"));
			}
			doctorProfile.setSpecialities(specialities);

			if (doctorCollection.getServices() != null && !doctorCollection.getServices().isEmpty()) {
				// System.out.println(doctorCollection.getServices());
				services = (List<String>) CollectionUtils.collect(
						(Collection<?>) servicesRepository.findAllById(doctorCollection.getServices()),
						new BeanToPropertyValueTransformer("service"));
				// System.out.println(services);
			}
			doctorProfile.setServices(services);

			// set medical councils using medical councils ids
			registrationDetails = new ArrayList<DoctorRegistrationDetail>();
			if (doctorProfile.getRegistrationDetails() != null && !doctorProfile.getRegistrationDetails().isEmpty()) {
				for (DoctorRegistrationDetail registrationDetail : doctorProfile.getRegistrationDetails()) {
					DoctorRegistrationDetail doctorRegistrationDetail = new DoctorRegistrationDetail();
					BeanUtil.map(registrationDetail, doctorRegistrationDetail);
					registrationDetails.add(doctorRegistrationDetail);
				}
			}
			doctorProfile.setRegistrationDetails(registrationDetails);
			// set professional memberships using professional membership ids
			if (doctorCollection.getProfessionalMemberships() != null
					&& !doctorCollection.getProfessionalMemberships().isEmpty()) {
				professionalMemberships = (List<String>) CollectionUtils.collect(
						(Collection<?>) professionalMembershipRepository
								.findAllById(doctorCollection.getProfessionalMemberships()),
						new BeanToPropertyValueTransformer("membership"));
			}
			doctorProfile.setProfessionalMemberships(professionalMemberships);

			if (doctorProfile.getRegistrationImageUrl() != null) {
				doctorProfile.setRegistrationImageUrl(getFinalImageURL(doctorProfile.getRegistrationImageUrl()));
			}

			if (doctorProfile.getPhotoIdImageUrl() != null) {
				doctorProfile.setPhotoIdImageUrl(getFinalImageURL(doctorProfile.getPhotoIdImageUrl()));
			}

			if (doctorProfile.getRegistrationThumbnailUrl() != null) {
				doctorProfile
						.setRegistrationThumbnailUrl(getFinalImageURL(doctorProfile.getRegistrationThumbnailUrl()));
			}

			// set clinic profile details
			doctorProfile.setClinicProfile(clinicProfile);

			// set subscription Detail
			SubscriptionCollection subscriptionCollection = subscriptionRepository
					.findByDoctorId(new ObjectId(doctorId));
			if (subscriptionCollection != null) {
				BeanUtil.map(subscriptionCollection, subscription);
				doctorProfile.setSubscriptionDetail(subscription);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Doctor Profile");
		}
		return doctorProfile;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private DoctorClinicProfile getDoctorClinic(DoctorClinicProfileCollection doctorClinicCollection,
			int locationSize) {
		DoctorClinicProfile doctorClinic = new DoctorClinicProfile();
		try {
			LocationCollection locationCollection = locationRepository.findById(doctorClinicCollection.getLocationId())
					.orElse(null);

			if (locationCollection != null) {
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (!DPDoctorUtils.anyStringEmpty(address) && address.length() >= 2
						&& address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				// doctorClinic.setBulkSmsCredit(doctorClinicCollection.getBulkSmsCredit());
				doctorClinic.setClinicAddress(address);
				BeanUtil.map(locationCollection, doctorClinic);
			}
			if (doctorClinicCollection != null)
				BeanUtil.map(doctorClinicCollection, doctorClinic);
			doctorClinic.setLocationId(doctorClinicCollection.getLocationId().toString());
			doctorClinic.setDoctorId(doctorClinicCollection.getDoctorId().toString());

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		}
		return doctorClinic;
	}

	@Override
	@Transactional
	public List<ProfessionalMembership> getProfessionalMemberships(int page, int size, String updatedTime) {
		List<ProfessionalMembership> professionalMemberships = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "membership")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "membership")));
			AggregationResults<ProfessionalMembership> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProfessionalMembershipCollection.class, ProfessionalMembership.class);
			professionalMemberships = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Professional Memberships");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
		}
		return professionalMemberships;
	}

	@Override
	@Transactional
	public Boolean addEditProfessionalMembership(DoctorProfessionalAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		List<ProfessionalMembershipCollection> professionalMembershipCollections = null;
		List<ObjectId> professionalMemberships = null;
		Boolean response = false;
		try {
			professionalMembershipCollections = professionalMembershipRepository.findAll();
			professionalMemberships = new ArrayList<ObjectId>();
			if (request.getMembership() != null)
				for (String professionalMembership : request.getMembership()) {
					Boolean professionalMembershipFound = false;
					for (ProfessionalMembershipCollection professionalMembershipCollection : professionalMembershipCollections) {
						if (professionalMembership.trim()
								.equalsIgnoreCase(professionalMembershipCollection.getMembership())) {
							professionalMemberships.add(professionalMembershipCollection.getId());
							professionalMembershipFound = true;
							break;
						}
					}
					if (!professionalMembershipFound) {
						ProfessionalMembershipCollection professionalMembershipCollection = new ProfessionalMembershipCollection();
						professionalMembershipCollection.setMembership(professionalMembership);
						professionalMembershipCollection.setCreatedTime(new Date());
						professionalMembershipCollection = professionalMembershipRepository
								.save(professionalMembershipCollection);
						professionalMemberships.add(professionalMembershipCollection.getId());
					}
				}
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setProfessionalMemberships(professionalMemberships);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditAppointmentNumbers(DoctorAppointmentNumbersAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorAppointmentNumbersAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorAppointmentNumbersAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean addEditVisitingTime(DoctorVisitingTimeAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorVisitingTimeAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setWorkingSchedules(request.getWorkingSchedules());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorVisitingTimeAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean addEditConsultationFee(DoctorConsultationFeeAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorConsultationFeeAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorConsultationFeeAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean addEditAppointmentSlot(DoctorAppointmentSlotAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		DoctorAppointmentSlotAddEditRequest response = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorAppointmentSlotAddEditRequest();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean addEditGeneralInfo(DoctorGeneralInfo request) {
		DoctorGeneralInfo response = null;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			}
			doctorClinicProfileCollection.setAppointmentBookingNumber(request.getAppointmentBookingNumber());
			doctorClinicProfileCollection.setConsultationFee(request.getConsultationFee());
			doctorClinicProfileCollection.setAppointmentSlot(request.getAppointmentSlot());
			doctorClinicProfileCollection.setFacility(request.getFacility());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = new DoctorGeneralInfo();
			BeanUtil.map(doctorClinicProfileCollection, response);
			response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding or editing general info : " + e.getMessage());
		}
		return true;
	}

	@Override
	@Transactional
	public List<Speciality> getSpecialities(int page, int size, String updatedTime) {
		List<Speciality> specialities = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Speciality> aggregationResults = mongoTemplate.aggregate(aggregation,
					SpecialityCollection.class, Speciality.class);
			specialities = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Specialities");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Specialities");
		}
		return specialities;
	}

	@Override
	@Transactional
	public List<EducationInstitute> getEducationInstitutes(int page, int size, String updatedTime, String searchTerm,
			Boolean discarded) {
		List<EducationInstitute> educationInstitutes = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (searchTerm != null)
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));

			// criteria.and("discarded").is(discarded);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						// Aggregation.match(new Criteria("updatedTime").gte(new
						// Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						// Aggregation.match(new Criteria("updatedTime").gte(new
						// Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")));
			educationInstitutes = mongoTemplate
					.aggregate(aggregation, EducationInstituteCollection.class, EducationInstitute.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Education Institutes");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Education Institutes");
		}
		return educationInstitutes;
	}

	@Override
	@Transactional
	public EducationInstitute addEditEducationInstitutes(EducationInstitute request) {
		EducationInstitute educationInstitute = null;
		try {
			EducationInstituteCollection educationInstituteCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				educationInstituteCollection = educationInstituteRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (educationInstituteCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " Education Institutes not present with Id");
				}
				BeanUtil.map(request, educationInstituteCollection);
				educationInstituteCollection.setUpdatedTime(new Date());
				educationInstituteCollection.setCreatedBy("Admin");
			} else {
				educationInstituteCollection = new EducationInstituteCollection();
				BeanUtil.map(request, educationInstituteCollection);
				educationInstituteCollection.setUpdatedTime(new Date());
				educationInstituteCollection.setCreatedTime(new Date());
				educationInstituteCollection.setCreatedBy("Admin");
			}
			educationInstituteCollection = educationInstituteRepository.save(educationInstituteCollection);
			educationInstitute = new EducationInstitute();
			BeanUtil.map(educationInstituteCollection, educationInstitute);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Add Edit Education Institutes");
			throw new BusinessException(ServiceError.Unknown, "Error Add Edit Education Institutes");
		}
		return educationInstitute;
	}

	@Override
	@Transactional
	public EducationQualification addEditEducationQualification(EducationQualification request) {
		EducationQualification educationQualification = null;
		try {
			EducationQualificationCollection educationQualificationCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				educationQualificationCollection = educationQualificationRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				if (educationQualificationCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Qulification not present with Id");
				}
				BeanUtil.map(request, educationQualificationCollection);
				educationQualificationCollection.setUpdatedTime(new Date());
				educationQualificationCollection.setCreatedBy("Admin");
			} else {
				educationQualificationCollection = new EducationQualificationCollection();
				BeanUtil.map(request, educationQualificationCollection);
				educationQualificationCollection.setUpdatedTime(new Date());
				educationQualificationCollection.setCreatedTime(new Date());
				educationQualificationCollection.setCreatedBy("Admin");
			}
			educationQualificationCollection = educationQualificationRepository.save(educationQualificationCollection);
			educationQualification = new EducationQualification();
			BeanUtil.map(educationQualificationCollection, educationQualification);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Add Edit Education Qulification");
			throw new BusinessException(ServiceError.Unknown, "Error Add Edit Education Qulification");
		}
		return educationQualification;
	}

	@Override
	@Transactional
	public List<EducationQualification> getEducationQualifications(int page, int size, String updatedTime) {
		List<EducationQualification> qualifications = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "name")));
			AggregationResults<EducationQualification> aggregationResults = mongoTemplate.aggregate(aggregation,
					EducationQualificationCollection.class, EducationQualification.class);
			qualifications = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Professional Memberships");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Professional Memberships");
		}
		return qualifications;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorMultipleDataAddEditResponse addEditMultipleData(DoctorMultipleDataAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		List<String> specialitiesresponse = new ArrayList<>();
		DoctorMultipleDataAddEditResponse response = null;
		try {
			userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (userCollection != null && doctorCollection != null) {

				if (!DPDoctorUtils.anyStringEmpty(request.getTitle())) {
					userCollection.setTitle(request.getTitle());
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getFirstName())) {
					userCollection.setFirstName(request.getFirstName());
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getGender())) {
					doctorCollection.setGender(request.getGender());
				}
				doctorCollection.setDob(request.getDob());

				response = new DoctorMultipleDataAddEditResponse();
				response.setDoctorId(request.getDoctorId());

				if (request.getExperience() > 0) {
					DoctorExperience doctorExperience = new DoctorExperience();
					doctorExperience.setExperience(request.getExperience());
					doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
					doctorCollection.setExperience(doctorExperience);
				} else {
					doctorCollection.setExperience(null);
				}

				if (request.getSpeciality() != null && !request.getSpeciality().isEmpty()) {
					List<ObjectId> oldSpecialities = doctorCollection.getSpecialities();

					List<SpecialityCollection> specialityCollections = specialityRepository
							.findBySuperSpecialityIn(request.getSpeciality());
					Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("id"));
					Collection<String> specialities = CollectionUtils.collect(specialityCollections,
							new BeanToPropertyValueTransformer("superSpeciality"));
					if (specialityIds != null && !specialityIds.isEmpty()) {
						doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
						specialitiesresponse.addAll(specialities);
						if (oldSpecialities != null && !oldSpecialities.isEmpty()) {
							removeOldSpecialityPermissions(specialityIds, oldSpecialities, request.getDoctorId());
							List<ServicesCollection> servicesCollections = servicesRepository
									.findBySpecialityIdsIn(oldSpecialities);
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
									new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);
							if (doctorCollection.getServices() != null)
								doctorCollection.getServices().removeAll(services);
						}
						if (doctorCollection.getSpecialities() != null
								&& !doctorCollection.getSpecialities().isEmpty()) {
							List<ServicesCollection> servicesCollections = servicesRepository
									.findBySpecialityIdsIn(doctorCollection.getSpecialities());
							List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
									new BeanToPropertyValueTransformer("id"));
							Set<ObjectId> services = new HashSet<>(servicesIds);

							if (doctorCollection.getServices() != null)
								doctorCollection.getServices().addAll(services);
							else
								doctorCollection.setServices(services);
						}
					} else {
						doctorCollection.setSpecialities(null);
						assignDefaultUIPermissions(request.getDoctorId());
						specialitiesresponse = null;
					}
				} else {
					doctorCollection.setSpecialities(null);
					assignDefaultUIPermissions(request.getDoctorId());
					specialitiesresponse = null;
				}

				if (request.getProfileImage() != null) {
					String path = "profile-image";
					// save image
					request.getProfileImage()
							.setFileName(request.getProfileImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager
							.saveImageAndReturnImageUrl(request.getProfileImage(), path, true);
					userCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setThumbnailUrl(imageURLResponse.getImageUrl());
					response.setProfileImageUrl(imageURLResponse.getImageUrl());
					response.setThumbnailProfileImageUrl(imageURLResponse.getImageUrl());
				}

				if (request.getCoverImage() != null) {
					String path = "cover-image";
					// save image
					request.getCoverImage().setFileName(request.getCoverImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getCoverImage(),
							path, true);
					userCollection.setCoverImageUrl(imageURLResponse.getImageUrl());
					userCollection.setCoverThumbnailImageUrl(imageURLResponse.getThumbnailUrl());
					response.setCoverImageUrl(imageURLResponse.getImageUrl());
					response.setThumbnailCoverImageUrl(imageURLResponse.getThumbnailUrl());
				}
				userCollection.setFirstName(request.getFirstName());
				userCollection = userRepository.save(userCollection);
				doctorCollection = doctorRepository.save(doctorCollection);

				BeanUtil.map(userCollection, response);
				BeanUtil.map(doctorCollection, response);
				response.setSpecialities(specialitiesresponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditFacility(DoctorAddEditFacilityRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean response = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection == null) {
				doctorClinicProfileCollection = new DoctorClinicProfileCollection();
				doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
				doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
				doctorClinicProfileCollection.setCreatedTime(new Date());
			} else {
				doctorClinicProfileCollection.setUpdatedTime(new Date());
			}
			doctorClinicProfileCollection.setFacility(request.getFacility());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;

	}

	@Override
	@Transactional
	public Boolean addEditGender(DoctorGenderAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setGender(request.getGender());
			doctorRepository.save(doctorCollection);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Gender");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Gender");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDOB(DoctorDOBAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setDob(request.getDob());
			doctorRepository.save(doctorCollection);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditVerification(String doctorId, Boolean isVerified) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			doctorCollection.setIsVerified(isVerified);
			doctorRepository.save(doctorCollection);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean uploadVerificationDocuments(String doctorId, FormDataBodyPart file) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		ImageURLResponse imageURLResponse = null;
		Date createdTime = new Date();
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "doctor not found");
			}
			if (file != null) {
				String path = "doctor_verification" + File.separator + doctorId;
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
				System.out.println(imageURLResponse);
				if (imageURLResponse != null) {
					int counter = 0;
					if (doctorCollection.getVerificationDocuments() != null) {
						counter = doctorCollection.getVerificationDocuments().size();
						ClinicImage image = new ClinicImage();
						image.setCounter(counter++);
						image.setImageUrl(imageURLResponse.getImageUrl());
						image.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						doctorCollection.getVerificationDocuments().add(image);
					} else {
						List<ClinicImage> clinicImages = new ArrayList<>();
						ClinicImage image = new ClinicImage();
						image.setCounter(counter++);
						image.setImageUrl(imageURLResponse.getImageUrl());
						image.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						clinicImages.add(image);
						doctorCollection.setVerificationDocuments(clinicImages);
					}

				}
				doctorRepository.save(doctorCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public AddEditSEORequest addEditSEO(AddEditSEORequest request) {
		DoctorCollection doctorCollection = null;
		AddEditSEORequest response = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setMetaTitle(request.getMetaTitle());
			doctorCollection.setMetaKeyword(request.getMetaKeyword());
			doctorCollection.setMetaDesccription(request.getMetaDesccription());
			doctorCollection.setSlugUrl(request.getSlugUrl());
			doctorRepository.save(doctorCollection);
			response = new AddEditSEORequest();
			BeanUtil.map(doctorCollection, response);
			response.setDoctorId(doctorCollection.getUserId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor SEO");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor SEO");
		}
		return response;
	}

	@Override
	public Boolean deleteDoctorProfilePicture(String doctorId) {
		Boolean response = false;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			if (userCollection == null) {
				logger.warn("User not found");
				throw new BusinessException(ServiceError.NotFound, "doctor not found");
			}
			userCollection.setImageUrl(null);
			userCollection.setThumbnailUrl(null);

			userCollection = userRepository.save(userCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updatePackageType(String doctorId, String locationId, String packageType) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean status = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			doctorClinicProfileCollection.setPackageType(packageType);
			doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return status;
	}

	@Override
	@Transactional
	public Boolean updateVaccinationModule(String doctorId, String locationId, Boolean vaccinationModule) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean status = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			doctorClinicProfileCollection.setIsVaccinationModuleOn(vaccinationModule);
			doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return status;
	}

	@Override
	@Transactional
	public Boolean updateKiosk(String doctorId, String locationId) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean status = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			doctorClinicProfileCollection.setIskiosk(!doctorClinicProfileCollection.getIskiosk());
			doctorClinicProfileCollection = doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DoctorServicesAddEditRequest addEditServices(DoctorServicesAddEditRequest request) {
		DoctorServicesAddEditRequest response = null;
		DoctorCollection doctorCollection = null;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (doctorCollection != null) {
				response = new DoctorServicesAddEditRequest();
				if (request.getServices() != null && !request.getServices().isEmpty()) {
					doctorCollection.getServices().clear();
					doctorRepository.save(doctorCollection);

					List<ServicesCollection> servicesCollections = servicesRepository
							.findByServiceIn(request.getServices());

					List<ObjectId> servicesIds = (List<ObjectId>) CollectionUtils.collect(servicesCollections,
							new BeanToPropertyValueTransformer("id"));
					Set<ObjectId> services = new HashSet<>(servicesIds);

					if (services != null && !services.isEmpty()) {
						doctorCollection.setServices(services);
					} else {
						doctorCollection.setServices(null);
					}
				} else {
					doctorCollection.setServices(null);
				}
				doctorRepository.save(doctorCollection);
//				System.out.println("doctorCollection.getServices" + doctorCollection.getServices().size());
				BeanUtil.map(doctorCollection, response);
				response.setDoctorId(doctorCollection.getUserId().toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditCity(DoctorAddEditCityRequest request) {
		DoctorClinicProfileCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (request.getCityId() != null) {
				doctorCollection.setCityId(new ObjectId(request.getCityId()));
				response = true;
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "City Id is null");
			}
			doctorClinicProfileRepository.save(doctorCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public List<Services> getServices(int page, int size, String updatedTime, String searchTerm) {
		List<Services> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria.and("service").regex(searchTerm, "i");

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Services> aggregationResults = mongoTemplate.aggregate(aggregation,
					ServicesCollection.class, Services.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Services");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Services");
		}
		return response;
	}

	@Override
	public Services addEditServices(Services request) {
		Services response = null;
		try {

			ServicesCollection servicesCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				servicesCollection = servicesRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (servicesCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " Services not present with Id");
				}
				BeanUtil.map(request, servicesCollection);
				servicesCollection.setUpdatedTime(new Date());
				servicesCollection.setCreatedBy("Admin");
			} else {
				servicesCollection = new ServicesCollection();
				BeanUtil.map(request, servicesCollection);
				servicesCollection.setUpdatedTime(new Date());
				servicesCollection.setCreatedTime(new Date());
				servicesCollection.setCreatedBy("Admin");
			}
			servicesCollection = servicesRepository.save(servicesCollection);
			response = new Services();
			BeanUtil.map(servicesCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Services");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Services");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditMRCode(AddEditMRCodeRequest request) {
		DoctorClinicProfileCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (request.getMrCode() != null) {
				doctorCollection.setCityId(new ObjectId(request.getMrCode()));
				response = true;
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Mr Code is null");
			}
			doctorClinicProfileRepository.save(doctorCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditFeedbackURL(DoctorMultipleDataAddEditRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean response = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setUpdatedTime(new Date());
				doctorClinicProfileCollection.setFeedbackURL(request.getFeedbackURL());
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Feedback URL of Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Feedback URL of Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditNutritionistRequest(AddEditNutritionistRequest request) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		Boolean response = false;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
					new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setUpdatedTime(new Date());
				doctorClinicProfileCollection.setIsAdminNutritionist(request.getIsAdminNutritionist());
				doctorClinicProfileCollection.setIsNutritionist(request.getIsNutritionist());
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Nutrition Details of Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown,
					"Error Editing  Nutrition Details of Doctor Clinic Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditOnlineWorkingTime(DoctorOnlineWorkingTimeRequest request) {
		List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
		Boolean response = false;
		try {
			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());
			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setOnlineWorkingSchedules(request.getOnlineWorkingSchedules());
					doctorDocument.setIsOnlineConsultationAvailable(request.getIsOnlineConsultationAvailable());
					esdoctorRepository.save(doctorDocument);
				}

			if (doctorClinicProfileCollections != null) {
				for (DoctorClinicProfileCollection clinicProfileCollection : doctorClinicProfileCollections) {
					clinicProfileCollection.setOnlineWorkingSchedules(request.getOnlineWorkingSchedules());
					clinicProfileCollection
							.setIsOnlineConsultationAvailable(request.getIsOnlineConsultationAvailable());
					// clinicProfileCollection.setConsultationType(request.getConsultationType());
					clinicProfileCollection.setUpdatedTime(new Date());
					doctorClinicProfileRepository.save(clinicProfileCollection);
					response = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;

	}

	@Override
	public DoctorOnlineGeneralInfo addEditDoctorOnlineInfo(DoctorOnlineGeneralInfo request) {
		List<DoctorClinicProfileCollection> doctorClinicProfileCollections = new ArrayList<DoctorClinicProfileCollection>();
		DoctorOnlineGeneralInfo response = null;
		try {
			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());

			if (doctorClinicProfileCollections != null)
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					doctorClinicProfileCollection.setConsultationType(request.getConsultationType());
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				}

			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setConsultationType(request.getConsultationType());
					esdoctorRepository.save(doctorDocument);
				}

			DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
			if (doctorClinicProfileCollections != null)
				doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);

			response = new DoctorOnlineGeneralInfo();
			BeanUtil.map(doctorClinicProfileCollection, response);
			// response.setDoctorId(doctorClinicProfileCollection.getDoctorId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Clinic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Clinic Profile");
		}
		return response;

	}

	@Override
	public DoctorProfile checkMedicalDocument(String doctorId, Boolean isRegistrationDetailsVerified,
			Boolean isPhotoIdVerified) {
		DoctorProfile response = null;

		try {
			DoctorCollection doctorCollection = null;

			doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));

			if (doctorCollection == null) {
				throw new BusinessException(ServiceError.Unknown, "Error while searching doctor");
			}

			if (doctorCollection.getRegistrationImageUrl() != null || doctorCollection.getPhotoIdImageUrl() != null) {
				doctorCollection.setIsRegistrationDetailsVerified(isRegistrationDetailsVerified);
				doctorCollection.setIsPhotoIdVerified(isPhotoIdVerified);
				doctorRepository.save(doctorCollection);

				response = new DoctorProfile();

				BeanUtil.map(doctorCollection, response);

			}

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(doctorId);

			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setIsRegistrationDetailsVerified(isRegistrationDetailsVerified);
					doctorDocument.setIsPhotoIdVerified(isPhotoIdVerified);
					esdoctorRepository.save(doctorDocument);
				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying medical documents");
		}

		return response;

	}

	@Override
	@Transactional
	public String uploadRegistrationDetails(DoctorRegistrationDetails request) {
		DoctorCollection doctorCollection = null;

		String response = "";
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));

			if (request.getImage() != null) {
				String path = "registration-Docs";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);

				if (doctorCollection != null)
					if (request.getType() != null && request.getType().getType()
							.equalsIgnoreCase(RegistrationType.REGISTRATION_DETAILS.getType())) {

						doctorCollection.setRegistrationImageUrl(imageURLResponse.getImageUrl());
						doctorCollection.setRegistrationThumbnailUrl(imageURLResponse.getThumbnailUrl());
						response = doctorCollection.getRegistrationImageUrl();
					}

				if (request.getType() != null
						&& request.getType().getType().equalsIgnoreCase(RegistrationType.CLINIC_OWNERSHIP.getType())) {
					DoctorClinicProfileCollection doctorclinic = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(new ObjectId(request.getDoctorId()),
									new ObjectId(request.getLocationId()));
					doctorclinic.setClinicOwnershipImageUrl(imageURLResponse.getImageUrl());
					doctorClinicProfileRepository.save(doctorclinic);
					response = doctorclinic.getClinicOwnershipImageUrl();

				}
				if (doctorCollection != null)
					if (request.getType() != null
							&& request.getType().getType().equalsIgnoreCase(RegistrationType.PHOTOID_PROOF.getType())) {
						doctorCollection.setPhotoIdImageUrl(imageURLResponse.getImageUrl());
						response = doctorCollection.getPhotoIdImageUrl();

					}

				doctorCollection = doctorRepository.save(doctorCollection);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while uploading documents");
			throw new BusinessException(ServiceError.Unknown, "Error while uploading documents");
		}
		return response;
	}

	@Override
	public Boolean verifyClinicDocument(String doctorId, Boolean isClinicOwnershipVerified, String locationId) {
		Boolean response = false;
		try {

			DoctorClinicProfileCollection doctorclinic = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			if (doctorclinic == null) {
				throw new BusinessException(ServiceError.Unknown, "Error while searching doctor");
			}

			if (doctorclinic.getClinicOwnershipImageUrl() != null) {
				doctorclinic.setIsClinicOwnershipVerified(isClinicOwnershipVerified);

				response = true;

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying medical documents");
		}

		return response;

	}

	@Override
	public Boolean editDoctorSlugUrl(DoctorSlugUrlRequest request) {
		Boolean response = false;
		try {
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = new ArrayList<DoctorClinicProfileCollection>();

			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());

			if (doctorClinicProfileCollections != null)
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					doctorClinicProfileCollection.setDoctorSlugURL(request.getDoctorSlugUrl());
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				}

			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setDoctorSlugURL(request.getDoctorSlugUrl());
					esdoctorRepository.save(doctorDocument);
				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying medical documents");
		}
		return response;
	}

	@Override
	public MedicalCouncil deleteMedicalCouncil(String MedicalCouncilId, Boolean discarded) {
		MedicalCouncil response = null;
		try {
			MedicalCouncilCollection medical = new MedicalCouncilCollection();
			medical = medicalCouncilRepository.findById(new ObjectId(MedicalCouncilId)).orElse(null);
			if (medical != null) {
				medical.setDiscarded(discarded);
				medicalCouncilRepository.save(medical);
			}
			response = new MedicalCouncil();
			BeanUtil.map(medical, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while deleting medical councils");
			throw new BusinessException(ServiceError.Unknown, "Error while deleting medical councils");
		}
		return response;
	}

	@Override
	public EducationInstitute deleteEducationInstitute(String educationInstituteId, Boolean discarded) {
		EducationInstitute response = null;
		try {
			EducationInstituteCollection education = new EducationInstituteCollection();
			education = educationInstituteRepository.findById(new ObjectId(educationInstituteId)).orElse(null);

			if (education != null) {
				education.setDiscarded(discarded);
				educationInstituteRepository.save(education);

			}
			response = new EducationInstitute();

			BeanUtil.map(education, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while deleting education institutes");
			throw new BusinessException(ServiceError.Unknown, "Error while deleting education institutes");
		}
		return response;
	}

	@Override
	public Boolean checkBirthdaySms(String doctorId, String locationId, Boolean isSendBirthdaySMS) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollections = null;

			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			System.out.println(doctorClinicProfileCollections.toString());

			if (doctorClinicProfileCollections != null) {
//				for(DoctorClinicProfileCollection doctorClinicProfileCollection:doctorClinicProfileCollections) {
				doctorClinicProfileCollections.setIsSendBirthdaySMS(isSendBirthdaySMS);
				doctorClinicProfileRepository.save(doctorClinicProfileCollections);
				response = true;
//				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while changing status of birthday sms");
			throw new BusinessException(ServiceError.Unknown, "Error while changing status of birthday sms");
		}
		return response;
	}

	@Override
	public Boolean updateBirthdaySms(Boolean isSendBirthdaySMS, String doctorId, String locationId) {
		Boolean response = false;
		try {
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;

			if (doctorId != null && locationId != null) {

				DoctorClinicProfileCollection doctorClinicProfileCollection = null;

				doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

				if (doctorClinicProfileCollection != null) {

					doctorClinicProfileCollection.setIsSendBirthdaySMS(isSendBirthdaySMS);
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					response = true;

				}
			} else {

				doctorClinicProfileCollections = doctorClinicProfileRepository.findByIsSendBirthdaySMS(true);

				if (doctorClinicProfileCollections != null) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						doctorClinicProfileCollection.setIsSendBirthdaySMS(isSendBirthdaySMS);
						doctorClinicProfileRepository.save(doctorClinicProfileCollection);
						response = true;
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while changing status of birthday sms");
			throw new BusinessException(ServiceError.Unknown, "Error while changing status of birthday sms");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditOnlineConsultationSlot(DoctorOnlineConsultationAddEditRequest request) {
		List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
		Boolean response = false;
		try {
			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));
			if (doctorClinicProfileCollections != null) {
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					doctorClinicProfileCollection.setOnlineConsultationSlot(request.getOnlineConsultationSlot());
					doctorClinicProfileCollection.setLocationId(doctorClinicProfileCollection.getLocationId());
					doctorClinicProfileCollection.setDoctorId(doctorClinicProfileCollection.getDoctorId());
					doctorClinicProfileCollection.setUpdatedTime(new Date());
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					response = true;
				}
			}

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());

			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setOnlineConsultationSlot(request.getOnlineConsultationSlot());
					esdoctorRepository.save(doctorDocument);
				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error add edit Online Consultation Slot");
			throw new BusinessException(ServiceError.Unknown, "Error add edit Online Consultation Slot");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDoctorDetails(DoctorDetails request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			if (doctorCollection != null) {
				doctorCollection.setIsHealthcocoDoctor(request.getIsHealthcocoDoctor());
				doctorCollection.setUpdatedTime(new Date());
				doctorRepository.save(doctorCollection);
				response = true;
			}

			List<ESDoctorDocument> doctorDocuments = esdoctorRepository.findByUserId(request.getDoctorId());

			if (doctorDocuments != null)
				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorDocument.setIsHealthcocoDoctor(request.getIsHealthcocoDoctor());
					esdoctorRepository.save(doctorDocument);
				}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error add edit healthcoco doctor");
			throw new BusinessException(ServiceError.Unknown, "Error add edit healthcoco doctor");
		}
		return response;
	}

	@Override
	public Boolean updateSuperAdmin(Boolean isSuperAdmin, String doctorId, String locationId) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollection = null;

			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			if (doctorClinicProfileCollection != null) {

				doctorClinicProfileCollection.setIsSuperAdmin(isSuperAdmin);
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				response = true;

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error add edit healthcoco doctor");
			throw new BusinessException(ServiceError.Unknown, "Error add edit healthcoco doctor");
		}
		return response;
	}

	@Override
	public Boolean updateDoctorTransactionSms(Boolean isTransactionalSms, String doctorId, String locationId) {
		Boolean response = false;
		try {
			DoctorCollection doctorClinicProfileCollection = null;

			doctorClinicProfileCollection = doctorRepository.findByUserId(new ObjectId(doctorId));

			if (doctorClinicProfileCollection != null) {

				doctorClinicProfileCollection.setIsTransactionalSms(isTransactionalSms);
				doctorClinicProfileCollection.setUpdatedTime(new Date());
				doctorRepository.save(doctorClinicProfileCollection);
				response = true;

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error add edit healthcoco doctor");
			throw new BusinessException(ServiceError.Unknown, "Error add edit healthcoco doctor");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateClinicImage(DoctorClinicImage request) {
		Boolean response = false;
		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(request.getLocationId()))
					.orElse(null);
			List<ClinicImage> clinicImg = new ArrayList<ClinicImage>();
			if (locationCollection == null) {
				logger.warn("Clinic not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				if (locationCollection.getImages() != null)
					clinicImg.addAll(locationCollection.getImages());

				if (request.getClinicImage() != null)
					clinicImg.addAll(request.getClinicImage());

				locationCollection.setImages(clinicImg);
				locationRepository.save(locationCollection);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error add edit healthcoco doctor");
			throw new BusinessException(ServiceError.Unknown, "Error add edit healthcoco doctor");
		}
		return response;

	}

	// @Scheduled(cron = "0 20 12 31 3 ?", zone = "IST")
	@Override
	@Transactional
	public Boolean checkMedicalDocuments() {
		Boolean response = false;

		try {
			List<DoctorCollection> doctorCollections = null;
			System.out.println("Medical Docs");
			doctorCollections = doctorRepository.findByIsPhotoIdVerified(true);

			if (doctorCollections == null) {
				throw new BusinessException(ServiceError.Unknown, "Error while searching doctor");
			}
			for (DoctorCollection doctorCollection : doctorCollections) {
				if (doctorCollection.getRegistrationImageUrl() != null
						|| doctorCollection.getPhotoIdImageUrl() != null) {

					List<ESDoctorDocument> doctorDocuments = esdoctorRepository
							.findByUserId(doctorCollection.getUserId().toString());

					if (doctorDocuments != null)
						for (ESDoctorDocument doctorDocument : doctorDocuments) {
							doctorDocument.setIsRegistrationDetailsVerified(
									doctorCollection.getIsRegistrationDetailsVerified());
							doctorDocument.setIsPhotoIdVerified(doctorCollection.getIsPhotoIdVerified());
							esdoctorRepository.save(doctorDocument);
						}
					response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying medical documents");
		}
		return response;
	}

	@Override
	public Boolean addEditTraining(DoctorTrainingAddEditRequest request) {
		// TODO Auto-generated method stub
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(new ObjectId(request.getDoctorId()));
			doctorCollection.setTrainingsCertifications(request.getTrainingsCertifications());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean updateWelcomePatientSMS(Boolean isWelcomePatientSMS, String doctorId, String locationId) {
		Boolean response = false;
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollections = null;

			doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			if (doctorClinicProfileCollections != null) {
				doctorClinicProfileCollections.setIsPatientWelcomeMessageOn(isWelcomePatientSMS);
				doctorClinicProfileRepository.save(doctorClinicProfileCollections);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while changing status of birthday sms");
			throw new BusinessException(ServiceError.Unknown, "Error while changing status of birthday sms");
		}
		return response;
	}

}
