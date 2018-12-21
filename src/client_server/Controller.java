package client_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
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
import java.util.Map;
import java.util.Random;

public class Controller {
	
	// TODO supprimer
	private static final int USER_ID = 3;
	
	// Utilisateur associe au controller
	private User user;
	
	// GUI
	private GUI gui;
	
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
		
		
		try {
			messages = DataManager.readAllMessages();
			groups = DataManager.readAllGroups();
			
			// TODO traitement des groupes
			
			/*for(Group g : groups) {
				System.out.println(g.getID());
			}
			
			for(Message m : messages) {
				if(m.getReceiverGroup() != null) {
					System.out.println("Group " + m.getReceiverGroup().getID());
					
					if(!groups.contains(m.getReceiverGroup()))
						groups.add(m.getReceiverGroup());
				}
					
				else
					System.out.println("no group");
			}*/
			
			//testSaveMessages();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// TODO a supprimer
	public void testSaveMessages() {
		user = new User(1, "toto", "password");
		ArrayList<User> members0 = new ArrayList<User>();
		members0.add(new User(5, "truc", "bidule"));
		members0.add(user);
		
		ArrayList<User> members1 = new ArrayList<User>();
		members1.add(user);
		members1.add(new User(10, "jean", "jacques"));
		
		Group group0 = new Group(0, members0, user);
		Group group1 = new Group(1, members1, user);
		
		messages.add(new Message(new Date(), "coucou", user, group0, Message.FUNCTION_NORMAL));
		messages.add(new Message(new Date(), "coucou2", user, group0, Message.FUNCTION_NORMAL));
		messages.add(new Message(new Date(), "coucou3", user, group1, Message.FUNCTION_NORMAL));
	}
	
	public void setGUI(GUI gui) {
		this.gui = gui;
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
	public void sendMessage(String textToSend, String receiverGroupNameForUser, int function) throws IOException {
		// TODO ajout BDD

		/*controller.sendMessage(textToSend);*/

		/*Message message = new Message(textToSend);
		controller.sendMessage(message);*/
		
//		if(function == Message.FUNCTION_STOP &&)

		Group group = getGroupByName(receiverGroupNameForUser);
		
		// Si le groupe est déjà dans la liste des groupes de l'utilisateur
		if(group != null) {
			
			// Le groupe a déjà été démarré
			ArrayList<User> members = group.getMembers();
			User contact;
			
			if(members.get(0).equals(user))
				contact = members.get(1);
			else
				contact = members.get(0);
			
			// On regarde si le groupe n'est plus actif
			// TODO Throws erreur pas connecté
			if(!group.isOnline() && connectedUsers.contains(contact)) {
				restartGroup(group);
				// TODO pas tous les connectés !
			}
		}
		else {
			
			ArrayList<User> members = new ArrayList<User>();
			
			// TODO choisir le bon user
			// TODO vérifier si connectedUsers est pas vide
			members.add(findUserByName(receiverGroupNameForUser));
			members.add(user);
			group = startGroup(members);
		}
		
		Message message = new Message(new Date(), textToSend, user, group, function);
		messageToSend = message;
		
		messages.add(message);

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
	
	
	
	
	
	public void receiveMessage(Message message) {
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
		
		messages.add(message);
		
		gui.updateMessages();
	}
	
	/**e
	 * Obtient un groupe à partir de son ID
	 * @param groupID L'ID du groupe a obtenir
	 * @return Le groupe ou null si ce groupe n'existe pas
	 */
	private Group getGroupByID(int groupID) {
		for(Group g : groups) {
			if(g.getID() == groupID)
				return g;
		}
		
		return null;
	}
	
	/**
	 * Obtient un groupe a partir de son nom, vu par un certain utilisateur
	 * @param groupName Le nom du groupe vu par l'utilisateur
	 * @return Le groupe ou null si ce groupe n'existe pas
	 */
	public Group getGroupByName(String groupName) {
		for(Group g : groups) {
			if(g.getGroupNameForUser(user).equals(groupName))
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
		user = new User(USER_ID, username, password);

		
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
		
		// Tous les groupes sont maintenant offline
		for(Group g : groups)
			g.setOnline(false);
		
		// TODO Gestion de l'erreur
		DataManager.writeAllMessages(messages);
		DataManager.writeAllGroups(groups);

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
		
		// Mise à jour du GUI
		if(gui != null)
			gui.updateConnectedUsers();
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
		
		// Mise à jour du GUI
		if(gui != null)
			gui.updateConnectedUsers();
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
		Random rand = new Random();
		int idGroup = rand.nextInt(999999999); // Rand()
		
		// Démarre un groupe :
		// - ID du groupe
		// - Membres du groupe (uniquement deux personnes pour le moment)
		// - Utilisateur qui a initié la conversation (utile pour savoir qui est client/serveur)
		Group group = new Group(idGroup, members, user);
		groups.add(group);
		
		// TODO Ajout à la BDD
		
		// Création d'un socket client : l'utilisateur se connecte à l'autre utilisateur
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("clientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("clientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
		
		return group;
	}
	
	private void restartGroup(Group group) throws IOException {
		
		group.setStarter(user);
		group.setOnline(true);
		
		ArrayList<User> members = group.getMembers();
		User contact;
		
		if(members.get(0).equals(user))
			contact = members.get(1);
		else
			contact = members.get(0);
		
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("restartclientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("restartclientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
	}

	/**
	 * Retourne la liste des messages d'un groupe donné
	 * @param group Le groupe dont les messages sont recherchés
	 * @return La liste des message du groupe indiqué
	 */
	public ArrayList<Message> getGroupMessages(Group group){
		
		ArrayList<Message> groupMessages = new ArrayList<Message>();
		
		for(Message m : messages) {
			if(m.getReceiverGroup().equals(group))
				groupMessages.add(m);
		}
		
		return groupMessages;
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
	        
	        for (InterfaceAddress a : networkInterface.getInterfaceAddresses()) {
	        	if (a.getAddress() instanceof Inet4Address)
	        		listIP.put(a.getAddress(), a.getBroadcast());
	        }
	    }
	    return listIP;
	}
}
