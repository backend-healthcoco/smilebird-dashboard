package com.dpdocter.services.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Offer;
import com.dpdocter.collections.OfferCollection;
import com.dpdocter.elasticsearch.document.ESOfferDocument;
import com.dpdocter.elasticsearch.repository.ESOfferRepository;
import com.dpdocter.elasticsearch.services.ESOfferServices;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OfferRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.OfferResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.NutritionService;
import com.dpdocter.services.OfferServices;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PrescriptionServices;

import common.util.web.DPDoctorUtils;;

@Transactional
@Service
public class OfferServicesImpl implements OfferServices {

	private static Logger logger = LogManager.getLogger(OfferServicesImpl.class.getName());

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private ESOfferRepository esOfferRepository;

	@Autowired
	private ESOfferServices esOfferServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private NutritionService nutritionService;

	@Autowired
	private PrescriptionServices prescriptionService;

	@Autowired
	private PatientTreatmentServices patientTreatmentService;

	@Autowired
	private FileManager fileManager;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public OfferResponse addEditOffer(Offer request) {
		OfferResponse response = null;

		try {
			if (request.getTitleImage() != null) {
				request.getTitleImage()
						.setImageUrl(!DPDoctorUtils.anyStringEmpty(request.getTitleImage().getImageUrl())
								? request.getTitleImage().getImageUrl().replace(imagePath, "")
								: "");

				request.getTitleImage()
						.setThumbnailUrl(!DPDoctorUtils.anyStringEmpty(request.getTitleImage().getThumbnailUrl())
								? request.getTitleImage().getThumbnailUrl().replace(imagePath, "")
								: "");
			}
			if (request.getType() != null && request.getType().isEmpty()) {
				request.setType(null);
			}
			if (request.getProductType() != null && request.getProductType().isEmpty()) {
				request.setProductType(null);
			}

			OfferCollection offerCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				offerCollection = offerRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (offerCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Offer Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(offerCollection.getCreatedBy());
				request.setCreatedTime(offerCollection.getCreatedTime());
				offerCollection = new OfferCollection();
				BeanUtil.map(request, offerCollection);

			} else {
				offerCollection = new OfferCollection();
				BeanUtil.map(request, offerCollection);
				offerCollection.setCreatedBy("ADMIN");
				offerCollection.setUpdatedTime(new Date());
				offerCollection.setCreatedTime(new Date());
			}
			offerCollection = offerRepository.save(offerCollection);
			ESOfferDocument esOfferDocument = new ESOfferDocument();
			BeanUtil.map(offerCollection, esOfferDocument);
			esOfferServices.addOffer(esOfferDocument);
			response = new OfferResponse();
			BeanUtil.map(offerCollection, response);
			if (offerCollection.getTreatmentServiceIds() != null && !offerCollection.getTreatmentServiceIds().isEmpty())
				response.setTreatmentServices(
						patientTreatmentService.getTreatmentServices(offerCollection.getTreatmentServiceIds()));
			if (offerCollection.getNutritionPlanIds() != null && !offerCollection.getNutritionPlanIds().isEmpty())
				response.setNutritionPlans(nutritionService.getNutritionPlans(offerCollection.getNutritionPlanIds()));
			if (offerCollection.getSubscriptionPlanIds() != null && !offerCollection.getSubscriptionPlanIds().isEmpty())
				response.setSubscriptionPlans(
						nutritionService.getSubscritionPlans(offerCollection.getSubscriptionPlanIds()));
			if (offerCollection.getDrugIds() != null && !offerCollection.getDrugIds().isEmpty())
				response.setDrugs(prescriptionService.getDrugs(offerCollection.getDrugIds()));
		} catch (BusinessException e) {
			logger.error("Error while addedit Offer " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Offer " + e.getMessage());

		}
		return response;
	}

	@Override
	public OfferResponse getOffer(String id) {
		OfferResponse response = null;
		try {

			OfferCollection offerCollection = offerRepository.findById(new ObjectId(id)).orElse(null);
			response = new OfferResponse();

			BeanUtil.map(offerCollection, response);
			if (offerCollection.getTreatmentServiceIds() != null && !offerCollection.getTreatmentServiceIds().isEmpty())
				response.setTreatmentServices(
						patientTreatmentService.getTreatmentServices(offerCollection.getTreatmentServiceIds()));
			if (offerCollection.getNutritionPlanIds() != null && !offerCollection.getNutritionPlanIds().isEmpty())
				response.setNutritionPlans(nutritionService.getNutritionPlans(offerCollection.getNutritionPlanIds()));
			if (offerCollection.getSubscriptionPlanIds() != null && !offerCollection.getSubscriptionPlanIds().isEmpty())
				response.setSubscriptionPlans(
						nutritionService.getSubscritionPlans(offerCollection.getSubscriptionPlanIds()));
			if (offerCollection.getDrugIds() != null && !offerCollection.getDrugIds().isEmpty())
				response.setDrugs(prescriptionService.getDrugs(offerCollection.getDrugIds()));
			if (response != null) {
				if (response.getTitleImage() != null) {
					if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage().getImageUrl()))
						response.getTitleImage().setImageUrl(getFinalImageURL(response.getTitleImage().getImageUrl()));

					if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage().getThumbnailUrl()))
						response.getTitleImage()
								.setThumbnailUrl(getFinalImageURL(response.getTitleImage().getThumbnailUrl()));

				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting nutrients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrients " + e.getMessage());

		}
		return response;
	}

	@Override
	public OfferResponse deleteOffer(String id, boolean discarded) {
		OfferResponse response = null;
		try {
			ESOfferDocument esOfferDocument = null;
			OfferCollection offerCollection = offerRepository.findById(new ObjectId(id)).orElse(null);
			if (offerCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Offer Not found with Id");
			}
			offerCollection.setDiscarded(discarded);
			offerCollection.setUpdatedTime(new Date());
			offerCollection = offerRepository.save(offerCollection);
			esOfferDocument = esOfferRepository.findById(id).orElse(null);
			if (esOfferDocument != null) {
				esOfferDocument.setUpdatedTime(new Date());
				esOfferDocument.setDiscarded(discarded);
				esOfferRepository.save(esOfferDocument);
			}
			response = new OfferResponse();
			BeanUtil.map(offerCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete offer " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard offer " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Offer> getOffers(int size, int page, Boolean discarded, String searchTerm, String productId,
			String offerType, String productType) {
		List<Offer> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.and("title").regex("^" + searchTerm);
			}

			if (!DPDoctorUtils.anyStringEmpty(offerType)) {
				criteria.and("type").is(offerType.toUpperCase());
			}

			if (!DPDoctorUtils.anyStringEmpty(productType)) {
				criteria.and("productType").is(productType.toUpperCase());
			}
			if (!DPDoctorUtils.anyStringEmpty(productId)) {
				criteria.orOperator(criteria.and("drugIds").is(new ObjectId(productId)),
						criteria.and("nutritionPlanIds").is(new ObjectId(productId)),
						criteria.and("subscriptionPlanIds").is(new ObjectId(productId)),
						criteria.and("treatmentServiceIds").is(new ObjectId(productId)));
			}
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, OfferCollection.class, Offer.class).getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Offer " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Offer " + e.getMessage());

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
	public ImageURLResponse uploadImages(FileDetails request, String module) {
		ImageURLResponse response = new ImageURLResponse();
		try {
			request.setFileName(request.getFileName() + new Date().getTime());
			String path = module + File.separator + "images";
			ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request, path, true);
			response.setImageUrl(getFinalImageURL(imageURLResponse.getImageUrl()));
			response.setThumbnailUrl(getFinalImageURL(imageURLResponse.getThumbnailUrl()));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}
}
