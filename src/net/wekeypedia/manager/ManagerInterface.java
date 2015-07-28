package net.wekeypedia.manager;

import net.wekeypedia.elements.WikiPage;

/** A manager is a class that take care of the storage and the loading of the data.
 * 
 * @author jeremie
 *
 */
public interface ManagerInterface {
	/** Return a wikipage.
	 * 
	 * @param domain of the wiki.
	 * @param title of the wikipage.
	 * @return the WikiPage; null if an error occurred.
	 */
	public WikiPage getWikiPage(String domain, String title);
	
	/** Save a wikipage.
	 * 
	 * @param wp the page to save.
	 * @return true if the saving was successful; false otherwise.
	 */
	public boolean saveWikiPage(WikiPage wp);
}
