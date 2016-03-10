package org.exquisite.fragmentation.genetic;

import org.exquisite.datamodel.Fragment;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to determine the fitness (inversed complexity) of a fragmentation given as a chromosome.
 *
 * @author Schmitz
 */
public class FragmentationFitnessFunction extends FitnessFunction {

    public static final double MAX_COMPLEXITY = 10000d;
    /**
     *
     */
    private static final long serialVersionUID = -3980215090739303757L;
    // List of all one-cell fragments
    List<Fragment> baseFragments;

    // Stores for each base fragments the positions of the other fragments, it can be merged with
    List<List<Integer>> possibleMergings;

    GeneticFragmentExtractor fragmentExtractor;

    public FragmentationFitnessFunction(List<Fragment> baseFragments, List<List<Integer>> possibleMergings,
                                        GeneticFragmentExtractor fragmentExtractor) {
        this.baseFragments = baseFragments;
        this.possibleMergings = possibleMergings;
        this.fragmentExtractor = fragmentExtractor;
    }

    @Override
    protected double evaluate(IChromosome a_subject) {
        List<Fragment> fragmentation = buildFragmentation(a_subject);

        double complexity = 0d;

        for (int i = 0; i < fragmentation.size(); i++) {
            Fragment fragment = fragmentation.get(i);
            complexity += fragment.getComplexity();
        }

        return MAX_COMPLEXITY - complexity;
    }

    /**
     * Builds an actual fragmentation of a chromosome
     *
     * @param chromosome A chromosome stores an int for each base fragment, telling with which other fragment it should be merged
     * @return
     */
    public List<Fragment> buildFragmentation(IChromosome chromosome) {

        // Created for each test evaluation
        List<Fragment> mergedFragments = new ArrayList<Fragment>(baseFragments);

        for (int i = 0; i < baseFragments.size(); i++) {
            Fragment oldFragment = mergedFragments.get(i);
            int merge = (int) chromosome.getGene(i).getAllele();
            if (merge >= 0 && oldFragment != mergedFragments.get(merge)) {
                Fragment mergeFragment = mergedFragments.get(merge);
                Fragment newFragment = fragmentExtractor.buildMergedFragment(oldFragment, mergeFragment, false);

                // Replace all old fragments with the newly merged fragment
                for (int k = 0; k < mergedFragments.size(); k++) {
                    Fragment comparison = mergedFragments.get(k);
                    if (comparison == oldFragment || comparison == mergeFragment) {
                        mergedFragments.set(k, newFragment);
                    }
                }
            }
        }

        // find all different fragments
        List<Fragment> fragmentation = new ArrayList<Fragment>();
        for (int i = 0; i < mergedFragments.size(); i++) {
            Fragment fragment = mergedFragments.get(i);
            if (!fragmentation.contains(fragment)) {
                fragmentExtractor.calculateComplexity(fragment);
                fragmentation.add(fragment);
            }
        }

        return fragmentation;
    }
}
