package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ReasonType;

public class LeadsTypeReasonsRequest extends GenericCollection{
	private String id;

	private String reason;
	
	private ReasonType reasonType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ReasonType getReasonType() {
		return reasonType;
	}

	public void setReasonType(ReasonType reasonType) {
		this.reasonType = reasonType;
	}
}
