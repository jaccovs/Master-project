package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.ConflictAxiomList;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.exquisite.protege.ui.list.RepairTestCasesAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author wolfi
 */
public class RepairDiagnosisPanel extends JComponent {

    private static final int PREF_WIDTH = 800;

    private static final int PREF_HEIGHT = 600;

    private OWLEditorKit editorKit;

    private EditorKitHook editorKitHook;

    private Debugger debugger;

    private RepairAxiomList repairComponent;

    private RepairTestCasesAxiomList testcaseComponent;

    private ConflictAxiomList conflictComponent;



    public RepairDiagnosisPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        this.editorKitHook = (EditorKitHook) this.editorKit.get("org.exquisite.protege.EditorKitHook");
        this.debugger = editorKitHook.getActiveOntologyDebugger();

        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

        addComponentToPane(this);

        setVisible(true);
    }

    private void addComponentToPane(Container pane) {
        pane.setLayout(new GridBagLayout());

        repairComponent = new RepairAxiomList(editorKit, editorKitHook);
        repairComponent.updateList(this.debugger.getDiagnoses(), this.debugger.getDiagnosisEngineFactory().getOntology());
        addToPane(0,0,2,1,1.0,0.5, repairComponent, "Repair", pane);

        testcaseComponent = new RepairTestCasesAxiomList(editorKit, editorKitHook);
        addToPane(0,1,1,1,0.5,0.5,testcaseComponent,"Testcases", pane);

        conflictComponent = new ConflictAxiomList(editorKit, editorKitHook);
        addToPane(1,1,1,1, 0.5, 0.5, conflictComponent, "Conflicts", pane);
    }

    private void addToPane(int x, int y, int w, int h, double weightx, double weighty, JComponent component, String title, Container pane) {

        GridBagConstraints c = new GridBagConstraints();

        // sets the GridBagConstraints
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));

        panel.add(ComponentFactory.createScrollPane(component), BorderLayout.CENTER);

        pane.add(panel, c);
    }

    public void dispose() {
        repairComponent.dispose();
        testcaseComponent.dispose();
        conflictComponent.dispose();
    }
}
