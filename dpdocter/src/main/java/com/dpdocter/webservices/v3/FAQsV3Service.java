package com.dpdocter.webservices.v3;

import com.dpdocter.beans.FAQsResponse;

import common.util.web.Response;

public interface FAQsV3Service {

	Boolean deleteFaqsById(String id, Boolean isDiscarded);

	Response<Object> getFaqsList(int page, int size);

	FAQsResponse addEditFaqsReasons(FAQsResponse request);

}
