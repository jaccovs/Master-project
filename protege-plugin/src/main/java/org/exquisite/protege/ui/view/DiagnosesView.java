package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.DiagnosisAxiomList;
import org.protege.editor.core.ui.list.MList;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.event.ChangeEvent;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import static org.exquisite.protege.model.event.EventType.*;

/**
 * <p>
 *     A view that represents the current diagnoses with respect to the given diagnosis problem instance (DPI) consisting of
 *     possibly faulty and correct axioms, original test cases and acquired test cases.
 * </p>
 * <p>
 *     The diagnoses (more user friendly titled as Faulty Axioms Sets) are listed in descending order of their measures.
 * </p>
 *
 * @author wolfi
 */
public class DiagnosesView extends AbstractListViewComponent {

    //private RepairButton repairButton;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        updateView();
    }

    @Override
    protected MList createListForComponent() {
        return new DiagnosisAxiomList(getOWLEditorKit());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, QUERY_CALCULATED, DIAGNOSIS_FOUND).contains(((OntologyDebuggerChangeEvent) e).getType()))
            updateView();
    }

    private void updateView() {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        // sort and show the list of diagnoses depending on their measures in descending order
        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = new TreeSet<>(Comparator.reverseOrder());
        diagnoses.addAll(debugger.getDiagnoses());

        // updating the list
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        ((DiagnosisAxiomList)getList()).updateList(diagnoses,ontology);
    }


}
