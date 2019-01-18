package ChatSystemServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ChatSystem.User;

/**
 * Servlet implementation class ChatServer
 */
public class ChatServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int ACTION_NEW_USER = 1;
	public static final int ACTION_GET_CONNECTED_USERS = 2;
	public static final int ACTION_USER_DECONNECTION = 3;
	
	public static final int NO_ERROR = 0;
	public static final int ERROR_NO_ACTION = 1;
	public static final int ERROR_NO_USER_DATA = 2;
	public static final int ERROR_JSON_FORMAT = 50;
	
	private ArrayList<User> connectedUsers;
//	Gson gson;
 
	
	/**
	 * Sous-classe utilisee pour envoyer une reponse du serveur
	 */
	public class ServerResponse {
		private int code;
		private String dataFormat;
		private String data;
		
		public ServerResponse(int code, String dataFormat) {
			this.code = code;
			this.data = null;
			this.dataFormat = dataFormat;
		}
		
		public int getCode() {
			return code;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
		
		public String getDataFormat() {
			return dataFormat;
		}
		
		public void setDataFormat(String dataFormat) {
			this.dataFormat = dataFormat;
		}
		
		public String getData() {
			return data;
		}
		
		public void setData(String data) {
			this.data = data;
		}
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatServer() {
        super();
        // TODO Auto-generated constructor stub
        
        connectedUsers = new ArrayList<User>();
        connectedUsers.add(new User(10, "jean", null));
        connectedUsers.add(new User(70, "truc", null));
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("application/json");
		
		ServerResponse serverResponse = new ServerResponse(NO_ERROR, "json");
		
		PrintWriter out = response.getWriter();
		HashMap<String, String> parameters = getParametersMap(request);

		Gson gson = new Gson();
		String jsonData;
		
		if(parameters.containsKey("action")) {
			
			try {
				int action = Integer.parseInt(parameters.get("action"));
				
				if(action == ACTION_NEW_USER) {
					
					if(parameters.containsKey("userdata")) {
						
						// Lecture des donnees de l'utilisateur
						User newUser = gson.fromJson(parameters.get("userdata"), User.class);
						
						// On se met a la fin (et on n'apparait pas a soi-meme)
						if(connectedUsers.contains(newUser))
							connectedUsers.remove(newUser);
						
						// On renvoie la liste des utilisateurs
						serverResponse.setData(gson.toJson(connectedUsers));

						// Ajout du nouvel utilisateur en dernier
						connectedUsers.add(newUser);
						
					}
					else {
						serverResponse.setCode(ERROR_NO_USER_DATA);
					}

				}
				
				else if(action == ACTION_USER_DECONNECTION) {
					
					if(parameters.containsKey("userdata")) {
						
						// TODO
						
					}
					
				}
					
				else if(action == ACTION_GET_CONNECTED_USERS) {

					// On renvoie la liste des utilisateurs
					serverResponse.setData(gson.toJson(connectedUsers));
					
				}
				else
					out.write("Do nothing");
				
			}
			catch (JsonSyntaxException e) {
				serverResponse.setCode(ERROR_JSON_FORMAT);
			}
			finally {
				jsonData = gson.toJson(serverResponse);
				out.write(jsonData);
			}
			
		}
		else {
			serverResponse.setCode(ERROR_NO_ACTION);
			jsonData = gson.toJson(serverResponse);
			out.write(jsonData);
		}
		
	}
	
	private HashMap<String, String> getParametersMap(HttpServletRequest request){
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Enumeration<String> parameterNames = request.getParameterNames();
		
		while(parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue = "";
			
			for(String paramVal : request.getParameterValues(paramName))
				paramValue += paramVal;
			
			map.put(paramName, paramValue);
		}
		
		return map;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
