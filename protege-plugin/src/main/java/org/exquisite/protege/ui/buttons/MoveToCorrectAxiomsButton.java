package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.AxiomListItem;
import org.exquisite.protege.ui.view.InputOntologyView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A button to move a selected axiom from the set of possibly faulty axioms to the set of correct axioms.
 */
public class MoveToCorrectAxiomsButton extends AbstractAnswerButton {

    public MoveToCorrectAxiomsButton(AxiomListItem axiomListItem, InputOntologyView inputOntologyView) {
        super("Assume as Correct", Color.GREEN.darker(), e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            inputOntologyView.getEditorKitHook().getActiveOntologyDebugger().moveToToCorrectAxioms(selectedValues);
        });
    }

    @Override
    public void paintButtonContent(Graphics2D g) {
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;

        int x = getBounds().x;
        int y = getBounds().y;

        int insetX = size / 4;
        int insetY = size / 4;
        int insetHeight = size / 2;
        int insetWidth = size / 2;
        g.fillRect(x + size / 2  - thickness / 2, y + insetY, thickness, insetHeight);
        g.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);
    }

    @Override
    protected int getSizeMultiple() {
        return 4;
    }

}
