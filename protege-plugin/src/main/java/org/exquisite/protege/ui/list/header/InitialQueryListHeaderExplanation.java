package org.exquisite.protege.ui.list.header;

import org.protege.editor.core.ui.list.MListSectionHeader;

/**
 * @author wolfi
 */
public class InitialQueryListHeaderExplanation implements MListSectionHeader {

    private boolean checkCoherency;

    public InitialQueryListHeaderExplanation(boolean checkCoherency) {
        this.checkCoherency = checkCoherency;
    }

    @Override
    public String getName() {
        return "When the ontology is inconsistent" + ((checkCoherency) ? " and/or incoherent " : " ") + ", queries in the form of axioms can be answered here in order to identify the ontology's faulty axioms.";
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
