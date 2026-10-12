# Hotel Reservation System (StayEase)

A modular, portfolio-quality Console Application built with Core Java, adhering strictly to Object-Oriented Programming (OOP) principles, robust file handling, input validation, transaction auditing, and formatted Excel reporting.

Developed as part of the **CodeAlpha Java Programming Internship**.

---

## 📌 Overview

The **Hotel Reservation System** automates the end-to-end hospitality lifecycle:
- Browsing and searching categorized room inventory (Standard, Deluxe, Suite).
- Real-time room availability management.
- Guest registration and booking coordination.
- Automated calendar night calculation and dynamic billing.
- Realistic payment processing simulation (UPI, Card, Cash) with audit transaction IDs.
- Reservation lookup, cancellation with instant inventory release, and guest history tracking.
- Complete data persistence using text files inside the root-level `data/` directory across application sessions.
- Comprehensive Excel spreadsheet export (`StayEase_Hotel_Data.xlsx`) generating formatted multi-sheet reports.

---

## ✨ Features

- **Room Catalog & Real-Time Availability**:
  - Pre-configured rooms across Standard (101–105), Deluxe (201–205), and Suite (301–303) categories.
  - Formatted tabular console views with price per night and live status.
- **Categorized Room Search**:
  - Filter available rooms by category (`Standard`, `Deluxe`, `Suite`, `All`).
  - Handles occupied states with user-friendly notices when a category is fully booked.
- **Guest Profile Management**:
  - Auto-generated sequential Customer IDs (`CUS1001`, `CUS1002`).
  - Multi-criteria lookup by Customer ID or phone number.
  - Linked booking history display.
- **Booking Lifecycle**:
  - Step-by-step reservation workflow with pre-confirmation cost breakdown.
  - Collision protection: Double-booking prevention on occupied rooms.
  - Unique Booking IDs (`BK1001`, `BK1002`).
- **Date Management & Invariants**:
  - `java.time.LocalDate` with strict `YYYY-MM-DD` formatting.
  - Past check-in date blocking (`checkIn >= today`).
  - Strict chronological order enforcement (`checkOut > checkIn`).
  - Automated duration computation via `ChronoUnit.DAYS`.
- **Payment Simulation**:
  - Supports UPI, Credit/Debit Card, and Cash at check-in.
  - Simulates processing delay and generates reference IDs (`TXN10001`).
- **Reservation Cancellation**:
  - Cancellations update booking status to `CANCELLED` and immediately release the room back to `Available`.
  - Guarded against duplicate cancellations.
- **Fault-Tolerant Text File Persistence**:
  - Clean CSV-based storage in `data/rooms.txt`, `data/customers.txt`, and `data/bookings.txt`.
  - Stored strictly in text files under the root-level `data/` directory (no database required).
  - Auto-initialization of storage directories and default inventory.
  - Complete state restoration and sequence counter synchronization on restart.
- **Excel Spreadsheet Export (.xlsx)**:
  - Generates a styled, multi-sheet Microsoft Excel workbook (`StayEase_Hotel_Data.xlsx`).
  - Creates three dedicated worksheets: **Rooms**, **Customers**, and **Bookings**.
  - Formats numbers, dates, currency amounts (`₹ #,##0.00`), and availability/status badges.
  - Preserves every historical record, including **cancelled bookings**.
- **Crash-Resilient Input Validation**:
  - Sanitizes invalid menu selections, negative integers, malformed dates, and irregular phone/email formats without crashing.

---

## 🛠 Technologies Used

- **Language**: Java 17+ (Core Java)
- **Paradigm**: Object-Oriented Programming (OOP)
- **Collections Framework**: `java.util.ArrayList`, `java.util.List`
- **Date & Time API**: `java.time.LocalDate`, `java.time.temporal.ChronoUnit`
- **File I/O & Packaging**: `java.io.BufferedReader`, `java.io.BufferedWriter`, `java.io.File`, `java.util.zip.ZipOutputStream`
- **Regular Expressions**: `java.util.regex.Pattern`
- **User Interface**: Standard Console UI with formatted tables
- **Reporting Engine**: OpenXML (ECMA-376) zero-dependency `.xlsx` generation

*(Note: In accordance with project requirements, external frameworks such as Spring Boot, Hibernate, MySQL, or third-party SDKs are purposefully omitted in this core edition to prioritize pure OOP and Java standard library fundamentals. All records are persisted using plain text files without an external database).*

---

## 📂 Project Structure

```
HotelReservationSystem/
│
├── src/
│   ├── Main.java               # Application entry point & interactive console menu
│   ├── Hotel.java              # Central business logic engine & collections manager
│   ├── Room.java               # Room entity model
│   ├── Customer.java           # Customer entity model
│   ├── Booking.java            # Booking model with Customer composition
│   ├── Payment.java            # Payment simulation & transaction generator
│   ├── FileManager.java        # Persistent File I/O manager (text-file persistence)
│   ├── ExcelExporter.java      # OpenXML multi-sheet Excel (.xlsx) workbook generator
│   ├── InputValidator.java     # Input validation & date parsing utilities
│   └── AppTest.java            # Comprehensive 18-point automated test suite
│
├── data/
│   ├── rooms.txt               # Persistent room inventory records (plain text)
│   ├── customers.txt           # Persistent customer profiles (plain text)
│   └── bookings.txt            # Persistent reservation records (plain text)
│
├── screenshots/                # Application execution screenshots
├── .gitignore                  # Git ignore rules for bytecode, IDE files, and Excel outputs
└── README.md                   # Project documentation
```

---

## 🧩 OOP Concepts Applied

1. **Encapsulation & Data Hiding**:
   - All fields across model classes (`Room`, `Customer`, `Booking`, `Payment`) are `private`.
   - State access and mutations are channeled strictly through validated getters and setters.
2. **Composition (`HAS-A` Relationship)**:
   - A `Booking` *has a* `Customer` object, eliminating data redundancy and enabling direct traversal of customer details.
   - `Hotel` *has collections of* `Room`, `Customer`, `Booking`, and `Payment` entities.
3. **Separation of Concerns (SoC)**:
   - `Main.java`: UI orchestration, menus, and user interaction.
   - `Hotel.java`: Core domain and business logic.
   - `FileManager.java`: Pure text-file I/O persistence.
   - `ExcelExporter.java`: Dedicated spreadsheet formatting and OpenXML workbook packaging.
   - `InputValidator.java`: Input sanitization and rule checking.
4. **Abstraction**:
   - High-level business operations (`createBooking`, `cancelBooking`, `displayAvailableRooms`, `exportToExcel`) encapsulate internal list traversals and state transitions.

---

## 💾 File Handling Architecture

The application does **not** rely on a database. All application data is persisted directly in flat text files located inside the root-level `data/` directory:

| File | Purpose | Storage Schema |
| :--- | :--- | :--- |
| `data/rooms.txt` | Room inventory & status | `roomNumber,category,pricePerNight,available` |
| `data/customers.txt` | Registered customer records | `customerId,fullName,phoneNumber,email,numberOfGuests` |
| `data/bookings.txt` | Active and historical bookings | `bookingId,customerId,roomNumber,roomCategory,checkInDate,checkOutDate,numberOfNights,numberOfGuests,pricePerNight,totalAmount,bookingStatus,paymentStatus,transactionId` |

---

## 📊 Excel Export Feature

The system includes a built-in Excel export utility that compiles live records into an authentic Microsoft Office OpenXML workbook:

- **Target File**: `StayEase_Hotel_Data.xlsx` (generated at the project root)
- **Menu Trigger**: Select option **`10. Export Data to Excel (.xlsx)`** from the main console menu.
- **Three Worksheets**:
  1. **Rooms**: Lists every room number, category, price per night (₹), and live availability status (`Available` or `Occupied`).
  2. **Customers**: Contains customer IDs, full names, contact phone numbers, email addresses, and guest counts.
  3. **Bookings**: Contains comprehensive reservation details including booking ID, customer ID, guest name, room details, check-in/check-out dates, night counts, pricing breakdown, booking status, payment status, and audit transaction IDs.
- **Preserves Cancelled Bookings**: Every historical record is retained in the export, including `CANCELLED` bookings, clearly designated with status badges.
- **Professional Formatting**: Styled with dark navy header rows, auto-tuned column widths, borders, formatted dates, and currency formatting (`₹ #,##0.00`).

---

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK 17 or newer) installed.
- Terminal / Command Prompt or any IDE (VS Code, IntelliJ IDEA, Eclipse).

### Compilation and Execution

1. **Compile all Java source files**:
   ```bash
   javac -d out src/*.java
   ```

2. **Run the Hotel Reservation System application**:
   ```bash
   java -cp out Main
   ```

3. **Run the Automated Test Suite**:
   ```bash
   java -cp out AppTest
   ```

---

## 📖 How to Use

When the application launches, the main menu displays 11 options:

```
==================================================
              HOTEL RESERVATION SYSTEM            
==================================================
1. View All Rooms
2. Search Available Rooms
3. Book a Room
4. View Booking Details
5. Cancel Booking
6. View All Bookings
7. Search Booking
8. View Customer Details
9. View Payment Details
10. Export Data to Excel (.xlsx)
11. Exit
==================================================
```

### Typical Workflows

- **Book a Room (Option 3)**:
  1. Enter guest phone number (existing guests are recognized automatically).
  2. Select an available room number (e.g., `201`).
  3. Enter check-in and check-out dates (`YYYY-MM-DD`).
  4. Review the booking summary and choose payment method (UPI, Card, or Cash).
  5. Receive the confirmation card with unique Booking ID (`BK1001`) and Transaction ID (`TXN10001`).

- **Export Data to Excel (Option 10)**:
  1. Choose Option **10** from the menu.
  2. The system flushes all active records to disk and generates `StayEase_Hotel_Data.xlsx`.
  3. Open `StayEase_Hotel_Data.xlsx` in Microsoft Excel or Google Sheets to inspect the **Rooms**, **Customers**, and **Bookings** worksheets.

---

## 🖥 Sample Console Output

### 1. Booking Confirmation Card
```
==================================================
              BOOKING CONFIRMED                   
==================================================
Booking ID        : BK1001
Customer ID       : CUS1001
Customer Name     : Rahul Kumar
Phone             : 9876543210
Room Number       : 201
Room Category     : Deluxe
Check-in          : 2026-10-10
Check-out         : 2026-10-13
Number of Nights  : 3
Guests            : 2
Price/Night       : ₹2500.00
Total Amount      : ₹7500.00

Payment Method    : UPI
Transaction ID    : TXN10001
Payment Status    : SUCCESS
Booking Status    : CONFIRMED
==================================================
[✓] Booking created successfully!
```

### 2. Tabular Reservations Overview
```
-----------------------------------------------------------------------
   ALL RESERVATIONS (2)
-----------------------------------------------------------------------
ID        Customer         Room      Check-in      Amount       Status    
-----------------------------------------------------------------------
BK1001    Rahul Kumar      201       2026-10-10    ₹7500.00     CONFIRMED 
BK1002    Aman Sharma      301       2026-10-15    ₹12000.00    CONFIRMED 
-----------------------------------------------------------------------
```

---

## 🧪 Automated Test Verification

The project includes an end-to-end automated verification harness ([AppTest.java](file:///d:/Hotel%20Reservation%20System/src/AppTest.java)) executing all 18 CodeAlpha required scenarios:

| Test ID | Test Scenario | Verified Result |
| :--- :---: | :--- | :---: |
| **TEST 1** | View all 13 initial rooms | ✅ PASS |
| **TEST 2** | Search Standard rooms (5 rooms) | ✅ PASS |
| **TEST 3** | Search Deluxe rooms (5 rooms) | ✅ PASS |
| **TEST 4** | Search Suite rooms (3 rooms) | ✅ PASS |
| **TEST 5** | Book an available room | ✅ PASS |
| **TEST 6** | Block double-booking on occupied room | ✅ PASS |
| **TEST 7** | View booking by Booking ID | ✅ PASS |
| **TEST 8** | Search booking by Customer Name | ✅ PASS |
| **TEST 9** | Cancel active reservation | ✅ PASS |
| **TEST 10** | Block duplicate cancellation | ✅ PASS |
| **TEST 11** | Verify file persistence after app restart | ✅ PASS |
| **TEST 12** | Reject malformed & past dates | ✅ PASS |
| **TEST 13** | Reject out-of-range menu options | ✅ PASS |
| **TEST 14** | Safely handle non-existent room numbers | ✅ PASS |
| **TEST 15** | Strictly validate 10-digit phone format | ✅ PASS |
| **TEST 16** | Validate total billing math (`rate × nights`) | ✅ PASS |
| **TEST 17** | Verify unique transaction ID generation | ✅ PASS |
| **TEST 18** | Confirm room becomes available upon cancellation | ✅ PASS |

---

## 🔮 Future Improvements

- Add a graphical desktop interface using JavaFX or Swing.
- Integrate an RDBMS (e.g., MySQL or PostgreSQL) with JDBC.
- Add seasonal pricing rules, discounts, and room amenities metadata.
- Support automated PDF invoice generation.

---

## 👨‍💻 Author

**Java Programming Intern**  
Developed for the **CodeAlpha Java Programming Internship**.
