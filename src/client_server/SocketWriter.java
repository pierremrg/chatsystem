package client_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketWriter extends Thread {
	
	private Socket socket;
	
	public SocketWriter(Socket socket) {
		super();
		this.socket = socket;
	}
	
	public void run() {		
		try {
			System.out.println("Connected...");
			
			BufferedReader in_data = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String data = null;
			
			while(!(data = in_data.readLine()).equals("-1")) {
				if(data != null)
					System.out.println("Message : " + data);
			}
		} catch (Exception e) {
			System.out.println("Erreur client...");
		} finally {			
			System.out.println("Deconnecting...");
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println("Erreur deconnexion");
			}
			System.out.println("Deconnected...");	
		}	
	}
}
