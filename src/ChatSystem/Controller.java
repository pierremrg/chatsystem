package ChatSystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import com.google.gson.Gson;

import ChatSystem.DataManager.PasswordError;
import ChatSystemServer.ChatServer;
import ChatSystemServer.ChatServer.ServerResponse;

/**
 * Controller de l'application
 */
public class Controller {
	
	private User user;
	
	// GUI
	private GUI gui;
	
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
	
	// Informations sur le serveur (si besoin)
	private static boolean useServer;
	private static String serverIP;
	private static int serverPort;
	private static int timeoutConnection;
	private static int updateInterval;
	
	// Timer utilise pour les requetes
	private Timer timer;
	
	
	/**
	 * Constantes
	 */
	private static final String PATH_WEBPAGE = "/chatsystem/ChatServer";

	public static final int EXIT_NO_ERROR = 0;
	public static final int EXIT_ERROR_SERVER_UNAVAILABLE = 1;
	public static final int EXIT_ERROR_GET_CONNECTED_USERS = 2;
	public static final int EXIT_ERROR_SEND_CONNECTION = 3;
	public static final int EXIT_ERROR_SEND_DECONNECTION = 4;
	
	/**
	 * Erreurs
	 */
	@SuppressWarnings("serial")
	public static class ConnectionError extends Exception {};
	@SuppressWarnings("serial")
	public static class SendConnectionError extends Exception {};
	@SuppressWarnings("serial")
	public static class SendDeconnectionError extends Exception {};

	
	/**
	 * Cree le controller de l'application
	 * @param ipBroadcast L'adresse IP de la machine
	 * @param serverIP L'adresse IP du serveur a utiliser
	 * @param serverPort Le port a utiliser (Negatif si pas d'utilisation du serveur)
	 * @throws FileNotFoundException Erreur dans la lecture des fichiers
	 * @throws ClassNotFoundException Erreur dans la lecture des fichiers
	 * @throws IOException Erreur dans la lecture des fichiers
	 */
	public Controller (InetAddress ipBroadcast, String serverIP, int serverPort) throws FileNotFoundException, ClassNotFoundException, IOException {

		connectedUsers = new ArrayList<User>();
		groups = new ArrayList<Group>();
		messages = new ArrayList<Message>();
		
		messages = DataManager.readAllMessages();
		groups = DataManager.readAllGroups();
		

		// TODO lire config ici
		
		// On utilise le serveur
		if(serverPort > 0) {
			useServer = true;
			Controller.serverIP = serverIP;
			Controller.serverPort = serverPort;
			
			timeoutConnection = Integer.parseInt(DataManager.getSetting("server", "timeout", "5000"));
			updateInterval = Integer.parseInt(DataManager.getSetting("server", "update_interval", "1000"));
		}
		
		// On utilise le service UDP
		else {
			useServer = false;
			Controller.serverIP = null;
			Controller.serverPort = -1;
			
			this.ipBroadcast = ipBroadcast;
			udp = new Udp(this);
		}
		
	}
	
	/***************************** Getters/Setters/Fonctions de recherche *****************************/
	
	/**
	 * Associe un GUI au controller
	 * @param gui Le GUI a associer
	 */
	public void setGUI(GUI gui) {
		this.gui = gui;
	}
	
	/**
	 * Retourne l'utilisateur associe au controller
	 * @return User L'utilisateur associe au controller
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Retourne la liste des groupes de l'utilisateur
	 * @return La liste des groupes de l'utilisateur
	 */
//	public ArrayList<Group> getGroups() {
//		return groups;
//	}
	// TODO remove

	/**
	 * Retourne la liste des utilisateurs connectes
	 * @return La liste des utilisateurs connectes
	 */
	public ArrayList<User> getConnectedUsers() {
		return connectedUsers;
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
	
	/**
	 * Retourne le message qui doit etre envoye (null si aucun)
	 * Utilise par les threads d'ecriture
	 * @return le message a envoyer
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
	
	
	/***************************** Methodes liees a la connexion/deconnexion *****************************/
	
	/**
	 * Connexion de l'utilisateur au service et envoi d'un message a tout le monde pour indiquer sa presence
	 * @param id L'ID de l'utilisateur
	 * @param username L'username de l'utilisateur
	 * @param ip L'IP de l'utilisateur
	 * @throws IOException Si une erreur survient
	 */
	public void connect(int id, String username, InetAddress ip) throws IOException {
		
		// Les verifications sur les identifiants de l'utilisateur sont faites avant

		// Creation de l'utilisateur associe au controller
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

		// Si on utilise le serveur
		if(useServer) {
			// Lancement du timer
			// Ce timer sert a recuperer les utilisateurs connectes et a indiquer sa presence de facon reguliere
			timer = new Timer();
			timer.scheduleAtFixedRate(new ResquestTimer(this), 0, updateInterval);
		}
		
		// Si on utilise UDP
		else {
			// Demarrage du service UDP et envoi du message de presence
			udp.start();
			udp.sendUdpMessage(udp.createMessage(Udp.STATUS_CONNEXION, getUser()), ipBroadcast);
		}
		
		
		// Ajout des groupes deja sauvegardes au GUI
		for(Group g : groups)
			gui.addGroup(g);
	}
	
	/**
	 * Deconnecte l'utilisateur et l'annonce a tout le monde
	 * @throws IOException Si une erreur survient
	 * @throws ConnectionError Si le serveur n'est pas accessible
	 * @throws SendDeconnectionError Si une erreur survient lors de l'envoi des donnees
	 */
	public void deconnect() throws IOException, ConnectionError, SendDeconnectionError {
		
		// Tous les groupes de l'utilisateur sont maintenant offline
		for(Group g : groups)
			g.setOnline(false);
		
		// Enregistrement des donnees
		DataManager.writeAllMessages(messages);
		DataManager.writeAllGroups(groups);

		// Si on utilise le serveur
		if(useServer) {
			// Connexion au serveur et envoie des donnees au format JSON
			Gson gson = new Gson();
			
			// Creation des donnees utilisateur
			String jsonData = gson.toJson(user);
			String paramValue = "userdata=" + jsonData;
			
			// Test de la connexion
			if(!testConnectionServer())
				throw new ConnectionError();
			
			// Connexion au serveur et traitement de la reponse
			HttpURLConnection con = sendRequestToServer(ChatSystemServer.ChatServer.ACTION_USER_DECONNECTION, paramValue);		
			
			int status = con.getResponseCode();
			if(status != HttpURLConnection.HTTP_OK)
				throw new SendDeconnectionError();
			
			String jsonResponse = getResponseContent(con);
			ServerResponse serverResponse = gson.fromJson(jsonResponse, ServerResponse.class);
	
			if(serverResponse.getCode() != ChatServer.NO_ERROR)
				throw new SendDeconnectionError();
		}
		
		// Si on utilise le service UDP
		else {
			udp.sendUdpMessage(udp.createMessage(Udp.STATUS_DECONNEXION, getUser()), ipBroadcast);
		}
		
	}

	/**
	 * Recoit les informations d'un utilisateur qui vient de se connecter (via UDP)
	 * @param receivedUser
	 */
	public void receiveConnection(User receivedUser) {
		
		if(receivedUser == null)
			return;

//		System.out.println("connexion recue! iduser=" +receivedUser.getID());

		boolean listHasChanged = false;
		boolean userHasChanged = false;
		
		// On verifie qu'on ne recoit pas sa propre annonce et qu'on ne connait pas deja l'utilisateur
		if(!connectedUsers.contains(receivedUser) && !receivedUser.equals(user)) {
			userHasChanged = true;
			listHasChanged = true;
			connectedUsers.add(receivedUser);
		}
		
		// Mise a jour des groupes avec les nouvelles informations de l'utilisateur connecte
		String oldUsername = "", newUsername = "";
		
		for(Group group : groups) {
			oldUsername = group.getGroupNameForUser(user);
			userHasChanged = userHasChanged || group.updateMember(receivedUser);
			newUsername = group.getGroupNameForUser(user);
		}
		
		if(userHasChanged) {
			listHasChanged = true;
			
			// Mise a jour des messages avec les nouvelles informations de l'utilisateur
			for(Message m : messages)
				m.updateSender(receivedUser);
		}
		
		// Ajout du nouvel utilisateur (GUI)
		if(gui != null)
			gui.updateConnectedUsers();
		
		// Mise a jour des usernames
		if(listHasChanged)
			gui.replaceUsernameInList(oldUsername, newUsername);

	}
	
	/**
	 * Recoit la deconexion d'un utilisateur (via UDP)
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
	 * Met a jour la liste des utilisateurs connectes (et leurs informations)
	 * Les donnees sont envoyees par le serveur
	 * @param receivedUsers La liste des utilisateurs connectes sur le serveur
	 */
	public void receiveConnectedUsersFromServer(ArrayList<User> receivedUsers) {
	
		boolean hasNewUser = false;
		boolean listHasChanged = false;
		
		String oldUsername = "", newUsername = "";
		
		// Utilise pour supprimer les utilisateurs deconnectes
		ArrayList<User> disconnectedUsers = new ArrayList<User>(connectedUsers);
		
		// On traite chaque utilisateur recu
		for(User u : receivedUsers) {
			
			disconnectedUsers.remove(u);
			
			boolean userHasChanged = false;
			
			// On verifie qu'on ne recoit pas sa propre annonce et qu'on ne connait pas deja l'utilisateur
			if(!connectedUsers.contains(u) && !u.equals(user)) {
				userHasChanged = true;
				hasNewUser = true;
				listHasChanged = true;
				connectedUsers.add(u);
			}
			
			// Mise a jour des groupes avec les nouvelles informations de l'utilisateur connecte
			for(Group group : groups) {
				oldUsername = group.getGroupNameForUser(user);
				userHasChanged = userHasChanged || group.updateMember(u);
				newUsername = group.getGroupNameForUser(user);
			}
			
			if(userHasChanged) {
				listHasChanged = true;
				
				// Mise a jour des messages avec les nouvelles informations de l'utilisateur
				for(Message m : messages)
					m.updateSender(u);
			}
		}
		
		// Gestion des utilisateurs deconnectes
		if(!disconnectedUsers.isEmpty()) {
			hasNewUser = true;
			
			for(User u : disconnectedUsers)
				connectedUsers.remove(u);
		}
		
		if(hasNewUser) {
			// Ajout du nouvel utilisateur (GUI)
			if(gui != null)
				gui.updateConnectedUsers();
		}
		
		// Mise a jour des usernames
		if(listHasChanged)
			gui.replaceUsernameInList(oldUsername, newUsername);
		
	}
	
	
	/***************************** Methodes liees a l'envoi des messages et a la gestion des conversations *****************************/
	
	/**
	 * Permet d'envoyer un message
	 * @param textToSend Le contenu du message a envoyer
	 * @param receiverGroupNameForUser Le groupe a qui envoyer le message. Le nom de groupe est different selon l'utilisateur.
	 * @param function La fonction du message
	 * @throws IOException Si erreur lors de l'envoi du message
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
		
		gui.setGroupNoRead(group);
	}

	/**
	 * Demarre une nouvelle conversation
	 * @param members Utilisateurs presents dans la conversation
	 * @return Le groupe cree
	 * @throws IOException Si une erreur survient
	 */
	private Group startGroup(ArrayList<User> members) throws IOException {
		
		User contact = members.get(0);

		// Random pour l'ID du nouveau groupe
		Random rand = new Random();
		int idGroup = rand.nextInt(999999999);
		
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
	 * @throws IOException Si une erreur survient
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
	
	
	/***************************** Methodes liees a la gestion de l'utilisateur *****************************/

	/**
	 * Modifie le username de l'utilisateur
	 * @param newUsername Le nouveau username
	 * @throws IOException Si erreur a l'ecriture du fichier
	 * @throws ClassNotFoundException Si erreur a l'ecriture du fichier
	 */
	public void editUsername(String newUsername) throws IOException, ClassNotFoundException {
		
		DataManager.changeUsername(newUsername);
		
		user.setUsername(newUsername);
		
		udp.sendUdpMessage(udp.createMessage(Udp.STATUS_USERNAME_CHANGED, user), ipBroadcast);
		
	}
	
	/**
	 * Modifie le mot de passe de l'utilisateur
	 * @param oldPassword L'ancien mot de passe
	 * @param newPassword Le nouveau mot de passe
	 * @throws ClassNotFoundException Si erreur a l'ecriture du fichier
	 * @throws NoSuchAlgorithmException Si erreur a l'ecriture du fichier
	 * @throws IOException Si erreur a l'ecriture du fichier
	 * @throws PasswordError Si erreur a l'ecriture du fichier
	 */
	public void editPassword(char[] oldPassword, char[] newPassword) throws ClassNotFoundException, NoSuchAlgorithmException, IOException, PasswordError {
		DataManager.changePassword(oldPassword, newPassword);		
	}
	
	/**
	 * Indique qu'on a recu un changement de pseudo d'un utilisateur
	 * @param receivedUser
	 */
	public void receiveUsernameChanged(User receivedUser) {
		
		String oldUsername = "";

		// Mise a jour de l'utilisateur concerne
		for(User u : connectedUsers) {
			if(u.equals(receivedUser)) {
				oldUsername = u.getUsername();
				connectedUsers.remove(u);
				connectedUsers.add(receivedUser);
				break;
			}
		}
		
		// Mise a jour des groupes avec les nouvelles informations de l'utilisateur
		for(Group group : groups)
			group.updateMember(receivedUser);
		
		// Mise a jour des messages avec les nouvelles informations de l'utilisateur
		for(Message m : messages)
			m.updateSender(receivedUser);
		
		// Mise a jour du GUI
		gui.replaceUsernameInList(oldUsername, receivedUser.getUsername());

	}
	
	/***************************** Methodes liees a l'utilisation du serveur *****************************/

	/**
	 * Envoie une requete au serveur
	 * @param action L'action demandee au serveur
	 * @param paramValue La valeur du parametre passe
	 * @return La connexion au serveur (contenant le status, la reponse, etc.)
	 * @throws IOException Si le serveur est inaccessible
	 */
	public static HttpURLConnection sendRequestToServer(int action, String paramValue) throws IOException {
		
		// Creation de l'URL
		URL url = new URL("http://" + serverIP + ":" + serverPort + PATH_WEBPAGE +"?action=" + action + "&" + paramValue);
		
		// Envoi de la requete
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setConnectTimeout(500);
		con.setReadTimeout(500);
			
		return con;
	}
	
	/**
	 * Teste si le serveur est accessible
	 * @param ip L'IP du serveur
	 * @param port Le port sur lequel se connecter
	 * @return True si le serveur est accessible, False sinon
	 */
	public static boolean testConnectionServer() {
		
		try {
			// Creation de l'URL
			URL url = new URL("http://" + serverIP + ":" + serverPort + PATH_WEBPAGE);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			con.setConnectTimeout(timeoutConnection);
			
			// Renvoie True si tout se passe bien
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		
		// Permet de détecter un timeout
		catch (IOException e) {
			return false;
		}

	}
	
	/**
	 * Retourne le contenu texte d'une reponse du serveur
	 * @param con La connexion au serveur
	 * @return Le contenu texte de la reponse
	 * @throws IOException Si une erreur dans la connexion survient
	 */
	public static String getResponseContent(HttpURLConnection con) throws IOException {
		
		int responseCode = con.getResponseCode();
		InputStream inputStream;
		
		if(200 <= responseCode && responseCode <= 299)
			inputStream = con.getInputStream();
		else
			inputStream = con.getErrorStream();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		
		StringBuilder content = new StringBuilder();
		String currentLine;
		
		while((currentLine = in.readLine()) != null)
			content.append(currentLine);
		
		in.close();
		
		return content.toString();
	}
	
	
	/***************************** Methodes diverses *****************************/
	
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
