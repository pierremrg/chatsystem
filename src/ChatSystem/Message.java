package ChatSystem;

import java.io.Serializable;
import java.util.Date;

/**
 * Represente un message envoye entre deux utilisateurs
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String content;
	private User sender;
	private Group receiverGroup;
	private int function;
	
	// Fonctions du message
	public static final int FUNCTION_NORMAL = 0;
	public static final int FUNCTION_STOP = 1;
	public static final int FUNCTION_FILE = 2;
	public static final int FUNCTION_IMAGE = 3;
	
	/**
	 * Cree un message
	 * @param date Date d'envoi du message
	 * @param content Contenu du message
	 * @param sender Auteur du message
	 * @param receiverGroup Groupe destinataire du message 
	 * @param function Fonction du message
	 */
	public Message(Date date, String content, User sender, Group receiverGroup, int function) {
		this.date = date;
		this.content = content;
		this.sender = sender;
		this.receiverGroup = receiverGroup;
		this.function = function;
	}

	/**
	 * Retourne la date du message
	 * @return La Date du message
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Retourne le contenu du message
	 * @return Le contenu du message
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Modifie le contenu du message
	 * @param content Le contenu du message
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Retourne l'auteur du message
	 * @return L'auteur du message
	 */
	public User getSender() {
		return sender;
	}

	/**
	 * Retourne le groupe destinataire
	 * @return Le groupe destinataire
	 */
	public Group getReceiverGroup() {
		return receiverGroup;
	}
	
	/**
	 * Retourne la fonction du message
	 * @return La fonction du message
	 */
	public int getFunction() {
		return function;
	}
	
	/**
	 * Permet de mettre a jour les informations sur le sender
	 * @param newVersionSender La nouvelle version du sender
	 */
	public void updateSender(User newVersionSender) {

		if(sender.equals(newVersionSender))
			sender = newVersionSender;
		
		receiverGroup.updateMember(newVersionSender);
		
	}
	
}
