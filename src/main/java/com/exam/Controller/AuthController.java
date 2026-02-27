package com.exam.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showLoginPage(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String errorField,
                                @RequestParam(required = false) String errorMessage,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String logout,
                                @RequestParam(required = false) String registered,
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
    public String showRegistrationPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String fullName,
                               @RequestParam String role,
                               Model model) {
        String result = switch (role) {
            case "STUDENT" -> userService.registerStudent(email, password, fullName);
            case "TEACHER" -> userService.registerTeacher(email, password, fullName);
            default -> {
                model.addAttribute("error", "Invalid role selected");
                yield null;
            }
        };

        if (result == null) {
            return "register";
        }

        if (result.startsWith("ERROR")) {
            model.addAttribute("error", result.replace("ERROR: ", ""));
            return "register";
        }

        return "redirect:/login?registered=true";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
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
            if (user != null && user.getRole() == User.Role.STUDENT) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login";
    }
}
