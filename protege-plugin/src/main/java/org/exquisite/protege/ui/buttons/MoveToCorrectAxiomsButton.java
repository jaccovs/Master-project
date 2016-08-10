package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.AxiomListItem;
import org.exquisite.protege.ui.view.InputOntologyView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A button to move a selected axiom from the set of possibly faulty axioms to the set of correct axioms.
 */
public class MoveToCorrectAxiomsButton extends AbstractImageButton {

    public MoveToCorrectAxiomsButton(AxiomListItem axiomListItem, InputOntologyView inputOntologyView) {
        super("Assume as Correct", Color.GREEN.darker(),"Accept2.png", e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            inputOntologyView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().moveToToCorrectAxioms(selectedValues);
        });
    }
}
