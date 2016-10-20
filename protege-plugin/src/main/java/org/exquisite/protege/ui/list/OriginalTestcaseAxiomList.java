package org.exquisite.protege.ui.list;

import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.protege.Debugger.TestcaseType;

/**
 * List of entailed and non test cases already given the ontology.
 */
public class OriginalTestcaseAxiomList extends AbstractTestcaseAxiomList {

    public OriginalTestcaseAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
    }

    @Override
    protected TestcaseType getEntailedType() {
        return TestcaseType.ORIGINAL_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getEntailedTestcases() {
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getOriginalEntailedTestcases());
    }

    @Override
    protected TestcaseType getNonEntailedType() {
        return TestcaseType.ORIGINAL_NON_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getNonEntailedTestcases() {
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getOriginalNonEntailedTestcases());
    }
}
