package org.exquisite.protege.ui.panel.explanation;

import org.protege.editor.owl.ui.explanation.ExplanationResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author wolfi
 */
public class NoExplanationResult extends ExplanationResult {

    public NoExplanationResult() {
        setLayout(new BorderLayout());
        add(new JPanel());
    }

    @Override
    public void dispose() {
    }

}
