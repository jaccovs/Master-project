package org.exquisite.protege.ui.editor;

import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;

class NotificationLabel extends JLabel {

    NotificationLabel() {
        super(Icons.getIcon("error.png"));
        this.setToolTipText("");
        this.setVisible(false);
    }

    void showNotification(String message) {
        this.setVisible(true);
        this.setToolTipText(message);
    }

    void hideNotification() {
        this.setToolTipText("");
        this.setVisible(false);
    }
}
