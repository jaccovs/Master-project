package org.exquisite.protege.ui.view;

import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.exquisite.protege.ui.buttons.AddToBackgroundButton;
import org.exquisite.protege.ui.buttons.CreateBackgroundAxiomButton;
import org.exquisite.protege.ui.buttons.RemoveFromBackgroundButton;
import org.exquisite.protege.ui.component.BackgroundAxiomFinder;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.*;

public class BackgroundView extends AbstractQueryViewComponent {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(BackgroundView.class.getName());

    private BasicAxiomList backgroundAxiomsList;

    private BasicAxiomList possiblyFaultyAxiomsList;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        backgroundAxiomsList = new BasicAxiomList(getOWLEditorKit());
        possiblyFaultyAxiomsList = new BasicAxiomList(getOWLEditorKit());

        Box box = Box.createVerticalBox();

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.add(createAxiomCreationToolBar(),BorderLayout.NORTH);
        backgroundPanel.add(ComponentFactory.createScrollPane(backgroundAxiomsList),BorderLayout.CENTER);
        box.add(backgroundPanel);

        JPanel kbPanel = new JPanel(new BorderLayout());
        kbPanel.add(createSwitchAxiomsToolBar(), BorderLayout.NORTH);
        kbPanel.add(ComponentFactory.createScrollPane(possiblyFaultyAxiomsList),BorderLayout.CENTER);
        box.add(kbPanel);

        add(box, BorderLayout.CENTER);
        updateDisplayedBackgroundAxioms();
        updateDisplayedPossiblyFaultyAxioms();

    }

    private OWLEntity selectedEntity = null;

    public void setSelectedEntity(OWLEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public OWLEntity getSelectedEntity() {
        return selectedEntity;
    }


    public BasicAxiomList getBackgroundAxiomsList() {
        return backgroundAxiomsList;
    }

    public BasicAxiomList getPossiblyFaultyAxiomsList() {
        return possiblyFaultyAxiomsList;
    }

    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        return toolBar;
    }

    protected JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        //label.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize()+1));
        return label;
    }

    protected JToolBar createSwitchAxiomsToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Possibly Faulty Axioms (KB)"));
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel axiomFinderPanel = new JPanel();
        axiomFinderPanel.add(new BackgroundAxiomFinder(this,getOWLEditorKit()));
        toolBar.add(axiomFinderPanel);
        toolBar.add(new AddToBackgroundButton(this));
        toolBar.add(new RemoveFromBackgroundButton(this));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        toolBar.setToolTipText("Axioms from the knowledge base are possible candidates for diagnoses.");

        return toolBar;
    }

    protected JToolBar createAxiomCreationToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Correct Axioms (Background)"));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new CreateBackgroundAxiomButton(this));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        toolBar.setToolTipText("Axioms from the background are to be considered correct and hence are no candidates for diagnoses.");

        return toolBar;
    }

    protected void copySet(Set<OWLLogicalAxiom> from, Set<OWLLogicalAxiom> to) {
        for (OWLLogicalAxiom axiom : from)
            to.add(axiom);
    }

    public void updateDisplayedPossiblyFaultyAxioms() {
        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        java.util.List<OWLLogicalAxiom> bgAxioms = diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas();
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();

        Set<OWLLogicalAxiom> ontologyAxMinusBg = new LinkedHashSet<>();
        if (selectedEntity==null)
            copySet(ontology.getLogicalAxioms(),ontologyAxMinusBg);
        else {
            for (OWLLogicalAxiom axiom : ontology.getLogicalAxioms()) {
                if (axiom.getSignature().contains(selectedEntity))
                    ontologyAxMinusBg.add(axiom);
            }
        }
        ontologyAxMinusBg.removeAll(bgAxioms);
        possiblyFaultyAxiomsList.updateList(ontologyAxMinusBg,ontology);

    }

    protected void updateDisplayedBackgroundAxioms() {

        DiagnosisEngineFactory diagnosisEngineFactory = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<OWLLogicalAxiom> bgAxioms = new HashSet<>(diagnosisEngineFactory.getDiagnosisEngine().getSolver().getDiagnosisModel().getCorrectFormulas());
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        backgroundAxiomsList.updateList(bgAxioms,ontology);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        logger.debug("stateChanged(" + e + ")");
        updateDisplayedBackgroundAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

}
