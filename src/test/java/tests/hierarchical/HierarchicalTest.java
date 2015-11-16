package tests.hierarchical;

import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.engines.common.ConstraintComparator;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.models.Example;
import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Test driver for hierarchical diagnosis
 * @author dietmar
 *
 */
public class HierarchicalTest {

	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting hierarchy test");
		HierarchicalTest test = new HierarchicalTest();
		try{ 
		test.run();
		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Hierarchy test ended");
	}
	
	/**
	 * Worker method
	 * @throws Exception
	 */
	public void run() throws Exception {
		ExquisiteSession<Constraint> sessionData = new ExquisiteSession<>();
		sessionData.diagnosisModel = new DiagnosisModel<Constraint>();

		Hierarchy<Constraint> h = defineHierarchyAndProblem(sessionData.diagnosisModel);
//		h.printHierarchy();
		runHierarchicalDiagnosis(sessionData, h);
	}
	
	
	/**
	 * Runs the problem in a hierarchical manner
	 * @param model
	 * @param h
	 */
	public void runHierarchicalDiagnosis(ExquisiteSession<Constraint> sessionData, Hierarchy<Constraint> h) {
			
		HierarchicalHSDagBuilder dagBuilder = new HierarchicalHSDagBuilder(sessionData);
		dagBuilder.setHierarchy(h);
		
		
		// Set the set of diagnosable components.
		// Initially, the set of first level components (after root).
		List<ExpandableConstraint> diagnosisContext = new ArrayList<ExpandableConstraint>();
		for (HierarchyNode<Constraint> hnode : h.getRootNode().getNextLevelElements()) {
			diagnosisContext.add(new ExpandableConstraint(hnode));
		}
		System.out.println("Found a number of root components.." + diagnosisContext.size());
		
		dagBuilder.setHierarchicalContext(diagnosisContext);
		
//		if (true) {
//			System.exit(1);
//		}
		try{
			List<Diagnosis<Constraint>> diagnoses = dagBuilder.calculateDiagnoses();
			System.out.println("Found a number of diagnoses " + diagnoses.size());
			for (Diagnosis<Constraint> d : diagnoses) {
				List<Constraint> cts = (d.getElements());
				System.out.print("[");
				for (Constraint c : cts) {
					System.out.print( sessionData.diagnosisModel.getConstraintName(c) + " ");
				}
				System.out.println("]");
			}
		} catch (DomainSizeException e){
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * Defines some test hierarchy
	 * @return
	 */
	public Hierarchy<Constraint> defineHierarchyAndProblem(DiagnosisModel<Constraint> model) {
		
		
		// Some variables and constraints
		IntegerVariable v1 = Choco.makeIntVar("v1", 0,3);
		IntegerVariable v2 = Choco.makeIntVar("v2", 0,3);
		IntegerVariable v3 = Choco.makeIntVar("v3", 0,3);
		
		model.addIntegerVariable(v1);
		model.addIntegerVariable(v2);
		model.addIntegerVariable(v3);

		Constraint c1 = Choco.eq(v1, 1);
		Constraint c2 = Choco.eq(v1, 2);
		Constraint c3 = Choco.eq(v2, 1);
		Constraint c4 = Choco.eq(v2, 1);
		Constraint c5 = Choco.eq(v3, 1);
		Constraint c6 = Choco.eq(v3, 1);
		
		model.addPossiblyFaultyConstraint(c1, "c1");
		model.addPossiblyFaultyConstraint(c2, "c2");
		model.addPossiblyFaultyConstraint(c3, "c3");
		model.addPossiblyFaultyConstraint(c4, "c4");
		model.addPossiblyFaultyConstraint(c5, "c5");
		model.addPossiblyFaultyConstraint(c6, "c6");
		
		
		// Add some example
		List<Example<Constraint>> posExamples = new ArrayList<>();
		Example<Constraint> e1 = new Example<>();
		e1.addConstraint(Choco.eq(v1,2), "example1-constraint");
		posExamples.add(e1);
		model.setPositiveExamples(posExamples);
		
		
		// Set up the hierarchy
		Hierarchy<Constraint> h = new Hierarchy<>(model);
		HierarchyNode<Constraint> root = h.createNode();
		h.setRootNode(root);
		
		Set<Constraint> s1cts= new TreeSet<Constraint>(new ConstraintComparator());
		s1cts.add(c1);
		s1cts.add(c2);
	
		Set<Constraint> s2cts= new TreeSet<Constraint>(new ConstraintComparator());
		s2cts.add(c3);
		s2cts.add(c4);
		Set<Constraint> s3cts= new TreeSet<Constraint>(new ConstraintComparator());
		s3cts.add(c5);
		s3cts.add(c6);
		
		
		// Some sons.
		HierarchyNode<Constraint> s1 = h.createNode(s1cts);
		HierarchyNode<Constraint> s2 = h.createNode(s2cts);
		HierarchyNode<Constraint> s3 = h.createNode(s3cts);
		
		root.addSonNode(s1, "s1");
		root.addSonNode(s2, "s2");
		root.addSonNode(s3, "s3");
		
		return h;
	}
}
