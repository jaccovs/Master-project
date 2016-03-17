package org.exquisite.protege.ui.view;

import org.protege.editor.core.ui.util.ComponentFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListQueryViewComponent extends AbstractQueryViewComponent {

    private JComponent list;
/* TODO
    public JComponent getList() {
        return list;
    }
*/
    @Override
    protected void initialiseOWLView() throws Exception {
        super.initialiseOWLView();
        setLayout(new BorderLayout(10, 10));
        list = createListForComponent();

        JComponent panel =  new JPanel(new BorderLayout(10, 10));
        panel.add(ComponentFactory.createScrollPane(list));

        add(panel, BorderLayout.CENTER);
    }

    protected abstract JComponent createListForComponent();


}
