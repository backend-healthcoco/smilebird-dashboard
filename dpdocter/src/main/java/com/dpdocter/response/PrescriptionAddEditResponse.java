package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.collections.GenericCollection;

public class PrescriptionAddEditResponse extends GenericCollection {
    private String id;

    private String uniqueEmrId;

    private String name;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private List<PrescriptionItem> items;

    private String patientId;

    private String prescriptionCode;

    private Boolean discarded;

    private String visitId;

    private List<TestAndRecordDataResponse> diagnosticTests;

    private String advice;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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

    public List<PrescriptionItem> getItems() {
	return items;
    }

    public void setItems(List<PrescriptionItem> items) {
	this.items = items;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getPrescriptionCode() {
	return prescriptionCode;
    }

    public void setPrescriptionCode(String prescriptionCode) {
	this.prescriptionCode = prescriptionCode;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public String getAdvice() {
	return advice;
    }

    public void setAdvice(String advice) {
	this.advice = advice;
    }

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public List<TestAndRecordDataResponse> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<TestAndRecordDataResponse> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	@Override
	public String toString() {
		return "PrescriptionAddEditResponse [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", name=" + name
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", items="
				+ items + ", patientId=" + patientId + ", prescriptionCode=" + prescriptionCode + ", discarded="
				+ discarded + ", visitId=" + visitId + ", diagnosticTests=" + diagnosticTests + ", advice=" + advice
				+ "]";
	}
}
