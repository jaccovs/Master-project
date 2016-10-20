package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

public class ProbabPanel extends AbstractDebuggerPreferencesPanel {

    private ProbabPane probabPane;

    public ProbabPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration, EditorKitHook editorKitHook) {
        super(configuration,newConfiguration);
        setLayout(new BorderLayout());
        probabPane = new ProbabPane(editorKitHook);
        add(new JScrollPane(probabPane),BorderLayout.CENTER);
        JEditorPane helpArea = createHelpEditorPane();
        helpArea.setText("<html>Here you can specifiy the fault probabilities for the OWL keywords<html>");
        add(helpArea, BorderLayout.SOUTH);
    }

    @Override
    public void saveChanges() {
    }

    public Map<ManchesterOWLSyntax, BigDecimal> getMap() {
        return probabPane.getProbabMap();
    }

}
