package org.exquisite.core;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

/**
 * Created by kostya on 21-Mar-16.
 */
public class TestReasoner extends AbstractTest {


    @Test
    public void testECAI() throws OWLOntologyCreationException, DiagnosisException {

        File ontology = new File(ClassLoader.getSystemResource("ontologies/ecai2010.owl").getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);

        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();
        reasoner.isConsistent(diagnosisModel.getPossiblyFaultyStatements());

    }


}
