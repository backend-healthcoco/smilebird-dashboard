package com.dpdocter.beans;

import java.util.List;

public class ConferenceBulkSMS {
	
	private String id;
	
	private String smsHeader;

	private List<String> mobileNumbers;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSmsHeader() {
		return smsHeader;
	}

	public void setSmsHeader(String smsHeader) {
		this.smsHeader = smsHeader;
	}

	public List<String> getMobileNumbers() {
		return mobileNumbers;
	}

	public void setMobileNumbers(List<String> mobileNumbers) {
		this.mobileNumbers = mobileNumbers;
	}


	
	
}
