package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient_query_cl")
public class PatientQueryCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private ObjectId patientId;
	
	@Field
	private ObjectId doctorId;
	
	@Field
	private ObjectId locationId;
	
	@Field
	private String patient_problem;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	
	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}


	public String getPatient_problem() {
		return patient_problem;
	}

	public void setPatient_problem(String patient_problem) {
		this.patient_problem = patient_problem;
	}
	
	
	
	
	
}
