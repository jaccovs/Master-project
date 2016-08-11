package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.OriginalTestcaseAxiomList;

import javax.swing.*;

/**
 * @author wolfi
 */
public class OriginalTestcasesView extends AbstractTestcasesView {

    @Override
    protected JComponent createListForComponent() {
        return new OriginalTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
