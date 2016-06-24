package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.ui.buttons.StartDebuggingButton;
import org.exquisite.protege.ui.buttons.StopDebuggingButton;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Set;

public class DiagnosesView extends AbstractDiagnosesSetView {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DiagnosesView.class.getName());

    private StartDebuggingButton startDebuggingButton;
    private StopDebuggingButton stopDebuggingButton;


    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        add(createDiagnosesToolBar(), BorderLayout.NORTH);
        updateView();
    }

    protected JToolBar createDiagnosesToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);
        startDebuggingButton = new StartDebuggingButton(this);
        toolBar.add(startDebuggingButton);
        toolBar.add(Box.createHorizontalGlue());
        stopDebuggingButton = new StopDebuggingButton(this);
        toolBar.add(stopDebuggingButton);

        return toolBar;
    }

    private void updateView() {

        final OntologyDiagnosisSearcher ods = getEditorKitHook().getActiveOntologyDiagnosisSearcher();
        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = ods.getDiagnoses();
        logger.debug("updateView: got diagnoses: " + diagnoses);
        updateList(diagnoses);

        startDebuggingButton.setEnabled(!ods.isSessionRunning());
        stopDebuggingButton.setEnabled(ods.isSessionRunning());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateView();
    }

    @Override
    protected Color getHeaderColor() {
        return new Color(85, 255, 97, 174);
    }

    @Override
    protected String getHeaderPrefix() {
        return "Diagnosis ";
    }

    @Override
    protected boolean isIncludeMeasure() {
        return true;
    }

}
