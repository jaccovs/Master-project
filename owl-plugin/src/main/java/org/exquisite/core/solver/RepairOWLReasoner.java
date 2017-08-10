package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * @author wolfi
 */
public class RepairOWLReasoner extends ExquisiteOWLReasoner {

    public RepairOWLReasoner(DiagnosisModel<OWLLogicalAxiom> dm, OWLReasonerFactory reasonerFactory, OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(dm);
        this.debuggingOntologyManager = manager;
        // use of an anonymous ontology as the debugging ontology
        this.debuggingOntology = this.debuggingOntologyManager.createOntology();
        this.reasoner = reasonerFactory.createReasoner(debuggingOntology);

    }
}
