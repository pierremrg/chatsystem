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
	
	
	public Controller (User user) {
		this.user = user;
		this.udp = new Udp(this);
		
		this.connectedUsers = null;
		//Récupérer groupe dont l'utilisateur est membres dans la BDD
		//Récupérer tous les messages de l'utilisateur dans la BDD
	}
	
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
	
	public int connect(String username, String password) {
		//Check dans la BDD si info ok
		// TODO
		
		//Annonce à tous le monde la connexion
		//Récup ip broadcast
		udp.start();
		udp.sendUdpMessage("1 " + user.getID(), "255.255.255.255");
		
	}
	
	public void receiveConnection(int idUser) {
		//recup info user dans la bdd 
		connectedUsers.add(user);
	}
	
	public void receiveDeconnection(int idUser) {
		
		connectedUsers.remove(user);
	}
	
	public int deconnect() {
		// TODO
		
		udp.sendUdpMessage("0 " + user.getID(), "255.255.255.2555");
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
