package client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketWaiter extends Thread {

	private ServerSocket serverSocket;
	private Controller controller;
	
	public ServerSocketWaiter(ServerSocket serverSocket, Controller controller) {
		super();
		this.serverSocket = serverSocket;
		this.controller = controller;
	}
	
	public void run() {
		
		Socket socket;
		try {
			socket = serverSocket.accept();
			
			SocketWriter socketWriter = new SocketWriter(socket, controller);
			SocketReader socketReader = new SocketReader(socket, controller);
			socketWriter.start();
			socketReader.start();
			
			System.out.println("Server started");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
