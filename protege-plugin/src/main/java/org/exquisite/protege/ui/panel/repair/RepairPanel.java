package org.exquisite.protege.ui.panel.repair;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.view.RepairView;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * The repair panel popping up when user decides to repair the axioms from the diagnosis.
 *
 * @author wolfi
 */
public class RepairPanel extends JPanel implements Disposable, OWLModelManagerListener {

    private OWLEditorKit editorKit;

    private Debugger debugger;

    private JFrame parentWorkspaceFrame;

    private RepairView repairView;

    //private JButton applyButton, cancelButton;


    public RepairPanel(Debugger debugger) {
        this.debugger = debugger;
        this.debugger.doStartRepair();
        this.editorKit = this.debugger.getEditorKit();
        this.parentWorkspaceFrame = ProtegeManager.getInstance().getFrame(this.editorKit.getWorkspace());
        setLayout(new BorderLayout());
        editorKit.getModelManager().addListener(this);

    }

    public JOptionPane getOptionPane(JComponent parent) {
        JOptionPane pane;
        if (!(parent instanceof JOptionPane)) {
            pane = getOptionPane((JComponent)parent.getParent());
        } else {
            pane = (JOptionPane) parent;
        }
        return pane;
    }

    public void display() {
        RepairPanel repairPanel = this;
        setLayout(new BorderLayout());
        addRepairView();
        //createButtons();
        showOptionDialog(parentWorkspaceFrame,
                repairPanel,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                //new JButton[] {applyButton ,cancelButton}, applyButton);
                repairView.getButtons(), repairView.getButtons()[0]);
/*
        JOptionPane.showOptionDialog(parentWorkspaceFrame,
                repairPanel, "Repair Axioms",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new Object[] {applyButton ,cancelButton}, applyButton);
*/


    }

    private void showOptionDialog(JFrame parentComponent, RepairPanel repairPanel, int optionType, int messageType,
                                  Object[] options, Object initialValue) {
        JOptionPane pane = new JOptionPane(repairPanel, messageType,
                optionType, null,
                options, initialValue);
        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(parentComponent.getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, "Repair Axioms");
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                repairPanel.dispose();
            }
        });
        dialog.setModal(true);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    private void addRepairView() {

        repairView = new RepairView(this);
        repairView.setup(this.editorKit.getWorkspace().getViewManager().getViewComponentPlugin("org.exquisite.protege.RepairView"));

        try {
            repairView.initialise();
        } catch (Exception e) {
            e.printStackTrace();
        }
        add(repairView, BorderLayout.CENTER);
    }
/*
    private void createButtons() {
        this.applyButton = new JButton("Apply Changes");
        this.applyButton.setToolTipText("Applies all changes to the ontology");
        this.applyButton.addActionListener(this::doApply);
        this.applyButton.setEnabled(false);



        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setToolTipText("Does not change anything");
        this.cancelButton.addActionListener(this::doCancel);
    }

    private void doApply(ActionEvent e) {
        JOptionPane pane = getOptionPane((JComponent)e.getSource());
        pane.setValue(applyButton);
        dispose();
    }

    private void doCancel(ActionEvent e) {
        JOptionPane pane = getOptionPane((JComponent)e.getSource());
        pane.setValue(cancelButton);
        dispose();
    }
*/
    // Disposable interface
    @Override
    public void dispose() {
        editorKit.getModelManager().removeListener(this);
    }

    // OWLModelManagerListener interface
    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {

    }
}
