package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.AxiomListItem;
import org.exquisite.protege.ui.view.InputOntologyView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A button to move a selected axiom from the set of correct axioms to the set of possibly faulty axioms.
 */
public class MoveToPossiblyFaultyAxiomsButton extends AbstractImageButton {

    public MoveToPossiblyFaultyAxiomsButton(AxiomListItem axiomListItem, InputOntologyView inputOntologyView) {
        super("Assume as Possibly Faulty", Color.RED.darker(), "clear.png", e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            inputOntologyView.getEditorKitHook().getActiveOntologyDiagnosisSearcher().moveToPossiblyFaultyAxioms(selectedValues);
        });
    }
}
