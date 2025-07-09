package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class OnlineConsultationSpecialityPrice extends GenericCollection{

	private String id;
	
	private Speciality speciality;
	
	private Long total_Amount;
	
	private Long discounted_Amount;
	
	private String imageUrl;
	
	private String imageBaner;
	
	private List<String> commonSymptoms;
	
	private String shortDescription;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public Speciality getSpeciality() {
		return speciality;
	}

	public void setSpeciality(Speciality speciality) {
		this.speciality = speciality;
	}

	public Long getTotal_Amount() {
		return total_Amount;
	}

	public void setTotal_Amount(Long total_Amount) {
		this.total_Amount = total_Amount;
	}

	public Long getDiscounted_Amount() {
		return discounted_Amount;
	}

	public void setDiscounted_Amount(Long discounted_Amount) {
		this.discounted_Amount = discounted_Amount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	

	public String getImageBaner() {
		return imageBaner;
	}

	public void setImageBaner(String imageBaner) {
		this.imageBaner = imageBaner;
	}

	public List<String> getCommonSymptoms() {
		return commonSymptoms;
	}

	public void setCommonSymptoms(List<String> commonSymptoms) {
		this.commonSymptoms = commonSymptoms;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	
	
	
}
