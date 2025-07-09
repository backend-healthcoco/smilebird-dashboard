package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.CuminLinkRequest;
import com.dpdocter.request.RegistrationUserRequest;

public interface CuminService {

	Boolean joinGroup();

	Boolean addEditGroupLink(CuminLinkRequest request);

	Boolean userRegistration(RegistrationUserRequest request);

	Boolean updateDoctorToCuminExpert(String locationId, String doctorId, Boolean isCuminExpert);

	List<RegistrationUserRequest> getCuminUserList(int size, int page, Boolean isDiscarded, String searchTerm);

}
