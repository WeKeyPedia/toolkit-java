package net.wekeypedia.manager;

import java.net.UnknownHostException;

import net.wekeypedia.elements.WikiPage;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class MongoManager implements ManagerInterface {
	private String host = "localhost";
	private int port = 27017;
	private String database ="wekeypedia";
	private String collection ="wikipages";

	private MongoClient mongoClient=null;
	private DB databaseDB=null;
	private DBCollection wikipagesDB=null;
	
	/** Generate a MongoManger.
	 * The parameters used are: host = localhost; port = 27017; database = wekeypedia; collection = wikipages.
	 */
	public MongoManager(){
		
	}
	
	/** Connect to the database.
	 * 
	 * @throws UnknownHostException
	 */
	public void connect() throws UnknownHostException{
		mongoClient= new MongoClient( host , port );
		databaseDB = mongoClient.getDB(database);
		wikipagesDB = databaseDB.getCollection(collection);
	}
	

	
	@Override
	public  WikiPage getWikiPage(String domain, String title) {
		DBObject key = new BasicDBObject();
		key.put("domain", domain);
		key.put("title", title);
		DBObject obj = wikipagesDB.findOne(key);
		
		if (obj==null){
			try{
				WikiPage wp = new WikiPage(domain,title);
				wp.fetchInfo();
				wp.resolveRedirects();
				if (!wp.containsField("missing") && !wp.containsField("redirects_to")){
					wp.fetchLinks();
					wp.fetchPageviews("201502", "201502");
					wp.fetchBacklinks();
					wp.fetchRevisions();
					wp.fetchCategories();
				}
				return wp;
			}catch(Exception e){
				System.err.println("Error when fetching data for the page '"+title+"' in the domain '"+domain+"'.");
				e.printStackTrace();
				return null;
			} 
		}else{
			return new WikiPage(obj);
		}
	}

	@Override
	public synchronized boolean saveWikiPage(WikiPage wp) {
		String domain = (String) wp.get("domain");
		String title = (String) wp.get("title");
		
		DBObject key = new BasicDBObject();
		key.put("domain", domain);
		key.put("title", title);
		DBObject doc = wikipagesDB.findOne(key);
		
		if (doc!=null){
			wikipagesDB.update(doc, wp);
		}else{
			wikipagesDB.save(wp);
		}

		return true;
	}

	/** Remove the page from the database.
	 * 
	 * @param domain
	 * @param title
	 */
	public synchronized void remove(String domain, String title) {
		DBObject key = new BasicDBObject();
		key.put("domain", domain);
		key.put("title", title);
		wikipagesDB.findAndRemove(key);
	}
	
	
	/** Set the host of the database.
	 * 
	 * @param host 
	 */
	public void setHost(String host) {
		this.host = host;
	}

	
	/** Set the port of the database.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	
	/** Set the name of the database to use.
	 * 
	 * @param database
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	/** Set the name of the collection that contains the pages.
	 * 
	 * @param collection
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}
	

/*	public static void main(String[] args) throws UnknownHostException{
		MongoManager MM = new MongoManager();
		WikiPage wp = MM.getWikiPage("en", "Algorithms");
		//System.out.println(wp);
		wp.print();
		MM.saveWikiPage(wp);
		//MM.remove("en", "Algorithm");
		//MM.remove("en", "Algorithms");

	}*/
	
	
}
