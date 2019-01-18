package ChatSystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ChatSystem.Controller.ConnectionError;
import ChatSystem.Controller.SendDeconnectionError;
	
/**
 * Fenetre principale du programme
 *
 */
public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;

	private static Controller controller;
	
	private Map<Integer, Boolean> newMessageGroups = new HashMap<Integer, Boolean>();
	
	private JPanel panel; // Panel principal
	private JButton sendButton; // Bouton Envoyer 
	private JTextField textField; // Zone de texte
	private JEditorPane messagesArea; // Zone des messages
	private JScrollPane scrollMessageArea; // TODO Auto-scroll en bas
	private static JList<String> groupList; // Liste des groupes deja demarres
	private JList<String> connectedUsersList; // Liste des utilisateurs connectes
	private JLabel labelGroups; // Label "Conversations demarees"
	private JLabel labelConnectedUsers; // Label "Utilisateurs connectes"
	private JButton userButton; // Bouton "Profil"
	
	@SuppressWarnings("unchecked")
	public GUI(String username) {
		
		/* Fenetre principale */
		super("Chatsystem");

		addWindowListener(new windowClosingListener());
		setSize(new Dimension(900, 500));
		setLocationRelativeTo(null);
		
		
		/* Panel principal en mode grille */
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(new Color(200,200,200));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1,1,1,1);
		
		
		/* Bouton Envoyer */
		sendButton = new JButton("Envoyer");
		sendButton.addActionListener(new sendMessageListener());
		sendButton.setEnabled(false);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.gridx = 3;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(sendButton, c);
		
		
		/* Zone de texte */
		textField = new JTextField();
		textField.addActionListener(new sendMessageListener());
		textField.setBorder(null);
		textField.setEditable(false);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.6;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel.add(textField, c);
		
		
		/* Zone des messages */
		messagesArea = new JEditorPane();
		messagesArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		messagesArea.setFont(textField.getFont());
		messagesArea.setContentType("text/html");
		messagesArea.setEditable(false);
		messagesArea.setMinimumSize(new Dimension(600, 600));
		messagesArea.setMaximumSize(new Dimension(600, 600));
        messagesArea.setPreferredSize(new Dimension(600, 600));
		
		scrollMessageArea = new JScrollPane(messagesArea);
		scrollMessageArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollMessageArea.setBorder(null);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.8;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 3;
		panel.add(scrollMessageArea, c);
		
		
		/* Liste des groupes deja demarres */
		DefaultListModel<String> groupnames = new DefaultListModel<String>();
		groupList = new JList<String>();
		groupList.setModel(groupnames);
		groupList.setPreferredSize(new Dimension(40,0));
		groupList.addListSelectionListener(new groupListSelectionChange());
		groupList.setCellRenderer(new MyListCellThing(MyListCellThing.STYLE_GROUP));
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 3;
		c.gridwidth = 1;
		panel.add(groupList, c);
		
		labelGroups = new JLabel("Conversations demarrees", SwingConstants.CENTER);
		Font font = labelGroups.getFont();
		labelGroups.setFont(new Font(font.getName(), Font.PLAIN, 11));
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.01;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(labelGroups, c);
		
		
		/* Liste de tous les utilisateurs connectes */
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		connectedUsersList = new JList<String>(usernames);
		connectedUsersList.setPreferredSize(new Dimension(40,0));
		connectedUsersList.addListSelectionListener(new connectedUsersListSelectionChange());
		connectedUsersList.setCellRenderer(new MyListCellThing(MyListCellThing.STYLE_USERS));
		c.weightx = 0.1;
		c.weighty = 2;
		c.gridx = 3;
		c.gridy = 2;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(connectedUsersList, c);
		
		labelConnectedUsers = new JLabel("Utilisateurs connectes", SwingConstants.CENTER);
		font = labelConnectedUsers.getFont();
		labelConnectedUsers.setFont(new Font(font.getName(), Font.PLAIN, 11));
		c.weightx = 0.1;
		c.weighty = 0.01;
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(labelConnectedUsers, c);
		
		
		/* Bouton "Profil" */
		userButton = new JButton("Mon profil");
		userButton.addActionListener(new editUserListener(this));
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(userButton, c);
		
		
		/* Affichage */
		add(panel);
		setVisible(true);
		
	}
	
	/**
	 * Permet de mettre en forme les listes des groupes et des utilisateurs connectes
	 */
	@SuppressWarnings("rawtypes")
	public class MyListCellThing extends JLabel implements ListCellRenderer {
		
		private static final long serialVersionUID = 1L;

		private int style;
		
		// Pour ne pas mettre en gras le nom des utilisateurs connectes (uniquement les groupes)
		public static final int STYLE_GROUP = 1;
		public static final int STYLE_USERS = 2;

		/**
		 * Cree une nouvelle liste
		 */
	    public MyListCellThing(int style) {
	        setOpaque(true);
	        this.style = style;
	    }
	    
	    /**
	     * Permet de definir le rendu graphique de la liste
	     */
	    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

	        setText(value.toString());

	        String groupName = list.getModel().getElementAt(index).toString();
	        Group selectedGroup = controller.getGroupByName(groupName);
	        
	        // On met en gras dans la liste des groupes si nouveau message
	        if(style == STYLE_GROUP && selectedGroup != null &&
	        	newMessageGroups.containsKey(selectedGroup.getID()) && newMessageGroups.get(selectedGroup.getID()))
	        	
	        	setFont(getFont().deriveFont(Font.BOLD));
	        else
	        	setFont(getFont().deriveFont(Font.PLAIN));

	        // Coloration de l'item selectionne
	        if(isSelected)
	        	setBackground(new Color(230,230,230));
	        else
	        	setBackground(Color.WHITE);
	        
	        
	        setBorder(new EmptyBorder(10, 10, 10, 10));

	        return this;
	    }
	}
	

	/***************************** LISTENERS *****************************/
	
	/**
	 * Listener du bouton "Profil"
	 */
	public class editUserListener implements ActionListener {
		private GUI gui;
		
		public editUserListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
		// Affichage de la fenetre de profil
		public void actionPerformed(ActionEvent e) {			
			setEnabled(false);
			new GUIModifUser(gui, controller);			
		}
	}
	
	/**
	 * Listener de l'envoi d'un message
	 */
	public class sendMessageListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			// Texte a envoyer
			String textToSend = textField.getText();
			
			if(textToSend.equals(""))
				return;
			
			
			/* Envoi du message */
			try {
				if(connectedUsersList.getSelectedIndex() == -1)
					return;
				
				// Obtention du groupe a partir de son nom
				String groupName = connectedUsersList.getSelectedValue();
				
				// Envoi du message
				controller.sendMessage(textToSend, groupName, Message.FUNCTION_NORMAL);
				
				// RAZ de la zone de texte
				textField.setText(null);
				
				displayMessages(controller.getGroupByName(groupName));
			
			} catch (Exception e1) {
				showError("Impossible d'envoyer le message à cet utilisateur.");
			}

		}
		
	}
	
	/**
	 * Listener de la fermeture de la fenetre
	 */
	public class windowClosingListener implements WindowListener {
		
		public void windowClosing(WindowEvent e) {
			
			try {
				// Deconnexion de l'utilisateur
				controller.deconnect();
				//controller.sendMessage(null, 0, Message.FUNCTION_STOP); // TODO pourquoi ?
				
				// Fin du programme sans erreur
				System.exit(Controller.EXIT_NO_ERROR);
				
			} catch (ConnectionError | SendDeconnectionError err) {
				showError("Une erreur est survenue lors de la connexion au serveur.");
				System.exit(Controller.EXIT_ERROR_SEND_DECONNECTION);
			} catch (IOException err) {
				showError("Une erreur est survenue lors de la déconnexion.");
				System.exit(Controller.EXIT_ERROR_SEND_DECONNECTION);
			}

		}
		
		public void windowOpened(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowActivated(WindowEvent arg0) {}
		public void windowDeactivated(WindowEvent arg0) {}

	}
	
	/**
	 * Listener du changement de groupe selectionne
	 */
	public class groupListSelectionChange implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()) {
				
				Group selectedGroup = controller.getGroupByName(groupList.getSelectedValue());
				
				// Selection de l'utilisateur correspond (si connecte)
				int index;
				boolean inList = false;
				for (index = 0; index < connectedUsersList.getModel().getSize(); index++) {
					String username = connectedUsersList.getModel().getElementAt(index);
					if (username.equals(groupList.getSelectedValue())) {
						connectedUsersList.setSelectedIndex(index);
						inList = true;
					} 
				}
				if (inList == false) {
					connectedUsersList.clearSelection();
					textField.setEditable(false);		
					sendButton.setEnabled(false);
				}
				
				
				// Rafraichissement des messages
				displayMessages(selectedGroup);
				
				if(selectedGroup != null)
					newMessageGroups.put(selectedGroup.getID(), false);
				
			}

		}

	}
	
	/**
	 * Listener du changement d'utilisateur connecte selection
	 */
	public class connectedUsersListSelectionChange implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()) {
				
				// (Des)activation de la zone de texte et du bouton "Envoyer"
				if(connectedUsersList.getSelectedIndex() == -1) {
					textField.setEditable(false);
					sendButton.setEnabled(false);
				}
				else {
					textField.setEditable(true);
					sendButton.setEnabled(true);
				}
				

				// Selection du groupe corespondant
				for(int index = 0; index < groupList.getModel().getSize(); index ++) {
					String username = groupList.getModel().getElementAt(index);
					
					if (username.equals(connectedUsersList.getSelectedValue())){
						groupList.setSelectedIndex(index);
						break;
					}
				}
				
			}
			
		}
		
	}
	
	/***************************** Methodes diverses *****************************/
	
	/**
	 * Met a jour la liste des utilisateurs connectes (GUI)
	 */
	public void updateConnectedUsers() {
		
		String prevSelected = groupList.getSelectedValue();
		
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers;
		connectedUsers = controller.getConnectedUsers();
		
		int selectedIndex = -1;
		int i = 0;
		for(User u : connectedUsers) {
			if(u.getUsername().equals(prevSelected))
				selectedIndex = i;
			
			usernames.addElement(u.getUsername());
			
			i++;
		}
		
		connectedUsersList.setModel(usernames);
		
		// Garde l'utilisateur selectionne
		if(selectedIndex >= 0)
			connectedUsersList.setSelectedIndex(selectedIndex);
	}
	
	/**
	 * Met a jour les noms de la liste des groupes (GUI)
	 * @param updatedGroup Le groupe recevant un nouveau message (null si c'est un clic de l'utilisateur)
	 */
	public void setGroupNoRead(Group updatedGroup) {
		
		// Indique qu'il y a un nouveau message pour updatedGroup
		newMessageGroups.put(updatedGroup.getID(), true);
		
		DefaultListModel<String> groupNames = new DefaultListModel<String>();
		
		String selectedGroupName = groupList.getSelectedValue();
		int selectedIndex = -1;
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String groupName = groupList.getModel().getElementAt(i);
			groupNames.addElement(groupName);
			
			if(groupName.equals(selectedGroupName))
				selectedIndex = i;
		}
		
		groupList.setModel(groupNames);
		
		// Garde le groupe selectionne
		if(selectedIndex >= 0)
			groupList.setSelectedIndex(selectedIndex);
		
	}
	
	/**
	 * Permet de selectionner un groupe dans la liste
	 * @param selectedGroup Le groupe a selectionner
	 */
	public void selectGroupInList(Group selectedGroup) {
		
		// Cherche le groupe dans la liste a partir de son nom
		String selectedGroupName = selectedGroup.getGroupNameForUser(controller.getUser());
		int selectedIndex = -1;
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String groupName = groupList.getModel().getElementAt(i);
			
			if(groupName.equals(selectedGroupName))
				selectedIndex = i;
		}
		
		// Garde le groupe selectionne
		if(selectedIndex >= 0)
			groupList.setSelectedIndex(selectedIndex);
	}
	
	/**
	 * Permet de modifier le username d'un utilisateur dans les deux listes
	 * @param oldUsername L'ancien username
	 * @param newUsername Le nouveau username
	 */
	public void replaceUsernameInList(String oldUsername, String newUsername) {
		
		// Remplacement dans la liste des groupes
		int selectedIndex = groupList.getSelectedIndex();
		DefaultListModel<String> groupNames = new DefaultListModel<String>();
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String groupName = groupList.getModel().getElementAt(i);
			
			if(groupName.equals(oldUsername))
				groupNames.addElement(newUsername);
			else
				groupNames.addElement(groupName);
		}
		
		groupList.setModel(groupNames);
		
		if(selectedIndex >= 0)
			groupList.setSelectedIndex(selectedIndex);
		
		
		
		// Remplacement dans la liste des utilisateurs connectes
		selectedIndex = connectedUsersList.getSelectedIndex();
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		
		for(int i=0; i<connectedUsersList.getModel().getSize(); i++) {
			String username = connectedUsersList.getModel().getElementAt(i);
			
			if(username.equals(oldUsername))
				usernames.addElement(newUsername);
			else
				usernames.addElement(username);
		}
		
		connectedUsersList.setModel(usernames);
		
		if(selectedIndex >= 0)
			connectedUsersList.setSelectedIndex(selectedIndex);
		
	}
	
	/**
	 * Permet d'ajouter un groupe a la liste
	 * @param group Le groupe a ajouter
	 */
	public void addGroup(Group group) {
		
		DefaultListModel<String> groupNames = new DefaultListModel<String>();
		
		String selectedGroupName = groupList.getSelectedValue();
		int selectedIndex = -1;
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String groupName = groupList.getModel().getElementAt(i);
			groupNames.addElement(groupName);
			
			if(groupName.equals(selectedGroupName))
				selectedIndex = i;
		}
		
		groupNames.addElement(group.getGroupNameForUser(controller.getUser()));
		
		groupList.setModel(groupNames);
		
		if(selectedIndex >= 0)
			groupList.setSelectedIndex(selectedIndex);
		
	}
	
	/**
	 * Permet de rafraichir la zone des messages pour le groupe selectionne
	 * @param selectedGroup Le groupe dont les messages doivent etre affiches (null si le groupe n'existe pas ou aucun selectionne)
	 */
	private void displayMessages(Group selectedGroup) {
		
		if(selectedGroup != null) {
			ArrayList<Message> groupMessages = controller.getGroupMessages(selectedGroup);
			
			String history = "<style type='text/css'>"
					+ ".message-sent{margin:3px 5px 3px 50px;padding:0 5px 5px 5px;background:#FF8075;color:white;font-size:14pt;}"
					+ ".message-received{margin:3px 50px 3px 5px;padding:0 5px 5px 5px;background:#eeeeee;color:black;font-size:14pt;}"
					+ ".date-sent{font-size:11pt;color:white;}"
					+ ".date-received{font-size:11pt;color:black;}"
					+ ".user-sent{font-size:11pt;color:#888888;margin:3px 0 0 55px;}"
					+ ".user-received{font-size:11pt;color:#888888;margin:3px 0 0 10px;}"
					+ "</style>";
			
			// Utilise pour ne pas repeter le nom de l'utilisateur si plusieurs messages consecutifs
			User prevSender = null;
			
			for(Message m : groupMessages) {
				String username, date, content = m.getContent();
				
				// Format de la date
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				date = dateFormat.format(m.getDate());
				
				// Message envoye par moi
				if(m.getSender().equals(controller.getUser())) {
					username = "<div class='user-sent'>Moi</div>";
					date = "<span class='date-sent'>" + date + "</span>";
					content = "<div class='message-sent'>" + date + "<br>" + content + "</div>";
				}
				// Message envoye par l'autre utilisateur
				else {
					username = "<div class='user-received'>" + m.getSender().getUsername() + "</div>";
					date = "<span class='date-received'>" + date + "</span>";
					content = "<div class='message-received'>" + date + "<br>" + content + "</div>";
				}
				
				if(m.getSender().equals(prevSender))
					history += content;
				else
					history += username + content;
				
				prevSender = m.getSender();
			}
			
			messagesArea.setText(history);
			// TODO Scroll auto
		
		}
		else {
			messagesArea.setText(null);
		}
		
	}
	
	/**
	 * Affiche une erreur a l'utilisateur
	 * @param errorMessage Le message d'erreur a afficher
	 */
	public static void showError(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
	}


	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException, UnknownHostException {	

		// Recupere la liste des adresses IP que possede la machine (et les adresses de broadcast correspondantes)
		Map<InetAddress, InetAddress> allIP = Controller.getAllIpAndBroadcast();
		InetAddress ipMachine;
		String username;
		int id;
		GUIConnect guiConnect = new GUIConnect(new ArrayList<InetAddress>(allIP.keySet()));
		
		// Attente de la connexion de l'utilisateur
		while(guiConnect.getStatusConnexion() == false);		
		ipMachine = guiConnect.getIPSelected();
		username = guiConnect.getUsername();
		id = guiConnect.getId();

		
		try {

			// On teste la connexion du serveur ici pour ne pas afficher la fenetre principale s'il y a une erreur
			boolean useServer = (DataManager.getSetting("general", "use_server", "0").equals("1")) ? true : false;
			
			if(useServer) {
				String serverIP = DataManager.getSetting("server", "ip", "0.0.0.0");
				int serverPort = Integer.parseInt(DataManager.getSetting("server", "port", "-1"));
				
				// Cree le controller en utilisant le serveur
				controller = new Controller(allIP.get(ipMachine), serverIP, serverPort);
				
				if(!Controller.testConnectionServer()) {
					showError("Impossible de se connecter au serveur.\nVerifiez la configuration de la connexion ou utilisez le protocole UDP.");
					System.exit(Controller.EXIT_ERROR_SERVER_UNAVAILABLE);
				}
			}
			else {
				// Cree le controller sans serveur (utilisation de UDP)
				controller = new Controller(allIP.get(ipMachine), null, -1);
			}
			
			controller.setGUI(new GUI(username));
			controller.connect(id, username, ipMachine);

		} catch (NumberFormatException e) {
			// Mauvaise configuration du timeout dans le fichier ini
			showError("Impossible de se connecter au serveur.\nVerifiez la configuration de la connexion ou utilisez le protocole UDP.");
			System.exit(Controller.EXIT_ERROR_SERVER_UNAVAILABLE);
		} catch (IOException e) {
			showError("Une erreur s'est produite dans la decouverte du reseau (Protocole UDP).");
			System.exit(Controller.EXIT_ERROR_GET_CONNECTED_USERS);
		}
		
	}
}
