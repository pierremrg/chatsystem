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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

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

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
	
/**
 * Fenetre principale du programme
 *
 */
public class GUI extends JFrame{
	
	private static Controller controller;
	private Map<Integer, Boolean> newMessageGroups = new HashMap<Integer, Boolean>();
	
	private JPanel panel; // Panel principal
	private JButton sendButton; // Bouton Envoyer 
	private JTextField textField; // Zone de texte
	//private JTextArea messagesArea; // Zone des messages
	private JEditorPane messagesArea;
	private JScrollPane scrollMessageArea;
	private static JList<String> groupList; // Liste des groupes déjà démarrés
	private JList<String> connectedUsersList; // Liste des utilisateurs connectés
	private JLabel labelGroups; // Label "Conversations démarrées"
	private JLabel labelConnectedUsers; // Label "Utilisateurs connectés"
	private JButton userButton; //Bouton info profil
	
	
	
	public GUI(String username) {
		/* Fenêtre principale */
		super("Chatsystem");
		//setDefaultCloseOperation(new windowClosingListener());
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
		//messagesArea = new JTextArea();
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
		
		
		/* Liste des groupes déjà démarrés */
		
		
		// TODO a supprimer
		/*usernames.addElement("jean");
		usernames.addElement("truc");
		DefaultListModel<String> groupnames = new DefaultListModel<String>();
		ArrayList<Group> startedGroups = controller.getGroups();
		//ArrayList<Group> startedGroups = new ArrayList<Group>();
		
		// TODO vide au début ?
		for(Group g : startedGroups)
			groupnames.addElement(g.getGroupNameForUser(controller.getUser()));*/
		
		DefaultListModel<String> groupnames = new DefaultListModel<String>();
		/*groupnames.addElement("jean");
		groupnames.addElement("truc");
		groupnames.addElement("titi");*/
		
		groupList = new JList<String>();
		groupList.setModel(groupnames);
		//groupList.setBorder(BorderFactory.createRaisedBevelBorder());
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
		//ArrayList<User> connectedUsers = controller.getConnectedUsers();
		//ArrayList<User> connectedUsers = new ArrayList<User>();
		
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		// TODO vide au début ?
		/*for(User u : connectedUsers)
			usernames.addElement(u.getUsername());*/
		
		userButton = new JButton("Mon profil");
		userButton.addActionListener(new modifUserListener(this));
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(userButton, c);
		
		connectedUsersList = new JList<String>(usernames);
		//connectedUsersList.setBorder(BorderFactory.createRaisedBevelBorder());
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
		
		
		/* Affichage */
		add(panel);
		setVisible(true);
		
	}
	
	public class MyListCellThing extends JLabel implements ListCellRenderer {
		
		int style;
		
		public static final int STYLE_GROUP = 1;
		public static final int STYLE_USERS = 2;

	    public MyListCellThing(int style) {
	        setOpaque(true);
	        this.style = style;
	    }

	    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        // Assumes the stuff in the list has a pretty toString
	        setText(value.toString());

	        // based on the index you set the color.  This produces the every other effect.
	        //if (index % 2 == 0) setBackground(Color.RED);
	        //else setBackground(Color.BLUE);
	        
	        //if(list.getModel().getElementAt(index).toString().startsWith(NEW_MESSAGE_INDICATOR))
	        String groupName = list.getModel().getElementAt(index).toString();
	        Group selectedGroup = controller.getGroupByName(groupName);
	        
	        if(style == STYLE_GROUP && selectedGroup != null &&
	        	newMessageGroups.containsKey(selectedGroup.getID()) && newMessageGroups.get(selectedGroup.getID()))
	        	
	        	setFont(getFont().deriveFont(Font.BOLD));
	        else
	        	setFont(getFont().deriveFont(Font.PLAIN));

	        if(isSelected)
	        	setBackground(new Color(230,230,230));
	        else
	        	setBackground(Color.WHITE);
	        
	        
	        setBorder(new EmptyBorder(10, 10, 10, 10));
	        	

	        return this;
	    }
	}

	public class modifUserListener implements ActionListener {
		private GUI gui;
		
		public modifUserListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
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
			
			/*ArrayList<User> members0 = new ArrayList<User>();
			members0.add(new User(5, "truc", null));
			
			Group group0 = new Group(0, members0, members0.get(0));
			
			controller.receiveMessage(new Message(new Date(), "coucou", members0.get(0), group0, Message.FUNCTION_NORMAL));*/
			
			String textToSend = textField.getText();
			
			if(textToSend.equals(""))
				return;
			
			textField.setText(null);
			
			/* Envoi du message */
			// TODO
			try {
				if(connectedUsersList.getSelectedIndex() == -1)
					return;
				
				// TODO on crée le groupe ici ou on garde que l'ID ? que le nom ?
				String groupName = connectedUsersList.getSelectedValue();

				controller.sendMessage(textToSend, groupName, Message.FUNCTION_NORMAL);
				
				displayMessages(controller.getGroupByName(groupName));
			
			} catch (Exception e1) {
				showError("Impossible d'envoyer le message à cet utilisateur.");
			}

		}
		
	}
	
	public class windowClosingListener implements WindowListener {
		
		public void windowClosing(WindowEvent e) {
			
			try {
				controller.deconnect();
				//controller.sendMessage(null, 0, Message.FUNCTION_STOP); // TODO pourquoi ?
			} catch (IOException e1) {
				showError("Une erreur est survenue lors de la déconnexion.");
			}
			finally {
				// Fin du programme sans erreur
				System.exit(0);
			}
		}
		
		public void windowOpened(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowActivated(WindowEvent arg0) {}
		public void windowDeactivated(WindowEvent arg0) {}

	}
	
	public class groupListSelectionChange implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()) {
				
				// Mise à jour des noms des groupes
				Group selectedGroup = controller.getGroupByName(groupList.getSelectedValue());
				
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
				
				
				displayMessages(selectedGroup);
				
				if(selectedGroup != null)
					newMessageGroups.put(selectedGroup.getID(), false);
				
			}
			
//			if(refreshNumber < 2 && !e.getValueIsAdjusting()) {
//				//System.out.println("Group changed " + refreshNumber);
//				refreshNumber++;
//			
//				// Mise à jour des noms des groupes
//				String selectedGroupName = getRealGroupName(groupList.getSelectedValue());
//				
//				DefaultListModel<String> groupNames = new DefaultListModel<String>();
//				
//				for(int i=0; i<groupList.getModel().getSize(); i++) {
//					
//					String realGroupName = getRealGroupName(groupList.getModel().getElementAt(i));
//					groupNames.addElement(realGroupName);
//					
//					/*if(!realGroupName.equals(selectedGroupName))
//						groupNames.addElement(realGroupName);
//					else
//						groupNames.addElement(selectedGroupName);*/
//				}
//				
//				int selectedIndex = groupList.getSelectedIndex();
//				groupList.setModel(groupNames);
//				groupList.setSelectedIndex(selectedIndex);
//				
//				
//				
//				Group selectedGroup = controller.getGroupByName(getRealGroupName(selectedGroupName));
//				
//				displayMessages(selectedGroup);
//				
//				/*if(selectedGroup != null) {
//					ArrayList<Message> groupMessages = controller.getGroupMessages(selectedGroup);
//					
//					String history = "";
//					
//					
//					for(Message m : groupMessages)
//						history += m.getContent() + "\n";
//						
//					
//					if(history.equals(null))
//						messagesArea.setText(null);
//					else
//						messagesArea.setText(history);
//				
//				}
//				else {
//					// TODO erreur
//					//System.out.println("Erreur groupe inexistant");
//				}*/
//				
//			}
//			else
//				refreshNumber = 0;

			
		}

	}
	
	public class connectedUsersListSelectionChange implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()) {
				
				if(connectedUsersList.getSelectedIndex() == -1) {
					textField.setEditable(false);
					sendButton.setEnabled(false);
				}
				else {
					textField.setEditable(true);
					sendButton.setEnabled(true);
				}
				

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
			
		
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		
		connectedUsersList.setModel(usernames);
		
		if(selectedIndex >= 0)
			connectedUsersList.setSelectedIndex(selectedIndex);
	}
	
	/**
	 * Met a jour les noms de la liste des groupes (GUI)
	 * @param updatedGroup Le groupe recevant un nouveau message (null si c'est un clic de l'utilisateur)
	 */
	public void setGroupNoRead(Group updatedGroup) {
		
		//if(!existReadGroup(updatedGroup))
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
		
		if(selectedIndex >= 0) {
			groupList.setSelectedIndex(selectedIndex);
			//displayMessages(updatedGroup);
		}
			
		
		
		
		
		// Nom du groupe dans la liste (pour cet utilisateur)
		/*String updatedGroupName = updatedGroup.getGroupNameForUser(controller.getUser());
		
		
		String selectedGroupName = groupList.getSelectedValue();
		int selectedIndex = 0;
		
		// Liste des groupes a afficher
		DefaultListModel<String> groupNames = new DefaultListModel<String>();
		
		// On met en evidence le groupe avec le nouveau message (indicateur + 1ere place)
		groupNames.addElement(NEW_MESSAGE_INDICATOR + updatedGroupName);
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String realGroupName = getRealGroupName(groupList.getModel().getElementAt(i));
			
			if(!realGroupName.equals(updatedGroupName))
				groupNames.addElement(realGroupName);
			
			if(realGroupName.equals(selectedGroupName))
				selectedIndex = i;
		}
		
		groupList.setModel(groupNames);*/
		//groupList.setSelectedIndex(selectedIndex);
		
	}
	
	public void selectGroupInList(Group selectedGroup) {
		String selectedGroupName = selectedGroup.getGroupNameForUser(controller.getUser());
		int selectedIndex = -1;
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			String groupName = groupList.getModel().getElementAt(i);
			
			if(groupName.equals(selectedGroupName))
				selectedIndex = i;
		}
		
		if(selectedIndex >= 0)
			groupList.setSelectedIndex(selectedIndex);
	}
	
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
				
				//history += date + " <strong>" + username + "</strong> : " + content + "<br/>";
			}
				
				
			
			if(history.equals(null))
				messagesArea.setText(null);
			else
				messagesArea.setText(history);
		
		}
		else {
			// TODO erreur
			messagesArea.setText(null);
			//System.out.println("Erreur groupe inexistant");
		}
		
	}
	
	/**
	 * Affiche une erreur
	 * @param errorMessage Le message d'erreur a afficher
	 */
	public static void showError(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
	}


	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException, UnknownHostException {	
		
		// TODO Tests : a supprimer
		/*try {
			URL url = new URL("http://localhost:8080/ChatSystemServer/ChatServer?toto=5&titi=2");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
//			con.setRequestProperty("Content-Type", "text/html");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			
			int status = con.getResponseCode();
			System.out.println("Status: " + status);
			
			String inputLine;
			StringBuffer content = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			while((inputLine = in.readLine()) != null)
				content.append(inputLine);
			
			System.out.println("Response :" + content.toString());
			
			File iniFile = new File("settings.ini");
			if(!iniFile.exists() || iniFile.isDirectory()) {
				System.out.println("Fichier de configuration inexistant.");
				return;
			}
			
			Ini ini = new Ini(iniFile);
			Preferences prefs = new IniPreferences(ini);
			System.out.println(prefs.node("toto").get("a", "none"));
			
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		
		// TODO A supprimer
		System.out.println("Main started");
		
		Map<InetAddress, InetAddress> allIP = Controller.getAllIpAndBroadcast();
		InetAddress ipMachine;
		String username;
		int id;
		GUIConnect guiConnect = new GUIConnect(new ArrayList<InetAddress>(allIP.keySet()));
		
		while(guiConnect.getStatusConnexion() == false);		
		ipMachine = guiConnect.getIPSelected();
		username = guiConnect.getUsername();
		id = guiConnect.getId();

		
		try {
			controller = new Controller(allIP.get(ipMachine));
			controller.setGUI(new GUI(username));
			controller.connect(id, username, ipMachine);
			
		} catch (IOException e) {
			showError("Une erreur s'est produite dans le service UDP.");
			
		} catch (Exception e) {
			showError("Une erreur s'est produite à l'ouverture.");
			System.exit(1);
		}
		
	}
}
