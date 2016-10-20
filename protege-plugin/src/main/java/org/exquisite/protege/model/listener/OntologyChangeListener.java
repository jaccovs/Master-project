package org.exquisite.protege.model.listener;

import org.exquisite.protege.Debugger;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * Listens to ontology changes. These ontology changes have also an effect on the debuggin session.
 */
public class OntologyChangeListener implements OWLOntologyChangeListener  {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(OntologyChangeListener.class);

    private Debugger debugger;

    public OntologyChangeListener(Debugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) throws OWLException {
        if (areChangesRelevant(changes) && debugger.isSessionRunning()) {
                logger.debug("Ontology has changed while debugging session is running -> stopping debugging session.");
                debugger.doStopDebugging(Debugger.SessionStopReason.ONTOLOGY_CHANGED);
        }
    }

    /**
     * Checks if the changes on an ontology are relevant for this debugger instance.
     *
     * @param changes A list of changes that have occurred. Each change may be examined
     *        to determine which ontology it was applied to.
     * @return <code>true</code> if there are changes that involve the current ontology. Otherwise <code>false</code>.
     */
    private boolean areChangesRelevant(List<? extends OWLOntologyChange> changes) {
        if (changes.isEmpty())
            return false;

        final OWLOntology activeOntology = debugger.getDiagnosisEngineFactory().getOntology();

        boolean areChangesRelevant = false;
        for (Iterator<? extends OWLOntologyChange> it = changes.iterator(); it.hasNext() && !areChangesRelevant; ) {
            areChangesRelevant = it.next().getOntology().equals(activeOntology);
        }
        return areChangesRelevant;
    }

}
