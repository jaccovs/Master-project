package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.exquisite.core.query.scoring.IQuerySelection;
import org.exquisite.core.DiagnosisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.exquisite.core.perfmeasures.PerfMeasurementManager.COUNTER_INTERACTIVE_PARTITIONS;
import static org.exquisite.core.perfmeasures.PerfMeasurementManager.incrementCounter;

/**
 * Simple query computation methods that implements a brute force approach to query computation. This approach simply
 * traverses all possible partitions of diagnoses into two sets and computes a query for each partition if it exists.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author kostya
 */
public class SimpleQC<F> implements IQueryComputation<F> {

    private static Logger logger = LoggerFactory.getLogger(SimpleQC.class);

    private final IQuerySelection<F> querySelection;

    private BigDecimal threshold = BigDecimal.ZERO;
    private Iterator<Query<F>> queriesIterator = null;

    /**
     * Default constructor that sets query selection strategy to MinScoreQSS
     */
    public SimpleQC() {
        this(new MinScoreQSS<>());
    }

    public SimpleQC(IQuerySelection<F> querySelection) {
        this.querySelection = querySelection;
    }

    @Override
    public void initialize(Set<Diagnosis<F>> diagnoses)
            throws DiagnosisException {
        Set<Query<F>> queries = new TreeSet<>((Comparator<Query<F>>) (o1, o2) -> {
            int res = o1.qPartition.score.compareTo(o2.qPartition.score);
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
        Set<F> kb = diagnoses.stream().map(Diagnosis::getFormulas).flatMap(Collection::stream).collect
                (Collectors.toSet());
        computeQueries(kb, new ArrayList<>(diagnoses), queries);
        this.queriesIterator = queries.iterator();
    }

    private int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
    public Query<F> next() {
        return queriesIterator.next();
    }

    @Override
    public boolean hasNext() {
        return queriesIterator.hasNext();
    }

    @Override
    public void reset() {
        this.queriesIterator = null;
    }

    public BigDecimal getThreshold() {
        return this.threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = BigDecimal.valueOf(threshold);
    }

    public IQuerySelection<F> getQuerySelection() {
        return querySelection;
    }

    protected long computeQueries(Set<F> kb, ArrayList<Diagnosis<F>> diagnoses,
                                  Set<Query<F>> queries) {

        return computeQueries(kb, diagnoses, queries, 1L);
    }

    private long computeQueries(Set<F> kb, ArrayList<Diagnosis<F>> diagnoses,
                                Set<Query<F>> queries, long start) {

        long n = diagnoses.size();
        long range = (long) Math.pow(2, n) - 1;
        if (start < 1 || start > range)
            throw new IllegalArgumentException("Incorrect start value!");

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

            Query<F> query = createQuery(kb, dx, remainingDiagnoses);
            if (query != null && query.qPartition.score.compareTo(getThreshold()) < 0) {
                queries.add(query);
                if (logger.isDebugEnabled())
                    logger.debug("Created query: \n dx:" + query.qPartition.dx + "\n remainingDiagnoses:" + query.qPartition.dnx + "\n dz:" + query.qPartition.dz);
            }

        }
        return start;
    }

    private long getNext(long i) {
        return i + 1;
    }

    public Query<F> createQuery(Set<F> kb, Collection<Diagnosis<F>> dx, Collection<Diagnosis<F>> remainingDiagnoses) {

        if (dx.isEmpty() || remainingDiagnoses.isEmpty())
            throw new IllegalArgumentException("Input sets of diagnoses must not be empty!");

        Query<F> query = new Query<>();
        // TODO check if we can simply use query.dx.addAll(dx);
        query.qPartition.dx.addAll(dx.stream().collect(Collectors.toList()));
        if (logger.isDebugEnabled())
            logger.debug("Creating a query with dx: " + dx);


        Set<F> ent = getCommonFormulas(kb, query.qPartition.dx);
        if (ent.isEmpty()) return null;

        incrementCounter(COUNTER_INTERACTIVE_PARTITIONS);

        query.formulas = Collections.unmodifiableSet(ent);
        if (logger.isDebugEnabled())
            logger.debug("Common entailments: " + query.formulas);

        // query the rest of diagnoses
        for (Diagnosis<F> hs : remainingDiagnoses) {
            if (!query.qPartition.dx.contains(hs)) {
                if (getEntailments(kb, hs).containsAll(ent))
                    query.qPartition.dx.add(hs);
                else if (hs.getFormulas().stream().anyMatch(ent::contains))
                    query.qPartition.dnx.add(hs);
                else
                    query.qPartition.dz.add(hs);
            }
        }
        query.qPartition.score = getQuerySelection().getScore(query);
        return query;
    }

    public Set<F> getCommonFormulas(Set<F> kb, Set<Diagnosis<F>> dx) {
        Set<F> intersection = null;
        for (Diagnosis<F> hs : dx) {
            if (intersection == null) {
                intersection = getEntailments(kb, hs);
            } else
                intersection.removeAll(hs.getFormulas());
            if (intersection.isEmpty())
                return intersection;
        }
        return intersection;
    }

    private Set<F> getEntailments(Set<F> kb, Diagnosis<F> hs) {
        Set<F> intersection = new HashSet<>(kb);
        intersection.removeAll(hs.getFormulas());
        return intersection;
    }
}
