package client_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
	
	// Utilisateur associe au controller
	private User user;
	
	// Groupes de l'utilisateur
	private ArrayList<Group> groups;
	
	// Messages (tous) de l'utilisateur
	private ArrayList<Message> messages;
	
	// Liste des utilisateurs connectes
	private ArrayList<User> connectedUsers;
	
	// Service UDP utilise pour le broadcast (connexion, deconnexion)
	private Udp udp;
	
	// Adresse IP de broadcast du réseau
	private InetAddress ipBroadcast;
	
	// Utilisé pour envoyer un message
	private Message messageToSend = null;
	
	
	
	/**
	 * Cree un controller
	 * @param user utilisateur associe au controller
	 */
	public Controller () {
		this.udp = new Udp(this);
		this.ipBroadcast = getBroadcast();
		
		this.connectedUsers = null;
		//Recuperer groupe dont l'utilisateur est membre dans la BDD
		//Recuperer tous les messages de l'utilisateur dans la BDD
		
	}
	
	/**
	 * Retourne l'utilisateur du controlleur
	 * @return User
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Retourne la liste des utilisateurs connectes
	 * @return la liste des utilisateurs connectes
	 */
	public ArrayList<User> getConnectedUsers() {
		return connectedUsers;
	}

	/**
	 * Permet d'envoyer un message
	 * @param message le message a envoyer
	 */
	public void sendMessage(Message message) {
		// TODO
		
		messageToSend = message;
	}
	
	/**
	 * Retourne le message qui doit être envoyé (null si aucun)
	 * Utilisé par les threads d'écriture
	 * @return le message à envoyer
	 */
	public Message getMessageToSend() {
		return messageToSend;
	}
	
	/**
	 * Permet à aux threads d'écriture d'indiquer que le message a été envoyé
	 */
	public void messageSent() {
		messageToSend = null;
	}
	
	
	
	
	
	public Message receiveMessage(String message) {
		// TODO
		System.out.println(message);
		
		return null;
	}
	
	public ArrayList<Message> getGroupMessages(Group group){
		// TODO
		
		return null;
	}
	
	/**
	 * Connection de l'utilisateur au service et envoi un message � tout le monde pour annoncer sa connection
	 * @param username Username de l'utilisateur
	 * @param password Mot de passe de l'utilisateur
	 * @return 0 si connexion �choue, 1 si OK
	 * @throws IOException 
	 */
	public void connect(String username, String password) throws IOException {
		// TODO Check dans la BDD si info ok
		// TODO id de l'utilisateur
		user = new User(1, username, password);
		
		// TODO Infos sur l'utilisateur
		user.setIP(InetAddress.getLocalHost());
		Random rand = new Random();
		int portRand = rand.nextInt(65000 - 10000 + 1) + 1000; // Rand(10000,65000)
		user.setPort(portRand);
		
		
		// Démarrage du serveur : l'utilisateur peut être sollicité pour une conversation
		int serverPort = user.getPort(); // Port du serveur = celui associé à l'utilisateur
		ServerSocket serverSocket = new ServerSocket(serverPort);
		ServerSocketWaiter serverSocketWaiter = new ServerSocketWaiter(serverSocket, this);
		serverSocketWaiter.start();

		
		// TODO Gerer l'erreur
		
		//Annonce � tout le monde la connexion
		//R�cup ip broadcast
		//udp.start();
		//udp.sendUdpMessage("1 " + user.getID(), ipBroadcast);
	}
	
	/**
	 * Deconnecte l'utilisateur et l'annonce � tout le monde
	 */
	public void deconnect() {
		// TODO Gestion de l'erreur

		udp.sendUdpMessage("0 " + user.getID(), ipBroadcast);

	}
	
	/**
	 * R�cup�re les infos d'un nouvelle utilisateur connect� et ajout dans la liste des utilisateurs connect�s
	 * @param idUser ID de l'utilisateur qui vient de se connecter
	 */
	public void receiveConnection(int idUser) {
		//recup info user dans la bdd 

		User newUser = null;
		connectedUsers.add(newUser);
	}
	
	/**
	 * Retire de la liste l'utilisateur qui vient de se d�connecter
	 * @param idUser ID de l'utilisateur qui se d�connecte
	 */
	public void receiveDeconnection(int idUser) {
		User delUser = null;
		//recup info user avec idUser
		connectedUsers.remove(delUser);
	}	
	
	/**
	 * Démarre une conversation
	 * @param members Utilisateurs présents dans la conversation
	 * @throws IOException 
	 */
	public void startGroup(ArrayList<User> members) throws IOException {
		
		// Première version : uniquement deux utilisateurs dans la conversation
		// TODO Faire pour plusieurs personnes
		User contact = members.get(0);
		
		
		// TODO Obtenir l'ID dans la BDD
		// TODO Gérer l'erreur
		int idGroup = 1;
		
		Group group = new Group(idGroup, members);
		groups.add(group);
		
		// TODO Ajout à la BDD
		
		
		// Création d'un socket client : l'utilisateur peut se connecter aux autres utilisateurs
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter(socket, this);
		SocketReader socketReader = new SocketReader(socket, this);
		socketWriter.start();
		socketReader.start();
	}
	
	public void createUser(String username, String password) {
		// TODO Check si username pas pris
		// TODO Gestion erreur
		
		// TODO Récupérer ID BDD
		int idUser = 1;
		user = new User(username, password);

		// TODO Ajout à la BDD
	}
	
	public void editUser(String username, String password) {
		// TODO Check si username pas pris
		// TODO Gestion erreur
		
		user.setUsername(username);
		user.setPassword(password);
		// TODO Update user BDD
	}
	
	/**
	 * R�cup�re l'adresse de broadcast de la machine
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
