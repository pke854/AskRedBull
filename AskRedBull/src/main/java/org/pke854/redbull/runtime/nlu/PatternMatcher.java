package org.pke854.redbull.runtime.nlu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pke854.redbull.util.NameRefComparator;
import org.pke854.redbull.util.regex.NPattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatternMatcher {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(PatternMatcher.class.getName());

	public static final String PATTERN_ORDER_FNAME="patternOrder";
	
	String synonymRegex = "";
	List<Pattern> patterns;
	List<NPattern> nPatterns;
	Pattern synonymPattern;
	NPattern synonymNPattern;
	
	String utteranceListener=null;
	ScheduledExecutorService exec=Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> removeUttListenerFuture=null;
	
	public PatternMatcher(String nluDir) {
		super();
		try {
			patterns = new LinkedList<Pattern>();
			nPatterns = new LinkedList<NPattern>();
			synonymRegex = loadSynonymRex(nluDir + "/synonyms.json");
			logger.info("synonymRegex: " + synonymRegex);
			synonymNPattern = new NPattern();
			synonymPattern = synonymNPattern.compile(synonymRegex, Pattern.CASE_INSENSITIVE); 
			
			List<String> patternNames = loadPatternNames(nluDir);
			Map<String, String> patternsMap = loadPatterns(nluDir, patternNames);
			List<String> patternRexps= expandNamesReferences(
					patternNames, patternsMap);
			for(String rexp: patternRexps){
				NPattern np = new NPattern();
				patterns.add(np.compile(rexp, Pattern.CASE_INSENSITIVE));
				nPatterns.add(np);
			}
			
//			String politeRex = loadRex("", nluDir + "/polite.json");
//			NPattern np = new NPattern();
//			patterns.add(np.compile(politeRex, Pattern.CASE_INSENSITIVE));
//			nPatterns.add(np);
//			String iRex = loadRex("intent:=", nluDir + "/intents.json");
//			np = new NPattern();
//			patterns.add(np.compile(iRex, Pattern.CASE_INSENSITIVE));
//			nPatterns.add(np);
//			String[] names = { "dbpediaClasses", "nationalities" };
//
//			for (String name : names) {
//				np = new NPattern();
//				String pRex = loadRex("", nluDir + "/" + name + ".json");
//				patterns.add(np.compile(pRex, Pattern.CASE_INSENSITIVE));
//				nPatterns.add(np);
//			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public String interpret(String utterance) {
		if(utteranceListener!=null){
			String result = utteranceListener+"(\""+utterance+"\");";
			setUtteranceListener(null);
			if(removeUttListenerFuture!=null){
				removeUttListenerFuture.cancel(false);
				removeUttListenerFuture=null;
			}
			return result;
		}
		String rUtterance = replaceSynonyms(utterance);
		logger.info("replaced utterance: " + rUtterance);
		String interpretation = null;
		List<String> intents = new LinkedList<String>();
		List<String> params = new LinkedList<String>();

		List<String> utterances = new LinkedList<String>();
		// TODO
		utterances.add(rUtterance);

		for (int k = 0; k < patterns.size(); k++) {
			Pattern pattern = patterns.get(k);
			NPattern nPattern = nPatterns.get(k);
			logger.fine("pattern: " + pattern.pattern());

			while (!utterances.isEmpty()) {
				List<String> resUtterances = new LinkedList<String>(utterances);
				for (String utt : resUtterances) {
					Matcher m = pattern.matcher(utt);
					int endMatch = 0;
					int startMatch = 0;
					String intent = null;
					String paramStr = null;
					while (m.find()) {
						if (intent == null) {
							intent = "";
						}
						if (paramStr == null) {
							paramStr = "";
						}
						logger.info("match: '"
								+ utt.substring(m.start(), m.end()) + "'");
						startMatch = m.start();
						endMatch = m.end();
						for (int i = 1; i <= m.groupCount(); i++) {
							String gv = m.group(i);
							if (gv != null)
								gv = gv.trim();
							String gn = nPattern.groupName(i);

							if (gv != null && gn != null) {
								logger.info("group(" + i + ", '" + gn + "'): "
										+ gv);
								if (gn.contains(";")) {
									String[] gns = gn.split(";");
									for (String g : gns) {
										if (g.contains(":=")) {
											String[] toks = g.split(":=");
											g = toks[0];
											gv = toks[1];
											if (gv != null)
												gv = gv.trim();
										}
										if (g.equalsIgnoreCase("intent")) {
											intent = gv;
										} else {
											if (paramStr.length() > 0)
												paramStr += ", ";
											paramStr += "\"" + g + "\" : "
													+ "\"" + gv + "\" ";
										}
									}
								} else {
									if (gn.contains(":=")) {
										String[] toks = gn.split(":=");
										gn = toks[0];
										gv = toks[1];
										if (gv != null)
											gv = gv.trim();
									}
									if (gn.equalsIgnoreCase("intent")) {
										intent = gv;
									} else {
										if (paramStr.length() > 0)
											paramStr += ", ";
										paramStr += "\"" + gn + "\" : " + "\""
												+ gv + "\" ";
									}
								}
							} else {
								if (gv != null)
									logger.info("gv: " + gv + ", gn: " + gn);
							}
						}
						if (paramStr != null && paramStr.trim().length() > 0
								&& !params.contains(paramStr))
							params.add(paramStr.trim());
						if (intent != null && intent.trim().length() > 0
								&& !intents.contains(intent)) {
							intents.add(intent);
						}
					}
					if (endMatch > startMatch && endMatch > 0) {
						String rutt = utt.substring(endMatch).trim();
						String lutt = utt.substring(0, startMatch).trim();
						if (lutt.length() > 0)
							utterances.add(lutt);
						if (rutt.length() > 0)
							utterances.add(rutt);
						utterances.remove(utt);
					}

					logger.info("utterances: " + utterances + "");
					logger.info("intents: " + intents);
					logger.info("params: " + params);
				}
				break;
			}
		}
		interpretation = disambiguate(intents, params, utterance);
		logger.info("interpretation: " + interpretation);
		return interpretation;

	}

	private String loadRex(String prefix, String fileName) throws Exception {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		File intents = new File(fileName);
		TypeReference<List<Map<String, List<String>>>> typeRef = new TypeReference<List<Map<String, List<String>>>>() {
		};
		List<Map<String, List<String>>> iList = mapper.readValue(intents,
				typeRef);
		// logger.info("iList: " + iList);
		String prefixrex = "";
		String suffixrex = "";
		for (Map<String, List<String>> m : iList) {
			Iterator<String> iter = m.keySet().iterator();
			while (iter.hasNext()) {
				String intent = iter.next();
				if ("prefixrex".equals(intent)) {
					List<String> rexps = m.get(intent);
					if (rexps != null && !rexps.isEmpty()) {
						prefixrex += "(";
						String r = "";
						for (String rexp : rexps) {
							if (r.length() > 0) {
								r += "|";
							}
							r += "(" + rexp + ")";
						}
						prefixrex += r + ")";
					}
				} else if ("suffixrex".equals(intent)) {
					List<String> rexps = m.get(intent);
					if (rexps != null && !rexps.isEmpty()) {
						suffixrex += "(";
						String r = "";
						for (String rexp : rexps) {
							if (r.length() > 0) {
								r += "|";
							}
							r += "(" + rexp + ")";
						}
						suffixrex += r + ")";
					}
				} else {
					List<String> rexps = m.get(intent);
					if (result.length() > 0) {
						result += "|";
					}
					result += "(?<" + prefix + intent + ">";
					String r = "";
					for (String rexp : rexps) {
						if (r.length() > 0) {
							r += "|";
						}
						r += "(" + rexp + ")";
					}
					result += r + ")";
				}
			}
		}
		return prefixrex + "(" + result + ")" + suffixrex;
	}

	private String loadSynonymRex(String fileName) throws Exception {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		File synonymsF = new File(fileName);
		TypeReference<Map<String, List<String>>> typeRef = new TypeReference<Map<String, List<String>>>() {
		};
		Map<String, List<String>> sMap = mapper.readValue(synonymsF, typeRef);
		logger.info("sMap: " + sMap);

		Iterator<String> iter = sMap.keySet().iterator();
		while (iter.hasNext()) {
			String concept = iter.next();
			List<String> synonymRexps = sMap.get(concept);
			if (result.length() > 0) {
				result += "|";
			}
			result += "(?<" + concept + ">";
			String r = "";
			for (String rexp : synonymRexps) {
				if (r.length() > 0) {
					r += "|";
				}
				r += "(" + rexp + ")\\b";
			}
			result += r + ")";
		}

		return "(" + result + ")";
	}

	private String replaceSynonyms(String utterance) {
		String result = new String(utterance);
		Matcher m = synonymPattern.matcher(utterance);
		while (m.find()) {
			// logger.info("match start: "+m.start()+"; end: "+m.end());
			for (int i = 1; i <= m.groupCount(); i++) {
				String gv = m.group(i);
				String gn = synonymNPattern.groupName(i);
				// logger.info("gv: "+gv+"; gn: "+gn);
				if (gv != null && gn != null) {
					int ix = result.indexOf(gv);
					if (ix > -1 && (ix + gv.length()) <= result.length()) {
						result = result.substring(0, ix) + gn
								+ result.substring(ix + gv.length());
					}
				}
			}
		}
		return result;
	}

	private String disambiguate(List<String> intents, List<String> params,
			String utterance) {
		String result = "";
		if (intents != null && !intents.isEmpty()) {
			// TODO
			result += intents.get(0);
			result += "({";
			int i = 0;
			for (String p : params) {
				result += p;
				i++;
				if (i < params.size())
					result += ", ";
			}
			result += "});";
		} else {
			if (params != null && !params.isEmpty())
				result += "expected({" + params + "});";
			else
				result += "nomatch(\"" + utterance + "\");";
		}
		return result;
	}
	
	private List<String> loadPatternNames(String nluDir)throws IOException{
		List<String> result= new LinkedList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(nluDir+"/"+PATTERN_ORDER_FNAME), "utf-8"));
		do{
			String l = br.readLine();
			if(l!=null){
				l=l.trim();
				if(l.length()>0)
					result.add(l);
			}
		}while(br.ready());
		br.close();
		return result;
	}
	
	private Map<String, String> loadPatterns(String nluDir, List<String> patternNames)throws Exception{
		Map<String, String> result= new HashMap<String, String>();
		for(String aName: patternNames){
			String pattern =null;
			if(aName.equals("intents"))
				pattern = loadRex("intent:=", nluDir + "/"+aName+".json");
			else
				pattern = loadRex("", nluDir + "/"+aName+".json");
			result.put(aName, pattern);
		}
		return result;
	}
	
	private List<String> expandNamesReferences(
			List<String> patternNames, Map<String, String> patternMap)throws Exception{
		// returns a list of expanded pattern reguar expressions in the order of given patternNmaes list.
		List<String> result = new LinkedList<String>();
		NameRefComparator c = new NameRefComparator();
		TreeSet<Entry<String, String>> s = new TreeSet<Entry<String, String>>(c);
		s.addAll(patternMap.entrySet());
		Map<String, String> expM=new HashMap<String, String>(patternMap);
		for(Entry<String, String> e: s){
			String n=e.getKey();
			String p=e.getValue();
			Pattern vp=Pattern.compile("\\$\\{([^\\{\\$\\}]+)\\}");
			Matcher vm=vp.matcher(p);
			String rp=new String(p);
			while(vm.find()){
				String varName = vm.group(1);
				rp=rp.substring(0, vm.start())+expM.get(varName)+rp.substring(vm.end());
			}
			expM.put(n, rp);
		}
		for(String name: patternNames){
			result.add(expM.get(name));
		}
		return result;
	}

	public String getUtteranceListener() {
		return utteranceListener;
	}

	public void setUtteranceListener(String utteranceListener) {
		this.utteranceListener = utteranceListener;
	}
	
	public void removeUtteranceListenerAfterTimeMillis(int milliseconds){
		if(milliseconds>0){
			if(removeUttListenerFuture!=null){
				removeUttListenerFuture.cancel(false);
			}
			removeUttListenerFuture=exec.schedule(new Runnable(){
				public void run(){
					setUtteranceListener(null);
				}
			}, milliseconds, TimeUnit.MILLISECONDS);
		}
	}
}
