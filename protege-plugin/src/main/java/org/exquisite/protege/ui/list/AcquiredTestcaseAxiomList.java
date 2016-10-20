package org.exquisite.protege.ui.list;

import org.exquisite.protege.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.protege.Debugger.TestcaseType;

/**
 * List of given answers to queries marked as either entailed or non-entailed.
 * This is a separate view to the set of entailed and non-entailed testcases in the diagnosis model.
 */
public class AcquiredTestcaseAxiomList extends AbstractTestcaseAxiomList {

    public AcquiredTestcaseAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
    }

    @Override
    protected TestcaseType getEntailedType() {
        return TestcaseType.ACQUIRED_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getEntailedTestcases() {
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getAcquiredEntailedTestcases());
    }

    @Override
    protected TestcaseType getNonEntailedType() {
        return TestcaseType.ACQUIRED_NON_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getNonEntailedTestcases() {
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getAcquiredNonEntailedTestcases());
    }
}
