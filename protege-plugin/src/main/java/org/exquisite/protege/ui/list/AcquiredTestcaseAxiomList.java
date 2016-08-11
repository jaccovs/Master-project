package org.exquisite.protege.ui.list;

import org.exquisite.protege.model.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import java.util.ArrayList;
import java.util.List;

import static org.exquisite.protege.model.OntologyDiagnosisSearcher.TestcaseType;

/**
 * @author wolfi
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
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDiagnosisSearcher().getTestcases().getAcquiredEntailedTestcases());
    }

    @Override
    protected TestcaseType getNonEntailedType() {
        return TestcaseType.ACQUIRED_NON_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getNonEntailedTestcases() {
        return new ArrayList<>(getEditorKitHook().getActiveOntologyDiagnosisSearcher().getTestcases().getAcquiredNonEntailedTestcases());
    }
}
