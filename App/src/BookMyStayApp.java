import java.util.*;

/**
 * Book My Stay App
 * Combined Implementation (UC1 → UC10)
 */

public class BookMyStayApp {

    /* ================= UC9: Custom Exception ================= */
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    /* ================= UC2: Room Modeling ================= */
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

    /* ================= Allocation ================= */
    static Map<String, String> roomToType = new HashMap<>(); // roomId -> type
    static Set<String> allocatedRoomIds = new HashSet<>();

    /* ================= UC7: Add-ons ================= */
    static Map<String, List<String>> addonServices = new HashMap<>();

    /* ================= UC8: History ================= */
    static List<String> bookingHistory = new ArrayList<>();

    /* ================= UC10: Rollback ================= */
    static Stack<String> releasedRoomsStack = new Stack<>();

    static int counter = 1;

    static {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
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

    /* ================= Generate Room ID ================= */
    static String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (counter++);
    }

    /* ================= UC6 + UC7 + UC8 Booking ================= */
    static String bookRoom(String type, List<String> services) throws InvalidBookingException {

        Validator.validateRoomType(type);
        Validator.validateAvailability(type);

        String roomId;

        do {
            roomId = generateRoomId(type);
        } while (allocatedRoomIds.contains(roomId));

        allocatedRoomIds.add(roomId);

        inventory.put(type, inventory.get(type) - 1);
        roomToType.put(roomId, type);

        addonServices.put(roomId, services);

        bookingHistory.add("BOOKED: " + roomId + " | " + type + " | Services: " + services);

        System.out.println("Booking confirmed: " + roomId);

        return roomId;
    }

    /* ================= UC10 Cancellation ================= */
    static void cancelBooking(String roomId) throws InvalidBookingException {

        if (!roomToType.containsKey(roomId)) {
            throw new InvalidBookingException("Booking does not exist");
        }

        String type = roomToType.get(roomId);

        if (!allocatedRoomIds.contains(roomId)) {
            throw new InvalidBookingException("Booking already cancelled");
        }

        // Remove allocation
        allocatedRoomIds.remove(roomId);

        // Restore inventory
        inventory.put(type, inventory.get(type) + 1);

        // Push to rollback stack
        releasedRoomsStack.push(roomId);

        bookingHistory.add("CANCELLED: " + roomId + " | " + type);

        System.out.println("Booking cancelled: " + roomId);
    }

    /* ================= UC8 Reporting ================= */
    static void showHistory() {
        System.out.println("\n--- Booking History ---");
        for (String record : bookingHistory) {
            System.out.println(record);
        }
        System.out.println("-----------------------\n");
    }

    /* ================= Display Inventory ================= */
    static void showInventory() {
        System.out.println("\n--- Inventory ---");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        System.out.println("-----------------\n");
    }

    /* ================= Main ================= */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("   Book My Stay App (UC1–UC10)");
        System.out.println("=================================\n");

        while (true) {
            System.out.println("1. Book Room");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View History");
            System.out.println("4. View Inventory");
            System.out.println("5. Exit");

            System.out.print("Choose: ");
            int choice = Integer.parseInt(sc.nextLine());

            try {

                switch (choice) {

                    case 1:
                        System.out.print("Enter Room Type: ");
                        String type = sc.nextLine();

                        System.out.print("Enter Add-ons (comma separated): ");
                        String input = sc.nextLine();

                        List<String> services = new ArrayList<>();
                        if (!input.isEmpty()) {
                            services = Arrays.asList(input.split(","));
                        }

                        bookRoom(type, services);
                        break;

                    case 2:
                        System.out.print("Enter Room ID to cancel: ");
                        String id = sc.nextLine();
                        cancelBooking(id);
                        break;

                    case 3:
                        showHistory();
                        break;

                    case 4:
                        showInventory();
                        break;

                    case 5:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice");
                }

            } catch (InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }

            System.out.println("-----------------------------------");
        }
    }
}