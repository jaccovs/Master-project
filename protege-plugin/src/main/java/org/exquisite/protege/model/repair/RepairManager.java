package org.exquisite.protege.model.repair;

import org.exquisite.core.DiagnosisRuntimeException;
import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.RepairOWLReasoner;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wolfi
 */
public class RepairManager {

    private Logger logger = LoggerFactory.getLogger(RepairManager.class.getCanonicalName());

    private RepairOWLReasoner reasoner;

    private DiagnosisModel<OWLLogicalAxiom> originalDiagnosisModel;

    private DiagnosisModel<OWLLogicalAxiom> diagnosisModel;

    public RepairManager(OWLOntologyManager owlOntologyManager, DiagnosisModel<OWLLogicalAxiom> dm, OWLReasonerFactory reasonerFactory, DebuggerConfiguration config) throws OWLOntologyCreationException {
        this.originalDiagnosisModel = dm;

        this.diagnosisModel = new DiagnosisModel<>();
        this.diagnosisModel.setCorrectFormulas(dm.getCorrectFormulas());
        this.diagnosisModel.setEntailedExamples(dm.getEntailedExamples());

        this.originalDiagnosisModel = new DiagnosisModel<>(originalDiagnosisModel);

        this.reasoner = new RepairOWLReasoner(this.diagnosisModel, reasonerFactory, owlOntologyManager);
        this.reasoner.setEntailmentTypes(config.getEntailmentTypes());
    }


    public OWLOntology getDebuggingOntology() {
        return reasoner.getDebuggingOntology();
    }

    public void dispose()  {
        logger.debug("disposal of " + this);
        reasoner.dispose();
    }

    public boolean isConsistent(OWLLogicalAxiom axiom) {
        final List<OWLLogicalAxiom> axioms = Collections.singletonList(axiom);
        boolean isConsistent;
        try {
            diagnosisModel.getCorrectFormulas().addAll(axioms);
            isConsistent = this.reasoner.isConsistent(Collections.emptySet());
        } catch (DiagnosisRuntimeException e) {
            isConsistent = false;
        } finally {
            diagnosisModel.getCorrectFormulas().removeAll(axioms);
        }

        return isConsistent;
    }

    public List<OWLLogicalAxiom> getEntailedTestCases(OWLLogicalAxiom axiom) {

        final List<OWLLogicalAxiom> axioms = Collections.singletonList(axiom);
        final List<OWLLogicalAxiom> entailedTestCases = new ArrayList<>();

        try {
            diagnosisModel.getCorrectFormulas().addAll(axioms);

            for (OWLLogicalAxiom testcase : this.originalDiagnosisModel.getNotEntailedExamples()) {
                if (this.reasoner.isEntailed(testcase)) {
                    entailedTestCases.add(testcase);
                }
            }

        } finally {
            diagnosisModel.getCorrectFormulas().removeAll(axioms);
        }



        return entailedTestCases;
    }
}
