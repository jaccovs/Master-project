package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;

abstract class AbstractAxiomList extends MList implements LinkedObjectComponent {

    protected OWLEditorKit editorKit;

    private LinkedObjectComponentMediator mediator;

    AbstractAxiomList(OWLEditorKit editorKit) {
        this.mediator = new LinkedObjectComponentMediator(editorKit, this);
        setCellRenderer(new BasicAxiomListItemRenderer(editorKit));
        getMouseListeners();
        this.editorKit = editorKit;
    }

    @Override
    public Point getMouseCellLocation() {
        Point mouseLoc = getMousePosition();
        if (mouseLoc == null) {
            return null;
        }
        int index = locationToIndex(mouseLoc);
        Rectangle cellRect = getCellBounds(index, index);
        return new Point(mouseLoc.x - cellRect.x, mouseLoc.y - cellRect.y);
    }

    @Override
    public Rectangle getMouseCellRect() {
        Point loc = getMousePosition();
        if (loc == null) {
            return null;
        }
        int index = locationToIndex(loc);
        return getCellBounds(index, index);
    }

    @Override
    public void setLinkedObject(OWLObject object) {
        mediator.setLinkedObject(object);
    }

    @Override
    public OWLObject getLinkedObject() {
        return mediator.getLinkedObject();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    public OWLEditorKit getEditorKit() {
        return editorKit;
    }
}
