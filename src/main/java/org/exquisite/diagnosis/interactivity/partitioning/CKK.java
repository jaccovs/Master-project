package org.exquisite.diagnosis.interactivity.partitioning;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kostyas CKK class.
 *
 * @author Schmitz
 */
public class CKK<Formula> extends BruteForce<Formula> implements Partitioning<Formula> {

    private static final Logger logger = Logger.getLogger(CKK.class.getSimpleName());
    BigDecimal bestdiff = new BigDecimal(Double.MAX_VALUE);
    private double threshold = 0.01d;
    private int count = 0;

    public CKK(IDiagnosisEngine<Formula> diagnosisEngine, Scoring<Formula> function) {
        super(diagnosisEngine, function);
    }

    protected void reset() {
        super.reset();
        count = 0;
        bestdiff = new BigDecimal(Double.MAX_VALUE);
    }

    public Partition<Formula> generatePartition(Set<Diagnosis<Formula>> hittingSets)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

        numOfHittingSets = hittingSets.size();
        reset();
        Set<Diagnosis<Formula>> hs = preprocess(hittingSets); // eliminates common entailments of all hs

        // find the best partition
        Set<Diagnosis<Formula>> desc = new LinkedHashSet<Diagnosis<Formula>>(
                new TreeSet<Diagnosis<Formula>>(hs).descendingSet());

        Differencing dif = new Differencing(desc);

        findPartition(dif); // calculates partition where diags in dx have common entailments
        Partition partition = null;
        sort(getPartitions()); // sorts partitions ascending by difference

        partition = nextPartition();

        if (logger.isLoggable(Level.INFO))
            logger.info("Searched through " + count + "/" + getPartitionsCount() + " partitionsCount");
        if (partition == null || getPartitions().isEmpty())
            logger.severe("No partition found! " + getPartitions().size() + " " + toString(hs));
        partition = getScoring().runPostprocessor(getPartitions(), partition);

        restoreEntailments(hittingSets);
        return partition;
    }

    private void sort(List<Partition<Formula>> partitions) {
        Collections.sort(partitions, new Comparator<Partition>() {
            public int compare(Partition o1, Partition o2) {
                int res = o1.difference.compareTo(o2.difference);
                if (res == 0) {
                    return -1 * Integer.valueOf(o1.dx.size()).compareTo(o2.dx.size());
                }
                return res;
            }
        });
        /*
         * Collections.sort(partitions, new Comparator<Partition<Id>>() { public int compare(Partition<Id> o1, Partition<Id> o2) { return
		 * o1.difference.compareTo(o2.difference); } });
		 */
    }

    @Override
    public Partition nextPartition(Partition partition)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (partition != null) {
            getPartitions().remove(partition);
            sort(getPartitions());
        }
        return nextPartition();
    }

    private Partition nextPartition()
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (getPartitions().isEmpty())
            return null;

        Partition partition = null;
        BigDecimal best = BigDecimal.valueOf(Double.MAX_VALUE);
        bestdiff = new BigDecimal(Double.MAX_VALUE);
        count = 0;
        List<Partition> empty = new LinkedList<Partition>();
        for (Partition part : getPartitions()) {
            if ((part.difference.compareTo(bestdiff) < 0 || (partition != null && partition.dnx
                    .size() == 0) || (part.difference.equals(bestdiff) && compare(
                    partition, part))) && verifyPartition(part)) {
                if (part.dnx.isEmpty())
                    empty.add(part);
                else {
                    BigDecimal score = getScoringFunction().getScore(part);
                    if ((score.compareTo(best) < 0) || (score.compareTo(best) == 0 && diff(part) < diff(partition))) {
                        partition = part;
                        best = score;
                        updateDifference(partition);
                        if (partition.difference.compareTo(bestdiff) < 0)
                            bestdiff = partition.difference;
                    }
                }
                count++;
            }
            if (partition != null && partition.score.compareTo(BigDecimal.valueOf(getThreshold())) < 0)
                break;
        }
        getPartitions().removeAll(empty);
        return partition;
    }

    private void updateDifference(Partition<Formula> partition) {
        BigDecimal left = sumProbabilities(partition.dx);
        BigDecimal right = sumProbabilities(partition.dnx);
        BigDecimal none = sumProbabilities(partition.dz);
        // TODO think about the following statement
        partition.difference = left.subtract(right).add(none.divide(new BigDecimal(2))).abs();
    }

    private BigDecimal sumProbabilities(Set<Diagnosis<Formula>> partition) {
        BigDecimal sum = new BigDecimal(0);
        for (Diagnosis<Formula> ids : partition) {
            sum = sum.add(ids.getMeasure());
        }
        return sum;
    }

    private boolean compare(Partition partition, Partition part) {
        if (part == null || partition == null)
            return true;
        return part.dx.size() > partition.dx.size();
    }

    private double diff(Partition part) {
        if (part == null)
            return Double.MAX_VALUE;
        return Math.abs(part.dx.size() - part.dnx.size()) + part.dz.size() / 2;
    }

    public boolean verifyPartition(Partition<Formula> partition)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        if (partition.isVerified)
            return true;
        Set<Formula> ent = partition.partition;
        // partition the rest of diagnoses
        for (Diagnosis<Formula> hs : getHittingSets()) {
            if (!partition.dx.contains(hs)) {
                if (hs.getEntailments().containsAll(ent)) {
                    partition.dx.add(hs);
                    partition.difference = partition.difference.add(hs.getMeasure());
                } else if (!diagnosisConsistent(hs, ent))
                    partition.dnx.add(hs);
                else if (diagnosisEntails(hs, ent)) {
                    partition.dx.add(hs);
                    partition.difference = partition.difference.add(hs.getMeasure());
                } else {
                    partition.dz.add(hs);
                    // partition.difference = partition.difference.add(new BigDecimal(hs.getConflictMeasure()/2d));
                }
            }
        }
        partition.isVerified = true;
        return true;
    }

    protected void findPartition(Differencing diff) { // throws SolverException, InconsistentTheoryException {
        // diff == null
        if (diff.tail.isEmpty()) {
            if (diff.left.isEmpty())
                return;
            else {
                Partition<Formula> part = new Partition<>(diagnosisEngine.getModel());
                for (Diagnosis<Formula> el : diff.left)
                    part.dx.add(el);
                Set<Formula> ent = getCommonEntailments(part.dx);
                if (ent == null || ent.isEmpty())
                    return;
                part.partition = Collections.unmodifiableSet(ent);
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "Adding partition with common entailments: " + part.partition);
                part.difference = diff.difference;
                getPartitions().add(part);
                incPartitionsCount();
                return;
            }
        }

        Diagnosis<Formula> hs = diff.getNext();

        Differencing less = diff.addLess(hs);
        findPartition(less);

        Differencing more = diff.addMore(hs);
        findPartition(more);
    }

    private class Differencing {
        private Set<Diagnosis<Formula>> left = new LinkedHashSet<Diagnosis<Formula>>();
        private Set<Diagnosis<Formula>> right = new LinkedHashSet<Diagnosis<Formula>>();
        private Set<Diagnosis<Formula>> tail = new LinkedHashSet<Diagnosis<Formula>>();
        private BigDecimal difference;
        private BigDecimal sumLeft = new BigDecimal(0);
        private BigDecimal sumRight = new BigDecimal(0);

        public Differencing(Set<Diagnosis<Formula>> desc) {
            tail.addAll(desc);
        }

        public Differencing(Differencing differencing) {
            right.addAll(differencing.right);
            left.addAll(differencing.left);
            tail.addAll(differencing.tail);
            sumLeft = differencing.sumLeft;
            sumRight = differencing.sumRight;
        }

        public Differencing addLess(Diagnosis<Formula> element) {
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

        public Differencing addMore(Diagnosis<Formula> element) {
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

        public Diagnosis<Formula> getNext() {
            Iterator<Diagnosis<Formula>> ti = this.tail.iterator();
            return ti.next();
        }
    }

}
