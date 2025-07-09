package com.dpdocter.beans;

import java.util.List;


import com.dpdocter.collections.GenericCollection;

public class AdminUIPermission extends GenericCollection {
	private String id;

	private String adminId;

	private List<String> uiPermissions;	
	
	private Boolean  isCuminAdmin = false;
	
	private Boolean  isDentalChainAdmin = false;

	public List<String> getUiPermissions() {
		return uiPermissions;
	}

	public void setUiPermissions(List<String> uiPermissions) {
		this.uiPermissions = uiPermissions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public Boolean getIsCuminAdmin() {
		return isCuminAdmin;
	}

	public void setIsCuminAdmin(Boolean isCuminAdmin) {
		this.isCuminAdmin = isCuminAdmin;
	}

	public Boolean getIsDentalChainAdmin() {
		return isDentalChainAdmin;
	}

	public void setIsDentalChainAdmin(Boolean isDentalChainAdmin) {
		this.isDentalChainAdmin = isDentalChainAdmin;
	}
	
	

}
