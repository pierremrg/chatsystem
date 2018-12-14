package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
	
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		c.weightx = 0.8;
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
		String week[] = {"User1", "User2", "User3", "User4"};
		usersList = new JList<String>(week);
		usersList.setBorder(BorderFactory.createRaisedBevelBorder());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
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
			Message message = new Message(textToSend);
			controller.sendMessage(message);
			
			
		}
		
	}
	
	/**
	 * Fonction principale du programme
	 * @param args
	 * @throws SocketException 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SocketException, ClassNotFoundException, SQLException {
		
		//new GUI();
		
		
		/*Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		Connection con = DriverManager.getConnection("jdbc:odbc:MovieCatalog");
		
		Statement statement = (Statement) con.createStatement();*/
		
		try {
			controller = new Controller();
			
			controller.connect("toto", "password");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	

}
