
public class Car {
    private int carId;
    private String brand;
    private String model;
    private double pricePerDay;
    private boolean available;

    public Car(int carId, String brand, String model, double pricePerDay, boolean available) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    public int getCarId() { return carId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getPricePerDay() { return pricePerDay; }
    public String getModel() { return model; }
    public String getBrand() { return brand; }
    
    public boolean matchesSearch(String query) {
        String lowerQuery = query.toLowerCase();
        return this.brand.toLowerCase().contains(lowerQuery) || 
               this.model.toLowerCase().contains(lowerQuery);
    }

    public void displayCar() {
        System.out.println("Car ID: " + carId + ", " + brand + " " + model +
                " | Price/Day: ₹" + pricePerDay +
                " | Available: " + (available ? "Yes" : "No"));
    }
}