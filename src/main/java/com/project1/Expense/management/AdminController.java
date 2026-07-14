package com.project1.Expense.management;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project1.Expense.management.model.Expense;
import com.project1.Expense.management.model.Resident;
import com.project1.Expense.management.repository.ExpenseRepository;
//import com.project1.Expense.management.repository.ResidentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExpenseRepository expenseRepository;

//    @Autowired
//    private ResidentRepository residentRepository; // Optional: remove if not used

    // Show Admin Panel with all submitted expenses
    @GetMapping("/panel")
    public String showAdminPanel(HttpSession session, Model model) {
        Resident admin = (Resident) session.getAttribute("user");

        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findAll();
        model.addAttribute("expenses", expenses);
        return "admin-panel";
    }

    // Approve expense by ID
    @PostMapping("/approve/{id}")
    public String approveExpense(@PathVariable Long id, HttpSession session) {
        Resident admin = (Resident) session.getAttribute("user");

        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }

        expenseRepository.findById(id).ifPresent(expense -> {
            expense.setStatus("Approved");
            expenseRepository.save(expense);
        });

        return "redirect:/admin/panel";
    }

    // Reject expense by ID
    @PostMapping("/reject/{id}")
    public String rejectExpense(@PathVariable Long id, HttpSession session) {
        Resident admin = (Resident) session.getAttribute("user");

        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }

        expenseRepository.findById(id).ifPresent(expense -> {
            expense.setStatus("Rejected"); // ✅ FIXED
            expenseRepository.save(expense);
        });

        return "redirect:/admin/panel";
    }
}
