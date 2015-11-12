package org.exquisite.fragmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.data.ConstraintsFactory;
import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.fragmentation.complexity.ComplexityEstimator;
import org.exquisite.parser.FormulaParser;
import org.exquisite.tools.CellAddressComparator;
import org.exquisite.tools.StringUtilities;
import org.exquisite.tools.Utilities;

/**
 * Fragment extraction class that builds one cell fragments and links all copy equivalent fragments.
 * @author Thomas
 *
 */
public class OneCellFragmentExtractor implements IFragmentExtractor {
	
	public boolean columnRowCopyEquivalenceOnly = false;
	
	private FragmentComparator fragmentComparator = new FragmentComparator();
	private CellAddressComparator cellAddressComparator = new CellAddressComparator();
	
	ExquisiteAppXML appXML = null;
	protected ComplexityEstimator complexityEstimator;
	
	public OneCellFragmentExtractor(ExquisiteAppXML appXML) {
		this.appXML = appXML;
		complexityEstimator = new ComplexityEstimator(appXML);
	}

	@Override
	public List<Fragment> calculateFragmentation() {
		List<Fragment> fragments = new ArrayList<Fragment>();
				
		Dictionary<String, String> formulas = appXML.getFormulas();
		Dictionary<String, String> formulasR1C1 = appXML.getFormulasR1C1();
		
		Enumeration<String> cells = formulas.keys();
		
		while (cells.hasMoreElements()) {
			String cell = cells.nextElement();
			String formula = formulas.get(cell);
			String formulaR1C1 = formulasR1C1.get(cell);
			
			List<String> fragmentCells = new ArrayList<String>();
			fragmentCells.add(cell);
			
			Fragment fragment = buildFragment(fragmentCells);
			fragments.add(fragment);
		}
		
		Collections.sort(fragments, fragmentComparator);
		
		linkCopyEquivalentFragments(fragments);
		
		return fragments;
	}

	/**
	 * Searches all fragments for copy-equivalent fragments and links them.
	 * @param fragments
	 */
	private void linkCopyEquivalentFragments(List<Fragment> fragments) {
		boolean searchAgain = true;
		while (searchAgain) {
			searchAgain = false;
			for (int i = 1; i < fragments.size(); i++) {
				Fragment fragmentToLink = fragments.get(i);
				
				for (int k = 0; k < i; k++) {
					Fragment fragmentParent = fragments.get(k);
					if (isCopyEquivalent(fragmentToLink, fragmentParent) && 
							(!columnRowCopyEquivalenceOnly || hasSameColumnOrRow(fragmentParent, fragmentToLink))) {
						if (columnRowCopyEquivalenceOnly) {
							searchAgain = true;
						}
						fragmentParent.getLinkedFragments().add(fragmentToLink.getRepresentative());
						fragments.remove(i);
						i--;
						break;
					}
				}
			}
		}
	}

	private boolean hasSameColumnOrRow(Fragment fragmentParent,
			Fragment fragmentToLink) {
		
		String cell1, cell2;
		// Go through all representative cells of fragmentParent (Representative and linkedFragments)
		for (int i = 0; i < fragmentParent.getLinkedFragments().size() + 1; i++) {
			if (i == fragmentParent.getLinkedFragments().size()) {
				cell1 = fragmentParent.getRepresentative();
			} else {
				cell1 = fragmentParent.getLinkedFragments().get(i);
			}
			
			// Go through all representative cells of fragmentToLink (Representative and linkedFragments)
			for (int k = 0; k < fragmentToLink.getLinkedFragments().size() + 1; k++) {
				if (k == fragmentToLink.getLinkedFragments().size()) {
					cell2 = fragmentToLink.getRepresentative();
				} else {
					cell2 = fragmentToLink.getLinkedFragments().get(k);
				}
				
				// Check for same column or row
				if (StringUtilities.getCellColumn(cell1) == StringUtilities.getCellColumn(cell2) 
						|| StringUtilities.getCellRow(cell1) == StringUtilities.getCellRow(cell2)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests if given fragments are copy-equivalent.
	 * @param f1
	 * @param f2
	 * @return
	 */
	private boolean isCopyEquivalent(Fragment f1, Fragment f2) {
		if (f1 == null || f2 == null) {
			return false;
		}
		if ((f1.getInputs().size() != f2.getInputs().size()) || (f1.getInterims().size() != f2.getInterims().size()) || (f1.getOutputs().size() != f2.getOutputs().size())) {
			return false;
		}
		
		for (int i = 0; i < f1.getInterims().size(); i++) {
			if (!appXML.getFormulasR1C1().get(f1.getInterims().get(i)).equals(appXML.getFormulasR1C1().get(f2.getInterims().get(i)))) {
				return false;
			}
		}
		
		for (int i = 0; i < f1.getOutputs().size(); i++) {
			if (!appXML.getFormulasR1C1().get(f1.getOutputs().get(i)).equals(appXML.getFormulasR1C1().get(f2.getOutputs().get(i)))) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Builds a fragment with the given cells.
	 * @param cells
	 * @param appXML
	 * @return
	 */
	public Fragment buildFragment(List<String> cells) {
		return buildFragment(cells, true);
	}
	
	/**
	 * Builds a fragment with the given cells.
	 * @param cells
	 * @param calcComplexity Should the complexity of the fragment be calculated?
	 * @return
	 */
	public Fragment buildFragment(List<String> cells, boolean calcComplexity) {
		Fragment fragment = new Fragment();
		
		if (cells.size() == 0) {
			return fragment;
		}
		
		
		// first add all cells to outputs
		fragment.getOutputs().addAll(cells);
		
		List<String> cellsLeft = new ArrayList<String>(cells);
		// then iterate over all outputs and find their input cells
		while (cellsLeft.size() > 0) {
			String cell = cellsLeft.remove(0);
			String formula = appXML.getFormulas().get(cell);
			List<String> inputCells = new ArrayList<>(getInputcells(formula));
			// for each input cell
			for (int i = 0; i < inputCells.size(); i++) {
				String inputCell = inputCells.get(i);
				if (fragment.getOutputs().contains(inputCell)) {
					// if it was an output cell before, it will be an interim cell
					fragment.getOutputs().remove(inputCell);
					if (!fragment.getInterims().contains(inputCell))
					{
						fragment.getInterims().add(inputCell);
					}
				}
				else if (!fragment.getInterims().contains(inputCell) && !fragment.getInputs().contains(inputCell)) {
					// otherwise if it also is no interim cell, it will be an input cell of the fragment
					fragment.getInputs().add(inputCell);
				}
			}
		}
		
		// sort cells
		Collections.sort(fragment.getInputs(), cellAddressComparator);
		Collections.sort(fragment.getInterims(), cellAddressComparator);
		Collections.sort(fragment.getOutputs(), cellAddressComparator);
		
		// set the first output as representative
		fragment.setRepresentative(fragment.getOutputs().get(0));
		
		// Let the representatives name be the name of the fragment for now
		fragment.setName(fragment.getRepresentative());
		
		// Calculate fragments complexity
		if (calcComplexity) {
			calculateComplexity(fragment);
		}
		
		return fragment;
	}
	
	@Override
	public void printComplexity(Fragment fragment) {
		boolean previous = complexityEstimator.Debug;
		complexityEstimator.Debug = true;
		complexityEstimator.getComplexity(fragment);
		complexityEstimator.Debug = previous;
	}

	public void calculateComplexity(Fragment fragment) {
		float complexity = complexityEstimator.getComplexity(fragment);
		fragment.setComplexity(complexity);
		//System.out.println(fragment.getRepresentative() + " with formula " + appXML.getFormulas().get(fragment.getRepresentative()) + " has complexity: " + complexity);
	}

	/**
	 * Returns the input cells for the given formula.
	 * @param formula
	 * @return
	 */
	private Set<String> getInputcells(String formula) {
		Set<String> inputs = new HashSet<String>();
		
		FormulaParser parser = new FormulaParser(null);
		parser.parse(formula);
		
		findAllReferences(parser.FormulaTree, inputs);
		
		return inputs;
	}

	/**
	 * Recursively searches for all references in the tree and stores them in inputs.
	 * @param tree
	 * @param inputs
	 */
	public static void findAllReferences(CommonTree tree, Set<String> inputs) {
		if (tree.getChildren() != null) {
			List<CommonTree> children = new ArrayList<CommonTree>(tree.getChildren());
			for (CommonTree child: children) {
				findAllReferences(child, inputs);
			}
		} else {
			String reference = tree.toString();
			if (!Utilities.isConstant(reference)) {
				List<String> references;
				if (Utilities.isCellRangeReference(reference)) {
					references = StringUtilities.rangeToCells(reference);				
				} else {
					references = new ArrayList<String>();
					references.add(reference);
				}
				for (String cell : references) {
					String inputCell = ConstraintsFactory.WORKSHEET_PREFIX + cell;
					inputs.add(inputCell);
	
					// Debug
					/*if (!appXML.getInputs().contains(inputCell) && !appXML.getInterims().contains(inputCell) && !appXML.getOutputs().contains(inputCell)) {
						System.out.println("Cell not found: " + inputCell);
					}*/
				}
			}
		}
	}

	/**
	 * Determines if the two given fragments can be merged.
	 * @param f1
	 * @param f2
	 * @return
	 */
	protected boolean canMerge(Fragment f1, Fragment f2) {
		if (f1.getLinkedFragments().size() != f2.getLinkedFragments().size()) {
			return false;
		}
		
		// Sort linked fragments to allow simultaneous iteration over both sets
		CellAddressComparator cac = new CellAddressComparator();
		List<String> f1Linked = new ArrayList<String>(f1.getLinkedFragments());
		Collections.sort(f1Linked, cac);
		List<String> f2Linked = new ArrayList<String>(f2.getLinkedFragments());
		Collections.sort(f2Linked, cac);
		
		int r1r = StringUtilities.getCellRow(f1.getRepresentative());
		int r1c = StringUtilities.getCellColumn(f1.getRepresentative());
		int r2r = StringUtilities.getCellRow(f2.getRepresentative());
		int r2c = StringUtilities.getCellColumn(f2.getRepresentative());
		
		for (int i = 0; i < f1Linked.size(); i++) {
			String l1 = f1Linked.get(i);
			String l2 = f2Linked.get(i);
			
			if (StringUtilities.getCellRow(l1) - r1r != StringUtilities.getCellRow(l2) - r2r 
					|| StringUtilities.getCellColumn(l1) - r1c != StringUtilities.getCellColumn(l2) - r2c) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Builds a new merged fragment of the two given fragments
	 * @param f1
	 * @param f2
	 * @return
	 */
	public Fragment buildMergedFragment(Fragment f1, Fragment f2, boolean calcComplexity) {
		List<String> cells = new ArrayList<String>(f1.getInterims());
		cells.addAll(f1.getOutputs());
		cells.addAll(f2.getInterims());
		cells.addAll(f2.getOutputs());
		Fragment merged = buildFragment(cells, calcComplexity);
		
		// Readd linked fragments (have to be the same in f1 and f2)
		int orc = StringUtilities.getCellColumn(f1.getRepresentative());
		int orr = StringUtilities.getCellRow(f1.getRepresentative());
		
		int nrc = StringUtilities.getCellColumn(merged.getRepresentative());
		int nrr = StringUtilities.getCellRow(merged.getRepresentative());
		
		int columnDifference = nrc - orc;
		int rowDifference = nrr - orr;
		for (int i = 0; i < f1.getLinkedFragments().size(); i++) {
			String cell = f1.getLinkedFragments().get(i);
			int lc = StringUtilities.getCellColumn(cell);
			int lr = StringUtilities.getCellRow(cell);
			
			int nlc = lc + columnDifference;
			int nlr = lr + rowDifference;
			
			merged.getLinkedFragments().add(StringUtilities.getCellName(nlc, nlr));
		}
		
		return merged;
	}

}
