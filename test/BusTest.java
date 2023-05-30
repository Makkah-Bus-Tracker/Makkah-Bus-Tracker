import org.junit.Test;
import static org.junit.Assert.*;

public class BusTest {

    @Test
    public void testGetId() {
        Bus bus = new Bus(1, "Departure Station", "Arrival Station", "10:00 AM", 50);
        int id = bus.getId();
        assertEquals(1, id);
    }

    @Test
    public void testGetDepartureStation() {
        Bus bus = new Bus(1, "Departure Station", "Arrival Station", "10:00 AM", 50);
        String departureStation = bus.getDepartureStation();
        assertEquals("Departure Station", departureStation);
    }

    @Test
    public void testGetArrivalStation() {
        Bus bus = new Bus(1, "Departure Station", "Arrival Station", "10:00 AM", 50);
        String arrivalStation = bus.getArrivalStation();
        assertEquals("Arrival Station", arrivalStation);
    }

    @Test
    public void testGetTime() {
        Bus bus = new Bus(1, "Departure Station", "Arrival Station", "10:00 AM", 50);
        String time = bus.getTime();
        assertEquals("10:00 AM", time);
    }

    @Test
    public void testGetSeatsAvailable() {
        Bus bus = new Bus(1, "Departure Station", "Arrival Station", "10:00 AM", 50);
        int seatsAvailable = bus.getSeatsAvailable();
        assertEquals(50, seatsAvailable);
    }
}