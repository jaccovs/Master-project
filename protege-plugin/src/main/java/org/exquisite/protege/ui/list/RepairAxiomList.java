package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.repair.RepairManager;
import org.exquisite.protege.ui.buttons.ResetAxiomButton;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.explanation.ExplanationDialog;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyManager;
import org.protege.editor.owl.ui.framelist.ExplainButton;
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

    protected EditorKitHook editorKitHook;

    protected Component parent;

    private List<MListButton> explanationButton;

    private RepairManager repairManager;

    private RepairDiagnosisPanel repairDiagnosisPanel;

    public RepairAxiomList(RepairDiagnosisPanel repairDiagnosisPanel, OWLEditorKit editorKit, EditorKitHook editorKitHook, RepairManager repairManager) {
        super(editorKit);
        this.repairDiagnosisPanel = repairDiagnosisPanel;
        this.editorKitHook = editorKitHook;

        explanationButton = new ArrayList<>();
        explanationButton.add(new ExplainButton(e -> invokeExplanationHandler()));

        addListSelectionListener(this);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.repairManager = repairManager;
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
        OWLAxiom ax = getAxiom();

        if (ax != null && getExplanationManager().hasExplanation(ax)) {
            JFrame frame = ProtegeManager.getInstance().getFrame(editorKit.getWorkspace());
            handleExplain(frame, ax);


        }

    }

    private OWLAxiom getAxiom() {
        // Variante 1: das selektierte Axiom
        /*
        Object obj = getSelectedValue();
        if (!(obj instanceof RepairListItem)) {
            return null;
        }
        RepairListItem row = (RepairListItem) obj;
        OWLAxiom ax = row.getAxiom();
        return ax;
        */

        // TODO Variante 2: inconsistenz
        OWLModelManager owlModelManager = editorKit.getOWLModelManager();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());
        return ax;

    }

    public void handleExplain(Frame owner, OWLAxiom axiom) {

        OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();

        // change active ontology to the the repair debugging ontology
        // TODO 1
        editorKit.getModelManager().setActiveOntology(repairManager.getDebuggingOntology());

        // TODO Variante 2 teste den InconsistentOntologyManager von der workbench
        InconsistentOntologyManager.get(editorKit.getOWLModelManager()).explain();

        // TODO Variante 1:
        /*
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
        */

        // TODO 2
        editorKit.getModelManager().setActiveOntology(activeOntology);
    }

    public void explainInconsistency() {
        /*
        final OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
        editorKit.getModelManager().setActiveOntology(repairManager.getDebuggingOntology());

        // TODO Variante 2 teste den InconsistentOntologyManager von der workbench
        //InconsistentOntologyManager.get(editorKit.getOWLModelManager()).explain();
        InconsistentOntologyPluginLoader loader= new InconsistentOntologyPluginLoader(this.editorKit);
        Set<InconsistentOntologyPlugin> plugins = loader.getPlugins();
        InconsistentOntologyPlugin lastSelectedPlugin = null;
        for (final InconsistentOntologyPlugin plugin : plugins) {
            lastSelectedPlugin = plugin;
        }
        InconsistentOntologyPluginInstance i = lastSelectedPlugin.newInstance();
        i.initialise();
        i.setup(this.editorKit);
        i.explain(this.editorKit.getOWLModelManager().getActiveOntology());
        explanations.add(i);

        editorKit.getModelManager().setActiveOntology(activeOntology);
        */
        this.explainEntailment(getAxiom());
    }

    public void explainEntailment(OWLAxiom entailment) {
        final WorkspaceFrame owner = ProtegeManager.getInstance().getFrame(editorKit.getWorkspace());
        OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
        // change active ontology to the the repair debugging ontology
        // TODO 1
        editorKit.getModelManager().setActiveOntology(repairManager.getDebuggingOntology());
        Collection<ExplanationService> teachers = getExplanationManager().getTeachers(entailment);
        if (teachers.size() == 1) {
            this.repairDiagnosisPanel.setExplanation(teachers.iterator().next().explain(entailment));
        }
        /*
        final ExplanationDialog explanation = new ExplanationDialog(getExplanationManager(), entailment);
        openedExplanations.add(explanation);
        JOptionPane op = new JOptionPane(explanation, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = op.createDialog(owner, getExplanationDialogTitle(entailment));
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
        */

        editorKit.getModelManager().setActiveOntology(activeOntology);
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
