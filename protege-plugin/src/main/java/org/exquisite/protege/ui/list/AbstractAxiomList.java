package org.exquisite.protege.ui.list;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponent;
import org.protege.editor.owl.ui.renderer.LinkedObjectComponentMediator;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 05.09.12
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAxiomList extends MList implements LinkedObjectComponent {

    private LinkedObjectComponentMediator mediator;

    public AbstractAxiomList(OWLEditorKit editorKit) {
        this.mediator = new LinkedObjectComponentMediator(editorKit, this);
        setCellRenderer(new BasicAxiomListItemRenderer(editorKit));
        getMouseListeners();
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
}
