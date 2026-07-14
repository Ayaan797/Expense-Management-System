package com.project1.Expense.management;

import com.project1.Expense.management.model.Expense;
import com.project1.Expense.management.model.ExpenseDTO;
import com.project1.Expense.management.model.Resident;
import com.project1.Expense.management.repository.ExpenseRepository;
import com.project1.Expense.management.repository.ResidentRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Show Login Page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("resident", new Resident());
        return "login";
    }

    // Handle Login Submit
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute Resident resident, HttpSession session, Model model) {
        List<Resident> matchedUsers = residentRepository.findByEmailAndPassword(resident.getEmail(), resident.getPassword());

        if (!matchedUsers.isEmpty()) {
            Resident user = matchedUsers.get(0); // ✅ just take the first match
            session.setAttribute("user", user);

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin/panel";
            } else {
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }


    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";  // ✅ adds query param
    }


    // Dashboard (for USER only)
    @GetMapping("/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        Resident user = (Resident) session.getAttribute("user");

        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/login";
        }

        List<Expense> expenses = expenseRepository.findByResidentEmail(user.getEmail());
        model.addAttribute("expenses", expenses);
        return "dashboard"; // dashboard.html
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("resident", new Resident());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute Resident resident) {
        resident.setRole("USER"); // default role
        residentRepository.save(resident);
        return "redirect:/login";
    }
    @GetMapping("/reports")
    public String showReports(@RequestParam(required = false) String category,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              HttpSession session, Model model) {

        Resident user = (Resident) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Expense> expenses;
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (category != null && !category.isEmpty() && date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            if (isAdmin) {
                expenses = expenseRepository.findByCategoryAndDateBetween(category, start, end);
            } else {
                expenses = expenseRepository.findByResidentEmailAndCategoryAndDateBetween(user.getEmail(), category, start, end);
            }

        } else if (category != null && !category.isEmpty()) {
            if (isAdmin) {
                expenses = expenseRepository.findByCategory(category);
            } else {
                expenses = expenseRepository.findByResidentEmailAndCategory(user.getEmail(), category);
            }

        } else {
            if (isAdmin) {
                expenses = expenseRepository.findAll();
            } else {
                expenses = expenseRepository.findByResidentEmail(user.getEmail());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        List<ExpenseDTO> formattedExpenses = expenses.stream()
            .map(e -> new ExpenseDTO(
                e.getDate().format(formatter),
                e.getCategory(),
                e.getAmount(),
                e.getStatus()
            ))
            .toList();

        model.addAttribute("expenses", formattedExpenses);
        model.addAttribute("role", user.getRole()); // ✅ Add this!

        return "reports";
    }
   


    @GetMapping("/expenses/form")
    public String showExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "submit-expense"; // must match HTML filename
    }


    // Handle form submission
    @PostMapping("/expenses/submit")
    public String submitExpense(@ModelAttribute Expense expense,
                                @RequestParam("receipt") MultipartFile file,
                                HttpSession session) {
        Resident user = (Resident) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            if (!file.isEmpty()) {
                // Create directory if not exists
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save file with unique name (to avoid overwrite)
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File uploaded to: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

        // Save expense
        expense.setResidentEmail(user.getEmail());
        expense.setStatus("Pending");
        expenseRepository.save(expense);

        return "redirect:/dashboard";
    }

    }