package com.exam.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exam.config.AcademicCatalog;
import com.exam.entity.User;
import com.exam.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(name = "error", required = false) String error,
                                @RequestParam(name = "errorField", required = false) String errorField,
                                @RequestParam(name = "errorMessage", required = false) String errorMessage,
                                @RequestParam(name = "username", required = false) String username,
                                @RequestParam(name = "logout", required = false) String logout,
                                @RequestParam(name = "registered", required = false) String registered,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorField", errorField);
            model.addAttribute("errorMessage", errorMessage != null ? errorMessage : "Invalid email or password");
        }
        if (username != null) {
            model.addAttribute("username", username);
        }
        addIfNotNull(model, logout, "success", "You have been logged out successfully");
        addIfNotNull(model, registered, "success", "Registration successful! Please login.");
        return "login";
    }

    private void addIfNotNull(Model model,
                              String flag,
                              @NonNull String attributeName,
                              @NonNull String message) {
        if (flag != null) {
            model.addAttribute(attributeName, message);
        }
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        populateRegistrationDefaults(model);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("fullName") String fullName,
                               @RequestParam("role") String role,
                               @RequestParam(name = "schoolName", required = false) String schoolName,
                               @RequestParam(name = "campusName", required = false) String campusName,
                               @RequestParam(name = "departmentName", required = false) String departmentName,
                               @RequestParam(name = "programName", required = false) String programName,
                               Model model) {
        String normalizedSchool = (schoolName == null || schoolName.isBlank()) ? AcademicCatalog.SCHOOL_NAME : schoolName.trim();
        String normalizedCampus = (campusName == null || campusName.isBlank()) ? AcademicCatalog.CAMPUS_NAME : campusName.trim();
        String normalizedDepartment = departmentName == null ? "" : departmentName.trim();
        String normalizedProgram = programName == null ? "" : programName.trim();
        boolean programRequired = "STUDENT".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role);

        if (normalizedDepartment.isBlank()) {
            model.addAttribute("error", "Please select your department.");
            populateRegistrationDefaults(model);
            return "register";
        }

        if (programRequired && normalizedProgram.isBlank()) {
            model.addAttribute("error", "Please select your program.");
            populateRegistrationDefaults(model);
            return "register";
        }

        String result = switch (role) {
            case "STUDENT" -> userService.registerStudent(
                email,
                password,
                fullName,
                normalizedSchool,
                normalizedCampus,
                normalizedDepartment,
                normalizedProgram
            );
            case "TEACHER" -> userService.registerTeacher(
                email,
                password,
                fullName,
                normalizedSchool,
                normalizedCampus,
                normalizedDepartment,
                normalizedProgram
            );
            case "DEPARTMENT_ADMIN" -> userService.registerDepartmentAdmin(
                email,
                password,
                fullName,
                normalizedSchool,
                normalizedCampus,
                normalizedDepartment,
                normalizedProgram
            );
            default -> {
                model.addAttribute("error", "Invalid role selected");
                populateRegistrationDefaults(model);
                yield null;
            }
        };

        if (result == null) {
            return "register";
        }

        if (result.startsWith("ERROR")) {
            model.addAttribute("error", result.replace("ERROR: ", ""));
            populateRegistrationDefaults(model);
            return "register";
        }

        return "redirect:/login?registered=true";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        return verified ? "verification-success" : "verification-failure";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName()).orElse(null);
            if (user != null && user.getRole() == User.Role.TEACHER) {
                return "redirect:/teacher/homepage";
            }
            if (user != null && user.getRole() == User.Role.DEPARTMENT_ADMIN) {
                return "redirect:/department-admin/dashboard";
            }
            if (user != null && user.getRole() == User.Role.STUDENT) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/account/update-affiliation")
    public String updateAffiliation(@RequestParam(name = "departmentName", required = false) String departmentName,
                                    @RequestParam(name = "programName", required = false) String programName,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName()).orElse(null);
        String redirectTo = "redirect:/student/profile";
        if (user != null && user.getRole() == User.Role.TEACHER) {
            redirectTo = "redirect:/teacher/department-dashboard";
        } else if (user != null && user.getRole() == User.Role.DEPARTMENT_ADMIN) {
            redirectTo = "redirect:/department-admin/dashboard";
        }

        String normalizedDepartment = departmentName == null ? "" : departmentName.trim();
        String normalizedProgram = programName == null ? "" : programName.trim();
        if (normalizedDepartment.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select your department.");
            return redirectTo;
        }

        boolean programRequired = user == null || user.getRole() != User.Role.DEPARTMENT_ADMIN;
        if (programRequired && normalizedProgram.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select your program.");
            return redirectTo;
        }

        boolean updated = userService.updateAffiliation(
            principal.getName(),
            AcademicCatalog.SCHOOL_NAME,
            AcademicCatalog.CAMPUS_NAME,
            normalizedDepartment,
            normalizedProgram
        );

        if (!updated) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to update account details.");
            return redirectTo;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Account affiliation updated.");
        return redirectTo;
    }

    private void populateRegistrationDefaults(Model model) {
        var programsByDepartment = userService.getRegistrationProgramsByDepartment();
        model.addAttribute("schoolName", AcademicCatalog.SCHOOL_NAME);
        model.addAttribute("campusName", AcademicCatalog.CAMPUS_NAME);
        model.addAttribute("departments", userService.getRegistrationDepartments());
        model.addAttribute("programsByDepartment", programsByDepartment);
    }
}
