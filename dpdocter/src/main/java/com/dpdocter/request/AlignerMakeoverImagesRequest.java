package com.dpdocter.request;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.MakeoverImages;

public class AlignerMakeoverImagesRequest {
	String userId;
	String planId;
	private Map<String, List<MakeoverImages>> makeoverImages;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Map<String, List<MakeoverImages>> getMakeoverImages() {
		return makeoverImages;
	}

	public void setMakeoverImages(Map<String, List<MakeoverImages>> makeoverImages) {
		this.makeoverImages = makeoverImages;
	}

}
