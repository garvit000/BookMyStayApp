import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * BookMyStayApp - Integrated System (UC1 → UC12)
 */

public class BookMyStayApp implements Serializable {

    /* ================= UC9: Custom Exception ================= */
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    /* ================= UC2: Room Hierarchy ================= */
    abstract static class Room implements Serializable {
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

    /* ================= Shared System State ================= */
    static Map<String, Integer> inventory = new HashMap<>();
    static Set<String> allocatedRooms = new HashSet<>();
    static Map<String, String> roomTypeMap = new HashMap<>();
    static Map<String, List<String>> addonServices = new HashMap<>();
    static List<String> bookingHistory = new ArrayList<>();
    static Stack<String> rollbackStack = new Stack<>();

    /* ================= UC11 Concurrency ================= */
    static Queue<BookingRequest> bookingQueue = new LinkedList<>();
    static final Object queueLock = new Object();
    static final Object inventoryLock = new Object();

    /* ================= UC12 Persistence ================= */
    static final String FILE_NAME = "bookmystay_state.dat";

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

    static int counter = 1;

    /* ================= Initialization ================= */
    static {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    /* ================= Validation ================= */
    static class Validator {
        static void validateRoom(String type) throws InvalidBookingException {
            if (!inventory.containsKey(type)) {
                throw new InvalidBookingException("Invalid room type");
            }
        }

        static void validateAvailability(String type) throws InvalidBookingException {
            if (inventory.get(type) <= 0) {
                throw new InvalidBookingException("No rooms available");
            }
        }
    }

    /* ================= Generate Room ID ================= */
    static String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + (counter++);
    }

    /* ================= Booking Logic ================= */
    static synchronized String bookRoom(String type, List<String> services)
            throws InvalidBookingException {

        Validator.validateRoom(type);
        Validator.validateAvailability(type);

        String roomId;
        do {
            roomId = generateRoomId(type);
        } while (allocatedRooms.contains(roomId));

        allocatedRooms.add(roomId);
        roomTypeMap.put(roomId, type);
        addonServices.put(roomId, services);

        inventory.put(type, inventory.get(type) - 1);

        bookingHistory.add("BOOKED: " + roomId + " | " + type + " | " + services);

        System.out.println("Booked: " + roomId);
        return roomId;
    }

    /* ================= Cancellation (UC10) ================= */
    static synchronized void cancelBooking(String roomId)
            throws InvalidBookingException {

        if (!roomTypeMap.containsKey(roomId)) {
            throw new InvalidBookingException("Reservation not found");
        }

        if (!allocatedRooms.contains(roomId)) {
            throw new InvalidBookingException("Already cancelled");
        }

        String type = roomTypeMap.get(roomId);

        allocatedRooms.remove(roomId);
        inventory.put(type, inventory.get(type) + 1);

        rollbackStack.push(roomId);

        bookingHistory.add("CANCELLED: " + roomId);

        System.out.println("Cancelled: " + roomId);
    }

    /* ================= Booking Queue (UC11) ================= */
    static void addRequest(BookingRequest req) {
        synchronized (queueLock) {
            bookingQueue.add(req);
        }
    }

    static void processQueue() {
        while (true) {
            BookingRequest req;

            synchronized (queueLock) {
                if (bookingQueue.isEmpty()) break;
                req = bookingQueue.poll();
            }

            try {
                bookRoom(req.roomType, req.services);
            } catch (InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static class Worker implements Runnable {
        public void run() {
            processQueue();
        }
    }

    /* ================= Persistence (UC12) ================= */
    static void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(allocatedRooms);
            oos.writeObject(roomTypeMap);
            oos.writeObject(addonServices);
            oos.writeObject(bookingHistory);
            System.out.println("State saved.");
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    static void loadState() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            System.out.println("No saved state found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            inventory = (Map<String, Integer>) ois.readObject();
            allocatedRooms = (Set<String>) ois.readObject();
            roomTypeMap = (Map<String, String>) ois.readObject();
            addonServices = (Map<String, List<String>>) ois.readObject();
            bookingHistory = (List<String>) ois.readObject();

            System.out.println("State restored.");
        } catch (Exception e) {
            System.out.println("Recovery failed. Starting fresh.");
        }
    }

    /* ================= Display ================= */
    static void showInventory() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }

    static void showHistory() {
        System.out.println("\nHistory:");
        for (String h : bookingHistory) {
            System.out.println(h);
        }
    }

    /* ================= MAIN ================= */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Load persisted data
        loadState();

        while (true) {

            System.out.println("\nBook My Stay App");
            System.out.println("1. Add Booking Request");
            System.out.println("2. Process Bookings (Concurrent)");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Inventory");
            System.out.println("5. View History");
            System.out.println("6. Save & Exit");

            System.out.print("Choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            try {
                switch (choice) {

                    case 1:
                        System.out.print("Guest: ");
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
                        System.out.print("Room ID: ");
                        String id = sc.nextLine();
                        cancelBooking(id);
                        break;

                    case 4:
                        showInventory();
                        break;

                    case 5:
                        showHistory();
                        break;

                    case 6:
                        saveState();
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}