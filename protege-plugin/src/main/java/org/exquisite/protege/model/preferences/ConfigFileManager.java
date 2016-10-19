package org.exquisite.protege.model.preferences;

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
     * Additionally does some input validation.
     *
     * @return The currently used SearchConfiguration.
     */
    static DebuggerConfiguration readConfiguration() {
        File confFile = getConfFile();

        Properties properties = new Properties();
        DebuggerConfiguration c = new DebuggerConfiguration();

        if (!confFile.exists()) {
            writeConfiguration(DefaultPreferences.getDefaultConfig());
            return DefaultPreferences.getDefaultConfig();
        }

        try {
            properties.load(new FileInputStream(confFile));
        } catch (IOException e) {
            logger.error("Cannot load " + confFile + " to load properties", e);
            throw new DiagnosisRuntimeException("Error when loading preferences from " + confFile, e);
        }

        // fault localization preferences
        c.engineType = InputValidator.parseEngineType((String) properties.get("enginetype"));
        c.numOfLeadingDiags = InputValidator.parseInt(properties,"numOfLeadingDiags", DefaultPreferences.getMinNumOfLeadingDiags(), DefaultPreferences.getMaxNumOfLeadingDiags(), DefaultPreferences.getDefaultNumOfLeadingDiags());
        c.reduceIncoherency = InputValidator.parseBoolean(properties,"reduceIncoherency", DefaultPreferences.getDefaultReduceIncoherency());
        c.extractModules = InputValidator.parseBoolean(properties,"extractModules", DefaultPreferences.getDefaultExtractModules());

        // query computation preferences
        c.enrichQuery = InputValidator.parseBoolean(properties, "enrichquery", DefaultPreferences.getDefaultEnrichQuery());
        c.sortCriterion = InputValidator.parseSortCriterion((String) properties.get("sortcriterion"));
        c.rm = InputValidator.parseRM((String) properties.get("rm"));
        c.entropyThreshold = InputValidator.parseDouble(properties, "entropythreshold", DefaultPreferences.getDefaultEntropyThreshold());
        c.cardinalityThreshold = InputValidator.parseDouble(properties, "cardinalitythreshold", DefaultPreferences.getDefaultCardinalityThreshold());
        c.cautiousParameter = InputValidator.parseDouble(properties, "cautiousparameter", DefaultPreferences.getDefaultCautiousParameter());

        // Preference measures
        c.costEstimator = InputValidator.parseCostEstimator((String) properties.get("costEstimator"));

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
    static void writeConfiguration(DebuggerConfiguration configuration) {
        File confFile = getConfFile();

        Properties properties = new Properties();

        // fault localization preferences
        properties.put("enginetype", InputValidator.validateEngineType(configuration.engineType).toString());
        properties.put("numOfLeadingDiags",
                InputValidator.validateInt(
                        configuration.numOfLeadingDiags,
                        DefaultPreferences.getMinNumOfLeadingDiags(),
                        DefaultPreferences.getMaxNumOfLeadingDiags(),
                        DefaultPreferences.getDefaultNumOfLeadingDiags()
                )
        );
        properties.put("reduceIncoherency",InputValidator.validateBoolean(configuration.reduceIncoherency, DefaultPreferences.getDefaultReduceIncoherency()).toString());
        properties.put("extractModules",InputValidator.validateBoolean(configuration.extractModules, DefaultPreferences.getDefaultExtractModules()).toString());

        // query computation preferences
        properties.put("enrichquery",InputValidator.validateBoolean(configuration.enrichQuery,DefaultPreferences.getDefaultEnrichQuery()).toString());
        properties.put("sortcriterion",InputValidator.validateSortCriterion(configuration.sortCriterion).toString());
        properties.put("rm",InputValidator.validateRM(configuration.rm).toString());
        properties.put("entropythreshold",
                InputValidator.validateDouble(
                        configuration.entropyThreshold,
                        DefaultPreferences.getMinEntropyThreshold(),
                        DefaultPreferences.getMaxEntropyThreshold(),
                        DefaultPreferences.getDefaultEntropyThreshold()
                )
        );
        properties.put("cardinalitythreshold",
                InputValidator.validateDouble(
                        configuration.cardinalityThreshold,
                        DefaultPreferences.getMinCardinalityThreshold(),
                        DefaultPreferences.getMaxCardinalityThreshold(),
                        DefaultPreferences.getDefaultCardinalityThreshold()
                )
        );
        properties.put("cautiousparameter",
                InputValidator.validateDouble(
                        configuration.cautiousParameter,
                        DefaultPreferences.getMinCautiousParameter(),
                        DefaultPreferences.getMaxCautiousParameter(),
                        DefaultPreferences.getDefaultCautiousParameter()
                )
        );

        // Preference measures
        properties.put("costEstimator",InputValidator.validateCostEstimator(configuration.costEstimator).toString());

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


}
