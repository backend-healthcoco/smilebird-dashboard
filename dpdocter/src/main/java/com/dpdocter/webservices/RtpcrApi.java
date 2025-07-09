package com.dpdocter.webservices;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.RtpcrFileRequest;

import com.dpdocter.beans.RtpcrFileResponse;

import com.dpdocter.beans.RtpcrTest;
import com.dpdocter.beans.RtpcrTestResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.RtpcrTestService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.RTPCR_BASE_URL,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.RTPCR_BASE_URL, description = "")
public class RtpcrApi {

	private static Logger logger = LogManager.getLogger(RtpcrApi.class.getName());
	
	@Autowired
	private RtpcrTestService rtpcrService;
	
	@Value(value = "${image.path}")
	private String imagePath;
	
	@PostMapping(value = PathProxy.RTPCRUrls.ADD_EDIT_RTPCR_TEST)
	@ApiOperation(value = PathProxy.RTPCRUrls.ADD_EDIT_RTPCR_TEST, notes = PathProxy.RTPCRUrls.ADD_EDIT_RTPCR_TEST)
	public Response<RtpcrTest> addEditRtpcr(@RequestBody RtpcrTest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		RtpcrTest resume = rtpcrService.addEditTest(request);
	
		Response<RtpcrTest> response = new Response<RtpcrTest>();
		response.setData(resume);
		return response;
	}
	
	
	@GetMapping(value=PathProxy.RTPCRUrls.GET_RTPCR_TEST)
	@ApiOperation(value = PathProxy.RTPCRUrls.GET_RTPCR_TEST, notes = PathProxy.RTPCRUrls.GET_RTPCR_TEST)
	public Response<RtpcrTestResponse> getRtpcrTests(@RequestParam(required = false,name = "page",defaultValue = "0")int page,
			@RequestParam(required = false,name = "size",defaultValue = "0")int size,
			@RequestParam(required = false,name = "searchTerm")String searchTerm,
			@RequestParam(required = false,name = "discarded")Boolean discarded,
			@RequestParam(required = false,name = "fromDate")String fromDate,
			@RequestParam(required = false,name = "toDate")String toDate,
			@RequestParam(required = false,name = "collectionBoy")String collectionBoy) {
		

		RtpcrTestResponse rtpcr = rtpcrService.getTest(page, size, searchTerm,fromDate,toDate,collectionBoy,discarded);
	
		Response<RtpcrTestResponse> response = new Response<RtpcrTestResponse>();
		response.setData(rtpcr);
		return response;
	}
	
	@PostMapping(value = PathProxy.RTPCRUrls.UPLOAD_RTPCR_IMAGE)
	@ApiOperation(value = PathProxy.RTPCRUrls.UPLOAD_RTPCR_IMAGE, notes = PathProxy.RTPCRUrls.UPLOAD_RTPCR_IMAGE)
	public Response<RtpcrFileResponse> uploadRtpcrImage(@RequestBody RtpcrFileRequest request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}


		RtpcrFileResponse resume = rtpcrService.uploadRtpcrImage(request);
		Response<RtpcrFileResponse> response = new Response<RtpcrFileResponse>();
		response.setData(resume);
		return response;
	}
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}
	
	@DeleteMapping(value = PathProxy.RTPCRUrls.DISCARD_RTPCR_TEST)
	@ApiOperation(value = PathProxy.RTPCRUrls.DISCARD_RTPCR_TEST, notes = PathProxy.RTPCRUrls.DISCARD_RTPCR_TEST)
	public Response<Boolean> uploadRtpcrImage(@PathVariable("id") String id , @RequestParam(required = false, value ="discarded") Boolean discarded ) {
		if(DPDoctorUtils.allStringsEmpty(id))
		{
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input. Id cannot be null");
		}
		
		Boolean status = rtpcrService.discardOrder(id, discarded);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}
	


}
