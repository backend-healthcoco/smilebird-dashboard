package com.dpdocter.beans;

public class ClinicalNotesInvestigation {
    private String id;

    private String investigation;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getInvestigation() {
	return investigation;
    }

    public void setInvestigation(String investigation) {
	this.investigation = investigation;
    }

    @Override
    public String toString() {
	return "ClinicalNotesInvestigation [id=" + id + ", investigation=" + investigation + "]";
    }

}
