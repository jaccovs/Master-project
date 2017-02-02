package org.exquisite.protege.ui.list.header;

import org.protege.editor.core.ui.list.MListSectionHeader;

/**
 * <p>
 *     Section header for the query.
 * </p>
 *
 * @author wolfi
 */
public class QueryListHeader implements MListSectionHeader {

    @Override
    public String getName() {
        return "Please answer if these axioms are true (+) or false (-) for your ontology. Select not sure (?) for axioms you cannot answer.";
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
