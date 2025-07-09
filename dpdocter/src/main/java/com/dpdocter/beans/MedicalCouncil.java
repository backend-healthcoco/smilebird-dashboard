package com.dpdocter.beans;

public class MedicalCouncil {
    private String id;

    private String medicalCouncil;
    
    private Boolean discarded=false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getMedicalCouncil() {
	return medicalCouncil;
    }

    public void setMedicalCouncil(String medicalCouncil) {
	this.medicalCouncil = medicalCouncil;
    }
    
    

    public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
    public String toString() {
	return "MedicalCouncil [id=" + id + ", medicalCouncil=" + medicalCouncil + "]";
    }

}
