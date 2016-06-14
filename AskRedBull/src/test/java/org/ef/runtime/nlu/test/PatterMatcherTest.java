package org.ef.runtime.nlu.test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import org.junit.Test;
import org.pke854.redbull.runtime.nlu.PatternMatcher;

public class PatterMatcherTest {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(PatterMatcherTest.class.getName());
	public static final String NLUDIR = "WebContent/nlu";
	public static final String NLUTESTCASES = "testdata/nlu.test.cases.txt";

	@Test
	public void test() {
		try {
			PatternMatcher pm = new PatternMatcher(NLUDIR);
			Map<String, String> m = loadTestCases(NLUTESTCASES);
			Iterator<Entry<String, String>> iter = m.entrySet().iterator();
			int n=0;
			long t=0;
			int maxLat=0;
			while (iter.hasNext()) {
				Entry<String, String> ent = iter.next();
				String utt = ent.getKey();
				long t1 = System.currentTimeMillis();
				String nlu = pm.interpret(utt);
				int lat=(int)(System.currentTimeMillis()-t1);
				if(lat>maxLat)maxLat=lat;
				t+=lat;
				n++;
				if (!nlu.equals(ent.getValue())) {
					logger.warning("utt: '" + utt + "'");
					logger.warning("nlu: '" + nlu + "'");
					logger.warning("exp: '" + ent.getValue() + "'");
					fail("incorrect nlu: '" + nlu + "'; expected: '"
							+ ent.getValue() + "'");
					break;
				}
			}
			logger.warning("average latency: " + t/n +" ms; max latency: "+maxLat+" ms");

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			fail(e.toString());
		}

	}

	private Map<String, String> loadTestCases(String fname) throws Exception {
		Map<String, String> testCasesMap = new TreeMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fname), "utf-8"));
		do {
			String utt = br.readLine();
			String nlu = br.readLine();
			testCasesMap.put(utt, nlu);
		} while (br.ready());
		br.close();
		return testCasesMap;
	}

}
