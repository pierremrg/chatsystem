package client_server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class DataManager {
	
	private static final String PATH_DATA = "data/";
	private static final String PATH_MESSAGES = "data/messages.bin";
	private static final String PATH_GROUPS = "data/groups.bin";
	private static final String PATH_USER = "data/user.bin";
	
	/**
	 * Stocke sur la machine l'ensemble des messages de l'utilisateur
	 * @param messages Messages à stocker
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeAllMessages(ArrayList<Message> messages) throws FileNotFoundException, IOException {
		
		// Vérifie que le dossier "data" existe, sinon le crée
	    File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();

	    FileOutputStream file = new FileOutputStream(PATH_MESSAGES);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		for(Message m : messages) {
			out.writeObject(m);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne la liste de tous les messages stockés sur la machine
	 * @return La liste de tous les messages stockés sur la machine
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<Message> readAllMessages() throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ArrayList<Message> messages = new ArrayList<Message>();
		
		File messagesFile = new File(PATH_MESSAGES);
		if(messagesFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_MESSAGES);
			ObjectInputStream in = new ObjectInputStream(file);
			
			while(true) {
				try {
					Message message = (Message) in.readObject();
					messages.add(message);
				}
				catch (EOFException e) {
					break;
				}
			}
			
			in.close();
			file.close();
			
		}
		
		return messages;
		
	}
	
	/**
	 * Stocke sur la machine l'ensemble des groupes de l'utilisateur
	 * @param groups Groupes à stocker
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeAllGroups(ArrayList<Group> groups) throws FileNotFoundException, IOException {
		
		// Vérifie que le dossier "data" existe, sinon le crée
	    File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();

	    FileOutputStream file = new FileOutputStream(PATH_GROUPS);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		for(Group g : groups) {
			out.writeObject(g);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne la liste de tous les groupes stockés sur la machine
	 * @return La liste de tous les groupes stockés sur la machine
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<Group> readAllGroups() throws FileNotFoundException, IOException, ClassNotFoundException{
		
		ArrayList<Group> groups = new ArrayList<Group>();
		
		File groupsFile = new File(PATH_GROUPS);
		if(groupsFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_GROUPS);
			ObjectInputStream in = new ObjectInputStream(file);
			
			while(true) {
				try {
					Group group = (Group) in.readObject();
					groups.add(group);
				}
				catch (EOFException e) {
					break;
				}
			}
			
			in.close();
			file.close();
			
		}
		
		return groups;
		
	}
	
	public static void createUser(String username, char[] password) throws IOException {
		// TODO Check si username pas pris
		// TODO Gestion erreur		
		// TODO Récupérer ID BDD
		// TODO Ajout à la BDD
		 File directory = new File(PATH_DATA);
		    if(!directory.exists())
		        directory.mkdir();
		    
	    FileOutputStream file = new FileOutputStream(PATH_USER);
		ObjectOutputStream out = new ObjectOutputStream(file);   
		
		Random rand = new Random();
		int id = rand.nextInt(999999999);
		
		out.writeInt(id);
		out.writeObject(username);
		out.writeObject(password);
		
		out.close();
		file.close();
	}
	
	public static int checkUser(String username, char[] password) throws IOException, ClassNotFoundException {
		
		File groupsFile = new File(PATH_USER);
		
		if(groupsFile.exists()) {			
			FileInputStream file = new FileInputStream(PATH_USER);
			ObjectInputStream in = new ObjectInputStream(file);		
			
			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			char[] passwordFile = (char[]) in.readObject();
			
			if(usernameFile.equals(username) && charactereEquals(passwordFile, password)) {
				in.close();
				return id;
			}
			in.close();
		}	
		return -1;		
	}
	
	public static boolean charactereEquals(char[] char1, char[] char2) {
		if (char1.length != char2.length)
			return false;
		else {
			for(int i = 0; i < char1.length; i++) {
				if (char1[i] != char2[i])
					return false;
			}				
		}
		return true;
	}		
}
