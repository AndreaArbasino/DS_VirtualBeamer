package elementsOfNetwork;

import elementsOfNetwork.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testEquals(){
        User user1 = new User("Alberto", "100.0.0.0");
        User user2 = new User("Alberto", "100.0.0.0");
        User user3 = new User("Andrea", "100.0.0.0");
        User user4 = new User("Andrea", "100.0.0.1");

        assertTrue(user1.equals(user2));
        assertFalse(user1.equals(user3));
        assertFalse(user3.equals(user4));
    }
}
