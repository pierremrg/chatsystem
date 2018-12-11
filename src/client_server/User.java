package client_server;

public class User {

	private int id;
	private String username;
	private String password;
	private String ip;
	private int port;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void connect(String ip, int port) {
		// TODO
	}

}
