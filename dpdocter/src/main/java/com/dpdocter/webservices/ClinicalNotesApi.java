package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CLINICAL_NOTES_BASE_URL, produces = MediaType.APPLICATION_JSON , consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CLINICAL_NOTES_BASE_URL, description = "Endpoint for clinical notes")
public class ClinicalNotesApi {

	private static Logger logger = LogManager.getLogger(ClinicalNotesApi.class.getName());

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private ESClinicalNotesService esClinicalNotesService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.ADD_COMPLAINT)
	public Response<Complaint> addComplaint(@RequestBody Complaint request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Complaint complaint = clinicalNotesService.addEditComplaint(request);

		transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
		ESComplaintsDocument esComplaints = new ESComplaintsDocument();
		BeanUtil.map(complaint, esComplaints);
		esClinicalNotesService.addComplaints(esComplaints);

		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.ADD_OBSERVATION)
	public Response<Observation> addObservation(@RequestBody Observation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getObservation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Observation observation = clinicalNotesService.addEditObservation(request);

		transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
		ESObservationsDocument esObservations = new ESObservationsDocument();
		BeanUtil.map(observation, esObservations);
		esClinicalNotesService.addObservations(esObservations);
		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.ADD_INVESTIGATION)
	public Response<Investigation> addInvestigation(@RequestBody Investigation request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getInvestigation())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Investigation investigation = clinicalNotesService.addEditInvestigation(request);

		transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION, false);
		ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
		BeanUtil.map(investigation, esInvestigations);
		esClinicalNotesService.addInvestigations(esInvestigations);

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGNOSIS)
	public Response<Diagnoses> addDiagnosis(@RequestBody Diagnoses request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagnoses diagnosis = clinicalNotesService.addEditDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(diagnosis.getId()), Resource.DIAGNOSIS, false);
		ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
		BeanUtil.map(diagnosis, esDiagnoses);
		esClinicalNotesService.addDiagnoses(esDiagnoses);

		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_NOTES)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_NOTES, notes = PathProxy.ClinicalNotesUrls.ADD_NOTES)
	public Response<Notes> addNotes(Notes request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getNote())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Notes notes = clinicalNotesService.addEditNotes(request);

		transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
		ESNotesDocument esNotes = new ESNotesDocument();
		BeanUtil.map(notes, esNotes);
		esClinicalNotesService.addNotes(esNotes);

		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.ADD_DIAGRAM)
	public Response<Diagram> addDiagram(@RequestBody Diagram request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())
				|| request.getDiagram() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagram diagram = clinicalNotesService.addEditDiagram(request);
		transactionalManagementService.addResource(new ObjectId(diagram.getId()), Resource.DIAGRAM, false);
		ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
		BeanUtil.map(diagram, esDiagrams);
		esClinicalNotesService.addDiagrams(esDiagrams);

		if (diagram.getDiagramUrl() != null) {
			diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT)
	public Response<PresentComplaint> addPresentComplaints(@RequestBody PresentComplaint request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPresentComplaint())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaint presentComplaint = clinicalNotesService.addEditPresentComplaint(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()), Resource.PRESENT_COMPLAINT,
				false);
		ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
		BeanUtil.map(presentComplaint, esPresentComplaint);
		esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> addPresentComplaintsHistory(@RequestBody PresentComplaintHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPresentComplaintHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.addEditPresentComplaintHistory(request);

		transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
				Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
		ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
		BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
		esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.ADD_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> addProvisionalDiagnosis(@RequestBody ProvisionalDiagnosis request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getProvisionalDiagnosis())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.addEditProvisionalDiagnosis(request);

		transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
				Resource.PROVISIONAL_DIAGNOSIS, false);
		ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
		BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
		esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_SYSTEM_EXAM)
	public Response<SystemExam> addSystemExam(@RequestBody SystemExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getSystemExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		SystemExam systemExam = clinicalNotesService.addEditSystemExam(request);

		transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
				false);
		ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
		BeanUtil.map(systemExam, esSystemExam);
		esClinicalNotesService.addSystemExam(esSystemExam);
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM, notes = PathProxy.ClinicalNotesUrls.ADD_GENERAL_EXAM)
	public Response<GeneralExam> addGeneralExam(@RequestBody GeneralExam request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getGeneralExam())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		GeneralExam generalExam = clinicalNotesService.addEditGeneralExam(request);

		transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
				false);
		ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
		BeanUtil.map(generalExam, esGeneralExam);
		esClinicalNotesService.addGeneralExam(esGeneralExam);
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> addMenstrualHistory(@RequestBody MenstrualHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getMenstrualHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		MenstrualHistory menstrualHistory = clinicalNotesService.addEditMenstrualHistory(request);

		transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()), Resource.MENSTRUAL_HISTORY,
				false);
		ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
		BeanUtil.map(menstrualHistory, esMenstrualHistory);
		esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	public Response<ObstetricHistory> addObstetricHistory(@RequestBody ObstetricHistory request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getObstetricHistory())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		ObstetricHistory obstetricHistory = clinicalNotesService.addEditObstetricHistory(request);

		transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()), Resource.OBSTETRIC_HISTORY,
				false);
		ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
		BeanUtil.map(obstetricHistory, esObstetricHistory);
		esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG, notes = PathProxy.ClinicalNotesUrls.ADD_INDICATION_OF_USG)
	public Response<IndicationOfUSG> addIndicationOfUSG(@RequestBody IndicationOfUSG request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getIndicationOfUSG())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		IndicationOfUSG indicationOfUSG = clinicalNotesService.addEditIndicationOfUSG(request);

		transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()), Resource.INDICATION_OF_USG,
				false);
		ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
		BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
		esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PA)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PA, notes = PathProxy.ClinicalNotesUrls.ADD_PA)
	public Response<PA> addEditPA(@RequestBody PA request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPa())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PA pa = clinicalNotesService.addEditPA(request);

		transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
		ESPADocument espa = new ESPADocument();
		BeanUtil.map(pa, espa);
		esClinicalNotesService.addPA(espa);
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PV)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PV, notes = PathProxy.ClinicalNotesUrls.ADD_PV)
	public Response<PV> addEditPV(@RequestBody PV request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPv())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PV pv = clinicalNotesService.addEditPV(request);

		transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
		ESPVDocument espv = new ESPVDocument();
		BeanUtil.map(pv, espv);
		esClinicalNotesService.addPV(espv);
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	@PostMapping(value = PathProxy.ClinicalNotesUrls.ADD_PS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_PS, notes = PathProxy.ClinicalNotesUrls.ADD_PS)
	public Response<PS> addEditPS(@RequestBody PS request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPs())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		PS ps = clinicalNotesService.addEditPS(request);

		transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
		ESPSDocument esps = new ESPSDocument();
		BeanUtil.map(ps, esps);
		esClinicalNotesService.addPS(esps);
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.DELETE_COMPLAINT)
	public Response<Complaint> deleteComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Complaint complaint = clinicalNotesService.deleteComplaint(id, doctorId, locationId, hospitalId, discarded);

		if (complaint != null) {
			transactionalManagementService.addResource(new ObjectId(complaint.getId()), Resource.COMPLAINT, false);
			ESComplaintsDocument esComplaints = new ESComplaintsDocument();
			BeanUtil.map(complaint, esComplaints);
			esClinicalNotesService.addComplaints(esComplaints);
		}
		Response<Complaint> response = new Response<Complaint>();
		response.setData(complaint);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION, notes = PathProxy.ClinicalNotesUrls.DELETE_OBSERVATION)
	public Response<Observation> deleteObservation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Observation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Observation observation = clinicalNotesService.deleteObservation(id, doctorId, locationId, hospitalId,
				discarded);
		if (observation != null) {
			transactionalManagementService.addResource(new ObjectId(observation.getId()), Resource.OBSERVATION, false);
			ESObservationsDocument esObservations = new ESObservationsDocument();
			BeanUtil.map(observation, esObservations);
			esClinicalNotesService.addObservations(esObservations);
		}

		Response<Observation> response = new Response<Observation>();
		response.setData(observation);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION, notes = PathProxy.ClinicalNotesUrls.DELETE_INVESTIGATION)
	public Response<Investigation> deleteInvestigation(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Investigation Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Investigation investigation = clinicalNotesService.deleteInvestigation(id, doctorId, locationId, hospitalId,
				discarded);
		if (investigation != null) {
			transactionalManagementService.addResource(new ObjectId(investigation.getId()), Resource.INVESTIGATION,
					false);
			ESInvestigationsDocument esInvestigations = new ESInvestigationsDocument();
			BeanUtil.map(investigation, esInvestigations);
			esClinicalNotesService.addInvestigations(esInvestigations);
		}

		Response<Investigation> response = new Response<Investigation>();
		response.setData(investigation);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGNOSIS)
	public Response<Diagnoses> deleteDiagnosis(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Diagnoses diagnoses = clinicalNotesService.deleteDiagnosis(id, doctorId, locationId, hospitalId, discarded);
		if (diagnoses != null) {
			transactionalManagementService.addResource(new ObjectId(diagnoses.getId()), Resource.DIAGNOSIS, false);
			ESDiagnosesDocument esDiagnoses = new ESDiagnosesDocument();
			BeanUtil.map(diagnoses, esDiagnoses);
			esClinicalNotesService.addDiagnoses(esDiagnoses);
		}
		Response<Diagnoses> response = new Response<Diagnoses>();
		response.setData(diagnoses);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_NOTE, notes = PathProxy.ClinicalNotesUrls.DELETE_NOTE)
	public Response<Notes> deleteNote(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Note Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Notes notes = clinicalNotesService.deleteNotes(id, doctorId, locationId, hospitalId, discarded);
		if (notes != null) {
			transactionalManagementService.addResource(new ObjectId(notes.getId()), Resource.NOTES, false);
			ESNotesDocument esNotes = new ESNotesDocument();
			BeanUtil.map(notes, esNotes);
			esClinicalNotesService.addNotes(esNotes);
		}
		Response<Notes> response = new Response<Notes>();
		response.setData(notes);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM, notes = PathProxy.ClinicalNotesUrls.DELETE_DIAGRAM)
	public Response<Diagram> deleteDiagram(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Diagram Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagram, Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Diagram diagram = clinicalNotesService.deleteDiagram(id, doctorId, locationId, hospitalId, discarded);
		if (diagram != null) {
			ESDiagramsDocument esDiagrams = new ESDiagramsDocument();
			BeanUtil.map(diagram, esDiagrams);
			esClinicalNotesService.addDiagrams(esDiagrams);
		}
		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS, notes = PathProxy.ClinicalNotesUrls.DELETE_PROVISIONAL_DIAGNOSIS)
	public Response<ProvisionalDiagnosis> deleteProvisionalDiagnosis(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ProvisionalDiagnosis provisionalDiagnosis = clinicalNotesService.deleteProvisionalDiagnosis(id, doctorId,
				locationId, hospitalId, discarded);

		if (provisionalDiagnosis != null) {
			transactionalManagementService.addResource(new ObjectId(provisionalDiagnosis.getId()),
					Resource.PROVISIONAL_DIAGNOSIS, false);
			ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new ESProvisionalDiagnosisDocument();
			BeanUtil.map(provisionalDiagnosis, esProvisionalDiagnosis);
			esClinicalNotesService.addProvisionalDiagnosis(esProvisionalDiagnosis);
		}
		Response<ProvisionalDiagnosis> response = new Response<ProvisionalDiagnosis>();
		response.setData(provisionalDiagnosis);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY, notes = PathProxy.ClinicalNotesUrls.ADD_OBSTETRICS_HISTORY)
	public Response<ObstetricHistory> deleteObstetricHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		ObstetricHistory obstetricHistory = clinicalNotesService.deleteObstetricHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (obstetricHistory != null) {
			transactionalManagementService.addResource(new ObjectId(obstetricHistory.getId()),
					Resource.OBSTETRIC_HISTORY, false);
			ESObstetricHistoryDocument esObstetricHistory = new ESObstetricHistoryDocument();
			BeanUtil.map(obstetricHistory, esObstetricHistory);
			esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
		}
		Response<ObstetricHistory> response = new Response<ObstetricHistory>();
		response.setData(obstetricHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT, notes = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT)
	public Response<PresentComplaint> deletePresentComplaint(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaint presentComplaint = clinicalNotesService.deletePresentComplaint(id, doctorId, locationId,
				hospitalId, discarded);

		if (presentComplaint != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaint.getId()),
					Resource.PRESENT_COMPLAINT, false);
			ESPresentComplaintDocument esPresentComplaint = new ESPresentComplaintDocument();
			BeanUtil.map(presentComplaint, esPresentComplaint);
			esClinicalNotesService.addPresentComplaint(esPresentComplaint);
		}
		Response<PresentComplaint> response = new Response<PresentComplaint>();
		response.setData(presentComplaint);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY, notes = PathProxy.ClinicalNotesUrls.DELETE_PRESENT_COMPLAINT_HISTORY)
	public Response<PresentComplaintHistory> deletePresentComplaintHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PresentComplaintHistory presentComplaintHistory = clinicalNotesService.deletePresentComplaintHistory(id,
				doctorId, locationId, hospitalId, discarded);

		if (presentComplaintHistory != null) {
			transactionalManagementService.addResource(new ObjectId(presentComplaintHistory.getId()),
					Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
			ESPresentComplaintHistoryDocument esPresentComplaintHistory = new ESPresentComplaintHistoryDocument();
			BeanUtil.map(presentComplaintHistory, esPresentComplaintHistory);
			esClinicalNotesService.addPresentComplaintHistory(esPresentComplaintHistory);
		}
		Response<PresentComplaintHistory> response = new Response<PresentComplaintHistory>();
		response.setData(presentComplaintHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_GENERAL_EXAM)
	public Response<GeneralExam> deleteGeneralExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		GeneralExam generalExam = clinicalNotesService.deleteGeneralExam(id, doctorId, locationId, hospitalId,
				discarded);

		if (generalExam != null) {
			transactionalManagementService.addResource(new ObjectId(generalExam.getId()), Resource.GENERAL_EXAMINATION,
					false);
			ESGeneralExamDocument esGeneralExam = new ESGeneralExamDocument();
			BeanUtil.map(generalExam, esGeneralExam);
			esClinicalNotesService.addGeneralExam(esGeneralExam);
		}
		Response<GeneralExam> response = new Response<GeneralExam>();
		response.setData(generalExam);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM, notes = PathProxy.ClinicalNotesUrls.DELETE_SYSTEM_EXAM)
	public Response<SystemExam> deleteSystemExam(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		SystemExam systemExam = clinicalNotesService.deleteSystemExam(id, doctorId, locationId, hospitalId, discarded);

		if (systemExam != null) {
			transactionalManagementService.addResource(new ObjectId(systemExam.getId()), Resource.SYSTEMIC_EXAMINATION,
					false);
			ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
			BeanUtil.map(systemExam, esSystemExam);
			esClinicalNotesService.addSystemExam(esSystemExam);
		}
		Response<SystemExam> response = new Response<SystemExam>();
		response.setData(systemExam);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY, notes = PathProxy.ClinicalNotesUrls.DELETE_MENSTRUAL_HISTORY)
	public Response<MenstrualHistory> deleteMenstrualHistory(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		MenstrualHistory menstrualHistory = clinicalNotesService.deleteMenstrualHistory(id, doctorId, locationId,
				hospitalId, discarded);

		if (menstrualHistory != null) {
			transactionalManagementService.addResource(new ObjectId(menstrualHistory.getId()),
					Resource.MENSTRUAL_HISTORY, false);
			ESMenstrualHistoryDocument esMenstrualHistory = new ESMenstrualHistoryDocument();
			BeanUtil.map(menstrualHistory, esMenstrualHistory);
			esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
		}
		Response<MenstrualHistory> response = new Response<MenstrualHistory>();
		response.setData(menstrualHistory);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG, notes = PathProxy.ClinicalNotesUrls.DELETE_INDICATION_OF_USG)
	public Response<IndicationOfUSG> deleteIndicationOfUSG(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		IndicationOfUSG indicationOfUSG = clinicalNotesService.deleteIndicationOfUSG(id, doctorId, locationId,
				hospitalId, discarded);

		if (indicationOfUSG != null) {
			transactionalManagementService.addResource(new ObjectId(indicationOfUSG.getId()),
					Resource.INDICATION_OF_USG, false);
			ESIndicationOfUSGDocument esIndicationOfUSG = new ESIndicationOfUSGDocument();
			BeanUtil.map(indicationOfUSG, esIndicationOfUSG);
			esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG);
		}
		Response<IndicationOfUSG> response = new Response<IndicationOfUSG>();
		response.setData(indicationOfUSG);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PA)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PA, notes = PathProxy.ClinicalNotesUrls.DELETE_PA)
	public Response<PA> deletePA(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PA pa = clinicalNotesService.deletePA(id, doctorId, locationId, hospitalId, discarded);

		if (pa != null) {
			transactionalManagementService.addResource(new ObjectId(pa.getId()), Resource.PA, false);
			ESPADocument espa = new ESPADocument();
			BeanUtil.map(pa, espa);
			esClinicalNotesService.addPA(espa);
		}
		Response<PA> response = new Response<PA>();
		response.setData(pa);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PV)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PV, notes = PathProxy.ClinicalNotesUrls.DELETE_PV)
	public Response<PV> deletePV(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PV pv = clinicalNotesService.deletePV(id, doctorId, locationId, hospitalId, discarded);

		if (pv != null) {
			transactionalManagementService.addResource(new ObjectId(pv.getId()), Resource.PV, false);
			ESPVDocument espv = new ESPVDocument();
			BeanUtil.map(pv, espv);
			esClinicalNotesService.addPV(espv);
		}
		Response<PV> response = new Response<PV>();
		response.setData(pv);
		return response;
	}

	@DeleteMapping(value = PathProxy.ClinicalNotesUrls.DELETE_PS)
	@ApiOperation(value = PathProxy.ClinicalNotesUrls.DELETE_PS, notes = PathProxy.ClinicalNotesUrls.DELETE_PS)
	public Response<PS> deletePS(@PathVariable(value = "id") String id, @PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue = "true")  Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Complaint Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		PS ps = clinicalNotesService.deletePS(id, doctorId, locationId, hospitalId, discarded);

		if (ps != null) {
			transactionalManagementService.addResource(new ObjectId(ps.getId()), Resource.PS, false);
			ESPSDocument esps = new ESPSDocument();
			BeanUtil.map(ps, esps);
			esClinicalNotesService.addPS(esps);
		}
		Response<PS> response = new Response<PS>();
		response.setData(ps);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
