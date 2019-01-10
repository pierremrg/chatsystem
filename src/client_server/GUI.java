package client_server;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
	
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
	private JTextArea messagesArea; // Zone des messages
	private JScrollPane scrollMessageArea;
	private static JList<String> groupList; // Liste des groupes déjà démarrés
	private JList<String> connectedUsersList; // Liste des utilisateurs connectés
	private JLabel labelGroups; // Label "Conversations démarrées"
	private JLabel labelConnectedUsers; // Label "Utilisateurs connectés"
	
	private static final String NEW_MESSAGE_INDICATOR = "- ";
	
	
	public GUI() {
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
		c.gridy = 2;
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
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel.add(textField, c);
		
		
		/* Zone des messages */
		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		
		scrollMessageArea = new JScrollPane(messagesArea);
		scrollMessageArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollMessageArea.setBorder(null);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.8;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 2;
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
		groupnames.addElement("jean");
		groupnames.addElement("truc");
		groupnames.addElement("titi");
		
		groupList = new JList<String>();
		groupList.setModel(groupnames);
		//groupList.setBorder(BorderFactory.createRaisedBevelBorder());
		groupList.setPreferredSize(new Dimension(40,0));
		groupList.addListSelectionListener(new groupListSelectionChange());
		groupList.setCellRenderer(new MyListCellThing());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 2;
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
		
		usernames.addElement("jean");
		usernames.addElement("truc");
		
		// TODO vide au début ?
		/*for(User u : connectedUsers)
			usernames.addElement(u.getUsername());*/
		
		connectedUsersList = new JList<String>(usernames);
		//connectedUsersList.setBorder(BorderFactory.createRaisedBevelBorder());
		connectedUsersList.setPreferredSize(new Dimension(40,0));
		connectedUsersList.addListSelectionListener(new connectedUsersListSelectionChange());
		c.weightx = 0.1;
		c.weighty = 1;
		c.gridx = 3;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(connectedUsersList, c);
		
		labelConnectedUsers = new JLabel("Utilisateurs connectes", SwingConstants.CENTER);
		font = labelConnectedUsers.getFont();
		labelConnectedUsers.setFont(new Font(font.getName(), Font.PLAIN, 11));
		c.weightx = 0.1;
		c.weighty = 0.01;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(labelConnectedUsers, c);
		
		
		/* Affichage */
		add(panel);
		setVisible(true);
		
	}
	
	public class MyListCellThing extends JLabel implements ListCellRenderer {

	    public MyListCellThing() {
	        setOpaque(true);
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
	        
	        if(selectedGroup != null &&
	        	newMessageGroups.containsKey(selectedGroup.getID()) && newMessageGroups.get(selectedGroup.getID()))
	        	
	        	setFont(getFont().deriveFont(Font.BOLD));
	        else
	        	setFont(getFont().deriveFont(Font.PLAIN));

	        if(isSelected)
	        	setBackground(new Color(230,230,230));
	        else
	        	setBackground(Color.WHITE);
	        	

	        return this;
	    }
	}

	
	/**
	 * Listener de l'envoi d'un message
	 */
	public class sendMessageListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			ArrayList<User> members0 = new ArrayList<User>();
			members0.add(new User(5, "truc", null));
			
			Group group0 = new Group(0, members0, members0.get(0));
			
			controller.receiveMessage(new Message(new Date(), "coucou", members0.get(0), group0, Message.FUNCTION_NORMAL));
			
			/*String textToSend = textField.getText();
			
			String history = messagesArea.getText();
			String newText = "Moi >> " + textToSend;
			
			if(history.equals(""))
				messagesArea.setText(newText);
			else
				messagesArea.setText(history + "\n" + newText);
			
			textField.setText(null);
			
			/* Envoi du message */
			// TODO
			/*try {
				// TODO on crée le groupe ici ou on garde que l'ID ? que le nom ?
				String groupName = getRealGroupName(connectedUsersList.getSelectedValue());

				controller.sendMessage(textToSend, groupName, Message.FUNCTION_NORMAL);
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/

		}
		
	}
	
	public class windowClosingListener implements WindowListener {
		
		public void windowClosing(WindowEvent e) {
			
			try {
				controller.deconnect();
				//controller.sendMessage(null, 0, Message.FUNCTION_STOP);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
		
		private int refreshNumber = 0;

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
				int index;
				for(index = 0; index < groupList.getModel().getSize(); index ++) {
					String username = groupList.getModel().getElementAt(index);
					if (username.equals(connectedUsersList.getSelectedValue())){
						groupList.setSelectedIndex(index);
					}
				}
				textField.setEditable(true);
				sendButton.setEnabled(true);			
			}
			
		}
		
	}
	
	/**
	 * Met a jour la liste des utilisateurs connectes (GUI)
	 */
	public void updateConnectedUsers() {
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers;
		connectedUsers = controller.getConnectedUsers();
		
		for(User u : connectedUsers)
			usernames.addElement(u.getUsername());
		
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		connectedUsersList.setModel(usernames);
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
			displayMessages(updatedGroup);
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
	
	private void displayMessages(Group selectedGroup) {
		
		if(selectedGroup != null) {
			ArrayList<Message> groupMessages = controller.getGroupMessages(selectedGroup);
			
			String history = "";
			
			
			for(Message m : groupMessages)
				history += m.getContent() + "\n";
				
			
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
	
//	private static String getRealGroupName(String groupName) {
//		if(groupName != null && groupName.startsWith(NEW_MESSAGE_INDICATOR))
//			return groupName.substring(NEW_MESSAGE_INDICATOR.length());
//		else
//			return groupName;
//	}


	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException, UnknownHostException {	
		
		System.out.println("Main started");
		
		/*Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:MovieCatalog");
		
		Statement statement = (Statement) con.createStatement();*/
		
		Map<InetAddress, InetAddress> allIP = Controller.getAllIpAndBroadcast();
		InetAddress ipMachine;
		String username ;
		int id;
		GUIConnect guiConnect = new GUIConnect(new ArrayList<InetAddress>(allIP.keySet()));
		
		while(guiConnect.getStatusConnexion() == false);		
		ipMachine = guiConnect.getIPSelected();
		username = guiConnect.getUsername();
		id = guiConnect.getId();

		
		try {
			controller = new Controller(allIP.get(ipMachine));
			controller.setGUI(new GUI());
			controller.connect(id, username, ipMachine);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
