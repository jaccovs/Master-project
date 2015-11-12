package org.exquisite.fragmentation;

import java.util.List;

import org.exquisite.datamodel.Fragment;

public interface IFragmentExtractor {
	List<Fragment> calculateFragmentation();
	Fragment buildFragment(List<String> cells);
	void printComplexity(Fragment fragment);
}
