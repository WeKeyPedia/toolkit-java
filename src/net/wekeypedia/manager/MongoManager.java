package net.wekeypedia.manager;

import java.io.IOException;
import java.net.UnknownHostException;

import net.wekeypedia.elements.WikiPage;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


public class MongoManager implements ManagerInterface {
	private String host = "localhost";
	private int port = 27017;
	private String database ="wekeypedia";
	private String collection ="wikipages";

	private MongoClient mongoClient=null;
	private DB databaseDB=null;
	private DBCollection collectionDB=null;
	
	
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
		collectionDB = databaseDB.getCollection(collection);
	}
	
	
	@Override
	public synchronized WikiPage getWikiPage(String domain, String title) {
		String page = "{'domain':'"+domain+"','title':'"+title+"'}";
		DBObject obj = collectionDB.findOne((DBObject) JSON.parse(page));
		
		if (obj==null){
			try{
				WikiPage wp = new WikiPage(domain,title);
				wp.fetchInfo();
				wp.resolveRedirects();
				if (!wp.containsField("redirects_to")){
					wp.fetchLinks();
					wp.fetchBacklinks();
					wp.fetchRevisions();
					wp.fetchCategories();
				}
				return wp;
			}catch(IOException e){
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
		String pageKey = "{'domain':'"+domain+"','title':'"+title+"'}";

		DBObject doc = collectionDB.findOne((DBObject) JSON.parse(pageKey));

		if (doc!=null){
			collectionDB.update(doc, wp);
		}else{
			collectionDB.save(wp);
		}

		return true;
	}

	/** Remove the page from the database.
	 * 
	 * @param domain
	 * @param title
	 */
	public synchronized void remove(String domain, String title) {
		String page = "{'domain':'"+domain+"','title':'"+title+"'}";
		collectionDB.findAndRemove((DBObject) JSON.parse(page));
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
