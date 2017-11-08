package org.exquisite.protege.explanation;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationProgressMonitor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;
/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge
 * The University of Manchester
 * Information Management Group
 * Date: 14-Oct-2009
 *
 * @apiNote This is a <i>modified</i> copy from the explanation-workbench 5.0.0-beta-19
 * (Revision Number 3c2a4fa7f0591c18693d2b8a6bd0a9739dde2340) at https://github.com/protegeproject/explanation-workbench.git
 * <br>modifications: visibility changes by @author wolfi, reformatting by @author wolfi
 */
public class ExplanationProgressPanel extends JPanel implements ExplanationProgressMonitor<OWLAxiom> {

    private JLabel messageLabel;

    private int numberFound = 0;

    private static final String MESSAGE = "Computing explanations.  Found ";

    private boolean cancelled = false;
    private Action cancelAction;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    ExplanationProgressPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(400, 100));
        JPanel progressPanel = new JPanel(new BorderLayout(3, 3));
        add(progressPanel, BorderLayout.NORTH);
        messageLabel = new JLabel(MESSAGE + "0  ");
        progressPanel.add(messageLabel, BorderLayout.NORTH);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressPanel.add(progressBar, BorderLayout.SOUTH);
        cancelAction = new AbstractAction("Stop searching") {
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                setEnabled(false);
            }
        };
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(new JButton(cancelAction));
    }

    public void reset() {
        cancelled = false;
        numberFound = 0;
        messageLabel.setText(MESSAGE + 0);
        cancelAction.setEnabled(true);
    }

    private void updateMessage() {
        Runnable runnable = () -> messageLabel.setText(MESSAGE + numberFound);
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Called by explanation generators that support progress monitors.  This is
     * called when a new explanation is found for an entailment when searching for
     * multiple explanations.
     *
     * @param explanationGenerator The explanation generator that found the explanation
     * @param explanation          The explanation that was found
     *                             for the entailment or <code>false</code> if the explanation generator should stop finding explanations
     *                             at the next opportunity.
     * @param allFoundExplanations All of the explanations found so far for the specified entailment
     */
    public void foundExplanation(ExplanationGenerator explanationGenerator, Explanation explanation, Set allFoundExplanations) {
        numberFound = allFoundExplanations.size();
        updateMessage();
    }

    /**
     * The explanation generator will periodically check to see if it should continue finding explanations by calling
     * this method.
     *
     * @return <code>true</code> if the explanation generator should cancel the explanation finding process or <code>false</code>
     * if the explanation generator should continue.
     */
    public boolean isCancelled() {
        return cancelled;
    }

}
