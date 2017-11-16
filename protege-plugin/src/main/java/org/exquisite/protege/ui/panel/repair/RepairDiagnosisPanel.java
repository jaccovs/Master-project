package org.exquisite.protege.ui.panel.repair;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.explanation.WorkbenchSettings;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author wolfi
 */
public class RepairDiagnosisPanel extends JPanel {

    private OWLEditorKit editorKit;

    private Diagnosis<OWLLogicalAxiom> diagnosis;

    private Debugger debugger;

    private RepairAxiomList repairAxiomList;

    private JPanel explanationContainer;

    private ExplanationResult explanation;

    private JLabel label;

    private WorkbenchSettings workbenchSettings;

    private boolean explanationEnabled = true;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(RepairDiagnosisPanel.class.getName());

    public RepairDiagnosisPanel(final OWLEditorKit editorKit, final Diagnosis<OWLLogicalAxiom> diagnosis) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        this.diagnosis = diagnosis;
        EditorKitHook editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();
        this.workbenchSettings = new WorkbenchSettings();

        setPreferredSize(getPreferredSize());

        setLayout(new GridBagLayout());
        addRepairAxiomList();
        addExplanationContainer();

        setVisible(true);
    }

    private void addRepairAxiomList() throws OWLOntologyCreationException {
        repairAxiomList = new RepairAxiomList(this, editorKit, workbenchSettings, debugger, this);
        repairAxiomList.updateList(this.diagnosis);
        GridBagConstraints c = new GridBagConstraints();

        // sets the GridBagConstraints
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 0.4;

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Repair"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        panel.add(ComponentFactory.createScrollPane(repairAxiomList), BorderLayout.CENTER);
        add(panel, c);
    }

    private void addExplanationContainer() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Explanations"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        final JCheckBox checkBox = new JCheckBox("<html>Compute explanations for selected axioms</html>");
        checkBox.setSelected(isExplanationEnabled());
        checkBox.addActionListener(e -> {
            explanationEnabled = !explanationEnabled;
            checkBox.setSelected(isExplanationEnabled());
            final RepairListItem selectedItem = repairAxiomList.getSelectedItem();
            if (selectedItem != null)
                selectedItem.showExplanation();
        });

        panel.add(checkBox, BorderLayout.NORTH);

        final JPanel explanations = new JPanel(new BorderLayout());

        label = new JLabel();
        explanations.add(this.label, BorderLayout.NORTH);

        explanationContainer = new JPanel();
        explanationContainer.setLayout(new BoxLayout(explanationContainer, BoxLayout.Y_AXIS));

        GridBagConstraints c = new GridBagConstraints();

        // sets the GridBagConstraints
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 0.6;

        explanations.add(explanationContainer, BorderLayout.CENTER);

        panel.add(explanations, BorderLayout.CENTER);
        add(panel, c);
    }

    public void dispose() {
        // before disposing all repair states and explanation, we must set the active ontology back to the original one
        final OWLOntology ontology = this.debugger.getDiagnosisEngineFactory().getOntology();
        editorKit.getModelManager().setActiveOntology(ontology);
        logger.debug("Set active ontology to " + ontology.getOntologyID());

        repairAxiomList.dispose();
        if (this.explanation != null) {
            this.explanation.dispose();
        }
    }

    public void setExplanation(final ExplanationResult expl, final String label) {
        explanationContainer.removeAll();
        if (this.explanation != null) {
            this.explanation.dispose();
        }
        this.explanation = expl;
        explanationContainer.add(this.explanation);
        if (this.label != null) {
            if (label != null) this.label.setText("<html><h3>" + label + "</h3></html>");
            else this.label.setText("");
        }
        explanationContainer.revalidate();
    }

    public void doCancelAction() {
        dispose();
    }

    public void doOkAction() {
        repairAxiomList.applyChangesOnOntology(this.debugger.getDiagnosisEngineFactory().getOntology());
        dispose();
    }

    public boolean hasChanged() {
        return repairAxiomList.hasChanged();
    }

    public Dimension getPreferredSize() {
        Dimension workspaceSize = editorKit.getWorkspace().getSize();
        int width = (int) (workspaceSize.getWidth() * 0.85);
        int height = (int) (workspaceSize.getHeight() * 0.6);
        return new Dimension(width, height);
    }

    public boolean isExplanationEnabled() {
        return explanationEnabled;
    }

}
