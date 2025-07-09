package com.dpdocter.beans;

public class ExcerciseSubType {

	private String name;
	
	private Integer timePerRepetition;
	
	private Float metValue;
	
	private Float speed;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimePerRepetition() {
		return timePerRepetition;
	}

	public void setTimePerRepetition(Integer timePerRepetition) {
		this.timePerRepetition = timePerRepetition;
	}

	public Float getMetValue() {
		return metValue;
	}

	public void setMetValue(Float metValue) {
		this.metValue = metValue;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "ExcerciseSubType [name=" + name + ", timePerRepetition=" + timePerRepetition + ", metValue=" + metValue
				+ ", speed=" + speed + "]";
	}
}
