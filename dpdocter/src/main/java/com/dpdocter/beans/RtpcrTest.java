package com.dpdocter.beans;

import java.util.Date;
import java.util.List;


import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.RtpcrStatus;

public class RtpcrTest extends GenericCollection{

	private String id;
	
	private String hospitalName;
	
	private String patientName;
	
	private String patientAddress;
	
	private String patientMobileNumber;
	
	private List<LabTestType>labTestType;
	
	private CollectionBoy collectionBoy; 
		
	private List<RtpcrFileResponse>reports;
	
	private String labName;
	
	private Boolean discarded=false;
	
	private String remarks;
	
	private String assignedTo;
	
	private RtpcrStatus status;
	
	private Date date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientAddress() {
		return patientAddress;
	}

	public void setPatientAddress(String patientAddress) {
		this.patientAddress = patientAddress;
	}

	public String getPatientMobileNumber() {
		return patientMobileNumber;
	}

	public void setPatientMobileNumber(String patientMobileNumber) {
		this.patientMobileNumber = patientMobileNumber;
	}

	public List<LabTestType> getLabTestType() {
		return labTestType;
	}

	public void setLabTestType(List<LabTestType> labTestType) {
		this.labTestType = labTestType;
	}

	public CollectionBoy getCollectionBoy() {
		return collectionBoy;
	}

	public void setCollectionBoy(CollectionBoy collectionBoy) {
		this.collectionBoy = collectionBoy;
	}


	public List<RtpcrFileResponse> getReports() {
		return reports;
	}

	public void setReports(List<RtpcrFileResponse> reports) {
		this.reports = reports;
	}
	
	

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
	

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	

	public RtpcrStatus getStatus() {
		return status;
	}

	public void setStatus(RtpcrStatus status) {
		this.status = status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "RtpcrTest [id=" + id + ", patientName=" + patientName + ", patientAddress=" + patientAddress
				+ ", patientMobileNumber=" + patientMobileNumber + ", labTestType=" + labTestType + ", collectionBoy="
				+ collectionBoy + ", reports=" + reports + "]";
	}

	
	
	
	
}
