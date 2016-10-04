package org.exquisite.protege.ui.view;

import org.exquisite.core.model.DiagnosisModel;
import org.exquisite.protege.model.event.EventType;
import org.exquisite.protege.model.event.OntologyDebuggerChangeEvent;
import org.exquisite.protege.ui.list.BasicAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

import static org.exquisite.protege.model.event.EventType.ACTIVE_ONTOLOGY_CHANGED;
import static org.exquisite.protege.model.event.EventType.INPUT_ONTOLOGY_CHANGED;
import static org.exquisite.protege.model.event.EventType.SESSION_STATE_CHANGED;

/**
 * A view to present the set of correct and possibly faulty axioms in our input ontology.
 * To be more precise, this view represents a direct mapping of the diagnosis model used in the debugger.
 */
public class InputOntologyView extends AbstractQueryViewComponent {

    private BasicAxiomList correctAxiomsList;

    private BasicAxiomList possiblyFaultyAxiomsList;

    private final int pageSize = 100;

    private int currPageNum = 1;

    private int lastPageNum = Integer.MAX_VALUE;

    private JButton first, prev, next, last;

    private JLabel infoLabel = null;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        correctAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, true);
        possiblyFaultyAxiomsList = new BasicAxiomList(getOWLEditorKit(), this, false);

        Box box = Box.createVerticalBox();

        JPanel correctAxiomsPanel = new JPanel(new BorderLayout());
        correctAxiomsPanel.add(createCorrectAxiomsToolBar(),BorderLayout.NORTH);
        correctAxiomsPanel.add(ComponentFactory.createScrollPane(correctAxiomsList),BorderLayout.CENTER);
        box.add(correctAxiomsPanel);

        JPanel possiblyFaultyPanel = new JPanel(new BorderLayout());
        possiblyFaultyPanel.add(createPossiblyFaultyAxiomsToolBar(), BorderLayout.NORTH);
        possiblyFaultyPanel.add(ComponentFactory.createScrollPane(possiblyFaultyAxiomsList),BorderLayout.CENTER);
        box.add(possiblyFaultyPanel);

        add(box, BorderLayout.CENTER);
        updateDisplayedCorrectAxioms();
        updateDisplayedPossiblyFaultyAxioms();
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        return toolBar;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize()+1));
        return label;
    }

    private JLabel createSizeLabel() {
        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.ITALIC, label.getFont().getSize()-1));
        return label;
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

        return toolBar;
    }

    private void start() {
        currPageNum = 1;
        updatePage();
    }

    private void prev() {
        if (--currPageNum <= 0)
            currPageNum = 1;
        updatePage();
    }

    private void next() {
        if (++currPageNum > lastPageNum)
            currPageNum = lastPageNum;
        updatePage();
    }

    private void end() {
        currPageNum = lastPageNum;
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

    private JToolBar createCorrectAxiomsToolBar() {
        JToolBar toolBar = createToolBar();
        toolBar.add(createLabel("Correct Axioms (Background)"));
        toolBar.setToolTipText("Axioms from the background are considered to be correct and therefore are not candidates for diagnoses.");
        toolBar.add(Box.createVerticalStrut(25));
        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    /**
     * Updates the view of displayed possible faulty axioms.
     * @see #stateChanged(ChangeEvent)
     */
    public void updateDisplayedPossiblyFaultyAxioms() {
        updatePage();
    }

    private void updatePage( ) {
        final OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
        BasicAxiomList list = possiblyFaultyAxiomsList;

        List<OWLLogicalAxiom> axioms = new ArrayList<>(diagnosisModel.getPossiblyFaultyFormulas());
        axioms.retainAll(ontology.getLogicalAxioms());
        Collections.sort(axioms);

        //work out how many pages there are
        this.lastPageNum = axioms.size() / pageSize + (axioms.size() % pageSize != 0 ? 1 : 0);

        //replace the list's model with a new model containing
        //only the entries in the current page.
        List<OWLLogicalAxiom> axiomsToDisplay = new ArrayList<>();
        final int start = (currPageNum - 1) * pageSize;
        int end = start + pageSize;
        if (end >= axioms.size()) {
            end = axioms.size();
        }
        for (int i = start; i < end; i++) {
            axiomsToDisplay.add(axioms.get(i));
        }
        list.updateList(axiomsToDisplay,ontology);

        // update buttons
        final boolean canGoBack = currPageNum != 1;
        final boolean canGoFwd = currPageNum != lastPageNum;
        first.setEnabled(canGoBack);
        prev.setEnabled(canGoBack);
        next.setEnabled(canGoFwd);
        last.setEnabled(canGoFwd);

        // update tooltips
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

        // update size label
        if (axioms.size() > 0)
            infoLabel.setText((start+1) + "-" + (end) + " of " + axioms.size());
        else
            infoLabel.setText("");
    }

    /**
     * Updates the view of displayed correct axioms.
     * @see #stateChanged(ChangeEvent)
     */
    private void updateDisplayedCorrectAxioms() {
        final DiagnosisModel<OWLLogicalAxiom> diagnosisModel = getEditorKitHook().getActiveOntologyDebugger().getDiagnosisModel();
        updateDisplayedAxioms(correctAxiomsList, diagnosisModel.getCorrectFormulas());
    }

    /**
     * Updates a list with a set of axioms that exists in the active ontology.
     *
     * @param list The list to update.
     * @param axioms The axioms to update the list with after a check if all axioms doe exist in the active ontology.
     * @see #updateDisplayedCorrectAxioms()
     * @see #updateDisplayedPossiblyFaultyAxioms()
     */
    private void updateDisplayedAxioms(BasicAxiomList list, java.util.List<OWLLogicalAxiom> axioms) {
        final OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();

        // show only those axioms that do also exist in the active ontology and show them in a sorted order (TreeSet)
        Set<OWLLogicalAxiom> axiomsToDisplay = new TreeSet<>(axioms);
        axiomsToDisplay.retainAll(ontology.getLogicalAxioms());
        list.updateList(axiomsToDisplay, ontology);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        final EventType type = ((OntologyDebuggerChangeEvent) e).getType();
        if (EnumSet.of(ACTIVE_ONTOLOGY_CHANGED, SESSION_STATE_CHANGED, INPUT_ONTOLOGY_CHANGED).contains(type)) {
            updateDisplayedCorrectAxioms();
            updateDisplayedPossiblyFaultyAxioms();
        }
    }

}