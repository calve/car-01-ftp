package ftpd;

import static org.junit.Assert.*;
import org.junit.Test;

public class ServerTest {
    @Test
    public void construct() {
        Server server = new Server();
        assertNotNull("Server is null ?", server);
    }
}
