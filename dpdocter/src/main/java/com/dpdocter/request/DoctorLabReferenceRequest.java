package com.dpdocter.request;

public class DoctorLabReferenceRequest {

	private String id;
	private Boolean isContacted;
	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsContacted() {
		return isContacted;
	}

	public void setIsContacted(Boolean isContacted) {
		this.isContacted = isContacted;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
