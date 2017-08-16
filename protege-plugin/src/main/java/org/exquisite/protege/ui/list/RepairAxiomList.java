package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.model.repair.RepairManager;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.ExplanationDialog;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class RepairAxiomList extends AbstractAxiomList implements ListSelectionListener {

    private Component parent;

    private RepairManager repairManager;

    private RepairDiagnosisPanel repairDiagnosisPanel;

    private final Collection<ExplanationDialog> openedExplanations = new HashSet<>();

    public RepairAxiomList(RepairDiagnosisPanel repairDiagnosisPanel, OWLEditorKit editorKit, RepairManager repairManager, Component parent) {
        super(editorKit);
        this.repairDiagnosisPanel = repairDiagnosisPanel;

        addListSelectionListener(this);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.repairManager = repairManager;
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

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        if (diagnoses.size() == 1) {
            List<Object> items = new ArrayList<>();

            for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
                items.add(new DiagnosisListHeader(diagnosis, createHeaderName(diagnosis)));
                items.addAll(diagnosis.getFormulas().stream().map(axiom -> new RepairListItem(this, axiom, ontology, getEditorKit(), parent)).collect(Collectors.toList()));
                items.add(" ");
            }

            if (items.size() > 0)
                items.remove(items.size() - 1);

            setListData(items.toArray());
        } else {
            setListData(new ArrayList<>().toArray());
        }

    }

    public void updateListItem(RepairListItem item) {
        final ListModel listModel = getModel();
        for (int i = 1; i < listModel.getSize(); i++) {
            final RepairListItem listModelElementAt = (RepairListItem) listModel.getElementAt(i);
            if (listModelElementAt.equals(item)) {
                System.out.println("found");
            }
        }
    }

    private String createHeaderName(final Diagnosis<OWLLogicalAxiom> diagnosis) {
        final int size = diagnosis.getFormulas().size();
        final String s = (size == 1) ? "this axiom" : "these " + size + " axioms";
        return "Repair " + s + " either by deletion or by modification.";
    }

    public void dispose() {
        for (ExplanationDialog explanation : openedExplanations) {
            explanation.dispose();
        }
        openedExplanations.clear();
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
            hasChanged |= listModelElementAt.hasChanged();

        }
        return hasChanged;
    }

    private ExplanationManager getExplanationManager() {
        return editorKit.getModelManager().getExplanationManager();
    }

    private void explainInconsistency() {
        OWLModelManager owlModelManager = editorKit.getOWLModelManager();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());
        this.explainEntailment(ax);
    }

    private void explainEntailment(OWLAxiom entailment) {
        final OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
        final OWLModelManager modelManager = editorKit.getModelManager();

        try {
            // change active ontology to the the repair debugging ontology
            modelManager.setActiveOntology(repairManager.getDebuggingOntology());
            Collection<ExplanationService> teachers = getExplanationManager().getTeachers(entailment);
            if (teachers.size() >= 1) {
                final ExplanationService explanationService = teachers.iterator().next();
                final ExplanationResult explanation = explanationService.explain(entailment);
                this.repairDiagnosisPanel.setExplanation(explanation);
            }
        } finally {
            modelManager.setActiveOntology(activeOntology);
        }
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
                        System.out.print("Is " + item.getAxiom() + " consistent? -> ");
                        final boolean isConsistent = this.repairManager.isConsistent(item.getAxiom());
                        System.out.println(isConsistent);
                        if (!isConsistent) {
                            System.out.println("Explaining inconsistency");
                            explainInconsistency();
                        } else {
                            for (OWLLogicalAxiom entailment : this.repairManager.getEntailedTestCases(item.getAxiom())) {
                                System.out.println("Explaining entailment " + entailment);
                                explainEntailment(entailment);
                            }
                        }
                    }
                }
            }
        }

    }
}
