package org.exquisite.core.query.querycomputation.heuristic;

import org.exquisite.core.engines.AbstractDiagnosisEngine;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.EntropyBasedMeasure;
import org.exquisite.core.query.querycomputation.heuristic.partitionmeasures.IQPartitionRequirementsMeasure;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.ISortCriterion;
import org.exquisite.core.query.querycomputation.heuristic.sortcriteria.MinQueryCardinality;

import java.math.BigDecimal;

/**
 * A configuration used for the heuristic query computation in class HeuristicQueryComputation.
 * It serves as a container of necessary information used during query computation.
 *
 * The configuration can be manipulated via getter and/or setter methods. However keep in mind that proper use of
 * initialize(), reset(), hasNext() and next() in class HeuristicQueryComputation is required for this manipulation.
 *
 * @param <F> Formulas, Statements, Axioms, Logical Sentences, Constraints etc.
 * @author wolfi
 */
public class HeuristicConfiguration<F> {

    /* ******************************* DEFAULT VALUES ********************************** */
    /** Default timeout of 1 second (=1000 ms) for query calculation */
    private static final long DEFAULT_TIMEOUT = 1000;

    /** By default find at least 1 query */
    private static final int DEFAULT_MIN_QUERIES = 1;

    /** By default find maximal 1 query */
    private static final int DEFAULT_MAX_QUERIES = 1;

    /**
     * Default sort criterion is MinQueryCardinality which guarantees a breath first search returning those queries
     * with fewest formulas first.
     * */
    private static final ISortCriterion DEFAULT_SORT_CRITIERION = new MinQueryCardinality<>();

    /** Default Requirements Measure used for finding optimal q-partition from a diagnosis */
    private static final IQPartitionRequirementsMeasure DEFAULT_REQUIREMENTS_MEASURE = new EntropyBasedMeasure(new BigDecimal("0.05"));

    /**
     * Shall the computed query be enriched and optimized after the search for an optimal q-partition and after a query selection step?
     * Default is <code>true</code>. Keep in mind that this will execute expensive calls to a reasoner.
     */
    private static final boolean DEFAULT_ENRICH_QUERIES = true;


    /* *************************** Member variables ************************************ */

    AbstractDiagnosisEngine<F> diagnosisEngine;

    IQPartitionRequirementsMeasure rm;

    ISortCriterion sortCriterion;

    long timeout;

    int minQueries;

    int maxQueries;

    boolean enrichQueries;

    /* *************************** Constructor ************************************ */

    public HeuristicConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine) {
        this(diagnosisEngine, DEFAULT_REQUIREMENTS_MEASURE, DEFAULT_SORT_CRITIERION, DEFAULT_TIMEOUT, DEFAULT_MIN_QUERIES, DEFAULT_MAX_QUERIES, DEFAULT_ENRICH_QUERIES);
    }

    public HeuristicConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine, IQPartitionRequirementsMeasure rm) {
        this(diagnosisEngine, rm, DEFAULT_SORT_CRITIERION, DEFAULT_TIMEOUT, DEFAULT_MIN_QUERIES, DEFAULT_MAX_QUERIES, DEFAULT_ENRICH_QUERIES);
    }

    public HeuristicConfiguration(AbstractDiagnosisEngine<F> diagnosisEngine, IQPartitionRequirementsMeasure rm, ISortCriterion sortCriterion, long timeout, int minQueries, int maxQueries, boolean enrichQueries) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("tm=").append(rm);
        sb.append(", sortCriterion=").append(sortCriterion);
        sb.append(", timeout=").append(timeout);
        sb.append(", minQueries=").append(minQueries);
        sb.append(", maxQueries=").append(maxQueries);
        sb.append(", enrichQueries=").append(enrichQueries);
        sb.append(", engine=").append(diagnosisEngine);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeuristicConfiguration<?> that = (HeuristicConfiguration<?>) o;

        if (timeout != that.timeout) return false;
        if (minQueries != that.minQueries) return false;
        if (maxQueries != that.maxQueries) return false;
        if (enrichQueries != that.enrichQueries) return false;
        if (diagnosisEngine != null ? !diagnosisEngine.equals(that.diagnosisEngine) : that.diagnosisEngine != null)
            return false;
        if (rm != null ? !rm.equals(that.rm) : that.rm != null) return false;
        return sortCriterion != null ? sortCriterion.equals(that.sortCriterion) : that.sortCriterion == null;

    }

    @Override
    public int hashCode() {
        int result = diagnosisEngine != null ? diagnosisEngine.hashCode() : 0;
        result = 31 * result + (rm != null ? rm.hashCode() : 0);
        result = 31 * result + (sortCriterion != null ? sortCriterion.hashCode() : 0);
        result = 31 * result + (int) (timeout ^ (timeout >>> 32));
        result = 31 * result + minQueries;
        result = 31 * result + maxQueries;
        result = 31 * result + (enrichQueries ? 1 : 0);
        return result;
    }
}
