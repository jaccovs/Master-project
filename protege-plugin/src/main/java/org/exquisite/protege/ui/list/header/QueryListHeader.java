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
        return "Please answer if these statements are true (+) or not true (-) in your intended domain. Select not sure (?) for statements you cannot answer.";
    }

    @Override
    public boolean canAdd() {
        return false;
    }
}
