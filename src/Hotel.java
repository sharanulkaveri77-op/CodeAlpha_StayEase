import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the central management engine of the Hotel Reservation System.
 * 
 * Manages collections of Rooms, Customers, Bookings, and Payments.
 * Encapsulates core business logic for room inventory, availability tracking,
 * customer registration, booking workflow, cancellation, and searching.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Hotel {
    private String hotelName;
    private List<Room> rooms;
    private List<Customer> customers;
    private List<Booking> bookings;
    private List<Payment> payments;
    private int nextCustomerIdSequence = 1001;
    private int nextBookingIdSequence = 1001;

    /**
     * Constructs a Hotel instance with standard default rooms.
     *
     * @param hotelName Name of the hotel establishment
     */
    public Hotel(String hotelName) {
        this.hotelName = hotelName;
        this.rooms = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.payments = new ArrayList<>();
        initializeDefaultRooms();
    }

    /**
     * Populates the standard default inventory across Standard, Deluxe, and Suite categories.
     * Standard: 101-105 @ ₹1500
     * Deluxe:   201-205 @ ₹2500
     * Suite:    301-303 @ ₹4000
     */
    public void initializeDefaultRooms() {
        rooms.clear();
        // Standard Rooms (101 - 105)
        for (int i = 101; i <= 105; i++) {
            rooms.add(new Room(i, "Standard", 1500.00, true));
        }

        // Deluxe Rooms (201 - 205)
        for (int i = 201; i <= 205; i++) {
            rooms.add(new Room(i, "Deluxe", 2500.00, true));
        }

        // Suite Rooms (301 - 303)
        for (int i = 301; i <= 303; i++) {
            rooms.add(new Room(i, "Suite", 4000.00, true));
        }
    }

    // --- Room Management Methods ---

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Room findRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public boolean isRoomAvailable(int roomNumber) {
        Room room = findRoomByNumber(roomNumber);
        return room != null && room.isAvailable();
    }

    public boolean updateRoomAvailability(int roomNumber, boolean available) {
        Room room = findRoomByNumber(roomNumber);
        if (room != null) {
            room.setAvailable(available);
            return true;
        }
        return false;
    }

    public void displayAllRooms() {
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-11s %-14s %-17s %-10s%n", "Room No", "Category", "Price/Night", "Status");
        System.out.println("------------------------------------------------------------");
        if (rooms.isEmpty()) {
            System.out.println("No rooms registered in the system.");
        } else {
            for (Room room : rooms) {
                System.out.println(room.toTableRow());
            }
        }
        System.out.println("------------------------------------------------------------");
    }

    public List<Room> searchAvailableRoomsByCategory(String category) {
        List<Room> availableList = new ArrayList<>();
        boolean matchAll = category == null || category.trim().equalsIgnoreCase("All");

        for (Room room : rooms) {
            if (room.isAvailable()) {
                if (matchAll || room.getCategory().equalsIgnoreCase(category.trim())) {
                    availableList.add(room);
                }
            }
        }
        return availableList;
    }

    public void displayAvailableRooms(String category) {
        List<Room> availableRooms = searchAvailableRoomsByCategory(category);

        System.out.println("------------------------------------------------------------");
        System.out.printf("   AVAILABLE ROOMS [%s]%n", (category == null ? "ALL" : category.toUpperCase()));
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-11s %-14s %-17s %-10s%n", "Room No", "Category", "Price/Night", "Status");
        System.out.println("------------------------------------------------------------");

        if (availableRooms.isEmpty()) {
            System.out.println("[!] No rooms are currently available for the selected category.");
        } else {
            for (Room room : availableRooms) {
                System.out.println(room.toTableRow());
            }
        }
        System.out.println("------------------------------------------------------------");
    }

    // --- Customer Management Methods ---

    public synchronized String generateCustomerId() {
        return "CUS" + (nextCustomerIdSequence++);
    }

    public void updateCustomerIdSequence(int lastIdNumber) {
        if (lastIdNumber >= nextCustomerIdSequence) {
            this.nextCustomerIdSequence = lastIdNumber + 1;
        }
    }

    public Customer registerCustomer(String fullName, String phoneNumber, String email, int numberOfGuests) {
        String customerId = generateCustomerId();
        Customer customer = new Customer(customerId, fullName, phoneNumber, email, numberOfGuests);
        customers.add(customer);
        return customer;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        try {
            if (customer.getCustomerId().startsWith("CUS")) {
                int idNum = Integer.parseInt(customer.getCustomerId().substring(3));
                updateCustomerIdSequence(idNum);
            }
        } catch (NumberFormatException ignored) {}
    }

    public Customer findCustomerById(String customerId) {
        if (customerId == null) return null;
        for (Customer c : customers) {
            if (c.getCustomerId().equalsIgnoreCase(customerId.trim())) {
                return c;
            }
        }
        return null;
    }

    public Customer findCustomerByPhone(String phoneNumber) {
        if (phoneNumber == null) return null;
        for (Customer c : customers) {
            if (c.getPhoneNumber().trim().equals(phoneNumber.trim())) {
                return c;
            }
        }
        return null;
    }

    public List<Booking> getBookingsForCustomer(String customerId) {
        List<Booking> customerBookings = new ArrayList<>();
        if (customerId == null) return customerBookings;

        for (Booking b : bookings) {
            if (b.getCustomer() != null && b.getCustomer().getCustomerId().equalsIgnoreCase(customerId.trim())) {
                customerBookings.add(b);
            }
        }
        return customerBookings;
    }

    public void displayCustomerWithBookings(Customer customer) {
        if (customer == null) {
            System.out.println("[!] Customer not found.");
            return;
        }

        customer.displayCustomerCard();

        List<Booking> guestBookings = getBookingsForCustomer(customer.getCustomerId());
        System.out.println("Associated Bookings (" + guestBookings.size() + "):");
        if (guestBookings.isEmpty()) {
            System.out.println("  No booking records found for this customer.");
        } else {
            System.out.println("  ---------------------------------------------------------------------");
            System.out.printf("  %-9s %-8s %-13s %-13s %-11s %-10s%n",
                    "BookingID", "Room", "Check-in", "Check-out", "Amount", "Status");
            System.out.println("  ---------------------------------------------------------------------");
            for (Booking b : guestBookings) {
                System.out.printf("  %-9s %-8d %-13s %-13s ₹%-10.2f %-10s%n",
                        b.getBookingId(), b.getRoomNumber(), b.getCheckInDate(),
                        b.getCheckOutDate(), b.getTotalAmount(), b.getBookingStatus());
            }
            System.out.println("  ---------------------------------------------------------------------");
        }
    }

    // --- Booking Management & Workflow ---

    public synchronized String generateBookingId() {
        return "BK" + (nextBookingIdSequence++);
    }

    public void updateBookingIdSequence(int lastIdNumber) {
        if (lastIdNumber >= nextBookingIdSequence) {
            this.nextBookingIdSequence = lastIdNumber + 1;
        }
    }

    public void displayBookingSummary(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = room.getPricePerNight() * nights;

        System.out.println("==================================================");
        System.out.println("              BOOKING SUMMARY                     ");
        System.out.println("==================================================");
        System.out.printf("Customer Name     : %s%n", customer.getFullName());
        System.out.printf("Room Number       : %d%n", room.getRoomNumber());
        System.out.printf("Room Category     : %s%n", room.getCategory());
        System.out.printf("Check-in          : %s%n", checkIn);
        System.out.printf("Check-out         : %s%n", checkOut);
        System.out.printf("Number of Nights  : %d%n", nights);
        System.out.printf("Guests            : %d%n", customer.getNumberOfGuests());
        System.out.printf("Price/Night       : ₹%.2f%n", room.getPricePerNight());
        System.out.printf("Total Amount      : ₹%.2f%n", total);
        System.out.println("==================================================");
    }

    public Booking createBooking(Customer customer, int roomNumber, LocalDate checkInDate,
                                 LocalDate checkOutDate, String paymentStatus, String transactionId) {
        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            System.out.println("[!] Room number does not exist.");
            return null;
        }
        if (!room.isAvailable()) {
            System.out.println("[!] Room " + roomNumber + " is already booked.");
            return null;
        }

        String bookingId = generateBookingId();
        Booking booking = new Booking(
                bookingId,
                customer,
                room.getRoomNumber(),
                room.getCategory(),
                checkInDate,
                checkOutDate,
                room.getPricePerNight(),
                paymentStatus,
                transactionId
        );

        room.setAvailable(false);
        bookings.add(booking);
        return booking;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        try {
            if (booking.getBookingId().startsWith("BK")) {
                int idNum = Integer.parseInt(booking.getBookingId().substring(2));
                updateBookingIdSequence(idNum);
            }
        } catch (NumberFormatException ignored) {}
    }

    public Booking findBookingById(String bookingId) {
        if (bookingId == null) return null;
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> searchBookingsByCustomerName(String name) {
        List<Booking> results = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) return results;
        String query = name.trim().toLowerCase();

        for (Booking b : bookings) {
            if (b.getCustomer() != null && b.getCustomer().getFullName().toLowerCase().contains(query)) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Booking> searchBookingsByCustomerPhone(String phone) {
        List<Booking> results = new ArrayList<>();
        if (phone == null || phone.trim().isEmpty()) return results;
        String query = phone.trim();

        for (Booking b : bookings) {
            if (b.getCustomer() != null && b.getCustomer().getPhoneNumber().trim().equals(query)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Cancels an existing reservation by Booking ID.
     * Updates booking status to CANCELLED and marks the room as Available again.
     *
     * @param bookingId The Booking ID to cancel (e.g., BK1001)
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[!] Booking not found.");
            return false;
        }

        if (booking.getBookingStatus().equalsIgnoreCase("CANCELLED")) {
            System.out.println("[!] This booking has already been cancelled.");
            return false;
        }

        // Update booking status
        booking.setBookingStatus("CANCELLED");

        // Release the room
        Room room = findRoomByNumber(booking.getRoomNumber());
        if (room != null) {
            room.setAvailable(true);
        }

        // Display confirmation
        System.out.println("==================================================");
        System.out.println("             BOOKING CANCELLED                    ");
        System.out.println("==================================================");
        System.out.println("Booking ID : " + booking.getBookingId());
        System.out.println("Room       : " + booking.getRoomNumber());
        System.out.println("Customer   : " + (booking.getCustomer() != null ? booking.getCustomer().getFullName() : "N/A"));
        System.out.println();
        System.out.println("[✓] Booking cancelled successfully.");
        System.out.println("==================================================");
        return true;
    }

    public void displayBookingConfirmation(Booking booking, String paymentMethod) {
        if (booking == null) return;
        System.out.println("==================================================");
        System.out.println("              BOOKING CONFIRMED                   ");
        System.out.println("==================================================");
        System.out.printf("Booking ID        : %s%n", booking.getBookingId());
        System.out.printf("Customer ID       : %s%n", booking.getCustomer().getCustomerId());
        System.out.printf("Customer Name     : %s%n", booking.getCustomer().getFullName());
        System.out.printf("Phone             : %s%n", booking.getCustomer().getPhoneNumber());
        System.out.printf("Room Number       : %d%n", booking.getRoomNumber());
        System.out.printf("Room Category     : %s%n", booking.getRoomCategory());
        System.out.printf("Check-in          : %s%n", booking.getCheckInDate());
        System.out.printf("Check-out         : %s%n", booking.getCheckOutDate());
        System.out.printf("Number of Nights  : %d%n", booking.getNumberOfNights());
        System.out.printf("Guests            : %d%n", booking.getNumberOfGuests());
        System.out.printf("Price/Night       : ₹%.2f%n", booking.getPricePerNight());
        System.out.printf("Total Amount      : ₹%.2f%n", booking.getTotalAmount());
        System.out.println();
        System.out.printf("Payment Method    : %s%n", paymentMethod);
        System.out.printf("Transaction ID    : %s%n", booking.getTransactionId());
        System.out.printf("Payment Status    : %s%n", booking.getPaymentStatus());
        System.out.printf("Booking Status    : %s%n", booking.getBookingStatus());
        System.out.println("==================================================");
        System.out.println("[✓] Booking created successfully!");
    }

    public void displayAllBookings() {
        displayBookingsList(bookings, "ALL RESERVATIONS");
    }

    public void displayBookingsList(List<Booking> list, String title) {
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("   %s (%d)%n", title, list.size());
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("%-9s %-16s %-9s %-13s %-12s %-10s%n",
                "ID", "Customer", "Room", "Check-in", "Amount", "Status");
        System.out.println("-----------------------------------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("No matching reservations found.");
        } else {
            for (Booking b : list) {
                System.out.println(b.toTableRow());
            }
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    // --- Payment Management ---

    public void addPayment(Payment payment) {
        payments.add(payment);
        try {
            if (payment.getTransactionId().startsWith("TXN")) {
                int idNum = Integer.parseInt(payment.getTransactionId().substring(3));
                Payment.updateTransactionSequence(idNum);
            }
        } catch (NumberFormatException ignored) {}
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public Payment findPaymentByTransactionId(String transactionId) {
        if (transactionId == null) return null;
        for (Payment p : payments) {
            if (p.getTransactionId().equalsIgnoreCase(transactionId.trim())) {
                return p;
            }
        }
        return null;
    }

    // --- General Accessors ---

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
