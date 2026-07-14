package com.project1.Expense.management.model;

public class ExpenseDTO {
    private String formattedDate;
    private String category;
    private String status;
    private double amount;

    public ExpenseDTO(String formattedDate, String category, double amount, String status) {
        this.formattedDate = formattedDate;
        this.category = category;
        this.amount = amount;
        this.status = status;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
