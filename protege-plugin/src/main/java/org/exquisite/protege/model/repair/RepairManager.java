package org.exquisite.protege.model.repair;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.core.solver.RepairOWLReasoner;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * @author wolfi
 */
public class RepairManager {

    private Logger logger = LoggerFactory.getLogger(RepairManager.class.getCanonicalName());

    private ExquisiteOWLReasoner reasoner;

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
/* var 1
        final List<OWLLogicalAxiom> axioms = Collections.singletonList(axiom);

        diagnosisModel.getPossiblyFaultyFormulas().addAll(axioms);

        boolean isConsistent = this.reasoner.isConsistent(Collections.emptySet());

        diagnosisModel.getPossiblyFaultyFormulas().removeAll(axioms);

        return isConsistent;
*/


        Collection<OWLLogicalAxiom> formulas = Collections.singletonList(axiom);
        return this.reasoner.isConsistent(formulas);
    }
}
