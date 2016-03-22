package org.exquisite.protege.model.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 21.05.12
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
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
        c.searchType = parseSearchType((String) properties.get("searchType"));
        c.treeType = parseTreeType((String) properties.get("treeType"));
        c.qss = parseQSS((String) properties.get("qss"));
        c.reduceIncoherency = Boolean.parseBoolean((String) properties.get("reduceIncoherency"));
        c.minimizeQuery = Boolean.parseBoolean((String) properties.get("minimizeQuery"));
        c.calcAllDiags = Boolean.parseBoolean((String) properties.get("calcAllDiags"));

        c.inclEntSubClass = Boolean.parseBoolean((String) properties.get("inclEntSubClass"));
        c.incEntClassAssert = Boolean.parseBoolean((String) properties.get("incEntClassAssert"));
        c.incEntEquivClass = Boolean.parseBoolean((String) properties.get("incEntEquivClass"));
        c.incEntDisjClasses = Boolean.parseBoolean((String) properties.get("incEntDisjClasses"));
        c.incEntPropAssert = Boolean.parseBoolean((String) properties.get("incEntPropAssert"));
        c.incOntolAxioms = Boolean.parseBoolean((String) properties.get("incOntolAxioms"));
        c.incAxiomsRefThing = Boolean.parseBoolean((String) properties.get("incAxiomsRefThing"));
        c.entailmentCalThres = Double.parseDouble((String) properties.get("entailmentCalThres"));

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return c;
    }

    private static SearchConfiguration.SearchType parseSearchType(String searchType) {
        for (SearchConfiguration.SearchType type : SearchConfiguration.SearchType.values())
            if (type.toString().equals(searchType))
                return type;
        throw new IllegalStateException("Unknown Search Type");
    }

    private static SearchConfiguration.TreeType parseTreeType(String treeType) {
        for (SearchConfiguration.TreeType type : SearchConfiguration.TreeType.values())
            if (type.toString().equals(treeType))
                return type;
        throw new IllegalStateException("Unknown Tree Type");
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
        conf.searchType = SearchConfiguration.SearchType.UNIFORM_COST;
        conf.treeType = SearchConfiguration.TreeType.REITER;
        conf.numOfLeadingDiags = 9;
        conf.qss = SearchConfiguration.QSS.MINSCORE;
        conf.reduceIncoherency = false;
        conf.minimizeQuery = true;
        conf.calcAllDiags = false;
        conf.inclEntSubClass = true;
        conf.incEntClassAssert = true;
        conf.incEntEquivClass = false;
        conf.incEntDisjClasses = false;
        conf.incEntPropAssert = false;
        conf.incOntolAxioms = false;
        conf.incAxiomsRefThing = false;
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
        properties.put("searchType",configuration.searchType.toString());
        properties.put("treeType",configuration.treeType.toString());
        properties.put("numOfLeadingDiags",configuration.numOfLeadingDiags.toString());
        properties.put("qss",configuration.qss.toString());
        properties.put("reduceIncoherency",configuration.reduceIncoherency.toString());
        properties.put("minimizeQuery",configuration.minimizeQuery.toString());
        properties.put("calcAllDiags",configuration.calcAllDiags.toString());

        properties.put("inclEntSubClass",configuration.inclEntSubClass.toString());
        properties.put("incEntClassAssert",configuration.incEntClassAssert.toString());
        properties.put("incEntEquivClass",configuration.incEntEquivClass.toString());
        properties.put("incEntDisjClasses",configuration.incEntDisjClasses.toString());
        properties.put("incEntPropAssert",configuration.incEntPropAssert.toString());
        properties.put("incOntolAxioms",configuration.incOntolAxioms.toString());
        properties.put("incAxiomsRefThing",configuration.incAxiomsRefThing.toString());
        properties.put("entailmentCalThres",configuration.entailmentCalThres.toString());

        try {
            properties.store(new FileOutputStream(confFile),null);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
