package com.dpdocter.response;

import java.util.List;

public class NutritionPlanNameWithCategoryResponse{

	private String category;

	private List<NutritionPlanWithNameResponse> nutritionPlan;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<NutritionPlanWithNameResponse> getNutritionPlan() {
		return nutritionPlan;
	}

	public void setNutritionPlan(List<NutritionPlanWithNameResponse> nutritionPlan) {
		this.nutritionPlan = nutritionPlan;
	}

}
