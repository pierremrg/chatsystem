package client_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SocketWriter extends Thread {
	
	private Socket socket;
	
	public SocketWriter(Socket socket) {
		super();
		this.socket = socket;
	}
	
	public void run() {		
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			Scanner sc = new Scanner(System.in);
			String msg = sc.nextLine();
			
			while(msg != null && !msg.equals("-1")) {
				out.println(msg);
				
				msg = sc.nextLine();
			}
			
			
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
		}
	}
}
