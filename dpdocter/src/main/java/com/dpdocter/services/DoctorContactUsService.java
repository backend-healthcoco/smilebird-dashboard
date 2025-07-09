package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorLabReference;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.request.DoctorLabReferenceRequest;

public interface DoctorContactUsService {

	public String submitDoctorContactUSInfo(DoctorContactUs doctorContactUs);

	// public List<DoctorContactUs> getDoctorContactList(int page, int size);

	public DoctorContactUs updateDoctorContactState(String contactId, DoctorContactStateType contactState,
			long contactLaterOnDate);

	public List<DoctorContactUs> getDoctorContactList(int page, int size, String searchTerm, String contactState);

	public Integer countDoctorContactList(String searchTerm,String contactState);
	
	public List<DoctorLabReference> getdoctorReferences(int size, int page, String searchTerm, Boolean isContacted);

	public DoctorLabReference getdoctorReference(String referenceId);
	
	public Boolean updateDoctorLabRefencence(DoctorLabReferenceRequest request);

	String resendDoctorWelcome(String emailAddress);
}
