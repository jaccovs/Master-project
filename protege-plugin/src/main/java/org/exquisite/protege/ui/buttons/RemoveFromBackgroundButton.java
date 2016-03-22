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
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RemoveFromBackgroundButton extends AbstractGuiButton {

    public RemoveFromBackgroundButton(final BackgroundView backgroundView) {
        super("", "Remove from Background", "arrow-down-icon.png", -1,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List selectedValues = new LinkedList();
                        for (int idx : backgroundView.getBackgroundAxiomList().getSelectedIndices())
                            selectedValues.add(backgroundView.getBackgroundAxiomList().getModel().getElementAt(idx));

                        backgroundView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().
                                removeBackgroundAxioms(selectedValues);
                    }
                }
        );

    }
}
