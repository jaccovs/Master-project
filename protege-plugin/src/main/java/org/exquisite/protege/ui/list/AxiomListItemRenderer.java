package org.exquisite.protege.ui.list;

import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;

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
        } else if (value instanceof DiagnosisListHeader) {
            DiagnosisListHeader listHeader = ((DiagnosisListHeader) value);
            result = getRenderer().getListCellRendererComponent(list, listHeader, index, isSelected, cellHasFocus);

            ((JComponent)result).setBorder(BorderFactory.createLineBorder(headerColor,2));
            result.setBackground(headerColor.brighter());

        }
        return result;

    }

}
