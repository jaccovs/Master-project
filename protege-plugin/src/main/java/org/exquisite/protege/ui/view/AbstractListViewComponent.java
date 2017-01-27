package org.exquisite.protege.ui.view;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.util.ComponentFactory;

import javax.swing.*;
import java.awt.*;

/**
 * A view component containing an MList instance.
 *
 * @see MList
 */
public abstract class AbstractListViewComponent extends AbstractViewComponent {

    private MList list;

    /**
     * @return the list of this view component.
     */
    public MList getList() {
        return list;
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        list = createListForComponent();

        JComponent panel =  new JPanel(new BorderLayout(10, 10));
        panel.add(ComponentFactory.createScrollPane(list));

        add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {
        super.disposeOWLView();
        list = null;
    }

    /**
     * Creates a list component.
     *
     * @return
     */
    protected abstract MList createListForComponent();


}
