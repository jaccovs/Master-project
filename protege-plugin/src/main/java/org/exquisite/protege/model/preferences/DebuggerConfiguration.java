package org.exquisite.protege.model.preferences;

import org.semanticweb.owlapi.reasoner.InferenceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The applied preferences and configuration used for diagnoses search and query computation.
 */
public class DebuggerConfiguration {


    /**
     * Configuration possibility to use diverse diagnosis engines.
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
                    return DefaultPreferences.getDefaultDiagnosisEngineType().toString();
            }
        }
    }

    /**
     * Configuration possibility to use diverse requirements measurements used for query computation.
     */
    public enum RM {
        ENT,
        SPL,
        RIO,
        KL,
        EMCb,
        BME;

        @Override
        public String toString() {
            switch (this) {
                case ENT:
                    return "Entropy";
                case SPL:
                    return "Split in Half";
                case RIO:
                    return "Dynamic Risk";
                case KL:
                    return "KL";
                case EMCb:
                    return "EMCb";
                case BME:
                    return "BME";
                default:
                    return DefaultPreferences.getDefaultRM().toString();
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
                    return DefaultPreferences.getDefaultSortCriterion().toString();
            }
        }
    }

    public enum CostEstimator {
        EQUAL,
        CARD,
        SYNTAX;

        @Override
        public String toString() {
            switch (this) {
                case EQUAL:
                    return "EqualCosts";
                case CARD:
                    return "Cardinality";
                case SYNTAX:
                    return "Syntax";
                default:
                    return DefaultPreferences.getDefaultCostEstimator().toString();
            }
        }
    }

    /** The diagnoses engine to be used: possible values are: HSDag, HSTree and InverseQuickXPlain (FastDiag). Default: HSTree  */
    public DiagnosisEngineType engineType = DefaultPreferences.getDefaultDiagnosisEngineType();

    /** The maximum number of leading diagnoses to search for. Default: 9. */
    public Integer numOfLeadingDiags = DefaultPreferences.getDefaultNumOfLeadingDiags();

    /** Also check for incoherency next to inconsistency in the ontology. */
    public Boolean reduceIncoherency = DefaultPreferences.getDefaultReduceIncoherency();

    public Boolean extractModules = DefaultPreferences.getDefaultExtractModules();

    /** Shall the query computation use enrichment of queries ? */
    public Boolean enrichQuery = DefaultPreferences.getDefaultEnrichQuery();

    public SortCriterion sortCriterion = DefaultPreferences.getDefaultSortCriterion();

    /**
     * The applied measure used during qPartition selection applied for query computation.
     * Possible measures are are entropy based, split in half or risk optimization(RIO) -based requirements measure.
     * Default: ENT
     */
    public RM rm = DefaultPreferences.getDefaultRM();

    public Double entropyThreshold = DefaultPreferences.getDefaultEntropyThreshold();
    public Double cardinalityThreshold = DefaultPreferences.getDefaultCardinalityThreshold();
    public Double cautiousParameter = DefaultPreferences.getDefaultCautiousParameter();

    public CostEstimator costEstimator = DefaultPreferences.getDefaultCostEstimator();

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

    public boolean hasConfigurationChanged(DebuggerConfiguration newConfiguration) {
        return !this.equals(newConfiguration);
    }

    public boolean hasConfigurationChanged(Properties properties) {
        return true;
    }

    public boolean hasCheckTypeChanged(DebuggerConfiguration newConfiguration) {
        return !this.reduceIncoherency.equals(newConfiguration.reduceIncoherency);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DebuggerConfiguration that = (DebuggerConfiguration) o;

        if (engineType != that.engineType) return false;
        if (numOfLeadingDiags != null ? !numOfLeadingDiags.equals(that.numOfLeadingDiags) : that.numOfLeadingDiags != null)
            return false;
        if (reduceIncoherency != null ? !reduceIncoherency.equals(that.reduceIncoherency) : that.reduceIncoherency != null)
            return false;
        if (extractModules != null ? !extractModules.equals(that.extractModules) : that.extractModules != null)
           return false;
        if (enrichQuery != null ? !enrichQuery.equals(that.enrichQuery) : that.enrichQuery != null) return false;
        if (sortCriterion != that.sortCriterion) return false;
        if (rm != that.rm) return false;
        if (entropyThreshold != null ? !entropyThreshold.equals(that.entropyThreshold) : that.entropyThreshold != null)
            return false;
        if (cardinalityThreshold != null ? !cardinalityThreshold.equals(that.cardinalityThreshold) : that.cardinalityThreshold != null)
            return false;
        if (cautiousParameter != null ? !cautiousParameter.equals(that.cautiousParameter) : that.cautiousParameter != null)
            return false;
        return costEstimator == that.costEstimator;

    }

    @Override
    public int hashCode() {
        int result = engineType != null ? engineType.hashCode() : 0;
        result = 31 * result + (numOfLeadingDiags != null ? numOfLeadingDiags.hashCode() : 0);
        result = 31 * result + (reduceIncoherency != null ? reduceIncoherency.hashCode() : 0);
        result = 31 * result + (extractModules != null ? extractModules.hashCode() : 0);
        result = 31 * result + (enrichQuery != null ? enrichQuery.hashCode() : 0);
        result = 31 * result + (sortCriterion != null ? sortCriterion.hashCode() : 0);
        result = 31 * result + (rm != null ? rm.hashCode() : 0);
        result = 31 * result + (entropyThreshold != null ? entropyThreshold.hashCode() : 0);
        result = 31 * result + (cardinalityThreshold != null ? cardinalityThreshold.hashCode() : 0);
        result = 31 * result + (cautiousParameter != null ? cautiousParameter.hashCode() : 0);
        result = 31 * result + (costEstimator != null ? costEstimator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EngineType: " +  engineType + ", " +
                "numOfLeadingDiags: " + numOfLeadingDiags + ", " +
                "reduceIncoherency: " + reduceIncoherency + ", " +
                "extractModules:" + extractModules + ", " +
                "enrichQuery: " + enrichQuery + ", " +
                "sortCriterion: " + sortCriterion + ", " +
                "RM: " + rm + ", " +
                "entropy threshold: " + entropyThreshold + ", " +
                "cardinality threshold: " + cardinalityThreshold + ", " +
                "cautious parameter: " + cautiousParameter + ", " +
                "costEstimator: " + costEstimator;
    }

}
