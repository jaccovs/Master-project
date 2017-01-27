package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.OriginalTestcaseAxiomList;
import org.protege.editor.core.ui.list.MList;

/**
 * @author wolfi
 */
public class OriginalTestcasesView extends AbstractTestcaseListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new OriginalTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
