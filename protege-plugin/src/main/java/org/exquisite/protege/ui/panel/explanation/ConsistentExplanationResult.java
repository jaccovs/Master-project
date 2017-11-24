package org.exquisite.protege.ui.panel.explanation;

import org.protege.editor.owl.ui.explanation.ExplanationResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author wolfi
 */
public class ConsistentExplanationResult extends ExplanationResult {

    public ConsistentExplanationResult() {
        setLayout(new BorderLayout());
        add(new JLabel("Consistent"));
    }

    @Override
    public void dispose() {

    }
}
