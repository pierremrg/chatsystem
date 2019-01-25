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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Fenetre de creation de l'utilisateur
 */
public class GUICreateUser extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel createUserPanel;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel confirmPasswordLabel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JButton createUserButton;
	private GUIConnect guiConnect;

	/**
	 * Cree une fenetre pour creer un utilisateur
	 */
	public GUICreateUser(GUIConnect guiConnect) {
		super("Creer utilisateur");
		addWindowListener(new windowClosingListener());
		this.guiConnect = guiConnect;

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
		setSize(new Dimension(400, 160));
		setResizable(false);
		setLocationRelativeTo(null);

		createUserPanel = new JPanel();
		createUserPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;

		usernameLabel = new JLabel("Login (max 20 caracteres) :");
		c.weightx = 0.1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		createUserPanel.add(usernameLabel, c);

		passwordLabel = new JLabel("Mot de passe : ");
		c.gridy = 1;
		createUserPanel.add(passwordLabel, c);

		confirmPasswordLabel = new JLabel("Confirmez mot de passe : ");
		c.gridy = 2;
		createUserPanel.add(confirmPasswordLabel, c);

		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter());
		usernameField.addActionListener(new CreateUserListener(this.guiConnect));
		c.weightx = 0.9;
		c.gridx = 1;
		c.gridy = 0;
		createUserPanel.add(usernameField, c);

		passwordField = new JPasswordField();
		passwordField.addActionListener(new CreateUserListener(this.guiConnect));
		c.gridy = 1;
		createUserPanel.add(passwordField, c);

		confirmPasswordField = new JPasswordField();
		confirmPasswordField.addActionListener(new CreateUserListener(this.guiConnect));
		c.gridy = 2;
		createUserPanel.add(confirmPasswordField, c);

		createUserButton = new JButton("CREER UTILISATEUR");
		createUserButton.addActionListener(new CreateUserListener(this.guiConnect));
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		createUserPanel.add(createUserButton, c);

		add(createUserPanel);
		setVisible(true);

	}

	/**
	 * Listener du bouton de creation d'utilisateur
	 */
	public class CreateUserListener implements ActionListener {
		private GUIConnect guiConnect;

		public CreateUserListener(GUIConnect guiConnect) {
			super();
			this.guiConnect = guiConnect;
		}

		/**
		 * Clique sur le bouton de creation
		 */
		public void actionPerformed(ActionEvent e) {
			// Obtient les donnees
			String username = usernameField.getText();
			char[] password = passwordField.getPassword();
			char[] confirmPassword = confirmPasswordField.getPassword();

			if (password.length != 0 && username.length() != 0 && confirmPassword.length != 0) {
				if (Arrays.equals(password, confirmPassword)) {
					try {
						DataManager.createUser(username, password);
						setVisible(false);
						guiConnect.setVisible(true);
						guiConnect.setEnabled(true);
					} catch (IOException | NoSuchAlgorithmException e1) {
						GUI.showError("Erreur lors de l'ecriture du fichier de donnees.");
					}
				} else {
					setAlwaysOnTop(false);
					GUI.showError("Les mots de passe indiques sont differents.");
					passwordField.setText("");
					confirmPasswordField.setText("");
				}
			} else {
				setAlwaysOnTop(false);
				GUI.showError("Veuillez remplir tous les champs.");
			}
		}

	}

	/**
	 * Permet de limiter le nombre de caracteres des zones de texte
	 */
	public class KeyAdapter implements KeyListener {

		/**
		 * Pour la longueur de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (usernameField.getText().length() >= 20)
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
			guiConnect.setEnabled(true);
		}

		public void windowOpened(WindowEvent arg0) {}

		public void windowClosed(WindowEvent arg0) {}

		public void windowIconified(WindowEvent arg0) {}

		public void windowDeiconified(WindowEvent arg0) {}

		public void windowActivated(WindowEvent arg0) {}

		public void windowDeactivated(WindowEvent arg0) {}

	}
}
