package com.dpdocter.beans;

import java.util.List;

public class ClinicWiseFinancialsDto {
    private List<ClinicFinancialData> clinics;

    public ClinicWiseFinancialsDto() {}

    public ClinicWiseFinancialsDto(List<ClinicFinancialData> clinics) {
        this.clinics = clinics;
    }

    public List<ClinicFinancialData> getClinics() { return clinics; }
    public void setClinics(List<ClinicFinancialData> clinics) { this.clinics = clinics; }

    public static class ClinicFinancialData {
        private String clinicId;
        private String clinicName;
        private double revenue;
        private double expenses;
        private double profit;
        private double profitPercentage;

        public ClinicFinancialData() {}

        public ClinicFinancialData(String clinicId, String clinicName, double revenue, double expenses, double profit, double profitPercentage) {
            this.clinicId = clinicId;
            this.clinicName = clinicName;
            this.revenue = revenue;
            this.expenses = expenses;
            this.profit = profit;
            this.profitPercentage = profitPercentage;
        }

        public String getClinicId() { return clinicId; }
        public void setClinicId(String clinicId) { this.clinicId = clinicId; }

        public String getClinicName() { return clinicName; }
        public void setClinicName(String clinicName) { this.clinicName = clinicName; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }

        public double getExpenses() { return expenses; }
        public void setExpenses(double expenses) { this.expenses = expenses; }

        public double getProfit() { return profit; }
        public void setProfit(double profit) { this.profit = profit; }

        public double getProfitPercentage() { return profitPercentage; }
        public void setProfitPercentage(double profitPercentage) { this.profitPercentage = profitPercentage; }
    }
} 