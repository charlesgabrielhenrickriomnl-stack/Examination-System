package com.exam.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/face-verification")
public class StudentFaceVerificationController {

    @GetMapping
    public String showPage() {
        return "redirect:/student/dashboard";
    }

    @PostMapping({"", "/confirm"})
    public String confirmFace() {
        return "redirect:/student/dashboard";
    }
}
