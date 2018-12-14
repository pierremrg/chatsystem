package client_server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class Udp extends Thread {
	private Controller controller;
	private DatagramSocket socket;
	
	private static final int PORT = 5003;
	
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
	
	public byte[] createMessage(int status, User user) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		oo.writeInt(status);
		oo.writeObject(user);
		oo.close();
		return bStream.toByteArray();
	}
	
	/**
	 * Envoie d'un message UDP
	 * @param message � envoyer
	 * @param ip o� envoyer le message
	 */
	public void sendUdpMessage(byte[] message, InetAddress ipAddress) {
		DatagramPacket out = new DatagramPacket(message, message.length, ipAddress, PORT);
		
		try {
			socket.send(out);
			System.out.println("message envoye : " + message + " a " + ipAddress.toString());
		} catch (IOException e) {
			System.out.println("Erreur socketsend udp");
			e.printStackTrace();
		}
	}
	
	/**
	 * Thread qui �coute en UDP et qui traite les messages suivant le contenu
	 */
	public void run() {
		byte[] buffer = new byte[256];
		DatagramPacket in = new DatagramPacket(buffer, buffer.length);
		int statutConnexion = -1;
		User receivedUser = null;
		while(true) {
			try {
				socket.receive(in);
			} catch (IOException e) {
				System.out.println("Erreur socket");
				e.printStackTrace();
			}
			byte[] receivedMessage = in.getData();
			ObjectInputStream iStream;
			try {
				iStream = new ObjectInputStream(new ByteArrayInputStream(receivedMessage));
				statutConnexion = (int) iStream.readInt();
				receivedUser = (User) iStream.readObject();
				iStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(statutConnexion == 0) {
				controller.receiveDeconnection(receivedUser);
			}
			else if(statutConnexion == 1) {
				try {
					if (!Controller.getIP().equals(in.getAddress())) {
						controller.receiveConnection(receivedUser);						
						sendUdpMessage(createMessage(2, controller.getUser()), in.getAddress());
					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(statutConnexion == 2) {
				controller.receiveConnection(receivedUser);
			}		
		}
	}	
}
