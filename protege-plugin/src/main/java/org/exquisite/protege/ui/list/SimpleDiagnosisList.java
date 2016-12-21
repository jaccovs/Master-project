package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleDiagnosisList extends AbstractAxiomList {

    public SimpleDiagnosisList(OWLEditorKit editorKit, Color headerColor) {
        super(editorKit);
        setCellRenderer(new AxiomListItemRenderer(editorKit,headerColor));
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology, String headerPref, boolean isIncludeMeasure) {
        List<Object> items = new ArrayList<>();
        int cnt = 0;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            items.add(new DiagnosisListHeader(diagnosis,(++cnt) + ". " + headerPref,isIncludeMeasure));
            items.addAll(diagnosis.getFormulas().stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }

}
