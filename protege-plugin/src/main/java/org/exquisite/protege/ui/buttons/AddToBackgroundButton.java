package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.AxiomListItem;
import org.exquisite.protege.ui.view.BackgroundView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AddToBackgroundButton extends AbstractImageButton {

    public AddToBackgroundButton(AxiomListItem axiomListItem, BackgroundView backgroundView) {
        super("Assume as Correct", Color.GREEN.darker(),"Accept2.png", e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            backgroundView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().addBackgroundAxioms(selectedValues);
        });
    }
}
