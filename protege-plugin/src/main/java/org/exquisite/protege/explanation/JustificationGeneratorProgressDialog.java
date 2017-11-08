package org.exquisite.protege.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Author: Matthew Horridge
 * Stanford University
 * Bio-Medical Informatics Research Group
 * Date: 20/03/2012
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi
 */
public class JustificationGeneratorProgressDialog extends JDialog {

    private ExplanationProgressPanel panel = new ExplanationProgressPanel();

    private ExplanationProgressMonitor<OWLAxiom> progressMonitor;
    
    JustificationGeneratorProgressDialog(Frame owner) {
        super(owner, "Computing explanations", true);
        setContentPane(panel);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlgSize = getSize();
        setLocation(screenSize.width / 2 - dlgSize.width / 2, screenSize.height / 2 - dlgSize.height / 2);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        progressMonitor = new JustificationGeneratorProgressDialogMonitor();
    }

    public void reset() {
        panel.reset();
    }

    ExplanationProgressMonitor<OWLAxiom> getProgressMonitor() {
        return progressMonitor;
    }

    private class JustificationGeneratorProgressDialogMonitor implements ExplanationProgressMonitor<OWLAxiom> {

        public void foundExplanation(ExplanationGenerator<OWLAxiom> owlAxiomExplanationGenerator, Explanation<OWLAxiom> explanation, Set<Explanation<OWLAxiom>> explanations) {
            panel.foundExplanation(owlAxiomExplanationGenerator, explanation, explanations);
        }

        public boolean isCancelled() {
            return panel.isCancelled();
        }
    }
}
