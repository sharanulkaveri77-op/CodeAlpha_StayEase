/**
 * Represents a customer in the Hotel Reservation System.
 * 
 * Encapsulates customer identity, contact details, and guest count.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Customer {
    private String customerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private int numberOfGuests;

    /**
     * Parameterized constructor initializing customer details.
     *
     * @param customerId     Unique identifier (e.g., CUS1001)
     * @param fullName       Full name of the customer
     * @param phoneNumber    Contact phone number
     * @param email          Email address
     * @param numberOfGuests Number of staying guests (must be >= 1)
     */
    public Customer(String customerId, String fullName, String phoneNumber, String email, int numberOfGuests) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.numberOfGuests = numberOfGuests;
    }

    // --- Getters and Setters ---

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    /**
     * Formats customer information for detailed console view.
     */
    public void displayCustomerCard() {
        System.out.println("--------------------------------------------------");
        System.out.println("               CUSTOMER PROFILE                   ");
        System.out.println("--------------------------------------------------");
        System.out.printf("Customer ID      : %s%n", customerId);
        System.out.printf("Full Name        : %s%n", fullName);
        System.out.printf("Phone Number     : %s%n", phoneNumber);
        System.out.printf("Email Address    : %s%n", email);
        System.out.printf("Number of Guests : %d%n", numberOfGuests);
        System.out.println("--------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Customer [ID=" + customerId +
                ", Name=" + fullName +
                ", Phone=" + phoneNumber +
                ", Email=" + email +
                ", Guests=" + numberOfGuests + "]";
    }
}
