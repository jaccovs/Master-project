package org.exquisite.protege.ui.view;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.ConflictAxiomList;
import org.protege.editor.core.ui.list.MList;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.event.ChangeEvent;
import java.util.EnumSet;
import java.util.Set;

import static org.exquisite.protege.model.event.EventType.*;

public class ConflictsView extends AbstractListViewComponent {

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND).contains(type))
            updateList(((Debugger) e.getSource()).getConflicts());
    }

    @Override
    protected MList createListForComponent() {
        return new ConflictAxiomList(getOWLEditorKit(), getEditorKitHook());
    }

    protected void updateList(Set<Set<OWLLogicalAxiom>> setOfAxiomsets) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        ((ConflictAxiomList)getList()).updateList(setOfAxiomsets, ontology);
    }
}
