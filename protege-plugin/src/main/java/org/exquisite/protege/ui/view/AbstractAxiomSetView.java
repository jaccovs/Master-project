package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.ui.list.SimpleAxiomList;
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

    public SimpleAxiomList getList() {
        return (SimpleAxiomList) super.getList();
    }

    protected void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        getList().updateList(diagnoses,ontology,getHeaderPrefix(),isIncludeMeasure());
    }

    @Override
    protected JComponent createListForComponent() {
        return new SimpleAxiomList(getOWLEditorKit(),getHeaderColor());
    }

    protected abstract boolean isIncludeMeasure();

    protected abstract Color getHeaderColor();

    protected abstract String getHeaderPrefix();

}
