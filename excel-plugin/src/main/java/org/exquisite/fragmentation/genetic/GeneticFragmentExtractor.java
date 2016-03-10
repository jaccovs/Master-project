package org.exquisite.fragmentation.genetic;

import org.exquisite.datamodel.ExquisiteAppXML;
import org.exquisite.datamodel.Fragment;
import org.exquisite.fragmentation.OneCellFragmentExtractor;
import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses an evolutionary algorithm to optimize the fragmentation
 *
 * @author Schmitz
 */
public class GeneticFragmentExtractor extends OneCellFragmentExtractor {

    public static final int POPULATION_SIZE = 50;
    public static final int EVOLUTION_COUNT = 50;

    // List of all one-cell fragments
    List<Fragment> baseFragments;

    // Stores for each base fragments the positions of the other fragments, it can be merged with
    List<List<Integer>> possibleMergings;


    public GeneticFragmentExtractor(ExquisiteAppXML appXML) {
        super(appXML);
    }

    @Override
    public List<Fragment> calculateFragmentation() {
        baseFragments = super.calculateFragmentation();

        determinePossibleMergings(baseFragments);

        List<Fragment> optimizedFragmentation = optimizeFragmentation();

        return optimizedFragmentation;
    }

    /**
     * Uses an evolutionary algorithm to optimize the fragmentation, that is stored in baseFragments
     *
     * @return
     */
    private List<Fragment> optimizeFragmentation() {
        List<Fragment> fragmentation = null;

        try {
            Configuration.reset();
            Configuration configuration = new DefaultConfiguration();

            FragmentationFitnessFunction fitnessFunction = new FragmentationFitnessFunction(baseFragments,
                    possibleMergings, this);

            configuration.setFitnessFunction(fitnessFunction);

            // each gene sampleGenes[i] tells for the fragment baseFragments[i], to which fragment of possibleMergings[i] it is merged
            // -1: Fragment will not be merged (except another fragment is being merged with this fragment)
            // k: baseFragments[i] will be merged with baseFragments[m] with m = possibleMergings[k]
            Gene[] sampleGenes = new Gene[baseFragments.size()];
            for (int i = 0; i < baseFragments.size(); i++) {
                sampleGenes[i] = new IntegerGene(configuration, -1, possibleMergings.get(i).size() - 1);
            }

            Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);

            configuration.setSampleChromosome(sampleChromosome);

            configuration.setPopulationSize(POPULATION_SIZE);

            Genotype population = Genotype.randomInitialGenotype(configuration);

            for (int i = 0; i < EVOLUTION_COUNT; i++) {
                population.evolve();
            }

            fragmentation = fitnessFunction.buildFragmentation(population.getFittestChromosome());

        } catch (InvalidConfigurationException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return fragmentation;
    }

    /**
     * Fills the possibleMergings structure
     *
     * @param fragments
     */
    private void determinePossibleMergings(List<Fragment> fragments) {
        possibleMergings = new ArrayList<List<Integer>>(fragments.size());
        for (int i = 0; i < fragments.size(); i++) {
            List<Integer> mergings = new ArrayList<Integer>();
            Fragment fragI = fragments.get(i);
            for (int k = 0; k < fragments.size(); k++) {
                if (k != i) {
                    if (canMerge(fragI, fragments.get(k))) {
                        mergings.add(k);
                    }
                }
            }

            possibleMergings.add(mergings);
        }

    }

}
