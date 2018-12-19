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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	private volatile Message messageToSend = null;
	
	
	
	/**
	 * Cree un controller
	 * @param user utilisateur associe au controller
	 * @throws SocketException 
	 * @throws UnknownHostException 
	 */
	public Controller (InetAddress ipBroadcast) throws SocketException, UnknownHostException {
		this.udp = new Udp(this);		
		this.connectedUsers = new ArrayList<User>();
		this.groups = new ArrayList<Group>();
		this.messages = new ArrayList<Message>();
		this.ipBroadcast = ipBroadcast;

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
	
	public User findUserByName(String username) {
		for (User u : connectedUsers) {
			if (u.getUsername().equals(username))
				return u;
		}
		return null;
	}
	/**
	 * Permet d'envoyer un message
	 * @param message le message a envoyer
	 * @throws IOException 
	 */
	public void sendMessage(String textToSend, int receiverGroupID, int function) throws IOException {
		// TODO ajout BDD

		/*controller.sendMessage(textToSend);*/

		/*Message message = new Message(textToSend);
		controller.sendMessage(message);*/
		
//		if(function == Message.FUNCTION_STOP &&)

		Group group;
		
		if(groupIsKnown(receiverGroupID)) {
			
			// Le groupe est déjà démarré
			group = getGroupByID(receiverGroupID);
			
			// On regarde si le groupe n'est plus actif
			if(!group.isOnline()) {
				restartGroup(group);
				// TODO pas tous les connectés !
			}
		}
		else {
			
			ArrayList<User> members = new ArrayList<User>();
			
			// TODO choisir le bon user
			members.add(getConnectedUsers().get(0));
			members.add(user);
			group = startGroup(members);
		}
		
		messageToSend = new Message(new Date(), textToSend, user, group, function);

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
	
	
	
	
	
	public Message receiveMessage(Message message) {
		// TODO ajout BDD
		
		// Recoit un message : ajout du groupe si besoin
		Group group = message.getReceiverGroup();
		
		if(!groupIsKnown(group.getID()))
			groups.add(group);
		else {
			Group groupToUpdate = getGroupByID(group.getID());
			groupToUpdate.setOnline(true);
		}
		
		System.out.println(message.getContent());
		
		return null;
	}
	
	/**e
	 * Obtient un groupe à partir de son ID
	 * @param groupID l'ID du groupe a obtenir
	 * @return le groupe ou null si ce groupe n'existe pas
	 */
	private Group getGroupByID(int groupID) {
		for(Group g : groups) {
			if(g.getID() == groupID)
				return g;
		}
		
		return null;
	}
	
	/**
	 * Indique si le groupe est déjà connu par le controller
	 * @param groupID L'ID du groupe à tester
	 * @return True si le groupe est déjà dans la liste, False sinon
	 */
	private boolean groupIsKnown(int groupID) {
		for(Group g : groups) {
			if(g.getID() == groupID)
				return true;
		}
		
		return false;
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
	public void connect(String username, String password, InetAddress ip) throws IOException {
		// TODO Check dans la BDD si info ok
		// TODO id de l'utilisateur
		user = new User(2, username, password);
		
		// TODO Infos sur l'utilisateur
		user.setIP(ip);

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
		
		udp.start();
		udp.sendUdpMessage(udp.createMessage(1, getUser()), ipBroadcast);
	}
	
	/**
	 * Deconnecte l'utilisateur et l'annonce � tout le monde
	 * @throws IOException 
	 */
	public void deconnect() throws IOException {
		// TODO Gestion de l'erreur

		udp.sendUdpMessage(udp.createMessage(0, getUser()), ipBroadcast);

	}
	
	/**
	 * R�cup�re les infos d'un nouvelle utilisateur connect� et ajout dans la liste des utilisateurs connect�s
	 * @param idUser ID de l'utilisateur qui vient de se connecter
	 */
	public void receiveConnection(User receivedUser) {
		if(receivedUser == null && receivedUser.getID() != user.getID())
			return;
		
		//recup info user dans la bdd 
		System.out.println("connexion reçu! iduser=" +receivedUser.getID());

		if(!connectedUsers.contains(receivedUser) && receivedUser.getID() != user.getID())
			connectedUsers.add(receivedUser);
		
		// Mise à jour des groupes
		for(Group group : groups)
			group.updateMember(receivedUser);
		
		
	}
	
	/**
	 * Retire de la liste l'utilisateur qui vient de se d�connecter
	 * @param idUser ID de l'utilisateur qui se d�connecte
	 */
	public void receiveDeconnection(User receivedUser) {
		if(receivedUser == null)
			return;
		
		//recup info user avec idUser
		//connectedUsers.remove(receivedUser);
		User userToRemove = null;
		for(User u : connectedUsers) {
			if(u.equals(receivedUser)) {
				userToRemove = u;
				break;
			}
		}
		
		if(userToRemove != null)
			connectedUsers.remove(userToRemove);
		
		// Mise à jour des groupes
		for(Group group : groups) {
			if(group.isMember(receivedUser)) {
				group.setOnline(false);
				group.setStarter(user);
			}
		}
		
	}	
	
	/**
	 * Démarre une conversation
	 * @param members Utilisateurs présents dans la conversation
	 * @return Le groupe créé
	 * @throws IOException 
	 */
	private Group startGroup(ArrayList<User> members) throws IOException {
		
		// Première version : uniquement deux utilisateurs dans la conversation
		// TODO Faire pour plusieurs personnes
		User contact = members.get(0);
		
		
		// TODO Obtenir l'ID dans la BDD
		// TODO Gérer l'erreur
		int idGroup = 0;
		
		// Démarre un groupe :
		// - ID du groupe
		// - Membres du groupe (uniquement deux personnes pour le moment)
		// - Utilisateur qui a initié la conversation (utile pour savoir qui est client/serveur)
		Group group = new Group(idGroup, members, user);
		groups.add(group);
		
		// TODO Ajout à la BDD
		
		// Création d'un socket client : l'utilisateur se connecte à l'autre utilisateur
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("clientSocketWriter",socket, this);
		SocketReader socketReader = new SocketReader("clientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
		
		return group;
	}
	
	private void restartGroup(Group group) throws IOException {
		
		group.setStarter(user);
		group.setOnline(true);
		
		User contact = group.getMembers().get(1);
		
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("restartclientSocketWriter",socket, this);
		SocketReader socketReader = new SocketReader("restartclientSocketReader", socket, this);
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
	 * R�cup�re toutes les adresses IP de la machine et les adresses de broadcast associ�es
	 * @return Map<InetAddress, InetAddress> 
	 * @throws SocketException
	 */
	public static Map<InetAddress, InetAddress> getAllIpAndBroadcast() throws SocketException {
		Map<InetAddress, InetAddress> listIP = new HashMap<>();
	    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	    while (interfaces.hasMoreElements()) {
	        NetworkInterface networkInterface = interfaces.nextElement();	 
	        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
	            continue;
	        }	        
	        networkInterface.getInterfaceAddresses().stream().forEach(a -> listIP.put(a.getAddress(), a.getBroadcast()));
	    }
	    return listIP;
	}
}
