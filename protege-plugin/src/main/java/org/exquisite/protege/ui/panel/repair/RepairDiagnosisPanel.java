package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.AcquiredTestcaseAxiomList;
import org.exquisite.protege.ui.list.ConflictAxiomList;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;

/**
 * @author wolfi
 */
public class RepairDiagnosisPanel extends JComponent {

    private static final int PREF_WIDTH = 500;

    private static final int PREF_HEIGHT = 300;

    private final OWLEditorKit editorKit;

    private EditorKitHook editorKitHook;

    private Debugger debugger;

    RepairAxiomList repairComponent;

    AcquiredTestcaseAxiomList testcaseComponent;

    ConflictAxiomList conflictComponent;

    public RepairDiagnosisPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        this.editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();

        //setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

        addComponentToPane(this);

        setVisible(true);
    }

    private void addComponentToPane(Container pane) {
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 0.5;
        repairComponent = new RepairAxiomList(editorKit, editorKitHook);
        repairComponent.updateList(this.debugger.getDiagnoses(), this.debugger.getDiagnosisEngineFactory().getOntology());
        pane.add(repairComponent, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        testcaseComponent = new AcquiredTestcaseAxiomList(editorKit, editorKitHook);
        pane.add(testcaseComponent, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.PAGE_END; //bottom of space
        conflictComponent = new ConflictAxiomList(editorKit, editorKitHook);
        pane.add(conflictComponent, c);

    }

    public void dispose() {
        repairComponent.dispose();
    }
}
