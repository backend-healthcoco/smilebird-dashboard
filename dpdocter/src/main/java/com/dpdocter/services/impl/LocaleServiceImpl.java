package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DataCount;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.PharmacyTypeEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditLocaleAddressDetailsRequest;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocaleService;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;

@Service
public class LocaleServiceImpl implements LocaleService {

	private static Logger logger = LogManager.getLogger(LocaleServiceImpl.class.getName());
	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	FileManager fileManager;

	@Autowired
	private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

	@Autowired
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private TransactionalManagementServiceImpl transnationalService;

	@Autowired
	ESLocaleService esLocaleService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${set.pharmacy.password.link}")
	private String setPharmacyPasswordLink;

	@Override
	@Transactional
	public Locale getLocaleDetails(String id) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findById(new ObjectId(id)).orElse(null);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {
			for (LocaleImage image : localeCollection.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}
		}
		response = new Locale();
		BeanUtil.map(localeCollection, response);
		return response;
	}

	@Override
	@Transactional
	public Locale getLocaleDetailsByContactDetails(String contactNumber) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findByContactNumber(contactNumber);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {
			for (LocaleImage image : localeCollection.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}
		}
		response = new Locale();
		BeanUtil.map(localeCollection, response);

		return response;
	}

	@Override
	@Transactional
	public Locale activateDeactivateLocale(String id, Boolean isActivate) {
		Locale response = null;
		try {

			LocaleCollection localeCollection = localeRepository.findById(new ObjectId(id)).orElse(null);
			if (localeCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Id");
			}
			if (!localeCollection.getIsVerified() && isActivate) {
				throw new BusinessException(ServiceError.NotAcceptable, "Pharmacy not verified");
			}
			localeCollection.setIsActivate(isActivate);
			response = new Locale();

			localeRepository.save(localeCollection);

			BeanUtil.map(localeCollection, response);
			if (response.getLocaleImages() != null && !response.getLocaleImages().isEmpty()) {

				for (LocaleImage image : response.getLocaleImages()) {
					image.setImageUrl(imagePath + image.getImageUrl());
					image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
				}

			}
			UserCollection userCollection = userRepository.findByMobileNumberAndUserState(localeCollection.getContactNumber(),
					UserState.PHARMACY.toString());

			if (userCollection != null) {
				userCollection.setIsActive(isActivate);
				userCollection = userRepository.save(userCollection);

				ESUserLocaleDocument esUserLocaleDocument = new ESUserLocaleDocument();
				BeanUtil.map(userCollection, esUserLocaleDocument);
				BeanUtil.map(localeCollection, esUserLocaleDocument);
				esUserLocaleDocument.setUserId(userCollection.getId().toString());
				esUserLocaleDocument.setLocaleId(localeCollection.getId().toString());

				esRegistrationService.addLocale(esUserLocaleDocument);
				if (isActivate) {
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(userCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null,
							"Hi,your account has been activated. please reset your password " + setPharmacyPasswordLink
									+ "?uid=" + tokenCollection.getId() + "\n Thank you.",
							userCollection.getMobileNumber(), "resetPassword");

					sMSServices.sendSMS(smsTrackDetail, false);

				} else {
					List<OAuth2AuthenticationRefreshTokenCollection> refreshTokenCollections = oAuth2RefreshTokenRepository
							.findByAuthenticationUserAuthenticationDetailsClient_IdAndAuthenticationUserAuthenticationDetailsUsername("healthco2business", userCollection.getMobileNumber());
					if (!refreshTokenCollections.isEmpty() && refreshTokenCollections != null) {
						oAuth2RefreshTokenRepository.deleteAll(refreshTokenCollections);
					}

					List<OAuth2AuthenticationAccessTokenCollection> accessTokenCollections = oAuth2AccessTokenRepository
							.findByClientIdAndUserName("healthco2business", userCollection.getMobileNumber());
					if (!accessTokenCollections.isEmpty() && accessTokenCollections != null) {
						oAuth2AccessTokenRepository.deleteAll(accessTokenCollections);
					}
				}

			} else {
				logger.warn("Mobile Number is empty.");
				throw new BusinessException(ServiceError.InvalidInput, "Mobile Number is empty.");
			}
		} catch (BusinessException be) {
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<Locale> getLocaleList(int page, int size, String searchTerm, Boolean isListed) {
		List<Locale> response = null;
		// String searchTerm = null;
		Criteria criteria = new Criteria();
		try {
			if (isListed != null) {
				criteria = criteria.and("isLocaleListed").is(isListed);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = new Criteria().orOperator(new Criteria("localeName").regex("^" + searchTerm, "i"),
						(new Criteria("contactNumber").regex("^" + searchTerm, "i")));
			/*
			 * if (!DPDoctorUtils.anyStringEmpty(contactState)) criteria =
			 * criteria.and("contactStateType").is(LocaleContactStateType.
			 * valueOf(contactState));
			 */
			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<Locale> aggregationResults = mongoTemplate.aggregate(aggregation, LocaleCollection.class,
					Locale.class);
			response = aggregationResults.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (Locale locale : response) {
					if (locale.getLocaleImages() != null && !locale.getLocaleImages().isEmpty()) {

						for (LocaleImage image : locale.getLocaleImages()) {
							image.setImageUrl(imagePath + image.getImageUrl());
							image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting locale List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer countLocaleList(String searchTerm, Boolean isListed) {
		Integer response = 0;

		Criteria criteria = new Criteria();
		if (isListed != null) {
			criteria = criteria.and("isLocaleListed").is(isListed);
		}
		try {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = new Criteria().orOperator(new Criteria("localeName").regex("^" + searchTerm, "i"),
						(new Criteria("contactNumber").regex("^" + searchTerm, "i")));

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<Locale> aggregationResults = mongoTemplate.aggregate(aggregation, LocaleCollection.class,
					Locale.class);
			response = aggregationResults.getMappedResults().size();

		} catch (Exception e) {
			logger.error("Error while count locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count locale List " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Locale updateLocaleAddress(AddEditLocaleAddressDetailsRequest request) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findById(new ObjectId(request.getId())).orElse(null);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		// System.out.println(request.getAddress());

		if (request.getAddress() != null) {
			localeCollection.setAddress(request.getAddress());
			localeCollection.setLocaleAddress((!DPDoctorUtils.anyStringEmpty(request.getAddress().getStreetAddress())
					? request.getAddress().getStreetAddress() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getLandmarkDetails())
							? request.getAddress().getLandmarkDetails() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getLocality())
							? request.getAddress().getLocality() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getCity())
							? request.getAddress().getCity() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getState())
							? request.getAddress().getState() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getCountry())
							? request.getAddress().getCountry() + ", " : "")
					+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getPostalCode())
							? request.getAddress().getPostalCode() : ""));
			if ((request.getAddress().getLatitude() != null && request.getAddress().getLatitude() != 0)
					&& (request.getAddress().getLongitude() != null && request.getAddress().getLongitude() != 0)) {
				localeCollection.getAddress().setLatitude(request.getAddress().getLatitude());
				localeCollection.getAddress().setLongitude(request.getAddress().getLongitude());
				esLocaleService.updateGeoPoint(request.getId(), request.getAddress());

			}
		}
		// localeCollection.setLocaleAddress();

		localeCollection = localeRepository.save(localeCollection);
		transnationalService.checkPharmacy(localeCollection.getId());
		response = new Locale();
		BeanUtil.map(localeCollection, response);
		if (response.getLocaleImages() != null && !response.getLocaleImages().isEmpty()) {

			for (LocaleImage image : response.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}

		}
		return response;
	}

	@Override
	@Transactional
	public boolean deleteLocaleImage(String localeId, List<String> imageIds) {
		boolean response = false;
		try {
			LocaleCollection localeCollection = localeRepository.findById(new ObjectId(localeId)).orElse(null);
			if (localeCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
			} else {
				List<LocaleImage> localeImage = localeCollection.getLocaleImages();
				if (localeImage != null && !localeImage.isEmpty()) {
					for (int i = 0; i < localeImage.size(); i++) {

						for (String imageId : imageIds) {
							if (!DPDoctorUtils.anyStringEmpty(localeImage.get(i).getId())) {
								if (imageId.equals(localeImage.get(i).getId())) {
									localeImage.remove(localeImage.get(i));
								}
							}
						}
					}
					localeCollection.setLocaleImages(localeImage);
					localeRepository.save(localeCollection);
					transnationalService.checkPharmacy(localeCollection.getId());
				}
				response = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Locale editLocaleprofile(Locale request) {
		Locale response = null;
		String slugUrl = null;
		Integer count = 0;
		try {
			LocaleCollection localeCollection = localeRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (localeCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
			}
			if (request.getLocaleImages() != null && !request.getLocaleImages().isEmpty()) {
				for (LocaleImage image : request.getLocaleImages()) {
					image.setImageUrl(image.getImageUrl().trim().replace(imagePath, ""));
					image.setThumbnailUrl(image.getThumbnailUrl().trim().replace(imagePath, ""));
				}
			}
			BeanUtil.map(request, localeCollection);
			if (request.getAddress() != null) {
				localeCollection.setAddress(request.getAddress());
				localeCollection
						.setLocaleAddress((!DPDoctorUtils.anyStringEmpty(request.getAddress().getStreetAddress())
								? request.getAddress().getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getLandmarkDetails())
										? request.getAddress().getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getLocality())
										? request.getAddress().getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getCity())
										? request.getAddress().getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getState())
										? request.getAddress().getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getCountry())
										? request.getAddress().getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(request.getAddress().getPostalCode())
										? request.getAddress().getPostalCode() : ""));
				if ((request.getAddress().getLatitude() != null && request.getAddress().getLatitude() != 0)
						&& (request.getAddress().getLongitude() != null && request.getAddress().getLongitude() != 0)) {
					localeCollection.getAddress().setLatitude(request.getAddress().getLatitude());
					localeCollection.getAddress().setLongitude(request.getAddress().getLongitude());
				}
			}
			if (request.getPharmacyType() != null) {

				List<String> types = new ArrayList<String>();
				for (String type : request.getPharmacyType()) {
					if (type.toUpperCase().equals(PharmacyTypeEnum.ALLOPATHIC.toString())
							|| type.toUpperCase().equals(PharmacyTypeEnum.HOMEOPATHIC.toString())
							|| type.toUpperCase().equals(PharmacyTypeEnum.AYURVEDIC.toString())
							|| type.toUpperCase().equals(PharmacyTypeEnum.GENERIC.toString())
							|| type.toUpperCase().equals(PharmacyTypeEnum.VETERINARY.toString())
							|| type.toUpperCase().equals(PharmacyTypeEnum.SURGICAL.toString())) {
						types.add(type.toUpperCase());
					}
					localeCollection.setPharmacyType(new ArrayList<String>());
					localeCollection.setPharmacyType(types);

				}
			}
			if (request.getPaymentInfos() != null) {

				List<String> infos = new ArrayList<String>();
				for (String info : request.getPaymentInfos()) {
					infos.add(info.toUpperCase());
				}
				localeCollection.setPaymentInfos(new ArrayList<String>());
				localeCollection.setPaymentInfos(infos);
			}
			if (request.getLocaleWorkingSchedules() != null) {
				localeCollection.setLocaleWorkingSchedules(request.getLocaleWorkingSchedules());
			}
			if (request.getIsLocaleListed() && DPDoctorUtils.anyStringEmpty(localeCollection.getPharmacySlugUrl())) {
				if (!DPDoctorUtils.anyStringEmpty(localeCollection.getLocaleName())) {
					slugUrl = localeCollection.getLocaleName();

					if (!localeCollection.getLocaleType().isEmpty() && localeCollection.getLocaleType() != null) {
						for (String type : localeCollection.getPharmacyType()) {
							if (DPDoctorUtils.anyStringEmpty(slugUrl)) {
								slugUrl = type;
							} else {
								slugUrl = slugUrl + "-" + type;
							}
						}
					}
					slugUrl = slugUrl.trim().replaceAll(",", "-").replaceAll("/", "-").replaceAll(" ", "-")
							.replaceAll("'", "");
					count = localeRepository.countBySlugUrl(slugUrl);
					slugUrl = slugUrl + "-0" + (count == null ? 1 : (count + 1));
					if (!DPDoctorUtils.anyStringEmpty(slugUrl)) {
						localeCollection.setPharmacySlugUrl(slugUrl.trim().replaceAll(",", "-").replaceAll("/", "-")
								.replaceAll(" ", "-").replaceAll("'", ""));
					}

				}
			}
			localeCollection.setUpdatedTime(new Date());
			localeCollection = localeRepository.save(localeCollection);
			transnationalService.checkPharmacy(localeCollection.getId());
			response = new Locale();
			BeanUtil.map(localeCollection, response);
			if (response.getLocaleImages() != null && !response.getLocaleImages().isEmpty()) {

				for (LocaleImage image : response.getLocaleImages()) {
					image.setImageUrl(imagePath + image.getImageUrl());
					image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Occure while edit Locale");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addLocaleImage(ClinicImageAddRequest request) {
		Boolean response = false;
		try {

			ImageURLResponse imageURLResponse = null;
			Date createdTime = new Date();
			List<LocaleImage> localeImages = null;
			LocaleCollection localeCollection = localeRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (localeCollection != null) {
				String path = "locale" + File.separator + request.getId();

				// String recordLabel = fileName;
				if (request.getImages() != null) {

					if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {

						localeImages = new ArrayList<LocaleImage>(localeCollection.getLocaleImages());

					} else {
						localeImages = new ArrayList<LocaleImage>();

					}

					for (FileDetails image : request.getImages()) {
						image.setFileName(image.getFileName() + new Date().getTime());
						imageURLResponse = fileManager.saveImageAndReturnImageUrl(image, path, true);
						LocaleImage imageresponse = new LocaleImage();
						imageresponse.setImageUrl(imageURLResponse.getImageUrl());
						imageresponse.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						imageresponse.setId("File" + DPDoctorUtils.generateRandomId());
						localeImages.add(imageresponse);
					}

				}
				localeCollection.setLocaleImages(localeImages);
				localeCollection.setUpdatedTime(new Date());
				localeCollection = localeRepository.save(localeCollection);

				transnationalService.checkPharmacy(localeCollection.getId());

				response = true;
			}

			else {
				throw new BusinessException(ServiceError.InvalidInput, "locale not found");
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			// logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DataCount getLocaleCount() {
		DataCount response = new DataCount();
		try {

			response.setCountOfListed(localeRepository.countListed(true));

			response.setCountOfUnListed(localeRepository.countListed(false));
			response.setTotal((int) localeRepository.count());

		} catch (Exception e) {
			logger.error("Error while count locales " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while count locale List " + e.getMessage());
		}
		return response;
	}

}
