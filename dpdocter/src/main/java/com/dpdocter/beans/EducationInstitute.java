package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class EducationInstitute extends GenericCollection {

    private String id;

    private String name;
    
    private Boolean discarded=false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
    
    

    public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
    public String toString() {
	return "EducationInstitute [id=" + id + ", name=" + name + "]";
    }
}
