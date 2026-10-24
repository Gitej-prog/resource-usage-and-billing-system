Resource Usage and Billing System
1. Project Overview
   The Resource Usage and Billing System is a Java console application for
   managing shared resources and calculating usage charges. Users can start
   and stop sessions, check resource availability, and view billing
   history.

The application uses PostgreSQL to save resources, service plans, usage
sessions, and billing records. Completed sessions remain available after
the application is restarted.
2. Objectives
- Manage resources and their capacities.
- Add service plans with different prices.
- Start and stop resource usage sessions.
- Track session start and end times.
- Check active sessions and available resource slots.
- Calculate charges based on usage time.
- Save session and billing information in PostgreSQL.
- View completed sessions and total revenue.
3. Features
   Resource and Service Management
- Display resources, capacities, services, and prices.
- Each resource can have one or more service plans.
- Check the number of active sessions and available slots.
  Usage Sessions
- Start a session using a user ID, resource ID, and service ID.
- Generate a unique session ID.
- Record the start time and end time.
- View all sessions or only active sessions.
- Look up a session by its ID.
- Prevent the same user from starting multiple active sessions on the
  same resource.
  Billing
- Calculate the bill when a session is stopped.
- Charge at least one hour for each session.
- Round partial hours up to the next billable hour.
- Use separate rates for the first hour and additional hours.
- Save completed billing records in PostgreSQL.
- Display billing history and total revenue.
  Database Storage
- Store resources and service plans in PostgreSQL.
- Save active sessions when they start.
- Save the end time, billed hours, and final charge when a session is
  stopped.
- Load saved resources and sessions when the application starts.
4. Technology Stack
- Language: Java 21
- Database: PostgreSQL 18
- Database Connectivity: JDBC
- IDE: IntelliJ IDEA
- Interface: Console / Command Line
5. Project Structure
   resource_usage_and_billing_system/
   └── src/
   └── com/
   └── assignment/
   └── billing/
   ├── Main.java
   ├── BillingSystem.java
   ├── Resource.java
   ├── ServicePlan.java
   ├── UsageSession.java
   ├── DatabaseConnection.java
   ├── DatabaseConnectionTest.java
   ├── DatabaseManager.java
   └── DatabaseInitializer.java
6. Class Responsibilities
- Main.java: Displays the menu, reads user input, and calls the
  application functions.
- BillingSystem.java: Handles resource lookup, session operations,
  capacity checks, and billing calculations.
- Resource.java: Stores a resource's ID, name, capacity, and
  service plans.
- ServicePlan.java: Stores a service's ID, name, first-hour price,
  and additional-hour price.
- UsageSession.java: Stores session details, timestamps, billed
  hours, and total charge.
- DatabaseConnection.java: Creates a connection to PostgreSQL.
- DatabaseConnectionTest.java: Tests whether the database
  connection works.
- DatabaseManager.java: Saves and loads resources, service plans,
  sessions, and billing records.
- DatabaseInitializer.java: Provides database initialization logic
  used by the project.
7. Data Structures and Java Classes Used
- LinkedHashMap: Stores resources and sessions by ID and keeps
  insertion order.
- ArrayList: Returns collections of resources or sessions for
  display.
- BigDecimal: Handles prices and billing amounts accurately.
- LocalDateTime: Stores session start and end times.
- Duration: Calculates the time between the start and end of a
  session.
- UUID: Generates unique session IDs.
- JDBC: Connects Java code to PostgreSQL and runs SQL statements.
8. Database Design
   The application uses the following PostgreSQL tables:
- resources: Stores resource IDs, names, and capacities.
- service_plans: Stores services and their first-hour and
  additional-hour prices.
- usage_sessions: Stores user, resource, service, start time, end
  time, billed hours, and session status.
- billing_records: Stores the billed hours, final charge, and
  billing timestamp for completed sessions.
  The tables use primary keys and foreign keys to link resources, service
  plans, sessions, and billing records.
9. Billing Logic
   Each service has a first-hour price and an additional-hour price. Usage
   time is rounded up to a whole billable hour, with a minimum of one hour.

For one billed hour:

Total charge = First-hour price

For more than one billed hour:

Total charge = First-hour price + (Billed hours - 1) × Additional-hour price

Example for Standard Meeting (SVC101):

Usage duration         Billed hours   Total charge
10 minutes                        1           ₹145
1 hour                            1           ₹145
1 hour 10 minutes                 2           ₹215
2 hours                           2           ₹215
2 hours 15 minutes                3           ₹285
10. Sample Resources and Services
    Resources
    Resource ID   Resource name         Capacity
    R101          Meeting Room A               6
    R102          Computer Lab                15
    R103          Gym                          8
    R104          Parking Area                20
    R105          Conference Room B           10
    Services
    Service ID   Resource ID  Service name        First hour  Additional hour
    SVC101       R101         Standard                  ₹145              ₹70
    Meeting

SVC102       R101         Video                     ₹230             ₹110
Conference

SVC201       R102         Workstation                ₹55              ₹30
Usage

SVC301       R103         Gym Usage                  ₹75              ₹35

SVC401       R104         Vehicle                    ₹40              ₹20
Parking

SVC501       R105         Conference                ₹280             ₹135
Booking                        
Make sure these records exist in PostgreSQL before testing the
application. Do not insert them again if they are already present.
11. Prerequisites
    Before running the project, install or prepare:
1. JDK 21.
2. PostgreSQL 18, with the PostgreSQL service running.
3. IntelliJ IDEA or another Java IDE.
4. The PostgreSQL JDBC driver.
5. The project source files.
   Check Java from a terminal:
   java -version
12. Database Setup
1. Open PostgreSQL using pgAdmin or psql.
2. Create the database if it does not already exist:
   CREATE DATABASE resource_billing_db;
3. Connect to resource_billing_db.
4. Create the tables and indexes required by the application, using the
   schema provided for the project.
5. Add the sample resource and service records listed above if they are
   not already present.
6. Open DatabaseConnection.java and check the JDBC URL, username, and
   password. Keep your actual database password private.
   The usual local JDBC URL for this setup is:
   jdbc:postgresql://localhost:5432/resource_billing_db
   If PostgreSQL uses a different port or your database has a different
   name, update the connection settings accordingly.

Do not run the table-creation SQL again if the tables already exist.
13. How to Build and Run
    Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA.
2. Make sure the src folder is marked as Sources Root.
3. Confirm that the PostgreSQL JDBC driver is listed under the project
   dependencies.
4. Select Build → Build Project.
5. Open Main.java.
6. Click the green Run button next to the main() method.
   When the application starts, it should connect to PostgreSQL and load
   the saved resources and sessions.
   Using a Terminal
   From the project root, compile the source files:
   javac -cp "C:\path\to\postgresql-jdbc.jar" -d out src\com\assignment\billing\*.java
   Run the application:
   java -cp "out;C:\path\to\postgresql-jdbc.jar" com.assignment.billing.Main
   Replace C:\path\to\postgresql-jdbc.jar with the actual path to your
   JDBC driver. These commands are for Windows.
14. Application Menu
1. View All Resources
2. Start Resource Usage
3. Stop Resource Usage
4. View Resource Availability
5. View All Sessions
6. View Active Sessions
7. View Session Details
8. View Billing History
0. Exit
- 1: Displays resources, capacities, services, and prices.
- 2: Starts a usage session.
- 3: Stops a session and calculates its bill.
- 4: Shows active users and available slots for a resource.
- 5: Displays all sessions.
- 6: Displays active sessions only.
- 7: Displays details for a session ID.
- 8: Displays completed billing records and total revenue.
- 0: Exits the application.
15. Testing
    The following checks can be used to test the application:
1. Build the project: Use Build → Build Project in IntelliJ and
   check that compilation succeeds.
2. Connect to PostgreSQL: Run the application and confirm that the
   database connection succeeds.
3. Load resources: Select option 1 and check the resource and
   service details.
4. Start a session: Select option 2, enter a user ID, R101, and
   SVC101. Save the session ID shown by the application.
5. Check active sessions: Select option 6 and confirm the session
   appears.
6. Check persistence: Exit and run the application again. Confirm
   the active session is still listed.
7. Stop the session: Select option 3 and enter the session ID.
   Check the end time, billed hours, and charge.
8. Check billing history: Select option 8 and confirm the
   completed session and charge appear.
9. Check persistence after completion: Restart the application and
   confirm the completed session and billing history remain available.
10. Check invalid input: Try an unknown resource ID, an invalid
    service ID, or a nonexistent session ID and check that the
    application reports the problem.
16. Assumptions and Limitations
- A resource's capacity is the maximum number of active sessions it
  can support.
- Each active session uses one slot.
- A user cannot have multiple active sessions on the same resource.
- Each session uses one service associated with its resource.
- Every started session is charged for at least one hour.
- Partial hours are rounded up.
- Prices are configured in the service plans.
- The application uses a console interface.
- Database availability is required to load and save persistent data.
- The current application is a simple console project, not a
  multi-user web application.
17. Future Improvements
    Possible additions include:
- User login and role-based access.
- PDF invoices.
- Discounts, taxes, and refunds.
- A graphical interface or REST API.
- Automated unit tests using JUnit.
- More detailed reports and search options.
- Better handling of simultaneous requests to the same resource.
18. Conclusion
    The Resource Usage and Billing System uses Java and PostgreSQL to manage
    resources, track usage sessions, calculate charges, and maintain billing
    history. It also demonstrates object-oriented programming, Java
    collections, JDBC, SQL relationships,