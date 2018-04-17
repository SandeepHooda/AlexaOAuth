package oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;


/**
 * Servlet implementation class Oauth
 */
public class OauthMasterBedroom extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static FetchOptions lFetchOptions = FetchOptions.Builder.doNotValidateCertificate().setDeadline(300d);
	private static URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
	
	private static final Logger log = Logger.getLogger(OauthMasterBedroom.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OauthMasterBedroom() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String code = request.getParameter("code");
		String client_id = request.getParameter("client_id");
		String state = request.getParameter("state");
		log.info("OauthMasterBedroom called state : "+state +" client_id : "+client_id+" code : "+code);
		if (null == state ){//Just a safety check - It don't happen
			state = "1";
		}
		if (null == client_id) {//Just a safety check - It don't happen
			client_id = "388798177526-u5operfjko4n0i0be6gh2ufv2ikq0695.apps.googleusercontent.com";
		
		}
		 if (null != code) {
			 String uname = request.getParameter("uname");
				String psw = request.getParameter("psw");
			String access_token = getAccesstoken(request, response, code, client_id);
			if ("alexatestsanhoo1@gmail.com".equalsIgnoreCase(uname) && "Sandeep@1234".equals(psw)) {
				access_token = UUID.randomUUID().toString();
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state+"&access_token="+access_token+"&token_type=Bearer");
			}else {
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state+"&token_type=Bearer");
			}
			
			/*String email = getUserEmail(access_token).get("email");
			String name = getUserEmail(access_token).get("name");
			addCookie("email", email,request, response );
			addCookie("name" , name,request, response );*/
			//response.getWriter().print(email);
		}else {
			showLoginPage(response,state);
			//getAuthCode(request, response,client_id, state);
		}
		
	}
	
	private void showLoginPage( HttpServletResponse response, String state) {
		String redirectUrl = "/login.html?state="+state;
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void addCookie(String cookieName, String cookieValue ,HttpServletRequest request, HttpServletResponse response){
		Cookie cookie = new Cookie(cookieName,cookieValue);
	      cookie.setMaxAge(60*60*24); 
	      response.addCookie(cookie);
	      request.getSession().setAttribute(cookieName, cookieValue);
	}
	private void getAuthCode(HttpServletRequest request, HttpServletResponse response, String client_id, String state){
		//Client id + redirect url + scope + response type
	
			String redirectUrl = "https://accounts.google.com/o/oauth2/auth?response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&client_id="+client_id+"&state="+state+"&redirect_uri=https%3A%2F%2Falexa-master.appspot.com%2FOauthMasterBedroom";
			try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	private String getAccesstoken(HttpServletRequest request, HttpServletResponse res, String code, String client_id) throws IOException{
		log.info("Got auth code , now try to get access token  "+code);
		
		String client_secret = "ix_C6GMxSdd0K5H8b7S45rNu";
	
		
		
		String urlParameters  = "grant_type=authorization_code&client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri=https%3A%2F%2Falexa-master.appspot.com%2FOauthMasterBedroom&code="+code;
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		
		
	    URL url = new URL("https://accounts.google.com/o/oauth2/token" );
	    
	    HTTPRequest req = new HTTPRequest(url, HTTPMethod.POST, lFetchOptions);
	    HTTPHeader contentType = new HTTPHeader("Content-type", "application/x-www-form-urlencoded");
	    HTTPHeader charset = new HTTPHeader("charset", "utf-8");
	    HTTPHeader contentLength = new HTTPHeader( "Content-Length", Integer.toString( postDataLength ));
	    req.setHeader(contentType);
	    req.setHeader(charset);
	    req.setHeader(contentLength);
	    req.setPayload(postData);
	    HTTPResponse resp= fetcher.fetch(req);
	    
	    
	    
	    
	        	
	  
	    
	    int respCode = resp.getResponseCode();
	    log.info("respCode "+respCode);
	    if (respCode == HttpURLConnection.HTTP_OK || respCode == HttpURLConnection.HTTP_NOT_FOUND ) {
	    	request.setAttribute("error", "");
	      String response = new String(resp.getContent());
	      

	     
	      log.info("Got access_token response  "+response);
	      Gson gson = new Gson(); 
	      String json = response;
	      Map<String,Object> map = new HashMap<String,Object>();
	      map = (Map<String,Object>) gson.fromJson(json, map.getClass());
	      
	      log.info("Extract access token "+map.get("access_token"));
	     return (String)map.get("access_token");
	    }
	     return null;
	}

	private Map<String, String> getUserEmail(String accessToken) throws IOException{
		Map<String, String> userProfile = new HashMap<String, String>();
		log.info("Will get getUserEmail for access token "+accessToken);
		URL url = new URL("https://people.googleapis.com/v1/people/me?personFields=emailAddresses,names" );
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Authorization",  "Bearer "+accessToken);
	    
	    BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		Gson gson = new Gson(); 
	      
	      Map<String,Object> map = new HashMap<String,Object>();
	      map = (Map<String,Object>) gson.fromJson(response.toString(), map.getClass());
	      
	     Map emailMap = (Map) ((List<Object>)map.get("emailAddresses")).get(0);
	     Map nameMap = (Map) ((List<Object>)map.get("names")).get(0);
	     
		log.info("response  "+emailMap.get("value"));
		userProfile.put("email", (String)emailMap.get("value"));
		userProfile.put("name", (String)nameMap.get("displayName"));
		return userProfile;

	}
	 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
