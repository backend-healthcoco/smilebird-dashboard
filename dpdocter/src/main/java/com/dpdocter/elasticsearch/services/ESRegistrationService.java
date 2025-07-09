package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;

public interface ESRegistrationService {
    boolean addPatient(ESPatientDocument request);

    boolean addDoctor(ESDoctorDocument request);

    void editLocation(DoctorLocation doctorLocation);

	void addEditReference(ESReferenceDocument esReferenceDocument);
	
	void activateUser(String userId);

	boolean addLocale(ESUserLocaleDocument request);

	boolean addCollectionBoy(ESCollectionBoyDocument request);
	

}
