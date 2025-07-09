package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class FoodGroup extends GenericCollection{

	private String id;
	
	private String value;

	private Boolean discarded = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "FoodGroup [id=" + id + ", value=" + value + ", discarded=" + discarded + "]";
	}
}
