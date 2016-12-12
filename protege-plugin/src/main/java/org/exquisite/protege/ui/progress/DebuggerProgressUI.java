package org.exquisite.protege.ui.progress;

import org.exquisite.protege.Debugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author wolfi
 */
public class DebuggerProgressUI {

    public static final int PADDING = 5;

    private Debugger debugger;

    public static final String DEFAULT_MESSAGE = "Calculating next queries ...";

    private JProgressBar progressBar;

    private JDialog window;

    private JLabel taskLabel;

    private Action cancelledAction;

    public DebuggerProgressUI(Debugger debugger) {
        this.debugger = debugger;
        this.progressBar = new JProgressBar(SwingConstants.HORIZONTAL,0,3);
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
        cancelledAction = new AbstractAction("Cancel") {
            private static final long serialVersionUID = 3688085823398242640L;
            public void actionPerformed(ActionEvent e) {
                setCancelled();
            }
        };
        JButton cancelledButton = new JButton(cancelledAction);
        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonHolder.add(cancelledButton);

        JPanel holderPanel = new JPanel(new BorderLayout(PADDING, PADDING));
        holderPanel.add(panel, BorderLayout.NORTH);
        holderPanel.add(buttonHolder, BorderLayout.SOUTH);

        holderPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(holderPanel, BorderLayout.NORTH);
        window.pack();
        Dimension windowSize = window.getPreferredSize();
        window.setSize(400, windowSize.height);
        window.setResizable(false);
    }

    private void setCancelled() {
        SwingUtilities.invokeLater(() -> {
            closeWindow();
        });
    }

    public void showWindow(final String message) {
        SwingUtilities.invokeLater(() -> {
            initWindow();
            taskLabel.setText(message);
            if (window.isVisible())
                return;
            cancelledAction.setEnabled(true);
            window.setLocationRelativeTo(window.getOwner());
            window.setVisible(true);
        });
    }

    public void closeWindow() {
        SwingUtilities.invokeLater(() -> {
            initWindow();
            window.dispose();
        });
    }


}
