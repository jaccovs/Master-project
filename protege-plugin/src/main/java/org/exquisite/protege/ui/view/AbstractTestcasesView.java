package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.AbstractTestcaseAxiomList;

import javax.swing.event.ChangeEvent;

abstract public class AbstractTestcasesView extends AbstractListQueryViewComponent {

    @Override
    public AbstractTestcaseAxiomList getList() {
        return (AbstractTestcaseAxiomList) super.getList();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        getList().updateView();
    }

}
