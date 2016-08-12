package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.AcquiredTestcaseAxiomList;

import javax.swing.*;

/**
 * @author wolfi
 */
public class AcquiredTestcasesView extends AbstractTestcasesView {

    @Override
    protected JComponent createListForComponent() {
        return new AcquiredTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
