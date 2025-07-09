package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class VerifiedDocuments extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private String documentsUrl;

	private String documentsPath;

	private String documentsLabel;

	private String documentsType;

	private String explaination;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	private Boolean isVerified = false;

	private Boolean discarded = false;

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getDocumentsUrl() {
		return documentsUrl;
	}

	public void setDocumentsUrl(String documentsUrl) {
		this.documentsUrl = documentsUrl;
	}

	public String getDocumentsPath() {
		return documentsPath;
	}

	public void setDocumentsPath(String documentsPath) {
		this.documentsPath = documentsPath;
	}

	public String getDocumentsLabel() {
		return documentsLabel;
	}

	public void setDocumentsLabel(String documentsLabel) {
		this.documentsLabel = documentsLabel;
	}

	public String getDocumentsType() {
		return documentsType;
	}

	public void setDocumentsType(String documentsType) {
		this.documentsType = documentsType;
	}

	public String getExplaination() {
		return explaination;
	}

	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
