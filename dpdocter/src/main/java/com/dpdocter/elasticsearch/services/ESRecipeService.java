package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESExerciseDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;

public interface ESRecipeService {

	public boolean addNutrient(ESNutrientDocument request);

	public boolean addIngredient(ESIngredientDocument request);

	public boolean addRecipe(ESRecipeDocument request);
	
	public boolean addExercise(ESExerciseDocument request);

	public Boolean uploadImage(ESExerciseDocument document);

	public Boolean uploadVideo(ESExerciseDocument document);
}
