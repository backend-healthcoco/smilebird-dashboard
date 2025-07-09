package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FAQs;
import com.dpdocter.beans.Testimonials;

@Document(collection = "dental_treatment_detail_cl")
public class DentalTreatmentDetailCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String treatmentName;

	@Field
	private String titleDescription;

	@Field
	private String treatmentDescription;

	@Field
	private List<String> treatmentSteps;// may be array of string

	@Field
	private List<String> personalizedTreatments;

	@Field
	private List<String> benifits;

	@Field
	private List<FAQs> faqs;

	@Field
	private List<Testimonials> testimonials;
	
	@Field
	private String titleImage;

	@Field
	private String thumbnailImage;

	@Field
	private String treamentImage;

	@Field
	private Boolean discarded = false;

	@Field
	private String image;

	@Field
	private String treatmentSlugUrl;
	
	@Field
	private String metaDescription;
	
	@Field
	private String schema;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleDescription() {
		return titleDescription;
	}

	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}

	public String getTreatmentDescription() {
		return treatmentDescription;
	}

	public void setTreatmentDescription(String treatmentDescription) {
		this.treatmentDescription = treatmentDescription;
	}

	public List<String> getTreatmentSteps() {
		return treatmentSteps;
	}

	public void setTreatmentSteps(List<String> treatmentSteps) {
		this.treatmentSteps = treatmentSteps;
	}

	public List<FAQs> getFaqs() {
		return faqs;
	}

	public void setFaqs(List<FAQs> faqs) {
		this.faqs = faqs;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public String getTreamentImage() {
		return treamentImage;
	}

	public void setTreamentImage(String treamentImage) {
		this.treamentImage = treamentImage;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public String getTreatmentName() {
		return treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTreatmentSlugUrl() {
		return treatmentSlugUrl;
	}

	public void setTreatmentSlugUrl(String treatmentSlugUrl) {
		this.treatmentSlugUrl = treatmentSlugUrl;
	}

	public List<String> getPersonalizedTreatments() {
		return personalizedTreatments;
	}

	public void setPersonalizedTreatments(List<String> personalizedTreatments) {
		this.personalizedTreatments = personalizedTreatments;
	}

	public List<String> getBenifits() {
		return benifits;
	}

	public void setBenifits(List<String> benifits) {
		this.benifits = benifits;
	}

	public List<Testimonials> getTestimonials() {
		return testimonials;
	}

	public void setTestimonials(List<Testimonials> testimonials) {
		this.testimonials = testimonials;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
