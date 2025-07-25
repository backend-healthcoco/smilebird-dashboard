package com.dpdocter.beans;

public class ClinicPerformanceDto {
    private String clinicName;
    private String zone;
    private String specialty;
    private int revenue;
    private int profitShare;
    private int capex;
    private int ebitda;
    private int netProfit;
    private String status;

    public ClinicPerformanceDto() {}

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public int getRevenue() { return revenue; }
    public void setRevenue(int revenue) { this.revenue = revenue; }

    public int getProfitShare() { return profitShare; }
    public void setProfitShare(int profitShare) { this.profitShare = profitShare; }

    public int getCapex() { return capex; }
    public void setCapex(int capex) { this.capex = capex; }

    public int getEbitda() { return ebitda; }
    public void setEbitda(int ebitda) { this.ebitda = ebitda; }

    public int getNetProfit() { return netProfit; }
    public void setNetProfit(int netProfit) { this.netProfit = netProfit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 