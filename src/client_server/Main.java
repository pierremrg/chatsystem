package client_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		/*System.out.println("Port du serveur : ");
		Scanner sc = new Scanner(System.in);
		int serverPort = sc.nextInt();
		
		Server server = new Server(serverPort);
		server.start();
		
		System.out.println("Port du client : ");
		int clientPort = sc.nextInt();
		
		Client client = new Client(clientPort);
		client.start();
		
		//sc.close();*/
		
		
		
		
		System.out.println("Serveur (0) ou client (1) : ");
		Scanner sc = new Scanner(System.in);
		int mode = sc.nextInt();
		
		// Serveur
		if(mode == 0) {
			System.out.println("Port du serveur : ");
			int serverPort = sc.nextInt();
			ServerSocket serverSocket = new ServerSocket(serverPort);
			Socket socket = serverSocket.accept();
			
			SocketWriter socketWriter = new SocketWriter(socket);
			SocketReader socketReader = new SocketReader(socket);
			socketWriter.start();
			socketReader.start();
			
			System.out.println("Server started");
			
		}
		
		// Client
		else {
			
			System.out.println("Port du client : ");
			int clientPort = sc.nextInt();
			
			byte ip[] = new byte[] {10,1,5,42};
			Socket socket = new Socket(InetAddress.getByAddress(ip), clientPort);
			
			SocketWriter socketWriter = new SocketWriter(socket);
			SocketReader socketReader = new SocketReader(socket);
			socketWriter.start();
			socketReader.start();
			
			System.out.println("Client started");
			
		}
		
		
		
		
		
		
		
		
		
		
		
	}

}
