package com.dpdocter.beans;

public class RateCard extends GenericCode {

	private String id;
	private String locationId;
	private String hospitalId;
	private DiagnosticTest diagnosticTest;
	private Integer turnaroundTime;
	private Double highRate;
	private Double normalRate;
	private Double lowRate;
	private Double specialRate;
	private String labId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Double getHighRate() {
		return highRate;
	}

	public void setHighRate(Double highRate) {
		this.highRate = highRate;
	}

	public Double getNormalRate() {
		return normalRate;
	}

	public void setNormalRate(Double normalRate) {
		this.normalRate = normalRate;
	}

	public Double getLowRate() {
		return lowRate;
	}

	public void setLowRate(Double lowRate) {
		this.lowRate = lowRate;
	}

	public Double getSpecialRate() {
		return specialRate;
	}

	public void setSpecialRate(Double specialRate) {
		this.specialRate = specialRate;
	}

	public String getLabId() {
		return labId;
	}

	public void setLabId(String labId) {
		this.labId = labId;
	}

	public DiagnosticTest getDiagnosticTest() {
		return diagnosticTest;
	}

	public void setDiagnosticTest(DiagnosticTest diagnosticTest) {
		this.diagnosticTest = diagnosticTest;
	}

	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	@Override
	public String toString() {
		return "RateCard [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", diagnosticTest="
				+ diagnosticTest + ", turnaroundTime=" + turnaroundTime + ", highRate=" + highRate + ", normalRate="
				+ normalRate + ", lowRate=" + lowRate + ", specialRate=" + specialRate + ", labId=" + labId + "]";
	}

}
