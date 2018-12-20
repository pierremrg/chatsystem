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
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GUIInfo extends JFrame{
	
	private JPanel infoPanel;
	private JComboBox iPList;
	private JButton connectButton;
	private JButton createUserButton;
	private ArrayList<InetAddress> ipListMachine;
	private volatile InetAddress IPSelected = null;
	private JTextField usernameField;
	private JTextField passwordField;
	private volatile String username = null;
	private volatile String password = null;
	
	
	public GUIInfo(ArrayList<InetAddress> allIPMachine) throws SocketException {
		super("Connection");
		this.ipListMachine = allIPMachine;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 125);
		setLocationRelativeTo(null);
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		iPList = new JComboBox(ipListMachine.toArray());
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3,3,3,3);
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		infoPanel.add(iPList, c);
		
		usernameField = new JTextField("username");
		usernameField.addKeyListener(new KeyAdapter());
		usernameField.addFocusListener(new FocusListener("username"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		infoPanel.add(usernameField, c);
		
		passwordField = new JTextField("password");
		passwordField.addFocusListener(new FocusListener("password"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		infoPanel.add(passwordField, c);
		
		connectButton = new JButton("CONNECT");
		connectButton.addActionListener(new ConnectListener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 2;		
		infoPanel.add(connectButton, c);
		
		createUserButton = new JButton("CREATE USER");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		infoPanel.add(createUserButton, c);
		
		add(infoPanel);
		
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

	public void setPassword(String password) {
		this.password = password;
	}

	public class ConnectListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {			
			while(!e.getActionCommand().equals("CONNECT"));
			setIPSelected((InetAddress) iPList.getSelectedItem());
			setUsername(usernameField.getText());
			setPassword(passwordField.getText());
			setVisible(false);
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
