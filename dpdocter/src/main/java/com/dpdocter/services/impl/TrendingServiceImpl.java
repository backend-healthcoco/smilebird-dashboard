package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Offer;
import com.dpdocter.beans.Trending;
import com.dpdocter.collections.OfferCollection;
import com.dpdocter.collections.TrendingCollection;
import com.dpdocter.elasticsearch.document.ESTrendingDocument;
import com.dpdocter.elasticsearch.repository.ESTrendingRepository;
import com.dpdocter.elasticsearch.services.ESOfferServices;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OfferRepository;
import com.dpdocter.repository.TrendingRepository;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.BlogService;
import com.dpdocter.services.TrendingService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class TrendingServiceImpl implements TrendingService {

	private static Logger logger = LogManager.getLogger(TrendingServiceImpl.class.getName());

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private TrendingRepository trendingRepository;

	@Autowired
	private ESTrendingRepository esTrendingRepository;

	@Autowired
	private ESOfferServices esOfferServices;

	@Autowired
	private BlogService blogService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public TrendingResponse addEditTrending(Trending request) {
		TrendingResponse response = null;

		try {
			TrendingCollection trendingCollection = null;
			if (request.getType() != null && request.getType().isEmpty()) {
				request.setType(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				trendingCollection = trendingRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (trendingCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Trending Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(trendingCollection.getCreatedBy());
				request.setCreatedTime(trendingCollection.getCreatedTime());
				trendingCollection = new TrendingCollection();
				BeanUtil.map(request, trendingCollection);

			} else {
				trendingCollection = new TrendingCollection();
				BeanUtil.map(request, trendingCollection);
				trendingCollection.setCreatedBy("ADMIN");
				trendingCollection.setUpdatedTime(new Date());
				trendingCollection.setCreatedTime(new Date());
			}
			trendingCollection = trendingRepository.save(trendingCollection);
			ESTrendingDocument esTrendingDocument = new ESTrendingDocument();
			BeanUtil.map(trendingCollection, esTrendingDocument);
			esOfferServices.addTrending(esTrendingDocument);
			response = new TrendingResponse();
			BeanUtil.map(trendingCollection, response);
			if (response != null) {

				if (!DPDoctorUtils.anyStringEmpty(response.getBlogId())) {
					response.setBlog(blogService.getBlog(response.getBlogId(), null));
				} else if (!DPDoctorUtils.anyStringEmpty(response.getOfferId())) {
					OfferCollection offerCollection = offerRepository.findById(trendingCollection.getOfferId()).orElse(null);
					Offer offer = new Offer();
					BeanUtil.map(offerCollection, offer);
					response.setOffer(offer);
					if (offer != null) {
						if (offer.getTitleImage() != null) {
							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
								offer.getTitleImage()
										.setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
								offer.getTitleImage()
										.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

						}
					}
				}
			}

		} catch (

		BusinessException e) {
			logger.error("Error while addedit Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Trending " + e.getMessage());

		}
		return response;
	}

	@Override
	public TrendingResponse getTrending(String id) {
		TrendingResponse response = null;
		try {
			response = new TrendingResponse();
			TrendingCollection trendingCollection = trendingRepository.findById(new ObjectId(id)).orElse(null);
			BeanUtil.map(trendingCollection, response);
			if (response != null) {
				if (!DPDoctorUtils.anyStringEmpty(trendingCollection.getBlogId())) {
					response.setBlog(blogService.getBlog(response.getBlogId(), null));
				} else if (!DPDoctorUtils.anyStringEmpty(trendingCollection.getOfferId())) {
					OfferCollection offerCollection = offerRepository.findById(trendingCollection.getOfferId()).orElse(null);
					Offer offer = new Offer();
					BeanUtil.map(offerCollection, offer);
					response.setOffer(offer);
					if (offer != null) {
						if (offer.getTitleImage() != null) {
							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
								offer.getTitleImage()
										.setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
								offer.getTitleImage()
										.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

						}
					}

				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Trending " + e.getMessage());

		}
		return response;
	}

	@Override
	public TrendingResponse deleteTrending(String id, boolean discarded) {
		TrendingResponse response = null;
		try {
			ESTrendingDocument esTrendingDocument = null;
			TrendingCollection trendingCollection = trendingRepository.findById(new ObjectId(id)).orElse(null);
			if (trendingCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Trending Not found with Id");
			}
			trendingCollection.setDiscarded(discarded);
			trendingCollection.setUpdatedTime(new Date());
			trendingCollection = trendingRepository.save(trendingCollection);
			esTrendingDocument = esTrendingRepository.findById(id).orElse(null);
			if (esTrendingDocument != null) {
				esTrendingDocument.setUpdatedTime(new Date());
				esTrendingDocument.setDiscarded(discarded);
				esTrendingRepository.save(esTrendingDocument);
			}
			response = new TrendingResponse();
			BeanUtil.map(trendingCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while delete Trending " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<TrendingResponse> getTrendings(int size, int page, Boolean discarded, String searchTerm,
			String trendingType, String resourceType,String toTime,String fromTime) {
		List<TrendingResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(trendingType)) {
				criteria.and("type").is(trendingType.toUpperCase());
			}

			if (!DPDoctorUtils.anyStringEmpty(resourceType)) {
				criteria.and("resourceType").is(resourceType.toUpperCase());
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			Criteria searchCriteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchCriteria.orOperator(Criteria.where("offer.title").regex("^" + searchTerm, "i"),
						Criteria.where("offer.title").regex("^" + searchTerm));

			Aggregation aggregation = null;
			
//			criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
//					new Criteria("title").regex("^" + searchTerm));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("blog_cl", "blogId", "_id", "blog"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$blog").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("offer_cl", "offerId", "_id", "offer"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$offer").append("preserveNullAndEmptyArrays", true))),Aggregation.match(searchCriteria),
						Aggregation.sort(new Sort(Direction.ASC, "rank")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("blog_cl", "blogId", "_id", "blog"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$blog").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("offer_cl", "offerId", "_id", "offer"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$offer").append("preserveNullAndEmptyArrays", true))),Aggregation.match(searchCriteria),
						Aggregation.sort(new Sort(Direction.ASC, "rank")));
			}

			response = mongoTemplate.aggregate(aggregation, TrendingCollection.class, TrendingResponse.class)
					.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (TrendingResponse trendingResponse : response) {
					if (trendingResponse.getOffer() != null) {

						if (trendingResponse.getOffer().getTitleImage() != null) {
							if (!DPDoctorUtils
									.anyStringEmpty(trendingResponse.getOffer().getTitleImage().getImageUrl()))
								trendingResponse.getOffer().getTitleImage().setImageUrl(
										getFinalImageURL(trendingResponse.getOffer().getTitleImage().getImageUrl()));

							if (!DPDoctorUtils
									.anyStringEmpty(trendingResponse.getOffer().getTitleImage().getThumbnailUrl()))
								trendingResponse.getOffer().getTitleImage().setThumbnailUrl(getFinalImageURL(
										trendingResponse.getOffer().getTitleImage().getThumbnailUrl()));

						}
					} else if (trendingResponse.getBlog() != null) {
						if (!DPDoctorUtils.anyStringEmpty(trendingResponse.getBlog().getTitleImage()))
							trendingResponse.getBlog()
									.setTitleImage(imagePath + trendingResponse.getBlog().getTitleImage());

					}
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Trending " + e.getMessage());

		}
		return response;

	}

	@Override
	public TrendingResponse updateRank(String id, int rank) {
		TrendingResponse response = null;
		try {
			ESTrendingDocument esTrendingDocument = null;
			TrendingCollection trendingCollection = trendingRepository.findById(new ObjectId(id)).orElse(null);
			if (trendingCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Trending Not found with Id");
			}
			trendingCollection.setRank(rank);
			trendingCollection.setUpdatedTime(new Date());
			trendingCollection = trendingRepository.save(trendingCollection);
			esTrendingDocument = esTrendingRepository.findById(id).orElse(null);
			if (esTrendingDocument != null) {
				esTrendingDocument.setUpdatedTime(new Date());
				esTrendingDocument.setRank(rank);
				esTrendingRepository.save(esTrendingDocument);
			}
			response = new TrendingResponse();
			BeanUtil.map(trendingCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while update rank Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while update rank Trending " + e.getMessage());

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
	public Integer countTrendings(Boolean discarded, String searchTerm, String trendingType, String resourceType) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(trendingType)) {
				criteria.and("type").is(trendingType.toUpperCase());
			}

			if (!DPDoctorUtils.anyStringEmpty(resourceType)) {
				criteria.and("resourceType").is(resourceType.toUpperCase());
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.ASC, "rank")));

			response = mongoTemplate.aggregate(aggregation, TrendingCollection.class, TrendingResponse.class)
					.getMappedResults().size();

		} catch (BusinessException e) {
			logger.error("Error while counting Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Trending " + e.getMessage());

		}
		return response;

	}

}
