package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.DiagnosisAxiomList;
import org.protege.editor.core.ui.list.MList;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.event.ChangeEvent;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.model.event.EventType.*;

public class DiagnosesView extends AbstractListViewComponent {

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        updateView();
    }

    @Override
    protected MList createListForComponent() {
        return new DiagnosisAxiomList(getOWLEditorKit());
    }

    protected void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        ((DiagnosisAxiomList)getList()).updateList(diagnoses,ontology);
    }

    private void updateView() {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        // sort and show the list of diagnoses depending on their measures in descending order
        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new TreeSet<>((o1, o2) -> o2.compareTo(o1));
        diagnoses.addAll(debugger.getDiagnoses());
        updateList(diagnoses);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND).contains(type))
            updateView();
    }


}
