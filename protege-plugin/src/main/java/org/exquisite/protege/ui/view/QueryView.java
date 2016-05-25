package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.GetAlternativeQueryButton;
import org.exquisite.protege.ui.buttons.GetQueryButton;
import org.exquisite.protege.ui.list.QueryAxiomList;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class QueryView extends AbstractListQueryViewComponent {

    protected JToolBar createNewQueryToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);
        toolBar.add(new GetQueryButton(this));
        toolBar.add(new GetAlternativeQueryButton(this));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new CommitAndGetNextButton(this));
        toolBar.setMaximumSize(toolBar.getPreferredSize());

        return toolBar;
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        add(createNewQueryToolBar(), BorderLayout.NORTH);

    }

    public QueryAxiomList getList() {
        return (QueryAxiomList) super.getList();
    }

    @Override
    protected JComponent createListForComponent() {
        return new QueryAxiomList(getOWLEditorKit(),getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        OntologyDiagnosisSearcher diagnosisSearcher = (OntologyDiagnosisSearcher) e.getSource();
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        switch(diagnosisSearcher.getQuerySearchStatus()) {
            case ASKING_QUERY:
                getList().updateList(diagnosisSearcher,ontology);
                break;
            case IDLE:
                getList().clearList();
                break;
        }

    }

}
