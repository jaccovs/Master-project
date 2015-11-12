package org.exquisite.fragmentation;

import java.util.List;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;

public class MergingFragmentExtractor extends OneCellFragmentExtractor {
	
	public static float MAX_COMPLEXITY = 5f;
	public static float COMPLEXITY_DIFFERENCE = 0.1f;

	public MergingFragmentExtractor(ExquisiteAppXML appXML) {
		super(appXML);
		columnRowCopyEquivalenceOnly = true;
	}

	@Override
	public List<Fragment> calculateFragmentation() {
		List<Fragment> fragments = super.calculateFragmentation();
		
		mergeFragments(fragments);
		
		return fragments;
	}

	protected void mergeFragments(List<Fragment> fragments) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment f1 = fragments.get(i);
			float c1 = f1.getComplexity();
			for (int k = i+1; k < fragments.size(); k++) {
				Fragment f2 = fragments.get(k);
				float c2 = f2.getComplexity();
				
				if (canMerge(f1, f2)) {
				
					Fragment merged = buildMergedFragment(f1, f2, true);
					float cm = merged.getComplexity();
					
					
					System.out.println(f1.getName() + ": " + c1 + " " + f2.getName() + ": " + c2 + " Merged: " + cm);
					
					if (cm + COMPLEXITY_DIFFERENCE < c1 + c2 && cm < MAX_COMPLEXITY) {
						System.out.println("Merged " + f1.getName() + " and " + f2.getName() + ".");
						fragments.set(i, merged);
						fragments.remove(k);
						k--;
						
						// Update f1 and its complexity
						f1 = merged;
						c1 = f1.getComplexity();
					}
				}
			}
		}
	}
}
