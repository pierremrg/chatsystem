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

/**
 * Permet de gerer la lecture et l'ecriture des donnees sur la machine locale
 *
 */
public class DataManager {
	
	private static final String PATH_DATA = "data/";
	private static final String PATH_MESSAGES = "data/messages.bin";
	private static final String PATH_GROUPS = "data/groups.bin";
	private static final String PATH_USER = "data/user.bin";
	
	/**
	 * Stocke sur la machine l'ensemble des messages de l'utilisateur
	 * @param messages Messages a stocker
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeAllMessages(ArrayList<Message> messages) throws FileNotFoundException, IOException {
		
		// Verifie que le dossier "data" existe, sinon le cree
	    File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();

	    FileOutputStream file = new FileOutputStream(PATH_MESSAGES);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		// Ecrit chaque message
		for(Message m : messages) {
			out.writeObject(m);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne la liste de tous les messages stockes sur la machine
	 * @return La liste de tous les messages stockes sur la machine
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
			
			// Lecture tant qu'il reste des messages
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
	 * @param groups Groupes a stocker
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeAllGroups(ArrayList<Group> groups) throws FileNotFoundException, IOException {
		
		// Verifie que le dossier "data" existe, sinon le cree
	    File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();

	    FileOutputStream file = new FileOutputStream(PATH_GROUPS);
		ObjectOutputStream out = new ObjectOutputStream(file);
		
		// Ecrit chaque groupe
		for(Group g : groups) {
			out.writeObject(g);
		}
		
		out.close();
		file.close();
	}
	
	/**
	 * Retourne la liste de tous les groupes stockes sur la machine
	 * @return La liste de tous les groupes stockes sur la machine
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
			
			// Lecture tant qu'il reste des groupes
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
	
	/**
	 * Permet de stocker un utilisateur sur la machine
	 * @param username Username de l'utilisateur
	 * @param password Password de l'utilisateur
	 * @throws IOException
	 */
	public static void createUser(String username, char[] password) throws IOException {
		
		// TODO Gestion erreur
		
		// Verifie que le dossier "data" existe, sinon le cree
		File directory = new File(PATH_DATA);
	    if(!directory.exists())
	        directory.mkdir();
		    
	    FileOutputStream file = new FileOutputStream(PATH_USER);
		ObjectOutputStream out = new ObjectOutputStream(file);   
		
		// ID de l'utilisateur random
		// TODO Check si username + ID pas pris
		Random rand = new Random();
		int id = rand.nextInt(999999999);
		
		out.writeInt(id);
		out.writeObject(username);
		// TODO : Chiffrer le mot de passe
		out.writeObject(password);
		
		out.close();
		file.close();
	}
	
	/**
	 * Permet de voir si les donnees de connexion sont correctes
	 * @param username Username de l'utilisateur
	 * @param password Password de l'utilisateur
	 * @return L'ID de l'utilisateur si les donnees sont correctes, -1 sinon
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static int checkUser(String username, char[] password) throws IOException, ClassNotFoundException {

		File groupsFile = new File(PATH_USER);

		if (groupsFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_USER);
			ObjectInputStream in = new ObjectInputStream(file);

			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			char[] passwordFile = (char[]) in.readObject();

			if (usernameFile.equals(username) && charArrayEquals(passwordFile, password)) {
				in.close();
				return id;
			}
			
			in.close();
		}
		
		return -1;
	}
	
	/**
	 * Permet de voir si deux tableaux de caracteres sont egaux
	 * @param char1 Tableau 1
	 * @param char2 Tableau 2
	 * @return True si egaux, False sinon
	 */
	// TODO Ailleurs ? Override charArray ?
	public static boolean charArrayEquals(char[] char1, char[] char2) {
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
