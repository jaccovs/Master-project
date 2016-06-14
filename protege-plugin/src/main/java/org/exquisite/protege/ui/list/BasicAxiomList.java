package org.exquisite.protege.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasicAxiomList extends AbstractAxiomList {

    public BasicAxiomList(OWLEditorKit editorKit) {
        super(editorKit);
    }

    public void updateList(Set<OWLLogicalAxiom> backgroundAxioms, OWLOntology ontology) {
        List<Object> items = new ArrayList<Object>();
        for (OWLLogicalAxiom axiom : backgroundAxioms)
            items.add(new AxiomListItem(axiom,ontology));

        setListData(items.toArray());

    }

}
