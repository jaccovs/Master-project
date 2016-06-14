package org.exquisite.protege.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLEntityCreationPanel;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CreateOWLEntityAction<T extends OWLEntity> extends AbstractAction {

    private OWLEditorKit edKT;

    private Class<T> t;
    private String out;

    public CreateOWLEntityAction(String name, OWLEditorKit ed, Icon icon, String out, Class<T> t) {

        putValue(Action.NAME, name);
        putValue(Action.SMALL_ICON, icon);
        edKT = ed;
        this.out = out;
        this.t = t;

        setEnabled(true);
    }


    public void actionPerformed(ActionEvent e) {
        OWLEntityCreationSet set = OWLEntityCreationPanel.showDialog(edKT, out, t);

        if (set != null) {

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (Object change : set.getOntologyChanges())
                changes.add((OWLOntologyChange) change);  // changes.addAll(set.getOntologyChanges());
            final OWLModelManager mngr = edKT.getModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            OWLDeclarationAxiom ax = df.getOWLDeclarationAxiom(set.getOWLEntity());
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            mngr.applyChanges(changes);
        }


    }

}
