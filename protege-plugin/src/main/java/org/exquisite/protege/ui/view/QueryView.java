package org.exquisite.protege.ui.view;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.buttons.CommitAndGetNextButton;
import org.exquisite.protege.ui.buttons.StartDebuggingButton;
import org.exquisite.protege.ui.buttons.StopDebuggingButton;
import org.exquisite.protege.ui.list.QueryAxiomList;
import org.protege.editor.core.ui.list.MList;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * A view used for stating queries to the user.
 */
public class QueryView extends AbstractListViewComponent {

    private StartDebuggingButton startDebuggingButton;
    private StopDebuggingButton stopDebuggingButton;
    private CommitAndGetNextButton commitAndGetNextButton;

    private JToolBar createNewQueryToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);

        startDebuggingButton = new StartDebuggingButton(this);
        stopDebuggingButton = new StopDebuggingButton(this);
        toolBar.add(startDebuggingButton);
        toolBar.add(stopDebuggingButton);

        toolBar.add(Box.createHorizontalGlue());

        commitAndGetNextButton = new CommitAndGetNextButton(this);
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
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();
        ((QueryAxiomList)getList()).updateList(debugger, debugger.getDiagnosisEngineFactory().getOntology());
    }

    @Override
    protected MList createListForComponent() {
        return new QueryAxiomList(getOWLEditorKit(),getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        final Debugger debugger = (Debugger) e.getSource();
        switch(debugger.getQuerySearchStatus()) {
            case ASKING_QUERY:
                OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
                ((QueryAxiomList)getList()).updateList(debugger,ontology);
                break;
            case IDLE:
                ((QueryAxiomList)getList()).clearList();
                break;
        }

        startDebuggingButton.updateView(debugger);
        stopDebuggingButton.updateView(debugger);
        commitAndGetNextButton.setEnabled(debugger.isSessionRunning() && debugger.sizeOfEntailedAndNonEntailedAxioms() > 0);
    }

}
