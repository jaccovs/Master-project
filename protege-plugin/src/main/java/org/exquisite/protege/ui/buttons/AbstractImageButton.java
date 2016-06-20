package org.exquisite.protege.ui.buttons;

import org.osgi.framework.Bundle;
import org.protege.editor.core.ProtegeApplication;

import java.awt.*;
import java.awt.event.ActionListener;

public class AbstractImageButton extends AbstractAnswerButton {

    private String image;

    public AbstractImageButton(String name, Color rollOverColor, String image, ActionListener actionListener) {
        super(name, rollOverColor, actionListener);
        this.image = image;
    }

    @Override
    public void paintButtonContent(Graphics2D g) {
        final Image img = loadCustomImage(image);
        final Rectangle bounds = getBounds();
        final int imgWidth = img.getWidth(null);
        final int imgHeight = img.getHeight(null);
        assert bounds.width >= imgWidth;
        assert bounds.height >= imgHeight;
        final int xOffset = (bounds.width - imgWidth) / 2;
        final int yOffset = (bounds.height - imgHeight) / 2;
        g.drawImage(img, bounds.x + xOffset, bounds.y + yOffset, imgWidth, imgHeight, null);
    }

    /**
     * Returns an image from the icons resources.
     *
     * @param name The icons name.
     * @return The icon.
     */
    protected Image loadCustomImage(String name) {
        Bundle bundle=null;
        for (Bundle b : ProtegeApplication.getContext().getBundles())
            if (b.getSymbolicName().equals("org.exquisite.protege"))
                bundle = b;

        assert bundle != null;
        return Toolkit.getDefaultToolkit().getImage(bundle.getEntry("/icons/" + name));
    }

    @Override
    public Color getBackground() {
        return Color.WHITE;
    }

}
