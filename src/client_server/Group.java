package client_server;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private ArrayList<User> members;
	private User starter;
	private boolean online;
	
	/**
	 * Création d'un groupe
	 * @param id ID du groupe
	 * @param members Liste des membres du groupe
	 */
	public Group(int id, ArrayList<User> members, User starter) {
		this.id = id;
		this.starter = starter;
		this.online = true;
		
		this.members = new ArrayList<User>();
		for(User m : members) {
			this.members.add(m);
		}
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
	
	public void addMember(User member) {
		members.add(member);
	}
	
	/**
	 * Retourne l'utilisateur qui a initié la conversation
	 * @return l'utilisateur qui a initié la conversation
	 */
	public User getStarter() {
		return starter;
	}
	
	public void setStarter(User starter) {
		this.starter = starter;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	/**
	 * Teste si un utilisateur est membre de ce groupe
	 * @param member L'utilisateur à tester
	 * @return True si l'utilisateur est dans le groupe, False sinon
	 */
	public boolean isMember(User member) {
		for(User m : members) {
			if(m.getID() == member.getID())
				return true;
		}
		
		return false;
	}
	
	public boolean updateMember(User newVersionMember) {
		for(User oldVersionMember : members) {
			if(oldVersionMember.equals(newVersionMember)) {
				members.remove(oldVersionMember);
				members.add(newVersionMember);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Renvoie le nom d'un groupe vu par un utilisateur en particulier
	 * Le nom correspond au nom du contact distant
	 * @param user L'utilisateur qui veut obtenir le nom
	 * @return Le nom du groupe vu par l'utilisateur demandeur
	 */
	public String getGroupNameForUser(User user) {
	
		if(members.get(0).equals(user))
			return members.get(1).getUsername();
		else
			return members.get(0).getUsername();
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Group))
			return false;
		
		Group g = (Group) obj;
		return g.id == id;
	}

}
