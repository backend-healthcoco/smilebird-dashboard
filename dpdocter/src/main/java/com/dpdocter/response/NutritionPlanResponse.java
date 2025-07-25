package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.PlanPriceDescription;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.NutritionPlanType;

public class NutritionPlanResponse extends GenericCollection {
	private String id;

	private String title;

	private NutritionPlanType type;

	private String planDescription;

	private String shortPlanDescription;

	private List<String> nutrientDescriptions;

	private List<String> recommendedFoods;

	private Double amount = 0.0;

	private String planImage;

	private String bannerImage;

	private String backgroundColor;

	private String secondaryBackgroundColor;

	private List<SubscriptionNutritionPlan> subscriptionNutritionPlan;

	private Map<String, PlanPriceDescription> planPriceDescription;

	private Integer rank = 0;

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public NutritionPlanType getType() {
		return type;
	}

	public void setType(NutritionPlanType type) {
		this.type = type;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}

	public List<String> getNutrientDescriptions() {
		return nutrientDescriptions;
	}

	public void setNutrientDescriptions(List<String> nutrientDescriptions) {
		this.nutrientDescriptions = nutrientDescriptions;
	}

	public List<String> getRecommendedFoods() {
		return recommendedFoods;
	}

	public void setRecommendedFoods(List<String> recommendedFoods) {
		this.recommendedFoods = recommendedFoods;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPlanImage() {
		return planImage;
	}

	public void setPlanImage(String planImage) {
		this.planImage = planImage;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public List<SubscriptionNutritionPlan> getSubscriptionNutritionPlan() {
		return subscriptionNutritionPlan;
	}

	public void setSubscriptionNutritionPlan(List<SubscriptionNutritionPlan> subscriptionNutritionPlan) {
		this.subscriptionNutritionPlan = subscriptionNutritionPlan;
	}

	public String getShortPlanDescription() {
		return shortPlanDescription;
	}

	public void setShortPlanDescription(String shortPlanDescription) {
		this.shortPlanDescription = shortPlanDescription;
	}

	public String getSecondaryBackgroundColor() {
		return secondaryBackgroundColor;
	}

	public void setSecondaryBackgroundColor(String secondaryBackgroundColor) {
		this.secondaryBackgroundColor = secondaryBackgroundColor;
	}

	public Map<String, PlanPriceDescription> getPlanPriceDescription() {
		return planPriceDescription;
	}

	public void setPlanPriceDescription(Map<String, PlanPriceDescription> planPriceDescription) {
		this.planPriceDescription = planPriceDescription;
	}

	@Override
	public String toString() {
		return "NutritionPlanResponse [id=" + id + ", title=" + title + ", type=" + type + ", planDescription="
				+ planDescription + ", nutrientDescriptions=" + nutrientDescriptions + ", recommendedFoods="
				+ recommendedFoods + ", amount=" + amount + ", planImage=" + planImage + ", bannerImage=" + bannerImage
				+ ", backgroundColor=" + backgroundColor + ", subscriptionNutritionPlan=" + subscriptionNutritionPlan
				+ "]";
	}

}
