package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ProductAndService;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.request.TreatmentRatelistRequest;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TreatmentRatelistResponse;

import common.util.web.Response;

public interface PatientTreatmentServices {

	boolean addEditProductService(ProductAndService productAndService);

	boolean addEditProductServiceCost(ProductAndService productAndService);

	List<ProductAndService> getProductsAndServices(String locationId, String hospitalId, String doctorId);

	TreatmentService addEditService(TreatmentService treatmentService);

	TreatmentService deleteService(String treatmentServiceId, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	List<?> getServices(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm,
			String category, String disease, String speciality,String ratelistId);

	public List<TreatmentService> getTreatmentServices(List<ObjectId> idList);
	
	PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request, Boolean isAppointmentAdd,
			String createdBy, Appointment appointment);

	TreatmentRatelistResponse addEditTreatmentRatelist(TreatmentRatelistRequest request);

	Boolean deleteTreatmentRatelistById(String rateListId, Boolean isDiscarded);

	Response<Object> getTreatmentRatelist(int page, int size, String searchTerm);
}
