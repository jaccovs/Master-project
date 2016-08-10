package org.exquisite.protege.ui.list;

import org.exquisite.protege.model.EditorKitHook;
import org.exquisite.protege.model.OntologyDiagnosisSearcher;
import org.exquisite.protege.model.configuration.DiagnosisEngineFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SimpleAxiomList extends AbstractAxiomList {

    public SimpleAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook, Color headerColor, String headerPrefix) {
        super(editorKit);
        setCellRenderer(new AxiomListItemRenderer(editorKit,headerColor));

        DiagnosisEngineFactory diagnosisEngineFactory = editorKitHook.getActiveOntologyDiagnosisSearcher().getDiagnosisEngineFactory();
        Set<Set<OWLLogicalAxiom>> conflicts = diagnosisEngineFactory.getDiagnosisEngine().getConflicts();
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
        updateList(conflicts, ontology, headerPrefix);
    }

    public void updateList(Set<Set<OWLLogicalAxiom>> setsOfAxioms, OWLOntology ontology, String headerPref) {
        List<Object> items = new ArrayList<>();
        for (Set<OWLLogicalAxiom> _axioms : setsOfAxioms) {
            Set<OWLLogicalAxiom> sortedAxioms = new TreeSet<>(_axioms);
            items.add(new AxiomListHeader(sortedAxioms,headerPref));
            items.addAll(sortedAxioms.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }

}
