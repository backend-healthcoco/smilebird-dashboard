package com.dpdocter.request;

public class DentalCampTreatmentNameRequest {
	
	private String id;
	private boolean discarded;
	private String dentalTreatment;
	private String type;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isDiscarded() {
		return discarded;
	}
	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}
	public String getDentalTreatment() {
		return dentalTreatment;
	}
	public void setDentalTreatment(String dentalTreatment) {
		this.dentalTreatment = dentalTreatment;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
