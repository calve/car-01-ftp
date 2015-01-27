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
	
	private InputStreamReader in;
	private DataOutputStream out;
	
	public FtpRequest(Socket socket){
		try{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			in = new InputStreamReader(is);
			out = new DataOutputStream(os);
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
		
		switch(command[1]){
			case USER:
				processUser();
				break;
			case PASS:
				break;
			default:
				break;
		}
	}

	public void processUser(){
		
	}
	
	public void processPass(){
		
	}
	
	
}