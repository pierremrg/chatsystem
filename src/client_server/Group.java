package client_server;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
	
	private int id;
	private ArrayList<User> members;
	private User starter;
	
	/**
	 * Création d'un groupe
	 * @param id ID du groupe
	 * @param members Liste des membres du groupe
	 */
	public Group(int id, ArrayList<User> members, User starter) {
		this.id = id;
		this.members = members;
		this.starter = starter;
	}
	
	/**
	 * Retourne l'ID du groupe
	 * @return l'ID du groupe
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retourne les membres du groupe
	 * @return les membres du groupe
	 */
	public ArrayList<User> getMembers() {
		return members;
	}
	
	/**
	 * Retourne l'utilisateur qui a initié la conversation
	 * @return l'utilisateur qui a initié la conversation
	 */
	public User getStarter() {
		return starter;
	}
	
	/**
	 * Teste si un utilisateur est membre de ce groupe
	 * @param member L'utilisateur à tester
	 * @return True si l'utilisateur est dans le groupe, False sinon
	 */
	public boolean isMember(User member) {
		for(User m : members) {
			if(m == member)
				return true;
		}
		
		return false;
	}
	
	

}
