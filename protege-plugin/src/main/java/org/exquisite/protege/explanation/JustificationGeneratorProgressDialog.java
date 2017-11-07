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
 */
public class JustificationGeneratorProgressDialog extends JDialog {

    private ExplanationProgressPanel panel = new ExplanationProgressPanel();

    private ExplanationProgressMonitor<OWLAxiom> progressMonitor;
    
    public JustificationGeneratorProgressDialog(Frame owner) {
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

    public ExplanationProgressMonitor<OWLAxiom> getProgressMonitor() {
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
