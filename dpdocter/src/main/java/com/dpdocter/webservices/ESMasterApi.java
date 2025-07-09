package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.EducationInstitute;
import com.dpdocter.beans.EducationQualification;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.ProfessionalMembership;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Speciality;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.response.DiseaseListResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_MASTER_BASE_URL, produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_MASTER_BASE_URL, description = "Endpoint for solr master")
public class ESMasterApi {

	private static Logger logger = LogManager.getLogger(ESMasterApi.class.getName());

	@Autowired
	ESMasterService esMasterService;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private CityRepository cityRepository;

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_REFERENCE, notes = PathProxy.SolrMasterUrls.SEARCH_REFERENCE)
	public Response<Reference> searchReference(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<Reference> searchResonse = esMasterService.searchReference(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<Reference> response = new Response<Reference>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_DISEASE, notes = PathProxy.SolrMasterUrls.SEARCH_DISEASE)
	public Response<DiseaseListResponse> searchDisease(@PathVariable("range") String range, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value ="doctorId") String doctorId,
			@RequestParam(required = false, value ="locationId") String locationId, @RequestParam(required = false, value ="hospitalId") String hospitalId,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0") String updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<DiseaseListResponse> searchResonse = esMasterService.searchDisease(range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, searchTerm);
		Response<DiseaseListResponse> response = new Response<DiseaseListResponse>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP, notes = PathProxy.SolrMasterUrls.SEARCH_BLOOD_GROUP)
	public Response<BloodGroup> searchBloodGroup() {

		List<BloodGroup> searchResonse = esMasterService.searchBloodGroup();
		Response<BloodGroup> response = new Response<BloodGroup>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = "add")
	public Response<Boolean> add() {

		Boolean searchResonse = esMasterService.add();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(searchResonse);
		return response;
	}

	@GetMapping(value = "addCity")
	public Response<Boolean> addCity() {

		List<CityCollection> cityCollections = cityRepository.findAll();
		for (CityCollection cityCollection : cityCollections) {
			ESCityDocument esCityDocument = new ESCityDocument();
			BeanUtil.map(cityCollection, esCityDocument);
			esCityDocument.setGeoPoint(new GeoPoint(cityCollection.getLatitude(), cityCollection.getLongitude()));
			esCityService.addCities(esCityDocument);
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSION, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSION)
	public Response<Profession> searchProfession(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<Profession> searchResonse = esMasterService.searchProfession(searchTerm, updatedTime, page, size);
		Response<Profession> response = new Response<Profession>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP, notes = PathProxy.SolrMasterUrls.SEARCH_PROFESSIONAL_MEMBERSHIP)
	public Response<ProfessionalMembership> searchProfessionalMembership(
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<ProfessionalMembership> searchResonse = esMasterService.searchProfessionalMembership(searchTerm,
				updatedTime, page, size);
		Response<ProfessionalMembership> response = new Response<ProfessionalMembership>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_INSTITUTE)
	public Response<EducationInstitute> searchEducationInstitute(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<EducationInstitute> searchResonse = esMasterService.searchEducationInstitute(searchTerm, updatedTime, page,
				size);
		Response<EducationInstitute> response = new Response<EducationInstitute>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION, notes = PathProxy.SolrMasterUrls.SEARCH_EDUCATION_QUALIFICATION)
	public Response<EducationQualification> searchEducationQualification(
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<EducationQualification> searchResonse = esMasterService.searchEducationQualification(searchTerm,
				updatedTime, page, size);
		Response<EducationQualification> response = new Response<EducationQualification>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL, notes = PathProxy.SolrMasterUrls.SEARCH_MEDICAL_COUNCIL)
	public Response<MedicalCouncil> searchMedicalCouncil(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<MedicalCouncil> searchResonse = esMasterService.searchMedicalCouncil(searchTerm, updatedTime, page, size);
		Response<MedicalCouncil> response = new Response<MedicalCouncil>();
		response.setDataList(searchResonse);
		return response;
	}

	@GetMapping(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
	@ApiOperation(value = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY, notes = PathProxy.SolrMasterUrls.SEARCH_SPECIALITY)
	public Response<Speciality> searchSpeciality(@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime, @RequestParam(required = false, value ="page", defaultValue = "0") int page,
			@RequestParam(required = false, value ="size", defaultValue = "0") int size) {

		List<Speciality> searchResonse = esMasterService.searchSpeciality(searchTerm, updatedTime, page, size);
		Response<Speciality> response = new Response<Speciality>();
		response.setDataList(searchResonse);
		return response;
	}
}
