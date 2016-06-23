package org.exquisite.protege.model.configuration;

import static org.exquisite.protege.model.configuration.SearchConfiguration.CostEstimator;
import static org.exquisite.protege.model.configuration.SearchConfiguration.DiagnosisEngineType;
import static org.exquisite.protege.model.configuration.SearchConfiguration.RM;
import static org.exquisite.protege.model.configuration.SearchConfiguration.SortCriterion;


/**
 * The default preference values for SearchConfiguration.
 */
public class DefaultConfiguration {

    public static DiagnosisEngineType getDefaultDiagnosisEngineType() { return DiagnosisEngineType.HSTree; }

    public static Integer getDefaultNumOfLeadingDiags() { return 9; }

    public static Boolean getDefaultReduceIncoherency() { return true; }

    public static Boolean getDefaultExtractModules() { return false; }

    public static Integer getDefaultMinimalQueries() { return 1; }

    public static Integer getDefaultMaximalQueries() { return 1; }

    public static Boolean getDefaultEnrichQuery() { return true; }

    public static SortCriterion getDefaultSortCriterion() { return SortCriterion.MINCARD; }

    public static RM getDefaultRM() { return RM.ENT; }

    public static Double getDefaultEntropyThreshold() { return 0.05; }

    public static Double getDefaultCardinalityThreshold() { return 0.00; }

    public static Double getDefaultCautiousParameter() { return 0.4; }

    public static CostEstimator getDefaultCostEstimator() { return CostEstimator.CARD; }


    static SearchConfiguration getDefaultConfig() {
        SearchConfiguration conf = new SearchConfiguration();

        // diagnosis calculation
        conf.engineType = getDefaultDiagnosisEngineType();
        conf.numOfLeadingDiags = getDefaultNumOfLeadingDiags();
        conf.reduceIncoherency = getDefaultReduceIncoherency();
        conf.extractModules = getDefaultExtractModules();

        // calculate query
        conf.minimalQueries = getDefaultMinimalQueries();
        conf.maximalQueries = getDefaultMaximalQueries();
        conf.enrichQuery = getDefaultEnrichQuery();
        conf.sortCriterion = getDefaultSortCriterion();
        conf.rm = getDefaultRM();
        conf.entropyThreshold = getDefaultEntropyThreshold();
        conf.cardinalityThreshold = getDefaultCardinalityThreshold();
        conf.cautiousParameter = getDefaultCautiousParameter();

        // Preferences measures
        conf.costEstimator = getDefaultCostEstimator();

        return conf;
    }

}
