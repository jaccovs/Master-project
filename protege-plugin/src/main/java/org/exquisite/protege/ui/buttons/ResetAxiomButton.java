package org.exquisite.protege.ui.buttons;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author wolfi
 */
public class ResetAxiomButton extends AbstractAnswerButton {

    private boolean enable;

    private String iconSymbol = "R";

    public ResetAxiomButton(ActionListener actionListener) {
        super("Reset axiom", Color.GREEN.darker(), actionListener);
        this.enable = enable;
    }

    @Override
    public void paintButtonContent(Graphics2D g) {
        //if (!enable)
        //    paintBackgroundColor(g, Color.LIGHT_GRAY.darker());

        int stringWidth = g.getFontMetrics().getStringBounds(iconSymbol, g).getBounds().width;
        int w = getBounds().width;
        int h = getBounds().height;
        g.drawString(iconSymbol,
                getBounds().x + w / 2 - stringWidth / 2,
                getBounds().y + g.getFontMetrics().getAscent() / 2 + h / 2);
    }

    protected int getSizeMultiple() {
        return 4;
    }
}
