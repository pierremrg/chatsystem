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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Fenetre de creation de l'utilisateur
 *
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
		c.weightx = 0.9;
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
		createUserButton.addActionListener(new CreateUserListener(this.guiConnect));
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		createUserPanel.add(createUserButton, c);

		add(createUserPanel);
		setVisible(true);

	}

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
			// TODO check username pas deja utilise
			
			// Obtient les donnees
			String username = usernameField.getText();
			char[] password = passwordField.getPassword();
			char[] confirmPassword = confirmPasswordField.getPassword();

			if (password.length != 0 && username.length() != 0 && confirmPassword.length != 0) {
				if (DataManager.charArrayEquals(password, confirmPassword)) {
					try {
						DataManager.createUser(username, password);
						setVisible(false);
						guiConnect.setEnabled(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					// TODO Gerer les erreurs ailleurs ?
					JOptionPane.showMessageDialog(null, "Mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
					passwordField.setText("");
					confirmPasswordField.setText("");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Champs vides", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public class KeyAdapter implements KeyListener {

		/**
		 * Pour la longueur de l'username
		 */
		public void keyTyped(KeyEvent e) {
			if (usernameField.getText().length() >= 20)
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
			guiConnect.setEnabled(true);
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
