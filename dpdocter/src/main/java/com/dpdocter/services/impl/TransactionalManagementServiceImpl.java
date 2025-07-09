package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESLocationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESLocationRepository;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.AppointmentFeedbackPatientSMSResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.opencsv.CSVWriter;

import common.util.web.DPDoctorUtils;

@Service
public class TransactionalManagementServiceImpl implements TransactionalManagementService {

	private static Logger logger = LogManager.getLogger(TransactionalManagementServiceImpl.class.getName());

	@Autowired
	private TransnationalRepositiory transnationalRepositiory;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private LabTestRepository labTestRepository;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private InvestigationRepository investigationRepository;

	@Autowired
	private DiagnosisRepository diagnosisRepository;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private ESLocationRepository esLocationRepository;

	@Value(value = "${mail.appointment.details.subject}")
	private String appointmentDetailsSub;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MailService mailService;

	@Value(value = "${custom.drugs.resource}")
	private String customDrugsFilePath;

	@Value(value = "${mail.pharmacy.email.id}")
	private String pharmacyEmailId;

	@Value("${is.env.production}")
	private Boolean isEnvProduction;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;

	@Scheduled(cron = "${mail.custom.drugs.cron.time}", zone = "IST")
	@Transactional
	public void mailCustomDrugsToAdmin() {
		try {
			if (isEnvProduction) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

				localCalendar.setTime(new Date());
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);
				DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));

				DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));

				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("doctorId").ne(null).and("locationId").ne(null).and("hospitalId")
								.ne(null).and("createdTime").gte(new Date(fromTime.getMillis()))
								.lte(new Date(toTime.getMillis()))),
						Aggregation.sort(new Sort(Direction.ASC, "createdTime")));
				AggregationResults<DrugCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
						DrugCollection.class, DrugCollection.class);

				List<DrugCollection> drugCollections = aggregationResults.getMappedResults();
				if (drugCollections != null && !drugCollections.isEmpty()) {
					boolean alreadyExists = new File(customDrugsFilePath).exists();

					CSVWriter csvOutput = new CSVWriter(new FileWriter(customDrugsFilePath, true));
					if (!alreadyExists) {
						String[] headers = { "DrugName", "Drug Type", "Drug Code", "Duration", "Dosage", "Dosage Time",
								"Direction", "Created By" };
						csvOutput.writeNext(headers);
					}

					for (DrugCollection drugCollection : drugCollections) {
						Integer drugCount = drugRepository.countByDrugCode(drugCollection.getDrugCode());
						if (drugCount != null && drugCount > 1)
							break;
						String[] data = { drugCollection.getDrugName(),
								drugCollection.getDrugType() != null ? drugCollection.getDrugType().getType() : null,
								drugCollection.getDrugCode(), drugCollection.getDuration() + "",
								drugCollection.getDosage(), drugCollection.getDosageTime() + "",
								drugCollection.getCreatedBy() };
						csvOutput.writeNext(data);
					}
					csvOutput.close();
					FileSystemResource fileSystemResource = new FileSystemResource(customDrugsFilePath);
					MailAttachment mailAttachment = new MailAttachment();
					mailAttachment.setFileSystemResource(fileSystemResource);
					mailAttachment.setAttachmentName("customDrugs.csv");

					MailResponse mailResponse = new MailResponse();
					mailResponse.setMailAttachment(mailAttachment);
					mailService.sendEmail(pharmacyEmailId, "Custom Drug list", "Hello All",
							mailResponse.getMailAttachment());
					fileSystemResource.getFile().delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0 */1 * * *", zone = "IST")
	@Transactional
	public void sendAppointmentFeedbackMessage() {
		try {
			Date date = new Date();
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("feedbackURL").exists(true).ne(null))),
							DoctorClinicProfileCollection.class, DoctorClinicProfileCollection.class)
					.getMappedResults();
			if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
				List<ObjectId> doctorIds = (List<ObjectId>) CollectionUtils.collect(doctorClinicProfileCollections,
						new BeanToPropertyValueTransformer("doctorId"));
				List<ObjectId> locationIds = (List<ObjectId>) CollectionUtils.collect(doctorClinicProfileCollections,
						new BeanToPropertyValueTransformer("locationId"));

				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				localCalendar.setTime(date);

				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);
				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				int currentHour = localCalendar.get(Calendar.HOUR_OF_DAY) - 1;
				if (currentHour >= 0) {
					List<AppointmentFeedbackPatientSMSResponse> appointmentCollections = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(new Criteria("state")
											.is(AppointmentState.CONFIRM.getState()).and("type")
											.is(AppointmentType.APPOINTMENT.getType()).and("doctorId").in(doctorIds)
											.and("locationId").in(locationIds).and("fromDate").gte(fromDateTime)
											.and("toDate").lte(toDateTime).and("time.fromTime").gte(currentHour * 60)
											.and("time.toTime").lte((currentHour * 60) + 59)),

											Aggregation.lookup("user_cl", "patientId", "_id", "user"),
											Aggregation.unwind("user"),
											Aggregation.lookup("location_cl", "locationId", "_id", "location"),
											Aggregation.unwind("location"),
											Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
											new CustomAggregationOperation(new Document("$unwind",
													new BasicDBObject("path", "$patient")
															.append("preserveNullAndEmptyArrays", true))),
											new CustomAggregationOperation(new Document("$redact", new BasicDBObject(
													"$cond",
													new BasicDBObject("if",
															new BasicDBObject("$eq",
																	Arrays.asList("$patient.locationId",
																			"$locationId"))).append("then", "$$KEEP")
																					.append("else", "$$PRUNE")))),
											new ProjectionOperation(Fields.from(Fields.field("doctorId", "$doctorId"),
													Fields.field("locationId", "$locationId"),
													Fields.field("patientName", "$patient.localPatientName"),
													Fields.field("patientMobileNumber", "$user.mobileNumber"),
													Fields.field("locationName", "$location.locationName")))),
									AppointmentCollection.class, AppointmentFeedbackPatientSMSResponse.class)
							.getMappedResults();

					if (appointmentCollections != null && !appointmentCollections.isEmpty()) {

						Map<String, String> feedbackURLMap = new HashMap<String, String>();
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							feedbackURLMap.put(
									doctorClinicProfileCollection.getDoctorId().toString()
											+ doctorClinicProfileCollection.getLocationId().toString(),
									doctorClinicProfileCollection.getFeedbackURL());
						}

						for (AppointmentFeedbackPatientSMSResponse appointmentCollection : appointmentCollections) {
							if (!DPDoctorUtils.anyStringEmpty(appointmentCollection.getPatientMobileNumber())) {
								SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
								SMSDetail smsDetail = new SMSDetail();
								SMS sms = new SMS();
								sms.setSmsText("Dear " + appointmentCollection.getPatientName()
										+ ", Thank you for visiting " + appointmentCollection.getLocationName()
										+ ". Kindly share your experience with us, by clicking on "
										+ feedbackURLMap.get(appointmentCollection.getDoctorId()
												+ appointmentCollection.getLocationId()));

								SMSAddress smsAddress = new SMSAddress();
								smsAddress.setRecipient(appointmentCollection.getPatientMobileNumber());
								sms.setSmsAddress(smsAddress);

								smsDetail.setSms(sms);
								smsTrackDetail.setTemplateId("1307161521948582753");
								smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
								List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
								smsDetails.add(smsDetail);
								smsTrackDetail.setSmsDetails(smsDetails);
								sMSServices.sendSMS(smsTrackDetail, false);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public void addResource(ObjectId resourceId, Resource resource, boolean isCached) {
		TransactionalCollection transactionalCollection = null;
		try {
			transactionalCollection = transnationalRepositiory.findByResourceIdAndResource(resourceId,
					resource.getType());
			if (transactionalCollection == null) {
				transactionalCollection = new TransactionalCollection();
				transactionalCollection.setResourceId(resourceId);
				transactionalCollection.setResource(resource);
				transactionalCollection.setIsCached(isCached);
				transnationalRepositiory.save(transactionalCollection);
			} else {
				transactionalCollection.setIsCached(isCached);
				transnationalRepositiory.save(transactionalCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public void checkPatient(ObjectId id) {
		try {
			UserCollection userCollection = userRepository.findById(id).orElse(null);
			List<PatientCollection> patientCollections = patientRepository.findByUserId(id);
			if (userCollection != null && patientCollections != null) {
				for (PatientCollection patientCollection : patientCollections) {
					ESPatientDocument patientDocument = new ESPatientDocument();

					BeanUtil.map(userCollection, patientDocument);
					if (patientCollection != null)
						BeanUtil.map(patientCollection, patientDocument);

					if (patientCollection != null)
						patientDocument.setId(patientCollection.getId().toString());

					if (patientCollection != null)
						esRegistrationService.addPatient(patientDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDrug(ObjectId id) {
		try {
			DrugCollection drugCollection = drugRepository.findById(id).orElse(null);
			if (drugCollection != null) {
				ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if (drugCollection.getDrugType() != null) {
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esPrescriptionService.addDrug(esDrugDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkLabTest(ObjectId id) {
		try {
			LabTestCollection labTestCollection = labTestRepository.findById(id).orElse(null);
			if (labTestCollection != null) {
				ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
				BeanUtil.map(labTestCollection, esLabTestDocument);
				esPrescriptionService.addLabTest(esLabTestDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkComplaint(ObjectId id) {
		try {
			ComplaintCollection complaintCollection = complaintRepository.findById(id).orElse(null);
			if (complaintCollection != null) {
				ESComplaintsDocument esComplaintsDocument = new ESComplaintsDocument();
				BeanUtil.map(complaintCollection, esComplaintsDocument);
				esClinicalNotesService.addComplaints(esComplaintsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkObservation(ObjectId id) {
		try {
			ObservationCollection observationCollection = observationRepository.findById(id).orElse(null);
			if (observationCollection != null) {
				ESObservationsDocument esObservationsDocument = new ESObservationsDocument();
				BeanUtil.map(observationCollection, esObservationsDocument);
				esClinicalNotesService.addObservations(esObservationsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkInvestigation(ObjectId id) {
		try {
			InvestigationCollection investigationCollection = investigationRepository.findById(id).orElse(null);
			if (investigationCollection != null) {
				ESInvestigationsDocument esInvestigationsDocument = new ESInvestigationsDocument();
				BeanUtil.map(investigationCollection, esInvestigationsDocument);
				esClinicalNotesService.addInvestigations(esInvestigationsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDiagnosis(ObjectId id) {
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findById(id).orElse(null);
			if (diagnosisCollection != null) {
				ESDiagnosesDocument esDiagnosesDocument = new ESDiagnosesDocument();
				BeanUtil.map(diagnosisCollection, esDiagnosesDocument);
				esClinicalNotesService.addDiagnoses(esDiagnosesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkNotes(ObjectId id) {
		try {
			NotesCollection notesCollection = notesRepository.findById(id).orElse(null);
			if (notesCollection != null) {
				ESNotesDocument esNotesDocument = new ESNotesDocument();
				BeanUtil.map(notesCollection, esNotesDocument);
				esClinicalNotesService.addNotes(esNotesDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkDiagrams(ObjectId id) {
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findById(id).orElse(null);
			if (diagramsCollection != null) {
				ESDiagramsDocument esDiagramsDocument = new ESDiagramsDocument();
				BeanUtil.map(diagramsCollection, esDiagramsDocument);
				esClinicalNotesService.addDiagrams(esDiagramsDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	@Transactional
	public void checkLocation(ObjectId resourceId) {
		try {
			LocationCollection locationCollection = locationRepository.findById(resourceId).orElse(null);
			if (locationCollection != null) {
				DoctorLocation doctorLocation = new DoctorLocation();
				BeanUtil.map(locationCollection, doctorLocation);
				doctorLocation.setLocationId(locationCollection.getId().toString());
				if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
					List<String> images = new ArrayList<String>();
					for (ClinicImage clinicImage : locationCollection.getImages()) {
						images.add(clinicImage.getImageUrl());
					}
					doctorLocation.setImages(images);
				}
				esRegistrationService.editLocation(doctorLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void checkDoctor(ObjectId resourceId, ObjectId locationId) {
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
			UserCollection userCollection = userRepository.findById(resourceId).orElse(null);
			if (doctorCollection != null && userCollection != null) {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
				if (locationId == null)
					doctorClinicProfileCollections = doctorClinicProfileRepository.findByDoctorId(resourceId);
				else {
					DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(resourceId, locationId);
					doctorClinicProfileCollections = new ArrayList<DoctorClinicProfileCollection>();
					doctorClinicProfileCollections.add(doctorClinicProfileCollection);
				}
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					LocationCollection locationCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(doctorClinicProfileCollection.getLocationId())) {
						locationCollection = locationRepository.findById(doctorClinicProfileCollection.getLocationId())
								.orElse(null);
					}
					GeoPoint geoPoint = null;

					ESDoctorDocument doctorDocument = new ESDoctorDocument();
					if (locationCollection != null) {
						if (locationCollection.getLatitude() != null && locationCollection.getLongitude() != null)
							geoPoint = new GeoPoint(locationCollection.getLatitude(),
									locationCollection.getLongitude());

						BeanUtil.map(locationCollection, doctorDocument);

						ESLocationDocument esLocationDocument = new ESLocationDocument();
						BeanUtil.map(doctorClinicProfileCollection, esLocationDocument);
						BeanUtil.map(locationCollection, esLocationDocument);

						if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
							List<String> images = new ArrayList<String>();
							for (ClinicImage clinicImage : locationCollection.getImages()) {
								images.add(clinicImage.getImageUrl());
							}
							doctorDocument.setImages(null);
							doctorDocument.setImages(images);

							esLocationDocument.setImages(null);
							esLocationDocument.setImages(images);

							doctorDocument.setClinicWorkingSchedules(null);
							doctorDocument.setClinicWorkingSchedules(locationCollection.getClinicWorkingSchedules());

							doctorDocument.setAlternateClinicNumbers(null);
							doctorDocument.setAlternateClinicNumbers(locationCollection.getAlternateClinicNumbers());

							esLocationDocument.setClinicWorkingSchedules(null);
							esLocationDocument
									.setClinicWorkingSchedules(locationCollection.getClinicWorkingSchedules());

							esLocationDocument.setAlternateClinicNumbers(null);
							esLocationDocument
									.setAlternateClinicNumbers(locationCollection.getAlternateClinicNumbers());

						}
						esLocationDocument.setGeoPoint(geoPoint);
						esLocationDocument.setId(locationCollection.getId().toString());
						esLocationRepository.save(esLocationDocument);

					}
					if (userCollection != null)
						BeanUtil.map(userCollection, doctorDocument);
					if (doctorCollection != null)
						BeanUtil.map(doctorCollection, doctorDocument);
					if (doctorClinicProfileCollection != null) {
						BeanUtil.map(doctorClinicProfileCollection, doctorDocument);
						doctorDocument.setAppointmentBookingNumber(null);
						doctorDocument.setAppointmentBookingNumber(
								doctorClinicProfileCollection.getAppointmentBookingNumber());
						doctorDocument.setWorkingSchedules(null);
						doctorDocument.setWorkingSchedules(doctorClinicProfileCollection.getWorkingSchedules());
					}
					if (locationCollection != null)
						doctorDocument.setLocationId(locationCollection.getId().toString());

					if (doctorCollection.getProfessionalMemberships() != null
							&& !doctorCollection.getProfessionalMemberships().isEmpty()) {
						List<String> professionalMemberships = (List<String>) CollectionUtils.collect(
								(Collection<?>) professionalMembershipRepository
										.findAllById(doctorCollection.getProfessionalMemberships()),
								new BeanToPropertyValueTransformer("membership"));
						doctorDocument.setProfessionalMemberships(professionalMemberships);
					}

					esRegistrationService.addDoctor(doctorDocument);
					// for city null issue listing off part
					if (locationCollection != null) {
						System.out.println("for listing");
						DoctorLocation doctorLocation = new DoctorLocation();
						BeanUtil.map(locationCollection, doctorLocation);
						doctorLocation.setLocationId(locationCollection.getId().toString());
						esRegistrationService.editLocation(doctorLocation);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	@Transactional
//	public void checkDoctor(ObjectId resourceId, ObjectId locationId) {
//		try {
//			DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
//			UserCollection userCollection = userRepository.findById(resourceId).orElse(null);
//
//			if (doctorCollection != null && userCollection != null) {
//				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = null;
//				if (locationId == null)
//					doctorClinicProfileCollections = doctorClinicProfileRepository
//							.findByDoctorIdAndIsActivateIsTrue(resourceId);
//				else {
//					DoctorClinicProfileCollection userLocationCollection = doctorClinicProfileRepository
//							.findByDoctorIdAndLocationId(resourceId, locationId);
//					doctorClinicProfileCollections = new ArrayList<DoctorClinicProfileCollection>();
//					doctorClinicProfileCollections.add(userLocationCollection);
//				}
//				for (DoctorClinicProfileCollection clinicProfileCollection : doctorClinicProfileCollections) {
//					LocationCollection locationCollection = locationRepository
//							.findById(clinicProfileCollection.getLocationId()).orElse(null);
//					GeoPoint geoPoint = null;
//					if (locationCollection.getLatitude() != null && locationCollection.getLongitude() != null)
//						geoPoint = new GeoPoint(locationCollection.getLatitude(), locationCollection.getLongitude());
//
//					ESDoctorDocument doctorDocument = new ESDoctorDocument();
//					if (locationCollection != null) {
//						BeanUtil.map(locationCollection, doctorDocument);
//						System.out.println("doctorCollection"+doctorCollection);
//						doctorDocument.setRegistrationImageUrl(doctorCollection.getRegistrationImageUrl());
//						doctorDocument.setRegistrationThumbnailUrl(doctorCollection.getRegistrationThumbnailUrl());
//						doctorDocument.setPhotoIdImageUrl(doctorCollection.getPhotoIdImageUrl());
//						doctorDocument.setIsRegistrationDetailsVerified(doctorCollection.getIsRegistrationDetailsVerified());
//						doctorDocument.setIsPhotoIdVerified(doctorCollection.getIsPhotoIdVerified());
//						//doctorDocument.setAchievements(doctorCollection.getAchievements());
//						//doctorDocument.setRegistrationDetails(doctorCollection.getRegistrationDetails());
//						//doctorDocument.setEducation(doctorCollection.getEducation());
//						System.out.println("doctorDocument"+doctorDocument);
//						ESLocationDocument esLocationDocument = new ESLocationDocument();
//						BeanUtil.map(clinicProfileCollection, esLocationDocument);
//						BeanUtil.map(locationCollection, esLocationDocument);
//						if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
//							List<String> images = new ArrayList<String>();
//							for (ClinicImage clinicImage : locationCollection.getImages()) {
//								images.add(clinicImage.getImageUrl());
//							}
//							doctorDocument.setImages(images);
//							esLocationDocument.setImages(images);
//						}
//						esLocationDocument.setGeoPoint(geoPoint);
//						esLocationDocument.setId(locationCollection.getId().toString());
//						esLocationRepository.save(esLocationDocument);
//					}
//					if (userCollection != null)
//						BeanUtil.map(userCollection, doctorDocument);
//					if (doctorCollection != null)
//						BeanUtil.map(doctorCollection, doctorDocument);
//					if (clinicProfileCollection != null)
//						BeanUtil.map(clinicProfileCollection, doctorDocument);
//					if (locationCollection != null) {
//						doctorDocument.setLocationId(locationCollection.getId().toString());
//						BeanUtil.map(locationCollection, doctorDocument);
//					}
//
//					if(doctorDocument.getRankingCount() == 0)doctorDocument.setRankingCount(1000);
//					esRegistrationService.addDoctor(doctorDocument);
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//		}
//	}

	@Override
	public Boolean mailDrugs() {
		Boolean response = false;
		try {
			if (isEnvProduction) {

				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("doctorId").exists(true).and("locationId").exists(true)
								.and("hospitalId").exists(true)),
						Aggregation.sort(new Sort(Direction.ASC, "createdTime")));
				AggregationResults<DrugCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
						DrugCollection.class, DrugCollection.class);

				List<DrugCollection> drugCollections = aggregationResults.getMappedResults();
				if (drugCollections != null && !drugCollections.isEmpty()) {
					boolean alreadyExists = new File(customDrugsFilePath).exists();

					CSVWriter csvOutput = new CSVWriter(new FileWriter(customDrugsFilePath, true));
					if (!alreadyExists) {
						String[] headers = { "DrugName", "Drug Type", "Drug Code", "Duration", "Dosage", "Dosage Time",
								"Direction", "Created By" };
						csvOutput.writeNext(headers);
					}

					for (DrugCollection drugCollection : drugCollections) {
						Integer drugCount = drugRepository.countByDrugCode(drugCollection.getDrugCode());
						/*
						 * if (drugCount != null && drugCount > 1) { break; }
						 */
						String[] data = { drugCollection.getDrugName(),
								drugCollection.getDrugType() != null ? drugCollection.getDrugType().getType() : null,
								drugCollection.getDrugCode(), drugCollection.getDuration() + "",
								drugCollection.getDosage(), drugCollection.getDosageTime() + "",
								drugCollection.getCreatedBy() };
						csvOutput.writeNext(data);
					}
					csvOutput.close();
					FileSystemResource fileSystemResource = new FileSystemResource(customDrugsFilePath);
					MailAttachment mailAttachment = new MailAttachment();
					mailAttachment.setFileSystemResource(fileSystemResource);
					mailAttachment.setAttachmentName("customDrugs.csv");

					MailResponse mailResponse = new MailResponse();
					mailResponse.setMailAttachment(mailAttachment);
					mailService.sendEmail("parag.pakhale@healthcoco.com", "Custom Drug list", "Hello All",
							mailResponse.getMailAttachment());
					fileSystemResource.getFile().delete();
					response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public void checkPharmacy(ObjectId resourceId) {
		try {
			LocaleCollection localeCollection = localeRepository.findById(resourceId).orElse(null);
			UserCollection userCollection = null;
			if (localeCollection != null) {
				userCollection = userRepository.findByMobileNumberAndUserState(localeCollection.getContactNumber(),
						"PHARMACY");
			}

			if (localeCollection != null && userCollection != null) {
				ESUserLocaleDocument esUserLocaleDocument = new ESUserLocaleDocument();
				BeanUtil.map(userCollection, esUserLocaleDocument);
				BeanUtil.map(localeCollection, esUserLocaleDocument);
				esUserLocaleDocument.setLocaleId(localeCollection.getId().toString());
				esRegistrationService.addLocale(esUserLocaleDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

}
