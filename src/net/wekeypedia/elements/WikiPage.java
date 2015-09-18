package net.wekeypedia.elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


/** This class is used to fetch and store the data related to a wikipedia page.
 * 
 * @author jeremie
 *
 */
public class WikiPage extends WikiElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4272064361888782514L;

	
	/** Initialize a newly created WikiPage object using the specified object.
	 * 
	 * @param obj
	 */
	public WikiPage(DBObject obj){
		super(obj);
	}
	
	
	/** Initialize a newly created WikiPage object using the specified domain and the title of a page of wikipedia.
	 * 
	 * @param title the title of a page of wikipedia.
	 * @param domain the domain of the specified page.
	 */
	public WikiPage(String domain, String title) {
		this.put("title",title);
		this.put("domain",domain);
	}

	/**  Perform a query using the specified parameters and filters the result. The parameters must contain ["action":"query"] and ["prop":???].
	 * 
	 * @param params the parameters for the query.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 */
	public void fetchProp(Map<String,String> params) throws IOException{
		String titleProp = params.get("prop");
		Query q = new Query((String) this.get("domain"),params);
		DBObject query =  q.fetch();
		DBObject pages=(DBObject) query.get("pages");
		DBObject page = (DBObject) pages.get(this.get("pageid")+"");
		BasicDBList list = new BasicDBList();
		if (page!=null && ((BasicDBList) page.get(titleProp))!=null){
			list.addAll((BasicDBList) page.get(titleProp));			
			DBObject cont=(DBObject) q.continueFetch();
			while (cont!=null){
				pages=(DBObject) cont.get("pages");
				page = (DBObject) pages.get(this.get("pageid")+"");
				list.addAll((BasicDBList) page.get(titleProp));
				cont = q.continueFetch();
			}
		}
		this.put(titleProp, list) ;

	}


	/** Fetch the info of the page.
	 * The parameters used are:
	 *  	action = query;
	 *  	prop = info.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 * 
	 */
	public void fetchInfo() throws IOException{
		Map<String,String> params = new HashMap<String,String>();
		params.put("action","query");
		params.put( "titles", (String) this.get("title") );
		params.put( "prop" , "info" );
		params.put( "format" , "json");
		params.put( "continue", "");

		Query q = new Query((String) this.get("domain"),params);
		DBObject query;
		query = q.fetch();
		DBObject pages=(DBObject) query.get("pages");
		Long pageid=Long.parseLong((String) pages.keySet().toArray()[0]);
		DBObject page = (DBObject) pages.get(pageid+"");
		this.putAll(page);
	}


	/** Fetch the api to verified if the page is a redirection page. If the page is a redirection, the field "redirects_to" is initialize with the destination of the redirection.
	 * 
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 */
	public void resolveRedirects() throws IOException {
		Map<String,String> params = new HashMap<String,String>();
		params.put("action","query");
		params.put( "titles", (String) this.get("title") );
		params.put( "prop" , "info" );
		params.put( "format" , "json");
		params.put( "continue", "");
		params.put( "redirects", "");

		Query q=new Query((String) this.get("domain"), params);
		DBObject query;
		query = q.fetch();
		if (query.containsField("redirects")){
			this.put("redirects_to", ((DBObject) ((BasicDBList) query.get("redirects")).get(0)).get("to"));
		}


	}


	/** Fetch all the categories of the page.
	 *  The parameters used are:
	 *  	action = query;
	 *  	prop = categories;
	 *  	clprop = timestamp.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 * 
	 */
	public void fetchCategories() throws IOException {
		Map<String,String> params = new HashMap<String,String>();
		params.put( "action","query");
		params.put( "titles", (String) this.get("title"));
		params.put( "prop", "categories");
		params.put( "clprop", "timestamp");
		params.put( "cllimit", "max");
		params.put( "format", "json");
		params.put( "continue", "");
		params.put( "redirects", "");

		this.fetchProp(params);
	}


	/** Fetch all the revisions of the page.
	 *  The parameters used are:
	 *  	action = query;
	 *  	prop = revisions;
	 *  	rvprop = user|userid|timestamp|size.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 * 
	 */
	public void fetchRevisions() throws IOException {
		Map<String,String> params = new HashMap<String,String>();
		params.put( "action","query");
		params.put( "titles", (String) this.get("title"));
		params.put( "prop", "revisions");
		params.put( "rvprop" , "user|userid|timestamp|size");
		params.put( "rvlimit", "max");
		params.put( "format", "json");
		params.put( "continue", "");
		params.put( "redirects", "");

		this.fetchProp(params);
	}

	/** Fetch all the links of the page.
	 *  The parameters used are:
	 *  	action = query;
	 *  	prop = links.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 * 
	 */
	public void fetchLinks() throws IOException{
		Map<String,String> params = new HashMap<String,String>();
		params.put( "action","query");
		params.put( "titles", (String) this.get("title"));
		params.put( "prop", "links");
		params.put( "plnamespace", "0");
		params.put( "pllimit", "max");
		params.put( "format", "json");
		params.put( "continue", "");
		params.put( "redirects", "");

		this.fetchProp(params);
	}


	/**  Perform a query using the specified parameters and filters the result. The parameters must contain ["action":"query"] and ["list":???].
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public void fetchList(Map<String,String> args) throws IOException{
		Query q = new Query((String) this.get("domain"),args);
		DBObject query =  q.fetch();
		String titleList=args.get("list");
		BasicDBList list = new BasicDBList();
		if ((BasicDBList) query.get(titleList)!=null){
			list.addAll((BasicDBList) query.get(titleList));			
			DBObject cont=(DBObject) q.continueFetch();
			while (cont!=null){
				list.addAll((BasicDBList) cont.get(titleList));
				cont = q.continueFetch();
			}
		}
		this.put(titleList, list);
	}

	/** Fetch all the backlinks of the page.
	 * 	The parameters used are:
	 *  	action = query;
	 *  	list = backlinks.
	 * @throws IOException if a problem occurs in the reading the result of the query.
	 * 
	 */
	public void fetchBacklinks() throws IOException {

		Map<String,String> params = new HashMap<String,String>();
		params.put( "action","query");
		params.put( "bltitle", (String) this.get("title"));
		params.put( "list", "backlinks");
		params.put( "blnamespace", "0");
		params.put( "bllimit", "max");
		params.put( "format", "json");
		params.put( "continue", "");
		params.put( "redirects", "");

		this.fetchList(params);
	}
	

	/** Fetch the daily page views statistics from http://stats.grok.se/.
	 * @param from : starting date must be at least December 2007 and the format "yyyyMM"
	 * @param to : ending date. If null then current date is used. The format must be "yyyyMM"
	 * @throws IOException : if a problem occurs in the reading the result of the query.
	 * @throws ParseException : if problem with the format of the date
	 */
	public void fetchPageviews(String from, String to) throws IOException, ParseException {
		String base_url = "http://stats.grok.se/json/"+this.get("domain");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date startDate = sdf.parse(from);
		Date endDate;
		
		if (to==null){
			endDate = new Date();
		}else{
			endDate = sdf.parse(to);
		}
		
		GregorianCalendar current = new  GregorianCalendar();
		current.setTime(startDate);
		
		GregorianCalendar end = new  GregorianCalendar();
		end.setTime(endDate);
	
		DBObject pageviews = new BasicDBObject();

		
		while ( end.compareTo(current)>=0){
			String addr = base_url+"/"+sdf.format(current.getTime())+"/"+this.get("title");
			
			URL url = new URL(addr);
			URLConnection connection = url.openConnection();
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			String line;
			String content = "";
			while (	(line = br.readLine())!=null)
				content+=line;
			br.close();

			DBObject obj = (DBObject) JSON.parse(content);
			DBObject views = (DBObject) obj.get("daily_views");
			pageviews.putAll(views);
			
			current.add(Calendar.MONTH, 1);
		}
		
		
		
		this.put("pageviews",pageviews);
	}


	public static void main(String[] args) throws IOException, ParseException{
		WikiPage wp = new WikiPage("en","Digital_object_identifier");
		wp.fetchInfo();
		wp.resolveRedirects();
		if (!wp.containsField("redirects_to")){
			wp.fetchPageviews("201502", "201502");
			wp.fetchBacklinks();
			//wp.fetchCategories();
			wp.fetchLinks();
			//wp.fetchRevisions();
		}
		System.out.println(((BasicDBList) wp.get("links")).size());
		//wp.print();	
	}
	
}
