

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import mangodb.MangoDB;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 Gson gson = new Gson(); 
	     
	      Map<String,Object> map = new HashMap<String,Object>();
	     
	      
	      String json = MangoDB.getData("google-oauth", "ya29GlyhBZQa3FkkxZw7yQN3jw6crqNFCLriW9dAy6GpQYYH09f2czk2oKOGWiJWOHFBEIhCO4NBMOo9", null);
	      map = (Map<String,Object>) gson.fromJson(json, map.getClass());
	      
	      Map emailMap = (Map) ((List<Object>)map.get("emailAddresses")).get(0);
	      
	      Map nameMap = (Map) ((List<Object>)map.get("names")).get(0);
		
		response.getWriter().append("Served at: ").append((String)emailMap.get("value")+" = "+(String)nameMap.get("displayName"));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
