package client_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Controller {
	private User user;
	private ArrayList<Group> groups;
	private ArrayList<Message> messages;
	private ArrayList<User> connectedUsers;
	private Udp udp;
	
	/**
	 * Creer un controler
	 * @param user utilisateur associer au controleur
	 */
	public Controller (User user) {
		this.user = user;
		this.udp = new Udp(this);
		
		this.connectedUsers = null;
		//Récupérer groupe dont l'utilisateur est membres dans la BDD
		//Récupérer tous les messages de l'utilisateur dans la BDD
	}
	
	/**
	 * Retourne l'utilisateur du controlleur
	 * @return User
	 */
	public User getUser() {
		return user;
	}

	public void sendMessage(Message message) {
		// TODO
	}
	
	public Message receiveMessage() {
		// TODO
	}
	
	public ArrayList<Message> getGroupMessages(Group group){
		// TODO
	}
	
	/**
	 * Connection de l'utilisateur au service et envoi un message à tout le monde pour annoncer sa connection
	 * @param username Username de l'utilisateur
	 * @param password Mot de passe de l'utilisateur
	 * @return 0 si connexion échoue, 1 si OK
	 */
	public int connect(String username, String password) {
		//Check dans la BDD si info ok
		// TODO
		
		//Annonce à tous le monde la connexion
		//Récup ip broadcast
		udp.start();
		udp.sendUdpMessage("1 " + user.getID(), "255.255.255.255");
		return 1;		
	}
	
	/**
	 * Récupère les infos d'un nouvelle utilisateur connecté et ajout dans la liste des utilisateurs connectés
	 * @param idUser ID de l'utilisateur qui vient de se connecter
	 */
	public void receiveConnection(int idUser) {
		//recup info user dans la bdd 
		connectedUsers.add(user);
	}
	
	/**
	 * Retire de la liste l'utilisateur qui vient de se déconnecter
	 * @param idUser ID de l'utilisateur qui se déconnecte
	 */
	public void receiveDeconnection(int idUser) {		
		connectedUsers.remove(user);
	}	
	
	/**
	 * Deconnecte l'utilisateur et l'annonce à tout le monde
	 * @return 0 si deconnexion echoue, 1 si OK
	 */
	public int deconnect() {
		// TODO
		
		udp.sendUdpMessage("0 " + user.getID(), "255.255.255.2555");
		return 1;
	}
	
	public int startGroup(ArrayList<User> members) {
		// TODO
	}
	
	public User createUser(String username, String password) {
		// TODO
	}
	
	public int editUser(String username, String password) {
		// TODO
	}
}
