package client_server;

import java.util.ArrayList;

public class Group {
	
	private int id;
	private ArrayList<User> members;
	
	/**
	 * Création d'un groupe
	 * @param id ID du groupe
	 * @param members Liste des membres du groupe
	 */
	public Group(int id, ArrayList<User> members) {
		this.id = id;
		this.members = members;
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
