package org.exquisite.datamodel;

import org.exquisite.data.DiagnosisConfiguration;
import org.exquisite.diagnosis.models.DiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for holding various models used during a Exquisite session.
 *
 * @author Arash
 */
public class ExquisiteSession<T> {
    public DiagnosisModel<T> diagnosisModel;
    public ExquisiteGraph<String> graph;
    public ExquisiteAppXML appXML;
    public DiagnosisConfiguration config;
    public Map<SearchStrategies, Integer> strategies;

    public ExquisiteSession(ExquisiteAppXML appXML, ExquisiteGraph<String> graph, DiagnosisModel<T> model) {
        this();
        this.appXML = appXML;
        this.graph = graph;
        this.diagnosisModel = model;
    }

    public ExquisiteSession(ExquisiteAppXML appXML) {
        this();
        this.appXML = appXML;
    }

    public ExquisiteSession() {
        this.appXML = new ExquisiteAppXML();
        this.graph = new ExquisiteGraph<String>();
        this.diagnosisModel = new DiagnosisModel<>();
        this.config = new DiagnosisConfiguration();
        this.strategies = new HashMap<SearchStrategies, Integer>();
    }

    public void UpdateStrategies(SearchStrategies strategy) {
        if (!this.strategies.containsKey(strategy)) {
            this.strategies.put(strategy, 0);
        }
        this.strategies.put(strategy, strategies.get(strategy) + 1);
        if (strategies.get(strategy) >= this.config.successTime) {
            this.config.searchStrategy = strategy;
        }
    }
}
