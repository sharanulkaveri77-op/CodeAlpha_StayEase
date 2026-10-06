import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input in the Hotel Reservation System.
 * 
 * Centralizes date parsing, chronological validation, string/numeric checks,
 * phone and email pattern validation, and crash-proof console reading.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class InputValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // --- Date Validation & Helpers ---

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isValidCheckInDate(LocalDate checkIn) {
        if (checkIn == null) return false;
        return !checkIn.isBefore(LocalDate.now());
    }

    public static boolean isValidCheckOutDate(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return false;
        return checkOut.isAfter(checkIn);
    }

    public static long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !isValidCheckOutDate(checkIn, checkOut)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public static LocalDate readValidCheckInDate(Scanner scanner) {
        while (true) {
            System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            LocalDate date = parseDate(input);

            if (date == null) {
                System.out.println("[!] Invalid date format. Please use YYYY-MM-DD (e.g., 2026-10-15).");
                continue;
            }

            if (!isValidCheckInDate(date)) {
                System.out.println("[!] Check-in date cannot be in the past. Please enter today or a future date.");
                continue;
            }

            return date;
        }
    }

    public static LocalDate readValidCheckOutDate(Scanner scanner, LocalDate checkIn) {
        while (true) {
            System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            LocalDate date = parseDate(input);

            if (date == null) {
                System.out.println("[!] Invalid date format. Please use YYYY-MM-DD (e.g., 2026-10-18).");
                continue;
            }

            if (!isValidCheckOutDate(checkIn, date)) {
                System.out.println("[!] Check-out date must be strictly after check-in date (" + checkIn + "). Minimum stay is 1 night.");
                continue;
            }

            return date;
        }
    }

    // --- String & Field Validation ---

    /**
     * Reads a non-empty string prompt from the console.
     */
    public static String readNonEmptyString(Scanner scanner, String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[!] " + fieldName + " cannot be empty. Please enter a valid value.");
                continue;
            }
            return input;
        }
    }

    /**
     * Validates and reads a 10-digit mobile phone number.
     */
    public static String readValidPhoneNumber(Scanner scanner) {
        while (true) {
            System.out.print("Enter Contact Phone Number (10 digits): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                System.out.println("[!] Phone number cannot be empty.");
                continue;
            }
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("[!] Invalid phone number. Please enter a valid 10-digit number (e.g., 9876543210).");
                continue;
            }
            return phone;
        }
    }

    /**
     * Validates and reads a standard formatted email address.
     */
    public static String readValidEmail(Scanner scanner) {
        while (true) {
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("[!] Email address cannot be empty.");
                continue;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("[!] Invalid email format. Please enter a valid email (e.g., user@example.com).");
                continue;
            }
            return email;
        }
    }

    /**
     * Reads a positive integer (e.g. number of guests > 0).
     */
    public static int readPositiveInt(Scanner scanner, String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    System.out.println("[!] " + fieldName + " must be greater than zero.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a valid whole number.");
            }
        }
    }

    /**
     * Reads a numeric menu selection within a specific inclusive range.
     */
    public static int readMenuChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Enter your choice (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice < min || choice > max) {
                    System.out.println("[!] Invalid option. Please select a number between " + min + " and " + max + ".");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a numeric choice.");
            }
        }
    }
}
