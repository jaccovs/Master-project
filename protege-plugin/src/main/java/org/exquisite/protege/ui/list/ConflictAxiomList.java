package org.exquisite.protege.ui.list;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.header.ConflictListHeader;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ConflictAxiomList extends AbstractAxiomList {

    public ConflictAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);

        Set<Set<OWLLogicalAxiom>> conflicts = editorKitHook.getActiveOntologyDebugger().getConflicts();
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
        updateList(conflicts, ontology);
    }

    public void updateList(Set<Set<OWLLogicalAxiom>> setsOfAxioms, OWLOntology ontology) {
        List<Object> items = new ArrayList<>();
        int cnt = 0;
        for (Set<OWLLogicalAxiom> _axioms : setsOfAxioms) {
            Set<OWLLogicalAxiom> sortedAxioms = new TreeSet<>(_axioms);
            items.add(new ConflictListHeader(sortedAxioms,createHeaderName(++cnt, sortedAxioms)));
            items.addAll(sortedAxioms.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList()));
            items.add(" ");
        }
        if (items.size()>0)
            items.remove(items.size()-1);

        setListData(items.toArray());
    }

    private String createHeaderName(final int position, final Set<OWLLogicalAxiom> axioms) {
        return "Minimal Conflict Set #" + (position) + " (Size: " + axioms.size() + ')';
    }

}
