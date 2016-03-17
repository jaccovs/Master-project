package org.exquisite.protege.model;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitHook;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: pfleiss
 * Date: 14.11.11
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class EditorKitHook extends OWLEditorKitHook implements OWLModelManagerListener, ChangeListener {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(EditorKitHook.class.getName());

    private int id = 0;

    private static int cnt = 0;

    boolean initialized = false;

    private Set<ChangeListener> changeListeners = new LinkedHashSet<ChangeListener>();

    private Map<OWLOntology,OntologyDiagnosisSearcher> ontologyDiagnosisSearcherMap;

    public void initialise() throws Exception {
        if (!initialized) {
            ontologyDiagnosisSearcherMap = new LinkedHashMap<OWLOntology, OntologyDiagnosisSearcher>();
            getEditorKit().getModelManager().addListener(this);
            id = cnt;
            cnt++;
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

    public OntologyDiagnosisSearcher getActiveOntologyDiagnosisSearcher() {
        return ontologyDiagnosisSearcherMap.get(getEditorKit().getModelManager().getActiveOntology());
    }

    // Every (OWL)EditorKit has a Model (get ModelManager) and a UI (get Workspace)

    public void dispose() throws Exception {
        getEditorKit().getModelManager().removeListener(this);
        for (OntologyDiagnosisSearcher searcher : ontologyDiagnosisSearcherMap.values())
            searcher.removeChangeListener(this);
        logger.debug("disposed editorKitHook " + id);
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
            OWLOntology activeOntology = event.getSource().getActiveOntology();
            if (!ontologyDiagnosisSearcherMap.containsKey(activeOntology)) {
                OntologyDiagnosisSearcher searcher = new OntologyDiagnosisSearcher(getEditorKit());
                searcher.addChangeListener(this);
                ontologyDiagnosisSearcherMap.put(activeOntology,searcher);
            }
            notifyActiveSearcherListeners(new ChangeEvent(getActiveOntologyDiagnosisSearcher()));
            logger.debug("ontology changed to " + activeOntology.getOntologyID().getOntologyIRI().get().getShortForm());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        OWLOntology activeOntology = getEditorKit().getModelManager().getActiveOntology();

        OntologyDiagnosisSearcher activeSearcher = ontologyDiagnosisSearcherMap.get(activeOntology);
        if (activeSearcher.equals((OntologyDiagnosisSearcher) e.getSource())) {
            // something in the active ontology searcher has changed
            notifyActiveSearcherListeners(e);
        }
    }

    public void addActiveSearcherChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeActiveSearcherChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void notifyActiveSearcherListeners(ChangeEvent event) {
        for (ChangeListener listener : changeListeners)
            listener.stateChanged(event);
    }


}
