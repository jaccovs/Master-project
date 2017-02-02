package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.AcquiredTestcaseAxiomList;
import org.protege.editor.core.ui.list.MList;

/**
 * <p>
 *     View for the list of acquired test cases (given answers by the user) separated between positively (or entailed)
 *     or negatively (or non entailed) test cases (given answers).
 * </p>
 *
 * @author wolfi
 */
public class AcquiredTestcasesView extends AbstractTestcaseListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new AcquiredTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
