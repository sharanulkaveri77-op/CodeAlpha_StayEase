import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Simulates a payment transaction in the Hotel Reservation System.
 * 
 * Supports simulated payment processing for UPI, Credit/Debit Card, and Cash,
 * generating unique transaction reference IDs and capturing audit timestamps.
 * 
 * NOTE: This is strictly an educational simulation. No real financial credentials
 * (card numbers, PINs, CVVs) are processed or stored.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Payment {
    private static int nextTransactionSequence = 10001;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String transactionId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    private LocalDateTime paymentDate;

    /**
     * Parameterized constructor initializing a payment transaction.
     */
    public Payment(String transactionId, double amount, String paymentMethod, String paymentStatus, LocalDateTime paymentDate) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    /**
     * Generates a unique transaction reference ID (e.g. TXN10001, TXN10002).
     */
    public static synchronized String generateTransactionId() {
        return "TXN" + (nextTransactionSequence++);
    }

    /**
     * Updates transaction sequence counter (used when loading historical payments).
     */
    public static synchronized void updateTransactionSequence(int lastIdNumber) {
        if (lastIdNumber >= nextTransactionSequence) {
            nextTransactionSequence = lastIdNumber + 1;
        }
    }

    /**
     * Simulates payment collection and processing with interactive console prompts.
     *
     * @param amount  Total payable amount in INR
     * @param scanner Scanner instance for console input
     * @return Processed Payment object with SUCCESS status
     */
    public static Payment processPayment(double amount, Scanner scanner) {
        System.out.println("==================================================");
        System.out.println("                 PAYMENT GATEWAY                  ");
        System.out.println("==================================================");
        System.out.printf("Total Payable Amount: Rs. %.2f%n", amount);
        System.out.println();
        System.out.println("Available Payment Methods:");
        System.out.println("  1. UPI (GPay, PhonePe, Paytm)");
        System.out.println("  2. Credit/Debit Card");
        System.out.println("  3. Cash at Check-in");
        System.out.println("--------------------------------------------------");

        String chosenMethod = "";
        while (true) {
            System.out.print("Choose payment method (1-3): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    chosenMethod = "UPI";
                    break;
                case "2":
                    chosenMethod = "Credit/Debit Card";
                    break;
                case "3":
                    chosenMethod = "Cash";
                    break;
                default:
                    System.out.println("[!] Invalid option. Please enter 1 for UPI, 2 for Card, or 3 for Cash.");
                    continue;
            }
            break;
        }

        System.out.println("\nProcessing payment...");
        try {
            Thread.sleep(600); // Realistic processing delay
        } catch (InterruptedException ignored) {}

        System.out.println("Payment successful!");

        String txnId = generateTransactionId();
        Payment payment = new Payment(txnId, amount, chosenMethod, "SUCCESS", LocalDateTime.now());

        System.out.println("Transaction ID: " + txnId);
        System.out.println("==================================================");
        return payment;
    }

    // --- Getters and Setters ---

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Prints payment receipt details card.
     */
    public void displayReceipt() {
        System.out.println("--------------------------------------------------");
        System.out.println("               PAYMENT RECEIPT                    ");
        System.out.println("--------------------------------------------------");
        System.out.printf("Transaction ID : %s%n", transactionId);
        System.out.printf("Paid Amount    : Rs. %.2f%n", amount);
        System.out.printf("Method         : %s%n", paymentMethod);
        System.out.printf("Status         : %s%n", paymentStatus);
        System.out.printf("Date & Time    : %s%n", paymentDate.format(TIME_FORMATTER));
        System.out.println("--------------------------------------------------");
    }

    @Override
    public String toString() {
        return "Payment [TxnID=" + transactionId +
                ", Amount=Rs. " + String.format("%.2f", amount) +
                ", Method=" + paymentMethod +
                ", Status=" + paymentStatus +
                ", Time=" + paymentDate.format(TIME_FORMATTER) + "]";
    }
}
