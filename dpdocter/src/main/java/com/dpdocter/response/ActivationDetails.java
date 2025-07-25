package com.dpdocter.response;

import java.util.List;

public class ActivationDetails {

	private String status;
	
	private List<String> fields_pending;
	
	private Long  activated_at;
	
	private Boolean can_submit;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getFields_pending() {
		return fields_pending;
	}

	public void setFields_pending(List<String> fields_pending) {
		this.fields_pending = fields_pending;
	}

	public Long getActivated_at() {
		return activated_at;
	}

	public void setActivated_at(Long activated_at) {
		this.activated_at = activated_at;
	}

	public Boolean getCan_submit() {
		return can_submit;
	}

	public void setCan_submit(Boolean can_submit) {
		this.can_submit = can_submit;
	}
	
	
}
