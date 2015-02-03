package ftpd;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;

/**
 * 
 * @author dessingue
 *
 */
public class FtpRequest extends Thread{
	
	static final String PASS = "PASS";
	static final String PORT = "PORT";
	static final String QUIT = "QUIT";
	static final String SYST = "SYST";
	static final String USER = "USER";

	private InputStreamReader in;
	private DataOutputStream commandOut;
	private String username;
	private Socket dataSocket;

	public FtpRequest(Socket socket){
		try{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			in = new InputStreamReader(is);
			commandOut = new DataOutputStream(os);
			this.answer(220, "ready");
		}
		catch(Exception e){
			//socket.close();
		}
	}
	
	public void run(){
		try {
			BufferedReader br = new BufferedReader(in);
			String line;
			
			while((line = br.readLine()) != null){
				System.out.println(" <-- "+line);
				this.processRequest(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processRequest(String line) throws IOException{
		String[] command = line.split("\\s");
		
		switch(command[0]){
			case USER:
				processUser(command);
				break;
			case PASS:
				processPass(command);
				break;
			case PORT:
				processPort(command);
				break;
			case SYST:
				processSyst(command);
				break;
			case QUIT:
				processQuit(command);
				break;
			default:
				this.answer(502, "Not implemented");
				break;
		}
	}

	public void processUser(String[] command){
		assert command.length >= 2;
		if (command[1].equals("anonymous")){
			this.username = command[1];
			System.out.println("set user to (%s)\n" + this.username);
			this.answer(331, "Username ok, send password.");
		}else{
			this.answer(530, "Invalid username or password.");
        }
	}

	public void processPass(String[] command){
		this.answer(230, "User loged in, proceed");
		if(Server.getUserByLogin(this.username).isPassword(command[1])){
			// Password OK
		}else{
			// Password KO
		}
	}


	/* Handles PORT verbs, which opens a TCP socket from the server to the ip and port specified by the client
	 */
	public void processPort(String[] command){
		String[] netAddress = command[1].split(",");
		if (netAddress.length != 6){
			this.answer(500, "Syntax error");
		}
		int remote_port = (Integer.parseInt(netAddress[4]) << 8) + Integer.parseInt(netAddress[5]);
		String remote_ip = String.format("%s.%s.%s.%s", netAddress[0], netAddress[1], netAddress[2], netAddress[3]);
		System.out.println("Opening socket to "+remote_ip+":"+remote_port);
		try {
			this.dataSocket = new Socket(InetAddress.getByName(remote_ip), remote_port);
			this.answer(200, "Active data connection etablished");
		}
		catch (Exception e){
			this.answer(500, "failed");
		}
	}

	public void processSyst(String[] command){
		/* This seems to be standard in the ftp-world */
		this.answer(215, "UNIX type : L8");
	}

	public void processQuit(String[] command){
		this.answer(221, "Goodbye");
	}

	/* Respond a status code and a message to the ftp client
	 */
	private void answer(int status, String respond){
		try{
			String raw = status+" "+respond+"\r\n";
			this.commandOut.writeBytes(raw);
			System.out.println(" --> "+raw);
		}
		catch (IOException e){
			System.out.println("cannot answer to client !");
		}
	}
	
	
}