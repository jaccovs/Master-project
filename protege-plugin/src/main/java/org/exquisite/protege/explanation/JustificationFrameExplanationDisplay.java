package org.exquisite.protege.explanation;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 19/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, code changes by @author wolfi
 */
public class JustificationFrameExplanationDisplay extends JPanel implements ExplanationDisplay, AxiomSelectionListener {

    private Explanation<OWLAxiom> explanation;
    
    private JustificationFrame frame;

    private final JustificationFrameList frameList;

    private WorkbenchManager workbenchManager;

    private AxiomSelectionModel axiomSelectionModel;
    
    private boolean transmittingSelectionToModel = false;

    JustificationFrameExplanationDisplay(OWLEditorKit editorKit, AxiomSelectionModel selectionModel, WorkbenchManager workbenchManager, Explanation<OWLAxiom> explanation) {
        this.workbenchManager = workbenchManager;
        this.axiomSelectionModel = selectionModel;
        this.explanation = explanation;
        frame = new JustificationFrame(editorKit);
        setLayout(new BorderLayout());
        frameList =  new JustificationFrameList(editorKit, selectionModel, workbenchManager, frame);
        add(frameList, BorderLayout.NORTH);
        frame.setRootObject(explanation);
        frameList.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));

        frameList.getSelectionModel().addListSelectionListener(e -> transmitSelectionToModel());

        axiomSelectionModel.addAxiomSelectionListener(new AxiomSelectionListener() {
            public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }

            public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
                respondToAxiomSelectionChange();
            }
        });
    }

    private void respondToAxiomSelectionChange() {
        if(!transmittingSelectionToModel) {
            frameList.clearSelection();
            frameList.repaint(frameList.getVisibleRect());
        }
        frameList.repaint(frameList.getVisibleRect());
    }


    private void transmitSelectionToModel() {
        try {
            transmittingSelectionToModel = true;
            for(int i = 1; i < frameList.getModel().getSize(); i++) {
                Object element = frameList.getModel().getElementAt(i);
                if(element instanceof JustificationFrameSectionRow) {
                    JustificationFrameSectionRow row = (JustificationFrameSectionRow) element;
                    OWLAxiom ax = row.getAxiom();
                    axiomSelectionModel.setAxiomSelected(ax, frameList.isSelectedIndex(i));
                }
            }
        }
        finally {
            transmittingSelectionToModel = false;
        }
    }



    public Explanation<OWLAxiom> getExplanation() {
        return explanation;
    }

    public void dispose() {
        frame.dispose();
        frameList.dispose();
    }

    public void setDisplayLaconicExplanation(boolean b) {
        if (b) {
            Explanation<OWLAxiom> lacExp = getLaconicExplanation();
            if (lacExp != null) {
                frame.setRootObject(lacExp);
            }
        }
        else {
            frame.setRootObject(explanation);
        }
    }

    private Explanation<OWLAxiom> getLaconicExplanation() {
        return workbenchManager.getJustificationManager().getLaconicJustification(explanation);
    }

    public void axiomAdded(AxiomSelectionModel source, OWLAxiom axiom) {
        System.out.println("SEL: " + axiom);
    }

    public void axiomRemoved(AxiomSelectionModel source, OWLAxiom axiom) {
    }
}
