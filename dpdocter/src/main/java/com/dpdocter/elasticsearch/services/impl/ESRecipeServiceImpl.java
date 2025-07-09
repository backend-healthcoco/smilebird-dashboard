package com.dpdocter.elasticsearch.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESExerciseDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.repository.ESExerciseRepository;
import com.dpdocter.elasticsearch.repository.ESIngredientRepository;
import com.dpdocter.elasticsearch.repository.ESNutrientRepository;
import com.dpdocter.elasticsearch.repository.ESRecipeRepository;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.enums.Resource;
import com.dpdocter.services.TransactionalManagementService;

@Service
public class ESRecipeServiceImpl implements ESRecipeService {

	private static Logger logger = LogManager.getLogger(ESPrescriptionServiceImpl.class.getName());

	@Autowired
	private ESNutrientRepository esNutrientRepository;

	@Autowired
	private TransactionalManagementService transnationalService;
	
	@Autowired
	private ESIngredientRepository esIngredientRepository;

	@Autowired
	private ESRecipeRepository esRecipeRepository;
	
	@Autowired
	private ESExerciseRepository esExerciseRepository;

	@Override
	public boolean addNutrient(ESNutrientDocument request) {
		boolean response = false;
		try {
			esNutrientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NUTRIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Nutrient in ES");
		}
		return response;
	}

	@Override
	public boolean addIngredient(ESIngredientDocument request) {
		boolean response = false;
		try {
			esIngredientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INGREDIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Ingredient in ES");
		}
		return response;
	}

	@Override
	public boolean addRecipe(ESRecipeDocument request) {
		boolean response = false;
		try {
			esRecipeRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.RECIPE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Recipe in ES");
		}
		return response;
	}
	
	@Override
	public boolean addExercise(ESExerciseDocument request) {
		boolean response = false;
		try {
			esExerciseRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.EXERCISE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Exercise in ES");
		}
		return response;
	}
	
	
	@Override
	public Boolean uploadImage(ESExerciseDocument request) {
		boolean response = false;
		try {
			esExerciseRepository.save(request);
			response = true;
			//transnationalService.addResource(new ObjectId(request.getId()), Resource.EXERCISE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Exercise in ES");
		}
		return response;
	}


@Override
public Boolean uploadVideo(ESExerciseDocument request) {
	boolean response = false;
	try {
		esExerciseRepository.save(request);
		response = true;
		//transnationalService.addResource(new ObjectId(request.getId()), Resource.EXERCISE, true);
	} catch (Exception e) {
		e.printStackTrace();
		logger.error(e + " Error Occurred While Saving Exercise in ES");
	}
	return response;
}
}
