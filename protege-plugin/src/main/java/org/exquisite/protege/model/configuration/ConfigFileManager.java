package org.exquisite.protege.model.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileManager {

    public static SearchConfiguration readConfiguration() {
        String userHome = System.getProperty("user.home");
        if(userHome == null)
            throw new IllegalStateException("user home directory is null");
        File confFile = new File(new File(userHome), "exquisitedebugger.properties");

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
        c.enrichQuery = Boolean.parseBoolean((String) properties.get("enrichquery"));
        c.sortCriterion = parseSortCriterion((String) properties.get("sortcriterion"));
        c.rm = parseRM((String) properties.get("rm"));
        c.entropyThreshold = Double.parseDouble((String) properties.get("entropythreshold"));
        c.cardinalityThreshold = Double.parseDouble((String) properties.get("cardinalitythreshold"));
        c.cautiousParameter = Double.parseDouble((String) properties.get("cautiousparameter"));

        // entailmenttypes
        c.incInferenceTypeClassHierarchy = Boolean.parseBoolean((String) properties.get("incclasshierarchy"));
        c.incInferenceTypeDisjointClasses = Boolean.parseBoolean((String) properties.get("incdisjointclasses"));
        c.incInferenceTypeObjectPropertyHierarchy = Boolean.parseBoolean((String) properties.get("incobjectpropertyhierarchy"));
        c.incInferenceTypeDataPropertyHierarchy = Boolean.parseBoolean((String) properties.get("incdatapropertyhierarchy"));
        c.incInferenceTypeClassAssertions = Boolean.parseBoolean((String) properties.get("incclassassertions"));
        c.incInferenceTypeObjectPropertyAssertions = Boolean.parseBoolean((String) properties.get("incobjectpropertyassertions"));
        c.incInferenceTypeDataPropertyAssertions = Boolean.parseBoolean((String) properties.get("incdatapropertyassertions"));
        c.incInferenceTypeSameIndividual = Boolean.parseBoolean((String) properties.get("incsameindividual"));
        c.incInferenceTypeDifferentIndividuals = Boolean.parseBoolean((String) properties.get("incdifferentindividuals"));


        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return c;
    }

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

    public static SearchConfiguration getDefaultConfig() {
        SearchConfiguration conf = new SearchConfiguration();

        // diagnosis calculation
        conf.engineType = SearchConfiguration.DiagnosisEngineType.Inverse;
        conf.numOfLeadingDiags = 9;
        conf.reduceIncoherency = false;
        conf.extractModules = false;

        // calculate query
        conf.enrichQuery = true;
        conf.sortCriterion = SearchConfiguration.SortCriterion.MINCARD;
        conf.rm = SearchConfiguration.RM.ENT;
        conf.entropyThreshold = 0.05;
        conf.cardinalityThreshold = 0.00;
        conf.cautiousParameter = 0.4;

        // entailmenttypes
        conf.incInferenceTypeClassHierarchy = true;
        conf.incInferenceTypeDisjointClasses = true;
        conf.incInferenceTypeObjectPropertyHierarchy = false;
        conf.incInferenceTypeDataPropertyHierarchy = false;
        conf.incInferenceTypeClassAssertions = false;
        conf.incInferenceTypeObjectPropertyAssertions = false;
        conf.incInferenceTypeDataPropertyAssertions = false;
        conf.incInferenceTypeSameIndividual = false;
        conf.incInferenceTypeDifferentIndividuals = false;

        return conf;
    }

    public static void writeConfiguration (SearchConfiguration configuration) {
        String userHome = System.getProperty("user.home");
        if(userHome == null)
            throw new IllegalStateException("user home directory is null");
        File confFile = new File(new File(userHome),  "exquisitedebugger.properties");

        Properties properties = new Properties();

        // diagnosis calculation
        properties.put("enginetype", configuration.engineType.toString());
        properties.put("numOfLeadingDiags",configuration.numOfLeadingDiags.toString());
        properties.put("reduceIncoherency",configuration.reduceIncoherency.toString());
        properties.put("extractModules",configuration.extractModules.toString());

        // query calculation
        properties.put("enrichquery",configuration.enrichQuery.toString());
        properties.put("sortcriterion", configuration.sortCriterion.toString());
        properties.put("rm",configuration.rm.toString());
        properties.put("entropythreshold",configuration.entropyThreshold.toString());
        properties.put("cardinalitythreshold",configuration.cardinalityThreshold.toString());
        properties.put("cautiousparameter",configuration.cautiousParameter.toString());


        // entailmenttypes
        properties.put("incclasshierarchy",configuration.incInferenceTypeClassHierarchy.toString());
        properties.put("incdisjointclasses",configuration.incInferenceTypeDisjointClasses.toString());
        properties.put("incobjectpropertyhierarchy",configuration.incInferenceTypeObjectPropertyHierarchy.toString());
        properties.put("incdatapropertyhierarchy",configuration.incInferenceTypeDataPropertyHierarchy.toString());
        properties.put("incclassassertions",configuration.incInferenceTypeClassAssertions.toString());
        properties.put("incobjectpropertyassertions",configuration.incInferenceTypeObjectPropertyAssertions.toString());
        properties.put("incdatapropertyassertions",configuration.incInferenceTypeDataPropertyAssertions.toString());
        properties.put("incsameindividual",configuration.incInferenceTypeSameIndividual.toString());
        properties.put("incdifferentindividuals",configuration.incInferenceTypeDifferentIndividuals.toString());


        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
