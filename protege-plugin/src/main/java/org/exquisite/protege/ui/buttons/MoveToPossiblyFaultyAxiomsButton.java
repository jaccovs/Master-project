package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.view.InputOntologyView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A button to move a selected axiom from the set of correct axioms to the set of possibly faulty axioms.
 */
public class MoveToPossiblyFaultyAxiomsButton extends AbstractAnswerButton {

    public static final Color ROLL_OVER_COLOR = new Color(240, 40, 40);

    public MoveToPossiblyFaultyAxiomsButton(AxiomListItem axiomListItem, InputOntologyView inputOntologyView) {
        super("Assume as Possibly Faulty", ROLL_OVER_COLOR, e -> {
            List<AxiomListItem> selectedValues = new LinkedList<>();
            selectedValues.add(axiomListItem);
            inputOntologyView.getEditorKitHook().getActiveOntologyDebugger().moveToPossiblyFaultyAxioms(selectedValues);
        });
    }

    public void paintButtonContent(Graphics2D gIn) {
        Graphics2D g = (Graphics2D) gIn.create();
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;

        int x = getBounds().x;
        int y = getBounds().y;

        g.rotate(Math.PI / 4, x + size / 2, y + size / 2);

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
