package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Advice;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.GenericCodesAndReaction;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.Response;

public interface PrescriptionServices {
	DrugAddEditResponse addDrug(DrugAddEditRequest request);

	DrugAddEditResponse editDrug(DrugAddEditRequest request);

	Drug deleteDrug(String drugId, String doctorId, String hospitalId, String locationIdString, Boolean discarded);

	Drug deleteDrug(String drugId, Boolean discarded);

	DrugAddEditResponse getDrugById(String drugId);

	DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request);

	DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request);

	DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request);

	DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request);

	DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request);

	DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request);

	DrugTypeAddEditResponse deleteDrugType(String drugTypeId, Boolean discarded);

	DrugDosageAddEditResponse deleteDrugDosage(String drugDosageId, Boolean discarded);

	DrugDirectionAddEditResponse deleteDrugDirection(String drugDirectionId, Boolean discarded);

	DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request);

	DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request);

	DrugDurationUnitAddEditResponse deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded);

	public List<?> getPrescriptionItems(String type, String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin,
			String searchTerm, String category, String disease, String speciality);

	void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	MailResponse getPrescriptionMailData(String prescriptionId, String doctorId, String locationId, String hospitalId);

	Boolean smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String mobileNumber, String type);

	LabTest addLabTest(LabTest request);

	LabTest editLabTest(LabTest request);

	LabTest deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded);

	LabTest deleteLabTest(String labTestId, Boolean discarded);

	LabTest getLabTestById(String labTestId);

	List<DiagnosticTestCollection> getDiagnosticTest();

	List<Prescription> getPrescriptions(String patientId, int page, int size, String updatedTime, Boolean discarded);

	DiagnosticTest addEditDiagnosticTest(DiagnosticTest request);

	DiagnosticTest getDiagnosticTest(String diagnosticTestId);

	DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, Boolean discarded);

	DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, String hospitalId, String locationId,
			Boolean discarded);

	PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId);

	GenericCode addEditGenericCode(GenericCode request);

	String getPrescriptionFile(String prescriptionId);

	Advice deleteAdvice(String adviceId, String doctorId, String locationId, String hospitalId, Boolean discarded);

	public Advice addAdvice(Advice request);

	Boolean addGenericCodeWithReaction(GenericCodesAndReaction request);

	Boolean deleteGenericCodeWithReaction(GenericCodesAndReaction request);

	Boolean uploadGenericCodeWithReaction(FormDataBodyPart file);

	DrugAddEditResponse makeCustomDrugGlobal(DrugAddEditRequest request);

	public Integer addCSVData(String filePath);

	Boolean uploadDiagnosticTest(FormDataBodyPart file);
	
	public List<Drug> getDrugs(List<ObjectId> drugIds);

	Drug getDrugNameById(String drugNameId);

	public Integer getPrescriptionItemsCount(String type, String range, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, Boolean isAdmin, String searchTerm, String category, String disease,
			String speciality);

	Boolean uploadDrugs();

	Boolean uploadDrugDetail();

	Response<Object> getDrugDtail(int page, int size, String searchTerm);

}
