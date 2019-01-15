package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

public class GUIModifUser extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private JPanel modifUserPanel;
	private JLabel modifUsernameLabel;
	private JTextField modifUsernameField;
	private JButton modifUsernameButton;
	private JLabel modifPasswordLabel;
	private JLabel oldPasswordLabel;
	private JLabel newPasswordLabel;
	private JLabel newConfirmPasswordLabel;
	private JPasswordField oldPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField newConfirmPasswordField;
	private JButton modifPasswordButton;
	private GUI gui;
	
	public GUIModifUser(GUI gui) {
		super("Modification utilisateur");
		this.gui = gui;
		addWindowListener(new windowClosingListener());
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(new Dimension(400, 300));
		setResizable(false);
		setLocationRelativeTo(null);
		
		modifUserPanel = new JPanel();
		modifUserPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		
		modifUsernameLabel = new JLabel("Nouveau nom d'utilisateur : ");
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		modifUserPanel.add(modifUsernameLabel, c);
		
		modifUsernameField = new JTextField();
		modifUsernameField.addKeyListener(new KeyAdapter());
		modifUsernameField.addActionListener(new ModifUsernameListener(gui));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		modifUserPanel.add(modifUsernameField, c);
		
		
		modifUsernameButton = new JButton("Changer nom d'utilisateur");
		modifUsernameButton.addActionListener(new ModifUsernameListener(gui));
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 2;
		modifUserPanel.add(modifUsernameButton, c);
		
		modifPasswordLabel = new JLabel("Changer mot de passe : ");
		c.insets = new Insets(30, 3, 3, 3);
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		modifUserPanel.add(modifPasswordLabel, c);
		
		oldPasswordLabel = new JLabel("Ancien mot de passe : ");
		c.insets = new Insets(3, 3, 3, 3);
		c.weightx = 0.5;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		modifUserPanel.add(oldPasswordLabel, c);
		
		oldPasswordField = new JPasswordField();
		oldPasswordField.addActionListener(new ModifPasswordListener(gui));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 5;
		modifUserPanel.add(oldPasswordField, c);
		
		newPasswordLabel = new JLabel("Nouveau mot de passe : ");
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 6;
		modifUserPanel.add(newPasswordLabel, c);
		
		newConfirmPasswordLabel = new JLabel("Confirmation mot de passe : ");
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 6;
		modifUserPanel.add(newConfirmPasswordLabel, c);		
		
		newPasswordField = new JPasswordField();
		newPasswordField.addActionListener(new ModifPasswordListener(gui));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 7;
		modifUserPanel.add(newPasswordField, c);
		
		newConfirmPasswordField = new JPasswordField();
		newConfirmPasswordField.addActionListener(new ModifPasswordListener(gui));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 7;
		modifUserPanel.add(newConfirmPasswordField, c);
		
		modifPasswordButton = new JButton("Changer mot de passe");
		modifPasswordButton.addActionListener(new ModifPasswordListener(gui));
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 8;
		modifUserPanel.add(modifPasswordButton, c);
		
		add(modifUserPanel);
		setVisible(true);		
	}
	
	public class ModifUsernameListener implements ActionListener {
		private GUI gui;
		
		public ModifUsernameListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
		public void actionPerformed(ActionEvent e) {
			String newUsername = modifUsernameField.getText();
			//Controller.editUsername(newUsername);
			setVisible(false);
			gui.setVisible(true);
			gui.setEnabled(true);
		}
	}	
	
	public class ModifPasswordListener implements ActionListener {
		private GUI gui;
		
		public ModifPasswordListener(GUI gui) {
			super();
			this.gui = gui;
		}
		
		public void actionPerformed(ActionEvent e) {
			char[] oldPassword = oldPasswordField.getPassword();
			char[] newPassword = newPasswordField.getPassword();
			char[] newConfirmPassword = newConfirmPasswordField.getPassword();
			
			if (oldPassword.length != 0 && newPassword.length != 0 && newConfirmPassword.length != 0) {
				if (Arrays.equals(newPassword, newConfirmPassword)) {
					//controller.editPassword(oldPassword, newPassword);
					setVisible(false);
					gui.setVisible(true);
					gui.setEnabled(true);
				}
			}else {
				setAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null, "Champs mot de passe vides", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
				
		}
	}
	
	public class KeyAdapter implements KeyListener {

		/**
		 * Pour la longueur de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (modifUsernameField.getText().length() >= 20)
				e.consume();
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	public class windowClosingListener implements WindowListener {

		public void windowClosing(WindowEvent e) {
			gui.setEnabled(true);
		}

		public void windowOpened(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowDeactivated(WindowEvent arg0) {
		}

	}

}
