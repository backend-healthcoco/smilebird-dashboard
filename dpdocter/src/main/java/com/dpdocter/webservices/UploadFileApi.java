package com.dpdocter.webservices;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.dpdocter.beans.UploadFile;
import com.dpdocter.beans.UploadFilePath;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.ImageUploadService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value = PathProxy.FILE_BASE_URL, description = "Endpoint for upload File ")
@RequestMapping(value=PathProxy.FILE_BASE_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

public class UploadFileApi {
	
	@Autowired
	private ImageUploadService imageUploadService;

	@PostMapping(value = PathProxy.FileBaseUrls.UPLOAD_FILE , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = PathProxy.FileBaseUrls.UPLOAD_FILE , notes = PathProxy.FileBaseUrls.UPLOAD_FILE)
	public Response<ImageURLResponse> saveImage(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "path")String path,@RequestParam(value = "createThumbnail",defaultValue = "false")Boolean createThumbnail
			,@RequestParam(value = "type", required = false)String type) {
		
		ImageURLResponse yoga = imageUploadService.uploadFile(file, path,createThumbnail,type);
		Response<ImageURLResponse> response = new Response<ImageURLResponse>();
		response.setData(yoga);
		return response;
	}
	
	@GetMapping(value = PathProxy.FileBaseUrls.GET_FILES,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = PathProxy.FileBaseUrls.GET_FILES, notes = PathProxy.FileBaseUrls.GET_FILES)
		public Response<UploadFile> getFiles(@RequestParam(required = false,value = "searchTerm")String searchTerm,@RequestParam(required = false,value = "type")String type,
				@RequestParam(required = false,value = "page",defaultValue = "0" )int page,@RequestParam(required = false,value = "size",defaultValue = "0" )int size) {
			
		 
		Response<UploadFile> response = new Response<UploadFile>();
		response.setDataList(imageUploadService.getFiles(searchTerm,type, page, size));
		return response;
	}
	
	@GetMapping(value = PathProxy.FileBaseUrls.GET_PATH,consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = PathProxy.FileBaseUrls.GET_PATH, notes = PathProxy.FileBaseUrls.GET_PATH)
		public Response<UploadFilePath> getPath(@RequestParam(required = false,value = "searchTerm")String searchTerm,@RequestParam(required = false,value = "type")String type,
				@RequestParam(required = false,value = "page",defaultValue = "0" )int page,@RequestParam(required = false,value = "size",defaultValue = "0" )int size) {
			
		 
		Response<UploadFilePath> response = new Response<UploadFilePath>();
		response.setDataList(imageUploadService.getFiles(searchTerm,type, page, size));
		return response;
	}
	
	
	@PostMapping(value = PathProxy.FileBaseUrls.UPLOAD_DATA)
	@ApiOperation(value = PathProxy.FileBaseUrls.UPLOAD_DATA , notes = PathProxy.FileBaseUrls.UPLOAD_DATA)
	public Response<Boolean> uploadDoctorData() {
		
		Boolean yoga = imageUploadService.uploadDoctorData();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(yoga);
		return response;
	}
	
	@GetMapping(value = PathProxy.FileBaseUrls.UPLOAD_NEW_DATA)
	@ApiOperation(value = PathProxy.FileBaseUrls.UPLOAD_NEW_DATA , notes = PathProxy.FileBaseUrls.UPLOAD_NEW_DATA)
	public Response<String> uploadDoctorNewData() {
		
		String yoga = imageUploadService.uploadDoctorNewData();
		Response<String> response = new Response<String>();
		response.setData(yoga);
		return response;
	}
	
	@PostMapping(value = PathProxy.FileBaseUrls.UPLOAD_CLINIC_DATA)
	@ApiOperation(value = PathProxy.FileBaseUrls.UPLOAD_CLINIC_DATA , notes = PathProxy.FileBaseUrls.UPLOAD_CLINIC_DATA)
	public Response<Boolean> uploadClinicData() {
		
		Boolean yoga = imageUploadService.uploadClinicData();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(yoga);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.FileBaseUrls.DOWNLOAD_DATA)
	@ApiOperation(value = PathProxy.FileBaseUrls.DOWNLOAD_DATA , notes = PathProxy.FileBaseUrls.DOWNLOAD_DATA)
	public Response<Boolean> downloadUserAppPatientData() {
		
		Boolean yoga = imageUploadService.downloadUserAppPatientData();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(yoga);
		return response;
	}
	
}
