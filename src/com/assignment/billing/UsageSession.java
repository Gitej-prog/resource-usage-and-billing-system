package com.assignment.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Stores the details of a resource usage session.
public class UsageSession {

    private String sessionId;
    private String userId;
    private Resource resource;
    private ServicePlan service;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private long billedHours;
    private BigDecimal totalCharge;

    // Create a new session when a user starts using a resource.
    public UsageSession(
            String sessionId,
            String userId,
            Resource resource,
            ServicePlan service,
            LocalDateTime startTime) {

        this.sessionId = sessionId;
        this.userId = userId;
        this.resource = resource;
        this.service = service;
        this.startTime = startTime;
    }

    // Save the end time and billing details when the session stops.
    public void stopSession(
            LocalDateTime endTime,
            long billedHours,
            BigDecimal totalCharge) {

        this.endTime = endTime;
        this.billedHours = billedHours;
        this.totalCharge = totalCharge;
    }

    // Restore saved session details when loading from the database.
    public void restoreSession(
            LocalDateTime endTime,
            long billedHours,
            BigDecimal totalCharge) {

        this.endTime = endTime;
        this.billedHours = billedHours;
        this.totalCharge = totalCharge;
    }

    // A session is active until an end time is recorded.
    public boolean isActive() {
        return endTime == null;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public Resource getResource() {
        return resource;
    }

    public ServicePlan getService() {
        return service;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getBilledHours() {
        return billedHours;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }
}