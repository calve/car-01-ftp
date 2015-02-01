package ftpd;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * @author dessingue
 *
 */
public class FtpRequest extends Thread{
	
	static final String USER = "USER";
	static final String PASS = "PASS";
	
	static final String QUIT = "QUIT";
	private InputStreamReader in;
	private DataOutputStream out;
	
	public FtpRequest(Socket socket){
		try{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			in = new InputStreamReader(is);
			out = new DataOutputStream(os);
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
				processUser();
				break;
			case PASS:
				break;
			case QUIT:
				processQuit(command);
				break;
			default:
				break;
		}
	}

	public void processUser(){
		
	public void processPass(String[] command){
	}
	
		
	public void processQuit(String[] command){
		this.answer(221, "Goodbye");
	}

	/* Respond a status code and a message to the ftp client
	 */
	private void answer(int status, String respond){
		try{
			String raw = status+" "+respond+"\n";
			this.out.writeChars(raw);
			System.out.println("Send : "+raw);
		}
		catch (IOException e){
			System.out.println("cannot answer to client !");
		}
	}
	
	
}