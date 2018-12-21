package client_server;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String username; // TODO : crypter ?
	private InetAddress ip;
	private int port;
	
	/**
	 * Crée un utilisateur
	 * @param id ID de l'utilisateur
	 * @param username Le username de l'utilisateur
	 * @param password Le password de l'utilisateur
	 */
	public User(int id, String username) {
		this.id = id;
		this.username = username;
	}

	/**
	 * Crée un utilisateur
	 * @param username Le username de l'utilisateur
	 * @param password Le password de l'utilisateur
	 */
	public User(String username) {
		this.username = username;
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
	 * Retourne l'IP de l'utilisateur
	 * @return l'IP de l'utilisateur
	 */
	public InetAddress getIP() {
		return ip;
	}
	
	/**
	 * Modifie l'IP de l'utilisateur
	 * @param ip l'IP de l'utilisateur
	 */
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	/**
	 * Retourne le port de l'utilisateur
	 * @return le port de l'utilisateur
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Modifie le port associé à l'utilisateur
	 * @param port Port associé à l'utilisateur
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User))
			return false;
		
		User u = (User) obj;
		return u.id == id;
	}

}
