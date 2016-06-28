package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.GetAlternativeQueryButton;
import org.exquisite.protege.ui.list.QueryAxiomList;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class QueryView extends AbstractListQueryViewComponent {

    private GetAlternativeQueryButton getAlternativeQueryButton;
    private CommitAndGetNextButton commitAndGetNextButton;

    private JToolBar createNewQueryToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);
        toolBar.add(Box.createHorizontalGlue());
        getAlternativeQueryButton = new GetAlternativeQueryButton(this);
        commitAndGetNextButton = new CommitAndGetNextButton(this);
        toolBar.add(getAlternativeQueryButton);
        toolBar.add(commitAndGetNextButton);
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

        OntologyDiagnosisSearcher s = (OntologyDiagnosisSearcher) e.getSource();
        switch(s.getQuerySearchStatus()) {
            case ASKING_QUERY:
                OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
                getList().updateList(s,ontology);
                break;
            case IDLE:
                getList().clearList();
                break;
        }

        getAlternativeQueryButton.setEnabled(false); // TODO NOT YET IMPLEMENTED
        commitAndGetNextButton.setEnabled(s.isSessionRunning() && s.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

}
