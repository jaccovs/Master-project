package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        checkDiagnosisModel();
    }

    public boolean isEntailed(OWLLogicalAxiom entailment) {
        return this.reasoner.isEntailed(Collections.singleton(entailment));
    }

    public void sync(Collection<OWLLogicalAxiom> addFormulas) {
        OWLOntology ontology = this.reasoner.getRootOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();

        List<OWLAxiomChange> changes = new ArrayList<>(addFormulas.size() + 2);

        for (OWLAxiom ax : addFormulas){
            assert ax != null;
            changes.add(new AddAxiom(ontology, ax));
        }

        if (!changes.isEmpty())
            manager.applyChanges(changes);

        this.reasoner.flush();
    }

}
