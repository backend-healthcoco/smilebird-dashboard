package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CommunicationDoctorTeamRequest;
import com.dpdocter.beans.DoctorResponseStatus;

public interface CommunicationService {

	CommunicationDoctorTeamRequest addEditCommunication(CommunicationDoctorTeamRequest request);

	Integer countDetailsList(DoctorResponseStatus status, String searchTerm);

	List<CommunicationDoctorTeamRequest> getDetailsLists(int size, int page, DoctorResponseStatus status, String searchTerm);

	CommunicationDoctorTeamRequest getByDoctorId(String doctorId);

	
}
