package client_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;

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
			
			String data = in_data.readLine();
			
			while(data != null && !data.equals("-1")) {
//				System.out.println("Message : " + data);
				controller.receiveMessage("Message : " + data);

				data = in_data.readLine();
			}
		} catch(SocketException e) {
			System.out.println("Stop reader");
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
	
}
