package com.dpdocter.beans;

public class FinancialSummaryDto {
    private String period;
    private int totalRevenue;
    private int totalCosts;
    private int ebit;
    private int interest;
    private int taxes;
    private int netIncome;

    public FinancialSummaryDto() {}

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(int totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalCosts() { return totalCosts; }
    public void setTotalCosts(int totalCosts) { this.totalCosts = totalCosts; }

    public int getEbit() { return ebit; }
    public void setEbit(int ebit) { this.ebit = ebit; }

    public int getInterest() { return interest; }
    public void setInterest(int interest) { this.interest = interest; }

    public int getTaxes() { return taxes; }
    public void setTaxes(int taxes) { this.taxes = taxes; }

    public int getNetIncome() { return netIncome; }
    public void setNetIncome(int netIncome) { this.netIncome = netIncome; }
} 