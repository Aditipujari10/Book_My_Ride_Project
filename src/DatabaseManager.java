import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {
   
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/car_rental_db?serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = "manager";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Ensure the Connector/J JAR is in the classpath.");
            throw new SQLException(e);
        }
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    
    public static ArrayList<User> loadAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email, password FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    public static ArrayList<Car> loadAllCars() {
        ArrayList<Car> cars = new ArrayList<>();
        String sql = "SELECT car_id, brand, model, price_per_day, is_available FROM cars";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(new Car(
                    rs.getInt("car_id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getDouble("price_per_day"),
                    rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading cars: " + e.getMessage());
        }
        return cars;
    }
    
    public static ArrayList<Driver> loadAllDrivers() {
        ArrayList<Driver> drivers = new ArrayList<>();
        String sql = "SELECT driver_id, name, license_no, phone_no FROM drivers ORDER BY driver_id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getInt("driver_id"),
                    rs.getString("name"),
                    rs.getString("license_no"),
                    rs.getString("phone_no")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading drivers: " + e.getMessage());
        }
        return drivers;
    }
    
    public static ArrayList<Booking> loadAllBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_id, user_id, car_id, driver_id, days, pickup_location, dropoff_location FROM bookings";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Find associated objects from in-memory lists (loaded by AppGUI)
                User user = BookMyCarAppGUI.findUserById(rs.getInt("user_id"));
                Car car = BookMyCarAppGUI.findCarById(rs.getInt("car_id"));
                Driver driver = rs.getInt("driver_id") > 0 ? BookMyCarAppGUI.findDriverById(rs.getInt("driver_id")) : null;

                if (user != null && car != null) {
                    bookings.add(new Booking(
                        rs.getInt("booking_id"), user, car, 
                        rs.getInt("days"), driver, 
                        rs.getString("pickup_location"), 
                        rs.getString("dropoff_location")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    public static void saveNewUser(User user) {
        // Assuming user_id is NOT auto-incremented and must be provided
        String sql = "INSERT INTO users (user_id, name, email, password) VALUES (?, ?, ?, ?)"; 
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Validation moved to GUI, but keeping check for safety
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                System.err.println("❌ Email cannot be empty!");
                return;
            }

            // --- FIX: Add user ID to prepared statement ---
            pstmt.setInt(1, user.getUserId()); 
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.executeUpdate();
            
            System.out.println("✅ User saved successfully!");
        } catch (SQLException e) {
            System.err.println("Error saving new user: " + e.getMessage());
        }
    }
    
    public static void saveNewBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, user_id, car_id, driver_id, days, pickup_location, dropoff_location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, booking.getBookingId());
            pstmt.setInt(2, booking.getUser().getUserId());
            pstmt.setInt(3, booking.getCar().getCarId());
            
            if (booking.getDriver() != null) {
                pstmt.setInt(4, booking.getDriver().getDriverId());
            } else {
                pstmt.setNull(4, Types.INTEGER); // Set null if no driver
            }
            pstmt.setInt(5, booking.getDays());
            pstmt.setString(6, booking.getPickupLocation());
            pstmt.setString(7, booking.getDropoffLocation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving new booking: " + e.getMessage());
        }
    }

    public static void updateCarAvailability(int carId, boolean isAvailable) {
        String sql = "UPDATE cars SET is_available = ? WHERE car_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, carId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating car status: " + e.getMessage());
        }
    }

    public static void deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
        }
    }
}






