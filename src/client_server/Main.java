package client_server;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Port du serveur : ");
		Scanner sc = new Scanner(System.in);
		int serverPort = sc.nextInt();
		
		Server server = new Server(serverPort);
		server.start();
		
		System.out.println("Port du client : ");
		int clientPort = sc.nextInt();
		
		Client client = new Client(clientPort);
		client.start();
		
		//sc.close();
		
		
	}

}
