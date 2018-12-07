package client_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {

	public Client() {
		super();
	}
	
	public void run() {
		Socket client = null;
		
		try {
			client = new Socket(InetAddress.getLocalHost(), 5000);
			System.out.println("Connected...");
			
			BufferedReader in_data = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			String data = null;
			
			while((data = in_data.readLine()) != "-1") {
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
