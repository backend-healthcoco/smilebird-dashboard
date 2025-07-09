package com.dpdocter.webservices.v3;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
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
import com.dpdocter.beans.AlignerIPRAndAttachmetDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AlignerDeliveryDetailCollection;
import com.dpdocter.collections.AlignerPackageCollection;
import com.dpdocter.collections.AlignerPlanCollection;
import com.dpdocter.collections.AlignerUserPlanCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.PlanStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AlignerDeliveryDetailRepository;
import com.dpdocter.repository.AlignerPackageRepository;
import com.dpdocter.repository.AlignerPlanRepository;
import com.dpdocter.repository.AlignerUserPlanRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AlignerDeliveryDetailRequest;
import com.dpdocter.request.AlignerMakeoverImagesRequest;
import com.dpdocter.request.AlignerMakeoverVisualsRequest;
import com.dpdocter.request.AlignerPackageRequest;
import com.dpdocter.request.AlignerPlanRequest;
import com.dpdocter.request.AlignerUserPlanRequest;
import com.dpdocter.response.AlignerDeliveryDetailResponse;
import com.dpdocter.response.AlignerPackageResponse;
import com.dpdocter.response.AlignerPlanResponse;
import com.dpdocter.response.AlignerUserPlanResponse;
import com.dpdocter.response.DentalChainFile;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class AlignersV3ServiceImpl implements AlignersV3Services {

	private static Logger logger = LogManager.getLogger(AlignersV3ServiceImpl.class.getName());
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AlignerPackageRepository alignerPackageRepository;

	@Autowired
	private AlignerPlanRepository alignerPlanRepository;

	@Autowired
	private AlignerDeliveryDetailRepository alignerDeliveryDetailRepository;

	@Autowired
	private AlignerUserPlanRepository userPlanRepository;

	@Autowired
	private UserRepository userRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Override
	public DentalChainFile uploadImage(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {
				String path = "smilebirdImage" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = saveImage(file, recordPath, true);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	@Override
	public DentalChainFile uploadVideo(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {

				String path = "smilebirdVideo" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = saveVideo(file, recordPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	@Override
	public DentalChainFile uploadPly(MultipartFile file) {
		String recordPath = null;
		DentalChainFile dentalChainFile = null;
		try {
			Date createdTime = new Date();
			if (file != null) {
				String path = "smilebirdPly" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				dentalChainFile = savePly(file, recordPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dentalChainFile;
	}

	public DentalChainFile saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
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

	public DentalChainFile saveVideo(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
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

	private DentalChainFile savePly(MultipartFile file, String recordPath) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		DentalChainFile response = new DentalChainFile();
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
			response.setPlyUrl(imagePath + recordPath);

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
	public AlignerPackageResponse addEditAlignerPackage(AlignerPackageRequest request) {
		AlignerPackageResponse response = null;
		AlignerPackageCollection alignerPackageCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				alignerPackageCollection = alignerPackageRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				request.setCreatedTime(alignerPackageCollection.getCreatedTime());
				alignerPackageCollection.setUpdatedTime(new Date());
			} else {
				alignerPackageCollection = new AlignerPackageCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, alignerPackageCollection);
			alignerPackageCollection = alignerPackageRepository.save(alignerPackageCollection);
			response = new AlignerPackageResponse();
			BeanUtil.map(alignerPackageCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getAlignerPackages(int size, int page, String searchTerm, Boolean isDiscarded) {
		List<AlignerPackageResponse> responseList = new ArrayList<AlignerPackageResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									AlignerPackageCollection.class, AlignerPackageResponse.class)
							.getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						AlignerPackageCollection.class, AlignerPackageResponse.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						AlignerPackageCollection.class, AlignerPackageResponse.class).getMappedResults();

			}

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteAlignerPackage(String packageId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			AlignerPackageCollection alignerPackageCollection = alignerPackageRepository
					.findById(new ObjectId(packageId)).orElse(null);
			if (alignerPackageCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			alignerPackageCollection.setUpdatedTime(new Date());
			alignerPackageCollection.setIsDiscarded(isDiscarded);
			alignerPackageCollection = alignerPackageRepository.save(alignerPackageCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public AlignerPlanResponse addEditAlignerPlan(AlignerPlanRequest request) {
		AlignerPlanResponse response = null;
		AlignerPlanCollection alignerPlanCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				alignerPlanCollection = alignerPlanRepository.findById(new ObjectId(request.getId())).orElse(null);
				alignerPlanCollection.setUpdatedTime(new Date());
				request.setCreatedTime(alignerPlanCollection.getCreatedTime());
			} else {
				alignerPlanCollection = new AlignerPlanCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, alignerPlanCollection);
			alignerPlanCollection = alignerPlanRepository.save(alignerPlanCollection);
			response = new AlignerPlanResponse();
			BeanUtil.map(alignerPlanCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getAlignerPlans(String packageId, int size, int page, String searchTerm,
			Boolean isDiscarded) {
		List<AlignerPlanResponse> responseList = new ArrayList<AlignerPlanResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			if (isDiscarded == false)
				criteria.and("isDiscarded").ne(true);
			else
				criteria.and("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(packageId))
				criteria.and("packageId").is(new ObjectId(packageId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("planName").regex("^" + searchTerm, "i"),
						new Criteria("planName").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerPlanCollection.class, AlignerPlanResponse.class).getMappedResults().size());

			if (size > 0) {
				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime"),
								Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
						AlignerPlanCollection.class, AlignerPlanResponse.class).getMappedResults();
			} else {

				responseList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(Sort.Direction.DESC, "createdTime")),
						AlignerPlanCollection.class, AlignerPlanResponse.class).getMappedResults();

			}
			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteAlignerPlan(String planId, Boolean isDiscarded) {
		Boolean response = false;
		try {
			AlignerPlanCollection alignerPlanCollection = alignerPlanRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (alignerPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			alignerPlanCollection.setUpdatedTime(new Date());
			alignerPlanCollection.setIsDiscarded(isDiscarded);
			alignerPlanCollection = alignerPlanRepository.save(alignerPlanCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public AlignerUserPlanResponse addEditAssignPlanToUser(AlignerUserPlanRequest request) {
		AlignerUserPlanResponse response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getPlanId())) {

				AlignerPlanCollection planCollection = alignerPlanRepository.findById(new ObjectId(request.getPlanId()))
						.orElse(null);
				if (planCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Error no such id");
				}

				UserCollection userCollection = userRepository.findById(new ObjectId(request.getUserId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "user not found By Id ");
				}
				AlignerUserPlanCollection userPlanCollection = new AlignerUserPlanCollection();
				if (request.getDuration() != null) {
					Calendar cal = Calendar.getInstance();

					if (request.getDuration().getDurationUnit().toString().equalsIgnoreCase("YEAR"))
						cal.add(Calendar.YEAR, request.getDuration().getValue().intValue()); // to
																								// 1
					if (request.getDuration().getDurationUnit().toString().equalsIgnoreCase("MONTH"))
						cal.add(Calendar.MONTH, request.getDuration().getValue().intValue()); // to
																								// get
					if (request.getDuration().getDurationUnit().toString().equalsIgnoreCase("DAY"))
						cal.add(Calendar.DAY_OF_MONTH, request.getDuration().getValue().intValue()); // to get next
																										// day add 1
					if (request.getDuration().getDurationUnit().toString().equalsIgnoreCase("WEEK"))
						cal.add(Calendar.WEEK_OF_MONTH, request.getDuration().getValue().intValue()); // to get next
																										// week add 1
					userPlanCollection.setToDate(cal.getTime());
				}
				userPlanCollection.setUserId(userCollection.getId());
				userPlanCollection.setPlanId(planCollection.getId());
				userPlanCollection.setMode(request.getMode());
				userPlanCollection.setPlanStatus(PlanStatus.ONGOING);
				userPlanCollection.setAdminCreatedTime(new Date());
				userPlanCollection.setCreatedTime(new Date());
				userPlanCollection.setIsAttachment(planCollection.getIsAttachment());
				userPlanCollection.setIsIPRRequird(planCollection.getIsIPRRequird());
				userPlanCollection.setCreatedTime(new Date());
//				userPlanCollection.setCreatedBy();
				userPlanCollection.setDiscount(request.getDiscount());
				userPlanCollection.setAmount(request.getAmount());
				userPlanCollection.setDiscount(request.getDiscount());
				User user = new User();
				BeanUtil.map(userCollection, user);

				userPlanCollection = userPlanRepository.save(userPlanCollection);

				response = new AlignerUserPlanResponse();
				BeanUtil.map(userPlanCollection, response);
				response.setUser(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getAlignerPlansNames(String packageId, String searchTerm) {
		List<AlignerPlanResponse> responseList = new ArrayList<AlignerPlanResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			criteria.and("isDiscarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(packageId))
				criteria.and("packageId").is(new ObjectId(packageId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("planName").regex("^" + searchTerm, "i"),
						new Criteria("planName").regex("^" + searchTerm));
			}

			response.setCount(mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerPlanCollection.class, AlignerPlanResponse.class).getMappedResults().size());

			responseList = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerPlanCollection.class, AlignerPlanResponse.class).getMappedResults();

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Response<Object> getAlignerPackagesNames(String searchTerm) {
		List<AlignerPackageResponse> responseList = new ArrayList<AlignerPackageResponse>();
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria();

			criteria.and("isDiscarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {

				criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			}

			response.setCount(
					mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.sort(Sort.Direction.DESC, "createdTime")),
									AlignerPackageCollection.class, AlignerPackageResponse.class)
							.getMappedResults().size());

			responseList = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerPackageCollection.class, AlignerPackageResponse.class).getMappedResults();

			response.setDataList(responseList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean addVideosToPlan(String videoUrl, String userId, String planId) {
		Boolean response = false;
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}

			if (!DPDoctorUtils.anyStringEmpty(planId)) {
				criteria.and("planId").is(new ObjectId(planId));
			}

			AlignerUserPlanCollection userPlanCollection = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerUserPlanCollection.class, AlignerUserPlanCollection.class).getUniqueMappedResult();
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			userPlanCollection.setMakeoverVideothumbnailUrl(videoUrl);
			userPlanCollection = userPlanRepository.save(userPlanCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean addMakeoverVisuals(AlignerMakeoverVisualsRequest request) {
		Boolean response = false;
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(request.getUserId())) {
				criteria.and("userId").is(new ObjectId(request.getUserId()));
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getPlanId())) {
				criteria.and("planId").is(new ObjectId(request.getPlanId()));
			}

			AlignerUserPlanCollection userPlanCollection = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerUserPlanCollection.class, AlignerUserPlanCollection.class).getUniqueMappedResult();
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			userPlanCollection.setMakeoverVisuals(request.getMakeoverVisuals());
			userPlanCollection = userPlanRepository.save(userPlanCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean addMakeoverImages(AlignerMakeoverImagesRequest request) {
		Boolean response = false;
		try {
			Criteria criteria = new Criteria();

			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(request.getUserId())) {
				criteria.and("userId").is(new ObjectId(request.getUserId()));
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getPlanId())) {
				criteria.and("planId").is(new ObjectId(request.getPlanId()));
			}

			AlignerUserPlanCollection userPlanCollection = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AlignerUserPlanCollection.class, AlignerUserPlanCollection.class).getUniqueMappedResult();
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			userPlanCollection.setMakeoverImages(request.getMakeoverImages());
			userPlanCollection = userPlanRepository.save(userPlanCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<AlignerUserPlanResponse> getUserAlignerPlan(String userId) {
		List<AlignerUserPlanResponse> response = null;
		try {
			List<AlignerUserPlanCollection> userPlanCollection = userPlanRepository.findByUserId(new ObjectId(userId));
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new ArrayList<AlignerUserPlanResponse>();
			for (AlignerUserPlanCollection alignerUserPlanCollection : userPlanCollection) {
				AlignerUserPlanResponse alignerUserPlanResponse = new AlignerUserPlanResponse();
				UserCollection userCollection = userRepository.findById(alignerUserPlanCollection.getUserId())
						.orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "user not found By Id ");
				}
				BeanUtil.map(alignerUserPlanCollection, alignerUserPlanResponse);
				User user = new User();
				BeanUtil.map(userCollection, user);
				alignerUserPlanResponse.setUser(user);
				response.add(alignerUserPlanResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean changeStatusOfPlan(String planId, String status) {
		Boolean response = false;
		try {
			AlignerUserPlanCollection userPlanCollection = userPlanRepository.findById(new ObjectId(planId))
					.orElse(null);
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			userPlanCollection.setPlanStatus(PlanStatus.valueOf(status));
			userPlanCollection = userPlanRepository.save(userPlanCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public AlignerDeliveryDetailResponse addEditDeliveryDetailOfPlan(AlignerDeliveryDetailRequest request) {
		AlignerDeliveryDetailResponse response = null;
		AlignerDeliveryDetailCollection deliveryDetailCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				deliveryDetailCollection = alignerDeliveryDetailRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				deliveryDetailCollection.setUpdatedTime(new Date());
				request.setCreatedTime(deliveryDetailCollection.getCreatedTime());
			} else {
				deliveryDetailCollection = new AlignerDeliveryDetailCollection();
				request.setUpdatedTime(new Date());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, deliveryDetailCollection);
			deliveryDetailCollection = alignerDeliveryDetailRepository.save(deliveryDetailCollection);
			response = new AlignerDeliveryDetailResponse();
			BeanUtil.map(deliveryDetailCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public AlignerIPRAndAttachmetDetail addAlignerIPRAndAttachmetDetail(AlignerIPRAndAttachmetDetail request) {
		AlignerIPRAndAttachmetDetail response = null;
		try {
			AlignerUserPlanCollection userPlanCollection = userPlanRepository.findById(new ObjectId(request.getPlanId()))
					.orElse(null);
			if (userPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			userPlanCollection.setiPROnAligner(request.getiPROnAligner());
			userPlanCollection.setAttachmentOnAligner(request.getAttachmentOnAligner());
			userPlanCollection.setiPRROnTeeth(request.getiPRROnTeeth());
			userPlanCollection.setAttachmentTeeth(request.getAttachmentTeeth());
			userPlanCollection = userPlanRepository.save(userPlanCollection);
			response = new AlignerIPRAndAttachmetDetail();
			BeanUtil.map(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}
