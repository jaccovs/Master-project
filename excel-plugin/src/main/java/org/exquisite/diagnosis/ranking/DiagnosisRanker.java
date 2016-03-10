package org.exquisite.diagnosis.ranking;

import org.exquisite.datamodel.ExcelExquisiteSession;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.tools.Utilities;

import java.util.*;

/**
 * Ranks the diagnoses according to different strategies
 *
 * @author dietmar
 */
public class DiagnosisRanker {


    // Set some global switch for the moment
    public static int RANKING_STRATEGY = 1;
    // Set to 0 or lower for no ranking
    // 1: first cardinality, then complexity
    // 2: strictly by complexity


    // Ranks the diagnoses
    // Ranking criterion 1: tests.diagnosis length
    public static <T> List<Diagnosis<T>> rankDiagnoses(List<Diagnosis<T>> diagnoses, ExcelExquisiteSession<T> session) {
//		System.out.println("Todo: " + diagnoses.size());
        if (RANKING_STRATEGY > 0) {
            // Prepare the string representations of the constraints
            Map<T, String> constraintFormulas = new HashMap<>();
            // Go through all the diagnoses
            for (Diagnosis<T> d : diagnoses) {
                for (T c : d.getElements()) {
                    String cName = session.getDiagnosisModel().getConstraintName(c);
                    String formula = session.appXML.getFormulas().get(cName);
                    constraintFormulas.put(c, formula);
                }
            }
//			System.out.println("Here are the formulas: " + constraintFormulas.values());


            List<Diagnosis<T>> rankedDiagnoses = new ArrayList<>();
            if (RANKING_STRATEGY == 1) {
//				System.out.println("Ranking by cardinality and then complexity");
                // Organize them by size
                List<Integer> sizes = new ArrayList<Integer>();
                Map<Integer, List<Diagnosis<T>>> sizesMap = new HashMap<>();
                // Determining sizes of the diagnoses
                for (Diagnosis<T> d : diagnoses) {
                    int s = d.getElements().size();
//					System.out.println("Found a diag of size: " + s);
                    List<Diagnosis<T>> existing = sizesMap.get(s);
                    if (existing == null) {
//						System.out.println("no element list so far.");
                        existing = new ArrayList<>();
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
                    List<Diagnosis<T>> diags = sizesMap.get(s);
//					System.out.println("Processing size " + s + ", " + diags.size() + " diags");

                    List<Diagnosis<T>> reRanked = reRankDiagnosesInternal(diags, constraintFormulas);
//					System.out.println("Reranked them leaving at " + reRanked.size() + " elements");
                    rankedDiagnoses.addAll(reRanked);
                }
//				System.out.println("Returning fully ranked diags: " + rankedDiagnoses.size());
                return rankedDiagnoses;

            } else if (RANKING_STRATEGY == 2) {
//				System.out.println("Ranking by complexity only -- not implemented yet");
                return reRankDiagnosesInternal(diagnoses, constraintFormulas);

            }
        }
        return diagnoses;
    }


    // Reranks a given list according to the combined complexity
    static <T> List<Diagnosis<T>> reRankDiagnosesInternal(List<Diagnosis<T>> diags, Map<T, String> constraintFormulas) {
        List<Diagnosis<T>> result = new ArrayList<>();
        Map<Diagnosis<T>, Double> scores = new HashMap<>();
        for (Diagnosis<T> d : diags) {
            double score = computeComplexity(d, constraintFormulas);
            scores.put(d, score);
//			System.out.println("Found a score: " + score);
        }
        // Now we should return the list ordered by decreasing complexity, highest ones first
        scores = Utilities.sortByValueDescending(scores);
        for (Diagnosis<T> d : scores.keySet()) {
            result.add(d);
        }
        return result;

    }

    // Computes the complexity of a formula
    static <T> double computeComplexity(Diagnosis<T> d, Map<T, String> constraintFormulas) {
        double result = 0.0;
        // Simply add the complexity of the diagnoses
        for (T c : d.getElements()) {
            ConstraintComplexityEstimator estimator = new ConstraintComplexityEstimator(constraintFormulas.get(c));
            double r = estimator.estimateComplexity();
//			System.out.println("Complexity of " + session.getDiagnosisModel().getConstraintName(c) + " : " + r + " (" + constraintFormulas.get(c) + ")");
            result += r;
        }
        return result;
    }

}
