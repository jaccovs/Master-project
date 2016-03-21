package org.exquisite.protege.ui.view;

/* TODO
import at.ainf.diagnosis.storage.FormulaSet;
import at.ainf.protegeview.gui.buttons.StartButton;
import at.ainf.protegeview.model.configuration.SearchCreator;
*/
import org.exquisite.protege.ui.view.buttons.StartButton;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

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

    private StartButton startButton;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        add(createDiagnosesToolBar(), BorderLayout.NORTH);
        updateView();
    }

    protected JToolBar createDiagnosesToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setFloatable(false);
        startButton = new StartButton(this);
        toolBar.add(startButton);
        toolBar.add(Box.createHorizontalGlue());

        return toolBar;
    }

    private void updateView() {
        /* TODO
        SearchCreator searchCreator = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getSearchCreator();
        Set<FormulaSet<OWLLogicalAxiom>> setOfFormulaSets = searchCreator.getSearch().getDiagnoses();
        updateList(setOfFormulaSets);
        */
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
