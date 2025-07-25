package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientNumberAndUserIds;
import com.dpdocter.beans.PatientRecord;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SearchRequestDetailsResponse;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.User;
import com.dpdocter.collections.BlockUserCollection;
import com.dpdocter.collections.CampNameCollection;
import com.dpdocter.collections.CertificateTemplateCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ContactUsCollection;
import com.dpdocter.collections.DentalReasonsCollection;
import com.dpdocter.collections.DentalTreatmentDetailCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.EmailListCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESEducationInstituteRepository;
import com.dpdocter.elasticsearch.repository.ESEducationQualificationRepository;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.repository.ESProfessionalMembershipRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.AppType;
import com.dpdocter.enums.ListType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlockUserRepository;
import com.dpdocter.repository.CampNameRepository;
import com.dpdocter.repository.CertificateTemplateRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ContactUsRepository;
import com.dpdocter.repository.DentalReasonRepository;
import com.dpdocter.repository.DentalTreatmentDetailRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.EmailListRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.ResumeRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DentalTreatmentDetailRequest;
import com.dpdocter.request.EmailList;
import com.dpdocter.request.LeadsTypeReasonsRequest;
import com.dpdocter.response.DentalAdminNameResponse;
import com.dpdocter.response.DentalTreatmentDetailResponse;
import com.dpdocter.response.DentalTreatmentNameResponse;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.DoctorResponseNew;
import com.dpdocter.response.DoctorSearchResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.LeadsTypeReasonsResponse;
import com.dpdocter.response.PatientAppUsersResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class AdminServicesImpl implements AdminServices {

	private static Logger logger = LogManager.getLogger(AdminServicesImpl.class.getName());

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DentalTreatmentDetailRepository dentalTreatmentDetailRepository;

	@Autowired
	private DentalReasonRepository dentalReasonRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ResumeRepository resumeRepository;

	@Autowired
	private ContactUsRepository contactUsRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private EducationInstituteRepository educationInstituteRepository;

	@Autowired
	private EducationQualificationRepository educationQualificationRepository;

	@Autowired
	private ESEducationInstituteRepository esEducationInstituteRepository;

	@Autowired
	private ESEducationQualificationRepository esEducationQualificationRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private ESDiagnosticTestRepository esDiagnosticTestRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;

	@Autowired
	private ESProfessionalMembershipRepository esProfessionalMembershipRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Autowired
	private BlockUserRepository blockUserRepository;

	@Autowired
	private TransactionalManagementService transactionalService;

	@Autowired
	private CertificateTemplateRepository certificateTemplateRepository;

	@Autowired
	private EmailListRepository emailListRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${app.link.message}")
	private String appLinkMessage;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Value(value = "${doctor.app.bit.link}")
	private String doctorAppBitLink;

	@Value(value = "${ipad.app.bit.link}")
	private String ipadAppBitLink;

	@Value(value = "${mail.get.app.link.subject}")
	private String getAppLinkSubject;

	@Autowired
	private CampNameRepository campNameRepository;

	@Override
	@Transactional
	public List<User> getInactiveUsers(int page, int size) {
		List<User> response = null;
		try {

			List<UserCollection> userCollections = null;
			if (size > 0)
				userCollections = userRepository.findByIsActive(true,
						PageRequest.of(page, size, Direction.DESC, "createdTime"));
			else
				userCollections = userRepository.findByIsActive(true, new Sort(Direction.DESC, "createdTime"));
			if (userCollections != null) {
				response = new ArrayList<User>();
				BeanUtil.map(userCollections, response);
			}
		} catch (Exception e) {
			logger.error("Error while getting inactive users " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting inactive users " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer countInactiveUsers() {
		Integer response = 0;
		try {

			response = userRepository.countInactiveDoctors(true);

		} catch (Exception e) {
			logger.error("Error while count inactive users " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting count inactive users " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Hospital> getHospitals(int page, int size) {
		List<Hospital> response = null;
		try {
			List<HospitalCollection> hospitalCollections = null;
			if (size > 0)
				hospitalCollections = hospitalRepository
						.findAll(PageRequest.of(page, size, Direction.DESC, "createdTime")).getContent();
			else
				hospitalCollections = hospitalRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			if (hospitalCollections != null) {
				response = new ArrayList<Hospital>();
				BeanUtil.map(hospitalCollections, response);
			}
		} catch (Exception e) {
			logger.error("Error while getting hospitals " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting inactive hospitals " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Location> getClinics(int page, int size, String hospitalId, Boolean isClinic, Boolean isLab,
			Boolean isParent, Boolean isDentalWorkLab, Boolean isDentalImagingLab, Boolean isDentalChain,
			String searchTerm, Boolean islisted, Boolean isActivate, String city, String toDate, String fromDate) {
		List<Location> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {

				criteria = criteria.andOperator(new Criteria("hospitalId").is(new ObjectId(hospitalId)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("locationName").regex("^" + searchTerm));
			}

			if (isActivate != null) {
				criteria.and("isActivate").is(isActivate);
			}
			if (city != null) {
				criteria.and("city").is(city);
			}

			if (islisted != null) {
				criteria.and("isLocationListed").is(islisted);
			}

			if (isDentalChain != null) {
				criteria.and("isDentalChain").is(isDentalChain);
			}

			if (isClinic != null) {
				criteria.and("isClinic").is(isClinic);
			}

			if (isLab != null) {
				criteria.and("isLab").is(isLab);
			}
			if (isParent != null) {
				criteria.and("isParent").is(isParent);
			}
			if (isDentalWorkLab != null) {
				criteria.and("isDentalWorksLab").is(isDentalWorkLab);
			}
			if (isDentalImagingLab != null) {
				criteria.and("isDentalImagingLab").is(isDentalImagingLab);
			}

			Date from = null;
			Date to = null;

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				criteria.and("createdTime").gte(from).lte(to);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<Location> results = mongoTemplate.aggregate(aggregation, LocationCollection.class,
					Location.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer countClinics(String hospitalId, Boolean isClinic, Boolean isLab, Boolean isParent,
			Boolean isDentalWorkLab, Boolean isDentalImagingLab, Boolean isDentalChain, String searchTerm,
			Boolean islisted, Boolean isActivate, String city, String toDate, String fromDate) {
		Integer response = 0;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {

				criteria = criteria.andOperator(new Criteria("hospitalId").is(new ObjectId(hospitalId)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("locationName").regex("^" + searchTerm, "i"),
						new Criteria("locationName").regex("^" + searchTerm));
			}
			if (islisted != null) {
				criteria.and("isLocationListed").is(islisted);
			}
			if (isClinic != null) {
				criteria.and("isClinic").is(isClinic);
			}
			if (isClinic != null) {
				criteria.and("isDentalChain").is(isDentalChain);
			}

			if (isLab != null) {
				criteria.and("isLab").is(isLab);
			}
			if (isParent != null) {
				criteria.and("isParent").is(isParent);
			}
			if (isDentalWorkLab != null) {
				criteria.and("isDentalWorksLab").is(isDentalWorkLab);
			}
			if (isDentalImagingLab != null) {
				criteria.and("isDentalImagingLab").is(isDentalImagingLab);
			}

			if (isActivate != null) {
				criteria.and("isActivate").is(isActivate);
			}
			if (city != null) {
				criteria.and("city").is(city);
			}

			Date from = null;
			Date to = null;

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				criteria.and("createdTime").gte(from).lte(to);
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			response = mongoTemplate.aggregate(aggregation, LocationCollection.class, LocationCollection.class)
					.getMappedResults().size();

		} catch (Exception e) {
			logger.error("Error while count doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Resume addResumes(Resume request) {
		Resume response = null;
		try {
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFile() != null) {
				request.getFile().setFileName(request.getFile().getFileName() + (new Date()).getTime());
				String path = "resumes" + File.separator + request.getType();
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFile(), path,
						false);
				resumeCollection.setPath(imageURLResponse.getImageUrl());
			}
			resumeCollection = resumeRepository.save(resumeCollection);
			if (resumeCollection != null) {
				response = new Resume();
				BeanUtil.map(resumeCollection, response);
			}
			String body = mailBodyGenerator.generateEmailBody(resumeCollection.getName(), resumeCollection.getType(),
					"applyForPostTemplate.vm");
			mailService.sendEmail(resumeCollection.getEmailAddress(), "Your application has been received", body, null);
		} catch (Exception e) {
			logger.error("Error while adding resume " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding resume " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Resume> getResumes(int page, int size, String type, String searchTerm) {
		List<Resume> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<Resume> results = mongoTemplate.aggregate(aggregation, ResumeCollection.class,
					Resume.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting Resume " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Resume " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public void importCity() {
		String csvFile = "/home/ubuntu/cities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				CityCollection cityCollection = new CityCollection();
				cityCollection.setCity(obj[0]);
				cityCollection.setState(obj[1]);
				cityCollection.setCountry("India");
				List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
						cityCollection.getCity() + " " + cityCollection.getState() + " " + cityCollection.getCountry());

				if (geocodedLocations != null && !geocodedLocations.isEmpty())
					BeanUtil.map(geocodedLocations.get(0), cityCollection);

				cityCollection = cityRepository.save(cityCollection);
				ESCityDocument esCityDocument = new ESCityDocument();
				BeanUtil.map(cityCollection, esCityDocument);
				esCityDocument.setGeoPoint(new GeoPoint(cityCollection.getLatitude(), cityCollection.getLongitude()));
				esCityService.addCities(esCityDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importDrug() {
	}

	@Override
	@Transactional
	public void importDiagnosticTest() {
		String csvFile = "/home/ubuntu/DiagnosticTests.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				DiagnosticTestCollection diagnosticTestCollection = new DiagnosticTestCollection();
				diagnosticTestCollection.setTestName(obj[0]);
				diagnosticTestRepository.save(diagnosticTestCollection);
				ESDiagnosticTestDocument document = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, document);
				esDiagnosticTestRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importEducationInstitute() {
		String csvFile = "/home/ubuntu/EducationInstitute.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				EducationInstituteCollection educationInstituteCollection = new EducationInstituteCollection();
				educationInstituteCollection.setName(obj[0]);
				educationInstituteCollection.setCreatedBy("ADMIN");
				educationInstituteCollection.setCreatedTime(new Date());
				educationInstituteCollection.setUpdatedTime(new Date());

				educationInstituteRepository.save(educationInstituteCollection);
				ESEducationInstituteDocument document = new ESEducationInstituteDocument();
				BeanUtil.map(educationInstituteCollection, document);
				esEducationInstituteRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importEducationQualification() {
		String csvFile = "/home/ubuntu/EducationQualification.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				EducationQualificationCollection educationQualificationCollection = new EducationQualificationCollection();
				educationQualificationCollection.setName(obj[0]);
				educationQualificationCollection.setCreatedBy("ADMIN");
				educationQualificationCollection.setCreatedTime(new Date());
				educationQualificationCollection.setUpdatedTime(new Date());

				educationQualificationRepository.save(educationQualificationCollection);
				ESEducationQualificationDocument document = new ESEducationQualificationDocument();
				BeanUtil.map(educationQualificationCollection, document);
				esEducationQualificationRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> getDoctors(int page, int size, String locationId, String state, String searchTerm,
			Boolean islisted, Boolean isNutritionist, Boolean isAdminNutritionist, Boolean isActive,
			Boolean isRegistrationDetailsVerified, Boolean isPhotoIdVerified, Boolean isOnlineConsultationAvailable,
			String city, String specialitiesIds, String toDate, String fromDate, Boolean isHealthcocoDoctor) {
		Response<Object> response = new Response<Object>();
		List<DoctorResponse> doctorResponses = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria = criteria.and("user.userState").is(state);
			}

			if (islisted != null) {
				criteria.and("isDoctorListed").is(islisted);
			}

			if (isActive != null) {
				criteria.and("user.isActive").is(isActive);
			}
			if (isRegistrationDetailsVerified != null) {
				criteria.and("doctorEducation.isRegistrationDetailsVerified").is(isRegistrationDetailsVerified);
			}
			if (isPhotoIdVerified != null) {
				criteria.and("doctorEducation.isPhotoIdVerified").is(isPhotoIdVerified);
			}
			if (isOnlineConsultationAvailable != null) {
				criteria.and("clinic.isOnlineConsultationAvailable").is(isOnlineConsultationAvailable);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(locationId);
			}

			if (isHealthcocoDoctor != null) {
				criteria.and("doctorEducation.isHealthcocoDoctor").is(isHealthcocoDoctor);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
//				String search = searchTerm.replaceAll(" ", ".*");
				// criteria = criteria.and("user.firstName").regex(searchTerm, "i");

				criteria = criteria.orOperator(new Criteria("user.firstName").regex(searchTerm, "i"),
						// new Criteria("user.firstName").regex("^" + searchTerm),
						new Criteria("user.emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("user.emailAddress").regex("^" + searchTerm),
						new Criteria("user.mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("user.mobileNumber").regex("^" + searchTerm));

			}

			if (isNutritionist != null) {
				criteria.and("isNutritionist").is(isNutritionist);
			}

			if (isAdminNutritionist != null) {
				criteria.and("user.isAdminNutritionist").is(isAdminNutritionist);
			}

			if (specialitiesIds != null) {
				System.out.println(specialitiesIds);
				criteria.and("doctorEducation.specialities").is(new ObjectId(specialitiesIds));
			}

			Date from = null;
			Date to = null;

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				criteria.and("user.createdTime").gte(from).lte(to);
			}

			if (!DPDoctorUtils.anyStringEmpty(city)) {

				criteria.and("location.city").is(city);
			}

			Aggregation aggregationCount = null;

			aggregationCount = Aggregation.newAggregation(

					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location", true),
					Aggregation.lookup("docter_cl", "doctorId", "userId", "doctorEducation"),
					Aggregation.unwind("doctorEducation", true),
					Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "clinic"),
					Aggregation.unwind("clinic", true), Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$project",
							new BasicDBObject("firstName", "$user.firstName").append("isActive", "$user.isActive")
									.append("isRegistrationDetailsVerified",
											"$doctorEducation.isRegistrationDetailsVerified")
									.append("isPhotoIdVerified", "$doctorEducation.isPhotoIdVerified")
									.append("isOnlineConsultationAvailable", "$isOnlineConsultationAvailable")
									.append("userId", "$doctorId").append("emailAddress", "$user.emailAddress")
									.append("userState", "$user.userState").append("userUId", "$user.userUId")
									.append("createdTime", "$user.createdTime")
									.append("mobileNumber", "$user.mobileNumber").append("city", "$location.city")
									.append("isHealthcocoDoctor", "$doctorEducation.isHealthcocoDoctor")
									.append("registrationNumber", "$doctorEducation.registrationDetails.registrationId")
									.append("qualification", "$doctorEducation.education.qualification")
									.append("specialitiesIds", "$doctorEducation.specialities")
									.append("title", "$user.title").append("isDoctorListed", "$isDoctorListed"))),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$userId")
									.append("firstName", new BasicDBObject("$first", "$firstName"))
									.append("isActive", new BasicDBObject("$first", "$isActive"))
									.append("userId", new BasicDBObject("$first", "$userId"))
									.append("isRegistrationDetailsVerified",
											new BasicDBObject("$first", "$isRegistrationDetailsVerified"))
									.append("isPhotoIdVerified", new BasicDBObject("$first", "$isPhotoIdVerified"))
									.append("isOnlineConsultationAvailable",
											new BasicDBObject("$first", "$isOnlineConsultationAvailable"))
									.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
									.append("userState", new BasicDBObject("$first", "$userState"))
									.append("userUId", new BasicDBObject("$first", "$userUId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
									.append("city", new BasicDBObject("$first", "$city"))
									.append("registrationNumber", new BasicDBObject("$first", "$registrationNumber"))
									.append("qualification", new BasicDBObject("$first", "$qualification"))
									.append("specialitiesIds", new BasicDBObject("$first", "$specialitiesIds"))
									.append("title", new BasicDBObject("$first", "$title"))
									.append("isDoctorListed", new BasicDBObject("$first", "$isDoctorListed"))

					)),

					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<DoctorResponse> resultsCount = mongoTemplate.aggregate(aggregationCount,
					// aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
					DoctorClinicProfileCollection.class, DoctorResponse.class);

			response.setCount(resultsCount.getMappedResults().size());

			if (aggregationCount != null && resultsCount.getMappedResults().size() > 0) {
				if (response.getCount() > 0) {
					if (size > 0) {
						aggregation = Aggregation.newAggregation(

								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location", true),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctorEducation"),
								Aggregation.unwind("doctorEducation", true),
								Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "clinic"),
								Aggregation.unwind("clinic", true), Aggregation.match(criteria),
								new CustomAggregationOperation(new Document("$project",
										new BasicDBObject("firstName", "$user.firstName")
												.append("isActive", "$user.isActive").append("userId", "$doctorId")
												.append("emailAddress", "$user.emailAddress")
												.append("userState", "$user.userState")
												.append("userUId", "$user.userUId")
												.append("createdTime", "$user.createdTime")
												.append("mobileNumber", "$user.mobileNumber")
												.append("city", "$location.city")
												.append("isHealthcocoDoctor", "$doctorEducation.isHealthcocoDoctor")
												.append("isRegistrationDetailsVerified",
														"$doctorEducation.isRegistrationDetailsVerified")
												.append("isPhotoIdVerified", "$doctorEducation.isPhotoIdVerified")
												.append("isOnlineConsultationAvailable",
														"$clinic.isOnlineConsultationAvailable")
												.append("registrationNumber",
														"$doctorEducation.registrationDetails.registrationId")
												.append("qualification", "$doctorEducation.education.qualification")
												.append("specialitiesIds", "$doctorEducation.specialities")
												.append("title", "$user.title")
												.append("isDoctorListed", "$isDoctorListed"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$userId")
												.append("firstName", new BasicDBObject("$first", "$firstName"))
												.append("isActive", new BasicDBObject("$first", "$isActive"))
												.append("userId", new BasicDBObject("$first", "$userId"))
												.append("isRegistrationDetailsVerified",
														new BasicDBObject("$first", "$isRegistrationDetailsVerified"))
												.append("isPhotoIdVerified",
														new BasicDBObject("$first", "$isPhotoIdVerified"))
												.append("isOnlineConsultationAvailable",
														new BasicDBObject("$first", "$isOnlineConsultationAvailable"))
												.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
												.append("userState", new BasicDBObject("$first", "$userState"))
												.append("userUId", new BasicDBObject("$first", "$userUId"))
												.append("createdTime", new BasicDBObject("$first", "$createdTime"))
												.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
												.append("city", new BasicDBObject("$first", "$city"))
												.append("isHealthcocoDoctor",
														new BasicDBObject("$first", "$isHealthcocoDoctor"))
												.append("registrationNumber",
														new BasicDBObject("$first", "$registrationNumber"))
												.append("qualification", new BasicDBObject("$first", "$qualification"))
												.append("specialitiesIds",
														new BasicDBObject("$first", "$specialitiesIds"))
												.append("title", new BasicDBObject("$first", "$title"))
												.append("isDoctorListed",
														new BasicDBObject("$first", "$isDoctorListed")))),

								Aggregation.sort(Sort.Direction.DESC, "createdTime"),

								Aggregation.skip((long) (page) * size), Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(

								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location", true),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctorEducation"),
								Aggregation.unwind("doctorEducation", true),
								Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "clinic"),
								Aggregation.unwind("clinic", true), Aggregation.match(criteria),
								new CustomAggregationOperation(new Document("$project",
										new BasicDBObject("firstName", "$user.firstName")
												.append("isActive", "$user.isActive").append("userId", "$doctorId")
												.append("emailAddress", "$user.emailAddress")
												.append("userState", "$user.userState")
												.append("userUId", "$user.userUId")
												.append("createdTime", "$user.createdTime")
												.append("mobileNumber", "$user.mobileNumber")
												.append("isRegistrationDetailsVerified",
														"$doctorEducation.isRegistrationDetailsVerified")
												.append("isPhotoIdVerified", "$doctorEducation.isPhotoIdVerified")
												.append("isOnlineConsultationAvailable",
														"$clinic.isOnlineConsultationAvailable")
												.append("city", "$location.city")
												.append("isHealthcocoDoctor", "$doctorEducation.isHealthcocoDoctor")
												.append("registrationNumber",
														"$doctorEducation.registrationDetails.registrationId")
												.append("qualification", "$doctorEducation.education.qualification")
												.append("specialitiesIds", "$doctorEducation.specialities")
												.append("title", "$user.title")
												.append("isDoctorListed", "$isDoctorListed"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$userId")
												.append("firstName", new BasicDBObject("$first", "$firstName"))
												.append("isActive", new BasicDBObject("$first", "$isActive"))
												.append("isRegistrationDetailsVerified",
														new BasicDBObject("$first", "$isRegistrationDetailsVerified"))
												.append("isPhotoIdVerified",
														new BasicDBObject("$first", "$isPhotoIdVerified"))
												.append("isOnlineConsultationAvailable",
														new BasicDBObject("$first", "$isOnlineConsultationAvailable"))

												.append("userId", new BasicDBObject("$first", "$userId"))
												.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
												.append("userState", new BasicDBObject("$first", "$userState"))
												.append("userUId", new BasicDBObject("$first", "$userUId"))
												.append("createdTime", new BasicDBObject("$first", "$createdTime"))
												.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
												.append("city", new BasicDBObject("$first", "$city"))
												.append("isHealthcocoDoctor",
														new BasicDBObject("$first", "$isHealthcocoDoctor"))
												.append("registrationNumber",
														new BasicDBObject("$first", "$registrationNumber"))
												.append("qualification", new BasicDBObject("$first", "$qualification"))
												.append("specialitiesIds",
														new BasicDBObject("$first", "$specialitiesIds"))
												.append("title", new BasicDBObject("$first", "$title"))
												.append("isDoctorListed",
														new BasicDBObject("$first", "$isDoctorListed"))

								)),

								Aggregation.sort(Sort.Direction.DESC, "createdTime"));
					}
					AggregationResults<DoctorResponse> results = mongoTemplate.aggregate(aggregation,
							// aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
							DoctorClinicProfileCollection.class, DoctorResponse.class);
					doctorResponses = results.getMappedResults();

					if (doctorResponses != null && !doctorResponses.isEmpty()) {
						for (DoctorResponse doctorResponse : doctorResponses) {

							if (doctorResponses != null) {
								List<String> speciality = null;
								speciality = (List<String>) CollectionUtils.collect(
										(Collection<?>) specialityRepository
												.findByIdIn(doctorResponse.getSpecialitiesIds()),
										new BeanToPropertyValueTransformer("superSpeciality"));
								doctorResponse.setSpecialities(speciality);
								doctorResponse.setSpecialitiesIds(null);
							}

							List<UserRoleCollection> userRoleCollection = userRoleRepository
									.findByUserId(new ObjectId(doctorResponse.getUserId()));
							@SuppressWarnings("unchecked")
							Collection<ObjectId> roleIds = CollectionUtils.collect(userRoleCollection,
									new BeanToPropertyValueTransformer("roleId"));
							if (roleIds != null && !roleIds.isEmpty()) {
								Integer count = roleRepository.findCountByIdAndRole(roleIds, RoleEnum.ADMIN.getRole());
								if (count != null && count > 0)
									doctorResponse.setRole(RoleEnum.ADMIN.getRole());
							}
							doctorResponse.setIsNutritionist(isNutritionist);
						}

						response.setDataList(doctorResponses);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countDoctors(String locationId, String state, String searchTerm, Boolean islisted,
			Boolean isNutritionist) {
		Integer response = 0;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria = criteria.and("userState").is(state);
			}

			if (islisted != null) {
				criteria.and("doctorProfile.isDoctorListed").is(islisted);
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria = criteria.andOperator(new Criteria("doctorProfile.locationId").is(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm));
			}
			criteria.and("doctorProfile.isNutritionist").is(isNutritionist);

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId", "doctorProfile"),
					Aggregation.unwind("doctorProfile"), Aggregation.match(criteria),
					Aggregation.lookup("docter_cl", "_id", "userId", "doctor"), Aggregation.unwind("doctor"),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$doctor.userId")
									.append("firstName", new BasicDBObject("$first", "$firstName"))
									.append("isActive", new BasicDBObject("$first", "$isActive"))
									.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
									.append("userState", new BasicDBObject("$first", "$userState"))
									.append("userUId", new BasicDBObject("$first", "$userUId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			response = mongoTemplate.aggregate(aggregation, UserCollection.class, UserCollection.class)
					.getMappedResults().size();

		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	public DataCount getDoctorCount() {
		DataCount response = new DataCount();
		try {
			Aggregation aggregation = null;

			aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria().and("isDoctorListed").is(true).orOperator(
							new Criteria("isNutritionist").is(false), new Criteria("isNutritionist").exists(false))));
//					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
//					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
//					Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"), Aggregation.unwind("doctor"),
//					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$doctorId"))));

			response.setCountOfListed(
					mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class, UserCollection.class)
							.getMappedResults().size());

			aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria().and("isDoctorListed").is(false).orOperator(
							new Criteria("isNutritionist").is(false), new Criteria("isNutritionist").exists(false))));
//					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
//					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
//					Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"), Aggregation.unwind("doctor"),
//					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$doctorId"))));

			response.setCountOfUnListed(
					mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class, UserCollection.class)
							.getMappedResults().size());

			aggregation = Aggregation.newAggregation(
//					Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId", "doctorProfile"),
//					Aggregation.lookup("docter_cl", "_id", "userId", "doctor"), Aggregation.unwind("doctor"),
//					Aggregation.unwind("doctorProfile"),
					Aggregation.match(new Criteria().and("isDoctorListed").exists(true).orOperator(
							new Criteria("isNutritionist").is(false), new Criteria("isNutritionist").exists(false))));
//					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$doctorId"))));
			response.setTotal(
					mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class, UserCollection.class)
							.getMappedResults().size());

		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<Location> getLabs(int page, int size, String hospitalId) {
		List<Location> response = null;
		try {
			List<LocationCollection> locationCollections = null;
			if (DPDoctorUtils.anyStringEmpty(hospitalId)) {
				if (size > 0)
					locationCollections = locationRepository.findByIsLab(true,
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					locationCollections = locationRepository.findByIsLab(true, new Sort(Direction.DESC, "createdTime"));
			} else {
				ObjectId hospitalObjectId = null;
				if (!DPDoctorUtils.anyStringEmpty(hospitalId))
					hospitalObjectId = new ObjectId(hospitalId);

				if (size > 0)
					locationCollections = locationRepository.findByHospitalId(hospitalObjectId, true,
							PageRequest.of(page, size, Direction.DESC, "createdTime"));
				else
					locationCollections = locationRepository.findByHospitalId(hospitalObjectId, true,
							new Sort(Direction.DESC, "createdTime"));
			}
			if (locationCollections != null) {
				response = new ArrayList<Location>();
				// for(LocationCollection location : locationCollections){
				// if (location.getImages() != null &&
				// !location.getImages().isEmpty()) {
				// for (ClinicImage clinicImage : location.getImages()) {
				// if (clinicImage.getImageUrl() != null) {
				// clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
				// }
				// if (clinicImage.getThumbnailUrl() != null) {
				// clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
				// }
				// }
				// }
				// if (location.getLogoUrl() != null)
				// location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				// if (location.getLogoThumbnailUrl() != null)
				// location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
				// }
				BeanUtil.map(locationCollections, response);
			}
		} catch (Exception e) {
			logger.error("Error while getting clinics " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting inactive clinics " + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countLabs(String hospitalId) {
		Integer response = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(hospitalId)) {

				response = locationRepository.countLabs(true);
			} else {
				ObjectId hospitalObjectId = null;
				if (!DPDoctorUtils.anyStringEmpty(hospitalId))
					hospitalObjectId = new ObjectId(hospitalId);

				response = locationRepository.countLabs(hospitalObjectId, true);
			}

		} catch (Exception e) {
			logger.error("Error while getting count lab " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting count lab " + e.getMessage());
		}
		return response;
	}

	@Override
	public DataCount getLabsCount() {
		DataCount response = new DataCount();
		try {
			response.setCountOfListed(locationRepository.countLabs(true, true));
			response.setCountOfUnListed(locationRepository.countLabs(true, false));
			response.setTotal(locationRepository.countLabs(true));

		} catch (Exception e) {
			logger.error("Error while getting count lab " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting count lab " + e.getMessage());
		}
		return response;
	}

	@Override
	public DataCount getclinicsCount() {
		DataCount response = new DataCount();
		try {
			response.setCountOfListed(locationRepository.countClinic(true, true));
			response.setCountOfUnListed(locationRepository.countClinic(true, false));
			response.setTotal(locationRepository.countClinic(true));

		} catch (Exception e) {
			logger.error("Error while getting count Clinic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting count Clinic " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public ContactUs addContactUs(ContactUs request) {
		ContactUs response = null;
		try {
			ContactUsCollection contactUsCollection = new ContactUsCollection();
			BeanUtil.map(request, contactUsCollection);
			contactUsCollection.setCreatedTime(new Date());
			contactUsCollection = contactUsRepository.save(contactUsCollection);
			if (contactUsCollection != null) {
				response = new ContactUs();
				BeanUtil.map(contactUsCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error while adding contact us " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding contact us " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ContactUs> getContactUs(int page, int size, String searchTerm) {
		List<ContactUs> response = null;
		try {

//			List<ContactUsCollection> contactUs = null;
//			if (size > 0)
//				contactUs = contactUsRepository.findAll(PageRequest.of(page, size, Direction.DESC, "createdTime"))
//						.getContent();
//			else
//				contactUs = contactUsRepository.findAll(new Sort(Direction.DESC, "createdTime"));
//
//			if (contactUs != null) {
//				response = new ArrayList<ContactUs>();
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("type").regex("^" + searchTerm, "i"),
						new Criteria("type").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, ContactUsCollection.class, ContactUs.class)
					.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting clinics " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting inactive clinics " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<Speciality> getUniqueSpecialities(String searchTerm, String updatedTime, int page, int size) {
		List<Speciality> response = null;
		try {
			Aggregation aggregation = null;

			if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
				aggregation = Aggregation.newAggregation(
						Aggregation.group("speciality").first("speciality").as("speciality"),
						Aggregation.sort(Sort.Direction.ASC, "speciality"));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("speciality").regex("^" + searchTerm, "i")),
						Aggregation.group("speciality").first("speciality").as("speciality"),
						Aggregation.sort(Sort.Direction.ASC, "speciality"));
			}

			AggregationResults<Speciality> groupResults = mongoTemplate.aggregate(aggregation,
					SpecialityCollection.class, Speciality.class);
			response = groupResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public void importProfessionalMembership() {
		String csvFile = "/home/ubuntu/Memberships.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				ProfessionalMembershipCollection professionalMembershipCollection = new ProfessionalMembershipCollection();
				professionalMembershipCollection.setMembership(obj[0]);
				professionalMembershipCollection.setCreatedBy("ADMIN");
				professionalMembershipCollection.setCreatedTime(new Date());
				professionalMembershipCollection.setUpdatedTime(new Date());

				professionalMembershipRepository.save(professionalMembershipCollection);
				ESProfessionalMembershipDocument document = new ESProfessionalMembershipDocument();
				BeanUtil.map(professionalMembershipCollection, document);
				esProfessionalMembershipRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void importMedicalCouncil() {

	}

	@Override
	public Boolean sendLink(SendAppLink request) {
		Boolean response = false;
		try {
			String appType = "", appBitLink = "", appDeviceType = "";
			if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO.getType())) {
				appType = "Healthcoco";
				appBitLink = patientAppBitLink;
				appDeviceType = "phone";
			} else if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PLUS.getType())) {
				appType = "Healthcoco+";
				appBitLink = doctorAppBitLink;
				appDeviceType = "phone";
			} else if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PAD.getType())) {
				appType = "Healthcoco Pad";
				appBitLink = ipadAppBitLink;
				appDeviceType = "ipad";
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
				SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null,
						appLinkMessage.replace("{appType}", appType).replace("{appLink}", appBitLink),
						request.getMobileNumber(), "Get App Link");
				sMSServices.sendSMS(smsTrackDetail, false);
				response = true;
			} else if (!DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
				String body = mailBodyGenerator.generateAppLinkEmailBody(appType, appBitLink, appDeviceType,
						"appLinkTemplate.vm");
				mailService.sendEmail(request.getEmailAddress(), getAppLinkSubject.replace("{appType}", appType), body,
						null);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<PatientRecord> getPatientRecord(int page, int size, int max, int min, String searchTerm, String type) {
		List<PatientRecord> response = new ArrayList<PatientRecord>();
		Aggregation aggregation = null;
		Criteria criteria = new Criteria("userState").ne("ADMIN");
		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("doctorId", "$user._id"),
				Fields.field("firstName", "$user.firstName"), Fields.field("emailAddress", "$user.emailAddress"),
				Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$user.updatedTime"),
				Fields.field("locationName", "$location.locationName"), Fields.field("locality", "$location.locality"),
				Fields.field("city", "$location.city")));
		CustomAggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria = criteria.orOperator(new Criteria("emailAddress").regex("^" + searchTerm, "i"),
					new Criteria("firstName").regex("^" + searchTerm),
					new Criteria("location").regex("^" + searchTerm));
		}
		if (size > 0) {
			if (type == null || type == "") {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
								Aggregation.unwind("user"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("records_cl", "doctorId", "doctorId", "records"),
								Aggregation.unwind("records"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("doctorId", "$doctorId")
														.append("locationId", "$locationId"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("records", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patients"),
								Aggregation.unwind("patients"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("clinical_notes_cl", "doctorId", "doctorId", "clinicalNotes"),
								Aggregation.unwind("clinicalNotes"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "prescription"),
								Aggregation.unwind("prescription"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
												.append("prescription", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("appointment_cl", "doctorId", "doctorId", "appointment"),
								Aggregation.unwind("appointment"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
												.append("prescription", new BasicDBObject("$first", "$prescription"))
												.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
												.append("appointment", new BasicDBObject("$sum", 1)))),
								projectList.and("records").as("totalNoOfRecords").and("patients")
										.as("totalNoOfPatients").and("clinicalNotes").as("totalNoOfClinincalNotes")
										.and("prescription").as("totalNoOfRX").and("appointment")
										.as("totalNoOfAppointments"),
								Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			} else {
				switch (ListType.valueOf(type.toUpperCase())) {

				case TOTAL_NO_OF_RECORDS: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfRecords").lte(min),
								new Criteria("totalNoOfRecords").gte(max));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("totalNoOfRecords", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("records_cl", "doctorId", "doctorId", "totalNoOfRecords"),
							Aggregation.unwind("totalNoOfRecords"), aggregationOperation,
							projectList.and("totalNoOfRecords").as("totalNoOfRecords"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "totalNoOfRecords"),
							Aggregation.skip((long) (page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;

				case TOTAL_NO_OF_PATIENTS: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfPatients").lte(max),
								new Criteria("totalNoOfPatients").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("totalNoOfPatients", new BasicDBObject("$sum", 1))));

					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("patient_cl", "doctorId", "doctorId", "totalNoOfPatients"),
							Aggregation.unwind("totalNoOfPatients"), aggregationOperation,
							projectList.and("totalNoOfPatients").as("totalNoOfPatients"), Aggregation.match(criteria),

							Aggregation.sort(Sort.Direction.DESC, "totalNoOfPatients"),
							Aggregation.skip((long) (page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				case TOTAL_NO_OF_CLINICAL_NOTES: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfClinincalNotes").lte(max),
								new Criteria("totalNoOfClinincalNotes").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("totalNoOfClinincalNotes", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("clinical_notes_cl", "doctorId", "doctorId",
											"totalNoOfClinincalNotes"),
									Aggregation.unwind("totalNoOfClinincalNotes"), aggregationOperation,
									projectList.and("totalNoOfClinincalNotes").as("totalNoOfClinincalNotes"),
									Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfClinincalNotes"),
									Aggregation.skip((long) (page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;

				case TOTAL_NO_OF_RX: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfRX").lte(max),
								new Criteria("totalNoOfRX").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("totalNoOfRX", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "totalNoOfRX"),
									Aggregation.unwind("totalNoOfRX"), aggregationOperation,
									projectList.and("totalNoOfRX").as("totalNoOfRX"), Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfRX"),
									Aggregation.skip((long) (page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				// changed Ritesh
				case TOTAL_NO_OF_APPOINTMENTS: {

					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfAppointments").lte(max),
								new Criteria("totalNoOfAppointments").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("totalNoOfAppointments", new BasicDBObject("$sum", 1))));

					// aggregation = Aggregation
					// .newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
					// Aggregation.unwind("user"),
					// Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					// Aggregation.unwind("location"),
					// Aggregation.lookup("appointment_cl", "doctorId", "doctorId",
					// "totalNoOfAppointments"),
					// Aggregation.unwind("totalNoOfAppointments"), aggregationOperation,
					// projectList.and("totalNoOfAppointments").as("totalNoOfAppointments"),
					// Aggregation.sort(Sort.Direction.DESC, "totalNoOfAppointments"),
					// Aggregation.skip((long)(page) * size), Aggregation.limit(size))
					// .withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("appointment_cl", "doctorId", "doctorId",
											"totalNoOfAppointments"),
									Aggregation.unwind("totalNoOfAppointments"), aggregationOperation,
									projectList.and("totalNoOfAppointments").as("totalNoOfAppointments"),
									Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfAppointments"),
									Aggregation.skip((long) (page) * size), Aggregation.limit(size))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				}
			}
		} else {
			if (type == null || type == "") {
				aggregation = Aggregation
						.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
								Aggregation.unwind("user"),
								Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("records_cl", "doctorId", "doctorId", "records"),
								Aggregation.unwind("records"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patients"),
								Aggregation.unwind("patients"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("clinical_notes_cl", "doctorId", "doctorId", "clinicalNotes"),
								Aggregation.unwind("clinicalNotes"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "prescription"),
								Aggregation.unwind("prescription"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
												.append("prescription", new BasicDBObject("$sum", 1)))),
								Aggregation.lookup("appointment_cl", "doctorId", "doctorId", "appointment"),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id",
												new BasicDBObject("id", "$_id").append("user", "$user._id")
														.append("location", "$location._id"))
												.append("user", new BasicDBObject("$first", "$user"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("location", new BasicDBObject("$first", "$location"))
												.append("records", new BasicDBObject("$first", "$records"))
												.append("patients", new BasicDBObject("$first", "$patients"))
												.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
												.append("prescription", new BasicDBObject("$first", "$prescription"))

												.append("appointment", new BasicDBObject("$sum", 1)))),
								projectList.and("records").as("totalNoOfRecords").and("patients")
										.as("totalNoOfPatients").and("clinicalNotes").as("totalNoOfClinincalNotes")
										.and("prescription").as("totalNoOfRX").and("appointment")
										.as("totalNoOfAppointments"),
								Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"))
						.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
			} else {
				switch (ListType.valueOf(type.toUpperCase())) {
				case TOTAL_NO_OF_RECORDS: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfRecords").lte(max),
								new Criteria("totalNoOfRecords").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("records", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("records_cl", "doctorId", "doctorId", "totalNoOfRecords"),
							Aggregation.unwind("totalNoOfRecords"), aggregationOperation,
							projectList.and("totalNoOfRecords").as("totalNoOfRecords"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "totalNoOfRecords"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;

				case TOTAL_NO_OF_PATIENTS: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfPatients").lte(max),
								new Criteria("totalNoOfPatients").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("totalNoOfPatients", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("patient_cl", "doctorId", "doctorId", "totalNoOfPatients"),
							aggregationOperation, Aggregation.unwind("totalNoOfPatients"),
							projectList.and("totalNoOfPatients").as("totalNoOfPatients"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "totalNoOfPatients"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				case TOTAL_NO_OF_CLINICAL_NOTES: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfClinincalNotes").lte(max),
								new Criteria("totalNoOfClinincalNotes").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("clinicalNotes", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("clinical_notes_cl", "doctorId", "doctorId",
											"totalNoOfClinincalNotes"),
									Aggregation.unwind("totalNoOfClinincalNotes"), aggregationOperation,
									projectList.and("totalNoOfClinincalNotes").as("totalNoOfClinincalNotes"),
									Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfClinincalNotes"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;

				case TOTAL_NO_OF_RX: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfRX").lte(max),
								new Criteria("totalNoOfRX").gte(min));
					}

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("prescription", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "totalNoOfRX"),
									Aggregation.unwind("totalNoOfRX"), aggregationOperation,
									projectList.and("totalNoOfRX").as("totalNoOfRX"), Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfRX"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				// changed ritesh
				case TOTAL_NO_OF_APPOINTMENTS: {
					if (max > 0) {
						criteria = criteria.andOperator(new Criteria("totalNoOfAppointments").lte(max),
								new Criteria("totalNoOfAppointments").gte(min));
					}
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id", "$_id").append("user", "$user._id").append("location",
											"$location._id"))
									.append("user", new BasicDBObject("$first", "$user"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("createdTime", new BasicDBObject("$first", "$user.createdTime"))
									.append("appointment", new BasicDBObject("$sum", 1))));
					aggregation = Aggregation
							.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
									Aggregation.unwind("user"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("appointment_cl", "doctorId", "doctorId",
											"totalNoOfAppointments"),
									Aggregation.unwind("totalNoOfAppointments"), aggregationOperation,
									projectList.and("totalNoOfAppointments").as("totalNoOfAppointments"),
									Aggregation.match(criteria),
									Aggregation.sort(Sort.Direction.DESC, "totalNoOfAppointments"))
							.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());
				}
					break;
				}
			}
		}

		AggregationResults<PatientRecord> results = mongoTemplate.aggregate(aggregation,
				DoctorClinicProfileCollection.class, PatientRecord.class);
		response = results.getMappedResults();

		return response;
		// changed
	}

	@Override
	public Boolean updateDoctorClinicIsListed(String locationId, String doctorId, Boolean isListed) {
		Boolean response = false;
		ObjectId doctorObjectId;
		ObjectId locationObjectId;
		Integer count;
		try {
			locationObjectId = new ObjectId(locationId);
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
			String slugurl = null;
			if (locationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					doctorObjectId = new ObjectId(doctorId);
					DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(doctorObjectId, locationObjectId);
					doctorClinicProfileCollection.setIsDoctorListed(isListed);
					ESDoctorDocument esDoctorDocument = esDoctorRepository.findByUserIdAndLocationId(doctorId,
							locationId);
					if (isListed && DPDoctorUtils.anyStringEmpty(esDoctorDocument.getDoctorSlugURL())) {

						if (!DPDoctorUtils.anyStringEmpty(esDoctorDocument.getFirstName())) {
							slugurl = "dr-" + esDoctorDocument.getFirstName().toLowerCase().trim();
						}
						if (esDoctorDocument.getSpecialities() != null) {
							List<String> specialities = new ArrayList<String>();
							for (String specialityId : esDoctorDocument.getSpecialities()) {
								SpecialityCollection specialityCollection = specialityRepository
										.findById(new ObjectId(specialityId)).orElse(null);
								if (specialityCollection != null) {
									specialities.add(specialityCollection.getSuperSpeciality());
									if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
										slugurl = slugurl + "-"
												+ specialityCollection.getSuperSpeciality().toLowerCase();
									} else {
										slugurl = specialityCollection.getSuperSpeciality().toLowerCase();
									}
								}
							}

						}
						if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
							esDoctorDocument.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-").replaceAll("/", "-")
									.replaceAll(",", "-").replaceAll("'", ""));
							doctorClinicProfileCollection.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-")
									.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", ""));
						}
					}

					esDoctorDocument.setIsDoctorListed(isListed);
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					esDoctorRepository.save(esDoctorDocument);
				} else {
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(locationObjectId, new Sort(Direction.DESC, "updatedTime"));
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						slugurl = null;
						if (doctorClinicProfileCollection != null) {
							doctorClinicProfileCollection.setIsDoctorListed(isListed);
							ESDoctorDocument esDoctorDocument = esDoctorRepository.findByUserIdAndLocationId(
									doctorClinicProfileCollection.getDoctorId().toString(), locationId);

							if (isListed && DPDoctorUtils.anyStringEmpty(esDoctorDocument.getDoctorSlugURL())) {

								if (!DPDoctorUtils.anyStringEmpty(esDoctorDocument.getFirstName())) {
									slugurl = "dr-" + esDoctorDocument.getFirstName().toLowerCase().trim();

								}

								if (esDoctorDocument.getSpecialities() != null) {
									List<String> specialities = new ArrayList<String>();
									for (String specialityId : esDoctorDocument.getSpecialities()) {
										SpecialityCollection specialityCollection = specialityRepository
												.findById(new ObjectId(specialityId)).orElse(null);
										if (specialityCollection != null) {
											specialities.add(specialityCollection.getSuperSpeciality());
											if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
												slugurl = slugurl + "-"
														+ specialityCollection.getSuperSpeciality().toLowerCase();
											} else {
												slugurl = specialityCollection.getSuperSpeciality().toLowerCase();
											}
										}
									}

								}
								if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
									esDoctorDocument.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-")
											.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", ""));
									doctorClinicProfileCollection.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-")
											.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", ""));
								}
							}
							esDoctorDocument.setIsDoctorListed(isListed);
							doctorClinicProfileRepository.save(doctorClinicProfileCollection);
							esDoctorRepository.save(esDoctorDocument);
						}
					}

					locationCollection.setIsLocationListed(isListed);
					ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);
					if (DPDoctorUtils.anyStringEmpty(locationCollection.getLocationSlugUrl())) {
						slugurl = null;
						slugurl = locationCollection.getLocationName().trim().replaceAll(" ", "-").replaceAll("/", "-")
								.replaceAll(",", "-").replaceAll("'", "");

						count = locationRepository.countBySlugUrl(slugurl);
						slugurl = slugurl + "-0" + (count == null ? 1 : (count + 1));

						if (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())) {
							slugurl = (slugurl + "-" + locationCollection.getLocality().trim()).replaceAll(" ", "-")
									.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", "");
						}
						if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
							esLocationDocument.setLocationSlugUrl(slugurl);
							locationCollection.setLocationSlugUrl(slugurl);
						}
					}
					locationRepository.save(locationCollection);

					if (esLocationDocument != null) {
						esLocationDocument.setIsLocationListed(isListed);
						esLocationRepository.save(esLocationDocument);
					}
				}
				response = true;

			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid locationId.");
			}

		} catch (Exception e) {
			logger.error("Error while updating clinic and doctor isListed " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while updating clinic and doctor isListed  " + e.getMessage());
		}

		return response;
	}

	@Override
	public Boolean updateDoctorClinicRankingCount(String locationId, String doctorId, long rankingCount) {
		Boolean response = false;
		ObjectId doctorObjectId = null, locationObjectId = null;
		try {
			List<ESDoctorDocument> esDoctorDocuments = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorObjectId = new ObjectId(doctorId);
				List<DoctorClinicProfileCollection> clinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(doctorObjectId);
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : clinicProfileCollections) {
					doctorClinicProfileCollection.setRankingCount(rankingCount);
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				}
				esDoctorDocuments = esDoctorRepository.findByUserId(doctorId);
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					doctorDocument.setRankingCount(rankingCount);
					esDoctorRepository.save(doctorDocument);
				}
			} else if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				locationObjectId = new ObjectId(locationId);
				LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
				locationCollection.setClinicRankingCount(rankingCount);
				locationRepository.save(locationCollection);
				esDoctorDocuments = esDoctorRepository.findByLocationId(locationId);
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					doctorDocument.setClinicRankingCount(rankingCount);
					esDoctorRepository.save(doctorDocument);
				}
				ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);
				if (esLocationDocument != null) {
					esLocationDocument.setClinicRankingCount(rankingCount);
					esLocationRepository.save(esLocationDocument);
				}
			}
			response = true;
		} catch (Exception e) {
			logger.error("Error while updating clinic and doctor isListed " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while updating clinic and doctor isListed  " + e.getMessage());
		}

		return response;
	}

	@Override
	public List<User> getAdmin(int size, int page, String searchTerm) {
		List<User> response = null;
		try {
			Criteria criteria = new Criteria("userState").is("ADMIN");

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm));

			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<User> results = mongoTemplate.aggregate(aggregation, UserCollection.class, User.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting Admin List" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting Admin List  " + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countAdmin(String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("userState").is("ADMIN");

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm));

			}
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			response = mongoTemplate.aggregate(aggregation, UserCollection.class, User.class).getMappedResults().size();

		} catch (Exception e) {
			logger.error("Error while getting count Admin List" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting count Admin List  " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<SearchRequestDetailsResponse> getUserSearchRequestdetails(int page, int size, Long fromDate,
			Long toDate, Boolean isdirectRequest) {
		List<SearchRequestDetailsResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (fromDate > 0 && toDate > 0) {
				criteria.and("createdTime").gte(new Date(fromDate)).lt(new Date(toDate));
			} else if (fromDate > 0) {
				criteria.and("createdTime").gte(new Date(fromDate));
			} else if (toDate > 0) {
				criteria.and("createdTime").lt(new Date(toDate));
			}

			if (isdirectRequest) {
				criteria.and("localeId").exists(true);
			} else {
				criteria.orOperator(new Criteria("localeId").is(null), new Criteria("localeId").exists(false));
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex3"))),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex3"))),
						Aggregation.lookup("locale_cl", "localeId", "_id", "locale"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$locale").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex3"))),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

		} catch (Exception e) {
			logger.error("Error while getting user request details" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting user request details " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<SearchRequestToPharmacyResponse> getPharmacyResponse(String uniqueRequestId, String replyType) {
		List<SearchRequestToPharmacyResponse> response = null;
		try {
			Criteria criteria = new Criteria("uniqueRequestId").is(uniqueRequestId);
			if (replyType.toUpperCase().equalsIgnoreCase("NO")) {
				criteria.and("replyType").is("NO");
			} else if (replyType.toUpperCase().equalsIgnoreCase("YES")) {
				criteria.orOperator(new Criteria("replyType").is("YES"),
						new Criteria("replyType").is("REQUEST_FULFILLED"));
			} else {
				criteria.orOperator(new Criteria("replyType").is(null), new Criteria("replyType").exists(false));
			}

		} catch (Exception e) {
			logger.error("Error while getting Pharmacy Response" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting Pharmacy Response " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean blockUser(String userId, Boolean blockForDay, Boolean blockForHour) {
		Boolean response = false;
		try {

			BlockUserCollection blockUserCollection = blockUserRepository.findByUserIds(new ObjectId(userId));

			if (blockUserCollection == null) {
				blockUserCollection = new BlockUserCollection();
				blockUserCollection.setCreatedBy("Admin");
				blockUserCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
				if (userCollection != null) {
					Aggregation aggregation = Aggregation.newAggregation(
							Aggregation
									.match(new Criteria("userName").regex("^" + userCollection.getMobileNumber(), "i")
											.and("userState").is("USERSTATECOMPLETE")),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$mobileNumber")
											.append("userIds", new BasicDBObject("$push", "$_id"))
											.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber")))));

					PatientNumberAndUserIds user = mongoTemplate
							.aggregate(aggregation, UserCollection.class, PatientNumberAndUserIds.class)
							.getUniqueMappedResult();
					blockUserCollection.setUserIds(user.getUserIds());
				} else {
					throw new BusinessException(ServiceError.Unknown, "Error Invalid UserId");
				}
			}
			if (blockForDay) {
				blockUserCollection.setIsForDay(true);
			} else {
				blockUserCollection.setIsForDay(false);
			}

			if (blockForHour) {
				blockUserCollection.setIsForHour(true);
			} else {
				blockUserCollection.setIsForHour(false);
			}
			blockUserCollection.setUpdatedTime(new Date());
			blockUserRepository.save(blockUserCollection);
			response = true;

		} catch (Exception e) {
			logger.error("Error while Blocking Pharmacy User" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while Blocking Pharmacy User" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean updateDoctorData() {
		try {
			String slugurl;
			Criteria criteria = new Criteria("isDoctorListed").is(true).and("user").exists(true).and("location")
					.exists(true);
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.match(criteria))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			AggregationResults<DoctorClinicProfileCollection> results = mongoTemplate.aggregate(aggregation,
					DoctorClinicProfileCollection.class, DoctorClinicProfileCollection.class);
			List<DoctorClinicProfileCollection> clinicProfileCollections = results.getMappedResults();
			for (DoctorClinicProfileCollection doctorClinicProfileCollection : clinicProfileCollections) {
				LocationCollection locationCollection = locationRepository
						.findById(doctorClinicProfileCollection.getLocationId()).orElse(null);
				slugurl = null;
				if (locationCollection != null) {
					if (doctorClinicProfileCollection != null) {
						doctorClinicProfileCollection.setIsDoctorListed(true);
						ESDoctorDocument esDoctorDocument = esDoctorRepository.findByUserIdAndLocationId(
								doctorClinicProfileCollection.getDoctorId().toString(),
								doctorClinicProfileCollection.getLocationId().toString());
						if (esDoctorDocument != null) {
							if (doctorClinicProfileCollection.getDoctorSlugURL() == null) {

								if (!DPDoctorUtils.anyStringEmpty(esDoctorDocument.getFirstName())) {
									slugurl = "dr-" + esDoctorDocument.getFirstName().toLowerCase().trim();
								}
								if (esDoctorDocument.getSpecialities() != null) {
									List<String> specialities = new ArrayList<String>();
									for (String specialityId : esDoctorDocument.getSpecialities()) {
										SpecialityCollection specialityCollection = specialityRepository
												.findById(new ObjectId(specialityId)).orElse(null);
										if (specialityCollection != null) {
											specialities.add(specialityCollection.getSuperSpeciality());
											if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
												slugurl = slugurl + "-"
														+ specialityCollection.getSuperSpeciality().toLowerCase();
											} else {
												slugurl = specialityCollection.getSuperSpeciality().toLowerCase();
											}
										}
									}

								}
								if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
									esDoctorDocument.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-")
											.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", ""));
									doctorClinicProfileCollection.setDoctorSlugURL(slugurl.trim().replaceAll(" ", "-")
											.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", ""));
								}
							}
							esDoctorDocument.setIsDoctorListed(true);
							doctorClinicProfileRepository.save(doctorClinicProfileCollection);
							esDoctorRepository.save(esDoctorDocument);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while update User" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while update User" + e.getMessage());
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean updateLocation() {
		try {
			String slugurl;
			Integer count = 0;
			List<LocationCollection> locationCollections = mongoTemplate
					.find(new Query(new Criteria("isLocationListed").is(true)), LocationCollection.class);
			for (LocationCollection locationCollection : locationCollections) {
				ESLocationDocument esLocationDocument = esLocationRepository
						.findById(locationCollection.getId().toString()).orElse(null);
				if (esLocationDocument != null) {
					if (DPDoctorUtils.anyStringEmpty(locationCollection.getLocationSlugUrl())
							&& !DPDoctorUtils.anyStringEmpty(locationCollection.getLocationName())) {
						slugurl = null;
						slugurl = locationCollection.getLocationName().trim().replaceAll(" ", "-").replaceAll("/", "-")
								.replaceAll(",", "-").replaceAll("'", "");
						count = locationRepository.countBySlugUrl(slugurl);
						slugurl = slugurl + "-" + (count == null ? 1 : (count + 1));

						if (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())) {
							slugurl = slugurl + "-" + locationCollection.getLocality().trim().replaceAll(" ", "-")
									.replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", "");
						}
						if (!DPDoctorUtils.anyStringEmpty(slugurl)) {
							esLocationDocument.setLocationSlugUrl(slugurl);
							locationCollection.setLocationSlugUrl(slugurl);
						}
					}
					locationRepository.save(locationCollection);

					if (esLocationDocument != null) {
						esLocationRepository.save(esLocationDocument);
					}
				}
			}
			locationRepository.saveAll(locationCollections);

		} catch (

		Exception e) {
			logger.error("Error while update Location" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while update Location" + e.getMessage());

		}

		return true;
	}

	@Override
	@Transactional
	public Boolean updatePharmacy() {
		try {
			String slugUrl;
			Integer count = 0;
			List<LocaleCollection> localeCollections = mongoTemplate
					.find(new Query(new Criteria("isLocationListed").is(true)), LocaleCollection.class);
			if (localeCollections != null && !localeCollections.isEmpty()) {
				for (LocaleCollection localeCollection : localeCollections) {
					if (DPDoctorUtils.anyStringEmpty(localeCollection.getPharmacySlugUrl())) {
						slugUrl = localeCollection.getLocaleName();

						if (!localeCollection.getLocaleType().isEmpty() && localeCollection.getLocaleType() != null) {
							for (String type : localeCollection.getPharmacyType()) {
								if (DPDoctorUtils.anyStringEmpty(slugUrl)) {
									slugUrl = type;
								} else {
									slugUrl = slugUrl + "-" + type;
								}
							}
						}
						slugUrl = slugUrl.trim().replaceAll(",", "-").replaceAll("/", "-").replaceAll(" ", "-")
								.replaceAll("'", "");
						count = localeRepository.countBySlugUrl(slugUrl);
						slugUrl = slugUrl + "-0" + (count == null ? 1 : (count + 1));
						if (!DPDoctorUtils.anyStringEmpty(slugUrl)) {
							localeCollection.setPharmacySlugUrl(
									slugUrl.trim().replaceAll(",", "-").replaceAll("/", "-").replaceAll(" ", "-"));
						}
						localeRepository.save(localeCollection);
						transactionalService.checkPharmacy(localeCollection.getId());

					}
				}
			}

		} catch (

		Exception e) {
			logger.error("Error while update Pharmacy" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while update Pharmacy" + e.getMessage());

		}

		return true;
	}

	@Override
	public Boolean addCertificateTemplates(CertificateTemplate request) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = new CertificateTemplateCollection();
			BeanUtil.map(request, certificateTemplateCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				certificateTemplateCollection.setCreatedTime(new Date());
				if (DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
					certificateTemplateCollection.setCreatedBy("ADMIN");
				else {
					UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
							.orElse(null);
					certificateTemplateCollection
							.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				}
			} else {
				CertificateTemplateCollection oldCertificateTemplateCollection = certificateTemplateRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				certificateTemplateCollection.setUpdatedTime(new Date());
				certificateTemplateCollection.setCreatedBy(oldCertificateTemplateCollection.getCreatedBy());
				certificateTemplateCollection.setCreatedTime(oldCertificateTemplateCollection.getCreatedTime());
				certificateTemplateCollection.setDiscarded(oldCertificateTemplateCollection.getDiscarded());
			}
			certificateTemplateCollection = certificateTemplateRepository.save(certificateTemplateCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while adding certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while adding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public CertificateTemplate getCertificateTemplateById(String templateId) {
		CertificateTemplate response = null;
		try {
			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(templateId)))),
					CertificateTemplateCollection.class, CertificateTemplate.class).getUniqueMappedResult();
			if (response == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting certificate template" + e.getMessage());
		}
		return response;

	}

	@Override
	public List<CertificateTemplate> getCertificateTemplates(int page, int size, Boolean discarded,
			List<String> specialities) {
		List<CertificateTemplate> response = null;
		try {
			Criteria criteria = new Criteria();
			if (specialities != null && !specialities.isEmpty()) {
				criteria.and("specialities").in(specialities);
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Direction.DESC, "updatedTime"), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else

			{
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Direction.DESC, "updatedTime"));
			}
			response = mongoTemplate
					.aggregate(aggregation, CertificateTemplateCollection.class, CertificateTemplate.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardCertificateTemplates(String templateId, Boolean discarded) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = certificateTemplateRepository
					.findById(new ObjectId(templateId)).orElse(null);
			if (certificateTemplateCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
			certificateTemplateCollection.setUpdatedTime(new Date());
			certificateTemplateCollection.setDiscarded(discarded);
			certificateTemplateCollection = certificateTemplateRepository.save(certificateTemplateCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while discarding certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while discarding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<DoctorSearchResponse> searchDoctor(int size, int page, String searchTerm, String speciality,
			String city) {
		List<DoctorSearchResponse> response = null;
		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(speciality)) {

				criteria = criteria.orOperator(
						new Criteria("speciality.speciality").regex(StringUtils.capitalize(speciality)),
						new Criteria("speciality.superSpeciality").regex(StringUtils.capitalize(speciality)));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.and("user.firstName").regex("^" + searchTerm, "i");
			}
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.unwind("specialities"),
							Aggregation.lookup("speciality_cl", "specialities", "_id", "speciality"),
							Aggregation.unwind("speciality"), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"), Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$userId")
									.append("doctorName", new BasicDBObject("$first", "$user.firstName"))
									.append("specialities", new BasicDBObject("$addToSet", "$speciality.speciality"))
									.append("superSpecialities",
											new BasicDBObject("$addToSet", "$speciality.superSpeciality"))
									.append("isActive", new BasicDBObject("$first", "$user.isActive"))
									.append("isVerified", new BasicDBObject("$first", "$user.isVerified"))
									.append("emailAddress", new BasicDBObject("$first", "$user.emailAddress"))
									.append("mobileNumber", new BasicDBObject("$first", "$user.mobileNumber"))
									.append("userState", new BasicDBObject("$first", "$user.userState")))))
					.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

			AggregationResults<DoctorSearchResponse> results = mongoTemplate.aggregate(aggregation,
					DoctorCollection.class, DoctorSearchResponse.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while search Doctor for Doctor Lab");
		}
		return response;
	}

	@Override
	public List<DoctorResponseNew> getDoctorsList(int page, int size, String locationId, String state,
			String searchTerm, Boolean isListed, Boolean isNutritionist, Boolean isAdminNutritionist, Boolean isActive,
			String city) {
		List<DoctorResponseNew> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("firstName").regex(searchTerm, "i");
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId", "doctorProfile"),
						Aggregation.unwind("doctorProfile"),
						Aggregation.lookup("location_cl", "doctorProfile.locationId", "_id", "location"),
						Aggregation.unwind("location", true), Aggregation.match(criteria),
						new CustomAggregationOperation(
								new Document("$project", new BasicDBObject("firstName", "$firstName")
//										.append("isActive", "$isActive").append("emailAddress", "$emailAddress")
//										.append("userState", "$userState").append("createdTime", "$createdTime")
//										.append("mobileNumber", "$mobileNumber").append("city", "$location.city")
//										.append("title", "$title").append("isDoctorListed",
//												"$doctorProfile.isDoctorListed")
								)),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("firstName", new BasicDBObject("$first", "$firstName"))
//										.append("isActive", new BasicDBObject("$first", "$isActive"))
//										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//										.append("userState", new BasicDBObject("$first", "$userState"))
//										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
//										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//										.append("city", new BasicDBObject("$first", "$city"))
//										.append("title", new BasicDBObject("$first", "$title"))
//										.append("isDoctorListed", new BasicDBObject("$first", "$isDoctorListed"))
						)),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(

						Aggregation.lookup("doctor_clinic_profile_cl", "_id", "doctorId", "doctorProfile"),
						Aggregation.unwind("doctorProfile"),
						Aggregation.lookup("location_cl", "doctorProfile.locationId", "_id", "location"),
						Aggregation.unwind("location", true), Aggregation.match(criteria),
						new CustomAggregationOperation(
								new Document("$project", new BasicDBObject("firstName", "$firstName")
//										.append("isActive", "$isActive").append("emailAddress", "$emailAddress")
//										.append("userState", "$userState").append("createdTime", "$createdTime")
//										.append("mobileNumber", "$mobileNumber").append("city", "$location.city")
//										.append("title", "$title").append("isDoctorListed",
//												"$doctorProfile.isDoctorListed")
								)),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("firstName", new BasicDBObject("$first", "$firstName"))
//										.append("isActive", new BasicDBObject("$first", "$isActive"))
//										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//										.append("userState", new BasicDBObject("$first", "$userState"))
//										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
//										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//										.append("city", new BasicDBObject("$first", "$city"))
//										.append("title", new BasicDBObject("$first", "$title"))
//										.append("isDoctorListed", new BasicDBObject("$first", "$isDoctorListed"))
						)),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation, UserCollection.class,
					UserCollection.class);
			List<UserCollection> userCollections = results.getMappedResults();
			if (userCollections != null && !userCollections.isEmpty()) {
				response = new ArrayList<DoctorResponseNew>();
				for (UserCollection userCollection : userCollections) {
					DoctorResponseNew doctorResponse = new DoctorResponseNew();
					BeanUtil.map(userCollection, doctorResponse);
					doctorResponse.setIsNutritionist(isNutritionist);
					response.add(doctorResponse);
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getUserAppUsers(int size, int page, String searchTerm, String mobileNumber) {
		Response<Object> response = new Response<Object>();
		List<PatientAppUsersResponse> patientResponse = null;
		try {
			Criteria criteria = new Criteria("user.userState").is(UserState.USERSTATECOMPLETE).and("role.role")
					.is("PATIENT").and("user").exists(true).and("user.signedUp").is(true);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("emailAddress").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
			}

			if (mobileNumber != null) {
				criteria.and("user.mobileNumber").is(mobileNumber);
			}

//			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
//					new BasicDBObject("_id", "$_id")
//					.append("title", "$user.title")		
//					.append("firstName", "$user.firstName")		
//					.append("lastName", "$user.lastName")
//					.append("middleName", "$user.middleName")
//					.append("emailAddress", "$user.emailAddress")					
//					.append("countryCode", "$user.countryCode")
//					.append("mobileNumber", "$user.mobileNumber")
//					.append("colorCode", "$user.colorCode")	
//					.append("createdTime", "$user.createdTime")	
//					.append("updatedTime", "$user.updatedTime")	
//					));
//
//			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//					new BasicDBObject("_id", "$_id")
//					.append("title", new BasicDBObject("$first", "$title"))
//					.append("firstName", new BasicDBObject("$first", "$firstName"))
//					.append("lastName", new BasicDBObject("$first", "$lastName"))
//					.append("middleName", new BasicDBObject("$first", "$middleName"))
//					.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//					.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//					.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//					.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//					.append("createdTime",  new BasicDBObject("$first", "$createdTime"))
//					.append("updatedTime",  new BasicDBObject("$first", "$updatedTime"))
//					));

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$userId"),

					Fields.field("title", "$user.title"), Fields.field("firstName", "$user.firstName"),
					Fields.field("middleName", "$user.middleName"), Fields.field("lastName", "$user.lastName"),

					Fields.field("emailAddress", "$user.emailAddress"),
					Fields.field("countryCode", "$user.countryCode"),
					Fields.field("mobileNumber", "$user.mobileNumber"), Fields.field("colorCode", "$user.colorCode"),
					Fields.field("createdTime", "$user.createdTime"),
					Fields.field("updatedTime", "$user.updatedTime")));
			Aggregation aggregation = null;
			if (size > 0) {

				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.lookup("role_cl", "roleId", "_id", "role"),
						Aggregation.unwind("role"), Aggregation.match(criteria), projectList,
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
										.append("firstName", new BasicDBObject("$first", "$firstName"))
										.append("middleName", new BasicDBObject("$first", "$middleName"))
										.append("lastName", new BasicDBObject("$first", "$lastName"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("countryCode", new BasicDBObject("$first", "$countryCode"))
										.append("colorCode", new BasicDBObject("$first", "$colorCode"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))

						)), Aggregation.sort(Direction.DESC, "createdTime"), Aggregation.skip((long) page * size),
						Aggregation.limit(size));

//						new CustomAggregationOperation(new Document("$group",
//								new BasicDBObject("_id", "$_id")
//								.append("title", new BasicDBObject("$first", "$title"))
//								.append("firstName", new BasicDBObject("$first", "$patient.firstName"))
//								.append("lastName", new BasicDBObject("$first", "$lastName"))
//								.append("middleName", new BasicDBObject("$first", "$middleName"))
//								.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
//								.append("countryCode", new BasicDBObject("$first", "$countryCode"))
//								.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
//								.append("colorCode", new BasicDBObject("$first", "$colorCode"))
//								.append("createdTime",  new BasicDBObject("$first", "$createdTime"))
//								.append("updatedTime",  new BasicDBObject("$first", "$updatedTime"))
//								)),
//						

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.lookup("role_cl", "roleId", "_id", "role"),
						Aggregation.unwind("role"), Aggregation.match(criteria), projectList,
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
										.append("firstName", new BasicDBObject("$first", "$firstName"))
										.append("middleName", new BasicDBObject("$first", "$middleName"))
										.append("lastName", new BasicDBObject("$first", "$lastName"))
										.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
										.append("countryCode", new BasicDBObject("$first", "$countryCode"))
										.append("colorCode", new BasicDBObject("$first", "$colorCode"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
										.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))

						)), Aggregation.sort(Direction.DESC, "createdTime"));
			}
			patientResponse = mongoTemplate
					.aggregate(aggregation, UserRoleCollection.class, PatientAppUsersResponse.class)

					.getMappedResults();
			response.setDataList(patientResponse);
			response.setCount(patientResponse.size());
			System.out.println("aggregation" + aggregation);
		} catch (BusinessException e) {
			logger.error("Error while getting patients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean updateDentalChainStateOfClinic(String locationId, Boolean isDentalChain) {
		Boolean response = false;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection != null) {
				ESLocationDocument esLocationDocument = esLocationRepository
						.findById(locationCollection.getId().toString()).orElse(null);
				if (esLocationDocument != null) {
					esLocationDocument.setIsDentalChain(isDentalChain);
					esLocationRepository.save(esLocationDocument);
				}
				locationCollection.setIsDentalChain(isDentalChain);
				locationRepository.save(locationCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean updatePatientIsDentalChain(String id, Boolean isDentalChainVerified) {
		Boolean response = false;
		UserCollection userCollection = null;
		try {
			userCollection = userRepository.findById(new ObjectId(id)).orElse(null);
			if (userCollection != null) {
				userCollection.setIsDentalChainVerified(isDentalChainVerified);
				userCollection.setUpdatedTime(new Date());
				userRepository.save(userCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public DentalTreatmentDetailResponse addEditDentalTreatmentDetail(DentalTreatmentDetailRequest request) {
		DentalTreatmentDetailResponse response = null;
		try {
			DentalTreatmentDetailCollection dentalTreatmentDetailCollection = new DentalTreatmentDetailCollection();
			BeanUtil.map(request, dentalTreatmentDetailCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				dentalTreatmentDetailCollection.setCreatedTime(new Date());

			} else {
				DentalTreatmentDetailCollection treatmentDetailCollection = dentalTreatmentDetailRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				dentalTreatmentDetailCollection.setUpdatedTime(new Date());
				dentalTreatmentDetailCollection.setCreatedBy(treatmentDetailCollection.getCreatedBy());
				dentalTreatmentDetailCollection.setCreatedTime(treatmentDetailCollection.getCreatedTime());
				dentalTreatmentDetailCollection.setDiscarded(treatmentDetailCollection.getDiscarded());
			}

			String slugurl = dentalTreatmentDetailCollection.getTreatmentName().toLowerCase().trim()
					.replaceAll(" ", "-").replaceAll("/", "-").replaceAll(",", "-").replaceAll("'", "");
			dentalTreatmentDetailCollection.setTreatmentSlugUrl(slugurl);

			if (request.getTreatmentSteps() != null && !request.getTreatmentSteps().isEmpty()) {
				dentalTreatmentDetailCollection.setTreatmentSteps(new ArrayList<String>());
				dentalTreatmentDetailCollection.setTreatmentSteps(request.getTreatmentSteps());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getTitleImage())) {
				dentalTreatmentDetailCollection
						.setTitleImage(dentalTreatmentDetailCollection.getTitleImage().replace(imagePath, ""));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getTreamentImage())) {
				dentalTreatmentDetailCollection
						.setTreamentImage(dentalTreatmentDetailCollection.getTreamentImage().replace(imagePath, ""));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getThumbnailImage())) {
				dentalTreatmentDetailCollection
						.setThumbnailImage(dentalTreatmentDetailCollection.getThumbnailImage().replace(imagePath, ""));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getImage())) {
				dentalTreatmentDetailCollection
						.setImage(dentalTreatmentDetailCollection.getImage().replace(imagePath, ""));
			}
			dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.save(dentalTreatmentDetailCollection);
			response = new DentalTreatmentDetailResponse();
			BeanUtil.map(dentalTreatmentDetailCollection, response);

			if (response != null) {

				if (dentalTreatmentDetailCollection.getTreatmentSteps() != null
						&& !dentalTreatmentDetailCollection.getTreatmentSteps().isEmpty()) {
					response.setTreatmentSteps(new ArrayList<String>());
					response.setTreatmentSteps(dentalTreatmentDetailCollection.getTreatmentSteps());
				}
				if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getTitleImage()))
					response.setTitleImage(getFinalImageURL(dentalTreatmentDetailCollection.getTitleImage()));

				if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getTreamentImage()))
					response.setTreamentImage(getFinalImageURL(dentalTreatmentDetailCollection.getTreamentImage()));

				if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getThumbnailImage()))
					response.setThumbnailImage(getFinalImageURL(dentalTreatmentDetailCollection.getThumbnailImage()));

				if (!DPDoctorUtils.anyStringEmpty(dentalTreatmentDetailCollection.getImage()))
					response.setImage(getFinalImageURL(dentalTreatmentDetailCollection.getImage()));

			}
		} catch (Exception e) {
			logger.error("Error while adding DentalTreatmentDetail" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while adding DentalTreatmentDetail" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteDentalTreatmentDetailById(String id, Boolean isDiscarded) {
		Boolean response = false;
		DentalTreatmentDetailCollection dentalTreatmentDetailCollection = null;
		try {
			dentalTreatmentDetailCollection = dentalTreatmentDetailRepository.findById(new ObjectId(id)).orElse(null);
			if (dentalTreatmentDetailCollection != null) {
				dentalTreatmentDetailCollection.setDiscarded(isDiscarded);
				dentalTreatmentDetailCollection.setUpdatedTime(new Date());
				dentalTreatmentDetailRepository.save(dentalTreatmentDetailCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<DentalTreatmentDetailResponse> getDentalTreatmentDetail(int page, int size, String searchTerm) {
		List<DentalTreatmentDetailResponse> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("treatmentName").regex(searchTerm, "i");
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			response = mongoTemplate
					.aggregate(aggregation, DentalTreatmentDetailCollection.class, DentalTreatmentDetailResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting treatment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting treatment " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<DentalTreatmentNameResponse> getDentalTreatmentNames(int page, int size, String searchTerm) {
		List<DentalTreatmentNameResponse> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("treatmentName").regex(searchTerm, "i");
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			response = mongoTemplate
					.aggregate(aggregation, DentalTreatmentDetailCollection.class, DentalTreatmentNameResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting treatment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting treatment " + e.getMessage());
		}
		return response;
	}

	@Override
	public List<DentalAdminNameResponse> getDentalAdminNames(Boolean isBuddy, Boolean isAgent) {
		List<DentalAdminNameResponse> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (isBuddy)
				criteria.and("isBuddy").is(isBuddy);
			if (isAgent)
				criteria.and("isAgent").is(isAgent);
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			response = mongoTemplate.aggregate(aggregation, UserCollection.class, DentalAdminNameResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting DentalAdminNames " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting DentalAdminNames " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean editLocationSlugUrl(String locationId, String locationSlugUrl) {
		Boolean response = false;
		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection != null) {
				locationCollection.setLocationSlugUrl(locationSlugUrl);
				locationRepository.save(locationCollection);

				response = true;
			}

			ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);
			if (esLocationDocument != null) {
				esLocationDocument.setLocationSlugUrl(locationSlugUrl);
				esLocationRepository.save(esLocationDocument);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while verifying medical documents");
		}
		return response;
	}

	@Override
	public LeadsTypeReasonsResponse addEditLeadsTypeReasons(LeadsTypeReasonsRequest request) {
		LeadsTypeReasonsResponse response = null;
		DentalReasonsCollection reasonsCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				reasonsCollection = dentalReasonRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setCreatedTime(reasonsCollection.getCreatedTime());
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
			} else {
				reasonsCollection = new DentalReasonsCollection();
				BeanUtil.map(request, reasonsCollection);
				reasonsCollection.setUpdatedTime(new Date());
				reasonsCollection.setCreatedTime(new Date());
			}
			reasonsCollection = dentalReasonRepository.save(reasonsCollection);
			response = new LeadsTypeReasonsResponse();
			BeanUtil.map(reasonsCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while add edit Reasons");
			throw new BusinessException(ServiceError.Unknown, "Error while add edit Reasons");
		}
		return response;
	}

	@Override
	public Boolean deleteDentalReasonsById(String reasonId, Boolean isDiscarded) {
		Boolean response = false;
		DentalReasonsCollection dentalReasonsCollection = null;
		try {
			dentalReasonsCollection = dentalReasonRepository.findById(new ObjectId(reasonId)).orElse(null);
			if (dentalReasonsCollection != null) {
				dentalReasonsCollection.setDiscarded(isDiscarded);
				dentalReasonsCollection.setUpdatedTime(new Date());
				dentalReasonRepository.save(dentalReasonsCollection);
				response = true;
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Response<Object> getDentalReasonsList(int page, int size, String searchTerm) {
		List<LeadsTypeReasonsResponse> responseList = new ArrayList<LeadsTypeReasonsResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("reason").regex("^" + searchTerm, "i"),
						new Criteria("reason").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									DentalReasonsCollection.class, LeadsTypeReasonsResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						DentalReasonsCollection.class, LeadsTypeReasonsResponse.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						DentalReasonsCollection.class, LeadsTypeReasonsResponse.class).getMappedResults();

			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getUserProfile(String userId) {
		Response<Object> response = new Response<Object>();
		User user = null;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null) {
				user = new User();
				BeanUtil.map(userCollection, user);

				List<ObjectId> objectIds = new ArrayList<>();
				objectIds.add(userCollection.getId());
				List<CampNameCollection> campNameCollections = campNameRepository.findByAssociateDoctorIdsIn(objectIds);
				System.out.println("campNameCollections" + campNameCollections.size());
				List<String> campIds = new ArrayList<>();
				List<String> campNames = new ArrayList<>();
				for (CampNameCollection campNameCollection : campNameCollections) {
					campIds.add(campNameCollection.getId().toString());
					campNames.add(campNameCollection.getCampName());
				}
				BeanUtil.map(userCollection, response);
				user.setCampIds(campIds);
				user.setCampNames(campNames);
				response.setData(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean updateClinicSlugURL(String locationId, String slugURL) {
		Boolean response = false;
		ObjectId locationObjectId;
		locationObjectId = new ObjectId(locationId);
		if (!DPDoctorUtils.anyStringEmpty(slugURL)) {
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);

			locationCollection.setLocationSlugUrl(slugURL);
			locationRepository.save(locationCollection);

			ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);

			if (esLocationDocument != null) {
				esLocationDocument.setLocationSlugUrl(slugURL);
				esLocationRepository.save(esLocationDocument);
			}
			response = true;
		}
		return response;
	}

	@Override
	public Boolean addEditClinicChargeCode(String locationId, String chargeCode) {
		Boolean response = false;
		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			if (locationCollection != null) {
				locationCollection.setChargeCode(chargeCode);
				locationRepository.save(locationCollection);

				response = true;
			}

			ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);
			if (esLocationDocument != null) {
				esLocationDocument.setChargeCode(chargeCode);
				esLocationRepository.save(esLocationDocument);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while verifying medical documents");
			throw new BusinessException(ServiceError.Unknown, "Error while updating chargecode");
		}
		return response;
	}

	@Override
	public EmailList addEmailList(EmailList request) {
		EmailList response = null;
		try {
			EmailListCollection emailListCollection = new EmailListCollection();
			BeanUtil.map(request, emailListCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				emailListCollection.setCreatedTime(new Date());
				emailListCollection.setCreatedBy("ADMIN");

			} else {
				EmailListCollection listCollection = emailListRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				emailListCollection.setUpdatedTime(new Date());
				emailListCollection.setCreatedBy(listCollection.getCreatedBy());
				emailListCollection.setCreatedTime(listCollection.getCreatedTime());
				emailListCollection.setDiscarded(listCollection.getDiscarded());
				emailListCollection.setEmails(null);
			}
			emailListCollection.setEmails(request.getEmails());

			emailListCollection = emailListRepository.save(emailListCollection);
			response = new EmailList();
			BeanUtil.map(emailListCollection, response);

		} catch (Exception e) {
			logger.error("Error while adding emails" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while adding emails" + e.getMessage());
		}
		return response;
	}

//	@Scheduled(cron = "0 0 22 * * ?", zone = "IST")
	@Override
	public Boolean getDailyReportAnalyticstoDoctor() {
		Boolean response = false;
		try {
			Date todayDate;
			todayDate = new Date();
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = todayDate.toInstant();
			LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
			LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
			LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);

			// Fetch payments for today
			Criteria paymentCriteria = Criteria.where("receivedDate").gte(startOfDay).lte(endOfDay).and("discarded")
					.is(false);
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(paymentCriteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.sort(Sort.Direction.DESC, "receivedDate"),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("date", new BasicDBObject("$first", "$receivedDate"))
							.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("uniqueReceiptId", new BasicDBObject("$first", "$uniqueReceiptId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("locationId", new BasicDBObject("$first", "$location._id"))
							.append("amountPaid", new BasicDBObject("$first", "$amountPaid"))
							.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
							.append("receiptType", new BasicDBObject("$first", "$receiptType"))
							.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("firstName", new BasicDBObject("$first", "$user.firstName"))
							.append("mobileNumber", new BasicDBObject("$first", "$user.mobileNumber"))
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("usedAdvanceAmount", new BasicDBObject("$first", "$usedAdvanceAmount")))));

			List<PaymentDetailsAnalyticsDataResponse> receiptCollections = Optional
					.ofNullable(mongoTemplate.aggregate(aggregation, DoctorPatientReceiptCollection.class,
							PaymentDetailsAnalyticsDataResponse.class).getMappedResults())
					.orElse(Collections.emptyList());

			// Group by locationId for quick access
			Map<String, List<PaymentDetailsAnalyticsDataResponse>> locationPayments = receiptCollections != null
					? receiptCollections.stream()
							.collect(Collectors.groupingBy(PaymentDetailsAnalyticsDataResponse::getLocationId))
					: new HashMap<>();

			// Loop through all locations
			List<LocationCollection> allLocations = locationRepository.findAll();

			for (LocationCollection locationCollection : allLocations) {

				String locationId = locationCollection.getId().toString();

				List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts = locationPayments
						.getOrDefault(locationId, new ArrayList<>());

				if (locationSpecificReceipts == null || locationSpecificReceipts.isEmpty()) {

					// No payments for this location – add blank row
					locationSpecificReceipts = new ArrayList<>();
					PaymentDetailsAnalyticsDataResponse blankEntry = new PaymentDetailsAnalyticsDataResponse();
					blankEntry.setLocalPatientName("No Patient for Today");
					blankEntry.setDate(null);
					blankEntry.setModeOfPayment(null);
					blankEntry.setUsedAdvanceAmount(0.0);
					blankEntry.setAmountPaid(0.0);
					locationSpecificReceipts.add(blankEntry);
				}

				response = createPdfAndSendEmail(locationSpecificReceipts, locationCollection);
			}
		} catch (Exception e) {
			logger.error("Error in getDailyReportAnalyticstoDoctor(): ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private boolean createPdfAndSendEmail(List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts,
			LocationCollection locationCollection) {
		boolean response = false;
		try {

			ByteArrayOutputStream byteArrayOutputStream = createPdf(locationSpecificReceipts,
					"Daily Payments Report : " + locationCollection.getLocationName(), false, null, null);

			// Send Email
			EmailListCollection emailListCollection = emailListRepository.findByLocationId(locationCollection.getId());
			if (emailListCollection != null && !emailListCollection.getEmails().isEmpty()) {
				List<String> emails = emailListCollection.getEmails();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();

				String subject = locationCollection.getLocationName() + " - Daily Payment Collection Report";

				String body = "Hello,\n\n" + "Please find attached the payments report for the last 24 hours.\n\n"
						+ "Thank you for your attention.\n\n\n\n" + "--- Auto-generated Report\n"
						+ "Do not reply to this email";
				// Replace newline characters with HTML line breaks
				String htmlBody = body.replace("\n", "<br/>");
				mailService.sendEmailWithPdf(emails, subject, htmlBody, pdfBytes);
				response = true;
			}
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private ByteArrayOutputStream createPdf(List<PaymentDetailsAnalyticsDataResponse> payments, String title,
			Boolean isWeekly, LocalDate startOfWeek, LocalDate endOfWeek) {

		// Create PDF
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			DateTime now = DateTime.now();
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

			// Add page event handler to show footer on every page
			writer.setPageEvent(new PdfPageEventHelper() {
				@Override
				public void onEndPage(PdfWriter writer, com.lowagie.text.Document document) {
					PdfContentByte cb = writer.getDirectContent();
					Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC);
					Phrase footer = new Phrase("Auto-generated PDF | Powered by Healthcoco", footerFont);

					// Precise bottom right corner positioning
					float x = document.right() - 10; // 10 points from right margin
					float y = document.bottom() + 10; // 10 points above bottom margin

					ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
				}
			});
			document.open();

			Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
			Paragraph titleParagraph = new Paragraph(title, titleFont);
			document.add(titleParagraph);
			Paragraph dateParagraph;
			Font dateFont = new Font(Font.HELVETICA, 12, Font.BOLD);
			if (isWeekly) {
				dateParagraph = new Paragraph("Date: " + new DateTime(startOfWeek.toString()).toString("dd-MM-yyyy")
						+ " to " + new DateTime(endOfWeek.toString()).toString("dd-MM-yyyy"), dateFont);
			} else {
				dateParagraph = new Paragraph("Date: " + now.toString("dd-MM-yyyy"), dateFont);
			}
			document.add(dateParagraph);
			document.add(new Paragraph(" "));

			float[] columnWidths = { 2f, 1.5f, 1.5f, 1.5f };
			PdfPTable table = new PdfPTable(columnWidths);
			table.setWidthPercentage(100);

			String[] headers = { "Patient Name", "Receipt Date", "Payment Method", "Paid Amount" };
			Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);

			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setPadding(5);
				table.addCell(cell);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			double totalAmount = 0.0;
			double[] totals = new double[6];

			for (PaymentDetailsAnalyticsDataResponse payment : payments) {

				String patientName = payment.getLocalPatientName() != null ? payment.getLocalPatientName() : "";
				String formattedDate = payment.getDate() != null ? sdf.format(payment.getDate()) : "--";
				String modeOfPayment = payment.getModeOfPayment() != null ? payment.getModeOfPayment().name() : "";
				Double usedAdvanceAmount = payment.getUsedAdvanceAmount() != null ? payment.getUsedAdvanceAmount()
						: 0.0;
//				System.out.println("payment.getAmountPaid()" + payment.getAmountPaid());
				Double amountPaid = payment.getAmountPaid() != null ? payment.getAmountPaid() : 0.0;
				table.addCell(createCenteredCell(patientName));
				table.addCell(createCenteredCell(formattedDate));
				table.addCell(createCenteredCell(modeOfPayment));
//				table.addCell(createCenteredCell(String.valueOf(usedAdvanceAmount)));
				table.addCell(createCenteredCell(String.valueOf(formatLargeNumber(amountPaid))));

				totalAmount += amountPaid;
				String mode = payment.getModeOfPayment() != null ? payment.getModeOfPayment().name().toUpperCase()
						: "UNKNOWN";
				switch (mode) {
				case "CASH":
					totals[0] += amountPaid;
					break;
				case "ONLINE":
					totals[1] += amountPaid;
					break;
				case "WALLET":
					totals[2] += amountPaid;
					break;
				case "CARD":
					totals[3] += amountPaid;
					break;
				case "UPI":
					totals[4] += amountPaid;
					break;
				case "CHEQUE":
					totals[5] += amountPaid;
					break;
				default:
					// optionally log or track unknown payment type
					break;
				}
			}

			document.add(table);

			// Summary Table with Dynamic Columns
			document.add(new Paragraph(" "));

			// Determine non-zero columns
			List<String> activeSummaryHeaders = new ArrayList<>();
			List<Double> activeTotals = new ArrayList<>();

			String[] summaryHeaders = { "Cash Amount", "Card Amount", "Online Amount", "Wallet Amount", "UPI Amount",
					"Cheque Amount" };

			// Collect non-zero totals and corresponding headers
			for (int i = 0; i < totals.length; i++) {
				if (totals[i] > 0) {
					activeSummaryHeaders.add(summaryHeaders[i]);
					activeTotals.add(totals[i]);
				}
			}

			// Add Total Amount
			activeSummaryHeaders.add("Total Amount");
			activeTotals.add(totalAmount);

			// Create dynamic width array
			float[] summaryColumnWidths = new float[activeSummaryHeaders.size()];
			Arrays.fill(summaryColumnWidths, 1f);

			PdfPTable summaryTable = new PdfPTable(summaryColumnWidths);
			summaryTable.setWidthPercentage(100);
			Font headerFont1 = new Font(Font.HELVETICA, 12, Font.BOLD); // Increased font size to 14

			// Add headers
			for (String header : activeSummaryHeaders) {
				PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont1));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPadding(5);
				summaryTable.addCell(cell);
			}

			for (Double total : activeTotals) {
				summaryTable.addCell(createCenteredCell(formatLargeNumber(total)));
			}

			document.add(summaryTable);

			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return byteArrayOutputStream;
	}

	// Formatting method to handle large numbers
	private String formatLargeNumber(double number) {
		// Use DecimalFormat to avoid scientific notation
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(number);
	}

	// Helper method to create a centered cell with a paragraph
	private PdfPCell createCenteredCell(String text) {
		PdfPCell cell = new PdfPCell(new Paragraph(text));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		return cell;
	}

//	@Scheduled(cron = "0 0 22 * * 6", zone = "IST")
	@Override
	public Boolean getWeeklyReportAnalyticstoDoctor() {
		Boolean response = false;
		try {
			LocalDate today = LocalDate.now();
			LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
			LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

			LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
			LocalDateTime endOfWeekDateTime = endOfWeek.atTime(LocalTime.MAX);

			Criteria paymentCriteria = Criteria.where("receivedDate").gte(startOfWeekDateTime).lte(endOfWeekDateTime)
					.and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(paymentCriteria),
					Aggregation.lookup("patient_cl", "patientId", "userId", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.sort(Sort.Direction.DESC, "receivedDate"),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("date", new BasicDBObject("$first", "$receivedDate"))
							.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("uniqueReceiptId", new BasicDBObject("$first", "$uniqueReceiptId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("locationId", new BasicDBObject("$first", "$location._id"))
							.append("amountPaid", new BasicDBObject("$first", "$amountPaid"))
							.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
							.append("receiptType", new BasicDBObject("$first", "$receiptType"))
							.append("localPatientName", new BasicDBObject("$first", "$patient.localPatientName"))
							.append("firstName", new BasicDBObject("$first", "$user.firstName"))
							.append("mobileNumber", new BasicDBObject("$first", "$user.mobileNumber"))
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("usedAdvanceAmount", new BasicDBObject("$first", "$usedAdvanceAmount")))));
			List<PaymentDetailsAnalyticsDataResponse> receiptCollections = mongoTemplate.aggregate(aggregation,
					DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class).getMappedResults();

			// Group by locationId for quick access
			Map<String, List<PaymentDetailsAnalyticsDataResponse>> locationPayments = (receiptCollections != null)
					? receiptCollections.stream()
							.collect(Collectors.groupingBy(PaymentDetailsAnalyticsDataResponse::getLocationId))
					: new HashMap<>();

			// Loop through all locations
			List<LocationCollection> allLocations = locationRepository.findAll();
			for (LocationCollection locationCollection : allLocations) {
				String locationId = locationCollection.getId().toHexString();
				List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts = locationPayments.get(locationId);

				if (locationSpecificReceipts == null || locationSpecificReceipts.isEmpty()) {
					// No payments for this location – add blank row
					locationSpecificReceipts = new ArrayList<>();
					PaymentDetailsAnalyticsDataResponse blankEntry = new PaymentDetailsAnalyticsDataResponse();
					blankEntry.setLocalPatientName("No Patient for Today");
					blankEntry.setDate(null);
					blankEntry.setModeOfPayment(null);
					blankEntry.setUsedAdvanceAmount(0.0);
					blankEntry.setAmountPaid(0.0);
					locationSpecificReceipts.add(blankEntry);
				}

				response = createPdfAndSendEmailforWeeklyData(locationSpecificReceipts, locationCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private Boolean createPdfAndSendEmailforWeeklyData(
			List<PaymentDetailsAnalyticsDataResponse> locationSpecificReceipts, LocationCollection locationCollection) {
		boolean response = false;
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

		try {
			ByteArrayOutputStream byteArrayOutputStream = createPdf(locationSpecificReceipts,
					"Weekly Payments Report : " + locationCollection.getLocationName(), true, startOfWeek, endOfWeek);

			// Send Email
			EmailListCollection emailListCollection = emailListRepository.findByLocationId(locationCollection.getId());
			if (emailListCollection != null && !emailListCollection.getEmails().isEmpty()) {
				List<String> emails = emailListCollection.getEmails();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();

				String subject = locationCollection.getLocationName() + " - Weekly Payment Collection Report";
				String body = "Hello,\n\n" + "Please find attached the payments report for the last week.\n\n"
						+ "Thank you for your attention.\n\n\n\n" + "--- Auto-generated Report\n"
						+ "Do not reply to this email";
				// Replace newline characters with HTML line breaks
				String htmlBody = body.replace("\n", "<br/>");
				mailService.sendEmailWithPdf(emails, subject, htmlBody, pdfBytes);
				response = true;
			}
		} catch (Exception e) {
			logger.error("Error generating/sending PDF: ", e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean updateClinicNpsScore(String locationId, double npsScore) {
		Boolean response = false;
		ObjectId locationObjectId = null;
		try {
			List<ESDoctorDocument> esDoctorDocuments = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				locationObjectId = new ObjectId(locationId);
				LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
				locationCollection.setNpsScore(npsScore);
				locationRepository.save(locationCollection);
				esDoctorDocuments = esDoctorRepository.findByLocationId(locationId);
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					doctorDocument.setNpsScore(npsScore);
					esDoctorRepository.save(doctorDocument);
				}
				ESLocationDocument esLocationDocument = esLocationRepository.findById(locationId).orElse(null);
				if (esLocationDocument != null) {
					esLocationDocument.setNpsScore(npsScore);
					esLocationRepository.save(esLocationDocument);
				}
			}
			response = true;
		} catch (Exception e) {
			logger.error("Error while updating clinic nps " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while updating clinic nps " + e.getMessage());
		}

		return response;
	}

}