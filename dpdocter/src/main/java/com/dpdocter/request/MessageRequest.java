package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.SmsRoute;

public class MessageRequest {

	private List<String> mobileNumber;
	
	private String message;
	
	private String subject;
	
	private SmsRoute smsType;
	
	private String type;

	public List<String> getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(List<String> mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SmsRoute getSmsType() {
		return smsType;
	}

	public void setSmsType(SmsRoute smsType) {
		this.smsType = smsType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	
	
	
}
