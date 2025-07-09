package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
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
import com.dpdocter.beans.CuratedMultilingual;
import com.dpdocter.beans.CuratedYogaSession;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Essentials;
import com.dpdocter.beans.Injury;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.Yoga;
import com.dpdocter.beans.YogaClasses;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaSession;
import com.dpdocter.beans.YogaTeacher;
import com.dpdocter.collections.CuratedYogaSessionCollection;
import com.dpdocter.collections.EssentialCollection;
import com.dpdocter.collections.InjuryCollection;
import com.dpdocter.collections.YogaClassesCollection;
import com.dpdocter.collections.YogaCollection;
import com.dpdocter.collections.YogaDiseaseCollection;
import com.dpdocter.collections.YogaSessionCollection;
import com.dpdocter.collections.YogaTeacherCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CuratedYogaSessionRepository;
import com.dpdocter.repository.EssentialsRepository;
import com.dpdocter.repository.InjuryRepository;
import com.dpdocter.repository.YogaClassesRepository;
import com.dpdocter.repository.YogaDiseaseRepository;
import com.dpdocter.repository.YogaRepository;
import com.dpdocter.repository.YogaSessionRepository;
import com.dpdocter.repository.YogaTeacherRepository;
import com.dpdocter.request.CuratedMultilingualRequest;
import com.dpdocter.request.YogaSessionRequest;
import com.dpdocter.services.YogaService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class YogaServiceImpl implements YogaService {

	private static Logger logger = LogManager.getLogger(YogaServiceImpl.class.getName());
	
	@Autowired
	private YogaTeacherRepository yogaTeacherRepository;
	
	@Autowired
	private InjuryRepository injuryRepository;
	
	@Autowired
	private YogaRepository yogaRepository;
	
	@Autowired
	private YogaClassesRepository yogaClassesRepository;
	
	@Autowired
	private YogaSessionRepository yogaSessionRepository;
	
	@Autowired
	private YogaDiseaseRepository yogaDiseaseRepository;
	
	@Autowired
	private CuratedYogaSessionRepository curatedYogaRepository;
	
	@Autowired
	private EssentialsRepository essentialYogaRepository;
	
	
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

	
	@Override
	public YogaTeacher addEditTeacher(YogaTeacher request) {
		YogaTeacher response = null;
		try {
			YogaTeacherCollection yogaTeacherCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				yogaTeacherCollection = yogaTeacherRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (yogaTeacherCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "yoga Teacher Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(yogaTeacherCollection.getCreatedBy());
				request.setCreatedTime(yogaTeacherCollection.getCreatedTime());
				yogaTeacherCollection.setMultilingualYogaTeacher(null);
			
				BeanUtil.map(request, yogaTeacherCollection);

			} else {
				yogaTeacherCollection = new YogaTeacherCollection();
				BeanUtil.map(request, yogaTeacherCollection);
				yogaTeacherCollection.setCreatedBy("ADMIN");
				yogaTeacherCollection.setAdminCreatedTime(new Date());
				yogaTeacherCollection.setUpdatedTime(new Date());
				yogaTeacherCollection.setCreatedTime(new Date());
			}
			yogaTeacherCollection = yogaTeacherRepository.save(yogaTeacherCollection);
			response = new YogaTeacher();
			BeanUtil.map(yogaTeacherCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit yoga Teacher " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit yoga Teacher " + e.getMessage());

		}
	
		
		return response;

	}

	@Override
	public List<YogaTeacher> getTeachers(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>teacherIds) {
		
		List<YogaTeacher> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if(teacherIds!=null && !teacherIds.isEmpty())
				criteria.and("id").in(teacherIds);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("multilingualYogaTeacher.yogaTeacherName").regex("^" + searchTerm, "i"),
						new Criteria("multilingualYogaTeacher.yogaTeacherName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, YogaTeacherCollection.class,YogaTeacher.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting yoga Teacher " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga Teacher " + e.getMessage());

		}
		return response;

	
	}

	@Override
	public YogaTeacher getTeacherById(String id) {
		YogaTeacher response=null;
		try {
			YogaTeacherCollection yogaTeacherCollection=yogaTeacherRepository.findById(new ObjectId(id)).orElse(null);
		    if(yogaTeacherCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(yogaTeacherCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;
	}

	@Override
	public YogaTeacher deleteTeacher(String id, Boolean discarded) {
		YogaTeacher response=null;
		try {
			YogaTeacherCollection yogaTeacherCollection=yogaTeacherRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaTeacherCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaTeacherCollection.setDiscarded(discarded);
		yogaTeacherCollection.setUpdatedTime(new Date());
		yogaTeacherCollection = yogaTeacherRepository.save(yogaTeacherCollection);
		response=new YogaTeacher();
		BeanUtil.map(yogaTeacherCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the yoga Teacher  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the yoga Teacher");
		}
		
		return response;

	}
	
	@Override
	public Integer countYogaTeacher(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("multilingualYogaTeacher.yogaTeacherName").regex("^" + searchTerm, "i"),
				new Criteria("multilingualYogaTeacher.yogaTeacherName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), YogaTeacherCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting yoga Teacher " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting yoga Teacher " + e.getMessage());

}
		return response;
	}

	@Override
	public Injury addEditInjury(Injury request) {
		Injury response = null;
		try {
			InjuryCollection injuryCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				injuryCollection = injuryRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (injuryCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "injury Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(injuryCollection.getCreatedBy());
				request.setCreatedTime(injuryCollection.getCreatedTime());
				injuryCollection.setInjuryMultilingual(null);
				BeanUtil.map(request, injuryCollection);

			} else {
				injuryCollection = new InjuryCollection();
				BeanUtil.map(request, injuryCollection);
				injuryCollection.setCreatedBy("ADMIN");
				injuryCollection.setAdminCreatedTime(new Date());
				injuryCollection.setUpdatedTime(new Date());
				injuryCollection.setCreatedTime(new Date());
			}
			injuryCollection = injuryRepository.save(injuryCollection);
			response = new Injury();
			BeanUtil.map(injuryCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit injury " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit injury " + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<Injury> getInjury(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>injuryIds) {
		List<Injury> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if(injuryIds!=null && !injuryIds.isEmpty())
				criteria.and("id").in(injuryIds);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("injuryMultilingual.injuryName").regex("^" + searchTerm, "i"),
						new Criteria("injuryMultilingual.injuryName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, InjuryCollection.class,Injury.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting injury " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting injury " + e.getMessage());

		}
		return response;

	}

	@Override
	public Injury getInjuryById(String id) {
		Injury response=null;
		try {
			InjuryCollection injuryCollection=injuryRepository.findById(new ObjectId(id)).orElse(null);
		    if(injuryCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(injuryCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;

	}

	@Override
	public Injury deleteInjury(String id, Boolean discarded) {
		Injury response=null;
		try {
			InjuryCollection yogaTeacherCollection=injuryRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaTeacherCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaTeacherCollection.setDiscarded(discarded);
		yogaTeacherCollection.setUpdatedTime(new Date());
		yogaTeacherCollection = injuryRepository.save(yogaTeacherCollection);
		response=new Injury();
		BeanUtil.map(yogaTeacherCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the injury  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the injury");
		}
		
		return response;

	}
	
	@Override
	public Integer countInjury(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("injuryMultilingual.injuryName").regex("^" + searchTerm, "i"),
				new Criteria("injuryMultilingual.injuryName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), InjuryCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting injury " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting injury " + e.getMessage());

}
		return response;
	}

	@Override
	public Yoga addEditYoga(Yoga request) {
		Yoga response = null;
		try {
			YogaCollection yogaCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				yogaCollection  = yogaRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (yogaCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Yoga Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(yogaCollection.getCreatedBy());
				request.setCreatedTime(yogaCollection.getCreatedTime());
				
				yogaCollection.setDiseaseIds(null);
				yogaCollection.setDiseasePrecautionIds(null);
				
				yogaCollection.setYogaType(null);
				yogaCollection.setTeacher(null);
				yogaCollection.setInjuryPrecautionIds(null);
				yogaCollection.setMultilingualYoga(null);
				
				BeanUtil.map(request, yogaCollection);

			} else {
				yogaCollection  = new YogaCollection();
				BeanUtil.map(request, yogaCollection );
				yogaCollection.setCreatedBy("ADMIN");
				yogaCollection.setAdminCreatedTime(new Date());
				yogaCollection.setUpdatedTime(new Date());
				yogaCollection.setCreatedTime(new Date());
			}
			yogaCollection  = yogaRepository.save(yogaCollection);
			response = new Yoga();
			BeanUtil.map(yogaCollection, response);
						

		} catch (BusinessException e) {
			logger.error("Error while add/edit Yoga " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Yoga " + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<Yoga> getYoga(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>yogaIds) {
		List<Yoga> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		
			
//			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
//					new BasicDBObject("_id", "$_id")
//					.append("type", "$type")
//					.append("multilingualYoga.yogaName", "$multilingualYoga.yogaName")
//					.append("multilingualYoga.alternateYogaName", "$multilingualYoga.alternateYogaName")
//					.append("multilingualYoga.benefits", "$multilingualYoga.benefits")
//					.append("multilingualYoga.beCareful", "$multilingualYoga.beCareful")
//					.append("multilingualYoga.languageId", "$multilingualYoga.languageId")
//					.append("durationInMin", "$durationInMin")
//					.append("gender", "$gender")
//					.append("imageUrl", "$imageUrl")
//					.append("fullVideoUrl", "$fullVideoUrl")
//					.append("trailVideoUrl", "$trailVideoUrl")
//					.append("thumbnailUrl", "$thumbnailUrl")
//					.append("level", "$level")
//					.append("pose", "$pose")
//					.append("calories", "$calories")
//					.append("priority", "$priority")
//					.append("yogaType", "$yogaType")
//			//		.append("disease.id", "$diseases._id")
//			//		.append("disease.yogaDiseaseMultilingual.diseaseName", "$diseases.yogaDiseaseMultilingual.diseaseName")
//			//		.append("diseasePrecaution._id", "$diseasePrecaution._id")
//			//		.append("diseasePrecaution.yogaDiseaseMultilingual.diseaseName", "$diseasePrecaution.yogaDiseaseMultilingual.diseaseName")
//		//			.append("injuryPrecaution.id", "$injuryPrecaution._id")
//	//				.append("injuryPrecaution.injuryMultilingual.injuryName", "$injuryPrecaution.injuryMultilingual.injuryName")
//					.append("discarded", "$discarded")));
//
//			Aggregation aggregation = null;
//			
//			if (size > 0) {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),		
//						new CustomAggregationOperation(new Document("$unwind",
//								new BasicDBObject("path", "$diseaseIds").append("includeArrayIndex", "arrayIndex"))),
//						Aggregation.lookup("yoga_disease_cl","diseaseIds.id", "id",  "diseases"),
//						Aggregation.unwind("diseases"),
//						Aggregation.unwind("diseases.yogaDiseaseMultilingual"),
//						Aggregation.unwind("multilingualYoga"),
//						project,
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
//						Aggregation.limit(size));
//			} else {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//			
//						new CustomAggregationOperation(new Document("$unwind",
//								new BasicDBObject("path", "$diseaseIds").append("includeArrayIndex", "arrayIndex"))),
//					Aggregation.lookup("yoga_disease_cl", "id", "diseaseIds.id", "diseases"),
//						Aggregation.unwind("diseases"),
//						Aggregation.unwind("diseases.yogaDiseaseMultilingual"),
//						Aggregation.unwind("multilingualYoga"),
//			
//						
//						project,
//			 
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
//			}

			
			
			if(yogaIds!=null && !yogaIds.isEmpty())
			criteria.and("id").in(yogaIds);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria = criteria.orOperator(new Criteria("multilingualYoga.yogaName").regex("^" + searchTerm, "i"),
					new Criteria("multilingualYoga.yogaName").regex("^" + searchTerm));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
					Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
		}
			
			
			
	
			
			response=new ArrayList<Yoga>();
			response= mongoTemplate.aggregate(aggregation, YogaCollection.class,Yoga.class).getMappedResults();
			
			
		
		//	BeanUtil.map(yogaCollections, response);
			
		//	response=new ArrayList<Yoga>();
					 

			
			
			
			
		} catch (BusinessException e) {
			logger.error("Error while getting yoga " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga " + e.getMessage());

		}
		return response;


	}
	
	@Override
	public Integer countYoga(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			
		    criteria = criteria.orOperator(new Criteria("multilingual.yogaName").regex("^" + searchTerm, "i"),
				new Criteria("multilingual.yogaName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),YogaCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting yoga " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting yoga " + e.getMessage());

}
		return response;
	}

	@Override
	public Yoga getYogaById(String id) {
		Yoga response=null;
		try {
			YogaCollection yogaCollection=yogaRepository.findById(new ObjectId(id)).orElse(null);
		    if(yogaCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
		    response=new Yoga();
			BeanUtil.map(yogaCollection, response);
		System.out.println(yogaCollection);
			if(yogaCollection.getDiseaseIds() !=null)
				response.setDisease(getYogaDisease(0, 0, false, null,yogaCollection.getDiseaseIds()));

			if(yogaCollection.getDiseasePrecautionIds()!=null)
				response.setDiseasePrecaution(getYogaDisease(0, 0, false, null, yogaCollection.getDiseasePrecautionIds()));
		
			if(yogaCollection.getInjuryPrecautionIds()!=null)
				response.setInjuryPrecaution(getInjury(0, 0, false, null, yogaCollection.getInjuryPrecautionIds()));


		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;

	}

	@Override
	public Yoga deleteYoga(String id, Boolean discarded) {
		Yoga response=null;
		try {
			YogaCollection yogaCollection=yogaRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaCollection.setDiscarded(discarded);
		yogaCollection.setUpdatedTime(new Date());
		yogaCollection = yogaRepository.save(yogaCollection);
		response=new Yoga();
		BeanUtil.map(yogaCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the yoga  "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the yoga");
		}
		
		return response;

	}

	@Override
	public YogaClasses addEditYogaClasses(YogaClasses request) {
		YogaClasses response = null;
		try {
			YogaClassesCollection yogaClassesCollection = null;
		
		
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				yogaClassesCollection  = yogaClassesRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (yogaClassesCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Yoga Not found with Id");
				}
				
				request.setUpdatedTime(new Date());
				request.setCreatedBy(yogaClassesCollection.getCreatedBy());
				request.setCreatedTime(request.getCreatedTime());
				
				yogaClassesCollection.setYogaIds(null);
				yogaClassesCollection.setInjuryPrecautionIds(null);
				yogaClassesCollection.setDiseaseIds(null);
				yogaClassesCollection.setDiseasePrecautionIds(null);
		
				
				yogaClassesCollection.setTeacherIds(null);
				yogaClassesCollection.setYogaClassMultilingual(null);
				BeanUtil.map(request, yogaClassesCollection);

			} else {
				yogaClassesCollection  = new YogaClassesCollection();
				BeanUtil.map(request, yogaClassesCollection);
				yogaClassesCollection.setCreatedBy("ADMIN");
				yogaClassesCollection.setAdminCreatedTime(new Date());
				yogaClassesCollection.setUpdatedTime(new Date());
				yogaClassesCollection.setCreatedTime(new Date());
			}
			yogaClassesCollection  = yogaClassesRepository.save(yogaClassesCollection);
			
			response = new YogaClasses();
			BeanUtil.map(yogaClassesCollection, response);
			
			if(request.getYoga() !=null)
				response.setYoga(getYoga(0, 0, false, null,yogaClassesCollection.getYogaIds()));

		
		} catch (BusinessException e) {
			logger.error("Error while add/edit Yoga classes " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Yoga classes" + e.getMessage());

		}
	
		
		return response;


	}
	
	

	@Override
	public List<YogaClasses> getYogaClasses(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>yogaClassesIds) {
		List<YogaClasses> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		
			if(yogaClassesIds!=null && !yogaClassesIds.isEmpty())
				criteria.and("id").in(yogaClassesIds);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("yogaClassMultilingual.yogaClassesName").regex("^" + searchTerm, "i"),
						new Criteria("yogaClassMultilingual.yogaClassesName").regex("^" + searchTerm));

			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
					.append("languageId", "$languageId")
					.append("yogaIds", "$yogaIds")
					.append("gender", "$gender")
					.append("yogaClassMultilingual.yogaClassesName", "$yogaClassMultilingual.yogaClassesName")
					.append("yogaClassMultilingual.benefits", "$yogaClassMultilingual.benefits")
					.append("yogaClassMultilingual.beCareful", "$yogaClassMultilingual.beCareful")
					.append("yogaClassMultilingual.shortDescription", "$yogaClassMultilingual.shortDescription")
					.append("level", "$level")
					.append("duration", "$duration")
					.append("calories", "$calories")
					.append("yoga", "$yoga")
					.append("coverImageUrl", "$coverImageUrl")
					.append("type", "$type")
					.append("yogaType", "$yogaType")
					.append("disease.id", "$disease.id")
					.append("disease.yogaDiseaseMultilingual.diseaseName", "$disease.yogaDiseaseMultilingual.diseaseName")
					.append("diseasePrecaution.id", "$diseasePrecaution.id")
					.append("diseasePrecaution.yogaDiseaseMultilingual.diseaseName", "$diseasePrecaution.yogaDiseaseMultilingual.diseaseName")
					.append("injuryPrecaution.id", "$injuryPrecaution.id")
					.append("injuryPrecaution.injuryMultilingual.injuryName", "$injuryPrecaution.injuryMultilingual.injuryName")
					.append("discarded", "$discarded")));
					
			Aggregation aggregation = null;
			
			
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			
//			if (size > 0) {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//					//	Aggregation.match(new Criteria("languageId").is(new ObjectId(languageId)),
//						
//					//	Aggregation.lookup("acadamic_profile_cl", "schoolId", "schoolId", "acadamicSection"),
//					//	Aggregation.unwind("acadamicSection",true), 
//						Aggregation.lookup("yoga_classes_cl", "id", "_id", "activitys"),
//						Aggregation.unwind("activitys",true),
//					//	Aggregation.unwind("activitys.yogaClassMultilingual",true),
//						
//					//	Aggregation.match(new Criteria("activitys.details.languageId").is(languageId)),
//					
//						project,
//				//		group,
//						
//						
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
//						Aggregation.limit(size));
//			} else {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//				//		Aggregation.match(new Criteria("schoolId").is(new ObjectId(schoolId)).and("branchId").is(new ObjectId(branchId))),
//						Aggregation.lookup("yoga_classes_cl", "id", "_id", "activitys"),
//						Aggregation.unwind("activitys",true),
//						
//				//		Aggregation.match(new Criteria("activitys.details.languageId").is(languageId)),
//						
//						project,
//				//		group,	
//				//		Aggregation.lookup("acadamic_profile_cl", "schoolId", "schoolId", "acadamicSection"),
//				//		Aggregation.unwind("acadamicSection",true), 
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
//			}

			
			response = mongoTemplate.aggregate(aggregation, YogaClassesCollection.class,YogaClasses.class).getMappedResults();
//			
			
			
			
			
			
			
//			Aggregation aggregation = null;
//			if (size > 0) {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
//						Aggregation.limit(size));
//			} else {
//				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
//			}
//			List<YogaClassesCollection>yogaClassCollection = mongoTemplate.aggregate(aggregation, YogaClassesCollection.class,YogaClassesCollection.class).getMappedResults();
//			BeanUtil.map(yogaClassCollection, response);
//			
//			response=new ArrayList<YogaClasses>();
//			for(YogaClassesCollection yogaClass:yogaClassCollection)
//				if(yogaClass.getYogaIds()!=null)
//					((YogaClasses) response).setYoga(getYoga(0, 0, false, null,yogaClass.getYogaIds()));

			
			
		} catch (BusinessException e) {
			logger.error("Error while getting yoga classes " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga classes" + e.getMessage());

		}
		return response;

	}
	
	@Override
	public Integer countYogaClasses(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("yogaClassMultilingual.yogaClassesName").regex("^" + searchTerm, "i"),
				new Criteria("yogaClassMultilingual.yogaClassesName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),YogaClassesCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting yoga classes " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting yoga classes" + e.getMessage());

}
		return response;
	}



	@Override
	public YogaClasses getYogaClassesById(String id) {
		YogaClasses response=null;
		try {
			YogaClassesCollection yogaCollection=yogaClassesRepository.findById(new ObjectId(id)).orElse(null);
		    if(yogaCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
		    response = new YogaClasses();
			
			
		
			BeanUtil.map(yogaCollection, response);
			
			if(yogaCollection.getYogaIds() !=null)
				response.setYoga(getYoga(0, 0, false, null,yogaCollection.getYogaIds()));
			
			if(yogaCollection.getDiseaseIds()!=null)
	response.setDisease(getYogaDisease(0, 0, false, null, yogaCollection.getDiseaseIds()));

			if(yogaCollection.getDiseasePrecautionIds()!=null)
	response.setDiseasePrecaution(getYogaDisease(0, 0, false, null, yogaCollection.getDiseasePrecautionIds()));

	
			if(yogaCollection.getInjuryPrecautionIds()!=null)
	response.setInjuryPrecaution(getInjury(0, 0, false, null, yogaCollection.getInjuryPrecautionIds()));

		if(yogaCollection.getTeacherIds()!=null)
	response.setTeacher(getTeachers(0, 0, false, null,yogaCollection.getTeacherIds()));

		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;

	}

	@Override
	public YogaClasses deleteYogaClasses(String id, Boolean discarded) {
		YogaClasses response=null;
		try {
			YogaClassesCollection yogaCollection=yogaClassesRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaCollection.setDiscarded(discarded);
		yogaCollection.setUpdatedTime(new Date());
		yogaCollection = yogaClassesRepository.save(yogaCollection);
		response=new YogaClasses();
		BeanUtil.map(yogaCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the yoga classes "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the yoga classes");
		}
		
		return response;

	}

	@Override
	public YogaSession addEditYogaSession(YogaSession request) {
		YogaSession response = null;
		try {
			YogaSessionCollection yogaSessionCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				yogaSessionCollection  = yogaSessionRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (yogaSessionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Yoga Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(yogaSessionCollection.getCreatedBy());
				request.setCreatedTime(yogaSessionCollection.getCreatedTime());
				yogaSessionCollection.setDiseaseIds(null);
				yogaSessionCollection.setPrecautionIds(null);
				yogaSessionCollection.setLevel(null);
				yogaSessionCollection.setYogaClassesIds(null);
				yogaSessionCollection.setYogaSessionMultilingual(null);
				
				BeanUtil.map(request, yogaSessionCollection);

			} else {
				yogaSessionCollection = new YogaSessionCollection();
				BeanUtil.map(request, yogaSessionCollection);
				yogaSessionCollection.setCreatedBy("ADMIN");
				yogaSessionCollection.setAdminCreatedTime(new Date());
				yogaSessionCollection.setUpdatedTime(new Date());
				yogaSessionCollection.setCreatedTime(new Date());
			}
			yogaSessionCollection = yogaSessionRepository.save(yogaSessionCollection);
			response = new YogaSession();
			BeanUtil.map(yogaSessionCollection, response);
			
			
			if(request.getYogaClasses() !=null)
				
			response.setYogaClasses(getYogaClasses(0, 0, false, null,yogaSessionCollection.getYogaClassesIds()));

		} catch (BusinessException e) {
			logger.error("Error while add/edit Yoga Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Yoga Session " + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<YogaSession> getYogaSession(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>yogaSessionIds) {
		List<YogaSession> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if(yogaSessionIds!=null && !yogaSessionIds.isEmpty())
				criteria.and("id").in(yogaSessionIds);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("yogaSessionMultilingual.yogaSessionName").regex("^" + searchTerm, "i"),
						new Criteria("yogaSessionMultilingual.yogaSessionName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, YogaSessionCollection.class,YogaSession.class).getMappedResults();
//			response=new ArrayList<YogaSession>();
//			YogaSession yogaSessions=new YogaSession();
//			YogaDisease yogaDisease=new YogaDisease();
//			YogaClasses yogaClasses=new YogaClasses();
//			List<YogaDisease>yogDiseases=new ArrayList<YogaDisease>();
//			List<YogaClasses>yogas=new ArrayList<YogaClasses>();
//			for(YogaSessionCollection yogaSession:yogaSessionCollection) {
//				BeanUtil.map(yogaSession, yogaSessions);
//				if (yogaSession.getYogaClassesIds()!=null) 
//					for(ObjectId yoga:yogaSession.getYogaClassesIds())
//						yogaClasses=getYogaClassesById(yoga.toString());
//				
//				yogas.add(yogaClasses);
//				yogaSessions.setYogaClasses(yogas);
//				response.add(yogaSessions);
//				
//				if(yogaSession.getDiseaseIds() !=null)
//					for(ObjectId disease:yogaSession.getDiseaseIds())
//						yogaDisease=getYogaDiseaseById(disease.toString());
//				yogDiseases.add(yogaDisease);
//				yogaSessions.setDisease(yogDiseases);
//				if(yogaSession.getPrecautionId() !=null)
//					for(ObjectId disease:yogaSession.getPrecautionId())
//						yogaDisease=getYogaDiseaseById(disease.toString());
//				yogDiseases.add(yogaDisease);
//				yogaSessions.setPrecaution(yogDiseases);
//				response.add(yogaSessions);
//			}
		} catch (BusinessException e) {
			logger.error("Error while getting yoga session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga session" + e.getMessage());

		}
		return response;


	}

	@Override
	public YogaSession getYogaSessionById(String id) {
		YogaSession response=null;
		try {
			YogaSessionCollection yogaSessionCollection=yogaSessionRepository.findById(new ObjectId(id)).orElse(null);
		    if(yogaSessionCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			response=new YogaSession();
			BeanUtil.map(yogaSessionCollection, response);
			
			if(yogaSessionCollection.getYogaClassesIds() !=null)
				response.setYogaClasses(getYogaClasses(0, 0, false, null,yogaSessionCollection.getYogaClassesIds()));

			if(yogaSessionCollection.getDiseaseIds()!=null)
				response.setDisease(getYogaDisease(0, 0, false, null, yogaSessionCollection.getDiseaseIds()));

						if(yogaSessionCollection.getPrecautionIds()!=null)
				response.setPrecaution(getYogaDisease(0, 0, false, null, yogaSessionCollection.getPrecautionIds()));

						
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;

	}

	@Override
	public YogaSession deleteYogaSession(String id, Boolean discarded) {
		YogaSession response=null;
		try {
			YogaSessionCollection yogaSessionCollection=yogaSessionRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaSessionCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaSessionCollection.setDiscarded(discarded);
		yogaSessionCollection.setUpdatedTime(new Date());
		yogaSessionCollection = yogaSessionRepository.save(yogaSessionCollection);
		response=new YogaSession();
		BeanUtil.map(yogaSessionCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the yoga session "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the yoga session");
		}
		
		return response;


	}


	@Override
	public Integer countYogaSession(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("yogaSessionMultilingual.yogaSessionName").regex("^" + searchTerm, "i"),
				new Criteria("yogaSessionMultilingual.yogaSessionName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),YogaSessionCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting yoga session " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting yoga session" + e.getMessage());

}
		return response;

	}
	
	
	
	@Override
	public Yoga uploadImage(MultipartFile file) {
		String recordPath = null;
		Yoga response = null;
		
		try {

			Date createdTime = new Date();
			if (file != null) {
				
				String path = "yoga" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path  + fileName + createdTime.getTime() +"." +fileExtension;
				response = saveImage(file, recordPath, true);
				
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
		return response;
	}

	@Override
	public Yoga uploadVideo(MultipartFile file) {
		String recordPath = null;
		Yoga response = null;
		
		try {

			Date createdTime = new Date();
			if (file != null) {
				
				String path = "yoga" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path  + fileName + createdTime.getTime() +"." +fileExtension;
				response = saveVideo(file, recordPath);
			
				
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
		return response;
	}

	public Yoga saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		Yoga response = new Yoga();
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
	
	public Yoga saveVideo(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		Yoga response = new Yoga();
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
			response.setFullVideoUrl(imagePath + recordPath);
			
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
	public YogaDisease addEditYogaDisease(YogaDisease request) {
		YogaDisease response = null;
		try {
			YogaDiseaseCollection yogaDiseaseCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				yogaDiseaseCollection = yogaDiseaseRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (yogaDiseaseCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "yoga Disease Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(yogaDiseaseCollection.getCreatedBy());
				request.setCreatedTime(yogaDiseaseCollection.getCreatedTime());
				yogaDiseaseCollection.setYogaDiseaseMultilingual(null);
			
				BeanUtil.map(request, yogaDiseaseCollection);

			} else {
				yogaDiseaseCollection = new YogaDiseaseCollection();
				BeanUtil.map(request, yogaDiseaseCollection);
				yogaDiseaseCollection.setCreatedBy("ADMIN");
				yogaDiseaseCollection.setAdminCreatedTime(new Date());
				yogaDiseaseCollection.setUpdatedTime(new Date());
				yogaDiseaseCollection.setCreatedTime(new Date());
			}
			yogaDiseaseCollection = yogaDiseaseRepository.save(yogaDiseaseCollection);
			response = new YogaDisease();
			BeanUtil.map(yogaDiseaseCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit yoga disease " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit yoga disease " + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<YogaDisease> getYogaDisease(int page, int size, Boolean discarded, String searchTerm,List<ObjectId>yogaDiseaseIds) {
		List<YogaDisease> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			
			if(yogaDiseaseIds!=null && !yogaDiseaseIds.isEmpty())
				criteria.and("id").in(yogaDiseaseIds);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("yogaDiseaseMultilingual.diseaseName").regex("^" + searchTerm, "i"),
						new Criteria("yogaDiseaseMultilingual.diseaseName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,YogaDiseaseCollection.class,YogaDisease.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting yoga disease" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga disease " + e.getMessage());

		}
		return response;

	}

	@Override
	public YogaDisease getYogaDiseaseById(String id) {
		YogaDisease response=null;
		try {
			YogaDiseaseCollection yogaDiseaseCollection=yogaDiseaseRepository.findById(new ObjectId(id)).orElse(null);
		    if(yogaDiseaseCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(yogaDiseaseCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;

	}

	@Override
	public YogaDisease deleteYogaDisease(String id, Boolean discarded) {
		YogaDisease response=null;
		try {
			YogaDiseaseCollection yogaDiseasenCollection=yogaDiseaseRepository.findById(new ObjectId(id)).orElse(null);
		if(yogaDiseasenCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		yogaDiseasenCollection.setDiscarded(discarded);
		yogaDiseasenCollection.setUpdatedTime(new Date());
		yogaDiseasenCollection = yogaDiseaseRepository.save(yogaDiseasenCollection);
		response=new YogaDisease();
		BeanUtil.map(yogaDiseasenCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the yoga disease "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the yoga disease");
		}
		
		return response;

	}
	
	@Override
	public Integer countYogaDisease(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("yogaDiseaseMultilingual.diseaseName").regex("^" + searchTerm, "i"),
				new Criteria("yogaDiseaseMultilingual.diseaseName").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),YogaDiseaseCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting yoga disease " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting yoga disease" + e.getMessage());

}
		return response;

	}

	@Override
	public CuratedYogaSession addEditCuratedYogaSession(CuratedYogaSession request) {
		CuratedYogaSession response = null;
		try {
			CuratedYogaSessionCollection curatedYogaSessionCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				curatedYogaSessionCollection = curatedYogaRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (curatedYogaSessionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "curated yoga session Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(curatedYogaSessionCollection.getCreatedBy());
				request.setCreatedTime(curatedYogaSessionCollection.getCreatedTime());
				curatedYogaSessionCollection.setCuratedMultilingual(null);
				curatedYogaSessionCollection.setYogaClassesIds(null);
				BeanUtil.map(request,curatedYogaSessionCollection);

			} else {
				curatedYogaSessionCollection = new CuratedYogaSessionCollection();
				BeanUtil.map(request, curatedYogaSessionCollection);
				curatedYogaSessionCollection.setCreatedBy("ADMIN");
				curatedYogaSessionCollection.setAdminCreatedTime(new Date());
				curatedYogaSessionCollection.setUpdatedTime(new Date());
				curatedYogaSessionCollection.setCreatedTime(new Date());
			}
			curatedYogaSessionCollection = curatedYogaRepository.save(curatedYogaSessionCollection);
			response = new CuratedYogaSession();

			BeanUtil.map(curatedYogaSessionCollection, response);
			
		
		} catch (BusinessException e) {
			logger.error("Error while add/edit curated yoga session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit curated yoga session" + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<CuratedYogaSession> getCuratedYogaSession(int page, int size, Boolean discarded, String searchTerm) {
		List<CuratedYogaSession> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("curatedMultilingual.name").regex("^" + searchTerm, "i"),
						new Criteria("curatedMultilingual.name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,CuratedYogaSessionCollection.class,CuratedYogaSession.class).getMappedResults();
//			response=new ArrayList<CuratedYogaSession>();
//			CuratedYogaSession yogaSessions=new CuratedYogaSession();
//			
//			YogaSession session=new YogaSession();
//		
//			List<YogaSession>yogas=new ArrayList<YogaSession>();
//			for(CuratedYogaSessionCollection yogaSession:yogaSessionCollection) {
//				BeanUtil.map(yogaSession, yogaSessions);
//				if (yogaSession.getCuratedMultilingual()!=null) 
//					for(CuratedMultilingualRequest curated:yogaSession.getCuratedMultilingual())
//						if(curated.getYogaSessions()!=null)
//					for(YogaSessionRequest yoga:curated.getYogaSessions())
//						session=getYogaSessionById(yoga.getId().toString());
//				
//				yogas.add(session);
//				yogaSessions.getCuratedMultilingual().get(0).setYogaSessions(yogas);
//				response.add(yogaSessions);
//				
//			}
//			
			
			
		} catch (BusinessException e) {
			logger.error("Error while getting curated yoga session" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting yoga curated yoga session " + e.getMessage());

		}
		return response;

	}

	@Override
	public CuratedYogaSession getCuratedYogaSessionById(String id) {
		CuratedYogaSession response=null;
		try {
			CuratedYogaSessionCollection curatedYogaSessionCollection=curatedYogaRepository.findById(new ObjectId(id)).orElse(null);
		    if(curatedYogaSessionCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			response=new CuratedYogaSession();
			BeanUtil.map(curatedYogaSessionCollection, response);
		if(curatedYogaSessionCollection.getYogaClassesIds()!=null)
			response.setYogaClasses(getYogaClasses(0, 0, false, null, curatedYogaSessionCollection.getYogaClassesIds()));
		
		if(curatedYogaSessionCollection.getDiseaseIds()!=null)
			response.setDisease(getYogaDisease(0, 0, false, null, curatedYogaSessionCollection.getDiseaseIds()));

					if(curatedYogaSessionCollection.getPrecautionIds()!=null)
			response.setPrecaution(getYogaDisease(0, 0, false, null, curatedYogaSessionCollection.getPrecautionIds()));

		
		
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;


	}

	@Override
	public CuratedYogaSession deleteCuratedYogaSession(String id, Boolean discarded) {
		CuratedYogaSession response=null;
		try {
			CuratedYogaSessionCollection curatedYogaSessionCollection=curatedYogaRepository.findById(new ObjectId(id)).orElse(null);
		if(curatedYogaSessionCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		curatedYogaSessionCollection.setDiscarded(discarded);
		curatedYogaSessionCollection.setUpdatedTime(new Date());
		curatedYogaSessionCollection = curatedYogaRepository.save(curatedYogaSessionCollection);
		response=new CuratedYogaSession();
		BeanUtil.map(curatedYogaSessionCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the curated yoga session "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the curated yoga session");
		}
		
		return response;


	}

	@Override
	public Integer countCuratedYogaSession(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("curatedMultilingual.name").regex("^" + searchTerm, "i"),
				new Criteria("curatedMultilingual.name").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),CuratedYogaSessionCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting curated yoga session " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting curated yoga session" + e.getMessage());

}
		return response;
	}
	
	@Override
	public Essentials addEditEssentials(Essentials request) {
		Essentials response = null;
		try {
			EssentialCollection essentialsCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				essentialsCollection = essentialYogaRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (essentialsCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "curated yoga session Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(essentialsCollection.getCreatedBy());
				request.setCreatedTime(essentialsCollection.getCreatedTime());
				essentialsCollection.setEssentialMultilingual(null);	
				essentialsCollection.setYogaClassesIds(null);
				BeanUtil.map(request,essentialsCollection);

			} else {
				essentialsCollection = new EssentialCollection();
				BeanUtil.map(request, essentialsCollection);
				essentialsCollection.setCreatedBy("ADMIN");
				essentialsCollection.setAdminCreatedTime(new Date());
				essentialsCollection.setUpdatedTime(new Date());
				essentialsCollection.setCreatedTime(new Date());
			}
			essentialsCollection = essentialYogaRepository.save(essentialsCollection);
			response = new Essentials();
//			List<YogaSession>sessions=new ArrayList<YogaSession>();
//			YogaSession yogaSession=new YogaSession();
//			if(request.getEssentialMultilingual() !=null)
//				for(CuratedMultilingual yoga:request.getEssentialMultilingual())
//				{
//					if(yoga.getYogaSessions()!=null)
//						for(YogaSession session:yoga.getYogaSessions())
//							yogaSession=addEditYogaSession(session);
//					
//				sessions.add(yogaSession);
//				
//				}
			BeanUtil.map(essentialsCollection, response);
		//	response.getEssentialMultilingual().get(0).setYogaSessions(sessions);

		} catch (BusinessException e) {
			logger.error("Error while add/edit essentials yoga session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit essentials yoga session" + e.getMessage());

		}
	
		
		return response;


	}

	@Override
	public List<Essentials> getEssentials(int page, int size, Boolean discarded, String searchTerm) {
		List<Essentials> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("essentialMultilingual.name").regex("^" + searchTerm, "i"),
						new Criteria("essentialMultilingual.name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,EssentialCollection.class,Essentials.class).getMappedResults();
//			response=new ArrayList<Essentials>();
//			Essentials yogaSessions=new Essentials();
//			
//			YogaSession session=new YogaSession();
//		
//			List<YogaSession>yogas=new ArrayList<YogaSession>();
//			for(EssentialCollection essentials:essentialCollection) {
//				BeanUtil.map(essentials, yogaSessions);
//				if (essentials.getEssentialMultilingual()!=null) 
//					for(CuratedMultilingualRequest curated:essentials.getEssentialMultilingual())
//						if(curated.getYogaSessions()!=null)
//					for(YogaSessionRequest yoga:curated.getYogaSessions())
//						session=getYogaSessionById(yoga.getId().toString());
//				
//				yogas.add(session);
//				yogaSessions.getEssentialMultilingual().get(0).setYogaSessions(yogas);
//				response.add(yogaSessions);
//				
//			}
//			
			
			
			
		} catch (BusinessException e) {
			logger.error("Error while getting essentials yoga session" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting essentials yoga session " + e.getMessage());

		}
		return response;

	}

	@Override
	public Essentials getEssentialById(String id) {
		Essentials response=null;
		try {
			EssentialCollection essentialsCollection=essentialYogaRepository.findById(new ObjectId(id)).orElse(null);
			if(essentialsCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			response=new Essentials();
			BeanUtil.map(essentialsCollection, response);
		if(essentialsCollection.getYogaClassesIds()!=null)
			response.setYogaClasses(getYogaClasses(0, 0, false, null, essentialsCollection.getYogaClassesIds()));
		
		if(essentialsCollection.getDiseaseIds()!=null)
			response.setDisease(getYogaDisease(0, 0, false, null, essentialsCollection.getDiseaseIds()));

					if(essentialsCollection.getPrecautionIds()!=null)
			response.setPrecaution(getYogaDisease(0, 0, false, null, essentialsCollection.getPrecautionIds()));

		
		
		}
		
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;
	}
	@Override
	public Essentials deleteEssentials(String id, Boolean discarded) {
		Essentials response=null;
		try {
			EssentialCollection essentialsCollection=essentialYogaRepository.findById(new ObjectId(id)).orElse(null);
		if(essentialsCollection==null)
		{
			throw new BusinessException(ServiceError.NotFound,"Error no such id to delete");
		}
		essentialsCollection.setDiscarded(discarded);
		essentialsCollection.setUpdatedTime(new Date());
		essentialsCollection = essentialYogaRepository.save(essentialsCollection);
		response=new Essentials();
		BeanUtil.map(essentialsCollection, response);
		}
		catch (BusinessException e) {
			logger.error("Error while deleting the essentials "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while deleting the essentials");
		}
		
		return response;


	}

	@Override
	public Integer countEssentials(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("essentialMultilingual.name").regex("^" + searchTerm, "i"),
				new Criteria("essentialMultilingual.name").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria),EssentialCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting essential yoga session " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting essential yoga session" + e.getMessage());

}
		return response;
	}
	

	
	

	
	

}
