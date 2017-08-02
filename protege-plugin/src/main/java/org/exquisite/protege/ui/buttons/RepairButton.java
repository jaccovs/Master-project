package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.Debugger;
import org.exquisite.protege.ui.view.AbstractViewComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A repair button that becomes active once a diagnosis has been found at the end of a debugging session.
 *
 * @author wolfi
 */
public class RepairButton extends AbstractGuiButton {

    private static final String TOOLTIP_DISABLED = "When one repair has been found you can fix the axioms";
    private static final String TOOLTIP_ENABLED = "Fix these incorrect axioms";

    public RepairButton(final AbstractViewComponent toolboxView) {
        super("Repair", TOOLTIP_DISABLED, "repair.png", KeyEvent.VK_R,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final Debugger debugger = toolboxView.getEditorKitHook().getActiveOntologyDebugger();
                        debugger.doStartRepair();
                    }
                }
        );

        updateView(toolboxView.getEditorKitHook().getActiveOntologyDebugger());
    }

    public void updateView(Debugger debugger) {
        final boolean isEnabled = (debugger.isSessionRunning() || debugger.isRepairing()) && debugger.getDiagnoses().size() == 1;
        setEnabled(isEnabled);
        if (isEnabled)
            setToolTipText(TOOLTIP_ENABLED);
        else
            setToolTipText(TOOLTIP_DISABLED);
    }
}
