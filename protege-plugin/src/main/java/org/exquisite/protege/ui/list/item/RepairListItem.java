package org.exquisite.protege.ui.list.item;

import org.exquisite.protege.model.repair.RepairManager;
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
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author wolfi
 */
public class RepairListItem extends AxiomListItem {

    private static final int EDITOR_SCREEN_MARGIN = 100;

    private OWLEditorKit editorKit;

    private Component parent;

    public RepairListItem(OWLLogicalAxiom axiom, OWLOntology ontology, OWLEditorKit editorKit, RepairManager repairManager, Component parent) {
        super(axiom, ontology);
        this.editorKit = editorKit;
        this.parent = parent;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public void handleEdit() {
        super.handleEdit();
        showEditorDialog(new EditHandler() {
            @Override
            public void handleEditFinished(OWLObjectEditor editor) {
                editor.getHandler().handleEditingFinished(editor.getEditedObjects());
            }
        });
    }

    @Override
    public boolean handleDelete() {
        return super.handleDelete();
    }

    /**
     * Slightly adapted code from OWLFrameList implementation.
     *
     * @param handler
     */
    private void showEditorDialog(final EditHandler handler) {
        // If we don't have any editing component then just return

        final OWLObjectEditor editor = new OWLGeneralAxiomEditor(editorKit); // todo create the correct type of editor depending on the axiom
        editor.setEditedObject(getAxiom());

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
        final InputVerificationStatusChangedListener verificationListener = verified -> optionPane.setOKEnabled(verified /*&& frameObject.checkEditorResults(editor) todo*/);
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

        Object rootObject = getAxiom();

        if (rootObject instanceof OWLObject) {
            dlg.setTitle(editorKit.getModelManager().getRendering((OWLObject) rootObject));
        }
        else if (rootObject != null) {
            dlg.setTitle(rootObject.toString());
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dlgSize = dlg.getSize();
        if(dlg.getHeight() > screenSize.height - EDITOR_SCREEN_MARGIN) {
            dlgSize.height = screenSize.height - EDITOR_SCREEN_MARGIN;
        }
        if(dlg.getWidth() > screenSize.width - EDITOR_SCREEN_MARGIN) {
            dlgSize.width = screenSize.width - EDITOR_SCREEN_MARGIN;
        }
        dlg.setSize(dlgSize);
        dlg.setVisible(true);

    }

    private Component getDialogParent() {
        // @@TODO move prefs somewhere more central
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        return prefs.getBoolean(GeneralPreferencesPanel.DIALOGS_ALWAYS_CENTRED, false) ? SwingUtilities.getAncestorOfClass(Frame.class, getParent()) : getParent();
    }

    private Component getParent() {
        return parent;
    }

    private interface EditHandler {

        void handleEditFinished(OWLObjectEditor editor);
    }
}
