package org.exquisite.protege.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 18/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: removed unused import statement by @author wolfi
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
