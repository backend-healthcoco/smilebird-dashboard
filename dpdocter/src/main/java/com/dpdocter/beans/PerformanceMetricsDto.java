package com.dpdocter.beans;

public class PerformanceMetricsDto {
    private String month;
    private int revenue;
    private int expenses;
    private int netProfit;
    private int newPatients;
    private int returningPatients;
    private int totalFootfall;

    public PerformanceMetricsDto() {}

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getRevenue() { return revenue; }
    public void setRevenue(int revenue) { this.revenue = revenue; }

    public int getExpenses() { return expenses; }
    public void setExpenses(int expenses) { this.expenses = expenses; }

    public int getNetProfit() { return netProfit; }
    public void setNetProfit(int netProfit) { this.netProfit = netProfit; }

    public int getNewPatients() { return newPatients; }
    public void setNewPatients(int newPatients) { this.newPatients = newPatients; }

    public int getReturningPatients() { return returningPatients; }
    public void setReturningPatients(int returningPatients) { this.returningPatients = returningPatients; }

    public int getTotalFootfall() { return totalFootfall; }
    public void setTotalFootfall(int totalFootfall) { this.totalFootfall = totalFootfall; }
} 