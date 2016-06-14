package org.pke854.redbull.util;

import java.util.Comparator;
import java.util.Map.Entry;

public class NameRefComparator implements Comparator<Entry<String, String>> {

	public int compare(Entry<String, String> e1, Entry<String, String> e2)throws IllegalArgumentException {
		if(e1==null && e2==null)throw new IllegalArgumentException("e1==null && e2==null");
		if(e1!=null && e2==null)return 1;
		if(e1==null && e2!=null)return -1;
		if(e1.getValue().contains("${"+e1.getKey()+"}"))
			throw new IllegalArgumentException("autoreference detected: "+e1.getKey()+" in "+e1.getKey()+": '"+e1.getValue()+"'");
		if(e2.getValue().contains("${"+e2.getKey()+"}"))
			throw new IllegalArgumentException("autoreference detected: "+e2.getKey()+" in "+e2.getKey()+": '"+e2.getValue()+"'");
		if(e1.getValue().contains("${"+e2.getKey()+"}") && e2.getValue().contains("${"+e1.getKey()+"}"))
			throw new IllegalArgumentException("cyclic autoreference detected: "+e1.getKey()+" in "+e1.getKey()+": '"+e1.getValue()+"' and "+
					e2.getKey()+" in "+e2.getKey()+": '"+e2.getValue()+"'"
		);
		if(e1.getValue().contains("${"+e2.getKey()+"}") && !e2.getValue().contains("${"+e1.getKey()+"}"))return 1;
		if(!e1.getValue().contains("${"+e2.getKey()+"}") && e2.getValue().contains("${"+e1.getKey()+"}"))return -1;
		if(!e1.getValue().contains("$"+e2.getKey()) && !e2.getValue().contains("$"+e1.getKey())){
			if(!e1.getValue().contains("${") && !e2.getValue().contains("${"))return -1;
			else if(e1.getValue().contains("${") && !e2.getValue().contains("${"))return 1;
			else if(!e1.getValue().contains("${") && e2.getValue().contains("${"))return 1;
		}
		return -1;
	}

}
