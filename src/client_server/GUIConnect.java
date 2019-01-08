package client_server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Fenetre de connexion de l'utilisateur
 *
 */
public class GUIConnect extends JFrame {
	
	private static final long serialVersionUID = 1L;

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

	/**
	 * Constructeur du GUI de connexion
	 * @param allIPMachine La liste de toutes les adresses IP de la machine
	 * @throws SocketException
	 */
	public GUIConnect(ArrayList<InetAddress> allIPMachine) throws SocketException {
		super("Connexion au chat");
		this.ipListMachine = allIPMachine;

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(400, 160));
		setResizable(false);
		setLocationRelativeTo(null);

		connectPanel = new JPanel();
		connectPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 3, 3, 3);

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

	/**
	 * Retourne l'adresse IP selectionnee
	 * @return L'adresse IP selectionnee
	 */
	public InetAddress getIPSelected() {
		return IPSelected;
	}

	/**
	 * Indique l'adresse IP selectionnee
	 * @param iPSelected L'adresse IP selectionnee
	 */
	public void setIPSelected(InetAddress iPSelected) {
		IPSelected = iPSelected;
	}

	/**
	 * Retourne l'username de l'utilisateur
	 * @return L'username de l'utilisateur
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Indique l'username de l'utilisateur
	 * @param username L'username de l'utilisateur
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Retourne l'ID de l'utilisateur
	 * @return L'ID de l'utilisateur
	 */
	public int getId() {
		return id;
	}

	/**
	 * Indique l'ID de l'utilisateur
	 * @param id L'ID de l'utilisateur
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retourne l'etat de la connexion
	 * @return L'etat de la connexion
	 */
	public boolean getStatusConnexion() {
		return statusConnexion;
	}

	/**
	 * Indique l'etat de la connexion
	 * @param statusConnexion L'etat de la connexion
	 */
	public void setStatusConnexion(boolean statusConnexion) {
		this.statusConnexion = statusConnexion;
	}

	public class ConnectListener implements ActionListener {

		/**
		 * Action lorsqu'on clique sur le bouton de connexion
		 */
		public void actionPerformed(ActionEvent e) {
			
			String username = usernameField.getText();
			char[] password = passwordField.getPassword();
			int id = -1;
			
			try {
				if ((id = DataManager.checkUser(username, password)) != -1) {
					
					// On indique les informations de l'utilisateur
					setId(id);
					setUsername(username);
					setIPSelected((InetAddress) iPList.getSelectedItem());
					
					setStatusConnexion(true);
					setVisible(false);
				
				} else {
					// TODO G�rer l'erreur avec les autres ?
					JOptionPane.showMessageDialog(null, "Erreur connexion", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public class CreateUserListener implements ActionListener {
		private GUIConnect guiConnect;

		public CreateUserListener(GUIConnect guiConnect) {
			super();
			this.guiConnect = guiConnect;
		}

		/**
		 * Creation d'un utilisateur
		 */
		public void actionPerformed(ActionEvent e) {
			setEnabled(false);
			new GUICreateUser(guiConnect);
		}
	}

	public class KeyAdapter implements KeyListener {

		/**
		 * Pour gerer la taille de l'username
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
}
