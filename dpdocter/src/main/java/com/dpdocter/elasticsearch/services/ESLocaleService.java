package com.dpdocter.elasticsearch.services;

import com.dpdocter.beans.Address;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;

public interface ESLocaleService {
	
	boolean addLocale(ESUserLocaleDocument request);

	Boolean updateGeoPoint(String localeId, Address address);

	public Boolean updateLocale(String localeId, LocaleCollection localeCollection);
}
