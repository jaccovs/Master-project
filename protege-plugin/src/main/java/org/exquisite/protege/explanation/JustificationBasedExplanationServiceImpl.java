package org.exquisite.protege.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 18/03/2012
 */
public class JustificationBasedExplanationServiceImpl extends ExplanationService {

    private OWLEditorKit owlEditorKit;

    public JustificationBasedExplanationServiceImpl(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }

    @Override
    public void initialise() throws Exception {
    }

    @Override
    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }

    @Override
    public boolean hasExplanation(OWLAxiom axiom) {
        return axiom instanceof OWLLogicalAxiom;
    }

    @Override
    public ExplanationResult explain(OWLAxiom entailment) {
        WorkbenchPanel workbenchPanel = new WorkbenchPanel(getOWLEditorKit(), entailment);
        return new WorkbenchPanelExplanationResult(workbenchPanel);
    }

    public void dispose() throws Exception {

    }
}
