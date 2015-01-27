package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ftpd.*;

public class ServerTest {
    @Test
    public void construct() {
        Server server = new Server();
        assertNotNull("Server is null ?", server);
    }
}
