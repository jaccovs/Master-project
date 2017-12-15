package org.exquisite.protege.ui.editor;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.action.CreateOWLEntityAction;
import org.protege.editor.core.ui.util.VerifyingOptionPane;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.semanticweb.owlapi.model.*;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Set;

public abstract class AbstractEditor {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractEditor.class.getName());

    private OWLEditorKit editorKit;

    private EditorKitHook editorKitHook;

    public AbstractEditor(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        this.editorKitHook = editorKitHook;
        this.editorKit = editorKit;

    }

    public EditorKitHook getEditorKitHook() {
        return editorKitHook;
    }

    protected JToolBar createAddEntitiesToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBorder(null);
        //setLayout(new BoxLayout(this, BoxLayout.X_AXIS));


        this.addAction(toolBar,new CreateOWLEntityAction<OWLClass>("Add subclass", editorKit, OWLIcons.getIcon("class.add.png"), "Please enter a class name", OWLClass.class));
        this.addAction(toolBar,new CreateOWLEntityAction<OWLObjectProperty>("Add object property", editorKit, OWLIcons.getIcon("property.object.add.png"), "Please enter an object property name", OWLObjectProperty.class));
        this.addAction(toolBar,new CreateOWLEntityAction<OWLDataProperty>("Add data property", editorKit, OWLIcons.getIcon("property.data.add.png"), "Please enter a data property name", OWLDataProperty.class));
        this.addAction(toolBar,new CreateOWLEntityAction<OWLNamedIndividual>("Add individual", editorKit, OWLIcons.getIcon("individual.add.png"), "Please enter an individual name", OWLNamedIndividual.class));
        this.addAction(toolBar,new CreateOWLEntityAction<OWLDatatype>("Add datatype", editorKit, OWLIcons.getIcon("datarange.add.png"), "Please enter a datatype name", OWLDatatype.class));

        return toolBar;

    }

    protected void addAction(JToolBar toolBar, Action action) {
        JButton button = toolBar.add(action);
        button.setFocusable(false);
    }

    protected JDialog createDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createAddEntitiesToolbar(), BorderLayout.NORTH);
        OWLExpressionChecker<Set<OWLLogicalAxiom>> checker = new AxiomChecker(editorKit.getModelManager());
        final ExpressionEditor<Set<OWLLogicalAxiom>> editor = new ExpressionEditor<>(editorKit, checker);
        setEditedAxioms(editor);

        JScrollPane scroller = new JScrollPane(editor);
        panel.add(scroller, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(400,300));

        final VerifyingOptionPane optionPane = new VerifyingOptionPane(panel);
        optionPane.setOKEnabled(false);

        editor.addStatusChangedListener(newState -> {
            try {
                optionPane.setOKEnabled(isWellFormed(editor) && isValid(editor.createObject()));
            } catch (OWLException ex) {
                logger.error(ex.getMessage(), ex);
            }
        });
        JDialog dlg = optionPane.createDialog(getEditorTitle());

        dlg.setModal(true);
        dlg.setResizable(true);
        dlg.pack();

        dlg.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                if (optionPane.getValue() != null && optionPane.getValue().equals(JOptionPane.OK_OPTION))
                    try {
                        handleEditorConfirmed(editor.createObject());
                    } catch (OWLException e1) {
                        logger.error(e1.getMessage(), e1);
                    }
            }
        });
        return dlg;
    }

    protected void setEditedAxioms(ExpressionEditor<Set<OWLLogicalAxiom>> editor) {}

    protected abstract String getEditorTitle();

    protected abstract void handleEditorConfirmed(Set<OWLLogicalAxiom> testcase);

    protected boolean isWellFormed(final ExpressionEditor<Set<OWLLogicalAxiom>> editor) {
        return editor.isWellFormed();
    }
    protected abstract boolean isValid(Set<OWLLogicalAxiom> testcase);

    public void show() {
        JDialog dialog = createDialog();
        dialog.setVisible(true);
    }

}
