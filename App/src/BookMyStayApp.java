import java.util.HashMap;
import java.util.Map;

/**
 * BookMyStay Application
 * UC3: Centralized Room Inventory using HashMap
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

/* Inventory Management */

class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        /* Initialize availability */
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void displayInventory() {
        System.out.println("\n---- Current Room Inventory ----");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
    }
}

/* Application Entry */

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println("     Room Inventory System");
        System.out.println("=================================");

        /* Create room objects */
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        /* Initialize inventory */
        RoomInventory inventory = new RoomInventory();

        System.out.println("\n--- Room Details ---");

        single.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getRoomType()));
        System.out.println("--------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getRoomType()));
        System.out.println("--------------------------");

        suite.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getRoomType()));
        System.out.println("--------------------------");

        inventory.displayInventory();
    }
}