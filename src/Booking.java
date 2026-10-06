import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a room reservation in the Hotel Reservation System.
 * 
 * Demonstrates OOP Composition by holding a Customer reference,
 * and maintains financial calculations, reservation lifecycle,
 * and payment audit status.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Booking {
    private String bookingId;
    private Customer customer;
    private int roomNumber;
    private String roomCategory;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private int numberOfGuests;
    private double pricePerNight;
    private double totalAmount;
    private String bookingStatus;  // CONFIRMED, CANCELLED
    private String paymentStatus;  // SUCCESS, FAILED, PENDING
    private String transactionId;

    /**
     * Parameterized constructor for creating a new booking.
     * Automatically calculates numberOfNights and totalAmount.
     */
    public Booking(String bookingId, Customer customer, int roomNumber, String roomCategory,
                   LocalDate checkInDate, LocalDate checkOutDate, double pricePerNight,
                   String paymentStatus, String transactionId) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.roomNumber = roomNumber;
        this.roomCategory = roomCategory;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.numberOfGuests = customer.getNumberOfGuests();
        this.pricePerNight = pricePerNight;
        this.totalAmount = pricePerNight * this.numberOfNights;
        this.bookingStatus = "CONFIRMED";
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    /**
     * Full constructor for restoring booking records from file storage.
     */
    public Booking(String bookingId, Customer customer, int roomNumber, String roomCategory,
                   LocalDate checkInDate, LocalDate checkOutDate, long numberOfNights,
                   int numberOfGuests, double pricePerNight, double totalAmount,
                   String bookingStatus, String paymentStatus, String transactionId) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.roomNumber = roomNumber;
        this.roomCategory = roomCategory;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.numberOfGuests = numberOfGuests;
        this.pricePerNight = pricePerNight;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    // --- Getters and Setters ---

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
        recalculateTotals();
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        recalculateTotals();
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
        recalculateTotals();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Recalculates total duration and payable price when dates or rate are updated.
     */
    private void recalculateTotals() {
        if (checkInDate != null && checkOutDate != null) {
            this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            this.totalAmount = this.pricePerNight * this.numberOfNights;
        }
    }

    /**
     * Prints complete booking information card.
     */
    public void displayBookingDetails() {
        System.out.println("==================================================");
        System.out.println("              BOOKING DETAILS                     ");
        System.out.println("==================================================");
        System.out.printf("Booking ID        : %s%n", bookingId);
        System.out.printf("Customer ID       : %s%n", customer.getCustomerId());
        System.out.printf("Customer Name     : %s%n", customer.getFullName());
        System.out.printf("Phone Number      : %s%n", customer.getPhoneNumber());
        System.out.printf("Email Address     : %s%n", customer.getEmail());
        System.out.printf("Room Number       : %d%n", roomNumber);
        System.out.printf("Room Category     : %s%n", roomCategory);
        System.out.printf("Check-in Date     : %s%n", checkInDate);
        System.out.printf("Check-out Date    : %s%n", checkOutDate);
        System.out.printf("Number of Nights  : %d%n", numberOfNights);
        System.out.printf("Number of Guests  : %d%n", numberOfGuests);
        System.out.printf("Price Per Night   : ₹%.2f%n", pricePerNight);
        System.out.printf("Total Amount      : ₹%.2f%n", totalAmount);
        System.out.printf("Booking Status    : %s%n", bookingStatus);
        System.out.printf("Payment Status    : %s%n", paymentStatus);
        System.out.printf("Transaction ID    : %s%n", transactionId);
        System.out.println("==================================================");
    }

    /**
     * Formats booking as a clean table row for tabular summary lists.
     */
    public String toTableRow() {
        return String.format("%-9s %-16s %-9d %-13s ₹%-11.2f %-10s",
                bookingId, customer.getFullName(), roomNumber, checkInDate, totalAmount, bookingStatus);
    }

    @Override
    public String toString() {
        return "Booking [ID=" + bookingId +
                ", Customer=" + customer.getFullName() +
                ", Room=" + roomNumber +
                ", Dates=" + checkInDate + " to " + checkOutDate +
                ", Nights=" + numberOfNights +
                ", Total=₹" + String.format("%.2f", totalAmount) +
                ", Status=" + bookingStatus + "]";
    }
}
