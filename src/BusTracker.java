import java.util.*;
import java.sql.*;

public class BusTracker {
    static Scanner scanner = new Scanner(System.in);

    static Statement stmt = null;
    static ResultSet rs = null;
    static int userId = 0;

    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            Statement stmt = conn.createStatement();

            UserAuth userAuth = new UserAuth(conn);
            while (true) {
                System.out.println("|------------------------------------------|");
                System.out.println(" Welcome to Makkah BusTracker Appplication!");
                System.out.println("|------------------------------------------|");
                System.out.println(" ");
                System.out.println("1. Login");
                System.out.println("2. Sign up");
                System.out.println("3. Exit");
                System.out.println(" ");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        userId = userAuth.login();
                        if (userId > 0) {
                            MainMenu mainMenu = new MainMenu(conn, userId);
                            mainMenu.showMainMenu();
                        }
                        break;
                    case 2:
                        userAuth.signup();
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static void viewBookings(Connection conn) throws SQLException {
        String sql = "SELECT b.id, u.name, bu.departure_station, bu.arrival_station, bu.time FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN buses bu ON b.bus_id = bu.id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("Booking ID | User Name | Departure Station | Arrival Station | Time");
        while (rs.next()) {
            System.out.printf("%-11s | %-9s | %-17s | %-15s | %s%n",
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"));
        }

        rs.close();
        stmt.close();
    }

}


class UserAuth {
    private final Connection conn;
    private ResultSet rs;

    UserAuth(Connection conn) {
        this.conn = conn;
    }

    int login() throws Exception {
        System.out.print("Enter your username: ");
        String userName = BusTracker.scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = BusTracker.scanner.nextLine();

        String sql = "SELECT id FROM users WHERE name = ? AND password = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userName);
        pstmt.setString(2, password);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("id");
            System.out.println("Logged in successfully!");
            return userId;
        } else {
            System.out.println("Invalid username or password!");
            return 0;
        }
    }

    void signup() throws Exception {
        System.out.print("Enter your name: ");
        String name = BusTracker.scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = BusTracker.scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = BusTracker.scanner.nextLine();

        String checkSql = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(checkSql);
        pstmt.setString(1, name);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println("Username already exists!");
        } else {
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();

            System.out.println("You have signed up successfully!");
        }
    }
}

class MainMenu {
    private final Connection conn;
    private final int userId;
    private MenuComponent mainMenu;

    MainMenu(Connection conn, int userId) {
        this.conn = conn;
        this.userId = userId;
        buildMenu();
    }

    private void buildMenu() {
        System.out.println("------------------------");
        mainMenu = new Menu("Main Menu");

        MenuComponent bookBusMenuItem = new MenuItem("1- Book a bus");
        MenuComponent busTrackerMenuItem = new MenuItem("2- Bus tracker");
        MenuComponent busCapacityMenuItem = new MenuItem("3- Bus capacity");
        MenuComponent logoutMenuItem = new MenuItem("4- View booked busses");
        MenuComponent cancelOrderMenuItem = new MenuItem("5- Cancel an order");
        MenuComponent viewBookings = new MenuItem("6- Logout");

        mainMenu.add(bookBusMenuItem);
        mainMenu.add(busTrackerMenuItem);
        mainMenu.add(busCapacityMenuItem);
        mainMenu.add(logoutMenuItem);
        mainMenu.add(cancelOrderMenuItem);
        mainMenu.add(viewBookings);
    }

    void showMainMenu() throws Exception {
        BusBooking busBooking = new BusBooking(conn, userId);

        while (true) {
            mainMenu.display();
            System.out.println("------------------------");
            System.out.print("Enter your choice: ");
            int choice = BusTracker.scanner.nextInt();
            BusTracker.scanner.nextLine();

            switch (choice) {
                case 1:
                    busBooking.bookBus();
                    break;
                case 2:
                    busBooking.busTracker();
                    break;
                case 3:
                    busBooking.BusCapacityAlert();
                    break;
                case 6:
                    return;
                case 4:
                    viewBookings(conn, userId); // Pass userId as a parameter
                    break;
                case 5:
                    cancelOrder(conn, userId); // Call the cancelOrder method
                    break;
                default:
                    System.out.println("Invalid choice! Try again...");
                    break;
            }

        }
    }
    private static void cancelOrder(Connection conn, int userId) throws SQLException {
        System.out.print("Enter the booking ID you want to cancel:");
        int bookingId = BusTracker.scanner.nextInt();
        BusTracker.scanner.nextLine();

        String sql = "SELECT b.num_seats, bu.seats_available, b.bus_id FROM bookings b " +
                "JOIN buses bu ON b.bus_id = bu.id " +
                "WHERE b.id = ? AND b.user_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, bookingId);
        pstmt.setInt(2, userId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int numSeats = rs.getInt("num_seats");
            int seatsAvailable = rs.getInt("seats_available");

            sql = "DELETE FROM bookings WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                sql = "UPDATE buses SET seats_available = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, seatsAvailable + numSeats);
                pstmt.setInt(2, rs.getInt("bus_id"));
                pstmt.executeUpdate();

                System.out.println("Booking canceled successfully. Number of seats increased.");
                System.out.println("------------------------");
            } else {
                System.out.println("Failed to cancel the booking. Please check the booking ID from Orders menu.");
                System.out.println("------------------------");
            }
        } else {
            System.out.println("Invalid booking ID or the booking does not belong to you.");
            System.out.println("------------------------");
        }


        pstmt.close();
    }

    private static void viewBookings(Connection conn, int userId) throws SQLException {
        String sql = "SELECT b.id, u.name, bu.departure_station, bu.arrival_station, bu.time, b.num_seats FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN buses bu ON b.bus_id = bu.id " +
                "WHERE b.user_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("-----------+------------+-------------------+-----------------+---------------+--------------");
        System.out.println("Booking ID | Username  | Departure Station | Arrival Station  | Time          | Booked Seats");
        System.out.println("-----------+------------+-------------------+-----------------+---------------+--------------");
        while (rs.next()) {
            System.out.printf("%-11s | %-10s | %-17s | %-15s | %-13s | %d%n",
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), rs.getInt("num_seats"));
        }
        System.out.println("-----------+------------+-------------------+-----------------+---------------+--------------");

        rs.close();
        pstmt.close();
    }
}
class BusBooking {
    private final Connection conn;
    private final int userId;
    private ResultSet rs;

    BusBooking(Connection conn, int userId) {
        this.conn = conn;
        this.userId = userId;
    }


    void bookBus() throws Exception {
        String sql = "SELECT * FROM Buses";
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        System.out.println("-----------+-------------------+-----------------+------+-----------------+-------");
        System.out.println("Bus Number | Departure Station | Arrival Station | Time | Seats Available | Price");
        System.out.println("-----------+-------------------+-----------------+------+-----------------+-------");
        while (rs.next()) {
            int seatsAvailable = rs.getInt("seats_available");
            System.out.printf("%-11s | %-17s | %-15s | %-4s | %-15s | %d%n",
                    rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), seatsAvailable, rs.getInt("price"));
        }
        System.out.println("-----------+-------------------+-----------------+------+-----------------+-------");
        System.out.println("Enter the bus number you want to book, or type '0' to go back to the main menu:");
        int busNumber = BusTracker.scanner.nextInt();
        BusTracker.scanner.nextLine();

        if (busNumber == 0) {
            return;
        }

        String checkSql = "SELECT seats_available FROM buses WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(checkSql);
        pstmt.setInt(1, busNumber);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            int seatsAvailable = rs.getInt("seats_available");

            if (seatsAvailable > 0) {

                SeatSelection seatSelection = new SeatSelection(conn, busNumber, userId);
                seatSelection.selectSeats();
            } else {
                System.out.println("Sorry, the selected bus is already full.");
            }
        } else {
            System.out.println("Invalid bus number!");

        }
    }




    void busTracker() throws Exception {
        String sql = "SELECT bu.id, bu.departure_station, bu.arrival_station, bu.time, bu.seats_available FROM buses bu";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("-----------+-------------------+-----------------+------+-----------------");
        System.out.println("Bus Number | Departure Station | Arrival Station | Time | Seats Available");
        System.out.println("-----------+-------------------+-----------------+------+-----------------");
        while (rs.next()) {
            String currentStation = rs.getString("departure_station");
            System.out.printf("%-11s | %-17s | %-15s | %-4s | %-15s%n",
                    rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), rs.getInt("seats_available"));
        }
        System.out.println("-----------+-------------------+-----------------+------+-----------------");
        rs.close();
        pstmt.close();
    }



    void BusCapacityAlert() throws Exception {
        String sql = "SELECT bu.id, bu.departure_station, bu.arrival_station, bu.time, bu.seats_available FROM buses bu";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Bus Number | Departure Station | Arrival Station | Time | Seats Available");
        System.out.println("-----------+-------------------+-----------------+------+-----------------");
        while (rs.next()) {
            System.out.printf("%-11s | %-17s | %-15s | %-4s | %d%n",
                    rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), rs.getInt("seats_available"));
        }
        System.out.println("-----------+-------------------+-----------------+------+-----------------");

        int CAPACITY = 1;
        int busNumber = 0;

        while (true) {
            System.out.print("Enter bus number (-1 to exit): ");

            busNumber = BusTracker.scanner.nextInt();
            BusTracker.scanner.nextLine();

            if (busNumber == -1) {
                break;
            }
            int passengerCount = getPassengerCount(busNumber);


            if (passengerCount <= CAPACITY) {
                System.out.println("Bus #" + busNumber + " has reached capacity.");
                System.out.println("--------------------------------------------------------------------------");
            } else if (passengerCount <= CAPACITY * 10) {
                System.out.println("Bus #" + busNumber + " is getting crowded, there are only " + (passengerCount)  + " seats, "  + "Consider waiting for the next bus.");
                System.out.println("--------------------------------------------------------------------------");
            }
            else {
                System.out.println("Bus #" + busNumber + " is not crowded, there are ("+ passengerCount + ") seats available.");
                System.out.println("--------------------------------------------------------------------------");
            }
        }
        rs.close();
        pstmt.close();

    }
    public int getPassengerCount(int busNumber) throws Exception {
        String sql = "SELECT bu.id,bu.seats_available FROM buses bu";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        int id = 0 ;
        int passengerCount = 0;

        while (rs.next()) {
            int currentBusNumber = rs.getInt("id");
            if (currentBusNumber == busNumber) {
                id = rs.getInt("id");
                passengerCount = rs.getInt("seats_available");
                break;
            }
        }

        rs.close();
        pstmt.close();
        return passengerCount;
    }

}