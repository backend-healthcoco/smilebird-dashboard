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

	Boolean activateUser(String tokenId, Boolean activate);

	DoctorSignUp doctorSignUp(DoctorSignupRequest request);

	Boolean checkUserNameExist(String username);

	Boolean checkMobileNumExist(String mobileNum);

	Boolean checkEmailAddressExist(String email);

	User adminSignUp(AdminSignupRequest request);

	Boolean resendVerificationEmail(String emailaddress);

	Boolean resendVerificationSMS(String mobileNumber);

	Boolean activateLocation(String locationId, Boolean activate);

	Locale signupLocale(Locale locale);

	public Boolean activateAdmin(String id, boolean isActive);
	
	public Boolean editAdminProfile(EditAdminRequest request);

	PharmaCompanyResponse signupPharmaCompany(PharmaCompanySignupRequest request);

	Boolean sendSetPasswordEmail(String id);

	Boolean sendDoctorWelcomeMessage(String id);

	CollectionBoyResponse signupCollectionBoys(CollectionBoy collectionBoy);

}
