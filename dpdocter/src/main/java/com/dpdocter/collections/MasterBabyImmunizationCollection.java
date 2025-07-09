package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "master_baby_immunization_cl")
public class MasterBabyImmunizationCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String longName;
	@Field
	private Integer periodTime;
	@Field
	private String duration;
	@Field
	private Boolean isChartVaccine = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public Integer getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(Integer periodTime) {
		this.periodTime = periodTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Boolean getIsChartVaccine() {
		return isChartVaccine;
	}

	public void setIsChartVaccine(Boolean isChartVaccine) {
		this.isChartVaccine = isChartVaccine;
	}

	@Override
	public String toString() {
		return "MasterBabyImmunizationCollection [id=" + id + ", name=" + name + ", longName=" + longName
				+ ", periodTime=" + periodTime + "]";
	}

}
