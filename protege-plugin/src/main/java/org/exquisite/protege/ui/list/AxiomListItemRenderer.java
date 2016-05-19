package org.exquisite.protege.ui.list;

import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;

/**
* Created with IntelliJ IDEA.
* User: pfleiss
* Date: 05.09.12
* Time: 16:40
* To change this template use File | Settings | File Templates.
*/
public class AxiomListItemRenderer extends BasicAxiomListItemRenderer {


    private Color headerColor;

    public AxiomListItemRenderer(OWLEditorKit editorKit, Color headerColor) {
        super(editorKit);
        this.headerColor = headerColor;

    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        Component result = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);

        if (value instanceof AxiomListHeader) {
            AxiomListHeader listHeader = ((AxiomListHeader) value);
            result = getRenderer().getListCellRendererComponent(list, listHeader, index, isSelected, cellHasFocus);

            ((JComponent)result).setBorder(BorderFactory.createLineBorder(headerColor,2));
            result.setBackground(headerColor.brighter());
        }
        return result;

    }

}
