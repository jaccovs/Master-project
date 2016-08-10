package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.SimpleAxiomList;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

abstract class AbstractAxiomSetView extends AbstractListQueryViewComponent {

    public SimpleAxiomList getList() {
        return (SimpleAxiomList) super.getList();
    }

    protected void updateList(Set<Set<OWLLogicalAxiom>> setOfAxiomsets) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        getList().updateList(setOfAxiomsets,ontology,getHeaderPrefix());
    }

    @Override
    protected JComponent createListForComponent() {
        return new SimpleAxiomList(getOWLEditorKit(), getEditorKitHook(), getHeaderColor(), getHeaderPrefix());
    }

    protected abstract Color getHeaderColor();

    protected abstract String getHeaderPrefix();

}
