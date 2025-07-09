package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.WorkingHours;

@Document(collection = "patient_treatment_cl")
public class PatientTreatmentCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private List<PatientTreatment> patientTreatments;

    @Field
	private List<Treatment> treatments;
    
    @Field
    private ObjectId patientId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private ObjectId doctorId;
    
    @Field
	private String uniqueEmrId;

    @Field
    private double totalCost = 0.0;

    @Field
	private Boolean discarded = false;
    
    @Field
	private Boolean inHistory = false;
   
	@Field
	private String appointmentId;

	@Field
	private WorkingHours time;

	@Field
	private Date fromDate;

	@Field
	private Boolean isPatientDiscarded = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public List<PatientTreatment> getPatientTreatments() {
	return patientTreatments;
    }

    public void setPatientTreatments(List<PatientTreatment> patientTreatments) {
	this.patientTreatments = patientTreatments;
    }

    public ObjectId getPatientId() {
	return patientId;
    }

    public void setPatientId(ObjectId patientId) {
	this.patientId = patientId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
	this.hospitalId = hospitalId;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public double getTotalCost() {
	return totalCost;
    }

    public void setTotalCost(double totalCost) {
	this.totalCost = totalCost;
    }

  

    public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
    public List<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	
	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}
	
	

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	@Override
    public String toString() {
	return "PatientTreatmentCollection [id=" + id + ", patientTreatments=" + patientTreatments + ", patientId=" + patientId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", totalCost=" + totalCost + ", discarded=" + discarded + ", inHistory=" + inHistory
		+ "]";
    }

}
