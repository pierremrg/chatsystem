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

import ChatSystem.User;

/**
 * Servlet implementation class ChatServer
 */
public class ChatServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final int ACTION_NEW_USER = 1;
	public static final int ACTION_GET_CONNECTED_USERS = 2;
	
	private ArrayList<User> connectedUsers;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChatServer() {
        super();
        // TODO Auto-generated constructor stub
        
        connectedUsers = new ArrayList<User>();
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
		// TODO Auto-generated method stub

//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
//		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		HashMap<String, String> parameters = getParametersMap(request);
		
		if(parameters.containsKey("action")) {
			
			try {
				int action = Integer.parseInt(parameters.get("action"));
				
				//Ini ini = new Ini(new File("ini.txt"));
				//Preferences prefs = new IniPreferences(ini);
				
				//out.write(prefs.node("toto").get("param1", "no value"));
			
				if(action == ACTION_NEW_USER) {
					
					if(!parameters.containsKey("userdata"))
						return;
					
					// TODO Obtenir User avec Gson
					User user = new User(10000, "ordipierre", null);
					
					connectedUsers.add(user);
					
					out.write("New user connected");
				}
					
				else if(action == ACTION_GET_CONNECTED_USERS) {
//					out.write(connectedUsers.toString());
					
					for(User u : connectedUsers)
						out.write(u.toString());
					
				}
				else
					out.write("Do nothing");
				
			}
			catch (NumberFormatException | NullPointerException e) {
				// Pas un entier, ne rien faire
			}
			
		}
//			out.write(Integer.parseInt(parameters.get("action")));
		
		
//		Enumeration<String> parameterNames = request.getParameterNames();
//		
//		while(parameterNames.hasMoreElements()) {
//			String paramName = parameterNames.nextElement();
//			String paramValue = "";
//			
//			for(String paramVal : request.getParameterValues(paramName))
//				paramValue += paramVal;
//			
//			out.write(paramName + " : " + paramValue + "\n");
//		}
		
//		String [] test = {"aaa", "bbb"};
//		
//		GsonBuilder builder = new GsonBuilder();
//		Gson gson = builder.create();
//		
//		out.write(gson.toJson(test));
		
		/*ObjetMapper mapper = new ObjectMapper();
		String json = new Gson().toJson(test);*/
	
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
