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

public class DataManager {
	
	private static final String PATH_DATA = "data/";
	private static final String PATH_MESSAGES = "data/messages.bin";
	private static final String PATH_GROUPS = "data/groups.bin";
	
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
	
}
