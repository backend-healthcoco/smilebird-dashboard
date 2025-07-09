package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AmountObject;
import com.dpdocter.beans.FaqsObject;
import com.dpdocter.beans.TestIncludedObject;
import com.dpdocter.beans.ThyrocarePlanObject;
import com.dpdocter.response.HealthiansPlanObject;

@Document(collection = "lab_test_plan_cl")

public class LabTestPlansCollection extends GenericCollection {

	
	@Id
	private ObjectId id;;

	@Field
	private String testName;

	@Field
	private List<String> planDescription;

	@Field
	private String aboutPlan;

	@Field
	private List<FaqsObject> faqs;

	@Field
	private String imageWeb;

	@Field
	private String imageMobile;

	@Field
	private HealthiansPlanObject healthiansPlanObject;
	
	@Field
	private ThyrocarePlanObject thyrocarePlanObject;


	@Field
	private AmountObject amount;

	@Field
	private Boolean isDiscarded ;

	@Field
	private String slugUrl;

	@Field
	private String planUId;

	@Field
	private List<TestIncludedObject> testIncluded;


	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	
	public List<String> getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(List<String> planDescription) {
		this.planDescription = planDescription;
	}

	public String getAboutPlan() {
		return aboutPlan;
	}

	public void setAboutPlan(String aboutPlan) {
		this.aboutPlan = aboutPlan;
	}

	public List<FaqsObject> getFaqs() {
		return faqs;
	}

	public void setFaqs(List<FaqsObject> faqs) {
		this.faqs = faqs;
	}

	public String getImageWeb() {
		return imageWeb;
	}

	public void setImageWeb(String imageWeb) {
		this.imageWeb = imageWeb;
	}

	public String getImageMobile() {
		return imageMobile;
	}

	public void setImageMobile(String imageMobile) {
		this.imageMobile = imageMobile;
	}

	public HealthiansPlanObject getHealthiansPlanObject() {
		return healthiansPlanObject;
	}

	public void setHealthiansPlanObject(HealthiansPlanObject healthiansPlanObject) {
		this.healthiansPlanObject = healthiansPlanObject;
	}

	public AmountObject getAmount() {
		return amount;
	}

	public void setAmount(AmountObject amount) {
		this.amount = amount;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
	}

	public String getPlanUId() {
		return planUId;
	}

	public void setPlanUId(String planUId) {
		this.planUId = planUId;
	}

	public ThyrocarePlanObject getThyrocarePlanObject() {
		return thyrocarePlanObject;
	}

	public void setThyrocarePlanObject(ThyrocarePlanObject thyrocarePlanObject) {
		this.thyrocarePlanObject = thyrocarePlanObject;
	}

	public List<TestIncludedObject> getTestIncluded() {
		return testIncluded;
	}

	public void setTestIncluded(List<TestIncludedObject> testIncluded) {
		this.testIncluded = testIncluded;
	}

	
}
