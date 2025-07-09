package com.dpdocter.webservices;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmFacility;
import com.dpdocter.services.NdhmServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.NDHM_BASE_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.NDHM_BASE_URL, description = "Endpoint for ndhm")
public class NdhmApi {

	private static Logger logger = LogManager.getLogger(NdhmApi.class.getName());

	@Autowired
	private NdhmServices ndhmService;

	@GetMapping(value = PathProxy.NdhmUrls.GET_LIST_STATES)
	@ApiOperation(value = PathProxy.NdhmUrls.GET_LIST_STATES, notes = PathProxy.NdhmUrls.GET_LIST_STATES)
	public Response<NDHMStates> getLoginPin() {

		List<NDHMStates> ndhmOauth = ndhmService.getListforStates();
		Response<NDHMStates> response = new Response<NDHMStates>();
		response.setDataList(ndhmOauth);
		return response;
	}

	@PostMapping(value = PathProxy.NdhmUrls.ADD_NDHM_FACILITY)
	@ApiOperation(value = PathProxy.NdhmUrls.ADD_NDHM_FACILITY, notes = PathProxy.NdhmUrls.ADD_NDHM_FACILITY)
	public Response<Boolean> registerFacility(@RequestBody NdhmFacility request) {

		Boolean ndhmOauth = ndhmService.registerFacility(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(ndhmOauth);
		return response;
	}

	@PostMapping(value = PathProxy.NdhmUrls.NOTIFY)
	@ApiOperation(value = PathProxy.NdhmUrls.NOTIFY, notes = PathProxy.NdhmUrls.NOTIFY)
	public Response<Boolean> notify(@RequestBody String request, @RequestHeader HttpHeaders headers)
			throws JsonParseException, JsonMappingException, IOException {

		System.out.println("notify request" + request);
		System.out.println("notify headers" + headers);

		ObjectMapper mapper = new ObjectMapper();
//		NotifyRequest request1 = mapper.readValue(request, NotifyRequest.class);
//		Boolean mobile = ndhmService.ndhmNotify(request1);
		Response<Boolean> response = new Response<Boolean>();
//		response.setData();
		return response;
	}

}
