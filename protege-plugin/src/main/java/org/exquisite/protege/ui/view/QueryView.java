package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.GetAlternativeQueryButton;
import org.exquisite.protege.ui.list.QueryAxiomList;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * A view used for stating queries to the user.
 */
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
        updateView();
    }

    private void updateView() {
        final OntologyDebugger debugger = getEditorKitHook().getActiveOntologyDebugger();
        getList().updateList(debugger, debugger.getDiagnosisEngineFactory().getOntology());
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

        OntologyDebugger debugger = (OntologyDebugger) e.getSource();
        switch(debugger.getQuerySearchStatus()) {
            case ASKING_QUERY:
                OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
                getList().updateList(debugger,ontology);
                break;
            case IDLE:
                getList().clearList();
                break;
        }

        getAlternativeQueryButton.setEnabled(false); // TODO NOT YET IMPLEMENTED
        commitAndGetNextButton.setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

}
