package com.project1.Expense.management.repository;


import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.project1.Expense.management.model.Resident;

public interface ResidentRepository extends JpaRepository<Resident, Long> {
    List<Resident> findByEmailAndPassword(String email, String password);
    
}

