package client_server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

/**
 * Permet d'ecrire des messages a destination d'un groupe
 *
 */
public class SocketWriter extends Thread {
	
	private Socket socket;
	private Controller controller;
	private Group group;
	
	/**
	 * Cree le SocketWriter (thread)
	 * @param name Le nom du thread
	 * @param socket Le socket a utiliser
	 * @param controller Le controller de l'application
	 * @param group Le groupe associe a ce SocketWriter
	 * @see Message
	 */
	public SocketWriter(String name, Socket socket, Controller controller, Group group) {
		super(name);
		this.socket = socket;
		this.controller = controller;
		this.group = group;
	}
	
	public void run() {
		
		try {
		
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			Message messageToSend;
			
			// TODO : impossible qu'il soit ferme, si ?
			while(!socket.isClosed()) {
				
				// On recupere le message a envoyer
				messageToSend = controller.getMessageToSend();
				
				if(messageToSend != null) {

					// On regarde si le message est un message de fin de conversation (deconnexion)
					if(messageToSend.getFunction() == Message.FUNCTION_STOP)
						break;
					
					// On envoie le message s'il est bien destine a ce groupe
					if(group.equals(messageToSend.getReceiverGroup())) {					
						out.println(encodeMessageToString(messageToSend)); // TODO a supprimer
						System.out.println("Message envoyé"); // TODO a supprimer
						controller.messageSent();
					}
					
				}
				
			}
			
			
		}
		catch(Exception e) {
			// TODO gerer erreurs dans controller + GUI
			System.out.println("Erreur sur le writer");
		}
		
		finally {
			System.out.println("Deconnecting writer...");
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					System.out.println("Erreur fermeture socket");
				}
			}
		}
	}
	
	/**
	 * Permet d'encoder un message sous forme de chaine de caracteres
	 * @param message Le Message a encoder
	 * @return Le Message encode sous forme de chaine de caracteres
	 * @throws IOException
	 */
	private static String encodeMessageToString(Message message) throws IOException {
		
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo;

		try {
			oo = new ObjectOutputStream(bStream);
			oo.writeObject(message);
			oo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Base64.getEncoder().encodeToString(bStream.toByteArray());
	}
}
