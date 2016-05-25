package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.QueryAxiomList;
import org.protege.editor.owl.ui.framelist.ExplainButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DebugExplainButton extends ExplainButton {

    public DebugExplainButton(final QueryAxiomList list) {
        super(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                list.handleAxiomExplain();
            }
        });
    }

}
