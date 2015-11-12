package org.exquisite.diagnosis.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exquisite.datamodel.ExquisiteSession;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Utilities;

import choco.kernel.model.constraints.Constraint;

/**
 * Ranks the diagnoses according to different strategies
 * @author dietmar
 *
 */
public class DiagnosisRanker {

	
	// Set some global switch for the moment
	public static int RANKING_STRATEGY = 1;
	// Set to 0 or lower for no ranking
	// 1: first cardinality, then complexity
	// 2: strictly by complexity
	
	
	// Ranks the diagnoses
	// Ranking criterion 1: tests.diagnosis length
	public static List<Diagnosis> rankDiagnoses(List<Diagnosis> diagnoses, ExquisiteSession session)  {
//		System.out.println("Todo: " + diagnoses.size());
		if (RANKING_STRATEGY > 0) {
			// Prepare the string representations of the constraints
			Map<Constraint, String> constraintFormulas = new HashMap<Constraint, String>();
			// Go through all the diagnoses
			for (Diagnosis d : diagnoses) {
				for (Constraint c : d.getElements()) {
					String cName = session.diagnosisModel.getConstraintName(c);
					String formula = session.appXML.getFormulas().get(cName);
					constraintFormulas.put(c, formula);
				}
			}
//			System.out.println("Here are the formulas: " + constraintFormulas.values());
			
			
			List<Diagnosis> rankedDiagnoses = new ArrayList<Diagnosis>();
			if (RANKING_STRATEGY == 1) {
//				System.out.println("Ranking by cardinality and then complexity");
				// Organize them by size
				List<Integer> sizes = new ArrayList<Integer>();
				Map<Integer, List<Diagnosis>> sizesMap = new HashMap<Integer, List<Diagnosis>>();
				// Determining sizes of the diagnoses
				for (Diagnosis d : diagnoses) {
					int s = d.getElements().size();
//					System.out.println("Found a diag of size: " + s);
					List<Diagnosis> existing = sizesMap.get(s);
					if (existing == null) {
//						System.out.println("no element list so far.");
						existing = new ArrayList<Diagnosis>();
						sizesMap.put(s, existing);
						if (!sizes.contains(s)) {
							sizes.add(s);
						}
					}
					existing.add(d);
				}
				
				
				// Sort the List and collect the results stepwise
//				System.out.println("We have " + sizes.size() + " different tests.diagnosis sizes: " + sizes);
//				System.out.println("sizes Map: " + sizesMap);
				Collections.sort(sizes);
				for (Integer s : sizes) {
					// do it one by one
					List<Diagnosis> diags = sizesMap.get(s);
//					System.out.println("Processing size " + s + ", " + diags.size() + " diags");
					
					List<Diagnosis> reRanked = reRankDiagnosesInternal(diags, constraintFormulas, session);
//					System.out.println("Reranked them leaving at " + reRanked.size() + " elements");
					rankedDiagnoses.addAll(reRanked);
				}
//				System.out.println("Returning fully ranked diags: " + rankedDiagnoses.size());
				return rankedDiagnoses;
				
			}
			else if (RANKING_STRATEGY == 2) {
//				System.out.println("Ranking by complexity only -- not implemented yet");
				List<Diagnosis> reRanked = reRankDiagnosesInternal(diagnoses, constraintFormulas, session);
				return reRanked;
				
			}
		}
		return diagnoses;
	}


	// Reranks a given list according to the combined complexity
	static List<Diagnosis> reRankDiagnosesInternal(List<Diagnosis> diags, Map<Constraint, String> constraintFormulas, ExquisiteSession session) {
		List<Diagnosis> result = new ArrayList<Diagnosis>();
		Map<Diagnosis, Double> scores = new HashMap<Diagnosis, Double>();
		for (Diagnosis d: diags) {
			double score = computeComplexity(d, constraintFormulas, session);
			scores.put (d, score);
//			System.out.println("Found a score: " + score);
		}
		// Now we should return the list ordered by decreasing complexity, highest ones first
		scores = Utilities.sortByValueDescending(scores);
		for (Diagnosis d : scores.keySet()) {
			result.add(d);
		}
		return result;
		
	}

	// Computes the complexity of a formula
	static double computeComplexity(Diagnosis d, Map<Constraint, String> constraintFormulas, ExquisiteSession session) {
		double result = 0.0;
		// Simply add the complexity of the diagnoses
		for (Constraint c : d.getElements()) {
			ConstraintComplexityEstimator estimator = new ConstraintComplexityEstimator(constraintFormulas.get(c));
			double r = estimator.estimateComplexity(); 
//			System.out.println("Complexity of " + session.diagnosisModel.getConstraintName(c) + " : " + r + " (" + constraintFormulas.get(c) + ")");
			result += r;
		}
		return result;
	} 
	
}
