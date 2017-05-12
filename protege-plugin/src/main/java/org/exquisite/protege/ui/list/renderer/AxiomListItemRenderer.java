package org.exquisite.protege.ui.list.renderer;

import org.exquisite.protege.ui.list.header.ConflictListHeader;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
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

        if (value instanceof ConflictListHeader) {
            ConflictListHeader listHeader = ((ConflictListHeader) value);
            result = getRenderer().getListCellRendererComponent(list, listHeader, index, isSelected, cellHasFocus);
            getRenderer().setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            getRenderer().setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            ((JComponent)result).setBorder(BorderFactory.createLineBorder(headerColor,2));
            result.setBackground(headerColor.brighter());
        } else if (value instanceof DiagnosisListHeader) {
            DiagnosisListHeader listHeader = ((DiagnosisListHeader) value);
            result = getRenderer().getListCellRendererComponent(list, listHeader, index, isSelected, cellHasFocus);
            getRenderer().setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            getRenderer().setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            ((JComponent)result).setBorder(BorderFactory.createLineBorder(headerColor,2));
            result.setBackground(headerColor.brighter());
        }
        return result;

    }

}
