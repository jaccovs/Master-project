package org.exquisite.protege.ui.panel.preferences;

import org.exquisite.protege.model.preferences.DebuggerConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

class AbstractDebuggerPreferencesPanel extends JPanel {

    private DebuggerConfiguration configuration;

    private DebuggerConfiguration newConfiguration;

    private ComponentHooverListener listener;

    private JEditorPane helpAreaPane;

    private final String PREFIX = "> ";

    AbstractDebuggerPreferencesPanel(DebuggerConfiguration configuration, DebuggerConfiguration newConfiguration) {
        this.newConfiguration = newConfiguration;
        this.configuration = configuration;
        helpAreaPane = createHelpEditorPane();
        listener = new ComponentHooverListener(helpAreaPane);
    }

    protected DebuggerConfiguration getConfiguration() {
        return configuration;
    }

    DebuggerConfiguration getNewConfiguration() {
        return newConfiguration;
    }

    ComponentHooverListener getListener() {
        return listener;
    }

    JEditorPane getHelpAreaPane() {
        return helpAreaPane;
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

        OptionBox(String id, ComponentHooverListener listener, JComponent component) {
            this(id,listener,null,component);
        }

        OptionBox(String id, ComponentHooverListener listener, JComponent label, JComponent component) {
            super(BoxLayout.X_AXIS);
            this.id = id;
            if (label == null)
                constructBox(Collections.singletonList(component),listener);
            else {
                this.label = label;
                List<JComponent> list = new LinkedList<>();
                list.add(label);
                list.add(component);
                constructBox(list,listener);
            }
        }

        void addListenerToComboBox(JComboBox comboBox, ComponentHooverListener listener) {
            for (Component component : comboBox.getComponents()) {
                if (component instanceof AbstractButton)
                    component.addMouseListener(listener);
            }
        }

        void constructBox(List<JComponent> components, ComponentHooverListener listener) {
            for (JComponent component : components) {
                component.setMaximumSize(component.getPreferredSize());
                if (component instanceof JComboBox)
                    addListenerToComboBox((JComboBox)component, listener);
                else
                    component.addMouseListener(listener);
                add(component);

            }
            add(Box.createHorizontalGlue());
            addMouseListener(listener);

        }

        String getId() {
            return id;
        }

        void setEnabledLabel(boolean b) {
            if (this.label != null)
                this.label.setEnabled(b);
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
    }
}
