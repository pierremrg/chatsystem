package client_server;

import java.io.PrintWriter;
import java.net.Socket;

public class SocketWriter extends Thread {
	
	private Socket socket;
	private String content;
	private Controller controller;
	
	public SocketWriter(Socket socket, Controller controller) {
		super();
		this.socket = socket;
		this.content = null;
		this.controller = controller;
	}
	
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			/*Scanner sc = new Scanner(System.in);
			String msg = sc.nextLine();*/
			
			//Message messageToSend = controller.getMessageToSend();
			String messageToSend;
			
			while(true) {
				messageToSend = controller.getMessageToSend();
				if(messageToSend != null) {
					out.println(messageToSend);
					System.out.println("Message envoyé");
					controller.messageSent();
				}
				
				//msg = sc.nextLine();
			}
			
			/*out.println(msg);
			sc.close();*/
			
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
	
	public void setContent(String content) {
		this.content = content;
	}
}
