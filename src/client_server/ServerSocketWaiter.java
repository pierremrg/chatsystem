package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Permet de se mettre en attente d'une connexion d'un utilisateur distant
 *
 */
public class ServerSocketWaiter extends Thread {

	private ServerSocket serverSocket;
	private Controller controller;
	
	/**
	 * Cree le ServerSocketWaiter (thread)
	 * @param serverSocket Le ServerSocket a utiliser
	 * @param controller Le controller de l'application
	 */
	public ServerSocketWaiter(ServerSocket serverSocket, Controller controller) {
		super("ServerSocketWaiter");
		this.serverSocket = serverSocket;
		this.controller = controller;
	}
	
	public void run() {
		
		Socket socket;
		
		try {
		
			// Cette boucle permet d'etre toujours en ecoute meme apres une premiere connexion
			while(true) {
				// On attent que quelqu'un se connecte
				socket = serverSocket.accept();
				SocketReader socketReader = new SocketReader("ServerSocketRead", socket, controller);
				socketReader.start();
				
				while (socketReader.getGroup() == null);
				
				SocketWriter socketWriter = new SocketWriter("ServerSocketWriter", socket, controller, socketReader.getGroup());
				socketWriter.start();

				// TODO : a supprimer
				System.out.println("Server started");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block --> lier au controller ?
			e.printStackTrace();
		}
		
	}
	
}
