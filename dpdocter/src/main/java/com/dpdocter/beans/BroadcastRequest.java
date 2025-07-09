package com.dpdocter.beans;

import java.util.List;

public class BroadcastRequest {

	private List<String> speciality;
	
	private String city;
	
	private String roleType;

	

	public List<String> getSpeciality() {
		return speciality;
	}

	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
	
	
	
}
