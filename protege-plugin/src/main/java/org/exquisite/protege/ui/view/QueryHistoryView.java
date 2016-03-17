package org.exquisite.protege.ui.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.11.12
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class QueryHistoryView extends AbstractListQueryViewComponent {


/* TODO
    @Override
    public QueryHistoryAxiomList getList() {
        return (QueryHistoryAxiomList) super.getList();
    }
*/
    @Override
    protected JComponent createListForComponent() {
        // return new QueryHistoryAxiomList(getOWLEditorKit(),getEditorKitHook()); TODO
        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // getList().updateView(); TODO
    }

}
