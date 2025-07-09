package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CompanyLocation;

public interface CompanyLocationService {
	public CompanyLocation addCompanyLocation(CompanyLocation request);

	public List<CompanyLocation> getCompanyLocationList(int page, int size, String searchTerm,
			Boolean discarded,String companyId);

	public Boolean discardCompanyLocation(String id, Boolean isDiscarded);
	
	Integer countCompanyLocation(String companyId, Boolean isDiscarded, String searchTerm);
	

	Integer getTotalEmployeeCountByLocationId(String companyId, String companylocationId);
	

}

