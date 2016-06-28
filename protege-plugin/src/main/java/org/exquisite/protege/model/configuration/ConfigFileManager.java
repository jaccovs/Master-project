package org.exquisite.protege.model.configuration;

import org.exquisite.core.DiagnosisRuntimeException;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A ConfigFileManager is responsible for correct loading and saving of the preferences for the ontology debugger.
 */
class ConfigFileManager {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigFileManager.class);

    /**
     * The name of the properties file. Usually can found in the .protege subdirectory of the user's home directory.
     */
    private static final String EXQUISITEDEBUGGER_PROPERTIES = "exquisitedebugger.properties";

    /**
     * Read previously used preference settings or search configuration into the currently used search configuration.
     * @return The currently used SearchConfiguration.
     */
    static SearchConfiguration readConfiguration() {
        File confFile = getConfFile();

        Properties properties = new Properties();
        SearchConfiguration c = new SearchConfiguration();

        if (!confFile.exists()) {
            writeConfiguration(DefaultConfiguration.getDefaultConfig());
            return DefaultConfiguration.getDefaultConfig();
        }

        try {
            properties.load(new FileInputStream(confFile));
        } catch (IOException e) {
            logger.error("Cannot load " + confFile + " to load properties", e);
            throw new DiagnosisRuntimeException("Error when loading preferences from " + confFile, e);
        }

        // diagnosis preferences
        c.engineType = parseEngineType((String) properties.get("enginetype"));
        c.numOfLeadingDiags = parseInt(properties,"numOfLeadingDiags",DefaultConfiguration.getDefaultNumOfLeadingDiags());
        c.reduceIncoherency = parseBoolean(properties,"reduceIncoherency",DefaultConfiguration.getDefaultReduceIncoherency());
        c.extractModules = parseBoolean(properties,"extractModules",DefaultConfiguration.getDefaultExtractModules());

        // query computation
        c.minimalQueries = parseInt(properties,"minimalQueries",DefaultConfiguration.getDefaultMinimalQueries());
        c.maximalQueries = parseInt(properties,"maximalQueries",DefaultConfiguration.getDefaultMaximalQueries());
        c.enrichQuery = parseBoolean(properties, "enrichquery", DefaultConfiguration.getDefaultEnrichQuery());
        c.sortCriterion = parseSortCriterion((String) properties.get("sortcriterion"));
        c.rm = parseRM((String) properties.get("rm"));
        c.entropyThreshold = parseDouble(properties, "entropythreshold", DefaultConfiguration.getDefaultEntropyThreshold());
        c.cardinalityThreshold = parseDouble(properties, "cardinalitythreshold", DefaultConfiguration.getDefaultCardinalityThreshold());
        c.cautiousParameter = parseDouble(properties, "cautiousparameter", DefaultConfiguration.getDefaultCautiousParameter());

        // Preference measures
        c.costEstimator = parseCostEstimator((String) properties.get("costEstimator"));

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            logger.error("Cannot use " + confFile+ " to save properties", e);
            throw new DiagnosisRuntimeException("Error when saving preferences to " + confFile, e);
        }

        return c;
    }

    /**
     * Save the currently used preference settings or search configuration into a properties file.
     * @param configuration The currently used search configuration.
     */
    static void writeConfiguration(SearchConfiguration configuration) {
        File confFile = getConfFile();

        Properties properties = new Properties();

        // diagnosis calculation
        properties.put("enginetype", configuration.engineType.toString());
        properties.put("numOfLeadingDiags",configuration.numOfLeadingDiags.toString());
        properties.put("reduceIncoherency",configuration.reduceIncoherency.toString());
        properties.put("extractModules",configuration.extractModules.toString());

        // query calculation
        properties.put("minimalQueries",configuration.minimalQueries.toString());
        properties.put("maximalQueries",configuration.maximalQueries.toString());
        properties.put("enrichquery",configuration.enrichQuery.toString());
        properties.put("sortcriterion", configuration.sortCriterion.toString());
        properties.put("rm",configuration.rm.toString());
        properties.put("entropythreshold",configuration.entropyThreshold.toString());
        properties.put("cardinalitythreshold",configuration.cardinalityThreshold.toString());
        properties.put("cautiousparameter",configuration.cautiousParameter.toString());

        // Preference measures
        properties.put("costEstimator",configuration.costEstimator.toString());

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            logger.error("Cannot use " + confFile+ " to save properties");
            throw new DiagnosisRuntimeException("Cannot use " + confFile+ " to save properties");
        }
    }

    /**
     * A helper method to find the config file and correctly locate the preference file for protege ontology debugger
     * plugin.
     * @return The file object where the preferences are found.
     */
    private static File getConfFile() {
        String userHome = System.getProperty("user.home");
        if(userHome == null) {
            logger.error("user home directory is null");
            throw new DiagnosisRuntimeException("user home directory is null");
        }

        final File userHomeDir = new File(userHome);

        if (userHomeDir.exists() && userHomeDir.isDirectory() && userHomeDir.canWrite()) {
            File prefHomeDir = new File(userHome + File.separator + ".protege" + File.separator + "org.exquisite.protege" + File.separator);
            if (!prefHomeDir.exists()) {
                try {
                    boolean isCreated = prefHomeDir.mkdir();
                    assert isCreated;
                } catch (SecurityException e) {
                    logger.error("Cannot create subdirectory .protege" + File.separator + "org.exquisite.protege in user home directory " + userHome + ". Fall back to " + userHome, e);
                    return new File(userHomeDir, EXQUISITEDEBUGGER_PROPERTIES);
                }
            }
            return new File(prefHomeDir,  EXQUISITEDEBUGGER_PROPERTIES);
        } else {
            logger.error("Cannot use user home directory " + userHome + " to save properties");
            throw new DiagnosisRuntimeException("Cannot use user home directory " + userHome + " to save properties");
        }
    }

    /**
     * Parse and read diagnosis engine type property.
     *
     * @param engineType the string value.
     * @return The engine type.
     */
    private static SearchConfiguration.DiagnosisEngineType parseEngineType(String engineType) {
        for (SearchConfiguration.DiagnosisEngineType type : SearchConfiguration.DiagnosisEngineType.values())
            if (type.toString().equals(engineType))
                return type;
        logger.warn("Unknown DiagnosisEngine Type " + engineType + ". Applying default value.");
        return DefaultConfiguration.getDefaultDiagnosisEngineType();
    }

    /**
     * Parse and read requirements measurement property.
     *
     * @param rm the string value.
     * @return The RM (requirements measurement).
     */
    private static SearchConfiguration.RM parseRM(String rm) {
        for (SearchConfiguration.RM type : SearchConfiguration.RM.values())
            if (type.toString().equals(rm))
                return type;
        logger.warn("Unknown Requirements Measurement " + rm + ". Applying default value.");
        return DefaultConfiguration.getDefaultRM();
    }

    /**
     * Parse and read sort criterion property.
     *
     * @param sortcriterion the string value.
     * @return The sort criterion.
     */
    private static SearchConfiguration.SortCriterion parseSortCriterion(String sortcriterion) {
        for (SearchConfiguration.SortCriterion type : SearchConfiguration.SortCriterion.values())
            if (type.toString().equals(sortcriterion))
                return type;
        logger.warn("Unknown Sortcriterion " + sortcriterion + ". Applying default value.");
        return DefaultConfiguration.getDefaultSortCriterion();
    }

    /**
     * Parse and read cost estimator property.
     *
     * @param costEstimator the string value.
     * @return The cost estimator.
     */
    private static SearchConfiguration.CostEstimator parseCostEstimator(String costEstimator) {
        for (SearchConfiguration.CostEstimator type : SearchConfiguration.CostEstimator.values())
            if (type.toString().equals(costEstimator))
                return type;
        logger.warn("Unknown Cost Estimator " + costEstimator + ". Applying default value.");
        return DefaultConfiguration.getDefaultCostEstimator();
    }

    /**
     * Helper method to parse integer property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    private static Integer parseInt(Properties properties, String key, Integer defaultValue) {
        String value = (String) properties.get(key);
        if (value == null) return defaultValue;
        return Integer.parseInt(value);
    }

    /**
     * Helper method to parse boolean property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    private static Boolean parseBoolean(Properties properties, String key, Boolean defaultValue) {
        String value = (String) properties.get(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    /**
     * Helper method to parse double property with fall back to default value.
     *
     * @param properties Properties.
     * @param key Key of property.
     * @param defaultValue Default value.
     * @return Value of property.
     */
    private static Double parseDouble(Properties properties, String key, Double defaultValue) {
        String value = (String) properties.get(key);
        if (value == null) return defaultValue;
        return Double.parseDouble(value);
    }

}
