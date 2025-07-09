package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.RtpcrFileRequest;

import com.dpdocter.beans.RtpcrFileResponse;

import com.dpdocter.beans.RtpcrTest;
import com.dpdocter.beans.RtpcrTestResponse;
import com.dpdocter.response.ImageURLResponse;

public interface RtpcrTestService {

	RtpcrTest addEditTest(RtpcrTest request);
	
	RtpcrTestResponse getTest(int page,int size,String searchTerm,String fromDate, String toDate,String CollectionBoy,Boolean discarded);


	RtpcrFileResponse uploadRtpcrImage(RtpcrFileRequest request);

	Boolean discardOrder(String id, Boolean discarded);

}
