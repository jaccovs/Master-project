package org.exquisite.protege.ui.view;

/*
import at.ainf.diagnosis.storage.FormulaSet;
import at.ainf.protegeview.gui.AbstractListQueryViewComponent;
import at.ainf.protegeview.gui.axiomsetviews.axiomslist.SimpleAxiomList;
*/
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAxiomSetView extends AbstractListQueryViewComponent {
/* TODO
    public SimpleAxiomList getList() {
        return (SimpleAxiomList) super.getList();
    }

    protected void updateList(Set<FormulaSet<OWLLogicalAxiom>> setOfFormulaSets) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        getList().updateList(setOfFormulaSets,ontology,getHeaderPrefix(),isIncludeMeasure());
    }
*/
    @Override
    protected JComponent createListForComponent() {
        // return new SimpleAxiomList(getOWLEditorKit(),getHeaderColor()); TODO
        return null;
    }

    protected abstract boolean isIncludeMeasure();

    protected abstract Color getHeaderColor();

    protected abstract String getHeaderPrefix();

}
