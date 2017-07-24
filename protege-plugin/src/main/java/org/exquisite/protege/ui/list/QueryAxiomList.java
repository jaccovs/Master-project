package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Query;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.buttons.AxiomIsEntailedButton;
import org.exquisite.protege.ui.buttons.AxiomIsNotEntailedButton;
import org.exquisite.protege.ui.buttons.NotSureButton;
import org.exquisite.protege.ui.list.header.InitialQueryListHeader;
import org.exquisite.protege.ui.list.header.InitialQueryListHeaderExplanation;
import org.exquisite.protege.ui.list.header.QueryListHeader;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.list.item.QueryAxiomListItem;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *     A list containing query axioms and used by QueryView.
 * </p>
 *
 * @author wolfi
 */
public class QueryAxiomList extends AssertedOrInferredAxiomList {

    public QueryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        if (value instanceof QueryAxiomListItem) {
            List<MListButton> buttons = new ArrayList<>();
            Debugger debugger = editorKitHook.getActiveOntologyDebugger();
            OWLLogicalAxiom axiom = ((QueryAxiomListItem) value).getAxiom();
            buttons.addAll(super.getButtons(value));
            buttons.add(new NotSureButton("I am not sure about this statement", this, !(debugger.isMarkedEntailed(axiom) || debugger.isMarkedNonEntailed(axiom))));
            buttons.add(new AxiomIsNotEntailedButton("No, this statement is not true", this, debugger.isMarkedNonEntailed(axiom)));
            buttons.add(new AxiomIsEntailedButton("Yes, this statement is true", this, debugger.isMarkedEntailed(axiom)));
            return buttons;
        } else {
            return super.getButtons(value);
        }
    }

    /**
     * The user pressed the entailed button of the list item.
     * @see AxiomIsEntailedButton
     */
    public void handleEntailed() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom();
        Debugger debugger = editorKitHook.getActiveOntologyDebugger();
        if (debugger.isMarkedEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedEntailed(axiom);
        } else if (debugger.isMarkedNonEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedNonEntailed(axiom);
            debugger.doAddAxiomsMarkedEntailed(axiom);
        } else {
            debugger.doAddAxiomsMarkedEntailed(axiom);
        }
    }

    /**
     * The user pressed the non entailed button of the list item.
     * @see AxiomIsNotEntailedButton
     */
    public void handleNotEntailed() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom();
        Debugger debugger = editorKitHook.getActiveOntologyDebugger();
        if (debugger.isMarkedNonEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedNonEntailed(axiom);
        } else if (debugger.isMarkedEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedEntailed(axiom);
            debugger.doAddAxiomsMarkedNonEntailed(axiom);
        } else {
            debugger.doAddAxiomsMarkedNonEntailed(axiom);
        }
    }

    public void handleNotSure() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom();
        Debugger debugger = editorKitHook.getActiveOntologyDebugger();
        if (debugger.isMarkedEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedEntailed(axiom);
        } else if (debugger.isMarkedNonEntailed(axiom)) {
            debugger.doRemoveAxiomsMarkedNonEntailed(axiom);
        }
    }

    /**
     * The user pressed the explanation button of the list item.
     * @see org.exquisite.protege.ui.buttons.DebugExplainButton
     */
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

    public void updateList(Debugger debugger, OWLOntology ontology) {
        List<Object> items = new ArrayList<>();
        Query<OWLLogicalAxiom> query = debugger.getActualQuery();
        if (query==null && debugger.isSessionStopped()) {
            boolean checkCoherency = debugger.getDiagnosisEngineFactory().getSearchConfiguration().reduceIncoherency;
            items.add(new InitialQueryListHeader(checkCoherency));
            items.add(new InitialQueryListHeaderExplanation(checkCoherency));
        } else if (query!=null && !query.formulas.isEmpty()) {
            items.add(new QueryListHeader()); // section header for query view
            items.addAll(query.formulas.stream().map(axiom -> new QueryAxiomListItem(axiom, ontology, debugger)).collect(Collectors.toList()));
        }
        setListData(items.toArray());
    }

}
