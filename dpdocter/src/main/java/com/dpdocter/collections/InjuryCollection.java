package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.InjuryMultilingual;
import com.dpdocter.beans.YogaMultilingual;
import com.dpdocter.collections.GenericCollection;

@Document(collection = "injury_cl")
public class InjuryCollection extends GenericCollection {

	@Id
	private String id;

	@Field
	private List<InjuryMultilingual>injuryMultilingual;
	
	@Field
	private Boolean discarded=false;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

	public List<InjuryMultilingual> getInjuryMultilingual() {
		return injuryMultilingual;
	}
	public void setInjuryMultilingual(List<InjuryMultilingual> injuryMultilingual) {
		this.injuryMultilingual = injuryMultilingual;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	@Override
	public String toString() {
		return "Injury [id=" + id + ", discarded=" + discarded + "]";
	}
	
	
}
