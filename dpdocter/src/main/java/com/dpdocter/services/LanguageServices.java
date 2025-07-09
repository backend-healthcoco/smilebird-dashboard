package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Language;

public interface LanguageServices {
	
	public List<Language> getLanguages(int size, int page, Boolean discarded,String searchTerm);
	public Language getLanguage(String id);
	public Integer countLanguage(Boolean discarded, String searchTerm);
	Language addEditLanguage(Language request);
	Language deleteLanguageById(String id);

}
