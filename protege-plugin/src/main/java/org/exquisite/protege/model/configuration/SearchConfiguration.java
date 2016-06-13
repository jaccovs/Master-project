package org.exquisite.protege.model.configuration;

import org.semanticweb.owlapi.reasoner.InferenceType;

import java.util.ArrayList;
import java.util.List;

/**
 * The applied preferences and configuration used for diagnoses search and query computation.
 */
public class SearchConfiguration {

    /** Default value for minimal queries */
    static final Integer DEFAULT_MINIMAL_QUERIES = 1;

    /** Default value for maximal queries */
    static final Integer DEFAULT_MAXIMAL_QUERIES = 1;

    /**
     * Configuration possibilty to use diverse diagnosis engines.
     */
    public enum DiagnosisEngineType {
        HSDAG,
        HSTree,
        Inverse;
        @Override
        public String toString() {
            switch (this) {
                case HSDAG:
                    return "HS-DAG";
                case HSTree:
                    return "HS-Tree";
                case Inverse:
                    return "Inv-QuickXPlain";
                default:
                    return this.toString();
            }
        }

    }

    /**
     * Configuration possibility to use diverse requirements measurements used for query computation.
     */
    public enum RM {
        ENT,
        SPL,
        RIO;

        @Override
        public String toString() {
            switch (this) {
                case ENT:
                    return "Entropy";
                case SPL:
                    return "Split in Half";
                case RIO:
                    return "Dynamic Risk";
                default:
                    return this.toString();
            }
        }

    }

    public enum SortCriterion {
        MINCARD,
        MINSUM,
        MINMAX;

        @Override
        public String toString() {
            switch (this) {
                case MINCARD:
                    return "MinCard";
                case MINSUM:
                    return "MinSum";
                case MINMAX:
                    return "MinMax";
                default:
                    return this.toString();
            }
        }
    }

    /** The diagnoses engine to be used: possible values are: HSDag, HSTree and InverseQuickXPlain (FastDiag). Default: HSTree  */
    public DiagnosisEngineType engineType = DiagnosisEngineType.HSTree;

    /** The maximum number of leading diagnoses to search for. Default: 9. */
    public Integer numOfLeadingDiags = 9;

    public Boolean reduceIncoherency = true;

    public Boolean extractModules = false;

    /** Generate at least minimal queries */
    public Integer minimalQueries = DEFAULT_MINIMAL_QUERIES;

    /** Generate at most maxmial queries (>= minimalQueries) */
    public Integer maximalQueries = DEFAULT_MAXIMAL_QUERIES;

    /** Shall the query computation use enrichment of queries ? */
    public Boolean enrichQuery = true;

    public SortCriterion sortCriterion = SortCriterion.MINCARD;

    /**
     * The applied measure used during qPartition selection applied for query computation.
     * Possible measures are are entropy based, split in half or risk optimization(RIO) -based requirements measure.
     * Default: ENT
     */
    public RM rm = RM.ENT;

    public Double entropyThreshold = 0.05;
    public Double cardinalityThreshold = 0.00;
    public Double cautiousParameter = 0.4;

    /**
     * Returns an array of preferred inference types (entailment types).
     * By default we have class hierarchy and disjoint classes.
     *
     * @return The preferred entailment types.
     */
    InferenceType[] getEntailmentTypes() {
        List<InferenceType> entailmentTypes = new ArrayList<>();
        entailmentTypes.add(InferenceType.CLASS_HIERARCHY);
        entailmentTypes.add(InferenceType.DISJOINT_CLASSES);

        return entailmentTypes.toArray(new InferenceType[entailmentTypes.size()]);
    }

    public boolean hasConfigurationChanged(SearchConfiguration newConfiguration) {
        return !this.equals(newConfiguration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchConfiguration that = (SearchConfiguration) o;

        if (engineType != that.engineType) return false;
        if (numOfLeadingDiags != null ? !numOfLeadingDiags.equals(that.numOfLeadingDiags) : that.numOfLeadingDiags != null)
            return false;
        if (reduceIncoherency != null ? !reduceIncoherency.equals(that.reduceIncoherency) : that.reduceIncoherency != null)
            return false;
        if (extractModules != null ? !extractModules.equals(that.extractModules) : that.extractModules != null)
            return false;
        if (minimalQueries != null ? !minimalQueries.equals(that.minimalQueries) : that.minimalQueries != null)
            return false;
        if (maximalQueries != null ? !maximalQueries.equals(that.maximalQueries) : that.maximalQueries != null)
            return false;
        if (enrichQuery != null ? !enrichQuery.equals(that.enrichQuery) : that.enrichQuery != null) return false;
        if (sortCriterion != that.sortCriterion) return false;
        if (rm != that.rm) return false;
        if (entropyThreshold != null ? !entropyThreshold.equals(that.entropyThreshold) : that.entropyThreshold != null)
            return false;
        if (cardinalityThreshold != null ? !cardinalityThreshold.equals(that.cardinalityThreshold) : that.cardinalityThreshold != null)
            return false;
        return cautiousParameter != null ? cautiousParameter.equals(that.cautiousParameter) : that.cautiousParameter == null;

    }

    @Override
    public int hashCode() {
        int result = engineType != null ? engineType.hashCode() : 0;
        result = 31 * result + (numOfLeadingDiags != null ? numOfLeadingDiags.hashCode() : 0);
        result = 31 * result + (reduceIncoherency != null ? reduceIncoherency.hashCode() : 0);
        result = 31 * result + (extractModules != null ? extractModules.hashCode() : 0);
        result = 31 * result + (minimalQueries != null ? minimalQueries.hashCode() : 0);
        result = 31 * result + (maximalQueries != null ? maximalQueries.hashCode() : 0);
        result = 31 * result + (enrichQuery != null ? enrichQuery.hashCode() : 0);
        result = 31 * result + (sortCriterion != null ? sortCriterion.hashCode() : 0);
        result = 31 * result + (rm != null ? rm.hashCode() : 0);
        result = 31 * result + (entropyThreshold != null ? entropyThreshold.hashCode() : 0);
        result = 31 * result + (cardinalityThreshold != null ? cardinalityThreshold.hashCode() : 0);
        result = 31 * result + (cautiousParameter != null ? cautiousParameter.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "EngineType: " +  engineType + ", " +
                "numOfLeadingDiags: " + numOfLeadingDiags + ", " +
                "reduceIncoherency: " + reduceIncoherency + ", " +
                "extractModules:" + extractModules + ", " +
                "minimalQueries:" + minimalQueries + ", " +
                "maximalQueries:" + maximalQueries + ", " +
                "enrichQuery: " + enrichQuery + ", " +
                "sortCriterion: " + sortCriterion + ", " +
                "RM: " + rm + ", " +
                "entropy threshold: " + entropyThreshold + ", " +
                "cardinality threshold: " + cardinalityThreshold + ", " +
                "cautious parameter: " + cautiousParameter;
    }

}
