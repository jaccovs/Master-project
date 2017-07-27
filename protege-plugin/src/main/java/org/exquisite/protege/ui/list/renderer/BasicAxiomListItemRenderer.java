package org.exquisite.protege.ui.list.renderer;

import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.list.item.RepairListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;

import javax.swing.*;
import java.awt.*;

public class BasicAxiomListItemRenderer implements ListCellRenderer  {

    private OWLCellRenderer renderer;

    public BasicAxiomListItemRenderer(OWLEditorKit editorKit) {
        renderer = new OWLCellRenderer(editorKit);
        renderer.setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
        renderer.setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
    }

    public OWLCellRenderer getRenderer() {
        renderer.setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
        renderer.setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
        return renderer;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value instanceof AxiomListItem) {
            AxiomListItem item = ((AxiomListItem) value);
            getRenderer().setOntology(item.getOntology());
            if (item instanceof RepairListItem && ((RepairListItem)item).isDeleted()) {
              getRenderer().setHighlightKeywords(false);
              getRenderer().setCommentedOut(true);
            } else {
                getRenderer().setHighlightKeywords(true);
            }
            getRenderer().setWrap(false);
            Component result =  getRenderer().getListCellRendererComponent(list, item.getAxiom(), index, isSelected, cellHasFocus);
            getRenderer().setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            getRenderer().setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            return result;
        }
        else {
            Component result = getRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            getRenderer().setHighlightUnsatisfiableClasses(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            getRenderer().setHighlightUnsatisfiableProperties(false); // a fix for Issue #87 to prevent race conditions with DebuggerProgressUI.showWindow()
            return result;
        }
    }
}
