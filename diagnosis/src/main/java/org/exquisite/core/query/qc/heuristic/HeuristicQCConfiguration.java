package org.exquisite.core.query.qc.heuristic;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.qc.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.qc.heuristic.sortcriteria.ISortCriterion;
import org.exquisite.core.query.qc.heuristic.sortcriteria.MinQueryCardinality;

import java.math.BigDecimal;

/**
 * A configuration template used for the heuristic query computation in HeuristicQC. It serves as a container of
 * necessary information used during query computation.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author wolfi
 */
public class HeuristicQCConfiguration<F> {

    private static final long DEFAULT_TIMEOUT = 1000;

    private static final int DEFAULT_MIN_QUERIES = 1;

    private static final int DEFAULT_MAX_QUERIES = 1;

    private static final ISortCriterion DEFAULT_SORT_CRITIERION = new MinQueryCardinality<>();

    private static final IQPartitionRequirementsMeasure DEFAULT_REQUIREMENTS_MEASURE = new EntropyBasedMeasure(new BigDecimal("0.05"));

    private static final boolean DEFAULT_ENRICH_QUERIES = false;


    protected AbstractDiagnosisEngine<F> diagnosisEngine;

    protected IQPartitionRequirementsMeasure rm;

    protected ISortCriterion sortCriterion;

    protected long timeout;

    protected int minQueries;

    protected int maxQueries;

    protected boolean enrichQueries;

    public HeuristicQCConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine) {
        this(diagnosisEngine, DEFAULT_REQUIREMENTS_MEASURE, DEFAULT_SORT_CRITIERION, DEFAULT_TIMEOUT, DEFAULT_MIN_QUERIES, DEFAULT_MAX_QUERIES, DEFAULT_ENRICH_QUERIES);
    }

    public HeuristicQCConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine, IQPartitionRequirementsMeasure rm) {
        this(diagnosisEngine, rm, DEFAULT_SORT_CRITIERION, DEFAULT_TIMEOUT, DEFAULT_MIN_QUERIES, DEFAULT_MAX_QUERIES, DEFAULT_ENRICH_QUERIES);
    }

    public HeuristicQCConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine, IQPartitionRequirementsMeasure rm, ISortCriterion sortCriterion, long timeout, int minQueries, int maxQueries, boolean enrichQueries) {
        this.diagnosisEngine = diagnosisEngine;
        this.rm = rm;
        this.sortCriterion = sortCriterion;
        this.timeout = timeout;
        this.minQueries = minQueries;
        this.maxQueries = maxQueries;
        this.enrichQueries = enrichQueries;
    }


    public AbstractDiagnosisEngine<F> getDiagnosisEngine() {
        return diagnosisEngine;
    }

    public void setDiagnosisEngine(AbstractDiagnosisEngine<F> diagnosisEngine) {
        this.diagnosisEngine = diagnosisEngine;
    }

    public IQPartitionRequirementsMeasure getRm() {
        return rm;
    }

    public void setRm(IQPartitionRequirementsMeasure rm) {
        this.rm = rm;
    }

    public ISortCriterion getSortCriterion() {
        return sortCriterion;
    }

    public void setSortCriterion(ISortCriterion sortCriterion) {
        this.sortCriterion = sortCriterion;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMinQueries() {
        return minQueries;
    }

    public void setMinQueries(int minQueries) {
        this.minQueries = minQueries;
    }

    public int getMaxQueries() {
        return maxQueries;
    }

    public void setMaxQueries(int maxQueries) {
        this.maxQueries = maxQueries;
    }
}
