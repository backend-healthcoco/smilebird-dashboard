package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.LabTestType;

import com.dpdocter.beans.RtpcrFileResponse;
import com.dpdocter.enums.RtpcrStatus;


@Document(collection = "rtpcr_test_cl")
public class RtpcrTestCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String patientName;
	
	@Field
	private String hospitalName;
	
	@Field
	private String patientAddress;
	
	@Field
	private String patientMobileNumber;
	

	@Field
	private List<LabTestType>labTestType;
	
	@Field
	private CollectionBoy collectionBoy; 
	
	@Field

	private List<RtpcrFileResponse>reports;
	
	@Field
	private String labName;
	
	@Field
	private Boolean discarded=false;
	
	@Field
	private String remarks;
	
	@Field
	private String assignedTo;
	
	@Field
	private RtpcrStatus status;
	@Field
	private Date date;



	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public RtpcrStatus getStatus() {
		return status;
	}

	public void setStatus(RtpcrStatus status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
	

	


	
	
	
}
