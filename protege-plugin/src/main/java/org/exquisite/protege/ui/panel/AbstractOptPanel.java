package org.exquisite.protege.ui.panel;

import org.exquisite.protege.model.configuration.SearchConfiguration;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 11.09.12
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public class AbstractOptPanel extends JPanel {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractOptPanel.class.getName());

    private SearchConfiguration configuration;

    private SearchConfiguration newConfiguration;

    private ComponentHooverListener listener;

    private JEditorPane helpAreaPane;

    public AbstractOptPanel(SearchConfiguration configuration, SearchConfiguration newConfiguration) {
        this.newConfiguration = newConfiguration;
        this.configuration = configuration;
        helpAreaPane = createHelpEditorPane();
        listener = new ComponentHooverListener(helpAreaPane);

    }

    protected SearchConfiguration getConfiguration() {
        return configuration;
    }

    public SearchConfiguration getNewConfiguration() {
        return newConfiguration;
    }

    public ComponentHooverListener getListener() {
        return listener;
    }

    public JEditorPane getHelpAreaPane() {
        return helpAreaPane;
    }

    public void saveChanges() {

    }

    protected JEditorPane createHelpEditorPane() {
        JEditorPane helpArea = new JEditorPane();
        helpArea.setBorder(BorderFactory.createTitledBorder("Info"));
        helpArea.setContentType("text/html");
        helpArea.setEditable(false);
        helpArea.setText(" ");
        return helpArea;
    }

    protected class ComponentHooverListener extends MouseAdapter {

        private JEditorPane editorPane;

        public ComponentHooverListener(JEditorPane editorPane) {
            this.editorPane = editorPane;
        }

        protected void showHelpMessage(String id) {
            ResourceBundle helpMsgBundle = ResourceBundle.getBundle("OptionHelpMessages");

            editorPane.setText(helpMsgBundle.getString(id));

        }

        protected String parseId(Object source) {

            JComponent component = (JComponent) source;
            String id;
            if (component instanceof OptionBox) {
                id = ((OptionBox)component).getId();
            }
            else {
                component = (JComponent) component.getParent();
                if (component instanceof JComboBox) {
                    id = ((OptionBox)component.getParent()).getId();
                }
                else {
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
            editorPane.setText(" ");
        }

    }

    public static class OptionBox extends Box {

        private String id;

        public OptionBox(String id, ComponentHooverListener listener, JComponent component) {
            this(id,listener,null,component);
        }

        public OptionBox(String id, ComponentHooverListener listener, JComponent label, JComponent component) {
            super(BoxLayout.X_AXIS);
            this.id = id;
            if (label == null)
                constructBox(Collections.singletonList(component),listener);
            else {
                List<JComponent> list = new LinkedList<JComponent>();
                list.add(label);
                list.add(component);
                constructBox(list,listener);
            }

        }

        protected void addListenerToComboBox(JComboBox comboBox, ComponentHooverListener listener) {
            for (Component component : comboBox.getComponents()) {
                if (component instanceof AbstractButton)
                    component.addMouseListener(listener);
            }
        }

        protected void constructBox(List<JComponent> components, ComponentHooverListener listener) {
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

        public String getId() {
            return id;
        }

    }

    public static class OptionGroupBox extends Box {

        public OptionGroupBox(String name) {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createTitledBorder(name));

        }

        public void addOptionBox(OptionBox box) {
            add(box);
            add(Box.createVerticalStrut(10));
        }

    }

}
