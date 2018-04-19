package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gson.Gson;


public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class.getName());

	public static String getGoogleDetails(String accessToken) throws IOException{
		Map<String, String> userProfile = new HashMap<String, String>();
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
		log.info("code "+conn.getResponseCode());

		if (conn.getResponseCode() == 200) {
			return response.toString();
		}

		return null;
		

	}

}
