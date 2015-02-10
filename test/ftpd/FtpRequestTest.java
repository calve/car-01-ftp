package ftpd;

import java.io.IOException;
import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MockitoJUnitRunner.class)
public class FtpRequestTest {

	@Mock private Server svr;
	@Mock private FtpRequest ftpr;
	@Mock private Socket socket;

	@Test
    public void construct() throws IOException {
		FtpRequest ftpRequest = new FtpRequest(socket);
		assertNotNull("ftpRequest is null ?", ftpRequest);
    }

    @Test
    @Ignore
    public void testAuthentificationLoginOK() throws IOException{
		String command = "USER anonymous";

		ftpr.processRequest(command);
		Mockito.verify(ftpr).answer(331, "Username ok, send password.");
    }

    @Test
    @Ignore
    public void testAuthentificationAvecLoginKO() throws IOException{
		String command = "USER inconnu";

		ftpr.processRequest(command);
		Mockito.verify(ftpr).answer(530, "Invalid username or password.");
    }

    @Test
    @Ignore
    public void test_authentification_client_avec_mot_de_passe_reussi() throws IOException{
		String commandUser = "USER anonymous";
		String commandPass = "PASS anonymous";

		ftpr.processRequest(commandUser);
		ftpr.processRequest(commandPass);
		Mockito.verify(ftpr).answer(230, "User loged in, proceed");
    }

    @Test
    @Ignore
    public void test_authentification_client_avec_mot_de_passe_errone() throws IOException{
		String commandUser = "USER anonymous";
		String commandPass = "PASS erreur";

		ftpr.processRequest(commandUser);
		ftpr.processRequest(commandPass);
		Mockito.verify(ftpr).answer(530, "Invalid password.");
    }
}
