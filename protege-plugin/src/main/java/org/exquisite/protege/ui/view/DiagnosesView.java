package org.exquisite.protege.ui.view;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.ui.buttons.SearchDiagnosesButton;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
public class DiagnosesView extends AbstractAxiomSetView {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(DiagnosesView.class.getName());

    private SearchDiagnosesButton searchDiagnosesButton;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        add(createDiagnosesToolBar(), BorderLayout.NORTH);
        updateView();
    }

    protected JToolBar createDiagnosesToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);
        searchDiagnosesButton = new SearchDiagnosesButton(this);
        toolBar.add(searchDiagnosesButton);
        toolBar.add(Box.createHorizontalGlue());

        return toolBar;
    }

    private void updateView() {

        Set<Diagnosis<OWLLogicalAxiom>> diagnoses = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnoses();
        logger.debug("updateView: got diagnoses: " + diagnoses);
        updateList(diagnoses);
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
