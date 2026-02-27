package com.exam.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.exam.entity.ExamSubmission;
import com.exam.repository.ExamSubmissionRepository;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final ExamSubmissionRepository examSubmissionRepository;

    public StudentController(ExamSubmissionRepository examSubmissionRepository) {
        this.examSubmissionRepository = examSubmissionRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String studentEmail = principal != null ? principal.getName() : "student";
        model.addAttribute("studentEmail", studentEmail);
        model.addAttribute("hasSubjects", false);
        model.addAttribute("subjectCards", new ArrayList<>());
        model.addAttribute("finalizedNotifications", new ArrayList<>());
        model.addAttribute("fullName", studentEmail);

        // Fetch distributed quizzes for this student
        List<ExamSubmission> allSubmissions = examSubmissionRepository.findByStudentEmail(studentEmail);
        model.addAttribute("allSubmissions", allSubmissions);
        model.addAttribute("recentSubmissions", allSubmissions.stream().limit(5).toList());
        model.addAttribute("totalAttempts", allSubmissions.size());
        double avgScore = allSubmissions.isEmpty() ? 0.0 : allSubmissions.stream().mapToDouble(ExamSubmission::getPercentage).average().orElse(0.0);
        model.addAttribute("avgScore", String.format("%.1f", avgScore));
        long passedCount = allSubmissions.stream().filter(sub -> sub.getPercentage() >= 60.0).count();
        long failedCount = allSubmissions.stream().filter(sub -> sub.getPercentage() < 60.0).count();
        model.addAttribute("passedCount", passedCount);
        model.addAttribute("failedCount", failedCount);
        double bestScore = allSubmissions.stream().mapToDouble(ExamSubmission::getPercentage).max().orElse(0.0);
        model.addAttribute("bestScore", String.format("%.1f", bestScore));
        model.addAttribute("enrollments", new ArrayList<>());
        model.addAttribute("hasSubmissions", !allSubmissions.isEmpty());
        return "student-dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String studentEmail = principal != null ? principal.getName() : "student";
        model.addAttribute("studentEmail", studentEmail);
        return "student-profile";
    }
}
