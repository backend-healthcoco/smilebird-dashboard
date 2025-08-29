package com.dpdocter.beans;

import java.util.List;

public class ExpensesBreakdownDto {
    private List<ExpenseCategory> categories;

    public ExpensesBreakdownDto() {}

    public ExpensesBreakdownDto(List<ExpenseCategory> categories) {
        this.categories = categories;
    }

    public List<ExpenseCategory> getCategories() { return categories; }
    public void setCategories(List<ExpenseCategory> categories) { this.categories = categories; }

    public static class ExpenseCategory {
        private String name;
        private double amount;

        public ExpenseCategory() {}

        public ExpenseCategory(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
} 