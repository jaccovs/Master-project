package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.QueryAxiomList;

import java.awt.*;

public class AxiomIsNotEntailedButton extends AbstractAnswerButton {

    private boolean isMarkedNonEntailed;

    public AxiomIsNotEntailedButton(final QueryAxiomList list, boolean isMarkedNonEntailed) {
        super("Not Entailed", Color.RED.darker(), e -> list.handleNotEntailed());
        this.isMarkedNonEntailed = isMarkedNonEntailed;
    }

    public void paintButtonContent(Graphics2D g) {

        if (isMarkedNonEntailed)
            paintBackgroundColor(g,Color.RED);
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;

        int x = getBounds().x;
        int y = getBounds().y;

        int insetX = size / 4;
        int insetWidth = size / 2;
        g.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);

    }

}
