package com.exam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.face-verification")
public class FaceVerificationProperties {

    private boolean enabled = false;
    private double matchThreshold = 0.55d;
    private int trustedDeviceDays = 180;
    private String cookieName = "exam_trusted_device";
    private String modelUrl = "https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getMatchThreshold() {
        return matchThreshold;
    }

    public void setMatchThreshold(double matchThreshold) {
        this.matchThreshold = matchThreshold;
    }

    public int getTrustedDeviceDays() {
        return trustedDeviceDays;
    }

    public void setTrustedDeviceDays(int trustedDeviceDays) {
        this.trustedDeviceDays = trustedDeviceDays;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }
}
