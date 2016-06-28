package org.exquisite.protege.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

import javax.swing.*;
import java.awt.*;

class BasicAxiomListItemRenderer implements ListCellRenderer  {

    private OWLCellRenderer renderer;

    BasicAxiomListItemRenderer(OWLEditorKit editorKit) {
        renderer = new OWLCellRenderer(editorKit);
    }

    OWLCellRenderer getRenderer() {
        return renderer;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value instanceof AxiomListItem) {
            AxiomListItem item = ((AxiomListItem) value);
            getRenderer().setOntology(item.getOntology());
            getRenderer().setHighlightKeywords(true);
            getRenderer().setWrap(false);
            return getRenderer().getListCellRendererComponent(list, item.getAxiom(), index, isSelected, cellHasFocus);
        }
        else {
            return getRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
