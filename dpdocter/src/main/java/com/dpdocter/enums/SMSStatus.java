package com.dpdocter.enums;

public enum SMSStatus {
    IN_PROGRESS("IN_PROGRESS"), DELIVERED("DELIVERED"), FAILED("FAILED"), REJECTED("REJECTED");

    private String smsStatus;

    private SMSStatus(String smsStatus) {
	this.smsStatus = smsStatus;
    }

    public String getSmsStatus() {
	return smsStatus;
    }

}
