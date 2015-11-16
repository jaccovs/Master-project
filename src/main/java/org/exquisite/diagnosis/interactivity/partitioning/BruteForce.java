package org.exquisite.diagnosis.interactivity.partitioning;

import org.exquisite.diagnosis.DiagnosisException;
import org.exquisite.diagnosis.IDiagnosisEngine;
import org.exquisite.diagnosis.interactivity.partitioning.scoring.Scoring;
import org.exquisite.diagnosis.models.Diagnosis;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.quickxplain.QuickXPlain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Kostyas BruteForce class.
 *
 * @author Schmitz
 */
public class BruteForce<T> implements Partitioning<T> {

    private static Logger logger = LoggerFactory.getLogger(BruteForce.class.getName());

    // private Searchable<Id> theory;

    protected IDiagnosisEngine<T> diagnosisEngine;
    protected int numOfHittingSets;
    private Set<Diagnosis<T>> hittingSets;
    private int partitionsCount = 0;
    private Scoring<T> scoring = null;
    private ArrayList<Partition<T>> partitions = new ArrayList<>();
    private Partition bestPartition = null;
    private double threshold = 0.01d;

    public BruteForce(IDiagnosisEngine<T> diagnosisEngine, Scoring<T> function) {
        this.diagnosisEngine = diagnosisEngine;
        this.scoring = function;
        this.scoring.setPartitionSearcher(this);
    }

    public static <T> Set<T> getCommonEntailments(Set<Diagnosis<T>> dx) { // throws SolverException {
        Set<T> intersection = null;
        for (Diagnosis<T> hs : dx) {
            if (intersection == null)
                intersection = new LinkedHashSet<>(hs.getEntailments());
            else
                intersection.retainAll(hs.getEntailments());
            if (intersection.isEmpty())
                return intersection;
        }
        return intersection;
    }

    protected void reset() {
        partitionsCount = 0;
        hittingSets = null;
        partitions = new ArrayList<>();
        bestPartition = null;

    }

    protected String toString(Set<Diagnosis<T>> hittingSets) {
        StringBuilder res = new StringBuilder();
        for (Diagnosis<T> hittingSet : hittingSets) {
            res.append(hittingSet.toString()).append(" ");
        }
        return res.toString();
    }

    public int getNumOfHittingSets() {
        return numOfHittingSets;
    }

    public Partition<T> generatePartition(Set<Diagnosis<T>> hittingSets)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

        numOfHittingSets = hittingSets.size();
        reset();
        Set<Diagnosis<T>> hs = preprocess(hittingSets);

        // find the best partition
        Set<Diagnosis<T>> desc = new LinkedHashSet<>(new TreeSet<>(hs).descendingSet());
        Partition<T> partition = findPartition(desc, new LinkedHashSet<>());
        if (logger.isDebugEnabled())
            logger.debug("Searched through " + getPartitionsCount() + " partitionsCount");
        if (getScoring() != null) {
            partition = getScoring().runPostprocessor(getPartitions(), partition);
        }
        restoreEntailments(hittingSets);
        return partition;
    }

    public Partition nextPartition(Partition lastPartition)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        getPartitions().remove(lastPartition);
        Partition partition = getPartitions().get(0);
        if (!partition.isVerified)
            verifyPartition(partition);
        if (getScoring() != null)
            partition = getScoring().runPostprocessor(getPartitions(), partition);
        lastPartition = partition;
        return partition;
    }

    protected Set<Diagnosis<T>> preprocess(Set<Diagnosis<T>> hittingSets) { // throws SolverException {
//		ensureCapacity((int) Math.pow(2, hittingSets.size()));
        if (getScoringFunction() == null)
            throw new IllegalStateException("Scoring function is not set!");
        // save the original hitting sets

        setHittingSets(Collections.unmodifiableSet(hittingSets));
        // preprocessing
        Set<Diagnosis<T>> hs = new LinkedHashSet<>(hittingSets);
        removeCommonEntailments(hs);
        for (Iterator<Diagnosis<T>> hsi = hs.iterator(); hsi.hasNext(); )
            if (hsi.next().getEntailments().isEmpty())
                hsi.remove();
        getScoringFunction().normalize(hs);

        return new TreeSet<Diagnosis<T>>(hs);
    }

    protected void restoreEntailments(Set<Diagnosis<T>> hittingSets) {
        for (Diagnosis<T> hs : hittingSets)
            hs.restoreEntailments();
    }

    protected void removeCommonEntailments(Set<Diagnosis<T>> hittingSets) { // throws SolverException {
        Set<T> ent = getCommonEntailments(hittingSets);
        if (!ent.isEmpty())
            for (Diagnosis<T> hs : hittingSets) {
                Set<T> hse = new LinkedHashSet<>(hs.getEntailments());
                hse.removeAll(ent);
                hs.setEntailments(hse);
            }
    }

    protected void ensureCapacity(int cap) {
        this.partitions.ensureCapacity(cap);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean verifyPartition(Partition<T> partition) throws DiagnosisException {
        Set<T> ent = getCommonEntailments(partition.dx);
        partition.partition = Collections.unmodifiableSet(ent);
        if (logger.isDebugEnabled())
            logger.debug("Common entailments: " + partition.partition);
        if (ent == null || ent.isEmpty())
            return false;
        // partition the rest of diagnoses
        for (Diagnosis<T> hs : getHittingSets()) {
            if (!partition.dx.contains(hs)) {
                if (hs.getEntailments().containsAll(ent))
                    partition.dx.add(hs);
                else if (!diagnosisConsistent(hs, ent))
                    partition.dnx.add(hs);
                else if (diagnosisEntails(hs, ent))
                    partition.dx.add(hs);
                else
                    partition.dz.add(hs);
            }
        }
        return true;
    }

    protected boolean diagnosisConsistent(Diagnosis<T> diagnosis, Set<T> entailments) throws DiagnosisException {
        QuickXPlain<T> qx = new QuickXPlain<>(diagnosisEngine.getSessionData(), diagnosisEngine);

        List<T> constraints = new ArrayList<T>();
        DiagnosisModel<T> model = diagnosisEngine.getModel();

        constraints.addAll(model.getPossiblyFaultyStatements());
        constraints.removeAll(diagnosis.getElements());

        constraints.addAll(model.getCorrectStatements());

        constraints.addAll(entailments);

        // TODO: We only use first testcase at the moment
        if (diagnosisEngine.getModel().getPositiveExamples().size() > 0) {
            constraints.addAll(diagnosisEngine.getModel().getPositiveExamples().get(0).constraints);
        }

        return qx.isConsistent(constraints);
    }

    protected boolean diagnosisEntails(Diagnosis<T> diagnosis, Set<T> entailments) throws DiagnosisException {
        QuickXPlain<T> qx = new QuickXPlain<T>(diagnosisEngine.getSessionData(), diagnosisEngine);

        List<T> constraints = new ArrayList<T>();
        DiagnosisModel<T> model = diagnosisEngine.getModel();

        constraints.addAll(model.getPossiblyFaultyStatements());
        constraints.removeAll(diagnosis.getElements());

        constraints.addAll(model.getCorrectStatements());

        // TODO: We only use first testcase at the moment
        if (diagnosisEngine.getModel().getPositiveExamples().size() > 0) {
            constraints.addAll(diagnosisEngine.getModel().getPositiveExamples().get(0).constraints);
        }

        return qx.isEntailed(constraints, entailments);
    }

    protected Partition<T> findPartition(Set<Diagnosis<T>> hittingSets, Set<Diagnosis<T>> head)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

        // if (this.bestPartition != null && this.bestPartition.score < this.threshold)
        // return this.bestPartition;
        if ((hittingSets == null || hittingSets.isEmpty())) {
            if (head.isEmpty())
                return new Partition<>(diagnosisEngine.getModel());
            else {
                incPartitionsCount();
                Partition<T> part = new Partition<>(diagnosisEngine.getModel());
                for (Diagnosis<T> el : head)
                    part.dx.add(el); // getOriginalHittingSet(el));
                if (logger.isDebugEnabled())
                    logger.debug("Creating a partition with dx: " + head);
                if (verifyPartition(part)) {
                    if (logger.isDebugEnabled())
                        logger.debug("Created partition: \n dx:" + part.dx + "\n dnx:" + part.dnx + "\n dz:" + part.dz);
                    if (getPartitions() != null && !getPartitions().contains(part)) {
                        getPartitions().add(part);
                    }
                    return part;
                }
                return null;
            }
        }

        Set<Diagnosis<T>> tail = new LinkedHashSet<Diagnosis<T>>(hittingSets);
        Iterator<Diagnosis<T>> ti = tail.iterator();
        Diagnosis<T> hs = ti.next();
        ti.remove();

        if (logger.isDebugEnabled())
            logger.debug("Partitions: " + partitionsCount + " head: " + head.size() + " hsets:" + hittingSets.size());
        Partition part = findPartition(tail, head);

        head.add(hs);
        Partition partHead = null;

        // if (this.bestPartition == null || (getScoringFunction().getPartitionScore(part) <= this.bestPartition.score))
        partHead = findPartition(tail, head);

        head.remove(hs);

        Partition best = partHead;
        if (getScoringFunction().getScore(part).compareTo(getScoringFunction().getScore(partHead)) < 0) {
            best = part;
        }
        if (this.bestPartition == null || (best != null && this.bestPartition.score.compareTo(best.score) > 0))
            this.bestPartition = best;
        return best;
    }

    protected void incPartitionsCount() {
        this.partitionsCount++;
    }

    // protected Diagnosis getOriginalHittingSet(Diagnosis el) {
    // for (Diagnosis elem : getHittingSets()) {
    // if (el.compareTo(elem) == 0)
    // return elem;
    // }
    // return null;
    // }

    // public Searchable<Id> getTheory() {
    // return theory;
    // }

    public Scoring<T> getScoringFunction() {
        return this.scoring;
    }

    public Set<Diagnosis<T>> getHittingSets() {
        return hittingSets;
    }

    protected void setHittingSets(Set<Diagnosis<T>> hittingSets) {
        this.hittingSets = hittingSets;
    }

    public int getPartitionsCount() {
        return partitionsCount;
    }

    public Scoring<T> getScoring() {
        return scoring;
    }

    public List<Partition<T>> getPartitions() {
        return partitions;
    }

}
