package client_server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

public class SocketReader extends Thread {
	
	private Socket socket;
	private Controller controller;

	public SocketReader(Socket socket, Controller controller) {
		super();
		this.socket = socket;
		this.controller = controller;
	}
	
	public void run() {
		
		try {
			System.out.println("SocketReader connected...");
			
			BufferedReader in_data = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String stringData = in_data.readLine();
			
			while(stringData != null && !stringData.equals("-1")) {
//				System.out.println("Message : " + data);
//				controller.receiveMessage("Message : " + stringData);
				controller.receiveMessage(decodeMessageFromString(stringData));

				stringData = in_data.readLine();
			}
		} catch (Exception e) {
			System.out.println("Erreur reader...");
		} finally {			
			System.out.println("Deconnecting reader...");
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println("Erreur deconnexion reader");
			}
			System.out.println("Reader deconnected");	
		}	
		
	}
	
	private Message decodeMessageFromString(String stringData) throws ClassNotFoundException, IOException {
		
		byte [] data = Base64.getDecoder().decode(stringData);
		
		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
		return (Message) iStream.readObject();
	}
	
}
