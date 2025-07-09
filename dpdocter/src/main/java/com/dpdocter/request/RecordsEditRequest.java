package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class RecordsEditRequest {

    private String id;

    private String patientId;

    private String doctorId;

    private String explanation;

    private FileDetails fileDetails;

    private String locationId;

    private String hospitalId;

    private String visitId;

    private String recordsUrl;
    
    private String recordsLabel;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public FileDetails getFileDetails() {
	return fileDetails;
    }

    public void setFileDetails(FileDetails fileDetails) {
	this.fileDetails = fileDetails;
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

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

	public String getRecordsUrl() {
		return recordsUrl;
	}

	public void setRecordsUrl(String recordsUrl) {
		this.recordsUrl = recordsUrl;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
	}

	@Override
	public String toString() {
		return "RecordsEditRequest [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", explanation="
				+ explanation + ", fileDetails=" + fileDetails + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", visitId=" + visitId + ", recordsUrl=" + recordsUrl + ", recordsLabel=" + recordsLabel
				+ "]";
	}
}
