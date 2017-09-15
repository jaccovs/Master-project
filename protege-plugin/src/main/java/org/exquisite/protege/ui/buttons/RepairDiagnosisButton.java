package org.exquisite.protege.ui.buttons;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author wolfi
 */
public class RepairDiagnosisButton extends AbstractImageButton {

    public RepairDiagnosisButton(ActionListener actionListener) {
        super("Repair", Color.GREEN.darker(), "repair.png", actionListener);
    }

}
