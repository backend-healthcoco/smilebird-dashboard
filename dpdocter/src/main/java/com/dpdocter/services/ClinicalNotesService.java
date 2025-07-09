package com.dpdocter.services;

import java.util.List;

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
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.XRayDetails;
import com.dpdocter.response.MailResponse;

public interface ClinicalNotesService {

	Complaint addEditComplaint(Complaint complaint);

	Observation addEditObservation(Observation observation);

	Investigation addEditInvestigation(Investigation investigation);

	Diagnoses addEditDiagnosis(Diagnoses diagnosis);

	Notes addEditNotes(Notes notes);

	Diagram addEditDiagram(Diagram diagram);

	Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	PA addEditPA(PA pa);

	PA deletePA(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	PV deletePV(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	PV addEditPV(PV pv);

	PS addEditPS(PS ps);

	PS deletePS(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ProvisionalDiagnosis addEditProvisionalDiagnosis(ProvisionalDiagnosis provisionalDiagnosis);

	GeneralExam addEditGeneralExam(GeneralExam generalExam);

	SystemExam addEditSystemExam(SystemExam systemExam);

	MenstrualHistory addEditMenstrualHistory(MenstrualHistory menstrualHistory);

	PresentComplaint addEditPresentComplaint(PresentComplaint presentComplaint);

	PresentComplaintHistory addEditPresentComplaintHistory(PresentComplaintHistory presentComplaintHistory);

	ObstetricHistory addEditObstetricHistory(ObstetricHistory obstetricHistory);

	ProvisionalDiagnosis deleteProvisionalDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentComplaint deletePresentComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentComplaintHistory deletePresentComplaintHistory(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	GeneralExam deleteGeneralExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	SystemExam deleteSystemExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ObstetricHistory deleteObstetricHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	MenstrualHistory deleteMenstrualHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	IndicationOfUSG deleteIndicationOfUSG(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	IndicationOfUSG addEditIndicationOfUSG(IndicationOfUSG indicationOfUSG);

	ECGDetails addEditECGDetails(ECGDetails ecgDetails);

	XRayDetails addEditXRayDetails(XRayDetails xRayDetails);

	Echo addEditEcho(Echo echo);

	Holter addEditHolter(Holter holter);

	XRayDetails deleteXRayDetails(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ECGDetails deleteECGDetails(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Echo deleteEcho(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	Holter deleteHolter(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	ProcedureNote addEditProcedureNote(ProcedureNote precedureNote);

	ProcedureNote deleteProcedureNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintNose addEditPCNose(PresentingComplaintNose presentingComplaintNotes);

	PresentingComplaintEars addEditPCEars(PresentingComplaintEars presentingComplaintEars);

	PresentingComplaintThroat addEditPCThroat(PresentingComplaintThroat presentingComplaintThroat);

	PresentingComplaintOralCavity addEditPCOralCavity(PresentingComplaintOralCavity presentingComplaintOralCavity);

	NoseExamination addEditNoseExam(NoseExamination noseExamination);

	EarsExamination addEditEarsExam(EarsExamination earsExamination);

	NeckExamination addEditNeckExam(NeckExamination neckExamination);

	OralCavityAndThroatExamination addEditOralCavityThroatExam(
			OralCavityAndThroatExamination oralCavityAndThroatExamination);

	IndirectLarygoscopyExamination addEditIndirectLarygoscopyExam(
			IndirectLarygoscopyExamination indirectLarygoscopyExamination);

	PresentingComplaintNose deletePCNose(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintEars deletePCEars(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintOralCavity deletePCOralCavity(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PresentingComplaintThroat deletePCThroat(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	NeckExamination deleteNeckExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	NoseExamination deleteNoseExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	OralCavityAndThroatExamination deleteOralCavityThroatExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	EarsExamination deleteEarsExam(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	IndirectLarygoscopyExamination deleteIndirectLarygoscopyExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	List<?> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm,
			String speciality);

	Integer getClinicalNotesCount(String doctorId, String patientId, String locationId, String hospitalId,
			boolean isOTPVerified);

	MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId,
			String hospitalId);

	void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	String getClinicalNotesFile(String clinicalNotesId);

}
