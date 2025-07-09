package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESPVDocument;

public interface ESPVRepository extends ElasticsearchRepository<ESPVDocument, String> {

}
