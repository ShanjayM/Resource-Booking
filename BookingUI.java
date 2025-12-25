// BookingUI.java

// Import all the new UI (Swing) and backend classes
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BookingUI {

    // --- All Backend Logic and Data ---
    private static List<Booking> bookings = new ArrayList<>();
    private static SmsService smsService = new SmsService();

    // Constants for Tab Indices
    private static final int HOME_TAB_INDEX = 0;
    private static final int ADD_BOOKING_TAB_INDEX = 1; 
    private static final int MANAGE_BOOKINGS_TAB_INDEX = 2; 
    private static final int SEARCH_TAB_INDEX = 3; 

    private static String loggedInStudentRoll = null; 
    
    // Resource list constants
    private static final String[] ALL_RESOURCES = {
        "CC1", "CC2", "CC3", "CC4", "CC5","CC6", "CC7", "CC8", "CC9", "CC10"
    };
    private static final String DEFAULT_RESOURCE_SELECTION = "--- Select a Facility ---";
    
    // Admin and HOD Usernames/Passwords
    private static final String CC1_USER = "cc1_admin";
    private static final String CC1_PASS = "cc1_pass123";
    private static final String CC2_USER = "cc2_admin";
    private static final String CC2_PASS = "cc2_pass123";
    private static final String CC3_USER = "cc3_admin";
    private static final String CC3_PASS = "cc3_pass123";
    private static final String CC4_USER = "cc4_admin";
    private static final String CC4_PASS = "cc4_pass123";
    private static final String CC5_USER = "cc5_admin";
    private static final String CC5_PASS = "cc5_pass123";
    private static final String HOD_USER = "hod_admin";
    private static final String HOD_PASS = "hod_pass123";

    private static final String HOD_PHONE_NUMBER = "+910000000000";
    private static final String DB_URL = "jdbc:h2:./bookingdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    // --- UI Components ---
    private static JList<Booking> bookingList;
    private static DefaultListModel<Booking> listModel;
    private static JList<Booking> searchResultList;
    private static DefaultListModel<Booking> searchListModel;
    
    private static JTabbedPane mainTabbedPane;

    // --- Main Method ---
    public static void main(String[] args) {
        initDatabase();
        loadBookingsFromDb();

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    // --- UI Creation Methods ---
    private static void showLoginScreen() {
        if (loggedInStudentRoll != null) return;
        
        JFrame loginFrame = new JFrame("Student/Staff Login - Event Registration");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        loginFrame.setSize(400, 200);
        loginFrame.setLocationRelativeTo(mainTabbedPane); 

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        JTextField rollField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15); 
        JButton loginButton = new JButton("Login / Register");

        c.gridx = 0; c.gridy = 0; c.weightx = 0.3; panel.add(new JLabel("Roll/Staff ID:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 0.7; panel.add(rollField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0.3; panel.add(new JLabel("Password:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 0.7; panel.add(passwordField, c);

        c.gridx = 1; c.gridy = 2; c.anchor = GridBagConstraints.EAST; c.fill = GridBagConstraints.NONE;
        panel.add(loginButton, c);

        loginButton.addActionListener(e -> {
            String roll = rollField.getText().trim();
            String pass = new String(passwordField.getPassword());
            
            if (!roll.isEmpty() && !pass.isEmpty()) {
                loggedInStudentRoll = roll; 
                loginFrame.dispose();       
                
                mainTabbedPane.setSelectedIndex(ADD_BOOKING_TAB_INDEX); 
                mainTabbedPane.setComponentAt(ADD_BOOKING_TAB_INDEX, createAddBookingTab());
                showSuccess("Welcome, " + roll + "! You can now submit a booking.");
            } else {
                showError("Roll/Staff ID and Password cannot be empty.");
            }
        });

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }
    
    private static JPanel createHomePageTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 15, 15, 15);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2; 
        
        JLabel welcomeLabel = new JLabel("Welcome to the Facility Booking Portal", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        c.gridx = 0; c.gridy = 0; 
        panel.add(welcomeLabel, c);

        JButton registerButton = new JButton("1. Register New Booking");
        JButton manageButton = new JButton("2. View/Manage Bookings (Staff/Admin)");
        JButton searchButton = new JButton("3. Search Your Bookings");
        
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        manageButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 14));

        c.gridy = 1; panel.add(new JSeparator(), c);
        c.gridy = 2; panel.add(registerButton, c);
        c.gridy = 3; panel.add(manageButton, c);
        c.gridy = 4; panel.add(searchButton, c);

        registerButton.addActionListener(e -> mainTabbedPane.setSelectedIndex(ADD_BOOKING_TAB_INDEX));
        manageButton.addActionListener(e -> mainTabbedPane.setSelectedIndex(MANAGE_BOOKINGS_TAB_INDEX));
        searchButton.addActionListener(e -> mainTabbedPane.setSelectedIndex(SEARCH_TAB_INDEX));

        return panel;
    }


    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Resource Booking Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        mainTabbedPane = new JTabbedPane(); 

        JPanel homeTab = createHomePageTab(); 
        JPanel addBookingTab = createAddBookingTab();
        JPanel manageBookingsTab = createManageBookingsTab();
        JPanel searchTab = createSearchTab();

        mainTabbedPane.addTab("Home", homeTab); 
        mainTabbedPane.addTab("Add New Booking (Register)", addBookingTab);
        mainTabbedPane.addTab("Manage Bookings (Admin/Status)", manageBookingsTab); 
        mainTabbedPane.addTab("Search by Roll Number", searchTab);
        
        mainTabbedPane.setSelectedIndex(HOME_TAB_INDEX); 

        // Listener to intercept clicks on the registration tab
        mainTabbedPane.addChangeListener(new ChangeListener() {
            private int lastSelectedIndex = HOME_TAB_INDEX;

            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = mainTabbedPane.getSelectedIndex();
                
                if (selectedIndex == ADD_BOOKING_TAB_INDEX && loggedInStudentRoll == null) {
                    
                    if (lastSelectedIndex != ADD_BOOKING_TAB_INDEX) {
                         mainTabbedPane.setSelectedIndex(lastSelectedIndex);
                    } else {
                         mainTabbedPane.setSelectedIndex(HOME_TAB_INDEX); 
                    }
                   
                    showLoginScreen();
                } else {
                    lastSelectedIndex = selectedIndex;
                    
                    if (selectedIndex == ADD_BOOKING_TAB_INDEX && loggedInStudentRoll != null) {
                        mainTabbedPane.setComponentAt(ADD_BOOKING_TAB_INDEX, createAddBookingTab());
                    }
                }
            }
        });

        frame.add(mainTabbedPane);
        frame.setVisible(true);
    }
    
    // Helper method to get unavailable resources for a date
    private static List<String> getUnavailableResources(LocalDate date) {
        List<String> unavailable = new ArrayList<>();
        if (date == null) return unavailable;

        for (Booking booking : bookings) {
            String status = booking.getStatus();
            // Check only bookings that are confirmed or pending approval
            if (status.equals("Approved") || status.equals("Pending-HOD") || status.equals("Pending-CC")) {
                if (date.equals(booking.getBookingDate())) {
                    unavailable.add(booking.getResource());
                }
            }
        }
        return unavailable;
    }
    
    // Helper method to update the resource JComboBox. Returns the count of available resources.
    private static int updateResourceComboBox(JComboBox<String> resourceBox, String dateString) {
        LocalDate bookingDate = null;
        final String defaultDateText = "dd-MM-yyyy";
        
        try {
            if (!dateString.isEmpty() && !dateString.equals(defaultDateText)) {
                 bookingDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } else {
                 resourceBox.setEnabled(true);
                 resourceBox.removeAllItems();
                 resourceBox.addItem(DEFAULT_RESOURCE_SELECTION);
                 for (String resource : ALL_RESOURCES) {
                     resourceBox.addItem(resource);
                 }
                 return ALL_RESOURCES.length; 
            }
        } catch (DateTimeParseException ex) {
            resourceBox.setEnabled(false);
            resourceBox.removeAllItems();
            resourceBox.addItem("--- Invalid Date Format ---");
            return 0; 
        }
        
        List<String> unavailableResources = getUnavailableResources(bookingDate);
        
        resourceBox.setEnabled(false);
        resourceBox.removeAllItems();
        resourceBox.addItem(DEFAULT_RESOURCE_SELECTION);

        int availableCount = 0;
        for (String resource : ALL_RESOURCES) {
            if (!unavailableResources.contains(resource)) {
                resourceBox.addItem(resource);
                availableCount++;
            }
        }
        
        resourceBox.setEnabled(true);
        
        if (availableCount == 0) {
             resourceBox.removeAllItems();
             resourceBox.addItem("--- No Resources Available on this Date ---");
             resourceBox.setEnabled(false);
        }
        
        return availableCount;
    }


    private static JPanel createAddBookingTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        class FormBuilder {
            int row = 0;
            void addRow(String labelText, Component field) {
                c.gridx = 0; c.gridy = row; c.weightx = 0.3; panel.add(new JLabel(labelText), c);
                c.gridx = 1; c.gridy = row; c.weightx = 0.7; panel.add(field, c);
                row++;
            }
        }
        FormBuilder form = new FormBuilder();

        JTextField nameField = new JTextField(20);
        JTextField rollField = new JTextField(20);
        
        // Roll/Staff ID setup (based on login)
        if (loggedInStudentRoll != null) {
            rollField.setText(loggedInStudentRoll); 
            rollField.setEditable(false); 
        } else {
            rollField.setText("Login required");
            rollField.setEditable(false); 
        }
        
        JTextField eventField = new JTextField(20);
        
        JComboBox<String> resourceBox = new JComboBox<>();
        JTextField dateField = new JTextField("dd-MM-yyyy");
        JTextField timeField = new JTextField("10:00 AM");
        
        // --- UI FIX: Wider phone field (using 20 columns) ---
        JTextField phoneField = new JTextField("+91...", 20); 
        JButton submitButton = new JButton("Submit Booking");

        // --- UI FIX: Focus Listener to clear default date text on click ---
        final String defaultDateText = "dd-MM-yyyy";
        dateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (dateField.getText().equals(defaultDateText)) {
                    dateField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (dateField.getText().isEmpty()) {
                    dateField.setText(defaultDateText);
                }
            }
        });
        // -------------------------------------------------------------------

        // Initial population of resourceBox
        updateResourceComboBox(resourceBox, dateField.getText());
        
        // Button to trigger resource filtering
        JButton checkAvailabilityButton = new JButton("Check Availability");
        
        // Listener for Check Availability Button (with pop-up)
        checkAvailabilityButton.addActionListener(e -> {
             int availableCount = updateResourceComboBox(resourceBox, dateField.getText());
             
             if (availableCount > 0) {
                 showSuccess(String.format("Availability Check Complete! %d resource(s) are available on %s. Please select from the dropdown.", 
                                           availableCount, dateField.getText()));
             } else if (availableCount == 0) {
                 if (resourceBox.getItemCount() == 1 && resourceBox.getItemAt(0).equals("--- Invalid Date Format ---")) {
                     showError("Please enter the date in the valid format (dd-MM-yyyy) before checking availability.");
                 } else {
                     showError(String.format("Availability Check Complete! No resources are available on %s.", dateField.getText()));
                 }
             }
        });

        // Disable submission if not logged in
        if (loggedInStudentRoll == null) {
            submitButton.setEnabled(false);
            nameField.setEditable(false);
            eventField.setEditable(false);
            dateField.setEditable(false);
            phoneField.setEditable(false);
            resourceBox.setEnabled(false);
            checkAvailabilityButton.setEnabled(false);
        }

        form.addRow("Student Name:", nameField);
        form.addRow("Roll/Staff ID:", rollField);
        form.addRow("Event Name:", eventField);
        form.addRow("Booking Date (dd-MM-yyyy):", dateField);
        
        // Add the Check button below the Date field
        c.gridx = 1; c.gridy = form.row; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.NONE;
        panel.add(checkAvailabilityButton, c);
        form.row++; 

        form.addRow("Facility:", resourceBox);
        form.addRow("Booking Time (e.g., 10:00 AM):", timeField);
        form.addRow("Your Phone (+91...):", phoneField);

        c.gridx = 1; c.gridy = form.row; c.anchor = GridBagConstraints.EAST; c.fill = GridBagConstraints.NONE;
        panel.add(submitButton, c);

        submitButton.addActionListener(e -> {
            addBooking_UI(
                nameField.getText(), rollField.getText(), eventField.getText(),
                (String)resourceBox.getSelectedItem(), dateField.getText(),
                timeField.getText(), phoneField.getText()
            );
            nameField.setText("");
            eventField.setText("");
            // Refresh resource box to show new availability state
            updateResourceComboBox(resourceBox, dateField.getText()); 
        });
        return panel;
    }

    private static JPanel createManageBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        listModel = new DefaultListModel<>();
        refreshBookingList();
        bookingList = new JList<>(listModel);
        bookingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(bookingList);
        panel.add(listScrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginPanel.add(new JLabel("Admin Username:"));
        JTextField userField = new JTextField(15);
        loginPanel.add(userField);
        loginPanel.add(new JLabel("Admin Password:"));
        JPasswordField passField = new JPasswordField(15);
        loginPanel.add(passField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton approveButton = new JButton("Approve Selected");
        JButton rejectButton = new JButton("Reject Selected");
        JButton refreshButton = new JButton("Refresh List");
        buttonPanel.add(approveButton); buttonPanel.add(rejectButton); buttonPanel.add(refreshButton);

        actionPanel.add(new JLabel("Select a booking, enter credentials, and click action.", SwingConstants.CENTER), BorderLayout.NORTH);
        actionPanel.add(loginPanel, BorderLayout.CENTER);
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(actionPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> {
            Booking selectedBooking = bookingList.getSelectedValue();
            if(selectedBooking == null) { showError("Please select a booking."); return; }
            updateBookingStatus_UI(selectedBooking, "Approved", userField.getText(), new String(passField.getPassword()));
            passField.setText("");
        });

        rejectButton.addActionListener(e -> {
            Booking selectedBooking = bookingList.getSelectedValue();
            if(selectedBooking == null) { showError("Please select a booking."); return; }
            updateBookingStatus_UI(selectedBooking, "Rejected", userField.getText(), new String(passField.getPassword()));
            passField.setText("");
        });

        refreshButton.addActionListener(e -> refreshBookingList());
        return panel;
    }

    private static JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBarPanel.add(new JLabel("Enter Student Roll Number:"));
        JTextField rollSearchField = new JTextField(20);
        searchBarPanel.add(rollSearchField);
        JButton searchButton = new JButton("Search");
        searchBarPanel.add(searchButton);
        panel.add(searchBarPanel, BorderLayout.NORTH);

        searchListModel = new DefaultListModel<>();
        searchResultList = new JList<>(searchListModel);
        JScrollPane listScrollPane = new JScrollPane(searchResultList);
        panel.add(listScrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String rollNumber = rollSearchField.getText();
            if (rollNumber.isEmpty()) {
                showError("Please enter a roll number to search.");
                return;
            }
            searchByRollNumber_UI(rollNumber); 
        });
        return panel;
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void refreshBookingList() {
        loadBookingsFromDb();
        listModel.clear();
        for (Booking b : bookings) {
            listModel.addElement(b);
        }
    }

    private static void addBooking_UI(String name, String roll, String event, String resource, String date, String time, String phone) {
        if (resource.equals(DEFAULT_RESOURCE_SELECTION) || resource.equals("--- No Resources Available on this Date ---")) {
            showError("Please select a specific resource (CC1-CC5) for your booking.");
            return;
        }
        
        if(name.isEmpty() || roll.isEmpty() || date.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all required fields."); return;
        }
        LocalDate bookingDate;
        try {
            bookingDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            showError("Invalid Date Format. Please use dd-MM-yyyy."); return;
        }

        // CONFLICT CHECK at the moment of submission
        List<String> bookedResources = getUnavailableResources(bookingDate);
        if (bookedResources.contains(resource)) {
             showError(String.format("The resource %s is now unavailable on %s. Please select another resource or date.", resource, date));
             return;
        }
        
        Booking newBooking = new Booking();
        newBooking.setStudentName(name); newBooking.setStudentRoll(roll); newBooking.setEventName(event);
        newBooking.setResource(resource); newBooking.setBookingDate(bookingDate);
        newBooking.setBookingTime(time); newBooking.setPhone(phone); newBooking.setStatus("Pending-CC");

        String insertSQL = "INSERT INTO BOOKINGS (STUDENT_NAME, STUDENT_ROLL, EVENT_NAME, RESOURCE, BOOKING_DATE, BOOKING_TIME, PHONE, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name); pstmt.setString(2, roll); pstmt.setString(3, event);
            pstmt.setString(4, resource); pstmt.setDate(5, Date.valueOf(bookingDate));
            pstmt.setString(6, time); pstmt.setString(7, phone); pstmt.setString(8, "Pending-CC");
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) { newBooking.setId(generatedKeys.getInt(1)); }
            }

            String targetAdminPhone;
            switch (resource) {
                case "CC1": targetAdminPhone = "+91"; break;
                case "CC2": targetAdminPhone = "+91"; break;
                case "CC3": targetAdminPhone = "+91"; break;
                case "CC4": targetAdminPhone = "+91"; break;
                case "CC5": targetAdminPhone = "+91"; break;
                default: targetAdminPhone = null;
            }

            if (targetAdminPhone != null) {
                String adminMessage = String.format("New booking by %s (Roll: %s) for resource %s. Awaiting approval.", name, roll, resource);
                new Thread(() -> smsService.sendSms(targetAdminPhone, adminMessage)).start();
            }

            showSuccess("Booking submitted! Status: Pending-CC.");
            refreshBookingList();

        } catch (SQLException e) {
            showError("Error saving to database: " + e.getMessage()); e.printStackTrace();
        }
    }

    private static void updateBookingStatus_UI(Booking bookingToUpdate, String newStatusInput, String username, String password) {
        String oldStatus = bookingToUpdate.getStatus();
        String resource = bookingToUpdate.getResource();

        if (oldStatus.equals("Approved") || oldStatus.equals("Rejected")) {
            showError("This booking has already been finalized."); return;
        }

        String requiredUser = null, requiredPass = null;
        if (oldStatus.equals("Pending-CC")) {
            switch (resource) {
                case "CC1": requiredUser = CC1_USER; requiredPass = CC1_PASS; break;
                case "CC2": requiredUser = CC2_USER; requiredPass = CC2_PASS; break;
                case "CC3": requiredUser = CC3_USER; requiredPass = CC3_PASS; break;
                case "CC4": requiredUser = CC4_USER; requiredPass = CC4_PASS; break;
                case "CC5": requiredUser = CC5_USER; requiredPass = CC5_PASS; break;
            }
        } else if (oldStatus.equals("Pending-HOD")) {
            requiredUser = HOD_USER; requiredPass = HOD_PASS;
        }

        if (requiredUser == null || !username.equals(requiredUser) || !password.equals(requiredPass)) {
            showError("Access Denied. Incorrect username or password."); return;
        }

        String finalDbStatus = oldStatus;
        String userPhone = bookingToUpdate.getPhone();
        String userMessage = "";
        boolean sendToUser = false, sendToHod = false;

        if (oldStatus.equals("Pending-CC")) {
            if (newStatusInput.equalsIgnoreCase("Approved")) {
                finalDbStatus = "Pending-HOD"; sendToHod = true;
            } else if (newStatusInput.equalsIgnoreCase("Rejected")) {
                finalDbStatus = "Rejected";
                userMessage = String.format("Your booking for '%s' rejected by CC.", bookingToUpdate.getEventName());
                sendToUser = true;
            }
        } else if (oldStatus.equals("Pending-HOD")) {
            if (newStatusInput.equalsIgnoreCase("Approved")) {
                finalDbStatus = "Approved";
                userMessage = String.format("Your booking for '%s' approved by HOD.", bookingToUpdate.getEventName());
                sendToUser = true;
            } else if (newStatusInput.equalsIgnoreCase("Rejected")) {
                finalDbStatus = "Rejected";
                userMessage = String.format("Your booking for '%s' rejected by HOD.", bookingToUpdate.getEventName());
                sendToUser = true;
            }
        }

        if (finalDbStatus.equals(oldStatus)) return;

        String updateSQL = "UPDATE BOOKINGS SET STATUS = ? WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, finalDbStatus); pstmt.setInt(2, bookingToUpdate.getId());
            pstmt.executeUpdate();

            final boolean finalSendToHod = sendToHod;
            final boolean finalSendToUser = sendToUser;
            final String finalUserMessage = userMessage;

            new Thread(() -> { 
                if (finalSendToHod) {
                    String hodMessage = String.format("HOD: Booking %d for %s by %s approved by CC. Awaiting approval.",
                                                        bookingToUpdate.getId(), resource, bookingToUpdate.getStudentName());
                    smsService.sendSms(HOD_PHONE_NUMBER, hodMessage);
                }
                if (finalSendToUser && userPhone != null && !userPhone.isEmpty()) {
                    smsService.sendSms(userPhone, finalUserMessage); 
                }
            }).start();

            showSuccess("Status updated to: " + finalDbStatus);
            refreshBookingList();

        } catch (SQLException e) {
            showError("Error updating status: " + e.getMessage()); e.printStackTrace();
        }
    }

    private static void searchByRollNumber_UI(String rollNumber) {
        searchListModel.clear();
        boolean found = false;
        for (Booking b : bookings) {
            if (b.getStudentRoll().equalsIgnoreCase(rollNumber)) {
                searchListModel.addElement(b); 
                found = true;
            }
        }
        if (!found) {
            showSuccess("No bookings found for roll number: " + rollNumber);
        }
    }

    private static Connection getConnection() throws SQLException {
        try { Class.forName("org.h2.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException("H2 driver not found", e); }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void initDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS BOOKINGS (ID INT AUTO_INCREMENT PRIMARY KEY,STUDENT_NAME VARCHAR(255),STUDENT_ROLL VARCHAR(255),EVENT_NAME VARCHAR(255),RESOURCE VARCHAR(255),BOOKING_DATE DATE,BOOKING_TIME VARCHAR(100),PHONE VARCHAR(100),STATUS VARCHAR(100));";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void loadBookingsFromDb() {
        bookings.clear();
        String selectSQL = "SELECT * FROM BOOKINGS";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("ID")); b.setStudentName(rs.getString("STUDENT_NAME"));
                b.setStudentRoll(rs.getString("STUDENT_ROLL")); b.setEventName(rs.getString("EVENT_NAME"));
                b.setResource(rs.getString("RESOURCE")); Date dbDate = rs.getDate("BOOKING_DATE");
                if (dbDate != null) b.setBookingDate(dbDate.toLocalDate());
                b.setBookingTime(rs.getString("BOOKING_TIME")); b.setPhone(rs.getString("PHONE"));
                b.setStatus(rs.getString("STATUS")); bookings.add(b);
            }
            bookings.sort(Comparator.comparingInt(Booking::getId).reversed());
        } catch (SQLException e) { e.printStackTrace(); }
    }
}