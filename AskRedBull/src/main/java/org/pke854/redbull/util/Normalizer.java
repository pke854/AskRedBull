package org.pke854.redbull.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.Soundex;

public class Normalizer {
	
	public enum NORM_MODE{
		CONVERT_WORDS_TO_DIGITS, CONVERT_DIGITS_TO_WORDS, CONVERT_ARAB_DIGITS_TO_ROME
	}

	private static final Logger logger = Logger.getLogger(Normalizer.class
			.getSimpleName());

	static Soundex sndx = new Soundex();

	public static List<String> normalizeAndTokenize(String sentence) {
		return normalizeAndTokenize(sentence, NORM_MODE.CONVERT_DIGITS_TO_WORDS, null);
	}

	public static List<String> normalizeAndTokenize(String sentence, NORM_MODE mode,
			String lang) {
		List<String> result = new LinkedList<String>();
		if (sentence != null) {
			// trim
			sentence = sentence.trim();
			// lower case
			sentence = sentence.toLowerCase();
			// replace some values
			sentence = sentence.replaceAll("&", " and ");
			sentence = sentence.replaceAll("@rt", "art");
			sentence = sentence.replaceAll("''", "");
			sentence = sentence.replaceAll("´", "'");
			sentence = sentence.replaceAll("^'", "");
			sentence = sentence.replaceAll("'$", "");
			sentence = sentence.replaceAll("\\s+'", " ");
			sentence = sentence.replaceAll("'\\s+", " ");
			sentence = sentence.replaceAll("\\\"", "");
			if (mode == NORM_MODE.CONVERT_WORDS_TO_DIGITS) {
				// TODO lang
				if (lang != null && lang.equalsIgnoreCase("ger")) {
					sentence = sentence.replaceAll("\\bnull\\b", "0");
					sentence = sentence.replaceAll("\\beins\\b", "1");
					sentence = sentence.replaceAll("\\bzwei\\b", "2");
					sentence = sentence.replaceAll("\\bzwo\\b", "2");
					sentence = sentence.replaceAll("\\bdrei\\b", "3");
					sentence = sentence.replaceAll("\\bvier\\b", "4");
					sentence = sentence.replaceAll("\\bfünf\\b", "5");
					sentence = sentence.replaceAll("\\bsechs\\b", "6");
					sentence = sentence.replaceAll("\\bsieben\\b", "7");
					sentence = sentence.replaceAll("\\bacht\\b", "8");
					sentence = sentence.replaceAll("\\bneun\\b", "9");
					sentence = sentence.replaceAll("\\bund\\b", "&");
					sentence = sentence.replaceAll("\\bdoktor\\b", "dr");
				} else if (lang != null && lang.equalsIgnoreCase("eng")) {
					sentence = sentence.replaceAll("\\bnull\\b", "0");
					sentence = sentence.replaceAll("\\bone\\b", "1");
					sentence = sentence.replaceAll("\\btwo\\b", "2");
					sentence = sentence.replaceAll("\\bthree\\b", "3");
					sentence = sentence.replaceAll("\\bfour\\b", "4");
					sentence = sentence.replaceAll("\\bfive\\b", "5");
					sentence = sentence.replaceAll("\\bsix\\b", "6");
					sentence = sentence.replaceAll("\\bseven\\b", "7");
					sentence = sentence.replaceAll("\\beight\\b", "8");
					sentence = sentence.replaceAll("\\bnine\\b", "9");
					sentence = sentence.replaceAll("\\band\\b", "&");
				}
			} else if (mode == NORM_MODE.CONVERT_DIGITS_TO_WORDS) {
				// TODO
				if (lang != null && lang.equalsIgnoreCase("ger")) {
					sentence = sentence.replaceAll("\\b0\\b", "null");
					sentence = sentence.replaceAll("\\b1\\b", "eins");
					sentence = sentence.replaceAll("\\b2\\b", "zwei");
					sentence = sentence.replaceAll("\\b3\\b", "drei");
					sentence = sentence.replaceAll("\\b4\\b", "vier");
					sentence = sentence.replaceAll("\\b5\\b", "fünf");
					sentence = sentence.replaceAll("\\b6\\b", "sechs");
					sentence = sentence.replaceAll("\\b7\\b", "sieben");
					sentence = sentence.replaceAll("\\b8\\b", "acht");
					sentence = sentence.replaceAll("\\b9\\b", "neun");
					sentence = sentence.replaceAll("\\b&\\b", "und");
				} else if (lang != null && lang.equalsIgnoreCase("eng")) {
					sentence = sentence.replaceAll("\\b0\\b", "null");
					sentence = sentence.replaceAll("\\b1\\b", "one");
					sentence = sentence.replaceAll("\\b2\\b", "two");
					sentence = sentence.replaceAll("\\b3\\b", "three");
					sentence = sentence.replaceAll("\\b4\\b", "four");
					sentence = sentence.replaceAll("\\b5\\b", "five");
					sentence = sentence.replaceAll("\\b6\\b", "six");
					sentence = sentence.replaceAll("\\b7\\b", "seven");
					sentence = sentence.replaceAll("\\b8\\b", "eight");
					sentence = sentence.replaceAll("\\b9\\b", "nine");
					sentence = sentence.replaceAll("\\b&\\b", "and");
				}
			} else if (mode == NORM_MODE.CONVERT_ARAB_DIGITS_TO_ROME) {
				// TODO
				if (lang != null && lang.equalsIgnoreCase("ger")) {
					sentence = sentence.replaceAll("\\b1\\b", "i");
					sentence = sentence.replaceAll("\\b2\\b", "ii");
					sentence = sentence.replaceAll("\\b3\\b", "iii");
					sentence = sentence.replaceAll("\\b4\\b", "iv");
					sentence = sentence.replaceAll("\\b5\\b", "v");
					sentence = sentence.replaceAll("\\b6\\b", "vi");
					sentence = sentence.replaceAll("\\b7\\b", "vii");
					sentence = sentence.replaceAll("\\b8\\b", "viii");
					sentence = sentence.replaceAll("\\b9\\b", "ix");

				} else if (lang != null && lang.equalsIgnoreCase("eng")) {
					sentence = sentence.replaceAll("\\b1\\b", "i");
					sentence = sentence.replaceAll("\\b2\\b", "ii");
					sentence = sentence.replaceAll("\\b3\\b", "iii");
					sentence = sentence.replaceAll("\\b4\\b", "iv");
					sentence = sentence.replaceAll("\\b5\\b", "v");
					sentence = sentence.replaceAll("\\b6\\b", "vi");
					sentence = sentence.replaceAll("\\b7\\b", "vii");
					sentence = sentence.replaceAll("\\b8\\b", "viii");
					sentence = sentence.replaceAll("\\b9\\b", "ix");
					
				}
			}
			sentence = sentence.replaceAll("(?<=\\d)\\s+(?=\\d)", "");
			// remove all illegal characters
			// sentence = sentence.replaceAll("", "");
			// add whitespaces for cases like '10vor10' -> '10 vor 10'
			Pattern p = Pattern.compile("([a-zäöüßáàåãéèëêíïóúýžš']+)");
			Matcher m = p.matcher(sentence);
			boolean hasMatch = false;
			String nsent = "";
			int sp = 0;
			while (m.find()) {
				hasMatch = true;
				nsent += sentence.substring(sp, m.start()) + " " + m.group()
						+ " ";
				sp = m.end();
			}
			if (sp < sentence.length()) {
				nsent += sentence.substring(sp);
			}
			if (hasMatch)
				sentence = nsent;
			// split
			for (String t : sentence.split("[^a-zäöüßáàåãéèëêíïóúýžš'\\d]+")) {
				if (t.trim().length() > 0)
					result.add(t);
			}
		}
		if (logger.isLoggable(Level.FINE))
			logger.fine("normalizeAndTokenize('" + sentence + "'): " + result);
		return result;
	}

	public static String normalizeSentence(String sentence) {
		String result = "";
		for (String t : normalizeAndTokenize(sentence)) {
			result += " " + t;
		}
		return result.trim();
	}

	public static String encodeWithCommonSoundex(String t) {

		t = t.toLowerCase();
		t = t.replaceAll("ä", "ae");
		t = t.replaceAll("ö", "oe");
		t = t.replaceAll("ü", "ue");
		t = t.replaceAll("ß", "ss");
		t = t.replaceAll("á", "a");
		t = t.replaceAll("à", "a");
		t = t.replaceAll("å", "a");
		t = t.replaceAll("ã", "a");
		t = t.replaceAll("é", "e");
		t = t.replaceAll("è", "e");
		t = t.replaceAll("ë", "e");
		t = t.replaceAll("ê", "e");
		t = t.replaceAll("í", "i");
		t = t.replaceAll("ï", "i");
		t = t.replaceAll("ó", "o");
		t = t.replaceAll("ú", "u");
		t = t.replaceAll("ý", "y");
		t = t.replaceAll("ž", "z");
		t = t.replaceAll("š", "s");
		t = t.trim();
		String[] toks = t.split(" ");
		String result = "";
		for (String tt : toks) {
			String r = new String(tt);
			r = sndx.encode(r);
			if (r == null || r.length() == 0)
				r = tt;
			result += r + " ";
		}
		result = result.trim();
		return result;
	}

	public static String truncate(String s, int maxLen) {
		String result = s;
		if (s!=null && s.length() > maxLen) {
			int lix = s.lastIndexOf(" ", maxLen);
			if (lix > 0)
				result = s.substring(0, lix);
			else {
				result = s.substring(0, maxLen);
				logger.warning("strange name to truncate: '" + s + "'");
			}
		}
		return result;
	}

	public static Collection<String> groupTokens(String[] tokens, int order) {
		List<String> result = new LinkedList<String>();
		if (order > 0) {
			if (order == 1) {
				for (String t : tokens)
					result.add(t);
			} else if (tokens.length == 2 && order == 2) {
				result.add(tokens[0] + " " + tokens[1]);
			} else if (tokens.length == 2 && order == 3) {
				// nothing to do
			} else if (tokens.length == 3 && order == 3) {
				result.add(tokens[0] + " " + tokens[1] + " " + tokens[2]);
			} else if (tokens.length == 3 && order == 2) {
				result.add(tokens[0] + " " + tokens[1]);
				result.add(tokens[1] + " " + tokens[2]);
			} else {
				// forward
				for (int i = 0; i < tokens.length - 1; i++) {
					String t = "";
					for (int j = 0; j < order && ((i + order) < tokens.length); j++) {
						t += tokens[i + j] + " ";
					}
					t = t.trim();
					if (t.length() > 0 && !result.contains(t))
						result.add(t);
				}
				// backward
				for (int i = tokens.length - 1; i >= 0; i--) {
					String t = "";
					for (int j = order - 1; j >= 0 && ((i - order) >= 0); j--) {
						t = t + " " + tokens[i - j];
					}
					t = t.trim();
					if (t.length() > 0 && !result.contains(t))
						result.add(t);
				}
			}
		} else {
			logger.warning("order less than 1: " + order);
		}
		return result;
	}

	public static double probSum(double w1, double w2) {
		List<Double> weights = new LinkedList<Double>();
		weights.add(w1);
		weights.add(w2);
		return probSum(weights);
	}

	public static double probSum(List<Double> weights) {
		double result = 0.;
		if (weights != null && !weights.isEmpty()) {
			if (weights.size() == 1)
				result = weights.get(0);
			else {
				double p = 1.;
				for (int i = 1; i < weights.size(); i++) {
					p = p * (1. - weights.get(i));
				}
				result = 1. - (1. - weights.get(0)) * p;
			}
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			// String [] sent=new String[]{
			// "auch","mit", "der", "allerschlagfertigsten"
			// };
			// System.out.println(groupTokens(sent, 2));
			ArrayList<Double> weights = new ArrayList<Double>();
			// w1: 0.59820895536; w2: 0.311332406208; w: 0.5880328781274029
			// w1: 0.53820895536; w2: 0.3040153330372683; w: 0.6254148194474107
			// weights.add(0.59820895536);
			// weights.add(0.6);

			weights.add(0.53820895536);
			weights.add(0.6);

			/*
			 * weights.add(0.6*0.04); weights.add(0.6*0.22);
			 * weights.add(0.6*0.2); weights.add(0.6*0.48);
			 * weights.add(0.6*0.04); weights.add(0.6*0.22);
			 */
			System.out.println("probSum(" + weights + "): " + probSum(weights));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
}
