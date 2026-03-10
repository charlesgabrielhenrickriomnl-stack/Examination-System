package com.exam.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        boolean isTeacher = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("TEACHER"::equals);
        boolean isStudent = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("STUDENT"::equals);
        boolean isDepartmentAdmin = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("DEPARTMENT_ADMIN"::equals);

        if (isTeacher) {
            response.sendRedirect("/teacher/loading");
            return;
        }
        if (isDepartmentAdmin) {
            response.sendRedirect("/department-admin/dashboard");
            return;
        }
        if (isStudent) {
            response.sendRedirect("/student/dashboard");
            return;
        }

        response.sendRedirect("/dashboard");
    }
}
