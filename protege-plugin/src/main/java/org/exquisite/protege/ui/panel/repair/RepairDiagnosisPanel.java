package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author wolfi
 */
public class RepairDiagnosisPanel extends JComponent {

    private OWLEditorKit editorKit;

    private Debugger debugger;

    private RepairAxiomList repairAxiomList;

    private JPanel explanationContainer;

    private ExplanationResult explanation;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(RepairDiagnosisPanel.class.getName());

    public RepairDiagnosisPanel(OWLEditorKit editorKit) throws OWLOntologyCreationException {
        this.editorKit = editorKit;
        EditorKitHook editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();
        setPreferredSize(getPreferredSize());
        addComponentToPane(this);
        setVisible(true);
    }

    private void addComponentToPane(Container pane) throws OWLOntologyCreationException {
        pane.setLayout(new GridBagLayout());

        repairAxiomList = new RepairAxiomList(this, editorKit, this.debugger, this);
        repairAxiomList.updateList(this.debugger.getDiagnoses());
        addToPane(0,0,2,1,1.0,0.5, repairAxiomList, "Repair", pane);

        explanationContainer = new JPanel();
        explanationContainer.setLayout(new BoxLayout(explanationContainer, BoxLayout.Y_AXIS));
        addToPane(0,1,2,1,1.0,0.5,explanationContainer,"Explanations", pane);
    }

    private void addToPane(int x, int y, int w, int h, double weightx, double weighty, JComponent component, String title, Container pane) {

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

        panel.add(ComponentFactory.createScrollPane(component), BorderLayout.CENTER);

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

    public void setExplanation(ExplanationResult expl) {
        explanationContainer.removeAll();
        if (this.explanation != null) {
            this.explanation.dispose();
        }
        this.explanation = expl;
        explanationContainer.add(this.explanation);
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

}
