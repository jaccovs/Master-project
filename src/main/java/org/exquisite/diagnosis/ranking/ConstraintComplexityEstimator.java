package org.exquisite.diagnosis.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.exquisite.parser.FormulaParser;

/**
 * A class that assess the complexity of a given excel formula
 * @author dietmar
 *
 */
public class ConstraintComplexityEstimator  {

	// The formula
	String formula;

	// The stats
	public int nbVarUses;
	public int nestingLevel;
	public int nbOps;
	public int complexOps;
	
	// Our operators - make them static lateron 
	String[] knownOperators = new String[]{
									"IF", "WENN", "*", "+", "-", "/", "QUOTIENT", 
									"MIN", "MAX", "<", ">", "=", ">=", "<=", "NOT", "NICHT", "ODER", "OR", "AND", "UND" };

	String[] complexOperators = new String[]{"IF", "WENN", "ODER", "OR", "AND", "UND" }; 
	
	List<String> knownOperatorsList;
	List<String> complexOperatorsList;
	
	
	// String rep
	public String toString() {
		return 	"Nb var uses	:" + nbVarUses + "\n" + 
				"Nb ops		:" + nbOps  + "\n" + 
				"Nb complx ops	:" + complexOps  + "\n" + 
				"nesting		:" + (nestingLevel-1);
	}
	
	/**
	 * Creates a new estimator
	 * @param formula
	 */
	public ConstraintComplexityEstimator(String formula) {
		this.formula = formula;
		knownOperatorsList = Arrays.asList(knownOperators);
		complexOperatorsList = Arrays.asList(complexOperators);
	}
	

	/*
	 * Calculates some measure based on the number of variables, operators and nesting level
	 */
	public double estimateComplexity() {
		if (formula == null) {
			System.err.println("Null formula to estimate complexity");
			return 0;
		}
		FormulaParser parser = new FormulaParser(null);
		parser.parse(formula);
//		System.out.println(parser.FormulaTree);
		traverseTree(parser.FormulaTree, 0);
		
		double result = nbVarUses + nbOps + (complexOps*2) + this.nestingLevel;
		
		return result;

	}
	
	
	/**
	 * Iterates through the tree recursively
	 * @param tree
	 */
	public void traverseTree(CommonTree tree, int level)  {
//		System.out.println("Called at level: " + level);
		// remember the nesting level:
		if (level > this.nestingLevel) {
			nestingLevel = level;
		}
		
		if (tree != null) {
			if (tree.getChildren() != null) {
//				System.out.println("At intermediate node: " + tree);
				int newLevel = level;
				if (knownOperatorsList.contains(tree.toString().trim())) {
					nbOps++;
					newLevel = newLevel + 1;
				}
				if (complexOperatorsList.contains(tree.toString().trim())) {
//					System.out.println("complex one? " + tree.toString());
					complexOps++;
					newLevel = newLevel + 1;
				}
				// otherwise it is some FUNC or ROOT
				
				List<CommonTree> children = new ArrayList<CommonTree>(tree.getChildren());
				for (CommonTree child : children) {
//					System.out.println("child: " + child);
					traverseTree(child, newLevel);
				}
			}
			else {
				// Let's see if it is a number. If not, it's a cell (nothing else supported yet).
				String val = tree.toString();
				if (!FormulaParser.isNumeric(val)) {
					nbVarUses++;
				}
				
			}
		}
	}
}