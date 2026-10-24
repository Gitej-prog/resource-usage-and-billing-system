package com.assignment.billing;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseManager {

    // Save or update a resource.
    public static void addResource(
            String resourceId,
            String resourceName,
            int capacity) throws SQLException {

        String sql = """
                INSERT INTO resources
                (resource_id, resource_name, capacity)
                VALUES (?, ?, ?)
                ON CONFLICT (resource_id) DO UPDATE
                SET resource_name = EXCLUDED.resource_name,
                    capacity = EXCLUDED.capacity
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, resourceId);
            statement.setString(2, resourceName);
            statement.setInt(3, capacity);
            statement.executeUpdate();
        }
    }

    // Save or update a service plan.
    public static void addService(
            String serviceId,
            String resourceId,
            String serviceName,
            double firstHourPrice,
            double additionalHourPrice) throws SQLException {

        String sql = """
                INSERT INTO service_plans
                (service_id, resource_id, service_name,
                 first_hour_price, additional_hour_price)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (service_id) DO UPDATE
                SET resource_id = EXCLUDED.resource_id,
                    service_name = EXCLUDED.service_name,
                    first_hour_price = EXCLUDED.first_hour_price,
                    additional_hour_price = EXCLUDED.additional_hour_price
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, serviceId);
            statement.setString(2, resourceId);
            statement.setString(3, serviceName);
            statement.setBigDecimal(4, BigDecimal.valueOf(firstHourPrice));
            statement.setBigDecimal(5, BigDecimal.valueOf(additionalHourPrice));
            statement.executeUpdate();
        }
    }

    // Load resources and attach their service plans.
    public static Map<String, Resource> loadResources() throws SQLException {

        Map<String, Resource> resources = new LinkedHashMap<>();

        String resourceSql = """
                SELECT resource_id, resource_name, capacity
                FROM resources
                ORDER BY resource_id
                """;

        String serviceSql = """
                SELECT service_id, resource_id, service_name,
                       first_hour_price, additional_hour_price
                FROM service_plans
                ORDER BY service_id
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            try (PreparedStatement statement =
                         connection.prepareStatement(resourceSql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    Resource resource = new Resource(
                            result.getString("resource_id"),
                            result.getString("resource_name"),
                            result.getInt("capacity")
                    );

                    resources.put(resource.getResourceId(), resource);
                }
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(serviceSql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    String resourceId = result.getString("resource_id");

                    Resource resource = resources.get(resourceId);

                    if (resource != null) {
                        ServicePlan service = new ServicePlan(
                                result.getString("service_id"),
                                result.getString("service_name"),
                                result.getBigDecimal("first_hour_price"),
                                result.getBigDecimal("additional_hour_price")
                        );

                        resource.addService(service);
                    }
                }
            }
        }

        return resources;
    }

    // Save a newly started usage session.
    public static void saveSession(UsageSession session) throws SQLException {

        String sql = """
                INSERT INTO usage_sessions
                (session_id, user_id, resource_id, service_id,
                 start_time, end_time, billed_hours, status)
                VALUES (?, ?, ?, ?, ?, NULL, 0, 'ACTIVE')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, session.getSessionId());
            statement.setString(2, session.getUserId());
            statement.setString(3, session.getResource().getResourceId());
            statement.setString(4, session.getService().getServiceId());
            statement.setTimestamp(
                    5, Timestamp.valueOf(session.getStartTime()));

            statement.executeUpdate();
        }
    }

    // Complete a session and create its billing record in one transaction.
    public static void completeSession(UsageSession session) throws SQLException {

        String updateSql = """
                UPDATE usage_sessions
                SET end_time = ?, billed_hours = ?, status = 'COMPLETED'
                WHERE session_id = ? AND status = 'ACTIVE'
                """;

        String billingSql = """
                INSERT INTO billing_records
                (session_id, billed_hours, total_charge)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int updatedRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setTimestamp(
                            1, Timestamp.valueOf(session.getEndTime()));
                    statement.setLong(2, session.getBilledHours());
                    statement.setString(3, session.getSessionId());

                    updatedRows = statement.executeUpdate();
                }

                if (updatedRows != 1) {
                    throw new SQLException(
                            "Session was not active or could not be found: "
                                    + session.getSessionId());
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(billingSql)) {

                    statement.setString(1, session.getSessionId());
                    statement.setLong(2, session.getBilledHours());
                    statement.setBigDecimal(3, session.getTotalCharge());

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    // Load all saved sessions, including completed sessions.
    public static Map<String, UsageSession> loadSessions(
            Map<String, Resource> resources) throws SQLException {

        Map<String, UsageSession> sessions = new LinkedHashMap<>();

        String sql = """
                SELECT session_id, user_id, resource_id, service_id,
                       start_time, end_time, billed_hours, status
                FROM usage_sessions
                ORDER BY start_time
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                String resourceId = result.getString("resource_id");
                String serviceId = result.getString("service_id");

                Resource resource = resources.get(resourceId);

                if (resource == null) {
                    throw new SQLException(
                            "Resource not found for session: "
                                    + result.getString("session_id"));
                }

                ServicePlan service = resource.getService(serviceId);

                if (service == null) {
                    throw new SQLException(
                            "Service not found for session: "
                                    + result.getString("session_id"));
                }

                LocalDateTime startTime = result.getTimestamp("start_time")
                        .toLocalDateTime();

                UsageSession session = new UsageSession(
                        result.getString("session_id"),
                        result.getString("user_id"),
                        resource,
                        service,
                        startTime
                );

                Timestamp endTimestamp = result.getTimestamp("end_time");

                if (endTimestamp != null) {
                    BigDecimal totalCharge = getTotalCharge(
                            connection, result.getString("session_id"));

                    session.restoreSession(
                            endTimestamp.toLocalDateTime(),
                            result.getLong("billed_hours"),
                            totalCharge
                    );
                }

                sessions.put(session.getSessionId(), session);
            }
        }

        return sessions;
    }

    // Retrieve the saved charge for a completed session.
    private static BigDecimal getTotalCharge(
            Connection connection,
            String sessionId) throws SQLException {

        String sql = """
                SELECT total_charge
                FROM billing_records
                WHERE session_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sessionId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getBigDecimal("total_charge");
                }
            }
        }

        throw new SQLException(
                "Billing record not found for session: " + sessionId);
    }
}