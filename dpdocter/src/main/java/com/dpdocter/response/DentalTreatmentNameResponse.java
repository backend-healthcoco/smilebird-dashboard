package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class DentalTreatmentNameResponse extends GenericCollection {
	private String id;

	private String treatmentName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTreatmentName() {
		return treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

}
