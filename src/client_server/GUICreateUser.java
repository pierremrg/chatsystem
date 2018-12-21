package client_server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GUICreateUser extends JFrame {
	
	private JPanel createUserPanel;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel confirmPassword;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JButton createUserButton;
	
	public GUICreateUser () {
		super("Créer utilisateur");
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(300, 160);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		
		createUserPanel = new JPanel();
		createUserPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		usernameLabel = new JLabel("Login (max 20 caractères) :");
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		createUserPanel.add(usernameLabel, c);
		
		passwordLabel = new JLabel("Mot de passe : ");
		c.gridy = 1;
		createUserPanel.add(passwordLabel, c);
		
		confirmPassword = new JLabel("Confirmez mot de passe : ");
		c.gridy = 2;
		createUserPanel.add(confirmPassword, c);
		
		usernameField = new JTextField();
		c.gridx = 1;
		c.gridy = 0;
		createUserPanel.add(usernameField, c);
		
		passwordField = new JPasswordField();
		c.gridy = 1;
		createUserPanel.add(passwordField, c);
		
		confirmPasswordField = new JPasswordField();
		c.gridy = 2;
		createUserPanel.add(confirmPasswordField, c);
		
		createUserButton = new JButton("CREER UTILISATEUR");
		createUserButton.addActionListener(new CreateUserListener());
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		createUserPanel.add(createUserButton, c);
		
		add(createUserPanel);
		setVisible(true);
		
	}
	
	public class CreateUserListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {			
			setVisible(false);
		}
	}	
}
