package org.exquisite.protege.model.preferences;

import static org.exquisite.protege.model.preferences.DebuggerConfiguration.CostEstimator;
import static org.exquisite.protege.model.preferences.DebuggerConfiguration.DiagnosisEngineType;
import static org.exquisite.protege.model.preferences.DebuggerConfiguration.RM;
import static org.exquisite.protege.model.preferences.DebuggerConfiguration.SortCriterion;

/**
 * The default, max and min preference values for parameters of the Debugger.
 */
public class DefaultPreferences {

    public static DiagnosisEngineType getDefaultDiagnosisEngineType() { return DiagnosisEngineType.HSTree; }

    public static int getDefaultNumOfLeadingDiags() { return 9; }

    public static int getMinNumOfLeadingDiags() { return 2; }

    public static int getMaxNumOfLeadingDiags() { return 100; }

    public static boolean getDefaultReduceIncoherency() { return true; }

    public static boolean getDefaultExtractModules() { return false; }

    public static boolean getDefaultEnrichQuery() { return true; }

    public static SortCriterion getDefaultSortCriterion() { return SortCriterion.MINCARD; }

    public static RM getDefaultRM() { return RM.ENT; }

    public static double getDefaultEntropyThreshold() { return 0.05; }

    public static double getMinEntropyThreshold() { return 0d; }

    public static double getMaxEntropyThreshold() { return 0.5d; }

    public static double getDefaultCardinalityThreshold() { return 0.00d; }

    public static double getMinCardinalityThreshold() { return 0d; }

    public static double getMaxCardinalityThreshold() { return 100d; }

    public static double getDefaultCautiousParameter() { return 0.4; }

    public static double getMinCautiousParameter() { return 0.1d; }

    public static double getMaxCautiousParameter() { return 1d; }

    public static CostEstimator getDefaultCostEstimator() { return CostEstimator.CARD; }


    static DebuggerConfiguration getDefaultConfig() {
        final DebuggerConfiguration conf = new DebuggerConfiguration();

        // fault localization preferences
        conf.engineType = getDefaultDiagnosisEngineType();
        conf.numOfLeadingDiags = getDefaultNumOfLeadingDiags();
        conf.reduceIncoherency = getDefaultReduceIncoherency();
        conf.extractModules = getDefaultExtractModules();

        // query calculation preferences
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
