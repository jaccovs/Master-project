package org.exquisite.protege.ui.progress;

import org.exquisite.protege.Debugger;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.util.Resettable;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

import javax.swing.*;

/**
 * @author wolfi
 */
public class ReasonerProgressUI extends AbstractProgressUI implements ReasonerProgressMonitor, Disposable, Resettable {

    public ReasonerProgressUI(Debugger debugger) {
        super(debugger);
    }

    @Override
    public void reasonerTaskStarted(String taskName) {
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
    public void reasonerTaskStopped() {
        if (!taskIsRunning)
            return;
        taskIsRunning = false;
        SwingUtilities.invokeLater(() -> {
            if (taskIsRunning)
                return;
            initWindow();
            if (!window.isVisible())
                return;
            taskLabel.setText("");
            window.setVisible(false);
        });
    }

    @Override
    public void reasonerTaskProgressChanged(int value, int max) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setMaximum(max);
            progressBar.setValue(value);
        });
    }

    @Override
    public void reasonerTaskBusy() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(true);
        });
    }

    @Override
    public void dispose() throws Exception {
        reset();
    }

    @Override
    public void reset() {
        SwingUtilities.invokeLater(() -> {
            initWindow();
            window.dispose();
        });
    }
}
