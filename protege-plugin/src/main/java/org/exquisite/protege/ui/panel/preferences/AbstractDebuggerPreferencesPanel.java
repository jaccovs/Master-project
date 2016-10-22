package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DebuggerConfiguration;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

class AbstractDebuggerPreferencesPanel extends OWLPreferencesPanel {

    private DebuggerConfiguration configuration;

    private DebuggerConfiguration newConfiguration;

    private final String PREFIX = "> ";

    AbstractDebuggerPreferencesPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration) {
        this.newConfiguration = newConfiguration;
        this.configuration = configuration;
    }

    protected DebuggerConfiguration getConfiguration() {
        return configuration;
    }

    DebuggerConfiguration getNewConfiguration() {
        return newConfiguration;
    }

    static JLabel getHelpLabel(String helpText) {
        JLabel label = new JLabel(helpText);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 10f));
        label.setForeground(Color.GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(3, 20, 7, 0));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        return label;
    }

    public void saveChanges() {
    }

    JEditorPane createHelpEditorPane() {
        JEditorPane helpArea = new JEditorPane();
        helpArea.setBorder(BorderFactory.createTitledBorder("Info"));
        helpArea.setContentType("text/html");
        helpArea.setEditable(false);
        helpArea.setText(PREFIX);
        return helpArea;
    }

    @Override
    public void applyChanges() {

    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

    private class ComponentHooverListener extends MouseAdapter {

        private JEditorPane editorPane;

        ComponentHooverListener(JEditorPane editorPane) {
            this.editorPane = editorPane;
        }

        void showHelpMessage(String id) {
            ResourceBundle helpMsgBundle = ResourceBundle.getBundle("OptionHelpMessages");
            editorPane.setText(PREFIX + helpMsgBundle.getString(id));
        }

        String parseId(Object source) {

            JComponent component = (JComponent) source;
            String id;
            if (component instanceof OptionBox) {
                id = ((OptionBox)component).getId();
            } else {
                component = (JComponent) component.getParent();
                if (component instanceof JComboBox) {
                    id = ((OptionBox)component.getParent()).getId();
                } else {
                    id = ((OptionBox)component).getId();
                }
            }
            return id;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            String id = parseId(e.getSource());
            showHelpMessage(id);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            editorPane.setText(PREFIX);
        }

    }

    static class OptionBox extends Box {

        private String id;

        private JComponent label;
        private JComponent component;


        OptionBox(String id, JComponent component) {
            this(id,null,component);
        }

        OptionBox(String id, JComponent label, JComponent component) {
            super(BoxLayout.X_AXIS);
            this.id = id;
            this.label = label;
            this.component = component;

            if (label == null)
                constructBox(Collections.singletonList(component));
            else {
                this.label = label;
                List<JComponent> list = new LinkedList<>();
                list.add(label);
                list.add(component);
                constructBox(list);
            }
        }

        void constructBox(List<JComponent> components) {
            for (JComponent component : components) {
                component.setMaximumSize(component.getPreferredSize());
                add(component);

            }
            add(Box.createHorizontalGlue());

        }

        String getId() {
            return id;
        }

        void setEnabledLabel(boolean b) {
            if (this.label != null)
                this.label.setEnabled(b);

            if (this.component != null) {
                this.component.setEnabled(b);
            }
        }

    }

    static class OptionGroupBox extends Box {

        OptionGroupBox(String name) {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createTitledBorder(name));
        }

        void addOptionBox(OptionBox box) {
            add(box);
            add(Box.createVerticalStrut(10));
        }

        void addHelpText(String helpText) {
            add(getHelpLabel(helpText));

            add(Box.createVerticalStrut(5));
        }
    }
}
