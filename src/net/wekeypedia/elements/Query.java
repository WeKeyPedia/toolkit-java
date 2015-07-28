package net.wekeypedia.elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/** This class is used to query the api at the address https://[domain].wikipedia.org/w/api.php
 * 
 * @author jeremie
 *
 */

public class Query {
	protected Map<String,String> params;
	protected String domain="en";
	protected DBObject lastquery;


	/** Initialize a newly created Query object to query using the domain "en" and the specified parameters.
	 * 
	 * @param params parameters for the query
	 */
	public Query(Map<String,String> params){
		this.params=params;
	}


	/**Initialize a newly created Query object to query using the specified domain and the specified parameters.
	 * 
	 * @param domain the domain of the api to fetch ("en", "fr"...)
	 * @param params parameters for the query
	 */
	public Query(String domain, Map<String,String> params){
		this.params=params;
		this.domain=domain;
	}


	/** Return whether the last query performed can be continued.
	 * 
	 * @return true if the last query can be continued; false otherwise.
	 */
	public boolean hasContinue(){
		if (lastquery==null) return false;
		return lastquery.containsField("continue");
	}

	
	private InputStream getStream(String addr) {
		try{
			URL url = new URL(addr);
			URLConnection connection = url.openConnection();
			return connection.getInputStream();
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	private String forgeFetchURL(){
		try{
			String url = "https://"+domain+".wikipedia.org/w/api.php?";
			for(String par:params.keySet()){
				url=url+URLEncoder.encode(par,"utf-8")+"="+URLEncoder.encode(params.get(par),"utf-8")+"&";
			}				
			return url;
		}catch(UnsupportedEncodingException e){
			return "";
		}
	}

	private String forgeContinueURL(){
		try{
			String url = "https://"+domain+".wikipedia.org/w/api.php?";
			for(String par:params.keySet()){
				url=url+URLEncoder.encode(par,"utf-8")+"="+URLEncoder.encode(params.get(par),"utf-8")+"&";
			}	
			BasicDBObject cont=(BasicDBObject) lastquery.get("continue");
			if (cont==null) return null;
			Set<Entry<String,Object>> list = cont.entrySet();
			for (Entry<String,Object> e:list){
				if (e.getKey()!="continue"){
					if (e.getValue() instanceof String){
						url+=URLEncoder.encode(e.getKey(),"utf-8")+"="+URLEncoder.encode((String) e.getValue(),"utf-8")+"&";
					}else{
						url+=URLEncoder.encode(e.getKey(),"utf-8")+"="+URLEncoder.encode(((Long) e.getValue())+"","utf-8")+"&";
					}
				}
			}
			return url;
		}catch(UnsupportedEncodingException e){
			return "";
		}
	}

	
	/** Perform the query and return the result of the query.
	 * 
	 * @return The result from the query encoded in a DBObject.
	 * @throws IOException if a problem append when reading the result of the query.
	 */
	public DBObject fetch() throws IOException{
		InputStreamReader isr = new InputStreamReader(this.getStream(forgeFetchURL()));
		BufferedReader br = new BufferedReader(isr);

		String line;
		String content = "";

		while (	(line = br.readLine())!=null)
			content+=line;
		br.close();

		DBObject obj = (DBObject) JSON.parse(content);

		DBObject query= (DBObject) obj.get("query");
		lastquery=obj;

		return query;		
	}
	

	/** Try to continue the last query and return the result.
	 * 
	 * @return The result from the query encoded in a DBObject; null if the last query cannot be continued
	 * @throws IOException  if a problem append when reading the result of the query.
	 */
	public DBObject continueFetch() throws IOException {
		String url =forgeContinueURL();
		if (url==null) return null;

		InputStreamReader isr = new InputStreamReader(this.getStream(url));
		BufferedReader br = new BufferedReader(isr);

		String line;
		String content = "";

		while (	(line = br.readLine())!=null)
			content+=line;
		br.close();


		DBObject obj = (DBObject) JSON.parse(content);

		DBObject query= (DBObject) obj.get("query");
		lastquery=obj;

		return query;		
	}
	
	
}
