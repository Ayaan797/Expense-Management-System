package com.project1.Expense.management.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project1.Expense.management.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByResidentEmail(String email);
    List<Expense> findByResidentEmailAndCategory(String email, String category);
    List<Expense> findByCategory(String category);

    // ✅ Add these 2 methods:
    List<Expense> findByResidentEmailAndCategoryAndDateBetween(String email, String category, LocalDateTime start, LocalDateTime end);
    List<Expense> findByCategoryAndDateBetween(String category, LocalDateTime start, LocalDateTime end);
}
