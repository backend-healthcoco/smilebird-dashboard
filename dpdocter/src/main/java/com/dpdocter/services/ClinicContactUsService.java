package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.enums.DoctorContactStateType;

public interface ClinicContactUsService {

	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs);

	// public List<DoctorContactUs> getDoctorContactList(int page, int size);

	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState,
			long contactLaterOnDate);

	public List<ClinicContactUs> getClinicContactList(int page, int size, String searchTerm, String contactState);

	public Integer countClinicContactList(String searchTerm, String contactState);
}
