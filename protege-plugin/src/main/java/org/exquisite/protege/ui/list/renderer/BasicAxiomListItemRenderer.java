package org.exquisite.protege.ui.list.renderer;

import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

import javax.swing.*;
import java.awt.*;

public class BasicAxiomListItemRenderer implements ListCellRenderer  {

    private OWLCellRenderer renderer;

    public BasicAxiomListItemRenderer(OWLEditorKit editorKit) {
        renderer = new OWLCellRenderer(editorKit);
    }

    public OWLCellRenderer getRenderer() {
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
