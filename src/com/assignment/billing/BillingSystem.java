package com.assignment.billing;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BillingSystem {

    private final Map<String, Resource> resources = new LinkedHashMap<>();
    private final Map<String, UsageSession> sessions = new LinkedHashMap<>();

    // Load saved resources, services, and sessions from PostgreSQL.
    public BillingSystem() {
        try {
            resources.putAll(DatabaseManager.loadResources());
            sessions.putAll(DatabaseManager.loadSessions(resources));

            System.out.println("Database connected successfully.");
            System.out.println("Resources loaded: " + resources.size());
            System.out.println("Sessions loaded: " + sessions.size());

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to load data from PostgreSQL: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    // Add a resource to the system.
    public void addResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null.");
        }

        if (resource.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Resource capacity must be greater than zero."
            );
        }

        if (resources.containsKey(resource.getResourceId())) {
            throw new IllegalArgumentException(
                    "Resource already exists: " + resource.getResourceId()
            );
        }

        resources.put(resource.getResourceId(), resource);
    }

    // Find a resource by its ID.
    public Resource getResource(String resourceId) {
        return resources.get(resourceId);
    }

    // Get all resources.
    public List<Resource> getAllResources() {
        return new ArrayList<>(resources.values());
    }

    // Count users currently using a resource.
    public int getActiveUserCount(String resourceId) {
        int count = 0;

        for (UsageSession session : sessions.values()) {
            if (session.isActive()
                    && session.getResource().getResourceId()
                    .equals(resourceId)) {
                count++;
            }
        }

        return count;
    }

    // Check how many slots are available.
    public int getAvailableSlots(String resourceId) {
        Resource resource = resources.get(resourceId);

        if (resource == null) {
            throw new IllegalArgumentException(
                    "Resource not found: " + resourceId
            );
        }

        return resource.getCapacity() - getActiveUserCount(resourceId);
    }

    // Start a new usage session and save it to PostgreSQL.
    public UsageSession startUsage(
            String userId,
            String resourceId,
            String serviceId) {

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }

        Resource resource = resources.get(resourceId);

        if (resource == null) {
            throw new IllegalArgumentException(
                    "Resource not found: " + resourceId
            );
        }

        ServicePlan service = resource.getService(serviceId);

        if (service == null) {
            throw new IllegalArgumentException(
                    "Service not found: " + serviceId
            );
        }

        // Prevent the same user from starting another session
        // for the same resource.
        for (UsageSession existingSession : sessions.values()) {
            if (existingSession.isActive()
                    && existingSession.getUserId().equals(userId)
                    && existingSession.getResource().getResourceId()
                    .equals(resourceId)) {

                throw new IllegalStateException(
                        "This user already has an active session "
                                + "on this resource."
                );
            }
        }

        // Check resource capacity.
        if (getAvailableSlots(resourceId) <= 0) {
            throw new IllegalStateException(
                    "Resource is at full capacity: "
                            + resource.getResourceName()
            );
        }

        String sessionId = "S-" + UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now();

        UsageSession session = new UsageSession(
                sessionId,
                userId,
                resource,
                service,
                startTime
        );

        try {
            // Persist before adding the session to memory.
            DatabaseManager.saveSession(session);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not save usage session to PostgreSQL: "
                            + exception.getMessage(),
                    exception
            );
        }

        sessions.put(sessionId, session);

        return session;
    }

    // Stop a session, calculate the bill, and save it to PostgreSQL.
    public UsageSession stopUsage(String sessionId) {
        UsageSession session = sessions.get(sessionId);

        if (session == null) {
            throw new IllegalArgumentException(
                    "Session not found: " + sessionId
            );
        }

        if (!session.isActive()) {
            throw new IllegalStateException(
                    "This session has already been stopped."
            );
        }

        LocalDateTime endTime = LocalDateTime.now();

        long durationSeconds = Duration.between(
                session.getStartTime(),
                endTime
        ).getSeconds();

        // Charge for at least one hour and round up partial hours.
        long billedHours = Math.max(
                1,
                (durationSeconds + 3599) / 3600
        );

        ServicePlan service = session.getService();
        BigDecimal totalCharge = service.getFirstHourPrice();

        // Add charges for additional hours.
        if (billedHours > 1) {
            BigDecimal additionalCharges =
                    service.getAdditionalHourPrice()
                            .multiply(BigDecimal.valueOf(billedHours - 1));

            totalCharge = totalCharge.add(additionalCharges);
        }

        // Update the object temporarily so DatabaseManager can persist it.
        session.stopSession(endTime, billedHours, totalCharge);

        try {
            DatabaseManager.completeSession(session);
        } catch (SQLException exception) {
            // Restore the active state if persistence fails.
            session.restoreSession(null, 0, null);

            throw new IllegalStateException(
                    "Could not save billing details to PostgreSQL: "
                            + exception.getMessage(),
                    exception
            );
        }

        return session;
    }

    // Find a session by its ID.
    public UsageSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    // Get all usage sessions.
    public List<UsageSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    // Get sessions that are still active.
    public List<UsageSession> getActiveSessions() {
        List<UsageSession> activeSessions = new ArrayList<>();

        for (UsageSession session : sessions.values()) {
            if (session.isActive()) {
                activeSessions.add(session);
            }
        }

        return activeSessions;
    }

    // Show resource details and availability.
    public void displayResource(String resourceId) {
        Resource resource = resources.get(resourceId);

        if (resource == null) {
            System.out.println("Resource not found: " + resourceId);
            return;
        }

        resource.displayResource();

        System.out.println(
                "Active Users: " + getActiveUserCount(resourceId)
        );

        System.out.println(
                "Available Slots: " + getAvailableSlots(resourceId)
        );
    }

    // Show session details and billing information.
    public void displaySession(UsageSession session) {
        System.out.println("\n========== SESSION DETAILS ==========");
        System.out.println("Session ID: " + session.getSessionId());
        System.out.println("User ID: " + session.getUserId());

        System.out.println(
                "Resource: " + session.getResource().getResourceName()
        );

        System.out.println(
                "Service: " + session.getService().getServiceName()
        );

        System.out.println("Start Time: " + session.getStartTime());

        if (session.isActive()) {
            System.out.println("Status: ACTIVE");
            System.out.println("End Time: Not stopped yet");
        } else {
            System.out.println("Status: COMPLETED");
            System.out.println("End Time: " + session.getEndTime());
            System.out.println("Billed Hours: " + session.getBilledHours());

            System.out.println(
                    "Total Bill: Rs. "
                            + session.getTotalCharge().toPlainString()
            );
        }

        System.out.println("=====================================");
    }
}