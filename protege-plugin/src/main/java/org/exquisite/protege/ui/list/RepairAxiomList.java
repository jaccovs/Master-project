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
import org.semanticweb.owlapi.model.*;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public void updateList(final Diagnosis<OWLLogicalAxiom> diagnosis) throws OWLOntologyCreationException {
        List<Object> items = new ArrayList<>();
        items.add(new DiagnosisListHeader(diagnosis, createHeaderName(diagnosis)));
        for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
            Explanation explanation = new Explanation(diagnosis, this.repairDiagnosisPanel, axiom, debugger.getDiagnosisModel(), editorKit, debugger.getDiagnosisEngineFactory().getReasonerFactory(), debugger.getDiagnosisEngineFactory().getDebuggerConfiguration());
            items.add(new RepairListItem(axiom, explanation, getEditorKit(), parent));
        }
        items.add(" ");


        if (items.size() > 0)
            items.remove(items.size() - 1);

        setListData(items.toArray());

    }

    private String createHeaderName(final Diagnosis<OWLLogicalAxiom> diagnosis) {
        final int size = diagnosis.getFormulas().size();
        final String s = (size == 1) ? "this axiom" : "these " + size + " axioms";
        return "Repair " + s + " by deletion or by modification. Select an axiom to show it's explanation.";
    }

    public void dispose() {
        final ListModel listModel = getModel();
        for (int i = 1; i < listModel.getSize(); i++) {
            ((RepairListItem) listModel.getElementAt(i)).dispose();
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
        final RepairAxiomList lsm = (RepairAxiomList)e.getSource();
        final Point mousePoint = getMouseCellLocation();

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

                        if (! isButtonPressed(mousePoint, lsm.getListItemButtons(item))) {
                            // check if there exist some explanations for this axiom (only if user did not select a button)
                            item.showExplanation();
                        } else {
                            // if user clicked on a button, show the no-explanation panel
                            item.showNoExplanation();
                        }
                    }
                }
            }
        }
    }

    private boolean isButtonPressed(final Point mousePressedPoint, final List<MListButton> listItemButtons) {
        if (mousePressedPoint == null)
            return false;

        for (MListButton btn : listItemButtons) {
            final Rectangle btnBounds = btn.getBounds();
            if (btnBounds.x <= mousePressedPoint.x && btnBounds.x + btnBounds.width >= mousePressedPoint.x)
                return true;
        }
        return false;
    }

    private void changeActiveOntology(OWLOntology ontology) {
        // change active ontology to the the repair debugging ontology
        final OWLModelManager modelManager = editorKit.getModelManager();
        modelManager.setActiveOntology(ontology);

    }

    public void applyChangesOnOntology(final OWLOntology ontology) {
        if (hasChanged()) {
            List<OWLAxiomChange> changes = new ArrayList<>();
            final ListModel listModel = getModel();
            for (int i=1; i < listModel.getSize(); i++) {
                final RepairListItem listModelElementAt = (RepairListItem) listModel.getElementAt(i);
                if (listModelElementAt.hasChanged()) {
                    changes.addAll(listModelElementAt.getChanges(ontology));
                }
            }
            if (!changes.isEmpty()) {
                OWLOntologyManager manager = ontology.getOWLOntologyManager();
                manager.applyChanges(changes);
            }
        }
    }
}
