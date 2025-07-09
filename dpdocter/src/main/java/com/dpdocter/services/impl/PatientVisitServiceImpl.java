package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;

@Service
public class PatientVisitServiceImpl implements PatientVisitService {

	private static Logger logger = LogManager.getLogger(PatientVisitServiceImpl.class.getName());

	@Autowired
	private PatientVisitRepository patientVisitRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public 	String addRecord(Object details, VisitedFor visitedFor, String visitId) {
		PatientVisitCollection patientVisitCollection = new PatientVisitCollection();
		try {

			BeanUtil.map(details, patientVisitCollection);
			ObjectId id = patientVisitCollection.getId();

			if (visitId != null)
				patientVisitCollection = patientVisitRepository.findById(new ObjectId(visitId)).orElse(null);
			else
				patientVisitCollection.setId(null);

			if (patientVisitCollection.getId() == null) {
				patientVisitCollection
						.setUniqueEmrId(UniqueIdInitial.VISITS.getInitial() + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findById(patientVisitCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null) {
					patientVisitCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());

				}

			}

			if (patientVisitCollection.getVisitedFor() != null) {
				if (!patientVisitCollection.getVisitedFor().contains(visitedFor))
					patientVisitCollection.getVisitedFor().add(visitedFor);
			} else {
				List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
				visitedforList.add(visitedFor);
				patientVisitCollection.setVisitedFor(visitedforList);
			}

			patientVisitCollection.setVisitedTime(new Date());
			if (visitedFor.equals(VisitedFor.PRESCRIPTION)) {
				if (patientVisitCollection.getPrescriptionId() == null) {
					List<ObjectId> prescriptionId = new ArrayList<ObjectId>();
					prescriptionId.add(id);
					patientVisitCollection.setPrescriptionId(prescriptionId);
				} else {
					if (!patientVisitCollection.getPrescriptionId().contains(id))
						patientVisitCollection.getPrescriptionId().add(id);
				}

			} else if (visitedFor.equals(VisitedFor.CLINICAL_NOTES)) {
				if (patientVisitCollection.getClinicalNotesId() == null) {
					List<ObjectId> clinicalNotes = new ArrayList<ObjectId>();
					clinicalNotes.add(id);
					patientVisitCollection.setClinicalNotesId(clinicalNotes);
				} else {
					if (!patientVisitCollection.getClinicalNotesId().contains(id))
						patientVisitCollection.getClinicalNotesId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.REPORTS)) {
				if (patientVisitCollection.getRecordId() == null) {
					List<ObjectId> recordId = new ArrayList<ObjectId>();
					recordId.add(id);
					patientVisitCollection.setRecordId(recordId);
				} else {
					if (!patientVisitCollection.getRecordId().contains(id))
						patientVisitCollection.getRecordId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.TREATMENT)) {
				if (patientVisitCollection.getTreatmentId() == null) {
					List<ObjectId> treatmentId = new ArrayList<ObjectId>();
					treatmentId.add(id);
					patientVisitCollection.setTreatmentId(treatmentId);
				} else {
					if (!patientVisitCollection.getTreatmentId().add(id))
						patientVisitCollection.getTreatmentId().add(id);
				}
			}

			else if (visitedFor.equals(VisitedFor.EYE_PRESCRIPTION)) {
				if (patientVisitCollection.getEyePrescriptionId() == null) {
					patientVisitCollection.setEyePrescriptionId(id);
				}
			}
			patientVisitCollection.setUpdatedTime(new Date());
			patientVisitCollection.setCreatedTime(new Date());
			patientVisitCollection = patientVisitRepository.save(patientVisitCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while saving patient visit record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while saving patient visit record : " + e.getCause().getMessage());
		}
		return patientVisitCollection.getId().toString();
	}


}
