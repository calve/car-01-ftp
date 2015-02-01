package ftpd;

import static org.junit.Assert.*;
import org.junit.Test;

public class FtpRequestTest {
    @Test
    public void construct() {
        FtpRequest ftpRequest = new FtpRequest(null);
        assertNotNull("ftpRequest is null ?", ftpRequest);
    }
}
