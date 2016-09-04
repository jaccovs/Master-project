package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.OntologyDebugger;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.StartDebuggingButton;
import org.exquisite.protege.ui.buttons.StopDebuggingButton;
import org.exquisite.protege.ui.list.QueryAxiomList;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * A view used for stating queries to the user.
 */
public class QueryView extends AbstractListQueryViewComponent {

    private StartDebuggingButton startDebuggingButton;
    private StopDebuggingButton stopDebuggingButton;
    //private GetAlternativeQueryButton getAlternativeQueryButton;
    private CommitAndGetNextButton commitAndGetNextButton;

    private JToolBar createNewQueryToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);

        startDebuggingButton = new StartDebuggingButton(this);
        stopDebuggingButton = new StopDebuggingButton(this);
        toolBar.add(startDebuggingButton);
        toolBar.add(stopDebuggingButton);

        toolBar.add(Box.createHorizontalGlue());

        //getAlternativeQueryButton = new GetAlternativeQueryButton(this);
        commitAndGetNextButton = new CommitAndGetNextButton(this);
        //toolBar.add(getAlternativeQueryButton);
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

        final OntologyDebugger debugger = (OntologyDebugger) e.getSource();
        switch(debugger.getQuerySearchStatus()) {
            case ASKING_QUERY:
                OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
                getList().updateList(debugger,ontology);
                break;
            case IDLE:
                getList().clearList();
                break;
        }

        startDebuggingButton.updateView(debugger);
        stopDebuggingButton.updateView(debugger);
        //getAlternativeQueryButton.setEnabled(false); // TODO NOT YET IMPLEMENTED
        commitAndGetNextButton.setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

}
