package org.exquisite.protege.ui.list;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.header.TestcaseListHeader;
import org.exquisite.protege.ui.list.item.RepairTestcaseListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wolfi
 */
public class RepairTestCasesAxiomList extends AbstractTestcaseAxiomList {

    public RepairTestCasesAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
    }

    @Override
    public void updateView() {
        List<Object> items = new ArrayList<>();
        addToItems(items, getEntailedType(), getEntailedTestcases());
        addToItems(items, getNonEntailedType(), getNonEntailedTestcases());

        setListData(items.toArray());
    }

    private void addToItems(List<Object> items, Debugger.TestcaseType type, List<OWLLogicalAxiom> testcases) {
        OWLOntology ontology = getEditorKit().getModelManager().getActiveOntology();

        items.add(new TestcaseListHeader(type));
        items.addAll(testcases.stream().map(axiom -> new RepairTestcaseListItem(axiom, type, ontology, this.editorKitHook.getActiveOntologyDebugger())).collect(Collectors.toList()));
        items.add(" ");
    }

    @Override
    protected Debugger.TestcaseType getEntailedType() {
        return Debugger.TestcaseType.ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getEntailedTestcases() {
        final List<OWLLogicalAxiom> axioms = new ArrayList<>();
        // adding the acquired entailed test cases
        axioms.addAll(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getAcquiredEntailedTestcases());
        // adding the original entailed test cases
        axioms.addAll(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getOriginalEntailedTestcases());
        return axioms;
    }

    @Override
    protected Debugger.TestcaseType getNonEntailedType() {
        return Debugger.TestcaseType.NON_ENTAILED_TC;
    }

    @Override
    protected List<OWLLogicalAxiom> getNonEntailedTestcases() {
        final List<OWLLogicalAxiom> axioms = new ArrayList<>();
        // adding the acquired non entailed test cases
        axioms.addAll(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getAcquiredNonEntailedTestcases());
        // adding the original non entailed test cases
        axioms.addAll(getEditorKitHook().getActiveOntologyDebugger().getTestcases().getOriginalNonEntailedTestcases());
        return axioms;
    }

}
