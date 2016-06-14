package org.pke854.redbull.runtime.update;

import org.apache.commons.lang3.StringEscapeUtils;

public class Update {

	String id;
	String uscript;
	
	public Update() {
		super();
	}

	public Update(String id, String script) {
		super();
		this.id = id;
		this.uscript = script;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUscript() {
		return uscript;
	}

	public void setUscript(String script) {
		this.uscript = script;
	}

//	public String toString() {
//		return "{\"id\" : \"" + id + "\", \"uscript\" : \"" 
//	//+ uscript
//	+ StringEscapeUtils.escapeEcmaScript(uscript) 
//	+ "\"}";
//	}

	

}
