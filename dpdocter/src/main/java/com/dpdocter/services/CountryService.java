package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Country;
import com.dpdocter.collections.DrugDetailInformationCollection;
import com.dpdocter.collections.GenericCodeCollection;

public interface CountryService {
	
	public Country addEditCountry(Country request);
			
	public List<Country> getCountry(int size, int page, Boolean isDiscarded,String searchTerm);
	
	public Integer countCountry(Boolean isDiscarded,String searchTerm);
	
	public Country getCountryById(String id);
	
	public Boolean deleteCountry(String id, Boolean isDiscarded);

	List<GenericCodeCollection> getgenericcollection(int size, int page, Boolean isDiscarded, String searchTerm);

	List<DrugDetailInformationCollection> getDrugInfocollection(int size, int page, Boolean isDiscarded,
			String searchTerm);

}
