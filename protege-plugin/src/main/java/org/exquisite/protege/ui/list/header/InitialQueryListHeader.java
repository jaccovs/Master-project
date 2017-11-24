package org.exquisite.protege.ui.list.header;

import org.protege.editor.core.ui.list.MListSectionHeader;

/**
 * @author wolfi
 */
public class InitialQueryListHeader implements MListSectionHeader {

    private boolean checkCoherency;

    public InitialQueryListHeader(boolean checkCoherency) {
        this.checkCoherency = checkCoherency;
    }

    @Override
    public String getName() {
        return "Press Start to check the consistency" + ((checkCoherency) ? " and coherency " : " ") + "of the ontology";
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
