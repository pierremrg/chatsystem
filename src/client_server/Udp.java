package client_server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Udp extends Thread {
	private Controller controller;
	private DatagramSocket socket;
	
	// Header des paquets UDP utilises par l'application
	// Permet de ne pas traiter les paquets d'une autre application
	private static final int IDENT_UDP = 5289803;
	
	// Port utilise par notre service UDP
	private static final int PORT = 5003; // TODO : autre ?
	
	// Constantes de statut de connexion
	public static final int NO_STATUS = -1;
	public static final int STATUS_DECONNEXION = 0;
	public static final int STATUS_CONNEXION = 1;
	public static final int STATUS_CONNEXION_RESPONSE = 2;
	public static final int STATUS_USERNAME_CHANGED = 3;
	
	/**
	 * Creer un service UDP (thread)
	 * @param controller Controller associe a l'UDP
	 * @throws SocketException Si une erreur UDP survient
	 */
	public Udp(Controller controller) throws SocketException {
		super("UDP");
		this.controller = controller;

		this.socket = new DatagramSocket(PORT);

		socket.setBroadcast(true);
	}
	
	/**
	 * Cree un message UDP
	 * @param status Information envoyee par le message (connexion, deconnexion, etc.)
	 * @param user L'utilisateur qui envoie ce message
	 * @return Le message UDP sous forme de bytes
	 * @throws IOException
	 */
	public byte[] createMessage(int status, User user) throws IOException {
		
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		
		oo.writeInt(IDENT_UDP);
		oo.writeInt(status);
		oo.writeObject(user);
		oo.close();
		
		return bStream.toByteArray();
	}
	
	/**
	 * Envoie un message UDP
	 * @param message Message a envoyer
	 * @param ipAdresse Adresse IP a laquelle envoyer le message
	 * @throws IOException Si une erreur a lieu au moment de l'envoi du message UDP
	 */
	public void sendUdpMessage(byte[] message, InetAddress ipAddress) throws IOException {
		DatagramPacket out = new DatagramPacket(message, message.length, ipAddress, PORT);
		
		socket.send(out);
		System.out.println("message UDP envoye : " + message + " a " + ipAddress.toString()); // TODO : a supprimer
	}
	
	/**
	 * Thread qui ecoute en UDP et qui traite les messages suivant le contenu
	 */
	@Override
	public void run() {
		
		byte[] buffer = new byte[1024];
		DatagramPacket in = new DatagramPacket(buffer, buffer.length);
		
		int status = NO_STATUS;
		User receivedUser = null;
		int identUdp = -1;
		
		while(true) {
			
			// TODO gerer erreurs dans controller + GUI
			try {
				socket.receive(in);
			
				// Reception des donnees
				byte[] receivedMessage = in.getData();
				ObjectInputStream iStream;
				
				iStream = new ObjectInputStream(new ByteArrayInputStream(receivedMessage));
				identUdp = (int) iStream.readInt();
				
				// On verifie qu'on doit traiter le paquet
				if(identUdp != IDENT_UDP)
					continue;
				
				// Recuperation des informations du message
				status = (int) iStream.readInt();
				receivedUser = (User) iStream.readObject();
				iStream.close();
				
				// Traitement du message recu
				switch (status) {
				
					case STATUS_DECONNEXION:
						controller.receiveDeconnection(receivedUser);
						break;
				
					case STATUS_CONNEXION:
						if (!controller.getUser().getIP().equals(in.getAddress())) {
			 				
							controller.receiveConnection(receivedUser);
							sendUdpMessage(createMessage(STATUS_CONNEXION_RESPONSE, controller.getUser()), in.getAddress());
						
						}
						break;
						
					case STATUS_CONNEXION_RESPONSE:
						controller.receiveConnection(receivedUser);
						break;
						
					case STATUS_USERNAME_CHANGED:
						if (!controller.getUser().getIP().equals(in.getAddress()))
							controller.receiveUsernameChanged(receivedUser);
						
				}
				
			} catch (StreamCorruptedException | EOFException e) {
				// Message pas pour nous, ne rien faire
			} catch (IOException | ClassNotFoundException e1) {
				GUI.showError("Erreur lors de la lecture d'un message UDP.");
			}
			
		}
	}	
}
