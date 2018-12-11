package client_server;

import java.util.Date;

public class Message {
	private int id;
	private Date date;
	private String content;
	private int senderID;
	private int receiverGroupID;
	
	public Message(Date date, String content, int senderID, int receiverGroupID) {
		this.date = date;
		this.content = content;
		this.senderID = senderID;
		this.receiverGroupID = receiverGroupID;
	}

	public Date getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	public int getSenderID() {
		return senderID;
	}

	public int getReceiverGroupID() {
		return receiverGroupID;
	}
}
