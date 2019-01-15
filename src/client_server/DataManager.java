package client_server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * @throws NoSuchAlgorithmException 
	 */
	public static void createUser(String username, char[] password) throws IOException, NoSuchAlgorithmException {
		
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
		
		// Chiffrement du mot de passe avec MD5
		byte[] passwordBytes = new byte[password.length];
		for (int i = 0; i < passwordBytes.length; i++)
			passwordBytes[i] = (byte) password[i];
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] passwordHashed = md.digest(passwordBytes);
		out.writeObject(passwordHashed);
		
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
	 * @throws NoSuchAlgorithmException 
	 */
	public static int checkUser(String username, char[] passwordEnter) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		File usersFile = new File(PATH_USER);

		if (usersFile.exists()) {
			
			FileInputStream file = new FileInputStream(PATH_USER);
			ObjectInputStream in = new ObjectInputStream(file);

			int id = (int) in.readInt();
			String usernameFile = (String) in.readObject();
			byte[] passwordFileHashed = (byte[]) in.readObject();
			
			byte[] passwordEnterBytes = new byte[passwordEnter.length];
			for (int i = 0; i < passwordEnterBytes.length; i++)
				passwordEnterBytes[i] = (byte) passwordEnter[i];
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] passwordEnterHashed = md.digest(passwordEnterBytes);

			if (usernameFile.equals(username) && Arrays.equals(passwordFileHashed, passwordEnterHashed)) {
				in.close();
				return id;
			}
			
			in.close();
		}
		
		return -1;
	}
	
	public static void changeUsername(String newUsername) throws IOException, ClassNotFoundException {
		File usersFile = new File(PATH_USER);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(PATH_USER);			
			ObjectInputStream in = new ObjectInputStream(file_read);			
			int id = (int) in.readInt();
			String oldUsername = (String) in.readObject();
			byte[] password = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			FileOutputStream file_write = new FileOutputStream(PATH_USER);
			ObjectOutputStream out = new ObjectOutputStream(file_write);
			out.writeInt(id);
			out.writeObject(newUsername);
			out.writeObject(password);			
			out.close();
			file_write.close();
		}		
	}
	
	public static int changePassword(byte[] oldPassword, byte[] newPassword) throws IOException, ClassNotFoundException {
		File usersFile = new File(PATH_USER);
		
		if (usersFile.exists()) {
			FileInputStream file_read = new FileInputStream(PATH_USER);			
			ObjectInputStream in = new ObjectInputStream(file_read);			
			int id = (int) in.readInt();
			String username = (String) in.readObject();
			byte[] password = (byte[]) in.readObject();
			in.close();
			file_read.close();
			
			if(Arrays.equals(password, oldPassword)) {
				FileOutputStream file_write = new FileOutputStream(PATH_USER);
				ObjectOutputStream out = new ObjectOutputStream(file_write);
				out.writeInt(id);
				out.writeObject(username);
				out.writeObject(newPassword);			
				out.close();
				file_write.close();
				return 0;
			}			
		}
		return -1; 
	}
}
