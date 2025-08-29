package com.dpdocter.beans;

public class FinancialOverviewDto {
    private double totalRevenue;
    private double totalExpenses;
    private double totalProfit;
    private double profitPercentage;

    public FinancialOverviewDto() {}

    public FinancialOverviewDto(double totalRevenue, double totalExpenses, double totalProfit, double profitPercentage) {
        this.totalRevenue = totalRevenue;
        this.totalExpenses = totalExpenses;
        this.totalProfit = totalProfit;
        this.profitPercentage = profitPercentage;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }

    public double getProfitPercentage() { return profitPercentage; }
    public void setProfitPercentage(double profitPercentage) { this.profitPercentage = profitPercentage; }
} 