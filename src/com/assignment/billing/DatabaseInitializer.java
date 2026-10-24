package com.assignment.billing;

import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {

        // Meeting Room A
        DatabaseManager.addResource("R101", "Meeting Room A", 6);
        DatabaseManager.addService(
                "SVC101", "R101", "Standard Meeting", 145, 70
        );
        DatabaseManager.addService(
                "SVC102", "R101", "Video Conference", 230, 110
        );

        // Computer Lab
        DatabaseManager.addResource("R102", "Computer Lab", 15);
        DatabaseManager.addService(
                "SVC201", "R102", "Workstation Usage", 55, 30
        );

        // Gym
        DatabaseManager.addResource("R103", "Gym", 8);
        DatabaseManager.addService(
                "SVC301", "R103", "Gym Usage", 75, 35
        );

        // Parking Area
        DatabaseManager.addResource("R104", "Parking Area", 20);
        DatabaseManager.addService(
                "SVC401", "R104", "Vehicle Parking", 40, 20
        );

        // Conference Room B
        DatabaseManager.addResource("R105", "Conference Room B", 10);
        DatabaseManager.addService(
                "SVC501", "R105", "Conference Booking", 280, 135
        );
    }

    public static void main(String[] args) {

        try {
            initialize();

            System.out.println(
                    "Resources and services saved successfully."
            );

        } catch (SQLException e) {
            System.out.println("Database initialization failed.");
            System.out.println(e.getMessage());
        }
    }
}