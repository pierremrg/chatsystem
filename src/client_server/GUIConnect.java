package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class GUIConnect extends JFrame{
	
	private JPanel connectPanel;
	private JComboBox iPList;
	private JButton connectButton;
	private JButton createUserButton;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private ArrayList<InetAddress> ipListMachine;
	private volatile InetAddress IPSelected = null;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private volatile String username = null;
	private volatile int id = -1;
	private volatile boolean statusConnexion = false;
	
	public GUIConnect(ArrayList<InetAddress> allIPMachine) throws SocketException {
		super("Connexion");
		this.ipListMachine = allIPMachine;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(400, 160));
		setResizable(false);
		setLocationRelativeTo(null);
		
		connectPanel = new JPanel();
		connectPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3,3,3,3);
		
		iPList = new JComboBox(ipListMachine.toArray());		
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		connectPanel.add(iPList, c);
		
		usernameLabel = new JLabel("Login (max 20 caracteres) : ");
		c.weightx = 0.5;
		c.gridy = 1;
		c.gridwidth = 1;
		connectPanel.add(usernameLabel, c);
		
		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter());
		usernameField.addActionListener(new ConnectListener());
		c.gridy = 2;
		connectPanel.add(usernameField, c);
		
		connectButton = new JButton("CONNEXION");
		connectButton.addActionListener(new ConnectListener());
		c.gridy = 3;		
		connectPanel.add(connectButton, c);
		
		passwordLabel = new JLabel("Mot de passe : ");
		c.gridx = 1;
		c.gridy = 1;
		connectPanel.add(passwordLabel, c);
		
		passwordField = new JPasswordField();
		passwordField.addActionListener(new ConnectListener());
		c.gridy = 2;
		connectPanel.add(passwordField, c);
		
		createUserButton = new JButton("CREER UTILISATEUR");
		createUserButton.addActionListener(new CreateUserListener(this));
		c.gridy = 3;
		connectPanel.add(createUserButton, c);
		
		add(connectPanel);
		
		setVisible(true);
	}
		
	public InetAddress getIPSelected() {
		return IPSelected;
	}

	public void setIPSelected(InetAddress iPSelected) {
		IPSelected = iPSelected;
	}	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean getStatusConnexion() {
		return statusConnexion;
	}

	public void setStatusConnexion(boolean statusConnexion) {
		this.statusConnexion = statusConnexion;
	}

	public class ConnectListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {			
			String username = usernameField.getText();
			char[] password = passwordField.getPassword();
			int id = -1;
			try {
				if((id = DataManager.checkUser(username, password)) != -1) {
					setId(id);
					setUsername(username);
					setIPSelected((InetAddress) iPList.getSelectedItem());
					setStatusConnexion(true);
					setVisible(false);				
				}
				else {
					JOptionPane.showMessageDialog(null, "Erreur connexion", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}	
	
	public class CreateUserListener implements ActionListener{
		private GUIConnect guiConnect;
		
		public CreateUserListener (GUIConnect guiConnect) {
			super();
			this.guiConnect = guiConnect;
		}
		
		public void actionPerformed(ActionEvent e) {
			setEnabled(false);
			new GUICreateUser(guiConnect);
		}
	}
	
	public class KeyAdapter implements KeyListener {
		
		public void keyTyped(KeyEvent e) {
			if(usernameField.getText().length() >= 20)
				e.consume();
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
}
