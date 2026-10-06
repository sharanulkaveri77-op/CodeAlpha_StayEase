import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the Hotel Reservation System.
 * 
 * Provides an interactive console menu, coordinates user input,
 * invokes business logic inside Hotel, and manages file persistence.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Main {
    private static Hotel hotel;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Configure standard output and error streams to UTF-8 for proper currency symbol rendering
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception ignored) {}

        hotel = new Hotel("Grand CodeAlpha Hotel");
        scanner = new Scanner(System.in);

        // Load existing persistent data on application startup
        System.out.println("Initializing Hotel Reservation System...");
        FileManager.loadAll(hotel);
        System.out.println("[✓] Data loaded successfully from persistent storage.\n");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = InputValidator.readMenuChoice(scanner, 1, 10);
            System.out.println();

            switch (choice) {
                case 1:
                    handleViewAllRooms();
                    break;
                case 2:
                    handleSearchAvailableRooms();
                    break;
                case 3:
                    handleBookRoom();
                    break;
                case 4:
                    handleViewBookingDetails();
                    break;
                case 5:
                    handleCancelBooking();
                    break;
                case 6:
                    handleViewAllBookings();
                    break;
                case 7:
                    handleSearchBookings();
                    break;
                case 8:
                    handleViewCustomerDetails();
                    break;
                case 9:
                    handleViewPaymentDetails();
                    break;
                case 10:
                    handleExit();
                    running = false;
                    break;
                default:
                    System.out.println("[!] Invalid menu selection.");
            }
            if (running) {
                pause();
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("==================================================");
        System.out.println("              HOTEL RESERVATION SYSTEM            ");
        System.out.println("==================================================");
        System.out.println("1. View All Rooms");
        System.out.println("2. Search Available Rooms");
        System.out.println("3. Book a Room");
        System.out.println("4. View Booking Details");
        System.out.println("5. Cancel Booking");
        System.out.println("6. View All Bookings");
        System.out.println("7. Search Booking");
        System.out.println("8. View Customer Details");
        System.out.println("9. View Payment Details");
        System.out.println("10. Exit");
        System.out.println("==================================================");
    }

    // --- Option 1: View All Rooms ---
    private static void handleViewAllRooms() {
        System.out.println("==================================================");
        System.out.println("               ALL HOTEL ROOMS                    ");
        System.out.println("==================================================");
        hotel.displayAllRooms();
    }

    // --- Option 2: Search Available Rooms ---
    private static void handleSearchAvailableRooms() {
        System.out.println("==================================================");
        System.out.println("           SEARCH AVAILABLE ROOMS                 ");
        System.out.println("==================================================");
        System.out.println("Select room category:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");
        System.out.println("4. All");
        System.out.println("--------------------------------------------------");

        int choice = InputValidator.readMenuChoice(scanner, 1, 4);
        String category = "All";
        if (choice == 1) category = "Standard";
        else if (choice == 2) category = "Deluxe";
        else if (choice == 3) category = "Suite";

        hotel.displayAvailableRooms(category);
    }

    // --- Option 3: Book a Room ---
    private static void handleBookRoom() {
        System.out.println("==================================================");
        System.out.println("                 BOOK A ROOM                      ");
        System.out.println("==================================================");

        // STEP 1: Enter customer information
        System.out.println("STEP 1: Enter Customer Details");
        String phone = InputValidator.readValidPhoneNumber(scanner);
        Customer customer = hotel.findCustomerByPhone(phone);

        if (customer != null) {
            System.out.println("[✓] Returning customer recognized: " + customer.getFullName());
            System.out.print("Update number of guests for this stay? (Current: " + customer.getNumberOfGuests() + ")");
            int guests = InputValidator.readPositiveInt(scanner, "Enter Number of Guests: ", "Number of guests");
            customer.setNumberOfGuests(guests);
        } else {
            String name = InputValidator.readNonEmptyString(scanner, "Enter Customer Full Name: ", "Name");
            String email = InputValidator.readValidEmail(scanner);
            int guests = InputValidator.readPositiveInt(scanner, "Enter Number of Guests: ", "Number of guests");
            customer = hotel.registerCustomer(name, phone, email, guests);
            System.out.println("[✓] New customer registered with ID: " + customer.getCustomerId());
        }

        // STEP 2: Display available rooms
        System.out.println("\nSTEP 2: Current Available Rooms");
        List<Room> availableRooms = hotel.searchAvailableRoomsByCategory("All");
        if (availableRooms.isEmpty()) {
            System.out.println("[!] Sorry, there are currently no available rooms in the hotel.");
            return;
        }
        hotel.displayAvailableRooms("All");

        // STEP 3: Select a room
        Room selectedRoom = null;
        while (selectedRoom == null) {
            System.out.print("Enter Room Number to book (e.g., 101, 201, 301): ");
            String roomInput = scanner.nextLine().trim();
            int roomNum;
            try {
                roomNum = Integer.parseInt(roomInput);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid room number.");
                continue;
            }

            Room r = hotel.findRoomByNumber(roomNum);
            if (r == null) {
                System.out.println("[!] Room number does not exist.");
                continue;
            }
            if (!r.isAvailable()) {
                System.out.println("[!] Room " + roomNum + " is already booked. Please choose an available room.");
                continue;
            }
            selectedRoom = r;
        }

        // STEP 4 & 5: Ask check-in and check-out dates
        System.out.println("\nSTEP 3: Reservation Schedule");
        LocalDate checkIn = InputValidator.readValidCheckInDate(scanner);
        LocalDate checkOut = InputValidator.readValidCheckOutDate(scanner, checkIn);

        // STEP 6 & 7 & 8: Calculate and display booking summary
        System.out.println();
        hotel.displayBookingSummary(customer, selectedRoom, checkIn, checkOut);

        // Confirmation prompt
        System.out.println("Do you want to confirm this booking?");
        System.out.println("1. Confirm Booking");
        System.out.println("2. Cancel");
        int confirmChoice = InputValidator.readMenuChoice(scanner, 1, 2);

        if (confirmChoice == 2) {
            System.out.println("\n[!] Booking cancelled by user. Returning to main menu.");
            return;
        }

        // Process Payment simulation
        long nights = InputValidator.calculateNights(checkIn, checkOut);
        double total = selectedRoom.getPricePerNight() * nights;

        System.out.println();
        Payment payment = Payment.processPayment(total, scanner);
        hotel.addPayment(payment);

        // Create booking and update room availability
        Booking booking = hotel.createBooking(
                customer,
                selectedRoom.getRoomNumber(),
                checkIn,
                checkOut,
                payment.getPaymentStatus(),
                payment.getTransactionId()
        );

        // Persist data immediately
        FileManager.saveAll(hotel);

        // Display final confirmation
        System.out.println();
        hotel.displayBookingConfirmation(booking, payment.getPaymentMethod());
    }

    // --- Option 4: View Booking Details ---
    private static void handleViewBookingDetails() {
        System.out.println("==================================================");
        System.out.println("              VIEW BOOKING DETAILS                ");
        System.out.println("==================================================");
        System.out.print("Enter Booking ID (e.g., BK1001): ");
        String bookingId = scanner.nextLine().trim();

        Booking booking = hotel.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("[!] Booking not found.");
        } else {
            booking.displayBookingDetails();
        }
    }

    // --- Option 5: Cancel Booking ---
    private static void handleCancelBooking() {
        System.out.println("==================================================");
        System.out.println("                 CANCEL BOOKING                   ");
        System.out.println("==================================================");
        System.out.print("Enter Booking ID to cancel (e.g., BK1001): ");
        String bookingId = scanner.nextLine().trim();

        boolean cancelled = hotel.cancelBooking(bookingId);
        if (cancelled) {
            // Persist changes
            FileManager.saveAll(hotel);
        }
    }

    // --- Option 6: View All Bookings ---
    private static void handleViewAllBookings() {
        System.out.println("==================================================");
        System.out.println("                ALL RESERVATIONS                  ");
        System.out.println("==================================================");
        hotel.displayAllBookings();
    }

    // --- Option 7: Search Booking ---
    private static void handleSearchBookings() {
        System.out.println("==================================================");
        System.out.println("                SEARCH BOOKINGS                   ");
        System.out.println("==================================================");
        System.out.println("Search booking by:");
        System.out.println("1. Booking ID");
        System.out.println("2. Customer Name");
        System.out.println("3. Phone Number");
        System.out.println("--------------------------------------------------");

        int choice = InputValidator.readMenuChoice(scanner, 1, 3);
        switch (choice) {
            case 1:
                System.out.print("Enter Booking ID (e.g., BK1001): ");
                String bid = scanner.nextLine().trim();
                Booking b = hotel.findBookingById(bid);
                if (b == null) {
                    System.out.println("[!] Booking not found.");
                } else {
                    b.displayBookingDetails();
                }
                break;
            case 2:
                System.out.print("Enter Customer Name: ");
                String name = scanner.nextLine().trim();
                List<Booking> byName = hotel.searchBookingsByCustomerName(name);
                hotel.displayBookingsList(byName, "SEARCH RESULTS FOR: \"" + name + "\"");
                break;
            case 3:
                System.out.print("Enter Customer Phone Number: ");
                String phone = scanner.nextLine().trim();
                List<Booking> byPhone = hotel.searchBookingsByCustomerPhone(phone);
                hotel.displayBookingsList(byPhone, "SEARCH RESULTS FOR PHONE: \"" + phone + "\"");
                break;
        }
    }

    // --- Option 8: View Customer Details ---
    private static void handleViewCustomerDetails() {
        System.out.println("==================================================");
        System.out.println("             VIEW CUSTOMER DETAILS                ");
        System.out.println("==================================================");
        System.out.println("Search customer by:");
        System.out.println("1. Customer ID");
        System.out.println("2. Phone Number");
        System.out.println("--------------------------------------------------");

        int choice = InputValidator.readMenuChoice(scanner, 1, 2);
        Customer customer = null;
        if (choice == 1) {
            System.out.print("Enter Customer ID (e.g., CUS1001): ");
            String cid = scanner.nextLine().trim();
            customer = hotel.findCustomerById(cid);
        } else {
            System.out.print("Enter Phone Number (e.g., 9876543210): ");
            String phone = scanner.nextLine().trim();
            customer = hotel.findCustomerByPhone(phone);
        }

        if (customer == null) {
            System.out.println("[!] Customer not found.");
        } else {
            hotel.displayCustomerWithBookings(customer);
        }
    }

    // --- Option 9: View Payment Details ---
    private static void handleViewPaymentDetails() {
        System.out.println("==================================================");
        System.out.println("              VIEW PAYMENT DETAILS                ");
        System.out.println("==================================================");
        System.out.print("Enter Transaction ID (e.g., TXN10001) or Booking ID (e.g., BK1001): ");
        String query = scanner.nextLine().trim();

        // 1. Try finding payment in payment list
        Payment payment = hotel.findPaymentByTransactionId(query);
        if (payment != null) {
            payment.displayReceipt();
            return;
        }

        // 2. Try finding via Booking ID
        Booking booking = hotel.findBookingById(query);
        if (booking != null) {
            System.out.println("--------------------------------------------------");
            System.out.println("               PAYMENT RECEIPT                    ");
            System.out.println("--------------------------------------------------");
            System.out.printf("Booking ID     : %s%n", booking.getBookingId());
            System.out.printf("Transaction ID : %s%n", booking.getTransactionId());
            System.out.printf("Customer Name  : %s%n", booking.getCustomer().getFullName());
            System.out.printf("Paid Amount    : Rs. %.2f%n", booking.getTotalAmount());
            System.out.printf("Payment Status : %s%n", booking.getPaymentStatus());
            System.out.printf("Booking Status : %s%n", booking.getBookingStatus());
            System.out.println("--------------------------------------------------");
            return;
        }

        // 3. Try finding booking by transaction id
        for (Booking b : hotel.getBookings()) {
            if (b.getTransactionId().equalsIgnoreCase(query)) {
                System.out.println("--------------------------------------------------");
                System.out.println("               PAYMENT RECEIPT                    ");
                System.out.println("--------------------------------------------------");
                System.out.printf("Booking ID     : %s%n", b.getBookingId());
                System.out.printf("Transaction ID : %s%n", b.getTransactionId());
                System.out.printf("Customer Name  : %s%n", b.getCustomer().getFullName());
                System.out.printf("Paid Amount    : Rs. %.2f%n", b.getTotalAmount());
                System.out.printf("Payment Status : %s%n", b.getPaymentStatus());
                System.out.printf("Booking Status : %s%n", b.getBookingStatus());
                System.out.println("--------------------------------------------------");
                return;
            }
        }

        System.out.println("[!] No payment record found matching \"" + query + "\".");
    }

    // --- Option 10: Exit ---
    private static void handleExit() {
        System.out.println("Saving all changes to disk...");
        FileManager.saveAll(hotel);
        System.out.println("[✓] Data saved successfully.");
        System.out.println("\n==================================================");
        System.out.println(" Thank you for using Hotel Reservation System!   ");
        System.out.println("               Have a great day!                  ");
        System.out.println("==================================================");
    }

    private static void pause() {
        System.out.println("\nPress [ENTER] to return to the Main Menu...");
        scanner.nextLine();
    }
}
