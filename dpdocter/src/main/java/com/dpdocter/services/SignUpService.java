package com.dpdocter.services;

import com.dpdocter.beans.AdminSignupRequest;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Locale;
import com.dpdocter.beans.User;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.EditAdminRequest;
import com.dpdocter.request.PharmaCompanySignupRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.PharmaCompanyResponse;

public interface SignUpService {

	User adminSignUp(AdminSignupRequest request);
	
	public Boolean activateAdmin(String id, boolean isActive);
	
	public Boolean editAdminProfile(EditAdminRequest request);
	DoctorSignUp doctorSignUp(DoctorSignupRequest request);

}
