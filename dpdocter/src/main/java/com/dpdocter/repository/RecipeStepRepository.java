package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RecipeStepCollection;

public interface RecipeStepRepository extends MongoRepository<RecipeStepCollection, ObjectId>{

	RecipeStepCollection findByRecipeId(ObjectId recipeId);

}
