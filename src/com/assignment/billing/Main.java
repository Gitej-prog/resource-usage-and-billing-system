package com.assignment.billing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BillingSystem billingSystem = new BillingSystem();

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   RESOURCE USAGE AND BILLING SYSTEM");
        System.out.println("========================================");
        System.out.println("Connected to PostgreSQL successfully.");

        boolean running = true;

        while (running) {
            displayMenu();

            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        viewAllResources();
                        break;

                    case 2:
                        startResourceUsage();
                        break;

                    case 3:
                        stopResourceUsage();
                        break;

                    case 4:
                        viewResourceAvailability();
                        break;

                    case 5:
                        viewAllSessions();
                        break;

                    case 6:
                        viewActiveSessions();
                        break;

                    case 7:
                        viewSessionDetails();
                        break;

                    case 8:
                        viewBillingHistory();
                        break;

                    case 0:
                        running = false;
                        System.out.println("Thank you for using the system!");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // Display the main menu.
    private static void displayMenu() {

        System.out.println("\n========================================");
        System.out.println("                MAIN MENU");
        System.out.println("========================================");
        System.out.println("1. View All Resources");
        System.out.println("2. Start Resource Usage");
        System.out.println("3. Stop Resource Usage");
        System.out.println("4. View Resource Availability");
        System.out.println("5. View All Sessions");
        System.out.println("6. View Active Sessions");
        System.out.println("7. View Session Details");
        System.out.println("8. View Billing History");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    // Display all resources loaded from PostgreSQL.
    private static void viewAllResources() {

        List<Resource> resources = billingSystem.getAllResources();

        System.out.println("\n========== AVAILABLE RESOURCES ==========");

        if (resources.isEmpty()) {
            System.out.println("No resources available.");
            return;
        }

        for (Resource resource : resources) {

            String resourceId = resource.getResourceId();

            System.out.println("\nResource ID     : " + resourceId);
            System.out.println("Resource Name   : " + resource.getResourceName());
            System.out.println("Capacity        : " + resource.getCapacity());

            System.out.println("Active Users    : "
                    + billingSystem.getActiveUserCount(resourceId));

            System.out.println("Available Slots : "
                    + billingSystem.getAvailableSlots(resourceId));

            System.out.println("Services:");

            for (ServicePlan service : resource.getServices().values()) {

                System.out.println("  Service ID       : "
                        + service.getServiceId());

                System.out.println("  Service Name     : "
                        + service.getServiceName());

                System.out.println("  First Hour Price : Rs. "
                        + service.getFirstHourPrice().toPlainString());

                System.out.println("  Additional Hour  : Rs. "
                        + service.getAdditionalHourPrice().toPlainString());
            }

            System.out.println("-----------------------------------------");
        }
    }

    // Start resource usage and save the session to PostgreSQL.
    private static void startResourceUsage() {

        System.out.println("\n========== START RESOURCE USAGE ==========");

        String userId = readText("Enter User ID: ");
        String resourceId = readText("Enter Resource ID: ");

        Resource resource = billingSystem.getResource(resourceId);

        if (resource == null) {
            System.out.println("Resource not found.");
            return;
        }

        System.out.println("\nAvailable Services:");

        for (ServicePlan service : resource.getServices().values()) {

            System.out.println(
                    service.getServiceId()
                            + " - " + service.getServiceName()
                            + " | First Hour: Rs. "
                            + service.getFirstHourPrice().toPlainString()
                            + " | Additional Hour: Rs. "
                            + service.getAdditionalHourPrice().toPlainString()
            );
        }

        String serviceId = readText("Enter Service ID: ");

        UsageSession session = billingSystem.startUsage(
                userId,
                resourceId,
                serviceId
        );

        System.out.println("\nResource usage started successfully!");
        System.out.println("Session ID : " + session.getSessionId());
        System.out.println("User ID    : " + session.getUserId());

        System.out.println("Resource   : "
                + session.getResource().getResourceName());

        System.out.println("Service    : "
                + session.getService().getServiceName());

        System.out.println("Start Time : " + session.getStartTime());
    }

    // Stop resource usage and save billing details to PostgreSQL.
    private static void stopResourceUsage() {

        System.out.println("\n========== STOP RESOURCE USAGE ==========");

        String sessionId = readText("Enter Session ID: ");

        UsageSession session = billingSystem.stopUsage(sessionId);

        System.out.println("\nResource usage stopped successfully!");
        billingSystem.displaySession(session);
    }

    // Display resource capacity and availability.
    private static void viewResourceAvailability() {

        System.out.println("\n========== RESOURCE AVAILABILITY ==========");

        String resourceId = readText("Enter Resource ID: ");

        Resource resource = billingSystem.getResource(resourceId);

        if (resource == null) {
            System.out.println("Resource not found.");
            return;
        }

        System.out.println("Resource Name   : " + resource.getResourceName());
        System.out.println("Capacity        : " + resource.getCapacity());

        System.out.println("Active Users    : "
                + billingSystem.getActiveUserCount(resourceId));

        System.out.println("Available Slots : "
                + billingSystem.getAvailableSlots(resourceId));
    }

    // Display all sessions, including sessions restored from PostgreSQL.
    private static void viewAllSessions() {

        System.out.println("\n========== ALL SESSIONS ==========");

        List<UsageSession> sessions = billingSystem.getAllSessions();

        if (sessions.isEmpty()) {
            System.out.println("No sessions found.");
            return;
        }

        for (UsageSession session : sessions) {
            billingSystem.displaySession(session);
        }
    }

    // Display active sessions.
    private static void viewActiveSessions() {

        System.out.println("\n========== ACTIVE SESSIONS ==========");

        List<UsageSession> sessions = billingSystem.getActiveSessions();

        if (sessions.isEmpty()) {
            System.out.println("No active sessions.");
            return;
        }

        for (UsageSession session : sessions) {
            billingSystem.displaySession(session);
        }
    }

    // Display details for a particular session.
    private static void viewSessionDetails() {

        System.out.println("\n========== SESSION DETAILS ==========");

        String sessionId = readText("Enter Session ID: ");

        UsageSession session = billingSystem.getSession(sessionId);

        if (session == null) {
            System.out.println("Session not found.");
            return;
        }

        billingSystem.displaySession(session);
    }

    // Display billing history and calculate total revenue.
    private static void viewBillingHistory() {

        System.out.println("\n================ BILLING HISTORY ================");

        List<UsageSession> sessions = billingSystem.getAllSessions();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int completedTransactions = 0;

        System.out.printf(
                "%-38s %-12s %-22s %-14s%n",
                "SESSION ID", "USER ID", "RESOURCE", "AMOUNT (Rs.)"
        );

        System.out.println(
                "--------------------------------------------------------------------------------"
        );

        for (UsageSession session : sessions) {

            // Include only completed sessions with a bill.
            if (!session.isActive()) {

                completedTransactions++;

                BigDecimal amount = session.getTotalCharge();
                totalRevenue = totalRevenue.add(amount);

                System.out.printf(
                        "%-38s %-12s %-22s Rs. %-10s%n",
                        session.getSessionId(),
                        session.getUserId(),
                        session.getResource().getResourceName(),
                        amount.toPlainString()
                );

                System.out.println("  Service    : "
                        + session.getService().getServiceName());

                System.out.println("  Started    : "
                        + session.getStartTime());

                System.out.println("  Ended      : "
                        + session.getEndTime());

                System.out.println("  Billed For : "
                        + session.getBilledHours() + " hour(s)");

                System.out.println(
                        "--------------------------------------------------------------------------------"
                );
            }
        }

        if (completedTransactions == 0) {
            System.out.println("No completed billing transactions found.");
        }

        System.out.println("\n============== BILLING SUMMARY ==============");
        System.out.println("Completed Transactions : " + completedTransactions);
        System.out.println("Total Revenue           : Rs. "
                + totalRevenue.toPlainString());
        System.out.println("=============================================");
    }

    // Read non-empty text from the user.
    private static String readText(String prompt) {

        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // Read an integer from the user.
    private static int readInt(String prompt) {

        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}