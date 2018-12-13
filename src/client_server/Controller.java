package client_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
	private User user;
	private ArrayList<Group> groups;
	private ArrayList<Message> messages;
	private ArrayList<User> connectedUsers;
	private Udp udp;
	private InetAddress ipBroadcast;
	
	/**
	 * Creer un controler
	 * @param user utilisateur associer au controleur
	 */
	public Controller (User user) {
		this.user = user;
		this.udp = new Udp(this);
		this.ipBroadcast = getBroadcast();
		
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

	public ArrayList<User> getConnectedUsers() {
		return connectedUsers;
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
		udp.start();
		udp.sendUdpMessage("1 " + user.getID(), ipBroadcast);
		return 1;		
	}
	
	/**
	 * Récupère les infos d'un nouvelle utilisateur connecté et ajout dans la liste des utilisateurs connectés
	 * @param idUser ID de l'utilisateur qui vient de se connecter
	 */
	public void receiveConnection(int idUser) {
		//recup info user dans la bdd 
		User newUser = null;
		connectedUsers.add(newUser);
	}
	
	/**
	 * Retire de la liste l'utilisateur qui vient de se déconnecter
	 * @param idUser ID de l'utilisateur qui se déconnecte
	 */
	public void receiveDeconnection(int idUser) {
		User delUser = null;
		//recup info user avec idUser
		connectedUsers.remove(delUser);
	}	
	
	/**
	 * Deconnecte l'utilisateur et l'annonce à tout le monde
	 * @return 0 si deconnexion echoue, 1 si OK
	 */
	public int deconnect() {
		// TODO
		
		udp.sendUdpMessage("0 " + user.getID(), ipBroadcast);
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
	
	/**
	 * Récupère l'adresse de broadcast de la machine
	 * @return adresse de broadcast en String
	 */
	private static InetAddress getBroadcast() {
		InetAddress local = null;
		NetworkInterface temp;
		InetAddress broadcast = null;
		
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.out.println("getLocal " + e1.getMessage());
		}		
		try {
			temp = NetworkInterface.getByInetAddress(local);
			List<InterfaceAddress> addresses = temp.getInterfaceAddresses();			
			broadcast = addresses.get(0).getBroadcast();
			return broadcast;				
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("getBroadcast " + e.getMessage());
		}
		return null;
	}
}
