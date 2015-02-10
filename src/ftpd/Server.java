package ftpd;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import user.User;

public class Server{
    public static final int LISTENING_PORT = 1025; /* Ports under 1024 needs roots permissions to be binded */
    public static final List<User> usersList = new ArrayList<User>(); // Liste des utilisateurs
       
    
    public static void main(String[] args) throws IOException{
        System.out.println("Welcome in this ftpd java");
        System.out.println("Now listening on port "+LISTENING_PORT);
        ServerSocket serverSocket = new ServerSocket(LISTENING_PORT);
        Socket socket = null;
        createUsers();
        while(true){
            socket = serverSocket.accept(); /* Wait for an inbound connection */
            System.out.println("New incoming connection");
            (new FtpRequest(socket)).start();
        }
    }
        
    /**
	 * Initialisation de quelques utilisateurs
	 */
	public static void createUsers(){
		User anonymous = new User("anonymous", "");
		User calve = new User("calve", "123456");
		User paulette = new User("paulette", "456789");
		usersList.add(anonymous);
		usersList.add(calve);
		usersList.add(paulette);
	}
	
	/**
	 * Retrouver un User par son login
	 * @param login le login de l'utilisateur recherche
	 * @return User - le premier utilisateur trouve portant ce login
	 */
	public static User getUserByLogin(String login){
		for(User user : usersList){
			if(user.getLogin().equals(login))
				return user;
		}
		return null;
	}

	/**
	 * Retourne vrai s'il existe un uilisateur correspond a ce login
	 * @param name le login a rechercher parmi les utilisateurs
	 * @return True s'il existe un utilisateur, False sinon
	 */
	public static boolean containsUserByName(String name){
		for(User u: usersList)
			if(u.getLogin().equals(name))
				return true;
		return false;
	}

}
