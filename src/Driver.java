
public class Driver{
    private int driverId;
    private String name;
    private String licenseNo;
    private String phoneNo;

    // Constructor
    public Driver(int driverId, String name, String licenseNo, String phoneNo) {
        this.driverId = driverId;
        this.name = name;
        this.licenseNo = licenseNo;
        this.phoneNo = phoneNo;
    }

    // Getters
    public int getDriverId() { return driverId; }
    public String getName() { return name; }
    public String getLicenseNo() { return licenseNo; }
    public String getPhoneNo() { return phoneNo; }

    // Display Driver Details
    public void displayDriver() {
        System.out.println("Driver ID: " + driverId +
                " | Name: " + name +
                " | License No: " + licenseNo +
                " | Phone No: " + phoneNo);
    }
}