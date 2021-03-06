package user;

/**
 * Class modelisant un utilisateur
 * @author Pauline
 *
 */
public class User {
	private String login;
	private String password;
	
	public User(String login, String password){
		this.login = login;
		this.password = password;
	}
	
	/*
	 * getters, setters
	 */
	public String getLogin() { return login; }

	public void setLogin(String login) { this.login = login; }

	public String getPassword() { return password; }

	public void setPassword(String password) { this.password = password; }
	
	/**
	 * Verification du password
	 */	
	public boolean isPassword(String pass) {
		if (this.login.equals("anonymous"))
			return true;
		else
			return this.password.equals(pass);
	}
}
