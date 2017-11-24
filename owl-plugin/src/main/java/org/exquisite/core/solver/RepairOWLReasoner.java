package org.exquisite.core.solver;

import org.exquisite.core.model.DiagnosisModel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This type of exquisite reasoner is used for repair sessions once a diagnosis has been found and needs to be repaired.
 *
 * @author wolfi
 */
public class RepairOWLReasoner extends ExquisiteOWLReasoner {

    private Logger logger = LoggerFactory.getLogger(RepairOWLReasoner.class.getCanonicalName());

    /**
     * This type of reasoner uses the ontology manager given by the caller.
     *
     * @param dm                            a diagnosis model.
     * @param reasonerFactory               of a reasoner expressive enough to reason about consistency of the ontology.
     * @param manager                       an ontology manager provided by the caller.
     * @param entailmentTypes               a set of entailment types to be used.
     * @throws OWLOntologyCreationException An exception which describes an error during the creation of an ontology.
     */
    public RepairOWLReasoner(DiagnosisModel<OWLLogicalAxiom> dm, OWLReasonerFactory reasonerFactory, OWLOntologyManager manager, InferenceType[] entailmentTypes) throws OWLOntologyCreationException {
        super(dm);
        this.debuggingOntologyManager = manager;
        // use of an anonymous ontology as the debugging ontology
        this.debuggingOntology = this.debuggingOntologyManager.createOntology();
        this.reasoner = reasonerFactory.createReasoner(debuggingOntology);

        // checks if ontology without possibly faulty formulas is consistent
        checkDiagnosisModel();

        setEntailmentTypes(entailmentTypes);
        // now also add the possibly faulty formula from the diagnosis to the ontology
        sync(new HashSet<>(dm.getPossiblyFaultyFormulas()), Collections.emptySet());
    }

    public boolean isEntailed(OWLLogicalAxiom entailment) {
        return this.reasoner.isEntailed(Collections.singleton(entailment));
    }

    /**
     * Modifies an axiom of the reasoners root ontology by deleting it's old version and adding the new version of the axiom.
     *
     * @param newVersion The modified axiom.
     * @param oldVersion The old axiom.
     * @return <code>true</code> if the changes have been applied successfully, <code>false</code> otherwise.
     */
    public boolean modifyAxiom(OWLLogicalAxiom newVersion, OWLLogicalAxiom oldVersion) {
        final List<OWLAxiomChange> changes = new ArrayList<>(2);
        changes.add(new RemoveAxiom(reasoner.getRootOntology(), oldVersion));
        changes.add(new AddAxiom(reasoner.getRootOntology(), newVersion));
        return applyChanges(changes);
    }

    /**
     * Removes the axiom from the reasoner's root ontology (aka test ontology for repairs).
     *
     * @param axiom Axiom to be removed from the ontology.
     * @return <code>true</code> if the axiom has been successfully removed from the ontology, <code>false</code> otherwise.
     */
    public boolean deleteAxiom(OWLLogicalAxiom axiom) {
        final List<OWLAxiomChange> changes = new ArrayList<>(1);
        changes.add(new RemoveAxiom(reasoner.getRootOntology(), axiom));
        return applyChanges(changes);
    }

    /**
     * Restores a once modified or deleted axiom of the reasoner's root ontology to it's original form.
     *
     * @param axiom The axiom to be recreated.
     * @return <code>true</code> if the changes have been applied successfully, <code>false</code> otherwise.
     */
    public boolean restoreAxiom(OWLLogicalAxiom axiom) {
        final List<OWLAxiomChange> changes = new ArrayList<>(1);
        changes.add(new AddAxiom(reasoner.getRootOntology(), axiom));
        return applyChanges(changes);
    }

    /**
     * Applies the changes to the reasoners root ontology and flushes the reasoner.
     *
     * @param changes A list of changes.
     * @return <code>true</code> if the changes have been applied successfully, <code>false</code> otherwise.
     */
    private boolean applyChanges(final List<OWLAxiomChange> changes) {
        final OWLOntology ontology = this.reasoner.getRootOntology();
        final OWLOntologyManager manager = ontology.getOWLOntologyManager();
        boolean areChangesApplied = false;

        assert ontology.equals(this.debuggingOntology);

        if (!changes.isEmpty()) {
            final ChangeApplied appliedChange = manager.applyChanges(changes);
            areChangesApplied = appliedChange.equals(ChangeApplied.SUCCESSFULLY);

            if (areChangesApplied) {
                this.reasoner.flush();
            } else {
                logger.warn("Changes " + changes + " could not be applied to " + ontology);
            }
        }
        return areChangesApplied;
    }

}
