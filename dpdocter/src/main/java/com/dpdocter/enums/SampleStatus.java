package com.dpdocter.enums;

public enum SampleStatus {
	COLLECTED("COLLECTED"), NOT_COLLECTED("NOT_COLLECTED"), IN_PROGRESS("IN_PROGRESS"), REJECTED("REJECTED"),FAILED("FAILED");

    private String status;

	private SampleStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	


   

}
