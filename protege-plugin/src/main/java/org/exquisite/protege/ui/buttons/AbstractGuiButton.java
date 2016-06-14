package org.exquisite.protege.ui.buttons;

import org.osgi.framework.Bundle;
import org.protege.editor.core.ProtegeApplication;

import javax.swing.*;

public abstract class AbstractGuiButton extends JButton {

    public AbstractGuiButton(String name, String tooltip, String icon, int event, AbstractAction abstractAction) {
        super(abstractAction);
        setText(name);
        setName(name);
        setIcon(loadCustomIcon(icon));
        setToolTipText(tooltip);
        if (event != -1) setMnemonic(event);
        setEnabled(true);
    }

    protected ImageIcon loadCustomIcon(String name) {
        Bundle bundle=null;
        for (Bundle b : ProtegeApplication.getContext().getBundles())
            if (b.getSymbolicName().equals("org.exquisite.protege"))
                bundle = b;
        return new ImageIcon(bundle.getEntry("/icons/" + name));
    }

}
