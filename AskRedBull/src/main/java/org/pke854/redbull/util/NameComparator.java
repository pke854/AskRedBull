package org.pke854.redbull.util;

import java.util.Comparator;

public class NameComparator implements Comparator<String> {

	public int compare(String s1, String s2) {
		if (s1 != null && s2 != null) {
			if (s1.contains(s2))
				return -1;
			else if (s2.contains(s1))
				return 1;
			else
				return s1.compareTo(s2);
		} else {
			if (s1 == null && s2 == null)
				return 0;
			else if (s1 == null)
				return -1;
			else
				return 1;
		}
	}

}
