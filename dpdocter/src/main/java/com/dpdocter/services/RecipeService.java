package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Exercise;
import com.dpdocter.beans.ExerciseImage;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.FoodGroup;
import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.NutrientGoal;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeNutrientType;
import com.dpdocter.beans.RecipeStep;
import com.dpdocter.request.AddEditRecipePlanRequest;

public interface RecipeService {
	public Nutrient addEditNutrient(Nutrient request);

	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm, String category);

	public Nutrient discardNutrient(String id, boolean discarded);

	public Nutrient getNutrient(String id);

	public Ingredient addEditIngredient(Ingredient request);

	public List<Ingredient> getIngredients(int size, int page, boolean discarded, String searchTerm);

	public Ingredient discardIngredient(String id, boolean discarded);

	public Ingredient getIngredient(String id);

	Recipe addEditRecipe(Recipe request);

	Recipe getRecipe(String id);

	Recipe discardRecipe(String id, boolean discarded);

	List<Recipe> getRecipes(int size, int page, boolean discarded, String searchTerm, Boolean verified);

	public Boolean updateNutritentValueAtRecipeLevel(String recipeId);

	Exercise addEditExercise(Exercise request);

	Exercise getExercise(String id);
	
	ExerciseImage uploadImage(MultipartFile file);
	
	ExerciseImage uploadVideo(MultipartFile file);
	
	ExerciseImage saveImage(MultipartFile file, String recordPath, Boolean createThumbnail);

	ExerciseImage saveVideo(MultipartFile file, String recordPath);
	
	Exercise deleteExercise(String id, boolean discarded);

	List<Exercise> getExercises(int size, int page, boolean discarded, String searchTerm);

	public Integer countIngredients(boolean discarded, String searchTerm);

	public Integer countNutrients(boolean discarded, String searchTerm, String category);

	public Integer countRecipes(boolean discarded, String searchTerm);

	public Boolean verifyRecipe(String recipeId);

	Boolean addEditPlansforRecipe(AddEditRecipePlanRequest request);

	RecipeStep addEditRecipeStep(RecipeStep request);

	RecipeStep getRecipeStepByRecipeId(String recipeId);

	public FoodCommunity addEditFoodCommunity(FoodCommunity request);

	public FoodCommunity getFoodCommunity(String id);

	public Integer countFoodCommunities(Boolean discarded, String searchTerm);

	public List<FoodCommunity> getFoodCommunities(int size, int page, Boolean discarded, String searchTerm);

	public FoodCommunity discardFoodCommunity(String id, Boolean discarded);

	public FoodGroup addEditFoodGroup(FoodGroup request);

	public FoodGroup getFoodGroup(String id);

	public Integer countFoodGroups(Boolean discarded, String searchTerm);

	public List<FoodGroup> getFoodGroups(int size, int page, Boolean discarded, String searchTerm);

	public FoodGroup discardFoodGroup(String id, Boolean discarded);
	
	public NutrientGoal addEditNutrientGoal(NutrientGoal request);

	public NutrientGoal getNutrientGoal(String id);
	
	public Integer countNutrientGoals(Boolean discarded, String searchTerm);

	public List<NutrientGoal> getNutrientGoals(int size, int page, Boolean discarded, String searchTerm);

	public NutrientGoal discardNutrientGoal(String id, Boolean discarded);

	public RecipeNutrientType addEditRecipeNutrientType(RecipeNutrientType request);

	public RecipeNutrientType getRecipeNutrientType(String id);

	public Integer countRecipeNutrientTypes(Boolean discarded, String searchTerm);

	public List<RecipeNutrientType> getRecipeNutrientTypes(int size, int page, Boolean discarded, String searchTerm);

	public RecipeNutrientType discardRecipeNutrientType(String id, Boolean discarded);


	public NutritionDisease addEditDisease(NutritionDisease request);

	public NutritionDisease getDisease(String id);

	public List<NutritionDisease> getDiseases(int size, int page, Boolean discarded,String searchTerm);

	public NutritionDisease deleteDisease(String id,Boolean discarded);

	public Integer countDisease(Boolean discarded,String searchTerm);

	public Boolean updateIngredient(String ingredientId);

	public Boolean updateRecipe(int size, int page,String recipeId);

}
