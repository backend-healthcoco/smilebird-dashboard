package com.dpdocter.beans;

public class ProfessionalMembership {
    private String id;

    private String membership;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getMembership() {
	return membership;
    }

    public void setMembership(String membership) {
	this.membership = membership;
    }

    @Override
    public String toString() {
	return "ProfessionalMembership [id=" + id + ", membership=" + membership + "]";
    }

}
