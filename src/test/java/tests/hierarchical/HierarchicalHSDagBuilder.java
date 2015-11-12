package tests.hierarchical;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.engines.HSDagBuilder;
import org.exquisite.diagnosis.models.ConflictCheckingResult;
import org.exquisite.diagnosis.models.DAGNode;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.exquisite.tools.Debug;

import choco.kernel.model.constraints.Constraint;

/**
 * The class that calculates the HS-Dag based on hierarchical information
 * @author Dietmar
 * 
 *
 */
public class HierarchicalHSDagBuilder extends HSDagBuilder implements IDiagnosisEngine
{	
	/**
	 * Initialize things
	 */
	public HierarchicalHSDagBuilder(ExquisiteSession sessionData) {
		super(sessionData);
	}
	
	// The hierarchy
	public Hierarchy hierarchy;
	
	/**
	 * A setter for it.
	 * @param h
	 */
	public void setHierarchy(Hierarchy h) {
		this.hierarchy = h;
		this.hierarchy.fixLevelNumbering();
	}
	
	
	/**
	 * The current context of refinement
	 */
	List<ExpandableConstraint> hierarchicalContext;

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
	public List<Diagnosis> calculateDiagnoses() throws DomainSizeException {
		diagnoses = new ArrayList<Diagnosis>();
		if (rootNode == null) {
			try{
				Debug.msg("Empty root node - doing inital test (hierarchical)");
				QuickXPlain qx = new QuickXPlain(this.sessionData, this);
				ConflictCheckingResult checkingResult = qx.checkExamples(model.getPositiveExamples(), new ArrayList<Constraint>(), true);
							
				if (checkingResult != null)
				{
					if (checkingResult.conflictFound()) {					
						List<Constraint> firstConflict = (checkingResult.conflicts.get(0));
		
						// Replace the detailed conflict with one that matches the 
						// current abstraction level.
						List<Constraint> abstractConstraints = this.hierarchy.mapDetailedConflictToAbstractionLevel(firstConflict, hierarchicalContext);
						System.out.println("Here's the mapped list: "+ abstractConstraints);
						// Now it's time to exchange the conflict with the abstract constraints.. 
						// A lot of things to do afterwards...
						
						List<Constraint> set = new ArrayList<Constraint>();
						set.addAll(firstConflict);
						rootNode = new DAGNode(set);
						rootNode.examplesToCheck = new ArrayList<Example>(checkingResult.failedExamples);				
						allConstructedNodes.add(rootNode);
						
						List<DAGNode> nodes = new ArrayList<DAGNode>();
						nodes.add(rootNode);
						conflictNodeLookup.put((List<Constraint>)firstConflict, nodes);
						List<DAGNode> nodesToExpand = new ArrayList<DAGNode>();
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
