package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.view.BackgroundView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 24.09.12
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class AddToBackgroundButton extends AbstractGuiButton {

    public AddToBackgroundButton(final BackgroundView backgroundView) {
        super("","Add to Background","arrow-up-icon.png", -1,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List selectedValues = new LinkedList();
                        for (int idx : backgroundView.getOntologyAxiomList().getSelectedIndices())
                            selectedValues.add(backgroundView.getOntologyAxiomList().getModel().getElementAt(idx));

                        backgroundView.getEditorKitHook().getActiveOntologyDiagnosisSearcher()
                                .addBackgroundAxioms(selectedValues);
                    }
                }
        );

    }
}
