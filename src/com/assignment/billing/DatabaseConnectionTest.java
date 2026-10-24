package com.assignment.billing;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println("Database connected successfully!");

            System.out.println(
                    "Connected to: " +
                            connection.getMetaData().getDatabaseProductName()
            );

        } catch (SQLException e) {

            System.out.println("Database connection failed.");
            System.out.println(e.getMessage());

        }
    }

}