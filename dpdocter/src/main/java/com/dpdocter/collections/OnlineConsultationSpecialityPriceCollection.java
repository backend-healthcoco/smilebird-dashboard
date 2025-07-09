package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Speciality;

@Document(collection = "online_consultation_speciality_price_cl") 
public class OnlineConsultationSpecialityPriceCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private Speciality speciality;
	@Field
	private Long total_Amount;
	@Field
	private Long discounted_Amount;
	@Field
	private String imageUrl;
	@Field
	private String imageBaner;
	@Field
	private List<String> commonSymptoms;
	@Field
	private String shortDescription;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
