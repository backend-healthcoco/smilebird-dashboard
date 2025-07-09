package com.dpdocter.webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.dpdocter.elasticsearch.document.ESExerciseDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditRecipePlanRequest;
import com.dpdocter.services.RecipeService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = PathProxy.RECIPE_BASE_URL, produces = javax.ws.rs.core.MediaType.APPLICATION_JSON, consumes = javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RECIPE_BASE_URL, description = "Endpoint for recipe")
public class RecipeApi {
	private static Logger logger = LogManager.getLogger(RecipeApi.class.getName());

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private ESRecipeService esRecipeService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TransactionalManagementService transnationalService;

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT, notes = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT)
	public Response<Nutrient> addEditNutrient(@RequestBody Nutrient request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getName(), request.getNutrientCode())
				|| request.getCategory() == null || request.getType() == null) {
			logger.warn("name,nutritionCode,type and catogory should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Nutrient nutrient = recipeService.addEditNutrient(request);
		if (nutrient != null) {
			ESNutrientDocument document = new ESNutrientDocument();
			BeanUtil.map(nutrient, document);
			esRecipeService.addNutrient(document);
		}
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT, notes = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT)
	public Response<Ingredient> addEditIngredient(@RequestBody Ingredient request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getName()) || request.getCalories() == null) {
			logger.warn("name,calories should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Ingredient ingredient = recipeService.addEditIngredient(request);
		if (ingredient != null) {
			ESIngredientDocument document = new ESIngredientDocument();
			BeanUtil.map(ingredient, document);
			esRecipeService.addIngredient(document);
		}

		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENT, notes = PathProxy.RecipeUrls.GET_INGREDIENT)
	public Response<Ingredient> getIngredient(@PathVariable("ingredientId") String ingredientId) {

		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Ingredient ingredient = recipeService.getIngredient(ingredientId);
		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT, notes = PathProxy.RecipeUrls.GET_NUTRIENT)
	public Response<Nutrient> getNutrient(@PathVariable("nutrientId") String nutrientId) {

		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Nutrient nutrient = recipeService.getNutrient(nutrientId);
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENTS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENTS, notes = PathProxy.RecipeUrls.GET_NUTRIENTS)
	public Response<Nutrient> getNutrients(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "category") String category) {
		Integer count = recipeService.countNutrients(discarded, searchTerm, category);
		Response<Nutrient> response = new Response<Nutrient>();
		if (count > 0)
			response.setDataList(recipeService.getNutrients(size, page, discarded, searchTerm, category));
		response.setCount(count);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_INGREDIENTS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENTS, notes = PathProxy.RecipeUrls.GET_INGREDIENTS)
	public Response<Ingredient> getIngredient(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countIngredients(discarded, searchTerm);
		Response<Ingredient> response = new Response<Ingredient>();
		if (count > 0)
			response.setDataList(recipeService.getIngredients(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_INGREDIENT, notes = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	public Response<Ingredient> deleteIngredient(@PathVariable("ingredientId") String ingredientId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Ingredient ingredient = recipeService.discardIngredient(ingredientId, discarded);

		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.UPDATE_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.UPDATE_INGREDIENT, notes = PathProxy.RecipeUrls.UPDATE_INGREDIENT)
	public Response<Boolean> updateIngredient(@PathVariable("ingredientId") String ingredientId) {
		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean ingredient = recipeService.updateIngredient(ingredientId);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(ingredient);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.UPDATE_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.UPDATE_RECIPE, notes = PathProxy.RecipeUrls.UPDATE_RECIPE)
	public Response<Boolean> updateRecipe(@PathVariable("recipeId") String recipeId,
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean ingredient = recipeService.updateRecipe(size, page, recipeId);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(ingredient);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_NUTRIENT, notes = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	public Response<Nutrient> deleteNutrient(@PathVariable("nutrientId") String nutrientId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Nutrient nutrient = recipeService.discardNutrient(nutrientId, discarded);
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE, notes = PathProxy.RecipeUrls.DELETE_RECIPE)
	public Response<Recipe> deleteRecipe(@PathVariable("recipeId") String recipeId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		Recipe recipe = recipeService.discardRecipe(recipeId, discarded);
		if (recipe != null) {
			if (recipe.getRecipeImages() != null && !recipe.getRecipeImages().isEmpty())
				for (int index = 0; index <= recipe.getRecipeImages().size(); index++) {
					recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
				}
			if (!DPDoctorUtils.anyStringEmpty(recipe.getVideoUrl())) {
				recipe.setVideoUrl(getFinalImageURL(recipe.getVideoUrl()));
			}
		}
		response.setData(recipe);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE, notes = PathProxy.RecipeUrls.GET_RECIPE)
	public Response<Recipe> getRecipe(@PathVariable("recipeId") String recipeId) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		response.setData(recipeService.getRecipe(recipeId));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES, notes = PathProxy.RecipeUrls.GET_RECIPES)
	public Response<Recipe> getRecipes(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm,
			@RequestParam(required = false, value = "verified") Boolean verified) {
		Integer count = recipeService.countRecipes(discarded, searchTerm);
		Response<Recipe> response = new Response<Recipe>();
		if (count > 0)
			response.setDataList(recipeService.getRecipes(size, page, discarded, searchTerm, verified));
		response.setCount(count);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE)
	public Response<Recipe> addEditRecipes(@RequestBody Recipe request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		} else if (DPDoctorUtils.anyStringEmpty(request.getName()) || request.getCalories() == null) {
			logger.warn("name,calories should not null");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Recipe recipe = recipeService.addEditRecipe(request);
		Response<Recipe> response = new Response<Recipe>();

		if (recipe != null) {
			transnationalService.addResource(new ObjectId(recipe.getId()), Resource.RECIPE, false);

			ESRecipeDocument document = new ESRecipeDocument();
			BeanUtil.map(recipe, document);
			esRecipeService.addRecipe(document);
		}

		if (recipe != null) {
			if (recipe.getRecipeImages() != null && !recipe.getRecipeImages().isEmpty())
				for (int index = 0; index <= recipe.getRecipeImages().size(); index++) {
					recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
				}
			if (!DPDoctorUtils.anyStringEmpty(recipe.getVideoUrl())) {
				recipe.setVideoUrl(getFinalImageURL(recipe.getVideoUrl()));
			}
		}
		response.setData(recipe);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.UPDATE_NUTRIENT_VALUE_AT_RECIPE_LEVEL)
	@ApiOperation(value = PathProxy.RecipeUrls.UPDATE_NUTRIENT_VALUE_AT_RECIPE_LEVEL, notes = PathProxy.RecipeUrls.UPDATE_NUTRIENT_VALUE_AT_RECIPE_LEVEL)
	public Response<Boolean> updateNutritentValueAtRecipeLevel(@PathVariable("recipeId") String recipeId) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean recipe = recipeService.updateNutritentValueAtRecipeLevel(recipeId);
		Response<Boolean> response = new Response<Boolean>();

		response.setData(recipe);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_EXERCISES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_EXERCISES, notes = PathProxy.RecipeUrls.GET_EXERCISES)
	public Response<Exercise> getExercise(@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {

		Response<Exercise> response = new Response<Exercise>();
		response.setDataList(recipeService.getExercises(size, page, discarded, searchTerm));
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_EXERCISE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_EXERCISE, notes = PathProxy.RecipeUrls.DELETE_EXERCISE)
	public Response<Exercise> deleteExercise(@PathVariable("exerciseId") String exerciseId,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(exerciseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Exercise exercise = recipeService.deleteExercise(exerciseId, discarded);

		Response<Exercise> response = new Response<Exercise>();
		response.setData(exercise);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_EXERCISE)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_EXERCISE, notes = PathProxy.RecipeUrls.ADD_EDIT_EXERCISE)
	public Response<Exercise> addEditExercise(@RequestBody Exercise request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<Exercise> response = new Response<Exercise>();
		response.setData(recipeService.addEditExercise(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_EXERCISE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_EXERCISE, notes = PathProxy.RecipeUrls.GET_EXERCISE)
	public Response<Exercise> getExercise(@PathVariable("exerciseId") String exerciseId) {

		if (DPDoctorUtils.anyStringEmpty(exerciseId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Exercise exercise = recipeService.getExercise(exerciseId);
		Response<Exercise> response = new Response<Exercise>();
		response.setData(exercise);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.UPLOAD_EXERCISE_IMAGE, consumes = {
			javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.RecipeUrls.UPLOAD_EXERCISE_IMAGE, notes = PathProxy.RecipeUrls.UPLOAD_EXERCISE_IMAGE)
	public Response<ExerciseImage> saveExerciseImage(@RequestParam(value = "file") MultipartFile file) {

		ExerciseImage exerciseURL = recipeService.uploadImage(file);
		// imageURL = getFinalImageURL(imageURL);
		if (exerciseURL != null) {

			ESExerciseDocument document = new ESExerciseDocument();
			BeanUtil.map(exerciseURL, document);
			esRecipeService.uploadImage(document);
		}
		Response<ExerciseImage> response = new Response<ExerciseImage>();
		response.setData(exerciseURL);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.UPLOAD_EXERCISE_VIDEO, consumes = {
			javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.RecipeUrls.UPLOAD_EXERCISE_VIDEO, notes = PathProxy.RecipeUrls.UPLOAD_EXERCISE_VIDEO)
	public Response<ExerciseImage> saveExerciseVideo(@RequestParam(value = "file") MultipartFile file) {

		ExerciseImage exerciseURL = recipeService.uploadVideo(file);
		// imageURL = getFinalImageURL(imageURL);
		if (exerciseURL != null) {

			ESExerciseDocument document = new ESExerciseDocument();
			BeanUtil.map(exerciseURL, document);
			esRecipeService.uploadVideo(document);
		}
		Response<ExerciseImage> response = new Response<ExerciseImage>();
		response.setData(exerciseURL);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.VERIFY_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.VERIFY_RECIPE, notes = PathProxy.RecipeUrls.VERIFY_RECIPE)
	public Response<Boolean> verifyRecipe(@PathVariable("recipeId") String recipeId) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean status = recipeService.verifyRecipe(recipeId);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_STEPS)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_STEPS, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_STEPS)
	public Response<RecipeStep> addEditRecipeStep(@RequestBody RecipeStep request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		RecipeStep recipe = recipeService.addEditRecipeStep(request);
		Response<RecipeStep> response = new Response<RecipeStep>();
		response.setData(recipe);
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE_STEPS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_STEPS, notes = PathProxy.RecipeUrls.GET_RECIPE_STEPS)
	public Response<RecipeStep> getRecipeSteps(@PathVariable("recipeId") String recipeId) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		RecipeStep recipeSteps = recipeService.getRecipeStepByRecipeId(recipeId);
		Response<RecipeStep> response = new Response<RecipeStep>();
		response.setData(recipeSteps);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_PLANS)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_PLANS, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_PLANS)
	public Response<Boolean> addEditRecipePlan(@RequestBody AddEditRecipePlanRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Boolean recipe = recipeService.addEditPlansforRecipe(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(recipe);
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_FOOD_COMMUNITY)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_FOOD_COMMUNITY, notes = PathProxy.RecipeUrls.ADD_EDIT_FOOD_COMMUNITY)
	public Response<FoodCommunity> addEditFoodCommunity(@RequestBody FoodCommunity request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		Response<FoodCommunity> response = new Response<FoodCommunity>();
		response.setData(recipeService.addEditFoodCommunity(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITY_BY_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITY_BY_ID, notes = PathProxy.RecipeUrls.GET_FOOD_COMMUNITY_BY_ID)
	public Response<FoodCommunity> getFoodCommunity(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FoodCommunity> response = new Response<FoodCommunity>();
		response.setData(recipeService.getFoodCommunity(id));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES, notes = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES)
	public Response<FoodCommunity> getFoodCommunities(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countFoodCommunities(discarded, searchTerm);
		Response<FoodCommunity> response = new Response<FoodCommunity>();
		if (count > 0)
			response.setDataList(recipeService.getFoodCommunities(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_FOOD_COMMUNITY)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_FOOD_COMMUNITY, notes = PathProxy.RecipeUrls.DELETE_FOOD_COMMUNITY)
	public Response<FoodCommunity> discardFoodCommunity(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FoodCommunity> response = new Response<FoodCommunity>();
		response.setData(recipeService.discardFoodCommunity(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_FOOD_GROUP)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_FOOD_GROUP, notes = PathProxy.RecipeUrls.ADD_EDIT_FOOD_GROUP)
	public Response<FoodGroup> addEditFoodGroup(@RequestBody FoodGroup request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		Response<FoodGroup> response = new Response<FoodGroup>();
		response.setData(recipeService.addEditFoodGroup(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_GROUP_BY_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_GROUP_BY_ID, notes = PathProxy.RecipeUrls.GET_FOOD_GROUP_BY_ID)
	public Response<FoodGroup> getFoodGroup(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FoodGroup> response = new Response<FoodGroup>();
		response.setData(recipeService.getFoodGroup(id));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_GROUPS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_GROUPS, notes = PathProxy.RecipeUrls.GET_FOOD_GROUPS)
	public Response<FoodGroup> getFoodGroups(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countFoodGroups(discarded, searchTerm);
		Response<FoodGroup> response = new Response<FoodGroup>();
		if (count > 0)
			response.setDataList(recipeService.getFoodGroups(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_FOOD_GROUP)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_FOOD_GROUP, notes = PathProxy.RecipeUrls.DELETE_FOOD_GROUP)
	public Response<FoodGroup> discardFoodGroup(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<FoodGroup> response = new Response<FoodGroup>();
		response.setData(recipeService.discardFoodGroup(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT_GOAL)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT_GOAL, notes = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT_GOAL)
	public Response<NutrientGoal> addEditNutrientGoal(@RequestBody NutrientGoal request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutrientGoal> response = new Response<NutrientGoal>();
		response.setData(recipeService.addEditNutrientGoal(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOAL_BY_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOAL_BY_ID, notes = PathProxy.RecipeUrls.GET_NUTRIENT_GOAL_BY_ID)
	public Response<NutrientGoal> getNutrientGoal(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<NutrientGoal> response = new Response<NutrientGoal>();
		response.setData(recipeService.getNutrientGoal(id));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS, notes = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS)
	public Response<NutrientGoal> getNutrientGoals(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countNutrientGoals(discarded, searchTerm);
		Response<NutrientGoal> response = new Response<NutrientGoal>();
		if (count > 0)
			response.setDataList(recipeService.getNutrientGoals(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_NUTRIENT_GOAL)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_NUTRIENT_GOAL, notes = PathProxy.RecipeUrls.DELETE_NUTRIENT_GOAL)
	public Response<NutrientGoal> discardNutrientGoal(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<NutrientGoal> response = new Response<NutrientGoal>();
		response.setData(recipeService.discardNutrientGoal(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_NUTRIENT_TYPE)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_NUTRIENT_TYPE, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_NUTRIENT_TYPE)
	public Response<RecipeNutrientType> addEditRecipeNutrientType(@RequestBody RecipeNutrientType request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Response<RecipeNutrientType> response = new Response<RecipeNutrientType>();
		response.setData(recipeService.addEditRecipeNutrientType(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPE_BY_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPE_BY_ID, notes = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPE_BY_ID)
	public Response<RecipeNutrientType> getRecipeNutrientType(@PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeNutrientType> response = new Response<RecipeNutrientType>();
		response.setData(recipeService.getRecipeNutrientType(id));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES, notes = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES)
	public Response<RecipeNutrientType> getRecipeNutrientTypes(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countRecipeNutrientTypes(discarded, searchTerm);
		Response<RecipeNutrientType> response = new Response<RecipeNutrientType>();
		if (count > 0)
			response.setDataList(recipeService.getRecipeNutrientTypes(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_RECIPE_NUTRIENT_TYPE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE_NUTRIENT_TYPE, notes = PathProxy.RecipeUrls.DELETE_RECIPE_NUTRIENT_TYPE)
	public Response<RecipeNutrientType> discardRecipeNutrientType(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeNutrientType> response = new Response<RecipeNutrientType>();
		response.setData(recipeService.discardRecipeNutrientType(id, discarded));
		return response;
	}

	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRITION_DISEASE)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRITION_DISEASE, notes = PathProxy.RecipeUrls.ADD_EDIT_NUTRITION_DISEASE)
	public Response<NutritionDisease> addEditNutrition(@RequestBody NutritionDisease request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionDisease> response = new Response<NutritionDisease>();
		response.setData(recipeService.addEditDisease(request));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASE_BY_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASE_BY_ID, notes = PathProxy.RecipeUrls.GET_NUTRITION_DISEASE_BY_ID)
	public Response<NutritionDisease> getNutrition(@PathVariable("id") String id) {

		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<NutritionDisease> response = new Response<NutritionDisease>();
		response.setData(recipeService.getDisease(id));
		return response;
	}

	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES, notes = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES)
	public Response<NutritionDisease> getNutritionDisease(
			@RequestParam(required = false, value = "size", defaultValue = "0") int size,
			@RequestParam(required = false, value = "page", defaultValue = "0") int page,
			@RequestParam(required = false, value = "discarded") Boolean discarded,
			@RequestParam(required = false, value = "searchTerm") String searchTerm) {
		Integer count = recipeService.countDisease(discarded, searchTerm);
		Response<NutritionDisease> response = new Response<NutritionDisease>();
		if (count > 0)
			response.setDataList(recipeService.getDiseases(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_NUTRITION_DISEASE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_NUTRITION_DISEASE, notes = PathProxy.RecipeUrls.DELETE_NUTRITION_DISEASE)
	public Response<NutritionDisease> discardNutritionDisease(@PathVariable("id") String id,
			@RequestParam(required = false, value = "discarded", defaultValue = "true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<NutritionDisease> response = new Response<NutritionDisease>();
		response.setData(recipeService.deleteDisease(id, discarded));
		return response;
	}
}
