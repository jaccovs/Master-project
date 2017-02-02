package org.exquisite.protege.ui.view;

import org.exquisite.protege.ui.list.OriginalTestcaseAxiomList;
import org.protege.editor.core.ui.list.MList;

/**
 * <p>
 *     View that represents original test cases which are predefined (e.g. stored as annotation in the ontology).
 * </p>
 *
 * @author wolfi
 */
public class OriginalTestcasesView extends AbstractTestcaseListViewComponent {

    @Override
    protected MList createListForComponent() {
        return new OriginalTestcaseAxiomList(getOWLEditorKit(), getEditorKitHook());
    }
}
