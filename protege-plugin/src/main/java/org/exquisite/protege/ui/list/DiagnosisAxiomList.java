package org.exquisite.protege.ui.list;

import org.exquisite.core.model.Diagnosis;
import org.exquisite.protege.ui.list.header.DiagnosisListHeader;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.exquisite.protege.ui.list.renderer.AxiomListItemRenderer;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiagnosisAxiomList extends AbstractAxiomList {

    public DiagnosisAxiomList(OWLEditorKit editorKit) {
        super(editorKit);
    }

    public void updateList(Set<Diagnosis<OWLLogicalAxiom>> diagnoses, OWLOntology ontology) {
        List<Object> items = new ArrayList<>();
        int cnt = 0;
        for (Diagnosis<OWLLogicalAxiom> diagnosis : diagnoses) {
            items.add(new DiagnosisListHeader(diagnosis,createHeaderName(++cnt, diagnosis)));
            items.addAll(diagnosis.getFormulas().stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }

    private String createHeaderName(final int position, final Diagnosis<OWLLogicalAxiom> diagnosis) {
        final String roundedMeas = diagnosis.getMeasure().round(new java.math.MathContext(6)).toEngineeringString();
        return "Faulty Axioms Set #" + (position) + " (Size: " + diagnosis.getFormulas().size() + ", Measure: " + roundedMeas + ')';
    }

}
