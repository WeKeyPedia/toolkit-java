package net.wekeypedia.elements;

import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public abstract class WikiElement extends BasicDBObject{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4099931398660319665L;

	
	/** Initialize a newly created WikiElement.
	 * 
	 */
	public WikiElement(){	
	}
	
	
	/** Initialize a newly created WikiElement using the specified DBObject.
	 * 
	 * @param obj
	 */
	public WikiElement(DBObject obj){
		this.putAll(obj);
	}
	
	
	/** Display the content of the element.
	 * 
	 */
	public void print(){
		Set<Entry<String,Object>> setent = this.entrySet();
		for (Entry<String,Object> e : setent){
			if (e.getValue() instanceof BasicDBList){
				System.out.println(e.getKey()+" ("+((BasicDBList) e.getValue()).size()+") > "+e.getValue());
			}else{
				System.out.println(e.getKey()+" > "+e.getValue());
			}
		}
	}

}
