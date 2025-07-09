package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DrugCompany;
import com.dpdocter.beans.PharmaLicense;
import com.dpdocter.request.EditPharmaCompanyRequest;
import com.dpdocter.response.PharmaCompanyResponse;
import com.dpdocter.response.PharmaLicenseResponse;

public interface PharmaService {

	PharmaLicenseResponse addeditPharmaLicense(PharmaLicense pharmaLicense);

	List<PharmaLicenseResponse> getLicenses(String companyId, int page, int size);

	PharmaLicenseResponse discardPharmaLicense(String id, Boolean discarded);

	//List<PharmaCompanyResponse> getPharmaCompanyList(int page, int size);

	PharmaCompanyResponse activatePharmaCompany(String id, Boolean activated);

	PharmaCompanyResponse verifyPharmaCompany(String id, Boolean verified);

	PharmaCompanyResponse getCompanyDetails(String id);

	PharmaCompanyResponse editPharmaCompanyDetails(EditPharmaCompanyRequest request);

	List<DrugCompany> getDrugCompanyList(String searchTerm, int page, int size);

	List<PharmaCompanyResponse> getPharmaCompanyList(String searchTerm, int page, int size);
	
	

}
