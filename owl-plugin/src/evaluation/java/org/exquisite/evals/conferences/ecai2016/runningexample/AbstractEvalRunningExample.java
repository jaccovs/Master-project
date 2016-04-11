package org.exquisite.evals.conferences.ecai2016.runningexample;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.evals.conferences.ecai2016.AbstractEval;
import org.junit.BeforeClass;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wolfi
 */
abstract public class AbstractEvalRunningExample extends AbstractEval {

    protected static Map<Set<String>,BigDecimal> mapping = new HashMap<>();

    @BeforeClass
    public static void init() {
        mapping.put(getSet("A", "C", "E", "F", "M", "X", "Z"),  new BigDecimal("0.04"));
        mapping.put(getSet("C", "F", "H", "M", "X", "Z"),       new BigDecimal("0.07"));
        mapping.put(getSet("E", "F", "H", "K", "X"),            new BigDecimal("0.33"));
        mapping.put(getSet("B", "C", "F", "H", "X"),            new BigDecimal("0.14"));
        mapping.put(getSet("E", "F", "H", "M", "X"),            new BigDecimal("0.01"));
        mapping.put(getSet("A", "C", "F", "G", "H", "M", "Z"),  new BigDecimal("0.41"));
    }

    @Override
    protected void setDiagnosesMeasures(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            Set<String> axiomsInDiagnoses = new HashSet<>();
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                for (OWLClassExpression expression : axiom.getNestedClassExpressions()) {
                    if (expression instanceof OWLClass) {
                        final String s = ((OWLClass) expression).getIRI().getRemainder().get();
                        axiomsInDiagnoses.add(s);
                    }
                }
            }

            BigDecimal measure = mapping.get(axiomsInDiagnoses);
            if (measure != null) {
                diagnosis.setMeasure(measure);
                System.out.println("set measure " + measure + " for diagnosis " + axiomsInDiagnoses);
            }
        }
    }

    @Override
    protected String getOntology() {
        return "ontologies/running_example_annotated.owl";
    }

    @Override
    protected int getMaxNumberOfDiagnoses() {
        return 9;
    }
}
