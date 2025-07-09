package com.dpdocter.beans;

public class CampUser {
	private String id;
	private String userName;
	private Boolean isCampUser = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getIsCampUser() {
		return isCampUser;
	}

	public void setIsCampUser(Boolean isCampUser) {
		this.isCampUser = isCampUser;
	}

}
