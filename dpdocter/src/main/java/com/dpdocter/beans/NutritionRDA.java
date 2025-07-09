package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LifeStyleType;

public class NutritionRDA extends GenericCollection{

	private String id;

	private String countryId;
	
	private String country;
	
	private Age fromAge;
	
	private Age toAge;
	
	private double fromAgeInYears;
	
	private double toAgeInYears;
	
	private String gender;
	
	private LifeStyleType type;
	
	private List<String> pregnancyCategory;
	
	private Map<String, String> generalNutrients;

	private Map<String, String> carbNutrients;

	private Map<String, String> lipidNutrients;

	private Map<String, String> proteinAminoAcidNutrients;

	private Map<String, String> vitaminNutrients;

	private Map<String, String> mineralNutrients;

	private Map<String, String> otherNutrients;

	private Boolean discarded = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LifeStyleType getType() {
		return type;
	}

	public void setType(LifeStyleType type) {
		this.type = type;
	}

	public Map<String, String> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(Map<String, String> generalNutrients) {
		this.generalNutrients = generalNutrients;
	}

	public Map<String, String> getCarbNutrients() {
		return carbNutrients;
	}

	public void setCarbNutrients(Map<String, String> carbNutrients) {
		this.carbNutrients = carbNutrients;
	}

	public Map<String, String> getLipidNutrients() {
		return lipidNutrients;
	}

	public void setLipidNutrients(Map<String, String> lipidNutrients) {
		this.lipidNutrients = lipidNutrients;
	}

	public Map<String, String> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(Map<String, String> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
	}

	public Map<String, String> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(Map<String, String> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}

	public Map<String, String> getMineralNutrients() {
		return mineralNutrients;
	}

	public void setMineralNutrients(Map<String, String> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public Map<String, String> getOtherNutrients() {
		return otherNutrients;
	}

	public void setOtherNutrients(Map<String, String> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}

	public Age getFromAge() {
		return fromAge;
	}

	public void setFromAge(Age fromAge) {
		this.fromAge = fromAge;
	}

	public Age getToAge() {
		return toAge;
	}

	public void setToAge(Age toAge) {
		this.toAge = toAge;
	}

	public List<String> getPregnancyCategory() {
		return pregnancyCategory;
	}

	public void setPregnancyCategory(List<String> pregnancyCategory) {
		this.pregnancyCategory = pregnancyCategory;
	}

	public double getFromAgeInYears() {
		return fromAgeInYears;
	}

	public void setFromAgeInYears(double fromAgeInYears) {
		this.fromAgeInYears = fromAgeInYears;
	}

	public double getToAgeInYears() {
		return toAgeInYears;
	}

	public void setToAgeInYears(double toAgeInYears) {
		this.toAgeInYears = toAgeInYears;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "NutritionRDA [id=" + id + ", countryId=" + countryId + ", country=" + country + ", fromAge=" + fromAge
				+ ", toAge=" + toAge + ", fromAgeInYears=" + fromAgeInYears + ", toAgeInYears=" + toAgeInYears
				+ ", gender=" + gender + ", type=" + type + ", pregnancyCategory=" + pregnancyCategory
				+ ", generalNutrients=" + generalNutrients + ", carbNutrients=" + carbNutrients + ", lipidNutrients="
				+ lipidNutrients + ", proteinAminoAcidNutrients=" + proteinAminoAcidNutrients + ", vitaminNutrients="
				+ vitaminNutrients + ", mineralNutrients=" + mineralNutrients + ", otherNutrients=" + otherNutrients
				+ "]";
	}
}
