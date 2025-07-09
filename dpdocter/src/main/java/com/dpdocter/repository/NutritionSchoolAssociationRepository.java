package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutritionSchoolAssociationCollection;

public interface NutritionSchoolAssociationRepository extends MongoRepository<NutritionSchoolAssociationCollection, ObjectId>{

	//public NutritionSchoolAssociationCollection findByNutritionistIdAndSchoolBranchId(ObjectId nutritionistId, ObjectId schoolBranchId);
	
}
