package com.dpdocter.request;

import java.util.List;
import java.util.Map;

public class AlignerMakeoverVisualsRequest {
	private String userId;
	private String planId;
	private Map<String, List<String>> makeoverVisuals;

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

	public Map<String, List<String>> getMakeoverVisuals() {
		return makeoverVisuals;
	}

	public void setMakeoverVisuals(Map<String, List<String>> makeoverVisuals) {
		this.makeoverVisuals = makeoverVisuals;
	}

}
