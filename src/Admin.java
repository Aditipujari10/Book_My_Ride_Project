import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException; 

public class Admin {
    private String username;
    private String password;
    

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login(String inputUser, String inputPass) {
        return username.equals(inputUser) && password.equals(inputPass);
    }

    public void adminMenu(ArrayList<Car> cars, ArrayList<Booking> bookings, ArrayList<Driver> drivers, Scanner sc) { // <-- ADDED Scanner parameter
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Cars");
            System.out.println("2. View All Bookings");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Logout");
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
                case 1: viewAllCars(cars); break;
                case 2: viewAllBookings(bookings); break;
                case 3: cancelBooking(bookings, drivers, sc); break; // <-- ADDED Scanner parameter
                case 4: System.out.println("Logging out..."); return;
                default: System.out.println("❌ Invalid choice!");
            }
        }
    }

    private void viewAllCars(ArrayList<Car> cars) {
        System.out.println("\n--- All Cars ---");
        for (Car c : cars) {
            c.displayCar();
        }
    }

    private void viewAllBookings(ArrayList<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n--- All Bookings ---");
        for (Booking b : bookings) {
            b.displayBooking();
        }
    }

    private void cancelBooking(ArrayList<Booking> bookings, ArrayList<Driver> drivers, Scanner sc) { // <-- ADDED Scanner parameter
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
            if (b.getBookingId() == id) {
                toRemove = b;
                break;
            }
        }

        if (toRemove != null) {
            toRemove.getCar().setAvailable(true);
            if (toRemove.getDriver() != null) drivers.add(toRemove.getDriver());
            bookings.remove(toRemove);
            System.out.println("✅ Booking cancelled successfully!");
        } else {
            System.out.println("❌ Booking not found!");
        }
    }
}