package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.RepairAxiomList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

    private RepairAxiomList repairAxiomList;

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

        repairAxiomList = new RepairAxiomList(editorKit, editorKitHook, this);
        repairAxiomList.updateList(this.debugger.getDiagnoses(), this.debugger.getDiagnosisEngineFactory().getOntology());
        addToPane(0,0,2,1,1.0,0.5, repairAxiomList, "Repair", pane);

        repairAxiomList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(e);
            }
        });

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
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        title),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        panel.add(ComponentFactory.createScrollPane(component), BorderLayout.CENTER);

        pane.add(panel, c);
    }

    public void dispose() {
        repairAxiomList.dispose();
    }

    public void reset() {
        repairAxiomList.reset();
    }

    public boolean hasChanged() {
        return repairAxiomList.hasChanged();
    }
}
