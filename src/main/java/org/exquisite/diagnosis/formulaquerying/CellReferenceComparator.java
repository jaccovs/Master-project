package org.exquisite.diagnosis.formulaquerying;

import java.util.Comparator;
import java.util.Map;

/***
 * Class to compare two cell references. (e.g. WS_1_S16)
 * If class is instantiated with ranking, ranking is used first to sort the references.
 * If ranking is null or the compared references have the same ranking, the column and then the row is used for sorting.
 * @author Thomas
 *
 */
public class CellReferenceComparator implements Comparator<String> {
	
	private Map<String, Float> ranking = null;
	private static float MAX_FLOAT_DEVIATION = 0.00001f;
	
	/**
	 * Instantiates the comparator without a ranking.
	 */
	public CellReferenceComparator() {
		
	}
	
	/**
	 * Instantiates the comparator with the given ranking.
	 * @param ranking
	 */
	public CellReferenceComparator(Map<String, Float> ranking) {
		this.ranking = ranking;
	}

	@Override
	public int compare(String c1, String c2) {
		if (ranking != null) {
			float r1 = ranking.get(c1);
			float r2 = ranking.get(c2);
			if (!equals(r1, r2)) {
				float c = r2 - r1;
				if (c < 0) {
					return (int)Math.floor(c);
				} else {
					return (int)Math.ceil(c);
				}
			}
		}
		String[] f1s = split(c1);
		String[] f2s = split(c2);
		
		if (!f1s[0].equals(f2s[0])) {
			return f1s[0].compareTo(f2s[0]);
		}
		return Integer.parseInt(f1s[1]) - Integer.parseInt(f2s[1]);
	}
	
	private boolean equals(float f1, float f2) {
		return Math.abs(f1 - f2) <= MAX_FLOAT_DEVIATION;
	}
	
	private String[] split(String cell) {
		String[] ret = new String[2];
		for (int i = cell.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(cell.charAt(i))) {
				ret[0] = cell.substring(0, i+1);
				ret[1] = cell.substring(i+1);
				break;
			}
		}
		return ret;
	}


}
