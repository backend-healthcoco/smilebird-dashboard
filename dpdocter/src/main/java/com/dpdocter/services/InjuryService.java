package com.dpdocter.services;

import com.dpdocter.beans.Injury;

public interface InjuryService {

	Injury addEditInjury(Injury request);
	
	Injury getInjury(int page,int size,Boolean discarded,String searchTerm);
	
	Injury getInjuryById(String id);
	
	Injury deleteInjury(String id,Boolean discarded);
}
