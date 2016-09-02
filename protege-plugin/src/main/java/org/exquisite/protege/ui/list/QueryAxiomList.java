package org.exquisite.protege.ui.list;

import org.exquisite.core.query.Query;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.model.QueryExplanation;
import org.exquisite.protege.ui.buttons.AxiomIsEntailedButton;
import org.exquisite.protege.ui.buttons.AxiomIsNotEntailedButton;
import org.exquisite.protege.ui.buttons.DebugExplainButton;
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

public class QueryAxiomList extends AbstractAxiomList {

    private EditorKitHook editorKitHook;

    public QueryAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);
        this.editorKitHook = editorKitHook;
    }

    @Override
    protected List<MListButton> getButtons(Object value) {
        List<MListButton> buttons = new ArrayList<>();
        OntologyDebugger debugger = editorKitHook.getActiveOntologyDebugger();
        OWLLogicalAxiom axiom = ((AxiomListItem) value).getAxiom();
        buttons.addAll(super.getButtons(value));
        buttons.add(new AxiomIsEntailedButton(this,debugger.isMarkedEntailed(axiom)));
        buttons.add(new AxiomIsNotEntailedButton(this,debugger.isMarkedNonEntailed(axiom)));
        if (QueryExplanation.isAxiomInferredFromDebugger(debugger,axiom))
            buttons.add(new DebugExplainButton(this));

        return buttons;
    }

    public void handleEntailed() {
        OWLLogicalAxiom axiom = ((AxiomListItem) getSelectedValue()).getAxiom()  ;
        OntologyDebugger debugger = editorKitHook.getActiveOntologyDebugger();
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
        OntologyDebugger debugger = editorKitHook.getActiveOntologyDebugger();
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

    public void updateList(OntologyDebugger debugger, OWLOntology ontology) {
        Query<OWLLogicalAxiom> query = debugger.getActualQuery();
        if (query!=null && !query.formulas.isEmpty()) {
            List<Object> items = query.formulas.stream().map(axiom -> new QueryAxiomListItem(axiom, ontology)).collect(Collectors.toList());
            setListData(items.toArray());
        }
    }

}
