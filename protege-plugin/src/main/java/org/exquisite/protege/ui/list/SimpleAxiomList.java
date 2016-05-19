package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pfleiss
 * Date: 04.09.12
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class SimpleAxiomList extends AbstractAxiomList {

    public SimpleAxiomList(OWLEditorKit editorKit, Color headerColor) {
        super(editorKit);
        setCellRenderer(new AxiomListItemRenderer(editorKit,headerColor));
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology, String headerPref, boolean isIncludeMeasure) {
        List<Object> items = new ArrayList<Object>();
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            items.add(new AxiomListHeader(diagnosis,headerPref,isIncludeMeasure));
            for (OWLLogicalAxiom axiom : diagnosis.getFormulas()) {
                items.add(new AxiomListItem(axiom,ontology));
            }
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }




}
