package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javafx.collections.ListChangeListener;
	
public class GUI extends JFrame{
	
	private static Controller controller;
	
	private JPanel panel; // Panel principal
	private JButton sendButton; // Bouton Envoyer 
	private JTextField textField; // Zone de texte
	private JTextArea messagesArea; // Zone des messages
	private JScrollPane scrollMessageArea;
	private JList<String> usersList; // Liste des utilisateurs
	
	public GUI() {
		/* Fenêtre principale */
		super("Chatsystem");
		//setDefaultCloseOperation(new windowClosingListener());
		addWindowListener(new windowClosingListener());
		setSize(new Dimension(700, 400));
		setLocationRelativeTo(null);
		
		
		/* Panel principal en mode grille */
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		/* Bouton Envoyer */
		sendButton = new JButton("Envoyer");
		sendButton.addActionListener(new sendMessageListener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.2;
		c.gridx = 2;
		c.gridy = 1;
		panel.add(sendButton, c);
		
		
		/* Zone de texte */
		textField = new JTextField();
		textField.addActionListener(new sendMessageListener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridx = 1;
		c.gridy = 1;
		panel.add(textField, c);
		
		
		/* Zone des messages */
		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		
		scrollMessageArea = new JScrollPane(messagesArea);
		scrollMessageArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		panel.add(scrollMessageArea, c);
		
		
		/* Liste des utilisateurs */
		// TODO : à enlever (liste obtenue par le controller)
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers = controller.getConnectedUsers();
		
		for(User u : connectedUsers)
			usernames.addElement(u.getUsername());
		
		// TODO a supprimer
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		usersList = new JList<String>(usernames);
		usersList.setBorder(BorderFactory.createRaisedBevelBorder());
		usersList.setPreferredSize(new Dimension(50,0));
		usersList.addListSelectionListener(new groupListSelectionChange());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.2;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.gridwidth = 1;
		panel.add(usersList, c);
		
		
		/* Affichage */
		add(panel);
		setVisible(true);
	}
	
	public void updateConnectedUsers() {
		DefaultListModel<String> usernames = new DefaultListModel<String>();
		ArrayList<User> connectedUsers = controller.getConnectedUsers();
		
		for(User u : connectedUsers)
			usernames.addElement(u.getUsername());
		
		/*usernames.addElement("jean");
		usernames.addElement("truc");*/
		
		usersList.setModel(usernames);
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
				String groupName = usersList.getSelectedValue();
				
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

		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()) {
			
				Group selectedGroup = controller.getGroupByName(usersList.getSelectedValue());
				
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
			
		}

	}
	
	public void updateMessages() {
		
		
		
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
