package org.exquisite.protege.ui.panel.axioms;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.*;
import java.awt.*;

/**
 * Panel to display the set of correct axioms in the input ontology.
 */
public class CorrectAxiomsPanel extends AbstractAxiomsPanel {

    private BasicAxiomList correctAxiomsList;

    private JLabel infoLabel = null;

    public CorrectAxiomsPanel(OWLEditorKit editorKit, EditorKitHook editorKitHook, BasicAxiomList correctAxioms) {
        super(editorKit, editorKitHook);
        this.correctAxiomsList = correctAxioms;

        add(createCorrectAxiomsToolBar(),BorderLayout.NORTH);
        add(ComponentFactory.createScrollPane(correctAxiomsList),BorderLayout.CENTER);
    }

    private JToolBar createCorrectAxiomsToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Correct Axioms (Background)"));

        toolBar.add(Box.createHorizontalGlue());
        this.infoLabel = createSizeLabel();
        toolBar.add(this.infoLabel);
        toolBar.addSeparator();

        toolBar.setToolTipText("Axioms from the background are considered to be correct and therefore are not candidates for diagnoses.");
        toolBar.add(Box.createVerticalStrut(25));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    private JLabel createSizeLabel() {
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.ITALIC, label.getFont().getSize()-1));
        return label;
    }

    @Override
    public void updateDisplayedAxioms() {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
        updateDisplayedAxioms(correctAxiomsList, diagnosisModel.getCorrectFormulas());

        infoLabel.setText(correctAxiomsList.getModel().getSize() + " axioms");
    }
}
