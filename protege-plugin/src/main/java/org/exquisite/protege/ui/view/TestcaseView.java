package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.TcaeAxiomList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
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
