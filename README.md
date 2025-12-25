Resource Booking Application
A Java-based desktop application designed to streamline the process of booking university facilities (such as Computing Centers). The system implements a multi-level approval workflow, real-time availability checking, and SMS notifications.

Features
User Authentication: Login system for Students and Staff.

Booking Registration: Students can check availability for specific dates and submit booking requests for resources (CC1 - CC10).

Conflict Detection: Prevents double-booking of the same resource on the same date.

Multi-Level Approval Workflow:

Stage 1: Facility Admin (CC Admin) approval.

Stage 2: Head of Department (HOD) approval.

Status Tracking: Real-time tracking of booking status (Pending-CC, Pending-HOD, Approved, Rejected).

SMS Notifications: Integrated with Twilio to send SMS updates to Admins and Students upon booking submission and status changes.

Search: Search functionality to find bookings by Student Roll Number.

Data Persistence: Uses H2 Database (Embedded) for storing booking records.
Tech Stack
Language: Java (JDK 8+)

GUI: Java Swing (javax.swing)

Database: H2 Database Engine (JDBC)

Notification: Twilio API (SMS)

Build: Standard Java Application

Project Structure
Booking.java: The model class representing a booking entity.

BookingUI.java: The main application class containing the UI, business logic, and database operations.

SmsService.java: Handles the integration with the Twilio API for sending SMS.

ReadBookingData.java: A utility script to view all database records in the console (for debugging).

StartH2Console.java: A utility to start the H2 web console server to inspect the database via a browser.

Setup & Installation
1. Prerequisites
Ensure you have the following libraries added to your project's Classpath/Build Path:

H2 Database Driver: h2-*.jar

Twilio SDK: twilio-*.jar (and its dependencies: Jackson, HttpComponents, etc.)

2. Configuration (SMS)
To enable SMS notifications, you must add your Twilio credentials.

Open SmsService.java.

Update the following constants with your account details:

Java

public static final String ACCOUNT_SID = "YOUR_TWILIO_SID";
public static final String AUTH_TOKEN = "YOUR_TWILIO_AUTH_TOKEN";
private static final String FROM_NUMBER = "YOUR_TWILIO_PHONE_NUMBER";
3. Running the Application
Compile all .java files.

Run BookingUI to start the main application.

Usage & Workflow
1. Student Login & Booking
Login: Enter any Roll Number (e.g., 20CSR001) and a password to log in.

Submit: Go to the "Add New Booking" tab, select a date, check availability, and submit.

Status: The booking will be saved with status Pending-CC.

2. Admin Approval (Level 1)
Go to the "Manage Bookings" tab.

Select the booking from the list.

Enter the credentials for the specific resource (e.g., if "CC1" was booked, use the CC1 admin credentials).

Click Approve. Status changes to Pending-HOD.

3. HOD Approval (Level 2)
Select the booking again (now in Pending-HOD status).

Enter the HOD credentials.

Click Approve. Status changes to Approved.
Default Credentials
Use the following hardcoded credentials to test the approval flow in the "Manage Bookings" tab:
Role,Resource,Username,Password
HOD,All,hod_admin,hod_pass123
Admin,CC1,cc1_admin,cc1_pass123
Admin,CC2,cc2_admin,cc2_pass123
Admin,CC3,cc3_admin,cc3_pass123
Admin,CC4,cc4_admin,cc4_pass123
Admin,CC5,cc5_admin,cc5_pass123
Utility Tools
View Database in Console: Run ReadBookingData.java to see a formatted table of all current bookings in your terminal.

View Database in Browser:

Run StartH2Console.java.

Open your browser to http://localhost:8082.

JDBC URL: jdbc:h2:./bookingdb

User: sa, Password: (leave empty).
