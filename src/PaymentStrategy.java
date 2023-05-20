import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

interface PaymentStrategy {
    void processPayment();
}

class CreditCardPayment implements PaymentStrategy {
    private final Connection conn;
    private final int userId;
    private final int busNumber;
    private final int numSeats;

    CreditCardPayment(Connection conn, int userId, int busNumber, int numSeats) {
        this.conn = conn;
        this.userId = userId;
        this.busNumber = busNumber;
        this.numSeats = numSeats;
    }

    @Override
    public void processPayment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("DONT Enter Card number");
        String card = scanner.nextLine();
        System.out.println("DONT Enter the name on Card ");
        String name = scanner.nextLine();
        System.out.println("DONT Enter the expire date ");
        String date = scanner.nextLine();
        System.out.println("DONT Enter the security code ");
        int code = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        paymentCompleted();
    }

    private void paymentCompleted() {
        try {
            String updateSql = "UPDATE buses SET seats_available = seats_available - ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, numSeats);
            pstmt.setInt(2, busNumber);
            pstmt.executeUpdate();

            String insertSql = "INSERT INTO bookings (user_id, bus_id, num_seats) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, busNumber);
            pstmt.setInt(3, numSeats);
            pstmt.executeUpdate();

            System.out.println("Bus booked successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class PayPalPayment implements PaymentStrategy {
    private final Connection conn;
    private final int userId;
    private final int busNumber;
    private final int numSeats;

    PayPalPayment(Connection conn, int userId, int busNumber, int numSeats) {
        this.conn = conn;
        this.userId = userId;
        this.busNumber = busNumber;
        this.numSeats = numSeats;
    }

    @Override
    public void processPayment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("DONT Enter the Email");
        String email = scanner.nextLine();
        System.out.println("DONT Enter the password ");
        String password = scanner.nextLine();

        paymentCompleted();
    }

    private void paymentCompleted() {
        try {
            String updateSql = "UPDATE buses SET seats_available = seats_available - ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, numSeats);
            pstmt.setInt(2, busNumber);
            pstmt.executeUpdate();

            String insertSql = "INSERT INTO bookings (user_id, bus_id, num_seats) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, busNumber);
            pstmt.setInt(3, numSeats);
            pstmt.executeUpdate();

            System.out.println("Bus booked successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Payment {
    private final Connection conn;
    private final int userId;
    private final int busNumber;
    private final int numSeats;
    private final int total;

    Payment(Connection conn, int userId, int busNumber, int numSeats, int total) {
        this.conn = conn;
        this.userId = userId;
        this.busNumber = busNumber;
        this.numSeats = numSeats;
        this.total = total;
    }

    void selectPaymentMethod() throws IllegalArgumentException {
        System.out.println("Payment methods:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.print("Enter the number of the payment method: ");
        int paymentMethod = BusTracker.scanner.nextInt();
        BusTracker.scanner.nextLine();

        PaymentStrategy strategy;

        if (paymentMethod == 1) {
            strategy = new CreditCardPayment(conn, userId, busNumber, numSeats);
        } else if (paymentMethod == 2) {
            strategy = new PayPalPayment(conn, userId, busNumber, numSeats);
        } else {
            System.out.println("Invalid payment method");
            return;
        }

        strategy.processPayment();
    }
}
