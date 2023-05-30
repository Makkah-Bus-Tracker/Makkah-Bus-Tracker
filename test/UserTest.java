import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import static org.junit.Assert.*;


public class UserTest {

    @Test
    public void testGetId() {
        User user = new User(1, "John", "john@example.com", "password");
        int id = user.getId();
        assertEquals(1, id);
    }

    @Test
    public void testGetName() {
        User user = new User(1, "John", "john@example.com", "password");
        String name = user.getName();
        assertEquals("John", name);
    }

    @Test
    public void testGetEmail() {
        User user = new User(1, "John", "john@example.com", "password");
        String email = user.getEmail();
        assertEquals("john@example.com", email);
    }

    @Test
    public void testGetPassword() {
        User user = new User(1, "John", "john@example.com", "password");
        String password = user.getPassword();
        assertEquals("password", password);
    }

    @Test
    public void testLogin() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", "123456");
        String input = "testuser\ntestpassword\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);
        User loggedInUser = User.login(conn,scanner);
        assertNull(loggedInUser);
    }

    @Test
    public void testSignup() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", "123456");
        String input = "Test User\ntest@example.com\ntestpassword\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);
        boolean signupSuccess = User.signup(conn, scanner);
        assertFalse(signupSuccess);
    }
}