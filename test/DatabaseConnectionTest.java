import org.junit.Test;
import java.sql.Connection;
import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    @Test
    public void testGetInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testGetConnection() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection connection = instance.getConnection();
        assertNotNull(connection);
    }

    @Test
    public void testClose() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        Connection connection = instance.getConnection();
        instance.close();
        try {
            assertTrue(connection.isClosed());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}