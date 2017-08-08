package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationDialog;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.framelist.ExplainButton;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class RepairAxiomList extends AbstractAxiomList implements ListSelectionListener {

    protected EditorKitHook editorKitHook;

    protected Component parent;

    private List<MListButton> explanationButton;

    public RepairAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;

        explanationButton = new ArrayList<>();
        explanationButton.add(new ExplainButton(e -> invokeExplanationHandler()));

        addListSelectionListener(this);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

            if (!listItem.isDeleted() && getExplanationManager().hasExplanation(listItem.getAxiom())) {
                buttons.addAll(explanationButton);
            }

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

    protected ExplanationManager getExplanationManager() {
        return editorKit.getModelManager().getExplanationManager();
    }

    protected void invokeExplanationHandler() {
        Object obj = getSelectedValue();
        if (!(obj instanceof RepairListItem)) {
            return;
        }
        RepairListItem row = (RepairListItem) obj;
        OWLAxiom ax = row.getAxiom();
        if (getExplanationManager().hasExplanation(ax)) {
            JFrame frame = ProtegeManager.getInstance().getFrame(editorKit.getWorkspace());
            handleExplain(frame, ax);
        }

    }

    public void handleExplain(Frame owner, OWLAxiom axiom) {
        final ExplanationDialog explanation = new ExplanationDialog(getExplanationManager(), axiom);
        openedExplanations.add(explanation);
        JOptionPane op = new JOptionPane(explanation, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = op.createDialog(owner, getExplanationDialogTitle(axiom));
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(explanation);
            }
        });
        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                dispose(explanation);
            }
        });
        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setVisible(true);
    }

    private final Collection<ExplanationDialog> openedExplanations = new HashSet<>();

    private void dispose(ExplanationDialog explanation) {
        if (openedExplanations.remove(explanation)) {
            explanation.dispose();
        }
    }

    private String getExplanationDialogTitle(OWLAxiom entailment) {
        String rendering = editorKit.getOWLModelManager().getRendering(entailment).replaceAll("\\s", " ");
        return "Explanation for " + rendering;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        RepairAxiomList lsm = (RepairAxiomList)e.getSource();

        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting();
        System.out.print("Event for indexes "
                + firstIndex + " - " + lastIndex
                + "; isAdjusting is " + isAdjusting
                + "; selected indexes:");

        if (lsm.isSelectionEmpty()) {
            System.out.println(" <none>");
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    System.out.println(" " + i);
                }
            }
        }

    }
}
