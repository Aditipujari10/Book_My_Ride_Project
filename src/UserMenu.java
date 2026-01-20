import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern; 

public class UserMenu {
    private static int bookingIdCounter = 1;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[^@]+@[^@]+$"
    );

    public static void userMenu(User user, ArrayList<Car> cars, ArrayList<Booking> bookings, ArrayList<Driver> drivers, Scanner sc) {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View Available Cars");
            System.out.println("2. Book a Car");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel My Booking");
            System.out.println("5. Search Car by Brand/Model"); 
            System.out.println("6. Logout"); 
            System.out.print("Enter choice: ");
            
            int choice = -1;
            try { 
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                sc.nextLine(); 
                continue;
            }
            sc.nextLine();

            switch (choice) {
                case 1: viewAvailableCars(cars); break;
                case 2: bookCar(user, cars, bookings, drivers, sc); break; 
                case 3: viewMyBookings(user, bookings); break;
                case 4: cancelMyBooking(user, bookings, drivers, sc); break; 
                case 5: searchCar(cars, sc); break; 
                case 6: System.out.println("Logging out..."); return;
                default: System.out.println("❌ Invalid choice!");
            }
        }
    }

    public static void registerUser(ArrayList<User> users, int userId) {
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Create Password: "); 
        String password = sc.nextLine();
        
        
        if (name.isEmpty()) {
            System.out.println("❌ Registration failed! Name cannot be empty.");
            return;
        }

        
        if (!isValidEmail(email)) {
            System.out.println("❌ Registration failed! Email must contain exactly one '@' symbol with content before and after it.");
            return;
        }

        User newUser = new User(userId, name, email, password);
        users.add(newUser);
        System.out.println("✅ User Registered Successfully!");
        newUser.displayUser();
    }
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    private static void viewAvailableCars(ArrayList<Car> cars) {
        System.out.println("\n--- Available Cars ---");
        boolean found = false;
        for (Car c : cars) {
            if (c.isAvailable()) {
                c.displayCar();
                found = true;
            }
        }
        if (!found) System.out.println("❌ No cars available!");
    }
    
    private static void searchCar(ArrayList<Car> cars, Scanner sc) {
        System.out.print("\nEnter Brand or Model to search: ");
        String query = sc.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("❌ Search query cannot be empty!");
            return;
        }

        boolean found = false;
        System.out.println("\n--- Search Results for '" + query + "' (Available Cars) ---");
        for (Car c : cars) {
            if (c.isAvailable() && c.matchesSearch(query)) { 
                c.displayCar();
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("❌ No available cars found matching the query.");
        }
    }


    private static void bookCar(User user, ArrayList<Car> cars, ArrayList<Booking> bookings, ArrayList<Driver> drivers, Scanner sc) {
        
        System.out.print("Enter Pickup Location: ");
        String pickupLocation = sc.nextLine().trim();
        System.out.print("Enter Drop-off Location: ");
        String dropoffLocation = sc.nextLine().trim();
        
        if (pickupLocation.isEmpty() || dropoffLocation.isEmpty()) {
             System.out.println("❌ Pickup and Drop-off locations cannot be empty!");
             return;
        }
        
        System.out.print("Enter Car ID: ");
        int carId = -1;
        try { 
            carId = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid Car ID format!");
            sc.nextLine();
            return;
        }
        
        System.out.print("Enter No. of Days: ");
        int days = -1;
        try { 
            days = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid Days format!");
            sc.nextLine();
            return;
        }
        sc.nextLine();

        if (days <= 0) {
            System.out.println("❌ Days must be greater than 0!");
            return;
        }

        Car car = null;
        for (Car c : cars) {
            if (c.getCarId() == carId && c.isAvailable()) {
                if (c.getPricePerDay() <= 0) {
                    System.out.println("❌ Cannot book car with non-positive price/day!");
                    return;
                }
                car = c;
                break;
            }
        }

        if (car == null) {
            System.out.println("❌ Car not available or ID incorrect!");
            return;
        }

        String driverChoice;
        Driver driver = null;
        boolean validInput = false;

        do {
            System.out.print("Do you want a driver? (yes/no): ");
            driverChoice = sc.nextLine().trim();
            
            if (driverChoice.equalsIgnoreCase("yes") || driverChoice.equalsIgnoreCase("no")) {
                validInput = true;
            } else {
                System.out.println("❌ Invalid input. Please enter 'yes' or 'no'.");
            }
        } while (!validInput);
        
        if (driverChoice.equalsIgnoreCase("yes")) {
            if (!drivers.isEmpty()) {
                driver = drivers.remove(0);
                System.out.println("👨‍✈️ Driver Assigned: " + driver.getName() +
                                   " | Phone: " + driver.getPhoneNo());
            } else System.out.println("❌ No drivers available!");
        }

        int bookingId = bookingIdCounter++;
        bookings.add(new Booking(bookingId, user, car, days, driver, pickupLocation, dropoffLocation));
        car.setAvailable(false);

        System.out.println("✅ Booking Successful! Your Booking ID: " + bookingId);
    }

    private static void viewMyBookings(User user, ArrayList<Booking> bookings) {
        boolean found = false;
        System.out.println("\n--- My Bookings for " + user.getName() + " ---");
        for (Booking b : bookings) {
            if (b.getUser().equals(user)) {
                b.displayBooking();
                found = true;
            }
        }
        if (!found) System.out.println("No bookings found for you.");
    }

    private static void cancelMyBooking(User user, ArrayList<Booking> bookings, ArrayList<Driver> drivers, Scanner sc) {
        System.out.print("Enter Booking ID to cancel: ");
        int id = -1;
        try { 
            id = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid Booking ID format!");
            sc.nextLine();
            return;
        }
        sc.nextLine();
        
        Booking toRemove = null;

        for (Booking b : bookings) {
            if (b.getBookingId() == id && b.getUser().equals(user)) {
                toRemove = b;
                break;
            }
        }

        if (toRemove != null) {
            toRemove.getCar().setAvailable(true);
            if (toRemove.getDriver() != null) drivers.add(toRemove.getDriver());
            bookings.remove(toRemove);
            System.out.println("✅ Your booking cancelled successfully!");
        } else System.out.println("❌ Booking not found or not yours!");
    }
}







