/**
 * BookMyStay Application
 * Use Case: Room Modeling using Abstraction and Inheritance
 *
 * Demonstrates object modeling using an abstract Room class and
 * concrete implementations for different room types.
 *
 * @author Garvit
 * @version 1.0
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
        System.out.println("Room Size: " + size + " sq ft");
        System.out.println("Price per Night: ₹" + pricePerNight);
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

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("        Book My Stay App");
        System.out.println("     Room Availability System");
        System.out.println("=================================\n");

        /* Creating room objects (Polymorphism) */
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        /* Static availability variables */
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable = 2;

        System.out.println("---- Room Details ----\n");

        single.displayRoomDetails();
        System.out.println("Available Rooms: " + singleRoomAvailable);
        System.out.println("---------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + doubleRoomAvailable);
        System.out.println("---------------------------");

        suite.displayRoomDetails();
        System.out.println("Available Rooms: " + suiteRoomAvailable);
        System.out.println("---------------------------");

        System.out.println("\nApplication terminated.");
    }
}