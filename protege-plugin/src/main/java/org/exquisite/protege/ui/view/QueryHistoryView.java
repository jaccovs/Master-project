package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.QueryHistoryAxiomList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class QueryHistoryView extends AbstractListQueryViewComponent {

    @Override
    public QueryHistoryAxiomList getList() {
        return (QueryHistoryAxiomList) super.getList();
    }

    @Override
    protected JComponent createListForComponent() {
        return new QueryHistoryAxiomList(getOWLEditorKit(),getEditorKitHook());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        getList().updateView();
    }

}
