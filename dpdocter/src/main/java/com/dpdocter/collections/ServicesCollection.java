package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "services_cl")
public class ServicesCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private String service;

    @Field
    private String formattedService;
    
    @Field
    private Boolean toShow = true;

    @Field
    private List<String> specialities;
    
    @Field
    private List<String> formattedSpecialities;
    
    @Field
    private List<ObjectId> specialityIds;
    
    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<ObjectId> getSpecialityIds() {
		return specialityIds;
	}

	public void setSpecialityIds(List<ObjectId> specialityIds) {
		this.specialityIds = specialityIds;
	}

	public String getFormattedService() {
		return formattedService;
	}

	public void setFormattedService(String formattedService) {
		this.formattedService = formattedService;
	}

	public List<String> getFormattedSpecialities() {
		return formattedSpecialities;
	}

	public void setFormattedSpecialities(List<String> formattedSpecialities) {
		this.formattedSpecialities = formattedSpecialities;
	}

	@Override
	public String toString() {
		return "ServicesCollection [id=" + id + ", service=" + service + ", formattedService=" + formattedService
				+ ", toShow=" + toShow + ", specialities=" + specialities + ", formattedSpecialities="
				+ formattedSpecialities + ", specialityIds=" + specialityIds + "]";
	}

}
