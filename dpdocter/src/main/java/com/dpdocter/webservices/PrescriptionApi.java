package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.LabTest;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.DrugInformationResponse;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.TransactionalManagementService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PRESCRIPTION_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRESCRIPTION_BASE_URL, description = "Endpoint for prescription")
public class PrescriptionApi {

	private static Logger logger = LogManager.getLogger(PrescriptionApi.class.getName());

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_LAB_TEST, notes = PathProxy.PrescriptionUrls.ADD_LAB_TEST)
	public Response<LabTest> addLabTest(@RequestBody LabTest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId())
				|| request.getTest() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LabTest labTestResponse = prescriptionServices.addLabTest(request);
		transnationalService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_LAB_TEST, notes = PathProxy.PrescriptionUrls.EDIT_LAB_TEST)
	public Response<LabTest> editLabTest(@PathVariable(value = "labTestId") String labTestId, LabTest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(labTestId, request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(labTestId);
		LabTest labTestResponse = prescriptionServices.editLabTest(request);
		transnationalService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_LAB_TEST, notes = PathProxy.PrescriptionUrls.DELETE_LAB_TEST)
	public Response<LabTest> deleteLabTest(@PathVariable(value = "labTestId") String labTestId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(labTestId, hospitalId, locationId)) {
			logger.warn("Lab Test Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Lab Test Id, Hospital Id, Location Id Cannot Be Empty");
		}
		LabTest labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, hospitalId, locationId,
				discarded);
		transnationalService.addResource(new ObjectId(labTestDeleteResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestDeleteResponse, esLabTestDocument);
		if (labTestDeleteResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestDeleteResponse.getTest().getId());
		if (esLabTestDocument.getId() != null)
			transnationalService.checkLabTest(new ObjectId(esLabTestDocument.getId()));
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestDeleteResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.PrescriptionUrls.UPLOAD_DIAGNOSTIC_TEST,consumes = { MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPLOAD_DIAGNOSTIC_TEST, notes = PathProxy.PrescriptionUrls.UPLOAD_DIAGNOSTIC_TEST)
	public Response<Boolean> uploadDiagnosticTest(@FormDataParam("file") FormDataBodyPart file) {

		Boolean codeWithReaction = prescriptionServices.uploadDiagnosticTest(file);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(codeWithReaction);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGS, notes = PathProxy.PrescriptionUrls.UPLOAD_DRUGS)
	public Response<Boolean> uploadDrugs() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.uploadDrugs();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGSDETAILS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGSDETAILS, notes = PathProxy.PrescriptionUrls.UPLOAD_DRUGSDETAILS)
	public Response<Boolean> uploadDrugDtail() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.uploadDrugDetail();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_DRUGSDETAILS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DRUGSDETAILS, notes = PathProxy.PrescriptionUrls.GET_DRUGSDETAILS)
	public Response<Object> getDrugDtail(@RequestParam(required = false, value = "page", defaultValue="0") int page,
			@RequestParam(required = false, value = "size", defaultValue="0") int size,
//			@RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "state") String state,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		
		Response<Object> response = prescriptionServices.getDrugDtail(page,size,searchTerm);
		return response;
	}
}

	
