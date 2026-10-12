import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * High-performance, zero-dependency Excel (.xlsx) exporter for StayEase Hotel Reservation System.
 *
 * Generates an authentic Microsoft Office OpenXML (.xlsx) workbook directly adhering to
 * ECMA-376 / ISO/IEC 29500 standards using Java Standard Library (java.util.zip).
 *
 * Creates three structured, formatted worksheets:
 *   1. Rooms      - Room inventory, category, nightly rate (₹), availability
 *   2. Customers  - Customer ID, Name, Phone, Email, Guests
 *   3. Bookings   - All reservations (confirmed & cancelled), financials, dates, audit IDs
 *
 * Developed for CodeAlpha Java Programming Internship.
 */
public class ExcelExporter {

    private static final String DEFAULT_OUTPUT_FILE = "StayEase_Hotel_Data.xlsx";

    /**
     * Standalone main runner for direct CLI execution.
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   STAYEASE HOTEL SYSTEM - EXCEL EXPORT UTILITY   ");
        System.out.println("==================================================");

        String outputPath = (args.length > 0) ? args[0] : DEFAULT_OUTPUT_FILE;
        boolean success = exportFromFiles(outputPath);

        if (success) {
            System.out.println("\n[✓] Export completed successfully: " + outputPath);
        } else {
            System.err.println("\n[✗] Export encountered an error.");
            System.exit(1);
        }
    }

    /**
     * Reads existing persistent data files and exports them to the default target workbook.
     *
     * @return true if export succeeded, false otherwise
     */
    public static boolean exportFromFiles() {
        return exportFromFiles(DEFAULT_OUTPUT_FILE);
    }

    /**
     * Reads data from data/rooms.txt, data/customers.txt, and data/bookings.txt
     * and compiles the StayEase_Hotel_Data.xlsx workbook.
     *
     * @param targetFilePath Target path for the .xlsx file
     * @return true if export succeeded, false otherwise
     */
    public static boolean exportFromFiles(String targetFilePath) {
        System.out.println("Reading source data files...");
        List<Room> rooms = FileManager.loadRooms();
        List<Customer> customers = FileManager.loadCustomers();
        List<Booking> bookings = FileManager.loadBookings(customers);

        System.out.println(String.format("  • Rooms loaded: %d", rooms.size()));
        System.out.println(String.format("  • Customers loaded: %d", customers.size()));
        System.out.println(String.format("  • Bookings loaded: %d (including cancelled)", bookings.size()));

        return exportData(rooms, customers, bookings, targetFilePath);
    }

    /**
     * Exports provided collections into a formatted multi-sheet Excel (.xlsx) workbook.
     *
     * @param rooms          List of room entities
     * @param customers      List of customer entities
     * @param bookings       List of booking entities (including cancelled)
     * @param targetFilePath Destination file path
     * @return true if successful, false otherwise
     */
    public static boolean exportData(List<Room> rooms, List<Customer> customers, List<Booking> bookings, String targetFilePath) {
        File file = new File(targetFilePath);
        try (FileOutputStream fos = new FileOutputStream(file);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // 1. [Content_Types].xml
            addZipEntry(zos, "[Content_Types].xml", buildContentTypesXml());

            // 2. _rels/.rels
            addZipEntry(zos, "_rels/.rels", buildGlobalRelsXml());

            // 3. xl/workbook.xml
            addZipEntry(zos, "xl/workbook.xml", buildWorkbookXml());

            // 4. xl/_rels/workbook.xml.rels
            addZipEntry(zos, "xl/_rels/workbook.xml.rels", buildWorkbookRelsXml());

            // 5. xl/styles.xml
            addZipEntry(zos, "xl/styles.xml", buildStylesXml());

            // 6. xl/worksheets/sheet1.xml (Rooms)
            addZipEntry(zos, "xl/worksheets/sheet1.xml", buildRoomsSheetXml(rooms));

            // 7. xl/worksheets/sheet2.xml (Customers)
            addZipEntry(zos, "xl/worksheets/sheet2.xml", buildCustomersSheetXml(customers));

            // 8. xl/worksheets/sheet3.xml (Bookings)
            addZipEntry(zos, "xl/worksheets/sheet3.xml", buildBookingsSheetXml(bookings));

            zos.finish();
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to generate Excel workbook: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- XML Structure Builders ---

    private static String buildContentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
                "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
                "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
                "  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n" +
                "  <Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n" +
                "  <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                "  <Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                "  <Override PartName=\"/xl/worksheets/sheet3.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                "</Types>";
    }

    private static String buildGlobalRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n" +
                "</Relationships>";
    }

    private static String buildWorkbookXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" " +
                "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n" +
                "  <sheets>\n" +
                "    <sheet name=\"Rooms\" sheetId=\"1\" r:id=\"rId1\"/>\n" +
                "    <sheet name=\"Customers\" sheetId=\"2\" r:id=\"rId2\"/>\n" +
                "    <sheet name=\"Bookings\" sheetId=\"3\" r:id=\"rId3\"/>\n" +
                "  </sheets>\n" +
                "</workbook>";
    }

    private static String buildWorkbookRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n" +
                "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>\n" +
                "  <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet3.xml\"/>\n" +
                "  <Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
                "</Relationships>";
    }

    /**
     * Styles configuration:
     * Style indices (s):
     *   0 = Normal text (left)
     *   1 = Header (Navy blue background #1F4E78, bold white text, centered)
     *   2 = Left-aligned text
     *   3 = Right-aligned Integer
     *   4 = Right-aligned Currency (₹#,##0.00)
     *   5 = Centered text / ID / Date
     *   6 = Available / Confirmed / Success badge (Soft green #D9EAD3)
     *   7 = Occupied / Cancelled / Failed badge (Soft red #F4CCCC)
     */
    private static String buildStylesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n" +
                "  <numFmts count=\"1\">\n" +
                "    <numFmt numFmtId=\"164\" formatCode=\"&quot;₹ &quot;#,##0.00\"/>\n" +
                "  </numFmts>\n" +
                "  <fonts count=\"4\">\n" +
                "    <font><sz val=\"11\"/><name val=\"Segoe UI\"/><color rgb=\"FF1A1A1A\"/></font>\n" + // 0: Normal
                "    <font><b/><sz val=\"11\"/><name val=\"Segoe UI\"/><color rgb=\"FFFFFFFF\"/></font>\n" + // 1: Header bold white
                "    <font><b/><sz val=\"11\"/><name val=\"Segoe UI\"/><color rgb=\"FF274E13\"/></font>\n" + // 2: Success dark green
                "    <font><b/><sz val=\"11\"/><name val=\"Segoe UI\"/><color rgb=\"FF990000\"/></font>\n" + // 3: Danger dark red
                "  </fonts>\n" +
                "  <fills count=\"5\">\n" +
                "    <fill><patternFill patternType=\"none\"/></fill>\n" + // 0: required
                "    <fill><patternFill patternType=\"gray125\"/></fill>\n" + // 1: required
                "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF1B365D\"/></patternFill></fill>\n" + // 2: Navy blue
                "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD9EAD3\"/></patternFill></fill>\n" + // 3: Soft green
                "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFF4CCCC\"/></patternFill></fill>\n" + // 4: Soft red
                "  </fills>\n" +
                "  <borders count=\"2\">\n" +
                "    <border><left/><right/><top/><bottom/><diagonal/></border>\n" + // 0: None
                "    <border>\n" + // 1: Thin light gray border
                "      <left style=\"thin\"><color rgb=\"FFD9D9D9\"/></left>\n" +
                "      <right style=\"thin\"><color rgb=\"FFD9D9D9\"/></right>\n" +
                "      <top style=\"thin\"><color rgb=\"FFD9D9D9\"/></top>\n" +
                "      <bottom style=\"thin\"><color rgb=\"FFD9D9D9\"/></bottom>\n" +
                "      <diagonal/>\n" +
                "    </border>\n" +
                "  </borders>\n" +
                "  <cellStyleXfs count=\"1\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/>\n" +
                "  </cellStyleXfs>\n" +
                "  <cellXfs count=\"8\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\"/>\n" + // 0: Default
                "    <xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\" wrapText=\"1\"/></xf>\n" + // 1: Header
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"left\" vertical=\"center\"/></xf>\n" + // 2: Text Left
                "    <xf numFmtId=\"1\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyNumberFormat=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>\n" + // 3: Integer
                "    <xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyNumberFormat=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>\n" + // 4: Currency
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" + // 5: Center Text/ID/Date
                "    <xf numFmtId=\"0\" fontId=\"2\" fillId=\"3\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" + // 6: Available/Success Badge
                "    <xf numFmtId=\"0\" fontId=\"3\" fillId=\"4\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" + // 7: Occupied/Cancel Badge
                "  </cellXfs>\n" +
                "</styleSheet>";
    }

    // --- Worksheets ---

    private static String buildRoomsSheetXml(List<Room> rooms) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");

        // Column widths
        sb.append("  <cols>\n");
        sb.append("    <col min=\"1\" max=\"1\" width=\"16\" customWidth=\"1\"/>\n"); // Room Number
        sb.append("    <col min=\"2\" max=\"2\" width=\"20\" customWidth=\"1\"/>\n"); // Category
        sb.append("    <col min=\"3\" max=\"3\" width=\"22\" customWidth=\"1\"/>\n"); // Price Per Night
        sb.append("    <col min=\"4\" max=\"4\" width=\"22\" customWidth=\"1\"/>\n"); // Availability Status
        sb.append("  </cols>\n");

        sb.append("  <sheetData>\n");

        // Header Row (Row 1)
        int rowIdx = 1;
        sb.append(String.format("    <row r=\"%d\" ht=\"28\" customHeight=\"1\">\n", rowIdx));
        sb.append(cellInlineStr("A" + rowIdx, 1, "Room Number"));
        sb.append(cellInlineStr("B" + rowIdx, 1, "Category"));
        sb.append(cellInlineStr("C" + rowIdx, 1, "Price Per Night (₹)"));
        sb.append(cellInlineStr("D" + rowIdx, 1, "Availability Status"));
        sb.append("    </row>\n");

        // Data Rows
        for (Room r : rooms) {
            rowIdx++;
            sb.append(String.format("    <row r=\"%d\" ht=\"20\" customHeight=\"1\">\n", rowIdx));
            sb.append(cellNumber("A" + rowIdx, 5, r.getRoomNumber())); // Center room number
            sb.append(cellInlineStr("B" + rowIdx, 2, r.getCategory()));
            sb.append(cellDouble("C" + rowIdx, 4, r.getPricePerNight()));
            if (r.isAvailable()) {
                sb.append(cellInlineStr("D" + rowIdx, 6, "Available"));
            } else {
                sb.append(cellInlineStr("D" + rowIdx, 7, "Occupied"));
            }
            sb.append("    </row>\n");
        }

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    private static String buildCustomersSheetXml(List<Customer> customers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");

        // Column widths
        sb.append("  <cols>\n");
        sb.append("    <col min=\"1\" max=\"1\" width=\"18\" customWidth=\"1\"/>\n"); // Customer ID
        sb.append("    <col min=\"2\" max=\"2\" width=\"26\" customWidth=\"1\"/>\n"); // Full Name
        sb.append("    <col min=\"3\" max=\"3\" width=\"20\" customWidth=\"1\"/>\n"); // Phone Number
        sb.append("    <col min=\"4\" max=\"4\" width=\"32\" customWidth=\"1\"/>\n"); // Email Address
        sb.append("    <col min=\"5\" max=\"5\" width=\"18\" customWidth=\"1\"/>\n"); // Number of Guests
        sb.append("  </cols>\n");

        sb.append("  <sheetData>\n");

        // Header Row (Row 1)
        int rowIdx = 1;
        sb.append(String.format("    <row r=\"%d\" ht=\"28\" customHeight=\"1\">\n", rowIdx));
        sb.append(cellInlineStr("A" + rowIdx, 1, "Customer ID"));
        sb.append(cellInlineStr("B" + rowIdx, 1, "Full Name"));
        sb.append(cellInlineStr("C" + rowIdx, 1, "Phone Number"));
        sb.append(cellInlineStr("D" + rowIdx, 1, "Email Address"));
        sb.append(cellInlineStr("E" + rowIdx, 1, "Guests"));
        sb.append("    </row>\n");

        // Data Rows
        for (Customer c : customers) {
            rowIdx++;
            sb.append(String.format("    <row r=\"%d\" ht=\"20\" customHeight=\"1\">\n", rowIdx));
            sb.append(cellInlineStr("A" + rowIdx, 5, c.getCustomerId()));
            sb.append(cellInlineStr("B" + rowIdx, 2, c.getFullName()));
            sb.append(cellInlineStr("C" + rowIdx, 5, c.getPhoneNumber()));
            sb.append(cellInlineStr("D" + rowIdx, 2, c.getEmail()));
            sb.append(cellNumber("E" + rowIdx, 5, c.getNumberOfGuests()));
            sb.append("    </row>\n");
        }

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    private static String buildBookingsSheetXml(List<Booking> bookings) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");

        // Column widths
        sb.append("  <cols>\n");
        sb.append("    <col min=\"1\" max=\"1\" width=\"16\" customWidth=\"1\"/>\n");  // Booking ID
        sb.append("    <col min=\"2\" max=\"2\" width=\"16\" customWidth=\"1\"/>\n");  // Customer ID
        sb.append("    <col min=\"3\" max=\"3\" width=\"24\" customWidth=\"1\"/>\n");  // Guest Name
        sb.append("    <col min=\"4\" max=\"4\" width=\"14\" customWidth=\"1\"/>\n");  // Room No
        sb.append("    <col min=\"5\" max=\"5\" width=\"18\" customWidth=\"1\"/>\n");  // Category
        sb.append("    <col min=\"6\" max=\"6\" width=\"16\" customWidth=\"1\"/>\n");  // Check-In
        sb.append("    <col min=\"7\" max=\"7\" width=\"16\" customWidth=\"1\"/>\n");  // Check-Out
        sb.append("    <col min=\"8\" max=\"8\" width=\"12\" customWidth=\"1\"/>\n");  // Nights
        sb.append("    <col min=\"9\" max=\"9\" width=\"12\" customWidth=\"1\"/>\n");  // Guests
        sb.append("    <col min=\"10\" max=\"10\" width=\"18\" customWidth=\"1\"/>\n"); // Rate (₹)
        sb.append("    <col min=\"11\" max=\"11\" width=\"20\" customWidth=\"1\"/>\n"); // Total (₹)
        sb.append("    <col min=\"12\" max=\"12\" width=\"18\" customWidth=\"1\"/>\n"); // Status
        sb.append("    <col min=\"13\" max=\"13\" width=\"18\" customWidth=\"1\"/>\n"); // Payment
        sb.append("    <col min=\"14\" max=\"14\" width=\"20\" customWidth=\"1\"/>\n"); // Txn ID
        sb.append("  </cols>\n");

        sb.append("  <sheetData>\n");

        // Header Row (Row 1)
        int rowIdx = 1;
        sb.append(String.format("    <row r=\"%d\" ht=\"28\" customHeight=\"1\">\n", rowIdx));
        sb.append(cellInlineStr("A" + rowIdx, 1, "Booking ID"));
        sb.append(cellInlineStr("B" + rowIdx, 1, "Customer ID"));
        sb.append(cellInlineStr("C" + rowIdx, 1, "Guest Name"));
        sb.append(cellInlineStr("D" + rowIdx, 1, "Room No"));
        sb.append(cellInlineStr("E" + rowIdx, 1, "Category"));
        sb.append(cellInlineStr("F" + rowIdx, 1, "Check-In"));
        sb.append(cellInlineStr("G" + rowIdx, 1, "Check-Out"));
        sb.append(cellInlineStr("H" + rowIdx, 1, "Nights"));
        sb.append(cellInlineStr("I" + rowIdx, 1, "Guests"));
        sb.append(cellInlineStr("J" + rowIdx, 1, "Rate / Night (₹)"));
        sb.append(cellInlineStr("K" + rowIdx, 1, "Total Amount (₹)"));
        sb.append(cellInlineStr("L" + rowIdx, 1, "Booking Status"));
        sb.append(cellInlineStr("M" + rowIdx, 1, "Payment Status"));
        sb.append(cellInlineStr("N" + rowIdx, 1, "Transaction ID"));
        sb.append("    </row>\n");

        // Data Rows (includes CONFIRMED, CANCELLED, and all other historical records)
        for (Booking b : bookings) {
            rowIdx++;
            sb.append(String.format("    <row r=\"%d\" ht=\"20\" customHeight=\"1\">\n", rowIdx));
            sb.append(cellInlineStr("A" + rowIdx, 5, b.getBookingId()));

            String custId = (b.getCustomer() != null) ? b.getCustomer().getCustomerId() : "N/A";
            String guestName = (b.getCustomer() != null) ? b.getCustomer().getFullName() : "N/A";
            sb.append(cellInlineStr("B" + rowIdx, 5, custId));
            sb.append(cellInlineStr("C" + rowIdx, 2, guestName));

            sb.append(cellNumber("D" + rowIdx, 5, b.getRoomNumber()));
            sb.append(cellInlineStr("E" + rowIdx, 2, b.getRoomCategory()));
            sb.append(cellInlineStr("F" + rowIdx, 5, b.getCheckInDate() != null ? b.getCheckInDate().toString() : ""));
            sb.append(cellInlineStr("G" + rowIdx, 5, b.getCheckOutDate() != null ? b.getCheckOutDate().toString() : ""));
            sb.append(cellNumber("H" + rowIdx, 5, b.getNumberOfNights()));
            sb.append(cellNumber("I" + rowIdx, 5, b.getNumberOfGuests()));
            sb.append(cellDouble("J" + rowIdx, 4, b.getPricePerNight()));
            sb.append(cellDouble("K" + rowIdx, 4, b.getTotalAmount()));

            // Booking Status badge
            int bookingStyle = "CONFIRMED".equalsIgnoreCase(b.getBookingStatus()) ? 6 : 7;
            sb.append(cellInlineStr("L" + rowIdx, bookingStyle, b.getBookingStatus()));

            // Payment Status badge
            int paymentStyle = "SUCCESS".equalsIgnoreCase(b.getPaymentStatus()) ? 6 : 7;
            sb.append(cellInlineStr("M" + rowIdx, paymentStyle, b.getPaymentStatus()));

            sb.append(cellInlineStr("N" + rowIdx, 5, b.getTransactionId()));
            sb.append("    </row>\n");
        }

        sb.append("  </sheetData>\n");
        sb.append("</worksheet>");
        return sb.toString();
    }

    // --- Helper Cell Writers ---

    private static String cellInlineStr(String ref, int styleIdx, String text) {
        String safeText = escapeXml(text != null ? text : "");
        return String.format("      <c r=\"%s\" s=\"%d\" t=\"inlineStr\"><is><t>%s</t></is></c>\n", ref, styleIdx, safeText);
    }

    private static String cellNumber(String ref, int styleIdx, long value) {
        return String.format("      <c r=\"%s\" s=\"%d\"><v>%d</v></c>\n", ref, styleIdx, value);
    }

    private static String cellDouble(String ref, int styleIdx, double value) {
        return String.format("      <c r=\"%s\" s=\"%d\"><v>%.2f</v></c>\n", ref, styleIdx, value);
    }

    private static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static void addZipEntry(ZipOutputStream zos, String entryName, String xmlContent) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        byte[] bytes = xmlContent.getBytes(StandardCharsets.UTF_8);
        zos.write(bytes, 0, bytes.length);
        zos.closeEntry();
    }
}
