package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.AcquiredTestcaseAxiomList;
import org.protege.editor.core.ui.list.MList;

/**
 * @author wolfi
 */
public class AcquiredTestcasesView extends AbstractTestcaseListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new AcquiredTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
