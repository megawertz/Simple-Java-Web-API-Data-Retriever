import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class SimpleWebAPIDataRetriever {
	
	/**
	 * Simple method to synchronously retrive data from a web API as a String. 
	 * This only supports the GET request method.
	 * <p>
	 * Based on code from:
	 * http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
	 *
	 * @param	url 		A String representing the url of your API end point
	 * @param 	params 	A {@link HashMap} of query parameters required by the API. Null if none.
	 * @return				A string containing your data. Could be XML, JSON, CSV, text, etc.
	 */
	public static String getAPIDataAsString(String url, HashMap<String, String> params) throws HTTPResponseError, java.net.MalformedURLException, java.io.IOException {
		
		String data = null;
		
		url += (params == null || params.isEmpty()) ? "" : ("?" + buildQueryStringFromMap(params));
		System.out.println( url );
		
		URL obj = new URL(url);		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		
		if(responseCode != 200) {
			throw new HTTPResponseError(responseCode);
		}
		
		System.out.println("Response Code: " + responseCode);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		
		return response.toString();
	
	}
	
	/**
	 * Takes a String of XML and returns a Document for parsing.
	 * TODO: This should do a better job of handling exceptions.
	 * <p>
	 * Code from:
	 * http://stackoverflow.com/questions/562160/in-java-how-do-i-parse-xml-as-a-string-instead-of-a-file
	 *
	 * @param 	xml 	A {@link HashMap} of query parameters. Can not be null.
	 * @return			A {@link Document} for parsing
	 */
	public static Document loadXMLFromString(String xml) throws Exception
	{
   	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   	DocumentBuilder builder = factory.newDocumentBuilder();
    	InputSource is = new InputSource(new StringReader(xml));
    	return builder.parse(is);
	}

	/**
	 * Builds and returns an escaped query string. 
	 * Parameters are escaped using {@link URLEncoder}
	 *
	 * @param 	params 	A {@link HashMap} of query parameters. Can not be null.
	 * @return				A string containing the escaped query string.
	 */
	private static String buildQueryStringFromMap(HashMap<String, String> params) {
		
		String query = "";
		Set<String> set = params.keySet();
		int i = 0;
		
		for(String key : set) {
			
			if(i > 0) {
				query += "&";
			}
			
			try {
				query += URLEncoder.encode(key,"UTF-8") + "=";
				query += URLEncoder.encode(params.get(key),"UTF-8");
			} catch(Exception e) { }
			
			i++;
		}
		
		return query;
	}
	
	public static void main(String[] args) {
		
		String url = "http://thecatapi.com/api/images/get";
		
		HashMap<String, String> params = new HashMap<String,String>();
		params.put("format","xml");
		params.put("results_per_page","20");
				
		String answer = "";
				
		try {
			answer = getAPIDataAsString(url,params);
		} catch(HTTPResponseError e) { 
			System.out.println( e.getMessage() );
		} catch(Exception e) { } 	
		
		Document d = null;
		try {
			d = loadXMLFromString(answer);
		} catch(Exception e) { 
			System.out.println("Error processing XML document from string");
			System.exit(0);
		}
		
		NodeList nodes = d.getElementsByTagName("image");
	
		for(int i = 0 ; i < nodes.getLength() ; i++) {
			Element e = (Element) nodes.item(i);
			System.out.println(e.getElementsByTagName("url").item(0).getTextContent());
		}

	}

}