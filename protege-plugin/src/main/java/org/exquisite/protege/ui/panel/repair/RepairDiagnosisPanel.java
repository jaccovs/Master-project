package org.exquisite.protege.ui.panel.repair;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author wolfi
 */
public class RepairDiagnosisPanel extends JComponent {

    private OWLEditorKit editorKit;

    private Diagnosis<OWLLogicalAxiom> diagnosis;

    private Debugger debugger;

    private RepairAxiomList repairAxiomList;

    private JPanel explanationContainer;

    private ExplanationResult explanation;

    private JCheckBox label;

    private boolean explanationEnabled = false;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(RepairDiagnosisPanel.class.getName());

    public RepairDiagnosisPanel(final OWLEditorKit editorKit, final Diagnosis<OWLLogicalAxiom> diagnosis) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        this.diagnosis = diagnosis;
        EditorKitHook editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();
        setPreferredSize(getPreferredSize());
        addComponentToPane(this);
        setVisible(true);
    }

    private void addComponentToPane(Container pane) throws OWLOntologyCreationException {
        pane.setLayout(new GridBagLayout());

        repairAxiomList = new RepairAxiomList(this, editorKit, this.debugger, this);
        repairAxiomList.updateList(this.diagnosis);
        addToPane(0,0,2,1,1.0,0.4, repairAxiomList, "Repair", pane, false);

        explanationContainer = new JPanel();
        explanationContainer.setLayout(new BoxLayout(explanationContainer, BoxLayout.Y_AXIS));
        addToPane(0,1,2,1,1.0,0.6,explanationContainer,"Explanations", pane, true);
    }

    private void addToPane(int x, int y, int w, int h, double weightx, double weighty, JComponent component, String title, Container pane, boolean withLabel) {

        GridBagConstraints c = new GridBagConstraints();

        // sets the GridBagConstraints
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        title),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        if (withLabel) {
            label = new JCheckBox("<html>Select an axiom to explain why it has to be repaired</html>");
            label.setSelected(isExplanationEnabled());
            label.addItemListener(
                    new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            explanationEnabled = !explanationEnabled;
                        }
                    }
            );

            final Border border = label.getBorder();
            Border margin = new EmptyBorder(0,10,10,0);
            label.setBorder(new CompoundBorder(border, margin));
            panel.add(label, BorderLayout.NORTH);
        }

        if (withLabel) {
            panel.add(component, BorderLayout.CENTER);
        } else {
            panel.add(ComponentFactory.createScrollPane(component), BorderLayout.CENTER);
        }

        pane.add(panel, c);
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

    public void setExplanation(final ExplanationResult expl) {
        setExplanation(expl, null);
    }

    public void setExplanation(final ExplanationResult expl, final String label) {
        explanationContainer.removeAll();
        if (this.explanation != null) {
            this.explanation.dispose();
        }
        this.explanation = expl;
        explanationContainer.add(this.explanation);
        if (label != null) this.label.setText(label);
        else this.label.setText("");
        revalidate();
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
