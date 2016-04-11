package org.exquisite.core;

import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kostya on 21-Mar-16.
 */
public class TestReasoner extends TestExquisiteOWLReasoner {

    private static final Logger logger = LoggerFactory.getLogger(TestReasoner.class);

    @Test
    public void testECAI() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/ecai2010.owl");
        this.testConsistency(reasoner, false);
    }

    @Test
    public void testRunningExample() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/running_example_annotated.owl");
        this.testConsistency(reasoner, false);
    }

    @Test
    public void testEconomy() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/Economy-SDA.owl");
        this.testConsistency(reasoner, true);
    }

    @Test
    public void testTransportation() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/Transportation-SDA.owl");
        this.testConsistency(reasoner, true);
    }

    @Test
    public void testUniversity() throws OWLOntologyCreationException, DiagnosisException {
        ExquisiteOWLReasoner reasoner = loadOntology("ontologies/University.owl");
        this.testConsistency(reasoner, true);
    }

}
