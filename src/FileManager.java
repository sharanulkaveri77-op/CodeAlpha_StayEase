import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent file I/O operations for the Hotel Reservation System.
 * 
 * Manages storage and retrieval of rooms, customers, and bookings inside
 * the data/ directory, ensuring data survives across program restarts.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String ROOMS_FILE = "data/rooms.txt";
    private static final String CUSTOMERS_FILE = "data/customers.txt";
    private static final String BOOKINGS_FILE = "data/bookings.txt";

    /**
     * Initializes the data directory and required storage files if they do not exist.
     */
    public static void initializeStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        createFileIfNotExists(new File(ROOMS_FILE));
        createFileIfNotExists(new File(CUSTOMERS_FILE));
        createFileIfNotExists(new File(BOOKINGS_FILE));
    }

    private static void createFileIfNotExists(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[!] Warning: Could not create persistent file: " + file.getPath());
        }
    }

    // --- Rooms Persistence ---

    /**
     * Saves all rooms to data/rooms.txt.
     */
    public static void saveRooms(List<Room> rooms) {
        initializeStorage();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                // Format: roomNumber,category,pricePerNight,available
                writer.write(r.getRoomNumber() + "," +
                        r.getCategory() + "," +
                        r.getPricePerNight() + "," +
                        r.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save rooms: " + e.getMessage());
        }
    }

    /**
     * Loads rooms from data/rooms.txt.
     */
    public static List<Room> loadRooms() {
        initializeStorage();
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);
        if (!file.exists() || file.length() == 0) {
            return rooms;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    int roomNum = Integer.parseInt(parts[0].trim());
                    String category = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    boolean available = Boolean.parseBoolean(parts[3].trim());
                    rooms.add(new Room(roomNum, category, price, available));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error reading rooms file: " + e.getMessage());
        }
        return rooms;
    }

    // --- Customers Persistence ---

    /**
     * Saves all customers to data/customers.txt.
     */
    public static void saveCustomers(List<Customer> customers) {
        initializeStorage();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) {
                // Format: customerId,fullName,phoneNumber,email,numberOfGuests
                writer.write(c.getCustomerId() + "," +
                        c.getFullName() + "," +
                        c.getPhoneNumber() + "," +
                        c.getEmail() + "," +
                        c.getNumberOfGuests());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save customers: " + e.getMessage());
        }
    }

    /**
     * Loads all customers from data/customers.txt.
     */
    public static List<Customer> loadCustomers() {
        initializeStorage();
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists() || file.length() == 0) {
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    String email = parts[3].trim();
                    int guests = Integer.parseInt(parts[4].trim());
                    customers.add(new Customer(id, name, phone, email, guests));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error reading customers file: " + e.getMessage());
        }
        return customers;
    }

    // --- Bookings Persistence ---

    /**
     * Saves all bookings to data/bookings.txt.
     */
    public static void saveBookings(List<Booking> bookings) {
        initializeStorage();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) {
                String custId = (b.getCustomer() != null) ? b.getCustomer().getCustomerId() : "UNKNOWN";
                // Format: bookingId,customerId,roomNumber,roomCategory,checkInDate,checkOutDate,numberOfNights,numberOfGuests,pricePerNight,totalAmount,bookingStatus,paymentStatus,transactionId
                writer.write(b.getBookingId() + "," +
                        custId + "," +
                        b.getRoomNumber() + "," +
                        b.getRoomCategory() + "," +
                        b.getCheckInDate() + "," +
                        b.getCheckOutDate() + "," +
                        b.getNumberOfNights() + "," +
                        b.getNumberOfGuests() + "," +
                        b.getPricePerNight() + "," +
                        b.getTotalAmount() + "," +
                        b.getBookingStatus() + "," +
                        b.getPaymentStatus() + "," +
                        b.getTransactionId());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save bookings: " + e.getMessage());
        }
    }

    /**
     * Loads all bookings from data/bookings.txt and resolves Customer references.
     */
    public static List<Booking> loadBookings(List<Customer> customers) {
        initializeStorage();
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists() || file.length() == 0) {
            return bookings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 13) {
                    String bookingId = p[0].trim();
                    String customerId = p[1].trim();
                    int roomNumber = Integer.parseInt(p[2].trim());
                    String roomCategory = p[3].trim();
                    LocalDate checkIn = LocalDate.parse(p[4].trim());
                    LocalDate checkOut = LocalDate.parse(p[5].trim());
                    long nights = Long.parseLong(p[6].trim());
                    int guests = Integer.parseInt(p[7].trim());
                    double price = Double.parseDouble(p[8].trim());
                    double total = Double.parseDouble(p[9].trim());
                    String bookingStatus = p[10].trim();
                    String paymentStatus = p[11].trim();
                    String txnId = p[12].trim();

                    // Resolve Customer
                    Customer linkedCustomer = null;
                    for (Customer c : customers) {
                        if (c.getCustomerId().equalsIgnoreCase(customerId)) {
                            linkedCustomer = c;
                            break;
                        }
                    }
                    if (linkedCustomer == null) {
                        linkedCustomer = new Customer(customerId, "Unknown Guest", "N/A", "N/A", guests);
                    }

                    Booking booking = new Booking(
                            bookingId, linkedCustomer, roomNumber, roomCategory,
                            checkIn, checkOut, nights, guests, price, total,
                            bookingStatus, paymentStatus, txnId
                    );
                    bookings.add(booking);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error reading bookings file: " + e.getMessage());
        }
        return bookings;
    }

    // --- Master Load and Save Orchestrator ---

    /**
     * Loads all application data into the Hotel model.
     * If room files do not exist or are empty, writes default hotel inventory.
     */
    public static void loadAll(Hotel hotel) {
        initializeStorage();

        // 1. Load Rooms
        List<Room> loadedRooms = loadRooms();
        if (loadedRooms.isEmpty()) {
            hotel.initializeDefaultRooms();
            saveRooms(hotel.getRooms());
        } else {
            hotel.setRooms(loadedRooms);
        }

        // 2. Load Customers
        List<Customer> loadedCustomers = loadCustomers();
        hotel.setCustomers(loadedCustomers);
        for (Customer c : loadedCustomers) {
            try {
                if (c.getCustomerId().startsWith("CUS")) {
                    int idNum = Integer.parseInt(c.getCustomerId().substring(3));
                    hotel.updateCustomerIdSequence(idNum);
                }
            } catch (NumberFormatException ignored) {}
        }

        // 3. Load Bookings
        List<Booking> loadedBookings = loadBookings(loadedCustomers);
        hotel.setBookings(loadedBookings);
        for (Booking b : loadedBookings) {
            try {
                if (b.getBookingId().startsWith("BK")) {
                    int idNum = Integer.parseInt(b.getBookingId().substring(2));
                    hotel.updateBookingIdSequence(idNum);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Saves all hotel data (rooms, customers, bookings) to disk.
     */
    public static void saveAll(Hotel hotel) {
        saveRooms(hotel.getRooms());
        saveCustomers(hotel.getCustomers());
        saveBookings(hotel.getBookings());
    }
}
