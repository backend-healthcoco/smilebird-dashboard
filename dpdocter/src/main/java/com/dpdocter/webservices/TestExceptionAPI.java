package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.dpdocter.elasticsearch.document.ESProfessionDocument;
import com.dpdocter.elasticsearch.repository.ESProfessionRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;


import common.util.web.Response;

//@RestController
//@RequestMapping(value="testing", produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
//public class TestExceptionAPI {
//
//	@Autowired
//    ESProfessionRepository esProfessionRepository;
//	
//	@GetMapping("/exception/{id}")
//    public String exceptionTest(@PathVariable("id") String id) throws BusinessException {
//	throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
//    }
//    
//	@Autowired
//    TestRepository testRepository;
//    
//    @Autowired
//    MongoTemplate mongoTemplate;
//    
//    @Autowired
//    CryptVault cryptVault;
//    
//    @GET
//    @Path("/get")
//    public Response<TestCollection> get(@PathParam("id") String id) {
//	
//    	TestCollection testCollection = new TestCollection();
//    	testCollection.setId(null);
//    	testCollection.setAccNumber(cryptVault.encrypt("123456789012".getBytes()).toString());
//    	testCollection = mongoTemplate.save(testCollection);
//    	
//    	List<TestCollection> tests = testRepository.findAll();
//    	System.out.println(tests);
//    	if(tests.size()>0) {
//    		System.out.println(tests.get(0).getAccNumber());
//    		System.out.println(cryptVault.decrypt(tests.get(0).getAccNumber().getBytes()));
//    	}
//    	Response<TestCollection> response = new Response<TestCollection>();
//    	response.setDataList(tests);
//    	return response;
//    }
//}
