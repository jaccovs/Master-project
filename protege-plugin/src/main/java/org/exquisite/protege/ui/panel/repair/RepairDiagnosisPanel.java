package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
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

    private Debugger debugger;

    RepairAxiomList repairComponent;

    public RepairDiagnosisPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        EditorKitHook editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();

        setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

        repairComponent = new RepairAxiomList(this.editorKit,editorKitHook);
        repairComponent.updateList(this.debugger.getDiagnoses(), this.debugger.getDiagnosisEngineFactory().getOntology());

        add(new JScrollPane(repairComponent), BorderLayout.CENTER);

        setVisible(true);
    }

    public void dispose() {
        repairComponent.dispose();
    }
}
