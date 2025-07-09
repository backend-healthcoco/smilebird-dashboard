package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.Range;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.response.DiseaseAddEditResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.HistoryServices;

import common.util.web.DPDoctorUtils;

@Service
public class HistoryServicesImpl implements HistoryServices {

	private static Logger logger = LogManager.getLogger(HistoryServicesImpl.class.getName());

	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public List<DiseaseAddEditResponse> addDiseases(List<DiseaseAddEditRequest> request) {
		List<DiseaseAddEditResponse> response = new ArrayList<DiseaseAddEditResponse>();
		try {
			for (DiseaseAddEditRequest addEditRequest : request) {
				addEditRequest.setCreatedTime(new Date());
				DiseasesCollection diseasesCollection = new DiseasesCollection();
				BeanUtil.map(addEditRequest, diseasesCollection);
				if (!DPDoctorUtils.anyStringEmpty(addEditRequest.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(new ObjectId(addEditRequest.getDoctorId())).orElse(null);
					if (userCollection != null) {
						diseasesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diseasesCollection.setCreatedBy("ADMIN");
				}
				diseasesCollection = diseasesRepository.save(diseasesCollection);
				DiseaseAddEditResponse addEditResponse = new DiseaseAddEditResponse();
				BeanUtil.map(diseasesCollection, addEditResponse);
				response.add(addEditResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Disease(s)");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Disease(s)");
		}
		return response;
	}

	@Override
	@Transactional
	public DiseaseAddEditResponse editDiseases(DiseaseAddEditRequest request) {
		DiseaseAddEditResponse response = null;
		DiseasesCollection disease = new DiseasesCollection();
		BeanUtil.map(request, disease);
		try {
			DiseasesCollection oldDisease = diseasesRepository.findById(new ObjectId(request.getId())).orElse(null);
			disease.setCreatedBy(oldDisease.getCreatedBy());
			disease.setCreatedTime(oldDisease.getCreatedTime());
			disease.setDiscarded(oldDisease.getDiscarded());
			disease = diseasesRepository.save(disease);
			response = new DiseaseAddEditResponse();
			BeanUtil.map(disease, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Disease");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Disease");
		}
		return response;
	}

	@Override
	@Transactional
	public DiseaseAddEditResponse deleteDisease(String diseaseId, String doctorId, String hospitalId, String locationId,
			Boolean discarded) {
		DiseaseAddEditResponse response = null;
		DiseasesCollection disease = null;
		try {
			disease = diseasesRepository.findById(new ObjectId(diseaseId)).orElse(null);
			if (disease != null) {
				if (disease.getDoctorId() != null && disease.getHospitalId() != null
						&& disease.getLocationId() != null) {
					if (disease.getDoctorId().equals(doctorId) && disease.getHospitalId().equals(hospitalId)
							&& disease.getLocationId().equals(locationId)) {
						disease.setDiscarded(discarded);
						disease.setUpdatedTime(new Date());
						disease = diseasesRepository.save(disease);
						response = new DiseaseAddEditResponse();
						BeanUtil.map(disease, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					disease.setDiscarded(discarded);
					disease.setUpdatedTime(new Date());
					disease = diseasesRepository.save(disease);
					response = new DiseaseAddEditResponse();
					BeanUtil.map(disease, response);
				}
			} else {
				logger.warn("Disease Not Found");
				throw new BusinessException(ServiceError.NotFound, "Disease Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Disease");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Disease");
		}
		return response;
	}

	@Override
	@Transactional
	public List<DiseaseListResponse> getDiseases(String range, int page, int size, String doctorId, String hospitalId,
			String locationId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm) {
		List<DiseaseListResponse> diseaseListResponses = null;

		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			if (isAdmin)
				diseaseListResponses = getGlobalDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
			else
				diseaseListResponses = getGlobalDiseases(page, size, updatedTime, discarded);
			break;
		case CUSTOM:
			if (isAdmin)
				diseaseListResponses = getCustomDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
			else
				diseaseListResponses = getCustomDiseases(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
			break;
		case BOTH:
			if (isAdmin)
				diseaseListResponses = getCustomGlobalDiseasesForAdmin(page, size, updatedTime, discarded, searchTerm);
			else
				diseaseListResponses = getCustomGlobalDiseases(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
			break;
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getCustomDiseases(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (locationObjectId == null && hospitalObjectId == null) {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
							new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
							new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
							hospitalObjectId, new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
							hospitalObjectId, new Date(createdTimeStamp), discards,
							new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getGlobalDiseases(int page, int size, String updatedTime, Boolean discarded) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			if (size > 0)
				diseasesCollections = diseasesRepository.findByUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp), discards,
						PageRequest.of(page, size, Direction.DESC, "updatedTime"));
			else
				diseasesCollections = diseasesRepository.findByUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp), discards,
						new Sort(Sort.Direction.DESC, "updatedTime"));

			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getCustomGlobalDiseases(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (locationObjectId == null && hospitalObjectId == null) {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
							new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
							new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, locationObjectId,
							hospitalObjectId, new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findCustomGlobalDiseases(doctorObjectId, locationObjectId,
							hospitalObjectId, new Date(createdTimeStamp), discards,
							new Sort(Sort.Direction.DESC, "updatedTime"));
			}

			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getCustomDiseasesForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);

			long createdTimeStamp = Long.parseLong(updatedTime);
			if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp),
							discards, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp),
							discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp),
							discards, searchTerm, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findCustomDiseasesForAdmin(new Date(createdTimeStamp),
							discards, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getGlobalDiseasesForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdNullAndUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp),
							discards, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdNullAndUpdatedTimeGreaterThanAndDiscardedIn(new Date(createdTimeStamp),
							discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp),
							discards, searchTerm, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findGlobalDiseasesForAdmin(new Date(createdTimeStamp),
							searchTerm, discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			}

			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	private List<DiseaseListResponse> getCustomGlobalDiseasesForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<DiseaseListResponse> diseaseListResponses = null;
		List<DiseasesCollection> diseasesCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);

			long createdTimeStamp = Long.parseLong(updatedTime);
			if (DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (size > 0)
					diseasesCollections = diseasesRepository.findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(
							new Date(createdTimeStamp), discards,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findByDoctorIdNotNullAndUpdatedTimeGreaterThanAndDiscardedIn(
							new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(
							new Date(createdTimeStamp), discards, searchTerm,
							PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					diseasesCollections = diseasesRepository.findCustomGlobalDiseasesForAdmin(
							new Date(createdTimeStamp), discards, searchTerm,
							new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse(
							diseasesCollection.getId().toString(), diseasesCollection.getDisease(),
							diseasesCollection.getExplanation(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getDoctorId()) ? null
									: diseasesCollection.getDoctorId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getLocationId()) ? null
									: diseasesCollection.getLocationId().toString(),
							DPDoctorUtils.anyStringEmpty(diseasesCollection.getHospitalId()) ? null
									: diseasesCollection.getHospitalId().toString(),
							diseasesCollection.getDiscarded(), diseasesCollection.getCreatedTime(),
							diseasesCollection.getUpdatedTime(), diseasesCollection.getCreatedBy());
					diseaseListResponses.add(diseaseListResponse);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<DiseaseListResponse> getDiseasesByIds(List<ObjectId> medicalHistoryIds) {
		List<DiseaseListResponse> diseaseListResponses = null;
		try {
			List<DiseasesCollection> diseasesCollections = IteratorUtils
					.toList(diseasesRepository.findAllById(medicalHistoryIds).iterator());

			if (diseasesCollections != null) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				for (DiseasesCollection diseasesCollection : diseasesCollections) {
					DiseaseListResponse diseaseListResponse = new DiseaseListResponse();
					BeanUtil.map(diseasesCollection, diseaseListResponse);
					diseaseListResponses.add(diseaseListResponse);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diseaseListResponses;
	}
}
