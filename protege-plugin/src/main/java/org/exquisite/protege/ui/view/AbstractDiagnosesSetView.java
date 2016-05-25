package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.ui.list.SimpleAxiomList;
import org.exquisite.protege.ui.list.SimpleDiagnosisList;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public abstract class AbstractDiagnosesSetView extends AbstractListQueryViewComponent {

    public SimpleDiagnosisList getList() {
        return (SimpleDiagnosisList) super.getList();
    }

    protected void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses) {
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        getList().updateList(diagnoses,ontology,getHeaderPrefix(),isIncludeMeasure());
    }

    @Override
    protected JComponent createListForComponent() {
        return new SimpleDiagnosisList(getOWLEditorKit(),getHeaderColor());
    }

    protected abstract boolean isIncludeMeasure();

    protected abstract Color getHeaderColor();

    protected abstract String getHeaderPrefix();

}
