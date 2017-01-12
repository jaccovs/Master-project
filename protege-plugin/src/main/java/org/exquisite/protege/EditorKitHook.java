package org.exquisite.protege;

import org.exquisite.core.solver.ExquisiteOWLReasoner;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.model.exception.DiagnosisModelCreationException;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitHook;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

import static org.exquisite.protege.model.event.EventType.*;

public class EditorKitHook extends OWLEditorKitHook implements OWLModelManagerListener, ChangeListener {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(EditorKitHook.class.getName());

    private int id = 0;

    private static int cnt = 0;

    private boolean initialized = false;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<>();

    private Map<OWLOntology,Debugger> ontologyDebuggerMap;

    public void initialise() throws Exception {
        if (!initialized) {
            ontologyDebuggerMap = new LinkedHashMap<>();
            getEditorKit().getModelManager().addListener(this);
            id = cnt++;
            logger.debug("initialised editorKitHook " + id);
            logger.debug("this is ls4j logger");
            initialized = true;
        }
        else
            logger.debug("editorKitHook is already initialized " + id);
    }

    public OWLEditorKit getOWLEditorKit() {
        return getEditorKit();
    }

    public Debugger getActiveOntologyDebugger() {
        return ontologyDebuggerMap.get(getEditorKit().getModelManager().getActiveOntology());
    }

    public void dispose() throws Exception {
        final OWLModelManager modelManager = getEditorKit().getModelManager();
        modelManager.removeListener(this);

        for (Map.Entry<OWLOntology,Debugger> entry : ontologyDebuggerMap.entrySet()) {
            final Debugger debugger = entry.getValue();
            entry.getKey().getOWLOntologyManager().removeOntologyChangeListener(debugger.getOntologyChangeListener());

            debugger.dispose(this);
        }

        logger.debug("disposed editorKitHook " + id);
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
            final OWLOntology activeOntology = event.getSource().getActiveOntology();

            // check if the active ontology is a debugging ontology
            if (isDebuggingOntology(activeOntology)) {
                JOptionPane.showMessageDialog(null, "You have selected an ontology that is used by a running " +
                                "debugging session.\n\nPlease do not modify this ontology!",
                        "Debugging Ontology Selected",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            changeActiveOntologyDebugger(activeOntology);

        } else if (event.isType(EventType.REASONER_CHANGED)) {
            // notification of changing the reasoner
            for (Map.Entry<OWLOntology,Debugger> entry : ontologyDebuggerMap.entrySet()) {
                final Debugger debugger = entry.getValue();
                debugger.doStopDebugging(Debugger.SessionStopReason.REASONER_CHANGED);
                logger.debug("changed reasoner of " + debugger);
            }

        } else if (event.isType(EventType.ONTOLOGY_RELOADED)) {
            // it is not known which ontology has been reloaded
            // therefore we must call reload() of every debugger
            for (Debugger debugger : ontologyDebuggerMap.values())
                debugger.doReload();
        }
    }

    private boolean isDebuggingOntology(OWLOntology activeOntology) {
        if (activeOntology.isAnonymous()) {
            Debugger debugger = null;
            Iterator<Debugger> it = ontologyDebuggerMap.values().iterator();
            while (it.hasNext() && debugger == null) {
                final Debugger ontologyDebugger = it.next();
                final ExquisiteOWLReasoner reasoner = (ExquisiteOWLReasoner) ontologyDebugger.getDiagnosisEngineFactory().getDiagnosisEngine().getSolver();
                if (activeOntology.equals(reasoner.getDebuggingOntology()))
                    debugger = ontologyDebugger;
            }
            return debugger != null;
        }
        return false;
    }

    private void changeActiveOntologyDebugger(OWLOntology activeOntology) {
        if (!ontologyDebuggerMap.containsKey(activeOntology)) {
            final Debugger debugger = new Debugger(getEditorKit());
            final OWLOntologyManager ontologyManager = activeOntology.getOWLOntologyManager();
            debugger.addChangeListener(this);
            ontologyManager.addOntologyChangeListener(debugger.getOntologyChangeListener());
            ontologyDebuggerMap.put(activeOntology, debugger);
            try {
                debugger.createNewDiagnosisModel();
            } catch (DiagnosisModelCreationException e) {
                logger.error(e.getMessage(), e);
            }
        }
        notifyActiveDebuggerListeners(
                new OntologyDebuggerChangeEvent(getActiveOntologyDebugger(), ACTIVE_ONTOLOGY_CHANGED));

        if (activeOntology.getOntologyID().getOntologyIRI().isPresent())
            logger.debug("ontology changed to " + activeOntology.getOntologyID().getOntologyIRI().get().getShortForm());
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        Debugger activeDebugger = ontologyDebuggerMap.get(getEditorKit().getModelManager().getActiveOntology());
        if (activeDebugger != null && e != null && activeDebugger.equals(e.getSource())) {
            // something in the active ontology searcher has changed
            notifyActiveDebuggerListeners((OntologyDebuggerChangeEvent) e);
        } else {
            logger.warn("Unexpected NULL value for either the active debugger (value:" +
                    activeDebugger + ") or changeEvent (value: " + e + ")");
        }
    }

    public void addActiveDebuggerChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeActiveDebuggerChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void notifyActiveDebuggerListeners(OntologyDebuggerChangeEvent event) {
        for (ChangeListener listener : changeListeners) {
            if (listener != null)
                listener.stateChanged(event);
            else
                logger.warn("Unexpected NULL value for a change listener");
        }

    }

}
