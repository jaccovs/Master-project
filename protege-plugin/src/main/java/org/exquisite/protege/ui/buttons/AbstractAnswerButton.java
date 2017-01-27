package org.exquisite.protege.ui.buttons;

import org.protege.editor.core.ui.list.MListButton;

import java.awt.*;
import java.awt.event.ActionListener;

public abstract class AbstractAnswerButton extends MListButton  {

    protected int sizeMultiple = 4;

    protected AbstractAnswerButton(String name, Color rollOverColor, ActionListener actionListener) {
        super(name, rollOverColor, actionListener);
    }

    protected void paintBackgroundColor (Graphics2D g, Color color) {
        Rectangle buttonBounds = getBounds();
        Color oldColor = g.getColor();
        g.setColor(color);
        g.fillOval(buttonBounds.x, buttonBounds.y, buttonBounds.width, buttonBounds.height);
        g.setColor(oldColor);
    }

    @Override
    protected int getSizeMultiple() {
        return sizeMultiple;
    }

}
