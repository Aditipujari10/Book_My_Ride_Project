
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class BookMyCarAppGUI {
    
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Driver> drivers = new ArrayList<>();
    public static ArrayList<Booking> bookings = new ArrayList<>();
    
    public static int userIdCounter = 1;
    public static int driverIdCounter = 1;
    
    public static final Admin ADMIN = new Admin("admin", "admin123");

    private static JFrame mainFrame;

    public static void main(String[] args) {
        
        loadInitialDataFromDB(); 

        
        if (cars.isEmpty()) {
            System.out.println("⚠️ Data lists are empty. Seeding default data to memory.");
            
            cars.add(new Car(101, "Tata", "Nexon", 1500, true));
            cars.add(new Car(102, "Maruti", "Swift", 1200, true));
            cars.add(new Car(103, "Hyundai", "Creta", 2000, true));
            
            drivers.add(new Driver(driverIdCounter++,"Ramesh", "MH12AB1234", "9876543210"));
            drivers.add(new Driver(driverIdCounter++,"Suresh", "MH14XY5678", "9123456789"));
        
            users.add(new User(userIdCounter++, "TestUser", "test@mail.com", "pass"));
        }
        updateCounters();

        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("BookMyCar Application (Database Linked)");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setMinimumSize(new Dimension(500, 450)); 
            mainFrame.setLocationRelativeTo(null); 
            

            showLoginScreen();
            mainFrame.setVisible(true);
        });
    }

    public static void loadInitialDataFromDB() {

        users = DatabaseManager.loadAllUsers();
        cars = DatabaseManager.loadAllCars();
        drivers = DatabaseManager.loadAllDrivers();
        bookings = DatabaseManager.loadAllBookings();
        System.out.println("✅ Data load process complete.");
    }
    
 
    public static User findUserById(int id) {
        return users.stream().filter(u -> u.getUserId() == id).findFirst().orElse(null);
    }
    public static Car findCarById(int id) {
        return cars.stream().filter(c -> c.getCarId() == id).findFirst().orElse(null);
    }
    public static Driver findDriverById(int id) {
        return drivers.stream().filter(d -> d.getDriverId() == id).findFirst().orElse(null);
    }
    
    public static void registerUser(String name, String email, String password) {
        User newUser = new User(userIdCounter++, name, email, password);
        users.add(newUser);
        DatabaseManager.saveNewUser(newUser); 
    }


    public static void showLoginScreen() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new LoginScreen(mainFrame));
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setTitle("BookMyCar Application - Login");
    }

    public static void showAdminMenu() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AdminMenuPanel(mainFrame)); 
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setTitle("BookMyCar Application - Admin Dashboard");
    }

    public static void showUserMenu(User user) {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new UserMenuPanel(mainFrame, user)); 
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setTitle("BookMyCar Application - User Dashboard");
    }


 private static int bookingIdCounter = 1; 

 private static void updateCounters() {
     if (!users.isEmpty()) {
         userIdCounter = users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;
     }
     if (!drivers.isEmpty()) {
         driverIdCounter = drivers.stream().mapToInt(Driver::getDriverId).max().orElse(0) + 1;
     }

     if (!bookings.isEmpty()) {
         bookingIdCounter = bookings.stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;
     } else {
         bookingIdCounter = 1;
     }
 }

 public static int getNextBookingId() {
     return bookingIdCounter++;
 }

    public static User findUser(String name) {
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name)) return u;
        }
        return null;
    }
}