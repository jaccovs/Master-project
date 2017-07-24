package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.panel.repair.RepairDiagnosisPanel;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import java.awt.*;

/**
 * @author wolfi
 */
public class RepairView extends AbstractOWLViewComponent {

    RepairDiagnosisPanel panel;

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(6, 6));

        panel = new RepairDiagnosisPanel(getOWLEditorKit());
        add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {
        panel.dispose();
    }



/*
    private RepairPanel parent;

    public RepairView() {
        parent = null;
        createButtons();
    }

    public RepairView(RepairPanel repairPanel) {
        parent = repairPanel;
    }

    private JButton applyButton, cancelButton;

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        if (parent == null)
            add(createRepairToolBar(), BorderLayout.SOUTH);
        else
            createButtons();
        updateView();
    }

    @Override
    protected MList createListForComponent() {
        return new RepairAxiomList(getOWLEditorKit(), getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateView();
    }

    public JButton[] getButtons() {
        return new JButton[] {this.applyButton, this.cancelButton};
    }

    private void doApply(ActionEvent e) {
        if (parent !=null ) {
            JOptionPane pane = parent.getOptionPane((JComponent) e.getSource());
            pane.setValue(applyButton);
        }
        if (parent!=null)
            disposeOWLView();
    }

    private void doCancel(ActionEvent e) {
        if (parent !=null ) {
            JOptionPane pane = parent.getOptionPane((JComponent) e.getSource());
            pane.setValue(cancelButton);
        }
        if (parent!=null)
            disposeOWLView();
    }

    private void createButtons() {
        this.applyButton = new JButton("Apply Changes");
        this.applyButton.setToolTipText("Applies all changes to the ontology");
        this.applyButton.addActionListener(this::doApply);
        this.applyButton.setEnabled(false);

        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setToolTipText("Does not change anything");
        this.cancelButton.addActionListener(this::doCancel);
    }

    private JComponent createRepairToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);

        createButtons();

        toolBar.setMaximumSize(toolBar.getPreferredSize());
        return toolBar;
    }

    private void updateView() {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();

        // updating the list
        OWLOntology ontology = getOWLEditorKit().getModelManager().getActiveOntology();
        ((RepairAxiomList)getList()).updateList(debugger.getDiagnoses(), ontology);
    }

    @Override
    protected void disposeOWLView() {
        final Debugger debugger = getEditorKitHook().getActiveOntologyDebugger();
        debugger.doStopRepair();
        if (parent!=null) {
            parent.dispose();
        }
        super.disposeOWLView();
    }
*/
}
