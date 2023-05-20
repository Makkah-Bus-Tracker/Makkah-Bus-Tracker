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
                System.out.println("1. Login");
                System.out.println("2. Sign up");
                System.out.println("3. Exit");
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
        mainMenu = new Menu("Main Menu");

        MenuComponent bookBusMenuItem = new MenuItem("1- Book a bus");
        MenuComponent busTrackerMenuItem = new MenuItem("2- Bus tracker");
        MenuComponent busCapacityMenuItem = new MenuItem("3- Bus capacity");
        MenuComponent logoutMenuItem = new MenuItem("4- Logout");

        mainMenu.add(bookBusMenuItem);
        mainMenu.add(busTrackerMenuItem);
        mainMenu.add(busCapacityMenuItem);
        mainMenu.add(logoutMenuItem);
    }

    void showMainMenu() throws Exception {
        BusBooking busBooking = new BusBooking(conn, userId);

        while (true) {
            mainMenu.display();
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
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
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
        System.out.println("Bus Number | Departure Station | Arrival Station | Time | Seats Available | Price");
        while (rs.next()) {
            int seatsAvailable = rs.getInt("seats_available");
            System.out.printf("%-10s | %-17s | %-15s | %-10s |%-10s | %d%n",
                    rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), rs.getInt("seats_available"),rs.getInt("Price"));
        }

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

        System.out.println("Bus Number | Departure Station | Arrival Station | Time | seats_available");
        while (rs.next()) {
            String currentStation = rs.getString("departure_station");
            System.out.printf("%-10s | %-17s | %-15s | %-15s | %-10s | %d%n",  rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"), currentStation,   rs.getString("time"),rs.getInt("seats_available"));
        }
        rs.close();
        pstmt.close();
    }



    void BusCapacityAlert() throws Exception {
        String sql = "SELECT bu.id, bu.departure_station, bu.arrival_station, bu.time, bu.seats_available FROM buses bu";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Bus Number | Departure Station | Arrival Station | Time | seats_available");
        while (rs.next()) {
            System.out.printf("%-10s | %-17s | %-15s | %-10s | %d%n",
                    rs.getInt("id"), rs.getString("departure_station"), rs.getString("arrival_station"),
                    rs.getString("time"), rs.getInt("seats_available"));
        }


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
            System.out.println(passengerCount);

            if (passengerCount <= CAPACITY) {
                System.out.println("Bus #" + busNumber + " has reached capacity.");
            } else if (passengerCount <= CAPACITY * 10) {
                System.out.println("Bus #" + busNumber + " is getting crowded. Consider waiting for the next bus.");
            }
            else {
                System.out.println("Bus #" + busNumber + " is not crowded.");
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