package org.exquisite.protege.ui.panel.axioms;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract class used for displaying the axioms in the input ontology.
 */
abstract class AbstractAxiomsPanel extends JPanel {

    private OWLEditorKit editorKit;
    private EditorKitHook editorKitHook;

    AbstractAxiomsPanel(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(new BorderLayout());
        this.editorKit = editorKit;
        this.editorKitHook = editorKitHook;
    }

    JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize()+1));
        return label;
    }

    JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        //toolBar.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        return toolBar;
    }

    /**
     * Updates a list with a set of axioms that exists in the active ontology.
     *
     * @param list The list to update.
     * @param axioms The axioms to update the list with after a check if all axioms doe exist in the active ontology.
     */
    void updateDisplayedAxioms(BasicAxiomList list, java.util.List<OWLLogicalAxiom> axioms) {
        final OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();

        // show only those axioms that do also exist in the active ontology and show them in a sorted order (TreeSet)
        Set<OWLLogicalAxiom> axiomsToDisplay = new TreeSet<>(axioms);
        axiomsToDisplay.retainAll(ontology.getLogicalAxioms());
        list.updateList(axiomsToDisplay, ontology);
    }

    /**
     * Update the list of axioms shown in the panel.
     */
    abstract public void updateDisplayedAxioms();

    protected OWLEditorKit getOWLEditorKit() {
        return this.editorKit;
    }

    protected EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }
}
