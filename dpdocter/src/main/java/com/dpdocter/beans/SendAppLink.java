package com.dpdocter.beans;

import com.dpdocter.enums.AppType;

public class SendAppLink {

	private String mobileNumber;
	
	private String emailAddress;
	
	private AppType appType;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	@Override
	public String toString() {
		return "SendAppLink [mobileNumber=" + mobileNumber + ", emailAddress=" + emailAddress + ", appType=" + appType
				+ "]";
	}
}
