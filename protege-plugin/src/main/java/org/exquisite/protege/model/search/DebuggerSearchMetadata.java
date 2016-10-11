package org.exquisite.protege.model.search;

import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchMetadata;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author wolfi
 */
public class DebuggerSearchMetadata extends SearchMetadata {

    private OWLAxiom axiom;

    /**
     * Records search metadata for a given object.
     *
     * @param category         The category which the search metadata falls into.
     * @param groupDescription The description (human readable name) of the subgroup which the metadata falls into.
     * @param subject          The subject to which the search string pertains to.  This is usually an entity or an ontology
     *                         i.e.
     *                         something which can be selected in Protege.
     * @param subjectRendering A rendering of the subject.  This rendering is used to compare search metadata objects.
     * @param searchString     The string that should be searched.
     */
    public DebuggerSearchMetadata(SearchCategory category, String groupDescription, OWLObject subject, String subjectRendering, String searchString, OWLAxiom ax) {
        super(category, groupDescription, subject, subjectRendering, searchString);
        axiom = ax;
    }


    public OWLAxiom getAxiom() {
        return axiom;
    }

}
