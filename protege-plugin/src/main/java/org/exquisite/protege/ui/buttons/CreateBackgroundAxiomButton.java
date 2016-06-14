package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.BackgroundView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateBackgroundAxiomButton extends AbstractGuiButton {

    public CreateBackgroundAxiomButton(final BackgroundView backgroundView) {
        super("Create Axiom","Create Background Axiom","Button-New-icon.png", -1,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        /* TODO
                        CreateAxiomEditor editor = new CreateAxiomEditor(backgroundView.getEditorKitHook());
                        editor.show();
                        */
                    }
                }
        );

    }

}
