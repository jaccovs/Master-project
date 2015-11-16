package tests.hierarchical;

import choco.kernel.model.constraints.Constraint;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.HSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that calculates the HS-Dag based on hierarchical information
 * @author Dietmar
 * 
 *
 */
public class HierarchicalHSDagBuilder extends HSDagBuilder<Constraint> implements IDiagnosisEngine<Constraint> {
    // The hierarchy
    public Hierarchy<Constraint> hierarchy;
    /**
     * The current context of refinement
     */
    List<ExpandableConstraint> hierarchicalContext;

    /**
     * Initialize things
     */
	public HierarchicalHSDagBuilder(ExquisiteSession sessionData) {
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
                QuickXPlain<Constraint> qx = new QuickXPlain<>(this.sessionData, this);
                ConflictCheckingResult<Constraint> checkingResult = qx.checkExamples(model.getPositiveExamples(), new
                        ArrayList<>(), true);

                if (checkingResult != null) {
                    if (checkingResult.conflictFound()) {
                        List<Constraint> firstConflict = checkingResult.conflicts.get(0);

                        // Replace the detailed conflict with one that matches the
                        // current abstraction level.
						List<Constraint> abstractConstraints = this.hierarchy.mapDetailedConflictToAbstractionLevel(firstConflict, hierarchicalContext);
						System.out.println("Here's the mapped list: "+ abstractConstraints);
						// Now it's time to exchange the conflict with the abstract constraints.. 
						// A lot of things to do afterwards...

                        List<Constraint> set = new ArrayList<>();
                        set.addAll(firstConflict);
                        rootNode = new DAGNode<>(set);
                        rootNode.examplesToCheck = new ArrayList<>(checkingResult.failedExamples);
                        allConstructedNodes.add(rootNode);

                        List<DAGNode<Constraint>> nodes = new ArrayList<>();
                        nodes.add(rootNode);
                        conflictNodeLookup.put(firstConflict, nodes);
                        List<DAGNode<Constraint>> nodesToExpand = new ArrayList<>();
                        nodesToExpand.add(rootNode);
                        expandNodes(nodesToExpand);
					}
					else {
						Debug.msg("No conflict/s found.");
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
