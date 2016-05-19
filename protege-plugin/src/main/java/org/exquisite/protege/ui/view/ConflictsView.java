package org.exquisite.protege.ui.view;

/*
import at.ainf.diagnosis.storage.FormulaSet;
import at.ainf.protegeview.model.OntologyDiagnosisSearcher;
import at.ainf.protegeview.model.configuration.DiagnosisEngineFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
*/
import javax.swing.event.ChangeEvent;
import java.awt.*;
//import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
public class ConflictsView extends AbstractAxiomSetView {

    @Override
    public void stateChanged(ChangeEvent e) {
        /*
        DiagnosisEngineFactory searchCreator = ((OntologyDiagnosisSearcher) e.getSource()).getSearchCreator();
        Set<FormulaSet<OWLLogicalAxiom>> setOfFormulaSets = searchCreator.getSearch().getConflicts();
        updateList(setOfFormulaSets);
        */
    }

    @Override
    protected boolean isIncludeMeasure() {
        return false;
    }

    @Override
    protected Color getHeaderColor() {
        return new Color(52, 79, 255, 139);
    }

    @Override
    protected String getHeaderPrefix() {
        return "Conflict ";
    }

}
