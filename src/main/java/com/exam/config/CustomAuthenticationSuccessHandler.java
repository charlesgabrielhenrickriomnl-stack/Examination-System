package com.exam.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.exam.entity.User;
import com.exam.repository.UserRepository;
import com.exam.service.FaceVerificationService;
import com.exam.service.FaceVerificationSessionKeys;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final FaceVerificationService faceVerificationService;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository,
                                              FaceVerificationService faceVerificationService) {
        this.userRepository = userRepository;
        this.faceVerificationService = faceVerificationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        HttpSession session = request.getSession(true);
        session.removeAttribute(FaceVerificationSessionKeys.PENDING_FACE_USER_ID);
        session.removeAttribute(FaceVerificationSessionKeys.PENDING_FACE_DEVICE_TOKEN);

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || user.getRole() != User.Role.STUDENT) {
            session.setAttribute(FaceVerificationSessionKeys.FACE_VERIFIED, Boolean.TRUE);
            response.sendRedirect("/dashboard");
            return;
        }

        if (!faceVerificationService.isFeatureEnabled()) {
            session.setAttribute(FaceVerificationSessionKeys.FACE_VERIFIED, Boolean.TRUE);
            response.sendRedirect("/dashboard");
            return;
        }

        String deviceToken = faceVerificationService.resolveDeviceToken(request).orElse(null);
        if (deviceToken != null && faceVerificationService.isTrustedDevice(user, deviceToken)) {
            session.setAttribute(FaceVerificationSessionKeys.FACE_VERIFIED, Boolean.TRUE);
            response.sendRedirect("/dashboard");
            return;
        }

        if (deviceToken == null) {
            deviceToken = faceVerificationService.generateDeviceToken();
        }

        session.setAttribute(FaceVerificationSessionKeys.FACE_VERIFIED, Boolean.FALSE);
        session.setAttribute(FaceVerificationSessionKeys.PENDING_FACE_USER_ID, user.getId());
        session.setAttribute(FaceVerificationSessionKeys.PENDING_FACE_DEVICE_TOKEN, deviceToken);

        response.sendRedirect("/student/face-verification");
    }
}
