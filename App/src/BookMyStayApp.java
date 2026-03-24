import java.util.*;

/**
 * BookMyStay Application
 * UC3: Inventory
 * UC4: Room Search (Read-only)
 * UC5: Booking Request Queue (FIFO)
 */

/* =========================
   Room Domain Model
========================= */

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
}

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

/* =========================
   UC3: Inventory
========================= */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        System.out.println("\n---- Current Room Inventory ----");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/* =========================
   UC4: Room Search (Read-only)
========================= */

class RoomSearchService {

    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(List<Room> rooms) {

        System.out.println("\n--- Available Rooms ---");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            // Defensive check: only show available rooms
            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available);
                System.out.println("--------------------------");
            }
        }
    }
}

/* =========================
   UC5: Booking Request Queue (FIFO)
========================= */

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + " | Room: " + roomType);
    }
}

class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added: " + reservation.getGuestName());
    }

    public void displayQueue() {
        System.out.println("\n--- Booking Request Queue (FIFO) ---");

        if (queue.isEmpty()) {
            System.out.println("No requests.");
            return;
        }

        for (Reservation r : queue) {
            r.displayReservation();
        }
    }
}

/* =========================
   Main Application
========================= */

public class UseCase5BookingRequestQueue {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println(" UC3 + UC4 + UC5 Implementation");
        System.out.println("=================================");

        /* Rooms */
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        /* Inventory */
        RoomInventory inventory = new RoomInventory();

        /* UC4: Search */
        RoomSearchService searchService = new RoomSearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        /* Display Inventory */
        inventory.displayInventory();

        /* UC5: Booking Request Queue */
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        requestQueue.addRequest(new Reservation("Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("David", "Single Room"));

        requestQueue.displayQueue();
    }
}