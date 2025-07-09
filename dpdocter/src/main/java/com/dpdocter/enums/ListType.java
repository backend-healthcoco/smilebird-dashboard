package com.dpdocter.enums;

public enum ListType {
	TOTAL_NO_OF_RECORDS("TOTAL_NO_OF_RECORDS"), TOTAL_NO_OF_PATIENTS(
			"TOTAL_NO_OF_PATIENTS"), TOTAL_NO_OF_CLINICAL_NOTES("TOTAL_NO_OF_CLINICAL_NOTES"), TOTAL_NO_OF_RX(
					"TOTAL_NO_OF_RX"), TOTAL_NO_OF_APPOINTMENTS("TOTAL_NO_OF_APPOINTMENTS");

	private String type;

	private ListType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
