package net.wekeypedia.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import net.wekeypedia.manager.DownloadThread;
import net.wekeypedia.manager.FolderManager;
import net.wekeypedia.manager.JobManager;
import net.wekeypedia.manager.ManagerInterface;
import net.wekeypedia.manager.MongoManager;


/** This class allowed the use of the toolkit through command line.
 * 
 * @author jeremie
 *
 */
public class Main {
	/** Read the list of titles of the pages to download.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static List<String> getListOfNames(InputStream is) throws IOException{
		List<String> res = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line="";

		while ((line = reader.readLine()) != null) {
				res.add(line);
		}
		reader.close();
		return res;
	}
	
	/** Display the options of the command line.
	 * 
	 */
	public static void usage(){
		System.out.println("Usage: wikeypedia [OPTIONS]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -help : display this help");
		System.out.println("  -domain [domain] : set the domain of the wiki to use (default=en)");
		System.out.println("  -file [filename] : name of the file with the titles of the pages to donload. If this option is not present, the list of titles is read on the standard input.");
		System.out.println("  -nbThread [NBR] : number of threads to download the data");
		System.out.println("  -folder [path] : path to the folder to store the data (default='.')");
		System.out.println("  -mongo : use MongoDB for reading and storing the data");
		System.out.println("  -mongoHost [host] : define the host for the MongoDB");
		System.out.println("  -mongoPort [port] : define the port for the MongoDB");
		System.out.println("  -mongoDB [DB] : define the name of the DB to use");
		System.out.println("  -mongoCol [Collection] : define the name of the collection to use");
	}
	
	
	public static void main(String args[]){
		try{
			int i=0,nbThread=1;
			String file=null,domain="en";
			String folder=".";
			int mongoPort=-1;
			String mongoHost=null,mongoDB=null,mongoCollection=null;
			boolean mongo=false ;

			for(i=0;i<args.length;i++){
				if(args[i].equals("-domain")) domain = args[++i];
				if(args[i].equals("-file")) file = args[++i];
				if(args[i].equals("-nbThread")) nbThread = Integer.parseInt(args[++i]);
				if(args[i].equals("-help")) Main.usage();
				if(args[i].equals("-folder")) folder = args[++i];
				if(args[i].equals("-mongo")) mongo = true;
				if(args[i].equals("-mongoHost")) mongoHost = args[++i];
				if(args[i].equals("-mongoPort")) mongoPort = Integer.parseInt(args[++i]);
				if(args[i].equals("-mongoDB")) mongoDB = args[++i];
				if(args[i].equals("-mongoCol"))	mongoCollection = args[++i];
			}

			List<String> listNames = null;
			if (file==null) listNames =	Main.getListOfNames(System.in);
			else listNames = Main.getListOfNames(new FileInputStream(file));
			int jobsize=1+listNames.size()/(10*nbThread);
			JobManager JM = new JobManager(domain,listNames,jobsize);

			ManagerInterface MI = null;
			if (mongo){
				MongoManager MM = new MongoManager();
				if (mongoHost!=null) MM.setHost(mongoHost);
				if (mongoPort!=-1) MM.setPort(mongoPort);
				if (mongoDB!=null) MM.setHost(mongoDB);
				if (mongoCollection!=null) MM.setHost(mongoCollection);
				MM.connect();
				MI = MM;
			}else{
				FolderManager FM = new FolderManager(folder);
				MI = FM;
			}
			
			List<DownloadThread> threads = new LinkedList<DownloadThread>();
			for (int j=0;j<nbThread;j++) threads.add(new DownloadThread(JM,MI));
			for (DownloadThread df:threads){
				df.start();
			}
			for (DownloadThread df:threads) df.join();			
		}catch(Exception e){
			e.printStackTrace();
			Main.usage();
		}
	}
	
}
