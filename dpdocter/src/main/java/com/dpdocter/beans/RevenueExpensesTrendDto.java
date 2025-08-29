package com.dpdocter.beans;

import java.util.List;

public class RevenueExpensesTrendDto {
    private List<String> labels;
    private List<Double> revenue;
    private List<Double> expenses;

    public RevenueExpensesTrendDto() {}

    public RevenueExpensesTrendDto(List<String> labels, List<Double> revenue, List<Double> expenses) {
        this.labels = labels;
        this.revenue = revenue;
        this.expenses = expenses;
    }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Double> getRevenue() { return revenue; }
    public void setRevenue(List<Double> revenue) { this.revenue = revenue; }

    public List<Double> getExpenses() { return expenses; }
    public void setExpenses(List<Double> expenses) { this.expenses = expenses; }
} 