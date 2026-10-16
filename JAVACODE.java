import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class CinemaSeatBookingSystem {
    private static final Scanner SC = new Scanner(System.in);
    private static final int SHOWS_PER_MOVIE = 3;
    private static final int SEATS_PER_SHOW = 10;
    private static final Path BOOKINGS_FILE = Paths.get("bookings.txt");
    private static final Path MOVIES_FILE = Paths.get("movies.txt");

    private static final List<Movie> movies = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();
    private static int nextBookingId = 1;

    static class Movie {
        private final String name;
        private final String[] showTimings;
        // seats[showIndex][seatIndex] is true when booked.
        private final boolean[][] seats;

        Movie(String name, String[] showTimings) {
            this.name = name.trim();
            this.showTimings = Arrays.copyOf(showTimings, showTimings.length);
            this.seats = new boolean[SHOWS_PER_MOVIE][SEATS_PER_SHOW];
        }

        String getName() {
            return name;
        }

        String[] getShowTimings() {
            return Arrays.copyOf(showTimings, showTimings.length);
        }

        boolean isSeatBooked(int showIndex, int seatIndex) {
            return seats[showIndex][seatIndex];
        }

        boolean bookSeat(int showIndex, int seatIndex) {
            if (seats[showIndex][seatIndex]) {
                return false;
            }
            seats[showIndex][seatIndex] = true;
            return true;
        }

        int availableSeats(int showIndex) {
            int available = 0;
            for (boolean booked : seats[showIndex]) {
                if (!booked) {
                    available++;
                }
            }
            return available;
        }
    }

    static class Booking {
        private final int id;
        private final String customerName;
        private final String movieName;
        private final String showTiming;
        private final int seatNumber;

        Booking(int id, String customerName, String movieName,
                String showTiming, int seatNumber) {
            this.id = id;
            this.customerName = customerName.trim();
            this.movieName = movieName;
            this.showTiming = showTiming;
            this.seatNumber = seatNumber;
        }

        String getCustomerName() {
            return customerName;
        }

        String getMovieName() {
            return movieName;
        }

        int getSeatNumber() {
            return seatNumber;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("Booking ID: ").append(id)
                    .append(" | Customer: ").append(customerName)
                    .append(" | Movie: ").append(movieName)
                    .append(" | Show: ").append(showTiming)
                    .append(" | Seat: ").append(seatNumber)
                    .toString();
        }

        String toFileLine() {
            // Escape separators/newlines to keep one booking on one line.
            return id + "\t" + clean(customerName) + "\t" + clean(movieName)
                    + "\t" + clean(showTiming) + "\t" + seatNumber;
        }

        private static String clean(String value) {
            return value.replace("\t", " ").replace("\n", " ")
                        .replace("\r", " ").trim();
        }
    }

    public static void main(String[] args) {
        loadMoviesFromFile();
        if (movies.isEmpty()) {
            seedMovies();
            saveMoviesToFile();
        }
        loadBookingsFromFile();

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");

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
                    searchBookings();
                    break;
                case 6:
                    sortAndDisplayMovies();
                    break;
                case 7:
                    saveBookingsToFile();
                    System.out.println("Booking records saved to " + BOOKINGS_FILE.toAbsolutePath());
                    break;
                case 8:
                    saveBookingsToFile();
                    System.out.println("Thank you! Enjoy your movie.");
                    break;
                default:
                    System.out.println("Invalid menu choice. Please choose 1-8.");
            }
        } while (choice != 8);

        SC.close();
    }

    private static void seedMovies() {
        movies.add(new Movie("Avengers",
                new String[]{"10:00 AM", "2:00 PM", "6:00 PM"}));
        movies.add(new Movie("Avatar",
                new String[]{"11:00 AM", "3:00 PM", "7:00 PM"}));
        movies.add(new Movie("Inception",
                new String[]{"9:00 AM", "1:00 PM", "5:00 PM"}));
    }

    private static void printMenu() {
        System.out.println("\n========== CINEMA SEAT BOOKING SYSTEM ==========");
        System.out.println("1. View Movies and Show Timings");
        System.out.println("2. Book Ticket");
        System.out.println("3. View Booking Details");
        System.out.println("4. Admin - Add Movie");
        System.out.println("5. Search Bookings");
        System.out.println("6. Display Movies Alphabetically");
        System.out.println("7. Save Bookings to File");
        System.out.println("8. Exit");
    }

    private static void viewMovies() {
        System.out.println("\n--------------- AVAILABLE MOVIES ---------------");
        if (movies.isEmpty()) {
            System.out.println("No movies are available.");
            return;
        }

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            System.out.println((i + 1) + ". " + movie.getName());
            String[] timings = movie.getShowTimings();
            for (int j = 0; j < timings.length; j++) {
                System.out.println("   " + (j + 1) + ". " + timings[j]
                        + " | Available seats: " + movie.availableSeats(j));
            }
        }
    }

    private static void bookTicket() {
        if (movies.isEmpty()) {
            System.out.println("No movies are available to book.");
            return;
        }

        viewMovies();
        int movieNumber = readInt("Choose movie number: ");
        if (movieNumber < 1 || movieNumber > movies.size()) {
            System.out.println("Invalid movie selection.");
            return;
        }
        Movie movie = movies.get(movieNumber - 1);

        System.out.println("\nShow timings for " + movie.getName() + ":");
        String[] timings = movie.getShowTimings();
        for (int i = 0; i < timings.length; i++) {
            System.out.println((i + 1) + ". " + timings[i]);
        }

        int showNumber = readInt("Choose show number (1-3): ");
        if (showNumber < 1 || showNumber > SHOWS_PER_MOVIE) {
            System.out.println("Invalid show timing.");
            return;
        }
        int showIndex = showNumber - 1;

        displaySeats(movie, showIndex);
        int seatNumber = readInt("Choose seat number (1-10): ");
        if (seatNumber < 1 || seatNumber > SEATS_PER_SHOW) {
            System.out.println("Invalid seat number.");
            return;
        }
        int seatIndex = seatNumber - 1;

        if (movie.isSeatBooked(showIndex, seatIndex)) {
            System.out.println("Sorry, that seat is already booked.");
            return;
        }

        String customerName = readNonEmptyLine("Enter customer name: ");
        if (!movie.bookSeat(showIndex, seatIndex)) {
            // Defensive check in case booking logic is changed later.
            System.out.println("Sorry, that seat is no longer available.");
            return;
        }

        Booking booking = new Booking(nextBookingId++, customerName,
                movie.getName(), timings[showIndex], seatNumber);
        bookings.add(booking);

        System.out.println("\nBooking successful!");
        System.out.println(booking);
        saveBookingsToFile();
    }

    private static void displaySeats(Movie movie, int showIndex) {
        System.out.println("\nSeat status for " + movie.getName()
                + " at " + movie.getShowTimings()[showIndex]);
        System.out.println("A = AVAILABLE, B = BOOKED");
        for (int i = 0; i < SEATS_PER_SHOW; i++) {
            System.out.printf("Seat %2d: %-9s%s",
                    i + 1,
                    movie.isSeatBooked(showIndex, i) ? "BOOKED" : "AVAILABLE",
                    (i + 1) % 2 == 0 ? "\n" : "   ");
        }
        System.out.println();
    }

    private static void viewBookings() {
        System.out.println("\n---------------- BOOKING DETAILS ----------------");
        if (bookings.isEmpty()) {
            System.out.println("No bookings recorded yet.");
            return;
        }

        // CO6: Consumer functional interface and lambda expression.
        Consumer<Booking> printer = booking -> System.out.println(booking);
        bookings.forEach(printer);
    }

    private static void addMovie() {
        System.out.println("\n---------------- ADMIN: ADD MOVIE ----------------");
        String name = readNonEmptyLine("Enter new movie name: ");

        boolean duplicate = movies.stream()
                .anyMatch(movie -> movie.getName().equalsIgnoreCase(name));
        if (duplicate) {
            System.out.println("A movie with that name already exists.");
            return;
        }

        String[] timings = new String[SHOWS_PER_MOVIE];
        for (int i = 0; i < SHOWS_PER_MOVIE; i++) {
            timings[i] = readNonEmptyLine("Enter show timing " + (i + 1) + ": ");
        }

        // Movie creates its own 3 x 10 seat-status array.
        movies.add(new Movie(name, timings));
        saveMoviesToFile();
        System.out.println("Movie added successfully!");
        System.out.println("Movie: " + name);
        System.out.println("Show timings: " + String.join(", ", timings));
        System.out.println("Each show has " + SEATS_PER_SHOW + " available seats.");
    }

    private static void searchBookings() {
        System.out.println("\n---------------- SEARCH BOOKINGS ----------------");
        String query = readNonEmptyLine(
                "Enter customer name or movie name to search: ").toLowerCase();

        // CO6: Predicate + lambda + Stream filter.
        Predicate<Booking> matches = booking ->
                booking.getCustomerName().toLowerCase().contains(query)
                || booking.getMovieName().toLowerCase().contains(query);

        List<Booking> results = bookings.stream()
                .filter(matches)
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No matching bookings found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void sortAndDisplayMovies() {
        System.out.println("\n------------ MOVIES (ALPHABETICAL) -------------");
        movies.stream()
                .sorted(Comparator.comparing(
                        Movie::getName, String.CASE_INSENSITIVE_ORDER))
                .forEach(movie -> System.out.println(movie.getName()));
    }

    private static void saveMoviesToFile() {
        List<String> lines = movies.stream()
                .map(movie -> movie.getName() + "\\t"
                        + String.join("\\t", movie.getShowTimings()))
                .collect(Collectors.toList());
        try {
            Files.write(MOVIES_FILE, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not save movies: " + e.getMessage());
        }
    }

    private static void loadMoviesFromFile() {
        if (!Files.exists(MOVIES_FILE)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(MOVIES_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split("\\t", -1);
                if (parts.length == SHOWS_PER_MOVIE + 1 && !parts[0].trim().isEmpty()) {
                    movies.add(new Movie(parts[0],
                            new String[]{parts[1], parts[2], parts[3]}));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load movies: " + e.getMessage());
        }
    }

    private static void saveBookingsToFile() {
        List<String> lines = bookings.stream()
                .map(Booking::toFileLine)
                .collect(Collectors.toList());
        try {
            Files.write(BOOKINGS_FILE, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Could not save bookings: " + e.getMessage());
        }
    }

    private static void loadBookingsFromFile() {
        if (!Files.exists(BOOKINGS_FILE)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(BOOKINGS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split("\\t", -1);
                if (parts.length != 5) {
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    int seat = Integer.parseInt(parts[4]);
                    Booking booking = new Booking(id, parts[1], parts[2], parts[3], seat);
                    bookings.add(booking);
                    nextBookingId = Math.max(nextBookingId, id + 1);

                    // Restore seat status so a saved seat cannot be booked again.
                    for (Movie movie : movies) {
                        if (movie.getName().equalsIgnoreCase(parts[2])) {
                            String[] timings = movie.getShowTimings();
                            for (int show = 0; show < timings.length; show++) {
                                if (timings[show].equalsIgnoreCase(parts[3])
                                        && seat >= 1 && seat <= SEATS_PER_SHOW) {
                                    movie.bookSeat(show, seat - 1);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                } catch (NumberFormatException ignored) {
                    System.out.println("Skipped an invalid saved booking record.");
                }
            }
            if (!bookings.isEmpty()) {
                System.out.println("Loaded " + bookings.size()
                        + " saved booking record(s) from file.");
            }
        } catch (IOException e) {
            System.out.println("Could not load previous bookings: " + e.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SC.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }
}
