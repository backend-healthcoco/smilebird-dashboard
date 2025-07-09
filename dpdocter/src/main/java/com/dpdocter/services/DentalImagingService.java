package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CBDTArch;
import com.dpdocter.beans.CBDTQuadrant;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FOV;
import com.dpdocter.beans.Hospital;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationLookupResponse;

public interface DentalImagingService {

	DentalDiagnosticService addEditService(DentalDiagnosticService service);

	List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size);

	DentalDiagnosticService getServiceById(String serviceId);

	DentalDiagnosticService discardService(String serviceId, Boolean discarded);

	Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request);

	List<Hospital> getHospitalList(String doctorId, String hospitalId);

	/*List<DoctorHospitalDentalImagingAssociationLookupResponse> getDoctorHospitalAssociation(String doctorId,
			String hospitalId, String searchTerm, int page, int size, Boolean discarded);*/

	List<DoctorHospitalDentalImagingAssociationLookupResponse> getDoctorHospitalAssociation(String doctorId,
			String locationId, String hospitalId, String searchTerm, int page, int size, Boolean discarded);

	CBDTQuadrant addeditCBDTQuadrant(CBDTQuadrant cbdtQuadrant);

	CBDTArch addeditCBDTArch(CBDTArch cbdtArch);

	FOV addeditFOV(FOV fov);

	List<CBDTQuadrant> getCBDTQuadrants(String searchTerm, int page, int size);

	List<CBDTArch> getCBDTArchs(String searchTerm, int page, int size);

	List<FOV> getFOVs(String searchTerm, int page, int size);

	FOV getFOVById(String id);

	CBDTArch getCBDTArchById(String id);

	CBDTQuadrant getCBDTQuadrantById(String id);
	
}
