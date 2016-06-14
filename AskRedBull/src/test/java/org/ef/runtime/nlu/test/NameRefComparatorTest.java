package org.ef.runtime.nlu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.junit.Test;
import org.pke854.redbull.util.NameRefComparator;

public class NameRefComparatorTest {
	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(NameRefComparatorTest.class.getName());
	@Test
	public void test() {
		Map<String, String> m = new HashMap<String, String>();
		m.put("a", "123456");
		m.put("b", "123 ${a} 456");
		m.put("c", "123 ${c} 456");
		m.put("d", "123456");
		m.put("e", "123456");
		
		NameRefComparator c = new NameRefComparator();
		TreeSet<Entry<String, String>> s = new TreeSet<Entry<String, String>>(c);
		try{
			s.addAll(m.entrySet());
			fail("autoreference not detected.");
		}catch(IllegalArgumentException e){
			logger.info("autoreference detected.");
			m.put("c", "123 ${d} 456");
			m.put("a", "123 ${b} 456");
			s = new TreeSet<Entry<String, String>>(c);
			try{
				s.addAll(m.entrySet());
				fail("cyclic autoreference not detected.");
			}catch(IllegalArgumentException e1){
				logger.info("cyclic autoreference detected.");
				m.put("a", "123 ${e} 456");
				s = new TreeSet<Entry<String, String>>(c);
				try{
					s.addAll(m.entrySet());
					logger.info("sorted: "+s);
					assertEquals("[e=123456, d=123456, a=123 ${e} 456, c=123 ${d} 456, b=123 ${a} 456]", s.toString());
				}catch(IllegalArgumentException e2){
					fail(""+e2);
				}
			}
		}
	}

}
