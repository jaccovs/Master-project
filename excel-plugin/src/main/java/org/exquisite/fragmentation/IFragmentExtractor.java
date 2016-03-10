package org.exquisite.fragmentation;

import org.exquisite.datamodel.Fragment;

import java.util.List;

public interface IFragmentExtractor {
    List<Fragment> calculateFragmentation();

    Fragment buildFragment(List<String> cells);

    void printComplexity(Fragment fragment);
}
