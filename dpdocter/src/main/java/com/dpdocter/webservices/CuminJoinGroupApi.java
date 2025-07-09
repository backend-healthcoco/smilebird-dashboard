package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.collections.CuminGroupCollection;
import com.dpdocter.repository.CuminRepository;
import com.dpdocter.services.CuminService;
import io.swagger.annotations.Api;


@RestController
@Api(value = PathProxy.CUMIN_BASE_URL, description = "Endpoint for Country")
@RequestMapping(value = PathProxy.CUMIN_BASE_URL)
public class CuminJoinGroupApi {
		
	@Autowired
	CuminRepository cuminRepository;

	
	
	@GetMapping(value = PathProxy.CuminUrls.JOIN_GROUP)
	void handleFoo(HttpServletResponse response) throws IOException {
		
		List<CuminGroupCollection> cuminGroupCollection = cuminRepository.findAll();

		System.out.println(cuminGroupCollection.get(0).getLink());
		System.out.println("api hit...");
		response.setContentType("application/json");
	    response.addHeader("Content-Type", "application/json");
	    response.addHeader("Accept", "application/json");
	    response.sendRedirect(cuminGroupCollection.get(0).getLink());
//        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://chat.whatsapp.com/LRIq3HrIeNRDv91aRKChRh")).build();

	  }

}
