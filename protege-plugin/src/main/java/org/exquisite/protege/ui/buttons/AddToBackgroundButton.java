package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.BackgroundView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class AddToBackgroundButton extends AbstractGuiButton {

    public AddToBackgroundButton(final BackgroundView backgroundView) {
        super("","Add to Background","arrow-up-icon.png", -1,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List selectedValues = new LinkedList();
                        for (int idx : backgroundView.getPossiblyFaultyAxiomsList().getSelectedIndices())
                            selectedValues.add(backgroundView.getPossiblyFaultyAxiomsList().getModel().getElementAt(idx));

                        backgroundView.getEditorKitHook().getActiveOntologyDiagnosisSearcher()
                                .addBackgroundAxioms(selectedValues);
                    }
                }
        );

    }
}
