package ChatSystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

/**
 * Permet de lire les messages recus
 *
 */
public class SocketReader extends Thread {

	private Socket socket;
	private Controller controller;
	private volatile Group group = null;

	/**
	 * Cree un SocketReader (thread)
	 * @param name Nom du thread
	 * @param socket Socket a utiliser
	 * @param controller Controller de l'application
	 * @see Message
	 */
	public SocketReader(String name, Socket socket, Controller controller) {
		super(name);
		this.socket = socket;
		this.controller = controller;
	}

	/**
	 * Thread qui permet de lire les messages recus
	 */
	@Override
	public void run() {

		try {

			Message message = null;
			System.out.println("SocketReader connected..."); // TODO a supprimer

			BufferedReader in_data = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Recupere les donnees recues
			String stringData = in_data.readLine();

			// Tant que la connexion est ouverte, on lit les messages
		 // TODO : "-1" a supprimer ? Pas d'utilite, non ?
			while (stringData != null && !stringData.equals("-1")) {

				// On decode le message recu et on l'envoie au controller
				message = decodeMessageFromString(stringData);
				
				if (group == null)
					group = message.getReceiverGroup();

				controller.receiveMessage(message);

				// Lecture du prochain message
				stringData = in_data.readLine();
			
			}
		} catch (SocketException e) {

			// Socket deja ferme : pas d'erreur

		} catch (Exception e) {
			GUI.showError("Erreur dans la lecture des messages recus.");
			
		} finally {
			System.out.println("Deconnecting reader..."); // TODO A supprimer
			
			try {
				socket.close();
			} catch (Exception e) {
				GUI.showError("Erreur lors de la deconnexion du lecteur des messages reçus.");
			}
			System.out.println("Reader deconnected"); // TODO A supprimer
		}

	}

	/**
	 * Retourne le groupe qui utilise ce SocketReader
	 * @return Le groupe qui utilise ce SocketReader
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Permet de decoder un message a partir d'une chaine de caracteres
	 * @param stringData La chaine de caracteres
	 * @return Le Message decode
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Message decodeMessageFromString(String stringData) throws ClassNotFoundException, IOException {

		byte[] data = Base64.getDecoder().decode(stringData);

		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Message) iStream.readObject();
	
	}

}
