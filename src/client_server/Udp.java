package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Udp extends Thread {
	private Controller controller;
	private DatagramSocket socket;
	
	private static final int PORT = 5001;
	
	/**
	 * Creer un Udp
	 * @param controller associ� � l'UDP
	 */
	public Udp(Controller controller) {
		super();
		this.controller = controller;
		
		try {
			this.socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			System.out.println("Erreur socket udp 1");
			e.printStackTrace();
		}
		try {
			socket.setBroadcast(true);
		} catch (SocketException e) {
			System.out.println("Erreur socket udp 2");
			e.printStackTrace();
		}
	}
	
	/**
	 * Envoie d'un message UDP
	 * @param message � envoyer
	 * @param ip o� envoyer le message
	 */
	public void sendUdpMessage(String message, String ip) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("Erreur adresse IP");
			e.printStackTrace();
		}
		byte[] buffer = message.getBytes();
		DatagramPacket out = new DatagramPacket(buffer, buffer.length, address, PORT);
		try {
			socket.send(out);
		} catch (IOException e) {
			System.out.println("Erreur socketsend udp");
			e.printStackTrace();
		}
	}
	
	/**
	 * Permet au controller d'ajouter l'utilisateur qui vient de se connecter
	 * @param idUser ID de l'utilisateur � ajouter
	 */
	public void addConnectedUser(int idUser) {
		controller.receiveConnection(idUser);
	}
	
	/**
	 * Permet au controller de retirer l'utilisateur qui se d�connecte
	 * @param idUser ID de l'utilisateur � retirer
	 */
	public void removeConnectedUser(int idUser) {
		controller.receiveDeconnection(idUser);
	}
	
	/**
	 * Thread qui �coute en UDP et qui traite les messages suivant le contenu
	 */
	public void run() {
		byte[] buffer = new byte[256];
		DatagramPacket in = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(in);
		} catch (IOException e) {
			System.out.println("Erreur socket");
			e.printStackTrace();
		}
	
		String messageRecu = new String(in.getData(), 0, in.getLength());		
		int statutConnexion = Integer.parseInt(messageRecu.substring(0, 1));
		int idUser = Integer.parseInt(messageRecu.substring(2));		
		
		if(statutConnexion == 0) {
			removeConnectedUser(idUser);
		}
		else if(statutConnexion == 1) {
			addConnectedUser(idUser);
			this.sendUdpMessage("2 " + controller.getUser().getID(), in.getAddress().toString());
		} else if(statutConnexion == 2) {
			addConnectedUser(idUser);
		}		
	}	
}
