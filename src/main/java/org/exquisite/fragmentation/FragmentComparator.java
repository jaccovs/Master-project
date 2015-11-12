package org.exquisite.fragmentation;

import java.util.Comparator;

import org.exquisite.datamodel.Fragment;
import org.exquisite.tools.CellAddressComparator;

/**
 * A comparison for fragments based on the representative cell.
 * @author Thomas
 *
 */
public class FragmentComparator implements Comparator<Fragment> {
	
	//private AlphanumComparator comp = new AlphanumComparator();
	private CellAddressComparator comp = new CellAddressComparator();

	@Override
	public int compare(Fragment o1, Fragment o2) {
		return comp.compare(o1.getRepresentative(), o2.getRepresentative());
	}

}
