import java.util.*;

/**
 * BookMyStayApp - Combined Use Cases (UC1 to UC9)
 */

public class BookMyStayApp {

    /* ================= UC9: Custom Exception ================= */
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    /* ================= UC2: Room Abstraction ================= */
    abstract static class Room {
        String roomType;
        int price;

        Room(String roomType, int price) {
            this.roomType = roomType;
            this.price = price;
        }
    }

    static class SingleRoom extends Room {
        SingleRoom() { super("Single Room", 2500); }
    }

    static class DoubleRoom extends Room {
        DoubleRoom() { super("Double Room", 4000); }
    }

    static class SuiteRoom extends Room {
        SuiteRoom() { super("Suite Room", 7000); }
    }

    /* ================= Inventory ================= */
    static Map<String, Integer> inventory = new HashMap<>();
    static Map<String, Set<String>> allocatedRooms = new HashMap<>();

    /* ================= UC6: Room Allocation ================= */
    static Set<String> usedRoomIds = new HashSet<>();

    /* ================= UC7: Add-On Services ================= */
    static Map<String, List<String>> addonServices = new HashMap<>();

    /* ================= UC8: Booking History ================= */
    static List<String> bookingHistory = new ArrayList<>();

    /* ================= Initialization ================= */
    static {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());
    }

    /* ================= Validation ================= */
    static class Validator {

        static void validateRoomType(String type) throws InvalidBookingException {
            if (!inventory.containsKey(type)) {
                throw new InvalidBookingException("Invalid room type: " + type);
            }
        }

        static void validateAvailability(String type) throws InvalidBookingException {
            if (inventory.get(type) <= 0) {
                throw new InvalidBookingException("No rooms available for: " + type);
            }
        }
    }

    /* ================= UC6 + UC9 Booking ================= */
    static String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (usedRoomIds.size() + 1);
    }

    static void bookRoom(String type, List<String> services) {
        try {
            Validator.validateRoomType(type);
            Validator.validateAvailability(type);

            // Generate unique room ID
            String roomId;
            do {
                roomId = generateRoomId(type);
            } while (usedRoomIds.contains(roomId));

            usedRoomIds.add(roomId);

            // Allocate room
            allocatedRooms.get(type).add(roomId);
            inventory.put(type, inventory.get(type) - 1);

            // UC7: Add-on services
            addonServices.put(roomId, services);

            // UC8: Booking history
            bookingHistory.add(roomId + " | " + type + " | Services: " + services);

            System.out.println("Booking Confirmed:");
            System.out.println("Room ID: " + roomId);
            System.out.println("Type: " + type);
            System.out.println("Services: " + services);
            System.out.println("----------------------------------");

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
            System.out.println("----------------------------------");
        }
    }

    /* ================= UC8 Reporting ================= */
    static void showBookingHistory() {
        System.out.println("\n--- Booking History ---");
        for (String record : bookingHistory) {
            System.out.println(record);
        }
        System.out.println("------------------------\n");
    }

    /* ================= Main ================= */
    public static void main(String[] args) {

        System.out.println("Book My Stay App (UC1-UC9 Combined)\n");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter Room Type (or exit): ");
            String type = sc.nextLine();

            if (type.equalsIgnoreCase("exit")) break;

            System.out.print("Enter Add-on Services (comma separated): ");
            String input = sc.nextLine();

            List<String> services = new ArrayList<>();
            if (!input.isEmpty()) {
                services = Arrays.asList(input.split(","));
            }

            bookRoom(type, services);

            System.out.print("View booking history? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                showBookingHistory();
            }
        }

        sc.close();
        System.out.println("Application terminated.");
    }
}