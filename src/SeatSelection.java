import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeatSelection {
    private final Connection conn;
    private final int busNumber;
    private final int userId;
    private ResultSet rs;

    SeatSelection(Connection conn, int busNumber, int userId) {
        this.conn = conn;
        this.busNumber = busNumber;
        this.userId = userId;
    }

    void selectSeats() throws Exception {
        String checkSql = "SELECT seats_available, price FROM buses WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(checkSql);
        pstmt.setInt(1, busNumber);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            int seatsAvailable = rs.getInt("seats_available");
            int price = rs.getInt("price");

            if (seatsAvailable > 0) {
                System.out.println("Available seats: " + seatsAvailable);
                System.out.print("Enter the number of seats you want to book: ");
                int numSeats = BusTracker.scanner.nextInt();
                BusTracker.scanner.nextLine();

                if (numSeats > seatsAvailable) {
                    System.out.println("Sorry, only " + seatsAvailable + " seats are available.");
                } else {
                    int total = numSeats * price;
                    System.out.println("Total amount to be paid: " + total);

                    Payment payment = new Payment(conn, userId, busNumber, numSeats, total);
                    payment.selectPaymentMethod();
                }
            } else {
                System.out.println("Sorry, the selected bus is already full.");
            }
        } else {
            System.out.println("Invalid bus number!");
        }
    }
}
