package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESCollectionBoyRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.repository.ESReferenceRepository;
import com.dpdocter.elasticsearch.repository.ESServicesRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESRegistrationServiceImpl implements ESRegistrationService {

	private static Logger logger = LogManager.getLogger(ESRegistrationServiceImpl.class.getName());

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Autowired
	private ESPatientRepository esPatientRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private UserRepository userRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESReferenceRepository esReferenceRepository;

	@Autowired
	private ESUserLocaleRepository esUserLocaleRepository;

	@Autowired
	private ESCollectionBoyRepository esCollectionBoyRepository;

	@Autowired
	private ESServicesRepository esServicesRepository;

	@Autowired
	private ESSpecialityRepository esSpecialityRepository;
	
	@Override
	public boolean addPatient(ESPatientDocument request) {
		boolean response = false;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				String localPatientNameFormatted = request.getLocalPatientName().replaceAll("[^a-zA-Z0-9]", "");
				request.setLocalPatientNameFormatted(localPatientNameFormatted.toLowerCase());
			}

			esPatientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getUserId()), Resource.PATIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Patient");
		}
		return response;
	}
	
	@Override
	public boolean addDoctor(ESDoctorDocument request) {
		boolean response = false;
		try {
			ESDoctorDocument doctorDocument = esDoctorRepository.findByUserIdAndLocationId(request.getUserId(),
					request.getLocationId());
			if (doctorDocument != null)
				request.setId(doctorDocument.getId());
			else
				request.setId(request.getUserId() + request.getLocationId());

			if (request.getLatitude() != null && request.getLongitude() != null)
				request.setGeoPoint(new GeoPoint(request.getLatitude(), request.getLongitude()));
			
			if (request.getSpecialities() != null && !request.getSpecialities().isEmpty()) {
				Iterable<ESSpecialityDocument>  iterableSpecialities = esSpecialityRepository.findAllById(request.getSpecialities());
				List<String> specialities = new ArrayList<>();
				List<String> parentSpecialities = new ArrayList<>();
				if(iterableSpecialities != null) {
					for(ESSpecialityDocument esSpecialityDocument : iterableSpecialities) {
						specialities.add(esSpecialityDocument.getSuperSpeciality().toLowerCase());
						parentSpecialities.add(esSpecialityDocument.getSpeciality().toLowerCase());
					}
					request.setSpecialitiesValue(specialities);
					request.setParentSpecialities(parentSpecialities);
				}
			}
		

			if (request.getServices() != null  && !request.getServices().isEmpty()) {
				Iterable<ESServicesDocument> iterableServices = esServicesRepository.findAllById(request.getServices());
				List<String> services = new ArrayList<>();
				if(iterableServices != null) {
					for(ESServicesDocument esServicesDocument : iterableServices) {
						services.add(esServicesDocument.getService().toLowerCase());
					}
					request.setServicesValue(services);
				}					
			}
			
			esDoctorRepository.save(request);
			transnationalService.addResource(new ObjectId(request.getUserId()), Resource.DOCTOR, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Doctor Details to ES : " + e.getMessage());
		}
		return response;
	}

	@Override
	public boolean addLocale(ESUserLocaleDocument request) {
		boolean response = false;
		try {
			if (request.getAddress() != null && request.getAddress().getLatitude() != null
					&& request.getAddress().getLongitude() != null)
				request.setGeoPoint(
						new GeoPoint(request.getAddress().getLatitude(), request.getAddress().getLongitude()));
			esUserLocaleRepository.save(request);
			transnationalService.addResource(new ObjectId(request.getLocaleId()), Resource.PHARMACY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Locale Details to ES : " + e.getMessage());
		}
		return response;
	}

	@Override
	public void editLocation(DoctorLocation doctorLocation) {
		try {
			Boolean isActivate;
			GeoPoint geoPoint = null;
			if (doctorLocation.getLatitude() != null && doctorLocation.getLongitude() != null)
				geoPoint = new GeoPoint(doctorLocation.getLatitude(), doctorLocation.getLongitude());
			ESLocationDocument esLocationDocument = new ESLocationDocument();
			BeanUtil.map(doctorLocation, esLocationDocument);
			esLocationDocument.setGeoPoint(geoPoint);
			esLocationDocument.setCity(doctorLocation.getCity());
			esLocationDocument.setId(doctorLocation.getLocationId());
			esLocationRepository.save(esLocationDocument);

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.filter((QueryBuilders.matchQuery("locationId", doctorLocation.getLocationId())));
			List<ESDoctorDocument> doctorDocuments = elasticsearchTemplate.queryForList(
					new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).withPageable(PageRequest.of(0, 100))

							.build(),
					ESDoctorDocument.class);
			System.out.println(doctorDocuments.size());
			for (ESDoctorDocument doctorDocument : doctorDocuments) {
				String id = doctorDocument.getId();
				isActivate = doctorDocument.getIsActivate();
				BeanUtil.map(doctorLocation, doctorDocument);
				if (doctorLocation.getAlternateClinicNumbers() != null
						&& !doctorLocation.getAlternateClinicNumbers().isEmpty()) {
					doctorDocument.setAlternateClinicNumbers(new ArrayList<String>());
					doctorDocument.setAlternateClinicNumbers(doctorLocation.getAlternateClinicNumbers());
				}
				if (doctorLocation.getClinicWorkingSchedules() != null
						&& !doctorLocation.getClinicWorkingSchedules().isEmpty()) {
					doctorDocument.setClinicWorkingSchedules(new ArrayList<WorkingSchedule>());
					doctorDocument.setClinicWorkingSchedules(doctorLocation.getClinicWorkingSchedules());
				}
				if (doctorLocation.getImages() != null && !doctorLocation.getImages().isEmpty()) {
					doctorDocument.setImages(new ArrayList<String>());
					doctorDocument.setImages(doctorLocation.getImages());
				}
				if (doctorLocation.getSpecialization() != null && !doctorLocation.getSpecialization().isEmpty()) {
					doctorDocument.setSpecialization(new ArrayList<String>());
					doctorDocument.setSpecialization(doctorLocation.getSpecialization());
				}

				doctorDocument.setGeoPoint(geoPoint);
				doctorDocument.setId(id);
				doctorDocument.setIsActivate(isActivate);
				esDoctorRepository.save(doctorDocument);
				transnationalService.addResource(new ObjectId(doctorLocation.getLocationId()), Resource.LOCATION, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while editing location " + e.getMessage());
		}
	}

	@Override
	public void addEditReference(ESReferenceDocument esReferenceDocument) {
		try {
			esReferenceRepository.save(esReferenceDocument);
			transnationalService.addResource(new ObjectId(esReferenceDocument.getId()), Resource.REFERENCE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while editing reference " + e.getMessage());
		}
	}

	@Override
	public void activateUser(String userId) {
		try {
			List<ESDoctorDocument> doctorDocument = esDoctorRepository.findByUserId(userId);
			if (doctorDocument != null) {
				UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
				for (ESDoctorDocument esDoctorDocument : doctorDocument) {
					esDoctorDocument.setIsActive(userCollection.getIsActive());
					esDoctorDocument.setIsVerified(userCollection.getIsVerified());
					esDoctorDocument.setUserState(userCollection.getUserState().getState());
					esDoctorRepository.save(esDoctorDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Doctor Details to ES : " + e.getMessage());
		}
	}

	@Override
	public boolean addCollectionBoy(ESCollectionBoyDocument request) {
		boolean response = false;
		try {
			esCollectionBoyRepository.save(request);
			transnationalService.addResource(new ObjectId(request.getId()), Resource.COLLECTION_BOY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Saving Locale Details to ES : " + e.getMessage());
		}
		return response;
	}

}
