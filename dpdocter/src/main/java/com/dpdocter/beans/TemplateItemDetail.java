package com.dpdocter.beans;

import java.util.List;

public class TemplateItemDetail {
    private Drug drug;

    private Duration duration;

    private String dosage;

    private List<Long> dosageTime;
    
    private List<DrugDirection> direction;

    private String instructions;

    public Drug getDrug() {
	return drug;
    }

    public void setDrug(Drug drug) {
	this.drug = drug;
    }

    public Duration getDuration() {
	return duration;
    }

    public void setDuration(Duration duration) {
	this.duration = duration;
    }

    public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public List<Long> getDosageTime() {
		return dosageTime;
	}

	public void setDosageTime(List<Long> dosageTime) {
		this.dosageTime = dosageTime;
	}

	public List<DrugDirection> getDirection() {
	return direction;
    }

    public void setDirection(List<DrugDirection> direction) {
	this.direction = direction;
    }

    public String getInstructions() {
	return instructions;
    }

    public void setInstructions(String instructions) {
	this.instructions = instructions;
    }

	@Override
	public String toString() {
		return "TemplateItemDetail [drug=" + drug + ", duration=" + duration + ", dosage=" + dosage + ", dosageTime="
				+ dosageTime + ", direction=" + direction + ", instructions=" + instructions + "]";
	}
}
