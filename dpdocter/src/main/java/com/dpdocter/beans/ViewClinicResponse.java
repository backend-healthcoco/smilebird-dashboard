package com.dpdocter.beans;

public class ViewClinicResponse {
    private String clinicId;
    private int revenue;
    private int netIncome;
    private int totalPatient;
    private int footfall;
    private double newPatientConversion;
    private int newPatients;
    private int returning;
    private String doctorInCharge;
    private int operatories;
    private String breakevenStatus;
    private double averageRating;
    private double treatmentCompletion;

    public ViewClinicResponse() {}

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }

    public int getRevenue() { return revenue; }
    public void setRevenue(int revenue) { this.revenue = revenue; }

    public int getNetIncome() { return netIncome; }
    public void setNetIncome(int netIncome) { this.netIncome = netIncome; }

    public int getTotalPatient() { return totalPatient; }
    public void setTotalPatient(int totalPatient) { this.totalPatient = totalPatient; }

    public int getFootfall() { return footfall; }
    public void setFootfall(int footfall) { this.footfall = footfall; }

    public double getNewPatientConversion() { return newPatientConversion; }
    public void setNewPatientConversion(double newPatientConversion) { this.newPatientConversion = newPatientConversion; }

    public int getNewPatients() { return newPatients; }
    public void setNewPatients(int newPatients) { this.newPatients = newPatients; }

    public int getReturning() { return returning; }
    public void setReturning(int returning) { this.returning = returning; }

    public String getDoctorInCharge() { return doctorInCharge; }
    public void setDoctorInCharge(String doctorInCharge) { this.doctorInCharge = doctorInCharge; }

    public int getOperatories() { return operatories; }
    public void setOperatories(int operatories) { this.operatories = operatories; }

    public String getBreakevenStatus() { return breakevenStatus; }
    public void setBreakevenStatus(String breakevenStatus) { this.breakevenStatus = breakevenStatus; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public double getTreatmentCompletion() { return treatmentCompletion; }
    public void setTreatmentCompletion(double treatmentCompletion) { this.treatmentCompletion = treatmentCompletion; }
} 