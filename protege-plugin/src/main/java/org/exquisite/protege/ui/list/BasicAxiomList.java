package org.exquisite.protege.ui.list;

import org.exquisite.protege.ui.buttons.MoveToCorrectAxiomsButton;
import org.exquisite.protege.ui.buttons.MoveToPossiblyFaultyAxiomsButton;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.view.InputOntologyView;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BasicAxiomList extends AbstractAxiomList {

    private boolean isBackground;

    private InputOntologyView view;

    public BasicAxiomList(OWLEditorKit editorKit, InputOntologyView view, boolean isBackground, TransferHandler transferHandler) {
        super(editorKit);
        this.isBackground = isBackground;
        this.view = view;

        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        setTransferHandler(transferHandler);
    }

    @Override
    protected List<MListButton> getButtons(Object value) {

        List<MListButton> buttons = new ArrayList<>();
        buttons.addAll(super.getButtons(value));

        if (! view.getEditorKitHook().getActiveOntologyDebugger().isSessionRunning()) {
            if (!isBackground)
                buttons.add(new MoveToCorrectAxiomsButton((AxiomListItem) value, view));
            else
                buttons.add(new MoveToPossiblyFaultyAxiomsButton((AxiomListItem) value, view));
        }

        return buttons;
    }

    public void updateList(Collection<OWLLogicalAxiom> axioms, OWLOntology ontology) {
        List<Object> items = axioms.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList());
        if (items != null) setListData(items.toArray());
        if (view.getEditorKitHook()!=null && view.getEditorKitHook().getActiveOntologyDebugger() != null)
            setDragEnabled(! view.getEditorKitHook().getActiveOntologyDebugger().isSessionRunning());
    }

}
