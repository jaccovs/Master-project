package org.exquisite.protege.ui.progress;

import org.exquisite.core.IExquisiteProgressMonitor;
import org.exquisite.protege.Debugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A progress monitor for the debugger implementing the IExquisiteProgressMonitor interface.
 *
 * @author wolfi
 */
public class DebuggerProgressUI implements IExquisiteProgressMonitor {

    private static final int PADDING = 5;

    private Debugger debugger;

    private static final String DEFAULT_MESSAGE = "Calculating next queries ...";

    private JProgressBar progressBar;

    private JDialog window;

    private JLabel taskLabel;

    private JTextArea messages;

    private Action cancelledAction;

    private boolean taskIsRunning = false;

    private boolean cancelled = false;

    public DebuggerProgressUI(Debugger debugger) {
        this.debugger = debugger;
        this.progressBar = new JProgressBar();
    }

    private void initWindow() {
        if (window != null)
            return;
        JPanel panel = new JPanel(new BorderLayout(PADDING, PADDING));
        panel.add(progressBar, BorderLayout.SOUTH);
        taskLabel = new JLabel(DEFAULT_MESSAGE);
        panel.add(taskLabel, BorderLayout.NORTH);
        Frame parent = (Frame) (SwingUtilities.getAncestorOfClass(Frame.class,
                debugger.getEditorKit().getWorkspace()));
        window = new JDialog(parent, "Debugger progress", true);
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // cancel button
        cancelledAction = new AbstractAction("Continue >") {
            private static final long serialVersionUID = 3688085823398242640L;
            public void actionPerformed(ActionEvent e) {
                setCancelled();
            }
        };
        JButton cancelledButton = new JButton(cancelledAction);
        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING, 2));
        cancelledButton.setToolTipText("Stops the current task and continues with next task");
        buttonHolder.add(cancelledButton);

        // text area
        this.messages = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(this.messages);
        this.messages.setEditable(false);
        this.messages.setLineWrap(false);
        this.messages.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        JPanel holderPanel = new JPanel(new BorderLayout(PADDING, PADDING));
        holderPanel.add(panel, BorderLayout.NORTH);
        holderPanel.add(scrollPane, BorderLayout.CENTER);
        holderPanel.add(buttonHolder, BorderLayout.SOUTH);

        holderPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(holderPanel, BorderLayout.CENTER);

        window.pack();
        Dimension windowSize = window.getPreferredSize();
        window.setSize(400, windowSize.height);
        window.setResizable(false);
    }

    private void addMessage(String msg) {
        this.messages.append("[");
        this.messages.append(getTimeStamp());
        this.messages.append("] ");
        this.messages.append(msg);
        this.messages.append("\n");
    }

    private String getTimeStamp() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date ());
    }

    private void clearMessages() {
        this.messages.setText("");
    }

    private void setCancelled() {
        SwingUtilities.invokeLater(() -> {
            clearMessages();
            initWindow();
            taskLabel.setText("Cancelled.  Waiting to terminate...");
            cancelledAction.setEnabled(false);
        });
        cancelled = true;
    }

    private void showWindow(final String message) {
        SwingUtilities.invokeLater(() -> {
            initWindow();
            taskLabel.setText(message);
            if (window.isVisible())
                return;
            cancelledAction.setEnabled(false);
            window.setLocationRelativeTo(window.getOwner());
            window.setVisible(true);
        });
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
        cancelled = false;
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

    @Override
    public void setCancel(boolean isEnabled) {
        if (cancelledAction!=null)
            cancelledAction.setEnabled(isEnabled);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
