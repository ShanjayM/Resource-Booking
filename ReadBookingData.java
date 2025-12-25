import java.sql.*;

public class ReadBookingData {
    private static final String DB_URL = "jdbc:h2:./bookingdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try {
            // Load H2 Driver
            Class.forName("org.h2.Driver");
            
            // Connect to database
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database successfully!\n");
            
            // Query all bookings
            String sql = "SELECT * FROM BOOKINGS ORDER BY ID DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            // Print header
            String separator = "============================================================================================================================";
            System.out.println(separator);
            System.out.printf("%-5s | %-20s | %-15s | %-20s | %-10s | %-12s | %-10s | %-15s | %-15s%n",
                "ID", "Student Name", "Roll Number", "Event Name", "Resource", "Date", "Time", "Phone", "Status");
            System.out.println(separator);
            
            int count = 0;
            // Print data
            while (rs.next()) {
                count++;
                System.out.printf("%-5d | %-20s | %-15s | %-20s | %-10s | %-12s | %-10s | %-15s | %-15s%n",
                    rs.getInt("ID"),
                    rs.getString("STUDENT_NAME") != null ? rs.getString("STUDENT_NAME") : "N/A",
                    rs.getString("STUDENT_ROLL") != null ? rs.getString("STUDENT_ROLL") : "N/A",
                    rs.getString("EVENT_NAME") != null ? rs.getString("EVENT_NAME") : "N/A",
                    rs.getString("RESOURCE") != null ? rs.getString("RESOURCE") : "N/A",
                    rs.getDate("BOOKING_DATE") != null ? rs.getDate("BOOKING_DATE").toString() : "N/A",
                    rs.getString("BOOKING_TIME") != null ? rs.getString("BOOKING_TIME") : "N/A",
                    rs.getString("PHONE") != null ? rs.getString("PHONE") : "N/A",
                    rs.getString("STATUS") != null ? rs.getString("STATUS") : "N/A"
                );
            }
            
            System.out.println(separator);
            System.out.println("\nTotal bookings found: " + count);
            
            // Close resources
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver not found! Make sure h2-2.4.240.jar is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}