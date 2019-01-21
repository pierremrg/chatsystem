package ChatSystem;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import com.google.gson.Gson;

/**
 * Represente un utilisateur de l'application
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String username;
	private InetAddress ip;
	private int port;
	private Date lastVisit;
	
	/**
	 * Cree un utilisateur
	 * @param id ID de l'utilisateur
	 * @param username Le username de l'utilisateur
	 * @param ip L'adresse IP de l'utilisateur
	 */
	public User(int id, String username, InetAddress ip) {
		this.id = id;
		this.username = username;
		this.ip = ip;
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
	 * Retourne le port de l'utilisateur
	 * @return le port de l'utilisateur
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Modifie le port associe a l'utilisateur
	 * @param port Port associe a l'utilisateur
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Retourne la date de derniere visite sur le serveur de l'utilisateur
	 * @return La date de derniere visite sur le serveur de l'utilisateur
	 */
	public Date getLastVisit() {
		return lastVisit;
	}
	
	/**
	 * Modifie la date de derniere visite sur le serveur de l'utilisateur
	 * @param lastVisit La date de derniere visite sur le serveur de l'utilisateur
	 */
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}
	
	/**
	 * Teste si deux utilisateurs sont egaux (utilise l'ID)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User))
			return false;
		
		User u = (User) obj;
		return u.id == id;
	}
	
	/**
	 * Permet d'utiliser un utilisateur
	 * @return L'utilisateur sous forme de string
	 */
	@Override
	public String toString() {		
		return "{" + Integer.toString(id) + ", " + username + "}";
	}

}
