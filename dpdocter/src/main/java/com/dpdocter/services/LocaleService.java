package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.Locale;
import com.dpdocter.request.AddEditLocaleAddressDetailsRequest;
import com.dpdocter.request.ClinicImageAddRequest;

public interface LocaleService {

	public Locale getLocaleDetails(String id);

	Locale getLocaleDetailsByContactDetails(String contactNumber);

	Locale activateDeactivateLocale(String id, Boolean isActivate);

	List<Locale> getLocaleList(int page, int size, String searchTerm, Boolean isListed);

	Integer countLocaleList(String searchTerm, Boolean isListed);

	Locale updateLocaleAddress(AddEditLocaleAddressDetailsRequest request);

	public Locale editLocaleprofile(Locale request);

	Boolean addLocaleImage(ClinicImageAddRequest request);

	public boolean deleteLocaleImage(String localeId, List<String> imageIds);

	public DataCount getLocaleCount();
}
