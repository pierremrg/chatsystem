package client_server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private volatile String password = null;
	
	
	public GUIConnect(ArrayList<InetAddress> allIPMachine) throws SocketException {
		super("Connexion");
		this.ipListMachine = allIPMachine;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 160);
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
		
		usernameLabel = new JLabel("Login (max 20 caractères) : ");
		c.weightx = 0.5;
		c.gridy = 1;
		c.gridwidth = 1;
		connectPanel.add(usernameLabel, c);
		
		usernameField = new JTextField("Login");
		usernameField.addKeyListener(new KeyAdapter());
		usernameField.addFocusListener(new FocusListener("Login"));
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
		
		passwordField = new JPasswordField("Mot de passe");
		passwordField.addFocusListener(new FocusListener("Mot de passe"));
		passwordField.addActionListener(new ConnectListener());
		c.gridy = 2;
		connectPanel.add(passwordField, c);
		
		createUserButton = new JButton("CREER UTILISATEUR");
		createUserButton.addActionListener(new CreateUserListener());
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public class ConnectListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {			
			setIPSelected((InetAddress) iPList.getSelectedItem());
			setUsername(usernameField.getText());
			setPassword(passwordField.getText());
			setVisible(false);
		}
	}	
	
	public class CreateUserListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			setEnabled(false);
			new GUICreateUser();			
		}
	}
	
	public class FocusListener implements java.awt.event.FocusListener {
		private String placeHolder;
		
		public FocusListener(String placeHolder) {
			this.placeHolder = placeHolder;
		}
		
		public void focusGained(FocusEvent e) {
			if (((JTextField) e.getComponent()).getText().equals(placeHolder))
				((JTextField) e.getComponent()).setText("");
		}
		
		public void focusLost(FocusEvent e) {
			if (((JTextField) e.getComponent()).getText().equals(""))
				((JTextField) e.getComponent()).setText(placeHolder);
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
