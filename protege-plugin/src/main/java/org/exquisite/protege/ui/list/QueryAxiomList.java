package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Query;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.buttons.AxiomIsEntailedButton;
import org.exquisite.protege.ui.buttons.AxiomIsNotEntailedButton;
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

public class QueryAxiomList extends AssertedOrInferredAxiomList {

    public QueryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit, editorKitHook);
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        List<MListButton> buttons = new ArrayList<>();
        Debugger debugger = editorKitHook.getActiveOntologyDebugger();
        OWLLogicalAxiom axiom = ((AxiomListItem) value).getAxiom();
        buttons.addAll(super.getButtons(value));
        buttons.add(new AxiomIsEntailedButton("Yes, this axiom in entailed in the ontology", this, debugger.isMarkedEntailed(axiom)));
        buttons.add(new AxiomIsNotEntailedButton("No, this axiom is not entailed in this ontology", this, debugger.isMarkedNonEntailed(axiom)));

        return buttons;
    }

    public void handleEntailed() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom()  ;
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

    public void handleNotEntailed() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom()  ;
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
        Query<OWLLogicalAxiom> query = debugger.getActualQuery();
        if (query!=null && !query.formulas.isEmpty()) {
            List<Object> items = query.formulas.stream().map(axiom -> new QueryAxiomListItem(axiom, ontology, debugger)).collect(Collectors.toList());
            setListData(items.toArray());
        }
    }

}
