package org.exquisite.protege.ui.list;

import org.exquisite.protege.EditorKitHook;
import org.exquisite.protege.ui.list.header.ConflictListHeader;
import org.exquisite.protege.ui.list.item.AxiomListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     A list containing the minimal conflict sets.
 * </p>
 *
 * @author wolfi
 */
public class ConflictAxiomList extends AbstractAxiomList {

    public ConflictAxiomList(OWLEditorKit editorKit, EditorKitHook editorKitHook) {
        super(editorKit);

        Set<Set<OWLLogicalAxiom>> conflicts = editorKitHook.getActiveOntologyDebugger().getConflicts();
        OWLOntology ontology = editorKit.getModelManager().getActiveOntology();
        updateList(conflicts, ontology);
    }

    public void updateList(Set<Set<OWLLogicalAxiom>> minimalConflictSets, OWLOntology ontology) {
        // order the minimal conflict sets by size in ascending order
        List<Set<OWLLogicalAxiom>> orderedMinimalConflictSets = new ArrayList<>(minimalConflictSets);
        orderedMinimalConflictSets.sort(Comparator.comparingInt(Set::size));

        List<Object> items = new ArrayList<>();
        int cnt = 0;
        for (Set<OWLLogicalAxiom> minimalConflictSet : orderedMinimalConflictSets) {
            Set<OWLLogicalAxiom> sortedAxioms = new TreeSet<>(minimalConflictSet); // list the axioms in a sorted order
            items.add(new ConflictListHeader(sortedAxioms,createHeaderName(++cnt, sortedAxioms))); // section header for one conflict set
            items.addAll(sortedAxioms.stream().map(axiom -> new AxiomListItem(axiom, ontology)).collect(Collectors.toList())); // the conflict axioms of the conflict set
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
