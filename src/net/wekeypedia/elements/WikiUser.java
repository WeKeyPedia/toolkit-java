package net.wekeypedia.elements;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;


/** This class is used to fetch and store the data related to an user.
 * 
 * @author jeremie
 *
 */
public class WikiUser extends WikiElement{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6998855300168850867L;

	
	/** Initialize a newly created WikiUser object using the specified object.
	 * 
	 * @param obj
	 */
	public WikiUser(DBObject obj){
		super(obj);
	}
	
	
	/** Initialize a newly created user using the specified domain and username.
	 * 
	 * @param domain 
	 * @param username
	 */
	public WikiUser(String domain, String username){
		this.put("domain",domain);
		this.put("user", username);
	}
	
	/** Initialize a newly created user using the domain "en" and the specified username.
	 * 
	 * @param username
	 */
	public WikiUser(String username){
		this("en",username);
	}
	
	
	/** Fetch the info about the user.
	 * The parameters used are: list = users;
	 *  usprop = blockinfo|groups|implicitgroups|rights|registration|editcount|gender.
	 * 
	 */
	public void fetchInfo(){
		Map<String,String> params = new HashMap<String,String>();
		params.put("action","query");
		params.put( "ususers", (String) this.get("user") );
		params.put( "usprop" , "blockinfo|groups|implicitgroups|rights|registration|editcount|gender" );
		params.put( "list" , "users" );
		params.put( "format" , "json");
		params.put( "continue", "");
		
		Query q = new Query((String) this.get("domain"),params);
		DBObject query;
		try {
			query = q.fetch();
			BasicDBList users=(BasicDBList) query.get("users");
			DBObject user=(DBObject) users.get(0);
			this.putAll(user);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/** Perform a query using the specified parameters and filters the result. The parameters must contain ["action":"query"] and ["list":???].
	 * 
	 * @param params
	 */
	public void fetchList(Map<String,String> params){
		try{
			Query q = new Query((String) this.get("domain"),params);
			DBObject query =  q.fetch();
			String titleList=params.get("list");
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/** Fetch all the contributions of the user.
	 * The parameters used are:
	 * 		list = usercontribs;
	 * 		redirects.
	 * 
	 */
	public void fetchUserContribs() {
		
		Map<String,String> params = new HashMap<String,String>();
		params.put( "action","query");
		params.put( "ucuser", (String) this.get("user"));
		params.put( "list", "usercontribs");
		params.put( "uclimit", "max");
		params.put( "format", "json");
		params.put( "continue", "");
		params.put( "redirects", "");
		
		this.fetchList(params);
	}
	
/*	public static void main(String[] args){
		WikiUser wu = new WikiUser("Spumuq");
		wu.fetchInfo();
		wu.fetchUserContribs();
		wu.print();
	}*/
}
