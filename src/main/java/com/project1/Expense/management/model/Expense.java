package com.project1.Expense.management.model;



	import jakarta.persistence.*;
	import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

	@Entity
	public class Expense {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String residentEmail;  // Link to Resident by email

	    private String category;

	    private double amount;

	    private String status; // e.g., "Pending", "Approved", etc.

	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	    private LocalDateTime date;


	    // Constructors
	    public Expense() {
	    }

	    public Expense(String residentEmail, String category, double amount, String status, LocalDateTime date) {
	        this.residentEmail = residentEmail;
	        this.category = category;
	        this.amount = amount;
	        this.status = status;
	        this.date = date;
	    }

	    // Getters and Setters
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getResidentEmail() {
	        return residentEmail;
	    }

	    public void setResidentEmail(String residentEmail) {
	        this.residentEmail = residentEmail;
	    }

	    public String getCategory() {
	        return category;
	    }

	    public void setCategory(String category) {
	        this.category = category;
	    }

	    public double getAmount() {
	        return amount;
	    }

	    public void setAmount(double amount) {
	        this.amount = amount;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }

	    public LocalDateTime getDate() {
	        return date;
	    }

	    public void setDate(LocalDateTime date) {
	        this.date = date;
	    }

		public Object getResident() {
			// TODO Auto-generated method stub
			return null;
		}
	}



