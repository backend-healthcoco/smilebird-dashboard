package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LocaleContactUs;
import com.dpdocter.enums.LocaleContactStateType;

public interface LocaleContactUsService {

	List<LocaleContactUs> getLocaleContactList(int page, int size, String searchTerm, String contactState);

	LocaleContactUs updateLocaleContactState(String contactId, LocaleContactStateType contactState);

	public Integer countLocaleContactList(String searchTerm, String contactState);

	public Boolean submitContact(LocaleContactUs contactUs);

}
