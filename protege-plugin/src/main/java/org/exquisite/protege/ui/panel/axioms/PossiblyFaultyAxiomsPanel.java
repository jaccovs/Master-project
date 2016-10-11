package org.exquisite.protege.ui.panel.axioms;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.state.PagingState;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.exquisite.protege.ui.panel.search.SearchPanel;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wolfi
 */
public class PossiblyFaultyAxiomsPanel extends AbstractAxiomsPanel {

    private BasicAxiomList possiblyFaultyAxiomsList;

    private JButton first, prev, next, last;

    private JLabel infoLabel = null;

    private JScrollPane scrollPane = null;

    private SearchPanel searchPanel = null;

    private List<OWLLogicalAxiom> axiomsToDisplay = new ArrayList<>();

    /**
     * Number of axioms displayed at once.
     */
    private final static int pageSize = 100;

    private final static Point TOPPOSITION = new Point(0,0);

    public PossiblyFaultyAxiomsPanel(OWLEditorKit editorKit, EditorKitHook editorKitHook, BasicAxiomList possiblyFaultyAxiomsList) {
        super(editorKit, editorKitHook);
        this.possiblyFaultyAxiomsList = possiblyFaultyAxiomsList;

        // toolbar with nav-buttons
        add(createPossiblyFaultyAxiomsToolBar(), BorderLayout.NORTH);

        // search pane containing search field and search options and ...
        JPanel searchAndScrollPane = new JPanel(new BorderLayout());
        this.searchPanel = new SearchPanel(this, getOWLEditorKit(), getEditorKitHook());
        searchAndScrollPane.add(this.searchPanel,BorderLayout.NORTH);
        // ... the list of possibly faulty axioms
        this.scrollPane = ComponentFactory.createScrollPane(possiblyFaultyAxiomsList);
        searchAndScrollPane.add(this.scrollPane,BorderLayout.CENTER);
        add(searchAndScrollPane,BorderLayout.CENTER);
    }

    @Override
    public void updateDisplayedAxioms() {
        updatePage();
    }

    private void updatePage( ) {
        final OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        //final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
        BasicAxiomList list = possiblyFaultyAxiomsList;

        List<OWLLogicalAxiom> axioms = getAxiomsToDisplay(); // getAllPossiblyFaultyLogicalAxioms(ontology, diagnosisModel);

        final PagingState pagingState = getEditorKitHook().getActiveOntologyDebugger().getPagingState();

        // work out how many pages there are
        pagingState.lastPageNum = axioms.size() / pageSize + (axioms.size() % pageSize != 0 ? 1 : 0);

        // replace the list's model with a new model containing
        // only the entries in the current page.
        List<OWLLogicalAxiom> axiomsToDisplay = new ArrayList<>();
        final int start = (pagingState.currPageNum - 1) * pageSize;
        int end = start + pageSize;
        if (end >= axioms.size()) {
            end = axioms.size();
        }
        for (int i = start; i < end; i++) {
            axiomsToDisplay.add(axioms.get(i));
        }
        list.updateList(axiomsToDisplay,ontology);

        // update buttons
        final boolean canGoBack = pagingState.currPageNum != 1;
        final boolean canGoFwd = axioms.size() != 0 && pagingState.currPageNum != pagingState.lastPageNum;
        first.setEnabled(canGoBack);
        prev.setEnabled(canGoBack);
        next.setEnabled(canGoFwd);
        last.setEnabled(canGoFwd);

        // update tooltips for buttons
        if (canGoBack) {
            first.setToolTipText("First " + pageSize + " axioms");
            prev.setToolTipText("Previous " + pageSize + " axioms");
        } else {
            first.setToolTipText(null);
            prev.setToolTipText(null);
        }
        if (canGoFwd) {
            int nextSize = ((end+pageSize) > axioms.size()) ? (axioms.size()-end) : pageSize;
            next.setToolTipText("Next " + nextSize + " axioms");
            last.setToolTipText("Last axioms");
        } else {
            next.setToolTipText(null);
            last.setToolTipText(null);
        }

        // update text of info label
        if (axioms.size() > 0)
            infoLabel.setText((start+1) + "-" + (end) + " of " + axioms.size());
        else
            infoLabel.setText("");

        // position scroll pane to the top position each time a new page is displayed
        this.scrollPane.getViewport().setViewPosition(TOPPOSITION);
    }

    /**
     * Returns the list of axioms to be shown in the panel for possible faulty axioms.
     *
     * @param ontology The active ontology.
     * @param diagnosisModel The diagnosis model backing the debugger.
     * @return The list of axioms to be shown as candidates for possibly faulty axioms (without any search criteria)
     */
    public static List<OWLLogicalAxiom> getAllPossiblyFaultyLogicalAxioms(OWLOntology ontology, DiagnosisModel<OWLLogicalAxiom> diagnosisModel) {
        List<OWLLogicalAxiom> axioms = new ArrayList<>(diagnosisModel.getPossiblyFaultyFormulas());
        axioms.retainAll(ontology.getLogicalAxioms());
        Collections.sort(axioms);
        return axioms;
    }

    private List<OWLLogicalAxiom> getAxiomsToDisplay() {
        return axiomsToDisplay;
    }

    public void setAxiomsToDisplay(List<OWLLogicalAxiom> axiomsToDisplay) {
        this.axiomsToDisplay = axiomsToDisplay;
    }

    private JToolBar createPossiblyFaultyAxiomsToolBar() {
        JToolBar toolBar = createToolBar();

        toolBar.add(createLabel("Possibly Faulty Axioms (KB)"));

        /* TODO reactivate this finder after a working version has been implemented
        toolBar.add(Box.createHorizontalStrut(20));
        JPanel axiomFinderPanel = new JPanel();
        axiomFinderPanel.add(new PossiblyFaultyAxiomsFinder(this,getOWLEditorKit()));
        toolBar.add(axiomFinderPanel);
        */

        toolBar.add(Box.createHorizontalGlue());
        this.infoLabel = createSizeLabel();
        toolBar.add(this.infoLabel);
        toolBar.addSeparator();

        final JPanel controls = createControls();
        toolBar.add(controls, BorderLayout.EAST);
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        toolBar.setToolTipText("Axioms from the knowledge base are possible candidates for diagnoses.");


        //toolBar.add(new SearchPanel(getOWLEditorKit()));

        return toolBar;
    }

    private JLabel createSizeLabel() {
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.ITALIC, label.getFont().getSize()-1));
        return label;
    }

    private void start() {
        getEditorKitHook().getActiveOntologyDebugger().getPagingState().start();
        updatePage();
    }

    private void prev() {
        getEditorKitHook().getActiveOntologyDebugger().getPagingState().prev();
        updatePage();
    }

    private void next() {
        getEditorKitHook().getActiveOntologyDebugger().getPagingState().next();
        updatePage();
    }

    private void end() {
        getEditorKitHook().getActiveOntologyDebugger().getPagingState().end();
        updatePage();
    }

    private JPanel createControls() {
        first = new JButton(new AbstractAction("<<") {
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });

        prev = new JButton(new AbstractAction("<") {
            public void actionPerformed(ActionEvent e) {
                prev();
            }
        });

        next = new JButton(new AbstractAction(">") {
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });

        last = new JButton(new AbstractAction(">>") {
            public void actionPerformed(ActionEvent e) {
                end();
            }
        });

        JPanel bar = new JPanel(new GridLayout(1, 4));
        bar.add(first);
        bar.add(prev);
        bar.add(next);
        bar.add(last);
        return bar;
    }

    public void resetSearchField() {
        this.searchPanel.resetSearchField();
    }
}
