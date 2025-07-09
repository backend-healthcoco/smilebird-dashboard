package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Feeds;
import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.Forum;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;

import com.dpdocter.beans.UserPost;
import com.dpdocter.collections.CommentCollection;
import com.dpdocter.collections.FeedsCollection;
import com.dpdocter.collections.ForumResponseCollection;
import com.dpdocter.collections.LanguageCollection;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;

import com.dpdocter.repository.CommentRepository;
import com.dpdocter.repository.FeedsRepository;
import com.dpdocter.repository.ForumResponseRepository;
import com.dpdocter.repository.LanguageRepository;

import com.dpdocter.services.CommunityBuildingServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.webservices.AppointmentApi;
import com.dpdocter.webservices.CommunityBuildingApi;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class CommunityBuildingServiceImpl implements CommunityBuildingServices {

	private static Logger logger = LogManager.getLogger(CommunityBuildingServiceImpl.class.getName());

	

	@Autowired
	private ForumResponseRepository forumRepository;

	@Autowired
	private FeedsRepository feedsRepository;

	

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private LanguageRepository languageRepository;
	
	@Autowired
	private PushNotificationServices pushNotificationService; 
	
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public ForumResponse addEditForumResponse(ForumRequest request) {
		ForumResponse response = null;
		try {
			ForumResponseCollection collection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = forumRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (collection != null) {

					BeanUtil.map(request, collection);
					
//					if(request.getUserIds()!=null)
//					{
//						collection.setUserIds(null);
//						collection.setUserIds(request.getUserIds());
//					}
					collection.setUpdatedTime(new Date());
					forumRepository.save(collection);
				} else
					throw new BusinessException(ServiceError.NoRecord);
			} else {
				collection = new ForumResponseCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				forumRepository.save(collection);
			}
			response = new ForumResponse();
			BeanUtil.map(collection, response);

		} catch (BusinessException e) {
			logger.error("Error while getting learning screen");
			throw new BusinessException(ServiceError.Unknown, "Error while getting learning screen");
		}
		return response;
	}

	@Override
	public List<Forum> getForumResponse(int page, int size, String searchTerm, Boolean discarded) {
		List<Forum> response = null;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("text").regex("^" + searchTerm, "i"),
						new Criteria("text").regex("^" + searchTerm),
						new Criteria("title").regex("^" + searchTerm,"i"),
						new Criteria("title").regex("^" + searchTerm),
						new Criteria("userName").regex("^" + searchTerm),
						new Criteria("userName").regex("^" + searchTerm,"i"));
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("userId", "$userId").append("userName", "$userName")
							.append("text", "$text").append("title", "$title").append("comments", "$comments")));
			
			

			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
			new BasicDBObject("_id", "$_id")
			.append("userId", new BasicDBObject("$first", "$userId"))
			.append("userName", new BasicDBObject("$first", "$userName"))
			.append("text", new BasicDBObject("$first", "$text"))
				.append("title", new BasicDBObject("$first", "$title"))
				.append("comments", new BasicDBObject("$addToSet", "$comments"))));
	

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
				//		Aggregation.lookup("comment_cl", "_id","postId", "comments"),
				//		Aggregation.unwind("comments"),
				//		Aggregation.match(new Criteria("comments.discarded").is(false)),
						Aggregation.match(criteria),
					//	group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
					//	Aggregation.lookup("comment_cl", "_id","postId", "comments"),
					//	Aggregation.unwind("comments"),
					//	Aggregation.match(new Criteria("comments.discarded").is(false)),
						Aggregation.match(criteria),
					//	group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			response = mongoTemplate.aggregate(aggregation, ForumResponseCollection.class, Forum.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting forum "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting forum");
		}
		return response;
	}

	@Override
	public List<FeedsResponse> getLearningSession(int page, int size, Boolean discarded, String searchTerm,
			String languageId,String type) {
		List<FeedsResponse> response = null;
		try {
			LanguageCollection collection=null; 
			if(DPDoctorUtils.anyStringEmpty(languageId)) {
				collection=languageRepository.findByName("English");
			languageId=collection.getId().toString();
			}
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("multilingual.text").regex("^" + searchTerm, "i"),
						new Criteria("multilingual.text").regex("^" + searchTerm));
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("user", "$user")
					.append("multilingual.languageId", "$multilingual.languageId")
					.append("multilingual.name", "$multilingual.name")
					.append("multilingual.text", "$multilingual.text")
					.append("multilingual.imageUrl", "$multilingual.imageUrl")
					.append("multilingual.thumbnailUrl", "$multilingual.thumbnailUrl")
					.append("multilingual.videoUrl", "$multilingual.videoUrl")
					.append("type", "$type")
					.append("postByAdminId", "$postByAdminId")
					.append("type", "$type")
					.append("postByAdminName", "$postByAdminName")
					.append("postByDoctorId", "$postByDoctorId")
					.append("doctorName", "$doctorName")
					.append("postByExpertName", "$postByExpertName")
					.append("postByExpertId", "$postByExpertId")
					.append("userIds", "$userIds")
					.append("isSaved", "$isSaved")
					.append("likes", "$likes")
					.append("discarded", "$discarded")
					.append("comments", "$comments")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("user", new BasicDBObject("$first", "$user"))
					.append("multilingual", new BasicDBObject("$first", "$multilingual"))
					.append("type", new BasicDBObject("$first", "$type"))
					.append("postByAdminId", new BasicDBObject("$first", "$postByAdminId"))
					.append("postByAdminName", new BasicDBObject("$first", "$postByAdminName"))
					.append("postByDoctorId", new BasicDBObject("$first", "$postByDoctorId"))
					.append("postByDoctorName", new BasicDBObject("$first", "$postByDoctorName"))
					.append("postByUserId", new BasicDBObject("$first", "$postByUserId"))
					.append("postByUserName", new BasicDBObject("$first", "$postByUserName"))
					.append("postByExpertName", new BasicDBObject("$first", "$postByExpertName"))
					.append("postByExpertId", new BasicDBObject("$first", "$postByExpertId"))
					
					.append("userIds", new BasicDBObject("$addToSet", "$userIds"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("isSaved", new BasicDBObject("$first", "$isSaved"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("likes", new BasicDBObject("$first", "$likes"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("comments", new BasicDBObject("$addToSet", "$comments"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
					
						Aggregation.match(criteria),
						Aggregation.unwind("multilingual", true),
						Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
						Aggregation.lookup("comment_cl", "_id","postId", "comments"),
						Aggregation.unwind("comments"),
						Aggregation.match(new Criteria("comments.discarded").is(false)),group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.unwind("multilingual", true),
						Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
						Aggregation.lookup("comment_cl", "_id","postId", "comments"),
						Aggregation.unwind("comments"),
						Aggregation.match(new Criteria("comments.discarded").is(false)),group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			System.out.println("Aggregation "+aggregation);
			response = mongoTemplate.aggregate(aggregation, FeedsCollection.class, FeedsResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting learning screen "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting learning screen");
		}
		return response;
	}

	@Override
	public List<FeedsResponse> getArticles(int size, int page, String searchTerm, Boolean discarded,
			String languageId) {
		List<FeedsResponse> response = null;
		try {
			LanguageCollection collection=null; 
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			
			if(!DPDoctorUtils.anyStringEmpty(languageId)) {
				collection=languageRepository.findByName("Engilsh");
			languageId=collection.getId().toString();
			}
			

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("multilingual.text").regex("^" + searchTerm, "i"),
						new Criteria("multilingual.text").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
						Aggregation.unwind("multilingual",true),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
						Aggregation.unwind("multilingual",true),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			response = mongoTemplate.aggregate(aggregation, FeedsCollection.class, FeedsResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting articles "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting articles");
		}
		return response;
	}


	
	void notify(FeedsCollection collection)
	{
		Aggregation aggregation=null;
		
		 aggregation = Aggregation.newAggregation(
				
				Aggregation.match(new Criteria("user.userId").nin(collection.getUserId())),
				 Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

		List<FeedsCollection>feeds=mongoTemplate.aggregate(aggregation,FeedsCollection.class,FeedsCollection.class).getMappedResults();
		Stream<FeedsCollection> stream=feeds.stream();
		List<ObjectId>userObjectIds=stream.map(feed->feed.getUserId()).collect(Collectors.toList());
		//List<ObjectId>userObjectIds=userIds.stream().map(ObjectId::new).collect(Collectors.toList());
		
		String message="New message from "+collection.getUserName()+"in the forum.";		
		pushNotificationService.notifyAll(null, userObjectIds,message);
	}
	
	@Override
	public Comment addEditComment(CommentRequest request) {
		Comment response = null;
		try {
			CommentCollection commentCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				commentCollection = commentRepository.findById(new ObjectId(request.getId())).orElse(null);
				BeanUtil.map(request, commentCollection);
				commentCollection.setUpdatedTime(new Date());
				commentRepository.save(commentCollection);
			} else {
				commentCollection = new CommentCollection();
				BeanUtil.map(request, commentCollection);
				commentCollection.setCreatedTime(new Date());
				commentCollection.setUpdatedTime(new Date());
				commentRepository.save(commentCollection);
			}
			response = new Comment();
			BeanUtil.map(commentCollection, response);
		} 
			
		 catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit post");
		}
		return response;
	}
	
	@Override
	public Feeds addEditLikes(String userId,String postId) {
		Feeds response = null;
		try {
			FeedsCollection collection = null;
			
			if (!DPDoctorUtils.anyStringEmpty(postId)) {
				collection = feedsRepository.findById(new ObjectId(postId)).orElse(null);
				if ( userId != null) {

				List<String>ids=new ArrayList<String>();
				ids.add(userId);
				Set<String> numbers= new HashSet<>(ids);
				
				List<String> userIdlist = new ArrayList<String> (numbers);
				collection.setUserIds(userIdlist);
				Long like=(long) collection.getUserIds().size();
				collection.setLikes(like);
				collection.setUpdatedTime(new Date());
				feedsRepository.save(collection);
					}
					
					
					response = new Feeds();
					BeanUtil.map(collection, response);	
				} else
					throw new BusinessException(ServiceError.NoRecord);
			
		} 
			
		 catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit post");
		}
		return response;
	}
	
	@Override
	public Feeds savePost(String postId,boolean isSaved) {
		Feeds response = null;
		try {
			FeedsCollection collection = null;
	
			if (!DPDoctorUtils.anyStringEmpty(postId)) {
				collection = feedsRepository.findById(new ObjectId(postId)).orElse(null);
				if (isSaved==true) {

				collection.setIsSaved(isSaved);
				collection.setUpdatedTime(new Date());
				feedsRepository.save(collection);
					}
					
					
					response = new Feeds();
					BeanUtil.map(collection, response);	
				} else
					throw new BusinessException(ServiceError.NoRecord);
			
		} 
			
		 catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit post");
		}
		return response;
	}
	


	@Override
	public List<Feeds> getFeeds(int size, int page, Boolean discarded, String type, String languageId,String searchTerm) {
		List<Feeds> response = null;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			LanguageCollection collection=null;
			if(DPDoctorUtils.anyStringEmpty(languageId)) {
				collection=languageRepository.findByName("English");
			languageId=collection.getId().toString();
			}
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("user", new BasicDBObject("$first", "$user"))
					.append("multilingual", new BasicDBObject("$first", "$multilingual"))
					.append("type", new BasicDBObject("$first", "$type"))
					.append("postByAdminId", new BasicDBObject("$first", "$postByAdminId"))
					.append("postByAdminName", new BasicDBObject("$first", "$postByAdminName"))
					.append("postByDoctorId", new BasicDBObject("$first", "$postByDoctorId"))
					.append("postByDoctorName", new BasicDBObject("$first", "$postByDoctorName"))
					.append("postByUserId", new BasicDBObject("$first", "$postByUserId"))
					.append("postByUserName", new BasicDBObject("$first", "$postByUserName"))
					.append("postByExpertName", new BasicDBObject("$first", "$postByExpertName"))
					.append("postByExpertId", new BasicDBObject("$first", "$postByExpertId"))
					
					.append("userIds", new BasicDBObject("$addToSet", "$userIds"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("isSaved", new BasicDBObject("$first", "$isSaved"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("likes", new BasicDBObject("$first", "$likes"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("comments", new BasicDBObject("$addToSet", "$comments"))));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("multilingual.title").regex("^" + searchTerm, "i"),
						new Criteria("multilingual.title").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						//Aggregation.lookup("comment_cl", "_id", "postId","comments"),
						//Aggregation.unwind("comments"),
						//Aggregation.match(new Criteria("comments.discarded").is(false)),group,
					//	Aggregation.unwind("multilingual",true),
					//	Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),					
//						Aggregation.match(new Criteria("user.isUser").is(true)),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
						//Aggregation.lookup("comment_cl", "_id", "postId","comments"),
						//Aggregation.unwind("comments"),
						//Aggregation.match(new Criteria("comments.discarded").is(false)),group,
					//	Aggregation.unwind("multilingual"),
					//	Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
//						
//						Aggregation.match(new Criteria("user.isUser").is(true)),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			System.out.println("Aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation, FeedsCollection.class, Feeds.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting feeds "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting feeds");
		}
		return response;
	}

	@Override
	public ForumResponse getForumResponseById(String id) {
		ForumResponse response = null;
		try {
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("userId", "$userId").append("userName", "$userName")
							.append("text", "$text").append("title", "$title").append("comments", "$comments")));

			Aggregation aggregation = null;
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("userId", new BasicDBObject("$first", "$userId"))
					.append("userName", new BasicDBObject("$first", "$userName"))
					.append("userType", new BasicDBObject("$first", "$userType"))
					.append("speciality", new BasicDBObject("$first", "$speciality"))
					.append("userImageUrl", new BasicDBObject("$first", "$userImageUrl"))
					.append("shortDescription", new BasicDBObject("$first", "$shortDescription"))
					.append("title", new BasicDBObject("$first", "$title"))
					.append("languageId", new BasicDBObject("$first", "$languageId"))
					.append("comments", new BasicDBObject("$addToSet", "$comments"))
					));

			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					Aggregation.lookup("comment_cl", "_id","postId", "comments"),
					Aggregation.unwind("comments"),
					Aggregation.match(new Criteria("comments.discarded").is(false)),group,
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			System.out.println("Aggregation"+aggregation);
			
			response = mongoTemplate.aggregate(aggregation, ForumResponseCollection.class, ForumResponse.class)
					.getUniqueMappedResult();


			
		}catch (BusinessException e) {
			logger.error("Error while get by forum "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while get by forum");
		}
		return response;
	}

	@Override
	public FeedsResponse getArticleById(String id,String languageId) {
		FeedsResponse response = null;
		try {
			LanguageCollection collection=null;
//			if (DPDoctorUtils.anyStringEmpty(languageId)) {
//				collection = languageRepository.findByName("English");
//				languageId = collection.getId().toString();
//			}
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("user", "$user")
					.append("multilingual.languageId", "$multilingual.languageId")
					.append("multilingual.name", "$multilingual.name")
					.append("multilingual.text", "$multilingual.text")
					.append("multilingual.imageUrl", "$multilingual.imageUrl")
					.append("multilingual.thumbnailUrl", "$multilingual.thumbnailUrl")
					.append("multilingual.videoUrl", "$multilingual.videoUrl")
					.append("type", "$type")
					.append("postByAdminId", "$postByAdminId")
					.append("type", "$type")
					.append("postByAdminName", "$postByAdminName")
					.append("postByDoctorId", "$postByDoctorId")
					.append("doctorName", "$doctorName")
					.append("postByExpertName", "$postByExpertName")
					.append("postByExpertId", "$postByExpertId")
					.append("userIds", "$userIds")
					.append("isSaved", "$isSaved")
					.append("likes", "$likes")
					.append("discarded", "$discarded")
					.append("comments", "$comments")));
			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("user", new BasicDBObject("$first", "$user"))
					.append("multilingual", new BasicDBObject("$first", "$multilingual"))
					.append("type", new BasicDBObject("$first", "$type"))
					.append("postByAdminId", new BasicDBObject("$first", "$postByAdminId"))
					.append("postByAdminName", new BasicDBObject("$first", "$postByAdminName"))
					.append("postByDoctorId", new BasicDBObject("$first", "$postByDoctorId"))
					.append("postByDoctorName", new BasicDBObject("$first", "$postByDoctorName"))
					.append("postByUserId", new BasicDBObject("$first", "$postByUserId"))
					.append("postByUserName", new BasicDBObject("$first", "$postByUserName"))
					.append("postByExpertName", new BasicDBObject("$first", "$postByExpertName"))
					.append("postByExpertId", new BasicDBObject("$first", "$postByExpertId"))
					
					.append("userIds", new BasicDBObject("$addToSet", "$userIds"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("isSaved", new BasicDBObject("$first", "$isSaved"))
					.append("user", new BasicDBObject("$first", "$user"))
					.append("likes", new BasicDBObject("$first", "$likes"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("discarded", new BasicDBObject("$first", "$discarded"))
						.append("comments", new BasicDBObject("$addToSet", "$comments"))));

			
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					Aggregation.unwind("multilingual", true),
					Aggregation.match(new Criteria("multilingual.languageId").is(languageId)));

			System.out.println("Aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation, FeedsCollection.class, FeedsResponse.class)
					.getUniqueMappedResult();

			if (response == null)
				throw new BusinessException(ServiceError.NoRecord, "No record found");

		} catch (BusinessException e) {
			logger.error("Error while getting records byId " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while get by records Id");
		}
		return response;
	}
	
	
	@Override
	public Boolean deleteForumResponseById(String id,Boolean discarded) {
		Boolean response = null;
		try {
			ForumResponseCollection collection = forumRepository.findById(new ObjectId(id)).orElse(null);
			if (collection == null)
				throw new BusinessException(ServiceError.NoRecord, "No recound found");
			else {
				collection.setDiscarded(discarded);
				forumRepository.save(collection);
				response = true;
			
			}

		} catch (BusinessException e) {
			logger.error("Error while get by forum " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while get by forum");
		}
		return response;
	}
	
	@Override
	public FeedsResponse deleteFeedsById(String id,Boolean discarded) {
		FeedsResponse response = null;
		try {
			FeedsCollection collection = feedsRepository.findById(new ObjectId(id)).orElse(null);
			if (collection == null)
				throw new BusinessException(ServiceError.NoRecord, "No recound found");
			else {
				collection.setDiscarded(discarded);
				feedsRepository.save(collection);
				response = new FeedsResponse();
				BeanUtil.map(collection, response);
			}

		} catch (BusinessException e) {
			logger.error("Error while get by forum " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while get by forum");
		}
		return response;
	}
	
	@Override
	public Comment deleteCommentsById(String id,Boolean discarded) {
		Comment response = null;
		try {
			CommentCollection collection = commentRepository.findById(new ObjectId(id)).orElse(null);
			if (collection == null)
				throw new BusinessException(ServiceError.NoRecord, "No record found");
			else {
				collection.setDiscarded(discarded);
				commentRepository.save(collection);
				response = new Comment();
				BeanUtil.map(collection, response);
			}

		} catch (BusinessException e) {
			logger.error("Error while deleting comments " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting comments");
		}
		return response;
	}


	@Override
	public FeedsResponse addEditPost(FeedsRequest request) {
		FeedsResponse response = null;
		try {
			FeedsCollection collection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = feedsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (collection != null) {

					BeanUtil.map(request, collection);
					
					if (request.getMultilingual() != null) {
						collection.setMultilingual(null);
						collection.setMultilingual(request.getMultilingual());
					}

					collection.setUpdatedTime(new Date());
					feedsRepository.save(collection);
				} else
					throw new BusinessException(ServiceError.NoRecord);
			} else {
				collection = new FeedsCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				feedsRepository.save(collection);
				//if(collection!=null)
				//notify(collection);
			}
			response = new FeedsResponse();
			BeanUtil.map(collection, response);
			

		} catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit post");
		}
		return response;
	}
	
	
	@Override
	public List<Comment> getComments(int size, int page, Boolean discarded,String searchTerm,String feedId) {
		List<Comment> response = null;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			
			if(feedId!=null)
			{
				criteria.and("postId").is(new ObjectId(feedId));
			}
			
			
	
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("comment").regex("^" + searchTerm, "i"),
						new Criteria("comment").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
					
					//	Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),					
//						Aggregation.match(new Criteria("user.isUser").is(true)),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
						//Aggregation.lookup("comment_cl", "_id", "postId","comments"),
						//Aggregation.unwind("comments"),
						//Aggregation.match(new Criteria("comments.discarded").is(false)),group,
					
					//	Aggregation.match(new Criteria("multilingual.languageId").is(languageId)),
//						
//						Aggregation.match(new Criteria("user.isUser").is(true)),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			System.out.println("Aggregation"+aggregation);
			response = mongoTemplate.aggregate(aggregation, CommentCollection.class, Comment.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting feeds "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting feeds");
		}
		return response;
	}

}
