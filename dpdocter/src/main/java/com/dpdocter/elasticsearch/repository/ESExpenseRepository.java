package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;

public interface ESExpenseRepository extends ElasticsearchRepository<ESExpenseTypeDocument, String> {

}
