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
        File confFile = new File(new File(userHome), "querydebugger.properties");

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

        c.aBoxInBG = Boolean.parseBoolean((String) properties.get("aBoxInBG"));
        c.tBoxInBG = Boolean.parseBoolean((String) properties.get("tBoxInBG"));
        c.numOfLeadingDiags = Integer.parseInt((String) properties.get("numOfLeadingDiags"));
        c.engineType = parseEngineType((String) properties.get("enginetype"));
        c.qss = parseQSS((String) properties.get("qss"));
        c.reduceIncoherency = Boolean.parseBoolean((String) properties.get("reduceIncoherency"));
        c.extractModules = Boolean.parseBoolean((String) properties.get("extractModules"));
        c.minimizeQuery = Boolean.parseBoolean((String) properties.get("minimizeQuery"));
        c.calcAllDiags = Boolean.parseBoolean((String) properties.get("calcAllDiags"));

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

        c.entailmentCalThres = Double.parseDouble((String) properties.get("entailmentCalThres"));

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

    private static SearchConfiguration.QSS parseQSS (String qss) {
        for (SearchConfiguration.QSS type : SearchConfiguration.QSS.values())
            if (type.toString().equals(qss))
                return type;
        throw new IllegalStateException("Unknown QSS ");
    }

    public static SearchConfiguration getDefaultConfig() {
        SearchConfiguration conf = new SearchConfiguration();

        conf.aBoxInBG = true;
        conf.tBoxInBG = false;
        //conf.searchType = SearchConfiguration.SearchType.UNIFORM_COST;
        //conf.treeType = SearchConfiguration.TreeType.REITER;
        conf.engineType = SearchConfiguration.DiagnosisEngineType.Inverse;
        conf.numOfLeadingDiags = 9;
        conf.qss = SearchConfiguration.QSS.MINSCORE;
        conf.reduceIncoherency = false;
        conf.extractModules = false;
        conf.minimizeQuery = true;
        conf.calcAllDiags = false;

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

        conf.entailmentCalThres = 0.01;

        return conf;
    }

    public static void writeConfiguration (SearchConfiguration configuration) {
        String userHome = System.getProperty("user.home");
        if(userHome == null)
            throw new IllegalStateException("user home directory is null");
        File confFile = new File(new File(userHome), "querydebugger.properties");

        Properties properties = new Properties();

        properties.put("aBoxInBG",configuration.aBoxInBG.toString());
        properties.put("tBoxInBG",configuration.tBoxInBG.toString());
        properties.put("enginetype", configuration.engineType.toString());
        properties.put("numOfLeadingDiags",configuration.numOfLeadingDiags.toString());
        properties.put("qss",configuration.qss.toString());
        properties.put("reduceIncoherency",configuration.reduceIncoherency.toString());
        properties.put("extractModules",configuration.extractModules.toString());
        properties.put("minimizeQuery",configuration.minimizeQuery.toString());
        properties.put("calcAllDiags",configuration.calcAllDiags.toString());

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

        properties.put("entailmentCalThres",configuration.entailmentCalThres.toString());

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
