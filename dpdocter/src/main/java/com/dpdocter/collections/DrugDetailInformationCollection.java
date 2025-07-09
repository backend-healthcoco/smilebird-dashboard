package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.GenericCode;

@Document(collection = "drug_generic_info_cl")
public class DrugDetailInformationCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
//	private GenericCode genericNames;
	
	List<GenericCode> genericNames;

	
	@Field
	private List<String> categories;
	@Field
	private String description;
	@Field
	private String mechanismOfAction;
	@Field
	private String pharmalogicalUsesGeneral;
	@Field
	private String pharmalogicalUsesPhysician;
	@Field
	private String drugSchedule;
	@Field
	private String rxRequired;
	@Field
	private String habitForming;
	@Field
	private String adultDose;
	@Field
	private String pediatricDose;
	@Field
	private String geriatricDose;
	@Field
	private String missedDose;
	@Field
	private String overDose;
	@Field
	private String halfLifeOfDrug;
	@Field
	private String onsetOfAction;
	@Field
	private String durationOfEffect;
	@Field
	private String commanSideEffects;
	@Field
	private String contraindications;
	@Field
	private String drugMinor;
	@Field
	private String drugModerate;
	@Field
	private String drugMajor;
	@Field
	private String foodDrug;
	@Field
	private String drivingSafety;
	@Field
	private String alcoholDrugInteraction;
	@Field
	private String brain;
	

	@Field
	private String heart;

	@Field
	private String liver;
	
	@Field
	private String kidney;
	
	@Field
	private String git;
	
	@Field
	private String skin;
	@Field
	private String ent;
	@Field
	private String eye;
	@Field
	private String pediatric;
	
	@Field
	private String geriatric;
	@Field
	private String pregnantWoman;
	@Field
	private String pregnancyCategory;
	@Field
	private String lactating;
	@Field
	private String references;
	@Field
	private String commanBrandName;
	@Field
	private String url;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	
	public List<GenericCode> getGenericNames() {
		return genericNames;
	}
	public void setGenericNames(List<GenericCode> genericNames) {
		this.genericNames = genericNames;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMechanismOfAction() {
		return mechanismOfAction;
	}
	public void setMechanismOfAction(String mechanismOfAction) {
		this.mechanismOfAction = mechanismOfAction;
	}
	public String getPharmalogicalUsesGeneral() {
		return pharmalogicalUsesGeneral;
	}
	public void setPharmalogicalUsesGeneral(String pharmalogicalUsesGeneral) {
		this.pharmalogicalUsesGeneral = pharmalogicalUsesGeneral;
	}
	public String getPharmalogicalUsesPhysician() {
		return pharmalogicalUsesPhysician;
	}
	public void setPharmalogicalUsesPhysician(String pharmalogicalUsesPhysician) {
		this.pharmalogicalUsesPhysician = pharmalogicalUsesPhysician;
	}
	public String getDrugSchedule() {
		return drugSchedule;
	}
	public void setDrugSchedule(String drugSchedule) {
		this.drugSchedule = drugSchedule;
	}
	public String getRxRequired() {
		return rxRequired;
	}
	public void setRxRequired(String rxRequired) {
		this.rxRequired = rxRequired;
	}
	public String getHabitForming() {
		return habitForming;
	}
	public void setHabitForming(String habitForming) {
		this.habitForming = habitForming;
	}
	public String getAdultDose() {
		return adultDose;
	}
	public void setAdultDose(String adultDose) {
		this.adultDose = adultDose;
	}
	public String getPediatricDose() {
		return pediatricDose;
	}
	public void setPediatricDose(String pediatricDose) {
		this.pediatricDose = pediatricDose;
	}
	public String getGeriatricDose() {
		return geriatricDose;
	}
	public void setGeriatricDose(String geriatricDose) {
		this.geriatricDose = geriatricDose;
	}
	public String getMissedDose() {
		return missedDose;
	}
	public void setMissedDose(String missedDose) {
		this.missedDose = missedDose;
	}
	public String getOverDose() {
		return overDose;
	}
	public void setOverDose(String overDose) {
		this.overDose = overDose;
	}
	public String getHalfLifeOfDrug() {
		return halfLifeOfDrug;
	}
	public void setHalfLifeOfDrug(String halfLifeOfDrug) {
		this.halfLifeOfDrug = halfLifeOfDrug;
	}
	public String getOnsetOfAction() {
		return onsetOfAction;
	}
	public void setOnsetOfAction(String onsetOfAction) {
		this.onsetOfAction = onsetOfAction;
	}
	public String getDurationOfEffect() {
		return durationOfEffect;
	}
	public void setDurationOfEffect(String durationOfEffect) {
		this.durationOfEffect = durationOfEffect;
	}
	public String getCommanSideEffects() {
		return commanSideEffects;
	}
	public void setCommanSideEffects(String commanSideEffects) {
		this.commanSideEffects = commanSideEffects;
	}
	public String getContraindications() {
		return contraindications;
	}
	public void setContraindications(String contraindications) {
		this.contraindications = contraindications;
	}
	public String getDrugMinor() {
		return drugMinor;
	}
	public void setDrugMinor(String drugMinor) {
		this.drugMinor = drugMinor;
	}
	public String getDrugModerate() {
		return drugModerate;
	}
	public void setDrugModerate(String drugModerate) {
		this.drugModerate = drugModerate;
	}
	public String getDrugMajor() {
		return drugMajor;
	}
	public void setDrugMajor(String drugMajor) {
		this.drugMajor = drugMajor;
	}
	public String getFoodDrug() {
		return foodDrug;
	}
	public void setFoodDrug(String foodDrug) {
		this.foodDrug = foodDrug;
	}
	public String getDrivingSafety() {
		return drivingSafety;
	}
	public void setDrivingSafety(String drivingSafety) {
		this.drivingSafety = drivingSafety;
	}
	public String getAlcoholDrugInteraction() {
		return alcoholDrugInteraction;
	}
	public void setAlcoholDrugInteraction(String alcoholDrugInteraction) {
		this.alcoholDrugInteraction = alcoholDrugInteraction;
	}
	public String getBrain() {
		return brain;
	}
	public void setBrain(String brain) {
		this.brain = brain;
	}
	public String getHeart() {
		return heart;
	}
	public void setHeart(String heart) {
		this.heart = heart;
	}
	public String getLiver() {
		return liver;
	}
	public void setLiver(String liver) {
		this.liver = liver;
	}
	public String getKidney() {
		return kidney;
	}
	public void setKidney(String kidney) {
		this.kidney = kidney;
	}
	public String getGit() {
		return git;
	}
	public void setGit(String git) {
		this.git = git;
	}
	public String getSkin() {
		return skin;
	}
	public void setSkin(String skin) {
		this.skin = skin;
	}
	public String getEnt() {
		return ent;
	}
	public void setEnt(String ent) {
		this.ent = ent;
	}
	public String getEye() {
		return eye;
	}
	public void setEye(String eye) {
		this.eye = eye;
	}
	public String getPediatric() {
		return pediatric;
	}
	public void setPediatric(String pediatric) {
		this.pediatric = pediatric;
	}
	public String getGeriatric() {
		return geriatric;
	}
	public void setGeriatric(String geriatric) {
		this.geriatric = geriatric;
	}
	public String getPregnantWoman() {
		return pregnantWoman;
	}
	public void setPregnantWoman(String pregnantWoman) {
		this.pregnantWoman = pregnantWoman;
	}
	public String getPregnancyCategory() {
		return pregnancyCategory;
	}
	public void setPregnancyCategory(String pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}
	public String getLactating() {
		return lactating;
	}
	public void setLactating(String lactating) {
		this.lactating = lactating;
	}
	public String getReferences() {
		return references;
	}
	public void setReferences(String references) {
		this.references = references;
	}
	public String getCommanBrandName() {
		return commanBrandName;
	}
	public void setCommanBrandName(String commanBrandName) {
		this.commanBrandName = commanBrandName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
	



}
