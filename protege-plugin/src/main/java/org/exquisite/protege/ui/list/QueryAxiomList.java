package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Query;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.buttons.AxiomIsEntailedButton;
import org.exquisite.protege.ui.buttons.AxiomIsNotEntailedButton;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.DebugExplainButton;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryAxiomList extends AbstractAxiomList {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(QueryAxiomList.class.getName());

    private EditorKitHook editorKitHook;

    private CommitAndGetNextButton commitAndGetNextButton;

    public QueryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook, CommitAndGetNextButton commitAndGetNextButton) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
        this.commitAndGetNextButton = commitAndGetNextButton;
    }

    @Override
    protected List<MListButton> getButtons(Object value) {

        List<MListButton> buttons = new ArrayList<>();
        OntologyDiagnosisSearcher s = editorKitHook.getActiveOntologyDiagnosisSearcher();
        OWLLogicalAxiom axiom = ((AxiomListItem) value).getAxiom();
        buttons.addAll(super.getButtons(value));
        buttons.add(new AxiomIsEntailedButton(this,s.isMarkedEntailed(axiom)));
        buttons.add(new AxiomIsNotEntailedButton(this,s.isMarkedNonEntailed(axiom)));
        buttons.add(new DebugExplainButton(this));

        return buttons;
    }

    public void handleEntailed() {
        logger.debug("handle entailed");

        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom()  ;
        OntologyDiagnosisSearcher s = editorKitHook.getActiveOntologyDiagnosisSearcher();
        if (s.isMarkedEntailed(axiom)) {
            s.doRemoveAxiomsMarkedEntailed(axiom);
        } else if (s.isMarkedNonEntailed(axiom)) {
            s.doRemoveAxiomsMarkedNonEntailed(axiom);
            s.doAddAxiomsMarkedEntailed(axiom);
        } else {
            s.doAddAxiomsMarkedEntailed(axiom);
        }

        commitAndGetNextButton.setEnabled(s.isSessionRunning() && s.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

    public void handleNotEntailed() {
        logger.debug("handle notEntailed");

        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom()  ;
        OntologyDiagnosisSearcher s = editorKitHook.getActiveOntologyDiagnosisSearcher();
        if (s.isMarkedNonEntailed(axiom)) {
            s.doRemoveAxiomsMarkedNonEntailed(axiom);
        } else if (s.isMarkedEntailed(axiom)) {
            s.doRemoveAxiomsMarkedEntailed(axiom);
            s.doAddAxiomsMarkedNonEntailed(axiom);
        } else {
            s.doAddAxiomsMarkedNonEntailed(axiom);
        }

        commitAndGetNextButton.setEnabled(s.isSessionRunning() && s.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

    public void handleAxiomExplain() {
        Object obj = getSelectedValue();
        if (!(obj instanceof AxiomListItem))
            return;
        AxiomListItem item = (AxiomListItem) obj;
        OWLAxiom axiom = item.getAxiom();
        ExplanationManager explanationMngr = editorKitHook.getOWLEditorKit().getModelManager().getExplanationManager();
        if (explanationMngr.hasExplanation(axiom)) {
            explanationMngr.handleExplain((Frame) SwingUtilities.getAncestorOfClass(Frame.class, this), axiom);
        }
    }

    public void clearList() {
        setListData(new ArrayList<>().toArray());
    }

    public void updateList(OntologyDiagnosisSearcher diagnosisSearcher, OWLOntology ontology) {
        Query<OWLLogicalAxiom> query = diagnosisSearcher.getActualQuery();
        List<Object> items = query.formulas.stream().map(axiom -> new QueryAxiomListItem(axiom, ontology)).collect(Collectors.toList());

        setListData(items.toArray());
    }

}
