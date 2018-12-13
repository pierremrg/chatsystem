package client_server;

import java.io.PrintWriter;
import java.net.Socket;

public class SocketWriter extends Thread {
	
	private Socket socket;
	private String content;
	
	public SocketWriter(Socket socket) {
		super();
		this.socket = socket;
		this.content = null;
	}
	
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			/*Scanner sc = new Scanner(System.in);
			String msg = sc.nextLine();*/
			
			while(true) {
				if(content != null) {
					out.println(content);
					System.out.println("Message envoyé");
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
