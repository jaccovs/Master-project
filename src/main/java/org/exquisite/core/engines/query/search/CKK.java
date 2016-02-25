package org.exquisite.core.engines.query.search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.IDiagnosisEngine;
import org.exquisite.core.ISolver;
import org.exquisite.core.engines.query.Query;
import org.exquisite.core.engines.query.QueryComputation;
import org.exquisite.core.engines.query.scoring.QuerySelection;
import org.exquisite.core.model.Diagnosis;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kostyas CKK class.
 *
 * @author Schmitz
 */
public class CKK<F> extends BruteForce<F> implements QueryComputation<F> {

    private static final Logger logger = Logger.getLogger(CKK.class.getSimpleName());

    BigDecimal bestdiff = new BigDecimal(Double.MAX_VALUE);
    private double threshold = 0.01d;
    private int count = 0;

    public CKK(IDiagnosisEngine<F> diagnosisEngine, QuerySelection<F> function) {
        super(diagnosisEngine, function);
    }

    protected void reset() {
        super.reset();
        count = 0;
        bestdiff = new BigDecimal(Double.MAX_VALUE);
    }

    public void nextQuery(ISolver<F> solver, Set<Diagnosis<F>> hittingSets, boolean useEntailments)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

        numOfHittingSets = hittingSets.size();
        reset();
        Set<Diagnosis<F>> hs = preprocess(hittingSets); // eliminates common entailments of all hs

        // find the best query
        Set<Diagnosis<F>> desc = new LinkedHashSet<Diagnosis<F>>(
                new TreeSet<Diagnosis<F>>(hs).descendingSet());

        Differencing dif = new Differencing(desc);

        findPartition(dif); // calculates query where diags in dx have common entailments
        Query query = null;
        sort(getQueries()); // sorts partitions ascending by difference

        query = nextPartition();

        if (logger.isLoggable(Level.INFO))
            logger.info("Searched through " + count + "/" + getPartitionsCount() + " partitionsCount");
        if (query == null || getQueries().isEmpty())
            logger.severe("No query found! " + getQueries().size() + " " + toString(hs));
        query = getScoring().runPostprocessor(getQueries(), query);

        restoreEntailments(hittingSets);
        return query;
    }

    private void sort(List<Query<F>> queries) {
        Collections.sort(queries, new Comparator<Query>() {
            public int compare(Query o1, Query o2) {
                int res = o1.difference.compareTo(o2.difference);
                if (res == 0) {
                    return -1 * Integer.valueOf(o1.dx.size()).compareTo(o2.dx.size());
                }
                return res;
            }
        });
        /*
         * Collections.sort(queries, new Comparator<Query<Id>>() { public int compare(Query<Id> o1, Query<Id> o2) { return
		 * o1.difference.compareTo(o2.difference); } });
		 */
    }

    @Override
    public Query nextPartition(Query query)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (query != null) {
            getQueries().remove(query);
            sort(getQueries());
        }
        return nextPartition();
    }

    private Query nextPartition()
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (getQueries().isEmpty())
            return null;

        Query query = null;
        BigDecimal best = BigDecimal.valueOf(Double.MAX_VALUE);
        bestdiff = new BigDecimal(Double.MAX_VALUE);
        count = 0;
        List<Query> empty = new LinkedList<Query>();
        for (Query part : getQueries()) {
            if ((part.difference.compareTo(bestdiff) < 0 || (query != null && query.dnx
                    .size() == 0) || (part.difference.equals(bestdiff) && compare(
                    query, part))) && verifyPartition(part)) {
                if (part.dnx.isEmpty())
                    empty.add(part);
                else {
                    BigDecimal score = getScoringFunction().getScore(part);
                    if ((score.compareTo(best) < 0) || (score.compareTo(best) == 0 && diff(part) < diff(query))) {
                        query = part;
                        best = score;
                        updateDifference(query);
                        if (query.difference.compareTo(bestdiff) < 0)
                            bestdiff = query.difference;
                    }
                }
                count++;
            }
            if (query != null && query.score.compareTo(BigDecimal.valueOf(getThreshold())) < 0)
                break;
        }
        getQueries().removeAll(empty);
        return query;
    }

    private void updateDifference(Query<F> query) {
        BigDecimal left = sumProbabilities(query.dx);
        BigDecimal right = sumProbabilities(query.dnx);
        BigDecimal none = sumProbabilities(query.dz);
        // TODO think about the following statement

        query.difference = left.subtract(right).add(none.divide(TWO)).abs();
    }

    private BigDecimal sumProbabilities(Set<Diagnosis<F>> partition) {
        BigDecimal sum = new BigDecimal(0);
        for (Diagnosis<F> ids : partition) {
            sum = sum.add(ids.getMeasure());
        }
        return sum;
    }

    private boolean compare(Query query, Query part) {
        if (part == null || query == null)
            return true;
        return part.dx.size() > query.dx.size();
    }

    private double diff(Query part) {
        if (part == null)
            return Double.MAX_VALUE;
        return Math.abs(part.dx.size() - part.dnx.size()) + part.dz.size() / 2;
    }

    public boolean verifyPartition(Query<F> query)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (query.isVerified)
            return true;
        Set<F> ent = query.query;
        // query the rest of diagnoses
        for (Diagnosis<F> hs : getHittingSets()) {
            if (!query.dx.contains(hs)) {
                if (hs.getEntailments().containsAll(ent)) {
                    query.dx.add(hs);
                    query.difference = query.difference.add(hs.getMeasure());
                } else if (!diagnosisConsistent(hs, ent))
                    query.dnx.add(hs);
                else if (diagnosisEntails(hs, ent)) {
                    query.dx.add(hs);
                    query.difference = query.difference.add(hs.getMeasure());
                } else {
                    query.dz.add(hs);
                    // query.difference = query.difference.add(new BigDecimal(hs.getConflictMeasure()/2d));
                }
            }
        }
        query.isVerified = true;
        return true;
    }

    protected void findPartition(Differencing diff) { // throws SolverException, InconsistentTheoryException {
        // diff == null
        if (diff.tail.isEmpty()) {
            if (diff.left.isEmpty())
                return;
            else {
                Query<F> part = new Query<>(getDiagnosisModel());
                for (Diagnosis<F> el : diff.left)
                    part.dx.add(el);
                Set<F> ent = getCommonFormulas(part.dx);
                if (ent == null || ent.isEmpty())
                    return;
                part.query = Collections.unmodifiableSet(ent);
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "Adding query with common entailments: " + part.query);
                part.difference = diff.difference;
                getQueries().add(part);
                incPartitionsCount();
                return;
            }
        }

        Diagnosis<F> hs = diff.getNext();

        Differencing less = diff.addLess(hs);
        findPartition(less);

        Differencing more = diff.addMore(hs);
        findPartition(more);
    }

    private class Differencing {
        private Set<Diagnosis<F>> left = new LinkedHashSet<Diagnosis<F>>();
        private Set<Diagnosis<F>> right = new LinkedHashSet<Diagnosis<F>>();
        private Set<Diagnosis<F>> tail = new LinkedHashSet<Diagnosis<F>>();
        private BigDecimal difference;
        private BigDecimal sumLeft = new BigDecimal(0);
        private BigDecimal sumRight = new BigDecimal(0);

        public Differencing(Set<Diagnosis<F>> desc) {
            tail.addAll(desc);
        }

        public Differencing(Differencing differencing) {
            right.addAll(differencing.right);
            left.addAll(differencing.left);
            tail.addAll(differencing.tail);
            sumLeft = differencing.sumLeft;
            sumRight = differencing.sumRight;
        }

        public Differencing addLess(Diagnosis<F> element) {
            Differencing res = new Differencing(this);
            res.tail.remove(element);

            if (res.sumLeft.compareTo(res.sumRight) < 1) {
                res.left.add(element);
                res.sumLeft = res.sumLeft.add(element.getMeasure());
            } else {
                res.right.add(element);
                res.sumRight = res.sumRight.add(element.getMeasure());
            }

            res.difference = res.sumLeft.subtract(res.sumRight).abs();
            return res;
        }

        public Differencing addMore(Diagnosis<F> element) {
            Differencing res = new Differencing(this);
            res.tail.remove(element);

            if (res.sumLeft.compareTo(res.sumRight) == 1) {
                res.left.add(element);
                res.sumLeft = res.sumLeft.add(element.getMeasure());
            } else {
                res.right.add(element);
                res.sumRight = res.sumRight.add(element.getMeasure());
            }

            res.difference = res.sumLeft.subtract(res.sumRight).abs();
            return res;
        }

        public boolean isEmpty() {
            return this.tail.isEmpty();
        }

        public Diagnosis<F> getNext() {
            Iterator<Diagnosis<F>> ti = this.tail.iterator();
            return ti.next();
        }
    }

}
