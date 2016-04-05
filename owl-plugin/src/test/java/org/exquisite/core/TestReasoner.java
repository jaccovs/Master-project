package org.exquisite.core;

import org.exquisite.core.engines.InteractiveDiagnosisEngine;
import org.exquisite.core.model.Diagnosis;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.query.Answer;
import org.exquisite.core.query.IQueryAnswering;
import org.exquisite.core.query.Query;
import org.exquisite.core.query.qc.heuristic.HeuristicQC;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * Created by kostya on 21-Mar-16.
 */
public class TestReasoner extends AbstractTest {

    private static final Logger logger = LoggerFactory.getLogger(TestReasoner.class);

    @Test
    public void testECAI() throws OWLOntologyCreationException, DiagnosisException {

        File ontology = new File(ClassLoader.getSystemResource("ontologies/ecai2010.owl").getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);

        DiagnosisModel<OWLLogicalAxiom> diagnosisModel = reasoner.getDiagnosisModel();
        boolean b = reasoner.isConsistent(diagnosisModel.getPossiblyFaultyFormulas());


    }

    // deactivated test case to simulate interactive diagnosisengine with heuristic query computation and answering, currently infinite loop
    public void testECAIWithHeuristicGC() throws OWLOntologyCreationException, DiagnosisException {
        File ontology = new File(ClassLoader.getSystemResource("ontologies/ecai2010.owl").getFile());
        ExquisiteOWLReasoner reasoner = createReasoner(ontology);

        reasoner.setEntailmentTypes(InferenceType.DISJOINT_CLASSES, InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY);


        InteractiveDiagnosisEngine<OWLLogicalAxiom> engine = new InteractiveDiagnosisEngine<>(reasoner, new IQueryAnswering<OWLLogicalAxiom>() {
            @Override
            public Answer<OWLLogicalAxiom> getAnswer(Query<OWLLogicalAxiom> query) {
                Answer<OWLLogicalAxiom> answer = new Answer<>();
                int i = 0;
                for (OWLLogicalAxiom axiom : query.formulas) {
                    logger.info(axiom + "? (y/n): " + (i%2));

                    if (i++%2 == 0)
                        answer.negative.add(axiom);
                    else
                        answer.positive.add(axiom);

                }

                return answer;
            }
        });
        ((HeuristicQC<OWLLogicalAxiom>)engine.getQueryComputation()).getConfig().setEnrichQueries(true);


        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = engine.calculateDiagnoses();

    }


}
