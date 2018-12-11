package client_server;

public class User {

	private int id;
	private String username; // TODO : crypter ?
	private String password;
	private String ip;
	private int port;

	/**
	 * Crée un utilisateur
	 * @param username Le username de l'utilisateur
	 * @param password Le password de l'utilisateur
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Retourne l'ID de l'utilisateur
	 * @return l'ID de l'utilisateur
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retourne le username de l'utilisateur
	 * @return le username de l'utilisateur
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Modifie le username de l'utilisateur
	 * @param username Le nouveau username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Retourne le mot de passe de l'utilisateur
	 * @return le mot de passe de l'utilisateur
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifie le password de l'utilisateur
	 * @param password Le nouveau password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retourne l'IP de l'utilisateur
	 * @return l'IP de l'utilisateur
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * Retourne le port de l'utilisateur
	 * @return le port de l'utilisateur
	 */
	public int getPort() {
		return port;
	}

}
