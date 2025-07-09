package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Injury extends GenericCollection {

	private String id;
	private List<InjuryMultilingual>injuryMultilingual;
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
		return "Injury [id=" + id +  ", discarded=" + discarded + "]";
	}
	
	
}
