package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.model.event.EventType.*;

public class DiagnosesView extends AbstractDiagnosesSetView {

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        updateView();
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

    @Override
    protected Color getHeaderColor() {
        return new Color(85, 255, 97, 174);
    }

    @Override
    protected String getHeaderPrefix() {
        return "Faulty Axioms ";
    }

    @Override
    protected boolean isIncludeMeasure() {
        return true;
    }

}
