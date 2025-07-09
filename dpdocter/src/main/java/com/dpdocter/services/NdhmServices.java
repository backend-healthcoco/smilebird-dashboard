package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmFacility;

public interface NdhmServices {

	List<NDHMStates> getListforStates();

	List<Districts> getListforDistricts(String statecode);

	Boolean registerFacility(NdhmFacility request);

}
