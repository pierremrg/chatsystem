package ChatSystem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

import com.google.gson.Gson;

import ChatSystem.Controller.ConnectionError;
import ChatSystem.Controller.SendConnectionError;
import ChatSystemServer.ChatServer;
import ChatSystemServer.ChatServer.ServerResponse;

/**
 * Classe chargee de lancer des requetes au serveur
 */
public class ResquestTimer extends TimerTask {
	
	private Controller controller;
	
	public ResquestTimer(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		
		// Creation des donnees utilisateur
		String paramValue = "userdata=" + controller.getUser().toJson();
		
		try {
		
			// Test de la connexion
			if(!Controller.testConnectionServer())
				throw new ConnectionError();
			
			// Connexion au serveur et traitement de la reponse
			HttpURLConnection con = Controller.sendRequestToServer(ChatSystemServer.ChatServer.ACTION_USER_CONNECTION, paramValue);		
			
			// On verifie la reponse
			int status = con.getResponseCode();
			if(status != HttpURLConnection.HTTP_OK)
				throw new SendConnectionError();
			
			String jsonResponse = Controller.getResponseContent(con);
			
			Gson gson = new Gson();
			ServerResponse serverResponse = gson.fromJson(jsonResponse, ServerResponse.class);
	
			if(serverResponse.getCode() != ChatServer.NO_ERROR)
				throw new SendConnectionError();
			
			// On recupere la liste des utilisateurs connectes
			User[] responseUsers = gson.fromJson(serverResponse.getData(), User[].class);
			ArrayList<User> connectedUsers = new ArrayList<User>(Arrays.asList(responseUsers));
			
			//if(!connectedUsers.isEmpty())
				controller.receiveConnectedUsersFromServer(connectedUsers);
		
		} catch (ConnectionError | NumberFormatException e) {
			GUI.showError("Impossible de se connecter au serveur.\nVerifiez la configuration de la connexion ou utilisez le protocole UDP.");
			System.exit(Controller.EXIT_ERROR_SERVER_UNAVAILABLE);
		} catch (IOException e) {
			GUI.showError("Une erreur s'est produite dans la decouverte du reseau.");
			System.exit(Controller.EXIT_ERROR_GET_CONNECTED_USERS);
		} catch (SendConnectionError e) {
			GUI.showError("Impossible de se connecter au chat.");
			System.exit(Controller.EXIT_ERROR_SEND_CONNECTION);
		}

	}

}
