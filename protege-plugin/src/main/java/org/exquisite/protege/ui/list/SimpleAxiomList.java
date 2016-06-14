package org.exquisite.protege.ui.list;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimpleAxiomList extends AbstractAxiomList {

    public SimpleAxiomList(OWLEditorKit editorKit, Color headerColor) {
        super(editorKit);
        setCellRenderer(new AxiomListItemRenderer(editorKit,headerColor));
    }

    public void updateList(Set<Set<OWLLogicalAxiom>> setsOfAxioms, OWLOntology ontology, String headerPref) {
        List<Object> items = new ArrayList<>();
        for (Set<OWLLogicalAxiom> axioms : setsOfAxioms) {
            items.add(new AxiomListHeader(axioms,headerPref));
            for (OWLLogicalAxiom axiom : axioms) {
                items.add(new AxiomListItem(axiom,ontology));
            }
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }




}
