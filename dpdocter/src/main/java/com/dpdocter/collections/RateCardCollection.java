package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DiagnosticTest;

public class RateCardCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Indexed
	private ObjectId locationId;
	@Indexed
	private ObjectId hospitalId;
	@Field
	private DiagnosticTest diagnosticTest;
	@Field
	private Integer turnaroundTime;
	@Field
	private Double highRate;
	@Field
	private Double normalRate;
	@Field
	private Double lowRate;
	@Field
	private Double specialRate;
	@Field
	private ObjectId labId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Double getHighRate() {
		return highRate;
	}

	public void setHighRate(Double highRate) {
		this.highRate = highRate;
	}

	public Double getNormalRate() {
		return normalRate;
	}

	public void setNormalRate(Double normalRate) {
		this.normalRate = normalRate;
	}

	public Double getLowRate() {
		return lowRate;
	}

	public void setLowRate(Double lowRate) {
		this.lowRate = lowRate;
	}

	public Double getSpecialRate() {
		return specialRate;
	}

	public void setSpecialRate(Double specialRate) {
		this.specialRate = specialRate;
	}

	public ObjectId getLabId() {
		return labId;
	}

	public void setLabId(ObjectId labId) {
		this.labId = labId;
	}

	@Override
	public String toString() {
		return "RateCardCollection [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", highRate=" + highRate + ", normalRate=" + normalRate + ", lowRate=" + lowRate + ", specialRate="
				+ specialRate + ", labId=" + labId + "]";
	}

}
