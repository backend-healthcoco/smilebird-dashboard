package com.dpdocter.enums;

public enum UniqueIdInitial {

	APPOINTMENT("H"), PRESCRIPTION("P"), REPORTS("R"), CLINICALNOTES("C"), VISITS("V"), USER("USR"), HOSPITAL(
			"HOS"), LOCATION("LOC"), ISSUETRACK("HCI"), FEEDBACK("F"), DOCUMENTS("D") , PHARMACY("PHR") , COLLECTION_BOYS("CB"),ADMIN("ADM"),TREATMENT("T"),
	MEDICINE_ORDER("MO"),HEALTH_THERAPY_PLAN("HT"),LABTEST_PLANS("LT");

	private String initial;

	private UniqueIdInitial(String initial) {
		this.initial = initial;
	}

	public String getInitial() {
		return initial;
	}
}
