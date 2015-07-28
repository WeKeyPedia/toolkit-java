package net.wekeypedia.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JobManager {
	private Map<Integer,List<String>> works;
	private int indexWork=0;
	private String domain="en";
	
	public JobManager(String domain, List<String> names){
		this(domain,names,1000);
	}
	
	public JobManager(String domain, List<String> names,int jobSize){
		this.domain=domain;
		works=new HashMap<Integer,List<String>>();	
		for (int i=0;i<=names.size()/jobSize;i++){
			works.put(i,new LinkedList<String>());
		}
		int i=0;
		for (String name :names) {
			works.get(i%works.size()).add(name);
			i++;
		}
	}
	
	public synchronized boolean hasAnOtherJob(){
		return indexWork<works.size();
	}
	
	public String getDomain(){
		return domain;
	}
	
	public synchronized List<String> getAJob(){
		if (indexWork>=works.size())
			return null;
		List<String> res = works.get(indexWork);
		indexWork++;
		return res;
	}
}
