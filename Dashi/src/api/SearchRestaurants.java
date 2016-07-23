package api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.MongoDBConnection;
import db.MySQLDBConnection;

/**
 * Servlet implementation class SearchRestaurants
 */
// resource name
@WebServlet("/restaurants")
public class SearchRestaurants extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static DBConnection connection = new MySQLDBConnection();
//    private static DBConnection connection = new MongoDBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchRestaurants() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// GET result
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONArray array = new JSONArray();
		if (request.getParameterMap().containsKey("user_id") && request.getParameterMap().containsKey("lat")
				&& request.getParameterMap().containsKey("lon")) {
			String userId = request.getParameter("user_id");
			double lat = Double.parseDouble(request.getParameter("lat"));
			double lon = Double.parseDouble(request.getParameter("lon"));
			// return some fake restaurants
			// array.put(new JSONObject().put("name", "Panda Express"));
			// array.put(new JSONObject().put("name", "Hong Kong Express"));
			array = connection.searchRestaurants(userId, lat, lon);
		}
		RpcParser.writeOutput(response, array);
	}
	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // TODO Auto-generated
	 * method stub // // add response info ////
	 * response.setContentType("application/json"); //
	 * response.setContentType("text/html"); //
	 * response.addHeader("Access-Control-Allow-Origin", "*"); // * anyone can
	 * // // access // String username = ""; //// PrintWriter out =
	 * response.getWriter(); //// if (request.getParameter("username") != null)
	 * { //// username = request.getParameter("username"); //// String age =
	 * request.getParameter("age"); //// out.print("Hello " + username +
	 * ", age is " + age); //// } // PrintWriter out = response.getWriter(); //
	 * out.println("<html><body>"); //
	 * out.println("<h1>This is a HTML page</h1>"); //
	 * out.println("</body></html>"); // // flush like buffer send result to
	 * response // out.flush(); // out.close();
	 * response.setContentType("application/json");
	 * response.addHeader("Access-Control-Allow-Origin", "*");
	 * 
	 * String username = ""; // String age = ""; if
	 * (request.getParameter("username") != null) { username =
	 * request.getParameter("username"); // age = request.getParameter("age"); }
	 * JSONObject obj = new JSONObject(); try { obj.put("username", username);
	 * // obj.put("age", age); } catch (JSONException e) { e.printStackTrace();
	 * } PrintWriter out = response.getWriter(); out.print(obj); out.flush();
	 * out.close(); }
	 */

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
