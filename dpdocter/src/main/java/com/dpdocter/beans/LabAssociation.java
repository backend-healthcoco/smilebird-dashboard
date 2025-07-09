package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class LabAssociation extends GenericCollection{

	private String id;
	private String parentLabId;
	private String daughterLabId;
	private Boolean isActive = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(String parentLabId) {
		this.parentLabId = parentLabId;
	}

	public String getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(String daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "LabAssociation [id=" + id + ", parentLabId=" + parentLabId + ", daughterLabId=" + daughterLabId
				+ ", isActive=" + isActive + "]";
	}

}
