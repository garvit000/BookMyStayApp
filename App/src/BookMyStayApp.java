import java.util.*;
import java.util.concurrent.*;

/**
 * BookMyStayApp
 * Combined Use Cases UC1 → UC11
 */

public class BookMyStayApp {

    /* ================= UC9: Custom Exception ================= */
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    /* ================= Room Modeling (UC2) ================= */
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

    /* ================= Shared State ================= */
    static Map<String, Integer> inventory = new HashMap<>();
    static Set<String> allocatedRoomIds = new HashSet<>();
    static Map<String, String> roomToType = new HashMap<>();
    static Map<String, List<String>> addonServices = new HashMap<>();
    static List<String> bookingHistory = new ArrayList<>();
    static Stack<String> releasedRoomsStack = new Stack<>();

    /* ================= UC11 Concurrency ================= */
    static Queue<BookingRequest> bookingQueue = new LinkedList<>();
    static final Object queueLock = new Object();
    static final Object inventoryLock = new Object();

    static int counter = 1;

    /* ================= Booking Request ================= */
    static class BookingRequest {
        String guest;
        String roomType;
        List<String> services;

        BookingRequest(String guest, String roomType, List<String> services) {
            this.guest = guest;
            this.roomType = roomType;
            this.services = services;
        }
    }

    /* ================= Initialization ================= */
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

    /* ================= Core Booking Logic ================= */
    static synchronized String bookRoom(String type, List<String> services) throws InvalidBookingException {

        Validator.validateRoomType(type);
        Validator.validateAvailability(type);

        String roomId;

        do {
            roomId = generateRoomId(type);
        } while (allocatedRoomIds.contains(roomId));

        allocatedRoomIds.add(roomId);
        roomToType.put(roomId, type);
        addonServices.put(roomId, services);

        inventory.put(type, inventory.get(type) - 1);

        bookingHistory.add("BOOKED: " + roomId + " | " + type + " | " + services);

        System.out.println("BOOKED: " + roomId + " (" + type + ")");
        return roomId;
    }

    /* ================= Cancellation (UC10) ================= */
    static synchronized void cancelBooking(String roomId) throws InvalidBookingException {

        if (!roomToType.containsKey(roomId)) {
            throw new InvalidBookingException("Booking does not exist");
        }

        if (!allocatedRoomIds.contains(roomId)) {
            throw new InvalidBookingException("Booking already cancelled");
        }

        String type = roomToType.get(roomId);

        allocatedRoomIds.remove(roomId);
        inventory.put(type, inventory.get(type) + 1);

        releasedRoomsStack.push(roomId);

        bookingHistory.add("CANCELLED: " + roomId + " | " + type);

        System.out.println("CANCELLED: " + roomId);
    }

    /* ================= History ================= */
    static void showHistory() {
        System.out.println("\n--- Booking History ---");
        for (String h : bookingHistory) {
            System.out.println(h);
        }
        System.out.println("----------------------\n");
    }

    /* ================= Inventory ================= */
    static void showInventory() {
        System.out.println("\n--- Inventory ---");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        System.out.println("-----------------\n");
    }

    /* ================= Add Request (UC11 Queue) ================= */
    static void addRequest(BookingRequest request) {
        synchronized (queueLock) {
            bookingQueue.add(request);
        }
    }

    /* ================= Process Requests ================= */
    static void processRequests() {
        while (true) {
            BookingRequest req = null;

            synchronized (queueLock) {
                if (!bookingQueue.isEmpty()) {
                    req = bookingQueue.poll();
                }
            }

            if (req == null) break;

            try {
                bookRoom(req.roomType, req.services);
            } catch (InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /* ================= Worker Thread ================= */
    static class Worker implements Runnable {
        public void run() {
            processRequests();
        }
    }

    /* ================= Main ================= */
    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("Book My Stay App (UC1–UC11)\n");

        while (true) {
            System.out.println("1. Add Booking Request");
            System.out.println("2. Process Concurrent Bookings");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View History");
            System.out.println("5. View Inventory");
            System.out.println("6. Exit");

            System.out.print("Choose: ");
            int choice = Integer.parseInt(sc.nextLine());

            try {

                switch (choice) {

                    case 1:
                        System.out.print("Guest Name: ");
                        String guest = sc.nextLine();

                        System.out.print("Room Type: ");
                        String type = sc.nextLine();

                        System.out.print("Services (comma separated): ");
                        String input = sc.nextLine();

                        List<String> services = input.isEmpty()
                                ? new ArrayList<>()
                                : Arrays.asList(input.split(","));

                        addRequest(new BookingRequest(guest, type, services));
                        break;

                    case 2:
                        Thread t1 = new Thread(new Worker());
                        Thread t2 = new Thread(new Worker());

                        t1.start();
                        t2.start();

                        t1.join();
                        t2.join();
                        break;

                    case 3:
                        System.out.print("Enter Room ID to cancel: ");
                        String id = sc.nextLine();
                        cancelBooking(id);
                        break;

                    case 4:
                        showHistory();
                        break;

                    case 5:
                        showInventory();
                        break;

                    case 6:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice");
                }

            } catch (InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("-----------------------------------");
        }
    }
}