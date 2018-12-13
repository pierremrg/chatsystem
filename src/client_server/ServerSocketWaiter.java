package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketWaiter extends Thread {

	private ServerSocket serverSocket;
	
	public ServerSocketWaiter(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void run() {
		
		Socket socket;
		try {
			socket = serverSocket.accept();
			
			SocketWriter socketWriter = new SocketWriter(socket);
			SocketReader socketReader = new SocketReader(socket);
			socketWriter.start();
			socketReader.start();
			
			System.out.println("Server started");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
