import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * BookMyStay Application
 * UC4: Room Search & Availability Check (Read-Only)
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

/* Inventory (State Holder ONLY) */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0); // simulate unavailable
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAllInventory() {
        return inventory; // read-only usage expected
    }
}

/* ✅ Search Service (READ-ONLY) */

class RoomSearchService {

    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(List<Room> rooms) {

        System.out.println("\n--- Available Rooms ---");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            // ✅ Filter unavailable rooms
            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available);
                System.out.println("--------------------------");
            }
        }
    }
}

/* Application Entry */

public class UseCase4RoomSearch {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println("     Room Search System (UC4)");
        System.out.println("=================================");

        // Room objects (Domain layer)
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Inventory (State)
        RoomInventory inventory = new RoomInventory();

        // Search Service (Read-only)
        RoomSearchService searchService = new RoomSearchService(inventory);

        // Perform search
        searchService.searchAvailableRooms(rooms);
    }
}