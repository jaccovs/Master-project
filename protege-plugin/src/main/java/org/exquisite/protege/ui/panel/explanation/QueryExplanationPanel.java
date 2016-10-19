package org.exquisite.protege.ui.panel.explanation;

import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;

/**
 * An explanation panel that explains the inferred axiom in a query.
 */
public class QueryExplanationPanel extends ExplanationResult {

    private OWLAxiom axiom;

    public QueryExplanationPanel(OWLAxiom axiom) {
        this.axiom = axiom;
        createUI();
    }

    private void createUI() {
        setLayout(new BorderLayout());
        add(new JLabel(axiom + " has been inferred by the query computation component of the debugger!"));
    }

    @Override
    public void dispose() {}
}
