package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;

public interface ESECGDetailsRepository extends ElasticsearchRepository<ESECGDetailsDocument, String>{

}
