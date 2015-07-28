package net.wekeypedia.manager;

import java.util.LinkedList;
import java.util.List;

import net.wekeypedia.elements.WikiPage;

/** Thread that take care of retrieval of a list of pages belonging to one wiki.
 * 
 * @author jeremie
 *
 */
public class DownloadThread extends Thread{
	protected ManagerInterface mi;
	protected List<String> downloadNames;
	protected JobManager jm;

	/** Initialize a newly created thread using the specified parameters.
	 * 
	 * @param jm
	 * @param mi
	 */
	public DownloadThread(JobManager jm, ManagerInterface mi) {
		super();
		this.mi=mi;
		this.downloadNames = new LinkedList<String>();
	}

	/** Start the retrieval of the data.
	 * 
	 */
	public void run(){
		WikiPage wp=null;
		while (jm.hasAnOtherJob()){
			List<String> dlnames=jm.getAJob();
			for (String name:dlnames){
				try{
					wp=mi.getWikiPage(jm.getDomain(),name);
					mi.saveWikiPage(wp);
					if (wp!=null) downloadNames.add(name);
				}catch(Exception e){
					e.printStackTrace();
					System.err.println("Error >> "+name);
				}
			}
		}
	}

	
	/** Get the list of pages downloaded by the thread.
	 * 
	 * @return the list of pages
	 */
	public List<String> getListOfDownloadedPageNames(){
		return downloadNames;
	}

}
