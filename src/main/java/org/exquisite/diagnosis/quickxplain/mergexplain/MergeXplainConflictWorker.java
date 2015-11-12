package org.exquisite.diagnosis.quickxplain.mergexplain;

import java.util.ArrayList;
import java.util.List;

import org.exquisite.diagnosis.quickxplain.DomainSizeException;

import choco.kernel.model.constraints.Constraint;

public class MergeXplainConflictWorker extends MergeXplainWorker {
	
	public static final int CONFLICT_PRIORITY = 0;
	
	private MergeXplainTreeWorker parent, tw1, tw2;
	private MergeXplainResult result;
	
	private List<Constraint> correctStatements;

	public MergeXplainConflictWorker(ParallelMergeXplain mergeXplain, MergeXplainTreeWorker parent, MergeXplainTreeWorker tw1, MergeXplainTreeWorker tw2, int depth) {
		super(mergeXplain, CONFLICT_PRIORITY + depth, depth);
		this.parent = parent;
		this.tw1 = tw1;
		this.tw2 = tw2;
		this.correctStatements = mergeXplain.currentDiagnosisModel.getCorrectStatements();
	}
	
	@Override
	public void run() {
		if (!tw1.isFinished() || !tw2.isFinished()) {
//			System.out.println("Conflict aborted at level " + depth + ", tree1 f: " + tw1.isFinished() + ", tree2 f: " + tw2.isFinished() + ", Q: "+ mergeXplain.queue.size());
			mergeXplain.execute(this);
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			return;
		}
		
		try {
//			System.out.println("Conflict worker started at level " + depth);
		
			//Do the work
			MergeXplainResult r1 = tw1.getResult();
			MergeXplainResult r2 = tw2.getResult();
			result = parent.getResult();
			
			// Add found conflicts
			result.Conflicts.addAll(r1.Conflicts);
			result.Conflicts.addAll(r2.Conflicts);
			
	//		splittingConflicts = result.Conflicts.size();
			
	//		System.out.println("Found " + result.Conflicts.size() + " conflict(s) with splitting technique.");
			
			// Search for conflicts in remaining constraints
			List<Constraint> remainingConstraints = new ArrayList<Constraint>(r1.ConflictFreeStatements);
			remainingConstraints.addAll(r2.ConflictFreeStatements);
			List<Constraint> allTestStatements = new ArrayList<Constraint>(remainingConstraints);
			allTestStatements.addAll(correctStatements);
			List<Constraint> kb1 = new ArrayList<Constraint>(r1.ConflictFreeStatements);
			List<Constraint> kb2 = new ArrayList<Constraint>(r2.ConflictFreeStatements);
			List<Constraint> conflictToRemove = new ArrayList<Constraint>();
			while (!mergeXplain.isConsistent(allTestStatements)) {
				List<Constraint> conflict = findSingleConflict(correctStatements, kb2, kb2, kb1);
				conflict.addAll(findSingleConflict(correctStatements, conflict, conflict, kb2));
				//kb1.removeAll(conflict);
				
				result.Conflicts.add(conflict);
				
				switch (MergeXplain.ConflictSearchMode) {
				case Least:
					kb1.removeAll(conflict);
					kb2.removeAll(conflict);
					remainingConstraints.removeAll(conflict);
					allTestStatements.removeAll(conflict);
					break;
				case Some:
					kb1.removeAll(conflict);
					conflictToRemove.clear();
					conflictToRemove.addAll(conflict);
					conflictToRemove.removeAll(kb2);
					
					remainingConstraints.removeAll(conflictToRemove);
					allTestStatements.removeAll(conflictToRemove);
					break;
				case EnoughFor1Diag:
					List<Constraint> copy = new ArrayList<Constraint>(conflict);
					copy.retainAll(kb1);
					kb1.remove(copy.get(0));
					remainingConstraints.remove(copy.get(0));
					allTestStatements.remove(copy.get(0));
					break;
				}
			}
			result.ConflictFreeStatements.addAll(remainingConstraints);
			
		}
		catch (DomainSizeException e) {
			
		} finally {
//			System.out.println("Conflict finished at level " + depth + ", Q: " + mergeXplain.queue.size());
			parent.setFinished(true);
			if (depth == 0) {
				mergeXplain.threadPool.shutdown();
			}
		}
	}
	
	private List<Constraint> findSingleConflict(
			List<Constraint> correctStatements, 
			List<Constraint> kb, 
			List<Constraint> delta, 
			List<Constraint> c) throws DomainSizeException {
		
		List<Constraint> result = new ArrayList<Constraint>();
		
		List<Constraint> kbAndCorrectStatements = new ArrayList<Constraint>(kb);
		kbAndCorrectStatements.addAll(correctStatements);
		if (delta.size() != 0 && !mergeXplain.isConsistent(kbAndCorrectStatements)) {
			return result;
		}
		
		if (c.size() == 1) {
			result.addAll(c);
			return result;
		}
		
		// Split
		int split = mergeXplain.split(c);
		List<Constraint> s1 = new ArrayList<Constraint>(c.subList(0, split));
		List<Constraint> s2 = new ArrayList<Constraint>(c.subList(split, c.size()));
		
		List<Constraint> kbs1 = new ArrayList<Constraint>(kb);
		kbs1.addAll(s1);
		result.addAll(findSingleConflict(correctStatements, kbs1, s1, s2));
		
		List<Constraint> kbresult = new ArrayList<Constraint>(kb);
		kbresult.addAll(result);
		result.addAll(findSingleConflict(correctStatements, kbresult, result, s1));
		
//		if (listComparator.compare(kb, delta) == 0) {
//			result.addAll(findSingleConflict(correctStatements, result, result, kb));
//		}
		
		return result;
		
	}

}
