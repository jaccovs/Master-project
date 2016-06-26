package org.exquisite.protege.ui.list;

import org.exquisite.protege.ui.buttons.AddToBackgroundButton;
import org.exquisite.protege.ui.buttons.RemoveFromBackgroundButton;
import org.exquisite.protege.ui.view.BackgroundView;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasicAxiomList extends AbstractAxiomList {

    private boolean isBackground;
    private BackgroundView view;

    public BasicAxiomList(OWLEditorKit editorKit, BackgroundView view, boolean isBackground) {
        super(editorKit);
        this.isBackground = isBackground;
        this.view = view;
    }

    @Override
    protected List<MListButton> getButtons(Object value) {

        List<MListButton> buttons = new ArrayList<>();
        buttons.addAll(super.getButtons(value));

        if (! view.getEditorKitHook().getActiveOntologyDiagnosisSearcher().isSessionRunning()) {
            if (!isBackground)
                buttons.add(new AddToBackgroundButton((AxiomListItem) value, view));
            else
                buttons.add(new RemoveFromBackgroundButton((AxiomListItem) value, view));
        }

        return buttons;
    }

    public void updateList(Set<OWLLogicalAxiom> axioms, OWLOntology ontology) {
        List<Object> items = new ArrayList<>();
        for (OWLLogicalAxiom axiom : axioms)
            items.add(new AxiomListItem(axiom,ontology));

        setListData(items.toArray());

    }

}
