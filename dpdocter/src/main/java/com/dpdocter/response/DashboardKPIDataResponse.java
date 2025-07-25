package com.dpdocter.response;

public class DashboardKPIDataResponse {
	private long totalRevenue;
	private long totalEbita;
	private double nps;
	private int newPatients;
	private int visitedPatients;

	public long getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(long totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public long getTotalEbita() {
		return totalEbita;
	}

	public void setTotalEbita(long totalEbita) {
		this.totalEbita = totalEbita;
	}

	public double getNps() {
		return nps;
	}

	public void setNps(double nps) {
		this.nps = nps;
	}

	public int getNewPatients() {
		return newPatients;
	}

	public void setNewPatients(int newPatients) {
		this.newPatients = newPatients;
	}

	public int getVisitedPatients() {
		return visitedPatients;
	}

	public void setVisitedPatients(int visitedPatients) {
		this.visitedPatients = visitedPatients;
	}

}
