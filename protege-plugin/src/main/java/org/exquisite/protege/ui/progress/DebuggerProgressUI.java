package org.exquisite.protege.ui.progress;

import org.exquisite.core.ExquisiteProgressMonitor;
import org.exquisite.protege.Debugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author wolfi
 */
public class DebuggerProgressUI extends AbstractProgressUI implements ExquisiteProgressMonitor {

    public DebuggerProgressUI(Debugger debugger) {
        super(debugger);
    }

    @Override
    public void taskStarted(String taskName) {
        if (taskIsRunning)
            return;
        taskIsRunning = true;
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
        });
        showWindow(taskName);
    }

    @Override
    public void taskBusy(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message!=null) addMessage(message);
            progressBar.setIndeterminate(true);
        });
    }

    @Override
    public void taskProgressChanged(String message, int value, int max) {
        SwingUtilities.invokeLater(() -> {
            if (message != null) addMessage(message);
            progressBar.setIndeterminate(false);
            progressBar.setMaximum(max);
            progressBar.setValue(value);
        });
    }

    @Override
    public void taskStopped() {
        if (!taskIsRunning)
            return;
        taskIsRunning = false;
        SwingUtilities.invokeLater(() -> {
            clearMessages();
            if (taskIsRunning)
                return;
            initWindow();
            if (!window.isVisible())
                return;
            taskLabel.setText("");
            window.setVisible(false);
        });
    }
}
