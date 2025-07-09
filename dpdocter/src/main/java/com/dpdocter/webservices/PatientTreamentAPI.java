package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ProductAndService;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.TreatmentRatelistRequest;
import com.dpdocter.response.TreatmentRatelistResponse;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.PATIENT_TREATMENT_BASE_URL, produces = MediaType.APPLICATION_JSON ,  consumes = MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PATIENT_TREATMENT_BASE_URL, description = "Endpoint for patient treatment")
public class PatientTreamentAPI {

	private static Logger logger = LogManager.getLogger(PatientTreamentAPI.class.getName());
	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESTreatmentService esTreatmentService;

	@Value(value = "${invalid.input}")
	private String invalidInput;

	@PostMapping(PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_SERVICE)
	public Response<TreatmentService> addEditService(@RequestBody TreatmentService request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getName())) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}
		TreatmentService treatmentService = patientTreatmentServices.addEditService(request);
		transactionalManagementService.addResource(new ObjectId(treatmentService.getId()), Resource.TREATMENTSERVICE,
				false);
		ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
		BeanUtil.map(treatmentService, esTreatmentServiceDocument);
		esTreatmentService.addEditService(esTreatmentServiceDocument);

		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(treatmentService);
		return response;
	}

	@DeleteMapping(PathProxy.PatientTreatmentURLs.DELETE_SERVICE)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_SERVICE, notes = PathProxy.PatientTreatmentURLs.DELETE_SERVICE)
	public Response<TreatmentService> deleteService(@PathVariable(value = "treatmentServiceId") String treatmentServiceId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(treatmentServiceId)) {
			logger.warn("Treatment Service Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Treatment Service Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		TreatmentService treatmentService = patientTreatmentServices.deleteService(treatmentServiceId, doctorId,
				locationId, hospitalId, discarded);
		if (treatmentService != null) {
			transactionalManagementService.addResource(new ObjectId(treatmentService.getId()),
					Resource.TREATMENTSERVICE, false);
			ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
			BeanUtil.map(treatmentService, esTreatmentServiceDocument);
			esTreatmentService.addEditService(esTreatmentServiceDocument);
		}
		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setData(treatmentService);
		return response;
	}

	@GetMapping(PathProxy.PatientTreatmentURLs.GET_SERVICES)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_SERVICES, notes = PathProxy.PatientTreatmentURLs.GET_SERVICES)
	public Response<Object> getServices(@PathVariable("type") String type, @PathVariable("range") String range,
			@RequestParam(required = false, value ="page", defaultValue = "0") int page, @RequestParam(required = false, value ="size", defaultValue = "0") int size, @RequestParam(required = false, value = "doctorId") String doctorId,
			@RequestParam(required = false, value = "locationId") String locationId, @RequestParam(required = false, value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value = "ratelistId") String ratelistId,@RequestParam(required = false, value ="updatedTime", defaultValue = "0")  String updatedTime,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm, @RequestParam(required = false, value = "category") String category,
			@RequestParam(required = false, value = "disease") String disease, @RequestParam(required = false, value = "speciality") String speciality) {

		if (DPDoctorUtils.anyStringEmpty(type, range)) {
			logger.warn(invalidInput);
			throw new BusinessException(ServiceError.InvalidInput, invalidInput);
		}

		List<?> objects = patientTreatmentServices.getServices(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, true, searchTerm, category, disease, speciality,ratelistId);

		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
		return response;
	}

	@PostMapping(PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE)
	public Response<Boolean> addEditProductService(@RequestBody ProductAndService productAndService) {
		if (productAndService == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Empty");
		}
		boolean addEditProductServiceResponse = patientTreatmentServices.addEditProductService(productAndService);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditProductServiceResponse);
		return response;
	}

	@PostMapping(PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_PRODUCT_SERVICE_COST)
	public Response<Boolean> addEditProductServiceCost(@RequestBody ProductAndService productAndService) {
		if (productAndService == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Request Cannot Be Empty");
		} else if (DPDoctorUtils.anyStringEmpty(productAndService.getLocationId(), productAndService.getHospitalId(),
				productAndService.getDoctorId())) {
			throw new BusinessException(ServiceError.InvalidInput,
					"LocationId, HospitalId and DoctorId cannot be empty");
		}
		boolean addEditProductServiceResponse = patientTreatmentServices.addEditProductServiceCost(productAndService);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addEditProductServiceResponse);
		return response;
	}

	@GetMapping(PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES, notes = PathProxy.PatientTreatmentURLs.GET_PRODUCTS_AND_SERVICES)
	public Response<ProductAndService> getProductsAndServices(@RequestParam(required = false, value ="locationId") String locationId,
			@RequestParam(required = false, value ="hospitalId") String hospitalId, @RequestParam(required = false, value ="doctorId") String doctorId) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId, doctorId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"LocationId, HospitalId and DoctorId cannot be empty");
		}
		List<ProductAndService> getProductsAndServicesResponse = patientTreatmentServices
				.getProductsAndServices(locationId, hospitalId, doctorId);

		Response<ProductAndService> response = new Response<ProductAndService>();
		response.setDataList(getProductsAndServicesResponse);
		return response;
	}
	
	@PostMapping(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_TREATMENT_RATELIST)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.ADD_EDIT_TREATMENT_RATELIST, notes = PathProxy.PatientTreatmentURLs.ADD_EDIT_TREATMENT_RATELIST)
	public Response<TreatmentRatelistResponse> addEditTreatmentRatelist(@RequestBody TreatmentRatelistRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		System.out.println("true");
		TreatmentRatelistResponse dentalCampResponse = patientTreatmentServices.addEditTreatmentRatelist(request);

		Response<TreatmentRatelistResponse> response = new Response<TreatmentRatelistResponse>();
		response.setData(dentalCampResponse);
		return response;
	}

	@DeleteMapping(value = PathProxy.PatientTreatmentURLs.DELETE_TREATMENT_RATELIST)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.DELETE_TREATMENT_RATELIST, notes = PathProxy.PatientTreatmentURLs.DELETE_TREATMENT_RATELIST)
	public Response<Boolean> deleteTreatmentRatelistById(@PathVariable(value = "rateListId") String rateListId,
			@RequestParam(required = false, value = "isDiscarded", defaultValue = "true") Boolean isDiscarded) {
		if (DPDoctorUtils.anyStringEmpty(rateListId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		System.out.println("true");
		Response<Boolean> response = new Response<Boolean>();
		response.setData(patientTreatmentServices.deleteTreatmentRatelistById(rateListId, isDiscarded));
		return response;
	}

	@GetMapping(value = PathProxy.PatientTreatmentURLs.GET_TREATMENT_RATELIST)
	@ApiOperation(value = PathProxy.PatientTreatmentURLs.GET_TREATMENT_RATELIST, notes = PathProxy.PatientTreatmentURLs.GET_TREATMENT_RATELIST)
	public Response<Object> getTreatmentRatelist(
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		System.out.println("true");

		Response<Object> response = patientTreatmentServices.getTreatmentRatelist(page, size, searchTerm);
		return response;
	}
	
}
