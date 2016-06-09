package org.exquisite.protege.model.configuration;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A ConfigFileManager is responsible for correct loading and saving of the preferences for the ontology debugger.
 */
public class ConfigFileManager {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigFileManager.class);

    /**
     * The name of the properties file. Usually can found in the .protege subdirectory of the user's home directory.
     */
    private static final String EXQUISITEDEBUGGER_PROPERTIES = "exquisitedebugger.properties";


    private static SearchConfiguration.DiagnosisEngineType parseEngineType(String engineType) {
        for (SearchConfiguration.DiagnosisEngineType type : SearchConfiguration.DiagnosisEngineType.values())
            if (type.toString().equals(engineType))
                return type;
        throw new IllegalStateException("Unknown DiagnosisEngine Type");
    }

    private static SearchConfiguration.RM parseRM(String rm) {
        for (SearchConfiguration.RM type : SearchConfiguration.RM.values())
            if (type.toString().equals(rm))
                return type;
        throw new IllegalStateException("Unknown Requirements Measurement ");
    }

    private static SearchConfiguration.SortCriterion parseSortCriterion(String sortcriterion) {
        for (SearchConfiguration.SortCriterion type : SearchConfiguration.SortCriterion.values())
            if (type.toString().equals(sortcriterion))
                return type;
        throw new IllegalStateException("Unknown Sortcriterion");
    }

    private static SearchConfiguration getDefaultConfig() {
        SearchConfiguration conf = new SearchConfiguration();

        // diagnosis calculation
        conf.engineType = SearchConfiguration.DiagnosisEngineType.HSTree;
        conf.numOfLeadingDiags = 9;
        conf.reduceIncoherency = true;
        conf.extractModules = false;

        // calculate query
        conf.minimalQueries = SearchConfiguration.DEFAULT_MINIMAL_QUERIES;
        conf.maximalQueries = SearchConfiguration.DEFAULT_MAXIMAL_QUERIES;
        conf.enrichQuery = true;
        conf.sortCriterion = SearchConfiguration.SortCriterion.MINCARD;
        conf.rm = SearchConfiguration.RM.ENT;
        conf.entropyThreshold = 0.05;
        conf.cardinalityThreshold = 0.00;
        conf.cautiousParameter = 0.4;

        return conf;
    }

    public static SearchConfiguration readConfiguration() {
        File confFile = getConfFile();

        Properties properties = new Properties();
        SearchConfiguration c = new SearchConfiguration();

        if (!confFile.exists()) {
            writeConfiguration(getDefaultConfig());
            return getDefaultConfig();
        }

        try {
            properties.load(new FileInputStream(confFile));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // diagnosis preferences
        c.engineType = parseEngineType((String) properties.get("enginetype"));
        c.numOfLeadingDiags = Integer.parseInt((String) properties.get("numOfLeadingDiags"));
        c.reduceIncoherency = Boolean.parseBoolean((String) properties.get("reduceIncoherency"));
        c.extractModules = Boolean.parseBoolean((String) properties.get("extractModules"));

        // query computation
        c.minimalQueries = Integer.parseInt((String) properties.get("minimalQueries"));
        c.maximalQueries = Integer.parseInt((String) properties.get("maximalQueries"));
        c.enrichQuery = Boolean.parseBoolean((String) properties.get("enrichquery"));
        c.sortCriterion = parseSortCriterion((String) properties.get("sortcriterion"));
        c.rm = parseRM((String) properties.get("rm"));
        c.entropyThreshold = Double.parseDouble((String) properties.get("entropythreshold"));
        c.cardinalityThreshold = Double.parseDouble((String) properties.get("cardinalitythreshold"));
        c.cautiousParameter = Double.parseDouble((String) properties.get("cautiousparameter"));

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return c;
    }

    public static void writeConfiguration (SearchConfiguration configuration) {
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

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            logger.error("Cannot use " + confFile+ " to save properties");
            throw new IllegalStateException("Cannot use " + confFile+ " to save properties");
        }
    }

    /**
     * A helper method to find the config file and correctly locate the preference file for protege ontology debugger
     * plugin.
     * @return The file object where the preferences are found.
     */
    private static File getConfFile() {
        String userHome = System.getProperty("user.home");
        if(userHome == null)
            throw new IllegalStateException("user home directory is null");

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
            throw new IllegalStateException("Cannot use user home directory " + userHome + " to save properties");
        }
    }


}
