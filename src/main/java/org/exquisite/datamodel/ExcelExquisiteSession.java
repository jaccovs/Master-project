package org.exquisite.datamodel;

import org.exquisite.diagnosis.models.ConstraintsDiagnosisModel;
import org.exquisite.diagnosis.parallelsearch.SearchStrategies;

import java.util.Map;

/**
 * A class for holding various models used during a Excel Exquisite session.
 *
 * @author Arash
 */
public class ExcelExquisiteSession<T> extends DiagnosisModel<T> {
    public ExquisiteGraph<String> graph;
    public ExquisiteAppXML appXML;
    private Map<SearchStrategies, Integer> strategies;

    public ExcelExquisiteSession(ExquisiteAppXML appXML, ExquisiteGraph<String> graph, ConstraintsDiagnosisModel<T>
            model) {
        super(model);
        this.appXML = appXML;
        this.graph = graph;
    }

    public ExcelExquisiteSession(ExquisiteAppXML appXML) {
        this();
        this.appXML = appXML;
    }

    public ExcelExquisiteSession() {
        super();
        this.appXML = new ExquisiteAppXML();
        this.graph = new ExquisiteGraph<>();
    }

    public void updateStrategies(SearchStrategies strategy) {
        if (!this.strategies.containsKey(strategy)) {
            this.strategies.put(strategy, 0);
        }
        this.strategies.put(strategy, strategies.get(strategy) + 1);
        if (strategies.get(strategy) >= this.getConfiguration().successTime) {
            this.getConfiguration().searchStrategy = strategy;
        }
    }
}
