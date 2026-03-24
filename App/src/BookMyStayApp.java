/**
 * BookMyStay Application
 * UC6: Reservation Confirmation & Room Allocation
 */

import java.util.*;

/* =========================
   Room Model
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
   Inventory (UC3)
========================= */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\n--- Inventory ---");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
    }
}

/* =========================
   Reservation (UC5)
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
}

/* =========================
   Booking Queue (UC5)
========================= */

class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/* =========================
   UC6: Allocation Logic
========================= */

class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> allocationMap = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    private String generateRoomId(String roomType) {
        String id;
        do {
            id = roomType.substring(0, 2).toUpperCase() + "-" +
                 UUID.randomUUID().toString().substring(0, 4);
        } while (allocatedRoomIds.contains(id));

        allocatedRoomIds.add(id);
        return id;
    }

    public void processQueue(BookingRequestQueue queue) {

        System.out.println("\n--- Processing Bookings ---");

        while (!queue.isEmpty()) {

            Reservation res = queue.getNextRequest();
            String roomType = res.getRoomType();

            if (inventory.getAvailability(roomType) > 0) {

                String roomId = generateRoomId(roomType);

                inventory.decrement(roomType);

                allocationMap.putIfAbsent(roomType, new HashSet<>());
                allocationMap.get(roomType).add(roomId);

                System.out.println("✅ Confirmed Booking:");
                System.out.println("Guest: " + res.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Room ID: " + roomId);
                System.out.println("--------------------------");

            } else {
                System.out.println("❌ No availability for " + roomType + " (Guest: " + res.getGuestName() + ")");
            }
        }
    }

    public void displayAllocations() {
        System.out.println("\n--- Allocated Rooms ---");
        for (Map.Entry<String, Set<String>> e : allocationMap.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

/* =========================
   Main Class
========================= */

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println(" UC6: Room Allocation System");
        System.out.println("=================================");

        // Rooms
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Display room details
        System.out.println("\n--- Room Details ---");

        single.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getRoomType()));
        System.out.println("---------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getRoomType()));
        System.out.println("---------------------------");

        suite.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getRoomType()));
        System.out.println("---------------------------");

        // Queue (UC5)
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Suite Room"));

        // UC6 Allocation
        BookingService service = new BookingService(inventory);
        service.processQueue(queue);

        // Final state
        inventory.displayInventory();
        service.displayAllocations();

        System.out.println("\nApplication terminated.");
    }
}