package org.exquisite.core.engines.query.search;

import org.exquisite.core.DiagnosisException;
import org.exquisite.core.ISolver;
import org.exquisite.core.engines.query.Query;
import org.exquisite.core.engines.query.QueryComputation;
import org.exquisite.core.engines.query.scoring.QuerySelection;
import org.exquisite.core.engines.query.scoring.SplitInHalfQSS;
import org.exquisite.core.model.Diagnosis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Kostyas BruteForce class.
 *
 * @author Schmitz
 */
public class BruteForce<T> implements QueryComputation<T> {

    private static Logger logger = LoggerFactory.getLogger(BruteForce.class.getName());

    private ISolver<T> solver;
    protected int numOfHittingSets;
    private Set<Diagnosis<T>> hittingSets;
    private int partitionsCount = 0;
    private QuerySelection<T> scoring = null;
    private List<Query<T>> queries = new ArrayList<>();
    private Query bestQuery = null;
    private double threshold = 0.01d;

    public BruteForce(ISolver<T> solver, QuerySelection<T> function) {
        this.solver = solver;
        this.scoring = function;
        this.scoring.setPartitionSearcher(this);
    }

    public BruteForce(ISolver<T> solver) {
        this.scoring = new SplitInHalfQSS<>();
        this.scoring.setPartitionSearcher(this);
    }

    public static <T> Set<T> getCommonFormulas(Set<Diagnosis<T>> dx) { // throws SolverException {
        Set<T> intersection = null;
        for (Diagnosis<T> hs : dx) {
            if (intersection == null)
                intersection = new LinkedHashSet<>(hs.getFormulas());
            else
                intersection.retainAll(hs.getFormulas());
            if (intersection.isEmpty())
                return intersection;
        }
        return intersection;
    }

    protected void reset() {
        partitionsCount = 0;
        hittingSets = null;
        queries = new ArrayList<>();
        bestQuery = null;

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

    public void nextQuery(ISolver<T> solver, Set<Diagnosis<T>> hittingSets, boolean useEntailments)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {

        numOfHittingSets = hittingSets.size();
        reset();
        Set<Diagnosis<T>> hs = preprocess(hittingSets);

        // find the best query
        Set<Diagnosis<T>> desc = new LinkedHashSet<>(new TreeSet<>(hs).descendingSet());
        Query<T> query = findPartition(desc, new LinkedHashSet<>());
        if (logger.isDebugEnabled())
            logger.debug("Searched through " + getPartitionsCount() + " partitionsCount");
        if (getScoring() != null) {
            query = getScoring().runPostprocessor(getQueries(), query);
        }
        restoreEntailments(hittingSets);
        return query;
    }

    public Query nextPartition(Query lastQuery)
            throws DiagnosisException { // throws SolverException, InconsistentTheoryException {
        getQueries().remove(lastQuery);
        Query query = getQueries().get(0);
        if (!query.isVerified)
            verifyPartition(query);
        if (getScoring() != null)
            query = getScoring().runPostprocessor(getQueries(), query);
        lastQuery = query;
        return query;
    }

    protected Set<Diagnosis<T>> preprocess(Set<Diagnosis<T>> hittingSets) { // throws SolverException {
//		ensureCapacity((int) Math.pow(2, hittingSets.size()));
        if (getScoringFunction() == null)
            throw new IllegalStateException("QuerySelection function is not set!");
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
        Set<T> ent = getCommonFormulas(hittingSets);
        if (!ent.isEmpty())
            for (Diagnosis<T> hs : hittingSets) {
                Set<T> hse = new LinkedHashSet<>(hs.getEntailments());
                hse.removeAll(ent);
                hs.setEntailments(hse);
            }
    }

    protected void ensureCapacity(int cap) {
        this.queries.ensureCapacity(cap);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean verifyPartition(Query<T> query) throws DiagnosisException {
        Set<T> ent = getCommonFormulas(query.dx);
        query.query = Collections.unmodifiableSet(ent);
        if (logger.isDebugEnabled())
            logger.debug("Common entailments: " + query.query);
        if (ent == null || ent.isEmpty())
            return false;
        // query the rest of diagnoses
        for (Diagnosis<T> hs : getHittingSets()) {
            if (!query.dx.contains(hs)) {
                if (hs.getEntailments().containsAll(ent))
                    query.dx.add(hs);
                else if (!diagnosisConsistent(hs, ent))
                    query.dnx.add(hs);
                else if (diagnosisEntails(hs, ent))
                    query.dx.add(hs);
                else
                    query.dz.add(hs);
            }
        }
        return true;
    }

    protected boolean diagnosisConsistent(Diagnosis<T> diagnosis, Set<T> entailments) throws DiagnosisException {
        ConstraintsQuickXPlain<T> qx = new ConstraintsQuickXPlain<>(diagnosisEngine.getDiagnosisModel());

        List<T> constraints = new ArrayList<T>();
        DiagnosisModel<T> model = diagnosisEngine.getDiagnosisModel();

        constraints.addAll(model.getPossiblyFaultyStatements());
        constraints.removeAll(diagnosis.getFormulas());

        constraints.addAll(model.getCorrectStatements());

        constraints.addAll(entailments);

        // TODO: We only use first testcase at the moment
        if (diagnosisEngine.getDiagnosisModel().getPositiveExamples().size() > 0) {
            constraints.addAll(diagnosisEngine.getDiagnosisModel().getPositiveExamples().get(0).constraints);
        }

        return qx.isConsistent(constraints);
    }

    protected boolean diagnosisEntails(Diagnosis<T> diagnosis, Set<T> entailments) throws DiagnosisException {
        ConstraintsQuickXPlain<T> qx = new ConstraintsQuickXPlain<T>(diagnosisEngine.getDiagnosisModel());

        List<T> constraints = new ArrayList<T>();
        DiagnosisModel<T> model = diagnosisEngine.getDiagnosisModel();

        constraints.addAll(model.getPossiblyFaultyStatements());
        constraints.removeAll(diagnosis.getFormulas());

        constraints.addAll(model.getCorrectStatements());

        // TODO: We only use first testcase at the moment
        if (diagnosisEngine.getDiagnosisModel().getPositiveExamples().size() > 0) {
            constraints.addAll(diagnosisEngine.getDiagnosisModel().getPositiveExamples().get(0).constraints);
        }

        return qx.isEntailed(constraints, entailments);
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

    public QuerySelection<T> getScoringFunction() {
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

    public QuerySelection<T> getScoring() {
        return scoring;
    }

    public List<Query<T>> getQueries() {
        return queries;
    }

}
