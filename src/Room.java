/**
 * Represents a hotel room in the Hotel Reservation System.
 * 
 * Encapsulates room properties including room number, category,
 * price per night, and current availability status.
 * 
 * Developed for CodeAlpha Java Programming Internship.
 */
public class Room {
    private int roomNumber;
    private String category;
    private double pricePerNight;
    private boolean available;

    /**
     * Parameterized constructor initializing all room details with default availability as true.
     *
     * @param roomNumber    The unique room number (e.g., 101, 201)
     * @param category      Room category (Standard, Deluxe, Suite)
     * @param pricePerNight Nightly rate in INR
     */
    public Room(int roomNumber, String category, double pricePerNight) {
        this(roomNumber, category, pricePerNight, true);
    }

    /**
     * Parameterized constructor initializing all room details including availability.
     *
     * @param roomNumber    The unique room number
     * @param category      Room category
     * @param pricePerNight Nightly rate in INR
     * @param available     Initial availability status
     */
    public Room(int roomNumber, String category, double pricePerNight, boolean available) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    // --- Getters and Setters ---

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns a human-readable availability status string.
     *
     * @return "Available" if room is free, otherwise "Booked"
     */
    public String getStatusString() {
        return available ? "Available" : "Booked";
    }

    /**
     * Formats the room details as a neat row for tabular console display.
     */
    public String toTableRow() {
        return String.format("%-11d %-14s ₹%-16.2f %-10s",
                roomNumber, category, pricePerNight, getStatusString());
    }

    @Override
    public String toString() {
        return "Room [Room No=" + roomNumber +
                ", Category=" + category +
                ", Price/Night=₹" + String.format("%.2f", pricePerNight) +
                ", Status=" + getStatusString() + "]";
    }
}
