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
 * The configuration can be manipulated via getter and/or setter methods. Proper use of initialize(), reset(), hasNext()
 * and next() in HeuristicGQ is required for this manipulation.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author wolfi
 */
public class HeuristicQCConfiguration<F> {

    /* ******************************* DEFAULT VALUES ********************************** */
    /** Default timeout of 1 second */
    private static final long DEFAULT_TIMEOUT = 1000;

    /** Default is find at least 1 query */
    private static final int DEFAULT_MIN_QUERIES = 1;

    /** Default is to find maximal 1 query */
    private static final int DEFAULT_MAX_QUERIES = 1;

    /** Default sort criterion is MinQueryCardinality which guarantees a breath first search returning those queries with fewest formulas */
    private static final ISortCriterion DEFAULT_SORT_CRITIERION = new MinQueryCardinality<>();

    /** Default Requirements Measure used for finding optimal q-partition from a diagnosis */
    private static final IQPartitionRequirementsMeasure DEFAULT_REQUIREMENTS_MEASURE = new EntropyBasedMeasure(new BigDecimal("0.05"));

    /** Shall the query be enriched via (expensive) reasoner calls after finding optimal q-partition and query selection step? Default is No.*/
    private static final boolean DEFAULT_ENRICH_QUERIES = false;


    /* *************************** Member variables ************************************ */

    AbstractDiagnosisEngine<F> diagnosisEngine;

    IQPartitionRequirementsMeasure rm;

    ISortCriterion sortCriterion;

    long timeout;

    int minQueries;

    int maxQueries;

    boolean enrichQueries;

    /* *************************** Constructor ************************************ */

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

    public boolean isEnrichQueries() {
        return enrichQueries;
    }

    public void setEnrichQueries(boolean enrichQueries) {
        this.enrichQueries = enrichQueries;
    }
}
