package client_server;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Port du serveur : ");
		Scanner sc = new Scanner(System.in);
		int serverPort = sc.nextInt();
		
		System.out.println("Port du client : ");
		Scanner sc2 = new Scanner(System.in);
		int clientPort = sc2.nextInt();
		
		Server server = new Server(serverPort);
		server.start();
		
		Client client = new Client(clientPort);
		client.start();
		
		
	}

}
