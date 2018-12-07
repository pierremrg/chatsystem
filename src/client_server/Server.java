package client_server;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server extends Thread {

	public Server() {
		super();
	}

	public void run() {

		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(5000);
			socket = serverSocket.accept();
	
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	
			out.println(new Date().toString());
		}
		catch(Exception e) {
			System.out.println("Erreur sur le serveur");
		}
		
		finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					System.out.println("Erreur fermeture socket");
				}
			}
	
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (Exception e) {
					System.out.println("Erreur fermeture serverSocket");
				}
			}
		}
		
	}
}