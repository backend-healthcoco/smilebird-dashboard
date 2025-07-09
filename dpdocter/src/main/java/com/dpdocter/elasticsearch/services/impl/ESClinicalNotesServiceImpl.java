package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosesRepository;
import com.dpdocter.elasticsearch.repository.ESDiagramsRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESECGDetailsRepository;
import com.dpdocter.elasticsearch.repository.ESEarsExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESEchoRepository;
import com.dpdocter.elasticsearch.repository.ESGeneralExamRepository;
import com.dpdocter.elasticsearch.repository.ESHolterRepository;
import com.dpdocter.elasticsearch.repository.ESIndicationOfUSGRepository;
import com.dpdocter.elasticsearch.repository.ESIndirectLarygoscopryExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESInvestigationsRepository;
import com.dpdocter.elasticsearch.repository.ESMenstrualHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESNeckExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESNoseExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESNotesRepository;
import com.dpdocter.elasticsearch.repository.ESObservationsRepository;
import com.dpdocter.elasticsearch.repository.ESObstetricHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESOralCavityThroatExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESPARepository;
import com.dpdocter.elasticsearch.repository.ESPSRepository;
import com.dpdocter.elasticsearch.repository.ESPVRepository;
import com.dpdocter.elasticsearch.repository.ESPresentComplaintHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESPresentComplaintRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintEarsRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintNoseRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintOralCavityRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintThroatRepository;
import com.dpdocter.elasticsearch.repository.ESProcedureNoteRepository;
import com.dpdocter.elasticsearch.repository.ESProvisionalDiagnosisRepository;
import com.dpdocter.elasticsearch.repository.ESSystemExamRepository;
import com.dpdocter.elasticsearch.repository.ESXRayDetailsRepository;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESClinicalNotesServiceImpl implements ESClinicalNotesService {

	private static Logger logger = LogManager.getLogger(ESClinicalNotesServiceImpl.class.getName());

	@Autowired
	private ESComplaintsRepository esComplaintsRepository;

	@Autowired
	private ESDiagnosesRepository esDiagnosesRepository;

	@Autowired
	private ESNotesRepository esNotesRepository;

	@Autowired
	private ESDiagramsRepository esDiagramsRepository;

	@Autowired
	private ESInvestigationsRepository esInvestigationsRepository;

	@Autowired
	private ESObservationsRepository esObservationsRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESProcedureNoteRepository esProcedureNoteRepository;

	@Autowired
	private ESPresentComplaintRepository esPresentComplaintRepository;

	@Autowired
	private ESPresentComplaintHistoryRepository esPresentComplaintHistoryRepository;

	@Autowired
	private ESProvisionalDiagnosisRepository esProvisionalDiagnosisRepository;

	@Autowired
	private ESSystemExamRepository esSystemExamRepository;

	@Autowired
	private ESGeneralExamRepository esGeneralExamRepository;

	@Autowired
	private ESMenstrualHistoryRepository esMenstrualHistoryRepository;

	@Autowired
	private ESObstetricHistoryRepository esObstetricHistoryRepository;

	@Autowired
	private ESIndicationOfUSGRepository esIndicationOfUSGRepository;

	@Autowired
	private ESPARepository espaRepository;

	@Autowired
	private ESPSRepository espsRepository;

	@Autowired
	private ESPVRepository espvRepository;

	@Autowired
	private ESXRayDetailsRepository esxRayDetailsRepository;

	@Autowired
	private ESECGDetailsRepository esecgDetailsRepository;

	@Autowired
	private ESEchoRepository esEchoRepository;

	@Autowired
	private ESHolterRepository esholterRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	private ESPresentingComplaintNoseRepository esPresentingComplaintNoseRepository;
	
	@Autowired
	private ESPresentingComplaintEarsRepository esPresentingComplaintEarsRepository;
	
	@Autowired
	private ESPresentingComplaintThroatRepository esPresentingComplaintThroatRepository;
	
	@Autowired
	private ESPresentingComplaintOralCavityRepository esPresentingComplaintOralCavityRepository;
	
	@Autowired
	private ESNeckExaminationRepository esNeckExaminationRepository;
	
	@Autowired
	private ESNoseExaminationRepository esNoseExaminationRepository;
	
	@Autowired
	private ESOralCavityThroatExaminationRepository esOralCavityThroatExaminationRepository;
	
	@Autowired
	private ESEarsExaminationRepository esEarsExaminationRepository;
	
	@Autowired
	private ESIndirectLarygoscopryExaminationRepository esIndirectLarygoscopryExaminationRepository;
	

	@Override
	public boolean addComplaints(ESComplaintsDocument request) {
		boolean response = false;
		try {
			esComplaintsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.COMPLAINT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Complaints");
		}
		return response;
	}

	@Override
	public boolean addDiagnoses(ESDiagnosesDocument request) {
		boolean response = false;
		try {
			esDiagnosesRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.DIAGNOSIS, true);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Diagnosis");
		}
		return response;
	}

	@Override
	public boolean addNotes(ESNotesDocument request) {
		boolean response = false;
		try {
			esNotesRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NOTES, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Notes");
		}
		return response;
	}

	@Override
	public boolean addDiagrams(ESDiagramsDocument request) {
		boolean response = false;
		try {
			esDiagramsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.DIAGRAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Diagrams");
		}
		return response;
	}

	@Override
	public boolean addInvestigations(ESInvestigationsDocument request) {
		boolean response = false;
		try {
			esInvestigationsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INVESTIGATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;
	}

	@Override
	public boolean addObservations(ESObservationsDocument request) {
		boolean response = false;
		try {
			esObservationsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.OBSERVATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Observations");
		}
		return response;
	}

	@Override
	public List<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESObservationsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalObservations(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		}

		return response;
	}

	@Override
	public List<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESInvestigationsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		}

		return response;
	}

	@Override
	public List<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagramsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		}
		return response;
	}

	@Override
	public List<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESNotesDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalNotes(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		}
		return response;
	}

	@Override
	public List<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagnosesDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		}
		return response;
	}

	@Override
	public List<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESComplaintsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESComplaintsDocument> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESComplaintsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.COMPLAINT, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, "complaint");
			response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ESComplaintsDocument> getGlobalComplaints(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESComplaintsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.COMPLAINT, page, size, updatedTime,
					discarded, null, searchTerm, specialities, "complaint");
			response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private List<ESComplaintsDocument> getCustomComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESComplaintsDocument> response = null;
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
					updatedTime, discarded, null, searchTerm, "complaint");
			response = elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESDiagramsDocument> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagramsDocument> response = null;

		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				boolQueryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")))
						.should(QueryBuilders.termQuery("doctorId", doctorId)).minimumShouldMatch(1));

			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				boolQueryBuilder
						.must(QueryBuilders.boolQuery().should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")))
								.should(QueryBuilders.termQuery("locationId", locationId)).minimumShouldMatch(1))
						.must(QueryBuilders.boolQuery().should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")))
								.should(QueryBuilders.termQuery("hospitalId", hospitalId)).minimumShouldMatch(1));
			}
			
			if (specialities != null && !specialities.isEmpty())
				boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
			if (!discarded)
				boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<ESDiagramsDocument> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESDiagramsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				esDoctorRepository.findByUserId(doctorId);
			Collection<String> specialities = Collections.EMPTY_LIST;
			if (doctorCollections != null && !doctorCollections.isEmpty()) {
				Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
						new BeanToPropertyValueTransformer("specialities"));
				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
						.must(QueryBuilders.termsQuery("id", specialitiesId));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm))
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

				int count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESSpecialityDocument.class);
				if (count > 0) {
					SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withPageable(PageRequest.of(0, count)).build();

					List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
							ESSpecialityDocument.class);
					if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
						specialities = CollectionUtils.collect(resultsSpeciality,
								new BeanToPropertyValueTransformer("speciality"));
						specialities.add("ALL");
						specialities.add(null);
					}
				}
			}

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
					.mustNot(QueryBuilders.existsQuery("doctorId")).mustNot(QueryBuilders.existsQuery("locationId"))
					.mustNot(QueryBuilders.existsQuery("hospitalId"));

			if (specialities != null && !specialities.isEmpty())
				boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
			if (!discarded)
				boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<ESDiagramsDocument> getCustomDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagramsDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESDiagramsDocument>();
			else {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
						.must(QueryBuilders.termQuery("doctorId", doctorId));

				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					boolQueryBuilder.must(QueryBuilders.termQuery("locationId", locationId))
							.must(QueryBuilders.termQuery("hospitalId", hospitalId));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm))
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
				if (!discarded)
					boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

				SearchQuery searchQuery = null;
				if (size > 0)
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
				else
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

				response = elasticsearchTemplate.queryForList(searchQuery, ESDiagramsDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESInvestigationsDocument> getCustomGlobalInvestigations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESInvestigationsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.INVESTIGATION, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities,
					"investigation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESInvestigationsDocument> getGlobalInvestigations(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<ESInvestigationsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.INVESTIGATION, page, size, updatedTime,
					discarded, null, searchTerm, specialities, "investigation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<ESInvestigationsDocument> getCustomInvestigations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESInvestigationsDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESInvestigationsDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, "investigation");
				response = elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESObservationsDocument> getCustomGlobalObservations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESObservationsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.OBSERVATION, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, "observation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ESObservationsDocument> getGlobalObservations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESObservationsDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.OBSERVATION, page, size, updatedTime,
					discarded, null, searchTerm, specialities, "observation");
			response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<ESObservationsDocument> getCustomObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESObservationsDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESObservationsDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, "observation");
				response = elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESDiagnosesDocument> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagnosesDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DIAGNOSIS, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, "diagnosis");
			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESDiagnosesDocument> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESDiagnosesDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DIAGNOSIS, page, size, updatedTime,
					discarded, null, searchTerm, specialities, "diagnosis");
			response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<ESDiagnosesDocument> getCustomDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESDiagnosesDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESDiagnosesDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, "diagnosis");
				response = elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESNotesDocument> getCustomGlobalNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {

		List<ESNotesDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.NOTES, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, "note");
			response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ESNotesDocument> getGlobalNotes(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESNotesDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId);
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					Collection<String> specialitiesId = CollectionUtils.collect(doctorCollections,
							new BeanToPropertyValueTransformer("specialities"));
					BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
							.must(QueryBuilders.termsQuery("id", specialitiesId));
					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
						boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

					int count = (int) elasticsearchTemplate.count(
							new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
							ESSpecialityDocument.class);
					if (count > 0) {
						SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
								.withPageable(PageRequest.of(0, count)).build();
						List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate.queryForList(searchQuery,
								ESSpecialityDocument.class);
						if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
							specialities = CollectionUtils.collect(resultsSpeciality,
									new BeanToPropertyValueTransformer("speciality"));
							specialities.add("ALL");
							specialities.add(null);
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.NOTES, page, size, updatedTime,
					discarded, null, searchTerm, specialities, "note");
			response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<ESNotesDocument> getCustomNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESNotesDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESNotesDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, "note");
				response = elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	@Override
	public boolean addPresentComplaint(ESPresentComplaintDocument request) {
		boolean response = false;
		try {
			esPresentComplaintRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PRESENT_COMPLAINT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Present Complaint");
		}
		return response;
	}

	@Override
	public boolean addPresentComplaintHistory(ESPresentComplaintHistoryDocument request) {
		boolean response = false;
		try {
			esPresentComplaintHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.HISTORY_OF_PRESENT_COMPLAINT,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Present Complaint History");
		}
		return response;

	}

	@Override
	public boolean addProvisionalDiagnosis(ESProvisionalDiagnosisDocument request) {
		boolean response = false;
		try {
			esProvisionalDiagnosisRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PROVISIONAL_DIAGNOSIS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Provisional Diagnosis");
		}
		return response;

	}

	@Override
	public boolean addSystemExam(ESSystemExamDocument request) {
		boolean response = false;
		try {
			esSystemExamRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.SYSTEMIC_EXAMINATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addGeneralExam(ESGeneralExamDocument request) {
		boolean response = false;
		try {
			esGeneralExamRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.GENERAL_EXAMINATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addMenstrualHistory(ESMenstrualHistoryDocument request) {
		boolean response = false;
		try {
			esMenstrualHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.MENSTRUAL_HISTORY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addObstetricsHistory(ESObstetricHistoryDocument request) {
		boolean response = false;
		try {
			esObstetricHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.OBSTETRIC_HISTORY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addPA(ESPADocument request) {
		boolean response = false;
		try {
			espaRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PA, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/A");
		}
		return response;

	}

	@Override
	public boolean addIndicationOfUSG(ESIndicationOfUSGDocument request) {
		boolean response = false;
		try {
			esIndicationOfUSGRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INDICATION_OF_USG, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Indication of USG");
		}
		return response;

	}

	@Override
	public boolean addPV(ESPVDocument request) {
		boolean response = false;
		try {
			espvRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PV, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/V");
		}
		return response;

	}

	@Override
	public boolean addPS(ESPSDocument request) {
		boolean response = false;
		try {
			espsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/S");
		}
		return response;

	}

	@Override
	public boolean addXRayDetails(ESXRayDetailsDocument request) {
		boolean response = false;
		try {
			esxRayDetailsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.XRAY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving X ray details");
		}
		return response;

	}

	@Override
	public boolean addECGDetails(ESECGDetailsDocument request) {
		boolean response = false;
		try {
			esecgDetailsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ECG, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving ECG details");
		}
		return response;

	}

	@Override
	public boolean addEcho(ESEchoDocument request) {
		boolean response = false;
		try {
			esEchoRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ECHO, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Echo details");
		}
		return response;

	}

	@Override
	public boolean addHolter(ESHolterDocument request) {
		boolean response = false;
		try {
			esholterRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.HOLTER, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Holter");
		}
		return response;

	}

	@Override
	public boolean addProcedureNote(ESProcedureNoteDocument request) {
		boolean response = false;
		try {
			esProcedureNoteRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PROCEDURE_NOTE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/V");
		}
		return response;

	}

	@Override
	public List<ESProcedureNoteDocument> searchProcedureNote(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESProcedureNoteDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalProcedureNote(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESProcedureNoteDocument> getGlobalProcedureNote(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<ESProcedureNoteDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PROCEDURE_NOTE, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "procedureNote");
			response = elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	private List<ESProcedureNoteDocument> getCustomProcedureNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESProcedureNoteDocument> response = null;
		try {
			if (doctorId == null)
				response = new ArrayList<ESProcedureNoteDocument>();
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "procedureNote");
				response = elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ProcedureNote Document");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ESProcedureNoteDocument> getCustomGlobalProcedureNote(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESProcedureNoteDocument> response = null;
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PROCEDURE_NOTE, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "procedureNote");
			response = elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;

	}
	
	@Override
	public boolean addPCNose(ESPresentingComplaintNoseDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintNoseRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_NOSE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addPCEars(ESPresentingComplaintEarsDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintEarsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_EARS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addPCThroat(ESPresentingComplaintThroatDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintThroatRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_THROAT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	
	@Override
	public boolean addPCOralCavity(ESPresentingComplaintOralCavityDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintOralCavityRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_ORAL_CAVITY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addNeckExam(ESNeckExaminationDocument request) {
		boolean response = false;
		try {
			esNeckExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NECK_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addNoseExam(ESNoseExaminationDocument request) {
		boolean response = false;
		try {
			esNoseExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NOSE_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addEarsExam(ESEarsExaminationDocument request) {
		boolean response = false;
		try {
			esEarsExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.EARS_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addOralCavityThroatExam(ESOralCavityAndThroatExaminationDocument request) {
		boolean response = false;
		try {
			esOralCavityThroatExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ORAL_CAVITY_THROAT_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}
	
	@Override
	public boolean addIndirectLarygoscopyExam(ESIndirectLarygoscopyExaminationDocument request) {
		boolean response = false;
		try {
			esIndirectLarygoscopryExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INDIRECT_LARYGOSCOPY_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	// @Override
	// public List<ESDiagramsDocument> searchDiagramsBySpeciality(String
	// searchTerm) {
	// List<ESDiagramsDocument> response = null;
	// try {
	// response = esDiagramsRepository.findBySpeciality(searchTerm);
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Searching Diagrams");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Searching Diagrams");
	// }
	// return response;
	// }
}
