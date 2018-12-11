package client_server;

import java.util.ArrayList;

public class Controller {
	private User user;
	private ArrayList<Group> groups;
	private ArrayList<Message> messages;
	
	public void sendMessages(Message message) {
		//TODO
	}
	
	public Message receiveMessage() {
		//TODO
	}
	
	public ArrayList<Group> getGroups(int userID){
		//TODO
	}
	
	public ArrayList<Message> getGroupMessages(int groupID){
		//TODO
	}
	
	public int connect(String username, String password) {
		//TODO
	}
	
	public int deconnect(int userID) {
		//TODO
	}
	
	public int startGroup(ArrayList<String> usernames) {
		//TODO
	}
	
	public User createUser(String username, String password) {
		//TODO
	}

}
