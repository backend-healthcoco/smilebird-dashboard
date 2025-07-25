package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.Discount;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.TreatmentServicesCollection;

public class PatientTreatmentResponse extends GenericCollection {
    private String id;

    private List<PatientTreatment> patientTreatments;

    private String patientId;
    
	private String uniqueEmrId;

    private String locationId;

    private String hospitalId;

    private String doctorId;

    private double totalCost = 0.0;

    private Discount totalDiscount;

	private double grandTotal = 0.0;

	private Boolean inHistory = false;

	private String visitId;

	private String appointmentId;

	private WorkingHours time;
	
	private Appointment appointmentRequest;

	private Date fromDate;
	private List<TreatmentResponse> treatments;

	private List<TreatmentServicesCollection> treatmentServicesCollections;
	
    private boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<PatientTreatment> getPatientTreatments() {
	return patientTreatments;
    }

    public void setPatientTreatments(List<PatientTreatment> patientTreatments) {
	this.patientTreatments = patientTreatments;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public double getTotalCost() {
	return totalCost;
    }

    public void setTotalCost(double totalCost) {
	this.totalCost = totalCost;
    }

    public boolean isDiscarded() {
	return discarded;
    }

    public void setDiscarded(boolean discarded) {
	this.discarded = discarded;
    }
    
    public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
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

	public Appointment getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(Appointment appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public List<TreatmentResponse> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentResponse> treatments) {
		this.treatments = treatments;
	}

	public List<TreatmentServicesCollection> getTreatmentServicesCollections() {
		return treatmentServicesCollections;
	}

	public void setTreatmentServicesCollections(List<TreatmentServicesCollection> treatmentServicesCollections) {
		this.treatmentServicesCollections = treatmentServicesCollections;
	}

	@Override
    public String toString() {
	return "PatientTreatmentResponse [id=" + id + ", patientTreatments=" + patientTreatments + ", patientId=" + patientId + ", locationId=" + locationId
		+ ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", totalCost=" + totalCost + ", discarded=" + discarded + "]";
    }

}
