package com.dpdocter.request;

public class AddEditNutritionistRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private Boolean isAdminNutritionist = false;

	private Boolean isNutritionist = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Boolean getIsAdminNutritionist() {
		return isAdminNutritionist;
	}

	public void setIsAdminNutritionist(Boolean isAdminNutritionist) {
		this.isAdminNutritionist = isAdminNutritionist;
	}

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

}
