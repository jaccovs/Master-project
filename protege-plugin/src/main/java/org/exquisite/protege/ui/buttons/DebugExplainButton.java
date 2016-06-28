package org.exquisite.protege.ui.buttons;

import org.exquisite.protege.ui.list.QueryAxiomList;
import org.protege.editor.owl.ui.framelist.ExplainButton;

public class DebugExplainButton extends ExplainButton {

    public DebugExplainButton(final QueryAxiomList list) {
        super(e -> list.handleAxiomExplain());
    }

}
