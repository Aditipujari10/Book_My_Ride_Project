
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AdminMenuPanel extends JPanel implements ActionListener {
    private JFrame mainFrame;
    private JButton viewCarsButton, viewBookingsButton, cancelBookingButton, logoutButton;

    public AdminMenuPanel(JFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1, 10, 10));

        viewCarsButton = new JButton("1. View All Cars");
        viewBookingsButton = new JButton("2. View All Bookings");
        cancelBookingButton = new JButton("3. Cancel Booking");
        logoutButton = new JButton("4. Logout");

        viewCarsButton.addActionListener(this);
        viewBookingsButton.addActionListener(this);
        cancelBookingButton.addActionListener(this);
        logoutButton.addActionListener(this);
        
        menuPanel.add(viewCarsButton);
        menuPanel.add(viewBookingsButton);
        menuPanel.add(cancelBookingButton);
        menuPanel.add(logoutButton);
        
        add(menuPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewCarsButton) {
            viewAllCars();
        } else if (e.getSource() == viewBookingsButton) {
            viewAllBookings();
        } else if (e.getSource() == cancelBookingButton) {
            cancelBooking();
        } else if (e.getSource() == logoutButton) {
            BookMyCarAppGUI.showLoginScreen();
            JOptionPane.showMessageDialog(mainFrame, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewAllCars() {
        if (BookMyCarAppGUI.cars.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No cars found in the system.", "View All Cars", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String carDetails = "--- All Cars in System ---\n\n";
        carDetails += BookMyCarAppGUI.cars.stream()
            .map(c -> String.format("ID: %d | %s %s | Price/Day: ₹%.2f | Available: %s", 
                c.getCarId(), c.getBrand(), c.getModel(), c.getPricePerDay(), c.isAvailable() ? "Yes" : "No"))
            .collect(Collectors.joining("\n"));

        JTextArea textArea = new JTextArea(carDetails);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(mainFrame, scrollPane, "View All Cars", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void viewAllBookings() {
        ArrayList<Booking> bookings = BookMyCarAppGUI.bookings;
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No bookings found in the system.", "View All Bookings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder bookingDetails = new StringBuilder("--- All Bookings ---\n\n");
        for (Booking b : bookings) {
            if (b.getCar() == null || b.getUser() == null) continue; 
            
            double carCost = b.getCar().getPricePerDay() * b.getDays();
            double driverCost = b.getDriver() != null ? 500.0 * b.getDays() : 0;
            
            bookingDetails.append(String.format("ID: %d | User: %s | Car: %s %s\n",
                b.getBookingId(), b.getUser().getName(), b.getCar().getBrand(), b.getCar().getModel()));
            bookingDetails.append(String.format("  Days: %d | Route: %s -> %s | Total Cost: ₹%.2f\n",
                b.getDays(), b.getPickupLocation(), b.getDropoffLocation(), (carCost + driverCost)));
            bookingDetails.append(String.format("  Driver: %s\n\n", b.getDriver() != null ? b.getDriver().getName() : "None Assigned"));
        }

        JTextArea textArea = new JTextArea(bookingDetails.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        
        JOptionPane.showMessageDialog(mainFrame, scrollPane, "View All Bookings", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void cancelBooking() {
        String inputId = JOptionPane.showInputDialog(mainFrame, "Enter Booking ID to cancel:", "Cancel Booking", JOptionPane.QUESTION_MESSAGE);
        
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
            if (b.getBookingId() == id) {
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
            
            JOptionPane.showMessageDialog(mainFrame, "✅ Booking ID " + id + " cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "❌ Booking ID " + id + " not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    







