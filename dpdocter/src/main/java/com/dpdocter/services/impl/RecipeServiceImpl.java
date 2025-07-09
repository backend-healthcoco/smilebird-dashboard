package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.EquivalentQuantities;
import com.dpdocter.beans.Exercise;
import com.dpdocter.beans.ExerciseImage;
import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.FoodGroup;
import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.NutrientGoal;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.beans.RecipeNutrientType;
import com.dpdocter.beans.RecipeStep;
import com.dpdocter.collections.ExerciseCollection;
import com.dpdocter.collections.FoodCommunityCollection;
import com.dpdocter.collections.FoodGroupCollection;
import com.dpdocter.collections.IngredientCollection;
import com.dpdocter.collections.NutrientCollection;
import com.dpdocter.collections.NutrientGoalCollection;
import com.dpdocter.collections.NutritionDiseaseCollection;
import com.dpdocter.collections.NutritionPlanCollection;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.collections.RecipeNutrientTypeCollection;
import com.dpdocter.collections.RecipeStepCollection;
import com.dpdocter.elasticsearch.document.ESExerciseDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.repository.ESExerciseRepository;
import com.dpdocter.elasticsearch.repository.ESIngredientRepository;
import com.dpdocter.elasticsearch.repository.ESNutrientRepository;
import com.dpdocter.elasticsearch.repository.ESRecipeRepository;
import com.dpdocter.enums.QuantityEnum;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ExerciseRepository;
import com.dpdocter.repository.FoodCommunityRepository;
import com.dpdocter.repository.FoodGroupRepository;
import com.dpdocter.repository.IngredientRepository;
import com.dpdocter.repository.NutrientGoalRepository;
import com.dpdocter.repository.NutrientRepository;
import com.dpdocter.repository.NutritionDiseaseRepository;
import com.dpdocter.repository.RecipeNutrientTypeRepository;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.repository.RecipeStepRepository;
import com.dpdocter.request.AddEditRecipePlanRequest;
import com.dpdocter.response.NutritionPlanWithNameResponse;
import com.dpdocter.services.RecipeService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class RecipeServiceImpl implements RecipeService {

	private static Logger logger = LogManager.getLogger(RecipeServiceImpl.class.getName());

	@Autowired
	private NutrientRepository nutrientRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private ExerciseRepository exerciseRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Autowired
	private ESRecipeRepository eSRecipeRepository;

	@Autowired
	private ESIngredientRepository esIngredientRepository;

	@Autowired
	private ESExerciseRepository esExerciseRepository;

	@Autowired
	private ESNutrientRepository esNutrientRepository;

	@Autowired
	private RecipeStepRepository recipeStepRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private NutrientGoalRepository nutrientGoalRepository;

	@Autowired
	private FoodCommunityRepository foodCommunityRepository;

	@Autowired
	private FoodGroupRepository foodGroupRepository;

	@Autowired
	private NutritionDiseaseRepository nutritionDiseaseRepository;

	@Autowired
	private RecipeNutrientTypeRepository recipeNutrientTypeRepository;

	@Override
	public Nutrient addEditNutrient(Nutrient request) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutrientCollection = nutrientRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nutrientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(nutrientCollection.getCreatedBy());
				request.setCreatedTime(nutrientCollection.getCreatedTime());
				BeanUtil.map(request, nutrientCollection);

			} else {
				nutrientCollection = new NutrientCollection();
				BeanUtil.map(request, nutrientCollection);
				nutrientCollection.setCreatedBy("ADMIN");
				nutrientCollection.setUpdatedTime(new Date());
				nutrientCollection.setCreatedTime(new Date());
			}
			nutrientCollection = nutrientRepository.save(nutrientCollection);
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm, String category) {
		List<Nutrient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria.and("category").is(category.toUpperCase());

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NutrientCollection.class, Nutrient.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting nutrients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Nutrient discardNutrient(String id, boolean discarded) {
		Nutrient response = null;
		try {
			ESNutrientDocument esNutrientDocument = null;
			NutrientCollection nutrientCollection = nutrientRepository.findById(new ObjectId(id)).orElse(null);
			if (nutrientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
			}
			nutrientCollection.setDiscarded(discarded);
			nutrientCollection.setUpdatedTime(new Date());
			nutrientCollection = nutrientRepository.save(nutrientCollection);
			esNutrientDocument = esNutrientRepository.findById(id).orElse(null);
			if (esNutrientDocument != null) {
				esNutrientDocument.setUpdatedTime(new Date());
				esNutrientDocument.setDiscarded(discarded);
				esNutrientRepository.save(esNutrientDocument);
			}
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Nutrient getNutrient(String id) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = nutrientRepository.findById(new ObjectId(id)).orElse(null);
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Recipe addEditRecipe(Recipe request) {
		Recipe response = null;
		try {
			if (request != null) {
				if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty())
					for (int index = 0; index <= request.getRecipeImages().size(); index++) {
						request.getRecipeImages().add(index,
								request.getRecipeImages().get(index).replace(imagePath, ""));
					}
				if (!DPDoctorUtils.anyStringEmpty(request.getVideoUrl())) {
					request.setVideoUrl(request.getVideoUrl().replace(imagePath, ""));
				}

				RecipeCollection recipeCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
					recipeCollection = recipeRepository.findById(new ObjectId(request.getId())).orElse(null);
					if (recipeCollection == null) {
						throw new BusinessException(ServiceError.NotFound, "Recipe Not found with Id");
					}

					request.setCreatedBy(recipeCollection.getCreatedBy());
					request.setUpdatedTime(new Date());
					request.setCreatedTime(recipeCollection.getCreatedTime());
					recipeCollection = new RecipeCollection();
					BeanUtil.map(request, recipeCollection);

					if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty()) {

						recipeCollection.setRecipeImages(new ArrayList<String>());
						recipeCollection.setRecipeImages(request.getRecipeImages());
					}

				} else {
					recipeCollection = new RecipeCollection();
					BeanUtil.map(request, recipeCollection);
					recipeCollection.setCreatedBy("ADMIN");
					recipeCollection.setUpdatedTime(new Date());
					recipeCollection.setCreatedTime(new Date());

				}
				recipeCollection = recipeRepository.save(recipeCollection);
				response = new Recipe();
				BeanUtil.map(recipeCollection, response);

			}
		} catch (

		BusinessException e) {
			logger.error("Error while addedit Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Recipe> getRecipes(int size, int page, boolean discarded, String searchTerm, Boolean verified) {
		List<Recipe> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("name").regex(searchTerm, "i");
			}
			if (verified != null) {
				criteria.and("verified").is(verified);
			}
//			CustomAggregationOperation aggregationOperationFirst = new CustomAggregationOperation(new Document(
//					"$group",
//					new BasicDBObject("_id", new BasicDBObject("id", "$id").append("nutrientId", "$nutrients.id"))
//							.append("videoUrl", new BasicDBObject("$first", "$videoUrl"))
//							.append("quantity", new BasicDBObject("$first", "$quantity"))
//							.append("equivalentMeasurements", new BasicDBObject("$first", "$equivalentMeasurements"))
//							.append("name", new BasicDBObject("$first", "$name"))
//							.append("recipeImages", new BasicDBObject("$first", "$recipeImages"))
//							.append("includeIngredients", new BasicDBObject("$first", "$includeIngredients"))
//							.append("excludeIngredients", new BasicDBObject("$first", "$excludeIngredients"))
//							.append("dishType", new BasicDBObject("$first", "$dishType"))
//							.append("locationId", new BasicDBObject("$first", "$locationId"))
//							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
//							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
//							.append("technique", new BasicDBObject("$first", "$technique"))
//							.append("isPopular", new BasicDBObject("$first", "$isPopular"))
//							.append("isHoliday", new BasicDBObject("$first", "$isHoliday"))
//							.append("discarded", new BasicDBObject("$first", "$discarded"))
//							.append("direction", new BasicDBObject("$first", "$direction"))
//							.append("dietaryConcerns", new BasicDBObject("$first", "$dietaryConcerns"))
//							.append("forMember", new BasicDBObject("$first", "$forMember"))
//							.append("cost", new BasicDBObject("$first", "$cost"))
//							.append("meal", new BasicDBObject("$first", "$meal"))
//							.append("cuisine", new BasicDBObject("$first", "$cuisine"))
//							.append("course", new BasicDBObject("$first", "$course"))
//							.append("verified", new BasicDBObject("$first", "$verified"))
//							.append("preparationTime", new BasicDBObject("$first", "$preparationTime"))
//							.append("mealTiming", new BasicDBObject("$first", "$mealTiming"))
//							.append("calaries", new BasicDBObject("$first", "$calaries"))
//							.append("nutrientValueAtRecipeLevel", new BasicDBObject("$first", "$nutrientValueAtRecipeLevel"))
//							.append("nutrients", new BasicDBObject("$first", "$nutrients"))
//							.append("ingredients", new BasicDBObject("$first", "$ingredients"))
//							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
//							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
//							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			response = mongoTemplate.aggregate(aggregation, RecipeCollection.class, Recipe.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (Recipe recipe : response) {
					if (recipe.getRecipeImages() != null && !recipe.getRecipeImages().isEmpty())
						for (int index = 0; index <= recipe.getRecipeImages().size(); index++) {
							recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
						}
					if (!DPDoctorUtils.anyStringEmpty(recipe.getVideoUrl())) {
						recipe.setVideoUrl(getFinalImageURL(recipe.getVideoUrl()));
					}
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public Recipe discardRecipe(String id, boolean discarded) {
		Recipe response = null;
		try {
			ESRecipeDocument document = null;
			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(id)).orElse(null);
			if (recipeCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "recipe Not found with Id");
			}

			recipeCollection.setDiscarded(discarded);
			recipeCollection.setUpdatedTime(new Date());
			recipeCollection = recipeRepository.save(recipeCollection);
			response = new Recipe();
			document = eSRecipeRepository.findById(id).orElse(null);
			if (document != null) {
				document.setUpdatedTime(new Date());
				document.setDiscarded(discarded);
				eSRecipeRepository.save(document);
			}
			BeanUtil.map(recipeCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while delete Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public Recipe getRecipe(String id) {
		Recipe response = null;
		try {
			response = mongoTemplate.findById(new ObjectId(id), Recipe.class, "recipe_cl");
			if (response != null) {
				if (response.getRecipeImages() != null && !response.getRecipeImages().isEmpty())
					for (int index = 0; index <= response.getRecipeImages().size(); index++) {
						response.getRecipeImages().add(index, getFinalImageURL(response.getRecipeImages().get(index)));
					}
				if (!DPDoctorUtils.anyStringEmpty(response.getVideoUrl())) {
					response.setVideoUrl(getFinalImageURL(response.getVideoUrl()));
				}

				if (response.getPlanIds() != null) {
					Criteria criteria = new Criteria("id").in(response.getPlanIds());
					Aggregation aggregation = null;

					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

					List<NutritionPlanWithNameResponse> nutritionPlanWithNameResponses = mongoTemplate
							.aggregate(aggregation, NutritionPlanCollection.class, NutritionPlanWithNameResponse.class)
							.getMappedResults();

					response.setPlans(nutritionPlanWithNameResponses);
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public Ingredient addEditIngredient(Ingredient request) {
		Ingredient response = null;
		try {
			IngredientCollection ingredientCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				ingredientCollection = ingredientRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (ingredientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "ingredient Not found with Id");
				}
				request.setCreatedBy("ADMIN");
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
				ingredientCollection = new IngredientCollection();
				BeanUtil.map(request, ingredientCollection);

			} else {
				ingredientCollection = new IngredientCollection();
				BeanUtil.map(request, ingredientCollection);
				ingredientCollection.setCreatedBy("ADMIN");
				ingredientCollection.setUpdatedTime(new Date());
				ingredientCollection.setCreatedTime(new Date());
			}
			ingredientCollection = ingredientRepository.save(ingredientCollection);
			response = new Ingredient();
			BeanUtil.map(ingredientCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit Ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Ingredient " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Ingredient> getIngredients(int size, int page, boolean discarded, String searchTerm) {
		List<Ingredient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("name").regex(searchTerm, "i");
			}
//				criteria=criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
//						new Criteria("name").regex("^" + searchTerm),
//						new Criteria("name").regex(searchTerm + "$", "i"),
//						new Criteria("name").regex("^"+ searchTerm +"$","i"),
//						new Criteria("name").regex("^"+searchTerm + ".*","i"),
//						new Criteria("name").regex("/^" + searchTerm+"/", "i"),
//						new Criteria("name").regex(",?[a-zA-Z]*,?"+searchTerm + "$","i"),
//						new Criteria("name").regex("/^/" + searchTerm+"//", "i"),
//						new Criteria("name").regex(searchTerm,"x"));

			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("quantity", new BasicDBObject("$first", "$quantity"))
							.append("name", new BasicDBObject("$first", "$name"))
							.append("note", new BasicDBObject("$first", "$note"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("nutrients", new BasicDBObject("$first", "$nutrients"))
							.append("equivalentMeasurements", new BasicDBObject("$first", "$equivalentMeasurements"))
							.append("calaries", new BasicDBObject("$first", "$calaries"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, IngredientCollection.class, Ingredient.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Ingredients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Ingredients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Ingredient discardIngredient(String id, boolean discarded) {
		Ingredient response = null;
		try {
			ESIngredientDocument esIngredientDocument = null;
			IngredientCollection ingredientCollection = ingredientRepository.findById(new ObjectId(id)).orElse(null);
			if (ingredientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Ingredients Not found with Id");
			}
			ingredientCollection.setDiscarded(discarded);
			ingredientCollection.setUpdatedTime(new Date());
			ingredientCollection = ingredientRepository.save(ingredientCollection);
			esIngredientDocument = esIngredientRepository.findById(id).orElse(null);
			if (esIngredientDocument != null) {
				esIngredientDocument.setUpdatedTime(new Date());
				esIngredientDocument.setDiscarded(discarded);
				esIngredientRepository.save(esIngredientDocument);
			}
			response = new Ingredient();
			BeanUtil.map(ingredientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard ingredient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Ingredient getIngredient(String id) {
		Ingredient response = null;
		try {
			response = mongoTemplate.findById(new ObjectId(id), Ingredient.class, "ingredient_cl");
		} catch (BusinessException e) {
			logger.error("Error while getting ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting ingredient " + e.getMessage());

		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	public Boolean updateNutritentValueAtRecipeLevel(String recipeId) {
		Boolean response = false;
		try {
			ESRecipeDocument document = null;
			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(recipeId)).orElse(null);
			if (recipeCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "recipe Not found with Id");
			}

			recipeCollection.setNutrientValueAtRecipeLevel(!recipeCollection.getNutrientValueAtRecipeLevel());

			recipeCollection.setUpdatedTime(new Date());
			recipeCollection = recipeRepository.save(recipeCollection);

			document = eSRecipeRepository.findById(recipeId).orElse(null);
			if (document != null) {
				document.setUpdatedTime(new Date());
				document.setNutrientValueAtRecipeLevel(!document.isNutrientValueAtRecipeLevel());
				eSRecipeRepository.save(document);
			}

			response = true;
		} catch (BusinessException e) {
			logger.error("Error while delete Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public Exercise addEditExercise(Exercise request) {
		Exercise response = null;
		try {
			ExerciseCollection exerciseCollection = new ExerciseCollection();
			BeanUtil.map(request, exerciseCollection);
			exerciseCollection.setAdminCreatedTime(new Date());
			exerciseCollection.setCreatedTime(new Date());
			exerciseCollection.setUpdatedTime(new Date());
			exerciseCollection = exerciseRepository.save(exerciseCollection);

			response = new Exercise();
			BeanUtil.map(exerciseCollection, response);

			transnationalService.addResource(new ObjectId(response.getId()), Resource.EXERCISE, false);
			if (response != null) {
				ESExerciseDocument esExerciseDocument = new ESExerciseDocument();
				BeanUtil.map(response, esExerciseDocument);
				esExerciseRepository.save(esExerciseDocument);
			}
			transnationalService.addResource(new ObjectId(response.getId()), Resource.EXERCISE, true);
		} catch (Exception e) {
			logger.error("Error while adding exercise." + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding exercise.");
		}
		return response;

	}

	@Override
	@Transactional
	public ExerciseImage uploadImage(MultipartFile file) {
		String recordPath = null;
		ExerciseImage exeriseImage = null;

		try {

			Date createdTime = new Date();
			if (file != null) {

				String path = "exerciseImg" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				exeriseImage = recipeService.saveImage(file, recordPath, true);

				// exeriseImage.setImageUrl(exeriseImage.getImageUrl());
				// exeriseImage.setThumbnailUrl(exeriseImage.getThumbnailUrl());

//				if(imageURLResponse != null)
//				{
//					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
//					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl()); 
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return exeriseImage;
	}

	@Override
	@Transactional
	public ExerciseImage uploadVideo(MultipartFile file) {
		String recordPath = null;
		ExerciseImage exeriseImage = null;

		try {

			Date createdTime = new Date();
			if (file != null) {

				String path = "exerciseImg" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				exeriseImage = recipeService.saveVideo(file, recordPath);

//				if(imageURLResponse != null)
//				{
//					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
//					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl()); 
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return exeriseImage;
	}

	@Override
	@Transactional
	public ExerciseImage saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		ExerciseImage response = new ExerciseImage();
		Double fileSizeInMB = 10.0;
		try {
			InputStream fis = file.getInputStream();
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentType());
			metadata.setContentType(file.getContentType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, file.getInputStream(), metadata));
			response.setImageUrl(imagePath + recordPath);
			if (createThumbnail) {
				response.setThumbnailUrl(imagePath + saveThumbnailUrl(file, recordPath));
			}
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	public ExerciseImage saveVideo(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		ExerciseImage response = new ExerciseImage();
		Double fileSizeInMB = 10.0;
		try {
			InputStream fis = file.getInputStream();
			ObjectMetadata metadata = new ObjectMetadata();
			byte[] contentBytes = IOUtils.toByteArray(fis);

			/*
			 * fileSizeInMB = new BigDecimal(contentBytes.length).divide(new BigDecimal(1000
			 * * 1000)).doubleValue(); if (fileSizeInMB > 10) { throw new
			 * BusinessException(ServiceError.Unknown,
			 * " You cannot upload file more than 1O mb"); }
			 */

			metadata.setContentLength(contentBytes.length);
			metadata.setContentEncoding(file.getContentType());
			metadata.setContentType(file.getContentType());
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			s3client.putObject(new PutObjectRequest(bucketName, recordPath, file.getInputStream(), metadata));
			response.setVideoUrl(imagePath + recordPath);

		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
			System.out.println("Error Message:    " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			ace.printStackTrace();
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());

		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Message: " + e.getMessage());
		}
		return response;

	}

	public String saveThumbnailUrl(MultipartFile file, String path) {
		String thumbnailUrl = "";

		try {
			String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
			AmazonS3 s3client = new AmazonS3Client(credentials);
			S3Object object = s3client.getObject(new GetObjectRequest(bucketName, path));
			InputStream objectData = object.getObjectContent();

			BufferedImage originalImage = ImageIO.read(objectData);
			double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
			int height = originalImage.getHeight();

			int width = originalImage.getWidth();
			int max = 120;
			if (width == height) {
				width = max;
				height = max;
			} else if (width > height) {
				height = max;
				width = (int) (ratio * max);
			} else {
				width = max;
				height = (int) (max / ratio);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
					null);
			// String fileName = fileDetails.getFileName() + "_thumb." +
			// fileDetails.getFileExtension();
			thumbnailUrl = "thumb_" + path;

			originalImage.flush();
			originalImage = null;

			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ImageIO.write(img, fileExtension, outstream);
			byte[] buffer = outstream.toByteArray();
			objectData = new ByteArrayInputStream(buffer);

			String contentType = URLConnection.guessContentTypeFromStream(objectData);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(buffer.length);
			metadata.setContentEncoding(fileExtension);
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			s3client.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, metadata));
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
					+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
					+ " Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return thumbnailUrl;
	}

	@Override
	public List<Exercise> getExercises(int size, int page, boolean discarded, String searchTerm) {
		List<Exercise> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, ExerciseCollection.class, Exercise.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Exercises " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Exercises " + e.getMessage());

		}
		return response;
	}

	@Override
	public Exercise deleteExercise(String id, boolean discarded) {
		Exercise response = null;
		try {
			ESExerciseDocument esExerciseDocument = null;
			ExerciseCollection exerciseCollection = exerciseRepository.findById(new ObjectId(id)).orElse(null);
			if (exerciseCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Exercise Not found with Id");
			}
			exerciseCollection.setDiscarded(discarded);
			exerciseCollection.setUpdatedTime(new Date());
			exerciseCollection = exerciseRepository.save(exerciseCollection);
			esExerciseDocument = esExerciseRepository.findById(id).orElse(null);
			if (esExerciseDocument != null) {
				esExerciseDocument.setUpdatedTime(new Date());
				esExerciseDocument.setDiscarded(discarded);
				esExerciseRepository.save(esExerciseDocument);
			}
			response = new Exercise();
			BeanUtil.map(exerciseCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete exercise " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard exercise " + e.getMessage());

		}
		return response;

	}

	@Override
	public Exercise getExercise(String id) {
		Exercise response = null;
		try {
			ExerciseCollection exerciseCollection = exerciseRepository.findById(new ObjectId(id)).orElse(null);
			response = new Exercise();
			BeanUtil.map(exerciseCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting exercise " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting exercise " + e.getMessage());

		}
		return response;

	}

	@Override
	public Boolean verifyRecipe(String recipeId) {
		Boolean response = false;
		try {
			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(recipeId)).orElse(null);
			response = true;
			recipeCollection.setVerified(!recipeCollection.getVerified());
			recipeRepository.save(recipeCollection);

			if (recipeCollection != null) {
				ESRecipeDocument esRecipeDocument = new ESRecipeDocument();
				System.out.println("before mapper" + recipeCollection);
				BeanUtil.map(recipeCollection, esRecipeDocument);
				System.out.println("after mapper" + esRecipeDocument);

				eSRecipeRepository.save(esRecipeDocument);
				System.out.println("save" + esRecipeDocument);

			}

		} catch (BusinessException e) {
			logger.error("Error while verify recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while verify recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public Integer countIngredients(boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("name").regex(searchTerm, "i");
			}

			response = (int) mongoTemplate.count(new Query(criteria), IngredientCollection.class);
			;
		} catch (BusinessException e) {
			logger.error("Error while count Ingredients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count Ingredients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countNutrients(boolean discarded, String searchTerm, String category) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria.and("category").is(category.toUpperCase());

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

			response = mongoTemplate.aggregate(aggregation, NutrientCollection.class, Nutrient.class).getMappedResults()
					.size();
		} catch (BusinessException e) {
			logger.error("Error while count nutrients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count nutrients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countRecipes(boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				searchTerm = searchTerm.replaceAll(" ", ".*");
				criteria = criteria.and("name").regex(searchTerm, "i");
			}

			response = (int) mongoTemplate.count(new Query(criteria), RecipeCollection.class);

		} catch (BusinessException e) {
			logger.error("Error while count Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean addEditPlansforRecipe(AddEditRecipePlanRequest request) {
		Boolean response = false;
		List<ObjectId> ids = null;
		RecipeCollection recipeCollection = null;

		try {
			recipeCollection = recipeRepository.findById(new ObjectId(request.getRecipeId())).orElse(null);
			if (recipeCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Recipe not found");
			}
			if (request.getPlanIds() != null) {
				ids = new ArrayList<>();
				for (String planId : request.getPlanIds()) {
					ids.add(new ObjectId(planId));
				}
			}
			recipeCollection.setPlanIds(ids);
			recipeCollection = recipeRepository.save(recipeCollection);
			response = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public RecipeStep addEditRecipeStep(RecipeStep request) {
		RecipeStep response = null;
		try {
			if (request != null) {

				RecipeStepCollection recipeStepCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getRecipeId())) {
					recipeStepCollection = recipeStepRepository.findByRecipeId(new ObjectId(request.getRecipeId()));
					if (recipeStepCollection == null) {
						recipeStepCollection = new RecipeStepCollection();
						BeanUtil.map(request, recipeStepCollection);
						recipeStepCollection.setCreatedBy("ADMIN");
						recipeStepCollection.setUpdatedTime(new Date());
						recipeStepCollection.setCreatedTime(new Date());
					} else {
						BeanUtil.map(request, recipeStepCollection);
						recipeStepCollection.setStepDetails(request.getStepDetails());
					}
					recipeStepCollection = recipeStepRepository.save(recipeStepCollection);
					response = new RecipeStep();
					BeanUtil.map(recipeStepCollection, response);

				} else {
					throw new BusinessException(ServiceError.InvalidInput, "Recipe id is null");

				}
			}
		} catch (

		BusinessException e) {
			logger.error("Error while addedit Recipe Step " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Recipe step" + e.getMessage());

		}
		return response;

	}

	@Override
	public RecipeStep getRecipeStepByRecipeId(String recipeId) {
		RecipeStep response = null;
		try {
			RecipeStepCollection recipeStepCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(recipeId)) {
				recipeStepCollection = recipeStepRepository.findByRecipeId(new ObjectId(recipeId));
				if (recipeStepCollection != null) {

					response = new RecipeStep();
					BeanUtil.map(recipeStepCollection, response);
				}
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Recipe id is null");
			}
		} catch (BusinessException e) {
			logger.error("Error while addedit Recipe Step " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Recipe step" + e.getMessage());

		}
		return response;

	}

	@Override
	public FoodCommunity addEditFoodCommunity(FoodCommunity request) {
		FoodCommunity response = null;
		try {
			FoodCommunityCollection foodCommunityCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				foodCommunityCollection = foodCommunityRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (foodCommunityCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Food Community Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(foodCommunityCollection.getCreatedBy());
				request.setCreatedTime(foodCommunityCollection.getCreatedTime());
				BeanUtil.map(request, foodCommunityCollection);

			} else {
				foodCommunityCollection = new FoodCommunityCollection();
				BeanUtil.map(request, foodCommunityCollection);
				foodCommunityCollection.setCreatedBy("ADMIN");
				foodCommunityCollection.setUpdatedTime(new Date());
				foodCommunityCollection.setCreatedTime(new Date());
			}
			foodCommunityCollection = foodCommunityRepository.save(foodCommunityCollection);
			response = new FoodCommunity();
			BeanUtil.map(foodCommunityCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit food community " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit food community " + e.getMessage());

		}
		return response;

	}

	@Override
	public FoodCommunity getFoodCommunity(String id) {
		FoodCommunity response = null;
		try {
			FoodCommunityCollection foodCommunityCollection = foodCommunityRepository.findById(new ObjectId(id))
					.orElse(null);
			if (foodCommunityCollection != null) {
				response = new FoodCommunity();
				BeanUtil.map(foodCommunityCollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting food community " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting food community " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countFoodCommunities(Boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), FoodCommunityCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting food communities " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting food communities " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<FoodCommunity> getFoodCommunities(int size, int page, Boolean discarded, String searchTerm) {
		List<FoodCommunity> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, FoodCommunityCollection.class, FoodCommunity.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting food communities " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting food communities " + e.getMessage());

		}
		return response;
	}

	@Override
	public FoodCommunity discardFoodCommunity(String id, Boolean discarded) {
		FoodCommunity response = null;
		try {
			FoodCommunityCollection foodCommunityCollection = foodCommunityRepository.findById(new ObjectId(id))
					.orElse(null);
			if (foodCommunityCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Food Community Not found with Id");
			}
			foodCommunityCollection.setDiscarded(discarded);
			foodCommunityCollection.setUpdatedTime(new Date());
			foodCommunityCollection = foodCommunityRepository.save(foodCommunityCollection);

			response = new FoodCommunity();
			BeanUtil.map(foodCommunityCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while discarding Food Community " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while discarding Food Community " + e.getMessage());

		}
		return response;
	}

	@Override
	public FoodGroup addEditFoodGroup(FoodGroup request) {
		FoodGroup response = null;
		try {
			FoodGroupCollection foodGroupCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				foodGroupCollection = foodGroupRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (foodGroupCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Food Group Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(foodGroupCollection.getCreatedBy());
				request.setCreatedTime(foodGroupCollection.getCreatedTime());
				BeanUtil.map(request, foodGroupCollection);

			} else {
				foodGroupCollection = new FoodGroupCollection();
				BeanUtil.map(request, foodGroupCollection);
				foodGroupCollection.setCreatedBy("ADMIN");
				foodGroupCollection.setUpdatedTime(new Date());
				foodGroupCollection.setCreatedTime(new Date());
			}
			foodGroupCollection = foodGroupRepository.save(foodGroupCollection);
			response = new FoodGroup();
			BeanUtil.map(foodGroupCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit food group " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit food group " + e.getMessage());

		}
		return response;

	}

	@Override
	public FoodGroup getFoodGroup(String id) {
		FoodGroup response = null;
		try {
			FoodGroupCollection foodGroupCollection = foodGroupRepository.findById(new ObjectId(id)).orElse(null);
			if (foodGroupCollection != null) {
				response = new FoodGroup();
				BeanUtil.map(foodGroupCollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting food group " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting food group " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countFoodGroups(Boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), FoodGroupCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting food groups " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting food groups " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<FoodGroup> getFoodGroups(int size, int page, Boolean discarded, String searchTerm) {
		List<FoodGroup> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, FoodGroupCollection.class, FoodGroup.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting food groups " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting food groups " + e.getMessage());

		}
		return response;
	}

	@Override
	public FoodGroup discardFoodGroup(String id, Boolean discarded) {
		FoodGroup response = null;
		try {
			FoodGroupCollection foodGroupCollection = foodGroupRepository.findById(new ObjectId(id)).orElse(null);
			if (foodGroupCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Food Group Not found with Id");
			}
			foodGroupCollection.setDiscarded(discarded);
			foodGroupCollection.setUpdatedTime(new Date());
			foodGroupCollection = foodGroupRepository.save(foodGroupCollection);

			response = new FoodGroup();
			BeanUtil.map(foodGroupCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while discarding Food Group " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discarding Food Group " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutrientGoal addEditNutrientGoal(NutrientGoal request) {
		NutrientGoal response = null;
		try {
			NutrientGoalCollection nutrientGoalCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutrientGoalCollection = nutrientGoalRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nutrientGoalCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutrient Goal Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(nutrientGoalCollection.getCreatedBy());
				request.setCreatedTime(nutrientGoalCollection.getCreatedTime());
				BeanUtil.map(request, nutrientGoalCollection);

			} else {
				nutrientGoalCollection = new NutrientGoalCollection();
				BeanUtil.map(request, nutrientGoalCollection);
				nutrientGoalCollection.setCreatedBy("ADMIN");
				nutrientGoalCollection.setUpdatedTime(new Date());
				nutrientGoalCollection.setCreatedTime(new Date());
			}
			nutrientGoalCollection = nutrientGoalRepository.save(nutrientGoalCollection);
			response = new NutrientGoal();
			BeanUtil.map(nutrientGoalCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit nutrient goal " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit nutrient goal " + e.getMessage());

		}
		return response;

	}

	@Override
	public NutrientGoal getNutrientGoal(String id) {
		NutrientGoal response = null;
		try {
			NutrientGoalCollection nutrientGoalCollection = nutrientGoalRepository.findById(new ObjectId(id))
					.orElse(null);
			if (nutrientGoalCollection != null) {
				response = new NutrientGoal();
				BeanUtil.map(nutrientGoalCollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Nutrient Goal " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Nutrient Goal " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countNutrientGoals(Boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), NutrientGoalCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Nutrient Goals " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Nutrient Goals " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<NutrientGoal> getNutrientGoals(int size, int page, Boolean discarded, String searchTerm) {
		List<NutrientGoal> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NutrientGoalCollection.class, NutrientGoal.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Nutrient Goals " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Nutrient Goals " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutrientGoal discardNutrientGoal(String id, Boolean discarded) {
		NutrientGoal response = null;
		try {
			NutrientGoalCollection nutrientGoalCollection = nutrientGoalRepository.findById(new ObjectId(id))
					.orElse(null);
			if (nutrientGoalCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Nutrient Goal Not found with Id");
			}
			nutrientGoalCollection.setDiscarded(discarded);
			nutrientGoalCollection.setUpdatedTime(new Date());
			nutrientGoalCollection = nutrientGoalRepository.save(nutrientGoalCollection);

			response = new NutrientGoal();
			BeanUtil.map(nutrientGoalCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while discarding Nutrient Goal " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discarding Nutrient Goal " + e.getMessage());

		}
		return response;
	}

	@Override
	public RecipeNutrientType addEditRecipeNutrientType(RecipeNutrientType request) {
		RecipeNutrientType response = null;
		try {
			RecipeNutrientTypeCollection recipeNutrientTypeCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				recipeNutrientTypeCollection = recipeNutrientTypeRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (recipeNutrientTypeCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Recipe Nutrient Type Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(recipeNutrientTypeCollection.getCreatedBy());
				request.setCreatedTime(recipeNutrientTypeCollection.getCreatedTime());
				BeanUtil.map(request, recipeNutrientTypeCollection);

			} else {
				recipeNutrientTypeCollection = new RecipeNutrientTypeCollection();
				BeanUtil.map(request, recipeNutrientTypeCollection);
				recipeNutrientTypeCollection.setCreatedBy("ADMIN");
				recipeNutrientTypeCollection.setUpdatedTime(new Date());
				recipeNutrientTypeCollection.setCreatedTime(new Date());
			}
			recipeNutrientTypeCollection = recipeNutrientTypeRepository.save(recipeNutrientTypeCollection);
			response = new RecipeNutrientType();
			BeanUtil.map(recipeNutrientTypeCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Recipe Nutrient Type " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while add/edit Recipe Nutrient Type " + e.getMessage());

		}
		return response;

	}

	@Override
	public RecipeNutrientType getRecipeNutrientType(String id) {
		RecipeNutrientType response = null;
		try {
			RecipeNutrientTypeCollection recipeNutrientTypeCollection = recipeNutrientTypeRepository
					.findById(new ObjectId(id)).orElse(null);
			if (recipeNutrientTypeCollection != null) {
				response = new RecipeNutrientType();
				BeanUtil.map(recipeNutrientTypeCollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Recipe Nutrient Type " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Recipe Nutrient Type " + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer countRecipeNutrientTypes(Boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), RecipeNutrientTypeCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Recipe Nutrient Types " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting Recipe Nutrient Types " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<RecipeNutrientType> getRecipeNutrientTypes(int size, int page, Boolean discarded, String searchTerm) {
		List<RecipeNutrientType> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("value").regex("^" + searchTerm, "i"),
						new Criteria("value").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, RecipeNutrientTypeCollection.class, RecipeNutrientType.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Recipe Nutrient Types " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Recipe Nutrient Types " + e.getMessage());

		}
		return response;
	}

	@Override
	public RecipeNutrientType discardRecipeNutrientType(String id, Boolean discarded) {
		RecipeNutrientType response = null;
		try {
			RecipeNutrientTypeCollection recipeNutrientTypeCollection = recipeNutrientTypeRepository
					.findById(new ObjectId(id)).orElse(null);
			if (recipeNutrientTypeCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Recipe Nutrient Type Not found with Id");
			}
			recipeNutrientTypeCollection.setDiscarded(discarded);
			recipeNutrientTypeCollection.setUpdatedTime(new Date());
			recipeNutrientTypeCollection = recipeNutrientTypeRepository.save(recipeNutrientTypeCollection);

			response = new RecipeNutrientType();
			BeanUtil.map(recipeNutrientTypeCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while discarding Recipe Nutrient Type " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while discarding Recipe Nutrient Type " + e.getMessage());

		}
		return response;
	}

	@Override
	public NutritionDisease addEditDisease(NutritionDisease request) {
		NutritionDisease response = null;
		try {
			NutritionDiseaseCollection nutritionDiseaseCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionDiseaseCollection = nutritionDiseaseRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (nutritionDiseaseCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutrition Disease Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(nutritionDiseaseCollection.getCreatedBy());
				request.setCreatedTime(nutritionDiseaseCollection.getCreatedTime());
				BeanUtil.map(request, nutritionDiseaseCollection);

			} else {
				nutritionDiseaseCollection = new NutritionDiseaseCollection();
				BeanUtil.map(request, nutritionDiseaseCollection);
				nutritionDiseaseCollection.setCreatedBy("ADMIN");
				nutritionDiseaseCollection.setUpdatedTime(new Date());
				nutritionDiseaseCollection.setCreatedTime(new Date());
			}
			nutritionDiseaseCollection = nutritionDiseaseRepository.save(nutritionDiseaseCollection);
			response = new NutritionDisease();
			BeanUtil.map(nutritionDiseaseCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Nutrition Disease  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while add/edit Nutrition Disease " + e.getMessage());

		}

		return response;
	}

	@Override
	public NutritionDisease getDisease(String id) {
		NutritionDisease response = null;
		try {
			NutritionDiseaseCollection nutritionDiseaseCollection = nutritionDiseaseRepository
					.findById(new ObjectId(id)).orElse(null);
			if (nutritionDiseaseCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "no such nutrition disease id");
			}

			BeanUtil.map(nutritionDiseaseCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public List<NutritionDisease> getDiseases(int size, int page, Boolean discarded, String searchTerm) {
		List<NutritionDisease> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("disease").regex("^" + searchTerm, "i"),
						new Criteria("disease").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NutritionDiseaseCollection.class, NutritionDisease.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting nutrition diseases " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting nutrition diseases " + e.getMessage());

		}
		return response;

	}

	@Override
	public NutritionDisease deleteDisease(String id, Boolean discarded) {
		NutritionDisease response = null;
		try {
			NutritionDiseaseCollection nutritionDiseaseCollection = nutritionDiseaseRepository
					.findById(new ObjectId(id)).orElse(null);
			if (nutritionDiseaseCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			nutritionDiseaseCollection.setDiscarded(discarded);
			nutritionDiseaseCollection.setUpdatedTime(new Date());
			nutritionDiseaseCollection = nutritionDiseaseRepository.save(nutritionDiseaseCollection);
			response = new NutritionDisease();
			BeanUtil.map(nutritionDiseaseCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while deleting the nutrition disease  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the nutrition disease");
		}

		return response;
	}

	@Override
	public Integer countDisease(Boolean discarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			criteria = criteria.orOperator(new Criteria("disease").regex("^" + searchTerm, "i"),
					new Criteria("disease").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), NutritionDiseaseCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting nutrition diseases " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting nutrition diseases " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean updateIngredient(String id) {
		Boolean response = null;
		DecimalFormat df = new DecimalFormat("#.######");
		df.setRoundingMode(RoundingMode.CEILING);

		try {
//			IngredientCollection ingredientCollection = ingredientRepository.findById(new ObjectId(id)).orElse(null);
//			if (ingredientCollection == null) {
//				throw new BusinessException(ServiceError.NotFound, "Ingredients Not found with Id");
//			}
			List<IngredientCollection> ingredientCollectionList = ingredientRepository.findAll();
			for (IngredientCollection ingredientCollection : ingredientCollectionList) {

				if (ingredientCollection.getProteinAminoAcidNutrients() != null
						&& !ingredientCollection.getIsUpdated()) {
					Map<String, String> map = ingredientCollection.getProteinAminoAcidNutrients();
					double proteinFloatValue = 0.0;
					for (Map.Entry<String, String> entry : map.entrySet()) {
						if (entry.getKey().contains("Protein")) {
							if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
								String proteinValue = entry.getValue();
								proteinFloatValue = Double.parseDouble(proteinValue);
								System.out.println("Protein" + proteinFloatValue);
							}
						}
					}
					if (proteinFloatValue > 0.0) {
						for (Map.Entry<String, String> entry : map.entrySet()) {
							if (entry.getKey().contains("Glutamic Acid")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Tyrosine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Serine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Proline")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Glycine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Aspartic Acid")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Arginine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Alanine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Valine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Phenylalanine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Lysine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Luecine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Histidine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Methionine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Cystine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Isoleucine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Threonine")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
							if (entry.getKey().contains("Tryptophan")) {
								if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
									double d = Double.parseDouble(entry.getValue());
									d = (double) (d * proteinFloatValue) / 100;
									entry.setValue(df.format(d));
								}
							}
						}
					}
					ingredientCollection.setUpdatedTime(new Date());
					ingredientCollection.setIsUpdated(true);
					ingredientCollection = ingredientRepository.save(ingredientCollection);
					response = true;
					System.out.println("Ingredient Id: " + ingredientCollection.getId() + ",Ingredient Name: "
							+ ingredientCollection.getName() + "," + true);
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while update ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while update ingredient " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean updateRecipe(int size, int page, String recipeId) {
		Boolean response = false;
		Double value = 0.0;
		List<RecipeCollection> list = null;
		ESRecipeDocument document = null;
		DecimalFormat df = new DecimalFormat("#.######");
		df.setRoundingMode(RoundingMode.CEILING);
		try {
			Criteria criteria = new Criteria("discarded").is(false);
//			criteria.and("isUpdated").is(true);
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			list = mongoTemplate.aggregate(aggregation, RecipeCollection.class, RecipeCollection.class)
					.getMappedResults();
//			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(recipeId)).orElse(null);
//			if (recipeCollection == null) {
//				throw new BusinessException(ServiceError.NotFound, "recipe Not found with Id");
//			}
			for (RecipeCollection recipeCollection : list) {
//				List<RecipeItem> recipeItems = recipeCollection.getIngredients();
//				for (RecipeItem item : recipeItems) {
//					IngredientCollection ingredientCollection = ingredientRepository.findById(item.getId())
//							.orElse(null);
//					if (ingredientCollection == null) {
//						throw new BusinessException(ServiceError.NotFound, "Ingredients Not found with Id");
//					}
//					if (ingredientCollection.getProteinAminoAcidNutrients() != null) {
//						Map<String, String> map = ingredientCollection.getProteinAminoAcidNutrients();
//						double proteinFloatValue = 0.0;
//						for (Map.Entry<String, String> entry : map.entrySet()) {
//							if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//								if (entry.getKey().contains("Protein")) {
//									proteinFloatValue = Double.parseDouble(entry.getValue());
//								}
//							}
//						}
//						if (proteinFloatValue > 0.0) {
//							for (Map.Entry<String, String> entry : map.entrySet()) {
//								if (entry.getKey().contains("Glutamic Acid")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Glutamic Acid")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Tyrosine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Tyrosine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Serine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Serine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Proline")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Proline")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Glycine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Glycine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Aspartic Acid")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Aspartic Acid")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Arginine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Arginine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Alanine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Alanine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Valine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Valine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Phenylalanine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Phenylalanine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Lysine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Lysine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Luecine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Luecine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Histidine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Histidine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Methionine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Methionine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Cystine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Cystine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Isoleucine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Isoleucine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Threonine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Threonine")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//								if (entry.getKey().contains("Tryptophan")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										double d = Double.parseDouble(entry.getValue());
//										d = (double) d / 100;
//										if (item.getProteinAminoAcidNutrients() != null) {
//											for (Map.Entry<String, String> entry2 : item.getProteinAminoAcidNutrients()
//													.entrySet()) {
//												if (entry2.getKey().contains("Tryptophan")) {
//													QuantityEnum quantityEnumType = item.getQuantity().getType();
//													if (item.getEquivalentMeasurements() != null) {
//														for (EquivalentQuantities equivalentQuantities : item
//																.getEquivalentMeasurements()) {
//															if (equivalentQuantities.getServingType()
//																	.equals(quantityEnumType)) {
//																value = equivalentQuantities.getValue();
//															}
//														}
//														d = d * value * item.getQuantity().getValue();
//													} else {
//														d = d * item.getQuantity().getValue();
//													}
//													entry2.setValue(df.format(d));
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//
//				recipeCollection = recipeRepository.save(recipeCollection);
//				double GlutamicAcid = 0.0;
//				double Tyrosine = 0.0;
//				double Serine = 0.0;
//				double Proline = 0.0;
//				double Glycine = 0.0;
//				double AsparticAcid = 0.0;
//				double Arginine = 0.0;
//				double Alanine = 0.0;
//				double Valine = 0.0;
//				double Phenylalanine = 0.0;
//				double Lysine = 0.0;
//				double Luecine = 0.0;
//				double Histidine = 0.0;
//				double Methionine = 0.0;
//				double Cystine = 0.0;
//				double Isoleucine = 0.0;
//				double Threonine = 0.0;
//				double Tryptophan = 0.0;
//
//				if (recipeCollection.getProteinAminoAcidNutrients() != null) {
//					Map<String, String> map = recipeCollection.getProteinAminoAcidNutrients();
//
//					for (RecipeItem item : recipeItems) {
//						if (item.getProteinAminoAcidNutrients() != null) {
//							Map<String, String> map1 = item.getProteinAminoAcidNutrients();
//							for (Map.Entry<String, String> entry : map1.entrySet()) {
//								if (entry.getKey().contains("Glutamic Acid")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										GlutamicAcid = GlutamicAcid + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Tyrosine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Tyrosine = Tyrosine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Serine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Serine = Serine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Proline")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Proline = Proline + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Glycine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Glycine = Glycine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Aspartic Acid")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										AsparticAcid = AsparticAcid + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Arginine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Arginine = Arginine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Alanine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Alanine = Alanine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Valine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Valine = Valine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Phenylalanine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Phenylalanine = Phenylalanine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Lysine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Lysine = Lysine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Luecine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Luecine = Luecine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Histidine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Histidine = Histidine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Methionine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Methionine = Methionine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Cystine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Cystine = Cystine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Isoleucine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Isoleucine = Isoleucine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Threonine")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Threonine = Threonine + Double.parseDouble(entry.getValue());
//									}
//								}
//								if (entry.getKey().contains("Tryptophan")) {
//									if (!DPDoctorUtils.anyStringEmpty(entry.getValue())) {
//										Tryptophan = Tryptophan + Double.parseDouble(entry.getValue());
//									}
//								}
//							}
//						}
//					}
//					for (Map.Entry<String, String> collectionEntry : map.entrySet()) {
//						if (collectionEntry.getKey().contains("Glutamic Acid")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(GlutamicAcid)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Tyrosine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Tyrosine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Serine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Serine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Proline")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Proline)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Glycine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Glycine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Aspartic Acid")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(AsparticAcid)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Arginine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Arginine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Alanine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Alanine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Valine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Valine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Phenylalanine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Phenylalanine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Lysine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Lysine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Luecine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Luecine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Histidine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Histidine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Methionine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Methionine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Cystine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Cystine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Isoleucine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Isoleucine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Threonine")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Threonine)));
//							}
//						}
//						if (collectionEntry.getKey().contains("Tryptophan")) {
//							if (!DPDoctorUtils.anyStringEmpty(collectionEntry.getValue())) {
//								collectionEntry.setValue(String.valueOf(df.format(Tryptophan)));
//							}
//						}
//						response = true;
//					}
//				}
//				recipeCollection.setIsUpdated(true);
//				recipeCollection = recipeRepository.save(recipeCollection);
				document = eSRecipeRepository.findById(recipeCollection.getId().toString()).orElse(null);
				if (document != null) {
//					System.out.println("Recipe Id: " + recipeCollection.getId() + ",Recipe Name: "
//							+ recipeCollection.getName() + "," + true);
					BeanUtil.map(recipeCollection, document);
					eSRecipeRepository.save(document);
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recipe " + e.getMessage());
		}
		return response;
	}
}
