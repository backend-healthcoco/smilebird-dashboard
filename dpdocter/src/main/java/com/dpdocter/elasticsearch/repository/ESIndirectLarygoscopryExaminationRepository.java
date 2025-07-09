package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;

public interface ESIndirectLarygoscopryExaminationRepository extends ElasticsearchRepository<ESIndirectLarygoscopyExaminationDocument, String> {

}
