package com.exam.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.exam.config.FaceVerificationProperties;
import com.exam.entity.TrustedDevice;
import com.exam.entity.User;
import com.exam.repository.TrustedDeviceRepository;
import com.exam.repository.UserRepository;
import com.google.gson.Gson;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class FaceVerificationService {

    private final FaceVerificationProperties properties;
    private final TrustedDeviceRepository trustedDeviceRepository;
    private final UserRepository userRepository;
    private final Gson gson = new Gson();

    public FaceVerificationService(FaceVerificationProperties properties,
                                   TrustedDeviceRepository trustedDeviceRepository,
                                   UserRepository userRepository) {
        this.properties = properties;
        this.trustedDeviceRepository = trustedDeviceRepository;
        this.userRepository = userRepository;
    }

    public boolean isFeatureEnabled() {
        return properties.isEnabled();
    }

    public boolean isOperational() {
        return true;
    }

    public String getModelUrl() {
        return properties.getModelUrl();
    }

    public String getCookieName() {
        return properties.getCookieName();
    }

    public int getTrustedDeviceDays() {
        return properties.getTrustedDeviceDays();
    }

    public String generateDeviceToken() {
        return UUID.randomUUID().toString();
    }

    public Optional<String> resolveDeviceToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (cookie != null
                    && getCookieName().equals(cookie.getName())
                    && StringUtils.hasText(cookie.getValue())) {
                return Optional.of(cookie.getValue().trim());
            }
        }
        return Optional.empty();
    }

    public boolean userHasReferenceFace(User user) {
        return user != null && StringUtils.hasText(user.getFaceReferenceDescriptorJson());
    }

    public void validateDescriptorPayload(String descriptorJson) {
        parseDescriptor(descriptorJson);
    }

    public FaceDescriptorMatchResult verifyCapturedDescriptor(User user, String descriptorJson) {
        double[] reference = parseDescriptor(user.getFaceReferenceDescriptorJson());
        double[] candidate = parseDescriptor(descriptorJson);
        if (reference.length != candidate.length) {
            throw new FaceVerificationException("Reference and captured face descriptors are incompatible.");
        }

        double distance = euclideanDistance(reference, candidate);
        double threshold = properties.getMatchThreshold();
        boolean matched = distance <= threshold;
        return new FaceDescriptorMatchResult(matched, distance, threshold);
    }

    @Transactional
    public void saveReferenceFace(User user, String imageBase64, String descriptorJson) {
        validateDescriptorPayload(descriptorJson);
        user.setFaceReferenceImageBase64(imageBase64);
        user.setFaceReferenceDescriptorJson(descriptorJson);
        user.setFaceReferenceCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void trustDevice(User user, String deviceToken, String deviceLabel) {
        String tokenHash = hashDeviceToken(deviceToken);
        TrustedDevice trustedDevice = trustedDeviceRepository
                .findByUserIdAndDeviceTokenHash(user.getId(), tokenHash)
                .orElseGet(TrustedDevice::new);
        trustedDevice.setUser(user);
        trustedDevice.setDeviceTokenHash(tokenHash);
        trustedDevice.setDeviceLabel(sanitizeDeviceLabel(deviceLabel));
        trustedDevice.setLastUsedAt(LocalDateTime.now());
        trustedDeviceRepository.save(trustedDevice);
    }

    @Transactional
    public boolean isTrustedDevice(User user, String deviceToken) {
        if (user == null || !StringUtils.hasText(deviceToken)) {
            return false;
        }

        String tokenHash = hashDeviceToken(deviceToken);
        Optional<TrustedDevice> trustedDeviceOpt = trustedDeviceRepository
                .findByUserIdAndDeviceTokenHash(user.getId(), tokenHash);

        trustedDeviceOpt.ifPresent(device -> {
            device.setLastUsedAt(LocalDateTime.now());
            trustedDeviceRepository.save(device);
        });

        return trustedDeviceOpt.isPresent();
    }

    private String sanitizeDeviceLabel(String value) {
        if (!StringUtils.hasText(value)) {
            return "Unknown Device";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= 250) {
            return trimmed;
        }
        return trimmed.substring(0, 250);
    }

    private String hashDeviceToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Device token cannot be empty.");
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available in this JVM.", ex);
        }
    }

    private double[] parseDescriptor(String descriptorJson) {
        if (!StringUtils.hasText(descriptorJson)) {
            throw new FaceVerificationException("Face descriptor payload is missing.");
        }

        try {
            double[] descriptor = gson.fromJson(descriptorJson, double[].class);
            if (descriptor == null || descriptor.length == 0) {
                throw new FaceVerificationException("Face descriptor payload is empty.");
            }
            return descriptor;
        } catch (RuntimeException ex) {
            throw new FaceVerificationException("Failed to parse face descriptor payload.");
        }
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0d;
        for (int i = 0; i < a.length; i++) {
            double delta = a[i] - b[i];
            sum += delta * delta;
        }
        return Math.sqrt(sum);
    }

    public record FaceDescriptorMatchResult(boolean matched, double distance, double threshold) {
    }

    public static class FaceVerificationException extends RuntimeException {
        public FaceVerificationException(String message) {
            super(message);
        }
    }
}
