package client_server;

import java.util.Date;

public class Message {
	private int id;
	private Date date;
	private String content;
	private User sender;
	private Group receiverGroup;
	
	/**
	 * Cr�er un message
	 * @param date Date d'envoi du message
	 * @param content Contenu du message
	 * @param sender Auteur du message
	 * @param receiverGroup Destinataire(s) du message 
	 */
	
	public Message(Date date, String content, User sender, Group receiverGroup) {
		this.date = date;
		this.content = content;
		this.sender = sender;
		this.receiverGroup = receiverGroup;
	}
	
	public Message(String content) {
		this.content = content;
	}

	/**
	 * Retourne la date du message
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Retourne le contenu du message
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Retourne l'auteur du message
	 * @return sender
	 */
	public User getSender() {
		return sender;
	}

	/**
	 * Retourne le/les destinataire(s)
	 * @return receiverGroup
	 */
	public Group getReceiverGroup() {
		return receiverGroup;
	}
}
