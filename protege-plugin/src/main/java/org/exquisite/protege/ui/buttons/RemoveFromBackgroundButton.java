package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.AxiomListItem;
import org.exquisite.protege.ui.view.BackgroundView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class RemoveFromBackgroundButton extends AbstractImageButton {

    public RemoveFromBackgroundButton(AxiomListItem axiomListItem, BackgroundView backgroundView) {
        super("Move to Knowledge Base", Color.GREEN.darker(), "arrow-down-icon.png", e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            backgroundView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().removeBackgroundAxioms(selectedValues);
        });
    }
}
