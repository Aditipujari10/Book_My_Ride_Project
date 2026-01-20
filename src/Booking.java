//import java.io.Serializable;

public class Booking {
    private int bookingId;
    private User user;
    private Car car;
    private int days;
    private Driver driver; 
    private String pickupLocation; 
    private String dropoffLocation; 
    
    private static final double DRIVER_COST_PER_DAY = 500.0;

    public Booking(int bookingId, User user, Car car, int days, Driver driver, String pickupLocation, String dropoffLocation) {
        this.bookingId = bookingId;
        this.user = user;
        this.car = car;
        this.days = days;
        this.driver = driver;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }

    public int getBookingId() { return bookingId; }
    public User getUser() { return user; }
    public Car getCar() { return car; }
    public int getDays() { return days; }
    public Driver getDriver() { return driver; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }

    public void displayBooking() {
        System.out.println("\n--- Booking Details ---");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("User: " + (user != null ? user.getName() : "N/A"));
        System.out.println("Car: " + (car != null ? (car.getBrand() + " " + car.getModel()) : "N/A"));
        System.out.println("Days: " + days);
        
        System.out.println("Route: " + pickupLocation + " -> " + dropoffLocation);

        double carCost = (car != null ? car.getPricePerDay() * days : 0);
        double driverCost = (driver != null ? DRIVER_COST_PER_DAY * days : 0); 
        System.out.println("Total Cost: ₹" + (carCost + driverCost));

        if (driver != null) {
            System.out.println("Driver: " + driver.getName()
                + " | Phone: " + driver.getPhoneNo()
                + " | License: " + driver.getLicenseNo());
        } else {
            System.out.println("Driver: No driver assigned");
        }
    }
}









