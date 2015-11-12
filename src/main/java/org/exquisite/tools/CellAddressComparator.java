package org.exquisite.tools;

import java.util.Comparator;

/**
 * Class to compare two cell addresses.
 * @author Thomas
 *
 */
public class CellAddressComparator implements Comparator<String> {
	
	public enum ComparisonType {RowsFirst, ColumnsFirst};
	
	private ComparisonType comparisonType;
	
	public CellAddressComparator() {
		this.comparisonType = ComparisonType.RowsFirst;
	}
	
	public CellAddressComparator(ComparisonType comparisonType) {
		this.comparisonType = comparisonType;
	}

	@Override
	public int compare(String s1, String s2) {
		int t1, t2;
		if (comparisonType == ComparisonType.RowsFirst) {
			t1 = StringUtilities.getCellRow(s1);
			t2 = StringUtilities.getCellRow(s2);
		} else {
			t1 = StringUtilities.getCellColumn(s1);
			t2 = StringUtilities.getCellColumn(s2);
		}
		if (t1 != t2) {
			return t1 - t2;
		}
		
		if (comparisonType == ComparisonType.ColumnsFirst) {
			t1 = StringUtilities.getCellRow(s1);
			t2 = StringUtilities.getCellRow(s2);
		} else {
			t1 = StringUtilities.getCellColumn(s1);
			t2 = StringUtilities.getCellColumn(s2);
		}
		
		return t1 - t2;
	}

}
