import org.junit.Test;
import static org.junit.Assert.*;

public class BookingTest {

    @Test
    public void testGetId() {
        Booking booking = new Booking(1, 123, 456);
        int id = booking.getId();
        assertEquals(1, id);
    }

    @Test
    public void testGetUserId() {
        Booking booking = new Booking(1, 123, 456);
        int userId = booking.getUserId();
        assertEquals(123, userId);
    }

    @Test
    public void testGetBusId() {
        Booking booking = new Booking(1, 123, 456);
        int busId = booking.getBusId();
        assertEquals(456, busId);
    }
}