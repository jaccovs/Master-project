package org.exquisite.core;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * @author wolfi
 */
public class TestExquisiteOWLReasoner extends AbstractTest {

    /**
     *
     * @param ontologyName
     * @return
     * @throws OWLOntologyCreationException
     * @throws DiagnosisException
     */
    public ExquisiteOWLReasoner loadOntology(String ontologyName) throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource(ontologyName).getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);
        assertNotNull(reasoner);
        return reasoner;
    }

    /**
     *
     * @param reasoner
     * @param isConsistent
     */
    public void testConsistency(ExquisiteOWLReasoner reasoner, boolean isConsistent) {
        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();
        boolean b = reasoner.isConsistent(diagnosisModel.getPossiblyFaultyFormulas());
        if (isConsistent) assertTrue(b);
        else assertFalse(b);
    }
}
