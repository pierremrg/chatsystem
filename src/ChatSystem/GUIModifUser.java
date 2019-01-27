package ChatSystem;

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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import ChatSystem.DataManager.PasswordError;

/**
 * Fenetre de modification d'utilisateur
 */
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
	
	/**
	 * Cree une nouvelle fenetre
	 * @param gui Le GUI principal
	 * @param controller Le controller de l'application
	 */
	public GUIModifUser(GUI gui, Controller controller) {
		super(controller.getUser().getUsername());
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
		modifUsernameField.addActionListener(new EditUsernameListener(gui, controller));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		modifUserPanel.add(modifUsernameField, c);
		
		
		modifUsernameButton = new JButton("Changer nom d'utilisateur");
		modifUsernameButton.addActionListener(new EditUsernameListener(gui, controller));
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
		oldPasswordField.addActionListener(new EditPasswordListener(gui, controller));
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
		newPasswordField.addActionListener(new EditPasswordListener(gui, controller));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 7;
		modifUserPanel.add(newPasswordField, c);
		
		newConfirmPasswordField = new JPasswordField();
		newConfirmPasswordField.addActionListener(new EditPasswordListener(gui, controller));
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 7;
		modifUserPanel.add(newConfirmPasswordField, c);
		
		modifPasswordButton = new JButton("Changer mot de passe");
		modifPasswordButton.addActionListener(new EditPasswordListener(gui, controller));
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 8;
		modifUserPanel.add(modifPasswordButton, c);
		
		add(modifUserPanel);
		
		/* Icone du programme */
		ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
		setIconImage(icon.getImage());
		
		setVisible(true);		
	}
	
	/**
	 * Listener du bouton de modification de l'username
	 */
	public class EditUsernameListener implements ActionListener {
		private GUI gui;
		private Controller controller;
		
		public EditUsernameListener(GUI gui, Controller controller) {
			super();
			this.gui = gui;
			this.controller = controller;
		}
		
		public void actionPerformed(ActionEvent e) {
			String newUsername = modifUsernameField.getText();
			try {
				controller.editUsername(newUsername);
				setVisible(false);
				gui.setVisible(true);
				gui.setEnabled(true);
			} catch (ClassNotFoundException | IOException e1) {
				GUI.showError("Erreur lors de l'ecriture du fichier de donnees.");
			} 
		}
	}	
	
	/**
	 * Listener du bouton de modification du mot de passe
	 */
	public class EditPasswordListener implements ActionListener {
		private GUI gui;
		private Controller controller;
		
		public EditPasswordListener(GUI gui, Controller controller) {
			super();
			this.gui = gui;
			this.controller = controller;
		}
		
		public void actionPerformed(ActionEvent e) {
			char[] oldPassword = oldPasswordField.getPassword();
			char[] newPassword = newPasswordField.getPassword();
			char[] newConfirmPassword = newConfirmPasswordField.getPassword();
			
			if (oldPassword.length != 0 && newPassword.length != 0 && newConfirmPassword.length != 0) {
				if (Arrays.equals(newPassword, newConfirmPassword)) {
					try {
						controller.editPassword(oldPassword, newPassword);
						setVisible(false);
						gui.setVisible(true);
						gui.setEnabled(true);
					} catch (ClassNotFoundException | NoSuchAlgorithmException | IOException e1) {
						GUI.showError("Erreur lors de l'ecriture du fichier de donnees.");
					} catch (PasswordError e1) {
						setAlwaysOnTop(false);
						GUI.showError("L'ancien mot de passe est incorrect.");
						oldPasswordField.setText("");
						newPasswordField.setText("");
						newConfirmPasswordField.setText("");
					}
				}
				else {
					GUI.showError("Les nouveaux mots de passe ne correspondent pas.");
					oldPasswordField.setText("");
					newPasswordField.setText("");
					newConfirmPasswordField.setText("");
				}
			}else {
				setAlwaysOnTop(false);
				GUI.showError("Veuillez remplir tous les champs.");
			}
				
		}
	}
	
	/**
	 * Permet de limiter le nombre de caracterers des zones de texte
	 */
	public class KeyAdapter implements KeyListener {

		/**
		 * Pour la longueur de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (modifUsernameField.getText().length() >= 20)
				e.consume();
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
		
	}
	
	/**
	 * Listener de fermeture de fenetre
	 */
	public class windowClosingListener implements WindowListener {

		public void windowClosing(WindowEvent e) {
			gui.setEnabled(true);
		}

		public void windowOpened(WindowEvent arg0) {}

		public void windowClosed(WindowEvent arg0) {}

		public void windowIconified(WindowEvent arg0) {}

		public void windowDeiconified(WindowEvent arg0) {}

		public void windowActivated(WindowEvent arg0) {}

		public void windowDeactivated(WindowEvent arg0) {}

	}

}
