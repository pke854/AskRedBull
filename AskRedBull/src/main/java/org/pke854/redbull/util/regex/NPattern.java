package org.pke854.redbull.util.regex;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPattern {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(NPattern.class.getName());
	
	ArrayList<String> groupNames=new ArrayList<String>();
	
	public Pattern compile(String regex){
		String cleanRegex=collectAndRemoveGroupNames(regex);
		return Pattern.compile(cleanRegex);
	}
	
	public Pattern compile(String regex, int flags){
		String cleanRegex=collectAndRemoveGroupNames(regex);
		return Pattern.compile(cleanRegex, flags);
	}
	
	private String collectAndRemoveGroupNames(String regex){
		groupNames.clear();
		groupNames.add(null);
		String result = new String(regex);
		Pattern p = Pattern.compile("(?<!\\\\)\\((\\?<([^<>]+)>)?");
		Matcher m = p.matcher(regex);
		while(m.find()){
			String grName=m.group(2);
			groupNames.add(grName);
			if(grName!=null){
				//logger.info("grName: "+grName);
				int ix=result.indexOf("?<"+grName+">");
				String r1 = result.substring(0,ix);
				String r2 = result.substring(ix+grName.length()+3);
				result=r1+r2;
				//logger.info("result: "+result);
			}
		}
		//logger.info(""+result);
		return result;
	}
	
	public String groupName(int groupNum){
		if(groupNum>-1 && groupNum<groupNames.size()){
			return groupNames.get(groupNum);
		}else return null;
	}
}
