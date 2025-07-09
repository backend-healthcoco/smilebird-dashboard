package com.dpdocter.webservices.v3;

import java.util.List;

import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.request.PatientRegistrationV3Request;

public interface RegistrationV3Service {
	List<RegisteredPatientDetails> getPatientsByPhoneNumber(Boolean isDentalChainPatient, String mobileNumber);

	List<UserAddress> getUserAddress(String object, String mobileNumber, Boolean discardedAddress);

	Boolean updatePatientContactState(String patientId, String contactState);

	ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient);

	RegisteredPatientDetails registerExistingPatient(PatientRegistrationV3Request request);

	RegisteredPatientDetails registerNewPatient(PatientRegistrationV3Request patientRegistrationRequest);

	void checkPatientCount(String mobileNumber);

	void addUserToInretackt(PatientRegistrationV3Request request);

	Boolean updatePatientFollowUp(String patientId, String followUp,String followUpReason);

}
