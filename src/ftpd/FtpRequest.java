package ftpd;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;

/**
 *
 * @author dessingue
 *
 */
public class FtpRequest extends Thread{

	static final String CWD  = "CWD";
	static final String LIST = "LIST";
	static final String PASS = "PASS";
	static final String PORT = "PORT";
	static final String PASV = "PASV";
	static final String PWD  = "PWD";
	static final String QUIT = "QUIT";
	static final String RETR = "RETR";
	static final String STOR = "STOR";
	static final String SYST = "SYST";
	static final String TYPE = "TYPE";
	static final String USER = "USER";

	/* Current port to use in passive mode
	 * Each new client increments it
	 */
	static public int current_passive_port = 2048;

	private InputStreamReader in;
	private DataOutputStream commandOut;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	private Socket cnxSocket;
	private Socket dataSocket;
	private String username;
	private String pwd;     /* pwd is the current working directory _relative_ to basedir */
	private String basedir; /* the directory we started the server in */
	private String previousCommand = "";
	private boolean passive = false;
	private int passive_port = 0;
	private ServerSocket passive_socket;

	/** Instanciate a FtpRequest binded to a incoming socket
	 * @param socket : incoming socket
	 * @throws IOException 
	 */
	public FtpRequest(Socket socket) throws IOException{
		try{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			this.cnxSocket = socket;
			this.in = new InputStreamReader(is);
			this.commandOut = new DataOutputStream(os);
			this.answer(220, "ready");
			this.pwd = ".";
			this.basedir = new File("").getAbsoluteFile().getAbsolutePath();
		}
		catch(Exception e){
			this.cnxSocket.close();
		}
	}

	/** Parse the incoming requests as they arrive
	 */
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

	/**
	 * Process one request line
	 * Read the line and execute the appropriate command, if available
	 * Answer 502 Not implemented if the verb is not available
	 * @param line : line to be processed
	 */
	public void processRequest(String line) throws IOException{
		String[] command = line.split("\\s");
		
		switch(command[0].toUpperCase()){
			case CWD:
				processCwd(command);
				this.previousCommand = CWD;
				break;
			case LIST:
				processList(command);
				this.previousCommand = LIST;
				break;
			case USER:
				processUser(command);
				this.previousCommand = USER;
				break;
			case PASS:
				processPass(command);
				this.previousCommand = PASS;
				break;
			case PASV:
				processPasv(command);
				this.previousCommand = PASV;
				break;
			case PORT:
				processPort(command);
				this.previousCommand = PORT;
				break;
			case PWD:
				processPwd(command);
				this.previousCommand = PWD;
				break;
			case RETR:
				processRetr(command);
				this.previousCommand = RETR;
				break;
			case STOR:
				processStor(command);
				this.previousCommand = STOR;
				break;
			case SYST:
				processSyst(command);
				this.previousCommand = SYST;
				break;
			case TYPE:
				processType(command);
				this.previousCommand = TYPE;
				break;
			case QUIT:
				processQuit(command);
				this.previousCommand = QUIT;
				break;
			default:
				this.answer(502, "Not implemented");
				this.previousCommand = "";
				break;
		}
	}

	private void processCwd(String[] command){
		if (command.length < 2){
			this.answer(500, "Syntax error");
		}
		
		String pathname = command[1];
		if(pathname.startsWith("/")){
			this.pwd = pathname;
		}
		else{
			if(this.pwd.endsWith("/")){
				this.pwd += pathname;
			}
			else{
				this.pwd = this.pwd + "/" + pathname;
			}
		}
		String target = "";
		/* Construct a canonical path from the basedir and the directory user asked */
		try {
			target = new File(this.pwd).getCanonicalPath();
		}
		catch(IOException e){
			this.pwd = ".";
			this.answer(550, "This directory does not exist.");
			return;
		}

		/* Check for path transversal disclosure : we do not allow to go upper than the directory we started the server */
		if(target.startsWith(this.basedir)){
			// on verifie que le pathname est OK
			if(new File(target).exists()){
				this.answer(250, "Change directory to "+target);
				return;
			}
		}
		else{ // l utilisateur est sorti du basedir
			this.pwd = ".";
			this.answer(550, "Access denied.");
			return;
		}
		this.answer(500, "Uncatched error");
	}
	
	private void processList(String[] command){
		this.answer(125, "Proceed");
		String raw = "";
		// Construct the file list
		File dir = new File(this.basedir+"/"+this.pwd);
		System.out.println("List directory "+dir);
		String[] filesList = dir.list();
		for (String file : filesList) {
			raw += file+"\r\n";
		}
		// Okay, send it
		this.sendData(raw);
		this.answer(226, "Complete");
	}

	private void processUser(String[] command){
		assert command.length >= 2;
		if (Server.containsUserByName(command[1])){
			this.username = command[1];
			System.out.println("set user to " + this.username);
			this.answer(331, "Username ok, send password.");
		}else{
			this.answer(530, "Invalid username or password.");
        }
	}

	private void processPass(String[] command) {
		assert command.length >= 2;
		if (!this.previousCommand.equals(USER))
			this.answer(503, "Bad sequence of commands.");
		else {
			if (Server.getUserByLogin(this.username).isPassword(command[1]))
				this.answer(230, "User loged in, proceed");
			else
				this.answer(530, "Invalid password.");
		}
	}

	private void processPasv(String[] command) {
		this.passive_port = current_passive_port++;
		this.passive = true;
		System.out.println("going to passive at port "+this.passive_port);
		try {
			this.passive_socket = new ServerSocket(this.passive_port);
			this.answer(227, String.format("Entering Passive Mode (%d,%d,%d,%d,%d,%d)", 127, 0, 0, 1, this.passive_port >> 8, this.passive_port%256));
			this.dataSocket = this.passive_socket.accept();
			OutputStream os = this.dataSocket.getOutputStream();
			this.dataOut = new DataOutputStream(os);
			System.out.println("Connection etablished");
		}
		catch (Exception e){
			this.answer(500, "Cannot open passive socket, retry");
		}
	}

	/* Handles PORT verbs, which opens a TCP socket from the server to the ip and port specified by the client
	 */
	private void processPort(String[] command){
		String[] netAddress = command[1].split(",");
		if (netAddress.length != 6){
			this.answer(500, "Syntax error");
		}
		int remote_port = (Integer.parseInt(netAddress[4]) << 8) + Integer.parseInt(netAddress[5]);
		String remote_ip = String.format("%s.%s.%s.%s", netAddress[0], netAddress[1], netAddress[2], netAddress[3]);
		System.out.println("Opening socket to "+remote_ip+":"+remote_port);
		try {
			this.dataSocket = new Socket(InetAddress.getByName(remote_ip), remote_port);
			OutputStream os = this.dataSocket.getOutputStream();
			this.dataOut = new DataOutputStream(os);
			this.answer(200, "Active data connection etablished");
		}
		catch (Exception e){
			this.answer(500, "failed");
		}
	}

	private void processPwd(String[] command){
		String raw = String.format("\"%s\" is the current working directory", this.basedir+"/"+this.pwd);
		this.answer(257, raw);
	}

	private void processRetr(String[] command){
		if (command.length < 2){
			this.answer(500, "Syntax error");
		}
		String filename = this.basedir+"/"+command[1];
		try {
			this.answer(125, "Starting transfer");
			InputStream in = new FileInputStream(filename);
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while (len != -1) {
				this.dataOut.write(buffer, 0, len);
				len = in.read(buffer);
			}
			this.dataSocket.close();
			this.answer(226, "Transfer completed");
		}
		catch (FileNotFoundException e){
			this.answer(550, "File "+filename+" not available");
		}
		catch (IOException e){
			System.out.println("Cannot send "+filename+" file to client !");
		}
	}
	
	private void processStor(String[] command){
		if (command.length < 2){
			this.answer(500, "Syntax error");
		}
		
		String filename = command[1];
		try{
			int data;
			FileOutputStream out = new FileOutputStream(filename);
			
			while((data = this.dataIn.read()) != 1){
				out.write(data);
			}
			out.close();
			this.answer(226, "Transfer completed");
		}
		catch (FileNotFoundException e){
			this.answer(550, "File "+filename+" cannot be opened. Maybe it already exists ?");
		}
		catch(IOException e) {
			System.out.println("Cannot store "+filename+" file to server !");
		}
		
	}
	
	private void processSyst(String[] command){
		/* This seems to be standard in the ftp-world */
		this.answer(215, "UNIX type : L8");
	}

	private void processType(String[] command){
		assert command.length >= 2;
		if (command[1].equals("I") || command[1].equals("A")){
		    this.answer(200, "Type accepted");
		    return;
		}
		this.answer(502, "Type not implemented");
	}

	private void processQuit(String[] command) throws IOException{
		this.answer(221, "Goodbye");
		this.cnxSocket.close();
	}

	/**
	 * Respond a status code and a message to the ftp client
	 * over the command channel
	 * @param status : three digits status code
	 * @param respond : free message explaining the answer
	 * @throws IOException : the client cannot be reached
	 */
	public void answer(int status, String respond){
		try{
			String raw = status+" "+respond+"\r\n";
			this.commandOut.writeBytes(raw);
			System.out.println(" --> "+raw);
		}
		catch (IOException e){
			System.out.println("cannot answer to client !");
		}
	}

	/**
	 *  Send data to the client over the data channel
	 * @param data : a byte array
	 */
	private void sendData(byte[] data){
		try{
			/* Send data as */
			this.dataOut.write(data, 0, data.length);
			System.out.println("D--> "+data.toString());
			this.dataSocket.close();
		}
		catch (IOException e){
			System.out.println("cannot answer to client !");
		}
	}

	/**
	 * Send data to the client over the data channel
	 * @param data : string to be sent, will be converted to a byte array
	 */
	private void sendData(String data){
		this.sendData(data.getBytes());
	}
}
