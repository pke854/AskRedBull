package org.pke854.redbull.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;


public class SparqlRequest {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(SparqlRequest.class.getName());

	String endpoint = "http://dbpedia.org/sparql";

	String sparqlDir;
	String nluDir;

	public SparqlRequest(String sparqlDir, String nluDir) {
		super();
		this.sparqlDir = sparqlDir;
		this.nluDir = nluDir;
	}

	public void processQuery(String lang, String name) throws Exception {


		String queryString = loadQuery(sparqlDir + "/" + name + ".sparql");
		queryString = queryString.replaceAll("\\$LANG", lang);
		logger.info("queryString: " + queryString);

//		Query query = QueryFactory.create(queryString);
//
//		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint,
//				query);
//		try {
//			ResultSet results = qexec.execSelect();
//			logger.info("results: " + results);
//			// logger.info(ResultSetFormatter.asText(results, query));
//			PrintWriter out = new PrintWriter(new File(nluDir + "/" + name
//					+ ".json"), "UTF-8");
//
//			SortedMap<String, String> m = new TreeMap<String, String>(
//					new Comparator<String>() {
//
//						@Override
//						public int compare(String s0, String s1) {
//							if (s0 == null && s1 == null)
//								return 0;
//							if (s0 == null && s1 != null)
//								return 1;
//							if (s0 != null && s1 == null)
//								return -1;
//							if (s0.length() < s1.length())
//								return 1;
//							if (s0.length() > s1.length())
//								return -1;
//							if (s0.length() == s1.length())
//								return s0.compareTo(s1);
//							return 0;
//						}
//
//					});
//
//			for (; results.hasNext();) {
//				QuerySolution soln = results.nextSolution();
//				String s = "";
//				RDFNode p = soln.get("p"); // Get a result variable by name.
//				RDFNode o = soln.get("o"); // Get a result variable by name.
//				Literal l = soln.getLiteral("l"); // Get a result variable by
//				Literal id = soln.getLiteral("id"); // Get a result variable by
//				String value = query.shortForm(p.toString());
//				value = value.replaceAll("\"", "\\\\\"");
//				if (o != null) {
//					String vs = query.shortForm(o.toString());
//					if (!"rdf:nil".equals(vs)) {
//
//					}else {
//						
//						vs = query.shortForm(l.getLexicalForm());
//						vs = vs.replaceAll("\"", "\\\\\"");
//						vs = vs.replaceAll("\\(", "\\\\\\\\(");
//						vs = vs.replaceAll("\\)", "\\\\\\\\)");
//						value += ":=" + vs;
//						if(id!=null) {
//							value += ";ont:wikiPageID:=" + id.getLexicalForm();
//						}
//					}
//				}
//				String key = Normalizer.normalizeSentence(l.getLexicalForm());
//				if (!m.containsKey(key)) {
//					m.put(key, value);
//				}
//
//			}
//
//			int i = 0;
//			out.println("[");
//			Iterator<Entry<String, String>> iter = m.entrySet().iterator();
//			while (iter.hasNext()) {
//				Entry<String, String> entr = iter.next();
//				if (i > 0)
//					out.println(",");
//				out.print("\t" + "{\"" + entr.getValue() + "\" : [\""
//						+ entr.getKey() + "\"]}");
//
//				i++;
//			}
//			out.println();
//			out.println("]");
//			out.close();
//		} finally {
//			qexec.close();
//		}
	}

	private String loadQuery(String fName) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fName), "utf-8"));
		String result = "";
		do {
			result += br.readLine() + "\n";
		} while (br.ready());
		br.close();
		return result;
	}

	public static void main(String[] args) {
		try {
			SparqlRequest sr = new SparqlRequest("sparql", "WebContent/nlu");
			//String[] names= {"labels"};
			String[] names= {"labels", "manufacturer", "types", "forms", "os", "cpu"};
			for(String name: names)
				sr.processQuery("en", name);
			logger.info("query started..");
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.toString(), ex);
		}
	}
}
