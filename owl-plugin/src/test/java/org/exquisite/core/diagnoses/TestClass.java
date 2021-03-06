package org.exquisite.core.diagnoses;

import org.exquisite.core.AbstractTest;
import org.exquisite.core.DiagnosisException;
import org.exquisite.core.engines.*;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.exquisite.core.utils.OWLUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Set;

import static junit.framework.Assert.*;

public class TestClass extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(TestExquisiteOWLReasonerWithOntologies.class);

    public  Set<Diagnosis<OWLLogicalAxiom>> calculateDiagnoses(MyOntology ont) throws DiagnosisException, OWLOntologyCreationException {
        ExquisiteOWLReasoner reasoner = createReasoner(ont.getOntology(), false, false);
        IDiagnosisEngine<OWLLogicalAxiom> diagnosisEngine = new InverseDiagnosisEngine<>(reasoner);
        diagnosisEngine.resetEngine();
        diagnosisEngine.setMaxNumberOfDiagnoses(80);
        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = diagnosisEngine.calculateDiagnoses();

        logger.debug("based on " + diagnosisEngine.getSolver().getDiagnosisModel());

        return diagnoses;

    }


}
