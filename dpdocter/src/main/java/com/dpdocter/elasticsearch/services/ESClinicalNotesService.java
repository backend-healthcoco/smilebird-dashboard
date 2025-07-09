package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;

public interface ESClinicalNotesService {

	boolean addComplaints(ESComplaintsDocument request);

	boolean addDiagnoses(ESDiagnosesDocument request);

	boolean addNotes(ESNotesDocument request);

	boolean addDiagrams(ESDiagramsDocument request);

	// List<ESDiagramsDocument> searchDiagramsBySpeciality(String searchTerm);

	boolean addInvestigations(ESInvestigationsDocument request);

	boolean addObservations(ESObservationsDocument request);

	List<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	List<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	boolean addPresentComplaint(ESPresentComplaintDocument request);

	boolean addPresentComplaintHistory(ESPresentComplaintHistoryDocument request);

	boolean addProvisionalDiagnosis(ESProvisionalDiagnosisDocument request);

	boolean addSystemExam(ESSystemExamDocument request);

	boolean addGeneralExam(ESGeneralExamDocument request);

	boolean addMenstrualHistory(ESMenstrualHistoryDocument request);

	boolean addObstetricsHistory(ESObstetricHistoryDocument request);

	boolean addIndicationOfUSG(ESIndicationOfUSGDocument request);

	boolean addPA(ESPADocument request);

	boolean addPV(ESPVDocument request);

	boolean addPS(ESPSDocument request);

	boolean addXRayDetails(ESXRayDetailsDocument request);

	boolean addECGDetails(ESECGDetailsDocument request);

	boolean addEcho(ESEchoDocument request);

	boolean addHolter(ESHolterDocument request);

	boolean addProcedureNote(ESProcedureNoteDocument request);

	boolean addPCNose(ESPresentingComplaintNoseDocument request);

	boolean addPCEars(ESPresentingComplaintEarsDocument request);

	boolean addPCThroat(ESPresentingComplaintThroatDocument request);

	boolean addPCOralCavity(ESPresentingComplaintOralCavityDocument request);

	boolean addNeckExam(ESNeckExaminationDocument request);

	boolean addNoseExam(ESNoseExaminationDocument request);

	boolean addEarsExam(ESEarsExaminationDocument request);

	boolean addOralCavityThroatExam(ESOralCavityAndThroatExaminationDocument request);

	boolean addIndirectLarygoscopyExam(ESIndirectLarygoscopyExaminationDocument request);

	List<ESProcedureNoteDocument> searchProcedureNote(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

}
