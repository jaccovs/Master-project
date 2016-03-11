package org.exquisite.core.query;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.query.scoring.MinScoreQSS;
import org.exquisite.core.query.scoring.QuerySelection;
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
 * traverses all possible partitions of diagnoses into two sets and computes a query for each partition if it exists
 */
public class SimpleQC<T> implements QueryComputation<T> {

    private static Logger logger = LoggerFactory.getLogger(SimpleQC.class);

    private final QuerySelection<T> querySelection;

    private BigDecimal threshold = BigDecimal.ZERO;
    private Iterator<Query<T>> queriesIterator = null;

    /**
     * Default constructor that sets query selection strategy to MinScoreQSS
     */
    public SimpleQC() {
        this(new MinScoreQSS<>());
    }

    public SimpleQC(QuerySelection<T> querySelection) {
        this.querySelection = querySelection;
    }

    @Override
    public void initialize(Set<Diagnosis<T>> hittingSets)
            throws DiagnosisException {
        Set<Query<T>> queries = new TreeSet<>((Comparator<Query<T>>) (o1, o2) -> {
            int res = o1.score.compareTo(o2.score);
            if (res == 0) res = compare(o1.formulas.size(), o2.formulas.size());
            if (res == 0) {
                Iterator<T> it1 = o1.formulas.iterator();
                Iterator<T> it2 = o2.formulas.iterator();
                while (it1.hasNext() && it2.hasNext()) {
                    res = compare(it1.next().hashCode(), it2.next().hashCode());
                    if (res != 0)
                        return res;
                }
            }
            return res;
        });
        Set<T> kb = hittingSets.stream().map(Diagnosis::getFormulas).flatMap(Collection::stream).collect
                (Collectors.toSet());
        computeQueries(kb, new ArrayList<>(hittingSets), queries);
        this.queriesIterator = queries.iterator();
    }

    private int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    @Override
    public Query<T> next() {
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

    public QuerySelection<T> getQuerySelection() {
        return querySelection;
    }

    protected long computeQueries(Set<T> kb, ArrayList<Diagnosis<T>> diagnoses,
                                  Set<Query<T>> queries) {

        return computeQueries(kb, diagnoses, queries, 1L);
    }

    private long computeQueries(Set<T> kb, ArrayList<Diagnosis<T>> diagnoses,
                                Set<Query<T>> queries, long start) {

        long n = diagnoses.size();
        long range = (long) Math.pow(2, n) - 1;
        if (start < 1 || start > range)
            throw new IllegalArgumentException("Incorrect start value!");

        for (; start < range; start = getNext(start)) {
            Set<Diagnosis<T>> dx = new LinkedHashSet<>();

            // convert value to the bit representation and generate the partition of diagnoses
            long value = start;
            int index = 0;
            while (value != 0L) {
                if (value % 2L != 0)
                    dx.add(diagnoses.get(index));

                ++index;
                value = value >>> 1;
            }

            Set<Diagnosis<T>> remainingDiagnoses = diagnoses.stream().filter(d -> !dx.contains(d)).collect
                    (Collectors.toSet());

            Query<T> query = createQuery(kb, dx, remainingDiagnoses);
            if (query != null && query.score.compareTo(getThreshold()) < 0) {
                queries.add(query);
                if (logger.isDebugEnabled())
                    logger.debug("Created query: \n dx:" + query.dx + "\n remainingDiagnoses:" + query.dnx + "\n dz:" + query.dz);
            }

        }
        return start;
    }

    private long getNext(long i) {
        return i + 1;
    }

    public Query<T> createQuery(Set<T> kb, Collection<Diagnosis<T>> dx, Collection<Diagnosis<T>> remainingDiagnoses) {

        if (dx.isEmpty() || remainingDiagnoses.isEmpty())
            throw new IllegalArgumentException("Input sets of diagnoses must not be empty!");

        Query<T> query = new Query<>();
        // TODO check if we can simply use query.dx.addAll(dx);
        query.dx.addAll(dx.stream().collect(Collectors.toList()));
        if (logger.isDebugEnabled())
            logger.debug("Creating a query with dx: " + dx);


        Set<T> ent = getCommonFormulas(kb, query.dx);
        if (ent.isEmpty()) return null;

        incrementCounter(COUNTER_INTERACTIVE_PARTITIONS);

        query.formulas = Collections.unmodifiableSet(ent);
        if (logger.isDebugEnabled())
            logger.debug("Common entailments: " + query.formulas);

        // query the rest of diagnoses
        for (Diagnosis<T> hs : remainingDiagnoses) {
            if (!query.dx.contains(hs)) {
                if (getEntailments(kb, hs).containsAll(ent))
                    query.dx.add(hs);
                else if (hs.getFormulas().stream().anyMatch(ent::contains))
                    query.dnx.add(hs);
                else
                    query.dz.add(hs);
            }
        }
        query.score = getQuerySelection().getScore(query);
        return query;
    }

    public Set<T> getCommonFormulas(Set<T> kb, Set<Diagnosis<T>> dx) {
        Set<T> intersection = null;
        for (Diagnosis<T> hs : dx) {
            if (intersection == null) {
                intersection = getEntailments(kb, hs);
            } else
                intersection.removeAll(hs.getFormulas());
            if (intersection.isEmpty())
                return intersection;
        }
        return intersection;
    }

    private Set<T> getEntailments(Set<T> kb, Diagnosis<T> hs) {
        Set<T> intersection = new HashSet<>(kb);
        intersection.removeAll(hs.getFormulas());
        return intersection;
    }
}
