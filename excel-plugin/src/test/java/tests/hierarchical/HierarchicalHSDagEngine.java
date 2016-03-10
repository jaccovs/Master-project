package tests.hierarchical;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.core.engines.tree.Node;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.ConstraintsQuickXPlain;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.tools.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that calculates the HS-Dag based on hierarchical information
 * @author Dietmar
 * 
 *
 */
public class HierarchicalHSDagEngine extends HSDagEngine<Constraint> implements IDiagnosisEngine<Constraint> {
    // The hierarchy
    public Hierarchy<Constraint> hierarchy;
    /**
     * The current context of refinement
     */
    List<ExpandableConstraint> hierarchicalContext;

    /**
     * Initialize things
     */
	public HierarchicalHSDagEngine(ExcelExquisiteSession sessionData) {
		super(sessionData);
	}
	
	/**
	 * A setter for it.
	 * @param h
	 */
    public void setHierarchy(Hierarchy<Constraint> h) {
        this.hierarchy = h;
        this.hierarchy.fixLevelNumbering();
	}

	/**
	 * Sets the diagnosable context
	 * @param hierarchicalContext
	 */
	public void setHierarchicalContext(List<ExpandableConstraint> hierarchicalContext) {
		this.hierarchicalContext = hierarchicalContext;
	}
	
	/**
	 * The main method that calculate the diagnoses
	 * @return a set of diagnosis or an empty set, of there is no problem
	 * @throws DomainSizeException 
	 */
	@Override
    public List<Diagnosis<Constraint>> calculateDiagnoses() throws DomainSizeException {
        diagnoses = new ArrayList<Diagnosis<Constraint>>();
        if (rootNode == null) {
            try{
				Debug.msg("Empty root node - doing inital test (hierarchical)");
                ConstraintsQuickXPlain<Constraint> qx = new ConstraintsQuickXPlain<>(this.sessionData);
                ConflictCheckingResult<Constraint> checkingResult = qx.checkExamples(model.getPositiveExamples(), new
                        ArrayList<>(), true);

                if (checkingResult != null) {
                    if (checkingResult.conflictFound()) {
                        List<Constraint> firstConflict = checkingResult.conflicts.get(0);

                        // Replace the detailed nodeLabel with one that matches the
                        // current abstraction level.
						List<Constraint> abstractConstraints = this.hierarchy.mapDetailedConflictToAbstractionLevel(firstConflict, hierarchicalContext);
						System.out.println("Here's the mapped list: "+ abstractConstraints);
						// Now it's time to exchange the nodeLabel with the abstract constraints..
						// A lot of things to do afterwards...

                        List<Constraint> set = new ArrayList<>();
                        set.addAll(firstConflict);
                        rootNode = new Node<>(set);
                        rootNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                        allConstructedNodes.add(rootNode);

                        List<Node<Constraint>> nodes = new ArrayList<>();
                        nodes.add(rootNode);
                        conflictNodeLookup.put(firstConflict, nodes);
                        List<Node<Constraint>> nodesToExpand = new ArrayList<>();
                        nodesToExpand.add(rootNode);
                        expandNodes(nodesToExpand);
					}
					else {
						Debug.msg("No nodeLabel/s found.");
					}
				}
				else {
					Debug.msg("Checking result returned null, Thread must have been interrupted.");
				}	
			} catch (DomainSizeException e){
				throw e;
			}
		}
		//Debug.msg("solver solution: ");
		//Debug.msg(Utilities.printSolution(qxplain.solver));
		return diagnoses;
	}
	

	
			
}
