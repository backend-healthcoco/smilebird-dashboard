package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class AdminUIPermission extends GenericCollection {
	private String id;

	private String adminId;

	private List<String> uiPermissions;

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

}
