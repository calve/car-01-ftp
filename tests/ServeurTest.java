package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ftpd.*;

public class ServeurTest {
    @Test
    public void construct() {
        Serveur serveur = new Serveur();
        assertNotNull("Serveur is null ?", serveur);
    }
}
