package com.dpdocter.services.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dpdocter.beans.CompanyDetail;
import com.dpdocter.beans.Country;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.SafePlaceMobileNumber;
import com.dpdocter.collections.CompanyDetailCollection;
import com.dpdocter.collections.ConfexUserCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;
import com.dpdocter.collections.SafePlaceMobileNumberCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.enums.ConfexUserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CompanyDetailRepository;
import com.dpdocter.repository.ConfexUserRepository;
import com.dpdocter.repository.DashboardUserRepository;
import com.dpdocter.repository.SafePlaceMobileNumberRepository;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.CompanyDetailService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.security.AES;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

//for covid project

@Service
public class CompanyDetailServiceImpl implements CompanyDetailService {

	private static Logger logger = LogManager.getLogger(CompanyDetailServiceImpl.class.getName());

	@Autowired
	CompanyDetailRepository companyDetailRepository;

	@Autowired
	ConfexUserRepository confexUserRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FileManager fileManager;

	@Value(value = "${secret.key.account.details}")
	private String secretKeyAccountDetails;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private DashboardUserRepository dashboardUserRepository;
	
	
	@Autowired
	SafePlaceMobileNumberRepository safePlaceMobileNumberRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Override
	public CompanyDetail addEditCompanyDetail(CompanyDetail request) {
		CompanyDetail response = null;

		try {
			CompanyDetailCollection companyDetailCollection = null;
			ConfexUserCollection confexUserCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {

				if (!DPDoctorUtils.anyStringEmpty(request.getCompanyEmailId())) {
					logger.warn("Email Address cannot be change");
					throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be change");
				}
				companyDetailCollection = companyDetailRepository.findById(new ObjectId(request.getId())).orElse(null);

//				confexUserCollection = confexUserRepository.findByCompanyId(new ObjectId(request.getId()));

				confexUserCollection = confexUserRepository
						.findByEmailAddress(companyDetailCollection.getCompanyEmailId());

				if (companyDetailCollection == null && confexUserCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "company Not found with Id & email");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(companyDetailCollection.getCreatedBy());
				request.setCompanyEmailId(companyDetailCollection.getCompanyEmailId());

				BeanUtil.map(request, companyDetailCollection);
				companyDetailCollection = companyDetailRepository.save(companyDetailCollection);
				confexUserCollection.setId(confexUserCollection.getId());
				// confexUserCollection.setEmailAddress(companyDetailCollection.getCompanyEmailId());
				// confexUserCollection.setUserName(companyDetailCollection.getCompanyEmailId());
				confexUserCollection.setUserType(ConfexUserState.ADMIN);
				confexUserCollection.setFirstName(companyDetailCollection.getAdminName());
				confexUserCollection.setMobileNumber(companyDetailCollection.getCompanyNumber());
				confexUserCollection.setIsActive(companyDetailCollection.getIsActive());
				// confexUserCollection.setCompanyId(companyDetailCollection.getId());
				confexUserRepository.save(confexUserCollection);

			} else {
				if (DPDoctorUtils.anyStringEmpty(request.getCompanyEmailId())) {
					logger.warn("Email Address cannot be null");
					throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
				}
				CompanyDetailCollection companyDetailCollectionForMail = companyDetailRepository
						.findBycompanyEmailId(request.getCompanyEmailId());

				ConfexUserCollection confexUserCollectionForMail = confexUserRepository
						.findByEmailAddress(request.getCompanyEmailId());

				if (companyDetailCollectionForMail != null && confexUserCollectionForMail != null) {
					logger.warn("Email Address Already Present");
					throw new BusinessException(ServiceError.InvalidInput, "Email Address Already Present");
				}

				companyDetailCollection = new CompanyDetailCollection();
				BeanUtil.map(request, companyDetailCollection);
				// companyDetailCollection.setCreatedBy("ADMIN");
				companyDetailCollection.setUpdatedTime(new Date());
				companyDetailCollection.setCreatedTime(new Date());
				companyDetailCollection = companyDetailRepository.save(companyDetailCollection);

				confexUserCollection = new ConfexUserCollection();
				confexUserCollection.setEmailAddress(companyDetailCollection.getCompanyEmailId());
				confexUserCollection.setUserName(companyDetailCollection.getCompanyEmailId());
				confexUserCollection.setUserType(ConfexUserState.ADMIN);
				confexUserCollection.setFirstName(companyDetailCollection.getAdminName());
				confexUserCollection.setMobileNumber(companyDetailCollection.getCompanyNumber());
				confexUserCollection.setIsActive(companyDetailCollection.getIsActive());
				confexUserCollection.setCompanyId(companyDetailCollection.getId());
				confexUserRepository.save(confexUserCollection);

				// save token
				TokenCollection tokenCollection = new TokenCollection();
				tokenCollection.setResourceId(confexUserCollection.getId());
				tokenCollection.setCreatedTime(new Date());
				tokenCollection = tokenRepository.save(tokenCollection);

				String body = mailBodyGenerator.generateAdminActivationEmailBody(companyDetailCollection.getAdminName(),
						companyDetailCollection.getCompanyEmailId(), tokenCollection.getId(), "companyMailTemplate.vm");

				mailService.sendEmail(companyDetailCollection.getCompanyEmailId(), "Please set your password", body,
						null);
			}

//			companyDetailCollection
//					.setCompanyName(AES.encrypt(companyDetailCollection.getCompanyName(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyAddress(
//					AES.encrypt(companyDetailCollection.getCompanyAddress(), secretKeyAccountDetails));
//			companyDetailCollection
//					.setAdminName(AES.encrypt(companyDetailCollection.getAdminName(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyEmailId(
//					AES.encrypt(companyDetailCollection.getCompanyEmailId(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyWebsite(
//					AES.encrypt(companyDetailCollection.getCompanyWebsite(), secretKeyAccountDetails));
//			companyDetailCollection
//					.setCompanyNumber(AES.encrypt(companyDetailCollection.getCompanyNumber(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyLogoImageUrl(
//					AES.encrypt(companyDetailCollection.getCompanyLogoImageUrl(), secretKeyAccountDetails));

//			companyDetailCollection = companyDetailRepository.save(companyDetailCollection);

			response = new CompanyDetail();
			BeanUtil.map(companyDetailCollection, response);

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while add/edit company  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit company " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<CompanyDetail> getCompanyDetail(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<CompanyDetail> response = null;
		List<CompanyDetail> companyDetails = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("companyName").regex("^" + searchTerm, "i"),
						new Criteria("companyName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("companyName", "$companyName").append("companyNumber", "$companyNumber")
								.append("companyLogoImageUrl", "$companyLogoImageUrl")
								.append("companyEmailId", "$companyEmailId").append("companyWebsite", "$companyWebsite")
								.append("createdTime", "$createdTime").append("companyAddress", "$companyAddress")
								.append("city", "$city").append("state", "$state").append("country", "$country")
								.append("numberOfEmployees", "$numberOfEmployees").append("adminName", "$adminName")
								.append("activationState", "$activationState").append("isDiscarded", "$isDiscarded")
								.append("isActive", "$isActive").append("entity", "$entity"))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
								.append("companyName", "$companyName").append("companyNumber", "$companyNumber")
								.append("companyLogoImageUrl", "$companyLogoImageUrl")
								.append("companyEmailId", "$companyEmailId").append("companyWebsite", "$companyWebsite")
								.append("createdTime", "$createdTime").append("companyAddress", "$companyAddress")
								.append("city", "$city").append("state", "$state").append("country", "$country")
								.append("numberOfEmployees", "$numberOfEmployees").append("adminName", "$adminName")
								.append("activationState", "$activationState").append("isDiscarded", "$isDiscarded")
								.append("isActive", "$isActive").append("entity", "$entity"))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, CompanyDetailCollection.class, CompanyDetail.class)
					.getMappedResults();

//			if (companyDetails != null)
//				for (CompanyDetail companyDetailCollection : companyDetails) {
//
//					companyDetailCollection.setCompanyName(
//							AES.decrypt(companyDetailCollection.getCompanyName(), secretKeyAccountDetails));
//					companyDetailCollection.setCompanyAddress(
//							AES.decrypt(companyDetailCollection.getCompanyAddress(), secretKeyAccountDetails));
//					companyDetailCollection
//							.setAdminName(AES.decrypt(companyDetailCollection.getAdminName(), secretKeyAccountDetails));
//					companyDetailCollection.setCompanyEmailId(
//							AES.decrypt(companyDetailCollection.getCompanyEmailId(), secretKeyAccountDetails));
//					companyDetailCollection.setCompanyWebsite(
//							AES.decrypt(companyDetailCollection.getCompanyWebsite(), secretKeyAccountDetails));
//					companyDetailCollection.setCompanyNumber(
//							AES.decrypt(companyDetailCollection.getCompanyNumber(), secretKeyAccountDetails));
//					companyDetailCollection.setCompanyLogoImageUrl(
//							AES.decrypt(companyDetailCollection.getCompanyLogoImageUrl(), secretKeyAccountDetails));
//
//				}
//			response = new ArrayList<CompanyDetail>();
//			BeanUtil.map(companyDetails, response);

		} catch (BusinessException e) {
			logger.error("Error while getting company " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting company " + e.getMessage());

		}
		return response;

	}

	@Override
	public Integer countCompanyDetail(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("companyName").regex("^" + searchTerm, "i"),
					new Criteria("companyName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), CompanyDetailCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting company " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while company " + e.getMessage());

		}
		return response;
	}

	@Override
	public Boolean deleteCompanyDetail(String companyId, Boolean isDiscarded) {
		Boolean response = null;
		try {
			CompanyDetailCollection companyDetailCollection = companyDetailRepository.findById(new ObjectId(companyId))
					.orElse(null);

//			ConfexUserCollection confexUserCollection = confexUserRepository.findByCompanyId(new ObjectId(companyId));
			ConfexUserCollection confexUserCollection = confexUserRepository
					.findByEmailAddress(companyDetailCollection.getCompanyEmailId());

			if (companyDetailCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id to delete");
			}
			companyDetailCollection.setIsDiscarded(isDiscarded);
			confexUserCollection.setIsDiscarded(isDiscarded);
			companyDetailCollection.setUpdatedTime(new Date());
			companyDetailCollection = companyDetailRepository.save(companyDetailCollection);
			confexUserCollection = confexUserRepository.save(confexUserCollection);
//			BeanUtil.map(companyDetailCollection, response);

			response = true;
		} catch (BusinessException e) {
			logger.error("Error while deleting the company  " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while deleting the company");
		}
		return response;
	}

	@Override
	public CompanyDetail getCompanyDetailById(String companyId) {
		CompanyDetail response = null;
		try {
			CompanyDetailCollection companyDetailCollection = companyDetailRepository.findById(new ObjectId(companyId))
					.orElse(null);
			if (companyDetailCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

//			companyDetailCollection
//					.setCompanyName(AES.decrypt(companyDetailCollection.getCompanyName(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyAddress(
//					AES.decrypt(companyDetailCollection.getCompanyAddress(), secretKeyAccountDetails));
//			companyDetailCollection
//					.setAdminName(AES.decrypt(companyDetailCollection.getAdminName(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyEmailId(
//					AES.decrypt(companyDetailCollection.getCompanyEmailId(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyWebsite(
//					AES.decrypt(companyDetailCollection.getCompanyWebsite(), secretKeyAccountDetails));
//			companyDetailCollection
//					.setCompanyNumber(AES.decrypt(companyDetailCollection.getCompanyNumber(), secretKeyAccountDetails));
//			companyDetailCollection.setCompanyLogoImageUrl(
//					AES.decrypt(companyDetailCollection.getCompanyLogoImageUrl(), secretKeyAccountDetails));
			response = new CompanyDetail();
			BeanUtil.map(companyDetailCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}

//	@Override
//	public ImageURLResponse uploadImages(FileDetails request, String module) {
//		ImageURLResponse response = new ImageURLResponse();
//		try {
//			request.setFileName(request.getFileName() + new Date().getTime());
//			String path = module + File.separator + "images";
//			ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request, path, true);
//			response.setImageUrl(getFinalImageURL(imageURLResponse.getImageUrl()));
//			response.setThumbnailUrl(getFinalImageURL(imageURLResponse.getThumbnailUrl()));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//			throw new BusinessException(ServiceError.Unknown, e.getMessage());
//		}
//		return response;
//	}
//
//	private String getFinalImageURL(String imageURL) {
//		if (imageURL != null)
//			return imagePath + imageURL;
//		else
//			return null;
//	}

	@Override
	public Boolean activeAdmin(String id, Boolean isActive) {
		Boolean response = false;

		try {

			CompanyDetailCollection companyDetailCollection = companyDetailRepository.findById(new ObjectId(id))
					.orElse(null);

//			ConfexUserCollection confexUserCollection = confexUserRepository.findByCompanyId(new ObjectId(id));
			ConfexUserCollection confexUserCollection = confexUserRepository
					.findByEmailAddress(companyDetailCollection.getCompanyEmailId());

			if (companyDetailCollection != null) {
				companyDetailCollection.setIsActive(isActive);
				confexUserCollection.setIsActive(isActive);
				companyDetailCollection.setUpdatedTime(new Date());
				companyDetailCollection = companyDetailRepository.save(companyDetailCollection);
				confexUserCollection = confexUserRepository.save(confexUserCollection);
//				BeanUtil.map(companyDetailCollection, response);

				response = true;

			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Activating confex Admin");
			throw new BusinessException(ServiceError.Unknown, "Error While Activating Admin");
		}
		return response;
	}

	@Override
	public ImageURLResponse uploadImageMultipart(MultipartFile file) {
		String recordPath = null;
		ImageURLResponse response = null;

		try {

			Date createdTime = new Date();
			if (file != null) {

				String path = "safeappdashboard" + File.separator;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

				recordPath = path + fileName + createdTime.getTime() + "." + fileExtension;
				response = saveImage(file, recordPath, false);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public ImageURLResponse saveImage(MultipartFile file, String recordPath, Boolean createThumbnail) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);
		ImageURLResponse response = new ImageURLResponse();
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
//			if (createThumbnail) {
//				response.setThumbnailUrl(imagePath + saveThumbnailUrl(file, recordPath));
//			}
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
	public List<SafePlaceMobileNumber> getMobileNumbers(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<SafePlaceMobileNumber> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));
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
					.aggregate(aggregation, SafePlaceMobileNumberCollection.class, SafePlaceMobileNumber.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Subscription Payment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Subscription Payment" + e.getMessage());

		}
		return response;
	}

	@Override
	public Integer getMobileNumbersCount(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
					new Criteria("mobileNumber").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), SafePlaceMobileNumberCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while  " + e.getMessage());

		}
		return response;
	}

	@Override
	public SafePlaceMobileNumber addEditMobileNumberReason(SafePlaceMobileNumber request) {
		SafePlaceMobileNumber response = null;

		try {
			SafePlaceMobileNumberCollection safePlaceMobileNumberCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				safePlaceMobileNumberCollection = safePlaceMobileNumberRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (safePlaceMobileNumberCollection == null) {
					throw new BusinessException(ServiceError.NotFound, " Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(safePlaceMobileNumberCollection.getCreatedBy());
				BeanUtil.map(request, safePlaceMobileNumberCollection);

			} else {
				safePlaceMobileNumberCollection = new SafePlaceMobileNumberCollection();
				BeanUtil.map(request, safePlaceMobileNumberCollection);
				safePlaceMobileNumberCollection.setCreatedBy("ADMIN");
				safePlaceMobileNumberCollection.setUpdatedTime(new Date());
				safePlaceMobileNumberCollection.setCreatedTime(new Date());
			}
			safePlaceMobileNumberCollection = safePlaceMobileNumberRepository.save(safePlaceMobileNumberCollection);
			response = new SafePlaceMobileNumber();
			BeanUtil.map(safePlaceMobileNumberCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit   " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit  " + e.getMessage());

		}
		return response;
	}

}
