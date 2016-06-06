package org.exquisite.protege.model.configuration;

import org.semanticweb.owlapi.reasoner.InferenceType;

import java.util.ArrayList;
import java.util.List;

/**
 * The applied preferences or configuration used for diagnoses search and query computation.
 */
public class SearchConfiguration {

    /** Default value for minimal queries */
    public static final Integer DEFAULT_MINIMAL_QUERIES = 1;

    /** Default value for maximal queries */
    public static final Integer DEFAULT_MAXIMAL_QUERIES = 1;

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

    /** The diagnoses engine to be used: possible values are: HSDag, HSTree and InverseQuickXPlain (FastDiag). Default: Inverse  */
    public DiagnosisEngineType engineType = DiagnosisEngineType.Inverse;

    /** The maximum number of leading diagnoses to search for. Default: 9. */
    public Integer numOfLeadingDiags = 9;

    public Boolean reduceIncoherency = false;

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

    /** include specific InferenceTypes (or EntailmentTypes) for reasoner (yes/no) ?*/
    public Boolean incInferenceTypeClassHierarchy = true;
    public Boolean incInferenceTypeDisjointClasses = true;
    public Boolean incInferenceTypeObjectPropertyHierarchy = false;
    public Boolean incInferenceTypeDataPropertyHierarchy = false;
    public Boolean incInferenceTypeClassAssertions = false;
    public Boolean incInferenceTypeObjectPropertyAssertions = false;
    public Boolean incInferenceTypeDataPropertyAssertions = false;
    public Boolean incInferenceTypeSameIndividual = false;
    public Boolean incInferenceTypeDifferentIndividuals = false;

    /**
     * Returns an array of preferred inference types (entailment types).
     *
     * @return
     */
    public InferenceType[] getEntailmentTypes() {
        List<InferenceType> entailmentTypes = new ArrayList<>();
        if (incInferenceTypeClassHierarchy) entailmentTypes.add(InferenceType.CLASS_HIERARCHY);
        if (incInferenceTypeDisjointClasses) entailmentTypes.add(InferenceType.DISJOINT_CLASSES);
        if (incInferenceTypeObjectPropertyHierarchy) entailmentTypes.add(InferenceType.OBJECT_PROPERTY_HIERARCHY);
        if (incInferenceTypeDataPropertyHierarchy) entailmentTypes.add(InferenceType.DATA_PROPERTY_HIERARCHY);
        if (incInferenceTypeClassAssertions) entailmentTypes.add(InferenceType.CLASS_ASSERTIONS);
        if (incInferenceTypeObjectPropertyAssertions) entailmentTypes.add(InferenceType.OBJECT_PROPERTY_ASSERTIONS);
        if (incInferenceTypeDataPropertyAssertions) entailmentTypes.add(InferenceType.DATA_PROPERTY_ASSERTIONS);
        if (incInferenceTypeSameIndividual) entailmentTypes.add(InferenceType.SAME_INDIVIDUAL);
        if (incInferenceTypeDifferentIndividuals) entailmentTypes.add(InferenceType.DIFFERENT_INDIVIDUALS);

        return entailmentTypes.toArray(new InferenceType[entailmentTypes.size()]);
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
                "cautious parameter: " + cautiousParameter + ", " +
                "CLASS_HIERARCHY: " + incInferenceTypeClassHierarchy + ", " +
                "DISJOINT_CLASSES: " + incInferenceTypeDisjointClasses + ", " +
                "OBJECT_PROPERTY_HIERARCHY: " + incInferenceTypeObjectPropertyHierarchy + ", " +
                "DATA_PROPERTY_HIERARCHY: " + incInferenceTypeDataPropertyHierarchy + ", " +
                "CLASS_ASSERTIONS: " + incInferenceTypeClassAssertions + ", " +
                "OBJECT_PROPERTY_ASSERTIONS: " + incInferenceTypeObjectPropertyAssertions + ", " +
                "DATA_PROPERTY_ASSERTIONS: " + incInferenceTypeDataPropertyAssertions + ", " +
                "SAME_INDIVIDUAL: " + incInferenceTypeSameIndividual + ", " +
                "DIFFERENT_INDIVIDUALS: " + incInferenceTypeDifferentIndividuals + ", ";
    }

}
