package org.exquisite.core.query.querycomputation;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.IDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.querycomputation.heuristic.MinQ;
import org.exquisite.core.query.scoring.IQuerySelection;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.incrementCounter;

/**
 * Simple query computation methods that implements a brute force approach to query computation. This approach simply
 * traverses all possible partitions of diagnoses into two sets and computes a query for each partition if it exists.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class SimpleNaiveQueryComputation<F> implements IQueryComputation<F> {

    private static Logger logger = LoggerFactory.getLogger(SimpleNaiveQueryComputation.class);

    private final IQuerySelection<F> querySelection;

    private final IDiagnosisEngine<F> engine;

    private Iterator<Query<F>> queriesIterator = null;


    private Map<Diagnosis<F>, Set<F>> kbWithoutDiagEntailments = new HashMap<>();

    /**
     * Default constructor that sets query selection strategy to MinScoreQSS
     */
    public SimpleNaiveQueryComputation(IDiagnosisEngine engine) {
        this(engine, new MinScoreQSS<>());
    }

    public SimpleNaiveQueryComputation(IDiagnosisEngine<F> engine, IQuerySelection<F> querySelection) {
        this.querySelection = querySelection;
        this.engine = engine;
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses)
            throws DiagnosisException {
        Set<Query<F>> queries = new TreeSet<>((Comparator<Query<F>>) (o1, o2) -> {
            int res = o1.score.compareTo(o2.score);
            if (res == 0) res = compare(o1.formulas.size(), o2.formulas.size());
            if (res == 0) {
                Iterator<F> it1 = o1.formulas.iterator();
                Iterator<F> it2 = o2.formulas.iterator();
                while (it1.hasNext() && it2.hasNext()) {
                    res = compare(it1.next().hashCode(), it2.next().hashCode());
                    if (res != 0)
                        return res;
                }
            }
            return res;
        });
        Collection<F> kb = this.engine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas();
        calculateKBWithoutDiagEntailments(kb, diagnoses);
        computeQueries(kb, new ArrayList<>(diagnoses), queries);
        incrementCounter(COUNTER_QUERYCOMPUTATION_NAIVE_QUERYPOOL_SIZE, queries.size()); // zaehle wieviele Queries zurückgegeben werden // queries.size() Queries in Pool
        this.queriesIterator = queries.iterator();
    }

    private int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
    public Query<F> next() {
        Query query = queriesIterator.next();
        query.formulas = new HashSet<>(new MinQ<F>().minQ(
                new ArrayList<>(query.formulas.size()),
                new ArrayList<>(query.formulas.size()),
                new ArrayList<>(query.formulas),
                query.qPartition,
                this.engine));
        return query;
    }

    @Override
    public boolean hasNext() {
        return queriesIterator.hasNext();
    }

    @Override
    public void reset() {
        this.queriesIterator = null;
    }

    public IQuerySelection<F> getQuerySelection() {
        return querySelection;
    }

    protected long computeQueries(Collection<F> kb, ArrayList<Diagnosis<F>> diagnoses,
                                  Set<Query<F>> queries) {

        return computeQueries(kb, diagnoses, queries, 1L);
    }

    private long computeQueries(Collection<F> kb, ArrayList<Diagnosis<F>> diagnoses,
                                Set<Query<F>> queries, long start) {

        long n = diagnoses.size();
        long range = (long) Math.pow(2, n) - 1;
        if (start < 1 || start > range)
            throw new IllegalArgumentException("Incorrect start value!");

        //int cntInSet = 0, cntNotInSet = 0, cntQueriesNotNull = 0, cntQueries = 0;
        for (; start < range; start = getNext(start)) {
            Set<Diagnosis<F>> dx = new LinkedHashSet<>();

            // convert value to the bit representation and generate the partition of diagnoses
            long value = start;
            int index = 0;
            while (value != 0L) {
                if (value % 2L != 0)
                    dx.add(diagnoses.get(index));

                ++index;
                value = value >>> 1;
            }

            Set<Diagnosis<F>> remainingDiagnoses = diagnoses.stream().filter(d -> !dx.contains(d)).collect
                    (Collectors.toSet());

            Query<F> query = createQuery(dx, remainingDiagnoses);

            //if (query != null && query.score.compareTo(getThreshold()) < 0) {
            //cntQueries++;

            if (query != null) {
                //cntQueriesNotNull++;
                boolean isNotYetInSet = queries.add(query);

                /*
                if (isNotYetInSet)
                    cntNotInSet++;
                else
                    cntInSet++;
                */
                //if (logger.isDebugEnabled()) logger.debug("Created query: \n dx:" + query.qPartition.dx + "\n remainingDiagnoses:" + query.qPartition.dnx + "\n dz:" + query.qPartition.dz + "\n score: " + query.score);
            }

        }
        //if (logger.isDebugEnabled()) logger.debug("countNotInSet: " + cntNotInSet + ", countInSet: " + cntInSet + ", cntQueries: " + cntQueries + ", cntQueriesNotNull: " + cntQueriesNotNull);
        return start;
    }

    private long getNext(long i) {
        return i + 1;
    }

    public Query<F> createQuery(Collection<Diagnosis<F>> dx, Collection<Diagnosis<F>> remainingDiagnoses) {

        if (dx.isEmpty() || remainingDiagnoses.isEmpty())
            throw new IllegalArgumentException("Input sets of diagnoses must not be empty!");

        Query<F> query = new Query<>();
        query.qPartition.dx.addAll(dx.stream().collect(Collectors.toList()));

        Set<F> ent = new HashSet<>();

        Set<F> commonReasonerEntailments = getCommonReasonerEntailments(query.qPartition.dx);
        Collection<F> correctFormulas = engine.getSolver().getDiagnosisModel().getCorrectFormulas();
        Collection<F> entailedExamples = engine.getSolver().getDiagnosisModel().getEntailedExamples();

        ent.addAll(commonReasonerEntailments);
        ent.removeAll(correctFormulas); // remove background
        ent.removeAll(entailedExamples); // remove positive

        Collection<Diagnosis<F>> leadingDiagnoses = new HashSet<>(dx);
        leadingDiagnoses.addAll(remainingDiagnoses);

        Set<F> kbMinusUnionOfDiags = new HashSet<>(engine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());
        for (Diagnosis<F> diagnosis : leadingDiagnoses)
            kbMinusUnionOfDiags.removeAll(diagnosis.getFormulas());

        ent.removeAll(kbMinusUnionOfDiags);

        if (ent.isEmpty()) return null;

        query.formulas = Collections.unmodifiableSet(ent);

        // query the rest of diagnoses
        for (Diagnosis<F> hs : remainingDiagnoses) {
            if (!query.qPartition.dx.contains(hs)) {
                if (this.kbWithoutDiagEntailments.get(hs).containsAll(query.formulas))
                    query.qPartition.dx.add(hs);
                else if (!isUnionConsistent(hs,query.formulas))
                    query.qPartition.dnx.add(hs);
                else
                    query.qPartition.dz.add(hs);
            }
        }
        query.score = querySelection.getScore(query);
        query.qPartition.computeProbabilities();
        return query;
    }

    private boolean isUnionConsistent(Diagnosis<F> hs, Set<F> query) {

        Set<F> set = new HashSet<>();
        set.addAll(this.engine.getSolver().getDiagnosisModel().getPossiblyFaultyFormulas());
        set.removeAll(hs.getFormulas());
        set.addAll(this.engine.getSolver().getDiagnosisModel().getCorrectFormulas());
        set.addAll(this.engine.getSolver().getDiagnosisModel().getEntailedExamples());
        set.addAll(query);

        boolean result =  this.engine.getSolver().isConsistent(set);
        return result;
    }

    private void calculateKBWithoutDiagEntailments(Collection<F> kb, Set<Diagnosis<F>> diagnoses) {
        for (Diagnosis<F> diagnosis:diagnoses)
            this.kbWithoutDiagEntailments.put(diagnosis, getReasonerEntailments(kb, diagnosis));
    }

    private Set<F> getReasonerEntailments(Collection<F> kb, Diagnosis<F> hs) {
        Set<F> kbWithoutDiag = new HashSet<>(kb);
        kbWithoutDiag.removeAll(hs.getFormulas()); // K/D
        kbWithoutDiag.addAll(engine.getSolver().getDiagnosisModel().getCorrectFormulas()); // add background
        kbWithoutDiag.addAll(engine.getSolver().getDiagnosisModel().getEntailedExamples()); // add entailed examples
        Set<F> entailments = this.engine.getSolver().calculateEntailments(kbWithoutDiag);
        return entailments;
    }

    private Set<F> getCommonReasonerEntailments(Set<Diagnosis<F>> dx) {
        Set<F> commonEntailments = null;
        for (Diagnosis<F> hs : dx) {
            if (commonEntailments == null) {
                commonEntailments = new HashSet<>(this.kbWithoutDiagEntailments.get(hs));
            } else {
                commonEntailments.retainAll(new HashSet<>(this.kbWithoutDiagEntailments.get(hs)));
            }
            if (commonEntailments.isEmpty())
                return commonEntailments;
        }
        return commonEntailments;
    }

    @Override
    public String toString() {
        return "SimpleNaiveQueryComputation{" +
                "diagnosisEngine=" + engine +
                ", querySelection=" + querySelection +
                '}';
    }
}
