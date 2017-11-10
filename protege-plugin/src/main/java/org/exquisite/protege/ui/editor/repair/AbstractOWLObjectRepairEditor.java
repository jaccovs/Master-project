package org.exquisite.protege.ui.editor.repair;

import org.exquisite.protege.ui.dialog.DebuggingDialog;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditorHandler;
import org.protege.editor.owl.ui.preferences.GeneralPreferencesPanel;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Abstract editor class for repairing a diagnosed axiom.
 *
 * @author wolfi
 */
public abstract class AbstractOWLObjectRepairEditor<R extends OWLObject, A extends OWLAxiom, E> {

    private OWLEditorKit owlEditorKit;

    private Component parent;

    private A axiom;

    protected R rootObject;

    private OWLObjectEditorHandler handler;

    private OWLOntology ontology;

    private static final int EDITOR_SCREEN_MARGIN = 100;

    public AbstractOWLObjectRepairEditor(OWLEditorKit editorKit, Component parent, OWLOntology ontology, A axiom, OWLObjectEditorHandler handler) {
        this.owlEditorKit = editorKit;
        this.parent = parent;
        this.ontology = ontology;
        this.handler = handler;
        setAxiom(axiom); // use the setAxiom() method instead of direct assignment because subclasses may override the method
    }

    /**
     * Slightly adapted code from org.protege.editor.owl.ui.framelist.OWLFrameList implementation.
     *
     * @param handler A handler.
     */
    public void showEditorDialog(final EditHandler handler) {
        // If we don't have any editing component then just return

        final OWLObjectEditor<E> editor = getOWLObjectEditor();

        if (editor == null) {
            return;
        }

        editor.setHandler(this.handler);
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
        final InputVerificationStatusChangedListener verificationListener = verified -> optionPane.setOKEnabled(verified && this.checkEditorResults(editor));
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
                    try {
                        handler.handleEditFinished(editor);
                    } catch (Exception ex) {
                        DebuggingDialog.showErrorDialog("An error has occurred!", ex.getMessage(), ex);
                    }
                }
                if (editor instanceof VerifiedInputEditor) {
                    ((VerifiedInputEditor) editor).removeStatusChangedListener(verificationListener);
                }
                editor.dispose();
            }
        });

        Object rootObject = getRootObject();

        if (rootObject == null) {
            rootObject = getAxiom();
        }

        if (rootObject instanceof OWLLogicalAxiom) {
            dlg.setTitle(this.owlEditorKit.getModelManager().getRendering((OWLLogicalAxiom) rootObject));
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

    final public OWLObjectEditor getEditor() {
        OWLObjectEditor<E> editor = getOWLObjectEditor();
        if (editor != null) {
            editor.setHandler(this.handler);
        }
        return editor;
    }

    /**
     * Gets the object that the row holds.
     */
    public A getAxiom() {
        return axiom;
    }

    public void setAxiom(A axiom) {
        this.axiom = axiom;
    }

    /**
     * This row represents an assertion in a particular ontology.
     * This gets the ontology that the assertion belongs to.
     */
    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }

    public OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }

    public OWLDataFactory getOWLDataFactory() {
        return getOWLModelManager().getOWLDataFactory();
    }

    private Component getDialogParent() {
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ProtegeApplication.ID);
        return prefs.getBoolean(GeneralPreferencesPanel.DIALOGS_ALWAYS_CENTRED, false) ? SwingUtilities.getAncestorOfClass(Frame.class, parent) : parent;
    }

    public boolean hasEditor() {
        return true;
    }

    public abstract OWLObjectEditor<E> getOWLObjectEditor();

    public abstract A createAxiom(E editedObject);

    public boolean checkEditorResults(OWLObjectEditor<E> editor) {
        return true;
    }

    public R getRootObject() {
        return rootObject;
    }

}
