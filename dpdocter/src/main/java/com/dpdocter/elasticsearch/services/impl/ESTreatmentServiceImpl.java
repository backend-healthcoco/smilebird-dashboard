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

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.PatientTreatmentService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESTreatmentServiceImpl implements ESTreatmentService {

	private static Logger logger = LogManager.getLogger(ESTreatmentServiceImpl.class.getName());

	@Autowired
	private ESTreatmentServiceRepository esTreatmentServiceRepository;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public void addEditService(ESTreatmentServiceDocument esTreatmentServiceDocument) {
		try {
			esTreatmentServiceRepository.save(esTreatmentServiceDocument);
			transactionalManagementService.addResource(new ObjectId(esTreatmentServiceDocument.getId()),
					Resource.TREATMENTSERVICE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Treatment Service in ES");
		}
	}

	@Override
	public List<?> search(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<?> response = new ArrayList<Object>();

		switch (PatientTreatmentService.valueOf(type.toUpperCase())) {

		case SERVICE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalTreatmentServices(page, size, doctorId, updatedTime, discarded, searchTerm);
				break;
			case CUSTOM:
				response = getCustomTreatmentServices(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
				break;
			case BOTH:
				response = getCustomGlobalTreatmentServices(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded, searchTerm);
				break;
			default:
				break;
			}
			break;
		}

		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getGlobalTreatmentServices(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<ESTreatmentServiceDocument> response = null;
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
						if (!DPDoctorUtils.anyStringEmpty(searchTerm))
							boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

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

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.TREATMENTSERVICE, page, 0, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private List<?> getCustomTreatmentServices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESTreatmentServiceDocument> response = null;
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
					updatedTime, discarded, "rankingCount", searchTerm, null, null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getCustomGlobalTreatmentServices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESTreatmentServiceDocument> response = null;
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
						if (!DPDoctorUtils.anyStringEmpty(searchTerm))
							boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

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
								specialities.add(null);
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.TREATMENTSERVICE, page, 0,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESTreatmentServiceDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public SearchQuery createCustomGlobalQuery(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> serviceIds, Boolean calculateCount, Class classForCount, String sortBy) {

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

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (serviceIds != null && !serviceIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("treatmentServiceId", serviceIds));

		if (calculateCount)
			size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
					classForCount);
		SearchQuery searchQuery = null;
		if (!DPDoctorUtils.anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}
		return searchQuery;
	}

	@SuppressWarnings("unchecked")
	public SearchQuery createCustomQuery(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTermFieldName, String searchTerm,
			Collection<String> serviceIds, Boolean calculateCount, Class classForCount, String sortBy) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.must(QueryBuilders.termQuery("doctorId", doctorId))
				.must(QueryBuilders.termQuery("locationId", locationId))
				.must(QueryBuilders.termQuery("hospitalId", hospitalId));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName, searchTerm));
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));
		if (serviceIds != null && !serviceIds.isEmpty())
			boolQueryBuilder.must(QueryBuilders.termsQuery("treatmentServiceId", serviceIds));
		if (calculateCount)
			size = (int) elasticsearchTemplate.count(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
					classForCount);
		SearchQuery searchQuery = null;
		if (!DPDoctorUtils.anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		}

		return searchQuery;
	}

}
