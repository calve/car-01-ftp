package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ftpd.*;

public class FtpRequestTest {
    @Test
    public void construct() {
        FtpRequest ftpRequest = new FtpRequest(null);
        assertNotNull("ftpRequest is null ?", ftpRequest);
    }
}
