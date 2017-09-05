package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.model.explanation.Explanation;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wolfi
 */
public class RepairAxiomList extends AbstractAxiomList implements ListSelectionListener {

    private Component parent;

    private Debugger debugger;

    private RepairDiagnosisPanel repairDiagnosisPanel;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(RepairAxiomList.class.getName());

    public RepairAxiomList(RepairDiagnosisPanel repairDiagnosisPanel, OWLEditorKit editorKit, Debugger debugger, Component parent) {
        super(editorKit);
        this.repairDiagnosisPanel = repairDiagnosisPanel;
        this.debugger = debugger;

        addListSelectionListener(this);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.parent = parent;
    }

    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        super.addListSelectionListener(listener);
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof RepairListItem) {
            final RepairListItem listItem = (RepairListItem) value;
            List<MListButton> buttons = new ArrayList<>();

            buttons.addAll(super.getButtons(value));

            if (listItem.hasChanged()) {
                buttons.add(0, new ResetAxiomButton(e -> listItem.handleReset()));
            }
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) throws OWLOntologyCreationException {
        if (diagnoses.size() == 1) {
            List<Object> items = new ArrayList<>();

            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                items.add(new DiagnosisListHeader(diagnosis, createHeaderName(diagnosis)));
                int idx = 1;
                for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                    Explanation explanation = new Explanation(this.repairDiagnosisPanel, axiom, debugger.getDiagnosisModel(), editorKit, debugger.getDiagnosisEngineFactory().getReasonerFactory(), debugger.getDiagnosisEngineFactory().getDebuggerConfiguration());
                    items.add(new RepairListItem(axiom, explanation, getEditorKit(), parent));
                    idx++;
                }
                // items.addAll(diagnosis.getFormulas().stream().map(axiom -> new RepairListItem(this, axiom, this.explanationManager.getOntology(axiom), getEditorKit(), parent)).collect(Collectors.toList()));
                items.add(" ");
            }

            if (items.size() > 0)
                items.remove(items.size() - 1);

            setListData(items.toArray());
        } else {
            setListData(new ArrayList<>().toArray());
        }

    }

    private String createHeaderName(final Diagnosis<OWLLogicalAxiom> diagnosis) {
        final int size = diagnosis.getFormulas().size();
        final String s = (size == 1) ? "this axiom" : "these " + size + " axioms";
        return "Repair " + s + " either by deletion or by modification.";
    }

    public void dispose() {
        final ListModel listModel = getModel();
        for (int i = 1; i < listModel.getSize(); i++) {
            ((RepairListItem) listModel.getElementAt(i)).dispose();
        }
    }

    public void reset() {
        final ListModel listModel = getModel();

        for (int i = 1; i < listModel.getSize(); i++) {
            final RepairListItem listModelElementAt = (RepairListItem) listModel.getElementAt(i);
            listModelElementAt.handleReset();
        }
    }

    public boolean hasChanged() {
        boolean hasChanged = false;
        final ListModel listModel = getModel();
        for (int i = 1; !hasChanged && i < listModel.getSize(); i++) {
            final RepairListItem listModelElementAt = (RepairListItem) listModel.getElementAt(i);
            hasChanged = listModelElementAt.hasChanged();
        }
        return hasChanged;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        RepairAxiomList lsm = (RepairAxiomList)e.getSource();

        boolean isAdjusting = e.getValueIsAdjusting();
        if (!isAdjusting) {
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int idx = -1;
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        idx = i;
                    }
                }

                if (idx != -1) {
                    final Object o = lsm.getModel().getElementAt(idx);
                    if (o instanceof RepairListItem) {
                        final RepairListItem item = (RepairListItem) o;

                        // change to the active ontology to the selected item's ontology
                        changeActiveOntology(item.getOntology());

                        logger.debug("Set active ontology to " + item.getOntology().getOntologyID());

                        // check if there exist some explanations for this axioms
                        item.explain();
                    }
                }
            }
        }
    }

    private void changeActiveOntology(OWLOntology ontology) {
        // change active ontology to the the repair debugging ontology
        final OWLModelManager modelManager = editorKit.getModelManager();
        modelManager.setActiveOntology(ontology);

    }
}
