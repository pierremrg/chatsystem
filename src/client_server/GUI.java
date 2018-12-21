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
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
	
public class GUI extends JFrame{
	
	private static Controller controller;
	
	private JPanel panel; // Panel principal
	private JButton sendButton; // Bouton Envoyer 
	private JTextField textField; // Zone de texte
	private JTextArea messagesArea; // Zone des messages
	private JScrollPane scrollMessageArea;
	private static JList<String> groupList; // Liste des groupes déjà démarrés
	private JList<String> connectedUsersList; // Liste des utilisateurs connectés
	private JLabel labelGroups; // Label "Conversations démarrées"
	private JLabel labelConnectedUsers; // Label "Utilisateurs connectés"
	
	private static final String NEW_MESSAGE_INDICATOR = "• ";
	
	
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
		usernames.addElement("truc");*/
		DefaultListModel<String> groupnames = new DefaultListModel<String>();
		ArrayList<Group> startedGroups = controller.getGroups();
		//ArrayList<Group> startedGroups = new ArrayList<Group>();
		
		// TODO vide au début ?
		/*for(Group g : startedGroups)
			groupnames.addElement(g.getGroupNameForUser(controller.getUser()));*/
		
		groupList = new JList<String>(groupnames);
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
		
		labelGroups = new JLabel("Conversations démarrées", SwingConstants.CENTER);
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
		
		
		/* Liste de tous les utilisateurs connectés */
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers = controller.getConnectedUsers();
		//ArrayList<User> connectedUsers = new ArrayList<User>();
		
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
		
		labelConnectedUsers = new JLabel("Utilisateurs connectés", SwingConstants.CENTER);
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
	        
	        if(list.getModel().getElementAt(index).toString().startsWith(NEW_MESSAGE_INDICATOR))
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
			
			String textToSend = textField.getText();
			
			String history = messagesArea.getText();
			String newText = "Moi >> " + textToSend;
			
			if(history.equals(""))
				messagesArea.setText(newText);
			else
				messagesArea.setText(history + "\n" + newText);
			
			textField.setText(null);
			
			/* Envoi du message */
			// TODO
			try {
				// TODO on crée le groupe ici ou on garde que l'ID ? que le nom ?
				String groupName = getRealGroupName(connectedUsersList.getSelectedValue());

				controller.sendMessage(textToSend, groupName, Message.FUNCTION_NORMAL);
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

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
			
			if(refreshNumber < 2 && !e.getValueIsAdjusting()) {
				refreshNumber++;
			
				// Mise à jour des noms des groupes
				String selectedGroupName = getRealGroupName(groupList.getSelectedValue());
				
				DefaultListModel<String> groupNames = new DefaultListModel<String>();
				
				for(int i=0; i<groupList.getModel().getSize(); i++) {
					if(!getRealGroupName(groupList.getModel().getElementAt(i)).equals(selectedGroupName))
						groupNames.addElement(groupList.getModel().getElementAt(i));
					else
						groupNames.addElement(selectedGroupName);
				}
				
				int selectedIndex = groupList.getSelectedIndex();
				groupList.setModel(groupNames);
				groupList.setSelectedIndex(selectedIndex);
				
				
				
				Group selectedGroup = controller.getGroupByName(getRealGroupName(selectedGroupName));
				
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
					//System.out.println("Erreur groupe inexistant");
				}
				
			}
			else
				refreshNumber = 0;

			
		}

	}
	
	public class connectedUsersListSelectionChange implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	public void updateConnectedUsers() {
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers = controller.getConnectedUsers();
		
		for(User u : connectedUsers)
			usernames.addElement(u.getUsername());
		
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		connectedUsersList.setModel(usernames);
	}
	
	
	public void updateMessages(Group updatedGroup) {
		
		String updatedGroupName = updatedGroup.getGroupNameForUser(controller.getUser());
		
		DefaultListModel<String> groupNames = new DefaultListModel<String>();
		groupNames.addElement(NEW_MESSAGE_INDICATOR + updatedGroupName);
		
		for(int i=0; i<groupList.getModel().getSize(); i++) {
			if(!getRealGroupName(groupList.getModel().getElementAt(i)).equals(updatedGroupName))
				groupNames.addElement(groupList.getModel().getElementAt(i));
		}
		
		groupList.setModel(groupNames);
		
		
		Group selectedGroup = controller.getGroupByName(getRealGroupName(updatedGroupName));
		
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
			System.out.println("Erreur groupe inexistant");
		}
		
	}
	
	private static String getRealGroupName(String groupName) {
		if(groupName != null && groupName.startsWith(NEW_MESSAGE_INDICATOR))
			return groupName.substring(2);
		else
			return groupName;
	}


	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException, UnknownHostException {	
		
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
			controller.connect(id, username, ipMachine);
			
			controller.setGUI(new GUI());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
