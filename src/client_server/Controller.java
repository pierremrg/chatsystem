package client_server;

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

/**
 * Controller de l'application
 *
 */
public class Controller {
	
	// Utilisateur associe au controller
	private User user;
	
	// GUI
	private volatile GUI gui; // TODO Pourquoi volatile ?
	
	// Groupes de l'utilisateur
	private ArrayList<Group> groups;
	
	// Messages (tous) de l'utilisateur
	private ArrayList<Message> messages;
	
	// Liste des utilisateurs connectes
	private volatile ArrayList<User> connectedUsers;
	
	// Service UDP utilise pour le broadcast (connexion, deconnexion)
	private Udp udp;
	
	// Adresse IP de broadcast du reseau
	private InetAddress ipBroadcast;
	
	// Utilise pour envoyer un message
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
		
		try {
			messages = DataManager.readAllMessages();
			groups = DataManager.readAllGroups();
			
			/* Tests pour verifier le bon fonctionnement de la sauvegarde des donnees, TODO a supprimer */
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
			// TODO Gerer erreur dans GUI
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Tests pour la sauvegarde de donnees
	 * TODO a supprimer
	 */
	public void testSaveMessages() {
		user = new User(1, "toto", null);
		ArrayList<User> members0 = new ArrayList<User>();
		members0.add(new User(5, "truc", null));
		members0.add(user);
		
		ArrayList<User> members1 = new ArrayList<User>();
		members1.add(user);
		members1.add(new User(10, "jean", null));
		
		Group group0 = new Group(0, members0, user);
		Group group1 = new Group(1, members1, user);
		
		groups.add(group0);
		groups.add(group1);
		
		messages.add(new Message(new Date(), "coucou", user, group0, Message.FUNCTION_NORMAL));
		messages.add(new Message(new Date(), "coucou2", user, group0, Message.FUNCTION_NORMAL));
		messages.add(new Message(new Date(), "coucou3", user, group1, Message.FUNCTION_NORMAL));
	}
	
	
	/**
	 * Associe un GUI au controller
	 * @param gui Le GUI a associer
	 */
	public void setGUI(GUI gui) {
		this.gui = gui;
	}
	
	/**
	 * Retourne l'utilisateur associe au controlleur
	 * @return User
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Retourne la liste des groupes de l'utilisateur
	 * @return La liste des groupes de l'utilisateur
	 */
	public ArrayList<Group> getGroups() {
		return groups;
	}

	/**
	 * Retourne la liste des utilisateurs connectes
	 * @return La liste des utilisateurs connectes
	 */
	public ArrayList<User> getConnectedUsers() {
		return connectedUsers;
	}
	
	
	
	/**
	 * Retourne un utilisateur trouve par son pseudo
	 * @param username Le pseudo de l'utilisateur a trouver
	 * @return L'utilisateur trouve, ou null si aucun utilisateur avec ce pseudo n'existe
	 */
	public User findUserByName(String username) {
		for (User u : connectedUsers) {
			if (u.getUsername().equals(username))
				return u;
		}
		return null;
	}
	
	/**
	 * Permet d'envoyer un message
	 * @param textToSend Le contenu du message � envoyer
	 * @param receiverGroupNameForUser Le groupe a qui envoyer le message
	 * 		Le nom de groupe est different selon l'utilisateur
	 * @param function La fonction du message
	 * @see Message.java
	 * @throws IOException
	 */
	public void sendMessage(String textToSend, String receiverGroupNameForUser, int function) throws IOException {
		
		Group group = getGroupByName(receiverGroupNameForUser);
		
		// Si le groupe est deja dans la liste des groupes de l'utilisateur
		if(group != null) {
			
			// On recupere la lsite des membres du groupe
			ArrayList<User> members = group.getMembers();
			
			// Pour le moment, on fait des conversations entre deux personnes uniquement
			User contact;
			
			if(members.get(0).equals(user))
				contact = members.get(1);
			else
				contact = members.get(0);
			
			// Si le groupe n'etait plus actif (groupe hors ligne), on le redemarre
			// TODO Besoin de tester si user en ligne ? Logiquement, si le groupe est en ligne le contact aussi
			// TODO Throws erreur pas connecte
			if(!group.isOnline() && connectedUsers.contains(contact))
				restartGroup(group);
				
		}
		else {
			
			// Creation d'un nouveau groupe si le groupe n'existe pas
			ArrayList<User> members = new ArrayList<User>();
			members.add(findUserByName(receiverGroupNameForUser));
			members.add(user);
			group = startGroup(members);

		}
		
		// Envoi du message
		Message message = new Message(new Date(), textToSend, user, group, function);
		messageToSend = message;
		
		// Enregsitrement du message
		messages.add(message);

	}
	
	/**
	 * Retourne le message qui doit etre envoye (null si aucun)
	 * Utilise par les threads d'ecriture
	 * @return le message a envoyer
	 * @see SocketWriter
	 */
	public Message getMessageToSend() {
		return messageToSend;
	}
	
	/**
	 * Permet a aux threads d'ecriture d'indiquer que le message a ete envoye
	 * @see SocketWriter
	 */
	public void messageSent() {
		messageToSend = null;
	}
	
	/**
	 * Indique au controller que l'utilisateur a recu un message
	 * @param message Le message recu
	 */
	public void receiveMessage(Message message) {
		
		// Ajout du groupe si le groupe n'est pas connu par l'utilisateur (nouvelle conversation)
		Group group = message.getReceiverGroup();
		
		if(!groupIsKnown(group)) {
			groups.add(group);
			gui.addGroup(group);
		}
		else {
			// Si le groupe est connu, on l'indique en ligne
			Group groupToUpdate = getGroupByID(group.getID());
			groupToUpdate.setOnline(true);
		}
		
		// Enregistrement du message recu
		messages.add(message);
		
		// TODO affichage a faire correctement
		System.out.println(message.getContent());
		gui.setGroupNoRead(group);
		
	}
	
	/**
	 * Obtient un groupe a partir de son ID
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
	 * Indique si le groupe est deja connu par le controller
	 * @param group Le groupe a tester
	 * @return True si le groupe est deja dans la liste, False sinon
	 */
	private boolean groupIsKnown(Group group) {
		for(Group g : groups) {
			if(g.equals(group))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Connexion de l'utilisateur au service et envoi d'un message a tout le monde pour indiquer sa presence (via UDP)
	 * @param id L'ID de l'utilisateur
	 * @param username L'username de l'utilisateur
	 * @param ip L'IP de l'utilisateur
	 * @throws IOException
	 * @see ServerSocketWaiter
	 */
	public void connect(int id, String username, InetAddress ip) throws IOException {

		// Les verifications sur les identifiants de l'utilisateur sont faites avant

		// Cr�ation de l'utilisateur associe au controller
		user = new User(id, username, ip);
		
		// On associe un port random a ce nouvel utilisateur
		Random rand = new Random();
		int portRand = rand.nextInt(65000 - 10000 + 1) + 1000; // Rand(10000,65000)
		user.setPort(portRand);
		
		// On demarre un serveur d'ecoute : l'utilisateur peut etre sollicite pour une conversation
		int serverPort = user.getPort(); // Port du serveur = celui associe a l'utilisateur
		ServerSocket serverSocket = new ServerSocket(serverPort);
		ServerSocketWaiter serverSocketWaiter = new ServerSocketWaiter(serverSocket, this);
		serverSocketWaiter.start();

		// Demarrage du service UDP et envoi du message de presence
		udp.start();
		udp.sendUdpMessage(udp.createMessage(1, getUser()), ipBroadcast);
		// TODO : constantes a utiliser
		
		// Ajout des groupes au GUI
		for(Group g : groups) {
			gui.addGroup(g);
		}
		
	}
	
	/**
	 * Deconnecte l'utilisateur et l'annonce a tout le monde
	 * @throws IOException 
	 */
	public void deconnect() throws IOException {
		
		// Tous les groupes de l'utilisateur sont maintenant offline
		for(Group g : groups)
			g.setOnline(false);
		
		// TODO Gestion de l'erreur
		DataManager.writeAllMessages(messages);
		DataManager.writeAllGroups(groups);

		// TODO Gestion de l'erreur
		// TODO Constantes a utiliser
		udp.sendUdpMessage(udp.createMessage(0, getUser()), ipBroadcast);

	}

	/**
	 * Recoit les informations d'un utilisateur qui vient de se connecter
	 * @param receivedUser
	 */
	public void receiveConnection(User receivedUser) {
		
		if(receivedUser == null)
			return;

		// TODO Affichage dans le GUI
		System.out.println("connexion reçu! iduser=" +receivedUser.getID());

		// On verifie qu'on ne re�oit pas sa propre annonce et qu'on ne conna�t pas deja l'utilisateur
		if(!connectedUsers.contains(receivedUser) && !receivedUser.equals(user))
			connectedUsers.add(receivedUser);
		
		// Mise a jour des groupes avec les nouvelles informations de l'utilisateur connecte
		for(Group group : groups)
			group.updateMember(receivedUser);
		
		// Mise a jour du GUI
		if(gui != null)
			gui.updateConnectedUsers();
	}
	
	/**
	 * Recoit la deconexion d'un utilisateur
	 * @param receivedUser L'utilisateur qui vient de se deconnecter
	 */
	public void receiveDeconnection(User receivedUser) {

		if(receivedUser == null)
			return;
		
		// Suppression de l'utilisateur dans la liste des connectes
		User userToRemove = null;

		for(User u : connectedUsers) {
			if(u.equals(receivedUser)) {
				userToRemove = u;
				break;
			}
		}
		
		if(userToRemove != null)
			connectedUsers.remove(userToRemove);
		
		// Mise a jour des groupes (groupe passe en mode inactif)
		for(Group group : groups) {
			if(group.isMember(receivedUser)) {
				group.setOnline(false);
				group.setStarter(user);
			}
		}
		
		// Mise a jour du GUI
		if(gui != null)
			gui.updateConnectedUsers();
	}	
	
	/**
	 * Demarre une nouvelle conversation
	 * @param members Utilisateurs presents dans la conversation
	 * @return Le groupe cree
	 * @throws IOException
	 * @see SocketWriter SocketReader
	 */
	private Group startGroup(ArrayList<User> members) throws IOException {
		
		// Premiere version : uniquement deux utilisateurs dans la conversation
		// TODO Faire pour plusieurs personnes ?
		User contact = members.get(0);

		
		// TODO Gerer numero de groupe minimum ? + gerer l'erreur
		Random rand = new Random();
		int idGroup = rand.nextInt(999999999); // Rand()
		
		// Demarrage d'un groupe :
		// - ID du groupe
		// - Membres du groupe (uniquement deux personnes pour le moment)
		// - Utilisateur qui a initie la conversation (utile pour savoir qui est client/serveur)
		Group group = new Group(idGroup, members, user);
		groups.add(group);
		
		
		// Creation d'un socket client : l'utilisateur se connecte a l'utilisateur distant
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("clientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("clientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
		
		// Mise a jour de la liste des groupes dans le GUI
		gui.addGroup(group);
		gui.selectGroupInList(group);
		
		return group;
	}
	
	/**
	 * Permet de reprendre une conversation deja existante
	 * @param group Le groupe a redemarrer
	 * @throws IOException
	 * @see SocketWriter SocketReader
	 */
	private void restartGroup(Group group) throws IOException {
		
		// On passe le groupe est mode actif
		group.setStarter(user);
		group.setOnline(true);
		
		ArrayList<User> members = group.getMembers();
		User contact;
		
		if(members.get(0).equals(user))
			contact = members.get(1);
		else
			contact = members.get(0);
		
		// On recree un socket client : l'utilisateur se reconnecte a l'utilisateur distant
		Socket socket = new Socket(contact.getIP(), contact.getPort());
		
		SocketWriter socketWriter = new SocketWriter("restartclientSocketWriter",socket, this, group);
		SocketReader socketReader = new SocketReader("restartclientSocketReader", socket, this);
		socketWriter.start();
		socketReader.start();
	}

	/**
	 * Retourne la liste des messages d'un groupe donne
	 * @param group Le groupe dont les messages sont recherches
	 * @return La liste des message du groupe indique
	 */
	public ArrayList<Message> getGroupMessages(Group group){
		
		ArrayList<Message> groupMessages = new ArrayList<Message>();
		
		for(Message m : messages) {
			if(m.getReceiverGroup().equals(group))
				groupMessages.add(m);
		}
		
		return groupMessages;
	}
	
	// TODO
	public void editUser(String username, String password) {
		// TODO Check si username pas pris
		// TODO Gestion erreur
		// TODO Update user BDD
		//TODO update user controller
	}
	
	/**
	 * Recupere toutes les adresses IP de la machine et les adresses de broadcast associees
	 * @return Map<InetAddress, InetAddress> Une table contenant les associations @IP <> @broadcast de la machine
	 * @throws SocketException
	 */
	public static Map<InetAddress, InetAddress> getAllIpAndBroadcast() throws SocketException {
		
		Map<InetAddress, InetAddress> listIP = new HashMap<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		
		while (interfaces.hasMoreElements()) {
		
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;

			for (InterfaceAddress a : networkInterface.getInterfaceAddresses()) {
				if (a.getAddress() instanceof Inet4Address)
					listIP.put(a.getAddress(), a.getBroadcast());
			}
			
		}
		
		return listIP;
	}
	
}
