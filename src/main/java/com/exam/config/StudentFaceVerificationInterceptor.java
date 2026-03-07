package com.exam.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.exam.service.FaceVerificationSessionKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class StudentFaceVerificationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String path = request.getServletPath();
        if (path == null || !path.startsWith("/student/")) {
            return true;
        }

        if (path.startsWith("/student/face-verification")) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }

        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "STUDENT".equals(authority.getAuthority()));
        if (!isStudent) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/student/face-verification");
            return false;
        }

        Object verified = session.getAttribute(FaceVerificationSessionKeys.FACE_VERIFIED);
        if (!Boolean.TRUE.equals(verified)) {
            response.sendRedirect("/student/face-verification");
            return false;
        }

        return true;
    }
}
