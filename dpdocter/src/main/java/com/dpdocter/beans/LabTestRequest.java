package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.HealthiansPlanObject;

public class LabTestRequest extends GenericCollection {

	private String id;

	private String testName;

	private List<String> planDescription;
	
	private String aboutPlan;
	
	private List<FaqsObject> faqs;

	private String imageWeb;

	private String imageMobile;

	private HealthiansPlanObject healthiansPlanObject;
	
	private ThyrocarePlanObject thyrocarePlanObject;


	private AmountObject amount;

	private Boolean isDiscarded = false;

	private String slugUrl;
	
	private List<TestIncludedObject> testIncluded;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
