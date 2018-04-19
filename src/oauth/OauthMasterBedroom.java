package oauth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.communication.email.EmailAddess;
import com.communication.email.EmailVO;
import com.communication.email.MailService;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;


import mangodb.MangoDB;
import utils.Utils;


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
		
		if (null == state ){//Just a safety check - It don't happen
			state = "1";
		}
		if (null == client_id) {//Just a safety check - It don't happen
			client_id = "388798177526-u5operfjko4n0i0be6gh2ufv2ikq0695.apps.googleusercontent.com";
		
		}
		
		//Step 2. code is returned by google when google user approves the request by entering user id and pwd
		 if (null != code) {
			//We pass the code to google along with my secreat key to get access token. 
			 //Google needs my secreat so that he can ensure that he is handing over the access to ken to right person
			String access_token = getAccesstoken(request, response, code, client_id);
			String googleUserInfo = Utils.getGoogleDetails(access_token);
			if (null != googleUserInfo) {
				access_token = access_token.replaceAll("[^A-Za-z0-9]", "");
				if (access_token.length() > 80) {
					access_token = access_token.substring(0, 80);
				}
				log.info("collectiion to create "+access_token );
				log.info("inserting intoo mango DB  "+googleUserInfo );
				 EmailAddess toAddress = new EmailAddess();
				 toAddress.setAddress("sonu.hooda@gmail.com");
				new  MailService().sendSimpleMail(prepareEmailVO(toAddress, "Alexa acount linked", 	googleUserInfo, null, null));
				MangoDB.createNewDocument("google-oauth", access_token, googleUserInfo, null);
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state+"&access_token="+access_token+"&token_type=Bearer");
			}else {
				//Not authorised
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state);
			}
			
			
			
			/* This code is just for demo
			String uname = request.getParameter("uname");
			String psw = request.getParameter("psw");
			if ("alexatestsanhoo1@gmail.com".equalsIgnoreCase(uname) && "Sandeep@1234".equals(psw)) {
				access_token = UUID.randomUUID().toString();
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state+"&access_token="+access_token+"&token_type=Bearer");
			}else {
				response.sendRedirect("https://pitangui.amazon.com/spa/skill/account-linking-status.html?vendorId=MYFQ2S2E4F1Y#state="+state+"&token_type=Bearer");
			}
			*/
			
		}else {
			//showLoginPage(response,state); demo code
			//Step 1 redirect user to google Ask user to provide his concent to google that you want google to give access to his resource to us ( for which google is care taker)
			
			getAuthCode(request, response,client_id, state);
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
	
	private void getAuthCode(HttpServletRequest request, HttpServletResponse response, String client_id, String state){
		String redirectUrl = "https://accounts.google.com/o/oauth2/auth?response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&client_id="+client_id+"&state="+state+"&redirect_uri=https%3A%2F%2Falexa-master.appspot.com%2FOauthMasterBedroom";
			try {
				response.sendRedirect(redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	private String getAccesstoken(HttpServletRequest request, HttpServletResponse res, String code, String client_id) throws IOException{
		log.info("Got auth code , now try to get access token  ");
		
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
	      

	     
	      log.info("Got access_token response  ");
	      Gson gson = new Gson(); 
	      String json = response;
	      Map<String,Object> map = new HashMap<String,Object>();
	      map = (Map<String,Object>) gson.fromJson(json, map.getClass());
	      
	    
	     return (String)map.get("access_token");
	    }
	     return null;
	}

	private static EmailVO prepareEmailVO( EmailAddess toAddress, String subject , String htmlBody, String base64attachment, String attachmentName ) {
		EmailVO emailVO = new EmailVO();
		
		emailVO.setUserName( "myshopemailnotification@gmail.com");
		emailVO.setPassword( "gizmtcibqjnqhqtz");
		EmailAddess fromAddress = new EmailAddess();
		fromAddress.setAddress(emailVO.getUserName());
		fromAddress.setLabel("Alexa acount linked");
		emailVO.setFromAddress( fromAddress);
		
		
		List<EmailAddess> toAddressList = new ArrayList<EmailAddess>();
		
		toAddressList.add(toAddress);
		emailVO.setToAddress(toAddressList);
		emailVO.setSubject(subject);
		emailVO.setHtmlContent(htmlBody);
		emailVO.setBase64Attachment(base64attachment);
		emailVO.setAttachmentName(attachmentName);
		return emailVO;
	}

}
