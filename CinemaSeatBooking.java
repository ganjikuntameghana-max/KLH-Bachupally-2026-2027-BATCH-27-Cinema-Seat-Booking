 import java.util.*;

public class CinemaSeatBooking {

    static Scanner sc = new Scanner(System.in);

    // Movie details
    static String[] movies = {"Avengers", "Interstellar", "Inception"};
    static String[][] showTimings = {
            {"10:00 AM", "2:00 PM", "6:00 PM"},
            {"11:00 AM", "3:00 PM", "7:00 PM"},
            {"12:00 PM", "4:00 PM", "8:00 PM"}
    };

    // 10 seats for each movie
    static boolean[][] seats = new boolean[3][10];

    // Booking details
    static ArrayList<String> bookings = new ArrayList<>();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n=================================");
            System.out.println("     CINEMA SEAT BOOKING SYSTEM");
            System.out.println("=================================");
            System.out.println("1. View Movies");
            System.out.println("2. Book Ticket");
            System.out.println("3. View Booking Details");
            System.out.println("4. Admin - Add Movie");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewMovies();
                    break;

                case 2:
                    bookTicket();
                    break;

                case 3:
                    viewBookings();
                    break;

                case 4:
                    addMovie();
                    break;

                case 5:
                    System.out.println("Thank you for using the Cinema Seat Booking System!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 5);
    }

    // Display movies and show timings
    static void viewMovies() {

        System.out.println("\n----- AVAILABLE MOVIES -----");

        for (int i = 0; i < movies.length; i++) {

            if (movies[i] != null) {
                System.out.println("\n" + (i + 1) + ". " + movies[i]);

                System.out.println("Show Timings:");

                for (String time : showTimings[i]) {
                    System.out.println("   " + time);
                }
            }
        }
    }

    // Book a ticket
    static void bookTicket() {

        viewMovies();

        System.out.print("\nSelect movie number: ");
        int movieChoice = sc.nextInt();

        if (movieChoice < 1 || movieChoice >  movies.length ||
                movies[movieChoice - 1] == null) {
            System.out.println("Invalid movie selection.");
            return;
        }

        int movieIndex = movieChoice - 1;

        System.out.println("\nShow Timings:");

        for (int i = 0; i < showTimings[movieIndex].length; i++) {
            System.out.println((i + 1) + ". " + showTimings[movieIndex][i]);
        }

        System.out.print("Select show timing: ");
        int timeChoice = sc.nextInt();

        if (timeChoice < 1 ||  timeChoice > 3) {
            System.out.println("Invalid timing.");
            return;
        }

        // Display seat availability
        System.out.println("\n----- SEAT AVAILABILITY -----");

        for (int i = 0; i < seats[movieIndex].length; i++) {

            if (seats[movieIndex][i]) {
                System.out.println("Seat " + (i + 1) + " : BOOKED");
            } else {
                System.out.println("Seat " + (i + 1) + " : AVAILABLE");
            }
        }

        System.out.print("\nEnter seat number: ");
        int seatNumber = sc.nextInt();

        if (seatNumber < 1 ||  seatNumber > 10) {
            System.out.println("Invalid seat number.");
            return;
        }

        // Check if seat is already booked
        if (seats[movieIndex][seatNumber - 1]) {

            System.out.println("Sorry! This seat is already booked.");

        } else {

            seats[movieIndex][seatNumber - 1] = true;
 System.out.println("\nEnter customer name:");
            sc.nextLine();
            String customerName = sc.nextLine();

            String booking = "Customer: " + customerName
                    + " | Movie: " + movies[movieIndex]
                    + " | Show: " + showTimings[movieIndex][timeChoice - 1]
                    + " | Seat: " + seatNumber;

            bookings.add(booking);

            System.out.println("\n***** BOOKING SUCCESSFUL *****");
            System.out.println(booking);
        }
    }

    // Display all bookings
    static void viewBookings() {

        System.out.println("\n----- BOOKING DETAILS -----");

        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {

            for (int i = 0; i < bookings.size(); i++) {
                System.out.println((i + 1) + ". " + bookings.get(i));
            }
        }
    }

    // Admin function to add a movie
    static void addMovie() {

        System.out.print("\nEnter new movie name: ");
        String movieName = sc.nextLine();

        System.out.println("Movie added successfully!");

        // Displaying the movie name as confirmation
        System.out.println("Movie: " + movieName);
    }
}