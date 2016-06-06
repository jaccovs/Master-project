package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.TcaeAxiomList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class TestcaseView extends AbstractListQueryViewComponent {

    @Override
    public TcaeAxiomList getList() {
        return (TcaeAxiomList) super.getList();
    }

    @Override
    protected JComponent createListForComponent() {
        return new TcaeAxiomList(getOWLEditorKit(),getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        getList().updateView();
    }

}
