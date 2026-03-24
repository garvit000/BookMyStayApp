import java.util.*;

/**
 * BookMyStay Application
 * Use Case 8: Booking History & Reporting
 */

abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected int pricePerNight;

    public Room(String roomType, int beds, int size, int pricePerNight) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq ft");
        System.out.println("Price per Night: ₹" + pricePerNight);
    }

    public String getRoomType() {
        return roomType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }
}

/* Concrete Room Types */

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 2500);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 4000);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 600, 7000);
    }
}

/* Reservation Model */

class Reservation {
    String reservationId;
    String guestName;
    String roomType;
    int price;

    public Reservation(String reservationId, String guestName, String roomType, int price) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.price = price;
    }
}

/* Booking History */

class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void addBooking(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllBookings() {
        return history;
    }

    public void displayHistory() {
        System.out.println("\n---- Booking History ----");
        for (Reservation r : history) {
            System.out.println("Reservation ID: " + r.reservationId +
                    ", Guest: " + r.guestName +
                    ", Room Type: " + r.roomType +
                    ", Price: ₹" + r.price);
        }
    }
}

/* Reporting Service */

class BookingReportService {

    public void generateReport(List<Reservation> history) {
        System.out.println("\n---- Booking Report ----");

        int totalBookings = history.size();
        int totalRevenue = 0;

        Map<String, Integer> roomCountMap = new HashMap<>();

        for (Reservation r : history) {
            totalRevenue += r.price;

            roomCountMap.put(r.roomType,
                    roomCountMap.getOrDefault(r.roomType, 0) + 1);
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);

        System.out.println("\nBookings by Room Type:");
        for (Map.Entry<String, Integer> entry : roomCountMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/* Main Application */

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println("   Booking History & Reporting");
        System.out.println("=================================\n");

        /* Create rooms */
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        /* Booking history */
        BookingHistory history = new BookingHistory();

        /* Simulate confirmed bookings */
        Reservation r1 = new Reservation("R001", "Alice", single.getRoomType(), single.getPricePerNight());
        Reservation r2 = new Reservation("R002", "Bob", suite.getRoomType(), suite.getPricePerNight());
        Reservation r3 = new Reservation("R003", "Charlie", doubleRoom.getRoomType(), doubleRoom.getPricePerNight());
        Reservation r4 = new Reservation("R004", "David", suite.getRoomType(), suite.getPricePerNight());

        /* Add to history (after confirmation) */
        history.addBooking(r1);
        history.addBooking(r2);
        history.addBooking(r3);
        history.addBooking(r4);

        /* Display history */
        history.displayHistory();

        /* Generate report */
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history.getAllBookings());

        System.out.println("\nApplication terminated.");
    }
}