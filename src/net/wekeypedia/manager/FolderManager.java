package net.wekeypedia.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import net.wekeypedia.elements.WikiPage;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/** This manager can be used to stored the data in a folder and one file for each page.
 * 
 * @author jeremie
 *
 */

public class FolderManager implements ManagerInterface {
	private String dataBaseDir;
//	private MessageDigest md ;
	
	/** Initialize a newly created FolderManager using the specified directory.
	 * 
	 * @param dir
	 */
	public FolderManager(String dir) {
		this.dataBaseDir = dir;
/*		try {
			this.md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
	}
	
	/** Build the path with the name of the file used for a page . In particular, all the '/' in the title of the page will be replaced by '<slash>'.
	 * 
	 * @param title of a wikipedia page
	 * @return a string that contains the path to the file.
	 */
	public String buildPathName(String title){
		title = title.replace("/","<slash>");
		return dataBaseDir+"/"+title+".json";
	}

	
	@Override
	public WikiPage getWikiPage(String domain, String title) {
		WikiPage wp = null;
		File f = new File(this.buildPathName(title));
		if (f.exists()){
			try {
				InputStreamReader is = new InputStreamReader(new FileInputStream(f));
				BufferedReader br = new BufferedReader(is);
				String line;
				String content = "";
				while (	(line = br.readLine())!=null)
					content+=line;		
				br.close();
				DBObject obj = (DBObject) JSON.parse(content);
				wp = new WikiPage(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try{
				wp = new WikiPage(domain,title);
				wp.fetchInfo();
				wp.resolveRedirects();
				if (!wp.containsField("missing") && !wp.containsField("redirects_to")){
					wp.fetchLinks();
					wp.fetchBacklinks();
					wp.fetchRevisions();
					wp.fetchCategories();
				}
			}catch(IOException e){
				System.err.println("Error when fetching data for the page '"+title+"' in the domain '"+domain+"'.");
				e.printStackTrace();
			}
		}
		return wp;
	}

	@Override
	public boolean saveWikiPage(WikiPage wp) {
		File f = new File(this.buildPathName((String) wp.get("title")));
		try { 
			FileWriter file = new FileWriter(f);
			file.write(JSON.serialize(wp));
			file.flush();
			file.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}