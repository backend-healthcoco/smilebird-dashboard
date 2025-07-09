package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.XRayDetails;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.ECGDetailsCollection;
import com.dpdocter.collections.EarsExaminationCollection;
import com.dpdocter.collections.EchoCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.GeneralExamCollection;
import com.dpdocter.collections.HolterCollection;
import com.dpdocter.collections.IndicationOfUSGCollection;
import com.dpdocter.collections.IndirectLarygoscopyExaminationCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MenstrualHistoryCollection;
import com.dpdocter.collections.NeckExaminationCollection;
import com.dpdocter.collections.NoseExaminationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.ObstetricHistoryCollection;
import com.dpdocter.collections.OralCavityAndThroatExaminationCollection;
import com.dpdocter.collections.PACollection;
import com.dpdocter.collections.PSCollection;
import com.dpdocter.collections.PVCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PresentComplaintCollection;
import com.dpdocter.collections.PresentComplaintHistoryCollection;
import com.dpdocter.collections.PresentingComplaintEarsCollection;
import com.dpdocter.collections.PresentingComplaintNoseCollection;
import com.dpdocter.collections.PresentingComplaintOralCavityCollection;
import com.dpdocter.collections.PresentingComplaintThroatCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProcedureNoteCollection;
import com.dpdocter.collections.ProvisionalDiagnosisCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.SystemExamCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.XRayDetailsCollection;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.ECGDetailsRepository;
import com.dpdocter.repository.EarsExaminationRepository;
import com.dpdocter.repository.EchoRepository;
import com.dpdocter.repository.GeneralExamRepository;
import com.dpdocter.repository.HolterRepository;
import com.dpdocter.repository.IndicationOfUSGRepository;
import com.dpdocter.repository.IndirectLarygoscopyExaminationRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MenstrualHistoryRepository;
import com.dpdocter.repository.NeckExaminationRepository;
import com.dpdocter.repository.NoseExaminationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.ObstetricHistoryRepository;
import com.dpdocter.repository.OralCavityThroatExaminationRepository;
import com.dpdocter.repository.PARepository;
import com.dpdocter.repository.PSRepository;
import com.dpdocter.repository.PVRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PresentComplaintHistoryRepository;
import com.dpdocter.repository.PresentComplaintRepository;
import com.dpdocter.repository.PresentingComplaintEarsRepository;
import com.dpdocter.repository.PresentingComplaintNosesRepository;
import com.dpdocter.repository.PresentingComplaintOralCavityRepository;
import com.dpdocter.repository.PresentingComplaintThroatRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProcedureNoteRepository;
import com.dpdocter.repository.ProvisionalDiagnosisRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SystemExamRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.XRayDetailsRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

	private static Logger logger = LogManager.getLogger(ClinicalNotesServiceImpl.class.getName());

	@Autowired
	private ProvisionalDiagnosisRepository provisionalDiagnosisRepository;

	@Autowired
	private PresentComplaintRepository presentComplaintRepository;

	@Autowired
	private PresentComplaintHistoryRepository presentComplaintHistoryRepository;

	@Autowired
	private GeneralExamRepository generalExamRepository;

	@Autowired
	private SystemExamRepository systemExamRepository;

	@Autowired
	private MenstrualHistoryRepository menstrualHistoryRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

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
	private FileManager fileManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObstetricHistoryRepository obstetricHistoryRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PSRepository psRepository;

	@Autowired
	private PARepository paRepository;

	@Autowired
	private PVRepository pvRepository;

	@Autowired
	private ProcedureNoteRepository procedureNoteRepository;

	@Autowired
	private IndicationOfUSGRepository indicationOfUSGRepository;

	@Autowired
	private XRayDetailsRepository xRayDetailsRepository;

	@Autowired
	private ECGDetailsRepository ecgDetailsRepository;

	@Autowired
	private EchoRepository echoRepository;

	@Autowired
	private HolterRepository holterRepository;

	@Autowired
	private PresentingComplaintNosesRepository presentingComplaintNotesRepository;

	@Autowired
	private PresentingComplaintEarsRepository presentingComplaintEarsRepository;

	@Autowired
	private PresentingComplaintOralCavityRepository presentingComplaintOralCavityRepository;

	@Autowired
	private PresentingComplaintThroatRepository presentingComplaintThroatRepository;

	@Autowired
	private NoseExaminationRepository noseExaminationRepository;

	@Autowired
	private EarsExaminationRepository earsExaminationRepository;

	@Autowired
	private NeckExaminationRepository neckExaminationRepository;

	@Autowired
	private OralCavityThroatExaminationRepository oralCavityThroatExaminationRepository;

	@Autowired
	private IndirectLarygoscopyExaminationRepository indirectLarygoscopyExaminationRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${ClinicalNotes.getPatientsClinicalNotesWithVerifiedOTP}")
	private String getPatientsClinicalNotesWithVerifiedOTP;

	public ClinicalNotes getClinicalNote(ClinicalNotesCollection clinicalNotesCollection) {
		ClinicalNotes clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
		if (clinicalNotesCollection.getComplaints() != null && !clinicalNotesCollection.getComplaints().isEmpty())
			clinicalNote.setComplaints(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getComplaints()))),
					ComplaintCollection.class, Complaint.class).getMappedResults());
		if (clinicalNotesCollection.getInvestigations() != null
				&& !clinicalNotesCollection.getInvestigations().isEmpty())
			clinicalNote.setInvestigations(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getInvestigations()))),
					InvestigationCollection.class, Investigation.class).getMappedResults());
		if (clinicalNotesCollection.getObservations() != null && !clinicalNotesCollection.getObservations().isEmpty())
			clinicalNote.setObservations(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getObservations()))),
					ObservationCollection.class, Observation.class).getMappedResults());
		if (clinicalNotesCollection.getDiagnoses() != null && !clinicalNotesCollection.getDiagnoses().isEmpty())
			clinicalNote.setDiagnoses(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagnoses()))),
					DiagnosisCollection.class, Diagnoses.class).getMappedResults());
		if (clinicalNotesCollection.getNotes() != null && !clinicalNotesCollection.getNotes().isEmpty())
			clinicalNote.setNotes(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getNotes()))),
					NotesCollection.class, Notes.class).getMappedResults());
		if (clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
			clinicalNote.setDiagrams(mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagrams()))),
					DiagramsCollection.class, Diagram.class).getMappedResults());

		PatientVisitCollection patientVisitCollection = patientVisitRepository
				.findByClinicalNotesId(clinicalNotesCollection.getId());
		if (patientVisitCollection != null)
			clinicalNote.setVisitId(patientVisitCollection.getId().toString());

		return clinicalNote;
	}

	@Override
	@Transactional
	public Complaint addEditComplaint(Complaint complaint) {
		try {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			if (DPDoctorUtils.anyStringEmpty(complaintCollection.getId())) {
				complaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(complaintCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						complaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					complaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				ComplaintCollection oldComplaintCollection = complaintRepository.findById(complaintCollection.getId()).orElse(null);
				complaintCollection.setCreatedBy(oldComplaintCollection.getCreatedBy());
				complaintCollection.setCreatedTime(oldComplaintCollection.getCreatedTime());
				complaintCollection.setDiscarded(oldComplaintCollection.getDiscarded());
			}
			complaintCollection = complaintRepository.save(complaintCollection);

			BeanUtil.map(complaintCollection, complaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return complaint;
	}

	@Override
	@Transactional
	public Observation addEditObservation(Observation observation) {
		try {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			if (DPDoctorUtils.anyStringEmpty(observationCollection.getId())) {
				observationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(observationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						observationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					observationCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObservationCollection oldObservationCollection = observationRepository
						.findById(observationCollection.getId()).orElse(null);
				observationCollection.setCreatedBy(oldObservationCollection.getCreatedBy());
				observationCollection.setCreatedTime(oldObservationCollection.getCreatedTime());
				observationCollection.setDiscarded(oldObservationCollection.getDiscarded());
			}
			observationCollection = observationRepository.save(observationCollection);

			BeanUtil.map(observationCollection, observation);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return observation;
	}

	@Override
	@Transactional
	public Investigation addEditInvestigation(Investigation investigation) {
		try {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			if (DPDoctorUtils.anyStringEmpty(investigationCollection.getId())) {
				investigationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(investigationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(investigationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						investigationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					investigationCollection.setCreatedBy("ADMIN");
				}
			} else {
				InvestigationCollection oldInvestigationCollection = investigationRepository
						.findById(investigationCollection.getId()).orElse(null);
				investigationCollection.setCreatedBy(oldInvestigationCollection.getCreatedBy());
				investigationCollection.setCreatedTime(oldInvestigationCollection.getCreatedTime());
				investigationCollection.setDiscarded(oldInvestigationCollection.getDiscarded());
			}
			investigationCollection = investigationRepository.save(investigationCollection);

			BeanUtil.map(investigationCollection, investigation);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return investigation;
	}

	@Override
	@Transactional
	public Diagnoses addEditDiagnosis(Diagnoses diagnosis) {
		try {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(diagnosisCollection.getId())) {
				diagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(diagnosisCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						diagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosisCollection oldDiagnosisCollection = diagnosisRepository.findById(diagnosisCollection.getId()).orElse(null);
				diagnosisCollection.setCreatedBy(oldDiagnosisCollection.getCreatedBy());
				diagnosisCollection.setCreatedTime(oldDiagnosisCollection.getCreatedTime());
				diagnosisCollection.setDiscarded(oldDiagnosisCollection.getDiscarded());
			}
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);

			BeanUtil.map(diagnosisCollection, diagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagnosis;
	}

	@Override
	@Transactional
	public Notes addEditNotes(Notes notes) {
		try {
			NotesCollection notesCollection = new NotesCollection();
			BeanUtil.map(notes, notesCollection);
			if (DPDoctorUtils.anyStringEmpty(notesCollection.getId())) {
				notesCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(notesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(notesCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						notesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					notesCollection.setCreatedBy("ADMIN");
				}
			} else {
				NotesCollection oldNotesCollection = notesRepository.findById(notesCollection.getId()).orElse(null);
				notesCollection.setCreatedBy(oldNotesCollection.getCreatedBy());
				notesCollection.setCreatedTime(oldNotesCollection.getCreatedTime());
				notesCollection.setDiscarded(oldNotesCollection.getDiscarded());
			}
			notesCollection = notesRepository.save(notesCollection);

			BeanUtil.map(notesCollection, notes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return notes;
	}

	@Override
	@Transactional
	public Diagram addEditDiagram(Diagram diagram) {
		try {
			if (diagram.getDiagram() != null) {
				String path = "clinicalNotes" + File.separator + "diagrams";
				diagram.getDiagram().setFileName(diagram.getDiagram().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path,
						false);
				diagram.setDiagramUrl(imageURLResponse.getImageUrl());

			}
			DiagramsCollection diagramsCollection = new DiagramsCollection();
			BeanUtil.map(diagram, diagramsCollection);
			if (DPDoctorUtils.allStringsEmpty(diagram.getDoctorId()))
				diagramsCollection.setDoctorId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getLocationId()))
				diagramsCollection.setLocationId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getHospitalId()))
				diagramsCollection.setHospitalId(null);

			if (DPDoctorUtils.anyStringEmpty(diagramsCollection.getId())) {
				diagramsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagramsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(diagramsCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						diagramsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagramsCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagramsCollection oldDiagramsCollection = diagramsRepository.findById(diagramsCollection.getId()).orElse(null);
				diagramsCollection.setCreatedBy(oldDiagramsCollection.getCreatedBy());
				diagramsCollection.setCreatedTime(oldDiagramsCollection.getCreatedTime());
				diagramsCollection.setDiscarded(oldDiagramsCollection.getDiscarded());
				if (diagram.getDiagram() == null) {
					diagramsCollection.setDiagramUrl(oldDiagramsCollection.getDiagramUrl());
					diagramsCollection.setFileExtension(oldDiagramsCollection.getFileExtension());
				}
			}
			diagramsCollection = diagramsRepository.save(diagramsCollection);
			BeanUtil.map(diagramsCollection, diagram);
			diagram.setDiagram(null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

	@Override
	@Transactional
	public Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Complaint response = null;
		try {
			ComplaintCollection complaintCollection = complaintRepository.findById(new ObjectId(id)).orElse(null);
			if (complaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId(),
						complaintCollection.getHospitalId(), complaintCollection.getLocationId())) {
					if (complaintCollection.getDoctorId().toString().equals(doctorId)
							&& complaintCollection.getHospitalId().toString().equals(hospitalId)
							&& complaintCollection.getLocationId().toString().equals(locationId)) {

						complaintCollection.setDiscarded(discarded);
						complaintCollection.setUpdatedTime(new Date());
						complaintRepository.save(complaintCollection);
						response = new Complaint();
						BeanUtil.map(complaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					complaintCollection.setDiscarded(discarded);
					complaintCollection.setUpdatedTime(new Date());
					complaintRepository.save(complaintCollection);
					response = new Complaint();
					BeanUtil.map(complaintCollection, response);
				}
			} else {
				logger.warn("Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Complaint not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
//changed ritesh
	@Override
	@Transactional
	public Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Observation response = null;
		try {
			ObservationCollection observationCollection = observationRepository.findById(new ObjectId(id)).orElse(null);
			if (observationCollection != null) {
				if (doctorId == null && locationId == null
						&& hospitalId == null)  {
					if (observationCollection.getDoctorId().toString().equals(doctorId)
							&& observationCollection.getHospitalId().toString().equals(hospitalId)
							&& observationCollection.getLocationId().toString().equals(locationId)) {
						observationCollection.setDiscarded(true);
						observationCollection.setUpdatedTime(new Date());
						observationRepository.save(observationCollection);
						response = new Observation();
						BeanUtil.map(observationCollection, response);
					
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					observationCollection.setDiscarded(true);
					observationCollection.setUpdatedTime(new Date());
					observationRepository.save(observationCollection);
					response = new Observation();
					BeanUtil.map(observationCollection, response);
				}
			} else {
				logger.warn("Observation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Observation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
//change end
	@Override
	@Transactional
	public Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Investigation response = null;
		try {
			InvestigationCollection investigationCollection = investigationRepository.findById(new ObjectId(id)).orElse(null);
			if (investigationCollection != null) {
				if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
						&& investigationCollection.getLocationId() != null) {
					if (investigationCollection.getDoctorId().toString().equals(doctorId)
							&& investigationCollection.getHospitalId().toString().equals(hospitalId)
							&& investigationCollection.getLocationId().toString().equals(locationId)) {
						investigationCollection.setDiscarded(discarded);
						investigationCollection.setUpdatedTime(new Date());
						investigationRepository.save(investigationCollection);
						response = new Investigation();
						BeanUtil.map(investigationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					investigationCollection.setDiscarded(discarded);
					investigationCollection.setUpdatedTime(new Date());
					investigationRepository.save(investigationCollection);
					response = new Investigation();
					BeanUtil.map(investigationCollection, response);
				}
			} else {
				logger.warn("Investigation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Investigation not found!");
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
	public Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Diagnoses response = null;
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findById(new ObjectId(id)).orElse(null);
			if (diagnosisCollection != null) {
				if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null
						&& diagnosisCollection.getLocationId() != null) {
					if (diagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& diagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& diagnosisCollection.getLocationId().toString().equals(locationId)) {
						diagnosisCollection.setDiscarded(discarded);
						diagnosisCollection.setUpdatedTime(new Date());
						diagnosisRepository.save(diagnosisCollection);
						response = new Diagnoses();
						BeanUtil.map(diagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagnosisCollection.setDiscarded(discarded);
					diagnosisCollection.setUpdatedTime(new Date());
					diagnosisRepository.save(diagnosisCollection);
					response = new Diagnoses();
					BeanUtil.map(diagnosisCollection, response);
				}

			} else {
				logger.warn("Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagnosis not found!");
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
	public Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Notes response = null;
		try {
			NotesCollection notesCollection = notesRepository.findById(new ObjectId(id)).orElse(null);
			if (notesCollection != null) {
				if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null
						&& notesCollection.getLocationId() != null) {
					if (notesCollection.getDoctorId().toString().equals(doctorId)
							&& notesCollection.getHospitalId().toString().equals(hospitalId)
							&& notesCollection.getLocationId().toString().equals(locationId)) {
						notesCollection.setDiscarded(discarded);
						notesCollection.setUpdatedTime(new Date());
						notesRepository.save(notesCollection);
						response = new Notes();
						BeanUtil.map(notesCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					notesCollection.setDiscarded(discarded);
					notesCollection.setUpdatedTime(new Date());
					notesRepository.save(notesCollection);
					response = new Notes();
					BeanUtil.map(notesCollection, response);
				}

			} else {
				logger.warn("Notes not found!");
				throw new BusinessException(ServiceError.NoRecord, "Notes not found!");
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
	public Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Diagram response = null;
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findById(new ObjectId(id)).orElse(null);
			if (diagramsCollection != null) {
				if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null
						&& diagramsCollection.getLocationId() != null) {
					if (diagramsCollection.getDoctorId().toString().equals(doctorId)
							&& diagramsCollection.getHospitalId().toString().equals(hospitalId)
							&& diagramsCollection.getLocationId().toString().equals(locationId)) {
						diagramsCollection.setDiscarded(discarded);
						diagramsCollection.setUpdatedTime(new Date());
						diagramsRepository.save(diagramsCollection);
						response = new Diagram();
						BeanUtil.map(diagramsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagramsCollection.setDiscarded(discarded);
					diagramsCollection.setUpdatedTime(new Date());
					diagramsRepository.save(diagramsCollection);
					response = new Diagram();
					BeanUtil.map(diagramsCollection, response);
				}

			} else {
				logger.warn("Diagram not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagram not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Transactional

	public Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId,
			boolean isOTPVerified) {
		Integer clinicalNotesCount = 0;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (isOTPVerified)
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(patientObjectId, false);
			else
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCount(doctorObjectId, patientObjectId,
						hospitalObjectId, locationObjectId, false);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
		}
		return clinicalNotesCount;
	}

	@Transactional

	public List<?> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm,
			String speciality) {
		List<?> response = new ArrayList<Object>();

		switch (ClinicalItems.valueOf(type.toUpperCase())) {

		case COMPLAINTS: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalComplaintsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case INVESTIGATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalInvestigationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case OBSERVATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalObservationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalObservations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomObservationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalObservationsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case NOTES: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalNotesForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalNotes(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomNotesForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalNotesForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			}
			break;
		}
		case DIAGRAMS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalDiagramsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalPresentComplaintForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalPresentComplaint(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomPresentComplaintForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else

					response = getCustomPresentComplaint(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalPresentComplaintForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else

					response = getCustomGlobalPresentComplaint(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case HISTORY_OF_PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalPresentComplaintHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalPresentComplaintHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomPresentComplaintHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomPresentComplaintHistory(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalPresentComplaintHistoryForAdmin(page, size, updatedTime, discarded,
							searchTerm, speciality);
				else

					response = getCustomGlobalPresentComplaintHistory(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PROVISIONAL_DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalProvisionalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalProvisionalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomProvisionalDiagnosisForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalProvisionalDiagnosisForAdmin(page, size, updatedTime, discarded,
							searchTerm, speciality);
				else
					response = getCustomGlobalProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case GENERAL_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalGeneralExamForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalGeneralExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomGeneralExamForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalGeneralExamForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}

		case SYSTEMIC_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalSystemExamForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalSystemExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomSystemExamForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else

					response = getCustomSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalSystemExamForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}

		case MENSTRUAL_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalMenstrualHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalMenstrualHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomMenstrualHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else

					response = getCustomMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalMenstrualHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalMenstrualHistory(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case OBSTETRIC_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalObstetricHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalObstetricHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomObstetricHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalObstetricHistoryForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalObstetricHistory(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case INDICATION_OF_USG: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalIndicationOfUSGForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalIndicationOfUSG(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomIndicationOfUSGForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalIndicationOfUSGForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PA: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalPAForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalPA(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomPAForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalPAForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGlobalPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PV: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalPVForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalPV(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomPVForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalPVForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGlobalPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case PS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalPSForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalPS(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomPSForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalPSForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGlobalPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
		}
		case ECG: {
			switch (Range.valueOf(range.toUpperCase())) {
			case GLOBAL:
				if (isAdmin)
					response = getGlobalECGDetailsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalECGDetails(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomECGDetailsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalECGDetailsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}
		case XRAY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalXRayDetailsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalXRayDetails(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomXRayDetailsForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalXRayDetailsForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}
		case ECHO: {
			switch (Range.valueOf(range.toUpperCase())) {
			case GLOBAL:
				if (isAdmin)
					response = getGlobalEchoForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalEcho(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomEchoForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomEcho(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalEchoForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomGlobalEcho(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;
		}
		case HOLTER: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalHolterForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getGlobalHolter(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomHolterForAdmin(page, size, updatedTime, discarded, searchTerm, speciality);
				else
					response = getCustomHolter(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalHolterForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalHolter(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;

		}
		case PROCEDURE_NOTE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				if (isAdmin)
					response = getGlobalProcedureNoteForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getGlobalProcedureNote(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				if (isAdmin)
					response = getCustomProcedureNoteForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			case BOTH:
				if (isAdmin)
					response = getCustomGlobalProcedureNoteForAdmin(page, size, updatedTime, discarded, searchTerm,
							speciality);
				else
					response = getCustomGlobalProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded);
				break;
			default:
				break;
			}
			break;

		}
		case PC_NOSE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCNOse(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCNose(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCNOse(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_EARS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCEars(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_THROAT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCThroat(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case NECK_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNeckExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case NOSE_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNoseExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_ORAL_CAVITY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCOralCavity(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case EARS_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalEarsExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case ORAL_CAVITY_THROAT_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalOralCavityAndThroat(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomOralCavityAndThroatExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalOralCavityAndThroatExam(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case INDIRECT_LAGYROSCOPY_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalIndirectLarygoscopyExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomIndirectLarygoscopyExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalIndirectLarygoscopyExam(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Complaint> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = new ArrayList<Complaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Complaint> getGlobalComplaints(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Complaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), ComplaintCollection.class,
					Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private List<Complaint> getCustomComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = new ArrayList<Investigation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getGlobalInvestigations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Investigation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Investigation> getCustomInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			AggregationResults<Investigation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null),
							InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Observation> getCustomGlobalObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = new ArrayList<Observation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Observation> getGlobalObservations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Observation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<Observation> getCustomObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = null;
		try {
			AggregationResults<Observation> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null), Observation.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = new ArrayList<Diagnoses>();

		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), DiagnosisCollection.class,
					Diagnoses.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<Diagnoses> getCustomDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Notes> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = new ArrayList<Notes>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Notes> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities),
							NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Notes> getGlobalNotes(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<Notes> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null), NotesCollection.class,
					Notes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagram> response = new ArrayList<Diagram>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagram> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities),
							DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagram> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), DiagramsCollection.class,
					Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<Diagram> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Diagram> response = null;
		try {
			AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null),
					DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@Transactional
	public void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Clinical Notes", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you a Clinical Notes", body,
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

	@Transactional
	public MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId,
			String hospitalId) {
		return createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		ClinicalNotesCollection clinicalNotesCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			clinicalNotesCollection = clinicalNotesRepository.findById(new ObjectId(clinicalNotesId)).orElse(null);
			if (clinicalNotesCollection != null) {
				if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
						&& clinicalNotesCollection.getLocationId() != null) {
					if (clinicalNotesCollection.getDoctorId().equals(doctorId)
							&& clinicalNotesCollection.getHospitalId().equals(hospitalId)
							&& clinicalNotesCollection.getLocationId().equals(locationId)) {

						user = userRepository.findById(clinicalNotesCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
								clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(),
								clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());

						emailTrackCollection.setDoctorId(new ObjectId(doctorId));
						emailTrackCollection.setHospitalId(new ObjectId(hospitalId));
						emailTrackCollection.setLocationId(new ObjectId(locationId));
						emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
						emailTrackCollection.setSubject("Clinical Notes");
						if (user != null) {
							emailTrackCollection.setPatientName(user.getFirstName());
							emailTrackCollection.setPatientId(user.getId());
						}
						JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient,
								user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);

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
						response.setMailRecordCreatedDate(sdf.format(clinicalNotesCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());

						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Clinical Notes Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Clinical Notes Id, doctorId, location Id, hospital Id does not match");
					}
				}
			} else {
				logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
				throw new BusinessException(ServiceError.NotFound,
						"Clinical Notes not found. Please check clinicalNotesId.");
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
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

	@Transactional
	public List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		List<ClinicalNotesCollection> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
		
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			
			long createdTimestamp = Long.parseLong(updatedTime);
			ObjectId patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);

			if (size > 0)
				clinicalNotesCollections = clinicalNotesRepository.findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(patientObjectId,
						new Date(createdTimestamp), discards,
						PageRequest.of(page, size, Direction.DESC, "createdTime"));
			else
				clinicalNotesCollections = clinicalNotesRepository.findByPatientIdAndUpdatedTimeGreaterThanAndDiscardedIn(patientObjectId,
						new Date(createdTimestamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));

			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	public String getClinicalNotesFile(String clinicalNotesId) {
		String response = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository
					.findById(new ObjectId(clinicalNotesId)).orElse(null);

			if (clinicalNotesCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
						clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(),
						clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId());
				UserCollection user = userRepository.findById(clinicalNotesCollection.getPatientId()).orElse(null);

				JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user);
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

	private JasperReportResponse createJasper(ClinicalNotesCollection clinicalNotesCollection,
			PatientCollection patient, UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String observations = "";
		for (ObjectId observationId : clinicalNotesCollection.getObservations()) {
			ObservationCollection observationCollection = observationRepository.findById(observationId).orElse(null);
			if (observationCollection != null) {
				if (observations.isEmpty())
					observations = observationCollection.getObservation();
				else
					observations = observations + ", " + observationCollection.getObservation();
			}
		}
		parameters.put("observationIds", observations);

		String notes = "";
		for (ObjectId noteId : clinicalNotesCollection.getNotes()) {
			NotesCollection note = notesRepository.findById(noteId).orElse(null);
			if (note != null) {
				if (notes.isEmpty())
					notes = note.getNote();
				else
					notes = notes + ", " + note.getNote();
			}
		}
		parameters.put("noteIds", notes);

		String investigations = "";
		for (ObjectId investigationId : clinicalNotesCollection.getInvestigations()) {
			InvestigationCollection investigation = investigationRepository.findById(investigationId).orElse(null);
			if (investigation != null) {
				if (investigations.isEmpty())
					investigations = investigation.getInvestigation();
				else
					investigations = investigations + ", " + investigation.getInvestigation();
			}
		}
		parameters.put("investigationIds", investigations);

		String diagnosis = "";
		for (ObjectId diagnosisId : clinicalNotesCollection.getDiagnoses()) {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findById(diagnosisId).orElse(null);
			if (diagnosisCollection != null) {
				if (diagnosis.isEmpty())
					diagnosis = diagnosisCollection.getDiagnosis();
				else
					diagnosis = diagnosis + ", " + diagnosisCollection.getDiagnosis();
			}
		}
		parameters.put("diagnosesIds", diagnosis);

		String complaints = "";
		for (ObjectId complaintId : clinicalNotesCollection.getComplaints()) {
			ComplaintCollection complaint = complaintRepository.findById(complaintId).orElse(null);
			if (complaint != null) {
				if (complaints.isEmpty())
					complaints = complaint.getComplaint();
				else
					complaints = complaints + ", " + complaint.getComplaint();
			}
		}
		parameters.put("complaintIds", complaints);

		List<DBObject> diagramIds = new ArrayList<DBObject>();
		if (clinicalNotesCollection.getDiagrams() != null)
			for (ObjectId diagramId : clinicalNotesCollection.getDiagrams()) {
				DBObject diagram = new BasicDBObject();
				DiagramsCollection diagramsCollection = diagramsRepository.findById(diagramId).orElse(null);
				if (diagramsCollection != null) {
					if (diagramsCollection.getDiagramUrl() != null) {
						diagram.put("url", getFinalImageURL(diagramsCollection.getDiagramUrl()));
					}
					diagram.put("tags", diagramsCollection.getTags());
					diagramIds.add(diagram);
				}
			}
		if (!diagramIds.isEmpty())
			parameters.put("diagramIds", diagramIds);
		else
			parameters.put("diagramIds", null);

		parameters.put("clinicalNotesId", clinicalNotesCollection.getId().toString());
		if (clinicalNotesCollection.getVitalSigns() != null) {
			String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
			pulse = "Pulse: "
					+ (pulse != null && !pulse.isEmpty() ? pulse + " " + VitalSignsUnit.PULSE.getUnit() + "    "
							: "--    ");

			String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
			temp = "Temperature: "
					+ (temp != null && !temp.isEmpty() ? temp + " " + VitalSignsUnit.TEMPERATURE.getUnit() + "    "
							: "--    ");

			String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
			breathing = "Breathing: " + (breathing != null && !breathing.isEmpty()
					? breathing + " " + VitalSignsUnit.BREATHING.getUnit() + "    "
					: "--    ");

			String weight = clinicalNotesCollection.getVitalSigns().getWeight();
			weight = "Weight: "
					+ (weight != null && !weight.isEmpty() ? weight + " " + VitalSignsUnit.WEIGHT.getUnit() + "    "
							: "--    ");

			String bloodPressure = "";
			if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

				String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
				diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

				bloodPressure = "Blood Pressure: " + systolic + "/" + diastolic + " "
						+ VitalSignsUnit.BLOODPRESSURE.getUnit() + "    ";
			} else {
				bloodPressure = "Blood Pressure: --    ";
			}
			String vitalSigns = pulse + temp + breathing + bloodPressure + weight;
			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		} else
			parameters.put("vitalSigns", null);

		String patientName = "", dob = "", bloodGroup = "", gender = "", mobileNumber = "", refferedBy = "", pid = "",
				date = "", resourceId = "", logoURL = "";
		if (patient.getReferredBy() != null) {
			ReferencesCollection referencesCollection = referenceRepository.findById(patient.getReferredBy()).orElse(null);
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
		resourceId = "CID: "
				+ (clinicalNotesCollection.getUniqueEmrId() != null ? clinicalNotesCollection.getUniqueEmrId() : "--")
				+ "<br>";
		PrintSettingsCollection printSettings = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(
				clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(),
				clinicalNotesCollection.getHospitalId(), ComponentType.CLINICAL_NOTES.getType());

		if (printSettings == null) {
			printSettings = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(clinicalNotesCollection.getDoctorId(),
					clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(),
					ComponentType.ALL.getType());
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
								headerLeftText = "<span style='font-size:" + str.getFontSize() + ";'>" + text
										+ "</span>";
							else
								headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}

				if (printSettings.getHeaderSetup().getTopRightText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {
						if ((str.getFontSize() != null) && str.getFontSize().equalsIgnoreCase("10pt")
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
				if (printSettings.getFooterSetup().getCustomFooter())
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
							footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize()
									+ "'>" + text + "</span>";
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
				bloodGroup = "<span style='font-size:" + fontSize + "'>" + bloodGroup + "</span>";
				dob = "<span style='font-size:" + fontSize + "'>" + dob + "</span>";
				gender = "<span style='font-size:" + fontSize + "'>" + gender + "</span>";
				mobileNumber = "<span style='font-size:" + fontSize + "'>" + mobileNumber + "</span>";
				refferedBy = "<span style='font-size:" + fontSize + "'>" + refferedBy + "</span>";
				date = "<span style='font-size:" + fontSize + "'>" + date + "</span>";
				resourceId = "<span style='font-size:" + fontSize + "'>" + resourceId + "</span>";
			}
		}

		UserCollection doctorUser = userRepository.findById(clinicalNotesCollection.getDoctorId()).orElse(null);
		if (doctorUser != null)
			parameters.put("footerSignature", doctorUser.getTitle() + " " + doctorUser.getFirstName());

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

		String pdfName = (user != null ? user.getFirstName() : "") + "CLINICALNOTES-"
				+ clinicalNotesCollection.getUniqueEmrId();
		response = jasperReportService.createPDF(parameters, "mongo-clinical-notes", layout, pageSize, margins,
				pdfName.replaceAll("\\s+", ""));

		return response;
	}

	private List<Complaint> getCustomGlobalComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "complaint", speciality, null, null),
							ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	private List<Complaint> getGlobalComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "complaint", speciality, null, null),
							ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private List<Complaint> getCustomComplaintsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "complaint", speciality, null, null),
							ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private List<Investigation> getCustomGlobalInvestigationsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<Investigation> response = null;
		try {
			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"investigation", speciality, null, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Investigation> getGlobalInvestigationsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<Investigation> response = null;

		try {
			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"investigation", speciality, null, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Investigation> getCustomInvestigationsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<Investigation> response = null;
		try {
			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"investigation", speciality, null, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Observation> getCustomGlobalObservationsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<Observation> response = null;
		try {
			AggregationResults<Observation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "observation", speciality, null, null),
							ObservationCollection.class, Observation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	private List<Observation> getGlobalObservationsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Observation> response = null;
		try {

			AggregationResults<Observation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "observation", speciality, null, null),
							ObservationCollection.class, Observation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<Observation> getCustomObservationsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Observation> response = null;

		try {
			AggregationResults<Observation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "observation", speciality, null, null),
							ObservationCollection.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<Diagnoses> getCustomGlobalDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "diagnosis", speciality, null, null),
							DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;

	}

	private List<Diagnoses> getGlobalDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "diagnosis", speciality, null, null),
							DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<Diagnoses> getCustomDiagnosisForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "diagnosis", speciality, null, null),
							DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<Notes> getCustomGlobalNotesForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "note", speciality, null, null), NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	private List<Notes> getGlobalNotesForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate
					.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "note", speciality, null, null), NotesCollection.class, Notes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<Notes> getCustomNotesForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "note", speciality, null, null), NotesCollection.class, Notes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<Diagram> getCustomGlobalDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagram> response = null;
		try {
			AggregationResults<Diagram> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "tags", speciality, null, null),
							DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;

	}

	private List<Diagram> getGlobalDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagram> response = null;
		try {
			AggregationResults<Diagram> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "tags", speciality, null, null),
							DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<Diagram> getCustomDiagramsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Diagram> response = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			AggregationResults<Diagram> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "tags", speciality, null, null),
							DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<PresentComplaint> getGlobalPresentComplaint(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	private List<PresentComplaint> getCustomPresentComplaint(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			AggregationResults<PresentComplaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaint> getCustomGlobalPresentComplaint(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = new ArrayList<PresentComplaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getCustomGlobalPresentComplaintHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = new ArrayList<PresentComplaintHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getGlobalPresentComplaintHistory(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}

	private List<PresentComplaintHistory> getCustomPresentComplaintHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getCustomGlobalProvisionalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = new ArrayList<ProvisionalDiagnosis>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getGlobalProvisionalDiagnosis(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getCustomProvisionalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<SystemExam> getCustomGlobalSystemExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<SystemExam> response = new ArrayList<SystemExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<SystemExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<SystemExam> getGlobalSystemExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<SystemExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<SystemExam> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), SystemExamCollection.class,
					SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}

	private List<SystemExam> getCustomSystemExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<SystemExam> response = null;
		try {
			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getGlobalMenstrualHistory(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<MenstrualHistory> getCustomMenstrualHistory(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getCustomGlobalObstetricHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = new ArrayList<ObstetricHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getGlobalObstetricHistory(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<ObstetricHistory> getCustomObstetricHistory(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getCustomGlobalIndicationOfUSG(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = new ArrayList<IndicationOfUSG>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication Of USG");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getGlobalIndicationOfUSG(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	private List<IndicationOfUSG> getCustomIndicationOfUSG(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PS> getGlobalPS(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PS> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PSCollection.class, PS.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;
	}

	private List<PS> getCustomPS(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PS> response = null;
		try {
			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PSCollection.class,
					PS.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PS> getCustomGlobalPS(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PS> response = new ArrayList<PS>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PS> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PSCollection.class, PS.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PA> getCustomGlobalPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = new ArrayList<PA>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PA> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PA> getGlobalPA(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	private List<PA> getCustomPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PACollection.class,
					PA.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PV> getCustomGlobalPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = new ArrayList<PV>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PV> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PV> getGlobalPV(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	private List<PV> getCustomPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PVCollection.class,
					PV.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<GeneralExam> getCustomGlobalGeneralExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<GeneralExam> response = new ArrayList<GeneralExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<GeneralExam> getGlobalGeneralExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}

	private List<GeneralExam> getCustomGeneralExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ECGDetails> getCustomGlobalECGDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ECGDetails> response = new ArrayList<ECGDetails>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ECGDetails> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;

	}

	private List<ECGDetails> getCustomECGDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ECGDetails> response = null;
		try {
			AggregationResults<ECGDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private List<ECGDetails> getCustomGlobalECGDetailsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ECGDetails> response = null;
		try {
			AggregationResults<ECGDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "ecgDetails", speciality, null, null),
							ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private List<ECGDetails> getGlobalECGDetailsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<ECGDetails> response = null;

		try {
			AggregationResults<ECGDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "ecgDetails", speciality, null, null),
							ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private List<ECGDetails> getCustomECGDetailsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<ECGDetails> response = null;
		try {
			AggregationResults<ECGDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "ecgDetails", speciality, null, null),
							ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private List<XRayDetails> getCustomXRayDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<XRayDetails> response = null;
		try {
			AggregationResults<XRayDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	private List<XRayDetails> getCustomGlobalXRayDetailsForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<XRayDetails> response = null;
		try {
			AggregationResults<XRayDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "xrayDetails", speciality, null, null),
							XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	private List<XRayDetails> getGlobalXRayDetailsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<XRayDetails> response = null;

		try {
			AggregationResults<XRayDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "xrayDetails", speciality, null, null),
							XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	private List<XRayDetails> getCustomXRayDetailsForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<XRayDetails> response = null;
		try {
			AggregationResults<XRayDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "xrayDetails", speciality, null, null),
							XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<XRayDetails> getGlobalXRayDetails(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<XRayDetails> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<XRayDetails> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<XRayDetails> getCustomGlobalXRayDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<XRayDetails> response = new ArrayList<XRayDetails>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<XRayDetails> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;

	}

	private List<Echo> getCustomEcho(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Echo> response = null;
		try {
			AggregationResults<Echo> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), EchoCollection.class,
					Echo.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Echo> getGlobalEcho(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Echo> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Echo> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Echo> getGlobalECGDetails(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Echo> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Echo> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Echo> getCustomGlobalEcho(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Echo> response = new ArrayList<Echo>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Echo> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;

	}

	private List<Echo> getCustomGlobalEchoForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Echo> response = null;
		try {
			AggregationResults<Echo> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "echo", speciality, null, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	private List<Echo> getGlobalEchoForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Echo> response = null;

		try {
			AggregationResults<Echo> results = mongoTemplate
					.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "echo", speciality, null, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	private List<Echo> getCustomEchoForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Echo> response = null;
		try {
			AggregationResults<Echo> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "echo", speciality, null, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Holter> getGlobalHolter(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Holter> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Holter> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), HolterCollection.class,
					Holter.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Holter> getCustomGlobalHolter(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Holter> response = new ArrayList<Holter>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Holter> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					HolterCollection.class, Holter.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;

	}

	private List<Holter> getCustomHolter(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Holter> response = null;
		try {
			AggregationResults<Holter> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					HolterCollection.class, Holter.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private List<Holter> getCustomGlobalHolterForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Holter> response = null;
		try {
			AggregationResults<Holter> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "holter", speciality, null, null),
							HolterCollection.class, Holter.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private List<ProcedureNote> getCustomGlobalProcedureNoteForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ProcedureNote> response = null;
		try {
			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"procedureNote", speciality, null, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	private List<Holter> getGlobalHolterForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Holter> response = null;

		try {
			AggregationResults<Holter> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "holter", speciality, null, null),
							HolterCollection.class, Holter.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private List<ProcedureNote> getGlobalProcedureNoteForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ProcedureNote> response = null;

		try {
			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"procedureNote", speciality, null, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	private List<Holter> getCustomHolterForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<Holter> response = null;
		try {
			AggregationResults<Holter> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "holter", speciality, null, null),
							HolterCollection.class, Holter.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private List<ProcedureNote> getCustomProcedureNoteForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ProcedureNote> response = null;
		try {
			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"procedureNote", speciality, null, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getCustomGlobalMenstrualHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = new ArrayList<MenstrualHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;

	}

	@Override
	@Transactional
	public PS addEditPS(PS ps) {
		try {
			PSCollection psCollection = new PSCollection();
			BeanUtil.map(ps, psCollection);
			if (DPDoctorUtils.anyStringEmpty(psCollection.getId())) {
				psCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(psCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(psCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						psCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					psCollection.setCreatedBy("ADMIN");
				}
			} else {
				PSCollection oldPSCollecion = psRepository.findById(psCollection.getId()).orElse(null);
				psCollection.setCreatedBy(oldPSCollecion.getCreatedBy());
				psCollection.setCreatedTime(oldPSCollecion.getCreatedTime());
				psCollection.setDiscarded(oldPSCollecion.getDiscarded());
			}
			psCollection = psRepository.save(psCollection);

			BeanUtil.map(psCollection, ps);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return ps;
	}

	@Override
	@Transactional
	public PA addEditPA(PA pa) {
		try {
			PACollection paCollection = new PACollection();
			BeanUtil.map(pa, paCollection);
			if (DPDoctorUtils.anyStringEmpty(paCollection.getId())) {
				paCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(paCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						paCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					paCollection.setCreatedBy("ADMIN");
				}
			} else {
				PACollection oldPACollecion = paRepository.findById(paCollection.getId()).orElse(null);
				paCollection.setCreatedBy(oldPACollecion.getCreatedBy());
				paCollection.setCreatedTime(oldPACollecion.getCreatedTime());
				paCollection.setDiscarded(oldPACollecion.getDiscarded());
			}
			paCollection = paRepository.save(paCollection);

			BeanUtil.map(paCollection, pa);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pa;
	}

	@Override
	public PS deletePS(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PS response = null;
		try {
			PSCollection psCollection = psRepository.findById(new ObjectId(id)).orElse(null);
			if (psCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(psCollection.getDoctorId(), psCollection.getHospitalId(),
						psCollection.getLocationId())) {
					if (psCollection.getDoctorId().toString().equals(doctorId)
							&& psCollection.getHospitalId().toString().equals(hospitalId)
							&& psCollection.getLocationId().toString().equals(locationId)) {

						psCollection.setDiscarded(discarded);
						psCollection.setUpdatedTime(new Date());
						psRepository.save(psCollection);
						response = new PS();
						BeanUtil.map(psCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					psCollection.setDiscarded(discarded);
					psCollection.setUpdatedTime(new Date());
					psRepository.save(psCollection);
					response = new PS();
					BeanUtil.map(psCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/A not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PV deletePV(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PV response = null;
		try {
			PVCollection pvCollection = pvRepository.findById(new ObjectId(id)).orElse(null);
			if (pvCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId(), pvCollection.getHospitalId(),
						pvCollection.getLocationId())) {
					if (pvCollection.getDoctorId().toString().equals(doctorId)
							&& pvCollection.getHospitalId().toString().equals(hospitalId)
							&& pvCollection.getLocationId().toString().equals(locationId)) {

						pvCollection.setDiscarded(discarded);
						pvCollection.setUpdatedTime(new Date());
						pvRepository.save(pvCollection);
						response = new PV();
						BeanUtil.map(pvCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					pvCollection.setDiscarded(discarded);
					pvCollection.setUpdatedTime(new Date());
					pvRepository.save(pvCollection);
					response = new PV();
					BeanUtil.map(pvCollection, response);
				}
			} else {
				logger.warn("P/V not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/V not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public PA deletePA(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PA response = null;
		try {
			PACollection paCollection = paRepository.findById(new ObjectId(id)).orElse(null);
			if (paCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId(), paCollection.getHospitalId(),
						paCollection.getLocationId())) {
					if (paCollection.getDoctorId().toString().equals(doctorId)
							&& paCollection.getHospitalId().toString().equals(hospitalId)
							&& paCollection.getLocationId().toString().equals(locationId)) {

						paCollection.setDiscarded(discarded);
						paCollection.setUpdatedTime(new Date());
						paRepository.save(paCollection);
						response = new PA();
						BeanUtil.map(paCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					paCollection.setDiscarded(discarded);
					paCollection.setUpdatedTime(new Date());
					paRepository.save(paCollection);
					response = new PA();
					BeanUtil.map(paCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/A not found!");
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
	public PV addEditPV(PV pv) {
		try {
			PVCollection pvCollection = new PVCollection();
			BeanUtil.map(pv, pvCollection);
			if (DPDoctorUtils.anyStringEmpty(pvCollection.getId())) {
				pvCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(pvCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						pvCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					pvCollection.setCreatedBy("ADMIN");
				}
			} else {
				PVCollection oldPVCollecion = pvRepository.findById(pvCollection.getId()).orElse(null);
				pvCollection.setCreatedBy(oldPVCollecion.getCreatedBy());
				pvCollection.setCreatedTime(oldPVCollecion.getCreatedTime());
				pvCollection.setDiscarded(oldPVCollecion.getDiscarded());
			}
			pvCollection = pvRepository.save(pvCollection);

			BeanUtil.map(pvCollection, pv);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pv;
	}

	@Override
	@Transactional
	public IndicationOfUSG addEditIndicationOfUSG(IndicationOfUSG indicationOfUSG) {
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = new IndicationOfUSGCollection();
			BeanUtil.map(indicationOfUSG, indicationOfUSGCollection);
			if (DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getId())) {
				indicationOfUSGCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(indicationOfUSGCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						indicationOfUSGCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					indicationOfUSGCollection.setCreatedBy("ADMIN");
				}
			} else {
				IndicationOfUSGCollection oldIndicationOfUSGCollection = indicationOfUSGRepository
						.findById(indicationOfUSGCollection.getId()).orElse(null);
				indicationOfUSGCollection.setCreatedBy(oldIndicationOfUSGCollection.getCreatedBy());
				indicationOfUSGCollection.setCreatedTime(oldIndicationOfUSGCollection.getCreatedTime());
				indicationOfUSGCollection.setDiscarded(oldIndicationOfUSGCollection.getDiscarded());
			}
			indicationOfUSGCollection = indicationOfUSGRepository.save(indicationOfUSGCollection);

			BeanUtil.map(indicationOfUSGCollection, indicationOfUSG);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return indicationOfUSG;
	}

	@Override
	public IndicationOfUSG deleteIndicationOfUSG(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		IndicationOfUSG response = null;
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = indicationOfUSGRepository.findById(new ObjectId(id)).orElse(null);
			if (indicationOfUSGCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId(),
						indicationOfUSGCollection.getHospitalId(), indicationOfUSGCollection.getLocationId())) {
					if (indicationOfUSGCollection.getDoctorId().toString().equals(doctorId)
							&& indicationOfUSGCollection.getHospitalId().toString().equals(hospitalId)
							&& indicationOfUSGCollection.getLocationId().toString().equals(locationId)) {

						indicationOfUSGCollection.setDiscarded(discarded);
						indicationOfUSGCollection.setUpdatedTime(new Date());
						indicationOfUSGRepository.save(indicationOfUSGCollection);
						response = new IndicationOfUSG();
						BeanUtil.map(indicationOfUSGCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					indicationOfUSGCollection.setDiscarded(discarded);
					indicationOfUSGCollection.setUpdatedTime(new Date());
					indicationOfUSGRepository.save(indicationOfUSGCollection);
					response = new IndicationOfUSG();
					BeanUtil.map(indicationOfUSGCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "Indication of USG not found!");
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
	public ProvisionalDiagnosis deleteProvisionalDiagnosis(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		ProvisionalDiagnosis response = null;
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = provisionalDiagnosisRepository
					.findById(new ObjectId(id)).orElse(null);
			if (provisionalDiagnosisCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId(),
						provisionalDiagnosisCollection.getHospitalId(),
						provisionalDiagnosisCollection.getLocationId())) {
					if (provisionalDiagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& provisionalDiagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& provisionalDiagnosisCollection.getLocationId().toString().equals(locationId)) {

						provisionalDiagnosisCollection.setDiscarded(discarded);
						provisionalDiagnosisCollection.setUpdatedTime(new Date());
						provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
						response = new ProvisionalDiagnosis();
						BeanUtil.map(provisionalDiagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					provisionalDiagnosisCollection.setDiscarded(discarded);
					provisionalDiagnosisCollection.setUpdatedTime(new Date());
					provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
					response = new ProvisionalDiagnosis();
					BeanUtil.map(provisionalDiagnosisCollection, response);
				}
			} else {
				logger.warn("Provisional Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Provisional Diagnosis not found!");
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
	public GeneralExam deleteGeneralExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		GeneralExam response = null;
		try {
			GeneralExamCollection generalExamCollection = generalExamRepository.findById(new ObjectId(id)).orElse(null);
			if (generalExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId(),
						generalExamCollection.getHospitalId(), generalExamCollection.getLocationId())) {
					if (generalExamCollection.getDoctorId().toString().equals(doctorId)
							&& generalExamCollection.getHospitalId().toString().equals(hospitalId)
							&& generalExamCollection.getLocationId().toString().equals(locationId)) {

						generalExamCollection.setDiscarded(discarded);
						generalExamCollection.setUpdatedTime(new Date());
						generalExamRepository.save(generalExamCollection);
						response = new GeneralExam();
						BeanUtil.map(generalExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					generalExamCollection.setDiscarded(discarded);
					generalExamCollection.setUpdatedTime(new Date());
					generalExamRepository.save(generalExamCollection);
					response = new GeneralExam();
					BeanUtil.map(generalExamCollection, response);
				}
			} else {
				logger.warn("General Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "General Exam not found!");
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
	public PresentComplaintHistory deletePresentComplaintHistory(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		PresentComplaintHistory response = null;
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = presentComplaintHistoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentComplaintHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId(),
						presentComplaintHistoryCollection.getHospitalId(),
						presentComplaintHistoryCollection.getLocationId())) {
					if (presentComplaintHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintHistoryCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintHistoryCollection.setDiscarded(discarded);
						presentComplaintHistoryCollection.setUpdatedTime(new Date());
						presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
						response = new PresentComplaintHistory();
						BeanUtil.map(presentComplaintHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintHistoryCollection.setDiscarded(discarded);
					presentComplaintHistoryCollection.setUpdatedTime(new Date());
					presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
					response = new PresentComplaintHistory();
					BeanUtil.map(presentComplaintHistoryCollection, response);
				}
			} else {
				logger.warn("Present Complaint History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint History not found!");
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
	public SystemExam deleteSystemExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		SystemExam response = null;
		try {
			SystemExamCollection systemExamCollection = systemExamRepository.findById(new ObjectId(id)).orElse(null);
			if (systemExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId(),
						systemExamCollection.getHospitalId(), systemExamCollection.getLocationId())) {
					if (systemExamCollection.getDoctorId().toString().equals(doctorId)
							&& systemExamCollection.getHospitalId().toString().equals(hospitalId)
							&& systemExamCollection.getLocationId().toString().equals(locationId)) {

						systemExamCollection.setDiscarded(discarded);
						systemExamCollection.setUpdatedTime(new Date());
						systemExamRepository.save(systemExamCollection);
						response = new SystemExam();
						BeanUtil.map(systemExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					systemExamCollection.setDiscarded(discarded);
					systemExamCollection.setUpdatedTime(new Date());
					systemExamRepository.save(systemExamCollection);
					response = new SystemExam();
					BeanUtil.map(systemExamCollection, response);
				}
			} else {
				logger.warn("System Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "System Exam not found!");
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
	public PresentComplaint deletePresentComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentComplaint response = null;
		try {
			PresentComplaintCollection presentComplaintCollection = presentComplaintRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentComplaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId(),
						presentComplaintCollection.getHospitalId(), presentComplaintCollection.getLocationId())) {
					if (presentComplaintCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintCollection.setDiscarded(discarded);
						presentComplaintCollection.setUpdatedTime(new Date());
						presentComplaintRepository.save(presentComplaintCollection);
						response = new PresentComplaint();
						BeanUtil.map(presentComplaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintCollection.setDiscarded(discarded);
					presentComplaintCollection.setUpdatedTime(new Date());
					presentComplaintRepository.save(presentComplaintCollection);
					response = new PresentComplaint();
					BeanUtil.map(presentComplaintCollection, response);
				}
			} else {
				logger.warn("Present Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint not found!");
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
	public ObstetricHistory deleteObstetricHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ObstetricHistory response = null;
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = obstetricHistoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (obstetricHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId(),
						obstetricHistoryCollection.getHospitalId(), obstetricHistoryCollection.getLocationId())) {
					if (obstetricHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& obstetricHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& obstetricHistoryCollection.getLocationId().toString().equals(locationId)) {

						obstetricHistoryCollection.setDiscarded(discarded);
						obstetricHistoryCollection.setUpdatedTime(new Date());
						obstetricHistoryRepository.save(obstetricHistoryCollection);
						response = new ObstetricHistory();
						BeanUtil.map(obstetricHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					obstetricHistoryCollection.setDiscarded(discarded);
					obstetricHistoryCollection.setUpdatedTime(new Date());
					obstetricHistoryRepository.save(obstetricHistoryCollection);
					response = new ObstetricHistory();
					BeanUtil.map(obstetricHistoryCollection, response);
				}
			} else {
				logger.warn("Obstetric History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Obstetric History not found!");
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
	public MenstrualHistory deleteMenstrualHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		MenstrualHistory response = null;
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = menstrualHistoryRepository
					.findById(new ObjectId(id)).orElse(null);
			if (menstrualHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId(),
						menstrualHistoryCollection.getHospitalId(), menstrualHistoryCollection.getLocationId())) {
					if (menstrualHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& menstrualHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& menstrualHistoryCollection.getLocationId().toString().equals(locationId)) {

						menstrualHistoryCollection.setDiscarded(discarded);
						menstrualHistoryCollection.setUpdatedTime(new Date());
						menstrualHistoryRepository.save(menstrualHistoryCollection);
						response = new MenstrualHistory();
						BeanUtil.map(menstrualHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					menstrualHistoryCollection.setDiscarded(discarded);
					menstrualHistoryCollection.setUpdatedTime(new Date());
					menstrualHistoryRepository.save(menstrualHistoryCollection);
					response = new MenstrualHistory();
					BeanUtil.map(menstrualHistoryCollection, response);
				}
			} else {
				logger.warn("Menstrual History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Menstrual History not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public XRayDetails deleteXRayDetails(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		XRayDetails response = null;
		try {
			XRayDetailsCollection xRayDetailsCollection = xRayDetailsRepository.findById(new ObjectId(id)).orElse(null);
			if (xRayDetailsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getDoctorId(),
						xRayDetailsCollection.getHospitalId(), xRayDetailsCollection.getLocationId())) {
					if (xRayDetailsCollection.getDoctorId().toString().equals(doctorId)
							&& xRayDetailsCollection.getHospitalId().toString().equals(hospitalId)
							&& xRayDetailsCollection.getLocationId().toString().equals(locationId)) {

						xRayDetailsCollection.setDiscarded(discarded);
						xRayDetailsCollection.setUpdatedTime(new Date());
						xRayDetailsRepository.save(xRayDetailsCollection);
						response = new XRayDetails();
						BeanUtil.map(xRayDetailsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					xRayDetailsCollection.setDiscarded(discarded);
					xRayDetailsCollection.setUpdatedTime(new Date());
					xRayDetailsRepository.save(xRayDetailsCollection);
					response = new XRayDetails();
					BeanUtil.map(xRayDetailsCollection, response);
				}
			} else {
				logger.warn("X ray details not found!");
				throw new BusinessException(ServiceError.NoRecord, "X-RAY details not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public Echo deleteEcho(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Echo response = null;
		try {
			EchoCollection echoCollection = echoRepository.findById(new ObjectId(id)).orElse(null);
			if (echoCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(echoCollection.getDoctorId(), echoCollection.getHospitalId(),
						echoCollection.getLocationId())) {
					if (echoCollection.getDoctorId().toString().equals(doctorId)
							&& echoCollection.getHospitalId().toString().equals(hospitalId)
							&& echoCollection.getLocationId().toString().equals(locationId)) {

						echoCollection.setDiscarded(discarded);
						echoCollection.setUpdatedTime(new Date());
						echoRepository.save(echoCollection);
						response = new Echo();
						BeanUtil.map(echoCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					echoCollection.setDiscarded(discarded);
					echoCollection.setUpdatedTime(new Date());
					echoRepository.save(echoCollection);
					response = new Echo();
					BeanUtil.map(echoCollection, response);
				}
			} else {
				logger.warn("Echo not found!");
				throw new BusinessException(ServiceError.NoRecord, "Echo not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public ECGDetails deleteECGDetails(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ECGDetails response = null;
		try {
			ECGDetailsCollection ecgDetailsCollection = ecgDetailsRepository.findById(new ObjectId(id)).orElse(null);
			if (ecgDetailsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getDoctorId(),
						ecgDetailsCollection.getHospitalId(), ecgDetailsCollection.getLocationId())) {
					if (ecgDetailsCollection.getDoctorId().toString().equals(doctorId)
							&& ecgDetailsCollection.getHospitalId().toString().equals(hospitalId)
							&& ecgDetailsCollection.getLocationId().toString().equals(locationId)) {

						ecgDetailsCollection.setDiscarded(discarded);
						ecgDetailsCollection.setUpdatedTime(new Date());
						ecgDetailsRepository.save(ecgDetailsCollection);
						response = new ECGDetails();
						BeanUtil.map(ecgDetailsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					ecgDetailsCollection.setDiscarded(discarded);
					ecgDetailsCollection.setUpdatedTime(new Date());
					ecgDetailsRepository.save(ecgDetailsCollection);
					response = new ECGDetails();
					BeanUtil.map(ecgDetailsCollection, response);
				}
			} else {
				logger.warn("eCG details not found!");
				throw new BusinessException(ServiceError.NoRecord, "ECG details not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public Holter deleteHolter(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Holter response = null;
		try {
			HolterCollection holterCollection = holterRepository.findById(new ObjectId(id)).orElse(null);
			if (holterCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(holterCollection.getDoctorId(), holterCollection.getHospitalId(),
						holterCollection.getLocationId())) {
					if (holterCollection.getDoctorId().toString().equals(doctorId)
							&& holterCollection.getHospitalId().toString().equals(hospitalId)
							&& holterCollection.getLocationId().toString().equals(locationId)) {

						holterCollection.setDiscarded(discarded);
						holterCollection.setUpdatedTime(new Date());
						holterRepository.save(holterCollection);
						response = new Holter();
						BeanUtil.map(holterCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					holterCollection.setDiscarded(discarded);
					holterCollection.setUpdatedTime(new Date());
					holterRepository.save(holterCollection);
					response = new Holter();
					BeanUtil.map(holterCollection, response);
				}
			} else {
				logger.warn("Holter not found!");
				throw new BusinessException(ServiceError.NoRecord, "Holter not found!");
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
	public SystemExam addEditSystemExam(SystemExam systemExam) {
		try {
			SystemExamCollection systemExamCollection = new SystemExamCollection();
			BeanUtil.map(systemExam, systemExamCollection);
			if (DPDoctorUtils.anyStringEmpty(systemExamCollection.getId())) {
				systemExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(systemExamCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						systemExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					systemExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				SystemExamCollection oldSystemExamCollection = systemExamRepository
						.findById(systemExamCollection.getId()).orElse(null);
				systemExamCollection.setCreatedBy(oldSystemExamCollection.getCreatedBy());
				systemExamCollection.setCreatedTime(oldSystemExamCollection.getCreatedTime());
				systemExamCollection.setDiscarded(oldSystemExamCollection.getDiscarded());
			}
			systemExamCollection = systemExamRepository.save(systemExamCollection);

			BeanUtil.map(systemExamCollection, systemExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return systemExam;
	}

	@Override
	@Transactional
	public MenstrualHistory addEditMenstrualHistory(MenstrualHistory menstrualHistory) {
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = new MenstrualHistoryCollection();
			BeanUtil.map(menstrualHistory, menstrualHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getId())) {
				menstrualHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(menstrualHistoryCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						menstrualHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					menstrualHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				MenstrualHistoryCollection oldMenstrualHistoryCollection = menstrualHistoryRepository
						.findById(menstrualHistoryCollection.getId()).orElse(null);
				menstrualHistoryCollection.setCreatedBy(oldMenstrualHistoryCollection.getCreatedBy());
				menstrualHistoryCollection.setCreatedTime(oldMenstrualHistoryCollection.getCreatedTime());
				menstrualHistoryCollection.setDiscarded(oldMenstrualHistoryCollection.getDiscarded());
			}
			menstrualHistoryCollection = menstrualHistoryRepository.save(menstrualHistoryCollection);

			BeanUtil.map(menstrualHistoryCollection, menstrualHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return menstrualHistory;
	}

	@Override
	@Transactional
	public PresentComplaint addEditPresentComplaint(PresentComplaint presentComplaint) {
		try {
			PresentComplaintCollection presentComplaintCollection = new PresentComplaintCollection();
			BeanUtil.map(presentComplaint, presentComplaintCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getId())) {
				presentComplaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(presentComplaintCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentComplaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintCollection oldPresentComplaintCollection = presentComplaintRepository
						.findById(presentComplaintCollection.getId()).orElse(null);
				presentComplaintCollection.setCreatedBy(oldPresentComplaintCollection.getCreatedBy());
				presentComplaintCollection.setCreatedTime(oldPresentComplaintCollection.getCreatedTime());
				presentComplaintCollection.setDiscarded(oldPresentComplaintCollection.getDiscarded());
			}
			presentComplaintCollection = presentComplaintRepository.save(presentComplaintCollection);

			BeanUtil.map(presentComplaintCollection, presentComplaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaint;
	}

	@Override
	@Transactional
	public PresentComplaintHistory addEditPresentComplaintHistory(PresentComplaintHistory presentComplaintHistory) {
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = new PresentComplaintHistoryCollection();
			BeanUtil.map(presentComplaintHistory, presentComplaintHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getId())) {
				presentComplaintHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(presentComplaintHistoryCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentComplaintHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintHistoryCollection oldPresentComplaintHistoryCollection = presentComplaintHistoryRepository
						.findById(presentComplaintHistoryCollection.getId()).orElse(null);
				presentComplaintHistoryCollection.setCreatedBy(oldPresentComplaintHistoryCollection.getCreatedBy());
				presentComplaintHistoryCollection.setCreatedTime(oldPresentComplaintHistoryCollection.getCreatedTime());
				presentComplaintHistoryCollection.setDiscarded(oldPresentComplaintHistoryCollection.getDiscarded());
			}
			presentComplaintHistoryCollection = presentComplaintHistoryRepository
					.save(presentComplaintHistoryCollection);

			BeanUtil.map(presentComplaintHistoryCollection, presentComplaintHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaintHistory;
	}

	@Override
	@Transactional
	public ObstetricHistory addEditObstetricHistory(ObstetricHistory obstetricHistory) {
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = new ObstetricHistoryCollection();
			BeanUtil.map(obstetricHistory, obstetricHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getId())) {
				obstetricHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(obstetricHistoryCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						obstetricHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					obstetricHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObstetricHistoryCollection oldObstetricHistoryCollection = obstetricHistoryRepository
						.findById(obstetricHistoryCollection.getId()).orElse(null);
				obstetricHistoryCollection.setCreatedBy(oldObstetricHistoryCollection.getCreatedBy());
				obstetricHistoryCollection.setCreatedTime(oldObstetricHistoryCollection.getCreatedTime());
				obstetricHistoryCollection.setDiscarded(oldObstetricHistoryCollection.getDiscarded());
			}
			obstetricHistoryCollection = obstetricHistoryRepository.save(obstetricHistoryCollection);

			BeanUtil.map(obstetricHistoryCollection, obstetricHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return obstetricHistory;
	}

	@Override
	@Transactional
	public ProvisionalDiagnosis addEditProvisionalDiagnosis(ProvisionalDiagnosis provisionalDiagnosis) {
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = new ProvisionalDiagnosisCollection();
			BeanUtil.map(provisionalDiagnosis, provisionalDiagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getId())) {
				provisionalDiagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(provisionalDiagnosisCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						provisionalDiagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					provisionalDiagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				ProvisionalDiagnosisCollection oldProvisionalDiagnosisCollection = provisionalDiagnosisRepository
						.findById(provisionalDiagnosisCollection.getId()).orElse(null);
				provisionalDiagnosisCollection.setCreatedBy(oldProvisionalDiagnosisCollection.getCreatedBy());
				provisionalDiagnosisCollection.setCreatedTime(oldProvisionalDiagnosisCollection.getCreatedTime());
				provisionalDiagnosisCollection.setDiscarded(oldProvisionalDiagnosisCollection.getDiscarded());
			}
			provisionalDiagnosisCollection = provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);

			BeanUtil.map(provisionalDiagnosisCollection, provisionalDiagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return provisionalDiagnosis;
	}

	@Override
	@Transactional
	public GeneralExam addEditGeneralExam(GeneralExam generalExam) {
		try {
			GeneralExamCollection generalExamCollection = new GeneralExamCollection();
			BeanUtil.map(generalExam, generalExamCollection);
			if (DPDoctorUtils.anyStringEmpty(generalExamCollection.getId())) {
				generalExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(generalExamCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						generalExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					generalExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				GeneralExamCollection oldGeneralExamCollection = generalExamRepository
						.findById(generalExamCollection.getId()).orElse(null);
				generalExamCollection.setCreatedBy(oldGeneralExamCollection.getCreatedBy());
				generalExamCollection.setCreatedTime(oldGeneralExamCollection.getCreatedTime());
				generalExamCollection.setDiscarded(oldGeneralExamCollection.getDiscarded());
			}
			generalExamCollection = generalExamRepository.save(generalExamCollection);

			BeanUtil.map(generalExamCollection, generalExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generalExam;
	}

	@Override
	@Transactional
	public ECGDetails addEditECGDetails(ECGDetails ecgDetails) {
		try {
			ECGDetailsCollection ecgDetailsCollection = new ECGDetailsCollection();
			BeanUtil.map(ecgDetails, ecgDetailsCollection);
			if (DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getId())) {
				ecgDetailsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(ecgDetailsCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						ecgDetailsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					ecgDetailsCollection.setCreatedBy("ADMIN");
				}
			} else {
				ECGDetailsCollection oldECGDetailsCollection = ecgDetailsRepository
						.findById(ecgDetailsCollection.getId()).orElse(null);
				ecgDetailsCollection.setCreatedBy(oldECGDetailsCollection.getCreatedBy());
				ecgDetailsCollection.setCreatedTime(oldECGDetailsCollection.getCreatedTime());
				ecgDetailsCollection.setDiscarded(oldECGDetailsCollection.getDiscarded());
			}
			ecgDetailsCollection = ecgDetailsRepository.save(ecgDetailsCollection);

			BeanUtil.map(ecgDetailsCollection, ecgDetails);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return ecgDetails;
	}

	@Override
	@Transactional
	public XRayDetails addEditXRayDetails(XRayDetails xRayDetails) {
		try {
			XRayDetailsCollection xRayDetailsCollection = new XRayDetailsCollection();
			BeanUtil.map(xRayDetails, xRayDetailsCollection);
			if (DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getId())) {
				xRayDetailsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(xRayDetailsCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						xRayDetailsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					xRayDetailsCollection.setCreatedBy("ADMIN");
				}
			} else {
				XRayDetailsCollection oldXRayDetailsCollection = xRayDetailsRepository
						.findById(xRayDetailsCollection.getId()).orElse(null);
				xRayDetailsCollection.setCreatedBy(oldXRayDetailsCollection.getCreatedBy());
				xRayDetailsCollection.setCreatedTime(oldXRayDetailsCollection.getCreatedTime());
				xRayDetailsCollection.setDiscarded(oldXRayDetailsCollection.getDiscarded());
			}
			xRayDetailsCollection = xRayDetailsRepository.save(xRayDetailsCollection);

			BeanUtil.map(xRayDetailsCollection, xRayDetails);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return xRayDetails;
	}

	@Override
	@Transactional
	public Echo addEditEcho(Echo echo) {
		try {
			EchoCollection echoCollection = new EchoCollection();
			BeanUtil.map(echo, echoCollection);
			if (DPDoctorUtils.anyStringEmpty(echoCollection.getId())) {
				echoCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(echoCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(echoCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						echoCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					echoCollection.setCreatedBy("ADMIN");
				}
			} else {
				EchoCollection oldEchoCollection = echoRepository.findById(echoCollection.getId()).orElse(null);
				echoCollection.setCreatedBy(oldEchoCollection.getCreatedBy());
				echoCollection.setCreatedTime(oldEchoCollection.getCreatedTime());
				echoCollection.setDiscarded(oldEchoCollection.getDiscarded());
			}
			echoCollection = echoRepository.save(echoCollection);

			BeanUtil.map(echoCollection, echo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return echo;
	}

	@Override
	@Transactional
	public Holter addEditHolter(Holter holter) {
		try {
			HolterCollection holterCollection = new HolterCollection();
			BeanUtil.map(holter, holterCollection);
			if (DPDoctorUtils.anyStringEmpty(holterCollection.getId())) {
				holterCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(holterCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(holterCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						holterCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					holterCollection.setCreatedBy("ADMIN");
				}
			} else {
				HolterCollection oldHolterCollection = holterRepository.findById(holterCollection.getId()).orElse(null);
				holterCollection.setCreatedBy(oldHolterCollection.getCreatedBy());
				holterCollection.setCreatedTime(oldHolterCollection.getCreatedTime());
				holterCollection.setDiscarded(oldHolterCollection.getDiscarded());
			}
			holterCollection = holterRepository.save(holterCollection);

			BeanUtil.map(holterCollection, holter);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return holter;
	}

	private List<PresentComplaint> getCustomGlobalPresentComplaintForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaint> response = null;
		try {
			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaint", speciality, null, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaint");
		}
		return response;
	}

	private List<PresentComplaint> getGlobalPresentComplaintForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaint> response = null;

		try {
			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaint", speciality, null, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaint");
		}
		return response;
	}

	private List<PresentComplaint> getCustomPresentComplaintForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaint> response = null;
		try {
			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaint", speciality, null, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaint");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getCustomGlobalProvisionalDiagnosisForAdmin(int page, int size,
			String updatedTime, Boolean discarded, String searchTerm, String speciality) {
		List<ProvisionalDiagnosis> response = null;
		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"provisionalDiagnosis", speciality, null, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ProvisionalDiagnosis");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getGlobalProvisionalDiagnosisForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ProvisionalDiagnosis> response = null;

		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"provisionalDiagnosis", speciality, null, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ProvisionalDiagnosis");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getCustomProvisionalDiagnosisForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ProvisionalDiagnosis> response = null;
		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"provisionalDiagnosis", speciality, null, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ProvisionalDiagnosis");
		}
		return response;
	}

	private List<PresentComplaintHistory> getCustomGlobalPresentComplaintHistoryForAdmin(int page, int size,
			String updatedTime, Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaintHistory> response = null;
		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaintHistory", speciality, null, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaintHistory");
		}
		return response;
	}

	private List<PresentComplaintHistory> getGlobalPresentComplaintHistoryForAdmin(int page, int size,
			String updatedTime, Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaintHistory> response = null;

		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaintHistory", speciality, null, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaintHistory");
		}
		return response;
	}

	private List<PresentComplaintHistory> getCustomPresentComplaintHistoryForAdmin(int page, int size,
			String updatedTime, Boolean discarded, String searchTerm, String speciality) {
		List<PresentComplaintHistory> response = null;
		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"presentComplaintHistory", speciality, null, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PresentComplaintHistory");
		}
		return response;
	}

	private List<GeneralExam> getCustomGlobalGeneralExamForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<GeneralExam> response = null;
		try {
			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "generalExam", speciality, null, null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting GeneralExam");
		}
		return response;
	}

	private List<GeneralExam> getGlobalGeneralExamForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<GeneralExam> response = null;

		try {
			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "generalExam", speciality, null, null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting GeneralExam");
		}
		return response;
	}

	private List<GeneralExam> getCustomGeneralExamForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<GeneralExam> response = null;
		try {
			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "generalExam", speciality, null, null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting GeneralExam");
		}
		return response;
	}

	private List<SystemExam> getCustomGlobalSystemExamForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<SystemExam> response = null;
		try {
			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "systemExam", speciality, null, null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting SystemExam");
		}
		return response;
	}

	private List<SystemExam> getGlobalSystemExamForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<SystemExam> response = null;

		try {
			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "systemExam", speciality, null, null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting SystemExam");
		}
		return response;
	}

	private List<SystemExam> getCustomSystemExamForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<SystemExam> response = null;
		try {
			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded,
									searchTerm, "systemExam", speciality, null, null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting SystemExam");
		}
		return response;
	}

	private List<MenstrualHistory> getCustomGlobalMenstrualHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<MenstrualHistory> response = null;
		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"menstrualHistory", speciality, null, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting MenstrualHistory");
		}
		return response;
	}

	private List<MenstrualHistory> getGlobalMenstrualHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<MenstrualHistory> response = null;

		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"menstrualHistory", speciality, null, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting MenstrualHistory");
		}
		return response;
	}

	private List<MenstrualHistory> getCustomMenstrualHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<MenstrualHistory> response = null;
		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"menstrualHistory", speciality, null, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting MenstrualHistory");
		}
		return response;
	}

	private List<ObstetricHistory> getCustomGlobalObstetricHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ObstetricHistory> response = null;
		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"obstetricHistory", speciality, null, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ObstetricHistory");
		}
		return response;
	}

	private List<ObstetricHistory> getGlobalObstetricHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ObstetricHistory> response = null;

		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"obstetricHistory", speciality, null, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ObstetricHistory");
		}
		return response;
	}

	private List<ObstetricHistory> getCustomObstetricHistoryForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<ObstetricHistory> response = null;
		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"obstetricHistory", speciality, null, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ObstetricHistory");
		}
		return response;
	}

	private List<IndicationOfUSG> getCustomGlobalIndicationOfUSGForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<IndicationOfUSG> response = null;
		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"indicationOfUSG", speciality, null, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting IndicationOfUSG");
		}
		return response;
	}

	private List<IndicationOfUSG> getGlobalIndicationOfUSGForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<IndicationOfUSG> response = null;

		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"indicationOfUSG", speciality, null, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting IndicationOfUSG");
		}
		return response;
	}

	private List<IndicationOfUSG> getCustomIndicationOfUSGForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String speciality) {
		List<IndicationOfUSG> response = null;
		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregationForAdmin(page, size, updatedTime, discarded, searchTerm,
							"indicationOfUSG", speciality, null, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting IndicationOfUSG");
		}
		return response;
	}

	private List<PV> getCustomGlobalPVForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<PV> response = null;
		try {
			AggregationResults<PV> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "pv", speciality, null, null), PVCollection.class, PV.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PV");
		}
		return response;
	}

	private List<PV> getGlobalPVForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PV> response = null;

		try {
			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "pv", speciality, null, null), PVCollection.class,
					PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PV");
		}
		return response;
	}

	private List<PV> getCustomPVForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PV> response = null;
		try {
			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "pv", speciality, null, null), PVCollection.class,
					PV.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PV");
		}
		return response;
	}

	private List<PA> getCustomGlobalPAForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<PA> response = null;
		try {
			AggregationResults<PA> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "pa", speciality, null, null), PACollection.class, PA.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PA");
		}
		return response;
	}

	private List<PA> getGlobalPAForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PA> response = null;

		try {
			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "pa", speciality, null, null), PACollection.class,
					PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PA");
		}
		return response;
	}

	private List<PA> getCustomPAForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PA> response = null;
		try {
			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "pa", speciality, null, null), PACollection.class,
					PA.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PA");
		}
		return response;
	}

	private List<PS> getCustomGlobalPSForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String speciality) {
		List<PS> response = null;
		try {
			AggregationResults<PS> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomGlobalAggregationForAdmin(page, size, updatedTime, discarded,
							searchTerm, "ps", speciality, null, null), PSCollection.class, PS.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PS");
		}
		return response;
	}

	private List<PS> getGlobalPSForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PS> response = null;

		try {
			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "ps", speciality, null, null), PSCollection.class,
					PS.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PS");
		}
		return response;
	}

	private List<PS> getCustomPSForAdmin(int page, int size, String updatedTime, Boolean discarded, String searchTerm,
			String speciality) {
		List<PS> response = null;
		try {
			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregationForAdmin(page,
					size, updatedTime, discarded, searchTerm, "ps", speciality, null, null), PSCollection.class,
					PS.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PS");
		}
		return response;
	}

	@Override
	@Transactional
	public ProcedureNote addEditProcedureNote(ProcedureNote precedureNote) {
		try {
			ProcedureNoteCollection procedureNoteCollection = new ProcedureNoteCollection();
			BeanUtil.map(precedureNote, procedureNoteCollection);
			if (DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getId())) {
				procedureNoteCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(procedureNoteCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						procedureNoteCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					procedureNoteCollection.setCreatedBy("ADMIN");
				}
			} else {
				ProcedureNoteCollection oldProcedureNoteCollection = procedureNoteRepository
						.findById(procedureNoteCollection.getId()).orElse(null);
				procedureNoteCollection.setCreatedBy(oldProcedureNoteCollection.getCreatedBy());
				procedureNoteCollection.setCreatedTime(oldProcedureNoteCollection.getCreatedTime());
				procedureNoteCollection.setDiscarded(oldProcedureNoteCollection.getDiscarded());
			}
			procedureNoteCollection = procedureNoteRepository.save(procedureNoteCollection);

			BeanUtil.map(procedureNoteCollection, precedureNote);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return precedureNote;
	}

	@Override
	public ProcedureNote deleteProcedureNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ProcedureNote response = null;
		try {
			ProcedureNoteCollection procedureNoteCollection = procedureNoteRepository.findById(new ObjectId(id)).orElse(null);
			if (procedureNoteCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getDoctorId(),
						procedureNoteCollection.getHospitalId(), procedureNoteCollection.getLocationId())) {
					if (procedureNoteCollection.getDoctorId().toString().equals(doctorId)
							&& procedureNoteCollection.getHospitalId().toString().equals(hospitalId)
							&& procedureNoteCollection.getLocationId().toString().equals(locationId)) {

						procedureNoteCollection.setDiscarded(discarded);
						procedureNoteCollection.setUpdatedTime(new Date());
						procedureNoteRepository.save(procedureNoteCollection);
						response = new ProcedureNote();
						BeanUtil.map(procedureNoteCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					procedureNoteCollection.setDiscarded(discarded);
					procedureNoteCollection.setUpdatedTime(new Date());
					procedureNoteRepository.save(procedureNoteCollection);
					response = new ProcedureNote();
					BeanUtil.map(procedureNoteCollection, response);
				}
			} else {
				logger.warn("Holter not found!");
				throw new BusinessException(ServiceError.NoRecord, "Procedure Note not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private List<ProcedureNote> getCustomProcedureNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ProcedureNote> response = null;
		try {
			AggregationResults<ProcedureNote> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ProcedureNote> getCustomGlobalProcedureNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ProcedureNote> response = new ArrayList<ProcedureNote>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ProcedureNote> getGlobalProcedureNote(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<ProcedureNote> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	@Override
	@Transactional
	public PresentingComplaintNose addEditPCNose(PresentingComplaintNose presentingComplaintNotes) {
		try {
			PresentingComplaintNoseCollection presentingComplaintNotesCollection = new PresentingComplaintNoseCollection();
			BeanUtil.map(presentingComplaintNotes, presentingComplaintNotesCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintNotesCollection.getId())) {
				presentingComplaintNotesCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintNotesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(presentingComplaintNotesCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentingComplaintNotesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintNotesCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintNoseCollection oldPresentingComplaintNotesCollection = presentingComplaintNotesRepository
						.findById(presentingComplaintNotesCollection.getId()).orElse(null);
				presentingComplaintNotesCollection.setCreatedBy(oldPresentingComplaintNotesCollection.getCreatedBy());
				presentingComplaintNotesCollection
						.setCreatedTime(oldPresentingComplaintNotesCollection.getCreatedTime());
				presentingComplaintNotesCollection.setDiscarded(oldPresentingComplaintNotesCollection.getDiscarded());
			}
			presentingComplaintNotesCollection = presentingComplaintNotesRepository
					.save(presentingComplaintNotesCollection);

			BeanUtil.map(presentingComplaintNotesCollection, presentingComplaintNotes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintNotes;
	}

	@Override
	@Transactional
	public EarsExamination addEditEarsExam(EarsExamination earsExamination) {
		try {
			EarsExaminationCollection earsExaminationCollection = new EarsExaminationCollection();
			BeanUtil.map(earsExamination, earsExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getId())) {
				earsExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(earsExaminationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						earsExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					earsExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				EarsExaminationCollection oldEarsExaminationCollection = earsExaminationRepository
						.findById(earsExaminationCollection.getId()).orElse(null);
				earsExaminationCollection.setCreatedBy(oldEarsExaminationCollection.getCreatedBy());
				earsExaminationCollection.setCreatedTime(oldEarsExaminationCollection.getCreatedTime());
				earsExaminationCollection.setDiscarded(oldEarsExaminationCollection.getDiscarded());
			}
			earsExaminationCollection = earsExaminationRepository.save(earsExaminationCollection);

			BeanUtil.map(earsExaminationCollection, earsExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return earsExamination;
	}

	@Override
	@Transactional
	public NeckExamination addEditNeckExam(NeckExamination neckExamination) {
		try {
			NeckExaminationCollection neckExaminationCollection = new NeckExaminationCollection();
			BeanUtil.map(neckExamination, neckExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getId())) {
				neckExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(neckExaminationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						neckExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					neckExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				NeckExaminationCollection oldNeckExaminationCollection = neckExaminationRepository
						.findById(neckExaminationCollection.getId()).orElse(null);
				neckExaminationCollection.setCreatedBy(oldNeckExaminationCollection.getCreatedBy());
				neckExaminationCollection.setCreatedTime(oldNeckExaminationCollection.getCreatedTime());
				neckExaminationCollection.setDiscarded(oldNeckExaminationCollection.getDiscarded());
			}
			neckExaminationCollection = neckExaminationRepository.save(neckExaminationCollection);

			BeanUtil.map(neckExaminationCollection, neckExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return neckExamination;
	}

	@Override
	@Transactional
	public NoseExamination addEditNoseExam(NoseExamination noseExamination) {
		try {
			NoseExaminationCollection noseExaminationCollection = new NoseExaminationCollection();
			BeanUtil.map(noseExamination, noseExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getId())) {
				noseExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(noseExaminationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						noseExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					noseExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				NoseExaminationCollection oldNoseExaminationCollection = noseExaminationRepository
						.findById(noseExaminationCollection.getId()).orElse(null);
				noseExaminationCollection.setCreatedBy(oldNoseExaminationCollection.getCreatedBy());
				noseExaminationCollection.setCreatedTime(oldNoseExaminationCollection.getCreatedTime());
				noseExaminationCollection.setDiscarded(oldNoseExaminationCollection.getDiscarded());
			}
			noseExaminationCollection = noseExaminationRepository.save(noseExaminationCollection);

			BeanUtil.map(noseExaminationCollection, noseExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return noseExamination;
	}

	@Override
	@Transactional
	public OralCavityAndThroatExamination addEditOralCavityThroatExam(
			OralCavityAndThroatExamination oralCavityAndThroatExamination) {
		try {
			OralCavityAndThroatExaminationCollection oralCavityAndThroatExaminationCollection = new OralCavityAndThroatExaminationCollection();
			BeanUtil.map(oralCavityAndThroatExamination, oralCavityAndThroatExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getId())) {
				oralCavityAndThroatExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(oralCavityAndThroatExaminationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						oralCavityAndThroatExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					oralCavityAndThroatExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				OralCavityAndThroatExaminationCollection oldOralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
						.findById(oralCavityAndThroatExaminationCollection.getId()).orElse(null);
				oralCavityAndThroatExaminationCollection
						.setCreatedBy(oldOralCavityAndThroatExaminationCollection.getCreatedBy());
				oralCavityAndThroatExaminationCollection
						.setCreatedTime(oldOralCavityAndThroatExaminationCollection.getCreatedTime());
				oralCavityAndThroatExaminationCollection
						.setDiscarded(oldOralCavityAndThroatExaminationCollection.getDiscarded());
			}
			oralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
					.save(oralCavityAndThroatExaminationCollection);

			BeanUtil.map(oralCavityAndThroatExaminationCollection, oralCavityAndThroatExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return oralCavityAndThroatExamination;
	}

	@Override
	@Transactional
	public IndirectLarygoscopyExamination addEditIndirectLarygoscopyExam(
			IndirectLarygoscopyExamination indirectLarygoscopyExamination) {
		try {
			IndirectLarygoscopyExaminationCollection indirectLarygoscopyExaminationCollection = new IndirectLarygoscopyExaminationCollection();
			BeanUtil.map(indirectLarygoscopyExamination, indirectLarygoscopyExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExaminationCollection.getId())) {
				indirectLarygoscopyExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExamination.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(indirectLarygoscopyExaminationCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						indirectLarygoscopyExamination
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					indirectLarygoscopyExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				IndirectLarygoscopyExaminationCollection oldIndirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
						.findById(indirectLarygoscopyExaminationCollection.getId()).orElse(null);
				indirectLarygoscopyExaminationCollection
						.setCreatedBy(oldIndirectLarygoscopyExaminationCollection.getCreatedBy());
				indirectLarygoscopyExaminationCollection
						.setCreatedTime(oldIndirectLarygoscopyExaminationCollection.getCreatedTime());
				indirectLarygoscopyExaminationCollection
						.setDiscarded(oldIndirectLarygoscopyExaminationCollection.getDiscarded());
			}
			indirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
					.save(indirectLarygoscopyExaminationCollection);

			BeanUtil.map(indirectLarygoscopyExaminationCollection, indirectLarygoscopyExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return indirectLarygoscopyExamination;
	}

	@Override
	@Transactional
	public PresentingComplaintEars addEditPCEars(PresentingComplaintEars presentingComplaintEars) {
		try {
			PresentingComplaintEarsCollection presentingComplaintEarsCollection = new PresentingComplaintEarsCollection();
			BeanUtil.map(presentingComplaintEars, presentingComplaintEarsCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getId())) {
				presentingComplaintEarsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(presentingComplaintEarsCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentingComplaintEarsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintEarsCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintEarsCollection oldPresentingComplaintEarCollection = presentingComplaintEarsRepository
						.findById(presentingComplaintEarsCollection.getId()).orElse(null);
				presentingComplaintEarsCollection.setCreatedBy(oldPresentingComplaintEarCollection.getCreatedBy());
				presentingComplaintEarsCollection.setCreatedTime(oldPresentingComplaintEarCollection.getCreatedTime());
				presentingComplaintEarsCollection.setDiscarded(oldPresentingComplaintEarCollection.getDiscarded());
			}
			presentingComplaintEarsCollection = presentingComplaintEarsRepository
					.save(presentingComplaintEarsCollection);

			BeanUtil.map(presentingComplaintEarsCollection, presentingComplaintEars);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintEars;
	}

	@Override
	@Transactional
	public PresentingComplaintThroat addEditPCThroat(PresentingComplaintThroat presentingComplaintThroat) {
		try {
			PresentingComplaintThroatCollection presentingComplaintThroatCollection = new PresentingComplaintThroatCollection();
			BeanUtil.map(presentingComplaintThroat, presentingComplaintThroatCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getId())) {
				presentingComplaintThroatCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(presentingComplaintThroatCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentingComplaintThroatCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintThroatCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintThroatCollection oldPresentingComplaintThroatCollection = presentingComplaintThroatRepository
						.findById(presentingComplaintThroatCollection.getId()).orElse(null);
				presentingComplaintThroatCollection.setCreatedBy(oldPresentingComplaintThroatCollection.getCreatedBy());
				presentingComplaintThroatCollection
						.setCreatedTime(oldPresentingComplaintThroatCollection.getCreatedTime());
				presentingComplaintThroatCollection.setDiscarded(oldPresentingComplaintThroatCollection.getDiscarded());
			}
			presentingComplaintThroatCollection = presentingComplaintThroatRepository
					.save(presentingComplaintThroatCollection);

			BeanUtil.map(presentingComplaintThroatCollection, presentingComplaintThroat);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintThroat;
	}

	@Override
	@Transactional
	public PresentingComplaintOralCavity addEditPCOralCavity(
			PresentingComplaintOralCavity presentingComplaintOralCavity) {
		try {
			PresentingComplaintOralCavityCollection presentingComplaintOralCavityCollection = new PresentingComplaintOralCavityCollection();
			BeanUtil.map(presentingComplaintOralCavity, presentingComplaintOralCavityCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getId())) {
				presentingComplaintOralCavityCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(presentingComplaintOralCavityCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						presentingComplaintOralCavityCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintOralCavityCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintOralCavityCollection oldPresentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
						.findById(presentingComplaintOralCavityCollection.getId()).orElse(null);
				presentingComplaintOralCavityCollection
						.setCreatedBy(oldPresentingComplaintOralCavityCollection.getCreatedBy());
				presentingComplaintOralCavityCollection
						.setCreatedTime(oldPresentingComplaintOralCavityCollection.getCreatedTime());
				presentingComplaintOralCavityCollection
						.setDiscarded(oldPresentingComplaintOralCavityCollection.getDiscarded());
			}
			presentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
					.save(presentingComplaintOralCavityCollection);

			BeanUtil.map(presentingComplaintOralCavityCollection, presentingComplaintOralCavity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintOralCavity;
	}

	@Override
	public PresentingComplaintNose deletePCNose(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintNose response = null;
		try {
			PresentingComplaintNoseCollection presentingComplaintNoseCollection = presentingComplaintNotesRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentingComplaintNoseCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintNoseCollection.getDoctorId(),
						presentingComplaintNoseCollection.getHospitalId(),
						presentingComplaintNoseCollection.getLocationId())) {
					if (presentingComplaintNoseCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintNoseCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintNoseCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintNoseCollection.setDiscarded(discarded);
						presentingComplaintNoseCollection.setUpdatedTime(new Date());
						presentingComplaintNotesRepository.save(presentingComplaintNoseCollection);
						response = new PresentingComplaintNose();
						BeanUtil.map(presentingComplaintNoseCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintNoseCollection.setDiscarded(discarded);
					presentingComplaintNoseCollection.setUpdatedTime(new Date());
					presentingComplaintNotesRepository.save(presentingComplaintNoseCollection);
					response = new PresentingComplaintNose();
					BeanUtil.map(presentingComplaintNoseCollection, response);
				}
			} else {
				logger.warn("PC nose not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC nose not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintEars deletePCEars(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintEars response = null;
		try {
			PresentingComplaintEarsCollection presentingComplaintEarsCollection = presentingComplaintEarsRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentingComplaintEarsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getDoctorId(),
						presentingComplaintEarsCollection.getHospitalId(),
						presentingComplaintEarsCollection.getLocationId())) {
					if (presentingComplaintEarsCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintEarsCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintEarsCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintEarsCollection.setDiscarded(discarded);
						presentingComplaintEarsCollection.setUpdatedTime(new Date());
						presentingComplaintEarsRepository.save(presentingComplaintEarsCollection);
						response = new PresentingComplaintEars();
						BeanUtil.map(presentingComplaintEarsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintEarsCollection.setDiscarded(discarded);
					presentingComplaintEarsCollection.setUpdatedTime(new Date());
					presentingComplaintEarsRepository.save(presentingComplaintEarsCollection);
					response = new PresentingComplaintEars();
					BeanUtil.map(presentingComplaintEarsCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintOralCavity deletePCOralCavity(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		PresentingComplaintOralCavity response = null;
		try {
			PresentingComplaintOralCavityCollection presentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentingComplaintOralCavityCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getDoctorId(),
						presentingComplaintOralCavityCollection.getHospitalId(),
						presentingComplaintOralCavityCollection.getLocationId())) {
					if (presentingComplaintOralCavityCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintOralCavityCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintOralCavityCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintOralCavityCollection.setDiscarded(discarded);
						presentingComplaintOralCavityCollection.setUpdatedTime(new Date());
						presentingComplaintOralCavityRepository.save(presentingComplaintOralCavityCollection);
						response = new PresentingComplaintOralCavity();
						BeanUtil.map(presentingComplaintOralCavityCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintOralCavityCollection.setDiscarded(discarded);
					presentingComplaintOralCavityCollection.setUpdatedTime(new Date());
					presentingComplaintOralCavityRepository.save(presentingComplaintOralCavityCollection);
					response = new PresentingComplaintOralCavity();
					BeanUtil.map(presentingComplaintOralCavityCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintThroat deletePCThroat(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintThroat response = null;
		try {
			PresentingComplaintThroatCollection presentingComplaintThroatCollection = presentingComplaintThroatRepository
					.findById(new ObjectId(id)).orElse(null);
			if (presentingComplaintThroatCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getDoctorId(),
						presentingComplaintThroatCollection.getHospitalId(),
						presentingComplaintThroatCollection.getLocationId())) {
					if (presentingComplaintThroatCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintThroatCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintThroatCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintThroatCollection.setDiscarded(discarded);
						presentingComplaintThroatCollection.setUpdatedTime(new Date());
						presentingComplaintThroatRepository.save(presentingComplaintThroatCollection);
						response = new PresentingComplaintThroat();
						BeanUtil.map(presentingComplaintThroatCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintThroatCollection.setDiscarded(discarded);
					presentingComplaintThroatCollection.setUpdatedTime(new Date());
					presentingComplaintThroatRepository.save(presentingComplaintThroatCollection);
					response = new PresentingComplaintThroat();
					BeanUtil.map(presentingComplaintThroatCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public NeckExamination deleteNeckExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		NeckExamination response = null;
		try {
			NeckExaminationCollection neckExaminationCollection = neckExaminationRepository.findById(new ObjectId(id)).orElse(null);
			if (neckExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getDoctorId(),
						neckExaminationCollection.getHospitalId(), neckExaminationCollection.getLocationId())) {
					if (neckExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& neckExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& neckExaminationCollection.getLocationId().toString().equals(locationId)) {

						neckExaminationCollection.setDiscarded(discarded);
						neckExaminationCollection.setUpdatedTime(new Date());
						neckExaminationRepository.save(neckExaminationCollection);
						response = new NeckExamination();
						BeanUtil.map(neckExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					neckExaminationCollection.setDiscarded(discarded);
					neckExaminationCollection.setUpdatedTime(new Date());
					neckExaminationRepository.save(neckExaminationCollection);
					response = new NeckExamination();
					BeanUtil.map(neckExaminationCollection, response);
				}
			} else {
				logger.warn("neck exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "neck exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public NoseExamination deleteNoseExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		NoseExamination response = null;
		try {
			NoseExaminationCollection noseExaminationCollection = noseExaminationRepository.findById(new ObjectId(id)).orElse(null);
			if (noseExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getDoctorId(),
						noseExaminationCollection.getHospitalId(), noseExaminationCollection.getLocationId())) {
					if (noseExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& noseExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& noseExaminationCollection.getLocationId().toString().equals(locationId)) {

						noseExaminationCollection.setDiscarded(discarded);
						noseExaminationCollection.setUpdatedTime(new Date());
						noseExaminationRepository.save(noseExaminationCollection);
						response = new NoseExamination();
						BeanUtil.map(noseExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					noseExaminationCollection.setDiscarded(discarded);
					noseExaminationCollection.setUpdatedTime(new Date());
					noseExaminationRepository.save(noseExaminationCollection);
					response = new NoseExamination();
					BeanUtil.map(noseExaminationCollection, response);
				}
			} else {
				logger.warn("nose exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "nose exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public OralCavityAndThroatExamination deleteOralCavityThroatExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		OralCavityAndThroatExamination response = null;
		try {
			OralCavityAndThroatExaminationCollection oralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
					.findById(new ObjectId(id)).orElse(null);
			if (oralCavityAndThroatExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getDoctorId(),
						oralCavityAndThroatExaminationCollection.getHospitalId(),
						oralCavityAndThroatExaminationCollection.getLocationId())) {
					if (oralCavityAndThroatExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& oralCavityAndThroatExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& oralCavityAndThroatExaminationCollection.getLocationId().toString().equals(locationId)) {

						oralCavityAndThroatExaminationCollection.setDiscarded(discarded);
						oralCavityAndThroatExaminationCollection.setUpdatedTime(new Date());
						oralCavityThroatExaminationRepository.save(oralCavityAndThroatExaminationCollection);
						response = new OralCavityAndThroatExamination();
						BeanUtil.map(oralCavityAndThroatExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					oralCavityAndThroatExaminationCollection.setDiscarded(discarded);
					oralCavityAndThroatExaminationCollection.setUpdatedTime(new Date());
					oralCavityThroatExaminationRepository.save(oralCavityAndThroatExaminationCollection);
					response = new OralCavityAndThroatExamination();
					BeanUtil.map(oralCavityAndThroatExaminationCollection, response);
				}
			} else {
				logger.warn("OralCavity And Throat Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "OralCavity And Throat Exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public EarsExamination deleteEarsExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		EarsExamination response = null;
		try {
			EarsExaminationCollection earsExaminationCollection = earsExaminationRepository.findById(new ObjectId(id)).orElse(null);
			if (earsExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getDoctorId(),
						earsExaminationCollection.getHospitalId(), earsExaminationCollection.getLocationId())) {
					if (earsExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& earsExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& earsExaminationCollection.getLocationId().toString().equals(locationId)) {

						earsExaminationCollection.setDiscarded(discarded);
						earsExaminationCollection.setUpdatedTime(new Date());
						earsExaminationRepository.save(earsExaminationCollection);
						response = new EarsExamination();
						BeanUtil.map(earsExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					earsExaminationCollection.setDiscarded(discarded);
					earsExaminationCollection.setUpdatedTime(new Date());
					earsExaminationRepository.save(earsExaminationCollection);
					response = new EarsExamination();
					BeanUtil.map(earsExaminationCollection, response);
				}
			} else {
				logger.warn("Ears Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "Ears Exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public IndirectLarygoscopyExamination deleteIndirectLarygoscopyExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		IndirectLarygoscopyExamination response = null;
		try {
			IndirectLarygoscopyExaminationCollection indirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
					.findById(new ObjectId(id)).orElse(null);
			if (indirectLarygoscopyExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExaminationCollection.getDoctorId(),
						indirectLarygoscopyExaminationCollection.getHospitalId(),
						indirectLarygoscopyExaminationCollection.getLocationId())) {
					if (indirectLarygoscopyExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& indirectLarygoscopyExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& indirectLarygoscopyExaminationCollection.getLocationId().toString().equals(locationId)) {

						indirectLarygoscopyExaminationCollection.setDiscarded(discarded);
						indirectLarygoscopyExaminationCollection.setUpdatedTime(new Date());
						indirectLarygoscopyExaminationRepository.save(indirectLarygoscopyExaminationCollection);
						response = new IndirectLarygoscopyExamination();
						BeanUtil.map(indirectLarygoscopyExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					indirectLarygoscopyExaminationCollection.setDiscarded(discarded);
					indirectLarygoscopyExaminationCollection.setUpdatedTime(new Date());
					indirectLarygoscopyExaminationRepository.save(indirectLarygoscopyExaminationCollection);
					response = new IndirectLarygoscopyExamination();
					BeanUtil.map(indirectLarygoscopyExaminationCollection, response);
				}
			} else {
				logger.warn("Indirect Larygoscopy Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "Indirect Larygoscopy Exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintNose> getGlobalPCNOse(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintNose> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintNose> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Nose");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintEars> getGlobalPCEars(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintEars> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintEars> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Ears");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintThroat> getGlobalPCThroat(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintThroat> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintThroat> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint Throat");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NoseExamination> getGlobalNoseExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<NoseExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<NoseExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Nose Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NeckExamination> getGlobalNeckExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<NeckExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<NeckExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Neck Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<IndirectLarygoscopyExamination> getGlobalIndirectLarygoscopyExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,
							null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Indirect Larygoscopy Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<OralCavityAndThroatExamination> getGlobalOralCavityAndThroat(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,
							null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting OralCavity And Throat Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<EarsExamination> getGlobalEarsExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<EarsExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<EarsExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Ears Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintOralCavity> getGlobalPCOralCavity(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint OralCavity");
		}
		return response;
	}

	private List<PresentingComplaintNose> getCustomPCNose(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintNose> response = null;
		try {
			AggregationResults<PresentingComplaintNose> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Nose");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintNose> getCustomGlobalPCNOse(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintNose> response = new ArrayList<PresentingComplaintNose>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintNose> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Nose");
		}
		return response;

	}

	private List<PresentingComplaintEars> getCustomPCEars(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintEars> response = null;
		try {
			AggregationResults<PresentingComplaintEars> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Ears");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintEars> getCustomGlobalPCEars(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintEars> response = new ArrayList<PresentingComplaintEars>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintEars> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Presenting Complaint Ears");
		}
		return response;

	}

	private List<PresentingComplaintThroat> getCustomPCThroat(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintThroat> response = null;
		try {
			AggregationResults<PresentingComplaintThroat> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint Throat");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintThroat> getCustomGlobalPCThroat(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintThroat> response = new ArrayList<PresentingComplaintThroat>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintThroat> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint Throat");
		}
		return response;

	}

	private List<NeckExamination> getCustomNeckExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NeckExamination> response = null;
		try {
			AggregationResults<NeckExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Neck Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NeckExamination> getCustomGlobalNeckExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NeckExamination> response = new ArrayList<NeckExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<NeckExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Neck Examination");
		}
		return response;

	}

	private List<NoseExamination> getCustomNoseExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NoseExamination> response = null;
		try {
			AggregationResults<NoseExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Nose Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NoseExamination> getCustomGlobalNoseExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NoseExamination> response = new ArrayList<NoseExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<NoseExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Nose Examination");
		}
		return response;

	}

	private List<PresentingComplaintOralCavity> getCustomPCOralCavity(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = null;
		try {
			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint OralCavity");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintOralCavity> getCustomGlobalPCOralCavity(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = new ArrayList<PresentingComplaintOralCavity>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Presenting Complaint OralCavity");
		}
		return response;

	}

	private List<EarsExamination> getCustomEarsExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<EarsExamination> response = null;
		try {
			AggregationResults<EarsExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Ears Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<EarsExamination> getCustomGlobalEarsExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<EarsExamination> response = new ArrayList<EarsExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<EarsExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Ears Examination");
		}
		return response;

	}

	private List<OralCavityAndThroatExamination> getCustomOralCavityAndThroatExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = null;
		try {
			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting OralCavity And Throat Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<OralCavityAndThroatExamination> getCustomGlobalOralCavityAndThroatExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = new ArrayList<OralCavityAndThroatExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting OralCavity And Throat Examination");
		}
		return response;

	}

	private List<IndirectLarygoscopyExamination> getCustomIndirectLarygoscopyExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = null;
		try {
			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Indirect Larygoscopy Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<IndirectLarygoscopyExamination> getCustomGlobalIndirectLarygoscopyExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = new ArrayList<IndirectLarygoscopyExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findByIdIn(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Indirect Larygoscopy Examination");

		}
		return response;
	}

	
}
