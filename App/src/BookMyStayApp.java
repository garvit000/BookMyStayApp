import java.util.*;

public class BookMyStayApp {

    static class Hotel {
        int id;
        String name;
        String city;
        int pricePerNight;

        Hotel(int id, String name, String city, int pricePerNight) {
            this.id = id;
            this.name = name;
            this.city = city;
            this.pricePerNight = pricePerNight;
        }
    }

    static class Booking {
        String userName;
        Hotel hotel;
        int nights;
        int totalAmount;

        Booking(String userName, Hotel hotel, int nights) {
            this.userName = userName;
            this.hotel = hotel;
            this.nights = nights;
            this.totalAmount = nights * hotel.pricePerNight;
        }
    }

    private List<Hotel> hotels = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        BookMyStayApp app = new BookMyStayApp();
        app.initializeHotels();
        app.start();
    }

    private void initializeHotels() {
        hotels.add(new Hotel(1, "Sea View Residency", "Chennai", 3000));
        hotels.add(new Hotel(2, "Grand Palace", "Chennai", 4500));
        hotels.add(new Hotel(3, "Hill Top Hotel", "Ooty", 3500));
        hotels.add(new Hotel(4, "Lake View Resort", "Ooty", 5000));
    }

    private void start() {
        System.out.println("=== Welcome to BookMyStay ===");

        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();

        System.out.print("Enter city to search hotels: ");
        String city = scanner.nextLine();

        List<Hotel> results = searchHotels(city);

        if (results.isEmpty()) {
            System.out.println("No hotels found.");
            return;
        }

        System.out.println("\nAvailable Hotels:");
        for (Hotel h : results) {
            System.out.println(h.id + ". " + h.name + " - ₹" + h.pricePerNight + " per night");
        }

        System.out.print("\nSelect hotel ID: ");
        int hotelId = scanner.nextInt();

        Hotel selectedHotel = getHotelById(hotelId);

        if (selectedHotel == null) {
            System.out.println("Invalid hotel selection.");
            return;
        }

        System.out.print("Enter number of nights: ");
        int nights = scanner.nextInt();

        Booking booking = new Booking(userName, selectedHotel, nights);
        bookings.add(booking);

        System.out.println("\n=== Booking Confirmation ===");
        System.out.println("Guest: " + booking.userName);
        System.out.println("Hotel: " + booking.hotel.name);
        System.out.println("City: " + booking.hotel.city);
        System.out.println("Nights: " + booking.nights);
        System.out.println("Total Amount: ₹" + booking.totalAmount);
        System.out.println("Booking Successful!");
    }

    private List<Hotel> searchHotels(String city) {
        List<Hotel> result = new ArrayList<>();
        for (Hotel h : hotels) {
            if (h.city.equalsIgnoreCase(city)) {
                result.add(h);
            }
        }
        return result;
    }

    private Hotel getHotelById(int id) {
        for (Hotel h : hotels) {
            if (h.id == id) {
                return h;
            }
        }
        return null;
    }
}