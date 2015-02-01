package ftpd;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server{
    public static final int LISTENING_PORT = 1025; /* Ports under 1024 needs roots permissions to be binded */

    public static void main(String[] args) throws IOException{
        System.out.println("Welcome in this ftpd java");
        System.out.println("Now listening on port "+LISTENING_PORT);
        ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
        Socket socket = null;
        while(true){
            socket = serverSocket.accept(); /* Wait for an inbound connection */
            System.out.println("New incoming connection");
            (new FtpRequest(socket)).start();
        }
    }
}
