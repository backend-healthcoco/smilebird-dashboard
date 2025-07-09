package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import com.dpdocter.beans.Activity;
import com.dpdocter.beans.ActivityAssign;
import com.dpdocter.beans.ExerciseImage;
import com.dpdocter.beans.Language;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.MindfulnessAssign;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.Stories;
import com.dpdocter.beans.StoriesAssign;
import com.dpdocter.beans.TodaySession;
import com.dpdocter.collections.ActivityAssignCollection;
import com.dpdocter.collections.ActivityCollection;
import com.dpdocter.collections.LanguageCollection;
import com.dpdocter.collections.MindfulnessAssignCollection;
import com.dpdocter.collections.MindfulnessCollection;
import com.dpdocter.collections.NutritionDiseaseCollection;
import com.dpdocter.collections.StoriesAssignCollection;
import com.dpdocter.collections.StoriesCollection;
import com.dpdocter.collections.TodaySessionCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ActivityAssignRepository;
import com.dpdocter.repository.ActivityRepository;
import com.dpdocter.repository.LanguageRepository;
import com.dpdocter.repository.MindfulnessAssignRepository;
import com.dpdocter.repository.MindfulnessRepository;
import com.dpdocter.repository.StoriesAssignRepository;
import com.dpdocter.repository.StoriesRepository;
import com.dpdocter.repository.TodaySessionRepository;
import com.dpdocter.services.HappinessServices;

import common.util.web.DPDoctorUtils;

@Service
public class HappinessServiceImpl implements HappinessServices {

	private static Logger logger = LogManager.getLogger(HappinessServiceImpl.class.getName());
	
	
	@Autowired
	LanguageRepository languageRepository;
	
	@Autowired
	ActivityRepository activityRepository;
	
	@Autowired
	MindfulnessRepository mindfulnessRepository;
	
	@Autowired
	StoriesRepository storiesRepository;
	
	@Autowired
	TodaySessionRepository todaysessionRepository;
	
	 @Autowired
	    private MongoTemplate mongoTemplate;
	 
	 @Value(value = "${image.path}")
		private String imagePath;
		
		@Value(value = "${bucket.name}")
		private String bucketName;

		@Value(value = "${mail.aws.key.id}")
		private String AWS_KEY;

		@Value(value = "${mail.aws.secret.key}")
		private String AWS_SECRET_KEY;
		
		@Autowired
		private ActivityAssignRepository activityAssignRepository;
		
		@Autowired
		private StoriesAssignRepository storiesAssignRepository;
		
		@Autowired
		private MindfulnessAssignRepository mindfulnessAssignRepository;
	
	 
	@Override
	public Language addEditLanguage(Language request) {
		Language response = null;
		try {
			LanguageCollection languageCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				languageCollection = languageRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (languageCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Language Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(languageCollection.getCreatedBy());
				request.setCreatedTime(languageCollection.getCreatedTime());
				BeanUtil.map(request, languageCollection);

			} else {
			 languageCollection = new LanguageCollection();
				BeanUtil.map(request, languageCollection);
				languageCollection.setCreatedBy("ADMIN");
				languageCollection.setUpdatedTime(new Date());
				languageCollection.setCreatedTime(new Date());
			}
			languageCollection = languageRepository.save(languageCollection);
			response = new Language();
			BeanUtil.map(languageCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Language  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Language " + e.getMessage());

		}
		
		return response;
	}
	@Override
	public Language getLanguage(String id) {
		Language response=null;
		try {
			LanguageCollection languageCollection=languageRepository.findById(new ObjectId(id)).orElse(null);
		    if(languageCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(languageCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		
		return response;
	}

	@Override
	public List<Language> getLanguages(int size, int page, Boolean discarded,String searchTerm) {
		List<Language> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, LanguageCollection.class, Language.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting languages " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting languages " + e.getMessage());

		}
		return response;

	}

	@Override
	public Language deleteLanguage(String id, Boolean discarded) {
		Language response=null;
		try {
			LanguageCollection languageCollection=languageRepository.findById(new ObjectId(id)).orElse(null);
		if(languageCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		languageCollection.setDiscarded(discarded);
		languageCollection.setUpdatedTime(new Date());
		languageCollection = languageRepository.save(languageCollection);
		response=new Language();
		BeanUtil.map(languageCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the language  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the language");
		}
		
		return response;
	}
	
	
	@Override
	public Integer countLanguage(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
				new Criteria("name").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), LanguageCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting language " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting language " + e.getMessage());

}
		return response;
	}

	@Override
	public Activity addEditActivity(Activity request)
	{ 
		Activity response = null;
		try {
			ActivityCollection activityCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				activityCollection = activityRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (activityCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Activity Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(activityCollection.getCreatedBy());
				
				activityCollection.setDetails(null);
			//LanguageCollection lang=languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
		
			BeanUtil.map(request, activityCollection);

			} else {
			 activityCollection = new ActivityCollection();
				BeanUtil.map(request, activityCollection);
				activityCollection.setCreatedBy("ADMIN");
				activityCollection.setUpdatedTime(new Date());
				activityCollection.setCreatedTime(new Date());
			}
			activityCollection = activityRepository.save(activityCollection);
			response = new Activity();
			
			//LanguageCollection language = languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
			BeanUtil.map(activityCollection, response);
            
		} catch (BusinessException e) {
			logger.error("Error while add/edit Activity  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Activity " + e.getMessage());

		}
		return response;
		
	}
	
	@Override
	public List<Activity> getActivity(int size, int page, Boolean discarded,String searchTerm,String languageId) {
		List<Activity> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("details.title").regex("^" + searchTerm, "i"),
						new Criteria("details.title").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, ActivityCollection.class, Activity.class).getMappedResults();
			
			
		
					
		} catch (BusinessException e) {
			logger.error("Error while getting activity " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting activity " + e.getMessage());

		}
		return response;

	}

	
	@Override
	public Activity deleteActivity(String id, Boolean discarded) {
		Activity response=null;
		try {
			ActivityCollection activityCollection=activityRepository.findById(new ObjectId(id)).orElse(null);
		if(activityCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		activityCollection.setDiscarded(discarded);
		activityCollection.setUpdatedTime(new Date());
		activityCollection = activityRepository.save(activityCollection);
		response=new Activity();
		BeanUtil.map(activityCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the activity  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the activity");
		}
		
		return response;
	}
	
	@Override
	public Integer countActivity(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("details.title").regex("^" + searchTerm, "i"),
				new Criteria("details.title").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), ActivityCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting activity " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while activity " + e.getMessage());

}
		return response;
	}
	@Override
	public Mindfulness addEditMindfulness(Mindfulness request) {

		Mindfulness response = null;
		try {
			MindfulnessCollection mindfulnessCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				mindfulnessCollection = mindfulnessRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (mindfulnessCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Mindfulness Not found with Id");
				}
				mindfulnessCollection.setGoals(null);
				request.setUpdatedTime(new Date());
				request.setCreatedBy(mindfulnessCollection.getCreatedBy());
				mindfulnessCollection.setDetails(null);
			
			//LanguageCollection lang=languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
		
			BeanUtil.map(request, mindfulnessCollection);

			} else {
			 mindfulnessCollection = new MindfulnessCollection();
				BeanUtil.map(request, mindfulnessCollection);
				mindfulnessCollection.setCreatedBy("ADMIN");
				mindfulnessCollection.setUpdatedTime(new Date());
				mindfulnessCollection.setCreatedTime(new Date());
			}
			mindfulnessCollection = mindfulnessRepository.save(mindfulnessCollection);
			response = new Mindfulness();
			BeanUtil.map(mindfulnessCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit mindfulness  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Mindfulness " + e.getMessage());

		}
		return response;
	}
	@Override
	public List<Mindfulness> getMindfulness(int size, int page,String searchTerm, Boolean discarded) {
		List<Mindfulness> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				if (!DPDoctorUtils.anyStringEmpty(searchTerm))
					criteria = criteria.orOperator(new Criteria("details.title").regex("^" + searchTerm, "i"),
							new Criteria("details.title").regex("^" + searchTerm));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, MindfulnessCollection.class, Mindfulness.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting mindfulness " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting mindfulness " + e.getMessage());

		}
		return response;

	}
	
	@Override
	public Integer countStories(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("storiesDetails.title").regex("^" + searchTerm, "i"),
				new Criteria("detail.title").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), StoriesCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting stories " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while stories count" + e.getMessage());

}
		return response;
	}
	
	
	@Override
	public Mindfulness deleteMindfulness(String id, Boolean discarded) {
		Mindfulness response=null;
		try {
			MindfulnessCollection mindfulnessCollection=mindfulnessRepository.findById(new ObjectId(id)).orElse(null);
		if(mindfulnessCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		mindfulnessCollection.setDiscarded(discarded);
		mindfulnessCollection.setUpdatedTime(new Date());
		mindfulnessCollection = mindfulnessRepository.save(mindfulnessCollection);
		response=new Mindfulness();
		BeanUtil.map(mindfulnessCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the mindfulness  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the Mindfulness");
		}
		
		return response;
	}
	
	
	@Override
	public Stories addEditStories(Stories request) {
		Stories response = null;
		try {
			StoriesCollection storiesCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				storiesCollection = storiesRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (storiesCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Stories Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(storiesCollection.getCreatedBy());
				storiesCollection.setStoryDetails(null);
			BeanUtil.map(request, storiesCollection);

			} else {
				
			    storiesCollection = new StoriesCollection();
				BeanUtil.map(request, storiesCollection);
				storiesCollection.setCreatedBy("ADMIN");
				storiesCollection.setUpdatedTime(new Date());
				storiesCollection.setCreatedTime(new Date());
			}
			storiesCollection = storiesRepository.save(storiesCollection);
			
			
			response = new Stories();
			
			BeanUtil.map(storiesCollection, response);
			

		} catch (BusinessException e) {
			logger.error("Error while add/edit Stories  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Stories " + e.getMessage());

		}
		return response;
	}
	
	
	@Override
	public List<Stories> getStories(int size, int page, String searchTerm,Boolean discarded,String languageId) {
		List<Stories> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("storyDetails.title").regex("^" + searchTerm, "i"),
						new Criteria("storyDetails.title").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, StoriesCollection.class, Stories.class)
					.getMappedResults();
		
		
			
		} catch (BusinessException e) {
			logger.error("Error while getting stories " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting stories " + e.getMessage());

		}
		return response;

	}
	
	
	@Override
	public Stories deleteStories(String id, Boolean discarded) {
		Stories response=null;
		try {
			StoriesCollection storiesCollection=storiesRepository.findById(new ObjectId(id)).orElse(null);
		if(storiesCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		storiesCollection.setDiscarded(discarded);
		storiesCollection.setUpdatedTime(new Date());
		storiesCollection = storiesRepository.save(storiesCollection);
		response=new Stories();
		BeanUtil.map(storiesCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the stories  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the Stories");
		}
		
		return response;
	}
	
	@Override
	public Integer countMindfulness(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
				new Criteria("title").regex("^" + searchTerm));
	
		    response = (int) mongoTemplate.count(new Query(criteria), MindfulnessCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting mindfulness " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while mindfulness count" + e.getMessage());

			}
		return response;
		}
	@Override
	public TodaySession addEditSession(TodaySession request) {
		TodaySession response = null;
		try {
			TodaySessionCollection sessionCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				sessionCollection = todaysessionRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (sessionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Activity Not found with Id");
				}
				
				request.setUpdatedTime(new Date());
				request.setCreatedBy(sessionCollection.getCreatedBy());
				

		
		sessionCollection.setStandards(null);
		
			BeanUtil.map(request, sessionCollection);

			} else {
			 sessionCollection = new TodaySessionCollection();
				BeanUtil.map(request, sessionCollection);
				sessionCollection.setCreatedBy("ADMIN");
				sessionCollection.setUpdatedTime(new Date());
				sessionCollection.setCreatedTime(new Date());
			}
			sessionCollection = todaysessionRepository.save(sessionCollection);
			response = new TodaySession();
			BeanUtil.map(sessionCollection, response);
		
		
		} catch (BusinessException e) {
			logger.error("Error while add/edit Todaysession  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Todaysession " + e.getMessage());

		}
		return response;
	}
	
	@Override
	public List<TodaySession> getSession(int size, int page, String searchTerm, Boolean discarded) {
		List<TodaySession> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("school.schoolName").regex("^" + searchTerm, "i"),
//						new Criteria("school.schoolName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, TodaySessionCollection.class, TodaySession.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting todaysession " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while today session " + e.getMessage());

		}
		return response;

	}
	
	@Override
	public Integer countSession(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
//		    criteria = criteria.orOperator(new Criteria("school.schoolName").regex("^" + searchTerm, "i"),
//				new Criteria("detail.title").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), TodaySessionCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting Sessions " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while Session count " + e.getMessage());

}
		return response;
	}
	@Override
	public TodaySession deleteSession(String id, Boolean discarded) {
		TodaySession response=null;
		try {
			TodaySessionCollection sessionCollection=todaysessionRepository.findById(new ObjectId(id)).orElse(null);
		if(sessionCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		sessionCollection.setDiscarded(discarded);
		sessionCollection.setUpdatedTime(new Date());
		sessionCollection = todaysessionRepository.save(sessionCollection);
		response=new TodaySession();
		BeanUtil.map(sessionCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the stories  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the Stories");
		}
		
		return response;
	}
	
	@Override
	@Transactional
	public Mindfulness uploadImage(MultipartFile file) {
		String recordPath = null;
		Mindfulness mindfulness = null;
		
		try {

			Date createdTime = new Date();
			if (file != null) {
				
				String path = "happiness" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path  + fileName + createdTime.getTime() +"." +fileExtension;
				mindfulness = saveImage(file, recordPath, true);
				
			//	exeriseImage.setImageUrl(exeriseImage.getImageUrl());
			//	exeriseImage.setThumbnailUrl(exeriseImage.getThumbnailUrl());
				
				
				
				
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
		return mindfulness;
	}
	

	@Override
	@Transactional
	public Mindfulness uploadVideo(MultipartFile file) {
		String recordPath = null;
		Mindfulness mindfulness = null;
		
		try {

			Date createdTime = new Date();
			if (file != null) {
				
				String path = "happiness" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path  + fileName + createdTime.getTime() +"." +fileExtension;
				mindfulness = saveVideo(file, recordPath);
			
				
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
		return mindfulness;
	}

	
			@Override
			@Transactional
			public Mindfulness saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
				BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
				AmazonS3 s3client = new AmazonS3Client(credentials);
				Mindfulness response = new Mindfulness();
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
			public Mindfulness saveVideo(MultipartFile file, String recordPath) {
				BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
				AmazonS3 s3client = new AmazonS3Client(credentials);
				Mindfulness response = new Mindfulness();
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
			public ActivityAssign addEditActivityAssign(ActivityAssign request) {
				ActivityAssign response = null;
				try {
					ActivityAssignCollection activityAssignCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
						activityAssignCollection = activityAssignRepository.findById(new ObjectId(request.getId())).orElse(null);
						if (activityAssignCollection == null) {
							throw new BusinessException(ServiceError.NotFound, "Id Not found ");
						}
						request.setUpdatedTime(new Date());
						request.setCreatedBy(activityAssignCollection.getCreatedBy());
						activityAssignCollection.setActivity(null);
						activityAssignCollection.setStandard(null);
						
					//LanguageCollection lang=languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
				
					BeanUtil.map(request, activityAssignCollection);

					} else {
					 activityAssignCollection = new ActivityAssignCollection();
						BeanUtil.map(request, activityAssignCollection);
						activityAssignCollection.setCreatedBy("ADMIN");
						activityAssignCollection.setUpdatedTime(new Date());
						activityAssignCollection.setCreatedTime(new Date());
					}
					activityAssignCollection = activityAssignRepository.save(activityAssignCollection);
					response = new ActivityAssign();
					
					//LanguageCollection language = languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
					
					BeanUtil.map(activityAssignCollection, response);
			//		Activity activity=activityRepository.findById(activityAssignCollection.getActivityId().toString());
			//		response.setActivity(activity);
				} catch (BusinessException e) {
					logger.error("Error while add/edit Activity  " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while add/edit Assign activity " + e.getMessage());

				}
				return response;
				
			}
			
			
			@Override
			public List<ActivityAssign> getActivityAssign(int size, int page, String searchTerm, Boolean discarded) {
				List<ActivityAssign> response = null;
				try {
					Criteria criteria = new Criteria("discarded").is(discarded);
//					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//						criteria = criteria.orOperator(new Criteria("storyDetails.title").regex("^" + searchTerm, "i"),
//								new Criteria("storyDetails.title").regex("^" + searchTerm));

					Aggregation aggregation = null;
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
								Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
					}
					response = mongoTemplate.aggregate(aggregation, ActivityAssignCollection.class, ActivityAssign.class)
							.getMappedResults();
				
				
					
				} catch (BusinessException e) {
					logger.error("Error while getting assign stories " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while getting assign stories " + e.getMessage());

				}
				return response;
			}
			@Override
			public ActivityAssign deleteActivityAssign(String id,Boolean discarded) {
				ActivityAssign response=null;
				try {
					ActivityAssignCollection activityAssignCollection=activityAssignRepository.findById(new ObjectId(id)).orElse(null);
				if(activityAssignCollection==null)
				{
					throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
				}
				activityAssignCollection.setDiscarded(discarded);
				activityAssignCollection.setUpdatedTime(new Date());
				activityAssignCollection = activityAssignRepository.save(activityAssignCollection);
				response=new ActivityAssign();
				BeanUtil.map(activityAssignCollection, response);
				}
				catch (BusinessException e) {
					logger.error("Error while deleting the assign activities  "+e.getMessage());
					throw new BusinessException(ServiceError.Unknown,"Error while deleting the assign activities");
				}
				
				return response;
			}
			@Override
			public StoriesAssign addEditStoriesAssign(StoriesAssign request) {
				StoriesAssign response = null;
				try {
					StoriesAssignCollection storiesAssignCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
						storiesAssignCollection = storiesAssignRepository.findById(new ObjectId(request.getId())).orElse(null);
						if (storiesAssignCollection == null) {
							throw new BusinessException(ServiceError.NotFound, "Id Not found ");
						}
						storiesAssignCollection.setStandard(null);
						storiesAssignCollection.setStories(null);
						request.setUpdatedTime(new Date());
						request.setCreatedBy(storiesAssignCollection.getCreatedBy());
					//	storiesAssignCollection.setStories(null);
						
					//LanguageCollection lang=languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
				
					BeanUtil.map(request, storiesAssignCollection);

					} else {
					 storiesAssignCollection = new StoriesAssignCollection();
						BeanUtil.map(request, storiesAssignCollection);
						storiesAssignCollection.setCreatedBy("ADMIN");
						storiesAssignCollection.setUpdatedTime(new Date());
						storiesAssignCollection.setCreatedTime(new Date());
					}
					storiesAssignCollection = storiesAssignRepository.save(storiesAssignCollection);
					response = new StoriesAssign();
					
					//LanguageCollection language = languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
					BeanUtil.map(storiesAssignCollection, response);
		            
				} catch (BusinessException e) {
					logger.error("Error while add/edit Activity  " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while add/edit Assign stories  " + e.getMessage());

				}
				return response;
				
			}
			@Override
			public List<StoriesAssign> getStoriesAssign(int size, int page, String searchTerm, Boolean discarded) {
				List<StoriesAssign> response = null;
				try {
					Criteria criteria = new Criteria("discarded").is(discarded);
//					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//						criteria = criteria.orOperator(new Criteria("storyDetails.title").regex("^" + searchTerm, "i"),
//								new Criteria("storyDetails.title").regex("^" + searchTerm));

					Aggregation aggregation = null;
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
								Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
					}
					response = mongoTemplate.aggregate(aggregation, StoriesAssignCollection.class, StoriesAssign.class)
							.getMappedResults();
				
				
					
				} catch (BusinessException e) {
					logger.error("Error while getting assign stories " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while getting assign stories " + e.getMessage());

				}
				return response;
			}
			@Override
			public StoriesAssign deleteStoriesAssign(String id,Boolean discarded) {
				StoriesAssign response=null;
				try {
					StoriesAssignCollection storiesAssignCollection=storiesAssignRepository.findById(new ObjectId(id)).orElse(null);
				if(storiesAssignCollection==null)
				{
					throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
				}
				storiesAssignCollection.setDiscarded(discarded);
				storiesAssignCollection.setUpdatedTime(new Date());
				storiesAssignCollection = storiesAssignRepository.save(storiesAssignCollection);
				response=new StoriesAssign();
				BeanUtil.map(storiesAssignCollection, response);
				}
				catch (BusinessException e) {
					logger.error("Error while deleting the assign stories  "+e.getMessage());
					throw new BusinessException(ServiceError.Unknown,"Error while deleting the assign stories");
				}
				
				return response;
			}
			
			@Override
			public MindfulnessAssign addEditMindfulnessAssign(MindfulnessAssign request) {
			MindfulnessAssign response = null;
				try {
					MindfulnessAssignCollection mindfulnessAssignCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
						mindfulnessAssignCollection = mindfulnessAssignRepository.findById(new ObjectId(request.getId())).orElse(null);
						if (mindfulnessAssignCollection == null) {
							throw new BusinessException(ServiceError.NotFound, "Id not found");
						}
						mindfulnessAssignCollection.setStandard(null);
						request.setUpdatedTime(new Date());
						request.setCreatedBy(mindfulnessAssignCollection.getCreatedBy());
						mindfulnessAssignCollection.setMindfulness(null);
						
					//LanguageCollection lang=languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
				
					BeanUtil.map(request, mindfulnessAssignCollection);

					} else {
					 mindfulnessAssignCollection = new MindfulnessAssignCollection();
						BeanUtil.map(request, mindfulnessAssignCollection);
						mindfulnessAssignCollection.setCreatedBy("ADMIN");
						mindfulnessAssignCollection.setUpdatedTime(new Date());
						mindfulnessAssignCollection.setCreatedTime(new Date());
					}
					mindfulnessAssignCollection = mindfulnessAssignRepository.save(mindfulnessAssignCollection);
					response = new MindfulnessAssign();
					
					//LanguageCollection language = languageRepository.findById(new ObjectId(request.getDetails().get(0).getLanguage().getId())).orElse(null);
					BeanUtil.map(mindfulnessAssignCollection, response);
		            
				} catch (BusinessException e) {
					logger.error("Error while add/edit Assign mindfulness " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while add/edit Assign mindfulness" + e.getMessage());

				}
				return response;
			}
			
			
			@Override
			public List<MindfulnessAssign> getMindfulnessAssign(int size, int page, String searchTerm, Boolean discarded) {
				List<MindfulnessAssign> response = null;
				try {
					Criteria criteria = new Criteria("discarded").is(discarded);
//					if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//						criteria = criteria.orOperator(new Criteria("storyDetails.title").regex("^" + searchTerm, "i"),
//								new Criteria("storyDetails.title").regex("^" + searchTerm));

					Aggregation aggregation = null;
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
								Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
					}
					response = mongoTemplate.aggregate(aggregation, MindfulnessAssignCollection.class, MindfulnessAssign.class)
							.getMappedResults();
				
				
					
				} catch (BusinessException e) {
					logger.error("Error while getting assign stories " + e.getMessage());
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, "Error while getting assign mindfulness " + e.getMessage());

				}
				return response;
			}
			
			
			@Override
			public MindfulnessAssign deleteMindfulnessAssign(String id,Boolean discarded) {
				MindfulnessAssign response=null;
				try {
					MindfulnessAssignCollection mindfulnessAssignCollection=mindfulnessAssignRepository.findById(new ObjectId(id)).orElse(null);
				if(mindfulnessAssignCollection==null)
				{
					throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
				}
				mindfulnessAssignCollection.setDiscarded(discarded);
				mindfulnessAssignCollection.setUpdatedTime(new Date());
				mindfulnessAssignCollection = mindfulnessAssignRepository.save(mindfulnessAssignCollection);
				response=new MindfulnessAssign();
				BeanUtil.map(mindfulnessAssignCollection, response);
				}
				catch (BusinessException e) {
					logger.error("Error while deleting the assign stories  "+e.getMessage());
					throw new BusinessException(ServiceError.Unknown,"Error while deleting the assign mindfulness");
				}
				
				return response;
			}
			
			
		

	
}
