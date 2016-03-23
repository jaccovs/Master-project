package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.buttons.AddToBackgroundButton;
import org.exquisite.protege.ui.buttons.CreateBackgroundAxiomButton;
import org.exquisite.protege.ui.buttons.RemoveFromBackgroundButton;
import org.exquisite.protege.ui.component.BackgroundAxiomFinder;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 24.09.12
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public class BackgroundView extends AbstractQueryViewComponent {

    private BasicAxiomList backgroundAxiomList;

    private BasicAxiomList ontologyAxiomList;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        backgroundAxiomList = new BasicAxiomList(getOWLEditorKit());
        ontologyAxiomList = new BasicAxiomList(getOWLEditorKit());

        Box box = Box.createVerticalBox();

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.add(createAxiomCreationToolBar(),BorderLayout.NORTH);
        backgroundPanel.add(ComponentFactory.createScrollPane(backgroundAxiomList),BorderLayout.CENTER);
        box.add(backgroundPanel);

        JPanel ontologyPanel = new JPanel(new BorderLayout());
        ontologyPanel.add(createSwitchAxiomsToolBar(), BorderLayout.NORTH);
        ontologyPanel.add(ComponentFactory.createScrollPane(ontologyAxiomList),BorderLayout.CENTER);
        box.add(ontologyPanel);

        add(box, BorderLayout.CENTER);
        updateDisplayedBackgroundAxioms();
        updateDisplayedOntologyAxioms();

    }

    private OWLEntity selectedEntity = null;

    public void setSelectedEntity(OWLEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public OWLEntity getSelectedEntity() {
        return selectedEntity;
    }


    public BasicAxiomList getBackgroundAxiomList() {
        return backgroundAxiomList;
    }

    public BasicAxiomList getOntologyAxiomList() {
        return ontologyAxiomList;
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
        label.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        return label;
    }

    protected JToolBar createSwitchAxiomsToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Ontology Axioms"));
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel axiomFinderPanel = new JPanel();
        axiomFinderPanel.add(new BackgroundAxiomFinder(this,getOWLEditorKit()));
        toolBar.add(axiomFinderPanel);
        toolBar.add(new AddToBackgroundButton(this));
        toolBar.add(new RemoveFromBackgroundButton(this));
        toolBar.setMaximumSize(toolBar.getPreferredSize());

        return toolBar;
    }

    protected JToolBar createAxiomCreationToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Background Axioms"));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new CreateBackgroundAxiomButton(this));
        toolBar.setMaximumSize(toolBar.getPreferredSize());

        return toolBar;
    }

    protected void copySet(Set<OWLLogicalAxiom> from, Set<OWLLogicalAxiom> to) {
        for (OWLLogicalAxiom axiom : from)
            to.add(axiom);
    }


    public void updateDisplayedOntologyAxioms() { // TODO

        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        Set<OWLLogicalAxiom> ontologyAxMinusBg = new LinkedHashSet<OWLLogicalAxiom>();
        copySet(ontology.getLogicalAxioms(),ontologyAxMinusBg);
        ontologyAxiomList.updateList(ontologyAxMinusBg,ontology);
        /*
        SearchCreator searchCreator = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getSearchCreator();
        Set<OWLLogicalAxiom> bgAxioms = searchCreator.getSearch().getSearchable().
                getKnowledgeBase().getBackgroundFormulas();
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();

        Set<OWLLogicalAxiom> ontologyAxMinusBg = new LinkedHashSet<OWLLogicalAxiom>();
        if (selectedEntity==null)
            copySet(ontology.getLogicalAxioms(),ontologyAxMinusBg);
        else {
            for (OWLLogicalAxiom axiom : ontology.getLogicalAxioms()) {
                if (axiom.getSignature().contains(selectedEntity))
                    ontologyAxMinusBg.add(axiom);
            }
        }
        ontologyAxMinusBg.removeAll(bgAxioms);
        ontologyAxiomList.updateList(ontologyAxMinusBg,ontology);
        */
    }

    protected void updateDisplayedBackgroundAxioms() { // TODO
        /*
        SearchCreator searchCreator = getEditorKitHook().getActiveOntologyDiagnosisSearcher().getSearchCreator();
        Set<OWLLogicalAxiom> bgAxioms = searchCreator.getSearch().getSearchable().
                getKnowledgeBase().getBackgroundFormulas();
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        backgroundAxiomList.updateList(bgAxioms,ontology);
        */
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateDisplayedBackgroundAxioms();
        updateDisplayedOntologyAxioms();
    }

}
