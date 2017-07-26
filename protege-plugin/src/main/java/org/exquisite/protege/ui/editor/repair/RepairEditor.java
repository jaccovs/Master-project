package org.exquisite.protege.ui.editor.repair;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.preferences.GeneralPreferencesPanel;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author wolfi
 */
public class RepairEditor {

    private OWLEditorKit editorKit;

    private Component parent;

    private OWLLogicalAxiom axiom;

    private static final int EDITOR_SCREEN_MARGIN = 100;

    public RepairEditor(OWLEditorKit editorKit, Component parent, OWLLogicalAxiom axiom) {
        this.editorKit = editorKit;
        this.parent = parent;
        this.axiom = axiom;
    }

    /**
     * Slightly adapted code from OWLFrameList implementation.
     *
     * @param handler
     */
    public void showEditorDialog(final EditHandler handler) {
        // If we don't have any editing component then just return

        final OWLObjectEditor editor = getEditor(axiom); // todo create the correct type of editor depending on the axiom

        if (editor == null) {
            return;
        }
        if (editor instanceof JWindow) {
            ((JWindow) editor).setVisible(true);
            return;
        }
        if (editor instanceof Wizard) {
            int ret = ((Wizard) editor).showModalDialog();
            if (ret == Wizard.FINISH_RETURN_CODE) {
                handler.handleEditFinished(editor);
            }
            return;
        }
        // Create the editing component dialog - we use an option pane
        // so that the buttons and keyboard actions are what are expected
        // by the user.
        final JComponent editorComponent = editor.getEditorComponent();
        final VerifyingOptionPane optionPane = new VerifyingOptionPane(editorComponent) {

            public void selectInitialValue() {
                // This is overriden so that the option pane dialog default
                // button
                // doesn't get the focus.
            }
        };
        final InputVerificationStatusChangedListener verificationListener = verified -> optionPane.setOKEnabled(verified);
        // if the editor is verifying, will need to prevent the OK button from
        // being available
        if (editor instanceof VerifiedInputEditor) {
            ((VerifiedInputEditor) editor).addStatusChangedListener(verificationListener);
        }
        final Component parent = getDialogParent();


        final JDialog dlg = optionPane.createDialog(parent, null);
        // The editor shouldn't be modal (or should it?)
        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.addComponentListener(new ComponentAdapter() {

            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                editorComponent.setPreferredSize(editorComponent.getSize());
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handler.handleEditFinished(editor);
                }
                //setSelectedValue(frameObject, true); todo
                if (editor instanceof VerifiedInputEditor) {
                    ((VerifiedInputEditor) editor).removeStatusChangedListener(verificationListener);
                }
                editor.dispose();
            }
        });

        Object rootObject = axiom;

        if (rootObject instanceof OWLLogicalAxiom) {
            dlg.setTitle(editorKit.getModelManager().getRendering((OWLLogicalAxiom) rootObject));
        } else if (rootObject != null) {
            dlg.setTitle(rootObject.toString());
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlgSize = dlg.getSize();
        if (dlg.getHeight() > screenSize.height - EDITOR_SCREEN_MARGIN) {
            dlgSize.height = screenSize.height - EDITOR_SCREEN_MARGIN;
        }
        if (dlg.getWidth() > screenSize.width - EDITOR_SCREEN_MARGIN) {
            dlgSize.width = screenSize.width - EDITOR_SCREEN_MARGIN;
        }
        dlg.setSize(dlgSize);
        dlg.setVisible(true);

    }

    private OWLObjectEditor getEditor(OWLLogicalAxiom axiom) {

        OWLObjectEditor editor = null;
        Object editedObject = null;
        if (axiom instanceof OWLClassAxiom) {
            editor = new OWLGeneralAxiomEditor(editorKit);
            editedObject = axiom;
        } else {
            throw new UnsupportedOperationException("No editor for axiom type " + axiom.getAxiomType() + " available!");
        }
        editor.setEditedObject(editedObject);
        return editor;


    }

    public static boolean hasEditor(OWLLogicalAxiom axiom) {
        return axiom instanceof OWLClassAxiom;
    }

    private Component getDialogParent() {
        // @@TODO move prefs somewhere more central
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        return prefs.getBoolean(GeneralPreferencesPanel.DIALOGS_ALWAYS_CENTRED, false) ? SwingUtilities.getAncestorOfClass(Frame.class, parent) : parent;
    }
}
