package com.assignment.billing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = System.getenv()
            .getOrDefault("DB_URL",
                    "jdbc:postgresql://localhost:5432/resource_billing_db");

    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Database credentials are missing. Set DB_USER and DB_PASSWORD environment variables.");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}