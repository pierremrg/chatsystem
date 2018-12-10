package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
	
	private int port;

	public Client(int port) {
		super();
		
		this.port = port;
	}
	
	public void run() {
		Socket client = null;
		
		try {
			byte ip[] = new byte[] {10,1,5,42};
			client = new Socket(InetAddress.getByAddress(ip), port);
			System.out.println("Connected...");
			
			BufferedReader in_data = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			String data = null;
			
			while((data = in_data.readLine()) != "-1") {
				if(data != null)
					System.out.println("Message : " + data);
			}
		} catch (Exception e) {
			System.out.println("Erreur client...");
		} finally {			
			System.out.println("Deconnecting...");
			try {
				client.close();
			} catch (Exception e) {
				System.out.println("Erreur deconnexion");
			}
			System.out.println("Deconnected...");	
		}	
	}
}
