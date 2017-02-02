package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.QueryAxiomList;
import org.protege.editor.owl.ui.framelist.ExplainButton;

import java.awt.*;

/**
 * <p>
 *     A button in query view to  an answer open.
 * </p>
 * @author wolfi
 */
public class NotSureButton extends AbstractAnswerButton {

    private boolean isNotSure;

    public NotSureButton(final String name, final QueryAxiomList list, boolean isNotSure) {
        super(name, Color.GRAY , e -> list.handleNotSure());
        this.isNotSure = isNotSure;
    }

    /**
     * This is a copy from #ExplainButton implemented by Matthew Horridge.
     *
     * @param g The graphics which should be used for rendering
     * @see ExplainButton
     */
    public void paintButtonContent(Graphics2D g) {
        if (isNotSure)
            paintBackgroundColor(g, Color.GRAY.darker());
        int stringWidth = g.getFontMetrics().getStringBounds("?", g).getBounds().width;
        int w = getBounds().width;
        int h = getBounds().height;
        g.drawString("?",
                getBounds().x + w / 2 - stringWidth / 2,
                getBounds().y + g.getFontMetrics().getAscent() / 2 + h / 2);
    }
}
