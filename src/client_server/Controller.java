package client_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Controller {
	
	// Utilisateur associé au controller
	private User user;
	
	// Groupes de l'utilisateur
	private ArrayList<Group> groups;
	
	// Messages (tous) de l'utilisateur
	private ArrayList<Message> messages;
	
	// Liste des utilisateurs connectés
	private ArrayList<User> connectedUsers;
	
	// Service UDP utilisé pour le broadcast (connexion, déconnexion)
	private Udp udp;
	
	
	/**
	 * Crée un controller
	 * @param user utilisateur associé au controller
	 */
	public Controller () {
		this.udp = new Udp(this);
		
		this.connectedUsers = null;
		//R�cup�rer groupe dont l'utilisateur est membre dans la BDD
		//R�cup�rer tous les messages de l'utilisateur dans la BDD
		
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
		ServerSocketWaiter serverSocketWaiter = new ServerSocketWaiter(serverSocket);
		serverSocketWaiter.start();

		
		// TODO Gerer l'erreur
		
		//Annonce � tout le monde la connexion
		//R�cup ip broadcast
		//udp.start();
		//udp.sendUdpMessage("1 " + user.getID(), "255.255.255.255");
	}
	
	/**
	 * R�cup�re les infos d'un nouvelle utilisateur connect� et ajout dans la liste des utilisateurs connect�s
	 * @param idUser ID de l'utilisateur qui vient de se connecter
	 */
	public void receiveConnection(int idUser) {
		//recup info user dans la bdd 
		//connectedUsers.add(user);
	}
	
	/**
	 * Retire de la liste l'utilisateur qui vient de se d�connecter
	 * @param idUser ID de l'utilisateur qui se d�connecte
	 */
	public void receiveDeconnection(int idUser) {
		connectedUsers.remove(user);
	}	
	
	/**
	 * Deconnecte l'utilisateur et l'annonce � tout le monde
	 */
	public void deconnect() {
		// TODO Gestion de l'erreur
		
		//udp.sendUdpMessage("0 " + user.getID(), "255.255.255.255");
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
		
		SocketWriter socketWriter = new SocketWriter(socket);
		SocketReader socketReader = new SocketReader(socket);
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
}
