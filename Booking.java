import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Booking {
    private int id;
    private String studentName;
    private String eventName;
    private String studentRoll;
    private String resource;
    private String phone;
    private String status;
    private LocalDate bookingDate;
    private String bookingTime;

    public Booking() {
    }

    // --- Getters and Setters (Unchanged) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } 
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getStudentRoll() { return studentRoll; }
    public void setStudentRoll(String studentRoll) { this.studentRoll = studentRoll; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    /**
     * NEW: This method is updated to display nicely in a UI list.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dateString = (bookingDate != null) ? bookingDate.format(formatter) : "N/A";
        
        return String.format("ID: %d | Status: %s | Resource: %s | Name: %s | Date: %s",
               id, status, resource, studentName, dateString);
    }
}