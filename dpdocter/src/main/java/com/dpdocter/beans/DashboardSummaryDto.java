package com.dpdocter.beans;

import java.util.List;

public class DashboardSummaryDto {
    private String clinicId;
    private int month;
    private int year;

    private List<RevenueExpenseData> revenueVsExpenses;
    private List<PatientTrendData> patientTrends;

    private RevenueAnalytics revenueAnalytics;
    private ExpenseAnalytics expenseAnalytics;

    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public List<RevenueExpenseData> getRevenueVsExpenses() { return revenueVsExpenses; }
    public void setRevenueVsExpenses(List<RevenueExpenseData> revenueVsExpenses) { this.revenueVsExpenses = revenueVsExpenses; }
    public List<PatientTrendData> getPatientTrends() { return patientTrends; }
    public void setPatientTrends(List<PatientTrendData> patientTrends) { this.patientTrends = patientTrends; }
    public RevenueAnalytics getRevenueAnalytics() { return revenueAnalytics; }
    public void setRevenueAnalytics(RevenueAnalytics revenueAnalytics) { this.revenueAnalytics = revenueAnalytics; }
    public ExpenseAnalytics getExpenseAnalytics() { return expenseAnalytics; }
    public void setExpenseAnalytics(ExpenseAnalytics expenseAnalytics) { this.expenseAnalytics = expenseAnalytics; }

    // Inner DTO classes
    public static class RevenueExpenseData {
        private String month;
        private int revenue;
        private int expenses;
        public RevenueExpenseData() {}
        public RevenueExpenseData(String month, int revenue, int expenses) {
            this.month = month;
            this.revenue = revenue;
            this.expenses = expenses;
        }
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public int getRevenue() { return revenue; }
        public void setRevenue(int revenue) { this.revenue = revenue; }
        public int getExpenses() { return expenses; }
        public void setExpenses(int expenses) { this.expenses = expenses; }
    }

    public static class PatientTrendData {
        private String month;
        private int newPatients;
        private int returningPatients;
        private int totalFootfall;
        public PatientTrendData() {}
        public PatientTrendData(String month, int newPatients, int returningPatients, int totalFootfall) {
            this.month = month;
            this.newPatients = newPatients;
            this.returningPatients = returningPatients;
            this.totalFootfall = totalFootfall;
        }
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public int getNewPatients() { return newPatients; }
        public void setNewPatients(int newPatients) { this.newPatients = newPatients; }
        public int getReturningPatients() { return returningPatients; }
        public void setReturningPatients(int returningPatients) { this.returningPatients = returningPatients; }
        public int getTotalFootfall() { return totalFootfall; }
        public void setTotalFootfall(int totalFootfall) { this.totalFootfall = totalFootfall; }
    }

    public static class RevenueAnalytics {
        private int totalRevenue;
        private String detailsLink;
        public RevenueAnalytics() {}
        public int getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(int totalRevenue) { this.totalRevenue = totalRevenue; }
        public String getDetailsLink() { return detailsLink; }
        public void setDetailsLink(String detailsLink) { this.detailsLink = detailsLink; }
    }

    public static class ExpenseAnalytics {
        private int totalExpenses;
        private String detailsLink;
        public ExpenseAnalytics() {}
        public int getTotalExpenses() { return totalExpenses; }
        public void setTotalExpenses(int totalExpenses) { this.totalExpenses = totalExpenses; }
        public String getDetailsLink() { return detailsLink; }
        public void setDetailsLink(String detailsLink) { this.detailsLink = detailsLink; }
    }
} 