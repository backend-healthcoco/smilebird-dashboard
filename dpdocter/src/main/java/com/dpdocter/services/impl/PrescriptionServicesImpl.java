package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Advice;
import com.dpdocter.beans.Age;
import com.dpdocter.beans.Amount;
import com.dpdocter.beans.Code;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDosage;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.DrugWithGenericCodes;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.GenericCodesAndReaction;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.AdviceCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDetailInformationCollection;
import com.dpdocter.collections.DrugDirectionCollection;
import com.dpdocter.collections.DrugDosageCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.GenericCodeCollection;
import com.dpdocter.collections.GenericCodesAndReactionsCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LabTestCustomerdetailCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESGenericCodesAndReactions;
import com.dpdocter.elasticsearch.repository.ESGenericCodesAndReactionsRepository;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UnitType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdviceRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugDetailInformationRepository;
import com.dpdocter.repository.DrugDirectionRepository;
import com.dpdocter.repository.DrugDosageRepository;
import com.dpdocter.repository.DrugDurationUnitRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.GenericCodeRepository;
import com.dpdocter.repository.GenericCodesAndReactionsRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugInformationResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.LabTestCustomerResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.multipart.FormDataBodyPart;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Transactional
@Service
public class PrescriptionServicesImpl implements PrescriptionServices {

	private static Logger logger = LogManager.getLogger(PrescriptionServicesImpl.class.getName());

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private AdviceRepository adviceRepository;

	@Autowired
	private DrugDirectionRepository drugDirectionRepository;

	@Autowired
	private DrugTypeRepository drugTypeRepository;

	@Autowired
	private DrugDurationUnitRepository drugDurationUnitRepository;

	@Autowired
	private GenericCodeRepository genericCodeRepository;

	@Autowired
	private GenericCodesAndReactionsRepository genericCodesAndReactionsRepository;

	@Autowired
	private ESGenericCodesAndReactionsRepository esGenericCodesAndReactionsRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private DrugDosageRepository drugDosageRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private LabTestRepository labTestRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Prescription.checkPrescriptionExists}")
	private String checkPrescriptionExists;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Value(value = "${upload.drugs.file}")
	private String UPLOAD_DRUGS;

	@Value(value = "${upload.drugs.detail.file}")
	private String DRUG_DETAIL_INFORMATION_UPLOADED_FILE;

	@Value(value = "${upload.drugs.detail.not.uploaded.file}")
	private String DRUG_DETAIL_INFORMATION_NOT_UPLOADED_FILE;

	@Autowired
	private DrugDetailInformationRepository drugDetailInformationRepository;

	@Override
	@Transactional
	public DrugAddEditResponse addDrug(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		DrugCollection drugCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				drugCollection = new DrugCollection();
				BeanUtil.map(request, drugCollection);
				UUID drugCode = UUID.randomUUID();
				drugCollection.setDrugCode(drugCode.toString());
				if (!DPDoctorUtils.anyStringEmpty(drugCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(drugCollection.getDoctorId()).orElse(null);
					if (userCollection != null)
						drugCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
				} else {
					drugCollection.setCreatedBy("ADMIN");
				}
				Date createdTime = new Date();
				drugCollection.setCreatedTime(createdTime);

			} else {
				drugCollection = drugRepository.findById(new ObjectId(request.getId())).orElse(null);
				drugCollection.setUpdatedTime(new Date());
			}
			if (drugCollection.getDrugType() != null) {
				if (DPDoctorUtils.anyStringEmpty(drugCollection.getDrugType().getId()))
					drugCollection.setDrugType(null);
				else {
					DrugTypeCollection drugTypeCollection = drugTypeRepository
							.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
					if (drugTypeCollection != null) {
						DrugType drugType = new DrugType();
						BeanUtil.map(drugTypeCollection, drugType);
						drugCollection.setDrugType(drugType);
					}
				}
			}
			if (drugCollection.getStrength() != null && drugCollection.getStrength().getStrengthUnit() != null) {
				if (drugCollection.getStrength().getStrengthUnit().getId() == null)
					drugCollection.getStrength().setStrengthUnit(null);
			}
			drugCollection = drugRepository.save(drugCollection);
			response = new DrugAddEditResponse();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugAddEditResponse editDrug(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		DrugCollection drugCollection = new DrugCollection();
		BeanUtil.map(request, drugCollection);
		try {
			DrugCollection oldDrug = drugRepository.findById(new ObjectId(request.getId())).orElse(null);
			drugCollection.setCreatedBy(oldDrug.getCreatedBy());
			drugCollection.setCreatedTime(oldDrug.getCreatedTime());
			drugCollection.setDiscarded(oldDrug.getDiscarded());
			if (drugCollection.getDrugType() != null) {
				if (drugCollection.getDrugType().getId() == null)
					drugCollection.setDrugType(null);
				else {
					DrugTypeCollection drugTypeCollection = drugTypeRepository
							.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
					if (drugTypeCollection != null) {
						DrugType drugType = new DrugType();
						BeanUtil.map(drugTypeCollection, drugType);
						drugCollection.setDrugType(drugType);
					}
				}
			}
			if (drugCollection.getStrength() != null && drugCollection.getStrength().getStrengthUnit() != null) {
				if (drugCollection.getStrength().getStrengthUnit().getId() == null)
					drugCollection.getStrength().setStrengthUnit(null);
			}
			drugCollection = drugRepository.save(drugCollection);
			response = new DrugAddEditResponse();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public Drug deleteDrug(String drugId, String doctorId, String hospitalId, String locationId, Boolean discarded) {
		Drug response = null;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null
						&& drugCollection.getLocationId() != null) {
					if (drugCollection.getDoctorId().equals(doctorId)
							&& drugCollection.getHospitalId().equals(hospitalId)
							&& drugCollection.getLocationId().equals(locationId)) {
						drugCollection.setDiscarded(discarded);
						drugCollection.setUpdatedTime(new Date());
						drugCollection = drugRepository.save(drugCollection);
						response = new Drug();
						BeanUtil.map(drugCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Drug");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Drug");
				}
			} else {
				logger.warn("Drug Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public Drug deleteDrug(String drugId, Boolean discarded) {
		Drug response = null;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				drugCollection.setUpdatedTime(new Date());
				drugCollection.setDiscarded(discarded);
				drugCollection = drugRepository.save(drugCollection);
				response = new Drug();
				BeanUtil.map(drugCollection, response);
			} else {
				logger.warn("Drug Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugAddEditResponse getDrugById(String drugId) {
		DrugAddEditResponse drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				drugAddEditResponse = new DrugAddEditResponse();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				logger.warn("Drug not found. Please check Drug Id");
				throw new BusinessException(ServiceError.NoRecord, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}

	@Override
	@Transactional
	public List<Drug> getDrugs(List<ObjectId> drugIds) {
		List<Drug> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(drugIds).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<Drug> results = mongoTemplate.aggregate(aggregation, DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return response;
	}

	@Override
	public Advice deleteAdvice(String adviceId, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Advice response = new Advice();
		AdviceCollection adviceCollection = new AdviceCollection();
		try {
			adviceCollection = adviceRepository.findById(new ObjectId(adviceId)).orElse(null);
			if (adviceCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(adviceCollection.getDoctorId(), adviceCollection.getHospitalId(),
						adviceCollection.getLocationId())) {
					if (adviceCollection.getDoctorId().toString().equals(doctorId)
							&& adviceCollection.getHospitalId().toString().equals(hospitalId)
							&& adviceCollection.getLocationId().toString().equals(locationId)) {
						adviceCollection.setDiscarded(discarded);
						adviceCollection.setUpdatedTime(new Date());
						adviceCollection = adviceRepository.save(adviceCollection);

						BeanUtil.map(adviceCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					adviceCollection.setDiscarded(discarded);
					adviceCollection.setUpdatedTime(new Date());
					adviceRepository.save(adviceCollection);
					response = new Advice();
					BeanUtil.map(adviceCollection, response);
				}
			} else {
				logger.warn("Advice not found!");
				throw new BusinessException(ServiceError.NoRecord, "Advice not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Advice");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Advice");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request) {
		DrugTypeAddEditResponse response = null;

		DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
		BeanUtil.map(request, drugTypeCollection);
		try {
			drugTypeCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugTypeCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugTypeCollection.getDoctorId()).orElse(null);
				if (userCollection != null)
					drugTypeCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugTypeCollection.setCreatedBy("ADMIN");
			}
			drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
			response = new DrugTypeAddEditResponse();
			BeanUtil.map(drugTypeCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Type");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request) {

		DrugTypeAddEditResponse response = null;

		DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
		BeanUtil.map(request, drugTypeCollection);
		try {
			DrugTypeCollection oldDrug = drugTypeRepository.findById(new ObjectId(request.getId())).orElse(null);
			drugTypeCollection.setCreatedBy(oldDrug.getCreatedBy());
			drugTypeCollection.setCreatedTime(oldDrug.getCreatedTime());
			drugTypeCollection.setDiscarded(oldDrug.getDiscarded());
			drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
			response = new DrugTypeAddEditResponse();
			BeanUtil.map(drugTypeCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Type");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse deleteDrugType(String drugTypeId, Boolean discarded) {

		DrugTypeAddEditResponse response = null;
		DrugTypeCollection drugTypeCollection = null;
		try {
			drugTypeCollection = drugTypeRepository.findById(new ObjectId(drugTypeId)).orElse(null);
			if (drugTypeCollection != null) {
				drugTypeCollection.setDiscarded(discarded);
				drugTypeCollection.setUpdatedTime(new Date());
				drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
				response = new DrugTypeAddEditResponse();
				BeanUtil.map(drugTypeCollection, response);
			} else {
				logger.warn("Drug Type Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Type Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Type");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request) {

		DrugDosageAddEditResponse response = null;

		DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
		BeanUtil.map(request, drugDosageCollection);
		try {
			drugDosageCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDosageCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDosageCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDosageCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDosageCollection.setCreatedBy("ADMIN");
			}
			drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
			response = new DrugDosageAddEditResponse();
			BeanUtil.map(drugDosageCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request) {

		DrugDosageAddEditResponse response = null;

		DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
		BeanUtil.map(request, drugDosageCollection);
		try {
			DrugDosageCollection oldDrugDosage = drugDosageRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			drugDosageCollection.setCreatedBy(oldDrugDosage.getCreatedBy());
			drugDosageCollection.setCreatedTime(oldDrugDosage.getCreatedTime());
			drugDosageCollection.setDiscarded(oldDrugDosage.getDiscarded());
			drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
			response = new DrugDosageAddEditResponse();
			BeanUtil.map(drugDosageCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editin Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse deleteDrugDosage(String drugDosageId, Boolean discarded) {
		DrugDosageAddEditResponse response = null;
		DrugDosageCollection drugDosageCollection = null;
		try {
			drugDosageCollection = drugDosageRepository.findById(new ObjectId(drugDosageId)).orElse(null);
			if (drugDosageCollection != null) {
				drugDosageCollection.setDiscarded(discarded);
				drugDosageCollection.setUpdatedTime(new Date());
				drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
				response = new DrugDosageAddEditResponse();
				BeanUtil.map(drugDosageCollection, response);
			} else {
				logger.warn("Drug Dosage Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request) {

		DrugDirectionAddEditResponse response = null;

		DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
		BeanUtil.map(request, drugDirectionCollection);
		try {
			drugDirectionCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDirectionCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDirectionCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDirectionCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDirectionCollection.setCreatedBy("ADMIN");
			}
			drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
			response = new DrugDirectionAddEditResponse();
			BeanUtil.map(drugDirectionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Direction");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request) {

		DrugDirectionAddEditResponse response = null;

		DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
		BeanUtil.map(request, drugDirectionCollection);
		try {
			DrugDirectionCollection oldDrugDirection = drugDirectionRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			drugDirectionCollection.setCreatedBy(oldDrugDirection.getCreatedBy());
			drugDirectionCollection.setCreatedTime(oldDrugDirection.getCreatedTime());
			drugDirectionCollection.setDiscarded(oldDrugDirection.getDiscarded());
			drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
			response = new DrugDirectionAddEditResponse();
			BeanUtil.map(drugDirectionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Direction");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse deleteDrugDirection(String drugDirectionId, Boolean discarded) {
		DrugDirectionAddEditResponse response = null;
		DrugDirectionCollection drugDirectionCollection = null;
		try {
			drugDirectionCollection = drugDirectionRepository.findById(new ObjectId(drugDirectionId)).orElse(null);
			if (drugDirectionCollection != null) {
				drugDirectionCollection.setDiscarded(discarded);
				drugDirectionCollection.setUpdatedTime(new Date());
				drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
				response = new DrugDirectionAddEditResponse();
				BeanUtil.map(drugDirectionCollection, response);
			} else {
				logger.warn("Drug Dosage Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Direction");
		}
		return response;
	}

	@Override
	public Advice addAdvice(Advice request) {
		Advice response = null;
		try {
			response = new Advice();
			AdviceCollection adviceCollection = new AdviceCollection();
			BeanUtil.map(request, adviceCollection);

			if (DPDoctorUtils.anyStringEmpty(adviceCollection.getId())) {
				adviceCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(adviceCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(adviceCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						adviceCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					adviceCollection.setCreatedBy("ADMIN");
				}
			} else {
				AdviceCollection oldAdviceCollection = adviceRepository.findById(adviceCollection.getId()).orElse(null);
				adviceCollection.setCreatedBy(oldAdviceCollection.getCreatedBy());
				adviceCollection.setCreatedTime(oldAdviceCollection.getCreatedTime());
				adviceCollection.setDiscarded(oldAdviceCollection.getDiscarded());
			}
			adviceCollection.setDiscarded(false);
			adviceCollection = adviceRepository.save(adviceCollection);

			BeanUtil.map(adviceCollection, response);
		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Advice");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

		DrugDurationUnitAddEditResponse response = null;

		DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
		BeanUtil.map(request, drugDurationUnitCollection);
		try {
			drugDurationUnitCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDurationUnitCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDurationUnitCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDurationUnitCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDurationUnitCollection.setCreatedBy("ADMIN");
			}
			drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
			response = new DrugDurationUnitAddEditResponse();
			BeanUtil.map(drugDurationUnitCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Duration Unit");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

		DrugDurationUnitAddEditResponse response = null;

		DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
		BeanUtil.map(request, drugDurationUnitCollection);
		try {
			DrugDurationUnitCollection oldDrugDuration = drugDurationUnitRepository
					.findById(new ObjectId(request.getId())).orElse(null);
			drugDurationUnitCollection.setCreatedBy(oldDrugDuration.getCreatedBy());
			drugDurationUnitCollection.setCreatedTime(oldDrugDuration.getCreatedTime());
			drugDurationUnitCollection.setDiscarded(oldDrugDuration.getDiscarded());
			drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
			response = new DrugDurationUnitAddEditResponse();
			BeanUtil.map(drugDurationUnitCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Duration Unit");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded) {
		DrugDurationUnitAddEditResponse response = null;
		DrugDurationUnitCollection drugDurationUnitCollection = null;
		try {
			drugDurationUnitCollection = drugDurationUnitRepository.findById(new ObjectId(drugDurationUnitId))
					.orElse(null);
			if (drugDurationUnitCollection != null) {
				drugDurationUnitCollection.setDiscarded(discarded);
				drugDurationUnitCollection.setUpdatedTime(new Date());
				drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
				response = new DrugDurationUnitAddEditResponse();
				BeanUtil.map(drugDurationUnitCollection, response);
			} else {
				logger.warn("Drug Duration Unit Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Duration Unit Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Duration Unit");
		}
		return response;
	}

	@Override
	@Transactional
	public List<?> getPrescriptionItems(String type, String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin,
			String searchTerm, String category, String disease, String speciality) {

		List<?> response = new ArrayList<Object>();

		switch (PrescriptionItems.valueOf(type.toUpperCase())) {

		case DRUGS: {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchTerm = searchTerm.toUpperCase();
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugsForAdmin(page, size, updatedTime, discarded, searchTerm, category);
				else
					response = getGlobalDrugs(page, size, updatedTime, discarded);
				break;

			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugsForAdmin(page, size, updatedTime, discarded, searchTerm, category);
				else
					response = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugsForAdmin(page, size, updatedTime, discarded, searchTerm, category);
				else
					response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DRUGTYPE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugTypeForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getGlobalDrugType(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugTypeForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugTypeForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DRUGDIRECTION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDirectionForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getGlobalDrugDirection(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDirectionForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDirectionForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomGlobalDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DRUGDOSAGE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDosageForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getGlobalDrugDosage(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDosageForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDosageForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomGlobalDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DRUGDURATIONUNIT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDurationUnitForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getGlobalDrugDurationUnit(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDurationUnitForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDurationUnitForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			}
			break;
		}
		// case DRUGSTRENGTHUNIT: {
		// switch (Range.valueOf(range.toUpperCase())) {
		//
		// case GLOBAL:
		// if(isAdmin)response = getGlobalDrugStrengthUnit(page, size,
		// updatedTime, discarded);
		// else response = getGlobalDrugStrengthUnit(page, size,
		// updatedTime, discarded);
		// break;
		// case CUSTOM:
		// if(isAdmin)response = getCustomDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// else response = getCustomDrugStrengthUnit(page, size, doctorId,
		// locationId, hospitalId, updatedTime, discarded);
		// break;
		// case BOTH:
		// if(isAdmin)response = getCustomGlobalDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// else response = getCustomGlobalDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// break;
		// }
		// break;
		// }
		case LABTEST: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomLabTestsForAdmin(page, size, locationId, hospitalId, updatedTime, discarded,
							searchTerm);
				else
					response = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				break;
			}
			break;
		}

		case DIAGNOSTICTEST: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDiagnosticTestsForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getGlobalDiagnosticTests(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDiagnosticTestsForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDiagnosticTestsForAdmin(page, size, updatedTime, discarded, searchTerm);
				else
					response = getCustomGlobalDiagnosticTests(page, size, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}

		case DRUGBYGCODE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugCodeForAdmin(page, size, updatedTime, discarded, searchTerm, category);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugCodeForAdmin(page, size, updatedTime, discarded, searchTerm, category);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugCodeForAdmin(page, size, updatedTime, discarded, searchTerm,
							category);
				break;
			}
			break;
		}

		case DRUGBYICODE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugGenericCodeForAdmin(page, size, updatedTime, discarded, searchTerm,
							category);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugGenericCodeForAdmin(page, size, updatedTime, discarded, searchTerm,
							category);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugGenericCodeForAdmin(page, size, updatedTime, discarded, searchTerm,
							category);
				break;
			}
			break;
		}

		case GCODE: {
			if (isAdmin)
				response = getGenericCodeForAdmin(page, size, updatedTime, searchTerm);
			break;
		}
		case GCODEWITHREACTION: {
			if (isAdmin)
				response = getGenericCodeWithReactionForAdmin(page, size, updatedTime, discarded, searchTerm);
			break;
		}
		case ADVICE: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:

				response = getGlobalAdvicesForAdmin(page, size, updatedTime, searchTerm, discarded, disease,
						speciality);
				break;

			case CUSTOM:
				response = getCustomAdvicesForAdmin(page, size, updatedTime, searchTerm, discarded, disease,
						speciality);
				break;

			case BOTH:
				response = getCustomGlobalAdvicesForAdmin(page, size, updatedTime, searchTerm, discarded, disease,
						speciality);
				break;
			default:
				break;

			}
			break;
		}
		default:
			break;
		}
		return response;

	}

	private List<Object> getCustomLabTests(int page, int size, String locationId, String hospitalId, String updatedTime,
			boolean discarded) {
		List<Object> response = null;
		List<LabTestCollection> labTestCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			if (locationId == null && hospitalId == null) {
				labTestCollections = new ArrayList<LabTestCollection>();
			} else {
				if (size > 0)
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(
									new ObjectId(hospitalId), new ObjectId(locationId), new Date(createdTimeStamp),
									discards, PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(
									new ObjectId(hospitalId), new ObjectId(locationId), new Date(createdTimeStamp),
									discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (!labTestCollections.isEmpty()) {
				response = new ArrayList<Object>();
				for (LabTestCollection labTestCollection : labTestCollections) {
					LabTest labTest = new LabTest();
					BeanUtil.map(labTestCollection, labTest);
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(labTestCollection.getTestId()).orElse(null);
					DiagnosticTest diagnosticTest = new DiagnosticTest();
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					labTest.setTest(diagnosticTest);
					response.add(labTest);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	private List<Drug> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<Drug> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null), DrugCollection.class,
					Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<Drug> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugType> getGlobalDrugType(int page, int size, String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomDrugType(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null),
					DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomGlobalDrugType(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugDirection> getGlobalDrugDirection(int page, int size, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null),
							DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomGlobalDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null),
					DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDosage> getGlobalDrugDosage(int page, int size, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null),
					DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomGlobalDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDurationUnit> getGlobalDrugDurationUnit(int page, int size, String updatedTime,
			boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomDrugDurationUnit(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null),
							DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Unit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomGlobalDrugDurationUnit(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null),
					DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	// private List<Object> getGlobalDrugStrengthUnit(int page, int size, String
	// updatedTime, boolean discarded) {
	// List<Object> response = null;
	// List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	// try {
	// if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(PageRequest.of(page,
	// size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(new
	// Sort(Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(discarded, new
	// PageRequest(page, size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(discarded, new
	// Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// } else {
	// long createdTimeStamp = Long.parseLong(updatedTime);
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), PageRequest.of(page, size,
	// Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), discarded, PageRequest.of(
	// page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), discarded, new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// }
	// }
	// if (!drugStrengthUnitCollections.isEmpty()) {
	// response = new ArrayList<Object>();
	// BeanUtil.map(drugStrengthUnitCollections, response);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Getting Drug StrengthUnit");
	// }
	// return response;
	// }
	//
	// private List<Object> getCustomDrugStrengthUnit(int page, int size, String
	// doctorId, String locationId, String hospitalId, String updatedTime,
	// boolean discarded) {
	// List<Object> response = null;
	// List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	// try {
	// if (doctorId == null)
	// drugStrengthUnitCollections = new
	// ArrayList<DrugStrengthUnitCollection>();
	// else if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
	// if (locationId == null && hospitalId == null) {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// PageRequest(page, size,
	// Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// Sort(Sort.Direction.DESC,
	// "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, discarded, new
	// PageRequest(page, size,
	// Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, discarded, new
	// Sort(Sort.Direction.DESC,
	// "createdTime"));
	// }
	// } else {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, PageRequest.of(
	// page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, discarded,
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, discarded,
	// new Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// }
	// } else {
	// long createdTimeStamp = Long.parseLong(updatedTime);
	// if (locationId == null && hospitalId == null) {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp),
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp), new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp), discarded,
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp), discarded,
	// new Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// } else {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, new Date(
	// createdTimeStamp), PageRequest.of(page, size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, new Date(
	// createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, new Date(
	// createdTimeStamp), discarded, PageRequest.of(page, size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomDrugStrengthUnit(doctorId, hospitalId,
	// locationId, new Date(
	// createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
	// "createdTime"));
	// }
	// }
	// }
	// if (!drugStrengthUnitCollections.isEmpty()) {
	// response = new ArrayList<Object>();
	// BeanUtil.map(drugStrengthUnitCollections, response);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Getting Drug StrengthUnit");
	// }
	// return response;
	// }
	//
	// private List<Object> getCustomGlobalDrugStrengthUnit(int page, int size,
	// String doctorId, String locationId, String hospitalId, String
	// updatedTime,
	// boolean discarded) {
	// List<Object> response = null;
	// List<DrugStrengthUnitCollection> drugStrengthUnitCollections = null;
	// try {
	// if (doctorId == null) {
	// if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
	// long createdTimeStamp = Long.parseLong(updatedTime);
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), PageRequest.of(
	// page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), discarded,
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(new
	// Date(createdTimeStamp), discarded,
	// new Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// } else {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections = drugStrengthRepository.findAll(new
	// PageRequest(page, size, Direction.DESC, "createdTime"))
	// .getContent();
	// else
	// drugStrengthUnitCollections = drugStrengthRepository.findAll(new
	// Sort(Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(discarded, new
	// PageRequest(page, size,
	// Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(discarded, new
	// Sort(Sort.Direction.DESC,
	// "createdTime"));
	// }
	// }
	// } else if (DPDoctorUtils.anyStringEmpty(updatedTime)) {
	// if (locationId == null && hospitalId == null) {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// PageRequest(page, size,
	// Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// Sort(Sort.Direction.DESC,
	// "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// discarded, PageRequest.of(page,
	// size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// discarded, new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// }
	// } else {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId,
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, new Sort(
	// Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, discarded,
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, discarded,
	// new Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// }
	// } else {
	// long createdTimeStamp = Long.parseLong(updatedTime);
	// if (locationId == null && hospitalId == null) {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp),
	// PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp),
	// new Sort(Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp),
	// discarded, PageRequest.of(page, size, Direction.DESC, "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId, new
	// Date(createdTimeStamp),
	// discarded, new Sort(Sort.Direction.DESC, "createdTime"));
	// }
	// } else {
	// if (discarded) {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, new Date(
	// createdTimeStamp), PageRequest.of(page, size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, new Date(
	// createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	// } else {
	// if (size > 0)
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, new Date(
	// createdTimeStamp), discarded, PageRequest.of(page, size, Direction.DESC,
	// "createdTime"));
	// else
	// drugStrengthUnitCollections =
	// drugStrengthRepository.getCustomGlobalDrugStrengthUnit(doctorId,
	// hospitalId, locationId, new Date(
	// createdTimeStamp), discarded, new Sort(Sort.Direction.DESC,
	// "createdTime"));
	// }
	// }
	// }
	// if (!drugStrengthUnitCollections.isEmpty()) {
	// response = new ArrayList<Object>();
	// BeanUtil.map(drugStrengthUnitCollections, response);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Getting Drug Strength Unit");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Getting Drug StrengthUnit");
	// }
	// return response;
	// }

	@Override
	@Transactional
	public void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = createMailData(prescriptionId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Prescription", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you a Prescription", body,
					mailResponse.getMailAttachment());
			if (mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (MessagingException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public MailResponse getPrescriptionMailData(String prescriptionId, String doctorId, String locationId,
			String hospitalId) {
		return createMailData(prescriptionId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String prescriptionId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		PrescriptionCollection prescriptionCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId)
							&& prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId)) {

						user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
								prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
								prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId());
						emailTrackCollection.setDoctorId(prescriptionCollection.getDoctorId());
						emailTrackCollection.setHospitalId(prescriptionCollection.getHospitalId());
						emailTrackCollection.setLocationId(prescriptionCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
						emailTrackCollection.setSubject("Prescription");
						if (user != null) {
							emailTrackCollection.setPatientName(user.getFirstName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(prescriptionCollection, patient, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId))
								.orElse(null);

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						response.setClinicAddress(address);
						response.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						response.setMailRecordCreatedDate(sdf.format(prescriptionCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Prescription Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Prescription Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String mobileNumber, String type) {
		Boolean response = false;
		PrescriptionCollection prescriptionCollection = null;
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId)
							&& prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId)) {

						UserCollection userCollection = userRepository.findById(prescriptionCollection.getPatientId())
								.orElse(null);
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
										prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
										prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId());
						if (patientCollection != null) {
							String prescriptionDetails = "";
							int i = 0;
							for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
								if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
									DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId())
											.orElse(null);
									if (drug != null) {
										i++;

										String drugType = drug.getDrugType() != null
												? (drug.getDrugType().getType() != null ? drug.getDrugType().getType()
														: "")
												: "";
										String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";

										String durationValue = prescriptionItem.getDuration() != null
												? (prescriptionItem.getDuration().getValue() != null
														? prescriptionItem.getDuration().getValue()
														: "")
												: "";
										String durationUnit = prescriptionItem.getDuration() != null
												? (prescriptionItem.getDuration().getDurationUnit() != null
														? prescriptionItem.getDuration().getDurationUnit().getUnit()
														: "")
												: "";

										if (durationValue != "")
											durationValue = "," + durationValue + durationUnit;
										String dosage = prescriptionItem.getDosage() != null
												? "," + prescriptionItem.getDosage()
												: "";

										String directions = "";
										if (prescriptionItem.getDirection() != null
												&& !prescriptionItem.getDirection().isEmpty()) {
											for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
												if (drugDirection.getDirection() != null)
													if (directions != "")
														directions = "," + drugDirection.getDirection();
													else
														directions = drugDirection.getDirection();
											}
											if (directions != "")
												directions = "," + directions;
										}
										prescriptionDetails = prescriptionDetails + " " + i + ")" + drugType + " "
												+ drugName + dosage + durationValue + directions;
									}
								}
							}
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

							String patientName = patientCollection.getFirstName() != null
									? patientCollection.getFirstName().split(" ")[0]
									: "", doctorName = "", clinicContactNum = "";

							UserCollection doctor = userRepository.findById(new ObjectId(doctorId)).orElse(null);
							if (doctor != null)
								doctorName = doctor.getTitle() + " " + doctor.getFirstName();

							LocationCollection locationCollection = locationRepository
									.findById(new ObjectId(locationId)).orElse(null);
							if (locationCollection != null && locationCollection.getClinicNumber() != null)
								clinicContactNum = " " + locationCollection.getClinicNumber();

							smsTrackDetail.setDoctorId(new ObjectId(doctorId));
							smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
							smsTrackDetail.setLocationId(new ObjectId(locationId));
							smsTrackDetail.setType(type);
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(prescriptionCollection.getPatientId());
							if (userCollection != null)
								smsDetail.setUserName(userCollection.getFirstName());
							SMS sms = new SMS();
							sms.setSmsText("Hi " + patientName + ", your prescription "
									+ prescriptionCollection.getUniqueEmrId() + " by " + doctorName + ". "
									+ prescriptionDetails + ". For queries,contact Doctor" + clinicContactNum + ".");

							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(mobileNumber);
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							sMSServices.sendSMS(smsTrackDetail, true);
							response = false;
						}
					} else {
						logger.warn("Prescription not found.Please check prescriptionId.");
						throw new BusinessException(ServiceError.NoRecord,
								"Prescription not found.Please check prescriptionId.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LabTest addLabTest(LabTest request) {
		LabTest response = null;
		LabTestCollection labTestCollection = new LabTestCollection();
		BeanUtil.map(request, labTestCollection);
		try {
			if (request.getTest() != null) {
				Date createdTime = new Date();
				labTestCollection.setCreatedTime(createdTime);
				LocationCollection locationCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(labTestCollection.getLocationId())) {
					locationCollection = locationRepository.findById(labTestCollection.getLocationId()).orElse(null);
					if (locationCollection != null)
						labTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					labTestCollection.setCreatedBy("ADMIN");
				}
				DiagnosticTestCollection diagnosticTestCollection = null;
				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findById(new ObjectId(request.getTest().getId())).orElse(null);
				if (diagnosticTestCollection == null) {

					if (request.getTest().getTestName() == null) {
						logger.error("Cannot create lab test without diagnostic test");
						throw new BusinessException(ServiceError.Unknown,
								"Cannot create lab test without diagnostic test");
					}
					diagnosticTestCollection = new DiagnosticTestCollection();
					diagnosticTestCollection.setLocationId(new ObjectId(request.getLocationId()));
					diagnosticTestCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					diagnosticTestCollection.setTestName(request.getTest().getTestName());
					diagnosticTestCollection.setCreatedTime(createdTime);
					if (locationCollection != null)
						diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
					diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				}
				transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST, false);
				ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
				esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);

				labTestCollection.setTestId(diagnosticTestCollection.getId());
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
				DiagnosticTest diagnosticTest = new DiagnosticTest();
				if (diagnosticTestCollection != null) {
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					response.setTest(diagnosticTest);
				}
			} else {
				logger.error("Cannot create lab test without diagnostic test");
				throw new BusinessException(ServiceError.Unknown, "Cannot create lab test without diagnostic test");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public LabTest editLabTest(LabTest request) {
		LabTest response = null;
		LabTestCollection labTestCollection = new LabTestCollection();
		BeanUtil.map(request, labTestCollection);
		try {
			if (request.getTest() != null) {
				LabTestCollection oldLabTest = labTestRepository.findById(new ObjectId(request.getId())).orElse(null);
				labTestCollection.setCreatedBy(oldLabTest.getCreatedBy());
				labTestCollection.setCreatedTime(oldLabTest.getCreatedTime());
				labTestCollection.setDiscarded(oldLabTest.getDiscarded());
				DiagnosticTestCollection diagnosticTestCollection = null;

				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findById(new ObjectId(request.getTest().getId())).orElse(null);
				if (diagnosticTestCollection == null) {
					if (request.getTest().getTestName() == null) {
						logger.error("Cannot create lab test without diagnostic test");
						throw new BusinessException(ServiceError.Unknown,
								"Cannot create lab test without diagnostic test");
					}
					diagnosticTestCollection = new DiagnosticTestCollection();
					diagnosticTestCollection.setLocationId(new ObjectId(request.getLocationId()));
					diagnosticTestCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					diagnosticTestCollection.setTestName(request.getTest().getTestName());
					diagnosticTestCollection.setCreatedTime(new Date());
					diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				}
				transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST, false);
				ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
				esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);

				labTestCollection.setTestId(diagnosticTestCollection.getId());
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
				DiagnosticTest diagnosticTest = new DiagnosticTest();
				if (diagnosticTestCollection != null) {
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					response.setTest(diagnosticTest);
				}
			} else {
				logger.error("Cannot create lab test without diagnostic test");
				throw new BusinessException(ServiceError.Unknown, "Cannot create lab test without diagnostic test");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Lab Test");
		}
		return response;

	}

	@Override
	@Transactional
	public LabTest deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded) {
		LabTest response = null;
		LabTestCollection labTestCollection = null;
		try {
			labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				if (labTestCollection.getHospitalId() != null && labTestCollection.getLocationId() != null) {
					if (labTestCollection.getHospitalId().equals(hospitalId)
							&& labTestCollection.getLocationId().equals(locationId)) {
						labTestCollection.setDiscarded(discarded);
						labTestCollection.setUpdatedTime(new Date());
						labTestCollection = labTestRepository.save(labTestCollection);
						response = new LabTest();
						BeanUtil.map(labTestCollection, response);
					} else {
						logger.warn("Invalid Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Lab Test");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Lab Test");
				}
			} else {
				logger.warn("Lab Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
		}
		return response;

	}

	@Override
	@Transactional
	public LabTest deleteLabTest(String labTestId, Boolean discarded) {
		LabTest response = null;
		LabTestCollection labTestCollection = null;
		try {
			labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				labTestCollection.setUpdatedTime(new Date());
				labTestCollection.setDiscarded(discarded);
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
			} else {
				logger.warn("Lab Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public LabTest getLabTestById(String labTestId) {
		LabTest response = null;
		try {
			LabTestCollection labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
			} else {
				logger.warn("Lab Test not found. Please check Lab Test Id");
				throw new BusinessException(ServiceError.NoRecord, "Lab Test not found. Please check Lab Test Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Lab Test");
		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	@Override
	@Transactional
	public List<DiagnosticTestCollection> getDiagnosticTest() {
		List<DiagnosticTestCollection> response = null;
		try {
			response = diagnosticTestRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Prescription> getPrescriptions(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		List<PrescriptionCollection> prescriptionCollections = null;
		List<Prescription> prescriptions = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);

			long createdTimestamp = Long.parseLong(updatedTime);
			if (size > 0)
				prescriptionCollections = prescriptionRepository.findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(
						new ObjectId(patientId), new Date(createdTimestamp), discards,
						PageRequest.of(page, size, Direction.DESC, "createdTime"));
			else
				prescriptionCollections = prescriptionRepository.findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(
						new ObjectId(patientId), new Date(createdTimestamp), discards,
						new Sort(Sort.Direction.DESC, "createdTime"));

			if (prescriptionCollections != null) {
				prescriptions = new ArrayList<Prescription>();
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {

					Prescription prescription = new Prescription();
					List<TestAndRecordData> tests = prescriptionCollection.getDiagnosticTests();
					prescriptionCollection.setDiagnosticTests(null);
					BeanUtil.map(prescriptionCollection, prescription);
					if (prescriptionCollection.getItems() != null) {
						List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
							BeanUtil.map(prescriptionItem, prescriptionItemDetails);
							if (prescriptionItem.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findById(prescriptionItem.getDrugId())
										.orElse(null);
								Drug drug = new Drug();
								if (drugCollection != null)
									BeanUtil.map(drugCollection, drug);
								prescriptionItemDetails.setDrug(drug);
							}
							prescriptionItemDetailsList.add(prescriptionItemDetails);
						}
						prescription.setItems(prescriptionItemDetailsList);
					}
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByPrescriptionId(prescriptionCollection.getId());
					if (patientVisitCollection != null)
						prescription.setVisitId(patientVisitCollection.getId().toString());
					if (tests != null && !tests.isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : tests) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(
										new TestAndRecordDataResponse(diagnosticTest, data.getRecordId().toString()));
							}
						}
						prescription.setDiagnosticTests(diagnosticTests);
					}
					prescriptions.add(prescription);
				}
			} else {
				logger.warn("Prescription Not Found");
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public DiagnosticTest addEditDiagnosticTest(DiagnosticTest request) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = new DiagnosticTestCollection();
		BeanUtil.map(request, diagnosticTestCollection);
		try {
			if (request.getId() == null) {
				diagnosticTestCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagnosticTestCollection.getLocationId())) {
					LocationCollection locationCollection = locationRepository
							.findById(diagnosticTestCollection.getLocationId()).orElse(null);
					if (locationCollection != null)
						diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					diagnosticTestCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosticTestCollection oldDiagnosticTestCollection = diagnosticTestRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				oldDiagnosticTestCollection.setCreatedBy(oldDiagnosticTestCollection.getCreatedBy());
				oldDiagnosticTestCollection.setCreatedTime(oldDiagnosticTestCollection.getCreatedTime());
				oldDiagnosticTestCollection.setDiscarded(oldDiagnosticTestCollection.getDiscarded());

				diagnosticTestCollection.setUpdatedTime(new Date());
			}
			diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
			response = new DiagnosticTest();
			BeanUtil.map(diagnosticTestCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public DiagnosticTest getDiagnosticTest(String diagnosticTestId) {
		DiagnosticTest response = null;
		try {
			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
					.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				response = new DiagnosticTest();
				BeanUtil.map(diagnosticTestCollection, response);
			} else {
				logger.warn("Diagnostic Test not found. Please check DiagnosticT Test Id");
				throw new BusinessException(ServiceError.NoRecord,
						"Diagnostic Test not found. Please check DiagnosticT Test Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Test");
		}
		return response;

	}

	@Override
	@Transactional
	public DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, String hospitalId, String locationId,
			Boolean discarded) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = null;
		try {
			diagnosticTestCollection = diagnosticTestRepository.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				if (diagnosticTestCollection.getHospitalId() != null
						&& diagnosticTestCollection.getLocationId() != null) {
					if (diagnosticTestCollection.getHospitalId().equals(hospitalId)
							&& diagnosticTestCollection.getLocationId().equals(locationId)) {
						diagnosticTestCollection.setDiscarded(discarded);
						diagnosticTestCollection.setUpdatedTime(new Date());
						diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
						response = new DiagnosticTest();
						BeanUtil.map(diagnosticTestCollection, response);
					} else {
						logger.warn("Invalid Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Lab Test");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Lab Test");
				}
			} else {
				logger.warn("Diagnostic Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Diagnostic Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagnostic Test");
		}
		return response;
	}

	@Override
	@Transactional
	public DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, Boolean discarded) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = null;
		try {
			diagnosticTestCollection = diagnosticTestRepository.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				diagnosticTestCollection.setUpdatedTime(new Date());
				diagnosticTestCollection.setDiscarded(discarded);
				diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				response = new DiagnosticTest();
				BeanUtil.map(diagnosticTestCollection, response);
			} else {
				logger.warn("Diagnostic Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Diagnostic Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagnostic Test");
		}
		return response;
	}

	private List<DiagnosticTest> getGlobalDiagnosticTests(int page, int size, String updatedTime, Boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null),
					DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomDiagnosticTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregation(page, size, null, locationId, hospitalId,
							updatedTime, discarded, null, null), DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomGlobalDiagnosticTests(int page, int size, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, null, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId) {
		PrescriptionTestAndRecord response = null;
		List<TestAndRecordDataResponse> tests = null;
		try {
			PrescriptionCollection prescriptionCollection = prescriptionRepository
					.findByUniqueEmrIdAndPatientId(uniqueEmrId, new ObjectId(patientId));
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDiagnosticTests() != null
						&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
					tests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData recordData : prescriptionCollection.getDiagnosticTests()) {
						DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
								.findById(recordData.getTestId()).orElse(null);
						if (diagnosticTestCollection != null) {
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							TestAndRecordDataResponse dataResponse = new TestAndRecordDataResponse(diagnosticTest,
									recordData.getRecordId().toString());
							tests.add(dataResponse);
						}
					}
					if (tests != null && !tests.isEmpty()) {
						response = new PrescriptionTestAndRecord();
						response.setUniqueEmrId(prescriptionCollection.getUniqueEmrId());
						response.setTests(tests);
					}
				} else {
					throw new BusinessException(ServiceError.NoRecord, "No test Exists for this prescription");
				}
			} else {
				throw new BusinessException(ServiceError.InvalidInput, checkPrescriptionExists);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Checking Prescription Exists");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Checking Prescription Exists");
		}
		return response;
	}

	@Override
	@Transactional
	public GenericCode addEditGenericCode(GenericCode request) {
		GenericCode response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getCode())) {
				GenericCodeCollection genericCodeCollection = genericCodeRepository.findByCode(request.getCode());
				if (genericCodeCollection == null) {
					genericCodeCollection = new GenericCodeCollection();
					BeanUtil.map(request, genericCodeCollection);
					genericCodeCollection.setUpdatedTime(new Date());
					genericCodeCollection.setCreatedBy("ADMIN");
					genericCodeCollection = genericCodeRepository.save(genericCodeCollection);
					response = new GenericCode();
					BeanUtil.map(genericCodeCollection, response);
				} else {
					genericCodeCollection.setName(request.getName());
					genericCodeCollection.setCreatedTime(new Date());
					genericCodeCollection = genericCodeRepository.save(genericCodeCollection);
					response = new GenericCode();
					BeanUtil.map(genericCodeCollection, response);
				}
			} else {
				logger.error("Generic code is empty");
				throw new BusinessException(ServiceError.Unknown, "Generic code is empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Adding/Editing generic code");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Adding/Editing generic code");
		}
		return response;
	}

	@Override
	public String getPrescriptionFile(String prescriptionId) {
		String response = null;
		try {
			PrescriptionCollection prescriptionCollection = prescriptionRepository
					.findById(new ObjectId(prescriptionId)).orElse(null);

			if (prescriptionCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
						prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId());
				UserCollection user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);

				JasperReportResponse jasperReportResponse = createJasper(prescriptionCollection, patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(PrescriptionCollection prescriptionCollection, PatientCollection patient,
			UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
		JasperReportResponse response = null;
		int no = 0;
		if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty())
			for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
				if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
					DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId()).orElse(null);
					if (drug != null) {
						String drugType = drug.getDrugType() != null
								? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "")
								: "";
						String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
						drugName = (drugType + drugName) == "" ? "----" : drugType + " " + drugName;
						String durationValue = prescriptionItem.getDuration() != null
								? (prescriptionItem.getDuration().getValue() != null
										? prescriptionItem.getDuration().getValue()
										: "")
								: "";
						String durationUnit = prescriptionItem.getDuration() != null
								? (prescriptionItem.getDuration().getDurationUnit() != null
										? prescriptionItem.getDuration().getDurationUnit().getUnit()
										: "")
								: "";

						String directions = "";
						if (prescriptionItem.getDirection() != null)
							for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
								if (drugDirection.getDirection() != null)
									if (directions == "")
										directions = directions + (drugDirection.getDirection());
									else
										directions = directions + "," + (drugDirection.getDirection());
							}
						String duration = "";
						if (durationValue == "" && durationValue == "")
							duration = "----";
						else
							duration = durationValue + " " + durationUnit;
						no = no + 1;
						PrescriptionJasperDetails prescriptionJasperDetails = new PrescriptionJasperDetails(no,
								drugName, prescriptionItem.getDosage() != null ? prescriptionItem.getDosage() : "----",
								duration, directions.isEmpty() ? "----" : directions,
								prescriptionItem.getInstructions() != null ? prescriptionItem.getInstructions()
										: "----");

						prescriptionItems.add(prescriptionJasperDetails);
					}
				}
				parameters.put("prescriptionItems", prescriptionItems);
			}

		parameters.put("prescriptionId", prescriptionCollection.getId().toString());
		parameters.put("advice",
				prescriptionCollection.getAdvice() != null ? prescriptionCollection.getAdvice() : "----");
		String labTest = "";
		if (prescriptionCollection.getDiagnosticTests() != null
				&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
			int i = 1;
			for (TestAndRecordData tests : prescriptionCollection.getDiagnosticTests()) {
				if (tests.getTestId() != null) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(tests.getTestId()).orElse(null);
					if (diagnosticTestCollection != null) {
						labTest = labTest + i + ") " + diagnosticTestCollection.getTestName() + "<br>";
						i++;
					}
				}
			}
		}
		if (labTest != null && !labTest.isEmpty())
			parameters.put("labTest", labTest);
		else
			parameters.put("labTest", null);

		String patientName = "", dob = "", bloodGroup = "", gender = "", mobileNumber = "", refferedBy = "", pid = "",
				date = "", resourceId = "", logoURL = "";
		if (patient != null && patient.getReferredBy() != null) {
			ReferencesCollection referencesCollection = referenceRepository.findById(patient.getReferredBy())
					.orElse(null);
			if (referencesCollection != null)
				refferedBy = referencesCollection.getReference();
		}
		patientName = "Patient Name: " + (user != null ? user.getFirstName() : "--") + "<br>";
		String age = "--";
		if (patient != null && patient.getDob() != null) {
			Age ageObj = patient.getDob().getAge();
			if (ageObj.getYears() > 14)
				age = ageObj.getYears() + " years";
			else {
				int months = 0, days = ageObj.getDays();
				if (ageObj.getMonths() > 0) {
					months = ageObj.getMonths();
					if (ageObj.getYears() > 0)
						months = months + 12 * ageObj.getYears();
				}
				if (months == 0)
					age = days + " days";
				else
					age = months + " months " + days + " days";
			}
		}
		dob = "Age: " + age + "<br>";
		gender = "Gender: " + (patient != null && patient.getGender() != null ? patient.getGender() : "--") + "<br>";
		bloodGroup = "Blood Group: "
				+ (patient != null && patient.getBloodGroup() != null ? patient.getBloodGroup() : "--") + "<br>";
		mobileNumber = "Mobile: " + (user != null && user.getMobileNumber() != null ? user.getMobileNumber() : "--")
				+ "<br>";
		pid = "Patient Id: " + (patient != null && patient.getPID() != null ? patient.getPID() : "--") + "<br>";
		refferedBy = "Referred By: " + (refferedBy != "" ? refferedBy : "--") + "<br>";
		date = "Date: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "<br>";
		resourceId = "PID: "
				+ (prescriptionCollection.getUniqueEmrId() != null ? prescriptionCollection.getUniqueEmrId() : "--")
				+ "<br>";
		PrintSettingsCollection printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(prescriptionCollection.getDoctorId(),
						prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId(),
						ComponentType.PRESCRIPTIONS.getType());

		if (printSettings == null) {
			printSettings = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(
					prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
					prescriptionCollection.getHospitalId(), ComponentType.ALL.getType());
		}

		parameters.put("printSettingsId", printSettings != null ? printSettings.getId().toString() : "");
		String headerLeftText = "", headerRightText = "", footerBottomText = "";
		int headerLeftTextLength = 0, headerRightTextLength = 0;
		if (printSettings != null) {
			if (printSettings.getHeaderSetup() != null) {
				if (printSettings.getHeaderSetup().getTopLeftText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {

						if ((str.getFontSize() != null) && !str.getFontSize().equalsIgnoreCase("10pt")
								&& !str.getFontSize().equalsIgnoreCase("11pt")
								&& !str.getFontSize().equalsIgnoreCase("12pt")
								&& !str.getFontSize().equalsIgnoreCase("13pt")
								&& !str.getFontSize().equalsIgnoreCase("14pt")
								&& !str.getFontSize().equalsIgnoreCase("15pt"))
							str.setFontSize("10pt");
						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerLeftTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerLeftText.isEmpty())
								headerLeftText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}
				if (printSettings.getHeaderSetup().getTopRightText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {
						if ((str.getFontSize() != null) && !str.getFontSize().equalsIgnoreCase("10pt")
								&& !str.getFontSize().equalsIgnoreCase("11pt")
								&& !str.getFontSize().equalsIgnoreCase("12pt")
								&& !str.getFontSize().equalsIgnoreCase("13pt")
								&& !str.getFontSize().equalsIgnoreCase("14pt")
								&& !str.getFontSize().equalsIgnoreCase("15pt"))
							str.setFontSize("10pt");
						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerRightTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerRightText.isEmpty())
								headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerRightText = headerRightText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}

			}
			if (printSettings.getFooterSetup() != null) {
				for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
					if ((str.getFontSize() != null) && !str.getFontSize().equalsIgnoreCase("10pt")
							&& !str.getFontSize().equalsIgnoreCase("11pt")
							&& !str.getFontSize().equalsIgnoreCase("12pt")
							&& !str.getFontSize().equalsIgnoreCase("13pt")
							&& !str.getFontSize().equalsIgnoreCase("14pt")
							&& !str.getFontSize().equalsIgnoreCase("15pt"))
						str.setFontSize("10pt");
					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					String text = str.getText();
					if (isItalic)
						text = "<i>" + text + "</i>";
					if (isBold)
						text = "<b>" + text + "</b>";

					if (footerBottomText.isEmpty())
						footerBottomText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
					else
						footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize() + "'>"
								+ text + "</span>";
				}
			}
			if (printSettings.getClinicLogoUrl() != null)
				logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());

			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getPatientDetails() != null
					&& printSettings.getHeaderSetup().getPatientDetails().getStyle() != null) {
				PatientDetails patientDetails = printSettings.getHeaderSetup().getPatientDetails();
				boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(),
						patientDetails.getStyle().getFontStyle());
				boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(),
						patientDetails.getStyle().getFontStyle());
				String fontSize = patientDetails.getStyle().getFontSize();
				if ((fontSize != null) && !fontSize.equalsIgnoreCase("10pt") && !fontSize.equalsIgnoreCase("11pt")
						&& !fontSize.equalsIgnoreCase("12pt") && !fontSize.equalsIgnoreCase("13pt")
						&& !fontSize.equalsIgnoreCase("14pt") && !fontSize.equalsIgnoreCase("15pt"))
					fontSize = "10pt";

				if (isItalic) {
					patientName = "<i>" + patientName + "</i>";
					pid = "<i>" + pid + "</i>";
					dob = "<i>" + dob + "</i>";
					bloodGroup = "<i>" + bloodGroup + "</i>";
					gender = "<i>" + gender + "</i>";
					mobileNumber = "<i>" + mobileNumber + "</i>";
					refferedBy = "<i>" + refferedBy + "</i>";
					date = "<i>" + date + "</i>";
					resourceId = "<i>" + resourceId + "</i>";
				}
				if (isBold) {
					patientName = "<b>" + patientName + "</b>";
					pid = "<b>" + pid + "</b>";
					dob = "<b>" + dob + "</b>";
					bloodGroup = "<b>" + bloodGroup + "</b>";
					gender = "<b>" + gender + "</b>";
					mobileNumber = "<b>" + mobileNumber + "</b>";
					refferedBy = "<b>" + refferedBy + "</b>";
					date = "<b>" + date + "</b>";
					resourceId = "<b>" + resourceId + "</b>";
				}
				patientName = "<span style='font-size:" + fontSize + "'>" + patientName + "</span>";
				pid = "<span style='font-size:" + fontSize + "'>" + pid + "</span>";
				dob = "<span style='font-size:" + fontSize + "'>" + dob + "</span>";
				bloodGroup = "<span style='font-size:" + fontSize + "'>" + bloodGroup + "</span>";
				gender = "<span style='font-size:" + fontSize + "'>" + gender + "</span>";
				mobileNumber = "<span style='font-size:" + fontSize + "'>" + mobileNumber + "</span>";
				refferedBy = "<span style='font-size:" + fontSize + "'>" + refferedBy + "</span>";
				date = "<span style='font-size:" + fontSize + "'>" + date + "</span>";
				resourceId = "<span style='font-size:" + fontSize + "'>" + resourceId + "</span>";
			}
			UserCollection doctorUser = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
			if (doctorUser != null)
				parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());
		}

		parameters.put("patientLeftText", patientName + pid + dob + gender + bloodGroup);
		parameters.put("patientRightText", mobileNumber + refferedBy + date + resourceId);
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);
		parameters.put("logoURL", logoURL);
		if (headerLeftTextLength > 2 || headerRightTextLength > 2) {
			parameters.put("showTableOne", true);
		} else {
			parameters.put("showTableOne", false);
		}
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		String margins = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getMargins() : null)
				: null;

		String pdfName = (user != null ? user.getFirstName() : "") + "PRESCRIPTION-"
				+ prescriptionCollection.getUniqueEmrId();
		response = jasperReportService.createPDF(parameters, "mongo-prescription", layout, pageSize, margins,
				pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getCustomLabTestsForAdmin(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String searchTerm) {
		List<Object> response = null;
		List<LabTestCollection> labTestCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);
			Collection<ObjectId> testIds = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				List<DiagnosticTestCollection> diagnosticTestCollections = null;
				diagnosticTestCollections = diagnosticTestRepository
						.findByUpdatedTimeGreaterThanAndDiscardedInAndTestNameRegex(new Date(createdTimeStamp),
								discards, searchTerm, new Sort(Sort.Direction.DESC, "updatedTime"));
				testIds = CollectionUtils.collect(diagnosticTestCollections, new BeanToPropertyValueTransformer("id"));
				if (testIds == null || testIds.isEmpty())
					return response;
			}

			ObjectId locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (testIds != null) {
				if (size > 0)
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedInAndTestIdIn(
									hospitalObjectId, locationObjectId, new Date(createdTimeStamp), discards, testIds,
									PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedInAndTestIdIn(
									hospitalObjectId, locationObjectId, new Date(createdTimeStamp), discards, testIds,
									new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
				if (size > 0)
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(hospitalObjectId,
									locationObjectId, new Date(createdTimeStamp), discards,
									PageRequest.of(page, size, Direction.DESC, "updatedTime"));
				else
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(hospitalObjectId,
									locationObjectId, new Date(createdTimeStamp), discards,
									new Sort(Sort.Direction.DESC, "updatedTime"));
			}

			if (!labTestCollections.isEmpty()) {
				response = new ArrayList<Object>();
				for (LabTestCollection labTestCollection : labTestCollections) {
					LabTest labTest = new LabTest();
					BeanUtil.map(labTestCollection, labTest);
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(labTestCollection.getTestId()).orElse(null);
					DiagnosticTest diagnosticTest = new DiagnosticTest();
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					labTest.setTest(diagnosticTest);
					response.add(labTest);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getGlobalDrugsForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "drugName", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomDrugsForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "drugName", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomGlobalDrugsForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "drugName", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getGlobalDrugCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "drugCode", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomDrugCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "drugCode", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomGlobalDrugCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "genericCodes", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getGlobalDrugGenericCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "genericCodes", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomDrugGenericCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "genericCodes", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugWithGenericCodes> getCustomGlobalDrugGenericCodeForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm, String category) {
		List<DrugWithGenericCodes> response = null;
		try {
			AggregationResults<DrugWithGenericCodes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "genericCodes", null, category, null),
							DrugCollection.class, DrugWithGenericCodes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugType> getGlobalDrugTypeForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "type", null, null, null), DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomDrugTypeForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "type", null, null, null), DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomGlobalDrugTypeForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "type", null, null, null), DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugDirection> getGlobalDrugDirectionForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "direction", null, null, null),
							DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomDrugDirectionForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "direction", null, null, null),
							DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomGlobalDrugDirectionForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "direction", null, null, null),
							DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDosage> getGlobalDrugDosageForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "dosage", null, null, null),
							DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomDrugDosageForAdmin(int page, int size, String updatedTime, boolean discarded,
			String searchTerm) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "dosage", null, null, null),
							DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomGlobalDrugDosageForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "dosage", null, null, null),
							DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDurationUnit> getGlobalDrugDurationUnitForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "unit", null, null, null),
							DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomDrugDurationUnitForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "unit", null, null, null),
							DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Unit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomGlobalDrugDurationUnitForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "unit", null, null, null),
							DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	private List<DiagnosticTest> getGlobalDiagnosticTestsForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "testName", null, null, null),
							DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomDiagnosticTestsForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "testName", null, null, null),
							DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomGlobalDiagnosticTestsForAdmin(int page, int size, String updatedTime,
			boolean discarded, String searchTerm) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "testName", null, null, null),
							DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<GenericCode> getGenericCodeForAdmin(int page, int size, String updatedTime, String searchTerm) {
		List<GenericCode> response = null;
		try {
			AggregationResults<GenericCode> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, true,
									searchTerm, "code", null, null, null),
							GenericCodeCollection.class, GenericCode.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<GenericCodesAndReaction> getGenericCodeWithReactionForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<GenericCodesAndReaction> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria.orOperator(new Criteria("genericCode").is(searchTerm),
						new Criteria("codes.genericCode").is(searchTerm));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("generic_code_cl", "genericCode", "code", "firstGenericCode"),
						Aggregation.unwind("firstGenericCode"), Aggregation.unwind("codes"),
						Aggregation.lookup("generic_code_cl", "codes.genericCode", "code", "secondGenericCode"),
						Aggregation.unwind("secondGenericCode"),
						new ProjectionOperation(Fields.from(Fields.field("reactionType", "$codes.reaction"),
								Fields.field("firstGenericCode", "$firstGenericCode"),
								Fields.field("secondGenericCode", "$secondGenericCode"),
								Fields.field("createdTime", "$createdTime"),
								Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy"))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("generic_code_cl", "genericCode", "code", "firstGenericCode"),
						Aggregation.unwind("firstGenericCode"), Aggregation.unwind("codes"),
						Aggregation.lookup("generic_code_cl", "codes.genericCode", "code", "secondGenericCode"),
						Aggregation.unwind("secondGenericCode"),
						new ProjectionOperation(Fields.from(Fields.field("reactionType", "$codes.reaction"),
								Fields.field("firstGenericCode", "$firstGenericCode"),
								Fields.field("secondGenericCode", "$secondGenericCode"),
								Fields.field("createdTime", "$createdTime"),
								Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy"))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, GenericCodesAndReactionsCollection.class, GenericCodesAndReaction.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Generic codes with reaction");
		}
		return response;
	}

	private List<Advice> getCustomAdvicesForAdmin(int page, int size, String updatedTime, String searchTerm,
			Boolean discarded, String disease, String speciality) {
		List<Advice> response = null;
		try {
			AggregationResults<Advice> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "advice", speciality, null, disease),
							AdviceCollection.class, Advice.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}

	private List<Advice> getGlobalAdvicesForAdmin(int page, int size, String updatedTime, String searchTerm,
			Boolean discarded, String disease, String speciality) {
		List<Advice> response = null;
		try {
			AggregationResults<Advice> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "advice", speciality, null, disease),
							AdviceCollection.class, Advice.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}

	private List<Advice> getCustomGlobalAdvicesForAdmin(int page, int size, String updatedTime, String searchTerm,
			Boolean discarded, String disease, String speciality) {
		List<Advice> response = new ArrayList<Advice>();
		try {

			AggregationResults<Advice> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "advice", speciality, null, disease),
							AdviceCollection.class, Advice.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean addGenericCodeWithReaction(GenericCodesAndReaction request) {
		Boolean response = false, isFirstUpdated = false, isSecondUpdated = false;
		try {
			if (request.getFirstGenericCode() != null && request.getSecondGenericCode() != null) {
				List<GenericCodesAndReactionsCollection> genericCodesAndReactionsCollections = genericCodesAndReactionsRepository
						.findByGenericCodeIn(Arrays.asList(request.getFirstGenericCode().getCode(),
								request.getSecondGenericCode().getCode()));
				if (genericCodesAndReactionsCollections != null) {
					for (GenericCodesAndReactionsCollection codesAndReactionsCollection : genericCodesAndReactionsCollections) {
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
							List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
							Collection<String> codes = CollectionUtils.collect(codesWithReaction,
									new BeanToPropertyValueTransformer("genericCode"));
							if (codes.contains(request.getSecondGenericCode().getCode())) {
								for (Code code : codesWithReaction) {
									if (code.getGenericCode()
											.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
										code.setReaction(request.getReactionType());
										code.setExplanation(request.getExplanation());
									}
								}
							} else {
								Code code = new Code(request.getSecondGenericCode().getCode(),
										request.getReactionType(), request.getExplanation());
								codesWithReaction.add(code);
							}
							codesAndReactionsCollection.setCodes(codesWithReaction);
							genericCodesAndReactionsRepository.save(codesAndReactionsCollection);

							ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
									.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
							if (esCodesAndReactions == null)
								esCodesAndReactions = new ESGenericCodesAndReactions();
							esCodesAndReactions.setCodes(null);
							BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
							esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
							esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
							isFirstUpdated = true;
						}
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
							List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
							Collection<String> codes = CollectionUtils.collect(codesWithReaction,
									new BeanToPropertyValueTransformer("genericCode"));
							if (codes.contains(request.getFirstGenericCode().getCode())) {
								for (Code code : codesWithReaction) {
									if (code.getGenericCode()
											.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
										code.setReaction(request.getReactionType());
										code.setExplanation(request.getExplanation());
									}
								}
							} else {
								Code code = new Code(request.getFirstGenericCode().getCode(), request.getReactionType(),
										request.getExplanation());
								codesWithReaction.add(code);
							}
							codesAndReactionsCollection.setCodes(codesWithReaction);
							genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
							ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
									.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
							if (esCodesAndReactions == null)
								esCodesAndReactions = new ESGenericCodesAndReactions();
							esCodesAndReactions.setCodes(null);
							BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
							esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
							esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
							isSecondUpdated = true;
						}
					}
				}
				if (!isFirstUpdated) {
					GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
					codesAndReactionsCollection.setGenericCode(request.getFirstGenericCode().getCode());
					codesAndReactionsCollection.setCreatedTime(new Date());
					codesAndReactionsCollection.setUpdatedTime(new Date());
					List<Code> codesWithReaction = new ArrayList<Code>();
					Code code = new Code(request.getSecondGenericCode().getCode(), request.getReactionType(),
							request.getExplanation());
					codesWithReaction.add(code);
					codesAndReactionsCollection.setCodes(codesWithReaction);
					genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
					ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
							.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
					if (esCodesAndReactions == null)
						esCodesAndReactions = new ESGenericCodesAndReactions();
					esCodesAndReactions.setCodes(null);
					BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
					esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
					esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
				}
				if (!isSecondUpdated) {
					GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
					codesAndReactionsCollection.setGenericCode(request.getSecondGenericCode().getCode());
					codesAndReactionsCollection.setCreatedTime(new Date());
					codesAndReactionsCollection.setUpdatedTime(new Date());
					List<Code> codesWithReaction = new ArrayList<Code>();
					Code code = new Code(request.getFirstGenericCode().getCode(), request.getReactionType(),
							request.getExplanation());
					codesWithReaction.add(code);
					codesAndReactionsCollection.setCodes(codesWithReaction);
					genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
					ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
							.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
					if (esCodesAndReactions == null)
						esCodesAndReactions = new ESGenericCodesAndReactions();
					esCodesAndReactions.setCodes(null);
					BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
					esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
					esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
				}
				response = true;
			} else {
				logger.error("Generic code cannot be null");
				throw new BusinessException(ServiceError.Unknown, "Generic code cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Adding Generic code");
		}
		return response;
	}

	@Override
	public Boolean deleteGenericCodeWithReaction(GenericCodesAndReaction request) {
		Boolean response = false;
		try {
			if (request.getFirstGenericCode() != null && request.getSecondGenericCode() != null) {
				List<GenericCodesAndReactionsCollection> genericCodesAndReactionsCollections = genericCodesAndReactionsRepository
						.findByGenericCodeIn(Arrays.asList(request.getFirstGenericCode().getCode(),
								request.getSecondGenericCode().getCode()));
				if (genericCodesAndReactionsCollections != null) {
					for (GenericCodesAndReactionsCollection codesAndReactionsCollection : genericCodesAndReactionsCollections) {
						List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
						@SuppressWarnings("unchecked")
						Collection<String> codes = CollectionUtils.collect(codesWithReaction,
								new BeanToPropertyValueTransformer("genericCode"));

						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
							if (codes.contains(request.getSecondGenericCode().getCode())) {
								for (Code codeWithReaction : codesWithReaction)
									if (codeWithReaction.getGenericCode()
											.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
										codesWithReaction.remove(codeWithReaction);
										break;
									}
							}
						}
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
							if (codes.contains(request.getFirstGenericCode().getCode())) {
								for (Code codeWithReaction : codesWithReaction)
									if (codeWithReaction.getGenericCode()
											.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
										codesWithReaction.remove(codeWithReaction);
										break;
									}
							}
						}
						codesAndReactionsCollection.setCodes(codesWithReaction);
						codesAndReactionsCollection.setUpdatedTime(new Date());
						genericCodesAndReactionsRepository.save(codesAndReactionsCollection);

						ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
								.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
						if (esCodesAndReactions == null)
							esCodesAndReactions = new ESGenericCodesAndReactions();
						esCodesAndReactions.setCodes(null);
						BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
						esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
						esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
					}
					response = true;
				}
			} else {
				logger.error("Generic code cannot be null");
				throw new BusinessException(ServiceError.Unknown, "Generic code cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While deleting Generic code with reaction");
		}
		return response;
	}

	@Override
	public Boolean uploadGenericCodeWithReaction(FormDataBodyPart file) {
		Boolean response = false;
		try {
			String line = null;

			if (file != null) {
				InputStream fis = file.getEntityAs(InputStream.class);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (i != 0) {
						String[] data = line.split(",");
						String firstGenericCode = data[0], firstGenericCodeName = data[1], secondGenericCode = data[2],
								secondGenericCodeName = data[3], reaction = null, explanation = null;
						if (data.length > 4)
							reaction = data[4];
						if (data.length > 5)
							explanation = data[5];

						GenericCodesAndReaction request = new GenericCodesAndReaction();
						GenericCode firstGenericCodeObj = new GenericCode();
						firstGenericCodeObj.setCode(firstGenericCode);
						firstGenericCodeObj.setName(firstGenericCodeName);
						request.setFirstGenericCode(firstGenericCodeObj);

						GenericCode secondGenericCodeObj = new GenericCode();
						secondGenericCodeObj.setCode(secondGenericCode);
						secondGenericCodeObj.setName(secondGenericCodeName);
						request.setSecondGenericCode(secondGenericCodeObj);

						request.setReactionType(reaction);
						request.setExplanation(explanation);
						addGenericCodeWithReaction(request);
						response = true;
					}
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DrugAddEditResponse makeCustomDrugGlobal(DrugAddEditRequest request) {
		DrugAddEditResponse response = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (drugCollection == null) {
				throw new BusinessException(ServiceError.Unknown, "No Drug found. Invalid drug Id");
			}
			DrugCollection globalDrugCollection = drugRepository
					.findByDrugCodeAndDoctorId(request.getDrugCode().toString(), null);
			if (globalDrugCollection != null) {
				throw new BusinessException(ServiceError.Unknown, "Global drug is already found for this custom drug");
			} else
				globalDrugCollection = new DrugCollection();

			BeanUtil.map(drugCollection, globalDrugCollection);
			globalDrugCollection.setId(null);
			globalDrugCollection.setDoctorId(null);
			globalDrugCollection.setLocationId(null);
			globalDrugCollection.setHospitalId(null);
			globalDrugCollection.setCreatedTime(new Date());
			globalDrugCollection.setUpdatedTime(new Date());
			globalDrugCollection.setCreatedBy("ADMIN");
			globalDrugCollection.setGenericNames(request.getGenericNames());
			globalDrugCollection = drugRepository.save(globalDrugCollection);

			drugCollection.setGenericNames(drugCollection.getGenericNames());
			drugCollection.setUpdatedTime(new Date());
			drugCollection = drugRepository.save(drugCollection);

			transactionalManagementService.addResource(drugCollection.getId(), Resource.DRUG, false);
			if (drugCollection != null) {
				ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if (drugCollection.getDrugType() != null) {
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esPrescriptionService.addDrug(esDrugDocument);
			}

			if (globalDrugCollection != null) {
				response = new DrugAddEditResponse();
				BeanUtil.map(globalDrugCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While making Custom Drug Global");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While making Custom Drug Global");
		}
		return response;
	}

	@Override
	public Integer addCSVData(String filePath) {
		Integer response = 0;
		try {

			String line = "";
			String cvsSplitBy = ",";

			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] field = line.split(cvsSplitBy);

				DrugAddEditRequest request = new DrugAddEditRequest();
				request.setDrugName(field[1]);
				DrugType type = new DrugType();
				type.setType(field[2]);
				request.setDrugType(type);
				request.setPackSize(field[3]);
				request.setCompanyName(field[4]);
				request.setMRP(field[7] + " INR");
				addDrug(request);
				response++;

			}

		} catch (Exception e) {
			logger.error("Error while getting Admin List" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting Admin List  " + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean uploadDiagnosticTest(FormDataBodyPart file) {
		Boolean response = false;
		try {
			String line = null;

			if (file != null) {
				InputStream fis = file.getEntityAs(InputStream.class);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (i != 0) {
						String[] data = line.split(",");
						String testName = data[0], localeUId = data[1], cost = data[2], commission = null,
								commissionUnit = null, costForPatient = null;

						if (data.length > 3)
							commission = data[3];
						if (data.length > 4)
							commissionUnit = data[4];
						if (data.length > 5)
							costForPatient = data[5];

						DiagnosticTestCollection globalDiagnosticTestCollection = diagnosticTestRepository
								.findByTestNameRegexAndLocationId(testName, null);
						LocationCollection locationCollection = locationRepository.findByLocaleUID(localeUId);
						if (locationCollection != null) {
							DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
									.findByTestNameRegexAndLocationId(testName, locationCollection.getId());
							if (diagnosticTestCollection == null) {
								diagnosticTestCollection = new DiagnosticTestCollection();
								diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
								diagnosticTestCollection.setCreatedTime(new Date());
								diagnosticTestCollection
										.setDiagnosticTestCode(globalDiagnosticTestCollection.getDiagnosticTestCode());
								diagnosticTestCollection.setLocationId(locationCollection.getId());
								diagnosticTestCollection.setHospitalId(locationCollection.getHospitalId());
								diagnosticTestCollection.setSpecimen(globalDiagnosticTestCollection.getSpecimen());
							}

							Amount diagnosticTestComission = null;
							if (!DPDoctorUtils.anyStringEmpty(commission)) {
								diagnosticTestComission = new Amount();
								diagnosticTestComission.setValue(Double.parseDouble(commission));
								diagnosticTestComission.setUnit(
										!DPDoctorUtils.anyStringEmpty(commissionUnit) ? UnitType.valueOf(commissionUnit)
												: UnitType.INR);
								diagnosticTestCollection.setDiagnosticTestComission(diagnosticTestComission);
							}
							diagnosticTestCollection.setDiagnosticTestCost(
									!DPDoctorUtils.anyStringEmpty(cost) ? Double.parseDouble(cost) : 0.0);
							diagnosticTestCollection.setDiagnosticTestCostForPatient(
									!DPDoctorUtils.anyStringEmpty(costForPatient) ? Double.parseDouble(costForPatient)
											: 0.0);
							diagnosticTestCollection.setUpdatedTime(new Date());
							diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);

							transactionalManagementService.addResource(diagnosticTestCollection.getId(),
									Resource.DIAGNOSTICTEST, false);

							ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
							BeanUtil.map(diagnosticTestCollection, esDiagnosticTestDocument);
							esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
						}
					}
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while uploading diagnostic tests" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while uploading diagnostic tests");
		}
		return response;

	}

	@Override
	public Drug getDrugNameById(String drugNameId) {
		Drug response = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(drugNameId)).orElse(null);
			if (drugCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Integer getPrescriptionItemsCount(String type, String range, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm,
			String category, String disease, String speciality) {

		Integer response = 0;

		switch (PrescriptionItems.valueOf(type.toUpperCase())) {

		case DRUGS: {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchTerm = searchTerm.toUpperCase();
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugsForAdminCount(updatedTime, discarded, searchTerm, category);
				break;

			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugsForAdminCount(updatedTime, discarded, searchTerm, category);
//				else
//					response = getCustomDrugs(doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugsForAdminCount(updatedTime, discarded, searchTerm, category);
//				else
//					response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			}
			break;
		}
		case DRUGTYPE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugTypeForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getGlobalDrugType(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugTypeForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugTypeForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			}
			break;
		}
		case DRUGDIRECTION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDirectionForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getGlobalDrugDirection(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDirectionForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDirectionForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomGlobalDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			}
			break;
		}
		case DRUGDOSAGE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDosageForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getGlobalDrugDosage(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDosageForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDosageForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomGlobalDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			}
			break;
		}
		case DRUGDURATIONUNIT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDrugDurationUnitForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getGlobalDrugDurationUnit(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDrugDurationUnitForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
//							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDrugDurationUnitForAdminCount(updatedTime, discarded, searchTerm);
//				else
//					response = getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId,
//							updatedTime, discarded);
				break;
			}
			break;
		}
		// case DRUGSTRENGTHUNIT: {
		// switch (Range.valueOf(range.toUpperCase())) {
		//
		// case GLOBAL:
		// if(isAdmin)response = getGlobalDrugStrengthUnit(page, size,
		// updatedTime, discarded);
		// else response = getGlobalDrugStrengthUnit(page, size,
		// updatedTime, discarded);
		// break;
		// case CUSTOM:
		// if(isAdmin)response = getCustomDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// else response = getCustomDrugStrengthUnit(page, size, doctorId,
		// locationId, hospitalId, updatedTime, discarded);
		// break;
		// case BOTH:
		// if(isAdmin)response = getCustomGlobalDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// else response = getCustomGlobalDrugStrengthUnit(page, size,
		// doctorId, locationId, hospitalId, updatedTime, discarded);
		// break;
		// }
		// break;
		// }
//		case LABTEST: {
//
//			switch (Range.valueOf(range.toUpperCase())) {
//
//			case GLOBAL:
//				break;
//			case CUSTOM:
//				if (isAdmin)
//					response = getCustomLabTestsForAdmin(locationId, hospitalId, updatedTime, discarded, searchTerm);
////				else
////					response = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
//				break;
//			case BOTH:
//				break;
//			}
//			break;
//		}
//
//		case DIAGNOSTICTEST: {
//
//			switch (Range.valueOf(range.toUpperCase())) {
//
//			case GLOBAL:
//				if (isAdmin)
//					response = getGlobalDiagnosticTestsForAdmin(updatedTime, discarded, searchTerm);
////				else
////					response = getGlobalDiagnosticTests(page, size, updatedTime, discarded);
//				break;
//			case CUSTOM:
//				if (isAdmin)
//					response = getCustomDiagnosticTestsForAdmin(updatedTime, discarded, searchTerm);
////				else
////					response = getCustomDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded);
//				break;
//			case BOTH:
//				if (isAdmin)
//					response = getCustomGlobalDiagnosticTestsForAdmin(updatedTime, discarded, searchTerm);
////				else
////					response = getCustomGlobalDiagnosticTests(page, size, locationId, hospitalId, updatedTime,
////							discarded);
//				break;
//			}
//			break;
//		}
//
//		case DRUGBYGCODE: {
//			switch (Range.valueOf(range.toUpperCase())) {
//
//			case GLOBAL:
//				if (isAdmin)
//					response = getGlobalDrugCodeForAdmin( updatedTime, discarded, searchTerm, category);
//				break;
//			case CUSTOM:
//				if (isAdmin)
//					response = getCustomDrugCodeForAdmin(updatedTime, discarded, searchTerm, category);
//				break;
//			case BOTH:
//				if (isAdmin)
//					response = getCustomGlobalDrugCodeForAdmin(page, size, updatedTime, discarded, searchTerm,
//							category);
//				break;
//			}
//			break;
//		}
//
//		case DRUGBYICODE: {
//			switch (Range.valueOf(range.toUpperCase())) {
//
//			case GLOBAL:
//				if (isAdmin)
//					response = getGlobalDrugGenericCodeForAdmin(updatedTime, discarded, searchTerm, category);
//				break;
//			case CUSTOM:
//				if (isAdmin)
//					response = getCustomDrugGenericCodeForAdmin(updatedTime, discarded, searchTerm, category);
//				break;
//			case BOTH:
//				if (isAdmin)
//					response = getCustomGlobalDrugGenericCodeForAdmin(updatedTime, discarded, searchTerm, category);
//				break;
//			}
//			break;
//		}
//
//		case GCODE: {
//			if (isAdmin)
//				response = getGenericCodeForAdmin(updatedTime, searchTerm);
//			break;
//		}
//		case GCODEWITHREACTION: {
//			if (isAdmin)
//				response = getGenericCodeWithReactionForAdmin(updatedTime, discarded, searchTerm);
//			break;
//		}
		case ADVICE: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:

				response = getGlobalAdvicesForAdminCount(updatedTime, searchTerm, discarded, disease, speciality);
				break;

			case CUSTOM:
				response = getCustomAdvicesForAdminCount(updatedTime, searchTerm, discarded, disease, speciality);
				break;

			case BOTH:
				response = getCustomGlobalAdvicesForAdminCount(updatedTime, searchTerm, discarded, disease, speciality);
				break;
			default:
				break;

			}
			break;
		}
		default:
			break;
		}
		return response;

	}

	private Integer getCustomGlobalAdvicesForAdminCount(String updatedTime, String searchTerm, Boolean discarded,
			String disease, String speciality) {

		Integer response = null;

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("advice").regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("advice").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomAdvicesForAdminCount(String updatedTime, String searchTerm, Boolean discarded,
			String disease, String speciality) {
		Integer response = null;

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("advice").regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("advice").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalAdvicesForAdminCount(String updatedTime, String searchTerm, Boolean discarded,
			String disease, String speciality) {
		Integer response = null;

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("advice").regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("advice").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomGlobalDrugDurationUnitForAdminCount(String updatedTime, Boolean discarded,
			String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("unit").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDurationUnitCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomDrugDurationUnitForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("unit").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDurationUnitCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalDrugDurationUnitForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("unit").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDurationUnitCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomGlobalDrugDosageForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("dosage").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomDrugDosageForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("dosage").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalDrugDosageForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("dosage").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomGlobalDrugDirectionForAdminCount(String updatedTime, Boolean discarded,
			String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("direction").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomDrugDirectionForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("direction").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalDrugDirectionForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("direction").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomGlobalDrugTypeForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("type").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugTypeCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomDrugTypeForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("type").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugTypeCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalDrugTypeForAdminCount(String updatedTime, Boolean discarded, String searchTerm) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);

		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("type").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
//		if (!DPDoctorUtils.anyStringEmpty(category)) {
//			criteria.and("categories").is(category);
//		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugTypeCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomGlobalDrugsForAdminCount(String updatedTime, Boolean discarded, String searchTerm,
			String category) {
		Integer response = null;

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("drugName").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getCustomDrugsForAdminCount(String updatedTime, Boolean discarded, String searchTerm,
			String category) {

		Integer response = null;

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("drugName").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	private Integer getGlobalDrugsForAdminCount(String updatedTime, Boolean discarded, String searchTerm,
			String category) {
		Integer response = null;
		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and("drugName").regex(searchTerm, "i");
//		if (!DPDoctorUtils.anyStringEmpty(speciality))
//			criteria.and("speciality").is(speciality);
//		if (!DPDoctorUtils.anyStringEmpty(disease))
//			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}

		try {

			response = (int) mongoTemplate.count(new Query(criteria), DrugCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting Drugs");
		}
		return response;
	}

	public Boolean uploadDrugs() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(UPLOAD_DRUGS));
			Map<String, DrugType> drugTypesMap = new HashMap<String, DrugType>();
			List<DrugType> drugTypes = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("doctorId").is(null))),
							DrugTypeCollection.class, DrugType.class)
					.getMappedResults();
			if (drugTypes != null && !drugTypes.isEmpty()) {
				for (DrugType drugType : drugTypes)
					drugTypesMap.put(drugType.getType(), drugType);
			}

			while ((line = br.readLine()) != null) {
				if (lineCount > 0) {
					System.out.println("line 1");
					String[] fields = line.split(",");

					// this
					String drugCode = fields[0].trim(), drugName = fields[1].trim(), drugType = fields[2].trim(),
							companyName = fields[6].trim();
					System.out.println("line 2");

					if (drugType.equalsIgnoreCase("TABLET"))
						drugType = "TAB";
					if (drugType.equalsIgnoreCase("CAPSULE"))
						drugType = "CAP";
					if (drugType.equalsIgnoreCase("OINTMENT"))
						drugType = "OINT";
					if (drugType.equalsIgnoreCase("SYRUP"))
						drugType = "SYP";

					System.out.println("detail " + drugCode + " " + drugName + " " + drugType + companyName);

					List<DrugCollection> drugCollections = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("drugName").is(drugName)
									.and("drugType.type").is(drugType).and("companyName").is(companyName))),
							DrugCollection.class, DrugCollection.class).getMappedResults();
					System.out.println("line 3" + drugCollections.size() + drugCollections);
					if (drugCollections.size() == 0) {
						DrugCollection drugCollection = new DrugCollection();

						if (DPDoctorUtils.anyStringEmpty(drugCode)) {
							System.out.println("drugCode field empty" + drugCode);
							drugCode = generateDrugCode(drugName, drugType);// gene
						}
						System.out.println("drugCode" + drugCode);
						drugCollection.setDrugCode(drugCode);

						Date createdTime = new Date();
						drugCollection.setCreatedTime(createdTime);
						drugCollection.setCreatedBy("ADMIN");
						drugCollection.setRankingCount(1);

						drugCollection.setDrugName(drugName);
						drugCollection.setDrugType(drugTypesMap.get(drugType));
						if (drugCollection.getDrugType() == null) {
							DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
							drugTypeCollection.setAdminCreatedTime(new Date());
							drugTypeCollection.setCreatedBy("ADMIN");
							drugTypeCollection.setCreatedTime(new Date());
							drugTypeCollection.setUpdatedTime(new Date());
							drugTypeCollection.setType(drugType);
							drugTypeCollection = drugTypeRepository.save(drugTypeCollection);

							DrugType drugTypeObj = new DrugType();
							BeanUtil.map(drugTypeCollection, drugTypeObj);
							drugCollection.setDrugType(drugTypeObj);
							drugTypesMap.put(drugType, drugTypeObj);
						}
						drugCollection.setCompanyName(companyName);

						if (!DPDoctorUtils.anyStringEmpty(fields[3])) {
							String specialities[] = fields[3].split("\\+");
							drugCollection.setSpecialities(Arrays.asList(specialities));
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[4])) {
							drugCollection.setPackForm(fields[4]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[5])) {
							drugCollection.setPackSize(fields[5]);
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[7])
								&& !(fields[7].equalsIgnoreCase("NOT AVAILABLE"))) {
//							fields[7].replace("\\", "+");
							String genericsList[] = fields[7].split("\\+");

							Map<String, String> generics = new HashMap<String, String>();
							for (String genericName : genericsList) {
								genericName = genericName.replaceAll("\\s", "");
								String key = "", value = null;
								int indexOfStart = genericName.indexOf("("), indexOfEnd = genericName.indexOf(")");
								if (indexOfStart > -1 && indexOfEnd > -1) {
									key = genericName.substring(0, indexOfStart);
									value = genericName.substring(indexOfStart + 1, indexOfEnd);
									if (!DPDoctorUtils.anyStringEmpty(value) && value.equalsIgnoreCase("NA"))
										value = null;
								} else {
									key = genericName;
								}
								generics.put(key, value);
							}
							List<GenericCode> genericCodesFromDB = mongoTemplate.aggregate(
									Aggregation.newAggregation(
											Aggregation.match(new Criteria("name").in(generics.keySet()))),
									GenericCodeCollection.class, GenericCode.class).getMappedResults();
							for (GenericCode genericCode : genericCodesFromDB) {
								genericCode.setStrength(generics.get(genericCode.getName()));
							}

							List<GenericCode> genericCodes = new ArrayList<>();
							genericCodes.addAll(genericCodesFromDB);

							if (generics.size() != genericCodes.size()) {
								for (Entry<String, String> generic : generics.entrySet()) {
									boolean isPresent = false;
									for (GenericCode genericCode : genericCodes)
										if (generic.getKey().equalsIgnoreCase(genericCode.getName()))
											isPresent = true;

									if (!isPresent) {
										GenericCodeCollection genericCodeCollection = new GenericCodeCollection();
										genericCodeCollection.setAdminCreatedTime(new Date());
										genericCodeCollection.setName(generic.getKey());
										genericCodeCollection
												.setCode(generateGenericCode(genericCodeCollection.getName()));
										genericCodeCollection.setCreatedBy("ADMIN");
										genericCodeCollection.setCreatedTime(new Date());
										genericCodeCollection.setUpdatedTime(new Date());
										genericCodeCollection = genericCodeRepository.save(genericCodeCollection);

										GenericCode code = new GenericCode();
										BeanUtil.map(genericCodeCollection, code);
										code.setStrength(generic.getValue());
										genericCodes.add(code);
									}
								}
							}

							drugCollection.setGenericNames(genericCodes);
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[8])) {
							String categories[] = fields[8].split("\\+");
							drugCollection.setCategories(Arrays.asList(categories));
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[9])) {
							String composition[] = fields[9].split("\\+");
							drugCollection.setComposition(Arrays.asList(composition));
						}

						if (fields.length > 10 && !DPDoctorUtils.anyStringEmpty(fields[10])) {
							drugCollection.setMRP(fields[10] + " INR");
						}

						if (fields.length > 11 && !DPDoctorUtils.anyStringEmpty(fields[11])) {
							drugCollection.setPrizePerPack(fields[11] + " INR");
						}

						if (fields.length > 12 && !DPDoctorUtils.anyStringEmpty(fields[12])) {
							drugCollection.setRxRequired(fields[12]);
						}

						if (fields.length > 13 && !DPDoctorUtils.anyStringEmpty(fields[13])) {
							drugCollection.setUnsafeWith(fields[13]);
						}
						drugCollection = drugRepository.save(drugCollection);
						System.out.println(fields[0]);

						transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
						if (drugCollection != null) {
							ESDrugDocument esDrugDocument = new ESDrugDocument();
							BeanUtil.map(drugCollection, esDrugDocument);
							if (drugCollection.getDrugType() != null) {
								esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
								esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
							}
							esPrescriptionService.addDrug(esDrugDocument);
//							System.out.println("es update"+esDrugDocument);
						}
					} else {
						System.out.println("Already present: " + lineCount + " .. " + drugName);
					}
				}
				lineCount = lineCount + 1;
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred uploading drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred uploading drugs");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	private String generateDrugCode(String drugName, String drugType) {
		drugName = drugName.replaceAll("[^a-zA-Z0-9]", "");
		String drugCode = null;
		if (drugName.length() > 2)
			drugCode = drugType.substring(0, 2) + drugName.substring(0, 3);
		else
			drugCode = drugType.substring(0, 2) + drugName.substring(0, 2);

		List<DrugCollection> drugCollections = mongoTemplate
				.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(
										new Criteria("doctorId").is(null).and("drugCode").regex("^" + drugCode, "i")),
								Aggregation.sort(new Sort(Direction.DESC, "drugCode")), Aggregation.limit(1)),
						DrugCollection.class, DrugCollection.class)
				.getMappedResults();
		DrugCollection drugCollection = null;
		if (drugCollections != null && !drugCollections.isEmpty())
			drugCollection = drugCollections.get(0);
//		DrugCollection drugCollection = drugRepository.findByStartWithDrugCode(drugCode, null, null, null, new Sort(Sort.Direction.DESC, "drugCode"));
		if (drugName.equalsIgnoreCase("O2")) {
			drugCollection = null;
		}

		if (drugCollection != null) {
			Integer count = Integer.parseInt(drugCollection.getDrugCode().toUpperCase().replace(drugCode, "")) + 1;
			if (count < 1000) {
				drugCode = drugCode + String.format("%04d", count);
			} else {
				drugCode = drugCode + count;
			}
		} else {
			drugCode = drugCode + "0001";
		}

		return drugCode;
	}

	private String generateGenericCode(String genericName) {
		genericName = genericName.replaceAll("[^a-zA-Z0-9]", "");
		String genericCode = "GEN" + genericName.substring(0, 3);

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("code").is(genericCode)),
				Aggregation.sort(new Sort(Sort.Direction.DESC, "code")), Aggregation.limit(1));

		List<GenericCodeCollection> genericCodeCollections = mongoTemplate
				.aggregate(aggregation, GenericCodeCollection.class, GenericCodeCollection.class).getMappedResults();

		System.out.println("cou" + genericCodeCollections + genericCodeCollections.size());

//		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByGenericCodeStartsWith(genericCode,
//				new Sort(Sort.Direction.DESC, "code"));

		if (genericCodeCollections.size() != 0) {

			for (GenericCodeCollection genericCodeCollection : genericCodeCollections) {

				Integer count = Integer.parseInt(genericCodeCollection.getCode().replace(genericCode, "")) + 1;
				if (count < 1000) {
					genericCode = genericCode + String.format("%04d", count);
				} else {
					genericCode = genericCode + count;
				}
			}
		} else {
			genericCode = genericCode + "0001";
		}

		genericCode = updateIfGenericCodeExist(genericCode);

		System.out.println("genericCode" + genericCode);
		return genericCode;
	}

	private String updateIfGenericCodeExist(String genericCode) {
		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByGenericCodeStartsWith(genericCode,
				new Sort(Sort.Direction.DESC, "createdTime"));
		if (genericCodeCollection != null) {
			genericCode = genericCode.substring(0, 6);
			Integer count = Integer.parseInt(genericCodeCollection.getCode().replace(genericCode, "")) + 1;
			if (count < 1000) {
				genericCode = genericCode + String.format("%04d", count);
			} else {
				genericCode = genericCode + count;
			}
			genericCode = updateIfGenericCodeExist(genericCode);
		}

		return genericCode;

	}

	@Override
	public Boolean uploadDrugDetail() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int lineCount = 0;
		String csvLine = null;
//		int dataCountNotUploaded =0;
		FileWriter fileWriter = null;

		try {

			fileWriter = new FileWriter(DRUG_DETAIL_INFORMATION_NOT_UPLOADED_FILE);

			br = new BufferedReader(new FileReader(DRUG_DETAIL_INFORMATION_UPLOADED_FILE));

			while ((line = br.readLine()) != null) {
				if (lineCount > 0) {
					String[] fields = line.split(",");
					
					if (DPDoctorUtils.anyStringEmpty(fields[0]) && fields[0].equalsIgnoreCase("NA")) {
						throw new BusinessException(ServiceError.Unknown, "generic name Should not be null");
					}		
										
					System.out.println("step 1");
					DrugDetailInformationCollection drugDetailInformationCollection = new DrugDetailInformationCollection();
					System.out.println("gene " + fields[0]);
					String genericsList[] = fields[0].split("\\+");
					
					System.out.println("gene list n len" + Arrays.toString(genericsList) +" "+ genericsList.length);

					Criteria criteria = new Criteria();

					List<String> genObjectNames=null;
					if (genericsList.length != 0) {
						genObjectNames = new ArrayList<String>();
						for (String str : genericsList)
							genObjectNames.add(str);
					}
					
					System.out.println(genObjectNames);
					
					if(genObjectNames!=null)
						criteria.and("name").in(genObjectNames);
					
					List<GenericCode> responseGenericCode = null;

					Aggregation aggregation = null;
					
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
					
						System.out.println(aggregation);
					responseGenericCode = mongoTemplate.aggregate(aggregation, GenericCodeCollection.class, GenericCode.class).getMappedResults();

//					System.out.println("GenericCode "+responseGenericCode);
					System.out.println("GenericCode size "+responseGenericCode.size());

									
//					GenericCodeCollection genericCodeCollection = null;
//					genericCodeCollection = genericCodeRepository.findByName(fields[0]);
					if (responseGenericCode.size() != genericsList.length) {
//						throw new BusinessException(ServiceError.Unknown, "generic name not found");
						System.out.println("Generic name not Found " + fields[0]);

						fileWriter.append(fields[0]);
						fileWriter.append("/");

					} else {
						if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
							System.out.println("enter insave");
//							drugCollection.setGenericNames(Arrays.asList(genericsList));
							
//							GenericCode code = new GenericCode();
//							BeanUtil.map(genericCodeCollection, code);
							drugDetailInformationCollection.setGenericNames(responseGenericCode);
							System.out.println("code" + drugDetailInformationCollection.getGenericNames());
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[1])) {
							drugDetailInformationCollection.setDescription(fields[1]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[2])) {
							String categories[] = fields[2].split("\\+");
							drugDetailInformationCollection.setCategories(Arrays.asList(categories));
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[3])&& !fields[3].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setMechanismOfAction(fields[3]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[4])&& !fields[4].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPharmalogicalUsesGeneral(fields[4]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[5])&& !fields[5].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPharmalogicalUsesPhysician(fields[5]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[6])&& !fields[6].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setRxRequired(fields[6]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[7])&& !fields[7].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDrugSchedule(fields[7]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[8])&& !fields[8].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setHabitForming(fields[8]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[9])&& !fields[9].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setAdultDose(fields[9]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[10])&& !fields[10].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPediatricDose(fields[10]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[11])&& !fields[11].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setGeriatricDose(fields[11]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[12])&& !fields[12].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setMissedDose(fields[12]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[13])&& !fields[13].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setOverDose(fields[13]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[14])&& !fields[14].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setHalfLifeOfDrug(fields[14]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[15])&& !fields[15].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setOnsetOfAction(fields[15]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[16])&& !fields[16].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDurationOfEffect(fields[16]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[17])&& !fields[17].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setCommanSideEffects(fields[17]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[18])&& !fields[18].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setContraindications(fields[18]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[19])&& !fields[19].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDrugMinor(fields[19]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[20])&& !fields[20].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDrugModerate(fields[20]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[21])&& !fields[21].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDrugMajor(fields[21]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[22])&& !fields[22].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setFoodDrug(fields[22]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[23])&& !fields[23].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setDrivingSafety(fields[23]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[24])&& !fields[24].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setAlcoholDrugInteraction(fields[24]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[25]) && !fields[25].equalsIgnoreCase("NA")) {
							System.out.println("NA filed");
							drugDetailInformationCollection.setBrain(fields[25]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[26])&& !fields[26].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setHeart(fields[26]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[27])&& !fields[27].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setLiver(fields[27]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[28])&& !fields[28].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setKidney(fields[28]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[29])&& !fields[29].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setGit(fields[29]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[30])&& !fields[30].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setSkin(fields[30]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[31])&& !fields[31].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setEnt(fields[31]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[32])&& !fields[32].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setEye(fields[32]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[33])&& !fields[33].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPediatric(fields[33]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[34])&& !fields[34].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setGeriatric(fields[34]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[35])&& !fields[35].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPregnantWoman(fields[35]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[36])&& !fields[36].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setPregnancyCategory(fields[36]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[37])&& !fields[37].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setLactating(fields[37]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[38])&& !fields[38].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setReferences(fields[38]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[39])&& !fields[39].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setCommanBrandName(fields[39]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[40])&& !fields[40].equalsIgnoreCase("NA")) {
							drugDetailInformationCollection.setUrl(fields[40]);
						}
						drugDetailInformationCollection = drugDetailInformationRepository.save(drugDetailInformationCollection);
						System.out.println(drugDetailInformationCollection.getId());
					}
				}
				lineCount = lineCount + 1;
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
//		finally {

//		System.out.println(patientCount + " patients already exist with mobile number "
//				+ request.getMobileNumber());
//		fileWriter.append(csvLine);fileWriter.append(NEW_LINE_SEPARATOR);

//			if (br != null) {
//				try {
//					br.close();
//					if (fileWriter != null) {
//						fileWriter.flush();
//						fileWriter.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return response;

	}

	@Override
	public Response<Object> getDrugDtail(int page, int size, String searchTerm) {
		Response<Object> response = new Response<Object>();

		List<DrugCollection> responseDrug = null;

		try {
//			Criteria criteria = new Criteria();
//			
//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria.and("drugName").regex(searchTerm, "i");
//			
//			
//			Aggregation aggregationDrug = null;
//
//			aggregationDrug = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((long)(page) * size),
//						Aggregation.limit(1));					
//			
//			responseDrug = mongoTemplate.aggregate(aggregationDrug, DrugCollection.class, DrugCollection.class).getMappedResults();
//			
//			System.out.println(responseDrug);
//			DrugCollection drugCollection = null;
//
//			if (responseDrug != null && !responseDrug.isEmpty())
//				drugCollection = responseDrug.get(0);
//			System.out.println("drugCollection"+drugCollection.getGenericNames());
//			
			
			Criteria criteriaInfo = new Criteria();

			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
//				searchTerm = searchTerm.replaceAll(" ", ".*");
				// criteria = criteria.and("user.firstName").regex(searchTerm, "i");

				criteriaInfo = criteriaInfo.orOperator(new Criteria("drug_cl.drugName").regex("^" + searchTerm, "i"),
						new Criteria("drug_cl.drugName").regex("^" + searchTerm),
						new Criteria("drug_cl.genericNames.name").regex("^" + searchTerm, "i"),
						new Criteria("drug_cl.genericNames.name").regex("^" + searchTerm)
//						new Criteria("user.mobileNumber").regex("^" + searchTerm, "i"),
//						new Criteria("user.mobileNumber").regex("^" + searchTerm)
				);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteriaInfo),
						Aggregation.lookup("drug_cl", "_id", "_id", "drugNamecl"),
						Aggregation.unwind("drugNamecl"),
						Aggregation.unwind("drugNamecl.genericNames"),
						Aggregation.lookup("drug_generic_info_cl", "drugNamecl.genericNames.name", "genericNames.name", "infocl"),
						Aggregation.unwind("infocl", true), 
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("genericNames", "$infocl.genericNames")
										.append("drugType", "$drugNamecl.drugType")
										.append("drugName", "$drugNamecl.drugName")
										.append("categories", "$infocl.categories")
										.append("description", "$infocl.description")
										.append("rxRequired", "$infocl.rxRequired")
										.append("commanBrandName", "$infocl.commanBrandName"))),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("genericNames", new BasicDBObject("$first", "$genericNames"))
								.append("drugType", new BasicDBObject("$first", "$drugType"))
								.append("drugName", new BasicDBObject("$first", "$drugName"))
								.append("categories", new BasicDBObject("$first", "$categories"))
								.append("description", new BasicDBObject("$first", "$description"))
								.append("rxRequired", new BasicDBObject("$first", "$rxRequired"))
								.append("commanBrandName", new BasicDBObject("$first", "$commanBrandName")))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(

						Aggregation.lookup("drug_cl", "_id", "_id", "drugNamecl"),
						Aggregation.unwind("drugNamecl"),
						Aggregation.unwind("drugNamecl.genericNames"),
						Aggregation.lookup("drug_generic_info_cl", "genericNames.name", "drugNamecl.genericNames.name", "infocl"),
						Aggregation.unwind("infocl", true), Aggregation.match(criteriaInfo),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("genericNames", "$infocl.genericNames")
										.append("drugType", "$drugNamecl.drugType")
										.append("drugName", "$drugNamecl.drugName")
										.append("categories", "$infocl.categories")
										.append("description", "$infocl.description")
										.append("rxRequired", "$infocl.rxRequired")
										.append("commanBrandName", "$infocl.commanBrandName"))),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("genericNames", new BasicDBObject("$first", "$genericNames"))
								.append("drugType", new BasicDBObject("$first", "$drugType"))
								.append("drugName", new BasicDBObject("$first", "$drugName"))
								.append("categories", new BasicDBObject("$first", "$categories"))
								.append("description", new BasicDBObject("$first", "$description"))
								.append("rxRequired", new BasicDBObject("$first", "$rxRequired"))
								.append("commanBrandName", new BasicDBObject("$first", "$commanBrandName")))),

						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			System.out.println("aggregation" + aggregation);
			response.setDataList(mongoTemplate
					.aggregate(aggregation, DrugCollection.class, DrugInformationResponse.class).getMappedResults());

		} catch (BusinessException e) {
			logger.error("Error while getting plan " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting plan " + e.getMessage());

		}
		return response;
	}

}