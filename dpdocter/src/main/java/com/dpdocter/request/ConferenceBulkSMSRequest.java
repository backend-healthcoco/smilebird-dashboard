package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.ConfexUserState;

public class ConferenceBulkSMSRequest {
	
	private String conferenceId;

	private String message;

	private List<String> registerUserIds;

	private Boolean  paymentStatus;
	
	private ConfexUserState state;
	
	

	public ConfexUserState getState() {
		return state;
	}

	public void setState(ConfexUserState state) {
		this.state = state;
	}

	public String getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getRegisterUserIds() {
		return registerUserIds;
	}

	public void setRegisterUserIds(List<String> registerUserIds) {
		this.registerUserIds = registerUserIds;
	}

	public Boolean getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	
}
