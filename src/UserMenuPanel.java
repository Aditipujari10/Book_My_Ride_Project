
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserMenuPanel extends JPanel implements ActionListener {
    private JFrame mainFrame;
    private User loggedUser;
    private JButton viewAvailableCarsButton, bookCarButton, viewMyBookingsButton, cancelMyBookingButton, searchCarButton, logoutButton;

    public UserMenuPanel(JFrame frame, User user) {
        this.mainFrame = frame;
        this.loggedUser = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel title = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 10, 10));

        viewAvailableCarsButton = new JButton("1. View Available Cars");
        bookCarButton = new JButton("2. Book a Car");
        viewMyBookingsButton = new JButton("3. View My Bookings");
        cancelMyBookingButton = new JButton("4. Cancel My Booking");
        searchCarButton = new JButton("5. Search Car by Brand/Model");
        logoutButton = new JButton("6. Logout");

        viewAvailableCarsButton.addActionListener(this);
        bookCarButton.addActionListener(this);
        viewMyBookingsButton.addActionListener(this);
        cancelMyBookingButton.addActionListener(this);
        searchCarButton.addActionListener(this);
        logoutButton.addActionListener(this);
        
        menuPanel.add(viewAvailableCarsButton);
        menuPanel.add(bookCarButton);
        menuPanel.add(viewMyBookingsButton);
        menuPanel.add(cancelMyBookingButton);
        menuPanel.add(searchCarButton);
        menuPanel.add(logoutButton);
        
        add(menuPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewAvailableCarsButton) {
            viewAvailableCars();
        } else if (e.getSource() == searchCarButton) {
            searchCar();
        } else if (e.getSource() == bookCarButton) {
            bookCar();
        } else if (e.getSource() == viewMyBookingsButton) {
            viewMyBookings();
        } else if (e.getSource() == cancelMyBookingButton) {
            cancelMyBooking();
        } else if (e.getSource() == logoutButton) {
            
            BookMyCarAppGUI.showLoginScreen();
            JOptionPane.showMessageDialog(mainFrame, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        }
    }

   
    private void viewAvailableCars() {
        String carDetails = getAvailableCarDetails(BookMyCarAppGUI.cars);
        
        if (carDetails.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "❌ No cars currently available for booking.", "Available Cars", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        displayCarDetailsInDialog(carDetails, "Available Cars");
    }
    
    private void searchCar() {
        String query = JOptionPane.showInputDialog(mainFrame, "Enter Brand or Model to search:", "Search Car", JOptionPane.QUESTION_MESSAGE);
        
        if (query == null || query.trim().isEmpty()) {
            if (query != null) JOptionPane.showMessageDialog(mainFrame, "❌ Search query cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        String carDetails = BookMyCarAppGUI.cars.stream()
            .filter(Car::isAvailable)
            .filter(c -> c.matchesSearch(lowerQuery))
            .map(c -> String.format("ID: %d | %s %s | Price/Day: ₹%.2f", 
                c.getCarId(), c.getBrand(), c.getModel(), c.getPricePerDay()))
            .collect(Collectors.joining("\n"));

        if (carDetails.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "❌ No available cars found matching '" + query + "'.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            displayCarDetailsInDialog("--- Search Results for '" + query + "' ---\n\n" + carDetails, "Search Results");
        }
    }

    private void bookCar() {
        
        int nextBookingId = BookMyCarAppGUI.bookings.stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;

        
        JTextField pickupField = new JTextField(15);
        JTextField dropoffField = new JTextField(15);
        JTextField carIdField = new JTextField(10);
        JTextField daysField = new JTextField(5);
        JCheckBox driverCheckBox = new JCheckBox("Yes (₹500/day)");
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Pickup Location:"));
        panel.add(pickupField);
        panel.add(new JLabel("Drop-off Location:"));
        panel.add(dropoffField);
        panel.add(new JLabel("Car ID:"));
        panel.add(carIdField);
        panel.add(new JLabel("No. of Days:"));
        panel.add(daysField);
        panel.add(new JLabel("Include Driver?"));
        panel.add(driverCheckBox);
        
        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Book a Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        // --- Validation ---
        String pickup = pickupField.getText().trim();
        String dropoff = dropoffField.getText().trim();
        String carIdStr = carIdField.getText().trim();
        String daysStr = daysField.getText().trim();
        
        if (pickup.isEmpty() || dropoff.isEmpty() || carIdStr.isEmpty() || daysStr.isEmpty()) {
             JOptionPane.showMessageDialog(mainFrame, "❌ All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        int carId, days;
        try {
            carId = Integer.parseInt(carIdStr);
            days = Integer.parseInt(daysStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame, "❌ Car ID and Days must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (days <= 0) {
            JOptionPane.showMessageDialog(mainFrame, "❌ Days must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Car car = BookMyCarAppGUI.cars.stream()
            .filter(c -> c.getCarId() == carId && c.isAvailable())
            .findFirst().orElse(null);
        
        if (car == null) {
            JOptionPane.showMessageDialog(mainFrame, "❌ Car not available or ID incorrect!", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Driver driver = null;
        if (driverCheckBox.isSelected()) {
            if (!BookMyCarAppGUI.drivers.isEmpty()) {
                driver = BookMyCarAppGUI.drivers.remove(0);
                JOptionPane.showMessageDialog(mainFrame, "👨‍✈️ Driver Assigned: " + driver.getName(), "Driver Assigned", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "❌ No drivers available! Proceeding without a driver.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        // --- Finalize Booking (In-Memory and DB) ---
        Booking newBooking = new Booking(nextBookingId, loggedUser, car, days, driver, pickup, dropoff);
        BookMyCarAppGUI.bookings.add(newBooking);
        car.setAvailable(false);
        
        // PERSISTENCE
        DatabaseManager.saveNewBooking(newBooking);
        DatabaseManager.updateCarAvailability(car.getCarId(), false);
        
        double carCost = car.getPricePerDay() * days;
        double driverCost = driver != null ? 500.0 * days : 0;
        
        JOptionPane.showMessageDialog(mainFrame, 
            String.format("✅ Booking Successful!\nBooking ID: %d\nTotal Cost: ₹%.2f", nextBookingId, (carCost + driverCost)), 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewMyBookings() {
        String bookingDetails = BookMyCarAppGUI.bookings.stream()
            .filter(b -> b.getUser().equals(loggedUser))
            .map(this::formatBookingDetails)
            .collect(Collectors.joining("\n\n"));

        if (bookingDetails.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No bookings found for you.", "My Bookings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JTextArea textArea = new JTextArea("--- My Bookings for " + loggedUser.getName() + " ---\n\n" + bookingDetails);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        
        JOptionPane.showMessageDialog(mainFrame, scrollPane, "My Bookings", JOptionPane.PLAIN_MESSAGE);
    }

    private void cancelMyBooking() {
        String inputId = JOptionPane.showInputDialog(mainFrame, "Enter YOUR Booking ID to cancel:", "Cancel Booking", JOptionPane.QUESTION_MESSAGE);
        
        if (inputId == null || inputId.trim().isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(inputId.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame, "❌ Invalid Booking ID format! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Booking toRemove = null;
        for (Booking b : BookMyCarAppGUI.bookings) {
            if (b.getBookingId() == id && b.getUser().equals(loggedUser)) {
                toRemove = b;
                break;
            }
        }

        if (toRemove != null) {
            Car bookedCar = toRemove.getCar();

            bookedCar.setAvailable(true);
            if (toRemove.getDriver() != null) BookMyCarAppGUI.drivers.add(toRemove.getDriver());
            BookMyCarAppGUI.bookings.remove(toRemove);
           
            DatabaseManager.updateCarAvailability(bookedCar.getCarId(), true);
            DatabaseManager.deleteBooking(toRemove.getBookingId());
            
            JOptionPane.showMessageDialog(mainFrame, "✅ Your booking ID " + id + " cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "❌ Booking ID " + id + " not found or does not belong to you!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    


    private String getAvailableCarDetails(ArrayList<Car> cars) {
        return cars.stream()
            .filter(Car::isAvailable)
            .map(c -> String.format("ID: %d | %s %s | Price/Day: ₹%.2f", 
                c.getCarId(), c.getBrand(), c.getModel(), c.getPricePerDay()))
            .collect(Collectors.joining("\n"));
    }
    
    private void displayCarDetailsInDialog(String details, String title) {
        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(mainFrame, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }
    
    private String formatBookingDetails(Booking b) {
        // Check for null Car/User to prevent crashes if DB data is corrupted
        if (b.getCar() == null || b.getUser() == null) return "Error: Booking data incomplete.";

        double carCost = b.getCar().getPricePerDay() * b.getDays();
        double driverCost = b.getDriver() != null ? 500.0 * b.getDays() : 0;
        
        return String.format("Booking ID: %d | Car: %s %s\n" +
                             "Days: %d | Route: %s -> %s\n" +
                             "Driver: %s\n" +
                             "Total Cost: ₹%.2f",
            b.getBookingId(), b.getCar().getBrand(), b.getCar().getModel(),
            b.getDays(), b.getPickupLocation(), b.getDropoffLocation(),
            b.getDriver() != null ? b.getDriver().getName() : "None Assigned",
            (carCost + driverCost));
    }
}