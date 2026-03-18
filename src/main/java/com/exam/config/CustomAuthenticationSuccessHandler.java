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
        boolean isTeacher = hasAuthority(authentication, "TEACHER");
        boolean isStudent = hasAuthority(authentication, "STUDENT");
        boolean isDepartmentAdmin = hasAuthority(authentication, "DEPARTMENT_ADMIN");

        if (isTeacher) {
            response.sendRedirect("/teacher/loading");
            return;
        }
        if (isDepartmentAdmin) {
            response.sendRedirect("/department-admin/dashboard");
            return;
        }
        if (isStudent) {
            response.sendRedirect("/student/loading");
            return;
        }

        response.sendRedirect("/dashboard");
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        if (authentication == null || authority == null || authority.isBlank()) {
            return false;
        }

        String normalized = authority.trim();
        String roleVariant = "ROLE_" + normalized;

        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(granted -> normalized.equalsIgnoreCase(granted) || roleVariant.equalsIgnoreCase(granted));
    }
}
